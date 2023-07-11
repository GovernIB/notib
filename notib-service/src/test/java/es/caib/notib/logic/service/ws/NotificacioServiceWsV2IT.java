package es.caib.notib.logic.service.ws;

import es.caib.notib.client.domini.DocumentV2;
import es.caib.notib.client.domini.EntregaPostal;
import es.caib.notib.client.domini.EntregaPostalVia;
import es.caib.notib.client.domini.Enviament;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.Idioma;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.client.domini.NotificaDomiciliConcretTipus;
import es.caib.notib.client.domini.ServeiTipus;
import es.caib.notib.client.domini.NotificacioV2;
import es.caib.notib.client.domini.Persona;
import es.caib.notib.client.domini.RespostaAlta;
import es.caib.notib.logic.helper.NotificaHelper;
import es.caib.notib.logic.helper.PermisosHelper;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import es.caib.notib.logic.intf.ws.notificacio.NotificacioServiceWsV2;
import es.caib.notib.logic.service.BaseServiceTestV2;
import es.caib.notib.logic.test.data.ConfigTest;
import es.caib.notib.logic.test.data.EntitatItemTest;
import es.caib.notib.logic.test.data.NotificacioItemTest;
import es.caib.notib.logic.test.data.ProcedimentItemTest;
import es.caib.notib.persist.entity.AplicacioEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.cie.EntregaPostalEntity;
import es.caib.notib.persist.repository.AplicacioRepository;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.EnviamentTableRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.persist.repository.NotificacioTableViewRepository;
import es.caib.notib.persist.repository.PagadorCieRepository;
import es.caib.notib.persist.repository.PagadorPostalRepository;
import es.caib.notib.plugin.SistemaExternException;
import es.caib.notib.plugin.registre.RegistrePluginException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/logic/application-context-test.xml"})
@Transactional
public class NotificacioServiceWsV2IT extends BaseServiceTestV2 {
	
	private static final int NUM_ENVIAMENTS = 2;
	
	@Autowired
	PermisosHelper permisosHelper;
	@Autowired
	NotificacioRepository notificacioRepository;
	@Autowired
	EntitatRepository entitatRepository;
	@Autowired
	PagadorCieRepository cieRepository;
	@Autowired
	AplicacioRepository aplicacioRepository;
	@Autowired
	PagadorPostalRepository operadorPostalRepository;
	@Autowired
	NotificacioTableViewRepository notificacioTableViewRepository;
	@Autowired
	private NotificaHelper notificaHelper;

	@Autowired
	NotificacioServiceWsV2 notificacioService = new NotificacioServiceWsImplV2();


	EntitatDto entitatCreate;

	@Autowired
	ProcedimentItemTest procedimentCreator;
	@Autowired
	NotificacioItemTest notificacioCreator;
	@Autowired
	EnviamentTableRepository enviamentTableRepository;

	private ElementsCreats database;
	private AplicacioEntity aplicacio;

