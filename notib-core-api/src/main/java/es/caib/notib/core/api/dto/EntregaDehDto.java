package es.caib.notib.core.api.dto;

import java.io.Serializable;

public class EntregaDehDto implements Serializable{

	
	private boolean obligat;
	private String procedimentCodi;
	private String nif;

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
	public String getNif() {
		return nif;
	}
	public void setNif(String nif) {
		this.nif = nif;
	}

	private static final long serialVersionUID = 5160556424872017273L;
}
