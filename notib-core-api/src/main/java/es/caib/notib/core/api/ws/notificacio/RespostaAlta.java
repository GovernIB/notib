/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Informació retornada per l'alta d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@JsonAutoDetect
@XmlRootElement
public class RespostaAlta {

	private String identificador;
	private NotificacioEstatEnum estat;
	private boolean error;
	private String errorDescripcio;
	private List<EnviamentReferencia> referencies;

	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
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
	public String getErrorDescripcio() {
		return errorDescripcio;
	}
	public void setErrorDescripcio(String errorDescripcio) {
		this.errorDescripcio = errorDescripcio;
	}
	public List<EnviamentReferencia> getReferencies() {
		return referencies;
	}
	public void setReferencies(List<EnviamentReferencia> referencies) {
		this.referencies = referencies;
	}

}
