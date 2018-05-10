/**
 * 
 */
package es.caib.notib.client;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import es.caib.notib.ws.notificacio.Document;
import es.caib.notib.ws.notificacio.EntregaDeh;
import es.caib.notib.ws.notificacio.EntregaPostal;
import es.caib.notib.ws.notificacio.EntregaPostalTipusEnum;
import es.caib.notib.ws.notificacio.EntregaPostalViaTipusEnum;
import es.caib.notib.ws.notificacio.Enviament;
import es.caib.notib.ws.notificacio.EnviamentTipusEnum;
import es.caib.notib.ws.notificacio.Notificacio;
import es.caib.notib.ws.notificacio.PagadorCie;
import es.caib.notib.ws.notificacio.PagadorPostal;
import es.caib.notib.ws.notificacio.ParametresSeu;
import es.caib.notib.ws.notificacio.Persona;
import es.caib.notib.ws.notificacio.ServeiTipusEnum;

/**
 * Test per al client REST del servei de notificacions de NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NotificaWsTestIntegracioRest {

	private static final String ENTITAT_DIR3CODI = "A04013511";

//	@Autowired
//	private NotificaV2Helper notificaHelper;
//	@Autowired
//	private PluginHelper pluginHelper;
//	
//	private EntitatDto entitat;
//	private PermisDto permisAplicacio;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private static NotificacioRestClient client;
	
	/*

	@BeforeClass
	public static void setUpClass() throws IOException, DecoderException {
		client = NotificacioRestClientFactory.getRestClient(
				"http://localhost:8180/notib",
				"notapp",
				"notapp");
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
	
	// PETICIÓN CORRECTA TIPO CENTRO DE IMPRESIÓN, DOMICILIO CONCRETO Y NACIONAL
	// -------------------------------------------------------------------------------------
	// Para cada destinatario se comprobará que devuelve un
	// identificador, la referencia del emisor y el NIF del titular.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 000. OK
	
	// TODO: Necessitam tenir un CIE vàlid
//	@Test
	public void pruebaEmision01() throws Exception {
	
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = false;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(false));
			assertNotNull(info.getIdentificador());
			assertNotNull(info.getReferencia());
			assertNotNull(info.getTitular().getNif());
		}
		
	}
	
	
	// PETICIÓN CORRECTA DE TIPO CENTRO DE IMPRESIÓN, DOMICILIO CONCRETO E INTERNACIONAL.
	// -------------------------------------------------------------------------------------
	// Para cada destinatario se comprobará que devuelve un
	// identificador, la referencia del emisor y el NIF del titular.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 000. OK
	
	// TODO: Necessitam tenir un CIE vàlid
//	@Test
	public void pruebaEmision02() throws Exception {
	
		// Petició TIPO CENTRO DE IMPRESIÓN, DOMICILIO CONCRETO E INTERNACIONAL
		
//		String notificacioId = new Long(System.currentTimeMillis()).toString();
//		int numDestinataris = 1;
//		
//		Notificacio notificacio = generarNotificacio(
//				notificacioId,
//				numDestinataris,
//				true);
//		notificacio.setRetard(0);
//		
//		EntregaPostal entregaPostal = notificacio.getEnviaments().get(0).getEntregaPostal();
//		entregaPostal.setTipus(EntregaPostalTipusEnum.ESTRANGER);
//		entregaPostal.setPaisCodi("UK");
//		entregaPostal.setViaNom("Prime Minister's Office, 10 Downing Street");
//		entregaPostal.setPoblacio("London");
//		entregaPostal.setCodiPostal("00000");
//		entregaPostal.setViaTipus(null);
//		entregaPostal.setNumeroCasa(null);
//		entregaPostal.setNumeroQualificador(null);
//		entregaPostal.setPuntKm(null);
//		entregaPostal.setPortal(null);
//		entregaPostal.setEscala(null);
//		entregaPostal.setPlanta(null);
//		entregaPostal.setPorta(null);
//		entregaPostal.setBloc(null);
//		entregaPostal.setComplement(null);
//		entregaPostal.setMunicipiCodi(null);
//		entregaPostal.setProvinciaCodi(null);
//		entregaPostal.setLinea1(null);
//		entregaPostal.setLinea2(null);
//		
//		List<String> referencies = notificacioServiceWs.alta(notificacio);
//		assertNotNull(referencies);
//		assertThat(
//				referencies.size(),
//				is(numDestinataris));
//		
//		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findByNotificaReferencia(referencies.get(0));
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.ESTRANGER;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = false;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(false));
			assertNotNull(info.getIdentificador());
			assertNotNull(info.getReferencia());
			assertNotNull(info.getTitular().getNif());
		}
	}

	
	// PETICIÓN CORRECTA DE TIPO DEH VOLUNTARIO + CIE.
	// -------------------------------------------------------------------------------------
	// Para cada destinatario se comprobará que devuelve un
	// identificador, la referencia del emisor y el NIF del titular.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 000. OK
	
	// TODO: Necessitam tenir un CIE vàlid
//	@Test
	public void pruebaEmision03() throws Exception {
		
		// Petició TIPO DEH VOLUNTARIO + CIE

		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = true;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = false;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(false));
			assertNotNull(info.getIdentificador());
			assertNotNull(info.getReferencia());
			assertNotNull(info.getTitular().getNif());
		}	
	}
	
	
	// PETICIÓN CORRECTA DE TIPO DEH OBLIGADO.
	// -------------------------------------------------------------------------------------
	// Para cada destinatario se comprobará que devuelve un
	// identificador, la referencia del emisor y el NIF del titular.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 000. OK
	
//	@Test	// Resultat test: OK
	public void pruebaEmision04() throws Exception {
		
		// Petició TIPO DEH OBLIGADO
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = true;
		boolean ambEnviamentDEHObligat = true;
		boolean ambRetard = false;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(false));
			assertNotNull(info.getIdentificador());
			assertNotNull(info.getReferencia());
			assertNotNull(info.getTitular().getNif());
		}	
	}
	
	
	// PETICIÓN CORRECTA DE ENVIO SOLO CARPETA.
	// -------------------------------------------------------------------------------------
	// Para cada destinatario se comprobará que devuelve un
	// identificador, la referencia del emisor y el NIF del titular.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 000. OK
	
//	@Test	// Resultat test: OK
	public void pruebaEmision05() throws Exception {
		
		// Petició DE ENVIO SOLO CARPETA
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(false));
			assertNotNull(info.getIdentificador());
			assertNotNull(info.getReferencia());
			assertNotNull(info.getTitular().getNif());
		}
	}
	
	
	// PETICIÓN CORRECTA CON MAS DE UN DESTINATARIO.
	// -------------------------------------------------------------------------------------
	// Para cada destinatario se comprobará que devuelve un identificador, la referencia 
	// del emisor y el NIF del titular. En NotificaWS2 se haría como una remesa con varios 
	// envíos y en NotificaWS como un envio con varios destinatarios.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 000. OK

//	@Test	// Resultat test: OK
	public void pruebaEmision06() throws Exception {
		
		// Petició CON MAS DE UN DESTINATARIO
		
		int numDestinataris = 3;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(false));
			assertNotNull(info.getIdentificador());
			assertNotNull(info.getReferencia());
			assertNotNull(info.getTitular().getNif());
		}
	}
	
	
	// PRUEBAS DE EMISIÓN – ERRÓNEAS
	// =====================================================================================
	
	// ORGANISMO EMISOR DESCONOCIDO.
	// -------------------------------------------------------------------------------------
	// Se enviará una petición con un organismo emisor cuyo código
	// DIR3 no se encuentre en BBDD.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4011. Organismo emisor no dado de alta

//	@Test	// Resultat test: OK
	public void pruebaEmision07() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		notificacio.setEmisorDir3Codi("A00000000");
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(true));
			assertThat(
					info.getNotificaErrorDescripcio(),
					anyOf(
							containsString("[4011]"),
							containsString("[ORGANISMO_NO_RECONOCIDO]")));
		}
	}
	
	
	// PETICIÓN CON EL VALOR DE PDF NORMALIZADO VACÍO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el valor del campo normalizado está sin rellenar.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4151. El campo normalizado está vacío
	
//	@Test	// Prova impossible: Document no permet el camp normalitzat buid.
	public void pruebaEmision08() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
//		camp normalitzat a NULL
//		notificacio.getDocument.setNormalitzat(null);
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(true));
			assertThat(
					info.getNotificaErrorDescripcio(),
					containsString("[4151]"));
		}
	}
	
	
	// PETICIÓN CON UN VALOR DE PDF NORMALIZADO INCORRECTO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el valor del campo normalizado no es ni si ni no
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4152. El valor de normalizado no es válido, debe ser 'si' o 'no'
	

//	@Test	// Prova impossible: NotificaHelper únicament permet els valors "si" i "no".
	public void pruebaEmision09() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
//		valor del camp normalitzat incorrecte (diferent de "si" o "no")
//		notificacio.getDocument.setNormalitzat("yes");
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
		
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(true));
			assertThat(
					info.getNotificaErrorDescripcio(),
					containsString("[4152]"));
		}
	}
	
	
	// PETICIÓN CON NIF DEL TITULAR INCORRECTO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el NIF que se proporciona del titular es incorrecto
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4200. El documento de identificación no es válido
	
//	@Test	// Resultat test: OK
	public void pruebaEmision10() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Nif del titular no vàlid
		Enviament env = notificacio.getEnviaments().get(0);
		env.getTitular().setNif("00000000A");
				
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
		
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(true));
			assertThat(
					info.getNotificaErrorDescripcio(),
					containsString("[4200]"));
		}
		
	}
	
	// PETICIÓN CON NOMBRE DEL TITULAR VACÍO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el campo nombre del titular está vacío.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4241. Indique el nombre
	
//	@Test	// Resultat test: OK
	public void pruebaEmision11() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
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
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
		
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(true));
			assertThat(
					info.getNotificaErrorDescripcio(),
					anyOf(
							containsString("[4210]"),
							containsString("[4241]")));
		}
		
	}
	
	
	// PETICIÓN SIN DOCUMENTO. 
	// -------------------------------------------------------------------------------------
	// Se enviará una petición sin documento
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4311. Documento no puede estar vacío
	
//	@Test	// Prova impossible: NotificaHelper valida que el document no sigui null.
	public void pruebaEmision12() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Document buid
		notificacio.getDocument().setContingutBase64(null);
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
		
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(true));
			assertThat(
					info.getNotificaErrorDescripcio(),
					containsString("[4311]"));
		}
	}
	
	
	// PETICIÓN EN LA QUE EL HASH ENVIADO NO COINCIDE CON EL HASH DEL DOCUMENTO.
	// -------------------------------------------------------------------------------------
	// Se enviará una petición en la que el Hash calculado del PDF no
	// coincida con el que se recibe del WS. El algoritmo que se usa es
	// SHA256 y posteriormente codificado en Base64.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4320. No se corresponde el sha1 del documento con el contenido
	
//	@Test	// Resultat test: OK
	public void pruebaEmision13() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Hash del document incorrecte
		notificacio.getDocument().setHash("AAA");
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
		
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(true));
			assertThat(
					info.getNotificaErrorDescripcio(),
					containsString("[4320]"));
		}
	}
	
	
	// PETICIÓN CON UN VALOR PRIORIDAD DE SERVICIO INCORRECTO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el valor de servicio no es ni normal ni urgente.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4402. El campo Servicio solo puede contener los valores 'urgente' o 'normal'
	
//	@Test	// Prova impossible: ServeiTipus únicament pot ser null, NORMAL o URGENT, i el camp no existeix a la v.2.
	public void pruebaEmision14() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Tipus de servei no vàlid 
//		NotificacioEnviamentTest env = (NotificacioEnviamentTest)notificacio.getEnviaments().get(0);
//		env.setServeiTipus(NotificaServeiTipusEnumDto.ALTRE);
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
		
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(true));
			assertThat(
					info.getNotificaErrorDescripcio(),
					containsString("[4402]"));
		}
	}
	
	
	// PETICIÓN CON EL VALOR DE PRIORIDAD DE SERVICIO VACÍO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el valor del campo servicio está vacío.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4403. Debe rellenar el campo 'servicio'
	
//	@Test	// Prova impossible: ServeiTipus no existeix a la v.2.
	public void pruebaEmision15() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Tipus de servei buid
		Enviament env = notificacio.getEnviaments().get(0);
		env.setServeiTipus(null);
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
		
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(true));
			assertThat(
					info.getNotificaErrorDescripcio(),
					containsString("[4403]"));
		}
	}
	
	
	// PETICIÓN CON CONCEPTO VACÍO.
	// -------------------------------------------------------------------------------------
	// Se enviará una petición con el campo concepto vacío.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4500. Indique el concepto
	
//	@Test	// Prova impossible: Notib no permet el concepte buit.
	public void pruebaEmision16() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Concepte buit
		notificacio.setConcepte(null);
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
		
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(true));
			assertThat(
					info.getNotificaErrorDescripcio(),
					containsString("[4500]"));
		}
	}
	
	// PETICIÓN CON TIPO DE ENVÍO VACÍO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el tipo de envío está vacío.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4510. Tipo de envío vacío
	
//	@Test	// Prova impossible: Notib no permet l'enviament tipus buit.
	public void pruebaEmision17() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);

		// Enviament tipus buid
		notificacio.setEnviamentTipus(null);
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
		
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(true));
			assertThat(
					info.getNotificaErrorDescripcio(),
					containsString("[4510]"));
		}
	}
	
	
	// PETICIÓN CON TIPO DE ENVÍO INCORRECTO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el tipo de envío no es ni notificación ni comunicación.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4511. Tipo de envío no permitido
	
//	@Test	// Prova impossible: el sistema només permet tipus d'enviament COMUNICACIO i NOTIFICACIO
	public void pruebaEmision18() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Enviament tipus no vàlid
//		notificacio.setEnviamentTipus(NotificaEnviamentTipusEnumDto.ALTRE);
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
		
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(true));
			assertThat(
					info.getNotificaErrorDescripcio(),
					containsString("[4511]"));
		}
	}
	
	
	// PETICIÓN CON CÓDIGO SIA ERRÓNEO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el Código SIA enviado no es correcto o no se
	// corresponde con el emisor.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4531. Código SIA incorrecto
	
//	@Test	// Resultat test: Notifica no dona error al no indicar el codi del procediment SIA, o posant qualsevol valor
	public void pruebaEmision19() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);

		notificacio.setProcedimentCodi("XYZ");
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
		
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(true));
			assertThat(
					info.getNotificaErrorDescripcio(),
					containsString("[4531]"));
		}
	}
	
	
	// PETICIÓN CON TIPO DE DOMICILIO INCORRECTO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el tipo_domicilio no es concreto ni fiscal.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4600. Tipo domicilio incorrecto
	
//	@Test	// Prova impossible: el sistema només permet tipus de domicili CONCRET o FISCAL
	public void pruebaEmision20() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Tipus domicili no vàlid
//		NotificacioEnviamentTest env = (NotificacioEnviamentTest)notificacio.getEnviaments().get(0);
//		env.getEntregaPostal().setTipus(NotificaDomiciliTipusEnumDto.ALTRE);
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
		
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(true));
			assertThat(
					info.getNotificaErrorDescripcio(),
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
	public void pruebaEmision21() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Punt kilomètric emplenat (el número de casa també està emplenat)
		Enviament env = notificacio.getEnviaments().get(0);
		env.getEntregaPostal().setPuntKm("pk01");
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
		
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(true));
			assertThat(
					info.getNotificaErrorDescripcio(),
					containsString("[4631]"));
		}
	}
	
	
	// PETICIÓN CON TIPO DE DOMICILIO VACÍO.
	// -------------------------------------------------------------------------------------
	// Petición en la que no se rellena el campo tipo_domicilio.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4602. Tipo de domicilio vacío
	
//	@Test	//	Prova impossible: NotificaHelper no permet el camp tipus domicili buit
	public void pruebaEmision22() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Tipus de domicili buit
		Enviament env = notificacio.getEnviaments().get(0);
		env.getEntregaPostal().setTipus(null);
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
		
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(true));
			assertThat(
					info.getNotificaErrorDescripcio(),
					containsString("[4602]"));
		}
	}
	
	
	// PETICIÓN DE TIPO DOMICILIO CONCRETO CON NOMBRE DE VÍA VACÍO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el tipo_domicilio debe ser concreto, sin
	// Apartado de Correos y en la que el Nombre de Vía está vacío
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4610. El Nombre de vía no puede estar vacío
	
//	@Test	// Resultat test: OK
	public void pruebaEmision23() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Nom de la via buit
		Enviament env = notificacio.getEnviaments().get(0);
		env.getEntregaPostal().setViaNom(null);
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
		
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(true));
			assertThat(
					info.getNotificaErrorDescripcio(),
					containsString("[4610]"));
		}
	}
	
	
	// PETICIÓN DE TIPO DOMICILIO CONCRETO CON TIPO DE VÍA VACÍO
	// -------------------------------------------------------------------------------------
	// Petición en la que el campo tipo_via del Domicilio está sin rellenar.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4620. El Tipo de Vía no puede estar vacío
	
//	@Test	// Resultat test: OK
	public void pruebaEmision24() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Tipus de via buid
		Enviament env = notificacio.getEnviaments().get(0);
		env.getEntregaPostal().setViaTipus(null);
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
		
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(true));
			assertThat(
					info.getNotificaErrorDescripcio(),
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
//	@Test
	public void pruebaEmision25() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Número de casa buit (el punt kilomètric també està buid)
		Enviament env = notificacio.getEnviaments().get(0);
		env.getEntregaPostal().setNumeroCasa(null);
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
		
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(true));
			assertThat(
					info.getNotificaErrorDescripcio(),
					containsString("[4630]"));
		}
	}
	
	
	// PETICIÓN INCORRECTA DE TIPO DOMICILIO CONCRETO Y APARTADO DE CORREOS.
	// -------------------------------------------------------------------------------------
	// Petición en la que el tipo_domicilio debe ser concreto, con
	// Apartado de Correos relleno pero faltando el Código Postal.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4650. Código Postal no puede estar vacío
	
//	@Test	// Resultat test: OK
	public void pruebaEmision26() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.APARTAT_CORREUS;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Codi postal buid
		Enviament env = notificacio.getEnviaments().get(0);
		env.getEntregaPostal().setCodiPostal(null);
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
		
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(true));
			assertThat(
					info.getNotificaErrorDescripcio(),
					containsString("[4650]"));
		}
	}
	
	
	// PETICIÓN INCORRECTA DE TIPO DOMICILIO CONCRETO FALTANDO EL APARTADO DE CORREOS.
	// -------------------------------------------------------------------------------------
	// Petición en la que el tipo_domicilio debe ser concreto, pero el
	// campo apartado_correos se encuentre vacío.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4660. Apartado de correos no puede estar vacío
	
//	@Test	// Resultat test: OK
	public void pruebaEmision27() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.APARTAT_CORREUS;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Apartat de correus buit
		Enviament env = notificacio.getEnviaments().get(0);
		env.getEntregaPostal().setApartatCorreus(null);
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
		
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(true));
			assertThat(
					info.getNotificaErrorDescripcio(),
					containsString("[4660]"));
		}
	}
	
	
	// PETICIÓN DEL TIPO DOMICILIO CONCRETO CON CÓDIGO DE MUNICIPIO VACÍO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el código de municipio (código INE) proporcionado está vacío.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4670. Código de municipio vacío
	
//	@Test	// Resultat test: OK
	public void pruebaEmision28() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Codi de municipi buit
		Enviament env = notificacio.getEnviaments().get(0);
		env.getEntregaPostal().setMunicipiCodi(null);
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
		
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(true));
			assertThat(
					info.getNotificaErrorDescripcio(),
					containsString("[4670]"));
		}
	}
	
	
	// PETICIÓN DEL TIPO DOMICILIO CONCRETO CON CÓDIGO DE MUNICIPIO INCORRECTO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el código de municipio (código INE) proporcionado es incorrecto.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4671. Código de municipio (COD_MUNICIPIO) erróneo
	
//	@Test	// Resultat test: OK
	public void pruebaEmision29() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Codi de municipi no vàlid
		Enviament env = notificacio.getEnviaments().get(0);
		env.getEntregaPostal().setMunicipiCodi("CODI");
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
		
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(true));
			assertThat(
					info.getNotificaErrorDescripcio(),
					containsString("[4671]"));
		}
	}
	
	
	// PETICIÓN DEL TIPO DOMICILIO CONCRETO CON CÓDIGO DE PROVINCIA VACÍO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el código de provincia proporcionado está vacío.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4680. Código de provincia vacío
	
//	@Test	// Resultat test: OK
	public void pruebaEmision30() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// codi de provincia buit
		Enviament env = notificacio.getEnviaments().get(0);
		env.getEntregaPostal().setProvinciaCodi(null);
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
		
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(true));
			assertThat(
					info.getNotificaErrorDescripcio(),
					containsString("[4680]"));
		}
	}
	
	
	// PETICIÓN DEL TIPO DOMICILIO CONCRETO CON CÓDIGO DE PROVINCIA INEXISTENTE.
	// -------------------------------------------------------------------------------------
	// Petición en la que el código de provincia proporcionado es incorrecto.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4681. Código de provincia no válido
	
//	@Test	// Resultat test: OK
	public void pruebaEmision31() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Codi de província no vàlid
		Enviament env = notificacio.getEnviaments().get(0);
		env.getEntregaPostal().setProvinciaCodi("99");
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
		
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(true));
			assertThat(
					info.getNotificaErrorDescripcio(),
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
	public void pruebaEmision32() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Codi de país buid
		Enviament env = notificacio.getEnviaments().get(0);
		env.getEntregaPostal().setPaisCodi(null);
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
		
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(true));
			assertThat(
					info.getNotificaErrorDescripcio(),
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
	public void pruebaEmision33() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Codi de país no vàlid
		Enviament env = notificacio.getEnviaments().get(0);
		env.getEntregaPostal().setPaisCodi("ZZ");
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
		
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(true));
			assertThat(
					info.getNotificaErrorDescripcio(),
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
	public void pruebaEmision34() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		Notificacio notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		AltaResposta respostaAlta = client.alta(notificacio);
		assertThat(
				respostaAlta.getCodiResposta(),
				is("OK"));
		assertNotNull(respostaAlta);
		List<String> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));
		
		for (String referencia: referencies) {
			InformacioResposta respostaInfo = client.consulta(referencia);
			assertNotNull(respostaInfo);
			assertThat(
					respostaInfo.getCodiResposta(),
					is("OK"));
			InformacioEnviament info = respostaInfo.getInformacioEnviament();
		
			assertNotNull(info);
			assertThat(
					info.isNotificaError(),
					is(false));
			assertNotNull(info.getIdentificador());
			assertNotNull(info.getReferencia());
			assertNotNull(info.getTitular().getNif());
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
	
//	@Test	//	Prova impossible: Notib no permet una referència nula
	public void pruebaEmision35() throws Exception {
		
		InformacioResposta respostaInfo = client.consulta(null);
		assertNotNull(respostaInfo);
		assertThat(
				respostaInfo.getCodiResposta(),
				is("OK"));
		InformacioEnviament info = respostaInfo.getInformacioEnviament();
		
		assertThat(
				info.getIdentificador(), 
				is("XXX"));		
	}
	
	// IDENTIFICADOR DE ENVÍO ERRÓNEO.
	// -------------------------------------------------------------------------------------
	// Se realizará una petición SOAP con un identificador de envío erróneo
	// -------------------------------------------------------------------------------------
	// Se comprobará que el servicio web responde con el mensaje
	// “Identificador de envío erróneo”.
	
//	@Test	//	Prova impossible: Notib controla que la referència sigui vàlida.
	public void pruebaEmision36() throws Exception {
		
		InformacioResposta respostaInfo = client.consulta("XXX");
		assertNotNull(respostaInfo);
		assertThat(
				respostaInfo.getCodiResposta(),
				is("REFERENCIA"));
		InformacioEnviament info = respostaInfo.getInformacioEnviament();
		
		assertThat(
				info.getIdentificador(), 
				is("REFERENCIA"));		
	}
	
	
			
	private Notificacio generaNotificacio(
					int numDestinataris,
					boolean ambEnviamentPostal,
					EntregaPostalTipusEnum tipusEnviamentPostal,
					boolean ambEnviamentDEH,
					boolean ambEnviamentDEHObligat,
					boolean ambRetard) throws IOException, DecoderException, DatatypeConfigurationException {
	
		String notificacioId = new Long(System.currentTimeMillis()).toString();
		Notificacio notificacio = generarNotificacio(
				notificacioId,
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat);
		
		if (!ambRetard)
			notificacio.setRetard(0);
		
//		List<String> referencies = notificacioServiceWs.alta(notificacio);
//		assertNotNull(referencies);
//		assertThat(
//				referencies.size(),
//				is(numDestinataris));
		
//		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findByNotificaReferencia(referencies.get(0));
		return notificacio;
		
	}
			
	private Notificacio generarNotificacio(
			String notificacioId,
			int numDestinataris,
			boolean ambEnviamentPostal,
			EntregaPostalTipusEnum tipusEnviamentPostal,
			boolean ambEnviamentDEH,
			boolean enviamentDEHObligat) throws IOException, DecoderException, DatatypeConfigurationException {
		byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());
		Notificacio notificacio = new Notificacio();
		notificacio.setEmisorDir3Codi(ENTITAT_DIR3CODI);
		notificacio.setEnviamentTipus(EnviamentTipusEnum.COMUNICACIO);
		notificacio.setConcepte("concepte_" + notificacioId);
		notificacio.setDescripcio("descripcio_" + notificacioId);
		notificacio.setEnviamentDataProgramada(null);
		notificacio.setRetard(5);
		notificacio.setCaducitat(
				toXmlGregorianCalendar(
						new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000)));
		Document document = new Document();
		document.setArxiuNom("documentArxiuNom_" + notificacioId + ".pdf");
		document.setContingutBase64(Base64.encodeBase64String(arxiuBytes));
		document.setHash(
				Base64.encodeBase64String(
						Hex.decodeHex(
								DigestUtils.sha256Hex(arxiuBytes).toCharArray())));
		document.setNormalitzat(false);
		document.setGenerarCsv(false);
		notificacio.setDocument(document);
		notificacio.setProcedimentCodi("0000");
		if (ambEnviamentPostal) {
			PagadorPostal pagadorPostal = new PagadorPostal();
			pagadorPostal.setDir3Codi("A04013511");
			pagadorPostal.setFacturacioClientCodi("ccFac_" + notificacioId);
			pagadorPostal.setContracteNum("pccNum_" + notificacioId);
			pagadorPostal.setContracteDataVigencia(toXmlGregorianCalendar(new Date(0)));
			notificacio.setPagadorPostal(pagadorPostal);
			PagadorCie pagadorCie = new PagadorCie();
			pagadorCie.setDir3Codi("A04013511");
			pagadorCie.setContracteDataVigencia(toXmlGregorianCalendar(new Date(0)));
			notificacio.setPagadorCie(pagadorCie);
		}
		for (int i = 0; i < numDestinataris; i++) {
			Enviament enviament = new Enviament();
			Persona titular = new Persona();
			titular.setNom("titularNom" + i);
			titular.setLlinatge1("titLlinatge1_" + i);
			titular.setLlinatge2("titLlinatge2_" + i);
			titular.setNif("00000000T");
			titular.setTelefon("666010101");
			titular.setEmail("titular@gmail.com");
			enviament.setTitular(titular);
			List<Persona> destinataris = new ArrayList<Persona>();
			Persona destinatari = new Persona();
			destinatari.setNom("destinatariNom" + i);
			destinatari.setLlinatge1("destLlinatge1_" + i);
			destinatari.setLlinatge2("destLlinatge2_" + i);
			destinatari.setNif("12345678Z");
			destinatari.setTelefon("666020202");
			destinatari.setEmail("destinatari@gmail.com");
			destinataris.add(destinatari);
			enviament.getDestinataris().add(destinatari);
			if (ambEnviamentPostal) {
				EntregaPostal entregaPostal = new EntregaPostal();
				entregaPostal.setTipus(tipusEnviamentPostal);
				if (tipusEnviamentPostal.equals(EntregaPostalTipusEnum.ESTRANGER)) {
					entregaPostal.setPaisCodi("UK");
					entregaPostal.setViaNom("Prime Minister's Office, 10 Downing Street");
					entregaPostal.setPoblacio("London");
					entregaPostal.setCodiPostal("00000");
				} else {
					entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.CALLE);
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
					entregaPostal.setProvinciaCodi("07");
					entregaPostal.setPaisCodi("ES");
					entregaPostal.setLinea1("linea1_" + i);
					entregaPostal.setLinea2("linea2_" + i);
					if (tipusEnviamentPostal.equals(EntregaPostalTipusEnum.APARTAT_CORREUS))
						entregaPostal.setApartatCorreus("0228");
				}
				entregaPostal.setCie(new Integer(8));
				enviament.setEntregaPostal(entregaPostal);
			}
			if (ambEnviamentDEH) {
				EntregaDeh entregaDeh = new EntregaDeh();
				entregaDeh.setObligat(enviamentDEHObligat);
				entregaDeh.setProcedimentCodi("0000");
				enviament.setEntregaDeh(entregaDeh);
			}
			enviament.setServeiTipus(ServeiTipusEnum.URGENT);
			notificacio.getEnviaments().add(enviament);
		}
		ParametresSeu parametresSeu = new ParametresSeu();
		parametresSeu.setExpedientSerieDocumental("0000S");
		parametresSeu.setExpedientUnitatOrganitzativa("00000000T");
		parametresSeu.setExpedientIdentificadorEni("seuExpedientIdentificadorEni_" + notificacioId);
		parametresSeu.setExpedientTitol("seuExpedientTitol_" + notificacioId);
		parametresSeu.setRegistreOficina("seuRegistreOficina_" + notificacioId);
		parametresSeu.setRegistreLlibre("seuRegistreLlibre_" + notificacioId);
		parametresSeu.setIdioma("seuIdioma_" + notificacioId);
		parametresSeu.setAvisTitol("seuAvisTitol_" + notificacioId);
		parametresSeu.setAvisText("seuAvisText_" + notificacioId);
		parametresSeu.setAvisTextMobil("seuAvisTextMobil_" + notificacioId);
		parametresSeu.setOficiTitol("seuOficiTitol_" + notificacioId);
		parametresSeu.setOficiText("seuOficiText_" + notificacioId);
		notificacio.setParametresSeu(parametresSeu);
		return notificacio;
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
//		notificacio.setDocumentHash(Base64.encodeBase64String(
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
//		notificacio.setSeuExpedientSerieDocumental("0000S");
//		notificacio.setSeuExpedientUnitatOrganitzativa("00000000T");
//		notificacio.setSeuAvisTitol("seuAvisTitol_" + notificacioId);
//		notificacio.setSeuAvisText("seuAvisText_" + notificacioId);
//		notificacio.setSeuAvisTextMobil("seuAvisTextMobil_" + notificacioId);
//		notificacio.setSeuOficiTitol("seuOficiTitol_" + notificacioId);
//		notificacio.setSeuOficiText("seuOficiText_" + notificacioId);
//		notificacio.setSeuRegistreLlibre("seuRegistreLlibre_" + notificacioId);
//		notificacio.setSeuRegistreOficina("seuRegistreOficina_" + notificacioId);
//		notificacio.setSeuIdioma("seuIdioma_" + notificacioId);
//		notificacio.setSeuExpedientTitol("seuExpedientTitol_" + notificacioId);
//		notificacio.setSeuExpedientIdentificadorEni("seuExpedientIdentificadorEni_" + notificacioId);
//
//		List<NotificacioEnviamentEntity> enviaments = new ArrayList<NotificacioEnviamentEntity>();
//		for (int i = 0; i < numDestinataris; i++) {
//
//			NotificacioEnviamentTest enviament = new NotificacioEnviamentTest();
//			enviament.setTitularNif("00000000T");
//			enviament.setServeiTipus(NotificaServeiTipusEnumDto.URGENT);
//			enviament.setNotificacio(notificacio);
//			enviament.setNotificaEstat(NotificacioDestinatariEstatEnumDto.NOTIB_PENDENT);
//			enviament.setSeuEstat(NotificacioDestinatariEstatEnumDto.NOTIB_PENDENT);
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
	
	*/
}
