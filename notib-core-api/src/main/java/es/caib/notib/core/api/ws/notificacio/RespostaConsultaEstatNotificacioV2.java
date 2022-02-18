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
import java.util.Date;

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
public class RespostaConsultaEstatNotificacioV2 extends RespostaBase {

	private String identificador;
	private NotificacioEstatEnum estat;
	private String tipus;
	private String emisorDir3;
	private Procediment procediment;
	private String organGestorDir3;
	private String concepte;
	private String numExpedient;

	private Date dataCreada;
	private Date dataEnviada;
	private Date dataFinalitzada;
	private Date dataProcessada;

}
