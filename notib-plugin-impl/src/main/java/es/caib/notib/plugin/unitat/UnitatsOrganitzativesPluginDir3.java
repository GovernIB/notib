/**
 * 
 */
package es.caib.notib.plugin.unitat;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;

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
	private static final String WS_CATALEG = "ws/Dir3CaibObtenerCatalogos";
	
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
			System.out.println("URL: " + url);
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
			System.out.println("URL: " + url);
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
			System.out.println("URL: " + url);
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
			System.out.println("URL: " + url);
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
			System.out.println("URL: " + url);
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
			System.out.println("URL: " + url);
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
			System.out.println("URL: " + url);
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
					"No s'han pogut consultar les comunitats autònomes via REST",
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
	
}
