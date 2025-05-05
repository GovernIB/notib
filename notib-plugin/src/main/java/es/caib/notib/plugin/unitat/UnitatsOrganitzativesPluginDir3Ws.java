/**
 *
 */
package es.caib.notib.plugin.unitat;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.EstatSalutEnum;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.dir3caib.ws.api.catalogo.Dir3CaibObtenerCatalogosWs;
import es.caib.dir3caib.ws.api.catalogo.Dir3CaibObtenerCatalogosWsService;
import es.caib.dir3caib.ws.api.oficina.Dir3CaibObtenerOficinasWs;
import es.caib.dir3caib.ws.api.oficina.Dir3CaibObtenerOficinasWsService;
import es.caib.dir3caib.ws.api.oficina.OficinaTF;
import es.caib.dir3caib.ws.api.unidad.Dir3CaibObtenerUnidadesWs;
import es.caib.dir3caib.ws.api.unidad.Dir3CaibObtenerUnidadesWsService;
import es.caib.dir3caib.ws.api.unidad.UnidadTF;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.plugin.SistemaExternException;
import es.caib.notib.plugin.utils.NotibLoggerPlugin;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.xml.ws.BindingProvider;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
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
public class UnitatsOrganitzativesPluginDir3Ws implements UnitatsOrganitzativesPlugin {

	private static final String SERVEI_CERCA = "/rest/busqueda/";
	private static final String SERVEI_CATALEG = "/rest/catalogo/";
	private static final String SERVEI_UNITAT = "/rest/unidad/";
	private static final String SERVEI_ORGANIGRAMA = "/rest/organigrama/";
	private static final String WS_CATALEG = "ws/Dir3CaibObtenerCatalogos";
	private static final String WS_UNITATS = "ws/Dir3CaibObtenerUnidades";
	private static final String WS_OFICINA = "ws/Dir3CaibObtenerOficinas";
	private static final String WSDL = "?wsdl";
	private static final String URL = "URL: ";

	private final Properties properties;

	private NotibLoggerPlugin logger = new NotibLoggerPlugin(log);

//	public UnitatsOrganitzativesPluginDir3Ws(Properties properties) {
//
//		this.properties = properties;
//		logger.setMostrarLogs(Boolean.parseBoolean(properties.getProperty("es.caib.notib.log.tipus.plugin.UNITATS")));
//	}

	public UnitatsOrganitzativesPluginDir3Ws(Properties properties, boolean configuracioEspecifica) {
		this.properties = properties;
		this.configuracioEspecifica = configuracioEspecifica;
		logger.setMostrarLogs(Boolean.parseBoolean(properties.getProperty("es.caib.notib.log.tipus.plugin.UNITATS")));
	}

