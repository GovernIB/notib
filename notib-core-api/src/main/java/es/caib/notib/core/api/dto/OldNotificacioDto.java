/**
 * 
 */
package es.caib.notib.core.api.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una Notificacio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class OldNotificacioDto extends AuditoriaDto {

	private Long id;
	private String codi;


	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
