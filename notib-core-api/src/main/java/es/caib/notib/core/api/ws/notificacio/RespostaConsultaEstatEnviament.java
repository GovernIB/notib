/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Informació retornada per la consulta de l'estat d'un enviament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@JsonAutoDetect
@XmlRootElement
public class RespostaConsultaEstatEnviament {

	private EnviamentEstatEnum estat;
	private Date estatData;
	private String estatDescripcio;
	private String estatOrigen;
	private String receptorNif;
	private String receptorNom;
	private Certificacio certificacio;
	private boolean error;
	private Date errorData;
	private String errorDescripcio;

	public EnviamentEstatEnum getEstat() {
		return estat;
	}
	public void setEstat(EnviamentEstatEnum estat) {
		this.estat = estat;
	}
	public Date getEstatData() {
		return estatData;
	}
	public void setEstatData(Date estatData) {
		this.estatData = estatData;
	}
	public String getEstatDescripcio() {
		return estatDescripcio;
	}
	public void setEstatDescripcio(String estatDescripcio) {
		this.estatDescripcio = estatDescripcio;
	}
	public String getEstatOrigen() {
		return estatOrigen;
	}
	public void setEstatOrigen(String estatOrigen) {
		this.estatOrigen = estatOrigen;
	}
	public String getReceptorNif() {
		return receptorNif;
	}
	public void setReceptorNif(String receptorNif) {
		this.receptorNif = receptorNif;
	}
	public String getReceptorNom() {
		return receptorNom;
	}
	public void setReceptorNom(String receptorNom) {
		this.receptorNom = receptorNom;
	}
	public Certificacio getCertificacio() {
		return certificacio;
	}
	public void setCertificacio(Certificacio certificacio) {
		this.certificacio = certificacio;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public Date getErrorData() {
		return errorData;
	}
	public void setErrorData(Date errorData) {
		this.errorData = errorData;
	}
	public String getErrorDescripcio() {
		return errorDescripcio;
	}
	public void setErrorDescripcio(String errorDescripcio) {
		this.errorDescripcio = errorDescripcio;
	}

}
