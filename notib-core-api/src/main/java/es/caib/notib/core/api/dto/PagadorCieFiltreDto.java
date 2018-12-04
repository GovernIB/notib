package es.caib.notib.core.api.dto;

import java.io.Serializable;
import java.util.Date;

public class PagadorCieFiltreDto implements Serializable{

	private String dir3codi;
	private Date contracteDataVig;
	
	
	public String getDir3codi() {
		return dir3codi;
	}
	public void setDir3codi(String dir3codi) {
		this.dir3codi = dir3codi;
	}
	public Date getContracteDataVig() {
		return contracteDataVig;
	}
	public void setContracteDataVig(Date contracteDataVig) {
		this.contracteDataVig = contracteDataVig;
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 1305599728317046741L;

}
