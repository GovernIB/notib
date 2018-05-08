/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Informació retornada per la consulta de l'estat d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@JsonAutoDetect
@XmlRootElement
public class RespostaConsultaEstatNotificacio {

	private NotificacioEstatEnum estat;
	private boolean error;
	private Date errorData;
	private String errorDescripcio;

	public NotificacioEstatEnum getEstat() {
		return estat;
	}
	public void setEstat(NotificacioEstatEnum estat) {
		this.estat = estat;
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
