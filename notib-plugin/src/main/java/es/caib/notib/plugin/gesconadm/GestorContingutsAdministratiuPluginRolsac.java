package es.caib.notib.plugin.gesconadm;

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
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.CreateException;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.naming.NamingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class GestorContingutsAdministratiuPluginRolsac implements GestorContingutsAdministratiuPlugin {
	
	private static final String ROLSAC_SERVICE_PATH = "api/rest/v1/";
	private static Map<String, String> unitatsAdministratives = new HashMap<String, String>();
	private String baseUrl;

	private final Properties properties;

	public GestorContingutsAdministratiuPluginRolsac(Properties properties) {
		this.properties = properties;
	}

	@Override
	public GesconAdm getProcSerByCodiSia(String codiSia, boolean isServei) throws SistemaExternException {

		try {
			String url = getBaseUrl() + ROLSAC_SERVICE_PATH + (isServei ? "servicios" : "procedimientos");
			Client jerseyClient = generarClient();
			autenticarClient(jerseyClient, url);
			Form form = new Form();
			form.add("filtroPaginacion", "{\"page\":\"1\", \"size\":\"100000\"}");
			form.add("filtro", "{\"activo\":\"1\", \"codigoSia\":\"" + codiSia + "\"}");
			String json = jerseyClient.resource(url).type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).post(String.class, form);
			ObjectMapper mapper  = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			return isServei ? getServeiByCodiSia(mapper, json) :getProcedimentByCodiSia(mapper, json);
		} catch (Exception ex) {
			throw new SistemaExternException("No s'han pogut consultar el " + (isServei ? "servei" : "procediment")
					+ " amb codi SIA " + codiSia + " via REST", ex);
		}
	}

	@Override
	public GcaServei getServeiByCodiSia(ObjectMapper mapper, String json) throws Exception {

		try {
			RespostaServeis resposta = mapper.readValue(json, RespostaServeis.class);
			if (resposta == null) {
				return null;
			}
			List<GcaServei> procs = toServeiDto(resposta.getResultado());
			return !procs.isEmpty() ? procs.get(0) : null;
		} catch (Exception ex) {
			throw ex;
		}
	}

	@Override
	public GcaProcediment getProcedimentByCodiSia(ObjectMapper mapper, String json) throws Exception {

		try {
			RespostaProcediments resposta = mapper.readValue(json, RespostaProcediments.class);
			if (resposta == null) {
				return null;
			}
			List<GcaProcediment> procs = toProcedimentDto(resposta.getResultado());
			return !procs.isEmpty() ? procs.get(0) : null;
		} catch (Exception ex) {
			throw ex;
		}
	}

	@Override
	public List<GcaProcediment> getAllProcediments() throws SistemaExternException {
		List<Procediment> procediments = new ArrayList<Procediment>();
		try {
			String urlAmbMetode = getBaseUrl() + ROLSAC_SERVICE_PATH + "procedimientos";
			
			Client jerseyClient = generarClient();
			autenticarClient(
					jerseyClient,
					urlAmbMetode);
			
			Form form = new Form();
			form.add("filtroPaginacion", "{\"page\":\"1\", \"size\":\"100000\"}");
			form.add("filtro", "{\"activo\":\"1\"}");
		    
			String json = jerseyClient.
					resource(urlAmbMetode).
					type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).
					accept(MediaType.APPLICATION_JSON_TYPE).
					post(String.class, form);
			
			ObjectMapper mapper  = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			RespostaProcediments resposta = mapper.readValue(json, RespostaProcediments.class);
			if (resposta != null)
				procediments = resposta.getResultado();
			return toProcedimentDto(procediments);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar els procediments via REST",
					ex);
		}
	}
	
	@Override
	public List<GcaProcediment> getProcedimentsByUnitat(
			String codi,
			int numPagina) throws SistemaExternException {
		List<Procediment> procediments = new ArrayList<Procediment>();
		try {
			String urlAmbMetode = getBaseUrl() + ROLSAC_SERVICE_PATH + "procedimientos";
			
			Client jerseyClient = generarClient();
			autenticarClient(
					jerseyClient,
					urlAmbMetode);
			
			Form form = new Form();
			form.add("filtroPaginacion", "{\"page\":\"" + numPagina + "\", \"size\":\"30\"}");
			form.add("filtro", "{\"codigoUADir3\":\"" + codi + "\", \"buscarEnDescendientesUA\":\"1\", \"activo\":\"1\", \"estadoUA\":\"1\"}");
		    
			String json = jerseyClient.
					resource(urlAmbMetode).
					type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).
					accept(MediaType.APPLICATION_JSON_TYPE).
					post(String.class, form);
			
			ObjectMapper mapper  = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			RespostaProcediments resposta = mapper.readValue(json, RespostaProcediments.class);
			if (resposta != null)
				procediments = resposta.getResultado();
			return toProcedimentDto(procediments);
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
	
	@Override
	public int getTotalProcediments(String codi) throws SistemaExternException {
		int numeroElements = 0;
		try {
			String urlAmbMetode = getBaseUrl() + ROLSAC_SERVICE_PATH + "procedimientos";
			
			Client jerseyClient = generarClient();
			autenticarClient(
					jerseyClient,
					urlAmbMetode);
			
			Form form = new Form();
			form.add("filtroPaginacion", "{\"page\":\"1\", \"size\":\"1\"}");
			form.add("filtro", "{\"codigoUADir3\":\"" + codi + "\", \"buscarEnDescendientesUA\":\"1\", \"activo\":\"1\", \"estadoUA\":\"1\"}");
		    
			String json = jerseyClient.
					resource(urlAmbMetode).
					type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).
					accept(MediaType.APPLICATION_JSON_TYPE).
					post(String.class, form);
			
			ObjectMapper mapper  = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			RespostaProcediments resposta = mapper.readValue(json, RespostaProcediments.class);
			if (resposta != null) 
				numeroElements = resposta.getNumeroElementos();
			return numeroElements;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut consultar els total de procediments via REST",
					ex);
		}
	}

	@Override
	public List<GcaServei> getAllServeis() throws SistemaExternException {
		List<Servei> serveis = new ArrayList<Servei>();
		try {
			String urlAmbMetode = getBaseUrl() + ROLSAC_SERVICE_PATH + "servicios";

			Client jerseyClient = generarClient();
			autenticarClient(
					jerseyClient,
					urlAmbMetode);

			Form form = new Form();
			form.add("filtroPaginacion", "{\"page\":\"1\", \"size\":\"100000\"}");
			form.add("filtro", "{\"activo\":\"1\"}");

			String json = jerseyClient.
					resource(urlAmbMetode).
					type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).
					accept(MediaType.APPLICATION_JSON_TYPE).
					post(String.class, form);

			ObjectMapper mapper  = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			RespostaServeis resposta = mapper.readValue(json, RespostaServeis.class);
			if (resposta != null)
				serveis = resposta.getResultado();
			return toServeiDto(serveis);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar els procediments via REST",
					ex);
		}
	}

	@Override
	public List<GcaServei> getServeisByUnitat(String codi, int numPagina) throws SistemaExternException {
		List<Servei> serveis = new ArrayList<Servei>();
		try {
			String urlAmbMetode = getBaseUrl() + ROLSAC_SERVICE_PATH + "servicios";

			Client jerseyClient = generarClient();
			autenticarClient(
					jerseyClient,
					urlAmbMetode);

			Form form = new Form();
			form.add("filtroPaginacion", "{\"page\":\"" + numPagina + "\", \"size\":\"30\"}");
			form.add("filtro", "{\"codigoUADir3\":\"" + codi + "\", \"buscarEnDescendientesUA\":\"1\", \"activo\":\"1\", \"estadoUA\":\"1\"}");

			String json = jerseyClient.
					resource(urlAmbMetode).
					type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).
					accept(MediaType.APPLICATION_JSON_TYPE).
					post(String.class, form);

			ObjectMapper mapper  = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			RespostaServeis resposta = mapper.readValue(json, RespostaServeis.class);
			if (resposta != null)
				serveis = resposta.getResultado();
			return toServeiDto(serveis);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar els procediments via REST",
					ex);
		}
	}

	@Override
	public int getTotalServeis(String codi) throws SistemaExternException {
		int numeroElements = 0;
		try {
			String urlAmbMetode = getBaseUrl() + ROLSAC_SERVICE_PATH + "servicios";

			Client jerseyClient = generarClient();
			autenticarClient(
					jerseyClient,
					urlAmbMetode);

			Form form = new Form();
			form.add("filtroPaginacion", "{\"page\":\"1\", \"size\":\"1\"}");
			form.add("filtro", "{\"codigoUADir3\":\"" + codi + "\", \"buscarEnDescendientesUA\":\"1\", \"activo\":\"1\", \"estadoUA\":\"1\"}");

			String json = jerseyClient.
					resource(urlAmbMetode).
					type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).
					accept(MediaType.APPLICATION_JSON_TYPE).
					post(String.class, form);

			ObjectMapper mapper  = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			RespostaServeis resposta = mapper.readValue(json, RespostaServeis.class);
			if (resposta != null)
				numeroElements = resposta.getNumeroElementos();
			return numeroElements;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut consultar els total de procediments via REST",
					ex);
		}
	}

	private List<GcaProcediment> toProcedimentDto(List<Procediment> procediments) throws SistemaExternException {

		if (procediments == null) {
			return new ArrayList<>();
		}
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
		dto.setDataActualitzacio(procediment.getFechaActualizacion());
		//Com que Procediment ens ve amb Boolean i al nostre sistema ho tenim amb boolean primitiu, si es null ho tractam com false:
		if (procediment.getComun()!=null) 
			dto.setComu(procediment.getComun().booleanValue());	
		else 
			dto.setComu(false);
//		dto.setUnidadAdministrativa(getUnitatAdministrativa(procediment.getUnidadAdministrativa().getCodigo()));
//		dto.setUnitatAdministrativaPare(getUnitatAdministrativaArrel(procediment.getUnidadAdministrativa().getCodigo()));
		return dto;
	}

	private List<GcaServei> toServeiDto(List<Servei> serveis) throws SistemaExternException {
		List<GcaServei> serveisDto = new ArrayList<GcaServei>();
		for (Servei servei: serveis) {
			serveisDto.add(toDto(servei));
		}
		return serveisDto;
	}

	private GcaServei toDto(Servei servei) throws SistemaExternException {
		GcaServei dto = new GcaServei();
		dto.setCodiSIA(servei.getCodigoSIA());
		dto.setNom(servei.getNombre());
		dto.setUnitatAdministrativacodi(getUnitatAdministrativa(servei.getOrganoInstructor().getCodigo()));
		dto.setDataActualitzacio(servei.getFechaActualizacion());
		dto.setComu(servei.isComun());
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

	private void autenticarClient(Client jerseyClient, String urlAmbMetode) throws InstanceNotFoundException, MalformedObjectNameException, RemoteException, NamingException, CreateException {

		String username = getUsernameServiceUrl();
		String password = getPasswordServiceUrl();
		if (isServiceBasicAuthentication()) {
			logger.debug("Autenticant REST amb autenticació de tipus HTTP basic (" + "urlAmbMetode=" + urlAmbMetode + ", " + "username=" + username + "password=********)");
			jerseyClient.addFilter(new HTTPBasicAuthFilter(username, password));
			return;
		}
		logger.debug("Autenticant client REST per a fer peticions cap a servei desplegat a damunt jBoss (" +
					"urlAmbMetode=" + urlAmbMetode + ", " + "username=" + username + "password=********)");
		jerseyClient.resource(urlAmbMetode).get(String.class);
		Form form = new Form();
		form.putSingle("j_username", username);
		form.putSingle("j_password", password);
		jerseyClient.resource(baseUrl + "j_security_check").type("application/x-www-form-urlencoded").post(form);
	}
	
	private String getBaseUrl() {

		if (baseUrl != null && !baseUrl.isEmpty()) {
			return baseUrl;
		}
		baseUrl = properties.getProperty("es.caib.notib.plugin.gesconadm.base.url");
		if (baseUrl != null && !baseUrl.isEmpty() && !baseUrl.endsWith("/")) {
			baseUrl = baseUrl + "/";
		}
		return baseUrl;
	}
	
	private String getUsernameServiceUrl() {
		return properties.getProperty("es.caib.notib.plugin.gesconadm.username");
	}

	private String getPasswordServiceUrl() {
		return properties.getProperty("es.caib.notib.plugin.gesconadm.password");
	}
	
	private boolean isServiceBasicAuthentication() {
		String isBasicAuth = properties.getProperty("es.caib.notib.plugin.gesconadm.basic.authentication");
		return isBasicAuth == null || isBasicAuth.isEmpty() ? true : new Boolean(isBasicAuth).booleanValue();
	}

	@Getter @Setter
	private static class Unitat {
		private GdaUnitatAdministrativa unitatAdministrativa;
		private String codiPare;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(GestorContingutsAdministratiuPluginRolsac.class);

}
