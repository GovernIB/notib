/**
 * 
 */
package es.caib.notib.core.service;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.core.test.data.EntitatItemTest;
import org.apache.commons.lang.SerializationUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Tests per al servei d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class EntitatServiceIT extends BaseServiceTestV2 {

	@Autowired
	PermisosHelper permisosHelper;
	@Autowired
	private EntityManager entityManager;
	@Autowired
	protected EntitatService entitatService;

	private PermisDto permisUser;
	private PermisDto permisAdmin;

	@Autowired
	private EntitatItemTest entitatItemTest;

	private ElementsCreats database;

	@Before
	public void setUp() throws Exception {
		setDefaultConfigs();
//		addConfig("es.caib.notib.plugin.dades.usuari.class", "es.caib.notib.plugin.usuari.DadesUsuariPluginMock");
		EntitatDto entitatCreate = new EntitatDto();
		entitatCreate.setCodi("LIMIT2");
		entitatCreate.setNom("Limit Tecnologies");
		entitatCreate.setDescripcio("Descripció de Limit Tecnologies");
		entitatCreate.setTipus(EntitatTipusEnumDto.GOVERN);
		entitatCreate.setDir3Codi("23599770E");
		entitatCreate.setApiKey("123abc");
		entitatCreate.setAmbEntregaDeh(true);
//		entitatCreate.setAmbEntregaCie(true);
		TipusDocumentDto tipusDocDefault = new TipusDocumentDto();
		tipusDocDefault.setTipusDocEnum(TipusDocumentEnumDto.UUID);
		entitatCreate.setTipusDocDefault(tipusDocDefault);
		EntitatDto entitatUpdate = new EntitatDto();
		entitatUpdate.setCodi("LIMIT3");
		entitatUpdate.setNom("Limit Tecnologies 2");
		entitatUpdate.setDescripcio("Descripció de Limit Tecnologies 2");
		entitatUpdate.setTipus(EntitatTipusEnumDto.AJUNTAMENT);
		entitatUpdate.setDir3Codi("23599771E");
		entitatUpdate.setApiKey("cba321");
		entitatUpdate.setAmbEntregaDeh(false);
//		entitatUpdate.setAmbEntregaCie(false);
		TipusDocumentDto tipusDocDefault2 = new TipusDocumentDto();
		tipusDocDefault2.setTipusDocEnum(TipusDocumentEnumDto.CSV);
		entitatUpdate.setTipusDocDefault(tipusDocDefault2);
		permisUser = new PermisDto();
		permisUser.setUsuari(true);
		permisUser.setTipus(TipusEnumDto.USUARI);
		permisUser.setPrincipal("user");
		permisAdmin = new PermisDto();
		permisAdmin.setAdministration(true);
		permisAdmin.setTipus(TipusEnumDto.USUARI);
		permisAdmin.setPrincipal("admin");

		entitatItemTest.addObject("entitatCreate", entitatCreate);
		entitatItemTest.addObject("entitatUpdate", entitatUpdate);

		database = createDatabase(EntitatItemTest.getRandomInstance(),
				entitatItemTest
		);
	}

	@After
	public final void tearDown() {
		removeAllConfigs();
		destroyDatabase(database.getEntitat().getId(),
				entitatItemTest
		);
	}


	@Test
    public void create() {
		EntitatDto entitatACrear = EntitatItemTest.getRandomInstance();
		entitatACrear.setCodi("LIMIT4");
		entitatACrear.setNom("LIMIT4");
		entitatACrear.setDescripcio("Descripció de Limit Tecnologies 4");
		entitatACrear.setDir3Codi("A000999");
		// TODO: Hi ha camps que si no estan fixats es llança una excepció no controlada, s'haurien de controlar
		EntitatDto entitatCreada = entitatService.create(entitatACrear);
		assertNotNull(entitatCreada);
		assertNotNull(entitatCreada.getId());
		comprovarEntitatCoincideix(
				entitatACrear,
				entitatCreada);
		assertTrue(entitatCreada.isActiva());

		entitatService.delete(entitatCreada.getId());
	}
	
	@Test
	public void update() {
		authenticationTest.autenticarUsuari("super");
		EntitatDto entitatUpdate = (EntitatDto) database.get("entitatUpdate");
		entitatUpdate.setCodi("CODI_EDITAT");
		EntitatDto modificada = entitatService.update(entitatUpdate);
		assertNotNull(modificada);
		assertNotNull(modificada.getId());
		comprovarEntitatCoincideix(
				entitatUpdate,
				modificada);
		assertTrue(modificada.isActiva());
	}
	
	@Test
	public void delete() {
		authenticationTest.autenticarUsuari("super");
		EntitatDto creada = (EntitatDto) database.get("entitatCreate");
		EntitatDto esborrada = entitatService.delete(creada.getId());
		comprovarEntitatCoincideix(
				creada,
				esborrada);
		try {
			entitatService.findById(creada.getId());
			fail("La entitat esborrada no s'hauria d'haver trobat");
		} catch (NotFoundException expected) {
		}
	}

	@Test
	public void updateActiva() {
		authenticationTest.autenticarUsuari("super");
		EntitatDto creada = (EntitatDto) database.get("entitatCreate");
		EntitatDto desactivada = entitatService.updateActiva(
				creada.getId(),
				false);
		assertFalse(desactivada.isActiva());
		EntitatDto activada = entitatService.updateActiva(
				creada.getId(),
				true);
		assertTrue(activada.isActiva());
	}
	
	@Test
	public void findById() {
		authenticationTest.autenticarUsuari("super");
		EntitatDto creada = (EntitatDto) database.get("entitatCreate");
		EntitatDto trobada = entitatService.findById(creada.getId());
		assertNotNull(trobada);
		assertNotNull(trobada.getId());
		comprovarEntitatCoincideix(
				creada,
				trobada);
	}
	
	@Test
	public void findByCodi() {
		authenticationTest.autenticarUsuari("super");
		EntitatDto creada = (EntitatDto) database.get("entitatCreate");
		EntitatDto trobada = entitatService.findByCodi(
				creada.getCodi());
		assertNotNull(trobada);
		assertNotNull(trobada.getId());
		comprovarEntitatCoincideix(
				creada,
				trobada);
	}
	
	@Test
	public void findByCodiDir3() {
		authenticationTest.autenticarUsuari("super");
		EntitatDto creada = (EntitatDto) database.get("entitatCreate");
		EntitatDto trobada = entitatService.findByDir3codi(
				creada.getDir3Codi());
		assertNotNull(trobada);
		assertNotNull(trobada.getId());
		comprovarEntitatCoincideix(
				creada,
				trobada);
	}
	
	@Test
	public void managePermisAdmin() {
		EntitatDto creada = (EntitatDto) database.get("entitatCreate");
		authenticationTest.autenticarUsuari("user");
		List<EntitatDto> entitatsAccessibles = entitatService.findAccessiblesUsuariActual("tothom");
		assertThat(
				entitatsAccessibles.size(),
				is(0));
		authenticationTest.autenticarUsuari("super");
		List<PermisDto> permisos = permisosHelper.findPermisos(creada.getId(), EntitatEntity.class);
		assertThat(
				permisos.size(),
				is(0));
		entitatService.permisUpdate(
				creada.getId(),
				permisUser);
		permisos = permisosHelper.findPermisos(creada.getId(), EntitatEntity.class);
		assertThat(
				permisos.size(),
				is(1));
		comprovarPermisCoincideix(
				permisUser,
				permisos.get(0));
		authenticationTest.autenticarUsuari("user");
		entitatsAccessibles = entitatService.findAccessiblesUsuariActual("tothom");
		assertThat(
				entitatsAccessibles.size(),
				is(1));
		assertThat(
				entitatsAccessibles.get(0).getId(),
				is(creada.getId()));
		authenticationTest.autenticarUsuari("super");
		entitatService.permisUpdate(
				creada.getId(),
				permisAdmin);
		permisos = permisosHelper.findPermisos(creada.getId(), EntitatEntity.class);
		PermisDto permisPerUser = null;
		for (PermisDto permis: permisos) {
			if ("user".equals(permis.getPrincipal())) {
				permisPerUser = permis;
				break;
			}
		}
		assertNotNull(permisUser);
		assertThat(
				permisos.size(),
				is(2));
		comprovarPermisCoincideix(
				permisUser,
				permisPerUser);
		authenticationTest.autenticarUsuari("user");
		entitatsAccessibles = entitatService.findAccessiblesUsuariActual("tothom");
		assertThat(
				entitatsAccessibles.size(),
				is(1));
		assertThat(
				entitatsAccessibles.get(0).getId(),
				is(creada.getId()));
		authenticationTest.autenticarUsuari("super");
		entitatService.permisDelete(
				creada.getId(),
				permisPerUser.getId());
		permisos = permisosHelper.findPermisos(creada.getId(), EntitatEntity.class);
		assertThat(
				permisos.size(),
				is(1));
		authenticationTest.autenticarUsuari("user");
		entitatsAccessibles = entitatService.findAccessiblesUsuariActual("tothom");
		assertThat(
				entitatsAccessibles.size(),
				is(0));
		authenticationTest.autenticarUsuari("super");
		entitatService.permisDelete(
				creada.getId(),
				permisos.get(0).getId());
		permisos = permisosHelper.findPermisos(creada.getId(), EntitatEntity.class);
		assertThat(
				permisos.size(),
				is(0));
		authenticationTest.autenticarUsuari("user");
		entitatsAccessibles = entitatService.findAccessiblesUsuariActual("tothom");
		assertThat(
				entitatsAccessibles.size(),
				is(0));
	}
	
	@Test
	public void findTipusDocument() {
		// TODO
	}
	
	@Test
	public void findTipusDocumentDefault() {
		// TODO
	}
	
	@Test
	public void errorSiCodiDuplicat() {
		authenticationTest.autenticarUsuari("super");
		EntitatDto creada = (EntitatDto) database.get("entitatCreate");
		try {
			entitatService.create(creada);
			fail("L'execució no ha donat l'error de violació d'integritat per clau única repetida");
		} catch (DataIntegrityViolationException ex) {
			// Excepció esperada
			entityManager.clear();
		}
	}
	
	@Test
	public void errorSiDir3CodiDuplicat() {
		authenticationTest.autenticarUsuari("super");
		EntitatDto creada = (EntitatDto) database.get("entitatCreate");
		try {
			EntitatDto entitatSameDir3 = (EntitatDto)SerializationUtils.clone(creada);
			entitatSameDir3.setCodi("LIMIT0");
			entitatService.create(entitatSameDir3);
			fail("L'execució no ha donat l'error de violació d'integritat per clau única repetida");
		} catch (DataIntegrityViolationException ex) {
			// Excepció esperada
			entityManager.clear();
		}
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminCreate() {
		EntitatDto creada = (EntitatDto) database.get("entitatCreate");
		authenticationTest.autenticarUsuari("admin");
		entitatService.create(creada);
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserCreate() {
		EntitatDto creada = (EntitatDto) database.get("entitatCreate");
		authenticationTest.autenticarUsuari("user");
		entitatService.create(creada);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplCreate() {
		EntitatDto creada = (EntitatDto) database.get("entitatCreate");
		authenticationTest.autenticarUsuari("apl");
		entitatService.create(creada);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserUpdate() {
		EntitatDto creada = (EntitatDto) database.get("entitatCreate");
		authenticationTest.autenticarUsuari("user");
		entitatService.update(creada);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplUpdate() {
		EntitatDto creada = (EntitatDto) database.get("entitatCreate");
		authenticationTest.autenticarUsuari("apl");
		entitatService.update(creada);
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminDelete() {
		authenticationTest.autenticarUsuari("admin");
		entitatService.delete(new Long(1));
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserDelete() {
		authenticationTest.autenticarUsuari("user");
		entitatService.delete(new Long(1));
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplDelete() {
		authenticationTest.autenticarUsuari("apl");
		entitatService.delete(new Long(1));
	}
	
	
	private void comprovarEntitatCoincideix(
			EntitatDto original,
			EntitatDto perComprovar) {
		assertEquals(
				original.getCodi(),
				perComprovar.getCodi());
		assertEquals(
				original.getNom(),
				perComprovar.getNom());
		assertEquals(
				original.getDescripcio(),
				perComprovar.getDescripcio());
		assertEquals(
				original.getTipus(),
				perComprovar.getTipus());
		assertEquals(
				original.getDir3Codi(),
				perComprovar.getDir3Codi());
		assertEquals(
				original.getApiKey(),
				perComprovar.getApiKey());
		assertEquals(
				original.isAmbEntregaDeh(),
				perComprovar.isAmbEntregaDeh());
//		assertEquals(
//				original.isAmbEntregaCie(),
//				perComprovar.isAmbEntregaCie());
	}
	
	private void comprovarPermisCoincideix(
			PermisDto original,
			PermisDto perComprovar) {
		assertEquals(
				original.getPrincipal(),
				perComprovar.getPrincipal());
		assertEquals(
				original.getTipus(),
				perComprovar.getTipus());
		assertEquals(
				original.isRead(),
				perComprovar.isRead());
		assertEquals(
				original.isWrite(),
				perComprovar.isWrite());
		assertEquals(
				original.isCreate(),
				perComprovar.isCreate());
		assertEquals(
				original.isDelete(),
				perComprovar.isDelete());
		assertEquals(
				original.isAdministration(),
				perComprovar.isAdministration());
	}
	
}
