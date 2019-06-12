package es.caib.notib.core.api.dto;

import java.io.Serializable;

public class PagadorCieFormatSobreDto extends AuditoriaDto implements Serializable{

	private Long id;
	private String codi;
	private Long pagadorCieId;	

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
	public Long getPagadorCieId() {
		return pagadorCieId;
	}
	public void setPagadorCieId(Long pagadorCieId) {
		this.pagadorCieId = pagadorCieId;
	}

	private static final long serialVersionUID = -2057306471713763412L;
}
