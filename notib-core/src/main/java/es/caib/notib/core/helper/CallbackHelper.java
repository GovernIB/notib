package es.caib.notib.core.helper;

import java.io.IOException;

import javax.annotation.Resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import es.caib.notib.core.api.dto.AplicacioDto;
import es.caib.notib.core.api.dto.FitxerDto;
import es.caib.notib.core.api.dto.NotificacioDestinatariDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.api.service.UsuariAplicacioService;
import es.caib.notib.core.api.ws.callback.NotificacioCertificacioClient;
import es.caib.notib.core.api.ws.callback.NotificacioEstatClient;
import es.caib.notib.core.api.ws.notificacio.CertificacioArxiuTipusEnum;
import es.caib.notib.core.api.ws.notificacio.CertificacioTipusEnum;

public class CallbackHelper {
	
	private static final String NOTIFICACIO_ESTAT = "notificaEstat";
	private static final String NOTIFICACIO_CERTIFICACIO = "notificaCertificacio";
	
	@Resource
	private NotificacioService notificacioService;
	@Resource
	private UsuariAplicacioService usuariAplicacioService;
	
	
	public String notificaEstat(String referencia) throws JsonProcessingException {
			
		NotificacioDestinatariDto destinatari =
				notificacioService.destinatariFindByReferencia(referencia);
		
		UsuariDto usuari = destinatari.getCreatedBy();
		AplicacioDto aplicacio = usuariAplicacioService.findByUsuariCodi(usuari.getCodi());
		
		Client jerseyClient = new Client();
		ObjectMapper mapper  = new ObjectMapper();
		
		String username = null;
		String password = null;
		switch(aplicacio.getTipusAutenticacio()) {
			case TOKEN_CAIB:
//				try {
//					ControladorSesion controlador = new ControladorSesion();
//					controlador.autenticar(
//							getBackofficePropertyUsername(codigoCertificado),
//							getBackofficePropertyPassword(codigoCertificado));
//					AuthorizationToken token = controlador.getToken();
//					username = token.getUser();
//					password = token.getPassword();
//				} catch (Exception ex) {
//					logger.error("No s'ha pogut crear la instÃ ncia de ControladorSesion", ex);
//				}
				username = "";
				password = "";
				jerseyClient.addFilter( new HTTPBasicAuthFilter(username, password) );
				break;
			case TEXT_CLAR:
//				username = getAplicacioPropertyUsername(aplicacio.getUsuariCodi());
//				password = getAplicacioPropertyPassword(aplicacio.getUsuariCodi());
				jerseyClient.addFilter( new HTTPBasicAuthFilter(username, password) );
				break;
			case CAP:
			default:
				break;
		}
		
		String urlAmbMetode = aplicacio.getCallbackUrl();
		if(urlAmbMetode.charAt(urlAmbMetode.length() - 1) != '/')
			urlAmbMetode = urlAmbMetode + "/";
		
		urlAmbMetode = urlAmbMetode + NOTIFICACIO_ESTAT;
		
		NotificacioEstatClient notificacioEstat = new NotificacioEstatClient(
				destinatari.getEstatUnificat(),
				destinatari.getNotificaEstatData(),
				destinatari.getDestinatariNom(),
				destinatari.getDestinatariNif(),
				destinatari.getNotificaEstatOrigen(),
				destinatari.getNotificaEstatNumSeguiment());
		
		String body = mapper.writeValueAsString(notificacioEstat);
		
		
		ClientResponse response = jerseyClient.
				resource(urlAmbMetode).
				type("application/json").
				post(ClientResponse.class, body);
		
		
		return response.getEntity(String.class);
		
	}
	
	
	public String notificaCertificat(String referencia) throws IOException{
		
		NotificacioDestinatariDto destinatari =
				notificacioService.destinatariFindByReferencia(referencia);
		
		UsuariDto usuari = destinatari.getCreatedBy();
		AplicacioDto aplicacio = usuariAplicacioService.findByUsuariCodi(usuari.getCodi());
		
		Client jerseyClient = new Client();
		ObjectMapper mapper  = new ObjectMapper();
		
		String username = null;
		String password = null;
		switch(aplicacio.getTipusAutenticacio()) {
			case TOKEN_CAIB:
//				try {
//					ControladorSesion controlador = new ControladorSesion();
//					controlador.autenticar(
//							getBackofficePropertyUsername(codigoCertificado),
//							getBackofficePropertyPassword(codigoCertificado));
//					AuthorizationToken token = controlador.getToken();
//					username = token.getUser();
//					password = token.getPassword();
//				} catch (Exception ex) {
//					logger.error("No s'ha pogut crear la instÃ ncia de ControladorSesion", ex);
//				}
				username = "";
				password = "";
				jerseyClient.addFilter( new HTTPBasicAuthFilter(username, password) );
				break;
			case TEXT_CLAR:
//				username = getAplicacioPropertyUsername(aplicacio.getUsuariCodi());
//				password = getAplicacioPropertyPassword(aplicacio.getUsuariCodi());
				jerseyClient.addFilter( new HTTPBasicAuthFilter(username, password) );
				break;
			case CAP:
			default:
				break;
		}
		
		String urlAmbMetode = aplicacio.getCallbackUrl();
		if(urlAmbMetode.charAt(urlAmbMetode.length() - 1) != '/')
			urlAmbMetode = urlAmbMetode + "/";
		
		urlAmbMetode = urlAmbMetode + NOTIFICACIO_CERTIFICACIO;
		
		FitxerDto fitxer = notificacioService.findCertificacio(referencia);
		
		NotificacioCertificacioClient notificacioCertificacio = new NotificacioCertificacioClient(
				CertificacioTipusEnum.toCertificacioTipusEnum(destinatari.getNotificaCertificacioTipus()), 
				CertificacioArxiuTipusEnum.toCertificacioArxiuTipusEnum(destinatari.getNotificaCertificacioArxiuTipus()), 
				fitxer.getContingut(),
				destinatari.getNotificaCertificacioNumSeguiment(),
				destinatari.getNotificaCertificacioDataActualitzacio() );
		
		String body = mapper.writeValueAsString(notificacioCertificacio);
		
		
		ClientResponse response = jerseyClient.
				resource(urlAmbMetode).
				type("application/json").
				post(ClientResponse.class, body);
		
		
		return response.getEntity(String.class);
		
	}
	
//	private String getAplicacioPropertyUsername(String aplicacioCodi) {
//		String username = PropertiesHelper.getProperties().getProperty(
//				"es.caib.notib." + aplicacioCodi + ".auth.username");
//		if (username == null) {
//			username = PropertiesHelper.getProperties().getProperty(
//					"es.caib.notib.auth.username");
//		}
//		return username;
//	}
//	private String getAplicacioPropertyPassword(String aplicacioCodi) {
//		String password = PropertiesHelper.getProperties().getProperty(
//				"es.caib.notib." + aplicacioCodi + ".auth.password");
//		if (password == null) {
//			password = PropertiesHelper.getProperties().getProperty(
//					"es.caib.notib.auth.password");
//		}
//		return password;
//	}

}
