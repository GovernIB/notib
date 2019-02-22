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
import es.caib.notib.ws.notificacio.NotificacioServiceV2;
import es.caib.notib.ws.notificacio.RespostaAlta;
import es.caib.notib.ws.notificacio.RespostaConsultaEstatEnviament;

/**
 * Test per al client REST del servei de notificacions de NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientSoapTestV2 extends ClientBaseTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private NotificacioServiceV2 client;

	@Before
	public void setUp() throws IOException, InstanceNotFoundException, MalformedObjectNameException, MBeanProxyCreationException, NamingException, CreateException, AuthenticationFailureException {
		client = NotificacioWsClientFactory.getWsClientV2(
				getClass().getResource("/es/caib/notib/client/wsdl/NotificacioServiceWsV2.wsdl"),
				"http://localhost:8081/notib/ws/notificacioV2",
				"admin",
				"admin");
	}

	@Test
	public void test() throws IOException, DecoderException, DatatypeConfigurationException  {
		String notificacioId = new Long(System.currentTimeMillis()).toString();
		RespostaAlta respostaAlta = client.alta(
				generarNotificacioV2(
						notificacioId,
						1,
						false));
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