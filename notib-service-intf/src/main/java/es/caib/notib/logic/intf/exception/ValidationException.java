/**
 * 
 */
package es.caib.notib.logic.intf.exception;

/**
 * Excepció que es llança per errors validant un objecte o el seu estat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ValidationException extends RuntimeException {

	private Object objectId;
	private Class<?> objectClass;
	private String error;
	
	public ValidationException(Object objectId, Class<?> objectClass, String error) {

		super(error);
		this.objectId = objectId;
		this.objectClass = objectClass;
		this.error = error;
	}
	public ValidationException(Object objectId, String error) {

		super(error);
		this.objectId = objectId;
		this.error = error;
	}
	public ValidationException(String error) {

		super(error);
		this.error = error;
	}

	public Object getObjectId() {
		return objectId;
	}
	public Class<?> getObjectClass() {
		return objectClass;
	}
	public String getError() {
		return error;
	}

	public String getErrorInfo() {

		if (objectClass == null || objectId == null) {
			return error;
		}
		return error + " (" + objectClass.getName() + "#" + objectId + ")";

	}

}
