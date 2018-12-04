/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Informació sobre els paràmetres necessaris per a enviar
 * la notificació a la seu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@JsonAutoDetect
public class ParametresRegistre {

	private String organ;
	private String oficina;
	private String llibre;
	
	public String getOrgan() {
		return organ;
	}

	public void setOrgan(String organ) {
		this.organ = organ;
	}

	public String getOficina() {
		return oficina;
	}

	public void setOficina(String oficina) {
		this.oficina = oficina;
	}

	public String getLlibre() {
		return llibre;
	}

	public void setLlibre(String llibre) {
		this.llibre = llibre;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
