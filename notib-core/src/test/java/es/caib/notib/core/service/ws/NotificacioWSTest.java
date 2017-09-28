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
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.api.ws.notificacio.CertificacioArxiuTipusEnum;
import es.caib.notib.core.api.ws.notificacio.CertificacioTipusEnum;
import es.caib.notib.core.api.ws.notificacio.DomiciliConcretTipusEnum;
import es.caib.notib.core.api.ws.notificacio.DomiciliNumeracioTipusEnum;
import es.caib.notib.core.api.ws.notificacio.DomiciliTipusEnum;
import es.caib.notib.core.api.ws.notificacio.Notificacio;
import es.caib.notib.core.api.ws.notificacio.NotificacioCertificacio;
import es.caib.notib.core.api.ws.notificacio.NotificacioDestinatari;
import es.caib.notib.core.api.ws.notificacio.NotificacioDestinatariEstatEnum;
import es.caib.notib.core.api.ws.notificacio.NotificacioEstat;
import es.caib.notib.core.api.ws.notificacio.NotificacioWsService;
import es.caib.notib.core.api.ws.notificacio.ServeiTipusEnum;
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
public class NotificacioWSTest extends BaseServiceTest {

	private static final int NUM_DESTINATARIS = 2;

	@Autowired
	private EntitatService entitatService;
	@Autowired
	private NotificacioService notificacioService;
	@Autowired
	private NotificacioWsService notificacioWSService;

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
		entitat.setDir3Codi("A04013511");
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
	public void notificacioAltaError() {
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
			List<String> referencies = notificacioWSService.alta(notificacio);
			assertNotNull(referencies);
			assertThat(
					referencies.size(),
					is(NUM_DESTINATARIS));
			Notificacio consultada = notificacioWSService.consulta(referencies.get(0));
			comprovarNotificacio(
					notificacio,
					consultada,
					referencies);
			assertThat(
					consultada.getEstat(),
					is(NotificacioEstatEnumDto.PENDENT));
			notificacioService.notificaEnviamentsPendents();
			Notificacio consultada2 = notificacioWSService.consulta(referencies.get(0));
			assertTrue(consultada2.isError());
			assertThat(
					consultada2.getEstat(),
					is(NotificacioEstatEnumDto.PENDENT));
			assertNotNull(consultada2.getErrorEventError());
			assertTrue(consultada2.getErrorEventError().length() > 0);
		} finally {
			entitat.setDir3Codi("A04013511");
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
		List<String> referencies = notificacioWSService.alta(notificacio);
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(NUM_DESTINATARIS));
		Notificacio consultada = notificacioWSService.consulta(referencies.get(0));
		comprovarNotificacio(
				notificacio,
				consultada,
				referencies);
		assertThat(
				consultada.getEstat(),
				is(NotificacioEstatEnumDto.PENDENT));
		notificacioService.notificaEnviamentsPendents();
		Notificacio consultada2 = notificacioWSService.consulta(referencies.get(0));
		assertFalse(consultada2.isError());
		assertThat(
				consultada2.getEstat(),
				is(NotificacioEstatEnumDto.ENVIADA));
		assertNotNull(consultada2.getDestinataris());
		assertThat(consultada2.getDestinataris().size(), is(1));
		NotificacioDestinatari destinatari = consultada2.getDestinataris().get(0);
		assertThat(
				destinatari.getEstat(),
				is(NotificacioDestinatariEstatEnum.NOTIB_ENVIADA));
	}

	//@Test
	public void consultaEstat() {
		autenticarUsuari("admin");
		EntitatDto entitatCreada = entitatService.create(entitat);
		assertNotNull(entitatCreada);
		assertNotNull(entitatCreada.getId());
		entitatService.permisUpdate(
				entitatCreada.getId(),
				permisAplicacio);
		autenticarUsuari("apl");
		List<String> references = notificacioWSService.alta(notificacio);
		assertNotNull(references);
		assertThat(
				references.size(),
				is(NUM_DESTINATARIS));
		/*List<NotificacioDestinatariEstatEnumDto> nestat = Arrays.asList(
				NotificacioDestinatariEstatEnumDto.ABSENT,
				NotificacioDestinatariEstatEnumDto.DESCONEGUT,
				NotificacioDestinatariEstatEnumDto.ADRESA_INCORRECTA,
				NotificacioDestinatariEstatEnumDto.ENVIAMENT_PROGRAMAT);
		List<NotificacioDestinatariEstatEnumDto> sestat = Arrays.asList(
				NotificacioDestinatariEstatEnumDto.NOTIB_ENVIADA,
				NotificacioDestinatariEstatEnumDto.LLEGIDA);*/
		for (int i = 0; i < notificacio.getDestinataris().size(); i++) {
			autenticarUsuari("admin");
			/*notificacioService.updateDestinatariEstat(
					references.get(i),
					nestat.get(i),
					new Date(1234567),
					"EstatReceptorNom"+i,
					"ERNif"+i,
					"EstatOrigen"+i,
					"EstatNumSeguiment"+i,
					sestat.get(i));*/
			autenticarUsuari("apl");
			NotificacioEstat estat = notificacioWSService.consultaEstat(references.get(i));
			/*assertThat(
					estat.getEstat(),
					is(getEstatUnificat(
							nestat.get(i),
							sestat.get(i))));*/
			assertThat( estat.getData(), is(new Date(1234567)) );
			assertThat( estat.getReceptorNom(), is("EstatReceptorNom"+i) );
			assertThat( estat.getReceptorNif(), is("ERNif"+i) );
			assertThat( estat.getOrigen(), is("EstatOrigen"+i) );
			assertThat( estat.getNumSeguiment(), is("EstatNumSeguiment"+i) );
		}
	}

	//@Test
	public void consultaCertificacio() {
		autenticarUsuari("admin");
		EntitatDto entitatCreada = entitatService.create(entitat);
		assertNotNull(entitatCreada);
		assertNotNull(entitatCreada.getId());
		entitatService.permisUpdate(
				entitatCreada.getId(),
				permisAplicacio);
		autenticarUsuari("apl");
		List<String> references = notificacioWSService.alta(notificacio);
		assertNotNull(references);
		assertThat(
				references.size(),
				is(NUM_DESTINATARIS));
		for (int i = 0; i < notificacio.getDestinataris().size(); i++) {
			autenticarUsuari("admin");
			/*String certificacioId = pluginHelper.gestioDocumentalCreate(
					PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS,
					new ByteArrayInputStream(
							Base64.decodeBase64("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MCBvYmoKPDwvRjEgOSAwIFIKPj4KZW5kb2JqCgoxMSAwIG9iago8PC9Gb250IDEwIDAgUgovUHJvY1NldFsvUERGL1RleHRdCj4+CmVuZG9iagoKMSAwIG9iago8PC9UeXBlL1BhZ2UvUGFyZW50IDQgMCBSL1Jlc291cmNlcyAxMSAwIFIvTWVkaWFCb3hbMCAwIDU5NSA4NDJdL0dyb3VwPDwvUy9UcmFuc3BhcmVuY3kvQ1MvRGV2aWNlUkdCL0kgdHJ1ZT4+L0NvbnRlbnRzIDIgMCBSPj4KZW5kb2JqCgo0IDAgb2JqCjw8L1R5cGUvUGFnZXMKL1Jlc291cmNlcyAxMSAwIFIKL01lZGlhQm94WyAwIDAgNTk1IDg0MiBdCi9LaWRzWyAxIDAgUiBdCi9Db3VudCAxPj4KZW5kb2JqCgoxMiAwIG9iago8PC9UeXBlL0NhdGFsb2cvUGFnZXMgNCAwIFIKL09wZW5BY3Rpb25bMSAwIFIgL1hZWiBudWxsIG51bGwgMF0KL0xhbmcoZXMtRVMpCj4+CmVuZG9iagoKMTMgMCBvYmoKPDwvQ3JlYXRvcjxGRUZGMDA1NzAwNzIwMDY5MDA3NDAwNjUwMDcyPgovUHJvZHVjZXI8RkVGRjAwNEMwMDY5MDA2MjAwNzIwMDY1MDA0RjAwNjYwMDY2MDA2OTAwNjMwMDY1MDAyMDAwMzUwMDJFMDAzMT4KL0NyZWF0aW9uRGF0ZShEOjIwMTcwNTA4MTYwMzUwKzAyJzAwJyk+PgplbmRvYmoKCnhyZWYKMCAxNAowMDAwMDAwMDAwIDY1NTM1IGYgCjAwMDAwMDkwODQgMDAwMDAgbiAKMDAwMDAwMDAxOSAwMDAwMCBuIAowMDAwMDAwMjU5IDAwMDAwIG4gCjAwMDAwMDkyMjcgMDAwMDAgbiAKMDAwMDAwMDI3OSAwMDAwMCBuIAowMDAwMDA4MTQwIDAwMDAwIG4gCjAwMDAwMDgxNjEgMDAwMDAgbiAKMDAwMDAwODM1NiAwMDAwMCBuIAowMDAwMDA4NzQ5IDAwMDAwIG4gCjAwMDAwMDg5OTcgMDAwMDAgbiAKMDAwMDAwOTAyOSAwMDAwMCBuIAowMDAwMDA5MzI2IDAwMDAwIG4gCjAwMDAwMDk0MjMgMDAwMDAgbiAKdHJhaWxlcgo8PC9TaXplIDE0L1Jvb3QgMTIgMCBSCi9JbmZvIDEzIDAgUgovSUQgWyA8NzZCOTNDRjlCNUE3MzBDMURCNDYxNDU0NTcyMzVGNjI+Cjw3NkI5M0NGOUI1QTczMEMxREI0NjE0NTQ1NzIzNUY2Mj4gXQovRG9jQ2hlY2tzdW0gL0Q4QzM5NkMyRjBERDk4QTlGOTI1MEZEMjc5Nzg5OUZDCj4+CnN0YXJ0eHJlZgo5NTk4CiUlRU9GCg==")));
			notificacioService.updateCertificacio(
					references.get(i),
					NotificaCertificacioTipusEnumDto.ACUSE,
					NotificaCertificacioArxiuTipusEnumDto.PDF,
					certificacioId,
					"NumSeguiment"+i,
					new Date(1234567));*/
			autenticarUsuari("apl");
			NotificacioCertificacio certificacio =
					notificacioWSService.consultaCertificacio(references.get(i));
			assertThat( certificacio.getTipus(), is(CertificacioTipusEnum.JUSTIFICANT) );
			assertThat( certificacio.getArxiuTipus(), is(CertificacioArxiuTipusEnum.PDF) );
			assertThat( certificacio.getArxiuContingut(), is("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MCBvYmoKPDwvRjEgOSAwIFIKPj4KZW5kb2JqCgoxMSAwIG9iago8PC9Gb250IDEwIDAgUgovUHJvY1NldFsvUERGL1RleHRdCj4+CmVuZG9iagoKMSAwIG9iago8PC9UeXBlL1BhZ2UvUGFyZW50IDQgMCBSL1Jlc291cmNlcyAxMSAwIFIvTWVkaWFCb3hbMCAwIDU5NSA4NDJdL0dyb3VwPDwvUy9UcmFuc3BhcmVuY3kvQ1MvRGV2aWNlUkdCL0kgdHJ1ZT4+L0NvbnRlbnRzIDIgMCBSPj4KZW5kb2JqCgo0IDAgb2JqCjw8L1R5cGUvUGFnZXMKL1Jlc291cmNlcyAxMSAwIFIKL01lZGlhQm94WyAwIDAgNTk1IDg0MiBdCi9LaWRzWyAxIDAgUiBdCi9Db3VudCAxPj4KZW5kb2JqCgoxMiAwIG9iago8PC9UeXBlL0NhdGFsb2cvUGFnZXMgNCAwIFIKL09wZW5BY3Rpb25bMSAwIFIgL1hZWiBudWxsIG51bGwgMF0KL0xhbmcoZXMtRVMpCj4+CmVuZG9iagoKMTMgMCBvYmoKPDwvQ3JlYXRvcjxGRUZGMDA1NzAwNzIwMDY5MDA3NDAwNjUwMDcyPgovUHJvZHVjZXI8RkVGRjAwNEMwMDY5MDA2MjAwNzIwMDY1MDA0RjAwNjYwMDY2MDA2OTAwNjMwMDY1MDAyMDAwMzUwMDJFMDAzMT4KL0NyZWF0aW9uRGF0ZShEOjIwMTcwNTA4MTYwMzUwKzAyJzAwJyk+PgplbmRvYmoKCnhyZWYKMCAxNAowMDAwMDAwMDAwIDY1NTM1IGYgCjAwMDAwMDkwODQgMDAwMDAgbiAKMDAwMDAwMDAxOSAwMDAwMCBuIAowMDAwMDAwMjU5IDAwMDAwIG4gCjAwMDAwMDkyMjcgMDAwMDAgbiAKMDAwMDAwMDI3OSAwMDAwMCBuIAowMDAwMDA4MTQwIDAwMDAwIG4gCjAwMDAwMDgxNjEgMDAwMDAgbiAKMDAwMDAwODM1NiAwMDAwMCBuIAowMDAwMDA4NzQ5IDAwMDAwIG4gCjAwMDAwMDg5OTcgMDAwMDAgbiAKMDAwMDAwOTAyOSAwMDAwMCBuIAowMDAwMDA5MzI2IDAwMDAwIG4gCjAwMDAwMDk0MjMgMDAwMDAgbiAKdHJhaWxlcgo8PC9TaXplIDE0L1Jvb3QgMTIgMCBSCi9JbmZvIDEzIDAgUgovSUQgWyA8NzZCOTNDRjlCNUE3MzBDMURCNDYxNDU0NTcyMzVGNjI+Cjw3NkI5M0NGOUI1QTczMEMxREI0NjE0NTQ1NzIzNUY2Mj4gXQovRG9jQ2hlY2tzdW0gL0Q4QzM5NkMyRjBERDk4QTlGOTI1MEZEMjc5Nzg5OUZDCj4+CnN0YXJ0eHJlZgo5NTk4CiUlRU9GCg==") );
			assertThat( certificacio.getNumSeguiment(), is("NumSeguiment"+i) );
			assertThat( certificacio.getDataActualitzacio(), is(new Date(1234567)) );
		}
	}



	private void generarNotificacio(
			String notificacioId,
			int numDestinataris,
			boolean ambEnviamentPostal) throws IOException, DecoderException {
		List<NotificacioDestinatari> destinataris = new ArrayList<>();
		// l'enviament postal és opcional, per tant el domicili és opcional
		for (int i = 0; i < numDestinataris; i++) {
			destinataris.add(new NotificacioDestinatari(
					"referencia_" + notificacioId + "_" + i,
					"titularNom_" + notificacioId + "_" + i,
					"titularLlinatge1_" + notificacioId + "_" + i,
					"titularLlinatge2_" + notificacioId + "_" + i,
					"00000000T", //"tNif" + notificacioId + "_" + i,
					"666010101", //"tTelefon_" + notificacioId + "_" + i,
					"titular@gmail.com", //"titularEmail_" + notificacioId + "_" + i,
					"destinatariNom_" + notificacioId + "_" + i,
					"destinatariLlinatge1_" + notificacioId + "_" + i,
					"destinatariLlinatge2_" + notificacioId + "_" + i,
					"12345678Z", //"dNif" + notificacioId + "_" + i,
					"666020202", //"dTelefon_" + notificacioId + "_" + i,
					"destinatari@gmail.com", //"destinatariEmail_" + notificacioId + "_" + i,
					ambEnviamentPostal ? DomiciliTipusEnum.CONCRET : null,
					ambEnviamentPostal ? DomiciliConcretTipusEnum.NACIONAL : null,
					ambEnviamentPostal ? "domVT" : null,
					ambEnviamentPostal ? "domiciliViaNom_" + notificacioId + "_" + i : null,
					ambEnviamentPostal ? DomiciliNumeracioTipusEnum.PUNT_KILOMETRIC : null,
					ambEnviamentPostal ? "00" : null,
					ambEnviamentPostal ? "pk01" : null,
					ambEnviamentPostal ? "0228" : null,
					ambEnviamentPostal ? "bloc01" : null,
					ambEnviamentPostal ? "portal01" : null,
					ambEnviamentPostal ? "escala01" : null,
					ambEnviamentPostal ? "planta01" : null,
					ambEnviamentPostal ? "porta01" : null,
					ambEnviamentPostal ? "complement01" : null,
					ambEnviamentPostal ? "poblacio01" : null,
					ambEnviamentPostal ? "07033" : null,
					ambEnviamentPostal ? "Manacor" : null,
					ambEnviamentPostal ? "07500" : null,
					ambEnviamentPostal ? "07" : null,
					ambEnviamentPostal ? "Illes Balears" : null,
					ambEnviamentPostal ? "ES" : null,
					ambEnviamentPostal ? "Espanya" : null,
					ambEnviamentPostal ? "linea01" : null,
					ambEnviamentPostal ? "linea02" : null,
					ambEnviamentPostal ? new Integer(8) : null,
					true,
					"00000000T", //"dhN_" + notificacioId + "_" + i,
					"dehPC",
					ServeiTipusEnum.URGENT,
					5,
					new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000),
					"notificaIdentificador_" + notificacioId + "_" + i,
					null,
					null,
					null));
		}
		byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());
		notificacio = new Notificacio();
		notificacio.setCifEntitat(
				"12345678Z");
		notificacio.setEnviamentTipus(
				NotificaEnviamentTipusEnumDto.COMUNICACIO);
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
		notificacio.setEstat(
				NotificacioEstatEnumDto.FINALITZADA);
		notificacio.setDestinataris(
				destinataris);
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
		assertNotNull(consultada.getDestinataris());
		assertThat(
				consultada.getDestinataris().size(),
				is(1));
		comprovarDestinatari(
				original.getDestinataris().get(0),
				consultada.getDestinataris().get(0),
				referencies.get(0));
	}

	private void comprovarDestinatari(
			NotificacioDestinatari original,
			NotificacioDestinatari consultat,
			String referencia) {
		assertThat(
				consultat.getReferencia(),
				is(referencia));
		assertThat(
				consultat.getTitularNom(),
				is(original.getTitularNom()));
		assertThat(
				consultat.getTitularLlinatge1(),
				is(original.getTitularLlinatge1()));
		assertThat(
				consultat.getTitularLlinatge2(),
				is(original.getTitularLlinatge2()));
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
				consultat.getDestinatariLlinatge1(),
				is(original.getDestinatariLlinatge1()));
		assertThat(
				consultat.getDestinatariLlinatge2(),
				is(original.getDestinatariLlinatge2()));
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

	/*private NotificacioEstatEnum getEstatUnificat(
			NotificacioDestinatariEstatEnumDto notificaEstat,
			NotificacioSeuEstatEnumDto seuEstat) {
		switch(seuEstat) {
			case ENVIADA:
				return NotificacioEstatEnum.PENDENT_COMPAREIXENSA;
			case LLEGIDA:
				return NotificacioEstatEnum.LLEGIDA;
			case REBUTJADA:
				return NotificacioEstatEnum.REBUTJADA;
			default:
				if (notificaEstat == null) {
					return NotificacioEstatEnum.SENSE_INFORMACIO;
				} else {
					return NotificacioEstatEnum.toNotificacioEstatEnum(notificaEstat);
				}
		}
	}*/

	private InputStream getContingutNotificacioAdjunt() {
		return getClass().getResourceAsStream(
				"/es/caib/notib/core/notificacio_adjunt.pdf");
	}

}
