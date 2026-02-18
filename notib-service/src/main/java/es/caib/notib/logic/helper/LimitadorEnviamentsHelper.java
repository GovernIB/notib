package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.util.DatesUtils;
import es.caib.notib.persist.repository.AplicacioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.LongAdder;

@Slf4j
@Component
public class LimitadorEnviamentsHelper {

    @Autowired
    private AplicacioRepository aplicacioRepository;

    // Comptadors per minut (minut actual)
    private static final ConcurrentHashMap<Long, LongAdder> contadorMinutsLaboral = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, LongAdder> contadorMinutsNoLaboral = new ConcurrentHashMap<>();

    // Comptadors per dia (dia actual)
    private static final ConcurrentHashMap<Long, LongAdder> contadorDiesLaboral = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, LongAdder> contadorDiesNoLaboral = new ConcurrentHashMap<>();

    // Marques de temps "actuals" (shared entre threads)
    // minuteEpoch = minuts des de epoch; dayKey = YYYYMMDD (en zona horària)
    private static final AtomicLong currentMinuteEpoch = new AtomicLong(-1);
    private static final AtomicLong currentDayKey = new AtomicLong(-1);

    // Locks per fer resets consistents
    private static final ReentrantLock minuteResetLock = new ReentrantLock();
    private static final ReentrantLock dayResetLock = new ReentrantLock();

    // Defineix la zona horària que realment uses per "dia" i "horari laboral"
    private static final ZoneId ZONE = ZoneId.of("Europe/Madrid");

    private static long minuteEpochNow() {
        return Instant.now().getEpochSecond() / 60; // bucket de 60s
    }

    private static long dayKeyNow() {
        LocalDate d = LocalDate.now(ZONE);
        return d.getYear() * 10000L + d.getMonthValue() * 100L + d.getDayOfMonth(); // YYYYMMDD
    }

    /**
     * Cridar sempre al principi de cada petició que vulguis comptar.
     * Fa reset dels comptadors si ha canviat el minut o el dia.
     */
    private static void resetIfNeeded() {
        // 1) Reset per minut
        long nowMinute = minuteEpochNow();
        long seenMinute = currentMinuteEpoch.get();
        if (nowMinute != seenMinute) {
            minuteResetLock.lock();
            try {
                // doble-check dins el lock
                long again = currentMinuteEpoch.get();
                if (nowMinute != again) {
                    contadorMinutsLaboral.clear();
                    contadorMinutsNoLaboral.clear();
                    currentMinuteEpoch.set(nowMinute);
                }
            } finally {
                minuteResetLock.unlock();
            }
        }

        // 2) Reset per dia
        long nowDay = dayKeyNow();
        long seenDay = currentDayKey.get();
        if (nowDay != seenDay) {
            dayResetLock.lock();
            try {
                long again = currentDayKey.get();
                if (nowDay != again) {
                    contadorDiesLaboral.clear();
                    contadorDiesNoLaboral.clear();
                    currentDayKey.set(nowDay);
                }
            } finally {
                dayResetLock.unlock();
            }
        }
    }

    private static long getCount(ConcurrentHashMap<Long, LongAdder> map, Long appId) {
        LongAdder adder = map.get(appId);
        return adder == null ? 0L : adder.sum();
    }

    private static long incrementAndGet(ConcurrentHashMap<Long, LongAdder> map, Long appId) {
        LongAdder adder = map.computeIfAbsent(appId, k -> new LongAdder());
        adder.increment();
        return adder.sum();
    }

    // --- AQUÍ integraries el teu mètode ---
    public String checkLimitEnviamentsAplicacioSuperat(String usuariCodi, Long entitatId) {
        try {
            // IMPORTANT: reset per finestra temporal abans de llegir/incrementar
            resetIfNeeded();

            var msg = "";
            var aplicacio = aplicacioRepository.findByUsuariCodiAndEntitatId(usuariCodi, entitatId);

            if (aplicacio == null || !aplicacio.isAplicarLimitEnviaments()) {
                return null;
            }

            boolean diaLaboral = DatesUtils.isDiaLaboral();
            boolean isHorariLaboral = diaLaboral &&
                    DatesUtils.isHorariLaboral(aplicacio.getHorariLaboralInici(), aplicacio.getHorariLaboralFi());

            // Tria mapes segons el teu criteri actual
            var mapMinuts = isHorariLaboral ? contadorMinutsLaboral : contadorMinutsNoLaboral;

            // Nota: per "dies", tu tens dos mapes. Si realment vols comptar “per dia laboral vs no laboral”
            // ho mantenim igual que ara. Si no, t'ho recomanaria unificar.
            var mapDies = diaLaboral ? contadorDiesLaboral : contadorDiesNoLaboral;

            int maxEnvMinut = isHorariLaboral ? aplicacio.getMaxEnviamentsMinutLaboral()
                    : aplicacio.getMaxEnviamentsMinutNoLaboral();
            int maxEnvDia = diaLaboral ? aplicacio.getMaxEnviamentsDiaLaboral()
                    : aplicacio.getMaxEnviamentsDiaNoLaboral();

            long enviamentsMinutActual = getCount(mapMinuts, aplicacio.getId());
            if (enviamentsMinutActual >= maxEnvMinut) {
                msg = diaLaboral
                        ? "Superat el nombre màxim d'enviaments per minut en dies laborals. "
                        : "Superat el nombre màxim d'enviaments per minut en dies no laborals. ";
            }

            // increment minut
            enviamentsMinutActual = incrementAndGet(mapMinuts, aplicacio.getId());

            long enviamentsDiaActual = getCount(mapDies, aplicacio.getId());
            if (enviamentsDiaActual >= maxEnvDia) {
                msg += diaLaboral
                        ? "Superat el nombre màxim d'enviaments per dia en dies laborals"
                        : "Superat el nombre màxim d'enviaments per dia en dies no laborals";
            }

            // increment dia
            enviamentsDiaActual = incrementAndGet(mapDies, aplicacio.getId());

            if (!msg.isBlank()) {
                log.warn(msg + " aplicacio=" + aplicacio.getUsuariCodi()
                        + " enviamentsMinutActual=" + enviamentsMinutActual + " maxEnvMinut=" + maxEnvMinut
                        + " enviamentsDiaActual=" + enviamentsDiaActual + " maxEnvDia=" + maxEnvDia);

                // adapta aquesta crida segons la teva signatura
                printMissatgeDies(msg, aplicacio, maxEnvMinut, maxEnvDia, isHorariLaboral, diaLaboral);
            }

            return msg.isBlank() ? null : msg;

        } catch (Exception ex) {
            var msg = "Error checkejant el limit d'enviaments per l'aplicacio";
            log.error(msg, ex);
            return null;
        }
    }

    // placeholders
    private void printMissatgeDies(String msg, Object aplicacio, int maxEnvMinut, int maxEnvDia, boolean isHorariLaboral, boolean diaLaboral) {}
}