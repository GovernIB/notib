/**
 * 
 */
package es.caib.notib.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.EntitatTipusEnumDto;
import es.caib.notib.core.api.dto.TipusDocumentDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;

/**
 * Tests per al servei d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class EntitatServiceTest extends BaseServiceTest {

	private EntitatDto entitatCreate;
//	private EntitatDto entitatUpdate;
//	private PermisDto permisUser;
//	private PermisDto permisAdmin;

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
		entitatCreate.setActiva(true);
		TipusDocumentDto tipusDocDefault = new TipusDocumentDto();
		tipusDocDefault.setTipusDocEnum(TipusDocumentEnumDto.UUID);
		entitatCreate.setTipusDocDefault(tipusDocDefault);
//		entitatUpdate = new EntitatDto();
//		entitatUpdate.setId(new Long(1));
//		entitatUpdate.setCodi("LIMIT2");
//		entitatUpdate.setNom("Limit Tecnologies 2");
//		entitatCreate.setDescripcio("Descripció de Limit Tecnologies 2");
//		entitatUpdate.setDir3Codi("23599771E");
//		permisUser = new PermisDto();
//		permisUser.setRead(true);
//		permisUser.setTipus(TipusEnumDto.USUARI);
//		permisUser.setPrincipal("user");
//		permisAdmin = new PermisDto();
//		permisAdmin.setAdministration(true);
//		permisAdmin.setTipus(TipusEnumDto.USUARI);
//		permisAdmin.setPrincipal("admin");
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
//	@Test
//	public void findById() {
//		autenticarUsuari("admin");
//		EntitatDto entitatCreada = entitatService.create(entitatCreate);
//		assertNotNull(entitatService.findById(entitatCreada.getId()));
//		assertThat(
//				entitatCreate.getCodi(),
//				is(entitatCreada.getCodi()));
//		assertThat(
//				entitatCreate.getNom(),
//				is(entitatCreada.getNom()));
//		assertThat(
//				entitatCreate.getDir3Codi(),
//				is(entitatCreada.getDir3Codi()));
//    }
//	@Test
//	public void update() {
//		autenticarUsuari("admin");
//		EntitatDto entitatCreada = entitatService.create(entitatCreate);
//		assertNotNull(entitatService.findById(entitatCreada.getId()));
//		assertThat(
//				entitatCreate.getCodi(),
//				not(entitatUpdate.getCodi()));
//		assertThat(
//				entitatCreate.getNom(),
//				not(entitatUpdate.getNom()));
//		assertThat(
//				entitatCreate.getDir3Codi(),
//				not(entitatUpdate.getDir3Codi()));
//		entitatUpdate.setId(entitatCreada.getId());
//		EntitatDto entitatModificada = entitatService.update(entitatUpdate);
//		assertThat(
//				entitatUpdate.getCodi(),
//				is(entitatModificada.getCodi()));
//		assertThat(
//				entitatUpdate.getNom(),
//				is(entitatModificada.getNom()));
//		assertThat(
//				entitatUpdate.getDir3Codi(),
//				is(entitatModificada.getDir3Codi()));
//    }
//	@Test
//	public void delete() {
//		autenticarUsuari("admin");
//		EntitatDto entitatCreada = entitatService.create(entitatCreate);
//		assertNotNull(entitatService.findById(entitatCreada.getId()));
//		EntitatDto entitatEsborrada = entitatService.delete(entitatCreada.getId());
//		assertThat(
//				entitatCreate.getCodi(),
//				is(entitatEsborrada.getCodi()));
//		assertThat(
//				entitatCreate.getNom(),
//				is(entitatEsborrada.getNom()));
//		assertThat(
//				entitatCreate.getDir3Codi(),
//				is(entitatEsborrada.getDir3Codi()));
//		assertNull(entitatService.findById(entitatCreada.getId()));
//	}
//
//	@Test
//	public void updateActiva() {
//		autenticarUsuari("admin");
//		EntitatDto entitatCreada = entitatService.create(entitatCreate);
//		assertNotNull(entitatCreada.getId());
//		entitatService.updateActiva(entitatCreada.getId(), false);
//		EntitatDto entitatRecuperada = entitatService.findById(entitatCreada.getId());
//		assertFalse(entitatRecuperada.isActiva());
//		entitatService.updateActiva(entitatCreada.getId(), true);
//		entitatRecuperada = entitatService.findById(entitatCreada.getId());
//		assertTrue(entitatRecuperada.isActiva());
//	}
//
//	@Test
//	public void findByCodi() {
//		autenticarUsuari("admin");
//		EntitatDto entitatCreada = entitatService.create(entitatCreate);
//		assertNotNull(entitatService.findById(entitatCreada.getId()));
//		EntitatDto entitatAmbCodi = entitatService.findByCodi(entitatCreada.getCodi());
//		assertNotNull(entitatAmbCodi);
//		assertNotNull(entitatAmbCodi.getId());
//		assertThat(
//				entitatCreada.getId(),
//				is(entitatAmbCodi.getId()));
//	}
//
//	@Test
//	public void managePermisRepresentant() {
//		autenticarUsuari("rep");
//		List<EntitatDto> entitatsAccessibles = entitatService.findAccessiblesUsuariActual("NOT_USER");
//		assertThat(
//				entitatsAccessibles.size(),
//				is(0));
//		
//		autenticarUsuari("admin");
//		EntitatDto entitatCreada = entitatService.create(entitatCreate);
//		PermisDto permisRepresentat = new PermisDto();
//		permisRepresentat.setTipus(TipusEnumDto.USUARI);
//		permisRepresentat.setPrincipal("rep");
//		entitatService.permisUpdate(
//				entitatCreada.getId(),
//				permisRepresentat);
//		
//		autenticarUsuari("rep");
//		List<PermisDto> permisos = entitatService.permisFindByEntitatId(
//				entitatCreada.getId());
//		assertThat(
//				permisos.size(),
//				is(1));
//		entitatsAccessibles = entitatService.findAccessiblesUsuariActual("NOT_USER");
//		assertThat(
//				entitatsAccessibles.size(),
//				is(1));
//		
//		autenticarUsuari("admin");
//		for (PermisDto p : permisos)
//			if (p.getPrincipal().equals("rep"))
//				permisRepresentat = p;
//		entitatService.permisDelete(
//				entitatCreada.getId(),
//				permisRepresentat.getId());
//		autenticarUsuari("rep");
//		entitatsAccessibles = entitatService.findAccessiblesUsuariActual("NOT_USER");
//		assertThat(
//				entitatsAccessibles.size(),
//				is(0));
//	}
//
//	@Test(expected = DataIntegrityViolationException.class)
//	public void errorSiCodiDuplicat() {
//		autenticarUsuari("admin");
//		entitatService.create(entitatCreate);
//		entitatService.create(entitatCreate);
//	}
//
//	@Test(expected = AccessDeniedException.class)
//	public void errorSiAccesAdminCreate() {
//		autenticarUsuari("rep");
//		entitatService.create(entitatCreate);
//	}
//
//	@Test(expected = AccessDeniedException.class)
//	public void errorSiAccesAdminUpdate() {
//		autenticarUsuari("rep");
//		entitatService.update(entitatCreate);
//	}
//
//	@Test(expected = AccessDeniedException.class)
//	public void errorSiAccesAdminDelete() {
//		autenticarUsuari("rep");
//		entitatService.delete(new Long(1));
//	}

}
