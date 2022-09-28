package es.caib.notib.logic.service;

import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.cie.CieDataDto;
import es.caib.notib.logic.intf.dto.cie.CieDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.service.PagadorCieService;
import es.caib.notib.logic.test.data.CieItemTest;
import es.caib.notib.logic.test.data.EntitatItemTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/logic/application-context-test.xml"})
@Transactional
public class PagadorCieServiceTest extends BaseServiceTestV2 {
	@Autowired
	private PagadorCieService pagadorCieService;


	private CieDataDto createPagadorCie;
	private CieDataDto updatePagadorCie;


	private ElementsCreats database;
	@Autowired
	private CieItemTest cieCreator;
	
	@Before
	public void setUp() throws Exception {
		addConfig("es.caib.notib.metriques.generar", "false");

		createPagadorCie = CieItemTest.getRandomInstance();
		cieCreator.addObject("cie", createPagadorCie);
		database = createDatabase(EntitatItemTest.getRandomInstance(),
			cieCreator
		);

		updatePagadorCie = CieItemTest.getRandomInstance();
	}


	@After
	public final void tearDown() {
		destroyDatabase(database.getEntitat().getId(),
			cieCreator
		);
		log.info("-------------------------------------------------------------------");
		log.info("-- ...test \"" + currentTestDescription + "\" executat.");
		log.info("-------------------------------------------------------------------");
	}

	@Test
	public void create() {
		currentTestDescription = "Create PAGADOR CIE";
		EntitatDto entitatCreada = database.getEntitat();
		CieDto pagadorCreateCie = (CieDto) database.get("cie");
		authenticationTest.autenticarUsuari("admin");
		assertNotNull(pagadorCreateCie);
		assertNotNull(pagadorCreateCie.getId());

		compararPagadorCie(
				createPagadorCie,
				pagadorCreateCie);
		assertEquals(entitatCreada.getId(), pagadorCreateCie.getEntitatId());
	}
	
	
	@Test
	public void update() {
		currentTestDescription = "Update PAGADOR CIE";
		EntitatDto entitatCreada = database.getEntitat();
		CieDto cieCreat = (CieDto) database.get("cie");
		authenticationTest.autenticarUsuari("admin");

		updatePagadorCie.setId(cieCreat.getId());
		CieDto pagadorCieModificat = pagadorCieService.update(updatePagadorCie);

		assertNotNull(pagadorCieModificat);
		assertNotNull(pagadorCieModificat.getId());

		assertEquals(
				cieCreat.getId(),
				pagadorCieModificat.getId());
		compararPagadorCie(
				updatePagadorCie,
				pagadorCieModificat);
		assertEquals(entitatCreada.getId(), pagadorCieModificat.getEntitatId());
	}
	
	
	@Test
	public void delete() {
		currentTestDescription = "Delete PAGADOR CIE";
		EntitatDto entitatCreada = database.getEntitat();
		CieDto cieCreat = (CieDto) database.get("cie");
		authenticationTest.autenticarUsuari("admin");

		CieDto esborrada = pagadorCieService.delete(cieCreat.getId());
		compararPagadorCie(
				createPagadorCie,
				esborrada);
		try{
			pagadorCieService.findById(cieCreat.getId());
			fail("El Pagador esborrat no s'hauria d'haver trobat");
		}catch(NotFoundException expected) {
		}
	}
	
	@Test
	public void findById() {
		currentTestDescription = "FindById PAGADOR CIE";
		EntitatDto entitatCreada = database.getEntitat();
		CieDto cieCreat = (CieDto) database.get("cie");
		authenticationTest.autenticarUsuari("admin");

		CieDto trobat= pagadorCieService.findById(
				cieCreat.getId());

		assertNotNull(trobat);
		assertNotNull(trobat.getId());
		compararPagadorCie(
				createPagadorCie,
				trobat);
	}
	
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplCreate() {
		authenticationTest.autenticarUsuari("apl");
		pagadorCieService.create(1L, createPagadorCie);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplUpdate() {
		authenticationTest.autenticarUsuari("apl");
		pagadorCieService.update(createPagadorCie);
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplDelete() {
		authenticationTest.autenticarUsuari("apl");
		pagadorCieService.delete(1L);
	}
	
	private void compararPagadorCie(
			CieDataDto original,
			CieDataDto perComprovar) {
		assertEquals(
				original.getOrganismePagadorCodi(),
				perComprovar.getOrganismePagadorCodi());
	}

}



	
	
	

