/**
 * 
 */
package es.caib.notib.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.List;

import javax.ejb.CreateException;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.naming.NamingException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.codec.DecoderException;
import org.jboss.mx.util.MBeanProxyCreationException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import es.caib.loginModule.client.AuthenticationFailureException;
import es.caib.notib.ws.notificacio.EnviamentEstatEnum;
import es.caib.notib.ws.notificacio.EnviamentReferencia;
import es.caib.notib.ws.notificacio.NotificacioService;
import es.caib.notib.ws.notificacio.RespostaAlta;
import es.caib.notib.ws.notificacio.RespostaConsultaEstatEnviament;

/**
 * Test per al client REST del servei de notificacions de NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientSoapTest extends ClientBaseTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private NotificacioService client;

	@Before
	public void setUp() throws IOException, InstanceNotFoundException, MalformedObjectNameException, MBeanProxyCreationException, NamingException, CreateException, AuthenticationFailureException {
		client = NotificacioWsClientFactory.getWsClient(
				getClass().getResource("/es/caib/notib/client/wsdl/NotificacioServiceWs.wsdl"),
				"http://localhost:8080/notib/ws/notificacio",
				"notapp",
				"notapp");
	}

	@Test
	public void test() throws IOException, DecoderException, DatatypeConfigurationException  {
		String notificacioId = new Long(System.currentTimeMillis()).toString();
		RespostaAlta respostaAlta = client.alta(
				generarNotificacio(
						notificacioId,
						1,
						true));
		assertNotNull(respostaAlta);
		assertFalse(respostaAlta.isError());
		assertNull(respostaAlta.getErrorDescripcio());
		assertNotNull(respostaAlta.getReferencies());
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertEquals(1, referencies.size());
		assertNotNull(referencies.get(0).getReferencia());
		RespostaConsultaEstatEnviament respostaConsultaEstatEnviament = client.consultaEstatEnviament(referencies.get(0).getReferencia());
		assertNotNull(respostaConsultaEstatEnviament);
		assertFalse(respostaConsultaEstatEnviament.isError());
		assertNull(respostaConsultaEstatEnviament.getErrorDescripcio());
		assertEquals(
				EnviamentEstatEnum.NOTIB_ENVIADA,
				respostaConsultaEstatEnviament.getEstat());
	}

}
