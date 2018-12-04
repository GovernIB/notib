package es.caib.notib.core.api.dto;

import java.io.Serializable;
import java.util.Date;

public class PagadorPostalDto extends AuditoriaDto implements Serializable {

	private Long id;
	private String dir3codi;
	private String contracteNum;
	private Date contracteDataVig;
	private String facturacioClientCodi;
	
	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDir3codi() {
		return dir3codi;
	}
	public void setDir3codi(String dir3codi) {
		this.dir3codi = dir3codi;
	}
	public String getContracteNum() {
		return contracteNum;
	}
	public void setContracteNum(String contracteNum) {
		this.contracteNum = contracteNum;
	}
	public Date getContracteDataVig() {
		return contracteDataVig;
	}
	public void setContracteDataVig(Date contracteDataVig) {
		this.contracteDataVig = contracteDataVig;
	}
	public String getFacturacioClientCodi() {
		return facturacioClientCodi;
	}
	public void setFacturacioClientCodi(String facturacioClientCodi) {
		this.facturacioClientCodi = facturacioClientCodi;
	}



	/**
	 * 
	 */
	private static final long serialVersionUID = 6875716151909763392L;

}
