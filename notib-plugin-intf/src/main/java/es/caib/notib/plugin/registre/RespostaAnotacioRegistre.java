package es.caib.notib.plugin.registre;

import java.util.Date;

public class RespostaAnotacioRegistre {
	
	private String numero;
	private String numeroRegistroFormateado;
	private Date data;
    private String errorCodi;
    private String errorDescripcio;
	
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
	public String getErrorCodi() {
		return errorCodi;
	}
	public void setErrorCodi(String errorCodi) {
		this.errorCodi = errorCodi;
	}
	public String getErrorDescripcio() {
		return errorDescripcio;
	}
	public void setErrorDescripcio(String errorDescripcio) {
		this.errorDescripcio = errorDescripcio;
	}
	
}
