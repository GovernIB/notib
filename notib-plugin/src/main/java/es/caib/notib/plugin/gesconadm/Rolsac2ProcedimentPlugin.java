package es.caib.notib.plugin.gesconadm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.base.Strings;
import es.caib.notib.plugin.AbstractSalutPlugin;
import es.caib.notib.plugin.SistemaExternException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import es.caib.notib.plugin.gesconadm.rolsac2.Rolsac2FiltreOrden;
import es.caib.notib.plugin.gesconadm.rolsac2.Rolsac2FiltrePaginacio;
import es.caib.notib.plugin.gesconadm.rolsac2.Rolsac2Procediment;
import es.caib.notib.plugin.gesconadm.rolsac2.Rolsac2ProcedimentFilterRequest;
import es.caib.notib.plugin.gesconadm.rolsac2.Rolsac2ProcedimientosResponse;
import es.caib.notib.plugin.gesconadm.rolsac2.Rolsac2Servei;
import es.caib.notib.plugin.gesconadm.rolsac2.Rolsac2ServicioFilterRequest;
import es.caib.notib.plugin.gesconadm.rolsac2.Rolsac2ServiciosResponse;
import es.caib.notib.plugin.gesconadm.rolsac2.Rolsac2UAResponse;
import es.caib.notib.plugin.gesconadm.rolsac2.Rolsac2UnitatAdministrativa;
import es.caib.notib.plugin.utils.NotibLoggerPlugin;
import lombok.extern.slf4j.Slf4j;


