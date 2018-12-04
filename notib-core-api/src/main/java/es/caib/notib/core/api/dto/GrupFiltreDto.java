package es.caib.notib.core.api.dto;

import java.io.Serializable;

public class GrupFiltreDto implements Serializable{

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
	

	private static final long serialVersionUID = 2436397125477145283L;
}
