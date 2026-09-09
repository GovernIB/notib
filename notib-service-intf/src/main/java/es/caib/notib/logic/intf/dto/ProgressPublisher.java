package es.caib.notib.logic.intf.dto;

/**
 * Callback per a publicar el progrés d'una operació llarga (percentatge + missatge).
 *
 * @author Límit Tecnologies
 */
@FunctionalInterface
public interface ProgressPublisher {

	void publish(int percent, String message);

}
