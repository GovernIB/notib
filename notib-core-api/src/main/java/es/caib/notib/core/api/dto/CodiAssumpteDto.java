package es.caib.notib.core.api.dto;

import java.io.Serializable;

public class CodiAssumpteDto implements Serializable {
	private String codi;
	private String nom;
	private String tipusAssumpte;
	
	public String getCodi() {
		return codi;
	}
	public void setCodi(
			String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(
			String nom) {
		this.nom = nom;
	}
	public String getTipusAssumpte() {
		return tipusAssumpte;
	}
	public void setTipusAssumpte(
			String tipusAssumpte) {
		this.tipusAssumpte = tipusAssumpte;
	}
}
