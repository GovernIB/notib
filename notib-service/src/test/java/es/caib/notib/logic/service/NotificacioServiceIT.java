package es.caib.notib.logic.service;

import es.caib.notib.client.domini.NotificaDomiciliConcretTipus;
import es.caib.notib.logic.helper.PermisosHelper;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.NotificaDomiciliViaTipusEnumDto;
import es.caib.notib.logic.intf.dto.PermisDto;
import es.caib.notib.logic.intf.dto.PersonaDto;
import es.caib.notib.logic.intf.dto.TipusEnumDto;
import es.caib.notib.logic.intf.dto.cie.EntregaPostalDto;
import es.caib.notib.logic.intf.dto.notenviament.NotEnviamentDatabaseDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDatabaseDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.logic.intf.service.ProcedimentService;
import es.caib.notib.logic.test.data.ConfigTest;
import es.caib.notib.logic.test.data.EntitatItemTest;
import es.caib.notib.logic.test.data.NotificacioItemTest;
import es.caib.notib.logic.test.data.ProcedimentItemTest;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.EnviamentTableEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.NotificacioTableEntity;
import es.caib.notib.persist.entity.cie.EntregaPostalEntity;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.EnviamentTableRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.persist.repository.NotificacioTableViewRepository;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.PagadorCieRepository;
import es.caib.notib.persist.repository.PagadorPostalRepository;
import es.caib.notib.plugin.SistemaExternException;
import es.caib.notib.plugin.registre.RegistrePluginException;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/logic/application-context-test.xml"})
@Transactional
public class NotificacioServiceIT extends BaseServiceTestV2 {
	
	private static final int NUM_ENVIAMENTS = 2;
	
	@Autowired
	PermisosHelper permisosHelper;
	@Autowired
	NotificacioService notificacioService;
	@Autowired
	NotificacioRepository notificacioRepository;
	@Autowired
	PagadorCieRepository cieRepository;
	@Autowired
	EntitatRepository entitatRepository;
	@Autowired
	OrganGestorRepository organGestorRepository;
	@Autowired
	PagadorPostalRepository operadorPostalRepository;
	@Autowired
	NotificacioTableViewRepository notificacioTableViewRepository;
	@Autowired
	EnviamentTableRepository enviamentTableRepository;
	@Autowired
	ProcedimentService procedimentService;

	@Autowired
	private ProcedimentItemTest procedimentCreator;
	@Autowired
	private NotificacioItemTest notificacioCreator;

	private ElementsCreats database;

	@Before
	public void setUp() throws Exception {
		setDefaultConfigs();
		configureMockGestioDocumentalPlugin();

		EntitatEntity entitatEntity = entitatRepository.findByCodi("ENTITAT_TESTS");
//		PagadorCieEntity cie = cieRepository.save(PagadorCieEntity.builder("A04013511", "CIE NOM", new Date(0), entitatEntity).build());
//		PagadorPostalEntity operadorPostal = operadorPostalRepository.save(PagadorPostalEntity.builder("A04013511", "", "pccNum_" + 0, new Date(0), "ccFac_" + 0, null).build());

		procedimentCreator.addObject("procediment", procedimentCreator.getRandomInstance());
//		procedimentCreator.addObject("procedimentCIE", procedimentCreator.getRandomInstanceAmbEntregaCie(cie.getId(), operadorPostal.getId()));
		procedimentCreator.addObject("procedimentSensePermis", ProcedimentItemTest.getRandomProcedimentSensePermis());

		notificacioCreator.addObject("notificacio", NotificacioItemTest.getRandomInstance());
		notificacioCreator.addRelated("notificacio", "procediment", procedimentCreator);

		notificacioCreator.addObject("notificacioCIE", NotificacioItemTest.getRandomInstance(1));
		notificacioCreator.addRelated("notificacioCIE", "procedimentCIE", procedimentCreator);

		notificacioCreator.addObject("notificacioCIE", NotificacioItemTest.getRandomInstance(1));
		notificacioCreator.addRelated("notificacioCIE", "procedimentCIE", procedimentCreator);

		NotificacioDatabaseDto notificacioSensePermisos = NotificacioItemTest.getRandomInstance(1);
		notificacioSensePermisos.setOrganGestorCodi(ConfigTest.ORGAN_DIR3_SENSE_PERMISOS);
		notificacioCreator.addObject("notificacioSensePermisos", notificacioSensePermisos);
		notificacioCreator.addRelated("notificacioSensePermisos", "procedimentSensePermis", procedimentCreator);


		NotificacioDatabaseDto notificacioCIEAmbEntregaPostal = NotificacioItemTest.getRandomInstance(1);
		NotEnviamentDatabaseDto enviament = notificacioCIEAmbEntregaPostal.getEnviaments().get(0);
		EntregaPostalDto entregaPostal = getEntregaPostalDtoRandomData();
		enviament.setEntregaPostal(entregaPostal);

		notificacioCreator.addObject("notificacioCIEAmbEntregaPostal", notificacioCIEAmbEntregaPostal);
		notificacioCreator.addRelated("notificacioCIEAmbEntregaPostal", "procedimentCIE", procedimentCreator);

		database = createDatabase(EntitatItemTest.getRandomInstance(),
				procedimentCreator,
				notificacioCreator
		);
	}