	@Before
	public void setUp() throws Exception {
		setDefaultConfigs();
		configureMockGestioDocumentalPlugin();
		addConfig("es.caib.notib.document.metadades.por.defecto", "true");
		addConfig("es.caib.notib.procediment.alta.auto.retard", "10");
		addConfig("es.caib.notib.procediment.alta.auto.caducitat", "15");
		addConfig("es.caib.notib.notificacio.document.size", "10485760");

		entitatCreate = EntitatItemTest.getRandomInstance();

//		PagadorCieEntity cie = cieRepository.save(PagadorCieEntity.builder("A04013511", "", new Date(0), null).build());
//		PagadorPostalEntity operadorPostal = operadorPostalRepository.save(PagadorPostalEntity.builder("A04013511", "", "pccNum_" + 0, new Date(0), "ccFac_" + 0, null).build());
		procedimentCreator.addObject("procediment", procedimentCreator.getRandomInstance());
//		procedimentCreator.addObject("procedimentCIE", procedimentCreator.getRandomInstanceAmbEntregaCie(cie.getId(), operadorPostal.getId()));

		notificacioCreator.addObject("notificacio", notificacioCreator.getRandomInstance());
		notificacioCreator.addRelated("notificacio", "procediment", procedimentCreator);

		notificacioCreator.addObject("notificacioCIE", notificacioCreator.getRandomInstance(1));
		notificacioCreator.addRelated("notificacioCIE", "procedimentCIE", procedimentCreator);

		notificacioCreator.addObject("notificacioCIE", notificacioCreator.getRandomInstance(1));
		notificacioCreator.addRelated("notificacioCIE", "procedimentCIE", procedimentCreator);

//		NotificacioDatabaseDto notificacioCIEAmbEntregaPostal = notificacioCreator.getRandomInstance(1);
//		NotEnviamentDatabaseDto enviament = notificacioCIEAmbEntregaPostal.getEnviaments().get(0);
//		EntregaPostal entregaPostal = getEntregaPostalDtoRandomData();
//		enviament.setEntregaPostal(entregaPostal);
//
//		notificacioCreator.addObject("notificacioCIEAmbEntregaPostal", notificacioCIEAmbEntregaPostal);
//		notificacioCreator.addRelated("notificacioCIEAmbEntregaPostal", "procedimentCIE", procedimentCreator);


		database = createDatabase(EntitatItemTest.getRandomInstance(),
				procedimentCreator,
				notificacioCreator
		);

		aplicacio = AplicacioEntity.builder()
				.usuariCodi(ConfigTest.ADMIN_USER_CODE)
				.callbackUrl("")
				.activa(true)
				.entitat(entitatRepository.findById(database.getEntitat().getId()).orElseThrow())
				.build();
		aplicacio = aplicacioRepository.saveAndFlush(aplicacio);
	}

	@After
	public final void tearDown() {
		aplicacioRepository.delete(aplicacio);

		destroyDatabase(database.getEntitat().getId(),
				notificacioCreator,
				procedimentCreator
		);
	}
	@Test
	public void whenAlta_thenAllFieldsFilledCorrectly() throws IOException, RegistrePluginException, SistemaExternException, RegistreNotificaException, GeneralSecurityException {
		configureMockRegistrePlugin();
		configureMockDadesUsuariPlugin();

		authenticationTest.autenticarUsuari(ConfigTest.ADMIN_USER_CODE);

		ProcSerDto procedimentCreate = (ProcSerDto) database.get("procediment");
		assertNotNull(procedimentCreate);
		assertNotNull(procedimentCreate.getId());

		NotificacioV2 notificacio = getRandomNotificacio(2);
		notificacio.setProcedimentCodi(procedimentCreate.getCodi());

		RespostaAlta respostaAlta = notificacioService.alta(notificacio);
		assertNotNull(respostaAlta);
		assertFalse(respostaAlta.getErrorDescripcio(), respostaAlta.isError());
		assertNotNull(respostaAlta.getIdentificador());

		Long notificacioId = notificaHelper.desxifrarId(respostaAlta.getIdentificador());
		NotificacioEntity notificacioCreated = notificacioRepository.findById(notificacioId).orElseThrow();
		assertNotNull(notificacioCreated);
		try {
			assertEqualsNotificacions(notificacio, notificacioCreated, NUM_ENVIAMENTS);
		}finally {
//			notificacioService.delete(entitatCreate.getId(), notificacioCreatedDto.getId());
		}

	}

