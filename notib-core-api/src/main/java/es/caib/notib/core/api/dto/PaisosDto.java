package es.caib.notib.core.api.dto;

import java.io.Serializable;

public class PaisosDto implements Serializable {

	private String alfa2Pais;
	private String alfa3Pais;
	private Long codiPais;
	private String descripcioPais;
	
	public String getAlfa2Pais() {
		return alfa2Pais;
	}
	public void setAlfa2Pais(String alfa2Pais) {
		this.alfa2Pais = alfa2Pais;
	}
	public String getAlfa3Pais() {
		return alfa3Pais;
	}
	public void setAlfa3Pais(String alfa3Pais) {
		this.alfa3Pais = alfa3Pais;
	}
	public Long getCodiPais() {
		return codiPais;
	}
	public void setCodiPais(Long codiPais) {
		this.codiPais = codiPais;
	}
	public String getDescripcioPais() {
		return descripcioPais;
	}
	public void setDescripcioPais(String descripcioPais) {
		this.descripcioPais = descripcioPais;
	}
	

	private static final long serialVersionUID = 2208161112358920849L;
}