	@After
	public final void tearDown() {
		destroyDatabase(database.getEntitat().getId(),
				notificacioCreator,
				procedimentCreator
		);
	}
	@Test
	public void whenCreate_thenAllFieldsFilledCorrectly() throws IOException, RegistrePluginException, SistemaExternException, RegistreNotificaException {
		configureMockRegistrePlugin();
		configureMockDadesUsuariPlugin();

		authenticationTest.autenticarUsuari("admin");

		EntitatDto entitatCreate = database.entitat;
		ProcSerDto procedimentCreate = (ProcSerDto) database.get("procediment");
		assertNotNull(procedimentCreate);
		assertNotNull(procedimentCreate.getId());
		assertNotNull(entitatCreate);
		assertNotNull(entitatCreate.getId());

		NotificacioDatabaseDto notificacio = NotificacioItemTest.getRandomInstance();
		notificacio.setProcediment(procedimentCreate);

		NotificacioDatabaseDto notificacioCreatedDto = notificacioService.create(
				entitatCreate.getId(),
				notificacio);
		assertNotNull(notificacioCreatedDto);

		NotificacioEntity notificacioCreated = notificacioRepository.findById(notificacioCreatedDto.getId()).orElseThrow();
		assertNotNull(notificacioCreated);
		try {
			assertEqualsNotificacions(notificacio, notificacioCreated, NUM_ENVIAMENTS);
		}finally {
			notificacioService.delete(entitatCreate.getId(), notificacioCreatedDto.getId());
		}

	}

	private EntregaPostalDto getEntregaPostalDtoRandomData() {
		return EntregaPostalDto.builder()
				.domiciliConcretTipus(NotificaDomiciliConcretTipus.NACIONAL)
				.viaTipus(NotificaDomiciliViaTipusEnumDto.VIA)
				.viaNom("Via Asima")
				.numeroCasa("4")
				.provincia("07")
				.municipiCodi("337")
				.codiPostal("07500")
				.build();
	}

