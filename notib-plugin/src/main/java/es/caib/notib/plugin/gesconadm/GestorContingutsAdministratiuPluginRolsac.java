package es.caib.notib.plugin.gesconadm;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.representation.Form;
import es.caib.notib.plugin.SistemaExternException;
import lombok.extern.slf4j.Slf4j;

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

@Slf4j
public class GestorContingutsAdministratiuPluginRolsac implements GestorContingutsAdministratiuPlugin {
	
	private static final String ROLSAC_SERVICE_PATH = "api/rest/v1/";
	private static Map<String, String> unitatsAdministratives = new HashMap<>();
	private String baseUrl;
	private final Properties properties;
	private static final String SERVICIOS = "servicios";
	private static final String PROCEDIMIENTOS = "procedimientos";
	private static final String FILTRO_PAGINACION = "filtroPaginacion";
	private static final String PAGE = "{\"page\":\"1\", \"size\":\"100000\"}";
	private static final String FILTRO = "filtro";
	private static final String ERROR = "No s'han pogut consultar els procediments via REST";
	private static final String CODIGO_UA_DIR3 = "{\"codigoUADir3\":\"";
	private static final String CODIGO_UA_DIR3_PARAMS = "\", \"buscarEnDescendientesUA\":\"1\", \"activo\":\"1\", \"estadoUA\":\"1\"}";

	public GestorContingutsAdministratiuPluginRolsac(Properties properties) {
		this.properties = properties;
	}

