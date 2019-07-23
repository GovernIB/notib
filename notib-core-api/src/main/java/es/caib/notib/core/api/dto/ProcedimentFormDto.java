package es.caib.notib.core.api.dto;

import java.io.Serializable;
import java.util.List;

public class ProcedimentFormDto extends AuditoriaDto implements Serializable{

	private Long id;
	private String codi;
	private String nom;
	private String entitatNom;
	private String pagadorpostal;
	private String pagadorcie;
	private boolean agrupar;
	private Integer retard;
	private List<PermisDto> permisos;
	private List<GrupDto> grups;
	
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

	public List<PermisDto> getPermisos() {
		return permisos;
	}

	public void setPermisos(List<PermisDto> permisos) {
		this.permisos = permisos;
	}	
	
	public List<GrupDto> getGrups() {
		return grups;
	}

	public void setGrups(List<GrupDto> grups) {
		this.grups = grups;
	}

	public int getPermisosCount() {
		if  (permisos == null)
			return 0;
		else
			return permisos.size();
	}
	public int getGrupsCount() {
		if  (grups == null)
			return 0;
		else
			return grups.size();
	}


	private static final long serialVersionUID = 6058789232924135932L;

}
