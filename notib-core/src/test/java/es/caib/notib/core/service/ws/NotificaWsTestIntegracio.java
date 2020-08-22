/**
 * 
 */
package es.caib.notib.core.service.ws;

import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.notib.core.api.ws.callback.NotificacioCanviClient;

//import static org.hamcrest.CoreMatchers.is;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertThat;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Date;
//import java.util.HashSet;
//import java.util.Set;
//
//import org.apache.commons.codec.DecoderException;
//import org.apache.commons.codec.binary.Base64;
//import org.apache.commons.codec.binary.Hex;
//import org.apache.commons.codec.digest.DigestUtils;
//import org.apache.commons.io.IOUtils;
//import org.junit.Before;
//import org.junit.FixMethodOrder;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.rules.ExpectedException;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//import es.caib.notib.core.api.dto.EntitatDto;
//import es.caib.notib.core.api.dto.EntitatTipusEnumDto;
//import es.caib.notib.core.api.dto.NotificaDomiciliConcretTipusEnumDto;
//import es.caib.notib.core.api.dto.NotificaDomiciliNumeracioTipusEnumDto;
//import es.caib.notib.core.api.dto.NotificaDomiciliTipusEnumDto;
//import es.caib.notib.core.api.dto.NotificaDomiciliViaTipusEnumDto;
//import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
//import es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto;
//import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
//import es.caib.notib.core.api.dto.PermisDto;
//import es.caib.notib.core.api.dto.ServeiTipusEnumDto;
//import es.caib.notib.core.api.dto.TipusEnumDto;
//import es.caib.notib.core.api.ws.notificacio.EntregaPostalTipusEnum;
//import es.caib.notib.core.entity.NotificacioEntity;
//import es.caib.notib.core.entity.NotificacioEnviamentEntity;
//import es.caib.notib.core.helper.NotificaV2Helper;
//import es.caib.notib.core.helper.PluginHelper;
//import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.ResultadoAltaRemesaEnvios;
//import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.ResultadoEnvio;
//import es.caib.notib.core.wsdl.notificaV2.infoEnvioV2.ResultadoInfoEnvioV2;


