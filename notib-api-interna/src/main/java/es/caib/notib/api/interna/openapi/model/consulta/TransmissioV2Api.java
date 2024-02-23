/**
 * 
 */
package es.caib.notib.api.interna.openapi.model.consulta;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Date;
import java.util.List;

/**
 * Informació d'una Transmissió.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Schema(name = "TransmissioV2")
public class TransmissioV2Api {

	@Schema(name = "id", implementation = Long.class, example = "00000000-0000-0000-0000-000000000000", description = "Identificador de l'enviament")
	private Long id;						// Identificador de l'enviament
	@Schema(name = "emisor", implementation = String.class, example = "A04003003", description = "Codi dir3 de l'entitat emisora")
	private String emisor;					// Codi dir3 de l'entitat emisora
	@Schema(name = "organGestor", implementation = GenericInfoApi.class, description = "Codi dir3 de l'òrgan gestor")
	private GenericInfoApi organGestor;		// Codi dir3 de l'òrgan gestor
	@Schema(name = "procediment", implementation = GenericInfoApi.class, description = "Codi SIA del procediment")
	private GenericInfoApi procediment;		// Codi SIA del procediment
	@Schema(name = "numExpedient", implementation = String.class, example = "123/2023", description = "Número de l’expedient al que està associada la notificació")
	private String numExpedient;			// Número de l’expedient al que està associada la notificació
	@Schema(name = "concepte", implementation = String.class, example = "Concepte de la notificació", description = "Concepte de la notificació")
	private String concepte;				// Concepte de la notificació
	@Schema(name = "descripcio", implementation = String.class, example = "Descripció de la notificació", description = "Descripció de la notificació")
	private String descripcio;				// Descripció de la notificació
	@Schema(name = "dataEnviament", implementation = Long.class, example = "1706168093962", description = "Data d'enviament de la notificació")
	private Date dataEnviament;				// Data d'enviament de la notificació
	@Schema(name = "estat", implementation = GenericInfoApi.class, description = "Estat de l'enviament")
	private GenericInfoApi estat;			// Estat de l'enviament
	@Schema(name = "dataEstat", implementation = Long.class, example = "1706168093962", description = "Data en que s'ha realitzat l'enviament")
	private Date dataEstat;					// Data en que s'ha realitzat l'enviament
	@Schema(name = "document", implementation = DocumentConsultaV2Api.class, example = "codi", description = "Document notificat")
	private DocumentConsultaV2Api document;	// Document notificat

	@Schema(name = "titular", implementation = PersonaConsultaV2Api.class, description = "Persona titular de l'enviament")
	private PersonaConsultaV2Api titular;	// Persona titular de l'enviament
	@Schema(name = "destinataris", description = "Persones representans, destinatàries de l'enviament")
	private List<PersonaConsultaV2Api> destinataris;		// Persones representans, destinatàries de l'enviament

	// Error
	@Schema(name = "error", implementation = Boolean.class, example = "false", description = "Informa si s'ha produït algun error en la notificació")
	private boolean error;					// Informa si s'ha produït algun error en la notificació
	@Schema(name = "errorData", implementation = Long.class, example = "17061680939620", description = "Data de l'error")
	private Date errorData;					// Data de l'error
	@Schema(name = "errorDescripcio", implementation = String.class, example = "java.lang.NullPointerException", description = "Descripció de l'error")
	private String errorDescripcio;			// Descripció de l'error

	@Schema(name = "justificant", implementation = String.class, example = "http://localhost:8080/notibapi/interna/v2/justificant/00000000-0000-0000-0000-000000000000", description = "Url per a descarregar el justificant de registre")
	private String justificant;				// Justificant de registre
	@Schema(name = "certificacio", implementation = String.class, example = "http://localhost:8080/notibapi/interna/v2/certificacio/00000000-0000-0000-0000-000000000000", description = "Url per a descarregar la Certificació generada al realitzar la compareixença de la notificació")
	private String certificacio;			// Certificació generada al realitzar la compareixença de la notificació
	
}
