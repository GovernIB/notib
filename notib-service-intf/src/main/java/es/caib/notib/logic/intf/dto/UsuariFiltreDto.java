/**
 * 
 */
package es.caib.notib.logic.intf.dto;

import java.io.Serializable;


/**
 * Informació d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UsuariFiltreDto implements Serializable {

	private String codi;
	private String nom;

	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	private static final long serialVersionUID = -139254994389509932L;

}
