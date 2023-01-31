/**
 * 
 */
package es.caib.notib.client;

import es.caib.notib.client.domini.*;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Test per al client REST del servei de notificacions de NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("unchecked")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NotificaWsTestIntegracioRest {

//	private static final String ENTITAT_DIR3CODI = "A04013511";
//	private static final String ENTITAT_DIR3CODI = "A04003003";
	private static final String ENTITAT_DIR3CODI = "EA0004518";
//	@Autowired
//	private NotificaV2Helper notificaHelper;
//	@Autowired
//	private PluginHelper pluginHelper;
//	
//	private EntitatDto entitat;
//	private PermisDto permisAplicacio;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@SuppressWarnings("deprecation")
	private static NotificacioRestClient client;
	
	

	@BeforeClass
	public static void setUpClass() throws IOException, DecoderException {
//		client = NotificacioRestClientFactory.getRestClient(
//				"https://dev.caib.es/notib",
//				"$ripea_notib",
//				"ripea_notib");
		client = NotificacioRestClientFactory.getRestClient(
				"http://localhost:8081/notib",
				"usuari2",
				"usuari2");
	}
	
	@Before
	public void setUp() throws IOException, DecoderException {
//		es.caib.notib.core.helper.PropertiesHelper.getProperties("classpath:es/caib/notib/core/test.properties");
//		es.caib.notib.plugin.utils.PropertiesHelper.getProperties("classpath:es/caib/notib/core/test.properties");
//		notificaHelper.setModeTest(true);
//		entitat = new EntitatDto();
//		entitat.setCodi("DGTIC");
//		entitat.setNom("Dirección General de Desarrollo Tecnológico");
//		entitat.setDescripcio("Descripció Dirección General de Desarrollo Tecnológico");
//		entitat.setTipus(EntitatTipusEnumDto.GOVERN);
//		entitat.setDir3Codi(ENTITAT_DIR3CODI);
//		entitat.setActiva(true);
//		permisAplicacio = new PermisDto();
//		permisAplicacio.setAplicacio(true);
//		permisAplicacio.setTipus(TipusEnumDto.USUARI);
//		permisAplicacio.setPrincipal("apl");
	}
	
	// PRUEBAS DE EMISIÓN – CORRECTAS
	// =====================================================================================
	/* TESTS PRUEBAS EMISION 1 A 8 MOVIDAS A ClientRestTest	 */
	
	// PRUEBAS DE EMISIÓN – ERRÓNEAS
	// =====================================================================================
	
	// ORGANISMO EMISOR DESCONOCIDO.
	// -------------------------------------------------------------------------------------
	// Se enviará una petición con un organismo emisor cuyo código
	// DIR3 no se encuentre en BBDD.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4011. Organismo emisor no dado de alta

//	@Test	// Resultat test: OK
	public void pruebaEmision08() throws Exception {
		
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = false;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioV2 notificacio = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		notificacio.setEmisorDir3Codi("A00000000");
		
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getErrorDescripcio(),
				respostaAlta.isError(),
				is(false));
		assertNotNull(respostaAlta);
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		//Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertThat(
				respostaInfo.getErrorDescripcio(),
				respostaInfo.isError(),
				is(false));
		assertNotNull(respostaInfo);
		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertThat(
					info.isError(),
					is(false));
			assertThat(
					info.getErrorDescripcio(),
					anyOf(
							containsString("[4011]"),
							containsString("[ORGANISMO_NO_RECONOCIDO]")));
		}
		
	}
	// PRUEBAS DE EMISIÓN – ERRÓNEAS
	// =====================================================================================

	// [ENTITAT] No s'ha trobat cap entitat configurada a Notib amb el codi Dir3
	// A00000000. (emisorDir3Codi)
	// -------------------------------------------------------------------------------------
	// Se enviará una petición con un organismo emisor cuyo código
	// DIR3 no se encuentre en BBDD.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4011. Organismo emisor no dado de alta

//	@Test // Resultat test: OK
	public void pruebaEmision08_1() throws Exception {

		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = false;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;

		NotificacioV2 notificacio = generaNotificacio(numDestinataris, numEnviaments, ambEnviamentPostal,
				tipusEnviamentPostal, ambEnviamentDEH, ambEnviamentDEHObligat, ambRetard);

		notificacio.setEmisorDir3Codi("A00000000");

		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getErrorDescripcio(), 
				anyOf(
						containsString("[ENTITAT]"), 
						containsString("No s'ha trobat cap entitat configurada a Notib amb el codi Dir3 A00000000. (emisorDir3Codi)")));
	}

	// PETICIÓN CON EL VALOR DE PDF NORMALIZADO VACÍO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el valor del campo normalizado está sin rellenar.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4151. El campo normalizado está vacío
	
//	@Test	// Resultat test: Document no permet el camp normalitzat buid. (OK)
	public void pruebaEmision09() throws Exception {
		
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = false;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioV2 notificacio = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
//		camp normalitzat a NULL
//		notificacio.getDocument.setNormalitzat(null);
		
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getErrorDescripcio(),
				respostaAlta.isError(),
				is(false));
		assertNotNull(respostaAlta);
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		//Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertThat(
				respostaInfo.getErrorDescripcio(),
				respostaInfo.isError(),
				is(false));
		assertNotNull(respostaInfo);
		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertThat(
					info.isError(),
					is(false));
			assertThat(
					info.getErrorDescripcio(),
					containsString("[4151]"));
		}
	}
	
	
	// PETICIÓN CON UN VALOR DE PDF NORMALIZADO INCORRECTO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el valor del campo normalizado no es ni si ni no
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4152. NotificaHelper únicament permet els valors "true" i "false".
	

//	@Test // Resultat test: OK
	public void pruebaEmision10() throws Exception {
		
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = false;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioV2 notificacio = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
//		valor del camp normalitzat incorrecte (diferent de "true" o "false")
//		notificacio.getDocument.setNormalitzat("yes");
		
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getErrorDescripcio(),
				respostaAlta.isError(),
				is(false));
		assertNotNull(respostaAlta);
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		//Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertThat(
				respostaInfo.getErrorDescripcio(),
				respostaInfo.isError(),
				is(false));
		assertNotNull(respostaInfo);
		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertThat(
					info.isError(),
					is(false));
			assertThat(
					info.getErrorDescripcio(),
					containsString("[4151]"));
		}
	}
	
	
	// PETICIÓN CON NIF DEL TITULAR INCORRECTO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el NIF que se proporciona del titular es incorrecto
	// -------------------------------------------------------------------------------------
	// Resultat test: El registre no permet un Nif no vàlid. No es pot enviar una notificació a Notific@ amb nif no vàlid. 
	
