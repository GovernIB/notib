package es.caib.notib.core.api.service.rest;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@XmlRootElement
@JsonAutoDetect
public class LecturaNotificacio {

	public String referencia;			// Referència interna
	public Date dataLectura;			// Data de lectura de la notificació
	
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public Date getDataLectura() {
		return dataLectura;
	}
	public void setDataLectura(Date dataLectura) {
		this.dataLectura = dataLectura;
	} 
	
}