	@Test
	public void whenCreateAmbProcedimentCIEAmbEntregaPostal_thenAllFieldsFilledCorrectly() throws IOException, RegistrePluginException, SistemaExternException, RegistreNotificaException {
		configureMockRegistrePlugin();
		configureMockDadesUsuariPlugin();

		authenticationTest.autenticarUsuari("admin");

		// Given
		EntitatDto entitatCreate = database.entitat;
		ProcSerDto procedimentCreate = (ProcSerDto) database.get("procedimentCIE");
		assertNotNull(procedimentCreate);
		assertNotNull(procedimentCreate.getId());

		NotificacioDatabaseDto notificacio = NotificacioItemTest.getRandomInstance(1);
		notificacio.setProcediment(procedimentCreate);

		NotEnviamentDatabaseDto enviament = notificacio.getEnviaments().get(0);
		EntregaPostalDto entregaPostal = getEntregaPostalDtoRandomData();
		enviament.setEntregaPostal(entregaPostal);
		enviament.setEntregaPostalActiva(true);

		// When
		NotificacioDatabaseDto notificacioCreatedDto = notificacioService.create(
				entitatCreate.getId(),
				notificacio);
		assertNotNull(notificacioCreatedDto);
		try {
			// Then
			NotificacioEntity notificacioEntity = notificacioRepository.findById(notificacioCreatedDto.getId()).orElseThrow();
			assertNotNull(notificacioEntity);

			NotificacioEnviamentEntity env = notificacioEntity.getEnviaments().iterator().next();
			assertNotNull(env.getEntregaPostal());

			EntregaPostalEntity entregaPostalEntity = env.getEntregaPostal();
			assertEquals(entregaPostal.getDomiciliConcretTipus(), entregaPostalEntity.getDomiciliConcretTipus());
			assertEquals(entregaPostal.getViaTipus(), entregaPostalEntity.getDomiciliViaTipus());
			assertEquals(entregaPostal.getViaNom(), entregaPostalEntity.getDomiciliViaNom());
			assertEquals(entregaPostal.getNumeroCasa(), entregaPostalEntity.getDomiciliNumeracioNumero());
			assertEquals(entregaPostal.getProvincia(), entregaPostalEntity.getDomiciliProvinciaCodi());
			assertEquals(entregaPostal.getMunicipiCodi(), entregaPostalEntity.getDomiciliMunicipiCodiIne());
			assertEquals(entregaPostal.getCodiPostal(), entregaPostalEntity.getDomiciliCodiPostal());
		}finally {
			notificacioService.delete(entitatCreate.getId(), notificacioCreatedDto.getId());
		}
	}

	@Test
	public void whenCreateAmbProcedimentCIESenseEntregaPostal_thenProcessedCorrectly() throws IOException, RegistrePluginException, SistemaExternException, RegistreNotificaException {
		configureMockRegistrePlugin();
		configureMockDadesUsuariPlugin();

		authenticationTest.autenticarUsuari("admin");

		// Given
		EntitatDto entitatCreate = database.entitat;
		ProcSerDto procedimentCreate = (ProcSerDto) database.get("procedimentCIE");
		assertNotNull(procedimentCreate);
		assertNotNull(procedimentCreate.getId());

		NotificacioDatabaseDto notificacio = NotificacioItemTest.getRandomInstance(1);
		notificacio.setProcediment(procedimentCreate);

		// When
		NotificacioDatabaseDto notificacioCreatedDto = notificacioService.create(
				entitatCreate.getId(),
				notificacio);
		assertNotNull(notificacioCreatedDto);
		try {
			// Then
			NotificacioEntity notificacioEntity = notificacioRepository.findById(notificacioCreatedDto.getId()).orElseThrow();
			assertNotNull(notificacioEntity);

			// Comprovam que l'enviament no té cap entregapostal asociada
			NotificacioEnviamentEntity env = notificacioEntity.getEnviaments().iterator().next();
			assertNull(env.getEntregaPostal());

		}finally {
			notificacioService.delete(entitatCreate.getId(), notificacioCreatedDto.getId());
		}

	}

	@Test
	public void whenUpdate_thenAllFieldsFilledCorrectly() throws SistemaExternException, RegistreNotificaException {
		configureMockGestioDocumentalPlugin();

		EntitatDto entitatCreate = database.entitat;
		NotificacioDatabaseDto notificacioCreate = (NotificacioDatabaseDto) database.get("notificacio");

		NotificacioDatabaseDto notificacioEdicio = (NotificacioDatabaseDto) SerializationUtils.clone(notificacioCreate);
		notificacioEdicio.setNumExpedient("FFF000");
		notificacioEdicio.setConcepte("concepte edicio");
		notificacioEdicio.setRetard(2);

		NotificacioDatabaseDto notificacioEditada = notificacioService.update(entitatCreate.getId(),
				notificacioEdicio,
				true
				);

		// comprovar si les dades introduïdes són iguals a les que s'han posat a la base de dades
		NotificacioEntity notificacioEntity = notificacioRepository.findById(notificacioEditada.getId()).orElseThrow();
		assertNotNull(notificacioEntity);

		assertEqualsNotificacions(notificacioEdicio, notificacioEntity, NUM_ENVIAMENTS);
	}