//	@Test	// Resultat test: OK
	public void pruebaEmision11() throws Exception {
		
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = false;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioV2 notificacio = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Nif del titular no vàlid
		Enviament env = notificacio.getEnviaments().get(0);
		env.getTitular().setNif("00000000A");
		
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getErrorDescripcio(),
				respostaAlta.isError(),
				is(false));
	}
	
	// PETICIÓN CON NOMBRE DEL TITULAR VACÍO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el campo nombre del titular está vacío.
	// -------------------------------------------------------------------------------------
	// Resultat test: NotificacioServiceWsImplV2 valida que el titular/destinataris no siguin null (OK)
	
//	@Test	// Resultat test: OK
	public void pruebaEmision12() throws Exception {
		
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = false;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioV2 notificacio = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Nom del titular buid
		Enviament env = notificacio.getEnviaments().get(0);
		env.getTitular().setNom(null);
		env.getTitular().setLlinatge1(null);
		env.getTitular().setLlinatge2(null);
		
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getErrorDescripcio(),
				respostaAlta.isError(),
				is(false));
	}
	
	
	// PETICIÓN SIN DOCUMENTO. 
	// -------------------------------------------------------------------------------------
	// Se enviará una petición sin documento
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4311. Documento no puede estar vacío
	
//	@Test	// Resultat test: OK
	public void pruebaEmision13() throws Exception {
		
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = false;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioV2 notificacio = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Document buid
		notificacio.getDocument().setContingutBase64(null);
		
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getErrorDescripcio(),
				respostaAlta.isError(),
				is(false));
		assertNotNull(respostaAlta);
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		//Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertThat(
				respostaInfo.getErrorDescripcio(),
				respostaInfo.isError(),
				is(false));
		assertNotNull(respostaInfo);
		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertThat(
					info.isError(),
					is(false));
			assertThat(
					info.getErrorDescripcio(),
					containsString("[4311]"));
		}
	}
	
	
	// PETICIÓN TIPO DE ENVIO VACIO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el tipo de envio es vacio.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4320. No se corresponde el sha1 del documento con el contenido
	
//	@Test	// Resultat test: [ENVIAMENT_TIPUS] El tipus d'enviament de la notificació no pot ser null. (OK)
	public void pruebaEmision14() throws Exception {
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = false;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioV2 notificacio = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Nom del titular buid
		notificacio.setEnviamentTipus(null);
		
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getErrorDescripcio(),
				respostaAlta.isError(),
				is(false));
	}
	
	
	// PETICIÓN CON UN VALOR PRIORIDAD DE SERVICIO INCORRECTO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el valor de servicio no es ni normal ni urgente.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4402. El campo Servicio solo puede contener los valores 'urgente' o 'normal'
	
//	@Test	// Resultat test: ServeiTipus únicament pot ser null, NORMAL o URGENT, i el camp no existeix a la v.2 (BAD REQUEST). (OK)
	public void pruebaEmision15() throws Exception {
		
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = false;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioV2 notificacio = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Tipus de servei no vàlid 
//		Enviament env = (Enviament)notificacio.getEnviaments().get(0);
//		env.setServeiTipus(NotificaServeiTipusEnumDto.ALTRE);
		
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.isError(),
				is(false));
		assertNotNull(respostaAlta);
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		//Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertThat(
				respostaInfo.isError(),
				is(false));
		assertNotNull(respostaInfo);
		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertThat(
					info.isError(),
					is(false));
			assertThat(
					info.getErrorDescripcio(),
					containsString("[4402]"));
		}
	}
	
	
	// PETICIÓN CON EL VALOR DE PRIORIDAD DE SERVICIO VACÍO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el valor del campo servicio está vacío.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4403. Debe rellenar el campo 'servicio'
	
//	@Test	// Resultat test: [SERVEI_TIPUS] El camp 'serveiTipus' d'un enviament no pot ser null. (OK)
	public void pruebaEmision16() throws Exception {
		
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = false;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioV2 notificacio = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Tipus de servei buid
		Enviament env = notificacio.getEnviaments().get(0);
		env.setServeiTipus(null);
		
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getErrorDescripcio(),
				respostaAlta.isError(),
				is(false));
		assertNotNull(respostaAlta);
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		//Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertThat(
				respostaInfo.getErrorDescripcio(),
				respostaInfo.isError(),
				is(false));
		assertNotNull(respostaInfo);
		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertThat(
					info.isError(),
					is(false));
			assertThat(
					info.getErrorDescripcio(),
					containsString("[4403]"));
		}
	}
	
	
	// PETICIÓN CON CONCEPTO VACÍO.
	// -------------------------------------------------------------------------------------
	// Se enviará una petición con el campo concepto vacío.
	// -------------------------------------------------------------------------------------
	// Prova impossible: NotificacioServiceWsImplV2 valida que el camp 'concepte' no sigui null.
	
//	@Test	// Resultat test: [CONCEPTE] El concepte de la notificació no pot ser null. (OK)
	public void pruebaEmision17() throws Exception {
		
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = false;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioV2 notificacio = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Concepte buit
		notificacio.setConcepte(null);
		
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getErrorDescripcio(),
				respostaAlta.isError(),
				is(false));
		assertNotNull(respostaAlta);
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		//Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertThat(
				respostaInfo.getErrorDescripcio(),
				respostaInfo.isError(),
				is(false));
		assertNotNull(respostaInfo);
		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertThat(
					info.isError(),
					is(false));
			assertThat(
					info.getErrorDescripcio(),
					containsString("[4500]"));
		}
	}
	
	// PETICIÓN CON TIPO DE ENVÍO VACÍO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el tipo de envío está vacío.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4510. Tipo de envío vacío
	
