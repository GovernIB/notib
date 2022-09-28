package es.caib.notib.plugin.arxiu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import es.caib.plugins.arxiu.api.*;
import es.caib.plugins.arxiu.caib.ArxiuCaibClient;
import es.caib.plugins.arxiu.caib.ArxiuPluginCaib;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class ArxiuPluginConcsvImpl extends ArxiuPluginCaib implements IArxiuPlugin {

	
	public static final String ARXIU_BASE_PROPERTY = "es.caib.notib.plugin.arxiu.";

	private static final String ARXIUCAIB_BASE_PROPERTY = ARXIU_BASE_PROPERTY + "caib.";
	private static final String JERSEY_TIMEOUT_CONNECT = "10000";
	private static final String JERSEY_TIMEOUT_READ = "60000";
	private Client versioImprimibleClient;

	private ArxiuCaibClient arxiuClient;

	public ArxiuPluginConcsvImpl(String propertyKeyBase) {
		super(propertyKeyBase);
	}

	public ArxiuPluginConcsvImpl(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}

	@Override
	public Document documentDetalls(String identificador, String versio, boolean ambContingut) throws ArxiuException {
		if (identificador.contains("csv:")) {
			identificador = identificador.replace("csv:", "");
			return documentDetallsCsv(identificador, ambContingut);
		} else {
			identificador = identificador.replace("uuid:", "");
			return documentDetallsUuid(identificador, ambContingut);
		}
	}

	private Document documentDetallsCsv(String identificador, boolean ambContingut) {
		try {
			Document response = new Document();
			if (ambContingut)
				response.setContingut(documentImprimibleCsv(identificador));

			try {
				Map<String,Object> result = documentMetadadesCsv(identificador);
				response.setMetadades(toDocumentMetadades(result));
				if (result.containsKey("eni:tipoFirma"))
					response.setFirmes(Collections.singletonList(new Firma()));
				else
					response.setFirmes(null);
			} catch(Exception e) {
				log.debug("No ha estat possible obtenir les metadades del document amb CSV " + identificador);
				response.setMetadades(null);
			}
			
			return response;
		} catch (Exception var12) {
			throw new ArxiuException("S'ha produit un error obtenent els detalls del document: " + identificador, var12);
		}
	}

	private Document documentDetallsUuid(String identificador, boolean ambContingut) {
		try {
			Document response = new Document();
			if (ambContingut)
				response.setContingut(documentImprimibleUuid(identificador));

			try {
				Map<String,Object> result = documentMetadadesUuid(identificador);
				response.setMetadades(toDocumentMetadades(result));
				if (result.containsKey("eni:tipoFirma"))
					response.setFirmes(Collections.singletonList(new Firma()));
				else
					response.setFirmes(null);
			}
			catch(Exception e) {
				log.debug("No ha estat possible obtenir les metadades del document amb UUID " + identificador);
				response.setMetadades(null);
			}

			return response;
		} catch (Exception var12) {
			throw new ArxiuException("S'ha produit un error obtenent els detalls del document: " + identificador, var12);
		}
	}

	@Override
	public DocumentContingut documentImprimible(String identificador) throws ArxiuException {
		if(identificador.contains("uuid:")) {
			identificador = identificador.replace("uuid:", "");
			return documentImprimibleUuid(identificador);
		}else {
			identificador = identificador.replace("csv:", "");
			return documentImprimibleCsv(identificador);
		}
	}

	private DocumentContingut documentImprimibleCsv(
			final String identificador) throws ArxiuException {
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
			InputStream is = generarVersioImprimibleCsv(
					identificador,
					null, // metadada 1
					null, // metadada 2
					null); // marca d'aigua
			DocumentContingut contingut = new DocumentContingut();
			contingut.setArxiuNom("versio_imprimible.pdf");
			contingut.setTipusMime("application/pdf");
			contingut.setContingut(IOUtils.toByteArray(is));
			contingut.setTamany(contingut.getContingut().length);
			return contingut;
		} catch (Exception ex) {
			log.debug("S'ha produit un error generant la versió imprimible del document amb CSV " + identificador,
					ex);
			throw new ArxiuException(
					"S'ha produit un error generant la versió imprimible del document amb CSV " + identificador,
					ex);
		}
		
	}
	
	
	private DocumentContingut documentImprimibleUuid(
			final String identificador) throws ArxiuException {
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
			InputStream is = generarVersioImprimibleUuid(
					identificador,
					null, // metadada 1
					null, // metadada 2
					null); // marca d'aigua
			DocumentContingut contingut = new DocumentContingut();
			contingut.setArxiuNom("versio_imprimible.pdf");
			contingut.setTipusMime("application/pdf");
			contingut.setContingut(IOUtils.toByteArray(is));
			contingut.setTamany(contingut.getContingut().length);
			return contingut;
		} catch (Exception ex) {
			log.debug("S'ha produit un error generant la versió imprimible del document amb UUID " + identificador,
					ex);
			throw new ArxiuException(
					"S'ha produit un error generant la versió imprimible del document amb UUID " + identificador,
					ex);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String,Object> documentMetadadesUuid(String identificador) {
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
			url += "rest/metadata/uuid/";

			WebResource webResource = getVersioImprimibleClient().
					resource(url + identificador);
			String jsonData = webResource.accept(MediaType.APPLICATION_JSON).get(String.class);
			return new ObjectMapper().readValue(jsonData, HashMap.class);

		} catch (Exception ex) {
			log.debug("No ha estat possible obtenir les metadades del document amb UUID " + identificador,
					ex);
			throw new ArxiuException(
					"No ha estat possible obtenir les metadades del document amb UUID " + identificador,
					ex);
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
			url += "rest/metadata/";

			WebResource webResource = getVersioImprimibleClient().
					resource(url + identificador);
			String jsonData = webResource.accept(MediaType.APPLICATION_JSON).get(String.class);
			return new ObjectMapper().readValue(jsonData, HashMap.class);

		} catch (Exception ex) {
			log.error("No ha estat possible obtenir les metadades del document amb CSV " + identificador, ex);
			throw new ArxiuException(
					"No ha estat possible obtenir les metadades del document amb CSV " + identificador,
					ex);
		}
	}

	private InputStream generarVersioImprimibleCsv(
			String identificador,
			String metadada1,
			String metadada2,
			String marcaAigua) throws IOException {
		String url = getPropertyConversioImprimibleUrlCsv();
		WebResource webResource;
		if (url.endsWith("/")) {
			webResource = getVersioImprimibleClient().
					resource(url + identificador);
		} else {
			webResource = getVersioImprimibleClient().
					resource(url + "/" + identificador);
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
	
	private InputStream generarVersioImprimibleUuid(
			String identificador,
			String metadada1,
			String metadada2,
			String marcaAigua) throws IOException {
		String url = getPropertyConversioImprimibleUrlUuid();
		WebResource webResource;
		if (url.endsWith("/")) {
			webResource = getVersioImprimibleClient().
					resource(url + identificador);
		} else {
			webResource = getVersioImprimibleClient().
					resource(url + "/" + identificador);
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

		for (String key : metadatas.keySet()) {
			Object value = metadatas.get(key);
			if (value != null) {
				if ("eni:id".equals(key)) {
					metadades.setIdentificador(value.toString());
				} else if ("eni:v_nti".equals(key)) {
					metadades.setVersioNti(value.toString());
				} else if ("eni:origen".equals(key)) {
					metadades.setOrigen(ContingutOrigen.toEnum(String.valueOf(value)));
				} else if ("eni:fecha_inicio".equals(key)) {
					metadades.setDataCaptura(parseDateIso8601(value.toString()));
				} else if ("eni:estado_elaboracion".equals(key)) {
					metadades.setEstatElaboracio(DocumentEstatElaboracio.toEnum(value.toString()));
				} else if ("eni:tipo_doc_ENI".equals(key)) {
					metadades.setTipusDocumental(DocumentTipus.toEnum(value.toString()));
				} else {
					Object val;
					if ("eni:organo".equals(key)) {
						val = value;
						if (val instanceof List) {
							metadades.setOrgans((List<String>) value);
						} else {
							metadades.setOrgans(Arrays.asList((String) val));
						}
					} else if ("eni:nombre_formato".equals(key)) {
						metadades.setFormat(DocumentFormat.toEnum(value.toString()));
					} else if ("eni:extension_formato".equals(key)) {
						metadades.setExtensio(DocumentExtensio.toEnum(value.toString()));
					} else {
						Map<String, Object> metadadesAddicionals = metadades.getMetadadesAddicionals();
						if (metadadesAddicionals == null) {
							metadadesAddicionals = new HashMap<String, Object>();
							metadades.setMetadadesAddicionals(metadadesAddicionals);
						}
						metadadesAddicionals.put(key, value);
					}
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
	private Client getVersioImprimibleClient() {
		if (versioImprimibleClient == null) {
			versioImprimibleClient = Client.create();
			versioImprimibleClient.setConnectTimeout(
					getPropertyTimeoutConnect());
			versioImprimibleClient.setReadTimeout(
					getPropertyTimeoutRead());
			String usuari = getPropertyConversioImprimibleUsuari();
			String contrasenya = getPropertyConversioImprimibleContrasenya();
			if (usuari != null) {
				versioImprimibleClient.addFilter(
						new HTTPBasicAuthFilter(usuari, contrasenya));
			}
		}
		return versioImprimibleClient;
	}

	private ArxiuCaibClient getArxiuClient() {
		if (this.arxiuClient == null) {
			this.arxiuClient = new ArxiuCaibClient(
					this.getPropertyBaseUrl(),
					this.getPropertyAplicacioCodi(),
					this.getPropertyUsuari(),
					this.getPropertyContrasenya(),
					this.getPropertyTimeoutConnect(),
					this.getPropertyTimeoutRead(),
					false);
		}

		return this.arxiuClient;
	}

	private String getPropertyBaseUrl() {
		return getPluginProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "base.url");
	}
	private String getPropertyAplicacioCodi() {
		return getPluginProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "aplicacio.codi");
	}
	private String getPropertyUsuari() {
		return getPluginProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "usuari");
	}
	private String getPropertyContrasenya() {
		return getPluginProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "contrasenya");
	}
	private String getPropertyConversioImprimibleUrlCsv() {
		return getPluginProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "conversio.imprimible.url.csv");
	}
	private String getPropertyConversioImprimibleUrlUuid() {
		return getPluginProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "conversio.imprimible.url.uuid");
	}
	private String getPropertyConversioImprimibleUsuari() {
		return getPluginProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "conversio.imprimible.usuari");
	}
	private String getPropertyConversioImprimibleContrasenya() {
		return getPluginProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "conversio.imprimible.contrasenya");
	}
	private String getPropertyConcsvBaseUrl() {
		return getPluginProperties().getProperty(ARXIU_BASE_PROPERTY + "csv.base.url");
	}

	private int getPropertyTimeoutConnect() {
		String timeout = getPluginProperties().getProperty(
				ARXIUCAIB_BASE_PROPERTY + "timeout.connect",
				JERSEY_TIMEOUT_CONNECT);
		return Integer.parseInt(timeout);
	}
	private int getPropertyTimeoutRead() {
		String timeout = getPluginProperties().getProperty(
				ARXIUCAIB_BASE_PROPERTY + "timeout.read",
				JERSEY_TIMEOUT_READ);
		return Integer.parseInt(timeout);
	}

}
