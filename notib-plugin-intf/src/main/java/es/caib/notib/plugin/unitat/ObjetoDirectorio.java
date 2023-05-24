/**
 * 
 */
package es.caib.notib.plugin.unitat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;

/**
 * Informació d'una unitat organitzativa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data 
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ObjetoDirectorio implements Serializable, Comparable<ObjetoDirectorio> {

	@NonNull
	@JsonProperty("codigo")
	private String codi;
	@NonNull
	@JsonProperty("denominacion")
	private String denominacio;
	
	@Override
	public int compareTo(ObjetoDirectorio o) {
		return denominacio.compareToIgnoreCase(o.getDenominacio());
	}
	
	private static final long serialVersionUID = -5602898182576627524L;

}
