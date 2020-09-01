/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.io.Serializable;

/**
 * Informació d'un arxiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CacheDto implements Serializable {
	
	private String codi;
	private String descripcio;
	
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	private static final long serialVersionUID = 3838945683812678609L;
}
