/**
 * 
 */
package es.caib.notib.core.api.dto;

import org.apache.commons.lang.builder.ToStringBuilder;



/**
 * Informació d'una entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntitatUsuariDto extends AuditoriaDto {
	
	private EntitatDto entitat;
	private UsuariDto usuari;
	private Long id;
	
	private Boolean usuariAplicacio;
	
	private String callback;
	
	
	public EntitatDto getEntitat() {
		return entitat;
	}
	public void setEntitat(EntitatDto entitat) {
		this.entitat = entitat;
	}
	
	public UsuariDto getUsuari() {
		return usuari;
	}
	public void setUsuari(UsuariDto usuari) {
		this.usuari = usuari;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public boolean isUsuariAplicacio() {
		return usuariAplicacio.booleanValue();
	}
	public void setUsuariAplicacio(boolean usuariAplicacio) {
		this.usuariAplicacio = new Boolean( usuariAplicacio );
	}
	
	public String getCallback() {
		return callback;
	}
	public void setCallback(String callback) {
		this.callback = callback;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;


}