//	@Test	// Resultat test: [ENVIAMENT_TIPUS] El tipus d'enviament de la notificació no pot ser null. (OK)
	public void pruebaEmision18() throws Exception {
		
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = false;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioV2 notificacio = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);

		// Enviament tipus buid
		notificacio.setEnviamentTipus(null);
		
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getErrorDescripcio(),
				respostaAlta.isError(),
				is(false));
		assertNotNull(respostaAlta);
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		//Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertThat(
				respostaInfo.getErrorDescripcio(),
				respostaInfo.isError(),
				is(false));
		assertNotNull(respostaInfo);
		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertThat(
					info.isError(),
					is(false));
			assertThat(
					info.getErrorDescripcio(),
					containsString("[4510]"));
		}
	}
	
	
	// PETICIÓN CON TIPO DE ENVÍO INCORRECTO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el tipo de envío no es ni notificación ni comunicación.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4511. Tipo de envío no permitido
	
//	@Test	// Resultat test: Tipus enviament únicament pot ser COMUNICACIO o NOTIFICACIO en cas contrari: BAD REQUEST. (OK)
	public void pruebaEmision19() throws Exception {
		
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = false;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioV2 notificacio = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Enviament tipus no vàlid
//		notificacio.setEnviamentTipus(NotificaEnviamentTipusEnumDto.ALTRE);
		
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getErrorDescripcio(),
				respostaAlta.isError(),
				is(false));
		assertNotNull(respostaAlta);
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		//Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertThat(
				respostaInfo.getErrorDescripcio(),
				respostaInfo.isError(),
				is(false));
		assertNotNull(respostaInfo);
		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertThat(
					info.isError(),
					is(false));
			assertThat(
					info.getErrorDescripcio(),
					containsString("[4511]"));
		}
	}
	
	
	// PETICIÓN CON CÓDIGO SIA ERRÓNEO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el Código SIA enviado no es correcto o no se
	// corresponde con el emisor.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4531. Código SIA (PROCEDIMENT) incorrecto
	
//	@Test	// Resultat test: Notifica no dona error al no indicar el codi del procediment SIA, o posant qualsevol valor (OK)
	public void pruebaEmision20() throws Exception {
		
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = false;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioV2 notificacio = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);

		notificacio.setProcedimentCodi("XYZ");
		
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getErrorDescripcio(),
				respostaAlta.isError(),
				is(false));
		assertNotNull(respostaAlta);
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		//Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertThat(
				respostaInfo.getErrorDescripcio(),
				respostaInfo.isError(),
				is(false));
		assertNotNull(respostaInfo);
		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertThat(
					info.isError(),
					is(false));
			assertThat(
					info.getErrorDescripcio(),
					containsString("[4531]"));
		}
	}
	
	
	// PETICIÓN CON TIPO DE DOMICILIO INCORRECTO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el tipo_domicilio no es concreto ni fiscal.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4600. Tipo domicilio incorrecto
	
//	@Test	// Resultat impossible: el sistema només permet tipus de domicili ESTRANGER, NACIONAL, APARTAT_CORREUS o SENSE_NORMALITZAR en cas contrar: BAD REQUEST. (OK)
	public void pruebaEmision21() throws Exception {
		
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = true;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioV2 notificacio = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Tipus domicili no vàlid
//		NotificacioEnviamentTest env = (NotificacioEnviamentTest)notificacio.getEnviaments().get(0);
//		env.getEntregaPostal().setTipus(NotificaDomiciliConcretTipusEnumDto.ALTRE);
		
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getErrorDescripcio(),
				respostaAlta.isError(),
				is(false));
		assertNotNull(respostaAlta);
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		//Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertThat(
				respostaInfo.getErrorDescripcio(),
				respostaInfo.isError(),
				is(false));
		assertNotNull(respostaInfo);
		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertThat(
					info.isError(),
					is(false));
			assertThat(
					info.getErrorDescripcio(),
					containsString("[4600]"));
		}
	}
	
	
	// PETICIÓN DE TIPO DOMICILIO CONCRETO CON NÚMERO DE CASA Y PUNTO KILOMÉTRICO RELLENOS.
	// -------------------------------------------------------------------------------------
	// Petición en la que el tipo_domicilio es concreto pero se
	// introducen valores en ambos campos: punto_kilometrico y numero_casa.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4601. Dirección no válida, no pueden estar rellenos al mismo tiempo
	// Número de casa y Punto kilométrico
	
//	@Test	// Resultat test: OK
	public void pruebaEmision22() throws Exception {
		
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = true;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioV2 notificacio = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Punt kilomètric emplenat (el número de casa també està emplenat)
		Enviament env = notificacio.getEnviaments().get(0);
		env.getEntregaPostal().setPuntKm("pk01");
		
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getErrorDescripcio(),
				respostaAlta.isError(),
				is(false));
		assertNotNull(respostaAlta);
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		//Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertThat(
				respostaInfo.getErrorDescripcio(),
				respostaInfo.isError(),
				is(false));
		assertNotNull(respostaInfo);
		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertThat(
					info.isError(),
					is(false));
			assertThat(
					info.getErrorDescripcio(),
					containsString("[4631]"));
		}
	}
	
	
	// PETICIÓN CON TIPO DE DOMICILIO VACÍO.
	// -------------------------------------------------------------------------------------
	// Petición en la que no se rellena el campo tipo_domicilio.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4602. Tipo de domicilio vacío
	
//	@Test	//	Resultat impossible: [ENTREGA_POSTAL_TIPUS] El camp 'entregaPostalTipus' no pot ser null. (OK)
	public void pruebaEmision23() throws Exception {
		
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = true;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioV2 notificacio = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Tipus de domicili buit
		Enviament env = notificacio.getEnviaments().get(0);
		env.getEntregaPostal().setTipus(null);
		
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getErrorDescripcio(),
				respostaAlta.isError(),
				is(false));
		assertNotNull(respostaAlta);
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		//Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertThat(
				respostaAlta.getErrorDescripcio(),
				respostaInfo.isError(),
				is(false));
		assertNotNull(respostaInfo);
		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertThat(
					info.isError(),
					is(false));
			assertThat(
					info.getErrorDescripcio(),
					containsString("[4602]"));
		}
	}
	
	
	// PETICIÓN DE TIPO DOMICILIO CONCRETO CON NOMBRE DE VÍA VACÍO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el tipo_domicilio debe ser concreto, sin
	// Apartado de Correos y en la que el Nombre de Vía está vacío
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4610. El Nombre de vía no puede estar vacío
	
