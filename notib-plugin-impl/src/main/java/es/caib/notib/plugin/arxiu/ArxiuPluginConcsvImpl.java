package es.caib.notib.plugin.arxiu;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.DocumentNode;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.ResParamSearchDocument;
import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.VersionNode;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamNodeId;
import es.caib.arxiudigital.apirest.CSGD.entidades.parametrosLlamada.ParamSearch;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GetDocVersionListResult;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.SearchDocsResult;
import es.caib.arxiudigital.apirest.CSGD.peticiones.GetDocVersionList;
import es.caib.arxiudigital.apirest.CSGD.peticiones.SearchDocs;
import es.caib.notib.plugin.utils.PropertiesHelper;
import es.caib.plugins.arxiu.api.ArxiuException;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.ContingutTipus;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.IArxiuPlugin;
import es.caib.plugins.arxiu.caib.ArxiuCaibClient;
import es.caib.plugins.arxiu.caib.ArxiuConversioHelper;
import es.caib.plugins.arxiu.caib.ArxiuPluginCaib;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

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
			return documentDetallsCsv(identificador, versio, ambContingut);
		} else {
			identificador = identificador.replace("uuid:", "");
			return super.documentDetalls(identificador, versio, ambContingut);
		}
	}

	private Document documentDetallsCsv(String identificador, String versio, boolean ambContingut) {
		String metode = "/services/documentSearch";

		try {
			List<ContingutArxiu> resultatConsulta = new ArrayList();
			List continguts = null;

			final String query = getPropertyQueryCsv().replace("*IDF*", identificador);
			SearchDocsResult resposta = (SearchDocsResult)this.getArxiuClient().generarEnviarPeticio(metode, SearchDocs.class, new ArxiuCaibClient.GeneradorParam<ParamSearch>() {
				public ParamSearch generar() {
					ParamSearch param = new ParamSearch();
					param.setQuery(query);
					param.setPageNumber(0);
					return param;
				}
			}, ParamSearch.class, SearchDocsResult.class);
			List<DocumentNode> documents = new ArrayList();
			if (resposta.getSearchDocumentsResult().getResParam() != null && ((ResParamSearchDocument)resposta.getSearchDocumentsResult().getResParam()).getDocuments() != null) {
				documents = ((ResParamSearchDocument)resposta.getSearchDocumentsResult().getResParam()).getDocuments();
			}

			if (documents == null || documents.isEmpty())
				return null;

			String versioResposta = null;
			if (versio == null) {
				versioResposta = this.documentDarreraVersio(documents.get(0).getId());
			} else {
				versioResposta = versio;
			}

			return ArxiuConversioHelper.documentNodeToDocument(documents.get(0), versioResposta);
		} catch (ArxiuException var11) {
			throw var11;
		} catch (Exception var12) {
			throw new ArxiuException("S'ha produit un error cridant el mètode " + metode, var12);
		}
	}

	private String documentDarreraVersio(String identificador) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, UniformInterfaceException, ClientHandlerException, IOException {
		String darreraVersio = null;
		List<ContingutArxiu> versions = this.documentVersionsComu(identificador);
		if (versions != null && !versions.isEmpty()) {
			darreraVersio = ((ContingutArxiu)versions.get(versions.size() - 1)).getVersio();
		}

		return darreraVersio;
	}

	private List<ContingutArxiu> documentVersionsComu(final String identificador) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, UniformInterfaceException, ClientHandlerException, IOException {
		String metode = "/services/getDocVersionList";
		GetDocVersionListResult resposta = (GetDocVersionListResult)this.getArxiuClient().generarEnviarPeticio(metode, GetDocVersionList.class, new ArxiuCaibClient.GeneradorParam<ParamNodeId>() {
			public ParamNodeId generar() {
				ParamNodeId param = new ParamNodeId();
				param.setNodeId(identificador);
				return param;
			}
		}, ParamNodeId.class, GetDocVersionListResult.class);
		List<VersionNode> versions = resposta.getGetDocVersionListResult().getResParam();
		Collections.sort(versions, new Comparator<VersionNode>() {
			public int compare(VersionNode vn1, VersionNode vn2) {
				return vn1.getDate().compareTo(vn2.getDate());
			}
		});
		List<ContingutArxiu> continguts = new ArrayList();
		Iterator var7 = versions.iterator();

		while(var7.hasNext()) {
			VersionNode versio = (VersionNode)var7.next();
			continguts.add(ArxiuConversioHelper.crearContingutArxiu(identificador, (String)null, ContingutTipus.DOCUMENT, String.valueOf(versio.getId())));
		}

		return continguts;
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
			throw new ArxiuException(
					"S'ha produit un error generant la versió imprimible del document",
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
			throw new ArxiuException(
					"S'ha produit un error generant la versió imprimible del document",
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
		return PropertiesHelper.getProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "base.url");
	}
	private String getPropertyAplicacioCodi() {
		return PropertiesHelper.getProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "aplicacio.codi");
	}
	private String getPropertyUsuari() {
		return PropertiesHelper.getProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "usuari");
	}
	private String getPropertyContrasenya() {
		return PropertiesHelper.getProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "contrasenya");
	}
	private String getPropertyConversioImprimibleUrlCsv() {
		return PropertiesHelper.getProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "conversio.imprimible.url.csv");
	}
	private String getPropertyConversioImprimibleUrlUuid() {
		return PropertiesHelper.getProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "conversio.imprimible.url.uuid");
	}
	private String getPropertyConversioImprimibleUsuari() {
		return PropertiesHelper.getProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "conversio.imprimible.usuari");
	}
	private String getPropertyConversioImprimibleContrasenya() {
		return PropertiesHelper.getProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "conversio.imprimible.contrasenya");
	}
	private String getPropertyQueryCsv() {
		String query = PropertiesHelper.getProperties().getProperty(ARXIUCAIB_BASE_PROPERTY + "query.csv");
		if (query == null || query.isEmpty())
			query = "(+TYPE:\"eni:documento\" AND @eni\\:csv:\"*IDF*\" -ASPECT:\"gdib:borrador\" -ASPECT:\"gdib:trasladado\") " +
					" OR (+TYPE:\"gdib:documentoMigrado\" AND @gdib\\:hash:\"*IDF*\") ";
		return query;
	}

	private int getPropertyTimeoutConnect() {
		String timeout = PropertiesHelper.getProperties().getProperty(
				ARXIUCAIB_BASE_PROPERTY + "timeout.connect",
				JERSEY_TIMEOUT_CONNECT);
		return Integer.parseInt(timeout);
	}
	private int getPropertyTimeoutRead() {
		String timeout = PropertiesHelper.getProperties().getProperty(
				ARXIUCAIB_BASE_PROPERTY + "timeout.read",
				JERSEY_TIMEOUT_READ);
		return Integer.parseInt(timeout);
	}

}