	@Override
	public Map<String, NodeDir3> organigramaPerEntitat(String codiEntitat) throws SistemaExternException {

		Map<String, NodeDir3> organigrama = new HashMap<>();
		try {
			var url = new URL(getServiceUrl() + SERVEI_ORGANIGRAMA + "?codigo=" + codiEntitat);
			logger.info("[UNITATS] Consulta organigrama per entitat " + codiEntitat + " url" + url);
			var httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			var mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			var response = IOUtils.toByteArray(httpConnection.getInputStream());
			if (response != null && response.length > 0) {
				var arrel = mapper.readValue(response, NodeDir3.class);
				nodeToOrganigrama(arrel, organigrama);
			}
			incrementarOperacioOk();
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
		if (unitat.getFills() == null) {
			return;
		}
		for (NodeDir3 fill: unitat.getFills()) {
			nodeToOrganigrama(fill, organigrama);
		}
	}

	@Override
	public Map<String, NodeDir3> organigramaPerEntitat(String pareCodi, Date fechaActualizacion, Date fechaSincronizacion) throws SistemaExternException {

		Map<String, NodeDir3> organigrama = new HashMap<>();
		try {
			Timestamp tpActualizacion = null;
			Timestamp tpSincronizacion = null;
			if (fechaActualizacion != null) {
				tpActualizacion = new Timestamp(fechaActualizacion.getTime());
			}
			if (fechaSincronizacion != null) {
				tpSincronizacion = new Timestamp(fechaSincronizacion.getTime());
			}
			logger.info("[UNITATS] Consulta organigrama per entitat. Codi pare " + pareCodi + " `data actualitzacio" + fechaActualizacion + " data sincronitzacio " + fechaSincronizacion);
			var arbol = getObtenerUnidadesService().obtenerArbolUnidades(pareCodi, tpActualizacion, tpSincronizacion);
			NodeDir3 node;
			NodeDir3 pare;
			List<NodeDir3> fills;
			for(var unidadTF: arbol){
				if (!"V".equals(unidadTF.getCodigoEstadoEntidad()) && !"T".equals(unidadTF.getCodigoEstadoEntidad())) {
					continue;
				}
				// Unitats Vigents o Transitòries
				node = toNodeDir3(unidadTF);
				pare = organigrama.get(node.getSuperior());
				if (!node.getCodi().equalsIgnoreCase(pareCodi) && pare == null) {
					continue;
				}
				organigrama.put(node.getCodi(), node);
				if (pare == null) {
					continue;
				}
				fills = pare.getFills();
				if (fills == null) {
					fills = new ArrayList<>();
					pare.setFills(fills);
				}
				fills.add(node);
			}
			incrementarOperacioOk();
			return organigrama;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar les unitats organitzatives via WS (pareCodi=" + pareCodi + ")", ex);
		}
	}

	@Override
	public byte[] findAmbPareJson(String pareCodi, Date dataActualitzacio, Date dataSincronitzacio) throws SistemaExternException {
		incrementarOperacioOk();
		return new byte[0];
	}

	@Override
	public List<NodeDir3> findAmbPare(String pareCodi, Date dataActualitzacio, Date dataSincronitzacio) throws SistemaExternException {

		try {
			var dataAct = dataActualitzacio != null ? new Timestamp(dataActualitzacio.getTime()) : null;
			var dataSinc = dataSincronitzacio != null ? new Timestamp(dataSincronitzacio.getTime()) : null;
			logger.info("[UNITATS] Consulta organigrama per entitat. Codi pare " + pareCodi);
			List<UnidadTF> unidades = getObtenerUnidadesService().obtenerArbolUnidades(pareCodi, dataAct, dataSinc);
			if (unidades == null) {
				return new ArrayList<>();
			}
			List<NodeDir3> unitats = new ArrayList<>();
			for (UnidadTF unidad : unidades) {
				unitats.add(toNodeDir3(unidad));
			}
			incrementarOperacioOk();
			return unitats;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar les unitats organitzatives via WS (pareCodi=" + pareCodi + ")", ex);
		}
	}

	@Override
	public NodeDir3 findAmbCodi(String pareCodi, Date dataActualitzacio, Date dataSincronitzacio) throws SistemaExternException {

		try {
			var dataAct = dataActualitzacio != null ? new Timestamp(dataActualitzacio.getTime()) : null;
			var dataSinc = dataSincronitzacio != null ? new Timestamp(dataSincronitzacio.getTime()) : null;
			logger.info("[UNITATS] Consulta organigrama per entitat. Codi pare " + pareCodi + " `data actualitzacio" + dataActualitzacio + " data sincronitzacio " + dataSincronitzacio);
			var unidad = getObtenerUnidadesService().obtenerUnidad(pareCodi, dataAct, dataSinc);
			var result = unidad != null ? toNodeDir3(unidad) : null;
			incrementarOperacioOk();
			return result;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'ha pogut consultar la unitat organitzativa amb codi (pareCodi=" + pareCodi + ")", ex);
		}
	}

	private NodeDir3 toNodeDir3(UnidadTF unidadTF) {

		return NodeDir3.builder().codi(unidadTF.getCodigo()).denominacio(unidadTF.getDenominacion()).estat(unidadTF.getCodigoEstadoEntidad())
				.arrel(unidadTF.getCodUnidadRaiz()).superior(unidadTF.getCodUnidadSuperior()).localitat(unidadTF.getDescripcionLocalidad())
				.idPare(unidadTF.getCodUnidadSuperior()).historicosUO(unidadTF.getHistoricosUO()).build();
	}

	@Override
	public List<ObjetoDirectorio> unitatsPerEntitat(String codiEntitat, boolean inclourePare) throws SistemaExternException {

		try {
			var url = new URL(getServiceUrl() + SERVEI_UNITAT + "arbolUnidades?codigo=" + codiEntitat);
			logger.info("[UNITATS] Consulta les untiats per la entitat " + codiEntitat + " inclourePare " + inclourePare + " url " + url);
			var httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			var mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			var response = IOUtils.toByteArray(httpConnection.getInputStream());
			List<ObjetoDirectorio> unitats = new ArrayList<>();
			if (response != null && response.length > 0) {
				unitats = mapper.readValue(response, TypeFactory.defaultInstance().constructCollectionType(List.class, ObjetoDirectorio.class));
			}
			if (inclourePare) {
				var pare = new ObjetoDirectorio();
				pare.setCodi(codiEntitat);
				pare.setDenominacio(unitatDenominacio(codiEntitat));
				unitats.add(pare);
			}
			Collections.sort(unitats);
			incrementarOperacioOk();
			return unitats;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar les unitats organitzatives via REST (codiEntitat=" + codiEntitat + ")", ex);
		}
	}

	@Override
	public String unitatDenominacio(String codiDir3) throws SistemaExternException {

		try {
			var url = new URL(getServiceUrl() + SERVEI_UNITAT + "denominacion?codigo=" + codiDir3);
			logger.info("[UNITATS] Consulta la denominacio per la unitat " + codiDir3 + " url " + url);
			var httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			var mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			var result = IOUtils.toString(httpConnection.getInputStream(), StandardCharsets.ISO_8859_1);
			incrementarOperacioOk();
			return result;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar la denominació de la unitat organitzativ via REST (codiDir3=" + codiDir3 + ")", ex);
		}
	}

	@Override
	public List<NodeDir3> cercaUnitats(String codi, String denominacio, Long nivellAdministracio, Long comunitatAutonoma, Boolean ambOficines, Boolean esUnitatArrel,
									   Long provincia, String municipi) throws SistemaExternException {

		try {
			var url = new URL(getServiceUrl() + SERVEI_CERCA + "organismos?" + "codigo=" + (codi != null ? codi : "")
					+ "&denominacion=" + (denominacio != null ? denominacio : "")
					+ "&codNivelAdministracion=" + (nivellAdministracio != null ? nivellAdministracio : "-1")
					+ "&codComunidadAutonoma=" + (comunitatAutonoma != null ? comunitatAutonoma : "-1")
					+ "&conOficinas=" + (ambOficines != null && ambOficines ? "true" : "false")
					+ "&unidadRaiz=" + (esUnitatArrel != null && esUnitatArrel ? "true" : "false")
					+ "&provincia="+ (provincia != null ? provincia : "-1")
					+ "&localidad=" + ((municipi != null && !municipi.isEmpty() )  ? municipi+"-01" : "-1")
					+ "&vigentes=true");
			logger.info("[UNITATS] Consulta unitats " + url);
			var httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			var mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			var response = IOUtils.toByteArray(httpConnection.getInputStream());
			List<NodeDir3> unitats = new ArrayList<>();
			if (response != null && response.length > 0) {
				unitats = mapper.readValue(response, TypeFactory.defaultInstance().constructCollectionType(List.class, NodeDir3.class));
				Collections.sort(unitats);
			}
			incrementarOperacioOk();
			return unitats;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar les unitats organitzatives via REST (denominacio=" + denominacio + ", " +
							"nivellAdministracio=" + nivellAdministracio + ", comunitatAutonoma=" + comunitatAutonoma + ", ambOficines=" + ambOficines + ", " +
							"esUnitatArrel=" + esUnitatArrel + ", provincia=" + provincia + ", municipi=" + municipi + ")", ex);}
	}

	@Override
	public List<ObjetoDirectorio> unitatsPerDenominacio(String denominacio) throws SistemaExternException {

		List<ObjetoDirectorio> unitats = new ArrayList<>();
		try {
			var url = new URL(getServiceUrl() + SERVEI_UNITAT + "unidadesDenominacion?denominacion=" + denominacio);
			logger.info("[UNITATS] Consulta unitats amb denominacio " + denominacio + " url " + url);
			var httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			var mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			var response = IOUtils.toByteArray(httpConnection.getInputStream());
			if (response != null && response.length > 0) {
				unitats = mapper.readValue(response, TypeFactory.defaultInstance().constructCollectionType(List.class, ObjetoDirectorio.class));
			}
			incrementarOperacioOk();
			return unitats;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar les unitats organitzatives via REST (denominacio=" + denominacio + ")", ex);
		}
	}

	@Override
	public List<NodeDir3> cercaOficines(String codi, String denominacio, Long nivellAdministracio, Long comunitatAutonoma, Long provincia, String municipi) throws SistemaExternException {

		try {
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
			var mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			List<NodeDir3> oficines = mapper.readValue(httpConnection.getInputStream(), TypeFactory.defaultInstance().constructCollectionType(List.class, NodeDir3.class));
			Collections.sort(oficines);
			incrementarOperacioOk();
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
			var url = new URL(getServiceUrl() + SERVEI_CATALEG + "nivelesAdministracion");
			logger.info("[UNITATS] Consulta nivells administracio url " + url);
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			var mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			List<CodiValor> nivellsAdministracio = mapper.readValue(httpConnection.getInputStream(), TypeFactory.defaultInstance().constructCollectionType(List.class, CodiValor.class));
			Collections.sort(nivellsAdministracio);
			incrementarOperacioOk();
			return nivellsAdministracio;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar els nivells d'Administració via REST", ex);
		}
	}

	public List<CodiValorPais> paisos() throws SistemaExternException {

		try {
			logger.info("[UNITATS] Consulta paisos");
			var paisosWs = getCatalogosWsWithSecurityApi().obtenerCatPais();
			List<CodiValorPais> paisos = new ArrayList<>();
			CodiValorPais pais;
			for (var catPaisWs : paisosWs) {
				pais = new CodiValorPais();
				pais.setAlfa2Pais(catPaisWs.getAlfa2Pais());
				pais.setAlfa3Pais(catPaisWs.getAlfa3Pais());
				pais.setCodiPais(catPaisWs.getCodigoPais());
				pais.setDescripcioPais(catPaisWs.getDescripcionPais());
				paisos.add(pais);
			}
			incrementarOperacioOk();
			return paisos;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar els paisos via WS", ex);
		}
	}

	@Override
	public List<CodiValor> comunitatsAutonomes() throws SistemaExternException {

		try {
			var url = new URL(getServiceUrl() + SERVEI_CATALEG + "comunidadesAutonomas");
			logger.info("[UNITATS] Consulta comunitats autonomes url " + url);
			var httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			var mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			List<CodiValor> nivellsAdministracio = mapper.readValue(httpConnection.getInputStream(), TypeFactory.defaultInstance().constructCollectionType(List.class, CodiValor.class));
			Collections.sort(nivellsAdministracio);
			incrementarOperacioOk();
			return nivellsAdministracio;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar les comunitats autònomes via REST", ex);
		}
	}

	@Override
	public List<CodiValor> provincies() throws SistemaExternException {

		try {
			var url = new URL(getServiceUrl() + SERVEI_CATALEG + "provincias");
			logger.info("[UNITATS] Consulta provincies url " + url);
			var httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			var mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			List<CodiValor> provincies = mapper.readValue(httpConnection.getInputStream(), TypeFactory.defaultInstance().constructCollectionType(List.class, CodiValor.class));
			Collections.sort(provincies);
			var result = afegirZerosProvincies(provincies);
			incrementarOperacioOk();
			return result;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar les províncies via REST", ex);
		}
	}

	@Override
	public List<CodiValor> provincies(String codiCA) throws SistemaExternException {

		try {
			var url = new URL(getServiceUrl() + SERVEI_CATALEG + "provincias/comunidadAutonoma?id=" + codiCA);
			logger.info("[UNITATS] Consulta provincies de la comunitat autonoma " + codiCA + " url " + url);
			var httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			var mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			List<CodiValor> provincies = mapper.readValue(httpConnection.getInputStream(), TypeFactory.defaultInstance().constructCollectionType(List.class, CodiValor.class));
			Collections.sort(provincies);
			var result = afegirZerosProvincies(provincies);
			incrementarOperacioOk();
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
			URL url = new URL(getServiceUrl() + SERVEI_CATALEG
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
			var collection = TypeFactory.defaultInstance().constructCollectionType(List.class, CodiValor.class);
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
			incrementarOperacioOk();
			return localitats;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar les localitats via REST", ex);
		}
	}

	@Override
	public List<OficinaSir> oficinesSIRUnitat(String unitat, Map<String, OrganismeDto> arbreUnitats) throws SistemaExternException {

		try {
			List<OficinaSir> oficinesSIR = new ArrayList<>();
			List<OficinaTF> oficinesWS = new ArrayList<>();
			getOficinesUnitatSuperior(unitat, oficinesWS, arbreUnitats);
			OficinaSir oficinaSIR;
			for (OficinaTF oficinaTF : oficinesWS) {
				oficinaSIR = new OficinaSir();
				oficinaSIR.setCodi(oficinaTF.getCodigo());
				oficinaSIR.setNom(oficinaTF.getDenominacion());
				oficinesSIR.add(oficinaSIR);
			}
			incrementarOperacioOk();
			return oficinesSIR;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar les oficines SIR via REST (unitat=" + unitat + ")", ex);
		}
	}

	@Override
	public List<OficinaSir> getOficinesEntitat(String entitat) throws SistemaExternException {

		try {
			List<OficinaSir> oficinesSIR = new ArrayList<>();
			logger.info("[UNITATS] Consulta oficines de la entitat " + entitat);
			var oficinesWS = getObtenerOficinasSIRUnidad().obtenerArbolOficinas(entitat, null, null);
			for (OficinaTF oficinaTF : oficinesWS) {
				OficinaSir oficinaSIR = new OficinaSir();
				oficinaSIR.setCodi(oficinaTF.getCodigo());
				oficinaSIR.setNom(oficinaTF.getDenominacion());
				oficinaSIR.setOrganCodi(oficinaTF.getCodUoResponsable());
				oficinaSIR.setSir(oficinaTF.getSirOfi() != null && !oficinaTF.getSirOfi().isEmpty());
				oficinesSIR.add(oficinaSIR);
			}
			incrementarOperacioOk();
			return oficinesSIR;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException("No s'han pogut consultar les oficines SIR via REST (entitat=" + entitat + ")", ex);
		}
	}

	private void getOficinesUnitatSuperior(String unitat, List<OficinaTF> oficinesWS, Map<String, OrganismeDto> arbreUnitats) throws MalformedURLException {

		var arbre = arbreUnitats.get(unitat);
		logger.info("[UNITATS] Consulta oficines de la unitat " + unitat);
		var oficinesUnitatActual = getObtenerOficinasSIRUnidad().obtenerOficinasSIRUnidad(unitat);
		if (arbre != null) {
			var unitatSuperiorCurrentUnitat = arbre.getPare();
			// Cerca de forma recursiva a l'unitat superior si l'unitat actual no disposa d'una oficina
			if (oficinesUnitatActual.isEmpty() && unitatSuperiorCurrentUnitat != null) {
				getOficinesUnitatSuperior(unitatSuperiorCurrentUnitat, oficinesUnitatActual, arbreUnitats);
				// No cercar més si l'oficina actual és l'oficina arrel
			} else if (oficinesUnitatActual.isEmpty()) {
				oficinesWS.addAll(getObtenerOficinasSIRUnidad().obtenerOficinasSIRUnidad(arbre.getCodi()));
			}
		}
		oficinesWS.addAll(oficinesUnitatActual);
	}

	private Dir3CaibObtenerUnidadesWs getObtenerUnidadesService() throws MalformedURLException {

		final var endpoint = getServiceUrl() + WS_UNITATS;
		final var wsdl = new URL(endpoint + WSDL);
		var service = new Dir3CaibObtenerUnidadesWsService(wsdl);
		var api = service.getDir3CaibObtenerUnidadesWs();
		configAddressUserPassword(getUsernameServiceUrl(), getPasswordServiceUrl(), endpoint, api);
		return api;
	}

	public Dir3CaibObtenerCatalogosWs getCatalogosWsWithSecurityApi() throws Exception {

		final var endpoint = getServiceUrl() + WS_CATALEG;
		final var wsdl = new URL(endpoint + WSDL);
		var service = new Dir3CaibObtenerCatalogosWsService(wsdl);
		var api = service.getDir3CaibObtenerCatalogosWs();
		configAddressUserPassword(getUsernameServiceUrl(), getPasswordServiceUrl(), endpoint, api);
		return api;
	}

	private Dir3CaibObtenerOficinasWs getObtenerOficinasSIRUnidad() throws MalformedURLException {

		final String endpoint = getServiceUrl() + WS_OFICINA;
		final URL wsdl = new URL(endpoint + WSDL);
		var service = new Dir3CaibObtenerOficinasWsService(wsdl);
		var api = service.getDir3CaibObtenerOficinasWs();
		configAddressUserPassword(getUsernameServiceUrl(), getPasswordServiceUrl(), endpoint, api);
		return api;
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

		var reqContext = ((BindingProvider) api).getRequestContext();
		reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
		reqContext.put(BindingProvider.USERNAME_PROPERTY, usr);
		reqContext.put(BindingProvider.PASSWORD_PROPERTY, pwd);
	}


	// Mètodes de SALUT
	// /////////////////////////////////////////////////////////////////////////////////////////////

	private boolean configuracioEspecifica = false;
	private int operacionsOk = 0;
	private int operacionsError = 0;

	@Synchronized
	private void incrementarOperacioOk() {
		operacionsOk++;
	}

	@Synchronized
	private void incrementarOperacioError() {
		operacionsError++;
	}

	@Synchronized
	private void resetComptadors() {
		operacionsOk = 0;
		operacionsError = 0;
	}

	@Override
	public boolean teConfiguracioEspecifica() {
		return this.configuracioEspecifica;
	}

	@Override
	public EstatSalut getEstatPlugin() {
		try {
			Instant start = Instant.now();
			String url = getServiceUrl() + SERVEI_CERCA + "organismos?codigo=fakeUnitat&denominacion=&codNivelAdministracion=-1&codComunidadAutonoma=-1&conOficinas=false&unidadRaiz=false&provincia=-1&localidad=-1&vigentes=true";
			WebResource webResource = Client.create().resource(url);
			ClientResponse response = webResource.get(ClientResponse.class);
//			cercaUnitats("fakeUnitat", null, null, null, null, null, null, null);
			if (response.getStatus() == 204 || response.getStatus() == 200) {
				return EstatSalut.builder()
						.latencia((int) Duration.between(start, Instant.now()).toMillis())
						.estat(EstatSalutEnum.UP)
						.build();
			}
		} catch (Exception ex) {}
		return EstatSalut.builder().estat(EstatSalutEnum.DOWN).build();
	}

	@Override
	public IntegracioPeticions getPeticionsPlugin() {
		IntegracioPeticions integracioPeticions = IntegracioPeticions.builder()
				.totalOk(operacionsOk)
				.totalError(operacionsError)
				.build();
		resetComptadors();
		return integracioPeticions;
	}

}
