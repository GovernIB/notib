package es.caib.notib.core.helper;

import java.io.ByteArrayOutputStream;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.Base64;

import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.ws.callback.CertificacioArxiuTipusEnum;
import es.caib.notib.core.api.ws.callback.CertificacioTipusEnum;
import es.caib.notib.core.api.ws.callback.NotificacioCertificacioClient;
import es.caib.notib.core.api.ws.callback.NotificacioEstatClient;
import es.caib.notib.core.entity.AplicacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.UsuariEntity;
import es.caib.notib.core.repository.AplicacioRepository;

/** Classe per englobar la tasca de notificar l'estat o la certificació a l'aplicació
 * client a partir de la referència del destinatari de la notificació.
 * 
 * Recupera la informació de la notificació a partir de la referència i la informació
 * de l'aplicació client a partir del codi d'usuari que ha creat l'anotació.
 * Emplena la informació cap al client de la mateixa forma que el WS de consulta  NotificacioWsServiceImpl.
 * 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 * @see NotificacioWsServiceImpl
 */
@Component
public class CallbackHelper {
	
	private static final String NOTIFICACIO_ESTAT = "notificaEstat";
	private static final String NOTIFICACIO_CERTIFICACIO = "notificaCertificacio";
	
	@Resource
	private AplicacioRepository aplicacioRepository;
	@Autowired
	private PluginHelper pluginHelper;

	
	
	public String notificaEstat(NotificacioEnviamentEntity enviament) throws Exception {
		if (enviament == null)
			throw new Exception("El destinatari no pot ser nul.");
		
		// Resol si hi ha una aplicació pel codi d'usuari que ha creat l'enviament
		UsuariEntity usuari = enviament.getCreatedBy();
		AplicacioEntity aplicacio = aplicacioRepository.findByUsuariCodi(usuari.getCodi());
		if (aplicacio == null)
			throw new NotFoundException("codi usuari: " + usuari.getCodi(), AplicacioEntity.class);
		if (aplicacio.getCallbackUrl() == null)
			throw new Exception("La aplicació " + aplicacio.getUsuariCodi() + " no té cap url de callback configurada");
				
		// Omple l'objecte amb la informació cap a l'aplicació client
		NotificacioEstatClient notificacioEstat = new NotificacioEstatClient(
					NotificaHelper.calcularEstat(enviament),
					enviament.getNotificaEstatData(),
					enviament.getDestinatariNom(),
					enviament.getDestinatariNif(),
					enviament.getNotificaEstatOrigen(),
					enviament.getNotificaEstatNumSeguiment(),
					enviament.getNotificaReferencia() 
				);

		// Passa l'objecte a JSON
		ObjectMapper mapper  = new ObjectMapper();
		String body = mapper.writeValueAsString(notificacioEstat);
				
		// Prepara el client JSON per a la crida POST
		Client jerseyClient = this.getClient(aplicacio);

		// Completa la URL al mètode
		String urlBase = aplicacio.getCallbackUrl();
		String urlAmbMetode = urlBase + (urlBase.endsWith("/") ? "" : "/") +  NOTIFICACIO_ESTAT;
		
		// Fa la crida POST passant les dades JSON
		ClientResponse response = jerseyClient.
				resource(urlAmbMetode).
				type("application/json").
				post(ClientResponse.class, body);
		
		// Comprova que la resposta sigui 200 OK
		if ( ClientResponse.Status.OK.equals(response.getStatusInfo().getStatusCode()))
			throw new Exception("La resposta del client és: " + response.getStatusInfo().getStatusCode() + " - " + response.getStatusInfo().getReasonPhrase());

		return response.getEntity(String.class);
	}


	public String notificaCertificat(NotificacioEnviamentEntity enviament) throws Exception{
		if (enviament == null)
			throw new Exception("El destinatari no pot ser nul.");
		// Resol si hi ha una aplicació pel codi d'usuari que ha creat l'enviament
		UsuariEntity usuari = enviament.getCreatedBy();
		AplicacioEntity aplicacio = aplicacioRepository.findByUsuariCodi(usuari.getCodi());
		if (aplicacio == null)
			throw new NotFoundException("codi usuari: " + usuari.getCodi(), AplicacioEntity.class);
		if (aplicacio.getCallbackUrl() == null)
			throw new Exception("La aplicació " + aplicacio.getUsuariCodi() + " no té cap url de callback configurada");
		// Comprova que l'event tingui un fitxer associat
		if (enviament.getNotificaCertificacioArxiuId() == null)
			throw new Exception("L'event no té un fitxer associat.");
		if (aplicacio.getCallbackUrl() == null)
			throw new Exception("La aplicació " + aplicacio.getUsuariCodi() + " no té cap url de callback configurada");
		

		// Omple l'objecte amb la informació cap a l'aplicació client
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		pluginHelper.gestioDocumentalGet(
				enviament.getNotificaCertificacioArxiuId(),
				PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS,
				baos);
		NotificacioCertificacioClient notificacioCertificacio = new NotificacioCertificacioClient(
				CertificacioTipusEnum.toCertificacioTipusEnum(enviament.getNotificaCertificacioTipus()),
				CertificacioArxiuTipusEnum.toCertificacioArxiuTipusEnum(enviament.getNotificaCertificacioArxiuTipus()), 
				new String(Base64.encode(baos.toByteArray())),
				enviament.getNotificaCertificacioNumSeguiment(),
				enviament.getNotificaCertificacioData() );

		// Passa l'objecte a JSON
		ObjectMapper mapper  = new ObjectMapper();
		String body = mapper.writeValueAsString(notificacioCertificacio);
		
		// Prepara el client JSON per a la crida POST
		Client jerseyClient = this.getClient(aplicacio); 

		// Completa la URL al mètode
		String urlBase = aplicacio.getCallbackUrl();
    	String urlAmbMetode = urlBase + (urlBase.endsWith("/") ? "" : "/") +  NOTIFICACIO_CERTIFICACIO;

		// Fa la crida POST passant les dades JSON
		ClientResponse response = jerseyClient.
				resource(urlAmbMetode).
				type("application/json").
				post(ClientResponse.class, body);
		
		// Comprova que la resposta sigui 200 OK
		if ( ClientResponse.Status.OK.equals(response.getStatusInfo().getStatusCode()))
			throw new Exception("La resposta del client és: " + response.getStatusInfo().getStatusCode() + " - " + response.getStatusInfo().getReasonPhrase());

		return response.getEntity(String.class);
	}
	
	/** Mètode comú per preparar el client jersey per a la crida segons el la operació i 
	 * les dades de l'aplicació.
	 * @param metode
	 * 				Mètode REST a invocar
	 * @param aplicacio
	 * 				Entity amb les dades de l'aplicació.
	 * @return
	 */
	private Client getClient(AplicacioEntity aplicacio) {
		
		Client jerseyClient =  new Client();
		// Només per depurar la sortida, esborrar o comentar-ho: jerseyClient.addFilter(new LoggingFilter(System.out));		
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
	
}