	@Test
	public void whenUpdateAmbProcedimentCIEAmbEntregaPostal_thenAllFieldsFilledCorrectly() throws IOException, RegistrePluginException, SistemaExternException, RegistreNotificaException {
		configureMockGestioDocumentalPlugin();

		// Given
		EntitatDto entitatCreate = database.entitat;
		NotificacioDatabaseDto notificacioDto = (NotificacioDatabaseDto) database.get("notificacioCIE");
		NotificacioDatabaseDto notificacioEdicio = (NotificacioDatabaseDto) SerializationUtils.clone(notificacioDto);
		notificacioEdicio.setNumExpedient("FFF000");
		notificacioEdicio.setConcepte("concepte edicio");
		notificacioEdicio.setRetard(2);

		NotEnviamentDatabaseDto enviament = notificacioEdicio.getEnviaments().get(0);
		EntregaPostalDto entregaPostal = getEntregaPostalDtoRandomData();
		enviament.setEntregaPostal(entregaPostal);
		enviament.setEntregaPostalActiva(true);

		// When
		NotificacioDatabaseDto notificacioEditada = notificacioService.update(entitatCreate.getId(),
				notificacioEdicio,
				true
		);

		// Then

		NotificacioEntity notificacioEntity = notificacioRepository.findById(notificacioEditada.getId()).orElseThrow();
		assertNotNull(notificacioEntity);

		// comprovar si les dades introduïdes són iguals a les que s'han posat a la base de dades
		assertEqualsNotificacions(notificacioEdicio, notificacioEntity, 1);

		NotificacioEnviamentEntity env = notificacioEntity.getEnviaments().iterator().next();
		assertNotNull(env.getEntregaPostal());

	}

	public void whenUpdateAmbProcedimentCIESenseEntregaPostal_thenProcessedCorrectly() throws IOException, RegistrePluginException, SistemaExternException, RegistreNotificaException {
		configureMockGestioDocumentalPlugin();

		// Given
		EntitatDto entitatCreate = database.entitat;
		NotificacioDatabaseDto notificacioDto = (NotificacioDatabaseDto) database.get("notificacioCIEAmbEntregaPostal");
		NotificacioDatabaseDto notificacioEdicio = (NotificacioDatabaseDto) SerializationUtils.clone(notificacioDto);
		notificacioEdicio.setNumExpedient("FFF000");
		notificacioEdicio.setConcepte("concepte edicio");
		notificacioEdicio.setRetard(2);

		NotEnviamentDatabaseDto enviament = notificacioEdicio.getEnviaments().get(0);
		enviament.setEntregaPostal(null);

		// When
		NotificacioDatabaseDto notificacioEditada = notificacioService.update(entitatCreate.getId(),
				notificacioEdicio,
				true
		);

		// Then

		NotificacioEntity notificacioEntity = notificacioRepository.findById(notificacioEditada.getId()).orElseThrow();
		assertNotNull(notificacioEntity);

		// comprovar si les dades introduïdes són iguals a les que s'han posat a la base de dades
		assertEqualsNotificacions(notificacioEdicio, notificacioEntity, NUM_ENVIAMENTS);

		NotificacioEnviamentEntity env = notificacioEntity.getEnviaments().iterator().next();
		assertNull(env.getEntregaPostal());
	}


