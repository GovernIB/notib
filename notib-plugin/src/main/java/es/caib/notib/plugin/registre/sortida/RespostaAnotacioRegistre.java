package es.caib.notib.plugin.registre.sortida;

import java.util.Date;

public class RespostaAnotacioRegistre {
	
	private String numero;
	private String numeroRegistroFormateado;
	private Date data;
//	private ReferenciaRDSJustificante referenciaRDSJustificante;
	
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getNumeroRegistroFormateado() {
		return numeroRegistroFormateado;
	}
	public void setNumeroRegistroFormateado(String numeroRegistroFormateado) {
		this.numeroRegistroFormateado = numeroRegistroFormateado;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
//	public ReferenciaRDSJustificante getReferenciaRDSJustificante() {
//		return referenciaRDSJustificante;
//	}
//	public void setReferenciaRDSJustificante(ReferenciaRDSJustificante referenciaRDSJustificante) {
//		this.referenciaRDSJustificante = referenciaRDSJustificante;
//	}
	
}
