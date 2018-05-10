/**
 * 
 */
package es.caib.notib.core.service.ws;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.EntitatTipusEnumDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.api.ws.notificacio.Document;
import es.caib.notib.core.api.ws.notificacio.EntregaDeh;
import es.caib.notib.core.api.ws.notificacio.EntregaPostal;
import es.caib.notib.core.api.ws.notificacio.EntregaPostalTipusEnum;
import es.caib.notib.core.api.ws.notificacio.EntregaPostalViaTipusEnum;
import es.caib.notib.core.api.ws.notificacio.Enviament;
import es.caib.notib.core.api.ws.notificacio.EnviamentEstatEnum;
import es.caib.notib.core.api.ws.notificacio.EnviamentReferencia;
import es.caib.notib.core.api.ws.notificacio.EnviamentTipusEnum;
import es.caib.notib.core.api.ws.notificacio.Notificacio;
import es.caib.notib.core.api.ws.notificacio.NotificacioServiceWs;
import es.caib.notib.core.api.ws.notificacio.PagadorCie;
import es.caib.notib.core.api.ws.notificacio.PagadorPostal;
import es.caib.notib.core.api.ws.notificacio.ParametresSeu;
import es.caib.notib.core.api.ws.notificacio.Persona;
import es.caib.notib.core.api.ws.notificacio.RespostaAlta;
import es.caib.notib.core.api.ws.notificacio.RespostaConsultaEstatEnviament;
import es.caib.notib.core.api.ws.notificacio.ServeiTipusEnum;
import es.caib.notib.core.helper.NotificaV1Helper;
import es.caib.notib.core.service.BaseServiceTest;

