package es.caib.notib.core.api.dto;

import java.io.Serializable;
import java.util.Date;

public class PagadorCieFormatFullaDto extends AuditoriaDto implements Serializable{

	private Long id;
	private String codi;
	private Date pagadorCieId;	

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
	public Date getPagadorCieId() {
		return pagadorCieId;
	}
	public void setPagadorCieId(Date pagadorCieId) {
		this.pagadorCieId = pagadorCieId;
	}

	private static final long serialVersionUID = 4814433005549236274L;
}