	private EntregaPostal getEntregaPostalDtoRandomData() {
		return EntregaPostal.builder()
				.tipus(NotificaDomiciliConcretTipus.NACIONAL)
				.viaTipus(EntregaPostalVia.VIA)
				.viaNom("Via Asima")
				.numeroCasa("4")
				.provincia("07")
				.municipiCodi("337")
				.codiPostal("07500")
				.poblacio("Palma")
				.build();
	}
	@Test
	public void whenAltaAmbProcedimentCIEAmbEntregaPostal_thenAllFieldsFilledCorrectly() throws IOException, RegistrePluginException, SistemaExternException, RegistreNotificaException, GeneralSecurityException {
		configureMockRegistrePlugin();
		configureMockDadesUsuariPlugin();

		authenticationTest.autenticarUsuari(ConfigTest.ADMIN_USER_CODE);

		// Given
		ProcSerDto procedimentCreate = (ProcSerDto) database.get("procedimentCIE");
		assertNotNull(procedimentCreate);
		assertNotNull(procedimentCreate.getId());

		NotificacioV2 notificacio = getRandomNotificacio(1);
		notificacio.setProcedimentCodi(procedimentCreate.getCodi());

		Enviament enviament = notificacio.getEnviaments().get(0);
		EntregaPostal entregaPostal = getEntregaPostalDtoRandomData();
		enviament.setEntregaPostal(entregaPostal);
		enviament.setEntregaPostalActiva(true);

		// When
		RespostaAlta respostaAlta = notificacioService.alta(notificacio);
		assertNotNull(respostaAlta);
		assertFalse(respostaAlta.getErrorDescripcio(), respostaAlta.isError());
		assertNotNull(respostaAlta.getIdentificador());

		try {
			// Then
			Long notificacioId = notificaHelper.desxifrarId(respostaAlta.getIdentificador());
			NotificacioEntity notificacioEntity = notificacioRepository.findById(notificacioId).orElseThrow();
			assertNotNull(notificacioEntity);

			NotificacioEnviamentEntity env = notificacioEntity.getEnviaments().iterator().next();
			assertNotNull(env.getEntregaPostal());

			EntregaPostalEntity entregaPostalEntity = env.getEntregaPostal();
			assertEquals(entregaPostal.getTipus(), entregaPostalEntity.getDomiciliConcretTipus());
//			assertEquals(entregaPostal.getViaTipus(), entregaPostalEntity.getDomiciliViaTipus());
			assertEquals(entregaPostal.getViaNom(), entregaPostalEntity.getDomiciliViaNom());
			assertEquals(entregaPostal.getNumeroCasa(), entregaPostalEntity.getDomiciliNumeracioNumero());
			assertEquals(entregaPostal.getProvincia(), entregaPostalEntity.getDomiciliProvinciaCodi());
			assertEquals(entregaPostal.getMunicipiCodi(), entregaPostalEntity.getDomiciliMunicipiCodiIne());
			assertEquals(entregaPostal.getCodiPostal(), entregaPostalEntity.getDomiciliCodiPostal());
		}finally {
//			notificacioService.delete(entitatCreate.getId(), notificacioCreatedDto.getId());
		}
	}

	@Test
	public void whenAltaAmbProcedimentCIESenseEntregaPostal_thenProcessedCorrectly() throws IOException, RegistrePluginException, SistemaExternException, RegistreNotificaException, GeneralSecurityException {
		configureMockRegistrePlugin();
		configureMockDadesUsuariPlugin();

		authenticationTest.autenticarUsuari(ConfigTest.ADMIN_USER_CODE);

		// Given
		ProcSerDto procedimentCreate = (ProcSerDto) database.get("procedimentCIE");
		assertNotNull(procedimentCreate);
		assertNotNull(procedimentCreate.getId());

		NotificacioV2 notificacio = getRandomNotificacio(1);
		notificacio.setProcedimentCodi(procedimentCreate.getCodi());

		// When
		RespostaAlta respostaAlta = notificacioService.alta(notificacio);
		assertNotNull(respostaAlta);
		assertFalse(respostaAlta.getErrorDescripcio(), respostaAlta.isError());
		assertNotNull(respostaAlta.getIdentificador());

		try {
			// Then
			Long notificacioId = notificaHelper.desxifrarId(respostaAlta.getIdentificador());
			NotificacioEntity notificacioEntity = notificacioRepository.findById(notificacioId).orElseThrow();
			assertNotNull(notificacioEntity);

			// Comprovam que l'enviament no té cap entregapostal asociada
			NotificacioEnviamentEntity env = notificacioEntity.getEnviaments().iterator().next();
			assertNull(env.getEntregaPostal());

		}finally {
//			notificacioService.delete(entitatCreate.getId(), notificacioCreatedDto.getId());
		}

	}