//	@Test	//	Resultat test: [ENTREGA_POSTAL_NOM_VIA] El camp 'viaNom' de l'entrega postal d'un enviament no pot ser null. (OK)
	public void pruebaEmision24() throws Exception {
		
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = true;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioV2 notificacio = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Nom de la via buit
		Enviament env = notificacio.getEnviaments().get(0);
		env.getEntregaPostal().setViaNom(null);
		
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getErrorDescripcio(),
				respostaAlta.isError(),
				is(false));
		assertNotNull(respostaAlta);
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		//Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertThat(
				respostaInfo.getErrorDescripcio(),
				respostaInfo.isError(),
				is(false));
		assertNotNull(respostaInfo);
		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertThat(
					info.isError(),
					is(false));
			assertThat(
					info.getErrorDescripcio(),
					containsString("[4610]"));
		}
	}
	
	
	// PETICIÓN DE TIPO DOMICILIO CONCRETO CON TIPO DE VÍA VACÍO
	// -------------------------------------------------------------------------------------
	// Petición en la que el campo tipo_via del Domicilio está sin rellenar.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4620. El Tipo de Vía no puede estar vacío
	
//	@Test	// Resultat test: [VIA_TIPUS] El camp 'viaTipus' no pot ser null en cas d'entrega NACIONAL NORMALITZAT. (OK)
	public void pruebaEmision25() throws Exception {
		
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = true;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioV2 notificacio = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Tipus de via buid
		Enviament env = notificacio.getEnviaments().get(0);
		env.getEntregaPostal().setViaTipus(null);
		
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getErrorDescripcio(),
				respostaAlta.isError(),
				is(false));
		assertNotNull(respostaAlta);
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		//Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertThat(
				respostaInfo.getErrorDescripcio(),
				respostaInfo.isError(),
				is(false));
		assertNotNull(respostaInfo);
		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertThat(
					info.isError(),
					is(false));
			assertThat(
					info.getErrorDescripcio(),
					containsString("[4620]"));
		}
	}
	
	
	// PETICIÓN DE TIPO DOMICILIO CONCRETO CON NÚMERO DE CASA Y PUNTO KILOMÉTRICO VACÍO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el tipo_domicilio es concreto pero no se
	// introduce ningún valor en el campo punto_kilometrico ni en el campo numero_casa....
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4630. Dirección no válida, falta Número de casa o Punto kilométrico
	
	// TODO: Necessitam tenir un CIE vàlid
//	@Test // Resultat test: [PUNT_KM_NUM_CASA] S'ha d'indicar almenys un 'numeroCasa' o 'puntKm'. (OK)
	public void pruebaEmision26() throws Exception {
		
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = true;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioV2 notificacio = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Número de casa buit (el punt kilomètric també està buid)
		Enviament env = notificacio.getEnviaments().get(0);
		env.getEntregaPostal().setNumeroCasa(null);
		
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getErrorDescripcio(),
				respostaAlta.isError(),
				is(false));
		assertNotNull(respostaAlta);
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		//Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertThat(
				respostaInfo.getErrorDescripcio(),
				respostaInfo.isError(),
				is(false));
		assertNotNull(respostaInfo);
		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertThat(
					info.isError(),
					is(false));
			assertThat(
					info.getErrorDescripcio(),
					containsString("[4630]"));
		}
	}
	
	
	// PETICIÓN INCORRECTA DE TIPO DOMICILIO CONCRETO Y APARTADO DE CORREOS.
	// -------------------------------------------------------------------------------------
	// Petición en la que el tipo_domicilio debe ser concreto, con
	// Apartado de Correos relleno pero faltando el Código Postal.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4650. Código Postal no puede estar vacío
	
//	@Test	// Resultat test: [CODI_POSTAL] El camp 'codiPostal' no pot ser null (indicar 00000 en cas de no disposar del codi postal). (OK)
	public void pruebaEmision27() throws Exception {
		
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = true;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.APARTAT_CORREUS;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioV2 notificacio = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Codi postal buid
		Enviament env = notificacio.getEnviaments().get(0);
		env.getEntregaPostal().setCodiPostal(null);
		
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getErrorDescripcio(),
				respostaAlta.isError(),
				is(false));
		assertNotNull(respostaAlta);
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		//Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertThat(
				respostaInfo.getErrorDescripcio(),
				respostaInfo.isError(),
				is(false));
		assertNotNull(respostaInfo);
		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertThat(
					info.isError(),
					is(false));
			assertThat(
					info.getErrorDescripcio(),
					containsString("[4650]"));
		}
	}
	
	
	// PETICIÓN INCORRECTA DE TIPO DOMICILIO CONCRETO FALTANDO EL APARTADO DE CORREOS.
	// -------------------------------------------------------------------------------------
	// Petición en la que el tipo_domicilio debe ser concreto, pero el
	// campo apartado_correos se encuentre vacío.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4660. Apartado de correos no puede estar vacío
	
//	@Test	// Resultat test: [APARTAT_CORREUS] El camp 'apartatCorreus' no pot ser null en cas d'entrega APARTAT CORREUS. (OK)
	public void pruebaEmision28() throws Exception {
		
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = true;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.APARTAT_CORREUS;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioV2 notificacio = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Apartat de correus buit
		Enviament env = notificacio.getEnviaments().get(0);
		env.getEntregaPostal().setApartatCorreus(null);
		
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getErrorDescripcio(),
				respostaAlta.isError(),
				is(false));
		assertNotNull(respostaAlta);
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		//Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertThat(
				respostaInfo.getErrorDescripcio(),
				respostaInfo.isError(),
				is(false));
		assertNotNull(respostaInfo);
		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertThat(
					info.isError(),
					is(false));
			assertThat(
					info.getErrorDescripcio(),
					containsString("[4660]"));
		}
	}
	
	
	// PETICIÓN DEL TIPO DOMICILIO CONCRETO CON CÓDIGO DE MUNICIPIO VACÍO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el código de municipio (código INE) proporcionado está vacío.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4670. Código de municipio vacío
	
