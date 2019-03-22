package es.caib.notib.plugin.arxiu;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.io.IOUtils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import es.caib.notib.plugin.utils.PropertiesHelper;
import es.caib.plugins.arxiu.api.ArxiuException;
import es.caib.plugins.arxiu.api.Carpeta;
import es.caib.plugins.arxiu.api.ConsultaFiltre;
import es.caib.plugins.arxiu.api.ConsultaResultat;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.DocumentRepositori;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.plugins.arxiu.api.IArxiuPlugin;

public class ArxiuPluginConcsvImpl implements IArxiuPlugin {

	
	public static final String ARXIU_BASE_PROPERTY = "es.caib.notib.plugin.arxiu.";

	private static final String ARXIUCAIB_BASE_PROPERTY = ARXIU_BASE_PROPERTY + "caib.";
	private static final int NUM_PAGINES_RESULTAT_CERCA = 100;
	private static final String VERSIO_INICIAL_CONTINGUT = "1.0";
	private static final String JERSEY_TIMEOUT_CONNECT = "10000";
	private static final String JERSEY_TIMEOUT_READ = "60000";
	private Client versioImprimibleClient;

	public DocumentContingut documentImprimibleCsv(
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
	
	
	public DocumentContingut documentImprimibleUuid(
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

	private XMLGregorianCalendar toXmlGregorianCalendar(Date date) throws DatatypeConfigurationException {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(date);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
	}

	@Override
	public ContingutArxiu carpetaCopiar(String arg0, String arg1) throws ArxiuException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContingutArxiu carpetaCrear(Carpeta arg0, String arg1) throws ArxiuException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Carpeta carpetaDetalls(String arg0) throws ArxiuException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void carpetaEsborrar(String arg0) throws ArxiuException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ContingutArxiu carpetaModificar(Carpeta arg0) throws ArxiuException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void carpetaMoure(String arg0, String arg1) throws ArxiuException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ConsultaResultat documentConsulta(List<ConsultaFiltre> arg0, Integer arg1, Integer arg2,
			DocumentRepositori arg3) throws ArxiuException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContingutArxiu documentCopiar(String arg0, String arg1) throws ArxiuException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContingutArxiu documentCrear(Document arg0, String arg1) throws ArxiuException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document documentDetalls(String arg0, String arg1, boolean arg2) throws ArxiuException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void documentEsborrar(String arg0) throws ArxiuException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String documentExportarEni(String arg0) throws ArxiuException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DocumentContingut documentImprimible(String identificador) throws ArxiuException {
		if(identificador.contains("uuid:")) {
			identificador = identificador.replace("uuid:", "");
			return documentImprimibleCsv(identificador);
		}else {
			identificador = identificador.replace("csv:", "");
			return documentImprimibleUuid(identificador);
		}
	}

	@Override
	public ContingutArxiu documentModificar(Document arg0) throws ArxiuException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void documentMoure(String arg0, String arg1) throws ArxiuException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<ContingutArxiu> documentVersions(String arg0) throws ArxiuException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConsultaResultat expedientConsulta(List<ConsultaFiltre> arg0, Integer arg1, Integer arg2)
			throws ArxiuException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContingutArxiu expedientCrear(Expedient arg0) throws ArxiuException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContingutArxiu expedientCrearSubExpedient(Expedient arg0, String arg1) throws ArxiuException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expedient expedientDetalls(String arg0, String arg1) throws ArxiuException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void expedientEsborrar(String arg0) throws ArxiuException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String expedientExportarEni(String arg0) throws ArxiuException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContingutArxiu expedientModificar(Expedient arg0) throws ArxiuException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void expedientReobrir(String arg0) throws ArxiuException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void expedientTancar(String arg0) throws ArxiuException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<ContingutArxiu> expedientVersions(String arg0) throws ArxiuException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean generaIdentificadorNti() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean suportaMetadadesNti() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean suportaVersionatCarpeta() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean suportaVersionatDocument() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean suportaVersionatExpedient() {
		// TODO Auto-generated method stub
		return false;
	}

}
