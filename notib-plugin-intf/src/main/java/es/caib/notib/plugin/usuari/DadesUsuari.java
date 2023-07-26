/**
 * 
 */
package es.caib.notib.plugin.usuari;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Dades d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DadesUsuari implements Serializable {

	private String codi;
	private String nomSencer;
	private String nom;
	private String llinatges;
	private String nif;
	private String email;

	public String getNomSencer() {

		if (nomSencer != null) {
			return nomSencer;
		}
		if (nom == null) {
			return null;
		}
		return llinatges != null ? nom + " " + llinatges :nom;
	}
	
	public String getNomSencerAmbCodi() {
		if(getNomSencer() != null) {
			return getNomSencer() + " ("+codi+")"; 
		}
		return codi;
	}
	
	private static final long serialVersionUID = -139254994389509932L;

}