//	@Test	// Resultat test: [MUNICIPI_CODI] El camp 'municipiCodi' no pot ser null en cas d'entrega APARTAT CORREUS. OK
	public void pruebaEmision29() throws Exception {
		
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = true;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioV2 notificacio = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Codi de municipi buit
		Enviament env = notificacio.getEnviaments().get(0);
		env.getEntregaPostal().setMunicipiCodi(null);
		
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getErrorDescripcio(),
				respostaAlta.isError(),
				is(false));
		assertNotNull(respostaAlta);
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		//Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertThat(
				respostaInfo.getErrorDescripcio(),
				respostaInfo.isError(),
				is(false));
		assertNotNull(respostaInfo);
		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertThat(
					info.isError(),
					is(false));
			assertThat(
					info.getErrorDescripcio(),
					containsString("[4670]"));
		}
	}
	
	
	// PETICIÓN DEL TIPO DOMICILIO CONCRETO CON CÓDIGO DE MUNICIPIO INCORRECTO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el código de municipio (código INE) proporcionado es incorrecto.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4671. Código de municipio (COD_MUNICIPIO) erróneo
	
//	@Test	// Resultat test: OK
	public void pruebaEmision30() throws Exception {
		
		int numDestinataris = 1; 
		int numEnviaments = 1;
		boolean ambEnviamentPostal = true;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioV2 notificacio = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Codi de municipi no vàlid
		Enviament env = notificacio.getEnviaments().get(0);
		env.getEntregaPostal().setMunicipiCodi("CODI");
		
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getErrorDescripcio(),
				respostaAlta.isError(),
				is(false));
		assertNotNull(respostaAlta);
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		//Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertThat(
				respostaInfo.getErrorDescripcio(),
				respostaInfo.isError(),
				is(false));
		assertNotNull(respostaInfo);
		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertThat(
					info.isError(),
					is(false));
			assertThat(
					info.getErrorDescripcio(),
					containsString("[4671]"));
		}
	}
	
	
	// PETICIÓN DEL TIPO DOMICILIO CONCRETO CON CÓDIGO DE PROVINCIA VACÍO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el código de provincia proporcionado está vacío.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4680. Código de provincia vacío
	
//	@Test	// Resultat test: [PROVINCIA] El camp 'provincia' no pot ser null en cas d'entrega NACIONAL NORMALITZAT. (OK)
	public void pruebaEmision31() throws Exception {
		
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = true;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioV2 notificacio = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// codi de provincia buit
		Enviament env = notificacio.getEnviaments().get(0);
		env.getEntregaPostal().setProvincia(null);
		
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getErrorDescripcio(),
				respostaAlta.isError(),
				is(false));
		assertNotNull(respostaAlta);
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		//Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertThat(
				respostaInfo.getErrorDescripcio(),
				respostaInfo.isError(),
				is(false));
		assertNotNull(respostaInfo);
		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertThat(
					info.isError(),
					is(false));
			assertThat(
					info.getErrorDescripcio(),
					containsString("[4631]"));
		}
	}
	
	
	// PETICIÓN DEL TIPO DOMICILIO CONCRETO CON CÓDIGO DE PROVINCIA INEXISTENTE.
	// -------------------------------------------------------------------------------------
	// Petición en la que el código de provincia proporcionado es incorrecto.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4681. Código de provincia no válido
	
//	@Test	// Resultat test: OK
	public void pruebaEmision32() throws Exception {
		
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = true;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioV2 notificacio = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Codi de província no vàlid
		Enviament env = notificacio.getEnviaments().get(0);
		env.getEntregaPostal().setProvincia("99");
		
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getErrorDescripcio(),
				respostaAlta.isError(),
				is(false));
		assertNotNull(respostaAlta);
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		//Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertThat(
				respostaInfo.getErrorDescripcio(),
				respostaInfo.isError(),
				is(false));
		assertNotNull(respostaInfo);
		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertThat(
					info.isError(),
					is(false));
			assertThat(
					info.getErrorDescripcio(),
					containsString("[4681]"));
		}
	}
	
	
	// PETICIÓN DEL TIPO DOMICILIO CONCRETO CON CÓDIGO DE PAÍS VACÍO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el código de país (ISO 3166) proporcionado está vacío.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4690. Código de país vacío
	
	// TODO: Necessitam tenir un CIE vàlid
//	@Test
	public void pruebaEmision33() throws Exception {
		
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = true;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioV2 notificacio = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Codi de país buid
		Enviament env = notificacio.getEnviaments().get(0);
		env.getEntregaPostal().setPaisCodi(null);
		
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getErrorDescripcio(),
				respostaAlta.isError(),
				is(false));
		assertNotNull(respostaAlta);
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		//Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertThat(
				respostaInfo.getErrorDescripcio(),
				respostaInfo.isError(),
				is(false));
		assertNotNull(respostaInfo);
		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertThat(
					info.isError(),
					is(false));
			assertThat(
					info.getErrorDescripcio(),
					containsString("[4690]"));
		}
	}
	
	
	// PETICIÓN DEL TIPO DOMICILIO CONCRETO CON CÓDIGO DE PAÍS INEXISTENTE.
	// -------------------------------------------------------------------------------------
	// Petición en la que el código de país (ISO 3166) proporcionado es incorrecto.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4691. Código de país no válido
	
	// TODO: Necessitam tenir un CIE vàlid
//	@Test
	public void pruebaEmision34() throws Exception {
		
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = true;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioV2 notificacio = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Codi de país no vàlid
		Enviament env = notificacio.getEnviaments().get(0);
		env.getEntregaPostal().setPaisCodi("ZZ");
		
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.isError(),
				is(false));
		assertNotNull(respostaAlta);
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		//Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertThat(
				respostaInfo.isError(),
				is(false));
		assertNotNull(respostaInfo);
		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertThat(
					info.isError(),
					is(false));
			assertThat(
					info.getErrorDescripcio(),
					containsString("[4691]"));
		}
	}
	
	
	
	// PROVES V.1 DATADO i CERTIFICACION [...]
	
	
	// PRUEBA DE CONSULTA INFORAMACION ENVIO – CORRECTAS
	// =====================================================================================

	// PRUEBA CORRECTA
	// -------------------------------------------------------------------------------------
	// Se realizará una petición SOAP con un identificador de envío correcto.
	// -------------------------------------------------------------------------------------
	// Se comprobará la respuesta con la información sobre el envío,
	// sus datados y sus certificaciones.
	
