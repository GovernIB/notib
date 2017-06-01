/**
 * 
 */
package es.caib.notib.plugin.usuari;

import java.io.Serializable;

/**
 * Dades d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesUsuari implements Serializable {

	private String codi;
	private String nom;
	private String llinatges;
	private String nomSencer;
	private String email;

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
	public String getLlinatges() {
		return llinatges;
	}
	public void setLlinatges(String llinatges) {
		this.llinatges = llinatges;
	}
	public String getNomSencer() {
		return nomSencer;
	}
	public void setNomSencer(String nomSencer) {
		this.nomSencer = nomSencer;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