	private void assertEqualsNotificacions(NotificacioV2 notificacioDto, NotificacioEntity notificacioEntity, int numEnviaments) {
		// comprovar si les dades introduïdes són iguals a les que s'han posat a la base de dades
		assertEquals(notificacioDto.getProcedimentCodi(), notificacioEntity.getProcediment().getCodi());
		assertEquals(notificacioDto.getOrganGestor(), notificacioEntity.getOrganGestor().getCodi());
//			assertEquals(notificacio.getGrup().getCodi(), notificacioEntity.getGrupCodi());
		assertNull(notificacioDto.getGrupCodi());
		assertEquals(notificacioDto.getUsuariCodi(), notificacioEntity.getUsuariCodi());
		assertEquals(notificacioDto.getEmisorDir3Codi(), notificacioEntity.getEmisorDir3Codi());
//		assertEquals(notificacioDto.getEnviamentTipus(), notificacioEntity.getEnviamentTipus());
		assertEquals(notificacioDto.getConcepte(), notificacioEntity.getConcepte());
		assertEquals(notificacioDto.getDescripcio(), notificacioEntity.getDescripcio());
		assertEquals(notificacioDto.getEnviamentDataProgramada(), notificacioEntity.getEnviamentDataProgramada());
		assertEquals(notificacioDto.getRetard(), notificacioEntity.getRetard());
		assertEquals(notificacioDto.getCaducitat(), notificacioEntity.getCaducitat());
		assertEquals(notificacioDto.getNumExpedient(), notificacioEntity.getNumExpedient());
		assertEquals(notificacioDto.getIdioma(), notificacioEntity.getIdioma());

		assertEquals(notificacioDto.getDocument().getArxiuNom(), notificacioEntity.getDocument().getArxiuNom());
//		assertEquals(notificacioDto.getDocument().getMediaType(), notificacioEntity.getDocument().getMediaType());
//		assertEquals(notificacioDto.getDocument().getMida(), notificacioEntity.getDocument().getMida());
		if (notificacioDto.getDocument().getContingutBase64() != null) {
			assertNull(notificacioEntity.getDocument().getContingutBase64());
//							assertNotNull(notificacioEntity.getDocument().getArxiuGestdocId()); // El mockito del plugin de gestió documental està mal configurat, sempre retorna null
		}

//						assertEquals(notificacio.getDocument().getHash(), notificacioEntity.getDocument().getHash()); // no sé que fa
		assertEquals(notificacioDto.getDocument().isNormalitzat(), notificacioEntity.getDocument().getNormalitzat());
//			assertEquals(notificacio.getDocument().isGenerarCsv(), notificacioEntity.getDocument().isGenerarCsv());
		assertEquals(notificacioDto.getDocument().getUuid(), notificacioEntity.getDocument().getUuid());
		assertEquals(notificacioDto.getDocument().getCsv(), notificacioEntity.getDocument().getCsv());
		assertEquals(notificacioDto.getDocument().getOrigen(), notificacioEntity.getDocument().getOrigen());
		assertEquals(notificacioDto.getDocument().getValidesa(), notificacioEntity.getDocument().getValidesa());
		assertEquals(notificacioDto.getDocument().getTipoDocumental(), notificacioEntity.getDocument().getTipoDocumental());
		assertEquals(notificacioDto.getDocument().getModoFirma(), notificacioEntity.getDocument().getModoFirma());

		for (int i = 0; i < numEnviaments; i ++ ) {
			Enviament enviament = notificacioDto.getEnviaments().get(i);
			Enviament enviamentCreat = notificacioDto.getEnviaments().get(i);
			assertEquals(enviament.getServeiTipus(), enviamentCreat.getServeiTipus());
			assertEquals(enviament.getTitular().getNom(), enviamentCreat.getTitular().getNom());
			assertEquals(enviament.getTitular().getLlinatge1(), enviamentCreat.getTitular().getLlinatge1());
			assertEquals(enviament.getTitular().getLlinatge2(), enviamentCreat.getTitular().getLlinatge2());
			assertEquals(enviament.getTitular().getNom(), enviamentCreat.getTitular().getNom());

			if (enviament.getDestinataris() != null && enviament.getDestinataris().size() != 0) {
				for (int j = 0; j < enviament.getDestinataris().size(); j ++ ) {
					Persona destinatari = enviament.getDestinataris().get(j);
					Persona destinatariCreat = enviamentCreat.getDestinataris().get(j);

					assertEquals(destinatari.getNom(), destinatariCreat.getNom());
					assertEquals(destinatari.getLlinatge1(), destinatariCreat.getLlinatge1());
					assertEquals(destinatari.getLlinatge2(), destinatariCreat.getLlinatge2());
					assertEquals(destinatari.getEmail(), destinatariCreat.getEmail());
					assertEquals(destinatari.getDir3Codi(), destinatariCreat.getDir3Codi());
					assertEquals(destinatari.getInteressatTipus(), destinatariCreat.getInteressatTipus());
					assertEquals(destinatari.getTelefon(), destinatariCreat.getTelefon());
					assertEquals(destinatari.getRaoSocial(), destinatariCreat.getRaoSocial());

				}
			}
		}
	}

