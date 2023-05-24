/**
 * 
 */
package es.caib.notib.logic.intf.exception;

/**
 * Excepció que es llança quan l'objecte especificat no existeix.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class EventException extends RuntimeException {
    public EventException(String message) {
        super(message);
    }
}
