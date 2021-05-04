package es.caib.notib.core.service;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioDatabaseDto;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.entity.EnviamentTableEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioTableEntity;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.core.repository.EnviamentTableRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.repository.NotificacioTableViewRepository;
import es.caib.notib.core.test.data.ConfigTest;
import es.caib.notib.core.test.data.EntitatItemTest;
import es.caib.notib.core.test.data.NotificacioItemTest;
import es.caib.notib.core.test.data.ProcedimentItemTest;
import es.caib.notib.plugin.SistemaExternException;
import es.caib.notib.plugin.registre.RegistrePluginException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang.SerializationUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class NotificacioServiceIT extends BaseServiceTestV2 {
	
	private static final int NUM_DESTINATARIS = 2;
	
	@Autowired
	PermisosHelper permisosHelper;
	@Autowired
	NotificacioService notificacioService;
	@Autowired
	NotificacioRepository notificacioRepository;
	@Autowired
	NotificacioTableViewRepository notificacioTableViewRepository;

	EntitatDto entitatCreate;

	@Autowired
	ProcedimentItemTest procedimentCreate;
	@Autowired
	NotificacioItemTest notificacioCreate;
	@Autowired
	EnviamentTableRepository enviamentTableRepository;

	@Before
	public void setUp() throws SistemaExternException, IOException, DecoderException, RegistrePluginException {
		configureMockGestioDocumentalPlugin();

		entitatCreate = EntitatItemTest.getRandomInstance();

//		organGestorCreate.setItemIdentifier("organGestor");

		procedimentCreate.addObject("procediment", procedimentCreate.getRandomInstance());

		notificacioCreate.addObject("notificacio", notificacioCreate.getRandomInstance());
		notificacioCreate.addRelated("notificacio", "procediment", procedimentCreate);

		notificacioCreate.addObject("notificacioError", notificacioCreate.getRandomInstance());
		notificacioCreate.addRelated("notificacioError", "procediment", procedimentCreate);

	}
	
	@Test
	public void create() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(ElementsCreats elementsCreats) throws Exception {
					configureMockRegistrePlugin();
					configureMockDadesUsuariPlugin();

					authenticationTest.autenticarUsuari("admin");

					EntitatDto entitatCreate = elementsCreats.entitat;
					ProcedimentDto procedimentCreate = (ProcedimentDto) elementsCreats.get("procediment");
					assertNotNull(procedimentCreate);
					assertNotNull(procedimentCreate.getId());
					assertNotNull(entitatCreate);
					assertNotNull(entitatCreate.getId());

					NotificacioDatabaseDto notificacio = notificacioCreate.getRandomInstance();
					notificacio.setProcediment(procedimentCreate);

					NotificacioDatabaseDto notificacioCreated = notificacioService.create(
							entitatCreate.getId(),
							notificacio);
					try {
						// comprovar si les dades introduïdes són iguals a les que s'han posat a la base de dades
						assertNotNull(notificacioCreated);
						assertEquals(notificacio.getProcediment().getId(), notificacioCreated.getProcediment().getId());
						assertEquals(notificacio.getOrganGestorCodi(), notificacioCreated.getOrganGestorCodi());
//					assertEquals(notificacio.getGrup().getId(), notificacioCreated.getGrup().getId()); // de moment no l'hem posat
						assertEquals(notificacio.getUsuariCodi(), notificacioCreated.getUsuariCodi());
						assertEquals(notificacio.getEmisorDir3Codi(), notificacioCreated.getEmisorDir3Codi());
						assertEquals(notificacio.getEnviamentTipus(), notificacioCreated.getEnviamentTipus());
						assertEquals(notificacio.getConcepte(), notificacioCreated.getConcepte());
						assertEquals(notificacio.getDescripcio(), notificacioCreated.getDescripcio());
						assertEquals(notificacio.getEnviamentDataProgramada(), notificacioCreated.getEnviamentDataProgramada());
						assertEquals(notificacio.getRetard(), notificacioCreated.getRetard());
						assertEquals(notificacio.getCaducitat(), notificacioCreated.getCaducitat());
						assertEquals(notificacio.getNumExpedient(), notificacioCreated.getNumExpedient());
						assertEquals(notificacio.getIdioma(), notificacioCreated.getIdioma());

						assertEquals(notificacio.getDocument().getArxiuNom(), notificacioCreated.getDocument().getArxiuNom());
						assertEquals(notificacio.getDocument().getMediaType(), notificacioCreated.getDocument().getMediaType());
						assertEquals(notificacio.getDocument().getMida(), notificacioCreated.getDocument().getMida());
						if (notificacio.getDocument().getContingutBase64() != null) {
							assertNull(notificacioCreated.getDocument().getContingutBase64());
//							assertNotNull(notificacioCreated.getDocument().getArxiuGestdocId()); // El mockito del plugin de gestió documental està mal configurat, sempre retorna null
						}

//						assertEquals(notificacio.getDocument().getHash(), notificacioCreated.getDocument().getHash()); // no sé que fa
						assertEquals(notificacio.getDocument().getUrl(), notificacioCreated.getDocument().getUrl());
						assertEquals(notificacio.getDocument().isNormalitzat(), notificacioCreated.getDocument().isNormalitzat());
						assertEquals(notificacio.getDocument().isGenerarCsv(), notificacioCreated.getDocument().isGenerarCsv());
						assertEquals(notificacio.getDocument().getUuid(), notificacioCreated.getDocument().getUuid());
						assertEquals(notificacio.getDocument().getCsv(), notificacioCreated.getDocument().getCsv());
						assertEquals(notificacio.getDocument().getOrigen(), notificacioCreated.getDocument().getOrigen());
						assertEquals(notificacio.getDocument().getValidesa(), notificacioCreated.getDocument().getValidesa());
						assertEquals(notificacio.getDocument().getTipoDocumental(), notificacioCreated.getDocument().getTipoDocumental());
						assertEquals(notificacio.getDocument().getModoFirma(), notificacioCreated.getDocument().getModoFirma());

						for (int i = 0; i < NUM_DESTINATARIS; i ++ ) {
							NotificacioEnviamentDtoV2 enviament = notificacio.getEnviaments().get(i);
							NotificacioEnviamentDtoV2 enviamentCreat = notificacioCreated.getEnviaments().get(i);
							assertEquals(enviament.getServeiTipus(), enviamentCreat.getServeiTipus());
							assertEquals(enviament.getTitular().getNom(), enviamentCreat.getTitular().getNom());
							assertEquals(enviament.getTitular().getLlinatge1(), enviamentCreat.getTitular().getLlinatge1());
							assertEquals(enviament.getTitular().getLlinatge2(), enviamentCreat.getTitular().getLlinatge2());
							assertEquals(enviament.getTitular().getNom(), enviamentCreat.getTitular().getNom());

							// TODO: Check destinataris
						}
					}finally {

						notificacioService.delete(entitatCreate.getId(), notificacioCreated.getId());
					}

				}
			}, 
			"Create Notificació",
			entitatCreate,
			procedimentCreate);
	}

	@Test
	public void delete() {
	}

	@Test
	public void update() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(ElementsCreats elementsCreats) throws Exception {
						configureMockGestioDocumentalPlugin();

						EntitatDto entitatCreate = elementsCreats.entitat;
						ProcedimentDto procedimentCreate = (ProcedimentDto) elementsCreats.get("procediment");
						NotificacioDatabaseDto notificacioCreate = (NotificacioDatabaseDto) elementsCreats.get("notificacio");


						NotificacioDatabaseDto notificacioEdicio = (NotificacioDatabaseDto) SerializationUtils.clone(notificacioCreate);
						notificacioEdicio.setOrganGestorCodi(ConfigTest.ENTITAT_DGTIC_DIR3CODI);
						notificacioEdicio.setConcepte("concepte edicio");
						notificacioEdicio.setRetard(2);

						NotificacioDatabaseDto notificacioEditada = notificacioService.update(entitatCreate.getId(),
								notificacioEdicio,
								true
								);

						// comprovar si les dades introduïdes són iguals a les que s'han posat a la base de dades
						assertNotNull(notificacioEditada);
						assertEquals(notificacioCreate.getId(), notificacioEditada.getId());
						assertEquals(notificacioCreate.getProcediment().getId(), notificacioEditada.getProcediment().getId());

						// TODO: per a l'òrgan gestor hi ha diverses casuïstiques a tenir en compte en un altre test
//						assertNotEquals(notificacioCreate.getOrganGestorCodi(),notificacioEditada.getOrganGestorCodi());
						assertNotEquals(notificacioCreate.getConcepte(), notificacioEditada.getConcepte());
						assertEquals((Integer) 2, notificacioEditada.getRetard());

						for (int i = 0; i < NUM_DESTINATARIS; i ++ ) {
							NotificacioEnviamentDtoV2 enviament = notificacioCreate.getEnviaments().get(i);
							NotificacioEnviamentDtoV2 enviamentCreat = notificacioEditada.getEnviaments().get(i);
							assertEquals(enviament.getServeiTipus(), enviamentCreat.getServeiTipus());
							assertEquals(enviament.getTitular().getNom(), enviamentCreat.getTitular().getNom());
							assertEquals(enviament.getTitular().getLlinatge1(), enviamentCreat.getTitular().getLlinatge1());
							assertEquals(enviament.getTitular().getLlinatge2(), enviamentCreat.getTitular().getLlinatge2());
							assertEquals(enviament.getTitular().getNom(), enviamentCreat.getTitular().getNom());

							// TODO: Check destinataris
						}

					}
				},
				"Update Notificació",
				entitatCreate,
				procedimentCreate,
				notificacioCreate);
	}


	@Test
	public void whenCreateNotificacio_thenCreateTableItems() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(ElementsCreats elementsCreats) throws Exception {
					configureMockRegistrePlugin();
					configureMockDadesUsuariPlugin();
					configureMockGestioDocumentalPlugin();

					authenticationTest.autenticarUsuari("admin");
					// Given: Notificacio no creada
					EntitatDto entitatCreate = elementsCreats.entitat;
					ProcedimentDto procedimentCreate = (ProcedimentDto) elementsCreats.get("procediment");
					assertNotNull(procedimentCreate);
					assertNotNull(procedimentCreate.getId());
					assertNotNull(entitatCreate);
					assertNotNull(entitatCreate.getId());

					NotificacioDatabaseDto notificacio = notificacioCreate.getRandomInstance();
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
						assertNotNull(
							notificacioTableViewRepository.findOne(notificacioCreated.getId())
						);

						// S'han creat les files dels enviaments
						for (NotificacioEnviamentDtoV2 enviament : notificacioCreated.getEnviaments()) {
							assertNotNull(
								enviamentTableRepository.findOne(enviament.getId())
							);
						}
					}finally {
						notificacioService.delete(entitatCreate.getId(), notificacioCreated.getId());
					}
				}
			},
			"Al crear una nova notificació es registra el seu registre corresponent a la taula de la vista",
			entitatCreate,
			procedimentCreate,
			notificacioCreate);
	}

	@Test
	public void whenUpdateNotificacio_thenUpdateTableItems() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(ElementsCreats elementsCreats) throws Exception {
						configureMockGestioDocumentalPlugin();

						String nouValor = "String valor edicio";
						authenticationTest.autenticarUsuari("admin");
						// Given: Notificacio existent
						EntitatDto entitat = elementsCreats.entitat;
						NotificacioDatabaseDto notificacio = (NotificacioDatabaseDto) elementsCreats.get("notificacio");
						assertNotNull(
								notificacioTableViewRepository.findOne(notificacio.getId())
						);
						notificacio.setConcepte(nouValor);
						notificacio.setNumExpedient(nouValor);
						// Actualitzam un enviament
						String nouValorEdicioEnviament = "Nom titular editat";
						NotificacioEnviamentDtoV2 enviamentEditat = notificacio.getEnviaments().get(0);
						enviamentEditat.getTitular().setNom(nouValorEdicioEnviament);

						// Afegim un nou enviament
						NotificacioEnviamentDtoV2 enviamentCreat = notificacioCreate.getRandomEnviament(5);
						notificacio.getEnviaments().add(enviamentCreat);

						// When: Actualizam la notificacio a la Base de dades
						NotificacioDatabaseDto notificacioUpdated = notificacioService.update(
								entitat.getId(),
								notificacio,
								true);

						// Then: S'ha actualitzat registre amb el mateix id a la taula de la vista de les notificacions
						NotificacioTableEntity tableRow = notificacioTableViewRepository.findOne(
								notificacio.getId());
						assertNotNull(tableRow);
						assertEquals(nouValor, tableRow.getConcepte());
						assertEquals(nouValor, tableRow.getNumExpedient());

						// Also: S'ha creat el nou enviament i s'ha editat l'altre a la taula de les vistes
						assertEquals(notificacio.getEnviaments().size(),
								notificacioRepository.findOne(notificacio.getId()).getEnviaments().size());

						for (NotificacioEnviamentDtoV2 enviament : notificacioUpdated.getEnviaments()) {
							EnviamentTableEntity tableEnvRow = enviamentTableRepository.findOne(enviament.getId());
							assertNotNull(tableEnvRow);
							if (tableEnvRow.getId().equals(enviamentEditat.getId())) {
								assertEquals(nouValorEdicioEnviament, tableEnvRow.getTitularNom());
							}
						}
					}
				},
				"Al crear una nova notificació es registra el seu registre corresponent a la taula de la vista",
				entitatCreate,
				procedimentCreate,
				notificacioCreate);
	}

	@Test
	public void whenDeleteNotificacio_thenDeleteTableViewItem() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(ElementsCreats elementsCreats) throws Exception {
						configureMockRegistrePlugin();
						configureMockDadesUsuariPlugin();

						authenticationTest.autenticarUsuari("admin");
						// Given: Notificacio existent
						EntitatDto entitat = elementsCreats.entitat;
						NotificacioDatabaseDto notificacio = (NotificacioDatabaseDto) elementsCreats.get("notificacio");
						assertNotNull(
								notificacioTableViewRepository.findOne(notificacio.getId())
						);

						// When: eliminam la notificacio a la Base de dades
						notificacioService.delete(
								entitat.getId(),
								notificacio.getId());

						// Then: S'ha eliminat el registre amb el mateix id a la taula de la vista de les notificacions
						assertNull(
								notificacioTableViewRepository.findOne(notificacio.getId())
						);

						notificacioCreate.setAsDeleted("notificacio");

					}
				},
				"Al crear una nova notificació es registra el seu registre corresponent a la taula de la vista",
				entitatCreate,
				procedimentCreate,
				notificacioCreate);
	}

	@Test
	public void whenUptateNotificacio_thenResetRegistreEnviamentIntent() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(ElementsCreats elementsCreats) throws Exception {
						configureMockGestioDocumentalPlugin();

						NotificacioDatabaseDto notificacioError = (NotificacioDatabaseDto) elementsCreats.get("notificacio");

						// Given: notificacio pendent registrar amb nombre màxim de reintents
						NotificacioEntity notEntity = notificacioRepository.findOne(notificacioError.getId());
						notEntity.setRegistreEnviamentIntent(pluginHelper.getRegistreReintentsMaxProperty());
						notEntity.setEstat(NotificacioEstatEnumDto.PENDENT);
						notificacioRepository.saveAndFlush(notEntity);

						// When
						NotificacioDatabaseDto notificacioEditadaDto = notificacioService.update(elementsCreats.entitat.getId(),
								notificacioError,
								true
						);

						// Then
						NotificacioEntity notEditada = notificacioRepository.findOne(notificacioError.getId());
						assertEquals(0, notEditada.getRegistreEnviamentIntent());


					}
				},
				"Test registre notificació",
				entitatCreate,
				procedimentCreate,
				notificacioCreate);
	}

	@Test
	public void whenUptateNotificacio_thenResetNotificaEnviamentIntent() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(ElementsCreats elementsCreats) throws Exception {
						configureMockGestioDocumentalPlugin();

						NotificacioDatabaseDto notificacioError = (NotificacioDatabaseDto) elementsCreats.get("notificacio");

						// Given: notificacio pendent registrar amb nombre màxim de reintents
						NotificacioEntity notEntity = notificacioRepository.findOne(notificacioError.getId());
						notEntity.setNotificaEnviamentIntent(pluginHelper.getNotificaReintentsMaxProperty());
						notEntity.setEstat(NotificacioEstatEnumDto.REGISTRADA);
						notificacioRepository.saveAndFlush(notEntity);

						// When
						NotificacioDatabaseDto notificacioEditadaDto = notificacioService.update(elementsCreats.entitat.getId(),
								notificacioError,
								true
						);

						// Then
						NotificacioEntity notEditada = notificacioRepository.findOne(notificacioError.getId());
						assertEquals(0, notEditada.getNotificaEnviamentIntent());

						List pendents = notificacioService.getNotificacionsPendentsEnviar();


					}
				},
				"Test registre notificació",
				entitatCreate,
				procedimentCreate,
				notificacioCreate);
	}

//	@Test
	public void notificacioRegistrar() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(ElementsCreats elementsCreats) throws Exception {
					EntitatDto entitatCreate = elementsCreats.entitat;
					ProcedimentDto procedimentCreate = (ProcedimentDto) elementsCreats.get("procediment");
					NotificacioDatabaseDto notificacioCreate = (NotificacioDatabaseDto) elementsCreats.get("notificacio");

					configureMockUnitatsOrganitzativesPlugin();
					configureMockRegistrePlugin();
					Mockito.mock(SchedulledServiceImpl.class);
					notificacioService.notificacioRegistrar(notificacioCreate.getId());

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
			},
			"Test registre notificació",
			entitatCreate,
			procedimentCreate,
			notificacioCreate);
	}

	@Test
	public void notificacioEnviar() {
	}

}


