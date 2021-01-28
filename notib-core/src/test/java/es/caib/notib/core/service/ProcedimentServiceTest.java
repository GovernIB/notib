package es.caib.notib.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.EntitatTipusEnumDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.TipusDocumentDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.plugin.SistemaExternException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class ProcedimentServiceTest extends BaseServiceTest{
	
	

	@Autowired
	PermisosHelper permisosHelper;
	
	private PermisDto permisAdmin;
	private EntitatDto entitatCreate;
	private ProcedimentDto createProcediment;
	private ProcedimentDto updateProcediment;
	
	
		
	@Before
	public void setUp() throws SistemaExternException {
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
		
		permisAdmin = new PermisDto();
		permisAdmin.setAdministration(true);
		permisAdmin.setAdministradorEntitat(true);
		permisAdmin.setTipus(TipusEnumDto.USUARI);
		permisAdmin.setPrincipal("admin");

		entitatCreate.setPermisos(Arrays.asList(permisAdmin));
		
		createProcediment= new ProcedimentDto();
		createProcediment.setCodi("123456789");
		createProcediment.setNom("Procedimiento 1");
		createProcediment.setOrganGestor("A00000000");
		
		updateProcediment= new ProcedimentDto();
		updateProcediment.setCodi("234567890");
		updateProcediment.setNom("Procedimiento 2");
		updateProcediment.setOrganGestor("A00000001");
		
		configureMockUnitatsOrganitzativesPlugin();
		
	}
	

	
	@Test
	public void create() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws Exception {
					EntitatDto entitatCreate = (EntitatDto)elementsCreats.get(0);
					ProcedimentDto procedimentCreat = (ProcedimentDto)elementsCreats.get(1);
			
					autenticarUsuari("admin");
					assertNotNull(procedimentCreat);
					assertNotNull(procedimentCreat.getId());
					comprovarProcedimentCoincideix(
							procedimentCreat,
							createProcediment);
					assertEquals(entitatCreate.getId(), procedimentCreat.getEntitat().getId());
				}
			}, 
			"Create PROCEDIMENT", 
			entitatCreate,
			createProcediment);
	}
	
	@Test
	public void update() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws NotFoundException{
					EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);	
					ProcedimentDto procedimentCreat = (ProcedimentDto)elementsCreats.get(1);
					autenticarUsuari("admin");
					updateProcediment.setId(procedimentCreat.getId());
					ProcedimentDto modificat = procedimentService.update(
							entitatCreada.getId(), 
							updateProcediment, 
							true,
							true);
					
					assertNotNull(modificat);
					assertNotNull(modificat.getId());
					assertEquals(
							procedimentCreat.getId(),
							modificat.getId());
					comprovarProcedimentCoincideix(
							updateProcediment,
							modificat);
					
					assertEquals(entitatCreada.getId(), modificat.getEntitat().getId());
				}
			},
			"UPdate PROCEDIMENT",
			entitatCreate,
			createProcediment);
	}
	
	@Test
	public void delete() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws NotFoundException{
					 EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
					 ProcedimentDto procedimentCreat = (ProcedimentDto)elementsCreats.get(1);
					 autenticarUsuari("admin");
					 ProcedimentDto borrat = procedimentService.delete(
							entitatCreada.getId(), 
							procedimentCreat.getId(),
							true);
					
					comprovarProcedimentCoincideix(
							createProcediment,
							borrat);
					
					try {						
						procedimentService.findById(
								entitatCreada.getId(),
								true,
								procedimentCreat.getId());
						fail("El procediment esborrat no s'hauria d'haver trobat");												
					}catch(NotFoundException expected) {
					}catch (Exception ex) {
						ex.printStackTrace();
					}
					elementsCreats.remove(procedimentCreat);
				}
			},
			"Delete PROCEDIMENT",
			entitatCreate,
			createProcediment);
	}
				
	@Test
	public void findById() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats)throws NotFoundException{
					EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
					ProcedimentDto procedimentCreat = (ProcedimentDto)elementsCreats.get(1);
					autenticarUsuari("admin");
					
					ProcedimentDto trobat = procedimentService.findById(
							entitatCreada.getId(), 
							true,
							procedimentCreat.getId() );
							
					assertNotNull(trobat);
					assertNotNull(trobat.getId());
					comprovarProcedimentCoincideix(
							createProcediment,
							trobat);
					
				}
			},
			"FindById PROCEDIMENT",
			entitatCreate,
			createProcediment);
	
	}
	
	
	
	
	@Test
	public void findByCodi() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats)throws NotFoundException{
					EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
					autenticarUsuari("admin");
					
					ProcedimentDto trobat = procedimentService.findByCodi(
							entitatCreada.getId(), 
							createProcediment.getCodi());
					assertNotNull(trobat);
					assertNotNull(trobat.getId());
					comprovarProcedimentCoincideix(
							createProcediment,
							trobat);
				}
			},
			"FindByCodi PROCEDIMENT",
			entitatCreate,
			createProcediment
			);
	}
	
	/*
	
	@Test
	public void managePermisAdmin() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) {
					EntitatDto creada = (EntitatDto)elementsCreats.get(0);
					autenticarUsuari("user");
					List<EntitatDto> entitatsAccessibles = procedimentService.findAccessiblesUsuariActual("NOT_ADMIN");
					assertThat(
							entitatsAccessibles.size(),
							is(0));
					autenticarUsuari("super");
					List<PermisDto> permisos = permisosHelper.findPermisos(creada.getId(), EntitatEntity.class);
					assertThat(
							permisos.size(),
							is(0));
					procedimentService.permisUpdate(
							creada.getId(), 
							createProcediment.getId(), 
							permisUser, false);
							
					permisos = permisosHelper.findPermisos(creada.getId(), EntitatEntity.class);
					assertThat(
							permisos.size(),
							is(1));
					comprovarPermisCoincideix(
							permisUser,
							permisos.get(0));
					autenticarUsuari("user");
					entitatsAccessibles = entitatService.findAccessiblesUsuariActual("tothom");
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
					entitatsAccessibles = entitatService.findAccessiblesUsuariActual("tothom");
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
					entitatsAccessibles = entitatService.findAccessiblesUsuariActual("tothom");
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
					entitatsAccessibles = entitatService.findAccessiblesUsuariActual("tothom");
					assertThat(
							entitatsAccessibles.size(),
							is(0));
				}
			},
			entitatCreate);
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
	
	 */
	
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesSuperCreate() {
		autenticarUsuari("super");
		procedimentService.create(entitatCreate.getId(),createProcediment);
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplCreate() {
		autenticarUsuari("apl");
		procedimentService.create(entitatCreate.getId(),createProcediment);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplUpdate() {
		autenticarUsuari("apl");
		procedimentService.update(entitatCreate.getId(),createProcediment,false,false);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesSuperUpdate() {
		autenticarUsuari("super");
		procedimentService.update(entitatCreate.getId(),createProcediment,false,false);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplDelete() {
		autenticarUsuari("apl");
		procedimentService.delete(entitatCreate.getId(),createProcediment.getId(),false);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesSuperDelete() {
		autenticarUsuari("super");
		procedimentService.delete(entitatCreate.getId(),createProcediment.getId(),false);
	}
	
	private void comprovarProcedimentCoincideix(
			ProcedimentDto original,
			ProcedimentDto perComprovar) {
		assertEquals(
				original.getCodi(),
				perComprovar.getCodi());
		assertEquals(
				original.getNom(),
				perComprovar.getNom());
		assertEquals(
				original.getOrganGestor(),
				perComprovar.getOrganGestor());
		assertEquals(
				original.getCaducitat(),
				perComprovar.getCaducitat());
		assertEquals(
				original.getPagadorpostal(),
				perComprovar.getPagadorpostal());
	}
	
}