//	@Test	// Resultat test: OK
	public void pruebaEmision35() throws Exception {
		
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = false;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioV2 notificacio = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.isError(),
				is(false));
		assertNotNull(respostaAlta);
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		//Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertThat(
				respostaInfo.isError(),
				is(false));
		assertNotNull(respostaInfo);
		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertThat(
					info.isError(),
					is(false));
			assertNotNull(respostaAlta.getIdentificador());
			assertNotNull(referencia.getReferencia());
			assertNotNull(info.getReceptorNif());
		}
	}
	
	// PRUEBA DE CONSULTA INFORAMACION ENVIO – ERRÓNEAS
	// =====================================================================================

	// IDENTIFICADOR DE ENVÍO VACÍO.
	// -------------------------------------------------------------------------------------
	// Se realizará una petición SOAP con un identificador de envío vacío.
	// -------------------------------------------------------------------------------------
	// Se comprobará que el servicio web responde con el mensaje 
	// “Identificador de envío vacío”.
	
//	@Test	//	Resultat test: Notib no permet una referència nula
	public void pruebaEmision36() throws Exception {
		
		//Consulta estat notificacio amb identificador buit
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(null);
		assertNotNull(respostaInfo);
		assertThat(
				respostaInfo.isError(),
				is(false));
		//Consulta estat enviament amb referencia buida
		RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(null);
		assertThat(
				info.isError(),
				is(false));
	
	}
	
	// IDENTIFICADOR DE ENVÍO ERRÓNEO.
	// -------------------------------------------------------------------------------------
	// Se realizará una petición SOAP con un identificador de envío erróneo
	// -------------------------------------------------------------------------------------
	// Se comprobará que el servicio web responde con el mensaje
	// “Identificador de envío erróneo”.
	
//	@Test	//	Resultat test: Notib controla que la referència sigui vàlida.
	public void pruebaEmision37() throws Exception {
		//Consulta estat notificacio amb identificador incorrecte
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio("XXX");
		assertNotNull(respostaInfo);
		assertThat(
				respostaInfo.isError(),
				is(false));
		//Consulta estat enviament amb referencia incorrecte
		RespostaConsultaEstatEnviament info = client.consultaEstatEnviament("XXX");
		assertThat(
				info.isError(),
				is(false));
	}
	
	//Versió nova
	// IDENTIFICADOR PROCEDIMIENTO NO VÁLIDO.
	// -------------------------------------------------------------------------------------
	// Se realizará una petición SOAP con un identificador del procedimienot erroneo.
	// -------------------------------------------------------------------------------------
	// Se comprobará que el servicio web responde con el mensaje
	// “Identificador de envío erróneo”.
//	@Test
	public void pruebaEmision38() throws Exception {
	
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = true;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = false;
		
		NotificacioV2 notificacioV2 = generaNotificacio(
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		RespostaAlta respostaAlta = client.alta(notificacioV2);
		assertThat(
				respostaAlta.getErrorDescripcio(),
				respostaAlta.isError(),
				is(false));
		assertNotNull(respostaAlta);
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		//Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertThat(
				respostaInfo.isError(),
				is(false));
		assertNotNull(respostaInfo);
		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertThat(
					info.isError(),
					is(false));
			assertNotNull(referencia.getReferencia());
			assertNotNull(info.getCertificacio());
			assertNotNull(info.getReceptorNif());
		}
		
	}
	
			
	private NotificacioV2 generaNotificacio(
					int numDestinataris,
					int numEnviaments,
					boolean ambEnviamentPostal,
					NotificaDomiciliConcretTipus tipusEnviamentPostal,
					boolean ambEnviamentDEH,
					boolean ambEnviamentDEHObligat,
					boolean ambRetard) throws IOException, DecoderException, DatatypeConfigurationException {
	
		String notificacioId = new Long(System.currentTimeMillis()).toString();
		NotificacioV2 notificacioV2 = generarNotificacio(
				notificacioId,
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				true);
		
		if (!ambRetard)
			notificacioV2.setRetard(0);
		
//		List<String> referencies = notificacioServiceWs.alta(notificacio);
//		assertNotNull(referencies);
//		assertThat(
//				referencies.size(),
//				is(numDestinataris));
		
//		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findByNotificaReferencia(referencies.get(0));
		return notificacioV2;
		
	}
			
	private NotificacioV2 generarNotificacio(
			String notificacioId,
			int numDestinataris,
			int numEnviaments,
			boolean ambEnviamentPostal,
			NotificaDomiciliConcretTipus tipusEnviamentPostal,
			boolean ambEnviamentDEH,
			boolean enviamentDEHObligat,
			boolean ambTipusInteressat) throws IOException, DecoderException, DatatypeConfigurationException {
		byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());
		NotificacioV2 notificacioV2 = new NotificacioV2();
		notificacioV2.setEmisorDir3Codi(ENTITAT_DIR3CODI);
		notificacioV2.setEnviamentTipus(EnviamentTipus.NOTIFICACIO);
		notificacioV2.setConcepte("concepte_" + notificacioId);
		notificacioV2.setDescripcio("descripcio_" + notificacioId);
		notificacioV2.setEnviamentDataProgramada(null);
		
		notificacioV2.setRetard(0);
		notificacioV2.setUsuariCodi("e43110511r");
		notificacioV2.setCaducitat(new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000));
		DocumentV2 documentV2 = new DocumentV2();
		documentV2.setArxiuNom("documentArxiuNom_" + notificacioId + ".pdf");
		documentV2.setContingutBase64(Base64.encodeBase64String(arxiuBytes));
//		document.setHash(
//				Base64.getEncoder().encodeToString(
//						Hex.decodeHex(
//								DigestUtils.sha256Hex(arxiuBytes).toCharArray())));
		documentV2.setNormalitzat(false);
