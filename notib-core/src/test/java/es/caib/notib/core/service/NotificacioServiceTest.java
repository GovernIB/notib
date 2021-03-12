package es.caib.notib.core.service;

import static org.junit.Assert.assertNotNull;

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

import es.caib.notib.core.api.dto.DocumentDto;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.EntitatTipusEnumDto;
import es.caib.notib.core.api.dto.InteressatTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioDtoV2;
import es.caib.notib.core.api.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.PersonaDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.ServeiTipusEnumDto;
import es.caib.notib.core.api.dto.TipusDocumentDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.plugin.SistemaExternException;
import es.caib.notib.plugin.registre.RegistrePluginException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class NotificacioServiceTest extends BaseServiceTest {
	
	private static final String ENTITAT_DGTIC_DIR3CODI = "EA0004518";
	private static final String ENTITAT_DGTIC_KEY = "MjkwNTc3Mjk0MjkyNTU3OTkyNA==";
	
	private static final int NUM_DESTINATARIS = 2;
	
	@Autowired
	PermisosHelper permisosHelper;
	@Autowired
	NotificacioService notificacioService;
	
	EntitatDto entitatCreate;
	ProcedimentDto procedimentCreate;
	OrganGestorDto organGestorCreate;
	
	@Before
	public void setUp() throws SistemaExternException, IOException, DecoderException, RegistrePluginException {
		List<PermisDto> permisosEntitat = new ArrayList<PermisDto>();
		entitatCreate = new EntitatDto();
		entitatCreate.setCodi("LIMIT");
		entitatCreate.setNom("Limit Tecnologies");
		entitatCreate.setDescripcio("Descripció de Limit Tecnologies");
		entitatCreate.setTipus(EntitatTipusEnumDto.GOVERN);
		entitatCreate.setDir3Codi(ENTITAT_DGTIC_DIR3CODI);
		entitatCreate.setApiKey(ENTITAT_DGTIC_KEY);
		entitatCreate.setAmbEntregaDeh(true);
		entitatCreate.setAmbEntregaCie(true);
		TipusDocumentDto tipusDocDefault = new TipusDocumentDto();
		tipusDocDefault.setTipusDocEnum(TipusDocumentEnumDto.UUID);
		entitatCreate.setTipusDocDefault(tipusDocDefault);
		
		PermisDto permisUsuari = new PermisDto();
		PermisDto permisAdminEntitat = new PermisDto();
		
		permisUsuari.setUsuari(true);
		permisUsuari.setTipus(TipusEnumDto.USUARI);
		permisUsuari.setPrincipal("admin");
		permisosEntitat.add(permisUsuari);
		
		permisAdminEntitat.setAdministradorEntitat(true);
		permisAdminEntitat.setTipus(TipusEnumDto.USUARI);
		permisAdminEntitat.setPrincipal("admin");
		permisosEntitat.add(permisAdminEntitat);
		entitatCreate.setPermisos(permisosEntitat);
		
		List<PermisDto> permisosOrgan = new ArrayList<PermisDto>();
		organGestorCreate = new OrganGestorDto();
		organGestorCreate.setCodi("A00000000");
		organGestorCreate.setNom("Òrgan prova");
		PermisDto permisOrgan = new PermisDto();
		permisOrgan.setAdministrador(true);
		permisOrgan.setTipus(TipusEnumDto.USUARI);
		permisOrgan.setPrincipal("admin");
		permisosOrgan.add(permisOrgan);
		organGestorCreate.setPermisos(permisosOrgan);
		
		List<PermisDto> permisosProcediment = new ArrayList<PermisDto>();
		procedimentCreate = new ProcedimentDto();
		procedimentCreate.setCodi("216076");
		procedimentCreate.setNom("Procedimiento 1");
		procedimentCreate.setOrganGestor("A00000000");
		PermisDto permisNotificacio = new PermisDto();
		permisNotificacio.setNotificacio(true);
		permisNotificacio.setTipus(TipusEnumDto.USUARI);
		permisNotificacio.setPrincipal("admin");
		permisosProcediment.add(permisNotificacio);
		
		procedimentCreate.setPermisos(permisosProcediment);
	}
	
	@Test
	public void create() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws Exception {
					configureMockRegistrePlugin();
					configureMockUnitatsOrganitzativesPlugin();
					configureMockDadesUsuariPlugin();
					configureMockGestioDocumentalPlugin();
					autenticarUsuari("admin");
					
					EntitatDto entitatCreate = (EntitatDto)elementsCreats.get(0);
					ProcedimentDto procedimentCreate = (ProcedimentDto)elementsCreats.get(2);
					assertNotNull(procedimentCreate);
					assertNotNull(procedimentCreate.getId());
					assertNotNull(entitatCreate);
					assertNotNull(entitatCreate.getId());
					String notificacioId = new Long(System.currentTimeMillis()).toString();
					NotificacioDtoV2 notificacio = generarNotificacio(
							notificacioId,
							procedimentCreate,
							entitatCreate,
							NUM_DESTINATARIS,
							false);
					
					 NotificacioDtoV2 notificacioCreated = notificacioService.create(
							entitatCreate.getId(), 
							notificacio);
					
					assertNotNull(notificacioCreated);
				}
			}, 
			"Create Notificació",
			entitatCreate,
			organGestorCreate,
			procedimentCreate);
	}
	
	private NotificacioDtoV2 generarNotificacio(
			String notificacioId,
			ProcedimentDto procediment,
			EntitatDto entitat,
			int numDestinataris,
			boolean ambEnviamentPostal) throws IOException, DecoderException {
		byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());
		NotificacioDtoV2 notificacio = new NotificacioDtoV2();
		notificacio.setUsuariCodi("admin");
		notificacio.setEmisorDir3Codi(ENTITAT_DGTIC_DIR3CODI);
		notificacio.setEnviamentTipus(NotificaEnviamentTipusEnumDto.NOTIFICACIO);
		notificacio.setConcepte(
				"concepte_" + notificacioId);
		notificacio.setDescripcio(
				"descripcio_" + notificacioId);
		notificacio.setEnviamentDataProgramada(new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000));
		notificacio.setRetard(5);
		notificacio.setCaducitat(new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000));
		DocumentDto document = new DocumentDto();
		document.setArxiuNom("documentArxiuNom_" + notificacioId + ".pdf");
		document.setContingutBase64(Base64.encodeBase64String(arxiuBytes));
		document.setHash(
				Base64.encodeBase64String(
						Hex.decodeHex(
								DigestUtils.sha1Hex(arxiuBytes).toCharArray())));
		document.setNormalitzat(false);
		document.setGenerarCsv(false);
		notificacio.setDocument(document);
		notificacio.setProcediment(procediment);
		notificacio.setOrganGestor("A00000000");
		notificacio.setEntitat(entitat);
		List<NotificacioEnviamentDtoV2> enviaments = new ArrayList<NotificacioEnviamentDtoV2>();
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
		for (int i = 0; i < numDestinataris; i++) {
			NotificacioEnviamentDtoV2 enviament = new NotificacioEnviamentDtoV2();
			PersonaDto titular = new PersonaDto();
			titular.setInteressatTipus(InteressatTipusEnumDto.FISICA);
			titular.setNom("titularNom" + i);
			titular.setLlinatge1("titLlinatge1_" + i);
			titular.setLlinatge2("titLlinatge2_" + i);
			titular.setNif("00000000T");
			titular.setTelefon("666010101");
			titular.setEmail("titular@gmail.com");
			enviament.setTitular(titular);
			List<PersonaDto> destinataris = new ArrayList<PersonaDto>();
			PersonaDto destinatari = new PersonaDto();
			destinatari.setInteressatTipus(InteressatTipusEnumDto.FISICA);
			destinatari.setNom("destinatariNom" + i);
			destinatari.setLlinatge1("destLlinatge1_" + i);
			destinatari.setLlinatge2("destLlinatge2_" + i);
			destinatari.setNif("12345678Z");
			destinatari.setTelefon("666020202");
			destinatari.setEmail("destinatari@gmail.com");
			destinataris.add(destinatari);
			enviament.setDestinataris(destinataris);
//			if (ambEnviamentPostal) {
//				EntregaPostal entregaPostal = new EntregaPostal();
//				entregaPostal.setTipus(NotificaDomiciliConcretTipusEnumDto.NACIONAL);
//				entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.CALLE);
//				entregaPostal.setViaNom("Bas");
//				entregaPostal.setNumeroCasa("25");
//				entregaPostal.setNumeroQualificador("bis");
//				entregaPostal.setPuntKm("pk01");
//				entregaPostal.setApartatCorreus("0228");
//				entregaPostal.setPortal("portal" + i);
//				entregaPostal.setEscala("escala" + i);
//				entregaPostal.setPlanta("planta" + i);
//				entregaPostal.setPorta("porta" + i);
//				entregaPostal.setBloc("bloc" + i);
//				entregaPostal.setComplement("complement" + i);
//				entregaPostal.setCodiPostal("07500");
//				entregaPostal.setPoblacio("poblacio" + i);
//				entregaPostal.setMunicipiCodi("07033");
//				entregaPostal.setProvincia("07");
//				entregaPostal.setPaisCodi("ES");
//				entregaPostal.setLinea1("linea1_" + i);
//				entregaPostal.setLinea2("linea2_" + i);
//				entregaPostal.setCie(new Integer(8));
//			}
//			EntregaDehDto entregaDeh = new EntregaDehDto();
//			entregaDeh.setObligat(true);
//			entregaDeh.setProcedimentCodi("0000");
//			enviament.setEntregaDeh(entregaDeh);
			enviament.setServeiTipus(ServeiTipusEnumDto.URGENT);
			enviaments.add(enviament);
		}
		notificacio.setEnviaments(enviaments);
		return notificacio;
	}
	
	private InputStream getContingutNotificacioAdjunt() {
		return getClass().getResourceAsStream(
				"/es/caib/notib/core/notificacio_adjunt.pdf");
	}
}


