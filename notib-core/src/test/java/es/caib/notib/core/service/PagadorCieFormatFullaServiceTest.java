package es.caib.notib.core.service;

import es.caib.notib.core.api.dto.cie.CieDataDto;
import es.caib.notib.core.api.dto.cie.CieDto;
import es.caib.notib.core.api.dto.cie.CieFormatFullaDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.PagadorCieFormatFullaService;
import es.caib.notib.core.test.data.CieFormatFullaItemTest;
import es.caib.notib.core.test.data.CieItemTest;
import es.caib.notib.core.test.data.EntitatItemTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;


@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class PagadorCieFormatFullaServiceTest extends BaseServiceTestV2{

	private CieFormatFullaDto createPagadorCieFormatFulla;
	private CieFormatFullaDto updatePagadorCieFormatFulla;

	@Autowired
	protected PagadorCieFormatFullaService cieFormatFullaService;

	private ElementsCreats database;
	@Autowired
	private CieItemTest cieCreator;

	@Autowired
	private CieFormatFullaItemTest cieFormatFullaCreator;

	@Before
	public void setUp()throws Exception {
		addConfig("es.caib.notib.metriques.generar", "false");

		CieDataDto cieDto = CieItemTest.getRandomInstance();
		cieCreator.addObject("cie", cieDto);

		database = createDatabase(EntitatItemTest.getRandomInstance(),
				cieCreator,
				cieFormatFullaCreator
		);


		CieDto cieCreated = (CieDto) database.get("cie");
		createPagadorCieFormatFulla=new CieFormatFullaDto();
		createPagadorCieFormatFulla.setCodi("122");
		createPagadorCieFormatFulla.setPagadorCieId(cieCreated.getId());
		cieFormatFullaCreator.addObject("fulla1", createPagadorCieFormatFulla);

		updatePagadorCieFormatFulla=new CieFormatFullaDto();
		updatePagadorCieFormatFulla.setCodi("12333");
		updatePagadorCieFormatFulla.setPagadorCieId(cieCreated.getId());
		cieFormatFullaCreator.addObject("fulla2", updatePagadorCieFormatFulla);

		cieFormatFullaCreator.createAll(database.organ.getId());
		database.elementsCreats.putAll(cieFormatFullaCreator.getObjects());
	}

	@After
	public final void tearDown() {
		destroyDatabase(database.getEntitat().getId(),
				cieCreator,
				cieFormatFullaCreator
		);
		log.info("-------------------------------------------------------------------");
		log.info("-- ...test \"" + currentTestDescription + "\" executat.");
		log.info("-------------------------------------------------------------------");
	}

	@Test
	public void create() {
		currentTestDescription = "Create FORMAT FULLA";
		CieDto cie = (CieDto) database.get("cie");
		CieFormatFullaDto formatFullaCreada = (CieFormatFullaDto) database.get("fulla1");

		assertNotNull(formatFullaCreada);
		assertNotNull(formatFullaCreada.getId());
		comprobarPagadorCieFormatFulla(
				createPagadorCieFormatFulla,
				formatFullaCreada);
		assertEquals(cie.getId(), formatFullaCreada.getPagadorCieId());
	}
	
	
	
	@Test
	public void update() {
		currentTestDescription = "Update FORMAT FULLA";
		CieDto cie = (CieDto) database.get("cie");
		CieFormatFullaDto formatFullaCreada = (CieFormatFullaDto) database.get("fulla1");
		authenticationTest.autenticarUsuari("admin");

		updatePagadorCieFormatFulla.setId(formatFullaCreada.getId());
		CieFormatFullaDto formatModificat = cieFormatFullaService.update(
				updatePagadorCieFormatFulla);

		assertNotNull(formatModificat);
		assertNotNull(formatModificat.getId());
		assertEquals(
				formatFullaCreada.getId(),
				formatModificat.getId());

		comprobarPagadorCieFormatFulla(
				updatePagadorCieFormatFulla,
				formatModificat);
		assertEquals(cie.getId(), formatModificat.getPagadorCieId());
	}
	
	@Test
	public void delete() {
		currentTestDescription = "Delete FORMAT FULLA";
		CieFormatFullaDto formatFullaCreada = (CieFormatFullaDto) database.get("fulla1");
		authenticationTest.autenticarUsuari("admin");

		CieFormatFullaDto formatBorrat = cieFormatFullaService.delete(
				formatFullaCreada.getId());
		comprobarPagadorCieFormatFulla(
				createPagadorCieFormatFulla,
				formatBorrat);
		try {
			cieFormatFullaService.findById(formatFullaCreada.getId());
			fail("El format esborrat no s'hauria d'haver trobat");
		}catch(NotFoundException expected) {
		}

	}
				
	
	@Test
	public void findById() {
		currentTestDescription = "FindById FORMAT FULLA";
		authenticationTest.autenticarUsuari("admin");
		CieFormatFullaDto formatCreat = (CieFormatFullaDto) database.get("fulla1");

		CieFormatFullaDto formatTrobat = cieFormatFullaService.findById(
				formatCreat.getId());

		assertNotNull(formatTrobat);
		assertNotNull(formatTrobat.getId());
		comprobarPagadorCieFormatFulla(
				createPagadorCieFormatFulla,
				formatTrobat);
	}

	private void comprobarPagadorCieFormatFulla(
			CieFormatFullaDto original,
			CieFormatFullaDto perComprovar) {
		assertEquals(
				original.getCodi(),
				perComprovar.getCodi());
	}

}

