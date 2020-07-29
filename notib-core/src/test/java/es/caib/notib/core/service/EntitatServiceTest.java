/**
 * 
 */
package es.caib.notib.core.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang.SerializationUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.EntitatTipusEnumDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.TipusDocumentDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.helper.PermisosHelper;

/**
 * Tests per al servei d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class EntitatServiceTest extends BaseServiceTest {

	@Autowired
	PermisosHelper permisosHelper;
	@Autowired
	private EntityManager entityManager;
	
	private EntitatDto entitatCreate;
	private EntitatDto entitatUpdate;
	private PermisDto permisUser;
	private PermisDto permisAdmin;

	@Before
	public void setUp() {
		entitatCreate = new EntitatDto();
		entitatCreate.setCodi("LIMIT");
		entitatCreate.setNom("Limit Tecnologies");
		entitatCreate.setDescripcio("Descripció de Limit Tecnologies");
		entitatCreate.setTipus(EntitatTipusEnumDto.GOVERN);
		entitatCreate.setDir3Codi("23599770E");
		entitatCreate.setApiKey("123abc");
		entitatCreate.setAmbEntregaDeh(true);
		entitatCreate.setAmbEntregaCie(true);
		TipusDocumentDto tipusDocDefault = new TipusDocumentDto();
		tipusDocDefault.setTipusDocEnum(TipusDocumentEnumDto.UUID);
		entitatCreate.setTipusDocDefault(tipusDocDefault);
		entitatUpdate = new EntitatDto();
//		entitatUpdate.setId(new Long(1));
		entitatUpdate.setCodi("LIMIT2");
		entitatUpdate.setNom("Limit Tecnologies 2");
		entitatUpdate.setDescripcio("Descripció de Limit Tecnologies 2");
		entitatUpdate.setTipus(EntitatTipusEnumDto.AJUNTAMENT);
		entitatUpdate.setDir3Codi("23599771E");
		entitatUpdate.setApiKey("cba321");
		entitatUpdate.setAmbEntregaDeh(false);
		entitatUpdate.setAmbEntregaCie(false);
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
	}

	@Test
    public void create() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws Exception {
					EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
					assertNotNull(entitatCreada);
					assertNotNull(entitatCreada.getId());
					comprovarEntitatCoincideix(
							entitatCreate,
							entitatCreada);
					assertEquals(true, entitatCreada.isActiva());
				}
			}, 
			"Alta de ENTITAT", 
			entitatCreate);
	}
	
	@Test
	public void update() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) {
					autenticarUsuari("super");
					EntitatDto creada = (EntitatDto)elementsCreats.get(0);
					entitatUpdate.setId(creada.getId());
					EntitatDto modificada = entitatService.update(entitatUpdate);
					assertNotNull(modificada);
					assertNotNull(modificada.getId());
					assertEquals(
							creada.getId(),
							modificada.getId());
					comprovarEntitatCoincideix(
							entitatUpdate,
							modificada);
					assertEquals(true, modificada.isActiva());
				}
			},
			entitatCreate);
	}
	
	@Test
	public void delete() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) {
					autenticarUsuari("super");
					EntitatDto creada = (EntitatDto)elementsCreats.get(0);
					EntitatDto esborrada = entitatService.delete(creada.getId());
					comprovarEntitatCoincideix(
							entitatCreate,
							esborrada);
					try {
						entitatService.findById(creada.getId());
						fail("La entitat esborrada no s'hauria d'haver trobat");
					} catch (NotFoundException expected) {
					}
					elementsCreats.remove(creada);
				}
			},
			entitatCreate);
	}

	@Test
	public void updateActiva() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) {
					autenticarUsuari("super");
					EntitatDto creada = (EntitatDto)elementsCreats.get(0);
					EntitatDto desactivada = entitatService.updateActiva(
							creada.getId(),
							false);
					assertEquals(
							false,
							desactivada.isActiva());
					EntitatDto activada = entitatService.updateActiva(
							creada.getId(),
							true);
					assertEquals(
							true,
							activada.isActiva());
				}
			},
			entitatCreate);
	}
	
	@Test
	public void findById() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) {
					autenticarUsuari("super");
					EntitatDto creada = (EntitatDto)elementsCreats.get(0);
					EntitatDto trobada = entitatService.findById(creada.getId());
					assertNotNull(trobada);
					assertNotNull(trobada.getId());
					comprovarEntitatCoincideix(
							entitatCreate,
							trobada);
				}
			},
			entitatCreate);
	}
	
	@Test
	public void findByCodi() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) {
					autenticarUsuari("super");
					EntitatDto trobada = entitatService.findByCodi(
							entitatCreate.getCodi());
					assertNotNull(trobada);
					assertNotNull(trobada.getId());
					comprovarEntitatCoincideix(
							entitatCreate,
							trobada);
				}
			},
			entitatCreate);
	}
	
	@Test
	public void findByCodiDir3() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) {
					autenticarUsuari("super");
					EntitatDto trobada = entitatService.findByDir3codi(
							entitatCreate.getDir3Codi());
					assertNotNull(trobada);
					assertNotNull(trobada.getId());
					comprovarEntitatCoincideix(
							entitatCreate,
							trobada);
				}
			},
			entitatCreate);
	}
	
	@Test
	public void managePermisAdmin() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) {
					EntitatDto creada = (EntitatDto)elementsCreats.get(0);
					autenticarUsuari("user");
					List<EntitatDto> entitatsAccessibles = entitatService.findAccessiblesUsuariActual("NOT_USER");
					assertThat(
							entitatsAccessibles.size(),
							is(0));
					autenticarUsuari("super");
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
					autenticarUsuari("user");
					entitatsAccessibles = entitatService.findAccessiblesUsuariActual("NOT_USER");
					assertThat(
							entitatsAccessibles.size(),
							is(1));
					assertThat(
							entitatsAccessibles.get(0).getId(),
							is(creada.getId()));
					autenticarUsuari("super");
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
					autenticarUsuari("user");
					entitatsAccessibles = entitatService.findAccessiblesUsuariActual("NOT_USER");
					assertThat(
							entitatsAccessibles.size(),
							is(1));
					assertThat(
							entitatsAccessibles.get(0).getId(),
							is(creada.getId()));
					autenticarUsuari("super");
					entitatService.permisDelete(
							creada.getId(),
							permisPerUser.getId());
					permisos = permisosHelper.findPermisos(creada.getId(), EntitatEntity.class);
					assertThat(
							permisos.size(),
							is(1));
					autenticarUsuari("user");
					entitatsAccessibles = entitatService.findAccessiblesUsuariActual("NOT_USER");
					assertThat(
							entitatsAccessibles.size(),
							is(0));
					autenticarUsuari("super");
					entitatService.permisDelete(
							creada.getId(),
							permisos.get(0).getId());
					permisos = permisosHelper.findPermisos(creada.getId(), EntitatEntity.class);
					assertThat(
							permisos.size(),
							is(0));
					autenticarUsuari("user");
					entitatsAccessibles = entitatService.findAccessiblesUsuariActual("NOT_USER");
					assertThat(
							entitatsAccessibles.size(),
							is(0));
				}
			},
			entitatCreate);
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
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) {
					autenticarUsuari("super");
					try {
						entitatService.create(entitatCreate);
						fail("L'execució no ha donat l'error de violació d'integritat per clau única repetida");
					} catch (DataIntegrityViolationException ex) {
						// Excepció esperada
						entityManager.clear();
					}
				}
			},
			entitatCreate);
	}
	
	@Test
	public void errorSiDir3CodiDuplicat() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) {
					autenticarUsuari("super");
					try {
						EntitatDto entitatSameDir3 = (EntitatDto)SerializationUtils.clone(entitatCreate);
						entitatSameDir3.setCodi("LIMIT0");
						entitatService.create(entitatSameDir3);
						fail("L'execució no ha donat l'error de violació d'integritat per clau única repetida");
					} catch (DataIntegrityViolationException ex) {
						// Excepció esperada
						entityManager.clear();
					}
				}
			},
			entitatCreate);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminCreate() {
		autenticarUsuari("admin");
		entitatService.create(entitatCreate);
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserCreate() {
		autenticarUsuari("user");
		entitatService.create(entitatCreate);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplCreate() {
		autenticarUsuari("apl");
		entitatService.create(entitatCreate);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminUpdate() {
		autenticarUsuari("admin");
		entitatService.update(entitatCreate);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserUpdate() {
		autenticarUsuari("user");
		entitatService.update(entitatCreate);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplUpdate() {
		autenticarUsuari("apl");
		entitatService.update(entitatCreate);
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminDelete() {
		autenticarUsuari("admin");
		entitatService.delete(new Long(1));
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserDelete() {
		autenticarUsuari("user");
		entitatService.delete(new Long(1));
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplDelete() {
		autenticarUsuari("apl");
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
		assertEquals(
				original.isAmbEntregaCie(),
				perComprovar.isAmbEntregaCie());
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
