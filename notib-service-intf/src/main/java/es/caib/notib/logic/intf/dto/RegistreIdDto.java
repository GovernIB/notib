package es.caib.notib.logic.intf.dto;

import java.util.Date;


/**
 * DTO amb informació d'una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreIdDto {

	private Integer numero;
	private Date data;
	private String descripcioError;
	private String hora;
	private String numeroRegistreFormat;

	
	public String getDescripcioError() {
		return descripcioError;
	}
	public void setDescripcioError(String descripcioError) {
		this.descripcioError = descripcioError;
	}
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
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
	public String getNumeroRegistreFormat() {
		return numeroRegistreFormat;
	}
	public void setNumeroRegistreFormat(
			String numeroRegistreFormat) {
		this.numeroRegistreFormat = numeroRegistreFormat;
	}

}