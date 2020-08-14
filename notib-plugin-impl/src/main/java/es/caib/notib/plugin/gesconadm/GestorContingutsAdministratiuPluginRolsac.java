package es.caib.notib.plugin.gesconadm;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.CreateException;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.naming.NamingException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.representation.Form;

import es.caib.notib.plugin.SistemaExternException;
import es.caib.notib.plugin.utils.PropertiesHelper;
import lombok.Getter;
import lombok.Setter;

public class GestorContingutsAdministratiuPluginRolsac implements GestorContingutsAdministratiuPlugin {
	
	private static final String ROLSAC_SERVICE_PATH = "api/rest/v1/";
	private static Map<String, String> unitatsAdministratives = new HashMap<String, String>();
	private String baseUrl;
	
	@Override
	public List<GcaProcediment> getAllProcediments() throws SistemaExternException {
		List<Procediment> procediments = new ArrayList<Procediment>();
		try {
			String urlAmbMetode = getBaseUrl() + ROLSAC_SERVICE_PATH + "procedimientos";
			
			Client jerseyClient = generarClient();
			autenticarClient(
					jerseyClient,
					urlAmbMetode);
			
			String json = jerseyClient.
					resource(urlAmbMetode).
					type("application/json").
					post(String.class);
			
			ObjectMapper mapper  = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			RespostaProcediments resposta = mapper.readValue(json, RespostaProcediments.class);
			if (resposta != null)
				procediments = resposta.getResultado();
			return toDto(procediments);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar els procediments via REST",
					ex);
		}
	}
	
	@Override
	public String getUnitatAdministrativa(String codi) throws SistemaExternException {
		if (unitatsAdministratives.containsKey(codi))
			return unitatsAdministratives.get(codi);
		
		try {
			String urlAmbMetode = getBaseUrl() + ROLSAC_SERVICE_PATH + "unidades_administrativas/" + codi;
			
			Client jerseyClient = generarClient();
			autenticarClient(
					jerseyClient,
					urlAmbMetode);
			
			String json = jerseyClient.
					resource(urlAmbMetode).
					post(String.class);
			
			ObjectMapper mapper  = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			RespostaUnitatAdministrativa resposta = mapper.readValue(json, RespostaUnitatAdministrativa.class);
			String unitatCodi = null;
			if (resposta.getResultado() != null && !resposta.getResultado().isEmpty()) {
				UnitatAdministrativa unitat = resposta.getResultado().get(0);
				if (unitat.getCodigoDIR3() != null && !unitat.getCodigoDIR3().isEmpty()) {
					unitatCodi = unitat.getCodigoDIR3();
				} else if (unitat.getPadre() != null && unitat.getPadre().getCodigo() != null && !unitat.getPadre().getCodigo().isEmpty()){
					unitatCodi = getUnitatAdministrativa(unitat.getPadre().getCodigo());
				}
			}
			unitatsAdministratives.put(codi, unitatCodi);
			return unitatCodi;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar els procediments via REST",
					ex);
		}
		
//		UnitatAdministrativa unitat = getUnitatAdministrativaRolsac(codi);
//		GdaUnitatAdministrativa dto = toDto(unitat);
//		String codiPare = null;
//		if (unitat.getPadre() != null) 
//			codiPare = unitat.getPadre().getCodigo();
//		addUnitat(codi, dto, codiPare);
//		return unitat.getCodigoDIR3();
	}
	
//	private UnitatAdministrativa getUnitatAdministrativaRolsac(String codi) throws SistemaExternException {
//		try {
//			String urlAmbMetode = getBaseUrl() + ROLSAC_SERVICE_PATH + "unidades_administrativas/" + codi;
//			
//			Client jerseyClient = generarClient();
//			autenticarClient(
//					jerseyClient,
//					urlAmbMetode);
//			
//			String json = jerseyClient.
//					resource(urlAmbMetode).
//					post(String.class);
//			System.out.println("Missatge REST rebut: " + json);
//			
//			ObjectMapper mapper  = new ObjectMapper();
//			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//			RespostaUnitatAdministrativa resposta = mapper.readValue(json, RespostaUnitatAdministrativa.class);
//			UnitatAdministrativa unitat = null;
//			if (resposta.getResultado() != null && !resposta.getResultado().isEmpty()) {
//				unitat = resposta.getResultado().get(0);
//				unitatsAdministratives.put(codi, unitat);
//			}
//			return unitat;
//		} catch (Exception ex) {
//			throw new SistemaExternException(
//					"No s'han pogut consultar els procediments via REST",
//					ex);
//		}
//	}
	
