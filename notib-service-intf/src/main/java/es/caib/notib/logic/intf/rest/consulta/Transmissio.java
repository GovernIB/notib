/**
 * 
 */
package es.caib.notib.logic.intf.rest.consulta;

import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * Informació d'una Transmissió.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class Transmissio {

	// Notificació
	private Long id;						// Identificador de la comunicació
	private String emisor;					// Codi dir3 de l'entitat emisora
	private String organGestor;				// Codi dir3 de l'òrgan gestor
	private String procediment;				// Codi SIA del procediment
	private String numExpedient;			// Número de l’expedient al que està associada la comunicació
	private String concepte;				// Concepte de la comunicació
	private String descripcio;				// Descripció de la comunicació
	private Date dataEnviament;				// Data d'enviament de la comunicació
	private Estat estat;					// Estat de l'enviament
	private Date dataEstat;					// Data en que s'ha realitzat l'enviament
	private Document document;				// Document comunicat
	
	// Enviament
	private Persona titular;				// Persona titular de l'enviament
	private List<Persona> destinataris;		// Persones representans, destinatàries de l'enviament
	private SubEstat subestat;				// Subestat de l'enviament
	private Date dataSubestat;				// Data en que s'ha canviat al subestat actual

	// Error
	private boolean error;					// Informa si s'ha produït algun error en la comunicació
	private Date errorData;					// Data de l'error
	private String errorDescripcio;			// Descripció de l'error
	
	private String justificant;				// Justificant de registre
	private String certificacio;			// Certificació generada al realitzar la compareixença de la notificació
	
}
