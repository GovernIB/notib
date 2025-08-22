package es.caib.notib.plugin;

import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.EstatSalutEnum;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import lombok.Setter;
import lombok.Synchronized;

import java.util.concurrent.TimeUnit;

public class AbstractSalutPlugin implements SalutPlugin {

    // Mètodes de SALUT
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private MeterRegistry registry;
    private MeterRegistry localRegistry;
    private String codiPlugin;

    @Setter
    protected boolean configuracioEspecifica = false;
    private EstatSalutEnum darrerEstat = EstatSalutEnum.UNKNOWN;

    private Timer timerOkGlobal;
    private Counter counterErrorGlobal;
    private Timer timerOk;
    private Counter counterError;

    // Llindars d'avís en percentatge (0-100)
    private static final int DOWN_PCT = 100;     // 100% errors
    private static final int ERROR_GT_PCT = 30;  // >30% errors
    private static final int DEGRADED_GT_PCT = 10; // >10% errors
    private static final int UP_LT_PCT = 5;      // <5% errors

    public void init(MeterRegistry registry, String codiPlugin) {
        this.registry = registry;
        this.codiPlugin = codiPlugin;

        timerOkGlobal = Timer.builder("plugin." + codiPlugin)
                .tags("result", "success")
                .publishPercentiles(0.5, 0.75, 0.95, 0.99)
                .publishPercentileHistogram()
                .register(registry);
        counterErrorGlobal = Counter.builder("plugin." + codiPlugin).register(registry);

        initializeLocalTimers();
    }

    private void initializeLocalTimers() {
        if (localRegistry != null) {
            localRegistry.close(); // descarta mètriques existents
        }
        localRegistry = new SimpleMeterRegistry();

        timerOk = Timer.builder("plugin.peticions.ok")
                .tags("result", "success")
                .publishPercentiles(0.5, 0.75, 0.95, 0.99)
                .publishPercentileHistogram()
                .register(localRegistry);
        counterError = Counter.builder("plugin.peticions.error").register(localRegistry);
    }

    @Synchronized
    public void incrementarOperacioOk(Long durada) {
        timerOkGlobal.record(durada, TimeUnit.MILLISECONDS);
        timerOk.record(durada, TimeUnit.MILLISECONDS);
    }

    @Synchronized
    public void incrementarOperacioError() {
        counterErrorGlobal.increment();
        counterError.increment();
    }

    @Synchronized
    private void resetComptadors() {
        initializeLocalTimers();
    }

    @Override
    public boolean teConfiguracioEspecifica() {
        return this.configuracioEspecifica;
    }

    @Override
    public EstatSalut getEstatPlugin() {
        Integer duradaMitja = null;
        Long totalPeticionsOk = null;
        Long totalPeticionsError = null;
        
        if (timerOk != null) {
            duradaMitja = (int) timerOk.mean(TimeUnit.MILLISECONDS);
            totalPeticionsOk = timerOk.count();
        }
        if (counterError != null) {
            totalPeticionsError = (long) counterError.count();
        }
        final EstatSalutEnum estatCalculat = calculaEstat(totalPeticionsOk, totalPeticionsError);
        darrerEstat = estatCalculat;

        return EstatSalut.builder()
                .latencia(duradaMitja)
                .estat(estatCalculat)
                .build();
        
    }

    private EstatSalutEnum calculaEstat(Long totalPeticionsOk, Long totalPeticionsError) {
        final long peticionsOkSegures = (totalPeticionsOk != null) ? totalPeticionsOk : 0L;
        final long peticionsErrorSegures = (totalPeticionsError != null) ? totalPeticionsError : 0L;

        final long totalOperacions = peticionsOkSegures + peticionsErrorSegures;
        if (totalOperacions == 0L) {
            return darrerEstat;
        }

        // Percentatge d'errors arrodonit correctament evitant divisió d'enters
        final int errorRatePct = (int) Math.round((peticionsErrorSegures * 100.0) / totalOperacions);

        if (errorRatePct >= DOWN_PCT) {
            return EstatSalutEnum.DOWN;
        } else if (errorRatePct > ERROR_GT_PCT) {
            return EstatSalutEnum.ERROR;
        } else if (errorRatePct > DEGRADED_GT_PCT) {
            return EstatSalutEnum.DEGRADED;
        } else if (errorRatePct < UP_LT_PCT) {
            return EstatSalutEnum.UP;
        } else {
            return EstatSalutEnum.WARN; // 5-10%
        }
    }

    @Override
    public IntegracioPeticions getPeticionsPlugin() {
        Long peticionsOk = timerOk != null ? timerOk.count() : null;
        Long peticionsError = counterError != null ? (long) counterError.count() : null;
        
        IntegracioPeticions integracioPeticions = IntegracioPeticions.builder()
                .totalOk(peticionsOk)
                .totalError(peticionsError)
                .build();
        resetComptadors();
        return integracioPeticions;
    }

}
