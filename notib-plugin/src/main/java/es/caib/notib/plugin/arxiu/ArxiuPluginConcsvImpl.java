package es.caib.notib.plugin.arxiu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.EstatSalutEnum;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.notib.logic.intf.util.MimeUtils;
import es.caib.notib.plugin.utils.NotibLoggerPlugin;
import es.caib.plugins.arxiu.api.ArxiuException;
import es.caib.plugins.arxiu.api.ContingutOrigen;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.DocumentEstatElaboracio;
import es.caib.plugins.arxiu.api.DocumentExtensio;
import es.caib.plugins.arxiu.api.DocumentFormat;
import es.caib.plugins.arxiu.api.DocumentMetadades;
import es.caib.plugins.arxiu.api.DocumentTipus;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.caib.ArxiuPluginCaib;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class ArxiuPluginConcsvImpl extends ArxiuPluginCaib implements ArxiuPlugin {

	
	public static final String ARXIU_BASE_PROPERTY = "es.caib.notib.plugin.arxiu.";

	private static final String ARXIUCAIB_BASE_PROPERTY = ARXIU_BASE_PROPERTY + "caib.";
	private static final String JERSEY_TIMEOUT_CONNECT = "10000";
	private static final String JERSEY_TIMEOUT_READ = "60000";
	private static final String UUID = "uuid:";
	private static final String ERROR_METADADES_CSV = "No ha estat possible obtenir les metadades del document amb CSV ";
	private static final String ERROR_METADADES_UUID = "No ha estat possible obtenir les metadades del document amb UUID ";

	private Client versioImprimibleClient;

	private NotibLoggerPlugin logger = new NotibLoggerPlugin(log);

//	public ArxiuPluginConcsvImpl(String propertyKeyBase) {
//		super(propertyKeyBase);
//	}
//
//	public ArxiuPluginConcsvImpl(String propertyKeyBase, Properties properties) {
//
//		super(propertyKeyBase, properties);
//		logger.setMostrarLogs(Boolean.parseBoolean(properties.getProperty("es.caib.notib.log.tipus.plugin.ARXIU")));
//	}

	public ArxiuPluginConcsvImpl(String propertyKeyBase, Properties properties, boolean configuracioEspecifica) {

		super(propertyKeyBase, properties);
		this.configuracioEspecifica = configuracioEspecifica;
		logger.setMostrarLogs(Boolean.parseBoolean(properties.getProperty("es.caib.notib.log.tipus.plugin.ARXIU")));
	}

	@Override
	public Document documentDetalls(String identificador, String versio, boolean ambContingut) throws ArxiuException {

		try {
			Document document = identificador.contains("csv:") ? 
					documentDetallsCsv(identificador, ambContingut) : 
					documentDetallsUuid(identificador, ambContingut);
			incrementarOperacioOk();
			return document;
		} catch (Exception ex) {
			incrementarOperacioError();
			throw ex;
		}
	}

	@Override
	public DocumentContingut documentImprimible(String identificador) throws ArxiuException {

		DocumentContingut documentContingut;
		var id = identificador;
		try {
			if (identificador.contains(UUID)) {
				identificador = identificador.replace(UUID, "");
				documentContingut = documentImprimibleUuid(identificador);
			} else {
				identificador = identificador.replace("csv:", "");
				documentContingut = documentImprimibleCsv(identificador);
			}
			incrementarOperacioOk();
			return documentContingut;
		} catch (Exception ex) {
			incrementarOperacioError();
			return documentOriginal(id);
		}
	}

	private Document documentDetallsCsv(String identificador, boolean ambContingut) {

		try {
			var id = identificador;
			identificador = identificador.replace("csv:", "");
			var response = new Document();
			if (ambContingut) {
				try {

					response.setContingut(documentImprimibleCsv(identificador));
				} catch (Exception ex) {
					log.info("No s'ha pogut obtenir la versio imprimible. Obtenint versio original del document csv " + id);
					response.setContingut(documentOriginal(id));
				}
			}
			try {
				var result = documentMetadadesCsv(identificador);
				response.setMetadades(toDocumentMetadades(result));
				response.setFirmes(result.containsKey("eni:tipoFirma") ? Collections.singletonList(new Firma()) : null);
			} catch(Exception e) {
				logger.info("[ARXIU]" + ERROR_METADADES_CSV + identificador);
				response.setMetadades(null);
			}
			return response;
		} catch (Exception var12) {
			throw new ArxiuException("S'ha produit un error obtenent els detalls del document: " + identificador, var12);
		}
	}

	private Document documentDetallsUuid(String identificador, boolean ambContingut) {

		try {
			var id = identificador;
			identificador = identificador.replace(UUID, "");
			Document response = new Document();
			if (ambContingut) {
				try {
					response.setContingut(documentImprimibleUuid(identificador));
				} catch (Exception ex) {
					log.info("No s'ha pogut obtenir la versio imprimible. Obtenint versio original del document uuid " + id);
					response.setContingut(documentOriginal(id));
				}
			}
			try {
				var result = documentMetadadesUuid(identificador);
				response.setMetadades(toDocumentMetadades(result));
				response.setFirmes(result.containsKey("eni:tipoFirma") ? Collections.singletonList(new Firma()) : null);
			} catch(Exception e) {
				log.debug(ERROR_METADADES_UUID + identificador);
				response.setMetadades(null);
			}
			return response;
		} catch (Exception var12) {
			throw new ArxiuException("S'ha produit un error obtenent els detalls del document: " + identificador, var12);
		}
	}

	private DocumentContingut documentOriginal(String identificador) throws ArxiuException {

		try {
			logger.info("[ConCSV] Recuperant versio original uuid. identificador " + identificador);
			String url = identificador.contains(UUID) ? getPropertyConversioOriginalUrlUuid() : getPropertyConversioOriginalUrlCsv();
			var webResource = getVersioImprimibleClientConcsv().resource(url + (!url.endsWith("/") ? "/" : "") + identificador.split(":")[1]);
			var is = webResource.get(InputStream.class);
			var contingut = new DocumentContingut();
			contingut.setContingut(IOUtils.toByteArray(is));
			contingut.setTipusMime(MimeUtils.getMimeTypeFromContingut(null, contingut.getContingut()));
			contingut.setArxiuNom("versio_original" + MimeUtils.getExtension(contingut.getTipusMime()));
			contingut.setTamany(contingut.getContingut().length);
			return contingut;
		} catch (Exception ex) {
			log.debug("S'ha produit un error generant el document " + identificador, ex);
			throw new ArxiuException("S'ha produit un error recuperant el document original " + identificador, ex);
		}
	}

	private DocumentContingut documentImprimibleCsv(final String identificador) throws ArxiuException {


		/*
		 * Les URLs de consulta son les següents:
		 *   https://intranet.caib.es/concsv/rest/printable/uuid/IDENTIFICADOR?metadata1=METADADA_1&metadata2=METADADA_2&watermark=MARCA_AIGUA
		 *   https://intranet.caib.es/concsv/rest/printable/CSV?metadata1=METADADA_1&metadata2=METADADA_2&watermark=MARCA_AIGUA
		 * A on:
		 *   - CSV és el CSV del document a consultar [OBLIGATORI]
		 *   - IDENTIFICADOR és el UUID del document a consultar [OBLIGATORI]
		 *   - METADADA_1 és la primera metadada [OPCIONAL]
		 *   - METADADA_2 és la segona metadada [OPCIONAL]
		 *   - MARCA_AIGUA és el text de la marca d'aigua que apareixerà impresa a cada fulla [OPCIONAL]
		 * Només es obligatori informa la HASH, la resta d'elements son opcionals. Si no s'informen metadades s'imprimeix l'hora i dia de la generació del document imprimible.
		 */
		try {
//			if (identificador.contains("original_")) {
//				return documentOriginal(identificador.split("original_")[1]);
//			}
			var is = generarVersioImprimibleCsv(identificador, null, /* metadada 1 */ null, /* metadada 2 */ null); // marca d'aigua
			var contingut = new DocumentContingut();
			contingut.setArxiuNom("versio_imprimible.pdf");
			contingut.setTipusMime("application/pdf");
			contingut.setContingut(IOUtils.toByteArray(is));
			contingut.setTamany(contingut.getContingut().length);
			return contingut;
		} catch (Exception ex) {
			log.debug("S'ha produit un error generant la versió imprimible del document amb CSV " + identificador, ex);
			throw new ArxiuException("S'ha produit un error generant la versió imprimible del document amb CSV " + identificador, ex);
		}
	}
	
	
	private DocumentContingut documentImprimibleUuid(final String identificador) throws ArxiuException {
		/*
		 * Les URLs de consulta son les següents:
		 *   https://intranet.caib.es/concsv/rest/printable/uuid/IDENTIFICADOR?metadata1=METADADA_1&metadata2=METADADA_2&watermark=MARCA_AIGUA
		 *   https://intranet.caib.es/concsv/rest/printable/CSV?metadata1=METADADA_1&metadata2=METADADA_2&watermark=MARCA_AIGUA
		 * A on:
		 *   - CSV és el CSV del document a consultar [OBLIGATORI]
		 *   - IDENTIFICADOR és el UUID del document a consultar [OBLIGATORI]
		 *   - METADADA_1 és la primera metadada [OPCIONAL]
		 *   - METADADA_2 és la segona metadada [OPCIONAL]
		 *   - MARCA_AIGUA és el text de la marca d'aigua que apareixerà impresa a cada fulla [OPCIONAL]
		 * Només es obligatori informa la HASH, la resta d'elements son opcionals. Si no s'informen metadades s'imprimeix l'hora i dia de la generació del document imprimible.
		 */
		try {
//			if (identificador.contains("original_")) {
//				return documentOriginal(identificador.split("original_")[1]);
//			}
			InputStream is = generarVersioImprimibleUuid(identificador, null, null, null);
			DocumentContingut contingut = new DocumentContingut();
			contingut.setArxiuNom("versio_imprimible.pdf");
			contingut.setTipusMime("application/pdf");
			contingut.setContingut(IOUtils.toByteArray(is));
			contingut.setTamany(contingut.getContingut().length);
			return contingut;
		} catch (Exception ex) {
			log.debug("S'ha produit un error generant la versió imprimible del document amb UUID " + identificador, ex);
			throw new ArxiuException("S'ha produit un error generant la versió imprimible del document amb UUID " + identificador, ex);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String,Object> documentMetadadesUuid(String identificador) {
		/*
		 * Les URLs de consulta de metadades son les següents:
		 *   https://intranet.caib.es/concsv/rest/metadata/uuid/{IDENTIFICADOR}
		 *   https://intranet.caib.es/concsv/rest/metadata/{CSV}
		 * A on:
		 *   - {CSV} és el CSV del document a consultar [OBLIGATORI]
		 *   - {IDENTIFICADOR} és el UUID del document a consultar [OBLIGATORI]
		 */
		try {
			String url = getPropertyConcsvBaseUrl();
			if (!url.endsWith("/")) {
				url += "/";
			}
			url += "metadata/uuid/";
			logger.info("[ARXIU] Obtinguent metadades uuid del document " + identificador + " url " + url);
			var webResource = getVersioImprimibleClientConcsv().resource(url + identificador);
			var jsonData = webResource.accept(MediaType.APPLICATION_JSON).get(String.class);
			return new ObjectMapper().readValue(jsonData, HashMap.class);
		} catch (Exception ex) {
			var msg = ERROR_METADADES_UUID + identificador;
			log.debug(msg, ex);
			throw new ArxiuException(msg, ex);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String,Object> documentMetadadesCsv(String identificador) {
		/*
		 * Les URLs de consulta son les següents:
		 *   https://intranet.caib.es/concsv/rest/metadata/uuid/{IDENTIFICADOR}
		 *   https://intranet.caib.es/concsv/rest/metadata/{CSV}
		 * A on:
		 *   - {CSV} és el CSV del document a consultar [OBLIGATORI]
		 *   - {IDENTIFICADOR} és el UUID del document a consultar [OBLIGATORI]
		 */
		try {
			String url = getPropertyConcsvBaseUrl();
			if (!url.endsWith("/")) {
				url += "/";
			}
			url += "metadata/";
			logger.info("[ARXIU] Obtinguent metadades del document " + identificador + " url " + url);
			WebResource webResource = getVersioImprimibleClientConcsv().resource(url + identificador);
			String jsonData = webResource.accept(MediaType.APPLICATION_JSON).get(String.class);
			return new ObjectMapper().readValue(jsonData, HashMap.class);
		} catch (Exception ex) {
			var msg = ERROR_METADADES_CSV + identificador;
			log.error(msg, ex);
			throw new ArxiuException(msg, ex);
		}
	}

	private InputStream generarVersioImprimibleCsv(String identificador, String metadada1, String metadada2, String marcaAigua) {

		String url = getPropertyConversioImprimibleUrlCsvConcsv();
		WebResource webResource;
		logger.info("[ARXIU] Generant versio imprimible csv. identificador " + identificador + " metadada1 " + metadada1 + " metadada2 " + metadada2 + " marcaAigua " + marcaAigua);
		if (url.endsWith("/")) {
			webResource = getVersioImprimibleClientConcsv().resource(url + identificador);
		} else {
			webResource = getVersioImprimibleClientConcsv().resource(url + "/" + identificador);
		}
		if (metadada1 != null) {
			webResource.queryParam("metadata1", metadada1);
		}
		if (metadada2 != null) {
			webResource.queryParam("metadata2", metadada2);
		}
		if (marcaAigua != null) {
			webResource.queryParam("watermark", marcaAigua);
		}
		return webResource.get(InputStream.class);
	}
	
	private InputStream generarVersioImprimibleUuid(String identificador, String metadada1, String metadada2, String marcaAigua) {

		String url = getPropertyConversioImprimibleUrlUuidConcsv();
		WebResource webResource;
		logger.info("[ARXIU] Generant versio imprimible uuid. identificador " + identificador + " metadada1 " + metadada1 + " metadada2 " + metadada2 + " marcaAigua " + marcaAigua);
		if (url.endsWith("/")) {
			webResource = getVersioImprimibleClientConcsv().resource(url + identificador);
		} else {
			webResource = getVersioImprimibleClientConcsv().resource(url + "/" + identificador);
		}
		if (metadada1 != null) {
			webResource.queryParam("metadata1", metadada1);
		}
		if (metadada2 != null) {
			webResource.queryParam("metadata2", metadada2);
		}
		if (marcaAigua != null) {
			webResource.queryParam("watermark", marcaAigua);
		}
		return webResource.get(InputStream.class);
	}

	@SuppressWarnings("unchecked")
	private static DocumentMetadades toDocumentMetadades(Map<String,Object> metadatas) throws ArxiuException {

		DocumentMetadades metadades = new DocumentMetadades();
		for (var entry : metadatas.entrySet()) {
			Object value = entry.getValue();
			if (value == null) {
				continue;
			}
			if ("eni:id".equals(entry.getKey())) {
				metadades.setIdentificador(value.toString());
			} else if ("eni:v_nti".equals(entry.getKey())) {
				metadades.setVersioNti(value.toString());
			} else if ("eni:origen".equals(entry.getKey())) {
				metadades.setOrigen(ContingutOrigen.toEnum(String.valueOf(value)));
			} else if ("eni:fecha_inicio".equals(entry.getKey())) {
				metadades.setDataCaptura(parseDateIso8601(value.toString()));
			} else if ("eni:estado_elaboracion".equals(entry.getKey())) {
				metadades.setEstatElaboracio(DocumentEstatElaboracio.toEnum(value.toString()));
			} else if ("eni:tipo_doc_ENI".equals(entry.getKey())) {
				metadades.setTipusDocumental(DocumentTipus.toEnum(value.toString()));
			} else {
				Object val;
				if ("eni:organo".equals(entry.getKey())) {
					val = value;
					if (val instanceof List) {
						metadades.setOrgans((List<String>) value);
					} else {
						metadades.setOrgans(Arrays.asList((String) val));
					}
				} else if ("eni:nombre_formato".equals(entry.getKey())) {
					metadades.setFormat(DocumentFormat.toEnum(value.toString()));
				} else if ("eni:extension_formato".equals(entry.getKey())) {
					metadades.setExtensio(DocumentExtensio.toEnum(value.toString()));
				} else {
					Map<String, Object> metadadesAddicionals = metadades.getMetadadesAddicionals();
					if (metadadesAddicionals == null) {
						metadadesAddicionals = new HashMap<>();
						metadades.setMetadadesAddicionals(metadadesAddicionals);
					}
					metadadesAddicionals.put(entry.getKey(), value);
				}
			}
		}
		return metadades;
	}
	
	private static Date parseDateIso8601(String date) throws ArxiuException {
		if (date == null) {
			return null;
		} else {
			try {
				Calendar c = DatatypeConverter.parseDateTime(date);
				return c.getTime();
			} catch (IllegalArgumentException var2) {
				throw new ArxiuException("No s'ha pogut parsejar el valor de la data (valor=" + date + ")");
			}
		}
	}

	private Client getVersioImprimibleClientConcsv() {

		if (versioImprimibleClient != null) {
			return versioImprimibleClient;
		}
		versioImprimibleClient = Client.create();
		versioImprimibleClient.setConnectTimeout(getPropertyTimeoutConnectConcsv());
		versioImprimibleClient.setReadTimeout(getPropertyTimeoutReadConcsv());
		String usuari = getPropertyConversioImprimibleUsuariConcsv();
		String contrasenya = getPropertyConversioImprimibleContrasenyaConcsv();
		if (usuari != null) {
			versioImprimibleClient.addFilter(new HTTPBasicAuthFilter(usuari, contrasenya));
		}
		return versioImprimibleClient;
	}

//	private static String ORIGINAL_URL_CSV = ARXIUCAIB_BASE_PROPERTY + "conversio.original.url.csv";
//	private static String ORIGINAL_URL_UUID = ARXIUCAIB_BASE_PROPERTY + "conversio.original.url.uuid";
//	private static String IMPRIMIBLE_URL_CSV = ARXIUCAIB_BASE_PROPERTY + "conversio.imprimible.url.csv";
//	private static String IMPRIMIBLE_URL_UUID = ARXIUCAIB_BASE_PROPERTY + "conversio.imprimible.url.uuid";
//	private static String IMPRIMIBLE_USAURI = ARXIUCAIB_BASE_PROPERTY + "conversio.imprimible.usuari";
//	private static String IMPRIMIBLE_PASS = ARXIUCAIB_BASE_PROPERTY + "conversio.imprimible.contrasenya";


	private String getPropertyConversioOriginalUrlCsv() {
		return getPluginProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "conversio.original.url.csv");
	}

	private String getPropertyConversioOriginalUrlUuid() {
		return getPluginProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "conversio.original.url.uuid");
	}

	private String getPropertyConversioImprimibleUrlCsvConcsv() {
		return getPluginProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "conversio.imprimible.url.csv");
	}

	private String getPropertyConversioImprimibleUrlUuidConcsv() {
		return getPluginProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "conversio.imprimible.url.uuid");
	}

	private String getPropertyConversioImprimibleUsuariConcsv() {
		return getPluginProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "conversio.imprimible.usuari");
	}

	private String getPropertyConversioImprimibleContrasenyaConcsv() {
		return getPluginProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "conversio.imprimible.contrasenya");
	}

	private String getPropertyConcsvBaseUrl() {
		return getPluginProperties().getProperty(ARXIU_BASE_PROPERTY + "csv.base.url");
	}

	private int getPropertyTimeoutConnectConcsv() {

		var timeout = getPluginProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "timeout.connect", JERSEY_TIMEOUT_CONNECT);
		return Integer.parseInt(timeout);
	}

	private int getPropertyTimeoutReadConcsv() {

		var timeout = getPluginProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "timeout.read", JERSEY_TIMEOUT_READ);
		return Integer.parseInt(timeout);
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
			String url = getPropertyConversioImprimibleUrlUuidConcsv();
			String identificador = "00000000-0000-0000-0000-000000000000";
			WebResource webResource = getVersioImprimibleClientConcsv().resource(url.endsWith("/") ? url + identificador : url + "/" + identificador);
			ClientResponse response = webResource.get(ClientResponse.class);
			if (response.getStatus() == 204) {
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