	@Test
	public void whenCreateNotificacio_thenCreateTableItems() throws IOException, RegistrePluginException, SistemaExternException, RegistreNotificaException {
		configureMockRegistrePlugin();
		configureMockDadesUsuariPlugin();
		configureMockGestioDocumentalPlugin();

		authenticationTest.autenticarUsuari("admin");
		// Given: Notificacio no creada
		EntitatDto entitatCreate = database.entitat;
		ProcSerDto procedimentCreate = (ProcSerDto) database.get("procediment");
		assertNotNull(procedimentCreate);
		assertNotNull(procedimentCreate.getId());
		assertNotNull(entitatCreate);
		assertNotNull(entitatCreate.getId());

		NotificacioDatabaseDto notificacio = notificacioCreator.getRandomInstance();
		notificacio.setProcediment(procedimentCreate);
		assertEquals(2, notificacio.getEnviaments().size());

		// When: Registram la notificacio a la Base de dades
		NotificacioDatabaseDto notificacioCreated = notificacioService.create(
				entitatCreate.getId(),
				notificacio);

		// Then: S'ha creat un registre amb el mateix id a la taula de la vista de les notificacions.
		//		 S'han creat els dos enviaments, i s'han afegit els registres respectius a la taula de la vista de enviaments.
		try {
			// S'ha creat la fila de la notificació
			notificacioTableViewRepository.findById(notificacioCreated.getId()).orElseThrow();

			// S'han creat les files dels enviaments
			for (NotEnviamentDatabaseDto enviament : notificacioCreated.getEnviaments()) {
				enviamentTableRepository.findById(enviament.getId()).orElseThrow();
			}
		}finally {
			notificacioService.delete(entitatCreate.getId(), notificacioCreated.getId());
		}
	}

	@Test
	public void whenUpdateNotificacio_thenUpdateTableItems() throws SistemaExternException, RegistreNotificaException {
		configureMockGestioDocumentalPlugin();

		String nouValor = "String valor edicio";
		authenticationTest.autenticarUsuari("admin");
		// Given: Notificacio existent
		EntitatDto entitat = database.entitat;
		NotificacioDatabaseDto notificacio = (NotificacioDatabaseDto) database.get("notificacio");
		notificacioTableViewRepository.findById(notificacio.getId()).orElseThrow();
		notificacio.setConcepte(nouValor);
		notificacio.setNumExpedient(nouValor);
		// Actualitzam un enviament
		String nouValorEdicioEnviament = "Nom titular editat";
		NotEnviamentDatabaseDto enviamentEditat = notificacio.getEnviaments().get(0);
		enviamentEditat.getTitular().setNom(nouValorEdicioEnviament);

		// Afegim un nou enviament
		NotEnviamentDatabaseDto enviamentCreat = notificacioCreator.getRandomEnviament(5);
		notificacio.getEnviaments().add(enviamentCreat);

		// When: Actualizam la notificacio a la Base de dades
		NotificacioDatabaseDto notificacioUpdated = notificacioService.update(
				entitat.getId(),
				notificacio,
				true);

		// Then: S'ha actualitzat registre amb el mateix id a la taula de la vista de les notificacions
		NotificacioTableEntity tableRow = notificacioTableViewRepository.findById(notificacio.getId()).orElseThrow();
		assertNotNull(tableRow);
		assertEquals(nouValor, tableRow.getConcepte());
		assertEquals(nouValor, tableRow.getNumExpedient());

		// Also: S'ha creat el nou enviament i s'ha editat l'altre a la taula de les vistes
		assertEquals(notificacio.getEnviaments().size(),
				notificacioRepository.findById(notificacio.getId()).orElseThrow().getEnviaments().size());

		for (NotEnviamentDatabaseDto enviament : notificacioUpdated.getEnviaments()) {
			EnviamentTableEntity tableEnvRow = enviamentTableRepository.findById(enviament.getId()).orElseThrow();
			assertNotNull(tableEnvRow);
			if (tableEnvRow.getId().equals(enviamentEditat.getId())) {
				assertEquals(nouValorEdicioEnviament, tableEnvRow.getTitularNom());
			}
		}
	}

	@Test
	public void whenDeleteNotificacio_thenDeleteTableViewItem() throws IOException, RegistrePluginException, SistemaExternException {
		configureMockRegistrePlugin();
		configureMockDadesUsuariPlugin();

		authenticationTest.autenticarUsuari("admin");
		// Given: Notificacio existent
		EntitatDto entitat = database.entitat;
		NotificacioDatabaseDto notificacio = (NotificacioDatabaseDto) database.get("notificacio");
		notificacioTableViewRepository.findById(notificacio.getId()).orElseThrow();

		// When: eliminam la notificacio a la Base de dades
		notificacioService.delete(
				entitat.getId(),
				notificacio.getId());

		// Then: S'ha eliminat el registre amb el mateix id a la taula de la vista de les notificacions
		assertNull(
				notificacioTableViewRepository.findById(notificacio.getId()).orElse(null)
		);

		notificacioCreator.setAsDeleted("notificacio");

	}

