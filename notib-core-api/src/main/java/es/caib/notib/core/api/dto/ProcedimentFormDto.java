package es.caib.notib.core.api.dto;

import java.io.Serializable;

public class ProcedimentFormDto extends AuditoriaDto implements Serializable{

	private Long id;
	private String codi;
	private String nom;
	private String entitatNom;
	private String pagadorpostal;
	private String pagadorcie;
	private boolean agrupar;
	private Integer retard;
	
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

	public String getEntitatNom() {
		return entitatNom;
	}

	public void setEntitatNom(String entitatNom) {
		this.entitatNom = entitatNom;
	}

	public String getPagadorpostal() {
		return pagadorpostal;
	}

	public void setPagadorpostal(String pagadorpostal) {
		this.pagadorpostal = pagadorpostal;
	}

	public String getPagadorcie() {
		return pagadorcie;
	}

	public void setPagadorcie(String pagadorcie) {
		this.pagadorcie = pagadorcie;
	}

	public Integer getRetard() {
		return retard;
	}

	public void setRetard(Integer retard) {
		this.retard = retard;
	}

	public boolean isAgrupar() {
		return agrupar;
	}

	public void setAgrupar(boolean agrupar) {
		this.agrupar = agrupar;
	}

	private static final long serialVersionUID = 6058789232924135932L;

}
