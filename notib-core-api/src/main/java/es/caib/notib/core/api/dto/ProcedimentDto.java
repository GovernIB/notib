package es.caib.notib.core.api.dto;

import java.io.Serializable;
import java.util.List;

public class ProcedimentDto extends AuditoriaDto implements Serializable{

	private Long id;
	private String codi;
	private String nom;
	private String codisia;
	private EntitatDto  entitat;
	private PagadorPostalDto pagadorpostal;
	private PagadorCieDto pagadorcie;
	private boolean agrupar;
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
	public String getCodisia() {
		return codisia;
	}
	public void setCodisia(String codisia) {
		this.codisia = codisia;
	}
	public boolean isAgrupar() {
		return agrupar;
	}
	public void setAgrupar(boolean agrupar) {
		this.agrupar = agrupar;
	}
	public EntitatDto getEntitat() {
		return entitat;
	}
	public void setEntitat(EntitatDto entitat) {
		this.entitat = entitat;
	}
	public PagadorPostalDto getPagadorpostal() {
		return pagadorpostal;
	}
	public void setPagadorpostal(PagadorPostalDto pagadorpostal) {
		this.pagadorpostal = pagadorpostal;
	}
	public PagadorCieDto getPagadorcie() {
		return pagadorcie;
	}
	public void setPagadorcie(PagadorCieDto pagadorcie) {
		this.pagadorcie = pagadorcie;
	}
	public List<GrupDto> getGrups() {
		return grups;
	}
	public void setGrups(List<GrupDto> grupsDto) {
		this.grups = grupsDto;
	}



	private static final long serialVersionUID = 6058789232924135932L;

}
