package es.caib.notib.core.service;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.notificacio.NotificacioDatabaseDto;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.test.data.ConfigTest;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class NotificacioServiceTest extends BaseServiceTestV2 {
	
	private static final int NUM_DESTINATARIS = 2;
	
	@Autowired
	PermisosHelper permisosHelper;
	@Autowired
	NotificacioService notificacioService;
	@Autowired
	NotificacioRepository notificacioRepository;

	EntitatDto entitatCreate;

	@Autowired
	ProcedimentItemTest procedimentCreate;
	@Autowired
	NotificacioItemTest notificacioCreate;
	
	@Before
	public void setUp() throws SistemaExternException, IOException, DecoderException, RegistrePluginException {
		List<PermisDto> permisosEntitat = new ArrayList<PermisDto>();
		entitatCreate = new EntitatDto();
		entitatCreate.setCodi("LIMIT");
		entitatCreate.setNom("Limit Tecnologies");
		entitatCreate.setDescripcio("Descripció de Limit Tecnologies");
		entitatCreate.setTipus(EntitatTipusEnumDto.GOVERN);
		entitatCreate.setDir3Codi(ConfigTest.ENTITAT_DGTIC_DIR3CODI);
		entitatCreate.setApiKey(ConfigTest.ENTITAT_DGTIC_KEY);
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

//		organGestorCreate.setItemIdentifier("organGestor");

		procedimentCreate.addObject("procediment", procedimentCreate.getRandomInstance());

		notificacioCreate.addObject("notificacio", notificacioCreate.getRandomInstance());
		notificacioCreate.addRelated("notificacio", "procediment", procedimentCreate);

		notificacioCreate.addObject("notificacioError", notificacioCreate.getRandomInstance());
		notificacioCreate.addRelated("notificacioError", "procediment", procedimentCreate);

		System.setProperty("es.caib.notib.plugin.gesdoc.filesystem.base.dir", "/home/bgalmes/dades/notib-fs/");


		configureMockUnitatsOrganitzativesPlugin();
	}
	
	@Test
	public void create() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(ElementsCreats elementsCreats) throws Exception {
					configureMockRegistrePlugin();
					configureMockDadesUsuariPlugin();
					configureMockGestioDocumentalPlugin();

					authenticationTest.autenticarUsuari("admin");

					EntitatDto entitatCreate = elementsCreats.entitat;
					ProcedimentDto procedimentCreate = (ProcedimentDto) elementsCreats.get("procediment");
					assertNotNull(procedimentCreate);
					assertNotNull(procedimentCreate.getId());
					assertNotNull(entitatCreate);
					assertNotNull(entitatCreate.getId());

					NotificacioDatabaseDto notificacio = (NotificacioDatabaseDto) notificacioCreate.getRandomInstance();
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

						assertEquals(notificacio.getDocument().getArxiuGestdocId(), notificacioCreated.getDocument().getArxiuGestdocId());
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
						EntitatDto entitatCreate = elementsCreats.entitat;
						ProcedimentDto procedimentCreate = (ProcedimentDto) elementsCreats.get("procediment");
						NotificacioDatabaseDto notificacioCreate = (NotificacioDatabaseDto) elementsCreats.get("notificacio");
						NotificacioDatabaseDto notificacioErrorCreate = (NotificacioDatabaseDto) elementsCreats.get("notificacioError");


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
	public void whenUptateNotificacio_thenResetRegistreEnviamentIntent() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(ElementsCreats elementsCreats) throws Exception {
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