/**
 * Test per al client REST del servei de notificacions de NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
//@Transactional
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NotificaWsTestIntegracio {
	
//	@Test
	public void callbackjson() throws JsonProcessingException {
		NotificacioCanviClient notificacioCanvi = new NotificacioCanviClient(
				"aaaa", 
				"bbbb");
		notificacioCanvi.setData(new Date());

		// Passa l'objecte a JSON
		ObjectMapper mapper  = new ObjectMapper();
		String body = mapper.writeValueAsString(notificacioCanvi);
		System.out.println(body);
	}
/*
	private static final String ENTITAT_DIR3CODI = "A04013511";

	@Autowired
	private NotificaV2Helper notificaHelper;
	@Autowired
	private PluginHelper pluginHelper;
	
	private EntitatDto entitat;
	private PermisDto permisAplicacio;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();


	@Before
	public void setUp() throws IOException, DecoderException {
		es.caib.notib.core.helper.PropertiesHelper.getProperties("classpath:es/caib/notib/core/test.properties");
		es.caib.notib.plugin.utils.PropertiesHelper.getProperties("classpath:es/caib/notib/core/test.properties");
		notificaHelper.setModeTest(true);
		entitat = new EntitatDto();
		entitat.setCodi("DGTIC");
		entitat.setNom("Dirección General de Desarrollo Tecnológico");
		entitat.setDescripcio("Descripció Dirección General de Desarrollo Tecnológico");
		entitat.setTipus(EntitatTipusEnumDto.GOVERN);
		entitat.setDir3Codi(ENTITAT_DIR3CODI);
		entitat.setActiva(true);
		permisAplicacio = new PermisDto();
		permisAplicacio.setAplicacio(true);
		permisAplicacio.setTipus(TipusEnumDto.USUARI);
		permisAplicacio.setPrincipal("apl");
//		String notificacioId = new Long(System.currentTimeMillis()).toString();
//		notificacio = generarNotificacio(
//				notificacioId,
//				NUM_DESTINATARIS,
//				false);
	}
	
	// PRUEBAS DE EMISIÓN – CORRECTAS
	// =====================================================================================
	
	// PETICIÓN CORRECTA TIPO CENTRO DE IMPRESIÓN, DOMICILIO CONCRETO Y NACIONAL
	// -------------------------------------------------------------------------------------
	// Para cada destinatario se comprobará que devuelve un
	// identificador, la referencia del emisor y el NIF del titular.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 000. OK
//	@Test
	public void pruebaEmision01() throws Exception {
	
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = false;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("000"));
		
		assertNotNull(resultat.getResultadoEnvios());
		assertNotNull(resultat.getResultadoEnvios().getItem());
		assertThat(
				resultat.getResultadoEnvios().getItem().size(),
				is(numDestinataris));
		
		for(ResultadoEnvio envio : resultat.getResultadoEnvios().getItem()) {
			assertNotNull(envio.getIdentificador());
			assertNotNull(envio.getNifTitular());
		}
		
	}
	
	
	
	// PETICIÓN CORRECTA DE TIPO CENTRO DE IMPRESIÓN, DOMICILIO CONCRETO E INTERNACIONAL.
	// -------------------------------------------------------------------------------------
	// Para cada destinatario se comprobará que devuelve un
	// identificador, la referencia del emisor y el NIF del titular.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 000. OK
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
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("000"));
		
		assertNotNull(resultat.getResultadoEnvios());
		assertNotNull(resultat.getResultadoEnvios().getItem());
		assertThat(
				resultat.getResultadoEnvios().getItem().size(),
				is(numDestinataris));
		
		for(ResultadoEnvio envio : resultat.getResultadoEnvios().getItem()) {
			assertNotNull(envio.getIdentificador());
			assertNotNull(envio.getNifTitular());
		}
		
	}

	
	// PETICIÓN CORRECTA DE TIPO DEH VOLUNTARIO + CIE.
	// -------------------------------------------------------------------------------------
	// Para cada destinatario se comprobará que devuelve un
	// identificador, la referencia del emisor y el NIF del titular.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 000. OK
//	@Test
	public void pruebaEmision03() throws Exception {
		
		// Petició TIPO DEH VOLUNTARIO + CIE

		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = true;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = false;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("000"));
		
		assertNotNull(resultat.getResultadoEnvios());
		assertNotNull(resultat.getResultadoEnvios().getItem());
		assertThat(
				resultat.getResultadoEnvios().getItem().size(),
				is(numDestinataris));
		
		for(ResultadoEnvio envio : resultat.getResultadoEnvios().getItem()) {
			assertNotNull(envio.getIdentificador());
			assertNotNull(envio.getNifTitular());
		}
				
	}
	
	
	// PETICIÓN CORRECTA DE TIPO DEH OBLIGADO.
	// -------------------------------------------------------------------------------------
	// Para cada destinatario se comprobará que devuelve un
	// identificador, la referencia del emisor y el NIF del titular.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 000. OK
//	@Test
	public void pruebaEmision04() throws Exception {
		
		// Petició TIPO DEH OBLIGADO
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = true;
		boolean ambEnviamentDEHObligat = true;
		boolean ambRetard = false;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("000"));
		
		assertNotNull(resultat.getResultadoEnvios());
		assertNotNull(resultat.getResultadoEnvios().getItem());
		assertThat(
				resultat.getResultadoEnvios().getItem().size(),
				is(numDestinataris));
		
		for(ResultadoEnvio envio : resultat.getResultadoEnvios().getItem()) {
			assertNotNull(envio.getIdentificador());
			assertNotNull(envio.getNifTitular());
		}
					
	}
	
	
	// PETICIÓN CORRECTA DE ENVIO SOLO CARPETA.
	// -------------------------------------------------------------------------------------
	// Para cada destinatario se comprobará que devuelve un
	// identificador, la referencia del emisor y el NIF del titular.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 000. OK
	@Test
	public void pruebaEmision05() throws Exception {
		
		// Petició DE ENVIO SOLO CARPETA
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("000"));
		
		assertNotNull(resultat.getResultadoEnvios());
		assertNotNull(resultat.getResultadoEnvios().getItem());
		assertThat(
				resultat.getResultadoEnvios().getItem().size(),
				is(numDestinataris));
		
		for(ResultadoEnvio envio : resultat.getResultadoEnvios().getItem()) {
			assertNotNull(envio.getIdentificador());
			assertNotNull(envio.getNifTitular());
		}
				
	}
	
	
	// PETICIÓN CORRECTA CON MAS DE UN DESTINATARIO.
	// -------------------------------------------------------------------------------------
	// Para cada destinatario se comprobará que devuelve un identificador, la referencia 
	// del emisor y el NIF del titular. En NotificaWS2 se haría como una remesa con varios 
	// envíos y en NotificaWS como un envio con varios destinatarios.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 000. OK
//	@Test
	public void pruebaEmision06() throws Exception {
		
		// Petició CON MAS DE UN DESTINATARIO
		
		int numDestinataris = 3;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("000"));
		
		assertNotNull(resultat.getResultadoEnvios());
		assertNotNull(resultat.getResultadoEnvios().getItem());
		assertThat(
				resultat.getResultadoEnvios().getItem().size(),
				is(numDestinataris));
		
		for(ResultadoEnvio envio : resultat.getResultadoEnvios().getItem()) {
			assertNotNull(envio.getIdentificador());
			assertNotNull(envio.getNifTitular());
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
//	@Test
	public void pruebaEmision07() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		notificacio.setEmisorDir3Codi("A00000000");
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("4011"));
		
	}
	
	
	// PETICIÓN CON EL VALOR DE PDF NORMALIZADO VACÍO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el valor del campo normalizado está sin rellenar.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4151. El campo normalizado está vacío
	
// Prova impossible: Document no permet el camp normalitzat buid.	
//	@Test
	public void pruebaEmision08() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
//		camp normalitzat a NULL
//		notificacio.setDocumentNormalitzat(null);
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("4151"));
		
	}
	
	
	// PETICIÓN CON UN VALOR DE PDF NORMALIZADO INCORRECTO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el valor del campo normalizado no es ni si ni no
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4152. El valor de normalizado no es válido, debe ser 'si' o 'no'
	
// Prova impossible: NotificaHelper únicament permet els valors "si" i "no".
//	@Test
	public void pruebaEmision09() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
//		valor del camp normalitzat incorrecte (diferent de "si" o "no")
//		notificacio.setDocumentNormalitzat("yes");
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("4152"));
		
	}
	
	
	// PETICIÓN CON NIF DEL TITULAR INCORRECTO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el NIF que se proporciona del titular es incorrecto
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4200. El documento de identificación no es válido
//	@Test
	public void pruebaEmision10() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Nif del titular no vàlid
		NotificacioEnviamentTest env = (NotificacioEnviamentTest)notificacio.getEnviaments().iterator().next();
		env.getTitular().setNif("00000000A");
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("4200"));
		
	}
	
	// PETICIÓN CON NOMBRE DEL TITULAR VACÍO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el campo nombre del titular está vacío.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4241. Indique el nombre
//	@Test
	public void pruebaEmision11() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Nom del titular buid
		NotificacioEnviamentTest env = (NotificacioEnviamentTest)notificacio.getEnviaments().iterator().next();
		env.getTitular().setNom(null);
		env.getTitular().setLlinatge1(null);
		env.getTitular().setLlinatge2(null);
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("4241"));
		
	}
	
	
	// PETICIÓN SIN DOCUMENTO. 
	// -------------------------------------------------------------------------------------
	// Se enviará una petición sin documento
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4311. Documento no puede estar vacío
	
// Prova impossible: NotificaHelper valida que el document no sigui null.	
//	@Test
	public void pruebaEmision12() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);

		// Document buid
		notificacio.getDocument().setArxiuGestdocId(null);
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("4311"));
		
	}
	
	
	// PETICIÓN EN LA QUE EL HASH ENVIADO NO COINCIDE CON EL HASH DEL DOCUMENTO.
	// -------------------------------------------------------------------------------------
	// Se enviará una petición en la que el Hash calculado del PDF no
	// coincida con el que se recibe del WS. El algoritmo que se usa es
	// SHA256 y posteriormente codificado en Base64.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4320. No se corresponde el sha1 del documento con el contenido
//	@Test
	public void pruebaEmision13() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Hash del document incorrecte
		notificacio.getDocument().setHash("AAA");
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("4320"));
		
	}
	
	
	// PETICIÓN CON UN VALOR PRIORIDAD DE SERVICIO INCORRECTO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el valor de servicio no es ni normal ni urgente.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4402. El campo Servicio solo puede contener los valores 'urgente' o 'normal'
	
// Prova impossible: ServeiTipus únicament pot ser null, NORMAL o URGENT, i el camp no existeix a la v.2.
//	@Test
	public void pruebaEmision14() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Tipus de servei no vàlid 
//		NotificacioEnviamentTest env = (NotificacioEnviamentTest)notificacio.getEnviaments().get(0);
//		env.setServeiTipus(NotificaServeiTipusEnumDto.ALTRE);
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("4402"));
		
	}
	
	
	// PETICIÓN CON EL VALOR DE PRIORIDAD DE SERVICIO VACÍO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el valor del campo servicio está vacío.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4403. Debe rellenar el campo 'servicio'
	
// Prova impossible: ServeiTipus no existeix a la v.2.
//	@Test
	public void pruebaEmision15() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Tipus de servei buid
		NotificacioEnviamentTest env = (NotificacioEnviamentTest)notificacio.getEnviaments().iterator().next();
		env.setServeiTipus(null);
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("4403"));
		
	}
	
	
	// PETICIÓN CON CONCEPTO VACÍO.
	// -------------------------------------------------------------------------------------
	// Se enviará una petición con el campo concepto vacío.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4500. Indique el concepto
//	@Test
	public void pruebaEmision16() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Concepte buit
		notificacio.setConcepte(null);
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("4500"));
		
	}
	
	// PETICIÓN CON TIPO DE ENVÍO VACÍO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el tipo de envío está vacío.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4510. Tipo de envío vacío
//	@Test
	public void pruebaEmision17() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Enviament tipus buid
		notificacio.setEnviamentTipus(null);

		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("4510"));
		
	}
	
	
	// PETICIÓN CON TIPO DE ENVÍO INCORRECTO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el tipo de envío no es ni notificación ni comunicación.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4511. Tipo de envío no permitido
	
// Prova impossible: el sistema només permet tipus d'enviament null, COMUNICACIO i NOTIFICACIO
//	@Test
	public void pruebaEmision18() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Enviament tipus no vàlid
//		notificacio.setEnviamentTipus(NotificaEnviamentTipusEnumDto.ALTRE);
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("4511"));
		
	}
	
	
	// PETICIÓN CON CÓDIGO SIA ERRÓNEO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el Código SIA enviado no es correcto o no se
	// corresponde con el emisor.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4531. Código SIA incorrecto
//	@Test
	public void pruebaEmision19() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		notificacio.getProcediment().setCodisia("XYZ");
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("4531"));
		
	}
	
	
	// PETICIÓN CON TIPO DE DOMICILIO INCORRECTO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el tipo_domicilio no es concreto ni fiscal.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4600. Tipo domicilio incorrecto
	
// Prova impossible: el sistema només permet tipus de domicili CONCRET o FISCAL
//	@Test
	public void pruebaEmision20() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Tipus domicili no vàlid
//		NotificacioEnviamentTest env = (NotificacioEnviamentTest)notificacio.getEnviaments().get(0);
//		env.setDomiciliTipus(NotificaDomiciliTipusEnumDto.ALTRE);
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("4600"));
		
	}
	
	
	// PETICIÓN DE TIPO DOMICILIO CONCRETO CON NÚMERO DE CASA Y PUNTO KILOMÉTRICO RELLENOS.
	// -------------------------------------------------------------------------------------
	// Petición en la que el tipo_domicilio es concreto pero se
	// introducen valores en ambos campos: punto_kilometrico y numero_casa.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4601. Dirección no válida, no pueden estar rellenos al mismo tiempo
	// Número de casa y Punto kilométrico
//	@Test
	public void pruebaEmision21() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Punt kilomètric emplenat (el número de casa també està emplenat)
		NotificacioEnviamentTest env = (NotificacioEnviamentTest)notificacio.getEnviaments().iterator().next();
		env.setDomiciliNumeracioPuntKm("pk01");
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("4601"));
		
	}
	
	
	// PETICIÓN CON TIPO DE DOMICILIO VACÍO.
	// -------------------------------------------------------------------------------------
	// Petición en la que no se rellena el campo tipo_domicilio.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4602. Tipo de domicilio vacío
	
//	Prova impossible: NotificaHelper no permet el camp tipus domicili buit
//	@Test
	public void pruebaEmision22() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Tipus de domicili buit
		NotificacioEnviamentTest env = (NotificacioEnviamentTest)notificacio.getEnviaments().iterator().next();
		env.setDomiciliTipus(null);
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("4602"));
		
	}
	
	
	// PETICIÓN DE TIPO DOMICILIO CONCRETO CON NOMBRE DE VÍA VACÍO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el tipo_domicilio debe ser concreto, sin
	// Apartado de Correos y en la que el Nombre de Vía está vacío
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4610. El Nombre de vía no puede estar vacío
//	@Test
	public void pruebaEmision23() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Nom de la via buit
		NotificacioEnviamentTest env = (NotificacioEnviamentTest)notificacio.getEnviaments().iterator().next();
		env.setDomiciliViaNom(null);
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("4610"));
		
	}
	
	
	// PETICIÓN DE TIPO DOMICILIO CONCRETO CON TIPO DE VÍA VACÍO
	// -------------------------------------------------------------------------------------
	// Petición en la que el campo tipo_via del Domicilio está sin rellenar.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4620. El Tipo de Vía no puede estar vacío
//	@Test
	public void pruebaEmision24() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Tipus de via buid
		NotificacioEnviamentTest env = (NotificacioEnviamentTest)notificacio.getEnviaments().iterator().next();
		env.setDomiciliViaTipus(null);
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("4620"));
		
	}
	
	
	// PETICIÓN DE TIPO DOMICILIO CONCRETO CON NÚMERO DE CASA Y PUNTO KILOMÉTRICO VACÍO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el tipo_domicilio es concreto pero no se
	// introduce ningún valor en el campo punto_kilometrico ni en el campo numero_casa....
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4630. Dirección no válida, falta Número de casa o Punto kilométrico
//	@Test
	public void pruebaEmision25() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Número de casa buit (el punt kilomètric també està buid)
		NotificacioEnviamentTest env = (NotificacioEnviamentTest)notificacio.getEnviaments().iterator().next();
		env.setDomiciliNumeracioNumero(null);
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("4630"));
		
	}
	
	
	// PETICIÓN INCORRECTA DE TIPO DOMICILIO CONCRETO Y APARTADO DE CORREOS.
	// -------------------------------------------------------------------------------------
	// Petición en la que el tipo_domicilio debe ser concreto, con
	// Apartado de Correos relleno pero faltando el Código Postal.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4650. Código Postal no puede estar vacío
//	@Test
	public void pruebaEmision26() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.APARTAT_CORREUS;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Codi postal buid
		NotificacioEnviamentTest env = (NotificacioEnviamentTest)notificacio.getEnviaments().iterator().next();
		env.setDomiciliCodiPostal(null);
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("4650"));
		
	}
	
	
	// PETICIÓN INCORRECTA DE TIPO DOMICILIO CONCRETO FALTANDO EL APARTADO DE CORREOS.
	// -------------------------------------------------------------------------------------
	// Petición en la que el tipo_domicilio debe ser concreto, pero el
	// campo apartado_correos se encuentre vacío.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4660. Apartado de correos no puede estar vacío
//	@Test
	public void pruebaEmision27() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.APARTAT_CORREUS;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Apartat de correus buit
		NotificacioEnviamentTest env = (NotificacioEnviamentTest)notificacio.getEnviaments().iterator().next();
		env.setDomiciliApartatCorreus(null);
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("4660"));
		
	}
	
	
	// PETICIÓN DEL TIPO DOMICILIO CONCRETO CON CÓDIGO DE MUNICIPIO VACÍO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el código de municipio (código INE) proporcionado está vacío.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4670. Código de municipio vacío
//	@Test
	public void pruebaEmision28() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Codi de municipi buit
		NotificacioEnviamentTest env = (NotificacioEnviamentTest)notificacio.getEnviaments().iterator().next();
		env.setDomiciliMunicipiCodiIne(null);
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("4670"));
		
	}
	
	
	// PETICIÓN DEL TIPO DOMICILIO CONCRETO CON CÓDIGO DE MUNICIPIO INCORRECTO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el código de municipio (código INE) proporcionado es incorrecto.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4671. Código de municipio (COD_MUNICIPIO) erróneo
//	@Test
	public void pruebaEmision29() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Codi de municipi no vàlid
		NotificacioEnviamentTest env = (NotificacioEnviamentTest)notificacio.getEnviaments().iterator().next();
		env.setDomiciliMunicipiCodiIne("CODI");
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("4671"));
		
	}
	
	
	// PETICIÓN DEL TIPO DOMICILIO CONCRETO CON CÓDIGO DE PROVINCIA VACÍO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el código de provincia proporcionado está vacío.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4680. Código de provincia vacío
//	@Test
	public void pruebaEmision30() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// codi de provincia buit
		NotificacioEnviamentTest env = (NotificacioEnviamentTest)notificacio.getEnviaments().iterator().next();
		env.setDomiciliProvinciaCodi(null);
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("4680"));
		
	}
	
	
	// PETICIÓN DEL TIPO DOMICILIO CONCRETO CON CÓDIGO DE PROVINCIA INEXISTENTE.
	// -------------------------------------------------------------------------------------
	// Petición en la que el código de provincia proporcionado es incorrecto.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4681. Código de provincia no válido
//	@Test
	public void pruebaEmision31() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Codi de província no vàlid
		NotificacioEnviamentTest env = (NotificacioEnviamentTest)notificacio.getEnviaments().iterator().next();
		env.setDomiciliProvinciaCodi("CODI");
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("4681"));
		
	}
	
	
	// PETICIÓN DEL TIPO DOMICILIO CONCRETO CON CÓDIGO DE PAÍS VACÍO.
	// -------------------------------------------------------------------------------------
	// Petición en la que el código de país (ISO 3166) proporcionado está vacío.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4690. Código de país vacío
//	@Test
	public void pruebaEmision32() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Codi de país buid
		NotificacioEnviamentTest env = (NotificacioEnviamentTest)notificacio.getEnviaments().iterator().next();
		env.setDomiciliPaisCodiIso(null);
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("4690"));
		
	}
	
	
	// PETICIÓN DEL TIPO DOMICILIO CONCRETO CON CÓDIGO DE PAÍS INEXISTENTE.
	// -------------------------------------------------------------------------------------
	// Petición en la que el código de país (ISO 3166) proporcionado es incorrecto.
	// -------------------------------------------------------------------------------------
	// Código devuelto: 4691. Código de país no válido
//	@Test
	public void pruebaEmision33() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		// Codi de país no vàlid
		NotificacioEnviamentTest env = (NotificacioEnviamentTest)notificacio.getEnviaments().iterator().next();
		env.setDomiciliPaisCodiIso("CODI");
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("4691"));
		
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
//	@Test
	public void pruebaEmision34() throws Exception {
		
		int numDestinataris = 1;
		boolean ambEnviamentPostal = false;
		EntregaPostalTipusEnum tipusEnviamentPostal = EntregaPostalTipusEnum.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;
		
		NotificacioTest notificacio = generaNotificacio(
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);
		
		ResultadoAltaRemesaEnvios resultat = notificaHelper.enviaNotificacio(notificacio);
		
		assertNotNull(resultat);
		assertThat(
				resultat.getCodigoRespuesta(),
				is("000"));
		
		NotificacioEnviamentEntity enviament = new NotificacioEnviamentEntity();
		String identificador = resultat.getResultadoEnvios().getItem().iterator().next().getIdentificador();
		enviament.updateNotificaIdentificador(identificador);
		ResultadoInfoEnvioV2 info = notificaHelper.infoEnviament(enviament);
		
		assertThat(
				info.getIdentificador(), 
				is(identificador));
		
	}
	
	// PRUEBA DE CONSULTA INFORAMACION ENVIO – ERRÓNEAS
	// =====================================================================================

	// IDENTIFICADOR DE ENVÍO VACÍO.
	// -------------------------------------------------------------------------------------
	// Se realizará una petición SOAP con un identificador de envío vacío.
	// -------------------------------------------------------------------------------------
	// Se comprobará que el servicio web responde con el mensaje 
	// “Identificador de envío vacío”.
//	@Test
	public void pruebaEmision35() throws Exception {
		
		NotificacioEnviamentEntity enviament = new NotificacioEnviamentEntity();
		ResultadoInfoEnvioV2 resultat = notificaHelper.infoEnviament(enviament);
		
		assertThat(
				resultat.getDescripcion(), 
				is("Identificador de envío vacío"));
		
	}
	
	// IDENTIFICADOR DE ENVÍO ERRÓNEO.
	// -------------------------------------------------------------------------------------
	// Se realizará una petición SOAP con un identificador de envío erróneo
	// -------------------------------------------------------------------------------------
	// Se comprobará que el servicio web responde con el mensaje
	// “Identificador de envío erróneo”.
//	@Test
	public void pruebaEmision36() throws Exception {
		
		NotificacioEnviamentEntity enviament = new NotificacioEnviamentEntity();
		enviament.updateNotificaIdentificador("XXX");
		ResultadoInfoEnvioV2 resultat = notificaHelper.infoEnviament(enviament);
		
		assertThat(
				resultat.getDescripcion(), 
				is("Identificador de envío erróneo"));
		
	}
	
	
	
			
	private NotificacioTest generaNotificacio(
					int numDestinataris,
					boolean ambEnviamentPostal,
					EntregaPostalTipusEnum tipusEnviamentPostal,
					boolean ambEnviamentDEH,
					boolean ambEnviamentDEHObligat,
					boolean ambRetard) throws IOException, DecoderException {
	
		String notificacioId = new Long(System.currentTimeMillis()).toString();
		NotificacioTest notificacio = generarNotificacio(
				notificacioId,
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat);
		
		if (!ambRetard)
			notificacio.setRetardPostal(0);
		
//		List<String> referencies = notificacioServiceWs.alta(notificacio);
//		assertNotNull(referencies);
//		assertThat(
//				referencies.size(),
//				is(numDestinataris));
		
//		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findByNotificaReferencia(referencies.get(0));
		return notificacio;
		
	}
			
			
//	private Notificacio generarNotificacio(
//			String notificacioId,
//			int numDestinataris,
//			boolean ambEnviamentPostal,
//			EntregaPostalTipusEnum tipusEnviamentPostal,
//			boolean ambEnviamentDEH,
//			boolean enviamentDEHObligat) throws IOException, DecoderException {
//		byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());
//		Notificacio notificacio = new Notificacio();
//		notificacio.setEmisorDir3Codi(ENTITAT_DIR3CODI);
//		notificacio.setEnviamentTipus(EnviamentTipusEnum.COMUNICACIO);
//		notificacio.setConcepte(
//				"concepte_" + notificacioId);
//		notificacio.setDescripcio(
//				"descripcio_" + notificacioId);
//		notificacio.setEnviamentDataProgramada(null);
//		notificacio.setRetard(5);
//		notificacio.setCaducitat(
//				new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000));
//		Document document = new Document();
//		document.setArxiuNom("documentArxiuNom_" + notificacioId + ".pdf");
//		document.setContingutBase64(Base64.encodeBase64String(arxiuBytes));
//		document.setHash(
//				Base64.encodeBase64String(
//						Hex.decodeHex(
//								DigestUtils.sha1Hex(arxiuBytes).toCharArray())));
//		document.setNormalitzat(false);
//		document.setGenerarCsv(false);
//		notificacio.setDocument(document);
//		notificacio.setProcedimentCodi("0000");
//		List<Enviament> enviaments = new ArrayList<Enviament>();
//		if (ambEnviamentPostal) {
//			PagadorPostal pagadorPostal = new PagadorPostal();
//			pagadorPostal.setDir3Codi("A04013511");
//			pagadorPostal.setFacturacioClientCodi("ccFac_" + notificacioId);
//			pagadorPostal.setContracteNum("pccNum_" + notificacioId);
//			pagadorPostal.setContracteDataVigencia(new Date(0));
//			notificacio.setPagadorPostal(pagadorPostal);
//			PagadorCie pagadorCie = new PagadorCie();
//			pagadorCie.setDir3Codi("A04013511");
//			pagadorCie.setContracteDataVigencia(new Date(0));
//			notificacio.setPagadorCie(pagadorCie);
//		}
//		for (int i = 0; i < numDestinataris; i++) {
//			Enviament enviament = new Enviament();
//			Persona titular = new Persona();
//			titular.setNom("titularNom" + i);
//			titular.setLlinatge1("titLlinatge1_" + i);
//			titular.setLlinatge2("titLlinatge2_" + i);
//			titular.setNif("00000000T");
//			titular.setTelefon("666010101");
//			titular.setEmail("titular@gmail.com");
//			enviament.setTitular(titular);
//			List<Persona> destinataris = new ArrayList<Persona>();
//			Persona destinatari = new Persona();
//			destinatari.setNom("destinatariNom" + i);
//			destinatari.setLlinatge1("destLlinatge1_" + i);
//			destinatari.setLlinatge2("destLlinatge2_" + i);
//			destinatari.setNif("12345678Z");
//			destinatari.setTelefon("666020202");
//			destinatari.setEmail("destinatari@gmail.com");
//			destinataris.add(destinatari);
//			enviament.setDestinataris(destinataris);
//			if (ambEnviamentPostal) {
//				EntregaPostal entregaPostal = new EntregaPostal();
//				entregaPostal.setTipus(tipusEnviamentPostal);
//				if (tipusEnviamentPostal.equals(EntregaPostalTipusEnum.ESTRANGER)) {
//					entregaPostal.setPaisCodi("UK");
//					entregaPostal.setViaNom("Prime Minister's Office, 10 Downing Street");
//					entregaPostal.setPoblacio("London");
//					entregaPostal.setCodiPostal("00000");
//				} else {
//					entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.CALLE);
//					entregaPostal.setViaNom("Bas");
//					entregaPostal.setNumeroCasa("25");
//					entregaPostal.setNumeroQualificador("bis");
//	//				entregaPostal.setPuntKm("pk01");
//					entregaPostal.setApartatCorreus("0228");
//					entregaPostal.setPortal("portal" + i);
//					entregaPostal.setEscala("escala" + i);
//					entregaPostal.setPlanta("planta" + i);
//					entregaPostal.setPorta("porta" + i);
//					entregaPostal.setBloc("bloc" + i);
//					entregaPostal.setComplement("complement" + i);
//					entregaPostal.setCodiPostal("07500");
//					entregaPostal.setPoblacio("poblacio" + i);
//					entregaPostal.setMunicipiCodi("07033");
//					entregaPostal.setProvinciaCodi("07");
//					entregaPostal.setPaisCodi("ES");
//					entregaPostal.setLinea1("linea1_" + i);
//					entregaPostal.setLinea2("linea2_" + i);
//				}
//				entregaPostal.setCie(new Integer(8));
//				enviament.setEntregaPostal(entregaPostal);
//			}
//			if (ambEnviamentDEH) {
//				EntregaDeh entregaDeh = new EntregaDeh();
//				entregaDeh.setObligat(enviamentDEHObligat);
//				entregaDeh.setProcedimentCodi("0000");
//				enviament.setEntregaDeh(entregaDeh);
//			}
//			enviament.setServeiTipus(ServeiTipusEnum.URGENT);
//			enviaments.add(enviament);
//		}
//		notificacio.setEnviaments(enviaments);
//		ParametresSeu parametresSeu = new ParametresSeu();
//		parametresSeu.setExpedientSerieDocumental(
//				"0000S");
//		parametresSeu.setExpedientUnitatOrganitzativa(
//				"00000000T");
//		parametresSeu.setExpedientIdentificadorEni(
//				"seuExpedientIdentificadorEni_" + notificacioId);
//		parametresSeu.setExpedientTitol(
//				"seuExpedientTitol_" + notificacioId);
//		parametresSeu.setRegistreOficina(
//				"seuRegistreOficina_" + notificacioId);
//		parametresSeu.setRegistreLlibre(
//				"seuRegistreLlibre_" + notificacioId);
//		parametresSeu.setIdioma(
//				"seuIdioma_" + notificacioId);
//		parametresSeu.setAvisTitol(
//				"seuAvisTitol_" + notificacioId);
//		parametresSeu.setAvisText(
//				"seuAvisText_" + notificacioId);
//		parametresSeu.setAvisTextMobil(
//				"seuAvisTextMobil_" + notificacioId);
//		parametresSeu.setOficiTitol(
//				"seuOficiTitol_" + notificacioId);
//		parametresSeu.setOficiText(
//				"seuOficiText_" + notificacioId);
//		notificacio.setParametresSeu(parametresSeu);
//		return notificacio;
//	}
	
	public NotificacioTest generarNotificacio(
			String notificacioId,
			int numDestinataris,
			boolean ambEnviamentPostal,
			EntregaPostalTipusEnum tipusEnviamentPostal,
			boolean ambEnviamentDEH,
			boolean enviamentDEHObligat) throws IOException, DecoderException {
		
		byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());
		String documentGesdocId = pluginHelper.gestioDocumentalCreate(
				PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
				new ByteArrayInputStream(arxiuBytes));
		
		NotificacioTest notificacio = new NotificacioTest();
		
		notificacio.setEmisorDir3Codi(ENTITAT_DIR3CODI);
		notificacio.setEnviamentTipus(NotificaEnviamentTipusEnumDto.COMUNICACIO);
		notificacio.setEnviamentDataProgramada(null);
		notificacio.setConcepte("concepte_" + notificacioId);
		notificacio.getDocument().setArxiuNom("documentArxiuNom_" + notificacioId + ".pdf");
		notificacio.getDocument().setArxiuGestdocId(documentGesdocId);
		notificacio.getDocument().setHash(Base64.encodeBase64String(
				Hex.decodeHex(
						DigestUtils.sha256Hex(arxiuBytes).toCharArray())));
		notificacio.getDocument().setNormalitzat(false);
		notificacio.getDocument().setGenerarCsv(false);
		notificacio.setEstat(NotificacioEstatEnumDto.PENDENT);
		notificacio.setDescripcio("descripcio_" + notificacioId);
		notificacio.setCaducitat(new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000));
		notificacio.setRetardPostal(5);
		notificacio.getProcediment().setCodisia("0000");
		
		if (ambEnviamentPostal) {
			notificacio.getPagadorPostal().setDir3codi("A04013511");
			notificacio.getPagadorPostal().setContracteNum("pccNum_" + notificacioId);
			notificacio.getPagadorPostal().setFacturacioClientCodi("ccFac_" + notificacioId);
			notificacio.getPagadorPostal().setContracteDataVig(new Date(0));
			notificacio.getPagadorCie().setDir3codi("A04013511");
			notificacio.getPagadorCie().setContracteDataVig(new Date(0));
		}
		
		notificacio.setSeuExpedientSerieDocumental("0000S");
		notificacio.setSeuExpedientUnitatOrganitzativa("00000000T");
		notificacio.setSeuAvisTitol("seuAvisTitol_" + notificacioId);
		notificacio.setSeuAvisText("seuAvisText_" + notificacioId);
		notificacio.setSeuAvisTextMobil("seuAvisTextMobil_" + notificacioId);
		notificacio.setSeuOficiTitol("seuOficiTitol_" + notificacioId);
		notificacio.setSeuOficiText("seuOficiText_" + notificacioId);
		notificacio.setSeuRegistreLlibre("seuRegistreLlibre_" + notificacioId);
		notificacio.setSeuRegistreOficina("seuRegistreOficina_" + notificacioId);
		notificacio.setSeuIdioma("seuIdioma_" + notificacioId);
		notificacio.setSeuExpedientTitol("seuExpedientTitol_" + notificacioId);
		notificacio.setSeuExpedientIdentificadorEni("seuExpedientIdentificadorEni_" + notificacioId);

		Set<NotificacioEnviamentEntity> enviaments = new HashSet<NotificacioEnviamentEntity>();
		for (int i = 0; i < numDestinataris; i++) {

			NotificacioEnviamentTest enviament = new NotificacioEnviamentTest();
			enviament.getTitular().setNif("00000000T");
			enviament.setServeiTipus(ServeiTipusEnumDto.URGENT);
			enviament.setNotificacio(notificacio);
			enviament.setNotificaEstat(NotificacioEnviamentEstatEnumDto.NOTIB_PENDENT);
//			enviament.setSeuEstat(NotificacioEnviamentEstatEnumDto.NOTIB_PENDENT);
			enviament.getTitular().setNom("titularNom" + i);
			enviament.getTitular().setLlinatge1("titLlinatge1_" + i);
			enviament.getTitular().setLlinatge2("titLlinatge2_" + i);
			enviament.getTitular().setTelefon("666010101");
			enviament.getTitular().setEmail("titular@gmail.com");
			
			enviament.getDestinataris().iterator().next().setNif("12345678Z");
			enviament.getDestinataris().iterator().next().setNom("destinatariNom" + i);
			enviament.getDestinataris().iterator().next().setLlinatge1("destLlinatge1_" + i);
			enviament.getDestinataris().iterator().next().setLlinatge2("destLlinatge2_" + i);
			enviament.getDestinataris().iterator().next().setTelefon("666020202");
			enviament.getDestinataris().iterator().next().setEmail("destinatari@gmail.com");
			
			if (ambEnviamentPostal) {
				
				NotificaDomiciliTipusEnumDto tipus = null;
				NotificaDomiciliConcretTipusEnumDto tipusConcret = null;

				switch (tipusEnviamentPostal) {
					case APARTAT_CORREUS:
						tipusConcret = NotificaDomiciliConcretTipusEnumDto.APARTAT_CORREUS;
						break;
					case ESTRANGER:
						tipusConcret = NotificaDomiciliConcretTipusEnumDto.ESTRANGER;
						break;
					case NACIONAL:
						tipusConcret = NotificaDomiciliConcretTipusEnumDto.NACIONAL;
						break;
					case SENSE_NORMALITZAR:
						tipusConcret = NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR;
						break;
				}
				tipus = NotificaDomiciliTipusEnumDto.CONCRETO;
				
				enviament.setDomiciliTipus(tipus);
				enviament.setDomiciliConcretTipus(tipusConcret);
				
				if (tipusEnviamentPostal.equals(EntregaPostalTipusEnum.ESTRANGER)) {
					enviament.setDomiciliPaisCodiIso("UK");
					enviament.setDomiciliViaNom("Prime Minister's Office, 10 Downing Street");
					enviament.setDomiciliPoblacio("London");
					enviament.setDomiciliCodiPostal("00000");
					enviament.setDomiciliNumeracioTipus(NotificaDomiciliNumeracioTipusEnumDto.SENSE_NUMERO);
				} else {
					enviament.setDomiciliViaTipus(NotificaDomiciliViaTipusEnumDto.CALLE);
					enviament.setDomiciliViaNom("Bas");
					enviament.setDomiciliNumeracioNumero("25");
					enviament.setDomiciliBloc("B" + i);
					enviament.setDomiciliPortal("P" + i);
					enviament.setDomiciliEscala("E" + i);
					enviament.setDomiciliPlanta("PL" + i);
					enviament.setDomiciliPorta("PT" + i);
					enviament.setDomiciliComplement("complement" + i);
					enviament.setDomiciliCodiPostal("07500");
					enviament.setDomiciliPoblacio("poblacio" + i);
					enviament.setDomiciliMunicipiCodiIne("070337");
					enviament.setDomiciliProvinciaCodi("07");
					enviament.setDomiciliPaisCodiIso("ES");
					enviament.setDomiciliLinea1("linea1_" + i);
					enviament.setDomiciliLinea2("linea2_" + i);
//					enviament.setDomiciliNumeracioPuntKm(domiciliNumeracioPuntKm);
					if (tipusEnviamentPostal.equals(EntregaPostalTipusEnum.APARTAT_CORREUS)) {
						enviament.setDomiciliNumeracioTipus(NotificaDomiciliNumeracioTipusEnumDto.APARTAT_CORREUS);
						enviament.setDomiciliApartatCorreus("0228");
					} else {
						enviament.setDomiciliNumeracioTipus(NotificaDomiciliNumeracioTipusEnumDto.NUMERO);
					}
				}
				enviament.setDomiciliCie(new Integer(8));
			}

			if (ambEnviamentDEH) {
				enviament.setDehObligat(enviamentDEHObligat);
				enviament.setDehNif("00000000T");
				enviament.setDehProcedimentCodi("0000");
			}
			
			enviament.setNotificaReferencia(notificacioId + "_" + i);
			enviaments.add(enviament);
		}
		notificacio.setEnviaments(enviaments);
		return notificacio;
	}


	private InputStream getContingutNotificacioAdjunt() {
		return getClass().getResourceAsStream(
				"/es/caib/notib/core/notificacio_adjunt.pdf");
	}
	
	
	private class NotificacioTest extends NotificacioEntity {
		
		private static final long serialVersionUID = 1L;
		
		public void setEnviamentDataProgramada(Date enviamentDataProgramada) {
			this.enviamentDataProgramada = enviamentDataProgramada;
		}
		public void setEnviamentTipus(NotificaEnviamentTipusEnumDto enviamentTipus) {
			this.enviamentTipus = enviamentTipus;
		}
		public void setConcepte(String concepte) {
			this.concepte = concepte;
		}
//		public void setDocumentGenerarCsv(boolean documentGenerarCsv) {
//			this.documentGenerarCsv = documentGenerarCsv;
//		}
//		public void setDocumentNormalitzat(boolean documentNormalitzat) {
//			this.documentNormalitzat = documentNormalitzat;
//		}
//		public void setDocumentHash(String documentHash) {
//			this.documentHash = documentHash;
//		}
//		public void setDocumentArxiuId(String documentArxiuId) {
//			this.documentArxiuId = documentArxiuId;
//		}
//		public void setDocumentArxiuNom(String documentArxiuNom) {
//			this.documentArxiuNom = documentArxiuNom;
//		}
		public void setEmisorDir3Codi(String emisorDir3Codi) {
			this.emisorDir3Codi = emisorDir3Codi;
		}
		public void setDescripcio(String descripcio) {
			this.descripcio = descripcio;
		}
//		public void setPagadorCorreusCodiDir3(String pagadorCorreusCodiDir3) {
//			this.pagadorCorreusCodiDir3 = pagadorCorreusCodiDir3;
//		}
//		public void setPagadorCorreusContracteNum(String pagadorCorreusContracteNum) {
//			this.pagadorCorreusContracteNum = pagadorCorreusContracteNum;
//		}
//		public void setPagadorCorreusCodiClientFacturacio(String pagadorCorreusCodiClientFacturacio) {
//			this.pagadorCorreusCodiClientFacturacio = pagadorCorreusCodiClientFacturacio;
//		}
//		public void setPagadorCorreusDataVigencia(Date pagadorCorreusDataVigencia) {
//			this.pagadorCorreusDataVigencia = pagadorCorreusDataVigencia;
//		}
//		public void setPagadorCieCodiDir3(String pagadorCieCodiDir3) {
//			this.pagadorCieCodiDir3 = pagadorCieCodiDir3;
//		}
//		public void setPagadorCieDataVigencia(Date pagadorCieDataVigencia) {
//			this.pagadorCieDataVigencia = pagadorCieDataVigencia;
//		}
//		public void setProcedimentCodiSia(String procedimentCodiSia) {
//			this.procedimentCodiSia = procedimentCodiSia;
//		}
		public void setRetardPostal(Integer retardPostal) {
			this.retardPostal = retardPostal;
		}
		public void setCaducitat(Date caducitat) {
			this.caducitat = caducitat;
		}
		public void setSeuExpedientSerieDocumental(String seuExpedientSerieDocumental) {
			this.seuExpedientSerieDocumental = seuExpedientSerieDocumental;
		}
		public void setSeuExpedientUnitatOrganitzativa(String seuExpedientUnitatOrganitzativa) {
			this.seuExpedientUnitatOrganitzativa = seuExpedientUnitatOrganitzativa;
		}
		public void setSeuExpedientIdentificadorEni(String seuExpedientIdentificadorEni) {
			this.seuExpedientIdentificadorEni = seuExpedientIdentificadorEni;
		}
		public void setSeuExpedientTitol(String seuExpedientTitol) {
			this.seuExpedientTitol = seuExpedientTitol;
		}
		public void setSeuRegistreOficina(String seuRegistreOficina) {
			this.seuRegistreOficina = seuRegistreOficina;
		}
		public void setSeuRegistreLlibre(String seuRegistreLlibre) {
			this.seuRegistreLlibre = seuRegistreLlibre;
		}
		public void setSeuIdioma(String seuIdioma) {
			this.seuIdioma = seuIdioma;
		}
		public void setSeuAvisTitol(String seuAvisTitol) {
			this.seuAvisTitol = seuAvisTitol;
		}
		public void setSeuAvisText(String seuAvisText) {
			this.seuAvisText = seuAvisText;
		}
		public void setSeuAvisTextMobil(String seuAvisTextMobil) {
			this.seuAvisTextMobil = seuAvisTextMobil;
		}
		public void setSeuOficiTitol(String seuOficiTitol) {
			this.seuOficiTitol = seuOficiTitol;
		}
		public void setSeuOficiText(String seuOficiText) {
			this.seuOficiText = seuOficiText;
		}
		public void setEstat(NotificacioEstatEnumDto estat) {
			this.estat = estat;
		}
		public void setEnviaments(Set<NotificacioEnviamentEntity> enviaments) {
			this.enviaments = enviaments;
		}
	}
	private class NotificacioEnviamentTest extends NotificacioEnviamentEntity {
		
		private static final long serialVersionUID = 1L;
		
//		public void setTitularNom(String titularNom) {
//			this.titularNom = titularNom;
//		}
//		public void setTitularLlinatge1(String titularLlinatge1) {
//			this.titularLlinatge1 = titularLlinatge1;
//		}
//		public void setTitularLlinatge2(String titularLlinatge2) {
//			this.titularLlinatge2 = titularLlinatge2;
//		}
//		public void setTitularNif(String titularNif) {
//			this.titularNif = titularNif;
//		}
//		public void setTitularTelefon(String titularTelefon) {
//			this.titularTelefon = titularTelefon;
//		}
//		public void setTitularEmail(String titularEmail) {
//			this.titularEmail = titularEmail;
////		}
//		public void setDestinatariNom(String destinatariNom) {
//			this.destinatariNom = destinatariNom;
//		}
//		public void setDestinatariLlinatge1(String destinatariLlinatge1) {
//			this.destinatariLlinatge1 = destinatariLlinatge1;
//		}
//		public void setDestinatariLlinatge2(String destinatariLlinatge2) {
//			this.destinatariLlinatge2 = destinatariLlinatge2;
//		}
//		public void setDestinatariNif(String destinatariNif) {
//			this.destinatariNif = destinatariNif;
//		}
//		public void setDestinatariTelefon(String destinatariTelefon) {
//			this.destinatariTelefon = destinatariTelefon;
//		}
//		public void setDestinatariEmail(String destinatariEmail) {
//			this.destinatariEmail = destinatariEmail;
//		}
		public void setDomiciliTipus(NotificaDomiciliTipusEnumDto domiciliTipus) {
			this.domiciliTipus = domiciliTipus;
		}
		public void setDomiciliConcretTipus(NotificaDomiciliConcretTipusEnumDto domiciliConcretTipus) {
			this.domiciliConcretTipus = domiciliConcretTipus;
		}
		public void setDomiciliViaTipus(NotificaDomiciliViaTipusEnumDto domiciliViaTipus) {
			this.domiciliViaTipus = domiciliViaTipus;
		}
		public void setDomiciliViaNom(String domiciliViaNom) {
			this.domiciliViaNom = domiciliViaNom;
		}
		public void setDomiciliNumeracioTipus(NotificaDomiciliNumeracioTipusEnumDto domiciliNumeracioTipus) {
			this.domiciliNumeracioTipus = domiciliNumeracioTipus;
		}
		public void setDomiciliNumeracioNumero(String domiciliNumeracioNumero) {
			this.domiciliNumeracioNumero = domiciliNumeracioNumero;
		}
		public void setDomiciliNumeracioPuntKm(String domiciliNumeracioPuntKm) {
			this.domiciliNumeracioPuntKm = domiciliNumeracioPuntKm;
		}
		public void setDomiciliApartatCorreus(String domiciliApartatCorreus) {
			this.domiciliApartatCorreus = domiciliApartatCorreus;
		}
		public void setDomiciliBloc(String domiciliBloc) {
			this.domiciliBloc = domiciliBloc;
		}
		public void setDomiciliPortal(String domiciliPortal) {
			this.domiciliPortal = domiciliPortal;
		}
		public void setDomiciliEscala(String domiciliEscala) {
			this.domiciliEscala = domiciliEscala;
		}
		public void setDomiciliPlanta(String domiciliPlanta) {
			this.domiciliPlanta = domiciliPlanta;
		}
		public void setDomiciliPorta(String domiciliPorta) {
			this.domiciliPorta = domiciliPorta;
		}
		public void setDomiciliComplement(String domiciliComplement) {
			this.domiciliComplement = domiciliComplement;
		}
		public void setDomiciliPoblacio(String domiciliPoblacio) {
			this.domiciliPoblacio = domiciliPoblacio;
		}
		public void setDomiciliMunicipiCodiIne(String domiciliMunicipiCodiIne) {
			this.domiciliMunicipiCodiIne = domiciliMunicipiCodiIne;
		}
		public void setDomiciliCodiPostal(String domiciliCodiPostal) {
			this.domiciliCodiPostal = domiciliCodiPostal;
		}
		public void setDomiciliProvinciaCodi(String domiciliProvinciaCodi) {
			this.domiciliProvinciaCodi = domiciliProvinciaCodi;
		}
		public void setDomiciliPaisCodiIso(String domiciliPaisCodiIso) {
			this.domiciliPaisCodiIso = domiciliPaisCodiIso;
		}
		public void setDomiciliLinea1(String domiciliLinea1) {
			this.domiciliLinea1 = domiciliLinea1;
		}
		public void setDomiciliLinea2(String domiciliLinea2) {
			this.domiciliLinea2 = domiciliLinea2;
		}
		public void setDomiciliCie(Integer domiciliCie) {
			this.domiciliCie = domiciliCie;
		}
		public void setDehObligat(Boolean dehObligat) {
			this.dehObligat = dehObligat;
		}
		public void setDehNif(String dehNif) {
			this.dehNif = dehNif;
		}
		public void setDehProcedimentCodi(String dehProcedimentCodi) {
			this.dehProcedimentCodi = dehProcedimentCodi;
		}
		public void setServeiTipus(ServeiTipusEnumDto serveiTipus) {
			this.serveiTipus = serveiTipus;
		}
		public void setNotificaReferencia(String notificaReferencia) {
			this.notificaReferencia = notificaReferencia;
		}
		public void setNotificaEstat(NotificacioEnviamentEstatEnumDto notificaEstat) {
			this.notificaEstat = notificaEstat;
		}
//		public void setSeuEstat(NotificacioEnviamentEstatEnumDto seuEstat) {
//			this.seuEstat = seuEstat;
//		}
		public void setNotificacio(NotificacioEntity notificacio) {
			this.notificacio = notificacio;
		}
	}
	*/
}
