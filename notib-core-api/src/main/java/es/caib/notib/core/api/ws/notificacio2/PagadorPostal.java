/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio2;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Informació del pagador postal d'un enviament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@JsonAutoDetect
public class PagadorPostal {

	private String dir3Codi;
	private String contracteNum;
	private Date contracteDataVigencia;
	private String facturacioClientCodi;

	public String getDir3Codi() {
		return dir3Codi;
	}
	public void setDir3Codi(String dir3Codi) {
		this.dir3Codi = dir3Codi;
	}
	public String getContracteNum() {
		return contracteNum;
	}
	public void setContracteNum(String contracteNum) {
		this.contracteNum = contracteNum;
	}
	public Date getContracteDataVigencia() {
		return contracteDataVigencia;
	}
	public void setContracteDataVigencia(Date contracteDataVigencia) {
		this.contracteDataVigencia = contracteDataVigencia;
	}
	public String getFacturacioClientCodi() {
		return facturacioClientCodi;
	}
	public void setFacturacioClientCodi(String facturacioClientCodi) {
		this.facturacioClientCodi = facturacioClientCodi;
	}

}
