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
import java.util.List;

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
public class RespostaConsultaEstatEnviamentV2 extends RespostaBase {

	private String identificador;
	private String referencia;
	private String notificaIndentificador;
	private EnviamentEstatEnum estat;
	private Date estatData;
	private String estatDescripcio;
	private boolean enviamentSir;
	private boolean dehObligat;
	private String dehNif;
	private boolean entragaPostalActiva;
	private String adressaPostal;
	private Persona interessat;
	private List<Persona> representants;
	private Registre registre;
	private Sir sir;
	private Datat datat;
	private Certificacio certificacio;

}
