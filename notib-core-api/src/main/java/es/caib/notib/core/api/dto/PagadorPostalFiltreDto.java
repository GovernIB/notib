package es.caib.notib.core.api.dto;

import java.util.Date;

/**
 * Filtre per a la consulta de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PagadorPostalFiltreDto {
	
	private String dir3codi;
	private String contracteNum;
	
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
	
}
