/**
 * 
 */
package es.caib.notib.core.api.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AplicacioDto extends AuditoriaDto {

	private Long id;
	private String usuariCodi;
	private String callbackUrl;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getUsuariCodi() {
		return usuariCodi;
	}
	public void setUsuariCodi(String usuariCodi) {
		this.usuariCodi = usuariCodi;
	}
	
	public String getCallbackUrl() {
		return callbackUrl;
	}
	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}
	
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
