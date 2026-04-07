/**
 *
 */
package es.caib.notib.logic.intf.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.exception.ExceptionUtils;

import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.exception.PermissionDeniedException;
import es.caib.notib.logic.intf.exception.ValidationException;


/**
 * Excepció llençada per un servei.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class ExcepcioLogDto implements Serializable {

	private Long index;
	private EntitatDto entitat;
	private Date data = new Date();
	private Class<?> tipus;
	private Object objectId;
	private Class<?> objectClass;
	private String uri;
	private String param1;
	private String param2;
	private String message;
	private String stacktrace;


	public ExcepcioLogDto(Throwable exception) {

		this.setTipus(exception.getClass());
		this.setMessage(exception.getMessage());
		this.setStacktrace(ExceptionUtils.getStackTrace(exception));

		if (exception instanceof NotFoundException) {
			this.objectId = ((NotFoundException)exception).getObjectId();
			this.objectClass = ((NotFoundException)exception).getObjectClass();
			return;
		}
		if (exception instanceof PermissionDeniedException) {
			this.objectId = ((PermissionDeniedException)exception).getObjectId();
			this.objectClass = ((PermissionDeniedException)exception).getObjectClass();
			this.param1 = ((PermissionDeniedException)exception).getUserName();
			this.param2 = ((PermissionDeniedException)exception).getPermissionName();
			return;
		}
		if (exception instanceof ValidationException) {
			this.objectId = ((ValidationException)exception).getObjectId();
			this.objectClass = ((ValidationException)exception).getObjectClass();
			this.param1 = ((ValidationException)exception).getError();
		}

	}

	public ExcepcioLogDto(String uri, Throwable exception) {

		this.setUri(uri);
		this.setTipus(exception.getClass());
		this.setMessage(exception.getMessage());
		this.setStacktrace(ExceptionUtils.getStackTrace(exception));

		if (exception instanceof NotFoundException) {
			this.objectId = ((NotFoundException)exception).getObjectId();
			this.objectClass = ((NotFoundException)exception).getObjectClass();
			return;
		}
		if (exception instanceof PermissionDeniedException) {
			this.objectId = ((PermissionDeniedException)exception).getObjectId();
			this.objectClass = ((PermissionDeniedException)exception).getObjectClass();
			this.param1 = ((PermissionDeniedException)exception).getUserName();
			this.param2 = ((PermissionDeniedException)exception).getPermissionName();
			return;
		}
		if (exception instanceof ValidationException) {
			this.objectId = ((ValidationException)exception).getObjectId();
			this.objectClass = ((ValidationException)exception).getObjectClass();
			this.param1 = ((ValidationException)exception).getError();
		}

	}

}
