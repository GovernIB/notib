/**
 * 
 */
package es.caib.notib.core.api.rest.consulta;

import java.util.ArrayList;
import java.util.List;

import es.caib.notib.core.api.dto.NotificacioEnviamentDto;
import es.caib.notib.core.api.dto.PersonaDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Informació d'una anotació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Notificacio extends Comunicacio{

	private Document certificacio;	// Certificació generada per a la notificació --> Notificació

	public static Notificacio toNotificacio(NotificacioEnviamentDto enviament, String basePath) {
		Notificacio notificacio = new Notificacio();
		notificacio.setId(enviament.getId());
		notificacio.setEmisor(enviament.getNotificacio().getEntitat().getCodi());
		notificacio.setOrganGestor(enviament.getNotificacio().getOrganGestor());
		if (enviament.getNotificacio().getProcediment() != null)
			notificacio.setProcediment(enviament.getNotificacio().getProcediment().getCodi());
		notificacio.setNumExpedient(enviament.getNotificacio().getNumExpedient());
		notificacio.setConcepte(enviament.getNotificacio().getConcepte());
		notificacio.setDescripcio(enviament.getNotificacio().getDescripcio());
		notificacio.setDataEnviament(enviament.getNotificacio().getEnviamentDataProgramada());
		notificacio.setEstat(Estat.valueOf(enviament.getNotificacio().getEstat().name()));
		notificacio.setDataEstat(enviament.getNotificacio().getEstatDate());
		Document document = Document.builder()
				.nom(enviament.getNotificacio().getDocument().getArxiuNom())
				.mediaType(enviament.getNotificacio().getDocument().getMediaType())
				.mida(enviament.getNotificacio().getDocument().getMida())
				.url(basePath + "/document/" + enviament.getNotificacio().getId()).build();
		notificacio.setDocument(document);
		notificacio.setTitular(toPersona(enviament.getTitular()));
		List<Persona> destinataris = new ArrayList<Persona>();
		if (enviament.getDestinataris() != null && !enviament.getDestinataris().isEmpty()) {
			for (PersonaDto destinatari: enviament.getDestinataris()) {
				destinataris.add(toPersona(destinatari));
			}
		}
		notificacio.setDestinataris(destinataris);
		notificacio.setSubestat(SubEstat.valueOf(enviament.getNotificaEstat().name()));
		notificacio.setDataSubestat(enviament.getNotificaEstatData());

		notificacio.setError(enviament.isNotificaError());
		notificacio.setErrorData(enviament.getNotificaErrorData());
		notificacio.setErrorDescripcio(enviament.getNotificaErrorDescripcio());
		
		// Certificació
		if (enviament.getNotificaCertificacioData() != null) {
			Document certificacio = Document.builder()
					.nom(enviament.getNotificaCertificacioArxiuNom())
					.url(basePath + "/certificacio/" + enviament.getId()).build();
			notificacio.setCertificacio(certificacio);
		}
		
		return notificacio;
	}
	
}
