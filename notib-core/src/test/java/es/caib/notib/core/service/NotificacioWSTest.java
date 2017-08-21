/**
 * 
 */
package es.caib.notib.core.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.jersey.core.util.Base64;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.EntitatTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaCertificacioArxiuTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaCertificacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioSeuEstatEnumDto;
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
import es.caib.notib.core.api.ws.notificacio.NotificacioEstat;
import es.caib.notib.core.api.ws.notificacio.NotificacioEstatEnum;
import es.caib.notib.core.api.ws.notificacio.NotificacioWsService;
import es.caib.notib.core.api.ws.notificacio.ServeiTipusEnum;
import es.caib.notib.core.helper.PluginHelper;

/**
 * Tests per al servei web de gestió de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class NotificacioWSTest extends BaseServiceTest {

	@Autowired
	private EntitatService entitatService;
	@Autowired
	private NotificacioService notificacioService;
	@Autowired
	private NotificacioWsService notificacioWSService;
	@Autowired
	private PluginHelper pluginHelper;

	private EntitatDto entitat;
	private Notificacio notificacio;
	private PermisDto permisAplicacio;

	@Before
	public void setUp() {
		es.caib.notib.core.helper.PropertiesHelper.getProperties("classpath:es/caib/notib/core/test.properties");
		es.caib.notib.plugin.utils.PropertiesHelper.getProperties("classpath:es/caib/notib/core/test.properties");
		entitat = new EntitatDto();
		entitat.setCodi("DGTIC");
		entitat.setNom("Dirección General de Desarrollo Tecnológico");
		entitat.setDescripcio("Descripció Dirección General de Desarrollo Tecnológico");
		entitat.setCif("12345678Z");
		entitat.setTipus(EntitatTipusEnumDto.GOVERN);
		entitat.setDir3Codi("A04013511");
		entitat.setActiva(true);
		permisAplicacio = new PermisDto();
		permisAplicacio.setAplicacio(true);
		permisAplicacio.setTipus(TipusEnumDto.USUARI);
		permisAplicacio.setNom("apl");
		String notificacioId = "00";
		int destinatarisCodi = 0;
		List<NotificacioDestinatari> destinataris = new ArrayList<>();
		for (int i = destinatarisCodi; i < destinatarisCodi + 5; i++) {
			destinataris.add(new NotificacioDestinatari(
					"referencia_" + notificacioId + "_" + i,
					"titularNom_" + notificacioId + "_" + i,
					"titularLlinatge1_" + notificacioId + "_" + i,
					"titularLlinatge2_" + notificacioId + "_" + i,
					"tNif" + notificacioId + "_" + i,
					"tTelefon_" + notificacioId + "_" + i,
					"titularEmail_" + notificacioId + "_" + i,
					"destinatariNom_" + notificacioId + "_" + i,
					"destinatariLlinatge1_" + notificacioId + "_" + i,
					"destinatariLlinatge2_" + notificacioId + "_" + i,
					"dNif" + notificacioId + "_" + i,
					"dTelefon_" + notificacioId + "_" + i,
					"destinatariEmail_" + notificacioId + "_" + i,
					DomiciliTipusEnum.CONCRET,
					DomiciliConcretTipusEnum.NACIONAL,
					"domVT",
					"domiciliViaNom_" + notificacioId + "_" + i,
					DomiciliNumeracioTipusEnum.PUNT_KILOMETRIC,
					"dmNN_" + notificacioId + "_" + i,
					"dmPK_" + notificacioId + "_" + i,
					"dAC_" + notificacioId + "_" + i,
					"domiciliBloc_" + notificacioId + "_" + i,
					"domiciliPortal_" + notificacioId + "_" + i,
					"domiciliEscala_" + notificacioId + "_" + i,
					"domiciliPlanta_" + notificacioId + "_" + i,
					"domiciliPorta_" + notificacioId + "_" + i,
					"domiciliComplement_" + notificacioId + "_" + i,
					"domiciliPoblacio_" + notificacioId + "_" + i,
					"" + notificacioId + "_" + i,
					"domiciliMunicipiNom_" + notificacioId + "_" + i,
					"dCP_" + notificacioId + "_" + i,
					"PC",
					"domiciliProvinciaNom_" + notificacioId + "_" + i,
					"PCI",
					"domiciliPaisNom_" + notificacioId + "_" + i,
					"domiciliLinea1_" + notificacioId + "_" + i,
					"domiciliLinea2_" + notificacioId + "_" + i,
					new Integer(8),
					true,
					"dhN_" + notificacioId + "_" + i,
					"dehPC",
					ServeiTipusEnum.URGENT,
					5,
					new Date(0),
					"notificaIdentificador_" + notificacioId + "_" + i,
					"seuRegistreNumero_" + notificacioId + "_" + i,
					new Date(0),
					NotificacioSeuEstatEnumDto.ENVIADA)
					);
		}
		String arxiu64 = "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MCBvYmoKPDwvRjEgOSAwIFIKPj4KZW5kb2JqCgoxMSAwIG9iago8PC9Gb250IDEwIDAgUgovUHJvY1NldFsvUERGL1RleHRdCj4+CmVuZG9iagoKMSAwIG9iago8PC9UeXBlL1BhZ2UvUGFyZW50IDQgMCBSL1Jlc291cmNlcyAxMSAwIFIvTWVkaWFCb3hbMCAwIDU5NSA4NDJdL0dyb3VwPDwvUy9UcmFuc3BhcmVuY3kvQ1MvRGV2aWNlUkdCL0kgdHJ1ZT4+L0NvbnRlbnRzIDIgMCBSPj4KZW5kb2JqCgo0IDAgb2JqCjw8L1R5cGUvUGFnZXMKL1Jlc291cmNlcyAxMSAwIFIKL01lZGlhQm94WyAwIDAgNTk1IDg0MiBdCi9LaWRzWyAxIDAgUiBdCi9Db3VudCAxPj4KZW5kb2JqCgoxMiAwIG9iago8PC9UeXBlL0NhdGFsb2cvUGFnZXMgNCAwIFIKL09wZW5BY3Rpb25bMSAwIFIgL1hZWiBudWxsIG51bGwgMF0KL0xhbmcoZXMtRVMpCj4+CmVuZG9iagoKMTMgMCBvYmoKPDwvQ3JlYXRvcjxGRUZGMDA1NzAwNzIwMDY5MDA3NDAwNjUwMDcyPgovUHJvZHVjZXI8RkVGRjAwNEMwMDY5MDA2MjAwNzIwMDY1MDA0RjAwNjYwMDY2MDA2OTAwNjMwMDY1MDAyMDAwMzUwMDJFMDAzMT4KL0NyZWF0aW9uRGF0ZShEOjIwMTcwNTA4MTYwMzUwKzAyJzAwJyk+PgplbmRvYmoKCnhyZWYKMCAxNAowMDAwMDAwMDAwIDY1NTM1IGYgCjAwMDAwMDkwODQgMDAwMDAgbiAKMDAwMDAwMDAxOSAwMDAwMCBuIAowMDAwMDAwMjU5IDAwMDAwIG4gCjAwMDAwMDkyMjcgMDAwMDAgbiAKMDAwMDAwMDI3OSAwMDAwMCBuIAowMDAwMDA4MTQwIDAwMDAwIG4gCjAwMDAwMDgxNjEgMDAwMDAgbiAKMDAwMDAwODM1NiAwMDAwMCBuIAowMDAwMDA4NzQ5IDAwMDAwIG4gCjAwMDAwMDg5OTcgMDAwMDAgbiAKMDAwMDAwOTAyOSAwMDAwMCBuIAowMDAwMDA5MzI2IDAwMDAwIG4gCjAwMDAwMDk0MjMgMDAwMDAgbiAKdHJhaWxlcgo8PC9TaXplIDE0L1Jvb3QgMTIgMCBSCi9JbmZvIDEzIDAgUgovSUQgWyA8NzZCOTNDRjlCNUE3MzBDMURCNDYxNDU0NTcyMzVGNjI+Cjw3NkI5M0NGOUI1QTczMEMxREI0NjE0NTQ1NzIzNUY2Mj4gXQovRG9jQ2hlY2tzdW0gL0Q4QzM5NkMyRjBERDk4QTlGOTI1MEZEMjc5Nzg5OUZDCj4+CnN0YXJ0eHJlZgo5NTk4CiUlRU9GCg==";
		notificacio = new Notificacio();
		notificacio.setCifEntitat(
				"12345678Z");
		notificacio.setEnviamentTipus(
				NotificaEnviamentTipusEnumDto.COMUNICACIO);
		notificacio.setEnviamentDataProgramada(
				new Date(0));
		notificacio.setConcepte(
				"concepte_" + notificacioId);
		notificacio.setPagadorCorreusCodiDir3(
				"pccd3_" + notificacioId);
		notificacio.setPagadorCorreusContracteNum(
				"pccNum_" + notificacioId);
		notificacio.setPagadorCorreusCodiClientFacturacio(
				"ccFacturacio_" + notificacioId);
		notificacio.setPagadorCorreusDataVigencia(
				new Date(0));
		notificacio.setPagadorCieCodiDir3(
				"pcied3_" + notificacioId);
		notificacio.setPagadorCieDataVigencia(
				new Date(0));
		notificacio.setProcedimentCodiSia(
				"Sia_" + notificacioId);
		notificacio.setProcedimentDescripcioSia(
				"procedimentDescripcioSia_" + notificacioId);
		notificacio.setDocumentArxiuNom(
				"documentArxiuNom_" + notificacioId + ".pdf");
		notificacio.setDocumentContingutBase64(
				arxiu64);
		notificacio.setDocumentSha1(
				"documentSha1_" + notificacioId);
		notificacio.setDocumentNormalitzat(
				true);
		notificacio.setDocumentGenerarCsv(
				true);
		notificacio.setSeuExpedientSerieDocumental(
				"seuesd_" + notificacioId);
		notificacio.setSeuExpedientUnitatOrganitzativa(
				"seuEUO_" + notificacioId);
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
				NotificacioEstatEnumDto.PROCESSADA);
		notificacio.setDestinataris(
				destinataris);
	}
	
	@Test
    public void altaNotificacioConsulta() throws JsonProcessingException {
		autenticarUsuari("admin");
		EntitatDto entitatCreada = entitatService.create(entitat);
		assertNotNull(entitatCreada);
		assertNotNull(entitatCreada.getId());
		entitatService.updatePermis(
				entitatCreada.getId(),
				permisAplicacio);
		autenticarUsuari("apl");
		List<String> references = notificacioWSService.alta(notificacio);
		assertNotNull(references);
		assertThat(
				references.size(),
				is(5));
		Notificacio n = notificacioWSService.consulta(references.get(0));
		assertThat( n.getEnviamentTipus(), is(notificacio.getEnviamentTipus()) );
		assertThat( n.getEnviamentDataProgramada(), is(notificacio.getEnviamentDataProgramada()) );
		assertThat( n.getConcepte(), is(notificacio.getConcepte()) );
		assertThat( n.getPagadorCorreusCodiDir3(), is(notificacio.getPagadorCorreusCodiDir3()) );
		assertThat( n.getPagadorCorreusContracteNum(), is(notificacio.getPagadorCorreusContracteNum()) );
		assertThat( n.getPagadorCorreusCodiClientFacturacio(), is(notificacio.getPagadorCorreusCodiClientFacturacio()) );
		assertThat( n.getPagadorCieCodiDir3(), is(notificacio.getPagadorCieCodiDir3()) );
		assertThat( n.getPagadorCieDataVigencia(), is(notificacio.getPagadorCieDataVigencia()) );
		assertThat( n.getProcedimentCodiSia(), is(notificacio.getProcedimentCodiSia()) );
		assertThat( n.getProcedimentDescripcioSia(), is(notificacio.getProcedimentDescripcioSia()) );
		assertThat( n.getDocumentArxiuNom(), is(notificacio.getDocumentArxiuNom()) );
		assertThat( n.getDocumentContingutBase64(), is(notificacio.getDocumentContingutBase64()) );
		assertThat( n.getDocumentSha1(), is(notificacio.getDocumentSha1()) );
		assertThat( n.isDocumentNormalitzat(), is(notificacio.isDocumentNormalitzat()) );
		assertThat( n.isDocumentGenerarCsv(), is(notificacio.isDocumentGenerarCsv()) );
		assertThat( n.getSeuExpedientSerieDocumental(), is(notificacio.getSeuExpedientSerieDocumental()) );
		assertThat( n.getSeuExpedientUnitatOrganitzativa(), is(notificacio.getSeuExpedientUnitatOrganitzativa()) );
		assertThat( n.getSeuExpedientIdentificadorEni(), is(notificacio.getSeuExpedientIdentificadorEni()) );
		assertThat( n.getSeuExpedientTitol(), is(notificacio.getSeuExpedientTitol()) );
		assertThat( n.getSeuRegistreOficina(), is(notificacio.getSeuRegistreOficina()) );
		assertThat( n.getSeuRegistreLlibre(), is(notificacio.getSeuRegistreLlibre()) );
		assertThat( n.getSeuIdioma(), is(notificacio.getSeuIdioma()) );
		assertThat( n.getSeuAvisTitol(), is(notificacio.getSeuAvisTitol()) );
		assertThat( n.getSeuAvisText(), is(notificacio.getSeuAvisText()) );
		assertThat( n.getSeuAvisTextMobil(), is(notificacio.getSeuAvisTextMobil()) );
		assertThat( n.getSeuOficiTitol(), is(notificacio.getSeuOficiTitol()) );
		assertThat( n.getSeuOficiText(), is(notificacio.getSeuOficiText()) );
		assertThat( n.getEstat(), is(NotificacioEstatEnumDto.PENDENT) );
		if (n.getDestinataris() != null) {
			assertThat(
					n.getDestinataris().size(),
					is(notificacio.getDestinataris().size()));
		} else {
			assertNull(notificacio.getDestinataris());
		}
		for (int i = 0; i < n.getDestinataris().size(); i++) {
			NotificacioDestinatari d0 = n.getDestinataris().get(i);
			NotificacioDestinatari d1 = notificacio.getDestinataris().get(i);
			assertThat( d0.getReferencia(), is(references.get(i)) );
			assertThat( d0.getTitularNom(), is(d1.getTitularNom()) );
			assertThat( d0.getTitularLlinatge1(), is(d1.getTitularLlinatge1()) );
			assertThat( d0.getTitularLlinatge2(), is(d1.getTitularLlinatge2()) );
			assertThat( d0.getTitularNif(), is(d1.getTitularNif()) );
			assertThat( d0.getTitularTelefon(), is(d1.getTitularTelefon()) );
			assertThat( d0.getTitularEmail(), is(d1.getTitularEmail()) );
			assertThat( d0.getDestinatariNom(), is(d1.getDestinatariNom()) );
			assertThat( d0.getDestinatariLlinatge1(), is(d1.getDestinatariLlinatge1()) );
			assertThat( d0.getDestinatariLlinatge2(), is(d1.getDestinatariLlinatge2()) );
			assertThat( d0.getDestinatariNif(), is(d1.getDestinatariNif()) );
			assertThat( d0.getDestinatariTelefon(), is(d1.getDestinatariTelefon()) );
			assertThat( d0.getDestinatariEmail(), is(d1.getDestinatariEmail()) );
			assertThat( d0.getDomiciliTipus(), is(d1.getDomiciliTipus()) );
			assertThat( d0.getDomiciliConcretTipus(), is(d1.getDomiciliConcretTipus()) );
			assertThat( d0.getDomiciliViaTipus(), is(d1.getDomiciliViaTipus()) );
			assertThat( d0.getDomiciliViaNom(), is(d1.getDomiciliViaNom()) );
			assertThat( d0.getDomiciliNumeracioTipus(), is(d1.getDomiciliNumeracioTipus()) );
			assertThat( d0.getDomiciliNumeracioNumero(), is(d1.getDomiciliNumeracioNumero()) );
			assertThat( d0.getDomiciliNumeracioPuntKm(), is(d1.getDomiciliNumeracioPuntKm()) );
			assertThat( d0.getDomiciliApartatCorreus(), is(d1.getDomiciliApartatCorreus()) );
			assertThat( d0.getDomiciliBloc(), is(d1.getDomiciliBloc()) );
			assertThat( d0.getDomiciliPortal(), is(d1.getDomiciliPortal()) );
			assertThat( d0.getDomiciliEscala(), is(d1.getDomiciliEscala()) );
			assertThat( d0.getDomiciliPlanta(), is(d1.getDomiciliPlanta()) );
			assertThat( d0.getDomiciliPorta(), is(d1.getDomiciliPorta()) );
			assertThat( d0.getDomiciliComplement(), is(d1.getDomiciliComplement()) );
			assertThat( d0.getDomiciliPoblacio(), is(d1.getDomiciliPoblacio()) );
			assertThat( d0.getDomiciliMunicipiCodiIne(), is(d1.getDomiciliMunicipiCodiIne()) );
			assertThat( d0.getDomiciliMunicipiNom(), is(d1.getDomiciliMunicipiNom()) );
			assertThat( d0.getDomiciliCodiPostal(), is(d1.getDomiciliCodiPostal()) );
			assertThat( d0.getDomiciliProvinciaCodi(), is(d1.getDomiciliProvinciaCodi()) );
			assertThat( d0.getDomiciliProvinciaNom(), is(d1.getDomiciliProvinciaNom()) );
			assertThat( d0.getDomiciliPaisCodiIso(), is(d1.getDomiciliPaisCodiIso()) );
			assertThat( d0.getDomiciliPaisNom(), is(d1.getDomiciliPaisNom()) );
			assertThat( d0.getDomiciliLinea1(), is(d1.getDomiciliLinea1()) );
			assertThat( d0.getDomiciliLinea2(), is(d1.getDomiciliLinea2()) );
			assertThat( d0.getDomiciliCie(), is(d1.getDomiciliCie()) );
			assertThat( d0.isDehObligat(), is(d1.isDehObligat()) );
			assertThat( d0.getDehNif(), is(d1.getDehNif()) );
			assertThat( d0.getDehProcedimentCodi(), is(d1.getDehProcedimentCodi()) );
			assertThat( d0.getServeiTipus(), is(d1.getServeiTipus()) );
			assertThat( d0.getRetardPostal(), is(d1.getRetardPostal()) );
			assertThat( d0.getCaducitat(), is(d1.getCaducitat()) );
		}
		
	}
	
	@Test
    public void consultaEstat() throws JsonProcessingException {
		autenticarUsuari("admin");
		EntitatDto entitatCreada = entitatService.create(entitat);
		assertNotNull(entitatCreada);
		assertNotNull(entitatCreada.getId());
		entitatService.updatePermis(
				entitatCreada.getId(),
				permisAplicacio);
		autenticarUsuari("apl");
		List<String> references = notificacioWSService.alta(notificacio);
		assertNotNull(references);
		assertThat(
				references.size(),
				is(5));
		
		List<NotificaEstatEnumDto> nestat = Arrays.asList(
				NotificaEstatEnumDto.AUSENTE,
				NotificaEstatEnumDto.DESCONOCIDO,
				NotificaEstatEnumDto.DIRECCION_INCORRECTA,
				NotificaEstatEnumDto.EDITANDO,
				NotificaEstatEnumDto.ENVIADO_CENTRO_IMPRESION);
		List<NotificacioSeuEstatEnumDto> sestat = Arrays.asList(
				NotificacioSeuEstatEnumDto.ENVIADA,
				NotificacioSeuEstatEnumDto.ERROR_ENVIAMENT,
				NotificacioSeuEstatEnumDto.ERROR_NOTIFICA,
				NotificacioSeuEstatEnumDto.ERROR_PROCESSAMENT,
				NotificacioSeuEstatEnumDto.LLEGIDA);
		for(int i = 0; i < notificacio.getDestinataris().size(); i++) {
			autenticarUsuari("admin");
			notificacioService.updateDestinatariEstat(
					references.get(i),
					nestat.get(i),
					new Date(1234567),
					"EstatReceptorNom"+i,
					"ERNif"+i,
					"EstatOrigen"+i,
					"EstatNumSeguiment"+i,
					sestat.get(i));
			
			autenticarUsuari("apl");
			NotificacioEstat estat = notificacioWSService.consultaEstat(references.get(i));
			assertThat(
					estat.getEstat(),
					is(getEstatUnificat(
							nestat.get(i),
							sestat.get(i))));
			assertThat( estat.getData(), is(new Date(1234567)) );
			assertThat( estat.getReceptorNom(), is("EstatReceptorNom"+i) );
			assertThat( estat.getReceptorNif(), is("ERNif"+i) );
			assertThat( estat.getOrigen(), is("EstatOrigen"+i) );
			assertThat( estat.getNumSeguiment(), is("EstatNumSeguiment"+i) );
		}
		
	}
	private NotificacioEstatEnum getEstatUnificat(
			NotificaEstatEnumDto notificaEstat,
			NotificacioSeuEstatEnumDto seuEstat) {
		
		switch(seuEstat) {
			case ENVIADA: return NotificacioEstatEnum.PENDENT_COMPAREIXENSA;
			case LLEGIDA: return NotificacioEstatEnum.LLEGIDA;
			case REBUTJADA: return NotificacioEstatEnum.REBUTJADA;
			default:
				if(notificaEstat == null) return NotificacioEstatEnum.SENSE_INFORMACIO;
				return NotificacioEstatEnum.toNotificacioEstatEnum(notificaEstat);
		}
	}
	
	@Test
    public void consultaCertificacio() throws JsonProcessingException {
		autenticarUsuari("admin");
		EntitatDto entitatCreada = entitatService.create(entitat);
		assertNotNull(entitatCreada);
		assertNotNull(entitatCreada.getId());
		entitatService.updatePermis(
				entitatCreada.getId(),
				permisAplicacio);
		autenticarUsuari("apl");
		List<String> references = notificacioWSService.alta(notificacio);
		assertNotNull(references);
		assertThat(
				references.size(),
				is(5));
		
		for(int i = 0; i < notificacio.getDestinataris().size(); i++) {
			autenticarUsuari("admin");
			String certificacioId = pluginHelper.gestioDocumentalCreate(
					PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS,
					new ByteArrayInputStream(
							Base64.decode("JVBERi0xLjQKJcOkw7zDtsOfCjIgMCBvYmoKPDwvTGVuZ3RoIDMgMCBSL0ZpbHRlci9GbGF0ZURlY29kZT4+CnN0cmVhbQp4nF2OOwsCMRCE+/yKrYU7Z3N5HYSA9yrsDgIWYuejE7zGv+8mYiMpMvlmMrtomd7qRSCIsr1tNQXDbaDtpk47en49OdtDDVlZJ5b3RsL5SvuFiTXl+zmCk43Q6BJHmNT4CJt0hINHQF/oAUMhIybMRfiaCsKXYgdGoX1qXKGTfKyxUsMsVyMDan1tq6p6GtN/UyebjPXB5tfIll265KOas1rVSh9CrDWSCmVuZHN0cmVhbQplbmRvYmoKCjMgMCBvYmoKMTY5CmVuZG9iagoKNSAwIG9iago8PC9MZW5ndGggNiAwIFIvRmlsdGVyL0ZsYXRlRGVjb2RlL0xlbmd0aDEgMTE5NjQ+PgpzdHJlYW0KeJzlem10W9WV6DnnXulefVi6smX5Q7Z1lWv5Q5ItR4oTOx/2jWPL13FI5K9gJ7Utx5Zj58PfSYDA4A7hyyHFQykEkikpjzJdMLOQk5SXFB64a4XVsgo0syZl2hJo2kffzCxwcRng8QDbb58jOSShtGvNvLXej7m27t17n3322Wefffbe50oTYwdiyIwmEYfU3v09I9urKhsQQq8hhFN7D07ISzWqDPAVhIijf2T3/qLQW39AiPsUIUG3e9+t/T/9P5lvIGSCLhkXB2I9fbHbq4IIrbgIhNUDQNi5eKuAkCICnj+wf+IWRTevAu4HfMe+4d6eLeHTtYDfB3hwf88tI6/qH+EBfxFweahnf2xr7MmHAb+MkLhlZHh8og/lLyHkL6LtI2OxkY93PAqy/RroNwE0DH/0MgOopzjheJ1eEA1GkznFYpVsqfZ0Rwb6r3PpjqF0pOk2ICsaYffrLu7vURZ9Lr1//X1xy9Jn/y+1EBOP4+hpdBYdQ79CncmGMIqgQXQAKNdeP0b/CFR6RdAO9Aya+hqxf4/OQXuCL4oeRI99DV8EPYrOoJ9cN0oE7UeHQZcfol/hlehVcJVh9CEW0TfRKyD1Q6Dd9KdEEQvc+hnYfw31LXSCHEWbybuAPEZbSIBI6AI6ibtA8gTM89jVGa//itB70R1wb0ED6CDA7NJt+OLXyLD07zCrO9Bm9NdoI9p3TY8X8ROcEdavFT0BNv0xowWWGwWN20OeJ2Th24D8DdoNnx4McyfHuI2oVmfDZxFS6zra21pbmpsi27betKVxc4NWH66r3VSzUa2u2rB+3drKijWry1eWBUpL/EWFBZ58ZYXblWm3SVZLisloEAW9jucIRv46JRyV4wXROF+gaFoJxZUeIPRcQ4jGZSCFr+eJy1HGJl/PqQJn/w2caoJTvcqJJXk9Wl/il+sUOf56rSKfwzua2gE+Vqt0yPE5Bt/EYL6AISmAuN3QQ67LHKiV4zgq18XDBwem6qK1IG/GZNykbIoZS/xoxmgC0ARQvEgZmcFFVZgBpKhu7QxBYgodNs556nr64pGm9rpap9vdUeJviFuUWtaENjGRcf2muMBEyoNUdXRUnvHPTj1wTkK7oj5zn9LX8432ONcDfae4uqmpe+M2X7xYqY0X3/ZuJsw8FvcrtXVxH5Xa2Hx1nMYvh8RxnUdS5KmPEUxHmXv/ekpPkqL3SB8jCobBvFNTYUUOT0Wnes4tTe5SZEmZmjGbp0bqwMIo0g69zi396KgzHn6gIy5FB/Da5GTDzY3xtKad7XHiCcsDPUCB/2rFXeF02zqWeSJf14zAEGAOsKnbTSd+9JyKdgESn2xqT+Ay2uU8jdSAryNOorRldrklvY22TC63XO0eVWA1G1vap+K8p6FPqQMbH+2JT+4Cf9pDl0KR4pZPnG5lKtUmVwY6GK8MWjX0DcpxXQGYBXpd2wE8hXaZkhhi+STxmHPCAAW2VLlSATFUTp1SF03+HxzIBAFyiT+u+RJL39oeV2sBUHuSa1Q3UxaAHj1RWKLBWrZ88YAyErcrNVfXk6pVN9jSzroku8Xtm+Io2pvsFQ/U1dKR5bqpaG1CBSpLaWo/j0JLV2ZWyc4zIbQKddRSZscm8KuCuqn2vv64K+rsg53WL7c73XG1Axa4Q2mPdVBHAwsVX4Hh3GzEONnU2t7YojQ27WivSCqSaKDieE/dDWKUdmdCDLhcXPSIcjtxch3AKAFBDgOg1KyHe1zwiPCRwOCMSl21Zr3cjp1omRvUiBfLdbHaJB/FrxOqo+60SVuWpqcoyNmkOd0d7sRV4ifQLCcHhh4iNaq23MR5IBIAjYAYRqK2zKQ+L7crMaVDGZDjaqSdzo2ah1k5aQxm8+RatV6HXWMsMBNyQ/MyQo0ZD/uc1xo3Xs/wq6h2Q3PDcrM8JSqNLVNUuJIUiEDzhjiiLqxW2Jxs99P9rIR7YBPDjmb7eWpGVeleHqDbdkpp6JtSWtrXM26IIHc4b6NjpaJG3NhaU+KHYFYzo+D7mmZUfF/LjvbzEpRU97W2nyaYbIrWdMzkQ1v7eRlyBaMSSqVEisgUoZKaAREZv/O8itAka+UZgeG95zBiNHGZhlHvOZKgScs0AjQ+QVMZjV6wSpkDYGOI33VyH12f2zsGpqId1MeRAywC/ziOlSqwjlI1g4neHDcqsZq4Samh9GpKr07Q9ZQugGdgBy7x3zYl1SkfZ5aw1I2g/iR9ujaogAVUOoNRYP1pgRfngjN63eX1pzkCIJrhKFlHyacFveGL9acxpYdsbpvHbXPXEnkxHx9fHNC1ffZsLf86k2uHLH5Op0HNlYr+TdX0NmyDCtRqSMWpKWY9bjZL5h2C3i5ALsXN0BDldXae1zklSG9mmyZJ/BMCVoWIQEIC5gS73Y7ftWP7uaXZM2XtGn2qVl+pdtGOSdR+0T5v51ibnM/azmTnJXgKpDSt245X85hPsURTraCGDZukRomIEmRxs8Bbu404FVWH5oLBYHUo1NWZWhnoBAwHOjt9Pl9n96jPNzomvdbd2dlpq9wQ8I3emyn57vVdSD6k2VncKd1L7yvLPOnu8jU4hDPok3NzmHPj1xfrj+NXX8JvPbPw6tm7F+bvxUf/F/6n8vJyJ//p56ITnviuxTv4gYUDtIIvBuMd515BmWjvGd6IybmlX6oBg1VLc+FhfCfmMDbUI4tkkS2zlouWKxa9aHFld2cTNRtvT+tPI2lcJqETlwxmjZBMyRpJtRosEXM6qp4LVs9VhwI+6dVQJx4dC3R1zgUDncGVZb5OnK4UrrAQwcYUtxWWuzOquBA57lubo6rrHN9brDl0CKcaMiKdnfncK4tDYkqqcaEmq6Qki5OzSg6krfTnge5bl97ntoLuhWi/WigK9wlETLkvhYgGjLP0GOempRUWo2JcpRZPFp8qvlg8X6wrprq6vCVad/FzxWR7bn8uydVuNd5vJMbMiN0qFa5o0jmY6qE5WJE5WJPRrk7pjWBgZRnq6sRdnV2dnR4Lp6woJeWrqkgo6MiwKYWlWIHppNvzgFBF1nBbs7XmDu9t/zC0atMt39/VdLxqjc8zWLmxt07J2/LN3hX1m9ZlVKblphk3TZ4/MHn+UEWaefGzp9OzA32P793xN/0VOoNZgPmBT3O/hzNFEYqfRymguE+UtGJ7pZ1k2rGB/qfXWyXskLynvBh5Je+s94qXrzzlnfcSL52o3VemBbxY8uKIF494J73TXo42nHGt0BiDL82hIVf9ZD5G+VK+nD+bfzH/Sr5ezPdEipArXcqPpK1Iz9PpspqNElglZAslHReM09U5OhcEhx0dG/ONASJdpgsM5gG/9GE7rC4saTCPpCcXeVWBcv2S4zDGHMmJ3Hxz/uodGz1ji3vvaGrLqa5anXrnYt+hB3CQ+8RS5CtKkfLz0vJq9jQuPEIdgHS1dOhFE7+QRjEdYYGFIB/cUnVbUBrKRSfVFrTZZDxhfNbIvWf83EiOGLExq95k99lJo32n/YT9cztPsXX2Z+0v2N+z6yW7WrlBs7t4l91FKj9y4WkXJhHXKVfcNevipwEgLmq3kjKNPTOd7KlKKZKma7Hy2ZFcqz0rkrHs9XOYbuXR7jEwjHTZB9YZW7jUCQZbWUYt86X35BHuS3sctuUVORyFeTZbXqHDUZRnM35vMevU3djH/+ZaKnB93rS8F+gehlMkT0+XFuxU83YY9xinjNwOtAeRNjEmkjYuxhFOzzt4YhDOLV05I5o1ffKJzy399AzsXAPgaj4ARtwMp/pGg9FuMBgJbhYNYj1H7BxHCDYYcB5jTE2xaQYDZzQhJ9idW4EkCYzx37WohiRcT2HVVhTWrkj4rHRBuiRxpyDKUmp57gqItLJUJnG8hJ+CRjIpQUCVRiA+ckg0clzErLOqBqwzxAzkYwM2YALbMRTqHB0Fz8uAKBnshMg4OuobhU9Xp096o6szyOJkV2codH2oBGPDMkADjaWdBqxgsHK6YGAP7unFezcv3hHFz38Hp2L9d/A3uD1f/DV3GxjWuXCIHIUn9SsaY05DjDEhF3pAXX/E+LCR6Iz4qHhCJEYRH+VPgFl5fIQ8TIieYLApciO37CaSu8wdcV9x8xRT3dw6N7WAY+Nm7Qk3HnFj1R11T7pPufmoG7Mmi6dUc0DiihgkZ4SjQYjusrnEBqMeJL0D7kP3F7uwHdyooHzVaghBwqpScm0A4k6/+btLv/zl5Td/fTZ7Q1/D5miFw1ER3dzQtyEbv/XBElr84x+++N//3vPY4Jo1g4/17Hp8b2Xl3scT+VmD+Y5yP0ZetBo9qrr3FmBnhi+DWBxVDpIqm6xabmpJKjGn4hQbxjzmqD/kGmwaZAoxx7i6Xl8xWYG7K7BagQFYWW8vZBHXaNEKC7dBQi0oWOGL5OSg1aEmo9WhjxjSV0SQxCZLQ4utMjAHCRFCL00cc3Tu0mWIOHTiPjptesPLUaXwaiDmq3E5M4LeipXyKpwmWLh0eyi4eg3+R3UoUnJgcTHNGtK619Z2VmTmrW5o6y47ZnFXeMt2eVZUbDz65l3rtlfkPFjbG+R+nLm2t3Hh7qySLmuRkult3L2+amdVoUPE/Le9dcGc7PQDr1vSF/N4klYaqYq7MsFmm8Fm/wJxOhuy6e1qeX/RwSJyXMQG8X6RnOTxMR6beSymIqU+w4d8uB4+qm/SN+vjZF+UAbyPWikHCgyntk2HdRmR7PS0iAMVRoySApViE/OHkPQqsxPLTNQvaHBJGoddHgtWZFsyLZVyG6ijUzth5her19hontKTf/Lf1bF4Z2jPk8Oh8XLY2Pi7uHZi8dNFl6c2um79Ho93KHT3nWFlDf7tgRfuqjObTL6VZdaPMks+O59Vgl8fnO4ozJDIv4iGNxFZemVxC76fxd0iXKy+kSGLJg1t3ms8DBlVzXNrrRB6c/Lr99qxyY7T7F6T1+kli5e873rJXu9h71Evl+/Fz3rf8pJnvS94P/dyD3uxyYvf8Hn3QhJTT/9Q86p/94zWR6lOr8/Lffc94CIXvJe8xOltpAJ2UtZV3lovoQLIESZgP2Nr9O5kg5zw6rzqzm5tFW077KVDveV9z6tfB4mTyDR5yt4ybxwS6EWvPuKNekcA4RM5FGK+1YtFGuYhK1rdkdwsJ63f2EJQrxwd66b5z0fXg4amMRr1oZZLoAyT3gGGhdcuQTUBexj5vpIEGF5QWB7KcMBClbN0cD8N+BmJwJ9BE4DRt21/jV/1BJzu4Lr1x3DoK3nhs3f3PBr1G8Wf7M/5qwe4WZoeID84wT/fhvyQi46rHRkqFBFmw3oDMYvrRSJa9fVW0wcmYjdBlsPIJUHGu+LiK5FLdpW5VIB1qivqGoFUyMsMmGR5UY/q41n4wawnsshs1sUsksWMBaufJWRHDLlWPddstTtMEQvNiXRrV7MiF8w1OsdcN1kwMOcFgziYgyYddnljMztUNrZV3FLxLRw6tPgHMTdyc3v+6vZq5Rach80tHVbwxKySLx7PKtkirchJzasZbCT9yXkHoH76LcRuBT1/HokQqLwGSeNFLFpptWqV6k2m90zEYfI85cG8x+654HnXw1c+5fnIQzx0NplQPvk82O7Bsx4oBzx4xDPpmfZwnmQJxZhKaAmVXT8JZiPUejIAF8FqetElRxRrtmRyRSy56VkI2Zt1X19EdV9fRvlYwMN/qYKqzbmptfXa6qlqfTlUT3tGR7GZi95QOLW3dH9ZOC3XDval90kJ/02UhW5WK0mFaNN4PX7OiWeduNq5zUmMlnouYo/aid0uIE7iZI4TOd4cMagGi2YQTNZ0WxNiwak69IaPrjDNw4FOaS7Y2Tm2sqzTp4MsZVPKq3EoPZSu2BILnW7h8NZo9+E7YtX//M/ryjwNLuvKdTX2sd3k2yWFv/hF68KdG2uM+o1Gu9WYyEsR8OEwrKULMtOEWnK/HR9Pw6a0o2nE4SxwEkNmVmZx5mOZvFiguUwmlx/5cdWk/5R/3s/56XJt2qzRp5rhLdU8WLvPgR0o4vHo5UiWpG+yJdItZB4WW2FFklW/dDXl4qvVffpy1llNFycXY5p53ZB9MJ9ePdaZV1NTlZ2xcWt7yYHv9fnfeLnxrl2Vi49WNJVn4YdsPg3/KrXhnt0bdKJRX2F1OlLUv/rRrZ98WNT13YPN+GRg++EtWw5vZy+XE2czCfKKAd2rFunqfQibEF67E+1Fh9EJxDvRTvQC+hniKfYsnKhNF0zYRCdbHdZMLKFUrNOmTZggk2SKmE6Z4qZZk34agHkTZ0rWs4zRDHUs5GI4Q0e4ZA2LEwUshDHpcmK/gik8X7reMK1NT+Fw+JoyHHSuBp2f0T2J3Pg5NcWgz9IX6znRpOAFhY7T9enn2lEFr1JqlT6FO6JcUt5VPlL4EQXbgdQKRJ7eJpSzrEFvUpwKeW1ewRcYK8f60nbuqeW+CX4K6tgQxvgZjXU7yVDz8RPaCQVPKEcUwggr7z+mPatg2u2IwjkVzCv4IwW/oGAqh5F8CgHiXsrwsMKxXtOxAa1xmfdZ5QWFPKxgn7KTctoVQik/UzgK02lMKLq1nyv4LOhITik4X6ETnmDi9JIC66FgWSlTIsqkMq3ElSvKvCJKigzorMJnpqTk1HPILUHdOOnmRXeOO+JKR9kRLsuaGjF0W7DFYgBDs03GAmti1wXhdB1kyWgskYFG6ZWIKp2dCdTHSklWUi6zMAp18jSlfM3ykTZ9+Uibi+meZav+2yef9DUdaCgJ56wskQpyFH+28bPPfrbIH+XaVxbW7Pne/gqT+Ppho8m1sS98svWLT9wlJW5wiaWlRA2tm04tQAJCNgFqhU+hwfQ8icqKHnmwrzzhOyf5blKum0Y6kvO8QQdOJQrU9uu+WNLmBXxBwNPCKYH0CdgnYCTgd4WPBHKRNYwIk6zBJODLdiFfWCVwrJX2P/PmrzUmJ/PiJW1WuCiQswKeFKgsrk+YYEyTqu38ixov2AUCI10S3l2m/sMZLV+oFVoFDgmSQP6OCVrx5NNUoXcFvCyOKbYKGKnAI4KOsR04+m2NKUY1ojL6BN0R4WHhrHBBoNrpI0JUIDxTt1bgx/qg7Slou0TbygSVttGuXD4IfQq6XRJ0koB5AbcKCTlMiixQXs7ObFIDI6tnqjZqTH1DxQZt2Q6ABcs12i+J+QMaHTmJFRRrVEVmMXWT7NGoWELYrKPCCJiLznVe0AfoOEAkRXAKJBEuCodLHYeghieIPDRphQgdQJyIELhkNo2mNBuEaNkOOc4HHsj+fF1Jl0z4YSdzTXrRBmBm5zv2JixZ2jJnHmVpMa08LZSOT174xeJ3+W43ljyLHyVyhHtxCxeHHOFGZWhajQ0Gbg0QfS4+YnvYRvQ2fMT0sIlwJizqMTas0CxBNYhRcDJIKgGIBEeC08GLwfmgLgFw24LY5+Bz6hM78SLdie6cSJ6zNJLm8BY28QYJRTjrcnEzl9yFNLF3sni5fF5LTiAt+ZKL5vNktZfH5Sbe3OHE0UWw5WFIjsf3YpGkV9ZsLmh/YFdo1cDfDoZGQ/RN3FOL6iHSt2Jj17qy/QXe/tCRWzgoddak5qabqw7/8OD4+bvCJpPZ5c4xLGYGApnc1v7pnV6btGATDW9R+yC2D4/BUnnRg6oEUVkUrZlWYuGgTiB59NXfWlq+a4KcIRNJ9qt+jPyTflIp+af9RPVHAZn2x/2z/it+QWborJ/PNtX/xotZsZwG51+vJeJxZBuNuqZcyRaxI5ZZg/ToQqu/ueCyccBWtBSm79Tom7XkETZpA3ZyYe+QAHOkJ8vlQrLoqetZl7lmdTDVuy80dfvC0ftxAINtSr65dfb1Vfu+P1rWG91RgOf7j2738AazuJAhir/kSzNLFuNpK8vLMxXfv71/6OV7NFNqlhVdPd/rmlEKeE3PD10SnHCd55bm1Q1mq7Y9vT+d2CSADpnxrUZ8ix4f4rBlGApKRVWIqkSVUxC8+cxhVZwWT4mcmDZuEnLHdVlo+ZQG9Rz1gsTJ1QdlLoEj2goQmhoKpnJfObaX73t6lB7cL10+l7Wut2Fz9+r09NXdmxt612WRp36w+MXMTtyHW/FNuGfxvy0+N/27E01NJ343Pf37J9ranvg9i6Fnoea9W6chI6pSvZI5YiYR84g5bp4382hIlXQY6VRdRHdKF9fpRJ0wadQj3TiXyXJ+9uusIM0GrSHV68AzPTZduSdExnHqQgCnLf4R37sqQt8g8+Ge8nfYePCxvbfxNc8H3db1HyNX4rcHPz+24lvL34/TE6LuHFQE9IcJJEmEfoJ7sQ7dfPU7dnzDt/Tp5H1Uq/sJspNnUDF3DG3loYAmlcgHTwSfrVQU0DWAN5PKpVfg6YRPAHjsQI/Asxj6VjPeZyC3IETT0VZqIbj86Ke4AD9C/OQZrp27yF3k9/D/qvuRfrX+p0KO8IgwL35msBhLjS+ZNCib/jWpYSZaA3MogFqLwC4K0N9D8CZ9JcMRysHbr84jenVOGDijSRjiKRpJwhxyokNJmAeeh5KwDlnQk0lYj6wonoQFdBt6OQmLyI4rk7ABWfBNSdgEOuy8+qucUrwsPwUN4+8nYQuqInYYHfNQ76FZ0pyEMZK51CRMkIULJmEOrebUJMwDz8EkrEM53CNJWI/yuNNJWEAfcReTsIiK+AtJ2IBy+PeTsAlV6MQkbEbf0C3LT0Hv6E4mYQu6XX/bpuGRW8cGdw9MyEW9xXKwrGyN3Bzrk7WeCb/cMNRbKm/ct09mDOPyWGw8NnYw1lcqb2moqWve2Nqwbas8OC73yBNjPX2x/T1je+Xh/uv7bxncFRvrmRgcHpJbYmOD/c2x3Qf29YxtHO+NDfXFxuQS+UaOG/HtsbFxiqwsLVtTuurL1huZ/4IioP3uwfGJ2BgQB4fkttKWUjnSMxEbmpB7hvrk1qsdt/X3D/bGGLE3NjbRA8zDEwOg6p4DY4PjfYO9dLTx0qsz2DQ8NjKcVGkidjAm39QzMREbHx4amJgYWRsIHDp0qLQnydwLvKW9w/sDf65t4taRWF9sfHD3EMy8dGBi/74toNDQOCh+gI0I2lxrtfDwECzOvgSPXx6PxWQqfhzk98f6QLWRseE9sd6J0uGx3YFDg3sHAwl5g0O7A1+KoVKS4/zneqNNaBj24K1oDA2i3WgATSAZFaFeOG3JKAiVQxnscRk1oxjqg6eGeoDDD1ADGgKuUoDor4P2wfNLCeMMi8EzBs+DrC/l3AK9alAdSNuIWgHeBvFHhl6Uvwc+E8DdA7wxtB+eY3Cyk0G7/j87/hbov4uNQ1sGgX8IWlsYZRD60p670QHQkErcCGP1AmWIjTIGnCVMrz8v4y+1b2fQ+NWWlaAXtVspWvUn+/4lyf85iyRsv5tJmWCyE5yDTHYbcLQwrgjrSW0xwUYbYlytf2LEbTBiP/SnlvuSs5fJngA8IXkY4IGkVfeAxceYBn2s3/LcxmHkr64B9cEx8MLhG6xEtTvIxryJ0SeYT9G2AYaNoLWQdQKQN+hfKfBcL7k3KbeUQfuB8z/abwJ2yAizY4yt827gTax5KZO5H/xrS9JCQ8zvqYUOXDPHhG2+ztfC7JnYOfuuk0NXlj5p32Xtx5P697NxElYbgfsw2D3GrF3KqLvZHAdhDQcBulY/umK7k7QbtVnW5fr5/P8cm0vWTm4Y8U9cM4boS5h+Q1zN7i9jXu3AVxbwzxewvIDv/BxHPseTH05/SP44X+x6bv7lebLtg+4PnvuAK/sAWz/AIpqT5iJz0bmRuVNzeqP1fWxG72Hb/7xS4fpN6O22d0KX29DbeH3k7cm3429ztK7f8bZoCr+NubbLnMMlzcqzZbMjs5OzF2evzM7PipMvTb9E/seLAZf1RdeLxHVm25k7z3DRH2DrD1w/IJET0RNk+iS2nnSdDJzkHn+s1PVYfZ7r0UcKXVcemX+E/Xig/JEUW7j7O/jOhx58iIzcM3nP9D3c5N3Td5PnDr58kIxHil3DQz7XUL3XlRXKbBNCXJueW2JfwNbu8hSFo92qqxuYdu4oc+2oL3alhVLbdKAsD4xWzsVVc9u4Ye5B7mVOEJsjea4m+FyJzEeIdZtrW2Ab+w6rp9ENgjaPbJ7czDWEi11afYXLWu+qD9T/vP439R/U67vr8RPwH34u/HKYU8PFgbAaznOHczRnmyOU3iaFrG0EozYcQm0B65KVWK3d1jut9ICIyKQD6/A5PD3T2uLzNZ4Tlpob42JkZxzfF/e00LvatCOuvy+O2nbsbJ/B+Fsddx87hmpyG+PBlvZ4NLejMd4HgEqBSQCk3BkHqukYH5+g32/6sM8H4AG4I98BIHWNJ4jIt9yMfON4fByNj2MfbWMgUNC4j5IphfbB0LNrHNEbbfUxLgqNj2d2/V+tSjFPCmVuZHN0cmVhbQplbmRvYmoKCjYgMCBvYmoKNzc3NgplbmRvYmoKCjcgMCBvYmoKPDwvVHlwZS9Gb250RGVzY3JpcHRvci9Gb250TmFtZS9CQUFBQUErTGliZXJhdGlvblNlcmlmCi9GbGFncyA0Ci9Gb250QkJveFstMTc2IC0zMDMgMTAwNSA5ODFdL0l0YWxpY0FuZ2xlIDAKL0FzY2VudCA4OTEKL0Rlc2NlbnQgLTIxNgovQ2FwSGVpZ2h0IDk4MQovU3RlbVYgODAKL0ZvbnRGaWxlMiA1IDAgUgo+PgplbmRvYmoKCjggMCBvYmoKPDwvTGVuZ3RoIDMyNC9GaWx0ZXIvRmxhdGVEZWNvZGU+PgpzdHJlYW0KeJxdkstugzAQRfd8hZfpIgKbhCQSQkpJkFj0odJ+ALGH1FIxlnEW/H09nrSVugCdufPQZYa0bk+t0T59dZPswLNBG+Vgnm5OArvAVZuEC6a09PcovuXY2yQNvd0yexhbM0xlmaRvITd7t7DVUU0XeEjSF6fAaXNlq4+6C3F3s/YLRjCeZUlVMQVDmPPU2+d+hDR2rVsV0tov69DyV/C+WGAixpysyEnBbHsJrjdXSMosq1jZNFUCRv3LCUEtl0F+9i6U8lCaZRteBRaRdwI5j1zkyBviPfKW+IBcUH2BvCO9Qd5HFhnygfQ4/0i8QX4kPiLXNGeLfCL9jHwmPdY3pGMNz4hrZPJfoAdO/hv0zMn/Dj3wu3/8Lk7+86iT/5zHRd03givDm/6cgsmbc+EM8fBx/7h5beD337CTxa74fANwGJ6RCmVuZHN0cmVhbQplbmRvYmoKCjkgMCBvYmoKPDwvVHlwZS9Gb250L1N1YnR5cGUvVHJ1ZVR5cGUvQmFzZUZvbnQvQkFBQUFBK0xpYmVyYXRpb25TZXJpZgovRmlyc3RDaGFyIDAKL0xhc3RDaGFyIDIyCi9XaWR0aHNbMzY1IDcyMiAzMzMgNDQzIDUwMCAyNzcgNTAwIDUwMCAyNTAgNDQzIDUwMCAyNzcgNTAwIDUwMCAyNzcgNDQzCjI3NyAzMzMgNTAwIDUwMCA1MDAgNTAwIDUwMCBdCi9Gb250RGVzY3JpcHRvciA3IDAgUgovVG9Vbmljb2RlIDggMCBSCj4+CmVuZG9iagoKMTAgMCBvYmoKPDwvRjEgOSAwIFIKPj4KZW5kb2JqCgoxMSAwIG9iago8PC9Gb250IDEwIDAgUgovUHJvY1NldFsvUERGL1RleHRdCj4+CmVuZG9iagoKMSAwIG9iago8PC9UeXBlL1BhZ2UvUGFyZW50IDQgMCBSL1Jlc291cmNlcyAxMSAwIFIvTWVkaWFCb3hbMCAwIDU5NSA4NDJdL0dyb3VwPDwvUy9UcmFuc3BhcmVuY3kvQ1MvRGV2aWNlUkdCL0kgdHJ1ZT4+L0NvbnRlbnRzIDIgMCBSPj4KZW5kb2JqCgo0IDAgb2JqCjw8L1R5cGUvUGFnZXMKL1Jlc291cmNlcyAxMSAwIFIKL01lZGlhQm94WyAwIDAgNTk1IDg0MiBdCi9LaWRzWyAxIDAgUiBdCi9Db3VudCAxPj4KZW5kb2JqCgoxMiAwIG9iago8PC9UeXBlL0NhdGFsb2cvUGFnZXMgNCAwIFIKL09wZW5BY3Rpb25bMSAwIFIgL1hZWiBudWxsIG51bGwgMF0KL0xhbmcoZXMtRVMpCj4+CmVuZG9iagoKMTMgMCBvYmoKPDwvQ3JlYXRvcjxGRUZGMDA1NzAwNzIwMDY5MDA3NDAwNjUwMDcyPgovUHJvZHVjZXI8RkVGRjAwNEMwMDY5MDA2MjAwNzIwMDY1MDA0RjAwNjYwMDY2MDA2OTAwNjMwMDY1MDAyMDAwMzUwMDJFMDAzMT4KL0NyZWF0aW9uRGF0ZShEOjIwMTcwNTA4MTYwMzUwKzAyJzAwJyk+PgplbmRvYmoKCnhyZWYKMCAxNAowMDAwMDAwMDAwIDY1NTM1IGYgCjAwMDAwMDkwODQgMDAwMDAgbiAKMDAwMDAwMDAxOSAwMDAwMCBuIAowMDAwMDAwMjU5IDAwMDAwIG4gCjAwMDAwMDkyMjcgMDAwMDAgbiAKMDAwMDAwMDI3OSAwMDAwMCBuIAowMDAwMDA4MTQwIDAwMDAwIG4gCjAwMDAwMDgxNjEgMDAwMDAgbiAKMDAwMDAwODM1NiAwMDAwMCBuIAowMDAwMDA4NzQ5IDAwMDAwIG4gCjAwMDAwMDg5OTcgMDAwMDAgbiAKMDAwMDAwOTAyOSAwMDAwMCBuIAowMDAwMDA5MzI2IDAwMDAwIG4gCjAwMDAwMDk0MjMgMDAwMDAgbiAKdHJhaWxlcgo8PC9TaXplIDE0L1Jvb3QgMTIgMCBSCi9JbmZvIDEzIDAgUgovSUQgWyA8NzZCOTNDRjlCNUE3MzBDMURCNDYxNDU0NTcyMzVGNjI+Cjw3NkI5M0NGOUI1QTczMEMxREI0NjE0NTQ1NzIzNUY2Mj4gXQovRG9jQ2hlY2tzdW0gL0Q4QzM5NkMyRjBERDk4QTlGOTI1MEZEMjc5Nzg5OUZDCj4+CnN0YXJ0eHJlZgo5NTk4CiUlRU9GCg==")));
			notificacioService.updateCertificacio(
					references.get(i),
					NotificaCertificacioTipusEnumDto.ACUSE,
					NotificaCertificacioArxiuTipusEnumDto.PDF,
					certificacioId,
					"NumSeguiment"+i,
					new Date(1234567));
			
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

}