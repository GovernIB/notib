package es.caib.notib.logic.intf.resourceservice;

import es.caib.notib.logic.intf.model.SseEvent;
import es.caib.notib.logic.intf.model.sse.AvisosActiusEvent;

import java.util.function.Consumer;

/**
 * Definició del servei que gestiona l'enviament de Server Sent Events (SSE).
 *
 * @author Límit Tecnologies
 */
public interface SseEventService {

	/**
	 * Registra un listener per a una cua. Admet múltiples listeners simultanis per la mateixa cua
	 * (per exemple, un per cada usuari que té el llistat obert): tots reben els events publicats.
	 *
	 * @param queue
	 *            la coa d'events que es vol escoltar.
	 * @param listener
	 *            el consumidor.
	 * @return l'identificador únic d'aquest listener, necessari per eliminar-lo amb removeListener.
	 */
	String addListener(SseQueue queue, Consumer<SseEvent> listener);

	/**
	 * Publica un event. Es reenvia a tots els listeners registrats a la cua indicada.
	 *
	 * @param event
	 *            la informació de l'event.
	 */
	void publishEvent(SseQueue queue, SseEvent event);

	/**
	 * Esborra un listener concret del registre.
	 *
	 * @param queue
	 *            la coa d'events de la qual es vol esborrar el listener.
	 * @param listenerId
	 *            l'identificador retornat per addListener.
	 */
	void removeListener(SseQueue queue, String listenerId);

	enum SseQueue {
		PROGRESS,
		REMESA_ENVIAMENT_ESTAT,
		AVISOS,
		TEST
	}

	/**
	 * Notifica a totes les connexions obertes que els avisos actius han canviat: publica a la cua
	 * AVISOS un event amb tots els avisos actius, un cop confirmada la transacció en curs (si n'hi ha).
	 */
	void notifyAvisosActius();

	/**
	 * Retorna els avisos actius que ha de veure un usuari que opera amb el rol i l'entitat
	 * indicats, amb el mateix criteri que la interfície JSP ({@code AvisHelper.findAvisos}):
	 * amb el rol de superusuari tots els avisos actius, i amb qualsevol altre rol només els
	 * globals (sense entitat) i els de l'entitat de treball.
	 *
	 * @param rol rol amb què opera l'usuari.
	 * @param entitatId identificador de l'entitat de treball, o null si no n'hi ha cap.
	 * @return els avisos actius filtrats.
	 */
	AvisosActiusEvent getAvisosActius(String rol, Long entitatId);

}
