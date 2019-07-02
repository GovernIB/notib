/**
 * 
 */
package es.caib.notib.plugin.unitat;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Informació d'una tupla id-descripció.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data 
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CodiValor implements Serializable, Comparable<CodiValor> {

	@NonNull
	@JsonProperty("id")
	private String id;
	@NonNull
	@JsonProperty("descripcion")
	private String descripcio;
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescripcio() {
		return descripcio;
	}

	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	@Override
	public int compareTo(CodiValor o) {
		return descripcio.compareToIgnoreCase(o.getDescripcio());
	}
	
	private static final long serialVersionUID = -5602898182576627524L;

}