	@Test
	public void whenUptateNotificacio_thenResetRegistreEnviamentIntent() throws SistemaExternException, RegistreNotificaException {
		configureMockGestioDocumentalPlugin();

		NotificacioDatabaseDto notificacioError = (NotificacioDatabaseDto) database.get("notificacio");

		// Given: notificacio pendent registrar amb nombre màxim de reintents
		NotificacioEntity notEntity = notificacioRepository.findById(notificacioError.getId()).orElseThrow();
		notEntity.setRegistreEnviamentIntent(pluginHelper.getRegistreReintentsMaxProperty());
		notEntity.updateEstat(NotificacioEstatEnumDto.PENDENT);
		notificacioRepository.saveAndFlush(notEntity);

		// When
		NotificacioDatabaseDto notificacioEditadaDto = notificacioService.update(database.entitat.getId(),
				notificacioError,
				true
		);

		// Then
		NotificacioEntity notEditada = notificacioRepository.findById(notificacioError.getId()).orElseThrow();
		assertEquals(0, notEditada.getRegistreEnviamentIntent());

	}

	@Test
	public void whenUptateNotificacio_thenResetNotificaEnviamentIntent() throws SistemaExternException, RegistreNotificaException {
		configureMockGestioDocumentalPlugin();

		NotificacioDatabaseDto notificacioError = (NotificacioDatabaseDto) database.get("notificacio");

		// Given: notificacio pendent registrar amb nombre màxim de reintents
		NotificacioEntity notEntity = notificacioRepository.findById(notificacioError.getId()).orElseThrow();
		notEntity.setNotificaEnviamentIntent(pluginHelper.getNotificaReintentsMaxProperty());
		notEntity.updateEstat(NotificacioEstatEnumDto.REGISTRADA);
		notificacioRepository.saveAndFlush(notEntity);

		// When
		NotificacioDatabaseDto notificacioEditadaDto = notificacioService.update(database.entitat.getId(),
				notificacioError,
				true
		);

		// Then
		NotificacioEntity notEditada = notificacioRepository.findById(notificacioError.getId()).orElseThrow();
		assertEquals(0, notEditada.getNotificaEnviamentIntent());

//		List pendents = notificacioService.getNotificacionsPendentsEnviar();

	}

	@Test(expected=Exception.class)
	public void givenNotificacioNoFinalitzada_whenMarcarComProcessada_thenRaiseException() throws Exception {

		NotificacioDatabaseDto notificacio = (NotificacioDatabaseDto) database.get("notificacio");
		// Given: notificacio amb estat distint a finalitzada
		NotificacioEntity notEntity = notificacioRepository.findById(notificacio.getId()).orElseThrow();
		notEntity.updateEstat(NotificacioEstatEnumDto.REGISTRADA);
		notificacioRepository.saveAndFlush(notEntity);

		//When
		notificacioService.marcarComProcessada(notEntity.getId(), "motiu", false);

	}

	@Test
	public void givenNotificacioPermisProcessarProcediment_whenMarcarComProcessada() throws Exception {
		NotificacioDatabaseDto notificacio = (NotificacioDatabaseDto) database.get("notificacioSensePermisos");
		NotificacioEntity notEntity = notificacioRepository.findById(notificacio.getId()).orElseThrow();
		notEntity.updateEstat(NotificacioEstatEnumDto.FINALITZADA);
		notificacioRepository.saveAndFlush(notEntity);

		// Given: notificacio amb permis processar procediment
		ProcSerDto procediment = notificacio.getProcediment();
		PermisDto permisProcessar = new PermisDto();
		permisProcessar.setProcessar(true);
		permisProcessar.setTipus(TipusEnumDto.USUARI);
		permisProcessar.setPrincipal("user");

		procedimentService.permisUpdate(database.entitat.getId(), null, procediment.getId(),
				permisProcessar);

		assertEquals(NotificacioEstatEnumDto.FINALITZADA, notificacioRepository.findById(notificacio.getId()).orElseThrow().getEstat());

		//When
		authenticationTest.autenticarUsuari("user");
		notificacioService.marcarComProcessada(notificacio.getId(), "motiu", false);
		assertEquals(NotificacioEstatEnumDto.PROCESSADA, notificacioRepository.findById(notificacio.getId()).orElseThrow().getEstat());
	}

