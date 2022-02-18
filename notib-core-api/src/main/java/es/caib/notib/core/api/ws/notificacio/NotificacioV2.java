/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import es.caib.notib.core.api.dto.IdiomaEnumDto;
import es.caib.notib.core.api.util.TrimStringDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

/**
 * Informació d'una notificació per al seu enviament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificacioV2 {

	/**
	 * Codi Dir3 de l’organisme emisor
	 * Camp obligatori
	 */
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String emisorDir3Codi;


	/**
	 * Codi DIR3 de l’òrgan gestor que realitza la notificació/comunicació.
	 * Obligatori en el cas de procediments comuns. En cas contrari s’utilitzarà l’òrgan gestor al que pertany el procediment.
	 */
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String organGestor;

	/**
	 * Enumerat que indica si l’enviament és una comunicació o una notificació.
	 * Possibles valors: [NOTIFICACIO, COMUNICACIO]
	 * Valor per defecte: COMUNICACIO
	 * Camp obligatori
	 */
	private EnviamentTipusEnum enviamentTipus;

	/**
	 * Concepte de l’enviament. Si ha d’anar a CIE només s’agafaran els 50 primers caràcters.
	 * Camp obligatori
	 */
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String concepte;

	/**
	 * Descripció detallada de l’enviament
	 * Camp opcional
	 */
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String descripcio; //Observacions

	/**
	 * Data en la que l’enviament es posarà a disposició per a la compareixença
	 * Camp opcional. En cas de no informar-se es posarà en disposiciño per a la compareixença de forma inmediata
	 */
	private Date enviamentDataProgramada;

	/**
	 * Dies que l’enviament estarà a disposició de compareixença en carpeta abans d’entregar-lo per altres mitjans
	 * Camp opcional
	 */
	private Integer retard;

	/**
	 * Data d’expiració de l’enviament
	 * Aquest camp és excuient amb el de caducitatDiesNaturals. Només s'ha d'indicar un dels dos. En cas d'informar els dos, s'ignorarà el camp caducitatDiesNaturals.
	 * Camp opcional per a comunicacions
	 */
	private Date caducitat;

	/**
	 * Dies naturals abans que expiri l'enviament.
	 * Aquest camp és excuient amb el de caducitat. Només s'ha d'indicar un dels dos. En cas d'informar els dos, aquest s'ignorarà.
	 * Camp opcional per a comunicacions
	 */
	private Integer caducitatDiesNaturals;

	/**
	 * Codi de l’usuari que està realitzant la notificació.
	 * Requisit per fer el registre de sortida o assentament registral.
	 * Camp obligatori
	 */
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String usuariCodi;

	/**
	 * Identificador del procediment SIA al que pertany la notificació
	 * Camp obligatori
	 */
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String procedimentCodi;

	/**
	 * Codi del grup al que s’assigna la notificació.
	 * Aquest camp només té sentit quan es vol donar d’alta una notificació que s’ha configurat amb grups a Notifica (veure manual d’usuari)
	 * Camp opcional
	 */
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String grupCodi;

	/**
	 * Referència al nombre d'expedient de l'assentament
	 * Camp opcional
	 */
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String numExpedient;

	/**
	 * Llista d'enviaments continguts en la Notificació/Comunicació
	 * Camp obligatori
	 */
	private List<Enviament> enviaments;

	/**
	 * Enumerat que indica l’idioma de la notificació.
	 * Valors possibles: [CA, ES]
	 * Valor per defecte: CA
	 * Camp opcional
	 */
	private IdiomaEnumDto idioma;

	/**
	 * Document que s’envia en la notificació/comunicació
	 * Camp obligatori
	 */
	private DocumentV2 document;

	/**
	 * Document que s’envia en la comunicació. Únicament es pot adjuntar més d’un document en cas de comunicacions a la administració (SIR)
	 * Camp opcional
	 */
	private DocumentV2 document2;

	/**
	 * Document que s’envia en la comunicació. Únicament es pot adjuntar més d’un document en cas de comunicacions a la administració (SIR)
	 * Camp opcional
	 */
	private DocumentV2 document3;

	/**
	 * Document que s’envia en la comunicació. Únicament es pot adjuntar més d’un document en cas de comunicacions a la administració (SIR)
	 * Camp opcional
	 */
	private DocumentV2 document4;

	/**
	 * Document que s’envia en la comunicació. Únicament es pot adjuntar més d’un document en cas de comunicacions a la administració (SIR)
	 * Camp opcional
	 */
	private DocumentV2 document5;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
