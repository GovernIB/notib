/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Informació retornada per l'alta d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@JsonAutoDetect
@XmlRootElement
public class RespostaAlta {
	private String identificador;
	private NotificacioEstatEnum estat;
	private List<EnviamentReferencia> referencies;
	private boolean error;
	private String errorDescripcio;
}
