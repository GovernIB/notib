/**
 * 
 */
package es.caib.notib.logic.intf.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Informació d'auditoria.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuditoriaDto implements Serializable {

	private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";

	protected UsuariDto createdBy;
	protected Date createdDate;
	protected UsuariDto lastModifiedBy;
	protected Date lastModifiedDate;

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
