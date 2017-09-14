package es.caib.notib.core.helper;

public class CallbackHelper {
	
	/*private static final String NOTIFICACIO_ESTAT = "notificaEstat";
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
		if (urlAmbMetode.charAt(urlAmbMetode.length() - 1) != '/') {
			urlAmbMetode = urlAmbMetode + "/";
		}
		urlAmbMetode = urlAmbMetode + NOTIFICACIO_ESTAT;
		NotificacioEstatClient notificacioEstat = new NotificacioEstatClient(
				NotificacioDestinatariEntity.calcularEstatNotificacioDestinatari(destinatari),
				destinatari.getNotificaEstatData(),
				destinatari.getDestinatariNom(),
				destinatari.getDestinatariNif(),
				destinatari.getNotificaEstatOrigen(),
<<<<<<< HEAD
				destinatari.getNotificaEstatNumSeguiment(),
				destinatari.getReferencia());
		
=======
				destinatari.getNotificaEstatNumSeguiment());
>>>>>>> refs/heads/branch_15-Proves_amb_Notifica
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
		if (urlAmbMetode.charAt(urlAmbMetode.length() - 1) != '/') {
			urlAmbMetode = urlAmbMetode + "/";
		}
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
	}*/

}
