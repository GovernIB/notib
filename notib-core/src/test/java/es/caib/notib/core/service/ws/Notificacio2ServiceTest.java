/**
 * 
 */
package es.caib.notib.core.service.ws;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
import es.caib.notib.core.api.ws.notificacio2.DomiciliConcretTipusEnum;
import es.caib.notib.core.api.ws.notificacio2.DomiciliNumeracioTipusEnum;
import es.caib.notib.core.api.ws.notificacio2.DomiciliTipusEnum;
import es.caib.notib.core.api.ws.notificacio2.EnviamentTipusEnum;
import es.caib.notib.core.api.ws.notificacio2.ErrorOrigenEnum;
import es.caib.notib.core.api.ws.notificacio2.Notificacio;
import es.caib.notib.core.api.ws.notificacio2.Notificacio2Service;
import es.caib.notib.core.api.ws.notificacio2.NotificacioEnviament;
import es.caib.notib.core.api.ws.notificacio2.NotificacioEnviamentEstatEnum;
import es.caib.notib.core.api.ws.notificacio2.NotificacioEstatEnum;
import es.caib.notib.core.api.ws.notificacio2.ServeiTipusEnum;
import es.caib.notib.core.helper.NotificaHelper;
import es.caib.notib.core.service.BaseServiceTest;

