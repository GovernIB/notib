/**
 * 
 */
package es.caib.notib.client.domini.consulta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * Informació d'una Transmissió.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TransmissioV2 {

	private Long id;						// Identificador de l'enviament
	private String emisor;					// Codi dir3 de l'entitat emisora
	private GenericInfo organGestor;		// Codi dir3 de l'òrgan gestor
	private GenericInfo procediment;		// Codi SIA del procediment
	private String numExpedient;			// Número de l’expedient al que està associada la comunicació
	private String concepte;				// Concepte de la comunicació
	private String descripcio;				// Descripció de la comunicació
	private Date dataEnviament;				// Data d'enviament de la comunicació
	private GenericInfo estat;				// Estat de l'enviament
	private Date dataEstat;					// Data en que s'ha realitzat l'enviament
	private DocumentConsultaV2 document;				// Document comunicat
	
	private PersonaConsultaV2 titular;				// Persona titular de l'enviament
	private List<PersonaConsultaV2> destinataris;		// Persones representans, destinatàries de l'enviament

	// Error
	private boolean error;					// Informa si s'ha produït algun error en la comunicació
	private Date errorData;					// Data de l'error
	private String errorDescripcio;			// Descripció de l'error
	
	private String justificant;				// Justificant de registre
	private String certificacio;			// Certificació generada al realitzar la compareixença de la notificació
	
}
