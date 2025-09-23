/**
 * 
 */
package es.caib.notib.plugin.unitat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Strings;
import es.caib.dir3caib.ws.api.catalogo.CatPais;
import es.caib.dir3caib.ws.api.oficina.OficinaTF;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.plugin.AbstractSalutPlugin;
import es.caib.notib.plugin.SistemaExternException;
import es.caib.notib.plugin.utils.NotibLoggerPlugin;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.xml.ws.BindingProvider;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Implementació de proves del plugin d'unitats organitzatives.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class UnitatsOrganitzativesPluginDir3 extends AbstractSalutPlugin implements UnitatsOrganitzativesPlugin {
	
	private static final String SERVEI_CERCA = "/rest/busqueda/";
	private static final String SERVEI_CATALEG = "/rest/catalogo/";
	private static final String SERVEI_UNITAT = "/rest/unidad/";
	private static final String SERVEI_UNITATS = "/rest/unidades/";
	private static final String SERVEI_ORGANIGRAMA = "/rest/organigrama/";
	private static final String SERVEI_OFICINES = "/rest/oficinas/";
	private static final String URL = "URL: ";
	private static final String FECHA_ACT_PARAM = "&fechaActualizacion=";
	private static final String FECHA_SINC_PARAM = "&fechaSincronizacion=";
	private final Properties properties;

	private NotibLoggerPlugin logger = new NotibLoggerPlugin(log);

	public UnitatsOrganitzativesPluginDir3(Properties properties, boolean configuracioEspecifica) {
		this.properties = properties;
		this.configuracioEspecifica = configuracioEspecifica;
        urlPlugin = properties.getProperty("es.caib.notib.plugin.unitats.dir3.url");
		logger.setMostrarLogs(Boolean.parseBoolean(properties.getProperty("es.caib.notib.log.tipus.plugin.UNITATS")));
	}

	@Override
	public Map<String, NodeDir3> organigramaPerEntitat(String codiEntitat) throws SistemaExternException {

		Map<String, NodeDir3> organigrama = new HashMap<>();
		try {
            long startTime = System.currentTimeMillis();
			var url = new URL(getServiceUrl() + SERVEI_ORGANIGRAMA + "?codigo=" + codiEntitat);
			logger.info("[UNITATS] Consulta organigrama per entitat " + codiEntitat + " url" + url);
			var httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			var response = IOUtils.toByteArray(httpConnection.getInputStream());
			if (response != null && response.length > 0) {
				var arrel = mapper.readValue(response, NodeDir3.class);
				nodeToOrganigrama(arrel, organigrama);
			}
			incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return organigrama;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'ha pogut consultar l'organigrama de unitats organitzatives via REST (codiEntitat=" + codiEntitat + ")", ex);
		}
	}
	
	private void nodeToOrganigrama(NodeDir3 unitat, Map<String, NodeDir3> organigrama) {

		if (!unitat.getEstat().startsWith("V") && !unitat.getEstat().startsWith("T")) {
			return;
		}
		// Unitats Vigents o Transitòries
		organigrama.put(unitat.getCodi(), unitat);
		if (unitat.getFills() == null){
			return;
		}
		for (NodeDir3 fill: unitat.getFills()) {
			nodeToOrganigrama(fill, organigrama);
		}
	}
	
	@Override
	public Map<String, NodeDir3> organigramaPerEntitat(String pareCodi, Date fechaActualizacion, Date fechaSincronizacion) throws SistemaExternException {


		try {
            long startTime = System.currentTimeMillis();
			var url = new URL(getServiceUrl() + SERVEI_UNITATS + "obtenerArbolUnidades?codigo=" + pareCodi +
					(fechaActualizacion != null ? FECHA_ACT_PARAM + fechaActualizacion : "") +
					(fechaSincronizacion != null ? FECHA_SINC_PARAM + fechaSincronizacion : ""));
			logger.info("[UNITATS] Consulta organigrama per entitat. Codi pare " + pareCodi + " `data actualitzacio" + fechaActualizacion + " data sincronitzacio " + fechaSincronizacion + " url " + url);
			var response = getResponse(url);
			var mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			if (response == null || response.length == 0) {
				incrementarOperacioOk(System.currentTimeMillis() - startTime);
				return new HashMap<>();
			}
			List<UnitatOrganitzativa> arbol = mapper.readValue(response, new TypeReference<>() {});
			incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return emplenearOrganigrama(pareCodi, arbol);
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar les unitats organitzatives via WS (pareCodi=" + pareCodi + ")", ex);
		}
	}

	private Map<String, NodeDir3> emplenearOrganigrama(String pareCodi, List<UnitatOrganitzativa> arbol) {

		Map<String, NodeDir3> organigrama = new HashMap<>();
		for(UnitatOrganitzativa unidadTF: arbol){
			if (!"V".equals(unidadTF.getCodigoEstadoEntidad()) && !"T".equals(unidadTF.getCodigoEstadoEntidad())) {
				continue;
			}
			// Unitats Vigents o Transitòries
			var node = toNodeDir3(unidadTF);
			var pare = organigrama.get(node.getSuperior());
			if (!node.getCodi().equalsIgnoreCase(pareCodi) && pare == null) {
				continue;
			}
			organigrama.put(node.getCodi(), node);
			if (pare == null) {
				continue;
			}
			List<NodeDir3> fills = pare.getFills();
			if (fills == null) {
				fills = new ArrayList<>();
				pare.setFills(fills);
			}
			fills.add(node);
		}
		return organigrama;
	}

	@Override
	public byte[] findAmbPareJson(String pareCodi, Date dataActualitzacio, Date dataSincronitzacio) throws SistemaExternException {
		try {
            long startTime = System.currentTimeMillis();
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			var url = new URL(getServiceUrl() + SERVEI_UNITATS + "obtenerArbolUnidades?codigo=" + pareCodi + "&denominacionCooficial=false" +
					(dataActualitzacio != null ? FECHA_ACT_PARAM + sdf.format(dataActualitzacio) : "") +
					(dataSincronitzacio != null ? FECHA_SINC_PARAM + sdf.format(dataSincronitzacio) : ""));
			logger.info("[UNITATS] Consulta organigrama JSON per entitat. Codi pare " + pareCodi + " `data actualitzacio" + dataActualitzacio + " data sincronitzacio " + dataSincronitzacio + " url " + url);
			byte[] response = getResponse(url);
			incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return response;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar les unitats organitzatives via WS (pareCodi=" + pareCodi + ")", ex);
		}
	}

	@Override
	public List<NodeDir3> findAmbPare(String pareCodi, Date dataActualitzacio, Date dataSincronitzacio) throws SistemaExternException {

		try {
            long startTime = System.currentTimeMillis();
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			List<NodeDir3> unitats = new ArrayList<>();
			List<UnitatOrganitzativa> unidades = new ArrayList<>();
			var url = new URL(getServiceUrl() + SERVEI_UNITATS + "obtenerArbolUnidades?codigo=" + pareCodi + "&denominacionCooficial=false" +
					(dataActualitzacio != null ? FECHA_ACT_PARAM + sdf.format(dataActualitzacio) : "") +
					(dataSincronitzacio != null ? FECHA_SINC_PARAM + sdf.format(dataSincronitzacio) : ""));
			logger.info("[UNITATS] Consulta organigrama per entitat. Codi pare " + pareCodi + " `data actualitzacio" + dataActualitzacio + " data sincronitzacio " + dataSincronitzacio + " url " + url);
			byte[] response = getResponse(url);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			if (response != null && response.length > 0) {
				unidades = mapper.readValue(response, new TypeReference<>() {});
			}
			if (unidades != null) {
				for (UnitatOrganitzativa unidad : unidades) {
					unitats.add(toNodeDir3(unidad));
				}
			}
			incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return unitats;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar les unitats organitzatives via WS (pareCodi=" + pareCodi + ")", ex);
		}
	}

	@Override
	public NodeDir3 findAmbCodi(String codi, Date dataActualitzacio, Date dataSincronitzacio) throws SistemaExternException {

		try {
            long startTime = System.currentTimeMillis();
			UnitatOrganitzativa unidad = null;
			var url = new URL(getServiceUrl() + SERVEI_UNITATS + "obtenerUnidad?codigo=" + codi + "&denominacionCooficial=false" +
					(dataActualitzacio != null ? FECHA_ACT_PARAM + dataActualitzacio : "") +
					(dataSincronitzacio != null ? FECHA_SINC_PARAM + dataSincronitzacio : ""));
			logger.info("[UNITATS] Consulta organigrama JSON per entitat. Codi " + codi + " `data actualitzacio" + dataActualitzacio + " data sincronitzacio " + dataSincronitzacio + " url " + url);
			var response = getResponse(url);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			if (response != null && response.length > 0) {
				unidad = mapper.readValue(response, UnitatOrganitzativa.class);
			}
			incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return unidad != null ? toNodeDir3(unidad) : null;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'ha pogut consultar la unitat organitzativa amb codi (pareCodi=" + codi + ")", ex);
		}
	}

	private NodeDir3 toNodeDir3(UnitatOrganitzativa unidadTF) {
		return NodeDir3.builder()
				.codi(unidadTF.getCodigo())
				.versio(unidadTF.getVersion())
				.denominacio(unidadTF.getDenominacion())
				.denominacionCooficial(unidadTF.getDenominacionCooficial())
				.estat(unidadTF.getCodigoEstadoEntidad())
				.arrel(unidadTF.getCodUnidadRaiz())
				.superior(unidadTF.getCodUnidadSuperior())
				.localitat(unidadTF.getDescripcionLocalidad())
				.idPare(unidadTF.getCodUnidadSuperior())
				.historicosUO(unidadTF.getHistoricosUO())
				.build();
	}

	@Override
	public List<ObjetoDirectorio> unitatsPerEntitat(String codiEntitat, boolean inclourePare) throws SistemaExternException {

		List<ObjetoDirectorio> unitats = new ArrayList<>();
		try {
            long startTime = System.currentTimeMillis();
			var url = new URL(getServiceUrl() + SERVEI_UNITAT + "arbolUnidades?codigo=" + codiEntitat);
			logger.info("[UNITATS] Consulta les untiats per la entitat " + codiEntitat + " inclourePare " + inclourePare + " url " + url);
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			byte[] response = IOUtils.toByteArray(httpConnection.getInputStream());
			if (response != null && response.length > 0) {
				unitats = mapper.readValue(response, TypeFactory.defaultInstance().constructCollectionType(List.class, ObjetoDirectorio.class));
			}
			if (inclourePare) {
				ObjetoDirectorio pare = new ObjetoDirectorio();
				pare.setCodi(codiEntitat);
				pare.setDenominacio(unitatDenominacio(codiEntitat));
				unitats.add(pare);
			}
			Collections.sort(unitats);
			incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return unitats;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar les unitats organitzatives via REST (codiEntitat=" + codiEntitat + ")", ex);
		}
	}
	
	@Override
	public String unitatDenominacio(String codiDir3) throws SistemaExternException {

		try {
            long startTime = System.currentTimeMillis();
			URL url = new URL(getServiceUrl() + SERVEI_UNITAT + "denominacion?codigo=" + codiDir3);
			logger.info("[UNITATS] Consulta la denominacio per la unitat " + codiDir3 + " url " + url);
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			var result = IOUtils.toString(httpConnection.getInputStream(), StandardCharsets.ISO_8859_1);
			incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return result;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar la denominació de la unitat organitzativ via REST (codiDir3=" + codiDir3 + ")", ex);
		}
	}
	
	@Override
	public List<NodeDir3> cercaUnitats(String codi, String denominacio, Long nivellAdministracio, Long comunitatAutonoma, Boolean ambOficines, Boolean esUnitatArrel,
										Long provincia, String municipi) throws SistemaExternException {

		List<NodeDir3> unitats = new ArrayList<>();
		try {
            long startTime = System.currentTimeMillis();
			var url = new URL(getServiceUrl() + SERVEI_CERCA + "organismos?" + "codigo=" + (codi != null ? codi : "")
					+ "&denominacion=" + (denominacio != null ? denominacio : "")
					+ "&codNivelAdministracion=" + (nivellAdministracio != null ? nivellAdministracio : "-1")
					+ "&codComunidadAutonoma=" + (comunitatAutonoma != null ? comunitatAutonoma : "-1")
					+ "&conOficinas=" + (ambOficines != null && ambOficines ? "true" : "false")
					+ "&unidadRaiz=" + (esUnitatArrel != null && esUnitatArrel ? "true" : "false")
					+ "&provincia="+ (provincia != null ? provincia : "-1")
					+ "&localidad=" + ((municipi != null && !municipi.isEmpty() )  ? municipi+"-01" : "-1")
					+ "&vigentes=true");
			logger.info("[UNITATS] Consulta unitats url " + url);
			var httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			byte[] response = IOUtils.toByteArray(httpConnection.getInputStream());
			if (response != null && response.length > 0) {
				unitats = mapper.readValue(response, TypeFactory.defaultInstance().constructCollectionType(List.class, NodeDir3.class));
				Collections.sort(unitats);
			}
			incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return unitats;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException(
					"No s'han pogut consultar les unitats organitzatives via REST (" +
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
	public List<ObjetoDirectorio> unitatsPerDenominacio(String denominacio) throws SistemaExternException {

		List<ObjetoDirectorio> unitats = new ArrayList<>();
		try {
            long startTime = System.currentTimeMillis();
			var url = new URL(getServiceUrl() + SERVEI_UNITAT + "unidadesDenominacion?denominacion=" + denominacio);
			logger.info("[UNITATS] Consulta unitats amb denominacio " + denominacio + " url " + url);
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			byte[] response = IOUtils.toByteArray(httpConnection.getInputStream());
			if (response != null && response.length > 0) {
				unitats = mapper.readValue(response, TypeFactory.defaultInstance().constructCollectionType(List.class, ObjetoDirectorio.class));
			}
			incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return unitats;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar les unitats organitzatives via REST (denominacio=" + denominacio + ")", ex);
		}
	}
	
	@Override
	public List<NodeDir3> cercaOficines(String codi, String denominacio, Long nivellAdministracio, Long comunitatAutonoma, Long provincia, String municipi) throws SistemaExternException {

		try {
            long startTime = System.currentTimeMillis();
			var url = new URL(getServiceUrl() + SERVEI_CERCA
					+ "oficinas?"
					+ "codigo=" + (codi != null ? codi : "")
					+ "&denominacion=" + (denominacio != null ? denominacio : "")
					+ "&codNivelAdministracion=" + (nivellAdministracio != null ? nivellAdministracio : "-1")
					+ "&codComunidadAutonoma=" + (comunitatAutonoma != null ? comunitatAutonoma : "-1")
					+ "&provincia="+ (provincia != null ? provincia : "-1")
					+ "&localidad=" + (municipi != null ? municipi : "-1")
					+ "&oficinasSir=false"
					+ "&vigentes=true");
			logger.info("[UNITATS] Consulta oficines url " + url);
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			List<NodeDir3> oficines = mapper.readValue(httpConnection.getInputStream(), TypeFactory.defaultInstance().constructCollectionType(List.class, NodeDir3.class));
			Collections.sort(oficines);
			incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return oficines;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar les oficines via REST (codi=" + codi + ", denominacio=" + denominacio + ", " +
					"nivellAdministracio=" + nivellAdministracio + ", comunitatAutonoma=" + comunitatAutonoma + ", provincia=" + provincia + ", " +
					"municipi=" + municipi + ")", ex);
		}
	}
	
	@Override
	public List<CodiValor> nivellsAdministracio() throws SistemaExternException {

		try {
            long startTime = System.currentTimeMillis();
			var url = new URL(getServiceUrl() + SERVEI_CATALEG + "nivelesAdministracion");
			logger.info("[UNITATS] Consulta nivells administracio url " + url);
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			List<CodiValor> nivellsAdministracio = mapper.readValue(httpConnection.getInputStream(), TypeFactory.defaultInstance().constructCollectionType(List.class, CodiValor.class));
			Collections.sort(nivellsAdministracio);
			incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return nivellsAdministracio;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar els nivells d'Administració via REST", ex);
		}
	}

	@Override
	public List<CodiValorPais> paisos() throws SistemaExternException {

		List<CodiValorPais> paisos = new ArrayList<>();
        long startTime = System.currentTimeMillis();
		try {
			URL url = new URL(getServiceUrl() + SERVEI_CATALEG + "paises?estado=V");
			logger.info("[UNITATS] Consulta paisos url " + url);
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			List<CatPais> paisosRest = mapper.readValue(httpConnection.getInputStream(), TypeFactory.defaultInstance().constructCollectionType(List.class, CatPais.class));
			CodiValorPais pais;
			for (var catPais : paisosRest) {
				 pais = new CodiValorPais();
				pais.setAlfa2Pais(catPais.getAlfa2Pais());
				pais.setAlfa3Pais(catPais.getAlfa3Pais());
				pais.setCodiPais(catPais.getCodigoPais());
				pais.setDescripcioPais(catPais.getDescripcionPais());
				paisos.add(pais);
			}
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar els paisos", ex);
		}
		incrementarOperacioOk(System.currentTimeMillis() - startTime);
		return paisos;
	}
	
	@Override
	public List<CodiValor> comunitatsAutonomes() throws SistemaExternException {
		try {
            long startTime = System.currentTimeMillis();
			URL url = new URL(getServiceUrl() + SERVEI_CATALEG + "comunidadesAutonomas");
			logger.info("[UNITATS] Consulta comunitats autonomes url " + url);
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			List<CodiValor> nivellsAdministracio = mapper.readValue(httpConnection.getInputStream(), TypeFactory.defaultInstance().constructCollectionType(List.class, CodiValor.class));
			Collections.sort(nivellsAdministracio);
			incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return nivellsAdministracio;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar les comunitats autònomes via REST", ex);
		}
	}
	
	@Override
	public List<CodiValor> provincies() throws SistemaExternException {

		try {
            long startTime = System.currentTimeMillis();
			var url = new URL(getServiceUrl() + SERVEI_CATALEG + "provincias");
			logger.info("[UNITATS] Consulta provincies url " + url);
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			List<CodiValor> provincies = mapper.readValue(httpConnection.getInputStream(), TypeFactory.defaultInstance().constructCollectionType(List.class, CodiValor.class));
			Collections.sort(provincies);
			var result = afegirZerosProvincies(provincies);
			incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return result;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar les províncies via REST", ex);
		}
	}
	
	@Override
	public List<CodiValor> provincies(String codiCA) throws SistemaExternException {

		try {
            long startTime = System.currentTimeMillis();
			var url = new URL(getServiceUrl() + SERVEI_CATALEG + "provincias/comunidadAutonoma?id=" + codiCA);
			logger.info("[UNITATS] Consulta provincies de la comunitat autonoma " + codiCA + " url " + url);
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			List<CodiValor> provincies = mapper.readValue(httpConnection.getInputStream(), TypeFactory.defaultInstance().constructCollectionType(List.class, CodiValor.class));Collections.sort(provincies);
			var result = afegirZerosProvincies(provincies);
			incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return result;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar les comunitats autònomes via REST", ex);
		}
	}

	private List<CodiValor> afegirZerosProvincies(List<CodiValor> provincies) {

		String id;
		for (CodiValor provincia: provincies) {
			id = provincia.getId();
			if (id.length() < 2) {
				id = StringUtils.leftPad(id, 2, "0");
			}
			provincia.setId(id);
		}
		return provincies;
	}

	@Override
	public List<CodiValor> localitats(String codiProvincia) throws SistemaExternException {

		try {
            long startTime = System.currentTimeMillis();
			var url = new URL(getServiceUrl() + SERVEI_CATALEG
					+ "localidades/provincia/entidadGeografica?"
					+ "codigoProvincia=" + codiProvincia
					+ "&codigoEntidadGeografica=01");
			logger.info("[UNITATS] Consulta localistats de la provincia " + codiProvincia + " url " + url);
			var httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			var mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			CollectionType collection = TypeFactory.defaultInstance().constructCollectionType(List.class, CodiValor.class);
			List<CodiValor> localitats = mapper.readValue(httpConnection.getInputStream(), collection);
			Collections.sort(localitats);
			String cp = codiProvincia.length() < 2 ? 0 + codiProvincia : codiProvincia;
			String id;
			for (var localitat: localitats) {
				id = localitat.getId();
				if (id.length() < 4) {
					id = StringUtils.leftPad(id, 4, "0");
				}
				localitat.setId(cp + id);
			}
			incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return localitats;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar les localitats via REST", ex);
		}
	}
	
	@Override
	public List<OficinaSir> oficinesSIRUnitat(String unitat, Map<String, OrganismeDto> arbreUnitats) throws SistemaExternException {

		List<OficinaSir> oficinesSIR = new ArrayList<>();
		List<OficinaTF> oficinesWS = new ArrayList<>();
		try {
            long startTime = System.currentTimeMillis();
			getOficinesUnitatSuperior(unitat, oficinesWS, arbreUnitats);
			for (OficinaTF oficinaTF : oficinesWS) {
				OficinaSir oficinaSIR = new OficinaSir();
				oficinaSIR.setCodi(oficinaTF.getCodigo());
				oficinaSIR.setNom(oficinaTF.getDenominacion());
				oficinesSIR.add(oficinaSIR);
			}
			incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return oficinesSIR;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar les oficines SIR via REST (unitat=" + unitat + ")", ex);
		}
	}
	
	@Override
	public List<OficinaSir> getOficinesEntitat(String entitat) throws SistemaExternException {

		List<OficinaSir> oficines = new ArrayList<>();
		List<OficinaTF> oficinesWS = new ArrayList<>();
		try {
            long startTime = System.currentTimeMillis();
			var url = new URL(getServiceUrl() + SERVEI_OFICINES + "obtenerArbolOficinas?codigo=" + entitat);
			logger.info("[UNITATS] Consulta oficines de la entitat " + entitat + " url " + url);
			byte[] response = getResponse(url);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			if (response != null && response.length > 0) {
				oficinesWS = mapper.readValue(response, new TypeReference<List<OficinaTF>>() {});
			}

			for (OficinaTF oficinaTF : oficinesWS) {
				oficines.add(OficinaSir.builder()
						.codi(oficinaTF.getCodigo())
						.nom(oficinaTF.getDenominacion())
						.organCodi(oficinaTF.getCodUoResponsable())
						.sir(oficinaTF.getSirOfi() != null && !oficinaTF.getSirOfi().isEmpty())
						.build());
			}
			incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return oficines;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar les oficines SIR via REST (entitat=" + entitat + ")", ex);
		}
	}

	private void getOficinesUnitatSuperior(String unitat, List<OficinaTF> oficinesWS, Map<String, OrganismeDto> arbreUnitats) throws SistemaExternException {

		OrganismeDto arbre = arbreUnitats.get(unitat);
		List<OficinaTF> oficinesUnitatActual = getObtenerOficinasSIRUnidad(unitat);

		if (arbre != null) {
			String unitatSuperiorCurrentUnitat = arbre.getPare();
			// Cerca de forma recursiva a l'unitat superior si l'unitat actual no disposa d'una oficina
			if (oficinesUnitatActual.isEmpty() && unitatSuperiorCurrentUnitat != null) {
				getOficinesUnitatSuperior(unitatSuperiorCurrentUnitat, oficinesUnitatActual, arbreUnitats);
			// No cercar més si l'oficina actual és l'oficina arrel
			} else if (oficinesUnitatActual.isEmpty()) {
				oficinesWS.addAll(getObtenerOficinasSIRUnidad(arbre.getCodi()));
			}
		}
		oficinesWS.addAll(oficinesUnitatActual);
	}

	private List<OficinaTF> getObtenerOficinasSIRUnidad(String unitat) throws SistemaExternException {

		List<OficinaTF> oficines = new ArrayList<>();
		try {
			URL url = new URL(getServiceUrl() + SERVEI_OFICINES + "obtenerOficinasSIRUnidad?codigo=" + unitat);
			logger.info("[UNITATS] Consulta oficines de la unitat " + unitat + " url " + url);
			byte[] response = getResponse(url);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			if (response != null && response.length > 0) {
				oficines = mapper.readValue(response, new TypeReference<List<OficinaTF>>() {});
			}
			return oficines;
		} catch (Exception ex) {
			throw new SistemaExternException("No s'han pogut consultar les oficines SIR per organ (organ=" + unitat + ")", ex);
		}
	}

	private byte[] getResponse(URL url) throws IOException {

		var httpConnection = (HttpURLConnection) url.openConnection();
		httpConnection.setRequestMethod("GET");
		httpConnection.setRequestProperty("Authorization", createBasicAuthHeaderValue());
		httpConnection.setDoInput(true);
		httpConnection.setDoOutput(true);
		return IOUtils.toByteArray(httpConnection.getInputStream());
	}

	private String createBasicAuthHeaderValue() {

		var auth = getUsernameServiceUrl() + ":" + getPasswordServiceUrl();
		var encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
		return "Basic " + new String(encodedAuth);
	}
	
	private String getServiceUrl() {

		var dir3Url = properties.getProperty("es.caib.notib.plugin.unitats.dir3.url");
		return !dir3Url.endsWith("/") ? dir3Url + "/" : dir3Url;
	}
	
	private String getUsernameServiceUrl() {
		return properties.getProperty("es.caib.notib.plugin.unitats.dir3.username");
	}
	
	private String getPasswordServiceUrl() {
		return properties.getProperty("es.caib.notib.plugin.unitats.dir3.password");
	}
	
	public static void configAddressUserPassword(String usr, String pwd, String endpoint, Object api) {

		Map<String, Object> reqContext = ((BindingProvider) api).getRequestContext();
		reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
		reqContext.put(BindingProvider.USERNAME_PROPERTY, usr);
		reqContext.put(BindingProvider.PASSWORD_PROPERTY, pwd);
	}

}
