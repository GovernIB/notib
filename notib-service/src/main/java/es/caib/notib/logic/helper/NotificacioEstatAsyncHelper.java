package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.model.SseEvent;
import es.caib.notib.logic.intf.resourceservice.SseEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Càlcul asíncron de la columna estat de les remeses del llistat (not_notificacio_table.estat_string).
 *
 * El llistat retorna immediatament el darrer valor persistit de les remeses pendents d'actualitzar
 * (marcades amb estatPendent) i, en segon pla, se'n recalcula l'estat per blocs. Cada bloc
 * calculat s'envia via SSE (cua REMESA_ENVIAMENT_ESTAT, event NOTIFICACIO_ESTAT_CALCULAT) només a
 * l'usuari que ha fet la consulta, que actualitza les files visibles del grid.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificacioEstatAsyncHelper {

	public static final String PROPERTY_ESTAT_ASINCRON = "es.caib.notib.app.llistat.remeses.estat.asincron";

	// Remeses per transacció: cada bloc es persisteix i s'envia al client tan bon punt està calculat
	private static final int MIDA_BLOC = 20;
	private static final int FILS = 2;
	// Peticions de càlcul en espera. Si s'omple, les noves es descarten: les remeses continuen
	// marcades per_actualitzar i es calcularan a la propera consulta (o via el fallback del client).
	private static final int CUA_MAXIMA = 200;

	private final LegacyHelper legacyHelper;
	private final SseEventService sseEventService;

	private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
			FILS,
			FILS,
			60L,
			TimeUnit.SECONDS,
			new ArrayBlockingQueue<>(CUA_MAXIMA),
			threadFactory());

	// Remeses amb el càlcul en curs o en espera, amb els usuaris als quals s'ha d'enviar el resultat.
	// Evita calcular dues vegades la mateixa remesa si diversos usuaris la consulten alhora.
	private final Map<Long, Set<String>> pendents = new HashMap<>();

	private static java.util.concurrent.ThreadFactory threadFactory() {
		var counter = new AtomicInteger();
		return runnable -> {
			var thread = new Thread(runnable, "notib-estat-remeses-" + counter.incrementAndGet());
			thread.setDaemon(true);
			return thread;
		};
	}

	@PreDestroy
	public void shutdown() {
		executor.shutdownNow();
	}

	/**
	 * Programa el càlcul asíncron de la columna estat de les remeses indicades. Si hi ha una
	 * transacció activa, el càlcul s'inicia quan aquesta acaba.
	 *
	 * @param notificacioIds
	 *            ids de les remeses pendents d'actualitzar.
	 * @param usuariCodi
	 *            usuari al qual s'enviaran (via SSE) els estats calculats.
	 */
	public void calcularEstatsAsync(Collection<Long> notificacioIds, String usuariCodi) {

		List<Long> nous = new ArrayList<>();
		synchronized (pendents) {
			for (var id : notificacioIds) {
				var usuaris = pendents.get(id);
				if (usuaris == null) {
					usuaris = new HashSet<>();
					pendents.put(id, usuaris);
					nous.add(id);
				}
				usuaris.add(usuariCodi);
			}
		}
		if (nous.isEmpty()) {
			return;
		}
		// Una tasca per bloc (i no una per petició) perquè els fils del pool calculin en paral·lel
		// els blocs d'una mateixa pàgina. DelegatingSecurityContextRunnable: el càlcul tradueix
		// missatges a l'idioma de l'usuari actual.
		var entitatCodi = ConfigHelper.getEntitatCodi().get();
		List<List<Long>> blocs = new ArrayList<>();
		for (int i = 0; i < nous.size(); i += MIDA_BLOC) {
			blocs.add(new ArrayList<>(nous.subList(i, Math.min(i + MIDA_BLOC, nous.size()))));
		}
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCompletion(int status) {
					blocs.forEach(bloc -> programar(bloc, entitatCodi));
				}
			});
		} else {
			blocs.forEach(bloc -> programar(bloc, entitatCodi));
		}
	}

	private void programar(List<Long> bloc, String entitatCodi) {

		try {
			executor.execute(new DelegatingSecurityContextRunnable(() -> calcular(bloc, entitatCodi)));
		} catch (RejectedExecutionException ex) {
			log.warn("[NotificacioEstatAsync] Cua de càlcul plena, es descarta el càlcul de {} remeses", bloc.size());
			synchronized (pendents) {
				bloc.forEach(pendents::remove);
			}
		}
	}

	private void calcular(List<Long> bloc, String entitatCodi) {

		ConfigHelper.setEntitatCodi(entitatCodi);
		try {
			var t0 = System.currentTimeMillis();
			var estats = actualitzarColumnesEstat(legacyHelper, bloc);
			log.debug("[NotificacioEstatAsync] Bloc de {} remeses calculat en {} ms (pendents a la cua: {})", bloc.size(), System.currentTimeMillis() - t0, executor.getQueue().size());
			enviar(bloc, estats);
		} finally {
			ConfigHelper.setEntitatCodi(null);
		}
	}

	/**
	 * Recalcula la columna estat de les remeses indicades en una sola transacció. Si falla (per
	 * exemple, en desar alguna de les remeses), les recalcula una a una, cadascuna en una transacció
	 * pròpia, perquè una remesa amb problemes no impedeixi calcular les altres.
	 *
	 * @return el valor de la columna estat de cada remesa calculada, indexat per id.
	 */
	public static Map<Long, String> actualitzarColumnesEstat(LegacyHelper legacyHelper, Collection<Long> notificacioIds) {

		try {
			return legacyHelper.actualitzarColumnesEstat(notificacioIds);
		} catch (Exception ex) {
			if (notificacioIds.size() == 1) {
				log.error("[NotificacioEstatAsync] Error calculant l'estat de la remesa " + notificacioIds, ex);
				return new HashMap<>();
			}
			log.warn("[NotificacioEstatAsync] Error calculant l'estat del bloc de remeses " + notificacioIds + ", es recalculen una a una", ex);
		}
		Map<Long, String> estats = new HashMap<>();
		for (var id : notificacioIds) {
			try {
				estats.putAll(legacyHelper.actualitzarColumnesEstat(List.of(id)));
			} catch (Exception ex) {
				log.error("[NotificacioEstatAsync] Error calculant l'estat de la remesa " + id, ex);
			}
		}
		return estats;
	}

	private void enviar(List<Long> bloc, Map<Long, String> estats) {

		Map<String, Map<Long, String>> estatsPerUsuari = new HashMap<>();
		synchronized (pendents) {
			for (var id : bloc) {
				var usuaris = pendents.remove(id);
				var estat = estats.get(id);
				// Si el càlcul ha fallat no s'envia res: el client, passat un temps, demana la
				// remesa individualment (càlcul síncron) o en conserva el valor anterior
				if (usuaris == null || estat == null) {
					continue;
				}
				for (var usuari : usuaris) {
					estatsPerUsuari.computeIfAbsent(usuari, u -> new HashMap<>()).put(id, estat);
				}
			}
		}
		estatsPerUsuari.forEach((usuari, estatsUsuari) -> sseEventService.publishEvent(
				SseEventService.SseQueue.REMESA_ENVIAMENT_ESTAT,
				SseEvent.estatsRemesaCalculats(estatsUsuari, usuari)));
	}

}
