/**
 * 
 */
package es.caib.notib.plugin.unitat;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import es.caib.dir3caib.ws.api.catalogo.CatPais;
import es.caib.dir3caib.ws.api.catalogo.Dir3CaibObtenerCatalogosWs;
import es.caib.dir3caib.ws.api.catalogo.Dir3CaibObtenerCatalogosWsService;
import es.caib.notib.plugin.SistemaExternException;
import es.caib.notib.plugin.utils.PropertiesHelper;

/**
 * Implementació de proves del plugin d'unitats organitzatives.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class 	UnitatsOrganitzativesPluginDir3 implements UnitatsOrganitzativesPlugin {
	
	private static final String SERVEI_CERCA = "/rest/busqueda/";
	private static final String SERVEI_CATALEG = "/rest/catalogo/";
	private static final String SERVEI_UNITAT = "/rest/unidad/";
	private static final String SERVEI_ORGANIGRAMA = "/rest/organigrama/";
	private static final String WS_CATALEG = "ws/Dir3CaibObtenerCatalogos";
	
	public Map<String, NodeDir3> organigramaPerEntitat(String codiEntitat) throws SistemaExternException {
		Map<String, NodeDir3> organigrama = new HashMap<String, NodeDir3>();
		try {
			URL url = new URL(getServiceUrl() + SERVEI_ORGANIGRAMA + "?codigo=" + codiEntitat);
			logger.debug("URL: " + url);
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			byte[] response = IOUtils.toByteArray(httpConnection.getInputStream());
			if (response != null && response.length > 0) {
				NodeDir3 arrel = mapper.readValue(
					response, 
					NodeDir3.class);
				nodeToOrganigrama(arrel, organigrama);
			}
			return organigrama;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut consultar l'organigrama de unitats organitzatives via REST (" +
					"codiEntitat=" + codiEntitat + ")",
					ex);
		}
	}
	
	private void nodeToOrganigrama(NodeDir3 unitat, Map<String, NodeDir3> organigrama) {
		organigrama.put(unitat.getCodi(), unitat);
		if (unitat.getFills() != null)
			for (NodeDir3 fill: unitat.getFills())
				nodeToOrganigrama(fill, organigrama);
	}
	
	public List<ObjetoDirectorio> unitatsPerEntitat(String codiEntitat, boolean inclourePare) throws SistemaExternException {
		List<ObjetoDirectorio> unitats = new ArrayList<ObjetoDirectorio>();
		try {
			URL url = new URL(getServiceUrl() + SERVEI_UNITAT + "arbolUnidades?codigo=" + codiEntitat);
			logger.debug("URL: " + url);
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			byte[] response = IOUtils.toByteArray(httpConnection.getInputStream());
			if (response != null && response.length > 0) {
				unitats = mapper.readValue(
					response, 
					TypeFactory.defaultInstance().constructCollectionType(
							List.class,  
							ObjetoDirectorio.class));
			}
			if (inclourePare) {
				ObjetoDirectorio pare = new ObjetoDirectorio();
				pare.setCodi(codiEntitat);
				pare.setDenominacio(unitatDenominacio(codiEntitat));
				unitats.add(pare);
			}
			Collections.sort(unitats);
			return unitats;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar les unitats organitzatives via REST (" +
					"codiEntitat=" + codiEntitat + ")",
					ex);
		}
	}
	
	public String unitatDenominacio(String codiDir3) throws SistemaExternException {
		try {
			URL url = new URL(getServiceUrl() + SERVEI_UNITAT + "denominacion?codigo=" + codiDir3);
			logger.debug("URL: " + url);
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			String denominacio = IOUtils.toString(httpConnection.getInputStream(), StandardCharsets.ISO_8859_1.name());
			//System.out.println("Denominacio: " + denominacio);
			return denominacio;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar la denominació de la unitat organitzativ via REST (" +
					"codiDir3=" + codiDir3 + ")",
					ex);
		}
	}
	
	public List<NodeDir3> cercaUnitats(
			String codi, 
			String denominacio,
			Long nivellAdministracio, 
			Long comunitatAutonoma, 
			Boolean ambOficines, 
			Boolean esUnitatArrel,
			Long provincia, 
			String municipi) throws SistemaExternException {
		try {
			URL url = new URL(getServiceUrl() + SERVEI_CERCA
					+ "organismos?"
					+ "codigo=" + (codi != null ? codi : "")
					+ "&denominacion=" + (denominacio != null ? denominacio : "")
					+ "&codNivelAdministracion=" + (nivellAdministracio != null ? nivellAdministracio : "-1")
					+ "&codComunidadAutonoma=" + (comunitatAutonoma != null ? comunitatAutonoma : "-1")
					+ "&conOficinas=" + (ambOficines != null && ambOficines ? "true" : "false")
					+ "&unidadRaiz=" + (esUnitatArrel != null && esUnitatArrel ? "true" : "false")
					+ "&provincia="+ (provincia != null ? provincia : "-1")
					+ "&localidad=" + (municipi != null ? municipi : "-1")
					+ "&vigentes=true");
			logger.debug("URL: " + url);
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			List<NodeDir3> unitats = mapper.readValue(
					httpConnection.getInputStream(), 
					TypeFactory.defaultInstance().constructCollectionType(
							List.class,  
							NodeDir3.class));
			Collections.sort(unitats);
			return unitats;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar les unitats organitzatives via REST (" +
					"codi=" + codi + ", " +
					"denominacio=" + denominacio + ", " +
					"nivellAdministracio=" + nivellAdministracio + ", " +
					"comunitatAutonoma=" + comunitatAutonoma + ", " +
					"ambOficines=" + ambOficines + ", " +
					"esUnitatArrel=" + esUnitatArrel + ", " +
					"provincia=" + provincia + ", " +
					"municipi=" + municipi + ")",
					ex);
		}
	}

	
	@Override
	public List<NodeDir3> cercaOficines(
			String codi,
			String denominacio,
			Long nivellAdministracio,
			Long comunitatAutonoma,
			Long provincia,
			String municipi) throws SistemaExternException {
		try {
			URL url = new URL(getServiceUrl() + SERVEI_CERCA
					+ "oficinas?"
					+ "codigo=" + (codi != null ? codi : "")
					+ "&denominacion=" + (denominacio != null ? denominacio : "")
					+ "&codNivelAdministracion=" + (nivellAdministracio != null ? nivellAdministracio : "-1")
					+ "&codComunidadAutonoma=" + (comunitatAutonoma != null ? comunitatAutonoma : "-1")
					+ "&provincia="+ (provincia != null ? provincia : "-1")
					+ "&localidad=" + (municipi != null ? municipi : "-1")
					+ "&oficinasSir=false"
					+ "&vigentes=true");
			logger.debug("URL: " + url);
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			List<NodeDir3> oficines = mapper.readValue(
					httpConnection.getInputStream(), 
					TypeFactory.defaultInstance().constructCollectionType(
							List.class,  
							NodeDir3.class));
			Collections.sort(oficines);
			return oficines;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar les oficines via REST (" +
					"codi=" + codi + ", " +
					"denominacio=" + denominacio + ", " +
					"nivellAdministracio=" + nivellAdministracio + ", " +
					"comunitatAutonoma=" + comunitatAutonoma + ", " +
					"provincia=" + provincia + ", " +
					"municipi=" + municipi + ")",
					ex);
		}
	}
	
	@Override
	public List<CodiValor> nivellsAdministracio() throws SistemaExternException {
		try {
			URL url = new URL(getServiceUrl() + SERVEI_CATALEG + "nivelesAdministracion");
			logger.debug("URL: " + url);
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			List<CodiValor> nivellsAdministracio = mapper.readValue(
					httpConnection.getInputStream(), 
					TypeFactory.defaultInstance().constructCollectionType(
							List.class,  
							CodiValor.class));
			Collections.sort(nivellsAdministracio);
			return nivellsAdministracio;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar els nivells d'Administració via REST",
					ex);
		}
	}
	
	public List<CodiValorPais> paisos() throws SistemaExternException {
		List<CodiValorPais> paisos = new ArrayList<CodiValorPais>();
		try {
			List<CatPais> paisosWs = getCatalogosWsWithSecurityApi().obtenerCatPais();
			
			for (CatPais catPaisWs : paisosWs) {
				CodiValorPais pais = new CodiValorPais();
				pais.setAlfa2Pais(catPaisWs.getAlfa2Pais());
				pais.setAlfa3Pais(catPaisWs.getAlfa3Pais());
				pais.setCodiPais(catPaisWs.getCodigoPais());
				pais.setDescripcioPais(catPaisWs.getDescripcionPais());
				paisos.add(pais);
			}
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar els paisos via WS",
					ex);
		}
		return paisos;
	}
	
	@Override
	public List<CodiValor> comunitatsAutonomes() throws SistemaExternException {
		try {
			URL url = new URL(getServiceUrl() + SERVEI_CATALEG + "comunidadesAutonomas");
			logger.debug("URL: " + url);
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			List<CodiValor> nivellsAdministracio = mapper.readValue(
					httpConnection.getInputStream(), 
					TypeFactory.defaultInstance().constructCollectionType(
							List.class,  
							CodiValor.class));
			Collections.sort(nivellsAdministracio);
			return nivellsAdministracio;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar les comunitats autònomes via REST",
					ex);
		}
	}
	
	@Override
	public List<CodiValor> provincies() throws SistemaExternException {
		try {
			URL url = new URL(getServiceUrl() + SERVEI_CATALEG + "provincias");
			logger.debug("URL: " + url);
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			List<CodiValor> provincies = mapper.readValue(
					httpConnection.getInputStream(), 
					TypeFactory.defaultInstance().constructCollectionType(
							List.class,  
							CodiValor.class));
			Collections.sort(provincies);
			return provincies;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar les províncies via REST",
					ex);
		}
	}
	
	@Override
	public List<CodiValor> provincies(String codiCA) throws SistemaExternException {
		try {
			URL url = new URL(getServiceUrl() + SERVEI_CATALEG 
					+ "provincias/comunidadAutonoma?"
					+ "id=" + codiCA);
			logger.debug("URL: " + url);
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			List<CodiValor> provincies = mapper.readValue(
					httpConnection.getInputStream(), 
					TypeFactory.defaultInstance().constructCollectionType(
							List.class,  
							CodiValor.class));
			Collections.sort(provincies);
			return provincies;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar les comunitats autònomes via REST",
					ex);
		}
	}


	@Override
	public List<CodiValor> localitats(String codiProvincia) throws SistemaExternException {
		try {
			URL url = new URL(getServiceUrl() + SERVEI_CATALEG 
					+ "localidades/provincia/entidadGeografica?"
					+ "codigoProvincia=" + codiProvincia
					+ "&codigoEntidadGeografica=01");
			logger.debug("URL: " + url);
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			List<CodiValor> localitats = mapper.readValue(
					httpConnection.getInputStream(), 
					TypeFactory.defaultInstance().constructCollectionType(
							List.class,  
							CodiValor.class));
			Collections.sort(localitats);
			return localitats;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar les localitats via REST",
					ex);
		}
	}
	
	public Dir3CaibObtenerCatalogosWs getCatalogosWsWithSecurityApi() throws Exception {

		final String endpoint = getServiceUrl() + WS_CATALEG;
		final URL wsdl = new URL(endpoint + "?wsdl");
		
		Dir3CaibObtenerCatalogosWsService service = new Dir3CaibObtenerCatalogosWsService(wsdl);
		Dir3CaibObtenerCatalogosWs api = service.getDir3CaibObtenerCatalogosWs();

		configAddressUserPassword(getUsernameServiceUrl(), getPasswordServiceUrl(), endpoint, api);
		
		return api;
	}
	
	private String getServiceUrl() {
		String dir3Url = PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.plugin.unitats.dir3.url");
		if (!dir3Url.endsWith("/"))
			dir3Url = dir3Url + "/";
		return dir3Url;
	}
	
	private String getUsernameServiceUrl() {
		String dir3Username = PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.plugin.unitats.dir3.username");
		return dir3Username;
	}
	
	private String getPasswordServiceUrl() {
		String dir3Password = PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.plugin.unitats.dir3.password");
		return dir3Password;
	}
	
	public static void configAddressUserPassword(
			String usr, 
			String pwd,
			String endpoint, 
			Object api) {

		Map<String, Object> reqContext = ((BindingProvider) api).getRequestContext();
		reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
		reqContext.put(BindingProvider.USERNAME_PROPERTY, usr);
		reqContext.put(BindingProvider.PASSWORD_PROPERTY, pwd);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(UnitatsOrganitzativesPluginDir3.class);
}
