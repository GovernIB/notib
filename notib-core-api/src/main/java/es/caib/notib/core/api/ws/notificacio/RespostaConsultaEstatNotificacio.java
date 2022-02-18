/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Informació retornada per la consulta de l'estat d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonAutoDetect
@XmlRootElement
public class RespostaConsultaEstatNotificacio extends RespostaBase {

	private NotificacioEstatEnum estat;

}
