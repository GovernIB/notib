package es.caib.notib.plugin.unitat;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.notib.plugin.SistemaExternException;
import es.caib.notib.plugin.gesconadm.RestConsultaProcediments;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;

public class RestConsultaOrganigrama {

	private static final String DIR3_URL = "https://dev.caib.es/dir3caib/";
	private static final String DIR3_USERNAME = "$notib_dir3caib";
	private static final String DIR3_PASSWORD = "notib_dir3caib";
	private static final String SERVEI_ORGANIGRAMA = "rest/organigrama";


	UnitatsOrganitzativesPluginDir3 pluginDir3;

	@Before
	public void setUp() throws Exception {
		Properties properties = new Properties();
		String keystorePath = RestConsultaProcediments.class.getResource("/es/caib/notib/plugin/truststore.jks").toURI().getPath();
		System.setProperty("javax.net.ssl.trustStore", keystorePath);
		System.setProperty("javax.net.ssl.trustStorePassword", "tecnologies");
		properties.put("es.caib.notib.plugin.unitats.dir3.url", DIR3_URL);
		properties.put("es.caib.notib.plugin.unitats.dir3.username", DIR3_USERNAME);
		properties.put("es.caib.notib.plugin.unitats.dir3.password", DIR3_PASSWORD);
		pluginDir3 = new UnitatsOrganitzativesPluginDir3(properties, false);
	}

	@Test
	public void testOrganigramaPerEntitat() throws Exception {
		Map<String, NodeDir3> organigrama = pluginDir3.organigramaPerEntitat("A04003003", null, null);
		assertNotNull(organigrama);
	}

	@Test
	public void testFindAmbPare() throws Exception {
		List<NodeDir3> unitats = pluginDir3.findAmbPare("A04003003", null, null);
		assertNotNull(unitats);
	}

	@Test
	public void testFindAmbodi() throws Exception {
		NodeDir3 unitat = pluginDir3.findAmbCodi("A04003003", null, null);
		assertNotNull(unitat);
	}

	@Test
	public void testPaisos() throws Exception {
		List<CodiValorPais> paisos = pluginDir3.paisos();
		assertNotNull(paisos);
	}

	@Test
	public void testOficinesEntitat() throws Exception {
		List<OficinaSir> oficines = pluginDir3.getOficinesEntitat("A04003003");
		assertNotNull(oficines);
	}

	@Test
	public void testOficinesSIRUnitat() throws Exception {
		List<OficinaSir> oficines = pluginDir3.oficinesSIRUnitat("A04026953", null);
		assertNotNull(oficines);
	}




	
	@Test
	public void obtenirOrganigrama() throws Exception {

		HashMap<String, NodeDir3> organigrama = organigramaPerEntitat("A04003003");

		assertNotNull(organigrama);

	}
		
	private HashMap<String, NodeDir3> organigramaPerEntitat(String codiEntitat) throws SistemaExternException {
		HashMap<String, NodeDir3> organigrama = new HashMap<String, NodeDir3>();
		try {
			URL url = new URL(DIR3_URL + SERVEI_ORGANIGRAMA + "?codigo=" + codiEntitat);
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
	
}