//		document.setGenerarCsv(false);
		notificacioV2.setDocument(documentV2);
//		notificacioV2.setProcedimentCodi("234257");
		
		notificacioV2.setProcedimentCodi("846823");

		
		//Els pagadors postals i cie ja estan definits a nivell de procediment
//		if (ambEnviamentPostal) {
//			PagadorPostal pagadorPostal = new PagadorPostal();
//			pagadorPostal.setDir3Codi("A04013511");
//			pagadorPostal.setFacturacioClientCodi("ccFac_" + notificacioId);
//			pagadorPostal.setContracteNum("pccNum_" + notificacioId);
//			pagadorPostal.setContracteDataVigencia(toXmlGregorianCalendar(new Date(0)));
//			notificacio.setPagadorPostal(pagadorPostal);
//			PagadorCie pagadorCie = new PagadorCie();
//			pagadorCie.setDir3Codi("A04013511");
//			pagadorCie.setContracteDataVigencia(toXmlGregorianCalendar(new Date(0)));
//			notificacio.setPagadorCie(pagadorCie);
//		}

		for (int i = 0; i < numEnviaments; i++) {
			Enviament enviaments = new Enviament();
			Persona titular = new Persona();
			if (ambTipusInteressat) {
				titular.setInteressatTipus(InteressatTipus.FISICA);
			} else {
				titular.setInteressatTipus(null);
			}
			titular.setNom("titularNom" + i);
			titular.setLlinatge1("titLlinatge1_" + i);
			titular.setLlinatge2("titLlinatge2_" + i);
			titular.setNif("43120476F");
			titular.setTelefon("666010101");
			titular.setEmail("departamentals1@dgtic.caib.es");
			enviaments.setTitular(titular);
			List<Persona> destinataris = new ArrayList<Persona>();

			for (int k = 0; k < numDestinataris; k++) {
				Persona destinatari = new Persona();
				if (ambTipusInteressat) {
					destinatari.setInteressatTipus(InteressatTipus.FISICA);
				} else {
					destinatari.setInteressatTipus(null);
				}
				destinatari.setNom("destinatariNom" + k);
				destinatari.setLlinatge1("destLlinatge1_" + k);
				destinatari.setLlinatge2("destLlinatge2_" + k);
				destinatari.setNif("12345678Z");
				destinatari.setTelefon("666020202");
				destinatari.setEmail("destinatari@gmail.com");
				destinataris.add(destinatari);
			}
			enviaments.getDestinataris().addAll(destinataris);
			if (ambEnviamentPostal) {
				enviaments.setEntregaPostalActiva(true);
				EntregaPostal entregaPostal = new EntregaPostal();
				entregaPostal.setTipus(tipusEnviamentPostal);
				if (tipusEnviamentPostal.equals(NotificaDomiciliConcretTipus.ESTRANGER)) {
					entregaPostal.setPaisCodi("FR");
					entregaPostal.setViaNom("Prime Minister's Office, 10 Downing Street");
					entregaPostal.setPoblacio("London");
					entregaPostal.setCodiPostal("00000");
				} else {
					entregaPostal.setViaTipus(EntregaPostalVia.CALLE);
					entregaPostal.setViaNom("Bas");
					entregaPostal.setNumeroCasa("25");
					entregaPostal.setNumeroQualificador("bis");
//					entregaPostal.setPuntKm("pk01");
//					entregaPostal.setApartatCorreus("0228");
					entregaPostal.setPortal("P" + i);
					entregaPostal.setEscala("E" + i);
					entregaPostal.setPlanta("PL" + i);
					entregaPostal.setPorta("PT" + i);
					entregaPostal.setBloc("B" + i);
					entregaPostal.setComplement("complement" + i);
					entregaPostal.setCodiPostal("07500");
					entregaPostal.setPoblacio("poblacio" + i);
					entregaPostal.setMunicipiCodi("070337");
					entregaPostal.setProvincia("07");
					entregaPostal.setPaisCodi("ES");
					entregaPostal.setLinea1("linea1_" + i);
					entregaPostal.setLinea2("linea2_" + i);
					if (tipusEnviamentPostal.equals(NotificaDomiciliConcretTipus.APARTAT_CORREUS))
						entregaPostal.setApartatCorreus("0228");
				}
				
				//entregaPostal.setCie(new Integer(8));
				enviaments.setEntregaPostal(entregaPostal);
			}
			if (ambEnviamentDEH) {
				enviaments.setEntregaDehActiva(true);
				EntregaDeh entregaDeh = new EntregaDeh();
				entregaDeh.setObligat(enviamentDEHObligat);
//				entregaDeh.setProcedimentCodi("234257");
				entregaDeh.setProcedimentCodi("846823");
				enviaments.setEntregaDeh(entregaDeh);
			}
			enviaments.setServeiTipus(NotificaServeiTipusEnumDto.URGENT);
			notificacioV2.getEnviaments().add(enviaments);
		}

		return notificacioV2;
	}
	
