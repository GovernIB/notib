package es.caib.notib.plugin.gesconadm;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

public class RestConsultaProcediments {

	private static final String ROLSAC_URL = "https://dev.caib.es/rolsac/";
	private static final String ROLSAC_SERVICE_PATH = "api/rest/v1/";
	private static final String ROLSAC_USERNAME = "$notib_rolsac";
	private static final String ROLSAC_PASSWORD = "notib_rolsac";
	private static final Boolean ROLSAC_BASICAUTH = true;

	GestorContingutsAdministratiuPluginRolsac pluginRolsac;

	@Before
	public void setUp() throws Exception {
		Properties properties = new Properties();
		String keystorePath = RestConsultaProcediments.class.getResource("/es/caib/notib/plugin/truststore.jks").toURI().getPath();
		System.setProperty("javax.net.ssl.trustStore", keystorePath);
		System.setProperty("javax.net.ssl.trustStorePassword", "tecnologies");
//		System.setProperty("es.caib.notib.plugin.gesconadm.base.url", "https://dev.caib.es/rolsac");
//		System.setProperty("es.caib.notib.plugin.gesconadm.username", "$notib_rolsac");
//		System.setProperty("es.caib.notib.plugin.gesconadm.password", "notib_rolsac");
		properties.put("es.caib.notib.plugin.gesconadm.base.url", "https://dev.caib.es/rolsac");
		properties.put("es.caib.notib.plugin.gesconadm.username", "$notib_rolsac");
		properties.put("es.caib.notib.plugin.gesconadm.base.url", "https://dev.caib.es/rolsac");
		pluginRolsac = new GestorContingutsAdministratiuPluginRolsac(properties);
	}

//	@Test
	public void obtenirAllProcedimentsRolsac() throws Exception {

		List<GcaProcediment> allProcediments = pluginRolsac.getAllProcediments();

		assertNotNull("La consulta ha retornat null", allProcediments);
		assertTrue("La consulta no ha retornat cap resultat", allProcediments.size() > 0);

	}

	@Test
	public void obtenirProcedimentsRolsac() throws Exception {

		List<GcaProcediment> allProcediments = pluginRolsac.getProcedimentsByUnitat("A04003003", 0);

		assertNotNull(allProcediments);
		assertEquals(allProcediments.size(), 30);

	}

	@Test
	public void countProcedimentsRolsac() throws Exception {

		int countProcediments = pluginRolsac.getTotalProcediments("A04003003");

		assertTrue("La consulta no ha retornat cap resultat", countProcediments > 0);

	}

}
