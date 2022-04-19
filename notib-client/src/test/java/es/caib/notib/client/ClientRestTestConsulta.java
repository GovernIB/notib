/**
 * 
 */
package es.caib.notib.client;

import es.caib.notib.client.domini.RespostaConsultaEstatEnviament;
import es.caib.notib.client.domini.RespostaConsultaEstatNotificacio;
import es.caib.notib.client.domini.RespostaConsultaJustificantEnviament;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertNotNull;

/**
 * Test per al client REST del servei de notificacions de NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientRestTestConsulta {

	
	private static final String URL = "http://localhost:8280/notib";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin";
	private static final boolean BASIC_AUTH = false;
	private static final String CLAU_XIFRAT = "P0rt4FI8";
	
//	private static final String URL = "http://dev.caib.es/notib";
//	private static final String USERNAME = "$ripea_notib";
//	private static final String PASSWORD = "ripea_notib";

	private NotificacioRestClient client;

	@Before
	public void setUp() throws IOException, DecoderException {
		client = NotificacioRestClientFactory.getRestClient(
				URL,
				USERNAME,
				PASSWORD,
				BASIC_AUTH);
	}
	
	@Test
	public void testConsulta() throws DatatypeConfigurationException, IOException, DecoderException, GeneralSecurityException {
		
		// Consulta notificacio
//		RespostaConsultaEstatNotificacio respostaNot = client.consultaEstatNotificacio(xifrarId(172662L));
		RespostaConsultaEstatNotificacio respostaNot = client.consultaEstatNotificacio("8vzkicPP/sQ=");
		assertNotNull(respostaNot);

		// Consulta enviament
		RespostaConsultaEstatEnviament respostaEnv = client.consultaEstatEnviament(xifrarId(172665L));
		assertNotNull(respostaEnv);

	}

	@Test
	public void testConsultaJustificant() throws GeneralSecurityException, IOException {
		RespostaConsultaJustificantEnviament respostaJustif = client.consultaJustificantEnviament(xifrarId(14556L));

		assertNotNull(respostaJustif);
		assertNotNull(respostaJustif.getJustificant());

		Path path = Paths.get("./justificant_generat.pdf");
		Files.write(path, respostaJustif.getJustificant().getContingut());


	}

	protected String xifrarId(Long id) throws GeneralSecurityException {
		// Si el mode test està actiu concatena la data actual a l'identificador de
		// base de dades per a generar l'id de Notifica. Si no ho fessim així es
		// duplicarien els ids de Notifica en cada execució del test i les cridades
		// a Notifica donarien error.
		long idlong = id.longValue();
		byte[] bytes = longToBytes(idlong);
		Cipher cipher = Cipher.getInstance("RC4");
		SecretKeySpec rc4Key = new SecretKeySpec(CLAU_XIFRAT.getBytes(),"RC4");
		cipher.init(Cipher.ENCRYPT_MODE, rc4Key);
		byte[] xifrat = cipher.doFinal(bytes);
		return new String(Base64.encodeBase64(xifrat));
//		return new String(Hex.encodeHex(xifrat));
	}
	protected byte[] longToBytes(long l) {
		byte[] result = new byte[Long.SIZE / Byte.SIZE];
		for (int i = 7; i >= 0; i--) {
			result[i] = (byte)(l & 0xFF);
			l >>= 8;
		}
		return result;
	}
}