	public static NotificacioV2 getRandomNotificacio(int numEnviaments) {
		NotificacioV2 notCreated = getRandomInstanceWithoutEnviaments();
		List<Enviament> enviaments = new ArrayList<>();
		for (int i = 0; i < numEnviaments; i++) {
			Enviament enviament = getRandomEnviament(i);
			enviaments.add(enviament);
		}
		notCreated.setEnviaments(enviaments);

		return notCreated;
	}


	public static Enviament getRandomEnviament(int i){
		Enviament enviament = new Enviament();
		Persona titular = Persona.builder()
				.interessatTipus(InteressatTipus.FISICA)
				.nom("titularNom" + i)
				.llinatge1("titLlinatge1_" + i)
				.llinatge2("titLlinatge2_" + i)
				.nif("00000000T")
				.telefon("666010101")
				.email("titular@gmail.com").build();
		enviament.setTitular(titular);
		List<Persona> destinataris = new ArrayList<>();
		Persona destinatari = Persona.builder()
				.interessatTipus(InteressatTipus.FISICA)
				.nom("destinatariNom" + i)
				.llinatge1("destLlinatge1_" + i)
				.llinatge2("destLlinatge2_" + i)
				.nif("12345678Z")
				.telefon("666020202")
				.email("destinatari@gmail.com").build();
		destinataris.add(destinatari);
		enviament.setDestinataris(destinataris);
		enviament.setServeiTipus(ServeiTipus.URGENT);
//		enviament.setNotificaEstat(EnviamentEstat.NOTIB_PENDENT);
		return enviament;
	}

	public static NotificacioV2 getRandomInstanceWithoutEnviaments() {
		String notificacioId = new Long(System.currentTimeMillis()).toString();

		DocumentV2 document = new DocumentV2();
		try {
			byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());
			document.setContingutBase64(Base64.encodeBase64String(arxiuBytes));
//			document.setHash(
//					Base64.encodeBase64String(
//							Hex.decodeHex(
//									DigestUtils.sha1Hex(arxiuBytes).toCharArray())));
		} catch (IOException e) {
			e.printStackTrace();
		}

		document.setArxiuNom("documentArxiuNom_" + notificacioId + ".pdf");
		document.setNormalitzat(false);
//		document.setGenerarCsv(false);

		Date caducitat = new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000);
		Date enviamentDataProgramada = new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000);
		NotificacioV2 notCreated = NotificacioV2.builder()
				.emisorDir3Codi(ConfigTest.ENTITAT_DGTIC_DIR3CODI)
				.enviamentTipus(EnviamentTipus.NOTIFICACIO)
				.enviamentDataProgramada(enviamentDataProgramada)
				.concepte("Test")
				.descripcio("Test descripció")
				.organGestor("A00000000")
				.enviamentDataProgramada(new Date())
				.retard(5)
				.caducitat(caducitat)
//                .procediment(procediment)
//				.procedimentCodiNotib()
//                .grup(grupCreate)
				.enviaments(new ArrayList<Enviament>())
				.usuariCodi("admin")
//				.motiu()
				.numExpedient("EXPEDIENTEX")
				.idioma(Idioma.CA)
				.document(new DocumentV2())
				.build();
		notCreated.setDocument(document);
		return notCreated;
	}


	private static InputStream getContingutNotificacioAdjunt() {
		return NotificacioItemTest.class.getResourceAsStream(
                "/es/caib/notib/logic/notificacio_adjunt.pdf");
	}
}


