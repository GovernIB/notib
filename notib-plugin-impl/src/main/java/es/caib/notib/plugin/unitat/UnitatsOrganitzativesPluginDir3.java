/**
 * 
 */
package es.caib.notib.plugin.unitat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import es.caib.dir3caib.ws.api.catalogo.CatPais;
import es.caib.dir3caib.ws.api.oficina.OficinaTF;
import es.caib.dir3caib.ws.api.unidad.UnidadTF;
import es.caib.notib.core.api.dto.organisme.OrganismeDto;
import es.caib.notib.plugin.SistemaExternException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class UnitatsOrganitzativesPluginDir3 implements UnitatsOrganitzativesPlugin {
	
	private static final String SERVEI_CERCA = "/rest/busqueda/";
	private static final String SERVEI_CATALEG = "/rest/catalogo/";
	private static final String SERVEI_UNITAT = "/rest/unidad/";
	private static final String SERVEI_UNITATS = "/rest/unidades/";
	private static final String SERVEI_ORGANIGRAMA = "/rest/organigrama/";
	private static final String SERVEI_OFICINES = "/rest/oficinas/";

	private final Properties properties;

	public UnitatsOrganitzativesPluginDir3(Properties properties) {
		this.properties = properties;
	}

	@Override
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
				NodeDir3 arrel = mapper.readValue(response, NodeDir3.class);
				nodeToOrganigrama(arrel, organigrama);
			}
			return organigrama;
		} catch (Exception ex) {
			throw new SistemaExternException("No s'ha pogut consultar l'organigrama de unitats organitzatives via REST (codiEntitat=" + codiEntitat + ")", ex);
		}
	}
	
	private void nodeToOrganigrama(NodeDir3 unitat, Map<String, NodeDir3> organigrama) {
		if (unitat.getEstat().startsWith("V") || unitat.getEstat().startsWith("T")) {	// Unitats Vigents o Transitòries 
			organigrama.put(unitat.getCodi(), unitat);
			if (unitat.getFills() != null)
				for (NodeDir3 fill: unitat.getFills())
					nodeToOrganigrama(fill, organigrama);
		}
	}
	
	@Override
	public Map<String, NodeDir3> organigramaPerEntitat(
			String pareCodi,
			Date fechaActualizacion,
			Date fechaSincronizacion) throws SistemaExternException {
		Map<String, NodeDir3> organigrama = new HashMap<>();
		try {
			List<UnitatOrganitzativa> arbol = new ArrayList<>();

			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			URL url = new URL(getServiceUrl() + SERVEI_UNITATS + "obtenerArbolUnidades?codigo=" + pareCodi +
					(fechaActualizacion != null ? "&fechaActualizacion=" + fechaActualizacion : "") +
					(fechaSincronizacion != null ? "&fechaSincronizacion=" + fechaSincronizacion : ""));
			logger.debug("URL: " + url);
			byte[] response = getResponse(url);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			if (response != null && response.length > 0) {
				arbol = mapper.readValue(response, new TypeReference<List<UnidadTF>>() {});
			}

			for(UnitatOrganitzativa unidadTF: arbol){
				if ("V".equals(unidadTF.getCodigoEstadoEntidad()) || "T".equals(unidadTF.getCodigoEstadoEntidad())) {	// Unitats Vigents o Transitòries 
					NodeDir3 node = toNodeDir3(unidadTF);
					NodeDir3 pare = organigrama.get(node.getSuperior());
					if (node.getCodi().equalsIgnoreCase(pareCodi) || pare != null) {
						organigrama.put(node.getCodi(), node);
						if (pare != null) {
							List<NodeDir3> fills = pare.getFills();
							if (fills == null) {
								fills = new ArrayList<NodeDir3>();
								pare.setFills(fills);
							}
							fills.add(node);
						}
					}
				}
			}
			return organigrama;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar les unitats organitzatives via WS (" +
					"pareCodi=" + pareCodi + ")",
					ex);
		}
	}

	@Override
	public List<NodeDir3> findAmbPare(String pareCodi, Date dataActualitzacio, Date dataSincronitzacio) throws SistemaExternException {

		try {
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			List<NodeDir3> unitats = new ArrayList<>();
			List<UnitatOrganitzativa> unidades = new ArrayList<>();
			URL url = new URL(getServiceUrl() + SERVEI_UNITATS + "obtenerArbolUnidades?codigo=" + pareCodi +
					"&denominacionCooficial=false" +
					(dataActualitzacio != null ? "&fechaActualizacion=" + sdf.format(dataActualitzacio) : "") +
					(dataSincronitzacio != null ? "&fechaSincronizacion=" + sdf.format(dataSincronitzacio) : ""));
			byte[] response = getResponse(url);
//			response = getJson().getBytes();
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			if (response != null && response.length > 0) {
				unidades = mapper.readValue(response, new TypeReference<List<UnitatOrganitzativa>>() {});
			}

			if (unidades != null) {
				for (UnitatOrganitzativa unidad : unidades) {
					unitats.add(toNodeDir3(unidad));
				}
			}
			return unitats;
		} catch (Exception ex) {
			throw new SistemaExternException("No s'han pogut consultar les unitats organitzatives via WS ("
					+ "pareCodi=" + pareCodi + ")", ex);
		}
	}

