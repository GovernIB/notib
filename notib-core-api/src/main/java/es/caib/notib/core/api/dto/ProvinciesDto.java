package es.caib.notib.core.api.dto;

import java.io.Serializable;

public class ProvinciesDto implements Serializable {

	private String id;
	private String descripcio;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	
	
}
