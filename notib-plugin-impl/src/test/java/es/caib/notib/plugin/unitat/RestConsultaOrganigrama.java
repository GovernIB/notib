package es.caib.notib.plugin.unitat;

import static org.junit.Assert.assertNotNull;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.notib.plugin.SistemaExternException;

public class RestConsultaOrganigrama {

	private static final String DIR3_URL = "https://dev.caib.es/dir3caib/";
	private static final String SERVEI_ORGANIGRAMA = "rest/organigrama";
	
	
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
