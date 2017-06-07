/**
 * 
 */
package es.caib.notib.core.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

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
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.helper.PropertiesHelper;

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
	private EntitatService entitatService;

	private EntitatDto entitatCreate;
	private EntitatDto entitatUpdate;
	private PermisDto permisUserRepresentant;

	@Before
	public void setUp() {
		PropertiesHelper.getProperties("classpath:es/caib/notib/core/test.properties");
		entitatCreate = new EntitatDto();
		entitatCreate.setCodi("LIMIT");
		entitatCreate.setNom("Limit Tecnologies");
		entitatCreate.setDescripcio("Descripció de Limit Tecnologies");
		entitatCreate.setCif("12345678Z");
		entitatCreate.setTipus(EntitatTipusEnumDto.AJUNTAMENT);
		entitatCreate.setDir3Codi("LIM00001");
		entitatCreate.setActiva(true);
		entitatUpdate = new EntitatDto();
		entitatUpdate.setId(new Long(1));
		entitatUpdate.setCodi("LIMIT2");
		entitatUpdate.setNom("Limit Tecnologies 2");
		entitatCreate.setDescripcio("Descripció de Limit Tecnologies 2");
		entitatUpdate.setCif("23599770E");
		entitatUpdate.setDir3Codi("LIM00002");
		entitatCreate.setActiva(true);
		permisUserRepresentant = new PermisDto();
		permisUserRepresentant.setRepresentant(true);
		permisUserRepresentant.setTipus(TipusEnumDto.USUARI);
		permisUserRepresentant.setNom("user");
	}

	@Test
    public void create() {
		autenticarUsuari("admin");
		assertNotNull(entitatCreate);
		assertNull(entitatCreate.getId());
		EntitatDto entitatCreada = entitatService.create(entitatCreate);
		assertNotNull(entitatCreada);
		assertNotNull(entitatCreada.getId());
	}
	@Test
	public void findById() {
		autenticarUsuari("admin");
		EntitatDto entitatCreada = entitatService.create(entitatCreate);
		assertNotNull(entitatService.findById(entitatCreada.getId()));
		assertThat(
				entitatCreate.getCodi(),
				is(entitatCreada.getCodi()));
		assertThat(
				entitatCreate.getNom(),
				is(entitatCreada.getNom()));
		assertThat(
				entitatCreate.getCif(),
				is(entitatCreada.getCif()));
    }
	@Test
	public void update() {
		autenticarUsuari("admin");
		EntitatDto entitatCreada = entitatService.create(entitatCreate);
		assertNotNull(entitatService.findById(entitatCreada.getId()));
		assertThat(
				entitatCreate.getCodi(),
				not(entitatUpdate.getCodi()));
		assertThat(
				entitatCreate.getNom(),
				not(entitatUpdate.getNom()));
		assertThat(
				entitatCreate.getCif(),
				not(entitatUpdate.getCif()));
		entitatUpdate.setId(entitatCreada.getId());
		EntitatDto entitatModificada = entitatService.update(entitatUpdate);
		assertThat(
				entitatUpdate.getCodi(),
				is(entitatModificada.getCodi()));
		assertThat(
				entitatUpdate.getNom(),
				is(entitatModificada.getNom()));
		assertThat(
				entitatUpdate.getCif(),
				is(entitatModificada.getCif()));
    }
	@Test
	public void delete() {
		autenticarUsuari("admin");
		EntitatDto entitatCreada = entitatService.create(entitatCreate);
		assertNotNull(entitatService.findById(entitatCreada.getId()));
		EntitatDto entitatEsborrada = entitatService.delete(entitatCreada.getId());
		assertThat(
				entitatCreate.getCodi(),
				is(entitatEsborrada.getCodi()));
		assertThat(
				entitatCreate.getNom(),
				is(entitatEsborrada.getNom()));
		assertThat(
				entitatCreate.getCif(),
				is(entitatEsborrada.getCif()));
		assertNull(entitatService.findById(entitatCreada.getId()));
	}

	@Test
	public void updateActiva() {
		autenticarUsuari("admin");
		EntitatDto entitatCreada = entitatService.create(entitatCreate);
		assertNotNull(entitatCreada.getId());
		entitatService.updateActiva(entitatCreada.getId(), false);
		EntitatDto entitatRecuperada = entitatService.findById(entitatCreada.getId());
		assertFalse(entitatRecuperada.isActiva());
		entitatService.updateActiva(entitatCreada.getId(), true);
		entitatRecuperada = entitatService.findById(entitatCreada.getId());
		assertTrue(entitatRecuperada.isActiva());
	}

	@Test
	public void findByCodi() {
		autenticarUsuari("admin");
		EntitatDto entitatCreada = entitatService.create(entitatCreate);
		assertNotNull(entitatService.findById(entitatCreada.getId()));
		EntitatDto entitatAmbCodi = entitatService.findByCodi(entitatCreada.getCodi());
		assertNotNull(entitatAmbCodi);
		assertNotNull(entitatAmbCodi.getId());
		assertThat(
				entitatCreada.getId(),
				is(entitatAmbCodi.getId()));
	}

	@Test
	public void managePermisRepresentant() {
		autenticarUsuari("rep");
		List<EntitatDto> entitatsAccessibles = entitatService.findAccessiblesUsuariActual();
		assertThat(
				entitatsAccessibles.size(),
				is(0));
		
		autenticarUsuari("admin");
		EntitatDto entitatCreada = entitatService.create(entitatCreate);
		PermisDto permisRepresentat = new PermisDto();
		permisRepresentat.setRepresentant(true);
		permisRepresentat.setTipus(TipusEnumDto.USUARI);
		permisRepresentat.setNom("rep");
		entitatService.updatePermis(
				entitatCreada.getId(),
				permisRepresentat);
		
		autenticarUsuari("rep");
		List<PermisDto> permisos = entitatService.findPermis(entitatCreada.getId());
		assertThat(
				permisos.size(),
				is(1));
		entitatsAccessibles = entitatService.findAccessiblesUsuariActual();
		assertThat(
				entitatsAccessibles.size(),
				is(1));
		
		autenticarUsuari("admin");
		for(PermisDto p : permisos)
			if(p.getNom().equals("rep"))
				permisRepresentat = p;
		entitatService.deletePermis(
				entitatCreada.getId(),
				permisRepresentat.getId());
		autenticarUsuari("rep");
		entitatsAccessibles = entitatService.findAccessiblesUsuariActual();
		assertThat(
				entitatsAccessibles.size(),
				is(0));
	}

	@Test(expected = DataIntegrityViolationException.class)
	public void errorSiCodiDuplicat() {
		autenticarUsuari("admin");
		entitatService.create(entitatCreate);
		entitatService.create(entitatCreate);
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminCreate() {
		autenticarUsuari("rep");
		entitatService.create(entitatCreate);
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminUpdate() {
		autenticarUsuari("rep");
		entitatService.update(entitatCreate);
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminDelete() {
		autenticarUsuari("rep");
		entitatService.delete(new Long(1));
	}

}
