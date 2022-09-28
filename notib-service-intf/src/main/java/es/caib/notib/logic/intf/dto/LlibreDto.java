package es.caib.notib.logic.intf.dto;

import java.io.Serializable;

public class LlibreDto implements Serializable{

	private String codi;
	private String nomCurt;
	private String nomLlarg;
	private String organismeCodi;
	
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNomCurt() {
		return nomCurt;
	}
	public void setNomCurt(String nomCurt) {
		this.nomCurt = nomCurt;
	}
	public String getNomLlarg() {
		return nomLlarg;
	}
	public void setNomLlarg(String nomLlarg) {
		this.nomLlarg = nomLlarg;
	}
	public String getOrganismeCodi() {
		return organismeCodi;
	}
	public void setOrganismeCodi(String organismeCodi) {
		this.organismeCodi = organismeCodi;
	}




	private static final long serialVersionUID = -3831959843313056718L;
	
}
