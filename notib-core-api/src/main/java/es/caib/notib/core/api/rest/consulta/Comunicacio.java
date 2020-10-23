/**
 * 
 */
package es.caib.notib.core.api.rest.consulta;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.caib.notib.core.api.dto.InteressatTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDto;
import es.caib.notib.core.api.dto.PersonaDto;
import lombok.Data;

/**
 * Informació d'una anotació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class Comunicacio {

	// Notificació
	private Long id;					// Identificador de la comunicació
	private String emisor;				// Codi dir3 de l'entitat emisora
	private String organGestor;			// Codi dir3 de l'òrgan gestor
	private String procediment;			// Codi SIA del procediment
	private String numExpedient;		// Número de l’expedient al que està associada la comunicació
	private String concepte;			// Concepte de la comunicació
	private String descripcio;			// Descripció de la comunicació
	private Date dataEnviament;			// Data d'enviament de la comunicació
	private Estat estat;				// Estat de l'enviament
	private Date dataEstat;				// Data en que s'ha realitzat l'enviament
	private Document document;			// Document comunicat
	
	// Enviament
	private Persona titular;			// Persona titular de l'enviament
	private List<Persona> destinataris;	// Persones representans, destinatàries de l'enviament
	private SubEstat subestat;			// Subestat de l'enviament
	private Date dataSubestat;			// Data en que s'ha canviat al subestat actual

	// Error
	private boolean error;				// Informa si s'ha produït algun error en la comunicació
	private Date errorData;				// Data de l'error
	private String errorDescripcio;		// Descripció de l'error
	
	private Document justificant;		// Justificant de registre

	
	public static Comunicacio toComunicacio(NotificacioEnviamentDto enviament, String basePath) {
		Comunicacio comunicacio = new Comunicacio();
		comunicacio.setId(enviament.getId());
		comunicacio.setEmisor(enviament.getNotificacio().getEntitat().getCodi());
		comunicacio.setOrganGestor(enviament.getNotificacio().getOrganGestor());
		if (enviament.getNotificacio().getProcediment() != null)
			comunicacio.setProcediment(enviament.getNotificacio().getProcediment().getCodi());
		comunicacio.setNumExpedient(enviament.getNotificacio().getNumExpedient());
		comunicacio.setConcepte(enviament.getNotificacio().getConcepte());
		comunicacio.setDescripcio(enviament.getNotificacio().getDescripcio());
		comunicacio.setDataEnviament(enviament.getNotificacio().getEnviamentDataProgramada());
		comunicacio.setEstat(Estat.valueOf(enviament.getNotificacio().getEstat().name()));
		comunicacio.setDataEstat(enviament.getNotificacio().getEstatDate());
		Document document = Document.builder()
				.nom(enviament.getNotificacio().getDocument().getArxiuNom())
				.mediaType(enviament.getNotificacio().getDocument().getMediaType())
				.mida(enviament.getNotificacio().getDocument().getMida())
				.url(basePath + "/document/" + enviament.getNotificacio().getId()).build();
		comunicacio.setDocument(document);
		comunicacio.setTitular(toPersona(enviament.getTitular()));
		List<Persona> destinataris = new ArrayList<Persona>();
		if (enviament.getDestinataris() != null && !enviament.getDestinataris().isEmpty()) {
			for (PersonaDto destinatari: enviament.getDestinataris()) {
				destinataris.add(toPersona(destinatari));
			}
		}
		comunicacio.setDestinataris(destinataris);
		comunicacio.setSubestat(SubEstat.valueOf(enviament.getNotificaEstat().name()));
		comunicacio.setDataSubestat(enviament.getNotificaEstatData());

		comunicacio.setError(enviament.isNotificaError());
		comunicacio.setErrorData(enviament.getNotificaErrorData());
		comunicacio.setErrorDescripcio(enviament.getNotificaErrorDescripcio());
		
		return comunicacio;
	}
	
	protected static Persona toPersona(PersonaDto dto) {
		Persona persona= new Persona();
		persona.setNom(dto.getNom());
		if (dto.getInteressatTipus() != null) {
			persona.setTipus(PersonaTipus.valueOf(dto.getInteressatTipus().name()));
			if (!InteressatTipusEnumDto.FISICA.equals(dto.getInteressatTipus())) {
				if (dto.getRaoSocial() != null && !dto.getRaoSocial().isEmpty()) {
					persona.setNom(dto.getRaoSocial());
				} else {
					persona.setNom(dto.getNom());
				}
			}
		}
		persona.setLlinatge1(dto.getLlinatge1());
		persona.setLlinatge2(dto.getLlinatge2());
		persona.setNif(dto.getNif());
		persona.setEmail(dto.getEmail());
		return persona;
	}
}
