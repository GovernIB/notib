/**
 * 
 */
package es.caib.notib.core.helper;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;

import es.caib.notib.core.api.dto.CallbackEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.ws.callback.NotificacioCanviClient;
import es.caib.notib.core.entity.AplicacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.entity.NotificacioEventEntity.Builder;
import es.caib.notib.core.entity.UsuariEntity;
import es.caib.notib.core.repository.AplicacioRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;

/**
 * Classe per englobar la tasca de notificar l'estat o la certificació a l'aplicació
 * client a partir de la referència del destinatari de la notificació.
 * 
 * Recupera la informació de la notificació a partir de la referència i la informació
 * de l'aplicació client a partir del codi d'usuari que ha creat l'anotació.
 * Emplena la informació cap al client de la mateixa forma que el WS de consulta  NotificacioWsServiceImpl.
 * 
 * @see NotificacioWsServiceImpl
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class CallbackHelper {

	private static final String NOTIFICACIO_CANVI = "notificaCanvi";
//	private static final String NOTIFICACIO_ESTAT = "notificaEstat";
//	private static final String NOTIFICACIO_CERTIFICACIO = "notificaCertificacio";

	@Autowired
	private AplicacioRepository aplicacioRepository;
	@Autowired
	private NotificacioEventRepository notificacioEventRepository;

//	@Autowired
//	private PluginHelper pluginHelper;
	@Autowired
	private NotificaHelper notificaHelper;



	@Transactional
	public boolean notifica(Long eventId) {
		boolean ret = false;
		// Recupera l'event
		NotificacioEventEntity event = notificacioEventRepository.findOne(eventId);
		if (event == null)
			throw new NotFoundException(
					"eventId:" + eventId,
					NotificacioEventEntity.class);
		// Recupera la referència
		String referencia = null;
		if (event.getEnviament() != null)
			referencia = event.getEnviament().getNotificaReferencia();
		int intents = event.getCallbackIntents() + 1;
		Date ara = new Date();
		if (referencia != null) {
			// Notifica al client
			try {
				if (event.getTipus() == NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT
						|| event.getTipus() == NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO) {
					// Avisa al client que hi ha hagut una modificació a l'enviament
					notificaCanvi(event.getEnviament());
//					// Invoca el mètode de notificació de l'aplicació client segons és estat o certificat:
//					if (event.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT))
//						notificaEstat(event.getEnviament());
//					else if (event.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO))
//						notificaCertificat(event.getEnviament());					
					// Marca l'event com a notificat
					event.updateCallbackClient(CallbackEstatEnumDto.NOTIFICAT, ara, intents, null);
					ret = true;
				} else {
					// És un event pendent de notificar que no és del tipus esperat
					event.updateCallbackClient(CallbackEstatEnumDto.ERROR, ara, intents, "L'event id=" + event.getId() + " és del tipus " + event.getTipus() + " i no es pot notificar a l'aplicació client.");
				}
			} catch (Exception ex) {
				logger.debug("Error notificant l'event " + eventId + " amb referencia de destinatari " + referencia, ex);
				// Marca un error a l'event
				Integer maxIntents = this.getEventsIntentsMaxProperty();
				CallbackEstatEnumDto estatNou = maxIntents == null || intents < maxIntents ? 
													CallbackEstatEnumDto.PENDENT
													: CallbackEstatEnumDto.ERROR;
				event.updateCallbackClient(
						estatNou,
						ara,
						intents,
						"Error notificant l'event al client: " + ex.getMessage());
			}
		} else {
			// No és un event que es pugui notificar, el marca com a error
			event.updateCallbackClient(CallbackEstatEnumDto.ERROR, ara, intents, "L'event " + eventId + " no té referència de destinatari, no es pot fer un callback a l'aplicació client.");
		}
		
		// Crea una nova entrada a la taula d'events per deixar constància de la notificació a l'aplicació client
		Builder eventBuilder = NotificacioEventEntity.getBuilder(
				NotificacioEventTipusEnumDto.CALLBACK_CLIENT,
				event.getNotificacio()).
				enviament(event.getEnviament()).
				descripcio("Callback " + event.getTipus());
		if (!ret) {
			eventBuilder.error(true)
						.errorDescripcio(event.getCallbackError());
		}
		NotificacioEventEntity callbackEvent = eventBuilder.build();
		event.getNotificacio().updateEventAfegir(callbackEvent);
		
		return ret;
	}

	private String notificaCanvi(NotificacioEnviamentEntity enviament) throws Exception {
		if (enviament == null)
			throw new Exception("El destinatari no pot ser nul.");
		
		// Resol si hi ha una aplicació pel codi d'usuari que ha creat l'enviament
		UsuariEntity usuari = enviament.getCreatedBy();
		AplicacioEntity aplicacio = aplicacioRepository.findByUsuariCodi(usuari.getCodi());
		if (aplicacio == null)
			throw new NotFoundException("codi usuari: " + usuari.getCodi(), AplicacioEntity.class);
		if (aplicacio.getCallbackUrl() == null)
			throw new Exception("La aplicació " + aplicacio.getUsuariCodi() + " no té cap url de callback configurada");
				
//		// Omple l'objecte amb la informació cap a l'aplicació client
//		NotificacioEstatClient notificacioEstat = new NotificacioEstatClient(
//					calcularEstat(enviament),
//					enviament.getNotificaEstatData(),
//					enviament.getDestinatariNom(),
//					enviament.getDestinatariNif(),
//					enviament.getNotificaDatatOrigen(),
//					enviament.getNotificaDatatNumSeguiment(),
//					enviament.getNotificaReferencia() 
//				);
		NotificacioCanviClient notificacioCanvi = new NotificacioCanviClient(
				notificaHelper.xifrarId(enviament.getNotificacio().getId()), 
				enviament.getNotificaReferencia());

		// Passa l'objecte a JSON
		ObjectMapper mapper  = new ObjectMapper();
		String body = mapper.writeValueAsString(notificacioCanvi);
				
		// Prepara el client JSON per a la crida POST
		Client jerseyClient = this.getClient(aplicacio);

		// Completa la URL al mètode
		String urlBase = aplicacio.getCallbackUrl();
		String urlAmbMetode = urlBase + (urlBase.endsWith("/") ? "" : "/") +  NOTIFICACIO_CANVI;
		
		// Fa la crida POST passant les dades JSON
		ClientResponse response = jerseyClient.
				resource(urlAmbMetode).
				type("application/json").
				post(ClientResponse.class, body);
		
		// Comprova que la resposta sigui 200 OK
		if ( ClientResponse.Status.OK.getStatusCode() != response.getStatusInfo().getStatusCode())
			throw new Exception("La resposta del client és: " + response.getStatusInfo().getStatusCode() + " - " + response.getStatusInfo().getReasonPhrase());

		return response.getEntity(String.class);
	}
	
//	private String notificaEstat(NotificacioEnviamentEntity enviament) throws Exception {
//		if (enviament == null)
//			throw new Exception("El destinatari no pot ser nul.");
//		
//		// Resol si hi ha una aplicació pel codi d'usuari que ha creat l'enviament
//		UsuariEntity usuari = enviament.getCreatedBy();
//		AplicacioEntity aplicacio = aplicacioRepository.findByUsuariCodi(usuari.getCodi());
//		if (aplicacio == null)
//			throw new NotFoundException("codi usuari: " + usuari.getCodi(), AplicacioEntity.class);
//		if (aplicacio.getCallbackUrl() == null)
//			throw new Exception("La aplicació " + aplicacio.getUsuariCodi() + " no té cap url de callback configurada");
//				
//		// Omple l'objecte amb la informació cap a l'aplicació client
//		NotificacioEstatClient notificacioEstat = new NotificacioEstatClient(
//					calcularEstat(enviament),
//					enviament.getNotificaEstatData(),
//					enviament.getDestinatariNom(),
//					enviament.getDestinatariNif(),
//					enviament.getNotificaDatatOrigen(),
//					enviament.getNotificaDatatNumSeguiment(),
//					enviament.getNotificaReferencia() 
//				);
//
//		// Passa l'objecte a JSON
//		ObjectMapper mapper  = new ObjectMapper();
//		String body = mapper.writeValueAsString(notificacioEstat);
//				
//		// Prepara el client JSON per a la crida POST
//		Client jerseyClient = this.getClient(aplicacio);
//
//		// Completa la URL al mètode
//		String urlBase = aplicacio.getCallbackUrl();
//		String urlAmbMetode = urlBase + (urlBase.endsWith("/") ? "" : "/") +  NOTIFICACIO_ESTAT;
//		
//		// Fa la crida POST passant les dades JSON
//		ClientResponse response = jerseyClient.
//				resource(urlAmbMetode).
//				type("application/json").
//				post(ClientResponse.class, body);
//		
//		// Comprova que la resposta sigui 200 OK
//		if ( ClientResponse.Status.OK.getStatusCode() != response.getStatusInfo().getStatusCode())
//			throw new Exception("La resposta del client és: " + response.getStatusInfo().getStatusCode() + " - " + response.getStatusInfo().getReasonPhrase());
//
//		return response.getEntity(String.class);
//	}

//	private String notificaCertificat(NotificacioEnviamentEntity enviament) throws Exception{
//		if (enviament == null)
//			throw new Exception("El destinatari no pot ser nul.");
//		// Resol si hi ha una aplicació pel codi d'usuari que ha creat l'enviament
//		UsuariEntity usuari = enviament.getCreatedBy();
//		AplicacioEntity aplicacio = aplicacioRepository.findByUsuariCodi(usuari.getCodi());
//		if (aplicacio == null)
//			throw new NotFoundException("codi usuari: " + usuari.getCodi(), AplicacioEntity.class);
//		if (aplicacio.getCallbackUrl() == null)
//			throw new Exception("La aplicació " + aplicacio.getUsuariCodi() + " no té cap url de callback configurada");
//		// Comprova que l'event tingui un fitxer associat
//		if (enviament.getNotificaCertificacioArxiuId() == null)
//			throw new Exception("L'event no té un fitxer associat.");
//		if (aplicacio.getCallbackUrl() == null)
//			throw new Exception("La aplicació " + aplicacio.getUsuariCodi() + " no té cap url de callback configurada");
//		
//
//		// Omple l'objecte amb la informació cap a l'aplicació client
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		pluginHelper.gestioDocumentalGet(
//				enviament.getNotificaCertificacioArxiuId(),
//				PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS,
//				baos);
//		NotificacioCertificacioClient notificacioCertificacio = new NotificacioCertificacioClient(
//				CertificacioTipusEnum.toCertificacioTipusEnum(enviament.getNotificaCertificacioTipus()),
//				CertificacioArxiuTipusEnum.toCertificacioArxiuTipusEnum(enviament.getNotificaCertificacioArxiuTipus()), 
//				new String(Base64.encode(baos.toByteArray())),
//				enviament.getNotificaCertificacioNumSeguiment(),
//				enviament.getNotificaCertificacioData() );
//
//		// Passa l'objecte a JSON
//		ObjectMapper mapper  = new ObjectMapper();
//		String body = mapper.writeValueAsString(notificacioCertificacio);
//		
//		// Prepara el client JSON per a la crida POST
//		Client jerseyClient = this.getClient(aplicacio); 
//
//		// Completa la URL al mètode
//		String urlBase = aplicacio.getCallbackUrl();
//    	String urlAmbMetode = urlBase + (urlBase.endsWith("/") ? "" : "/") +  NOTIFICACIO_CERTIFICACIO;
//
//		// Fa la crida POST passant les dades JSON
//		ClientResponse response = jerseyClient.
//				resource(urlAmbMetode).
//				type("application/json").
//				post(ClientResponse.class, body);
//		
//		// Comprova que la resposta sigui 200 OK
//		if ( ClientResponse.Status.OK.getStatusCode() != response.getStatusInfo().getStatusCode())
//			throw new Exception("La resposta del client és: " + response.getStatusInfo().getStatusCode() + " - " + response.getStatusInfo().getReasonPhrase());
//
//		return response.getEntity(String.class);
//	}

//	private NotificacioDestinatariEstatEnum calcularEstat(
//			NotificacioEnviamentEntity enviament) {
//		NotificacioDestinatariEstatEnum estat = null;
//		switch (enviament.getNotificaEstat()) {
//		case ABSENT:
//			estat = NotificacioDestinatariEstatEnum.ABSENT;
//			break;
//		case ADRESA_INCORRECTA:
//			estat = NotificacioDestinatariEstatEnum.ADRESA_INCORRECTA;
//			break;
//		case DESCONEGUT:
//			estat = NotificacioDestinatariEstatEnum.DESCONEGUT;
//			break;
//		case ENTREGADA_OP:
//			estat = NotificacioDestinatariEstatEnum.ENTREGADA_OP;
//			break;
//		case ENVIADA_CI:
//			estat = NotificacioDestinatariEstatEnum.ENVIADA_CI;
//			break;
//		case ENVIADA_DEH:
//			estat = NotificacioDestinatariEstatEnum.ENVIADA_DEH;
//			break;
//		case ENVIAMENT_PROGRAMAT:
//			estat = NotificacioDestinatariEstatEnum.ENVIAMENT_PROGRAMAT;
//			break;
//		case ERROR_ENTREGA:
//			estat = NotificacioDestinatariEstatEnum.ERROR_ENTREGA;
//			break;
//		case EXPIRADA:
//			estat = NotificacioDestinatariEstatEnum.EXPIRADA;
//			break;
//		case EXTRAVIADA:
//			estat = NotificacioDestinatariEstatEnum.EXTRAVIADA;
//			break;
//		case LLEGIDA:
//			estat = NotificacioDestinatariEstatEnum.LLEGIDA;
//			break;
//		case MORT:
//			estat = NotificacioDestinatariEstatEnum.MORT;
//			break;
//		case NOTIFICADA:
//			estat = NotificacioDestinatariEstatEnum.NOTIFICADA;
//			break;
//		case PENDENT_CIE:
//			estat = NotificacioDestinatariEstatEnum.PENDENT_CIE;
//			break;
//		case PENDENT_DEH:
//			estat = NotificacioDestinatariEstatEnum.PENDENT_DEH;
//			break;
//		case PENDENT_ENVIAMENT:
//			estat = NotificacioDestinatariEstatEnum.PENDENT_ENVIAMENT;
//			break;
//		case PENDENT_SEU:
//			estat = NotificacioDestinatariEstatEnum.PENDENT_SEU;
//			break;
//		case REBUTJADA:
//			estat = NotificacioDestinatariEstatEnum.REBUTJADA;
//			break;
//		case SENSE_INFORMACIO:
//			estat = NotificacioDestinatariEstatEnum.SENSE_INFORMACIO;
//			break;
//		case NOTIB_ENVIADA:
//			estat = NotificacioDestinatariEstatEnum.NOTIB_ENVIADA;
//			break;
//		case NOTIB_PENDENT:
//			estat = NotificacioDestinatariEstatEnum.NOTIB_PENDENT;
//			break;
//		}
//		return estat;
//	}

	/** Propietat que assenayala el màxim de reintents. Si la propietat és null llavors no hi ha un màxim. */
	private Integer getEventsIntentsMaxProperty() {
		Integer ret = null;
		String maxIntents = PropertiesHelper.getProperties().getProperty("es.caib.notib.tasca.callback.pendents.notifica.events.intents.max");
		if (maxIntents != null && !"".equals(maxIntents))
			ret = Integer.parseInt(maxIntents);
		return ret;
	}

	private Client getClient(AplicacioEntity aplicacio) {
		Client jerseyClient =  new Client();
		// Només per depurar la sortida, esborrar o comentar-ho: 
		jerseyClient.addFilter(new LoggingFilter(System.out));		
		String username = null;
		String password = null;
		switch (aplicacio.getTipusAutenticacio()) {
		case TOKEN_CAIB:
			username = "";
			password = "";
			jerseyClient.addFilter(new HTTPBasicAuthFilter(username, password));
			break;
		case TEXT_CLAR:
			jerseyClient.addFilter(new HTTPBasicAuthFilter(username, password));
			break;
		case CAP:
		default:
			break;
		}	
		return jerseyClient;
	}

	private static final Logger logger = LoggerFactory.getLogger(CallbackHelper.class);

}
