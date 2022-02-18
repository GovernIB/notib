/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import es.caib.notib.core.api.util.TrimStringDeserializer;
import lombok.Data;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació del document que s'envia amb la notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@JsonAutoDetect
public class DocumentV2 {

	/**
	 * Id que retorna la gestió documental de l’arxiu físic adjuntat.
	 */
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String arxiuId;

	/**
	 * Nom de l’arxiu
	 */
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String arxiuNom;

	/**
	 * Contingut del document en base 64.
	 * S'ha d'informar un entre els 4 camps: contingutBase64, url, uuid i csv.
	 */
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String contingutBase64;

	/**
	 * Enllaç extern on es troba el document de l’enviament.
	 * S'ha d'informar un entre els 4 camps: contingutBase64, url, uuid i csv.
	 */
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String url;

	/**
	 * Codi uuid que es pot utilitzar per tal d’obtenir el document imprimible del sistema d’arxiu
	 * S'ha d'informar un entre els 4 camps: contingutBase64, url, uuid i csv.
	 */
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String uuid;

	/**
	 * Codi csv que es pot utilitzar per tal d’obtenir el document imprimible del sistema d’arxiu
	 * S'ha d'informar un entre els 4 camps: contingutBase64, url, uuid i csv.
	 */
	@JsonDeserialize(using = TrimStringDeserializer.class)
	private String csv;

	/**
	 * Indica si el document està normalitzat per a la impressió al CIE.
	 * Només aplica per enviaments postals.
	 */
	private boolean normalitzat;
	//	private Map<String, String> metadades;

	/**
	 * Enumerat que indica l’origen del document.
	 * Valors possibles: [CIUTADA, ADMINISTRACIO]
	 * Valor per defecte:  ADMINISTRACIO
	 * No s’utilitza en el cas de documents passats com a csv o uuid.
	 */
	private OrigenEnum origen;

	/**
	 * Enumerat que indica la validesa del document.
	 * Valors possibles: [COPIA, COPIA_AUTENTICA, ORIGINAL]
	 * Valor per defecte: ORIGINAL
	 * No s’utilitza en el cas de documents passats com a csv o uuid.
	 */
	private ValidesaEnum validesa;

	/**
	 * Enumerat que indica el tipus de document.
	 * Valors possibles: [RESSOLUCIO, ACORD, CONTRACTE, CONVENI, DECLARACIO, COMUNICACIO, NOTIFICACIO, PUBLICACIO, JUSTIFICANT_RECEPCIO, ACTA, CERTIFICAT, DILIGENCIA, INFORME, SOLICITUD, DENUNCIA, ALEGACIO, RECURS, COMUNICACIO_CIUTADA, FACTURA, ALTRES_INCAUTATS, ALTRES]
	 * Valor per defecte: NOTIFICACIO
	 * No s’utilitza en el cas de documents passats com a csv o uuid.
	 */
	private TipusDocumentalEnum tipoDocumental;

	/**
	 * Indica, en cas de document pdf, si aquest està firmat electrònicament.
	 * No s’utilitza en el cas de documents passats com a csv o uuid.
	 */
	private Boolean modoFirma;

	public boolean isEmpty() {
		return ((arxiuNom == null || arxiuNom.isEmpty()) &&
				(contingutBase64 == null || contingutBase64.isEmpty()) &&
				(url == null || url.isEmpty()) &&
				(uuid == null || uuid.isEmpty()) &&
				(csv == null || csv.isEmpty()));
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
