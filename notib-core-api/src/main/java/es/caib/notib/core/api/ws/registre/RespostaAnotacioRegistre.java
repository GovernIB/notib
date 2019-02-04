package es.caib.notib.core.api.ws.registre;

import java.util.Date;

/**
 * Resposta a una anotació de registre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RespostaAnotacioRegistre extends RespostaBase {

	private Integer numero;
	private String numeroRegistroFormateado;
	private Date data;
	private String hora;
//	private ReferenciaRDSJustificante referenciaRDSJustificante;
	
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
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
	public String getHora() {
		return hora;
	}
	public void setHora(String hora) {
		this.hora = hora;
	}
//	public ReferenciaRDSJustificante getReferenciaRDSJustificante() {
//		return referenciaRDSJustificante;
//	}
//	public void setReferenciaRDSJustificante(ReferenciaRDSJustificante referenciaRDSJustificante) {
//		this.referenciaRDSJustificante = referenciaRDSJustificante;
//	}
}
