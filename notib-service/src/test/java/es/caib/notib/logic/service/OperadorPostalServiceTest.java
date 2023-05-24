package es.caib.notib.logic.service;

import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.service.OperadorPostalService;
import es.caib.notib.logic.test.data.ConfigTest;
import es.caib.notib.logic.test.data.EntitatItemTest;
import es.caib.notib.logic.test.data.OperadorPostalItemTest;
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
public class OperadorPostalServiceTest extends BaseServiceTestV2 {

	private OperadorPostalDto crearPagadorPostal;
	private OperadorPostalDto updatePagadorPostal;


	@Autowired
	protected OperadorPostalService operadorPostalService;


	private ElementsCreats database;
	@Autowired
	private OperadorPostalItemTest operadorPostalCreator;

	@Before
	public void setUp() throws Exception {
		addConfig("es.caib.notib.metriques.generar", "false");

		crearPagadorPostal = new OperadorPostalDto();
		crearPagadorPostal.setOrganismePagadorCodi(ConfigTest.DEFAULT_ORGAN_DIR3);
		crearPagadorPostal.setContracteNum("00000001");
		operadorPostalCreator.addObject("operador1", crearPagadorPostal);

		updatePagadorPostal=new OperadorPostalDto();
		updatePagadorPostal.setOrganismePagadorCodi(ConfigTest.DEFAULT_ORGAN_DIR3);
		updatePagadorPostal.setContracteNum("00000002");
		operadorPostalCreator.addObject("operador2", updatePagadorPostal);

		database = createDatabase(EntitatItemTest.getRandomInstance(),
			operadorPostalCreator
		);
	}

	@After
	public final void tearDown() {
		destroyDatabase(database.getEntitat().getId(),
			operadorPostalCreator
		);
		log.info("-------------------------------------------------------------------");
		log.info("-- ...test \"" + currentTestDescription + "\" executat.");
		log.info("-------------------------------------------------------------------");
	}

	@Test
	public void create() throws NotFoundException, Exception {

		currentTestDescription = "Create PAGADOR POSTAL";
		EntitatDto entitatCreada = database.getEntitat();
		OperadorPostalDto operadorPostalCreat = (OperadorPostalDto) database.get("operador1");
		authenticationTest.autenticarUsuari("admin");
		assertNotNull(operadorPostalCreat);
		assertNotNull(operadorPostalCreat.getId());
		comprobarPagadorPostal(crearPagadorPostal, operadorPostalCreat);
		assertEquals(entitatCreada.getId(), operadorPostalCreat.getEntitatId());
	}
	
	@Test
	public void update() throws NotFoundException, Exception {
		currentTestDescription = "Update PAGADOR POSTAL";
		EntitatDto entitatCreada = database.getEntitat();
		OperadorPostalDto operadorPostalCreat = (OperadorPostalDto) database.get("operador1");
		authenticationTest.autenticarUsuari("admin");

		updatePagadorPostal.setId(operadorPostalCreat.getId());
		OperadorPostalDto pagadorModificat = operadorPostalService.upsert(entitatCreada.getId(), updatePagadorPostal);
		assertNotNull(pagadorModificat);
		assertNotNull(pagadorModificat.getId());
		assertEquals(operadorPostalCreat.getId(), pagadorModificat.getId());
		comprobarPagadorPostal(
				updatePagadorPostal,
				pagadorModificat);
		assertEquals(entitatCreada.getId(), pagadorModificat.getEntitatId());
	}

	@Test
	public void delete() {
		currentTestDescription = "Delete PAGADOR POSTAL";
		OperadorPostalDto operadorPostalCreat = (OperadorPostalDto) database.get("operador1");
		authenticationTest.autenticarUsuari("admin");
		OperadorPostalDto esborrada = operadorPostalService.delete(operadorPostalCreat.getId());
		comprobarPagadorPostal(
				crearPagadorPostal,
				esborrada);
		try {
			operadorPostalService.findById(operadorPostalCreat.getId());
			fail("El Pagador postal esborrat no s'hauria d'haver trobat");
		}catch(NotFoundException expected) {
		}
	}
				
	@Test
	public void findById() {
		currentTestDescription = "FindById PAGADOR POSTAL";
		OperadorPostalDto operadorPostalCreat = (OperadorPostalDto) database.get("operador1");
		authenticationTest.autenticarUsuari("admin");

		OperadorPostalDto trobat = operadorPostalService.findById(operadorPostalCreat.getId());

		assertNotNull(trobat);
		assertNotNull(trobat.getId());
		comprobarPagadorPostal(
				crearPagadorPostal,
				trobat);
	
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplCreate() {
		authenticationTest.autenticarUsuari("apl");
		operadorPostalService.upsert(database.getEntitat().getId(),crearPagadorPostal);
	}
//
//	@Test(expected = AccessDeniedException.class)
//	public void errorSiAccesAplUpdate() {
//		authenticationTest.autenticarUsuari("apl");
//		operadorPostalService.update(crearPagadorPostal);
//	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplDelete() {
		authenticationTest.autenticarUsuari("apl");
		operadorPostalService.delete(crearPagadorPostal.getId());
	}

	private void comprobarPagadorPostal(
			OperadorPostalDto original,
			OperadorPostalDto perComprovar) {

		assertEquals(
				original.getOrganismePagadorCodi(),
				perComprovar.getOrganismePagadorCodi());
				
	}
	
}

