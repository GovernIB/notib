/**
 * 
 */
package es.caib.notib.logic.intf.dto;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Informació genèrica d'una resposta de Notifica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificaRespostaDto implements Serializable {

	private String respostaCodi;
	private String respostaDescripcio;

	public String getRespostaCodi() {
		return respostaCodi;
	}
	public void setRespostaCodi(String respostaCodi) {
		this.respostaCodi = respostaCodi;
	}
	public String getRespostaDescripcio() {
		return respostaDescripcio;
	}
	public void setRespostaDescripcio(String respostaDescripcio) {
		this.respostaDescripcio = respostaDescripcio;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
