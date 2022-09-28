package es.caib.notib.logic.intf.exception;

/**
 * Excepció que indica que han sorgit errors en el procés de registrar o notificar
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreNotificaException extends Exception {

	private static final long serialVersionUID = 1L;

	public RegistreNotificaException(String msg) {
        super(msg);
    }

    public RegistreNotificaException(String msg, Throwable t) {
        super(msg, t);
    }

}
