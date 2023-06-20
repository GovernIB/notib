/**
 * 
 */
package es.caib.notib.logic.intf.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Informació d'auditoria.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class AuditoriaDto implements Serializable {

	private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";

	private UsuariDto createdBy;
	private Date createdDate;
	private UsuariDto lastModifiedBy;
	private Date lastModifiedDate;

	public String getCreatedDateAmbFormat() {
		if (createdDate == null) return null;
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		return sdf.format(createdDate);
	}
	public String getLastModifiedDateAmbFormat() {
		if (lastModifiedDate == null) return null;
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		return sdf.format(lastModifiedDate);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
