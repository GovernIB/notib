/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Informació de referència d'un enviament retornada per Notifica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@JsonAutoDetect
@XmlRootElement
@Getter @Setter
public class EnviamentReferencia {

	private String titularNif;
	private String referencia;

	public EnviamentReferencia() {

	}

	public EnviamentReferencia(String titularNif, String referencia) {
		this.titularNif = titularNif;
		this.referencia = referencia;
	}
}