/**
 * Tests per al servei web de gestió de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class Notificacio2ServiceTest extends BaseServiceTest {

	private static final int NUM_DESTINATARIS = 2;
	private static final String ENTITAT_DIR3CODI = "A04013511";

	@Autowired
	private EntitatService entitatService;
	@Autowired
	private NotificacioService notificacioService;
	@Autowired
	private Notificacio2Service notificacio2Service;

	@Autowired
	private NotificaHelper notificaHelper;

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
		entitat.setDir3Codi(ENTITAT_DIR3CODI);
		entitat.setActiva(true);
		permisAplicacio = new PermisDto();
		permisAplicacio.setAplicacio(true);
		permisAplicacio.setTipus(TipusEnumDto.USUARI);
		permisAplicacio.setPrincipal("apl");
		String notificacioId = new Long(System.currentTimeMillis()).toString();
		generarNotificacio(
				notificacioId,
				NUM_DESTINATARIS,
				false);
	}

	//@Test
	public void notificacioAltaAmbError() {
		try {
			entitat.setDir3Codi("12345678Z");
			autenticarUsuari("admin");
			EntitatDto entitatCreada = entitatService.create(entitat);
			assertNotNull(entitatCreada);
			assertNotNull(entitatCreada.getId());
			entitatService.permisUpdate(
					entitatCreada.getId(),
					permisAplicacio);
			autenticarUsuari("apl");
			List<String> referencies = notificacio2Service.alta(
					notificacio);
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
			Notificacio consultada2 = notificacio2Service.consulta(referencies.get(0));
			assertTrue(consultada2.isError());
			assertThat(
					consultada2.getEstat(),
					is(NotificacioEstatEnum.PENDENT));
			assertNotNull(consultada2.getErrorEventError());
			assertThat(
					consultada2.getErrorOrigen(),
					is(ErrorOrigenEnum.NOTIFICA));
			assertTrue(consultada2.getErrorEventError().length() > 0);
		} finally {
			entitat.setDir3Codi(ENTITAT_DIR3CODI);
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
			System.out.println("    - " + referencia + "(" + new String(Base64.decodeBase64(referencia.getBytes())) + ")");
		}
		Notificacio consultada2 = notificacio2Service.consulta(referencies.get(0));
		assertFalse(consultada2.isError());
		assertThat(
				consultada2.getEstat(),
				is(NotificacioEstatEnum.ENVIADA));
		assertNotNull(consultada2.getEnviaments());
		assertThat(consultada2.getEnviaments().size(), is(1));
		NotificacioEnviament enviament = consultada2.getEnviaments().get(0);
		assertThat(
				enviament.getEstat(),
				is(NotificacioEnviamentEstatEnum.NOTIB_ENVIADA));
	}



	private void generarNotificacio(
			String notificacioId,
			int numDestinataris,
			boolean ambEnviamentPostal) throws IOException, DecoderException {
		byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());
		notificacio = new Notificacio();
		notificacio.setEntitatDir3Codi(ENTITAT_DIR3CODI);
		notificacio.setEnviamentTipus(EnviamentTipusEnum.COMUNICACIO);
		notificacio.setEnviamentDataProgramada(null);
		notificacio.setConcepte(
				"concepte_" + notificacioId);
		if (ambEnviamentPostal) {
			notificacio.setPagadorCorreusCodiDir3("A04013511");
			notificacio.setPagadorCorreusContracteNum("00001");
			notificacio.setPagadorCorreusCodiClientFacturacio("A04013511");
			notificacio.setPagadorCorreusDataVigencia(new Date());
			notificacio.setPagadorCorreusContracteNum(
					"pccNum_" + notificacioId);
			notificacio.setPagadorCorreusCodiClientFacturacio(
					"ccFac_" + notificacioId);
			notificacio.setPagadorCorreusDataVigencia(
					new Date(0));
			notificacio.setPagadorCieCodiDir3(
					"A04013511");
			notificacio.setPagadorCieDataVigencia(
					new Date(0));
		}
		notificacio.setProcedimentCodiSia(
				"0000");
		notificacio.setProcedimentDescripcioSia(
				"Procediment desc.");
		notificacio.setDocumentArxiuNom(
				"documentArxiuNom_" + notificacioId + ".pdf");
		notificacio.setDocumentContingutBase64(
				Base64.encodeBase64String(arxiuBytes));
		notificacio.setDocumentSha1(
				Base64.encodeBase64String(
						Hex.decodeHex(
								DigestUtils.sha1Hex(arxiuBytes).toCharArray())));
		notificacio.setDocumentNormalitzat(
				false);
		notificacio.setDocumentGenerarCsv(
				false);
		notificacio.setSeuExpedientSerieDocumental(
				"0000S");
		notificacio.setSeuExpedientUnitatOrganitzativa(
				"00000000T");
		notificacio.setSeuExpedientIdentificadorEni(
				"seuExpedientIdentificadorEni_" + notificacioId);
		notificacio.setSeuExpedientTitol(
				"seuExpedientTitol_" + notificacioId);
		notificacio.setSeuRegistreOficina(
				"seuRegistreOficina_" + notificacioId);
		notificacio.setSeuRegistreLlibre(
				"seuRegistreLlibre_" + notificacioId);
		notificacio.setSeuIdioma(
				"seuIdioma_" + notificacioId);
		notificacio.setSeuAvisTitol(
				"seuAvisTitol_" + notificacioId);
		notificacio.setSeuAvisText(
				"seuAvisText_" + notificacioId);
		notificacio.setSeuAvisTextMobil(
				"seuAvisTextMobil_" + notificacioId);
		notificacio.setSeuOficiTitol(
				"seuOficiTitol_" + notificacioId);
		notificacio.setSeuOficiText(
				"seuOficiText_" + notificacioId);
		List<NotificacioEnviament> enviaments = new ArrayList<>();
		for (int i = 0; i < numDestinataris; i++) {
			NotificacioEnviament enviament = new NotificacioEnviament();
			enviament.setTitularNom("titularNom" + i);
			enviament.setTitularLlinatges("titularLlinatges" + i);
			enviament.setTitularNif("00000000T");
			enviament.setTitularTelefon("666010101");
			enviament.setTitularEmail("titular@gmail.com");
			enviament.setDestinatariNom("destinatariNom" + i);
			enviament.setDestinatariLlinatges("destinatariLlinatges" + i);
			enviament.setDestinatariNif("12345678Z");
			enviament.setDestinatariTelefon("666020202");
			enviament.setDestinatariEmail("destinatari@gmail.com");
			if (ambEnviamentPostal) {
				enviament.setDomiciliTipus(DomiciliTipusEnum.CONCRET);
				enviament.setDomiciliConcretTipus(DomiciliConcretTipusEnum.NACIONAL);
				enviament.setDomiciliViaTipus("CALLE");
				enviament.setDomiciliViaNom("Bas");
				enviament.setDomiciliNumeracioTipus(DomiciliNumeracioTipusEnum.SENSE_NUMERO);
				enviament.setDomiciliNumeracioNumero("00");
				enviament.setDomiciliNumeracioPuntKm("pk01");
				enviament.setDomiciliApartatCorreus("0228");
				enviament.setDomiciliBloc("bloc" + i);
				enviament.setDomiciliPortal("portal" + i);
				enviament.setDomiciliEscala("escala" + i);
				enviament.setDomiciliPlanta("planta" + i);
				enviament.setDomiciliPorta("porta" + i);
				enviament.setDomiciliComplement("complement" + i);
				enviament.setDomiciliPoblacio("poblacio" + i);
				enviament.setDomiciliMunicipiCodiIne("07033");
				enviament.setDomiciliMunicipiNom("Manacor");
				enviament.setDomiciliCodiPostal("07500");
				enviament.setDomiciliProvinciaCodi("07");
				enviament.setDomiciliProvinciaNom("Illes Balears");
				enviament.setDomiciliPaisCodiIso("ES");
				enviament.setDomiciliPaisNom("Espanya");
				enviament.setDomiciliLinea1("linea1" + i);
				enviament.setDomiciliLinea2("linea2" + i);
				enviament.setDomiciliCie(new Integer(8));
			}
			enviament.setDehObligat(true);
			enviament.setDehNif("00000000T");
			enviament.setDehProcedimentCodi("0000");
			enviament.setServeiTipus(ServeiTipusEnum.URGENT);
			enviament.setRetardPostal(5);
			enviament.setCaducitat(
					new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000));
			enviaments.add(enviament);
		}
		notificacio.setEnviaments(enviaments);
	}

	private void comprovarNotificacio(
			Notificacio original,
			Notificacio consultada,
			List<String> referencies) {
		assertThat(
				consultada.getEnviamentTipus(),
				is(original.getEnviamentTipus()));
		assertThat(
				consultada.getEnviamentDataProgramada(),
				is(original.getEnviamentDataProgramada()));
		assertThat(
				consultada.getConcepte(),
				is(original.getConcepte()));
		assertThat(
				consultada.getPagadorCorreusCodiDir3(),
				is(original.getPagadorCorreusCodiDir3()));
		assertThat(
				consultada.getPagadorCorreusContracteNum(),
				is(original.getPagadorCorreusContracteNum()));
		assertThat(
				consultada.getPagadorCorreusCodiClientFacturacio(),
				is(original.getPagadorCorreusCodiClientFacturacio()));
		assertThat(
				consultada.getPagadorCieCodiDir3(),
				is(original.getPagadorCieCodiDir3()));
		assertThat(
				consultada.getPagadorCieDataVigencia(),
				is(original.getPagadorCieDataVigencia()));
		assertThat(
				consultada.getProcedimentCodiSia(),
				is(original.getProcedimentCodiSia()));
		assertThat(
				consultada.getProcedimentDescripcioSia(),
				is(original.getProcedimentDescripcioSia()));
		assertThat(
				consultada.getDocumentArxiuNom(),
				is(original.getDocumentArxiuNom()));
		assertThat(
				consultada.getDocumentContingutBase64(),
				is(original.getDocumentContingutBase64()));
		assertThat(
				consultada.getDocumentSha1(),
				is(original.getDocumentSha1()));
		assertThat(
				consultada.isDocumentNormalitzat(),
				is(original.isDocumentNormalitzat()));
		assertThat(
				consultada.isDocumentGenerarCsv(),
				is(original.isDocumentGenerarCsv()));
		assertThat(
				consultada.getSeuExpedientSerieDocumental(),
				is(original.getSeuExpedientSerieDocumental()));
		assertThat(
				consultada.getSeuExpedientUnitatOrganitzativa(),
				is(original.getSeuExpedientUnitatOrganitzativa()));
		assertThat(
				consultada.getSeuExpedientIdentificadorEni(),
				is(original.getSeuExpedientIdentificadorEni()));
		assertThat(
				consultada.getSeuExpedientTitol(),
				is(original.getSeuExpedientTitol()));
		assertThat(
				consultada.getSeuRegistreOficina(),
				is(original.getSeuRegistreOficina()));
		assertThat(
				consultada.getSeuRegistreLlibre(),
				is(original.getSeuRegistreLlibre()));
		assertThat(
				consultada.getSeuIdioma(),
				is(original.getSeuIdioma()));
		assertThat(
				consultada.getSeuAvisTitol(),
				is(original.getSeuAvisTitol()));
		assertThat(
				consultada.getSeuAvisText(),
				is(original.getSeuAvisText()));
		assertThat(
				consultada.getSeuAvisTextMobil(),
				is(original.getSeuAvisTextMobil()));
		assertThat(
				consultada.getSeuOficiTitol(),
				is(original.getSeuOficiTitol()));
		assertThat(
				consultada.getSeuOficiText(),
				is(original.getSeuOficiText()));
		assertNotNull(consultada.getEnviaments());
		assertThat(
				consultada.getEnviaments().size(),
				is(1));
		comprovarEnviament(
				original.getEnviaments().get(0),
				consultada.getEnviaments().get(0),
				referencies.get(0));
	}

	private void comprovarEnviament(
			NotificacioEnviament original,
			NotificacioEnviament consultat,
			String referencia) {
		assertThat(
				consultat.getReferencia(),
				is(referencia));
		assertThat(
				consultat.getTitularNom(),
				is(original.getTitularNom()));
		assertThat(
				consultat.getTitularLlinatges(),
				is(original.getTitularLlinatges()));
		assertThat(
				consultat.getTitularNif(),
				is(original.getTitularNif()));
		assertThat(
				consultat.getTitularTelefon(),
				is(original.getTitularTelefon()));
		assertThat(
				consultat.getTitularEmail(),
				is(original.getTitularEmail()));
		assertThat(
				consultat.getDestinatariNom(),
				is(original.getDestinatariNom()));
		assertThat(
				consultat.getDestinatariLlinatges(),
				is(original.getDestinatariLlinatges()));
		assertThat(
				consultat.getDestinatariNif(),
				is(original.getDestinatariNif()));
		assertThat(
				consultat.getDestinatariTelefon(),
				is(original.getDestinatariTelefon()));
		assertThat(
				consultat.getDestinatariEmail(),
				is(original.getDestinatariEmail()));
		assertThat(
				consultat.getDomiciliTipus(),
				is(original.getDomiciliTipus()));
		assertThat(
				consultat.getDomiciliConcretTipus(),
				is(original.getDomiciliConcretTipus()));
		assertThat(
				consultat.getDomiciliViaTipus(),
				is(original.getDomiciliViaTipus()));
		assertThat(
				consultat.getDomiciliViaNom(),
				is(original.getDomiciliViaNom()));
		assertThat(
				consultat.getDomiciliNumeracioTipus(),
				is(original.getDomiciliNumeracioTipus()));
		assertThat(
				consultat.getDomiciliNumeracioNumero(),
				is(original.getDomiciliNumeracioNumero()));
		assertThat(
				consultat.getDomiciliNumeracioPuntKm(),
				is(original.getDomiciliNumeracioPuntKm()));
		assertThat(
				consultat.getDomiciliApartatCorreus(),
				is(original.getDomiciliApartatCorreus()));
		assertThat(
				consultat.getDomiciliBloc(),
				is(original.getDomiciliBloc()));
		assertThat(
				consultat.getDomiciliPortal(),
				is(original.getDomiciliPortal()));
		assertThat(
				consultat.getDomiciliEscala(),
				is(original.getDomiciliEscala()));
		assertThat(
				consultat.getDomiciliPlanta(),
				is(original.getDomiciliPlanta()));
		assertThat(
				consultat.getDomiciliPorta(),
				is(original.getDomiciliPorta()));
		assertThat(
				consultat.getDomiciliComplement(),
				is(original.getDomiciliComplement()));
		assertThat(
				consultat.getDomiciliPoblacio(),
				is(original.getDomiciliPoblacio()));
		assertThat(
				consultat.getDomiciliMunicipiCodiIne(),
				is(original.getDomiciliMunicipiCodiIne()));
		assertThat(
				consultat.getDomiciliMunicipiNom(),
				is(original.getDomiciliMunicipiNom()));
		assertThat(
				consultat.getDomiciliCodiPostal(),
				is(original.getDomiciliCodiPostal()));
		assertThat(
				consultat.getDomiciliProvinciaCodi(),
				is(original.getDomiciliProvinciaCodi()));
		assertThat(
				consultat.getDomiciliProvinciaNom(),
				is(original.getDomiciliProvinciaNom()));
		assertThat(
				consultat.getDomiciliPaisCodiIso(),
				is(original.getDomiciliPaisCodiIso()));
		assertThat(
				consultat.getDomiciliPaisNom(),
				is(original.getDomiciliPaisNom()));
		assertThat(
				consultat.getDomiciliLinea1(),
				is(original.getDomiciliLinea1()));
		assertThat(
				consultat.getDomiciliLinea2(),
				is(original.getDomiciliLinea2()));
		assertThat(
				consultat.getDomiciliCie(),
				is(original.getDomiciliCie()));
		assertThat(
				consultat.isDehObligat(),
				is(original.isDehObligat()));
		assertThat(
				consultat.getDehNif(),
				is(original.getDehNif()));
		assertThat(
				consultat.getDehProcedimentCodi(),
				is(original.getDehProcedimentCodi()));
		assertThat(
				consultat.getServeiTipus(),
				is(original.getServeiTipus()));
		assertThat(
				consultat.getRetardPostal(),
				is(original.getRetardPostal()));
		assertThat(
				consultat.getCaducitat(),
				is(original.getCaducitat()));
	}

	private InputStream getContingutNotificacioAdjunt() {
		return getClass().getResourceAsStream(
				"/es/caib/notib/core/notificacio_adjunt.pdf");
	}

}
