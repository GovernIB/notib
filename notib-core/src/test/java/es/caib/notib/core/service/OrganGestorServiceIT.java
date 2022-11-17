package es.caib.notib.core.service;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.PermisEnum;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.core.api.service.OrganGestorService;
import es.caib.notib.core.test.data.EntitatItemTest;
import es.caib.notib.core.test.data.OrganGestorItemTest;
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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class OrganGestorServiceIT extends BaseServiceTestV2 {

	@Autowired
	private OrganGestorService organGestorService;

	//	private PermisDto permisUser;
	// private PermisDto permisAdmin;

	private Collection<OrganGestorDto> organGestorsDatabase;

	OrganGestorDto organVigent;

	private ElementsCreats database;
	@Autowired
	private OrganGestorItemTest organGestorCreator;

	@Before
	public void setUp() throws Exception {
		addConfig("es.caib.notib.metriques.generar", "false");
		addConfig("es.caib.notib.plugin.unitats.dir3.protocol", "REST");
		addConfig("es.caib.notib.plugin.unitats.fitxer", "");
		configureMockUnitatsOrganitzativesPlugin();

		organVigent = new OrganGestorDto();
		organVigent.setCodi("DIR3-1");
		organVigent.setNom("Procedimiento 1");
		organVigent.setEstat(OrganGestorEstatEnum.V);

		OrganGestorDto organGestorNoVigent = new OrganGestorDto();
		organGestorNoVigent.setCodi("DIR3-2");
		organGestorNoVigent.setNom("Procedimiento 1");
		organGestorNoVigent.setEstat(OrganGestorEstatEnum.E);

		PermisDto permisUser = new PermisDto();
		permisUser.setUsuari(true);
		permisUser.setNotificacio(true);
		permisUser.setRead(true);
		permisUser.setTipus(TipusEnumDto.USUARI);
		permisUser.setPrincipal("user");
		organVigent.setPermisos(Arrays.asList(permisUser));
		organGestorNoVigent.setPermisos(Arrays.asList(permisUser));

		organGestorCreator.addObject("organGestorVigent", organVigent);
		organGestorCreator.addObject("organGestorNoVigent", organGestorNoVigent);
		database = createDatabase(EntitatItemTest.getRandomInstance(),
				organGestorCreator
		);
	}

	@After
	public final void tearDown() {
		destroyDatabase(database.getEntitat().getId(),
				organGestorCreator
		);
		log.info("-------------------------------------------------------------------");
		log.info("-- ...test \"" + currentTestDescription + "\" executat.");
		log.info("-------------------------------------------------------------------");
	}
	
	@Test
	public void create() {
		currentTestDescription = "create Organ Gestor";
		authenticationTest.autenticarUsuari("admin");
		EntitatDto entitatCreate = database.getEntitat();
		OrganGestorDto creat = (OrganGestorDto) database.get("organGestorVigent");

		assertNotNull(creat);
		assertNotNull(creat.getId());

		comprovarOrganoGestor(
				organVigent,
				creat);

		assertEquals(entitatCreate.getId(), creat.getEntitatId());
	}

//	@Test
//	public void delete() {
//		currentTestDescription = "Delete Organ Gestor";
//		EntitatDto entitatCreada = database.getEntitat();
//		OrganGestorDto organGestorCreat = (OrganGestorDto) database.get("organGestorVigent");
//
//		authenticationTest.autenticarUsuari("admin");
//		organGestorService.delete(
//				entitatCreada.getId(),
//				organGestorCreat.getId());
//
//		try {
//			organGestorService.findById(
//					entitatCreada.getId(),
//					organGestorCreat.getId());
//			fail("L'òrgan gestor esborrat no s'hauria d'haver trobat");
//		}catch(NotFoundException expected) {
//		}
//	}
	
	@Test
	public void findById() {
		currentTestDescription = "FindById Organ Gestor";
		authenticationTest.autenticarUsuari("admin");
		EntitatDto entitatCreada = database.getEntitat();
		OrganGestorDto organCreat = (OrganGestorDto) database.get("organGestorVigent");

		OrganGestorDto trobat = organGestorService.findById(
				entitatCreada.getId(),
				organCreat.getId());

		assertNotNull(trobat);
		assertNotNull(trobat.getId());
		comprovarOrganoGestor(
				organVigent,
				trobat);
	}
	
	@Test
	public void findByCodi() {

		currentTestDescription = "FindByCodi Organ Gestor";
		authenticationTest.autenticarUsuari("admin");
		EntitatDto entitatCreada = database.getEntitat();
		OrganGestorDto organCreat = (OrganGestorDto) database.get("organGestorVigent");

		OrganGestorDto trobat = organGestorService.findByCodi(
				entitatCreada.getId(),
				organCreat.getCodi());
		assertNotNull(trobat);
		assertNotNull(trobat.getId());
		comprovarOrganoGestor(
				organVigent,
				trobat);
	}

//	@Test
//	public void whenFindOrgansGestorsWithPermisConsulta_thenReturnAllOrgansWithPermis()
//	{
//		currentTestDescription = "whenFindOrgansGestorsWithPermisConsulta_thenReturnAllOrgansWithPermis Organ Gestor";
//		authenticationTest.autenticarUsuari("user");
//		EntitatDto entitatCreada = database.getEntitat();
//		List<OrganGestorDto> organs = organGestorService.findOrgansGestorsWithPermis(
//				entitatCreada.getId(),
//				"user",
//				PermisEnum.CONSULTA);
//		// Hi ha 3 organs gestors a la base de dades, només 2 amb permís de consulta
//		assertEquals(2, organs.size());
//	}
//
//	@Test
//	public void whenFindOrgansGestorsWithPermisNotificacio_thenReturnOrgansWithPermisAndVigents()
//	{
//		currentTestDescription = "whenFindOrgansGestorsWithPermisNotificacio_thenReturnOrgansWithPermisAndVigents Organ Gestor";
//		authenticationTest.autenticarUsuari("user");
//		EntitatDto entitatCreada = database.getEntitat();
//		List<OrganGestorDto> organs = organGestorService.findOrgansGestorsWithPermis(
//				entitatCreada.getId(),
//				"user",
//				PermisEnum.NOTIFICACIO);
//		// Hi ha 3 organs gestors a la base de dades, 2 amb permís de consulta, dels quals només 1 es vigent
//		assertEquals(1, organs.size());
//	}

//	@Test(expected = AccessDeniedException.class)
//	public void errorSiAccesSuperCreate() {
//		authenticationTest.autenticarUsuari("super");
//		organGestorService.create(organVigent);
//	}
//
//	@Test(expected = AccessDeniedException.class)
//	public void errorSiAccesAplCreate() {
//		authenticationTest.autenticarUsuari("apl");
//		organGestorService.create(organVigent);
//	}
//
//	@Test(expected = AccessDeniedException.class)
//	public void errorSiAccesAplDelete() {
//		OrganGestorDto organCreat = (OrganGestorDto) database.get("organGestorVigent");
//		authenticationTest.autenticarUsuari("apl");
//		organGestorService.delete(
//				database.getEntitat().getId(),
//				organCreat.getId());
//	}
//
//	@Test(expected = AccessDeniedException.class)
//	public void errorSiAccesSuperDelete() {
//		OrganGestorDto organCreat = (OrganGestorDto) database.get("organGestorVigent");
//		authenticationTest.autenticarUsuari("super");
//		organGestorService.delete(
//				database.getEntitat().getId(),
//				organCreat.getId());
//	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesSuperFinById() {
		OrganGestorDto organCreat = (OrganGestorDto) database.get("organGestorVigent");
		authenticationTest.autenticarUsuari("super");
		organGestorService.findById(database.getEntitat().getId(),
				organCreat.getId());
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplFinById() {
		OrganGestorDto organCreat = (OrganGestorDto) database.get("organGestorVigent");
		authenticationTest.autenticarUsuari("apl");
		organGestorService.findById(database.getEntitat().getId(),
				organCreat.getId());
	}
	
	
	private void comprovarOrganoGestor(
			OrganGestorDto original,
			OrganGestorDto perComprovar) {
		assertEquals(
				original.getCodi(),
				perComprovar.getCodi());
		assertEquals(
				original.getNom(),
				perComprovar.getNom());
		
		
	}

}