	@Test
	public void givenNotificacioPermisProcessarOrgan_whenMarcarComProcessada() throws Exception {
		NotificacioDatabaseDto notificacio = (NotificacioDatabaseDto) database.get("notificacioSensePermisos");
		NotificacioEntity notEntity = notificacioRepository.findById(notificacio.getId()).orElseThrow();
		notEntity.updateEstat(NotificacioEstatEnumDto.FINALITZADA);
		notificacioRepository.saveAndFlush(notEntity);

		// Given: notificacio amb permis processar òrgan
		Long organGestorId = organGestorRepository.findByCodi(notificacio.getOrganGestorCodi()).getId();
		PermisDto permis = new PermisDto();
		permis.setProcessar(true);
		permis.setTipus(TipusEnumDto.USUARI);
		permis.setPrincipal("user");
		organGestorService.permisUpdate(
				database.entitat.getId(),
				organGestorId,
				false,
				permis);

		assertEquals(NotificacioEstatEnumDto.FINALITZADA, notificacioRepository.findById(notificacio.getId()).orElseThrow().getEstat());

		//When
		authenticationTest.autenticarUsuari("user");
		notificacioService.marcarComProcessada(notificacio.getId(), "motiu", false);
		assertEquals(NotificacioEstatEnumDto.PROCESSADA, notificacioRepository.findById(notificacio.getId()).orElseThrow().getEstat());

	}

	@Test
	public void givenNotificacioPermisProcessarProcedimentOrgan_whenMarcarComProcessada() throws Exception {
		NotificacioDatabaseDto notificacio = (NotificacioDatabaseDto) database.get("notificacioSensePermisos");
		NotificacioEntity notEntity = notificacioRepository.findById(notificacio.getId()).orElseThrow();
		notEntity.updateEstat(NotificacioEstatEnumDto.FINALITZADA);
		notificacioRepository.saveAndFlush(notEntity);

		// Given: notificacio amb permis processar procediment per a l'òrgan gestor
		Long organGestorId = organGestorRepository.findByCodi(notificacio.getOrganGestorCodi()).getId();
		ProcSerDto procediment = notificacio.getProcediment();
		PermisDto permisProcessar = new PermisDto();
		permisProcessar.setProcessar(true);
		permisProcessar.setTipus(TipusEnumDto.USUARI);
		permisProcessar.setPrincipal("user");

		procedimentService.permisUpdate(database.entitat.getId(), organGestorId, procediment.getId(),
				permisProcessar);

		assertEquals(NotificacioEstatEnumDto.FINALITZADA, notificacioRepository.findById(notificacio.getId()).orElseThrow().getEstat());

		//When
		authenticationTest.autenticarUsuari("user");
		notificacioService.marcarComProcessada(notificacio.getId(), "motiu", false);
		assertEquals(NotificacioEstatEnumDto.PROCESSADA, notificacioRepository.findById(notificacio.getId()).orElseThrow().getEstat());

	}

	@Test(expected=Exception.class)
	public void givenNotificacioSenseCapPermis_whenMarcarComProcessada() throws Exception {
		NotificacioDatabaseDto notificacio = (NotificacioDatabaseDto) database.get("notificacioSensePermisos");
		NotificacioEntity notEntity = notificacioRepository.findById(notificacio.getId()).orElseThrow();
		notEntity.updateEstat(NotificacioEstatEnumDto.FINALITZADA);
		notificacioRepository.saveAndFlush(notEntity);

		assertEquals(NotificacioEstatEnumDto.FINALITZADA, notificacioRepository.findById(notificacio.getId()).orElseThrow().getEstat());

		//When
		authenticationTest.autenticarUsuari("user");
		notificacioService.marcarComProcessada(notificacio.getId(), "motiu", false);
	}