/**
 * Tests per al servei web de gestió de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class NotificacioServiceWsTest extends BaseServiceTest {

	private static final int NUM_DESTINATARIS = 2;
	private static final String ENTITAT_DGTIC_DIR3CODI = "A04013511";
	private static final String ENTITAT_NODGTIC_DIR3CODI = "12345678Z";

	@Autowired
	private EntitatService entitatService;
	@Autowired
	private NotificacioService notificacioService;
	@Autowired
	private NotificacioServiceWs notificacioServiceWs;

	@Autowired
	private NotificaV1Helper notificaHelper;

	private EntitatDto entitat;
	private Notificacio notificacio;
	private PermisDto permisAplicacio;



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
		entitat.setDir3Codi(ENTITAT_DGTIC_DIR3CODI);
		entitat.setActiva(true);
		permisAplicacio = new PermisDto();
		permisAplicacio.setAplicacio(true);
		permisAplicacio.setTipus(TipusEnumDto.USUARI);
		permisAplicacio.setPrincipal("apl");
		String notificacioId = new Long(System.currentTimeMillis()).toString();
		notificacio = generarNotificacio(
				notificacioId,
				NUM_DESTINATARIS,
				false);
	}

	//@Test
	public void notificacioAltaAmbError() {
		try {
			entitat.setDir3Codi(ENTITAT_NODGTIC_DIR3CODI);
			notificacio.setEmisorDir3Codi(ENTITAT_NODGTIC_DIR3CODI);
			autenticarUsuari("admin");
			EntitatDto entitatCreada = entitatService.create(entitat);
			assertNotNull(entitatCreada);
			assertNotNull(entitatCreada.getId());
			entitatService.permisUpdate(
					entitatCreada.getId(),
					permisAplicacio);
			autenticarUsuari("apl");
			RespostaAlta respostAlta = notificacioServiceWs.alta(notificacio);
			assertNotNull(respostAlta);
			assertNotNull(respostAlta.getReferencies());
			assertThat(
					respostAlta.getReferencies().size(),
					is(NUM_DESTINATARIS));
			RespostaConsultaEstatEnviament informacio = notificacioServiceWs.consultaEstatEnviament(
					respostAlta.getReferencies().get(0).getReferencia());
			comprovarInformacioEnviament(
					notificacio,
					notificacio.getEnviaments().get(0),
					informacio);
			assertThat(
					informacio.getEstat(),
					is(EnviamentEstatEnum.NOTIB_PENDENT));
			notificacioService.notificaEnviamentsPendents();
			RespostaConsultaEstatEnviament informacio2 = notificacioServiceWs.consultaEstatEnviament(
					respostAlta.getReferencies().get(0).getReferencia());
			assertThat(
					informacio2.getEstat(),
					is(EnviamentEstatEnum.NOTIB_PENDENT));
			/*assertTrue(informacio2.isNotificaError());
			assertNotNull(informacio2.getNotificaErrorData());
			assertNotNull(informacio2.getNotificaErrorDescripcio());*/
		} finally {
			entitat.setDir3Codi(ENTITAT_DGTIC_DIR3CODI);
			notificacio.setEmisorDir3Codi(ENTITAT_DGTIC_DIR3CODI);
		}
	}

	@Test
	public void notificacioAltaAmbConsulta() {
		autenticarUsuari("admin");
		EntitatDto entitatCreada = entitatService.create(entitat);
		assertNotNull(entitatCreada);
		assertNotNull(entitatCreada.getId());
		entitatService.permisUpdate(
				entitatCreada.getId(),
				permisAplicacio);
		autenticarUsuari("apl");
		RespostaAlta respostAlta = notificacioServiceWs.alta(notificacio);
		assertNotNull(respostAlta);
		assertNotNull(respostAlta.getReferencies());
		assertThat(
				respostAlta.getReferencies().size(),
				is(NUM_DESTINATARIS));
		RespostaConsultaEstatEnviament informacio = notificacioServiceWs.consultaEstatEnviament(
				respostAlta.getReferencies().get(0).getReferencia());
		comprovarInformacioEnviament(
				notificacio,
				notificacio.getEnviaments().get(0),
				informacio);
		assertThat(
				informacio.getEstat(),
				is(EnviamentEstatEnum.NOTIB_PENDENT));
		System.out.println("- Referències retornades per l'enviament de la notificació:");
		for (EnviamentReferencia referencia: respostAlta.getReferencies()) {
			System.out.println("    - " + referencia.getReferencia());
		}
		notificacioService.notificaEnviamentsPendents();
		RespostaConsultaEstatEnviament informacio2 = notificacioServiceWs.consultaEstatEnviament(
				respostAlta.getReferencies().get(0).getReferencia());
		assertThat(
				informacio2.getEstat(),
				is(EnviamentEstatEnum.NOTIB_ENVIADA));
		/*assertFalse(informacio2.isNotificaError());*/
	}

	/*/@Test
	public void notificacioAmbComunicacioSeu() {
		autenticarUsuari("admin");
		EntitatDto entitatCreada = entitatService.create(entitat);
		assertNotNull(entitatCreada);
		assertNotNull(entitatCreada.getId());
		entitatService.permisUpdate(
				entitatCreada.getId(),
				permisAplicacio);
		autenticarUsuari("apl");
		List<String> referencies = notificacio2Service.alta(notificacio);
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(NUM_DESTINATARIS));
		Notificacio consultada = notificacio2Service.consulta(referencies.get(0));
		comprovarNotificacio(
				notificacio,
				consultada,
				referencies);
		assertThat(
				consultada.getEstat(),
				is(NotificacioEstatEnum.PENDENT));
		notificacioService.notificaEnviamentsPendents();
		System.out.println("- Referències retornades per l'enviament de la notificació:");
		for (String referencia: referencies) {
			System.out.println("    - " + referencia);
		}
		Notificacio consultada2 = notificacio2Service.consulta(referencies.get(0));
		assertFalse(consultada2.isErrorNotifica());
		assertThat(
				consultada2.getEstat(),
				is(NotificacioEstatEnum.ENVIADA));
		assertNotNull(consultada2.getEnviaments());
		assertThat(consultada2.getEnviaments().size(), is(1));
		Enviament enviament = consultada.getEnviaments().get(0);
		assertThat(
				enviament.getEstat(),
				is(EnviamentEstatEnum.NOTIB_ENVIADA));
		NotificacioDestinatariDto destinatari = notificacioService.destinatariFindByReferencia(
				enviament.getReferencia());
		notificacioService.comunicacioSeu(destinatari.getId());
	}*/



	private Notificacio generarNotificacio(
			String notificacioId,
			int numDestinataris,
			boolean ambEnviamentPostal) throws IOException, DecoderException {
		byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());
		Notificacio notificacio = new Notificacio();
		notificacio.setEmisorDir3Codi(ENTITAT_DGTIC_DIR3CODI);
		notificacio.setEnviamentTipus(EnviamentTipusEnum.COMUNICACIO);
		notificacio.setConcepte(
				"concepte_" + notificacioId);
		notificacio.setDescripcio(
				"descripcio_" + notificacioId);
		notificacio.setEnviamentDataProgramada(null);
		notificacio.setRetard(5);
		notificacio.setCaducitat(
				new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000));
		Document document = new Document();
		document.setArxiuNom("documentArxiuNom_" + notificacioId + ".pdf");
		document.setContingutBase64(Base64.encodeBase64String(arxiuBytes));
		document.setHash(
				Base64.encodeBase64String(
						Hex.decodeHex(
								DigestUtils.sha1Hex(arxiuBytes).toCharArray())));
		document.setNormalitzat(false);
		document.setGenerarCsv(false);
		notificacio.setDocument(document);
		notificacio.setProcedimentCodi("0000");
		List<Enviament> enviaments = new ArrayList<Enviament>();
		if (ambEnviamentPostal) {
			PagadorPostal pagadorPostal = new PagadorPostal();
			pagadorPostal.setDir3Codi("A04013511");
			pagadorPostal.setFacturacioClientCodi("ccFac_" + notificacioId);
			pagadorPostal.setContracteNum("pccNum_" + notificacioId);
			pagadorPostal.setContracteDataVigencia(new Date(0));
			notificacio.setPagadorPostal(pagadorPostal);
			PagadorCie pagadorCie = new PagadorCie();
			pagadorCie.setDir3Codi("A04013511");
			pagadorCie.setContracteDataVigencia(new Date(0));
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
			enviament.setDestinataris(destinataris);
			if (ambEnviamentPostal) {
				EntregaPostal entregaPostal = new EntregaPostal();
				entregaPostal.setTipus(EntregaPostalTipusEnum.NACIONAL);
				entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.CALLE);
				entregaPostal.setViaNom("Bas");
				entregaPostal.setNumeroCasa("25");
				entregaPostal.setNumeroQualificador("bis");
				entregaPostal.setPuntKm("pk01");
				entregaPostal.setApartatCorreus("0228");
				entregaPostal.setPortal("portal" + i);
				entregaPostal.setEscala("escala" + i);
				entregaPostal.setPlanta("planta" + i);
				entregaPostal.setPorta("porta" + i);
				entregaPostal.setBloc("bloc" + i);
				entregaPostal.setComplement("complement" + i);
				entregaPostal.setCodiPostal("07500");
				entregaPostal.setPoblacio("poblacio" + i);
				entregaPostal.setMunicipiCodi("07033");
				entregaPostal.setProvinciaCodi("07");
				entregaPostal.setPaisCodi("ES");
				entregaPostal.setLinea1("linea1_" + i);
				entregaPostal.setLinea2("linea2_" + i);
				entregaPostal.setCie(new Integer(8));
			}
			EntregaDeh entregaDeh = new EntregaDeh();
			entregaDeh.setObligat(true);
			entregaDeh.setProcedimentCodi("0000");
			enviament.setEntregaDeh(entregaDeh);
			enviament.setServeiTipus(ServeiTipusEnum.URGENT);
			enviaments.add(enviament);
		}
		notificacio.setEnviaments(enviaments);
		ParametresSeu parametresSeu = new ParametresSeu();
		parametresSeu.setExpedientSerieDocumental(
				"0000S");
		parametresSeu.setExpedientUnitatOrganitzativa(
				"00000000T");
		parametresSeu.setExpedientIdentificadorEni(
				"seuExpedientIdentificadorEni_" + notificacioId);
		parametresSeu.setExpedientTitol(
				"seuExpedientTitol_" + notificacioId);
		parametresSeu.setRegistreOficina(
				"seuRegistreOficina_" + notificacioId);
		parametresSeu.setRegistreLlibre(
				"seuRegistreLlibre_" + notificacioId);
		parametresSeu.setIdioma(
				"seuIdioma_" + notificacioId);
		parametresSeu.setAvisTitol(
				"seuAvisTitol_" + notificacioId);
		parametresSeu.setAvisText(
				"seuAvisText_" + notificacioId);
		parametresSeu.setAvisTextMobil(
				"seuAvisTextMobil_" + notificacioId);
		parametresSeu.setOficiTitol(
				"seuOficiTitol_" + notificacioId);
		parametresSeu.setOficiText(
				"seuOficiText_" + notificacioId);
		notificacio.setParametresSeu(parametresSeu);
		return notificacio;
	}

	private void comprovarInformacioEnviament(
			Notificacio notificacio,
			Enviament enviament,
			RespostaConsultaEstatEnviament informacio) {
		/*assertThat(
				informacio.getConcepte(),
				is(notificacio.getConcepte()));
		assertThat(
				informacio.getDescripcio(),
				is(notificacio.getDescripcio()));
		assertThat(
				informacio.getEmisorDir3Codi(),
				is(notificacio.getEmisorDir3Codi()));
		assertThat(
				informacio.getEnviamentTipus(),
				is(notificacio.getEnviamentTipus()));
		assertThat(
				informacio.getDataCaducitat(),
				is(notificacio.getCaducitat()));
		assertThat(
				informacio.getRetard(),
				is(notificacio.getRetard()));
		assertThat(
				informacio.getProcedimentCodi(),
				is(notificacio.getProcedimentCodi()));
		Persona informacioTitular = informacio.getTitular();
		assertNotNull(informacioTitular);
		Persona enviamentTitular = enviament.getTitular();
		assertThat(
				informacioTitular.getNif(),
				is(enviamentTitular.getNif()));
		assertThat(
				informacioTitular.getNom(),
				is(enviamentTitular.getNom()));
		assertThat(
				informacioTitular.getLlinatge1(),
				is(enviamentTitular.getLlinatge1()));
		assertThat(
				informacioTitular.getLlinatge2(),
				is(enviamentTitular.getLlinatge2()));
		assertThat(
				informacioTitular.getTelefon(),
				is(enviamentTitular.getTelefon()));
		assertThat(
				informacioTitular.getEmail(),
				is(enviamentTitular.getEmail()));
		if (enviament.getDestinataris() != null) {
			assertNotNull(informacio.getDestinataris());
			assertThat(
					informacio.getDestinataris().size(),
					is(enviament.getDestinataris().size()));
			for (int i = 0; i < enviament.getDestinataris().size(); i++) {
				Persona destinatariEnviament = enviament.getDestinataris().get(i);
				Persona destinatariInformacio = informacio.getDestinataris().get(i);
				assertThat(
						destinatariInformacio.getNif(),
						is(destinatariEnviament.getNif()));
				assertThat(
						destinatariInformacio.getNom(),
						is(destinatariEnviament.getNom()));
				assertThat(
						destinatariInformacio.getLlinatge1(),
						is(destinatariEnviament.getLlinatge1()));
				assertThat(
						destinatariInformacio.getLlinatge2(),
						is(destinatariEnviament.getLlinatge2()));
				assertThat(
						destinatariInformacio.getTelefon(),
						is(destinatariEnviament.getTelefon()));
				assertThat(
						destinatariInformacio.getEmail(),
						is(destinatariEnviament.getEmail()));
			}
		} else {
			assertNull(informacio.getDestinataris());
		}
		if (enviament.getEntregaPostal() != null) {
			assertNotNull(informacio.getEntregaPostal());
			EntregaPostal entregaPostalEnviament = enviament.getEntregaPostal();
			EntregaPostal entregaPostalInformacio = informacio.getEntregaPostal();
			assertThat(
					entregaPostalInformacio.getTipus(),
					is(entregaPostalEnviament.getTipus()));
			assertThat(
					entregaPostalInformacio.getViaTipus(),
					is(entregaPostalEnviament.getViaTipus()));
			assertThat(
					entregaPostalInformacio.getViaNom(),
					is(entregaPostalEnviament.getViaNom()));
			assertThat(
					entregaPostalInformacio.getNumeroCasa(),
					is(entregaPostalEnviament.getNumeroCasa()));
			assertThat(
					entregaPostalInformacio.getNumeroQualificador(),
					is(entregaPostalEnviament.getNumeroQualificador()));
			assertThat(
					entregaPostalInformacio.getPuntKm(),
					is(entregaPostalEnviament.getPuntKm()));
			assertThat(
					entregaPostalInformacio.getApartatCorreus(),
					is(entregaPostalEnviament.getApartatCorreus()));
			assertThat(
					entregaPostalInformacio.getPortal(),
					is(entregaPostalEnviament.getPortal()));
			assertThat(
					entregaPostalInformacio.getEscala(),
					is(entregaPostalEnviament.getEscala()));
			assertThat(
					entregaPostalInformacio.getPlanta(),
					is(entregaPostalEnviament.getPlanta()));
			assertThat(
					entregaPostalInformacio.getPorta(),
					is(entregaPostalEnviament.getPorta()));
			assertThat(
					entregaPostalInformacio.getBloc(),
					is(entregaPostalEnviament.getBloc()));
			assertThat(
					entregaPostalInformacio.getComplement(),
					is(entregaPostalEnviament.getComplement()));
			assertThat(
					entregaPostalInformacio.getCodiPostal(),
					is(entregaPostalEnviament.getCodiPostal()));
			assertThat(
					entregaPostalInformacio.getPoblacio(),
					is(entregaPostalEnviament.getPoblacio()));
			assertThat(
					entregaPostalInformacio.getMunicipiCodi(),
					is(entregaPostalEnviament.getMunicipiCodi()));
			assertThat(
					entregaPostalInformacio.getProvinciaCodi(),
					is(entregaPostalEnviament.getProvinciaCodi()));
			assertThat(
					entregaPostalInformacio.getPaisCodi(),
					is(entregaPostalEnviament.getPaisCodi()));
			assertThat(
					entregaPostalInformacio.getLinea1(),
					is(entregaPostalEnviament.getLinea1()));
			assertThat(
					entregaPostalInformacio.getLinea2(),
					is(entregaPostalEnviament.getLinea2()));
			assertThat(
					entregaPostalInformacio.getCie(),
					is(entregaPostalEnviament.getCie()));
			assertThat(
					entregaPostalInformacio.getFormatSobre(),
					is(entregaPostalEnviament.getFormatSobre()));
			assertThat(
					entregaPostalInformacio.getFormatFulla(),
					is(entregaPostalEnviament.getFormatFulla()));
		} else {
			assertNull(informacio.getEntregaPostal());
		}
		if (enviament.getEntregaDeh() != null) {
			assertNotNull(informacio.getEntregaDeh());
			EntregaDeh entregaDehEnviament = enviament.getEntregaDeh();
			EntregaDeh entregaDehInformacio = informacio.getEntregaDeh();
			assertThat(
					entregaDehInformacio.isObligat(),
					is(entregaDehEnviament.isObligat()));
			assertThat(
					entregaDehInformacio.getProcedimentCodi(),
					is(entregaDehEnviament.getProcedimentCodi()));
		} else {
			assertNull(informacio.getEntregaDeh());
		}*/
	}

	private InputStream getContingutNotificacioAdjunt() {
		return getClass().getResourceAsStream(
				"/es/caib/notib/core/notificacio_adjunt.pdf");
	}

}
