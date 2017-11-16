/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Informació del pagador CIE d'un enviament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@JsonAutoDetect
public class PagadorCie {

	private String dir3Codi;
	private Date contracteDataVigencia;

	public String getDir3Codi() {
		return dir3Codi;
	}
	public void setDir3Codi(String dir3Codi) {
		this.dir3Codi = dir3Codi;
	}
	public Date getContracteDataVigencia() {
		return contracteDataVigencia;
	}
	public void setContracteDataVigencia(Date contracteDataVigencia) {
		this.contracteDataVigencia = contracteDataVigencia;
	}

}
