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
import es.caib.notib.plugin.AbstractSalutPlugin;
import es.caib.notib.plugin.SistemaExternException;
import es.caib.notib.plugin.utils.NotibLoggerPlugin;
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
public class GestorContingutsAdministratiuPluginRolsac extends AbstractSalutPlugin implements GestorContingutsAdministratiuPlugin {
	
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

	private NotibLoggerPlugin logger = new NotibLoggerPlugin(log);

	public GestorContingutsAdministratiuPluginRolsac(Properties properties, boolean configuracioEspecifica) {
		this.properties = properties;
		this.configuracioEspecifica = configuracioEspecifica;
		logger.setMostrarLogs(Boolean.parseBoolean(properties.getProperty("es.caib.notib.log.tipus.plugin.ROLSAC")));
	}

	@Override
	public GesconAdm getProcSerByCodiSia(String codiSia, boolean isServei) throws SistemaExternException {

		var procSer = isServei ? "servei" : "procediment";
		try {
            long startTime = System.currentTimeMillis();
			var url = getBaseUrl() + ROLSAC_SERVICE_PATH + (isServei ? SERVICIOS : PROCEDIMIENTOS);
			logger.info("[ROLSAC] Obtinguent el " + procSer + " amb codi " + codiSia + " url " + url);
			var jerseyClient = generarClient();
			autenticarClient(jerseyClient, url);
			var form = new Form();
			form.add(FILTRO_PAGINACION, PAGE);
			form.add(FILTRO, "{\"activo\":\"1\", \"codigoSia\":\"" + codiSia + "\"}");
			var json = jerseyClient.resource(url).type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).post(String.class, form);
			var mapper  = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			var result = isServei ? getServeiByCodiSia(mapper, json) :getProcedimentByCodiSia(mapper, json);
			incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return result;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar el " + procSer + " amb codi SIA " + codiSia + " via REST", ex);
		}
	}

	private GcaServei getServeiByCodiSia(ObjectMapper mapper, String json) throws Exception {

		var resposta = mapper.readValue(json, RespostaServeis.class);
		if (resposta == null) {
			return null;
		}
		var procs = toServeiDto(resposta.getResultado());
		return !procs.isEmpty() ? procs.get(0) : null;
	}

	private GcaProcediment getProcedimentByCodiSia(ObjectMapper mapper, String json) throws Exception {

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
            long startTime = System.currentTimeMillis();
			var urlAmbMetode = getBaseUrl() + ROLSAC_SERVICE_PATH + PROCEDIMIENTOS;
			logger.info("[ROLSAC] Obtinguent tots els procediments de la url " + urlAmbMetode);
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
			var result = toProcedimentDto(procediments);
			incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return result;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException(ERROR, ex);
		}
	}

	@Override
	public List<GcaProcediment> getProcedimentsByUnitat(String codi, int numPagina) throws SistemaExternException {

		List<Procediment> procediments = new ArrayList<>();
		try {
            long startTime = System.currentTimeMillis();
			var urlAmbMetode = getBaseUrl() + ROLSAC_SERVICE_PATH + PROCEDIMIENTOS;
			logger.info("[ROLSAC] Obtinguent tots els procediments (pagina " + numPagina + ") per la unitat " + codi + " de la url " + urlAmbMetode);
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
			var result = toProcedimentDto(procediments);
			incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return result;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException(ERROR, ex);
		}
	}

	@Override
	public List<GcaProcediment> getProcedimentsByUnitat(String codi) throws SistemaExternException {

		List<Procediment> procediments = new ArrayList<>();
		try {
            long startTime = System.currentTimeMillis();
			var numElements = getTotalProcediments(codi);
			var urlAmbMetode = getBaseUrl() + ROLSAC_SERVICE_PATH + PROCEDIMIENTOS;
			logger.info("[ROLSAC] Obtinguent tots els procediments per la unitat " + codi + " de la url " + urlAmbMetode);
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
			var result = toProcedimentDto(procediments);
			incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return result;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException(ERROR, ex);
		}
	}
	
	private String getUnitatAdministrativa(String codi) throws SistemaExternException {

		if (unitatsAdministratives.containsKey(codi)) {
			return unitatsAdministratives.get(codi);
		}
		try {
			var urlAmbMetode = getBaseUrl() + ROLSAC_SERVICE_PATH + "unidades_administrativas/" + codi;
			logger.info("[ROLSAC] Obtinguent unitat administrativa " + codi + " de la url " + urlAmbMetode);
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
            long startTime = System.currentTimeMillis();
			var urlAmbMetode = getBaseUrl() + ROLSAC_SERVICE_PATH + PROCEDIMIENTOS;
			logger.info("[ROLSAC] Obtinguent el total de procediments de la unitat administrativa " + codi + " de la url " + urlAmbMetode);
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
			incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return numeroElements;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'ha pogut consultar els total de procediments via REST", ex);
		}
	}

	@Override
	public List<GcaServei> getAllServeis() throws SistemaExternException {

		List<Servei> serveis = new ArrayList<>();
		try {
            long startTime = System.currentTimeMillis();
			var urlAmbMetode = getBaseUrl() + ROLSAC_SERVICE_PATH + SERVICIOS;
			logger.info("[ROLSAC] Obtinguent tots els serveis de la url " + urlAmbMetode);
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
			var result = toServeiDto(serveis);
			incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return result;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException(ERROR, ex);
		}
	}

	@Override
	public List<GcaServei> getServeisByUnitat(String codi, int numPagina) throws SistemaExternException {

		List<Servei> serveis = new ArrayList<>();
		try {
            long startTime = System.currentTimeMillis();
			var urlAmbMetode = getBaseUrl() + ROLSAC_SERVICE_PATH + SERVICIOS;
			logger.info("[ROLSAC] Obtinguent tots els serveis (pagina " + numPagina + ") per la unitat " + codi + " de la url " + urlAmbMetode);
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
			var result = toServeiDto(serveis);
			incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return result;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException(ERROR, ex);
		}
	}

	@Override
	public List<GcaServei> getServeisByUnitat(String codi) throws SistemaExternException {

		List<Servei> serveis = new ArrayList<>();
		try {
            long startTime = System.currentTimeMillis();
			var numElements = getTotalServeis(codi);
			var urlAmbMetode = getBaseUrl() + ROLSAC_SERVICE_PATH + SERVICIOS;
			logger.info("[ROLSAC] Obtinguent tots els serveis de la unitat administrativa " + codi + " de la url " + urlAmbMetode);
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
			var result = toServeiDto(serveis);
			incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return result;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException(ERROR, ex);
		}
	}

	@Override
	public int getTotalServeis(String codi) throws SistemaExternException {

		var numeroElements = 0;
		try {
            long startTime = System.currentTimeMillis();
			var urlAmbMetode = getBaseUrl() + ROLSAC_SERVICE_PATH + SERVICIOS;
			logger.info("[ROLSAC] Obtinguent el total de serveis de la unitat administrativa " + codi + " de la url " + urlAmbMetode);
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
			incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return numeroElements;
		} catch (Exception ex) {
			incrementarOperacioError();
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
			logger.info("[ROLSAC] Autenticant REST amb autenticació de tipus HTTP basic (" + "urlAmbMetode=" + urlAmbMetode + ", " + "username=" + username + ")");
			jerseyClient.addFilter(new HTTPBasicAuthFilter(username, password));
			return;
		}
		logger.info("[ROLSAC] Autenticant client REST per a fer peticions cap a servei desplegat a damunt jBoss (urlAmbMetode=" + urlAmbMetode + ", " + "username=" + username + ")");
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