//	private String getJson() throws IOException {
//        byte[] encoded = Files.readAllBytes(Paths.get(""));
//        return new String(encoded, StandardCharsets.UTF_8);
//    }

	@Override
	public NodeDir3 findAmbCodi(String pareCodi, Date dataActualitzacio, Date dataSincronitzacio) throws SistemaExternException {
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			UnitatOrganitzativa unidad = null;
			URL url = new URL(getServiceUrl() + SERVEI_UNITATS + "obtenerUnidad?codigo=" + pareCodi +
					"&denominacionCooficial=false" +
					(dataActualitzacio != null ? "&fechaActualizacion=" + dataActualitzacio : "") +
					(dataSincronitzacio != null ? "&fechaSincronizacion=" + dataSincronitzacio : ""));
			byte[] response = getResponse(url);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			if (response != null && response.length > 0) {
				unidad = mapper.readValue(response, UnitatOrganitzativa.class);
			}
			return unidad != null ? toNodeDir3(unidad) : null;
		} catch (Exception ex) {
			throw new SistemaExternException("No s'ha pogut consultar la unitat organitzativa amb codi ("
					+ "pareCodi=" + pareCodi + ")", ex);
		}
	}

	private NodeDir3 toNodeDir3(UnitatOrganitzativa unidadTF) {
		NodeDir3 node = NodeDir3.builder()
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
		return node;
	}

	@Override
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
	
	@Override
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
			return IOUtils.toString(httpConnection.getInputStream(), StandardCharsets.ISO_8859_1.name());
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar la denominació de la unitat organitzativ via REST (" +
					"codiDir3=" + codiDir3 + ")",
					ex);
		}
	}
	
	@Override
	public List<NodeDir3> cercaUnitats(String codi, String denominacio, Long nivellAdministracio, Long comunitatAutonoma, Boolean ambOficines, Boolean esUnitatArrel,
										Long provincia, String municipi) throws SistemaExternException {

		List<NodeDir3> unitats = new ArrayList<NodeDir3>();
		try {
			URL url = new URL(getServiceUrl() + SERVEI_CERCA + "organismos?" + "codigo=" + (codi != null ? codi : "")
					+ "&denominacion=" + (denominacio != null ? denominacio : "")
					+ "&codNivelAdministracion=" + (nivellAdministracio != null ? nivellAdministracio : "-1")
					+ "&codComunidadAutonoma=" + (comunitatAutonoma != null ? comunitatAutonoma : "-1")
					+ "&conOficinas=" + (ambOficines != null && ambOficines ? "true" : "false")
					+ "&unidadRaiz=" + (esUnitatArrel != null && esUnitatArrel ? "true" : "false")
					+ "&provincia="+ (provincia != null ? provincia : "-1")
					+ "&localidad=" + ((municipi != null && !municipi.isEmpty() )  ? municipi+"-01" : "-1")
					+ "&vigentes=true");
			logger.debug("URL: " + url);
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
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
			return unitats;
		} catch (Exception ex) {
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
		List<ObjetoDirectorio> unitats = new ArrayList<ObjetoDirectorio>();
		try {
			URL url = new URL(getServiceUrl() + SERVEI_UNITAT + "unidadesDenominacion?denominacion=" + denominacio);
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
			return unitats;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar les unitats organitzatives via REST (" +
					"denominacio=" + denominacio + ")",
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

			URL url = new URL(getServiceUrl() + SERVEI_CATALEG + "paises?estado=V");
			logger.debug("URL: " + url);
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			List<CatPais> paisosRest = mapper.readValue(
					httpConnection.getInputStream(),
					TypeFactory.defaultInstance().constructCollectionType(
							List.class,
							CatPais.class));

			for (CatPais catPais : paisosRest) {
				CodiValorPais pais = new CodiValorPais();
				pais.setAlfa2Pais(catPais.getAlfa2Pais());
				pais.setAlfa3Pais(catPais.getAlfa3Pais());
				pais.setCodiPais(catPais.getCodigoPais());
				pais.setDescripcioPais(catPais.getDescripcionPais());
				paisos.add(pais);
			}
		} catch (Exception ex) {
			throw new SistemaExternException("No s'han pogut consultar els paisos", ex);
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
			return afegirZerosProvincies(provincies);
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
			return afegirZerosProvincies(provincies);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar les comunitats autònomes via REST",
					ex);
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
			logger.debug("URL: " + url);
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			CollectionType collection = TypeFactory.defaultInstance().constructCollectionType(List.class, CodiValor.class);
			List<CodiValor> localitats = mapper.readValue(httpConnection.getInputStream(), collection);
			Collections.sort(localitats);
			String cp = codiProvincia.length() < 2 ? 0 + codiProvincia : codiProvincia;
			String id;
			for (CodiValor localitat: localitats) {
				id = localitat.getId();
				if (id.length() < 4) {
					id = StringUtils.leftPad(id, 4, "0");
				}
				localitat.setId(cp + id);
			}
			return localitats;
		} catch (Exception ex) {
			throw new SistemaExternException("No s'han pogut consultar les localitats via REST", ex);
		}
	}
	
	@Override
	public List<OficinaSir> oficinesSIRUnitat(String unitat, Map<String, OrganismeDto> arbreUnitats) throws SistemaExternException {

		List<OficinaSir> oficinesSIR = new ArrayList<>();
		List<OficinaTF> oficinesWS = new ArrayList<>();
		try {
			getOficinesUnitatSuperior(unitat, oficinesWS, arbreUnitats);
			for (OficinaTF oficinaTF : oficinesWS) {
				OficinaSir oficinaSIR = new OficinaSir();
				oficinaSIR.setCodi(oficinaTF.getCodigo());
				oficinaSIR.setNom(oficinaTF.getDenominacion());
				oficinesSIR.add(oficinaSIR);
			}
			return oficinesSIR;
		} catch (Exception ex) {
			throw new SistemaExternException("No s'han pogut consultar les oficines SIR via REST (unitat=" + unitat + ")", ex);
		}
	}
	
	@Override
	public List<OficinaSir> getOficinesEntitat(String entitat) throws SistemaExternException {

		List<OficinaSir> oficines = new ArrayList<>();
		List<OficinaTF> oficinesWS = new ArrayList<>();
		try {
			URL url = new URL(getServiceUrl() + SERVEI_OFICINES + "obtenerArbolOficinas?codigo=" + entitat);
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
			return oficines;
		} catch (Exception ex) {
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
		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
		httpConnection.setRequestMethod("GET");
		httpConnection.setRequestProperty("Authorization", createBasicAuthHeaderValue());
		httpConnection.setDoInput(true);
		httpConnection.setDoOutput(true);
		byte[] response = IOUtils.toByteArray(httpConnection.getInputStream());
		return response;
	}

	private String createBasicAuthHeaderValue() {
		String auth = getUsernameServiceUrl() + ":" + getPasswordServiceUrl();
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
		String authHeaderValue = "Basic " + new String(encodedAuth);
		return authHeaderValue;
	}
	
	private String getServiceUrl() {
		String dir3Url = properties.getProperty("es.caib.notib.plugin.unitats.dir3.url");
		if (!dir3Url.endsWith("/"))
			dir3Url = dir3Url + "/";
		return dir3Url;
	}
	
	private String getUsernameServiceUrl() {
		String dir3Username = properties.getProperty("es.caib.notib.plugin.unitats.dir3.username");
		return dir3Username;
	}
	
	private String getPasswordServiceUrl() {
		String dir3Password = properties.getProperty("es.caib.notib.plugin.unitats.dir3.password");
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
