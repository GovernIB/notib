/**
 * 
 */
package es.caib.notib.core.api.exception;

/**
 * Excepció que es llança quan l'objecte especificat no existeix.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class NotFoundException extends RuntimeException {

	private Object objectId;
	private Class<?> objectClass;
	private String message;
	
	public NotFoundException(
			Object objectId,
			Class<?> objectClass) {
		super(getExceptionMessage(objectId, objectClass));
		this.objectId = objectId;
		this.objectClass = objectClass;
	}
	
	public NotFoundException(
			Object objectId,
			Class<?> objectClass,
			String message) {
		super(getExceptionMessage(objectId, objectClass, message));
		this.objectId = objectId;
		this.objectClass = objectClass;
		this.message = message;
	}

	public Object getObjectId() {
		return objectId;
	}

	public Class<?> getObjectClass() {
		return objectClass;
	}

	public String getMessage() {
		return message;
	}
	
	public static String getExceptionMessage(
			Object objectId,
			Class<?> objectClass) {
		return getExceptionMessage(
				objectId, 
				objectClass, 
				null);
	}
	
	public static String getExceptionMessage(
			Object objectId,
			Class<?> objectClass,
			String message) {
		StringBuilder sb = new StringBuilder();
		if (message != null)
			sb.append(message + ". ");
		if (objectClass != null)
			sb.append(objectClass.getName());
		else
			sb.append("null");
		sb.append("#");
		if (objectId != null)
			sb.append(objectId.toString());
		else
			sb.append("null");
		return sb.toString();
	}

}