//	public NotificacioTest generarNotificacio(
//			String notificacioId,
//			int numDestinataris,
//			boolean ambEnviamentPostal,
//			EntregaPostalTipusEnum tipusEnviamentPostal,
//			boolean ambEnviamentDEH,
//			boolean enviamentDEHObligat) throws IOException, DecoderException {
//		
//		byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());
//		String documentGesdocId = pluginHelper.gestioDocumentalCreate(
//				PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
//				new ByteArrayInputStream(arxiuBytes));
//		
//		NotificacioTest notificacio = new NotificacioTest();
//		
//		notificacio.setEmisorDir3Codi(ENTITAT_DIR3CODI);
//		notificacio.setEnviamentTipus(NotificaEnviamentTipusEnumDto.COMUNICACIO);
//		notificacio.setEnviamentDataProgramada(null);
//		notificacio.setConcepte("concepte_" + notificacioId);
//		notificacio.setDocumentArxiuNom("documentArxiuNom_" + notificacioId + ".pdf");
//		notificacio.setDocumentArxiuId(documentGesdocId);
//		notificacio.setDocumentHash(Base64.getEncoder().encodeToString(
//				Hex.decodeHex(
//						DigestUtils.sha256Hex(arxiuBytes).toCharArray())));
//		notificacio.setDocumentNormalitzat(false);
//		notificacio.setDocumentGenerarCsv(false);
//		notificacio.setEstat(NotificacioEstatEnumDto.PENDENT);
//		notificacio.setDescripcio("descripcio_" + notificacioId);
//		notificacio.setCaducitat(new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000));
//		notificacio.setRetardPostal(5);
//		notificacio.setProcedimentCodiSia("0000");
//		
//		if (ambEnviamentPostal) {
//			notificacio.setPagadorCorreusCodiDir3("A04013511");
//			notificacio.setPagadorCorreusContracteNum("pccNum_" + notificacioId);
//			notificacio.setPagadorCorreusCodiClientFacturacio("ccFac_" + notificacioId);
//			notificacio.setPagadorCorreusDataVigencia(new Date(0));
//			notificacio.setPagadorCieCodiDir3("A04013511");
//			notificacio.setPagadorCieDataVigencia(new Date(0));
//		}
//		
//
//		List<NotificacioEnviamentEntity> enviaments = new ArrayList<NotificacioEnviamentEntity>();
//		for (int i = 0; i < numDestinataris; i++) {
//
//			NotificacioEnviamentTest enviament = new NotificacioEnviamentTest();
//			enviament.setTitularNif("00000000T");
//			enviament.setServeiTipus(NotificaServeiTipusEnumDto.URGENT);
//			enviament.setNotificacio(notificacio);
//			enviament.setNotificaEstat(NotificacioDestinatariEstatEnumDto.NOTIB_PENDENT);
//			enviament.setTitularNom("titularNom" + i);
//			enviament.setTitularLlinatge1("titLlinatge1_" + i);
//			enviament.setTitularLlinatge2("titLlinatge2_" + i);
//			enviament.setTitularTelefon("666010101");
//			enviament.setTitularEmail("titular@gmail.com");
//			
//			enviament.setDestinatariNif("12345678Z");
//			enviament.setDestinatariNom("destinatariNom" + i);
//			enviament.setDestinatariLlinatge1("destLlinatge1_" + i);
//			enviament.setDestinatariLlinatge2("destLlinatge2_" + i);
//			enviament.setDestinatariTelefon("666020202");
//			enviament.setDestinatariEmail("destinatari@gmail.com");
//			
//			if (ambEnviamentPostal) {
//				
//				NotificaDomiciliTipusEnumDto tipus = null;
//				NotificaDomiciliConcretTipusEnumDto tipusConcret = null;
//
//				switch (tipusEnviamentPostal) {
//					case APARTAT_CORREUS:
//						tipusConcret = NotificaDomiciliConcretTipusEnumDto.APARTAT_CORREUS;
//						break;
//					case ESTRANGER:
//						tipusConcret = NotificaDomiciliConcretTipusEnumDto.ESTRANGER;
//						break;
//					case NACIONAL:
//						tipusConcret = NotificaDomiciliConcretTipusEnumDto.NACIONAL;
//						break;
//					case SENSE_NORMALITZAR:
//						tipusConcret = NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR;
//						break;
//				}
//				tipus = NotificaDomiciliTipusEnumDto.CONCRETO;
//				
//				enviament.setDomiciliTipus(tipus);
//				enviament.setDomiciliConcretTipus(tipusConcret);
//				
//				if (tipusEnviamentPostal.equals(EntregaPostalTipusEnum.ESTRANGER)) {
//					enviament.setDomiciliPaisCodiIso("UK");
//					enviament.setDomiciliViaNom("Prime Minister's Office, 10 Downing Street");
//					enviament.setDomiciliPoblacio("London");
//					enviament.setDomiciliCodiPostal("00000");
//					enviament.setDomiciliNumeracioTipus(NotificaDomiciliNumeracioTipusEnumDto.SENSE_NUMERO);
//				} else {
//					enviament.setDomiciliViaTipus(NotificaDomiciliViaTipusEnumDto.CALLE);
//					enviament.setDomiciliViaNom("Bas");
//					enviament.setDomiciliNumeracioNumero("25");
//					enviament.setDomiciliBloc("B" + i);
//					enviament.setDomiciliPortal("P" + i);
//					enviament.setDomiciliEscala("E" + i);
//					enviament.setDomiciliPlanta("PL" + i);
//					enviament.setDomiciliPorta("PT" + i);
//					enviament.setDomiciliComplement("complement" + i);
//					enviament.setDomiciliCodiPostal("07500");
//					enviament.setDomiciliPoblacio("poblacio" + i);
//					enviament.setDomiciliMunicipiCodiIne("070337");
//					enviament.setDomiciliProvinciaCodi("07");
//					enviament.setDomiciliPaisCodiIso("ES");
//					enviament.setDomiciliLinea1("linea1_" + i);
//					enviament.setDomiciliLinea2("linea2_" + i);
//					enviament.setDomiciliNumeracioPuntKm(domiciliNumeracioPuntKm);
//					if (tipusEnviamentPostal.equals(EntregaPostalTipusEnum.APARTAT_CORREUS)) {
//						enviament.setDomiciliNumeracioTipus(NotificaDomiciliNumeracioTipusEnumDto.APARTAT_CORREUS);
//						enviament.setDomiciliApartatCorreus("0228");
//					} else {
//						enviament.setDomiciliNumeracioTipus(NotificaDomiciliNumeracioTipusEnumDto.NUMERO);
//					}
//				}
//				enviament.setDomiciliCie(new Integer(8));
//			}
//
//			if (ambEnviamentDEH) {
//				enviament.setDehObligat(enviamentDEHObligat);
//				enviament.setDehNif("00000000T");
//				enviament.setDehProcedimentCodi("0000");
//			}
//			
//			enviament.setNotificaReferencia(notificacioId + "_" + i);
//			enviaments.add(enviament);
//		}
//		notificacio.setEnviaments(enviaments);
//		return notificacio;
//	}


	private InputStream getContingutNotificacioAdjunt() {
		return getClass().getResourceAsStream(
				"/es/caib/notib/client/notificacio_adjunt.pdf");
	}
	
	private XMLGregorianCalendar toXmlGregorianCalendar(Date date) throws DatatypeConfigurationException {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(date);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
	}
}
