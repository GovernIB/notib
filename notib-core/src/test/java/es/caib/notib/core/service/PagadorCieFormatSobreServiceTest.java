package es.caib.notib.core.service;

import es.caib.notib.core.api.dto.cie.CieDataDto;
import es.caib.notib.core.api.dto.cie.CieDto;
import es.caib.notib.core.api.dto.cie.CieFormatSobreDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.PagadorCieFormatSobreService;
import es.caib.notib.core.test.data.CieFormatSobreItemTest;
import es.caib.notib.core.test.data.CieItemTest;
import es.caib.notib.core.test.data.EntitatItemTest;
import lombok.extern.slf4j.Slf4j;
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
public class PagadorCieFormatSobreServiceTest extends BaseServiceTestV2 {

	private CieFormatSobreDto createPagadorCieFormatSobre;
	private CieFormatSobreDto updatePagadorCieFormatSobre;


	@Autowired
	protected PagadorCieFormatSobreService cieFormatSobreService;

	private ElementsCreats database;
	@Autowired
	private CieItemTest cieCreator;

	@Autowired
	private CieFormatSobreItemTest cieFormatSobreCreator;
	
	@Before
	public void setUp() throws Exception {

		CieDataDto cieDto = CieItemTest.getRandomInstance();
		cieCreator.addObject("cie", cieDto);

		database = createDatabase(EntitatItemTest.getRandomInstance(),
				cieCreator,
				cieFormatSobreCreator
		);


		CieDto cieCreated = (CieDto) database.get("cie");
		createPagadorCieFormatSobre= new CieFormatSobreDto();
		createPagadorCieFormatSobre.setCodi("12345");
		createPagadorCieFormatSobre.setPagadorCieId(cieCreated.getId());
		cieFormatSobreCreator.addObject("sobre1", createPagadorCieFormatSobre);
		
		updatePagadorCieFormatSobre= new CieFormatSobreDto();
		updatePagadorCieFormatSobre.setCodi("23456");
		updatePagadorCieFormatSobre.setPagadorCieId(cieCreated.getId());
		cieFormatSobreCreator.addObject("sobre2", updatePagadorCieFormatSobre);

		cieFormatSobreCreator.createAll(database.organ.getId());
		database.elementsCreats.putAll(cieFormatSobreCreator.getObjects());

	}

	
	@Test
	public void create() {
		currentTestDescription = "Create FORMAT SOBRE";
		CieDto cie = (CieDto) database.get("cie");
		CieFormatSobreDto formatSobreCreat = (CieFormatSobreDto) database.get("sobre1");

		assertNotNull(formatSobreCreat);
		assertNotNull(formatSobreCreat.getId());
		comprobarPagadorCieFormatSobre(
				createPagadorCieFormatSobre,
				formatSobreCreat);
		assertEquals(cie.getId(), formatSobreCreat.getPagadorCieId());
	}
	
	
	@Test
	public void update() {
		currentTestDescription = "Update FORMAT SOBRE";
		CieDto cie = (CieDto) database.get("cie");
		CieFormatSobreDto formatSobreCreat = (CieFormatSobreDto) database.get("sobre1");
		authenticationTest.autenticarUsuari("admin");

		updatePagadorCieFormatSobre.setId(formatSobreCreat.getId());
		CieFormatSobreDto formatModificat = cieFormatSobreService.update(
				updatePagadorCieFormatSobre);

		assertNotNull(formatModificat);
		assertNotNull(formatModificat.getId());
		assertEquals(
				formatSobreCreat.getId(),
				formatModificat.getId());

		comprobarPagadorCieFormatSobre(
				updatePagadorCieFormatSobre,
				formatModificat);
		assertEquals(cie.getId(), formatModificat.getPagadorCieId());
	}


	@Test
	public void delete() {
		currentTestDescription = "Delete FORMAT SOBRE";
		CieFormatSobreDto formatSobreCreat = (CieFormatSobreDto) database.get("sobre1");
		authenticationTest.autenticarUsuari("admin");

		CieFormatSobreDto formatBorrat = cieFormatSobreService.delete(
				formatSobreCreat.getId());
		comprobarPagadorCieFormatSobre(
				createPagadorCieFormatSobre,
				formatBorrat);
		try {
			cieFormatSobreService.findById(formatSobreCreat.getId());
			fail("El format esborrat no s'hauria d'haver trobat");
		}catch(NotFoundException expected) {
		}

	}
				
	
	@Test
	public void findById() {
		currentTestDescription = "FindById FORMAT SOBRE";
		authenticationTest.autenticarUsuari("admin");
		CieFormatSobreDto formatSobreCreat = (CieFormatSobreDto) database.get("sobre1");

		CieFormatSobreDto formatTrobat = cieFormatSobreService.findById(
				formatSobreCreat.getId());

		assertNotNull(formatTrobat);
		assertNotNull(formatTrobat.getId());
		comprobarPagadorCieFormatSobre(
				createPagadorCieFormatSobre,
				formatTrobat);
	}
	
	private void comprobarPagadorCieFormatSobre(
			CieFormatSobreDto original,
			CieFormatSobreDto perComprovar) {
		assertEquals(
				original.getCodi(),
				perComprovar.getCodi());
	}
	
}
	