	//	@Test
	public void notificacioRegistrar() throws SistemaExternException, IOException, RegistrePluginException, RegistreNotificaException {
		EntitatDto entitatCreate = database.entitat;
		ProcSerDto procedimentCreate = (ProcSerDto) database.get("procediment");
		NotificacioDatabaseDto notificacioCreate = (NotificacioDatabaseDto) database.get("notificacio");

		configureMockUnitatsOrganitzativesPlugin();
		configureMockRegistrePlugin();
		Mockito.mock(SchedulledServiceImpl.class);
		notificacioService.enviarNotificacioARegistre(notificacioCreate.getId());

//						NotificacioDatabaseDto notificacioEdicio = (NotificacioDatabaseDto) SerializationUtils.clone(notificacioCreate);
//						notificacioEdicio.setOrganGestorCodi(ConfigTest.ENTITAT_DGTIC_DIR3CODI);
//						notificacioEdicio.setConcepte("concepte edicio");
//						notificacioEdicio.setRetard(2);
//
//						NotificacioDatabaseDto notificacioEditada = notificacioService.update(entitatCreate.getId(),
//								notificacioEdicio,
//								true
//						);
	}

//	@Test
	public void notificacioEnviar() {
	}

	private void assertEqualsNotificacions(NotificacioDatabaseDto notificacioDto, NotificacioEntity notificacioEntity, int numEnviaments) {
		// comprovar si les dades introduïdes són iguals a les que s'han posat a la base de dades
		assertEquals(notificacioDto.getProcediment().getId(), notificacioEntity.getProcediment().getId());
		assertEquals(notificacioDto.getOrganGestorCodi(), notificacioEntity.getOrganGestor().getCodi());
//			assertEquals(notificacio.getGrup().getCodi(), notificacioEntity.getGrupCodi());
		assertNull(notificacioDto.getGrup());
		assertEquals(notificacioDto.getUsuariCodi(), notificacioEntity.getUsuariCodi());
		assertEquals(notificacioDto.getEmisorDir3Codi(), notificacioEntity.getEmisorDir3Codi());
		assertEquals(notificacioDto.getEnviamentTipus(), notificacioEntity.getEnviamentTipus());
		assertEquals(notificacioDto.getConcepte(), notificacioEntity.getConcepte());
		assertEquals(notificacioDto.getDescripcio(), notificacioEntity.getDescripcio());
		assertEquals(notificacioDto.getEnviamentDataProgramada(), notificacioEntity.getEnviamentDataProgramada());
		assertEquals(notificacioDto.getRetard(), notificacioEntity.getRetard());
		assertEquals(notificacioDto.getCaducitat(), notificacioEntity.getCaducitat());
		assertEquals(notificacioDto.getNumExpedient(), notificacioEntity.getNumExpedient());
		assertEquals(notificacioDto.getIdioma(), notificacioEntity.getIdioma());

		assertEquals(notificacioDto.getDocument().getArxiuNom(), notificacioEntity.getDocument().getArxiuNom());
		assertEquals(notificacioDto.getDocument().getMediaType(), notificacioEntity.getDocument().getMediaType());
		assertEquals(notificacioDto.getDocument().getMida(), notificacioEntity.getDocument().getMida());
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
			NotEnviamentDatabaseDto enviament = notificacioDto.getEnviaments().get(i);
			NotEnviamentDatabaseDto enviamentCreat = notificacioDto.getEnviaments().get(i);
			assertEquals(enviament.getServeiTipus(), enviamentCreat.getServeiTipus());
			assertEquals(enviament.getTitular().getNom(), enviamentCreat.getTitular().getNom());
			assertEquals(enviament.getTitular().getLlinatge1(), enviamentCreat.getTitular().getLlinatge1());
			assertEquals(enviament.getTitular().getLlinatge2(), enviamentCreat.getTitular().getLlinatge2());
			assertEquals(enviament.getTitular().getNom(), enviamentCreat.getTitular().getNom());

			if (enviament.getDestinataris() != null && enviament.getDestinataris().size() != 0) {
				for (int j = 0; j < enviament.getDestinataris().size(); j ++ ) {
					PersonaDto destinatari = enviament.getDestinataris().get(j);
					PersonaDto destinatariCreat = enviamentCreat.getDestinataris().get(j);

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

}


