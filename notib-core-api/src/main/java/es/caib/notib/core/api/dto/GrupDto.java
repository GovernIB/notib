package es.caib.notib.core.api.dto;

import java.io.Serializable;

public class GrupDto extends AuditoriaDto implements Serializable {

	private Long id;
	private String codi;
	private String nom;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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


	private static final long serialVersionUID = 7999677809220395478L;

}
