package es.caib.notib.core.api.dto;

import java.io.Serializable;

public class EntregaDehDto implements Serializable{

	
	private boolean obligat;
	private String procedimentCodi;
	private String emisorNif;

	public boolean isObligat() {
		return obligat;
	}
	public void setObligat(boolean obligat) {
		this.obligat = obligat;
	}
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}
	public String getEmisorNif() {
		return emisorNif;
	}
	public void setEmisorNif(String emisorNif) {
		this.emisorNif = emisorNif;
	}
	
	private static final long serialVersionUID = 5160556424872017273L;
}