//	private GdaUnitatAdministrativa getUnitatAdministrativaArrel(String codi) throws SistemaExternException {
//		GdaUnitatAdministrativa unitatAdministrativa = null;
//		if (unitatsAdministratives.containsKey(codi)) {
//			Unitat u = unitatsAdministratives.get(codi);
//			if (u.getCodiPare() == null) {
//				unitatAdministrativa = u.getUnitatAdministrativa();
//			} else if (u.getCodiPare() != null) {
//				unitatAdministrativa = getUnitatAdministrativaArrel(u.getCodiPare());
//			}
//		} else {
//			UnitatAdministrativa unitat = getUnitatAdministrativaRolsac(codi);
//			GdaUnitatAdministrativa dto = toDto(unitat);
//			String codiPare = null;
//			if (unitat.getPadre() != null) 
//				codiPare = unitat.getPadre().getCodigo();
//			addUnitat(codi, dto, codiPare);
//			if (codiPare == null) {
//				unitatAdministrativa = dto;
//			} else {
//				unitatAdministrativa = getUnitatAdministrativaArrel(codiPare);
//			}
//		}
//		return unitatAdministrativa;
//	}
	
	
	private List<GcaProcediment> toDto(List<Procediment> procediments) throws SistemaExternException {
		List<GcaProcediment> procedimentsDto = new ArrayList<GcaProcediment>();
		for (Procediment procediment: procediments) {
			procedimentsDto.add(toDto(procediment));
		}
		return procedimentsDto;
	}
	
	private GcaProcediment toDto(Procediment procediment) throws SistemaExternException {
		GcaProcediment dto = new GcaProcediment();
		dto.setCodiSIA(procediment.getCodigoSIA());
		dto.setNom(procediment.getNombre());
		dto.setUnitatAdministrativacodi(getUnitatAdministrativa(procediment.getUnidadAdministrativa().getCodigo()));
		//Com que Procediment ens ve amb Boolean i al nostre sistema ho tenim amb boolean primitiu, si es null ho tractam com false:
		if (procediment.getComun()!=null) 
			dto.setComu(procediment.getComun().booleanValue());	
		else 
			dto.setComu(false);
//		dto.setUnidadAdministrativa(getUnitatAdministrativa(procediment.getUnidadAdministrativa().getCodigo()));
//		dto.setUnitatAdministrativaPare(getUnitatAdministrativaArrel(procediment.getUnidadAdministrativa().getCodigo()));
		return dto;
	}

//	private GdaUnitatAdministrativa toDto(UnitatAdministrativa unitat) {
//		GdaUnitatAdministrativa dto = new GdaUnitatAdministrativa();
//		dto.setCodiDir3(unitat.getCodigoDIR3());
//		dto.setNom(unitat.getNombre());
//		return dto;
//	}
	
	private Client generarClient() {
		Client jerseyClient = Client.create();
		jerseyClient.addFilter(
				new ClientFilter() {
					private ArrayList<Object> cookies;
					@Override
					public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
						if (cookies != null) {
							request.getHeaders().put("Cookie", cookies);
						}
						ClientResponse response = getNext().handle(request);
						if (response.getCookies() != null) {
							if (cookies == null) {
								cookies = new ArrayList<Object>();
							}
							cookies.addAll(response.getCookies());
						}
						return response;
					}
				}
		);
		jerseyClient.addFilter(
				new ClientFilter() {
					@Override
					public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
						ClientHandler ch = getNext();
				        ClientResponse resp = ch.handle(request);

				        if (resp.getStatusInfo().getFamily() != Response.Status.Family.REDIRECTION) {
				            return resp;
				        } else {
				            String redirectTarget = resp.getHeaders().getFirst("Location");
				            request.setURI(UriBuilder.fromUri(redirectTarget).build());
				            return ch.handle(request);
				        }
					}
				}
		);
		return jerseyClient;
	}

	private void autenticarClient(
			Client jerseyClient,
			String urlAmbMetode) throws InstanceNotFoundException, MalformedObjectNameException, RemoteException, NamingException, CreateException {
		String username = getUsernameServiceUrl();
		String password = getPasswordServiceUrl();
		
		if (!isServiceBasicAuthentication()) {
			logger.debug(
					"Autenticant client REST per a fer peticions cap a servei desplegat a damunt jBoss (" +
					"urlAmbMetode=" + urlAmbMetode + ", " +
					"username=" + username +
					"password=********)");
			jerseyClient.resource(urlAmbMetode).get(String.class);
			Form form = new Form();
			form.putSingle("j_username", username);
			form.putSingle("j_password", password);
			jerseyClient.
				resource(baseUrl + "j_security_check").
				type("application/x-www-form-urlencoded").
				post(form);
		} else {
			logger.debug(
					"Autenticant REST amb autenticació de tipus HTTP basic (" +
					"urlAmbMetode=" + urlAmbMetode + ", " +
					"username=" + username +
					"password=********)");
			jerseyClient.addFilter(new HTTPBasicAuthFilter(username, password));
		}
	}
	
	private String getBaseUrl() {
		if (baseUrl == null || baseUrl.isEmpty()) {
			baseUrl = PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.gesconadm.base.url");
			if (baseUrl != null && !baseUrl.isEmpty() && !baseUrl.endsWith("/")) {
				baseUrl = baseUrl + "/";
			}
		}
		return baseUrl;
	}
	
	private String getUsernameServiceUrl() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.gesconadm.username");
	}

	private String getPasswordServiceUrl() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.gesconadm.password");
	}
	
	private boolean isServiceBasicAuthentication() {
		String isBasicAuth = PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.gesconadm.basic.authentication");
		if (isBasicAuth == null || isBasicAuth.isEmpty()) {
			return true;
		} else {
			return new Boolean(isBasicAuth).booleanValue();
		}
	}
	
//	private static void addUnitat(String codi, GdaUnitatAdministrativa unitat, String codiPare) {
//		if (!unitatsAdministratives.containsKey(codi)) {
//			Unitat unitatAdministrativa = new Unitat();
//			unitatAdministrativa.setUnitatAdministrativa(unitat);
//			unitatAdministrativa.setCodiPare(codiPare);
//			unitatsAdministratives.put(codi, unitatAdministrativa);
//		}
//	}
	
	@Getter @Setter
	private static class Unitat {
		private GdaUnitatAdministrativa unitatAdministrativa;
		private String codiPare;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(GestorContingutsAdministratiuPluginRolsac.class);
	
}
