/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Informació d'una notificació per al seu enviament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@JsonAutoDetect
@XmlRootElement
public class DadesConsulta {

	String identificador;
	String referencia;
	boolean ambJustificant;
	
	public String getIdentificador() {
		return identificador;
	}

	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public boolean isAmbJustificant() {
		return ambJustificant;
	}

	public void setAmbJustificant(boolean ambJustificant) {
		this.ambJustificant = ambJustificant;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