	@Override
	public GesconAdm getProcSerByCodiSia(String codiSia, boolean isServei) throws SistemaExternException {

		try {
			var url = getBaseUrl() + ROLSAC_SERVICE_PATH + (isServei ? SERVICIOS : PROCEDIMIENTOS);
			var jerseyClient = generarClient();
			autenticarClient(jerseyClient, url);
			var form = new Form();
			form.add(FILTRO_PAGINACION, PAGE);
			form.add(FILTRO, "{\"activo\":\"1\", \"codigoSia\":\"" + codiSia + "\"}");
			var json = jerseyClient.resource(url).type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).post(String.class, form);
			var mapper  = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			return isServei ? getServeiByCodiSia(mapper, json) :getProcedimentByCodiSia(mapper, json);
		} catch (Exception ex) {
			throw new SistemaExternException("No s'han pogut consultar el " + (isServei ? "servei" : "procediment") + " amb codi SIA " + codiSia + " via REST", ex);
		}
	}

	@Override
	public GcaServei getServeiByCodiSia(ObjectMapper mapper, String json) throws Exception {

		var resposta = mapper.readValue(json, RespostaServeis.class);
		if (resposta == null) {
			return null;
		}
		var procs = toServeiDto(resposta.getResultado());
		return !procs.isEmpty() ? procs.get(0) : null;
	}

	@Override
	public GcaProcediment getProcedimentByCodiSia(ObjectMapper mapper, String json) throws Exception {

		var resposta = mapper.readValue(json, RespostaProcediments.class);
		if (resposta == null) {
			return null;
		}
		var procs = toProcedimentDto(resposta.getResultado());
		return !procs.isEmpty() ? procs.get(0) : null;
	}

	@Override
	public List<GcaProcediment> getAllProcediments() throws SistemaExternException {

		List<Procediment> procediments = new ArrayList<>();
		try {
			var urlAmbMetode = getBaseUrl() + ROLSAC_SERVICE_PATH + PROCEDIMIENTOS;
			var jerseyClient = generarClient();
			autenticarClient(jerseyClient, urlAmbMetode);
			var form = new Form();
			form.add(FILTRO_PAGINACION, PAGE);
			form.add(FILTRO, "{\"activo\":\"1\"}");
			var json = jerseyClient.resource(urlAmbMetode).type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).post(String.class, form);
			var mapper  = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			var resposta = mapper.readValue(json, RespostaProcediments.class);
			if (resposta != null) {
				procediments = resposta.getResultado();
			}
			return toProcedimentDto(procediments);
		} catch (Exception ex) {
			throw new SistemaExternException(ERROR, ex);
		}
	}

	@Override
	public List<GcaProcediment> getProcedimentsByUnitat(String codi, int numPagina) throws SistemaExternException {

		List<Procediment> procediments = new ArrayList<>();
		try {
			var urlAmbMetode = getBaseUrl() + ROLSAC_SERVICE_PATH + PROCEDIMIENTOS;
			var jerseyClient = generarClient();
			autenticarClient(jerseyClient, urlAmbMetode);
			var form = new Form();
			form.add(FILTRO_PAGINACION, "{\"page\":\"" + numPagina + "\", \"size\":\"30\"}");
			form.add(FILTRO, CODIGO_UA_DIR3 + codi + CODIGO_UA_DIR3_PARAMS);
			var json = jerseyClient.resource(urlAmbMetode).type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).post(String.class, form);
			var mapper  = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			var resposta = mapper.readValue(json, RespostaProcediments.class);
			if (resposta != null) {
				procediments = resposta.getResultado();
			}
			return toProcedimentDto(procediments);
		} catch (Exception ex) {
			throw new SistemaExternException(ERROR, ex);
		}
	}

	@Override
	public List<GcaProcediment> getProcedimentsByUnitat(String codi) throws SistemaExternException {

		List<Procediment> procediments = new ArrayList<>();
		try {
			var numElements = getTotalProcediments(codi);
			var urlAmbMetode = getBaseUrl() + ROLSAC_SERVICE_PATH + PROCEDIMIENTOS;
			var jerseyClient = generarClient();
			autenticarClient(jerseyClient, urlAmbMetode);
			var form = new Form();
			form.add(FILTRO_PAGINACION, "{\"page\":\"1\", \"size\":\"" + numElements + "\"}");
			form.add(FILTRO, CODIGO_UA_DIR3 + codi + CODIGO_UA_DIR3_PARAMS);
			var json = jerseyClient.resource(urlAmbMetode).type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).post(String.class, form);
			var mapper  = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			var resposta = mapper.readValue(json, RespostaProcediments.class);
			if (resposta != null) {
				procediments = resposta.getResultado();
			}
			return toProcedimentDto(procediments);
		} catch (Exception ex) {
			throw new SistemaExternException(ERROR, ex);
		}
	}
	
	@Override
	public String getUnitatAdministrativa(String codi) throws SistemaExternException {

		if (unitatsAdministratives.containsKey(codi)) {
			return unitatsAdministratives.get(codi);
		}
		try {
			var urlAmbMetode = getBaseUrl() + ROLSAC_SERVICE_PATH + "unidades_administrativas/" + codi;
			var jerseyClient = generarClient();
			autenticarClient(jerseyClient, urlAmbMetode);
			var json = jerseyClient.resource(urlAmbMetode).post(String.class);
			var mapper  = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			var resposta = mapper.readValue(json, RespostaUnitatAdministrativa.class);
			String unitatCodi = null;
			if (resposta.getResultado() != null && !resposta.getResultado().isEmpty()) {
				var unitat = resposta.getResultado().get(0);
				if (unitat.getCodigoDIR3() != null && !unitat.getCodigoDIR3().isEmpty()) {
					unitatCodi = unitat.getCodigoDIR3();
				} else if (unitat.getPadre() != null && unitat.getPadre().getCodigo() != null && !unitat.getPadre().getCodigo().isEmpty()){
					unitatCodi = getUnitatAdministrativa(unitat.getPadre().getCodigo());
				}
			}
			unitatsAdministratives.put(codi, unitatCodi);
			return unitatCodi;
		} catch (Exception ex) {
			throw new SistemaExternException(ERROR, ex);
		}
	}
	
	@Override
	public int getTotalProcediments(String codi) throws SistemaExternException {

		var numeroElements = 0;
		try {
			var urlAmbMetode = getBaseUrl() + ROLSAC_SERVICE_PATH + PROCEDIMIENTOS;
			var jerseyClient = generarClient();
			autenticarClient(jerseyClient, urlAmbMetode);
			var form = new Form();
			form.add(FILTRO_PAGINACION, "{\"page\":\"1\", \"size\":\"1\"}");
			form.add(FILTRO, CODIGO_UA_DIR3 + codi + CODIGO_UA_DIR3_PARAMS);
			var json = jerseyClient.resource(urlAmbMetode).type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).post(String.class, form);
			var mapper  = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			var resposta = mapper.readValue(json, RespostaProcediments.class);
			if (resposta != null) {
				numeroElements = resposta.getNumeroElementos();
			}
			return numeroElements;
		} catch (Exception ex) {
			throw new SistemaExternException("No s'ha pogut consultar els total de procediments via REST", ex);
		}
	}

	@Override
	public List<GcaServei> getAllServeis() throws SistemaExternException {

		List<Servei> serveis = new ArrayList<>();
		try {
			var urlAmbMetode = getBaseUrl() + ROLSAC_SERVICE_PATH + SERVICIOS;
			var jerseyClient = generarClient();
			autenticarClient(jerseyClient, urlAmbMetode);
			var form = new Form();
			form.add(FILTRO_PAGINACION, PAGE);
			form.add(FILTRO, "{\"activo\":\"1\"}");
			var json = jerseyClient.resource(urlAmbMetode).type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).post(String.class, form);
			var mapper  = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			var resposta = mapper.readValue(json, RespostaServeis.class);
			if (resposta != null) {
				serveis = resposta.getResultado();
			}
			return toServeiDto(serveis);
		} catch (Exception ex) {
			throw new SistemaExternException(ERROR, ex);
		}
	}

	@Override
	public List<GcaServei> getServeisByUnitat(String codi, int numPagina) throws SistemaExternException {

		List<Servei> serveis = new ArrayList<>();
		try {
			var urlAmbMetode = getBaseUrl() + ROLSAC_SERVICE_PATH + SERVICIOS;
			Client jerseyClient = generarClient();
			autenticarClient(jerseyClient, urlAmbMetode);
			var form = new Form();
			form.add(FILTRO_PAGINACION, "{\"page\":\"" + numPagina + "\", \"size\":\"30\"}");
			form.add(FILTRO, CODIGO_UA_DIR3 + codi + CODIGO_UA_DIR3_PARAMS);
			var json = jerseyClient.resource(urlAmbMetode).type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).post(String.class, form);
			var mapper  = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			RespostaServeis resposta = mapper.readValue(json, RespostaServeis.class);
			if (resposta != null) {
				serveis = resposta.getResultado();
			}
			return toServeiDto(serveis);
		} catch (Exception ex) {
			throw new SistemaExternException(ERROR, ex);
		}
	}

	@Override
	public List<GcaServei> getServeisByUnitat(String codi) throws SistemaExternException {

		List<Servei> serveis = new ArrayList<>();
		try {
			var numElements = getTotalServeis(codi);
			var urlAmbMetode = getBaseUrl() + ROLSAC_SERVICE_PATH + SERVICIOS;
			var jerseyClient = generarClient();
			autenticarClient(jerseyClient, urlAmbMetode);
			var form = new Form();
			form.add(FILTRO_PAGINACION, "{\"page\":\"1\", \"size\":\"" + numElements + "\"}");
			form.add(FILTRO, CODIGO_UA_DIR3 + codi + CODIGO_UA_DIR3_PARAMS);
			var json = jerseyClient.resource(urlAmbMetode).type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).post(String.class, form);
			var mapper  = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			var resposta = mapper.readValue(json, RespostaServeis.class);
			if (resposta != null) {
				serveis = resposta.getResultado();
			}
			return toServeiDto(serveis);
		} catch (Exception ex) {
			throw new SistemaExternException(ERROR, ex);
		}
	}

	@Override
	public int getTotalServeis(String codi) throws SistemaExternException {

		var numeroElements = 0;
		try {
			var urlAmbMetode = getBaseUrl() + ROLSAC_SERVICE_PATH + SERVICIOS;
			var jerseyClient = generarClient();
			autenticarClient(jerseyClient, urlAmbMetode);
			var form = new Form();
			form.add(FILTRO_PAGINACION, "{\"page\":\"1\", \"size\":\"1\"}");
			form.add(FILTRO, CODIGO_UA_DIR3 + codi + CODIGO_UA_DIR3_PARAMS);
			var json = jerseyClient.resource(urlAmbMetode).type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).post(String.class, form);
			var mapper  = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			var resposta = mapper.readValue(json, RespostaServeis.class);
			if (resposta != null) {
				numeroElements = resposta.getNumeroElementos();
			}
			return numeroElements;
		} catch (Exception ex) {
			throw new SistemaExternException("No s'ha pogut consultar els total de procediments via REST", ex);
		}
	}

	private List<GcaProcediment> toProcedimentDto(List<Procediment> procediments) throws SistemaExternException {

		if (procediments == null) {
			return new ArrayList<>();
		}
		List<GcaProcediment> procedimentsDto = new ArrayList<>();
		for (var procediment: procediments) {
			procedimentsDto.add(toDto(procediment));
		}
		return procedimentsDto;
	}
	
	private GcaProcediment toDto(Procediment procediment) throws SistemaExternException {

		var dto = new GcaProcediment();
		dto.setCodiSIA(procediment.getCodigoSIA());
		dto.setNom(procediment.getNombre());
		dto.setUnitatAdministrativacodi(getUnitatAdministrativa(procediment.getUnidadAdministrativa().getCodigo()));
		dto.setDataActualitzacio(procediment.getFechaActualizacion());
		//Com que Procediment ens ve amb Boolean i al nostre sistema ho tenim amb boolean primitiu, si es null ho tractam com false:
		dto.setComu(procediment.getComun() != null && procediment.getComun());
		return dto;
	}

	private List<GcaServei> toServeiDto(List<Servei> serveis) throws SistemaExternException {

		List<GcaServei> serveisDto = new ArrayList<>();
		for (var servei: serveis) {
			serveisDto.add(toDto(servei));
		}
		return serveisDto;
	}

	private GcaServei toDto(Servei servei) throws SistemaExternException {

		var dto = new GcaServei();
		dto.setCodiSIA(servei.getCodigoSIA());
		dto.setNom(servei.getNombre());
		dto.setUnitatAdministrativacodi(getUnitatAdministrativa(servei.getOrganoInstructor().getCodigo()));
		dto.setDataActualitzacio(servei.getFechaActualizacion());
		dto.setComu(servei.isComun());
		return dto;
	}

	private Client generarClient() {

		var jerseyClient = Client.create();
		jerseyClient.addFilter(
				new ClientFilter() {
					private ArrayList<Object> cookies;
					@Override
					public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
						if (cookies != null) {
							request.getHeaders().put("Cookie", cookies);
						}
						var response = getNext().handle(request);
						if (response.getCookies() == null) {
							return response;
						}
						if (cookies == null) {
							cookies = new ArrayList<>();
						}
						cookies.addAll(response.getCookies());
						return response;
					}
				}
		);
		jerseyClient.addFilter(
				new ClientFilter() {
					@Override
					public ClientResponse handle(ClientRequest request) throws ClientHandlerException {

						var ch = getNext();
				        var resp = ch.handle(request);
				        if (resp.getStatusInfo().getFamily() != Response.Status.Family.REDIRECTION) {
				            return resp;
				        }
						var redirectTarget = resp.getHeaders().getFirst("Location");
						request.setURI(UriBuilder.fromUri(redirectTarget).build());
						return ch.handle(request);
					}
				}
		);
		return jerseyClient;
	}

	private void autenticarClient(Client jerseyClient, String urlAmbMetode) throws InstanceNotFoundException, MalformedObjectNameException, RemoteException, NamingException, CreateException {

		var username = getUsernameServiceUrl();
		var password = getPasswordServiceUrl();
		if (isServiceBasicAuthentication()) {
			log.debug("Autenticant REST amb autenticació de tipus HTTP basic (" + "urlAmbMetode=" + urlAmbMetode + ", " + "username=" + username + ")");
			jerseyClient.addFilter(new HTTPBasicAuthFilter(username, password));
			return;
		}
		log.debug("Autenticant client REST per a fer peticions cap a servei desplegat a damunt jBoss (urlAmbMetode=" + urlAmbMetode + ", " + "username=" + username + ")");
		jerseyClient.resource(urlAmbMetode).get(String.class);
		var form = new Form();
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

		var isBasicAuth = properties.getProperty("es.caib.notib.plugin.gesconadm.basic.authentication");
		return Strings.isNullOrEmpty(isBasicAuth) || Boolean.parseBoolean(isBasicAuth);
	}

}
