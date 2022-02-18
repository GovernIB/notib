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
 * Informació retornada per la consulta de l'estat d'un enviament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonAutoDetect
@XmlRootElement
public class RespostaConsultaEstatEnviament extends RespostaBase {

	private EnviamentEstatEnum estat;
	private Date estatData;
	private String estatDescripcio;
	private String estatOrigen;
	private String receptorNif;
	private String receptorNom;
	private Certificacio certificacio;

}