/**
 * Implementació del plugin de consulta de procediments emprant ROLSAC2.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class Rolsac2ProcedimentPlugin extends AbstractSalutPlugin implements GestorContingutsAdministratiuPlugin {

	private final int PAGE_SIZE = 400;
	
	private Client jerseyClient;
	private ObjectMapper mapper;
	private String baseUrl;
	private final Properties properties;
	private Integer midaPagina = 30;
	private static Map<String, String> unitatsAdministratives = new HashMap<>();

	private NotibLoggerPlugin logger = new NotibLoggerPlugin(log);

//	public Rolsac2ProcedimentPlugin() {
//	}

	public Rolsac2ProcedimentPlugin(Properties properties) {
		super();
        this.properties = properties;
    }

	public Rolsac2ProcedimentPlugin(Properties properties, boolean configuracioEspecifica) {

		this.properties = properties;
		this.configuracioEspecifica = configuracioEspecifica;
		urlPlugin = properties.getProperty("es.caib.notib.plugin.gesconadm.base.url");
		logger.setMostrarLogs(Boolean.parseBoolean(properties.getProperty("es.caib.notib.log.tipus.plugin.ROLSAC")));
	}

	@Override
	public GesconAdm getProcSerByCodiSia(String codiSia, boolean isServei) throws SistemaExternException {

		var procSer = isServei ? "servei" : "procediment";
		logger.info("[ROLSAC2] Obtinguent el " + procSer + " amb codi " + codiSia);
		try {
			long startTime = System.currentTimeMillis();
			var resultat = isServei ? getServeiBySia(codiSia) : getProcedimentBySia(codiSia);
			incrementarOperacioOk(System.currentTimeMillis() - startTime);
			return resultat;
		} catch (Exception ex) {
			incrementarOperacioError();
			var msg = "No s'han pogut consultar el procediment/servei de ROLSAC2 (codiSia=" + codiSia + ")";
			logger.error(msg, ex);
			throw new SistemaExternException(msg, ex);
		}
	}

	private GesconAdm getServeiBySia(String codiSia) throws SistemaExternException {

		Rolsac2ServiciosResponse response;
		try {
				var filterServei = Rolsac2ServicioFilterRequest.builder()
						.codigoSia(codiSia)
						.estadoSia("A")
						.buscarEnDescendientesUA(1)
						.activo(1)
						.orden(new Rolsac2FiltreOrden("codigo", Rolsac2FiltreOrden.Rolsac2TipusOrdre.DESC))
						.build();
				response = findServeisRolsac(filterServei);
		} catch (Exception ex) {
			var msg = "No s'han pogut consultar el procediment/servei de ROLSAC2 (codiSia=" + codiSia + ")";
			logger.error(msg, ex);
			throw new SistemaExternException(msg, ex);
		}
		if (response == null || response.getItems() == null) {
			var msg = "No s'han pogut consultar el procediments/servie de ROLSAC2 (codiSia=" + codiSia + "). Resposta rebuda amb el codi " + response.getStatus();
			logger.error(msg, null);
			throw new SistemaExternException(msg);
		}
		List<GesconAdm> serveis = new ArrayList<>();
		for (var servei : response.getItems()) {
			serveis.add(this.toServei(servei));
		}
		return serveis.get(0);
	}

	private GesconAdm getProcedimentBySia(String codiSia) throws SistemaExternException {

		Rolsac2ProcedimientosResponse response;
		try {
			var filtre = Rolsac2ProcedimentFilterRequest.builder()
					.codigoSia(codiSia)
					.estadoSia("A")
					.buscarEnDescendientesUA(1)
					.activo(1)
					.orden(new Rolsac2FiltreOrden("codigoSia", Rolsac2FiltreOrden.Rolsac2TipusOrdre.DESC))
					.build();
			response = findAllProcedimentsRolsac(filtre);
		} catch (Exception ex) {
			var msg = "No s'han pogut consultar el procediment de ROLSAC2 (codiSia=" + codiSia + ")";
			logger.error(msg, ex);
			throw new SistemaExternException(msg, ex);
		}
		if (response == null || response.getItems() == null) {
			var msg = "No s'han pogut consultar el procediments de ROLSAC2 (codiSia=" + codiSia + "). Resposta rebuda amb el codi " + response.getStatus();
			logger.error(msg, null);
			throw new SistemaExternException(msg);
		}
		List<GesconAdm> procediments = new ArrayList<>();
		for (var procediment : response.getItems()) {
			procediments.add(this.toProcediment(procediment));
		}
		return procediments.get(0);
	}

	@Override
	public List<GcaProcediment> getAllProcediments() throws SistemaExternException {

		Rolsac2ProcedimientosResponse response;
		try {
			var filtre = Rolsac2ProcedimentFilterRequest.builder()
					.estadoSia("A")
					.buscarEnDescendientesUA(1)
					.activo(1)
					.orden(new Rolsac2FiltreOrden("codigoSia", Rolsac2FiltreOrden.Rolsac2TipusOrdre.DESC))
					.filtroPaginacion(new Rolsac2FiltrePaginacio(1, 100000))
					.build();
			response = findAllProcedimentsRolsac(filtre);
		} catch (Exception ex) {
			var msg = "No s'han pogut consultar  tots els procediments de ROLSAC2";
			logger.error(msg, ex);
			throw new SistemaExternException(msg, ex);
		}
		if (response == null || response.getItems() == null) {
			var msg = "No s'han pogut consultar tots els procediments de ROLSAC2. Resposta rebuda amb el codi " + response.getStatus();
			logger.error(msg, null);
			throw new SistemaExternException(msg);
		}
		List<GcaProcediment> procediments = new ArrayList<>();
		for (var procediment : response.getItems()) {
			procediments.add(this.toProcediment(procediment));
		}
		return procediments;
	}

	@Override
	public List<GcaProcediment> getProcedimentsByUnitat(String codiDir3, int numPagina) throws SistemaExternException {

		long startTime = System.currentTimeMillis();
		logger.info("[ROLSAC2] Consulta dels procediments de l'unitat organitzativa (codiDir3=" + codiDir3 + ")");
		Rolsac2ProcedimientosResponse response = null;
		Integer pagina = numPagina;
		try {
			if (numPagina == -1)  {
				pagina = 1;
			}
			var filtre = Rolsac2ProcedimentFilterRequest.builder()
					.codigoUADir3(codiDir3)
					.estadoSia("A")
					.buscarEnDescendientesUA(1)
					.activo(1)
					.orden(new Rolsac2FiltreOrden("codigo", Rolsac2FiltreOrden.Rolsac2TipusOrdre.DESC))
					.filtroPaginacion(new Rolsac2FiltrePaginacio(pagina, midaPagina))
					.build();
			response = findAllProcedimentsRolsac(filtre);
		} catch (Exception ex) {
			incrementarOperacioError();
			var msg = "No s'han pogut consultar els procediments de ROLSAC2 (codiDir3=" + codiDir3 + ")";
			logger.error(msg, ex);
			throw new SistemaExternException(msg, ex);}

		if (response == null || response.getItems() == null) {
			var msg = "No s'han pogut consultar els procediments de ROLSAC2 (codiDir3=" + codiDir3 + "). Resposta rebuda amb el codi " + response.getStatus();
			logger.error(msg, null);
			incrementarOperacioError();
			throw new SistemaExternException(msg);
		}
		List<GcaProcediment> procediments = new ArrayList<>();
		for (var procediment : response.getItems()) {
			procediments.add(this.toProcediment(procediment));
		}
		incrementarOperacioOk(System.currentTimeMillis() - startTime);
		return procediments;
	}

	@Override
	public List<GcaProcediment> getProcedimentsByUnitat(String codiDir3) throws SistemaExternException {
		midaPagina = getTotalProcediments(codiDir3);
		return getProcedimentsByUnitat(codiDir3, -1);
	}

	@Override
	public int getTotalProcediments(String codiDir3) throws SistemaExternException {

		long startTime = System.currentTimeMillis();
		logger.info("[ROLSAC2] Obtinguent el total de procediments de la unitat administrativa " + codiDir3);
		Rolsac2ProcedimientosResponse response = null;
		try {
			response = findAllProcedimentsRolsac(
					Rolsac2ProcedimentFilterRequest.builder()
							.codigoUADir3(codiDir3)
							.estadoSia("A")
							.buscarEnDescendientesUA(1)
							.activo(1)
							.orden(new Rolsac2FiltreOrden("codigo", Rolsac2FiltreOrden.Rolsac2TipusOrdre.DESC))
							.filtroPaginacion(new Rolsac2FiltrePaginacio(1, 1))
							.build()
			);
		} catch (Exception ex) {
			incrementarOperacioError();
			var msg = "[ROLSAC2] No s'han pogut consultar el el total de procediments de ROLSAC2 (codiDir3=" + codiDir3 + ")";
			logger.error(msg, ex);
			throw new SistemaExternException(msg, ex);
		}

		if (response == null || response.getItems() == null) {
			var msg = "No s'han pogut consultar els procediments de ROLSAC2 (codiDir3=" + codiDir3 + "). Resposta rebuda amb el codi " + response.getStatus();
			logger.error(msg, null);
			incrementarOperacioError();
			throw new SistemaExternException(msg);
		}
		incrementarOperacioOk(System.currentTimeMillis() - startTime);
		return response.getItems().size();
	}

	@Override
	public List<GcaServei> getAllServeis() throws SistemaExternException {

		logger.info("[ROLSAC2] Obtinguent tots els serveis");
		long startTime = System.currentTimeMillis();
		Rolsac2ServiciosResponse response;
		try {
			var filtre = Rolsac2ServicioFilterRequest.builder()
					.estadoSia("A")
					.buscarEnDescendientesUA(1)
					.activo(1)
					.orden(new Rolsac2FiltreOrden("codigoSia", Rolsac2FiltreOrden.Rolsac2TipusOrdre.DESC))
					.filtroPaginacion(new Rolsac2FiltrePaginacio(1, 100000))
					.build();
			response = findServeisRolsac(filtre);
		} catch (Exception ex) {
			incrementarOperacioError();
			var msg = "No s'han pogut consultar  tots els serveis de ROLSAC2";
			logger.error(msg, ex);
			throw new SistemaExternException(msg, ex);
		}
		if (response == null || response.getItems() == null) {
			incrementarOperacioError();
			var msg = "No s'han pogut consultar tots els serveis de ROLSAC2. Resposta rebuda amb el codi " + response.getStatus();
			logger.error(msg, null);
			throw new SistemaExternException(msg);
		}
		List<GcaServei> serveis = new ArrayList<>();
		for (var servei : response.getItems()) {
			serveis.add(this.toServei(servei));
		}
		incrementarOperacioOk(System.currentTimeMillis() - startTime);
		return serveis;
	}

	@Override
	public List<GcaServei> getServeisByUnitat(String codiDir3, int numPagina) throws SistemaExternException {

		long startTime = System.currentTimeMillis();
		logger.info("[ROLSAC2] Consulta dels serveis de l'unitat organitzativa (codiDir3=" + codiDir3 + ")");
		Rolsac2ServiciosResponse response = null;
		var pagina = numPagina;
		try {
			if (numPagina == -1)  {
				pagina = 1;
			}
			var filtre = Rolsac2ServicioFilterRequest.builder()
					.codigoUADir3(codiDir3)
					.estadoSia("A")
					.buscarEnDescendientesUA(1)
					.activo(1)
					.orden(new Rolsac2FiltreOrden("codigo", Rolsac2FiltreOrden.Rolsac2TipusOrdre.DESC))
					.filtroPaginacion(new Rolsac2FiltrePaginacio(pagina, 30))
					.build();
			response = findServeisRolsac(filtre);
		} catch (Exception ex) {
			incrementarOperacioError();
			var msg = "No s'han pogut consultar els serveis de ROLSAC2 (codiDir3=" + codiDir3 + ")";
			logger.error(msg, ex);
			throw new SistemaExternException(msg, ex);}

		if (response == null || response.getItems() == null) {
			var msg = "No s'han pogut consultar els serveis de ROLSAC2 (codiDir3=" + codiDir3 + "). Resposta rebuda amb el codi " + response.getStatus();
			logger.error(msg, null);
			incrementarOperacioError();
			throw new SistemaExternException(msg);
		}
		List<GcaServei> procediments = new ArrayList<>();
		for (var procediment : response.getItems()) {
			procediments.add(this.toServei(procediment));
		}
		incrementarOperacioOk(System.currentTimeMillis() - startTime);
		return procediments;
	}

	@Override
	public List<GcaServei> getServeisByUnitat(String codiDir3) throws SistemaExternException {

		midaPagina = getTotalServeis(codiDir3);
		return getServeisByUnitat(codiDir3, -1);
	}

	@Override
	public int getTotalServeis(String codiDir3) throws SistemaExternException {

		long startTime = System.currentTimeMillis();
		logger.info("[ROLSAC2] Obtinguent el total de serveis de la unitat administrativa " + codiDir3);
		Rolsac2ProcedimientosResponse response = null;
		try {
			response = findAllProcedimentsRolsac(
					Rolsac2ProcedimentFilterRequest.builder()
							.codigoUADir3(codiDir3)
							.estadoSia("A")
							.buscarEnDescendientesUA(1)
							.activo(1)
							.orden(new Rolsac2FiltreOrden("codigo", Rolsac2FiltreOrden.Rolsac2TipusOrdre.DESC))
							.filtroPaginacion(new Rolsac2FiltrePaginacio(1, 1))
							.build()
			);
		} catch (Exception ex) {
			incrementarOperacioError();
			var msg = "[ROLSAC2] No s'han pogut consultar el el total de serveis de ROLSAC2 (codiDir3=" + codiDir3 + ")";
			logger.error(msg, ex);
			throw new SistemaExternException(msg, ex);
		}

		if (response == null || response.getItems() == null) {
			var msg = "No s'han pogut consultar els serveis de ROLSAC2 (codiDir3=" + codiDir3 + "). Resposta rebuda amb el codi " + response.getStatus();
			logger.error(msg, null);
			incrementarOperacioError();
			throw new SistemaExternException(msg);
		}
		incrementarOperacioOk(System.currentTimeMillis() - startTime);
		return response.getItems().size();
	}




//		@Override
//	public List<Procediment> findServeisAmbCodiDir3(String codiDir3) throws SistemaExternException {
//
//		logger.info("[ROLSAC2] Consulta dels serveis de l'unitat organitzativa (codiDir3=" + codiDir3 + ")");
//		Rolsac2ServiciosResponse response = null;
//		try {
//			response = findServeisRolsac(Rolsac2ServicioFilterRequest
//						.builder()
//						.codigoUADir3(codiDir3)
//						.estadoSia("A")
//						.buscarEnDescendientesUA(1)
//						.activo(1)
//						.filtroPaginacion(new Rolsac2FiltrePaginacio(1, 400))
//						.build());
//		} catch (Exception ex) {
//			var msg = "No s'han pogut consultar els serveis de ROLSAC2 (codiDir3=" + codiDir3 + ")";
//			logger.error(msg, ex);
//			throw new SistemaExternException(msg, ex);
//		}
//
//		if (response == null || !response.getStatus().equals("200")) {
//			var msg = "No s'han pogut consultar els serveis de ROLSAC2 (codiDir3=" + codiDir3 + "). Resposta rebuda amb el codi " + response.getStatus();
//			logger.error(msg);
//			throw new SistemaExternException(msg);
//		}
//		List<Procediment> procediments = new ArrayList<>();
//		for (var procediment : response.getItems()) {
//			procediments.add(this.toServei(procediment));
//		}
//		return procediments;
//	}

	public GcaProcediment toProcediment(Rolsac2Procediment procediment) throws SistemaExternException {

		var dto = new GcaProcediment();
		if (procediment == null) {
			return dto;
		}
		dto.setCodi(String.valueOf(procediment.getCodigo()));
		dto.setCodiSIA(String.valueOf(procediment.getCodigoSIA()));
		dto.setNom(procediment.getNombreProcedimientoWorkFlow());
		dto.setComu(procediment.getComun() != null && procediment.getComun() > 0);
//		dto.setTipus(ProcedimentTipusEnumDto.PROCEDIMENT);
		String codi = null;
		if (procediment.getLinkUnidadAdministrativaResponsable() != null) {
 			codi = procediment.getLinkUnidadAdministrativaResponsable().getCodigo();
		} else if (procediment.getLinkUnidadAdministrativaCompetente() != null) {
 			codi = procediment.getLinkUnidadAdministrativaCompetente().getCodigo();
		} else if (procediment.getLinkUnidadAdministrativaInstructora() != null) {
 			codi = procediment.getLinkUnidadAdministrativaInstructora().getCodigo();
		}
		var codiDir3 = findUnitatAdministrativaAmbCodi(codi);
		dto.setUnitatAdministrativacodi(codiDir3);
		return dto;
	}
	
	public GcaServei toServei(Rolsac2Servei procediment) throws  SistemaExternException {

		var dto = new GcaServei();
		if (procediment == null) {
			return dto;
		}
		dto.setCodi(String.valueOf(procediment.getCodigo()));
		dto.setCodiSIA(String.valueOf(procediment.getCodigoSIA()));
		dto.setNom(procediment.getNombreProcedimientoWorkFlow());
		dto.setComu(procediment.getComun() != null && procediment.getComun().intValue() == 1);
//		dto.setTipus(ProcedimentTipusEnumDto.SERVEI); // TODO es necessari aquest attribut?
		String codi = null;
		if (procediment.getLinkUnidadAdministrativaResponsable() != null) {
			codi = procediment.getLinkUnidadAdministrativaResponsable().getCodigo();
		} else if (procediment.getLinkUnidadAdministrativaInstructora() != null) {
			codi = procediment.getLinkUnidadAdministrativaInstructora().getCodigo();
		}
		var codiDir3 = findUnitatAdministrativaAmbCodi(codi);
		dto.setUnitatAdministrativacodi(codiDir3);
		return dto;
	}

	private Client getJerseyClient() {

		if (jerseyClient != null) {
			return jerseyClient;
		}
		jerseyClient = new Client();
		if (getServiceTimeout() != null) {
			jerseyClient.setConnectTimeout(getServiceTimeout());
			jerseyClient.setReadTimeout(getServiceTimeout());
		}
		if (getServiceUsername() != null) {
			jerseyClient.addFilter(new HTTPBasicAuthFilter(getServiceUsername(), getServicePassword()));
		}
		mapper = new ObjectMapper();
		// Permet rebre un sol objecte en el lloc a on hi hauria d'haver una llista.
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		// Mecanisme de deserialització dels enums
		mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
		// Per a no serialitzar propietats amb valors NULL
		mapper.setSerializationInclusion(Include.NON_NULL);
		// No falla si hi ha propietats que no estan definides a l'objecte destí
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		return jerseyClient;
	}

	private Rolsac2ProcedimientosResponse findAllProcedimentsRolsac(Rolsac2ProcedimentFilterRequest body) throws Exception {

		body.setFiltroPaginacion(new Rolsac2FiltrePaginacio(0, PAGE_SIZE));
		final String url = getServiceUrl() + "/procedimientos";
		logger.info("[ROLSAC2] Enviant petició HTTP a l'arxiu (url=" + url + ", tipus=application/json, body=" + body + ")");
		var response = getJerseyClient().resource(url).accept("application/json").type("application/json").post(ClientResponse.class, body);
		var procedimentsResponse = response.getEntity(Rolsac2ProcedimientosResponse.class);
		var executor = Executors.newFixedThreadPool(procedimentsResponse.getTotalPages());
		List<Future<Rolsac2ProcedimientosResponse>> futures = new ArrayList<>();
		for (int i = 1; i <= procedimentsResponse.getTotalPages(); i++) {
			final Rolsac2ProcedimentFilterRequest taskBody = body.clone();
			taskBody.setFiltroPaginacion(new Rolsac2FiltrePaginacio(i, PAGE_SIZE));
			Callable<Rolsac2ProcedimientosResponse> task = () -> {
                int trys = 0;
                while(trys < 2) {
                    trys++;
                    var response1 = getJerseyClient().resource(url).accept("application/json").type("application/json").post(ClientResponse.class, taskBody);
                    if(response1.getStatus() != 200) {
                        continue;
                    }
                    return response1.getEntity(Rolsac2ProcedimientosResponse.class);
                }
                return null;
            };
			futures.add(executor.submit(task));
		}
		for (var future : futures) {
			var result = future.get();
			if (result != null) {
				procedimentsResponse.getItems().addAll(result.getItems());
			}
		}
		executor.shutdown();
		return procedimentsResponse;
	}
//
//	@Override // TODO VEURE SI ES NECESSITA AQUEST METODE
	private String findUnitatAdministrativaAmbCodi(String codi) throws SistemaExternException {

		logger.info("[ROLSAC2] Consulta de la unitat administrativa amb codi (codi=" + codi + ")");
		UnitatAdministrativa unitatAdministrativa = null;

		if (unitatsAdministratives.containsKey(codi)) {
			return unitatsAdministratives.get(codi);
		}
		try {
			String urlAmbMetode = getServiceUrl() + "/unidades_administrativas/" + codi;
			Client jerseyClient = getJerseyClient();

			Rolsac2UAResponse resposta = jerseyClient.
					resource(urlAmbMetode).
					post(Rolsac2UAResponse.class);

			if (resposta.getItems() != null && !resposta.getItems().isEmpty()) {
				Rolsac2UnitatAdministrativa unitatAdministrativaRolsac = resposta.getItems().get(0);
				unitatAdministrativa = new UnitatAdministrativa();
				unitatAdministrativa.setCodigo(Long.valueOf(unitatAdministrativaRolsac.getCodigo()));
				unitatAdministrativa.setCodigoDIR3(unitatAdministrativaRolsac.getCodigoDIR3());
				unitatAdministrativa.setNombre(unitatAdministrativaRolsac.getNombre());
				if (unitatAdministrativaRolsac.getLink_padre() != null) {
					unitatAdministrativa.setPadre(unitatAdministrativaRolsac.getLink_padre());
				}
			}
			unitatsAdministratives.put(codi, unitatAdministrativa.getCodigoDIR3());
			return unitatAdministrativa.getCodigoDIR3();
		} catch (Exception ex) {
			throw new SistemaExternException("No s'ha pogut consultar la unitat administrativa amb codi " + codi + " via REST: " + ex, ex);
		}
	}
	
	private Rolsac2ServiciosResponse findServeisRolsac(Rolsac2ServicioFilterRequest body) throws Exception {

		body.setFiltroPaginacion(new Rolsac2FiltrePaginacio(0, PAGE_SIZE));
		final String url = getServiceUrl() + "/servicios";
		logger.info("[ROLSAC2] Enviant petició HTTP a l'arxiu (url=" + url + ", tipus=application/json, body=" + body + ")");
		var response = getJerseyClient().resource(url).accept("application/json").type("application/json").post(ClientResponse.class, body);
		if(response.getStatus() != 200) {
			System.out.print(response.getEntity(String.class));
		}
		var serveisResponse = response.getEntity(Rolsac2ServiciosResponse.class);
		var executor = Executors.newFixedThreadPool(serveisResponse.getTotalPages());
		List<Future<Rolsac2ServiciosResponse>> futures = new ArrayList<>();
		for (int i = 1; i <= serveisResponse.getTotalPages(); i++) {
			final Rolsac2ServicioFilterRequest taskBody = body.clone();
			taskBody.setFiltroPaginacion(new Rolsac2FiltrePaginacio(i, PAGE_SIZE));
			Callable<Rolsac2ServiciosResponse> task = () -> {
                int trys = 0;
                while (trys < 2) {
                    trys++;
                    var response1 = getJerseyClient().resource(url).accept("application/json").type("application/json").post(ClientResponse.class, taskBody);
                    if (response1.getStatus() != 200) {
                        continue;
                    }
                    return response1.getEntity(Rolsac2ServiciosResponse.class);
                }
                return null;
            };
			futures.add(executor.submit(task));
		}
		for (var future : futures) {
			Rolsac2ServiciosResponse result = future.get();
			if(result != null) {
				serveisResponse.getItems().addAll(result.getItems());
			}
		}
		executor.shutdown();
		return serveisResponse;
	}
	
	private String getServiceUrl() {

		if (baseUrl != null && !baseUrl.isEmpty()) {
			return baseUrl;
		}
		baseUrl = properties.getProperty("es.caib.notib.plugin.gesconadm.base.url");
		if (baseUrl != null && !baseUrl.isEmpty() && !baseUrl.endsWith("/")) {
			baseUrl = baseUrl + "/";
		}
		baseUrl += "services/v1";
		return baseUrl;
	}

	private String getServiceUsername() {
		return properties.getProperty("es.caib.notib.plugin.gesconadm.username");
	}

	private String getServicePassword() {
		return properties.getProperty("es.caib.notib.plugin.gesconadm.password");
	}

	private Integer getServiceTimeout() { // TODO VEURE SI CAL AQUESTA PROPIETAT
//		String key = "app.plugins.procediments.rolsac.service.timeout";
//		return GlobalProperties.getInstance().getProperty(key) != null ? GlobalProperties.getInstance().getAsInt(key) : null;
		return null;
	}

	private boolean isServiceBasicAuthentication() {

		var isBasicAuth = properties.getProperty("es.caib.notib.plugin.gesconadm.basic.authentication");
		return Strings.isNullOrEmpty(isBasicAuth) || Boolean.parseBoolean(isBasicAuth);
	}

}
