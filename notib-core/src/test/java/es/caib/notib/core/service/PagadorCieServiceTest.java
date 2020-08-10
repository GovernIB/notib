package es.caib.notib.core.service;

import static org.hamcrest.CoreMatchers.is;


import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

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
import es.caib.notib.core.api.dto.PagadorCieDto;
import es.caib.notib.core.api.dto.PermisDto;

import es.caib.notib.core.api.dto.TipusDocumentDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.core.service.BaseServiceTest.TestAmbElementsCreats;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class PagadorCieServiceTest extends BaseServiceTest{

	
	
	
	@Autowired
	PermisosHelper permisosHelper;
	@Autowired
	private EntityManager entityManager;
	
	
	
	
	private PermisDto permisUser;
	private PermisDto permisAdmin;
	private EntitatDto entitatCreate;
	private EntitatDto entitatUpdate;
	private PagadorCieDto createPagadorCie;
	private PagadorCieDto updatePagadorCie;
	
	
	
	
	
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
		entitatCreate.setPermisos(Arrays.asList(permisUser));
		
		entitatUpdate = new EntitatDto();
		entitatUpdate.setId(new Long(1));
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
		
		
		entitatCreate.setPermisos(Arrays.asList(permisUser));
		
		createPagadorCie=new PagadorCieDto();
		createPagadorCie.setDir3codi("07002");
		createPagadorCie.setContracteDataVig(new Date());
		
		
		
		updatePagadorCie=new PagadorCieDto();
		updatePagadorCie.setDir3codi("07002");
		updatePagadorCie.setContracteDataVig(null);
		
			
	}
	

	@Test
	public void create() {
		
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws Exception {
					EntitatDto entitatCreate = (EntitatDto)elementsCreats.get(0);
					
					autenticarUsuari("admin");
					PagadorCieDto pagadorCreateCie=pagadorCieService.create(
							entitatCreate.getId(), createPagadorCie);
					
				
					
					assertNotNull(pagadorCreateCie);
					assertNotNull(pagadorCreateCie.getId());
					
//					comprobarPagadorCieService(
//							pagadorCreateCie,
//							createPagadorCie);
//					
					
				}


			
			}, 
			"Entitat Create", 
			
			entitatCreate);
	}
	
	
	/*
	 
	@Test
	public void update() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws NotFoundException{
					
					EntitatDto entitatCreada=(EntitatDto)elementsCreats.get(0);	
					PagadorCieDto PagadorCieCreado = (PagadorCieDto)elementsCreats.get(1);
							
					
					autenticarUsuari("admin");
					
				
					updatePagadorCie.setId(PagadorCieCreado.getId());
					PagadorCieDto PagadorCieUpdate=pagadorCieService.update(
							PagadorCieCreado);
								
					
					
					
					assertNotNull(PagadorCieUpdate);
					assertNotNull(PagadorCieUpdate.getId());
					
					assertEquals(
							PagadorCieCreado.getId(),
							createPagadorCie.getId());
					
					comprobarPagadorCieService(
							PagadorCieCreado,
							PagadorCieUpdate);
					
					assertEquals(entitatCreada.getId(), PagadorCieUpdate.getId());

					
					
		
				}
			},
			entitatCreate,
			createPagadorCie);
	}
	
	
	
	@Test
	public void delete() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws NotFoundException{
					
					EntitatDto entitatCreada=(EntitatDto)elementsCreats.get(0);
					 PagadorCieDto pagadorCieCreate=(PagadorCieDto)elementsCreats.get(1);
					 
					autenticarUsuari("admin");
							
					createPagadorCie.setId(pagadorCieCreate.getId());
					PagadorCieDto borradoCieDelete = pagadorCieService.delete(
							pagadorCieCreate.getId());
					
					comprobarPagadorCieService(
						
						createPagadorCie,
						borradoCieDelete);
					
					try{
						pagadorCieService.findById(
								pagadorCieCreate.getId());
						fail("El Pagador esborrat no s `hauria d'haver trobat");		
					}catch(NotFoundException expected) {
					}
					
					elementsCreats.remove(pagadorCieCreate);
					
					}
				},
			
			entitatCreate,
			createPagadorCie);
	
				
	}
	
	@Test
	public void findById() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats)throws NotFoundException{
					
					EntitatDto entitatCreada=(EntitatDto)elementsCreats.get(0);
					EntitatDto pagadorCieEncontrado=(EntitatDto)elementsCreats.get(1);
							
					autenticarUsuari("admin");
					
					createPagadorCie.setId(pagadorCieEncontrado.getId());
					
					PagadorCieDto encontradoCie= pagadorCieService.findById(pagadorCieEncontrado.getId());
					
					
					
					assertNotNull(encontradoCie);
					assertNotNull(encontradoCie.getId());
					
					comprobarPagadorCieService(
							createPagadorCie,
							encontradoCie);
					
					
				}
			},
			entitatCreate,
			createPagadorCie);
	
	}
	
	

	

	
	
	@Test
	public void managePermisAdmin() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) {
					PagadorCieDto creada = (PagadorCieDto)elementsCreats.get(0);
					autenticarUsuari("user");
					List<PagadorCieDto> entitatsAccessibles = pagadorCieService.findAccessiblesUsuariActual("NOT_USER");
					assertThat(
							entitatsAccessibles.size(),
							is(0));
					autenticarUsuari("super");
					List<PermisDto> permisos = permisosHelper.findPermisos(creada.getId(), EntitatEntity.class);
					assertThat(
							permisos.size(),
							is(0));
					procedimentService.permis(
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
					entitatsAccessibles = procedimentService("NOT_USER");
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
	
	
		*/
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
	
	
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminCreate() {
		autenticarUsuari("admin");
		pagadorCieService.create(entitatCreate.getId(), createPagadorCie);
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserCreate() {
		autenticarUsuari("user");
		pagadorCieService.create(entitatCreate.getId(),createPagadorCie);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplCreate() {
		autenticarUsuari("apl");
		pagadorCieService.create(entitatCreate.getId(),createPagadorCie);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminUpdate() {
		autenticarUsuari("admin");
		pagadorCieService.create(entitatCreate.getId(),createPagadorCie);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserUpdate() {
		autenticarUsuari("user");
		pagadorCieService.update(createPagadorCie);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplUpdate() {
		autenticarUsuari("apl");
		pagadorCieService.update(createPagadorCie);
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminDelete() {
		autenticarUsuari("admin");
		pagadorCieService.update(createPagadorCie);
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserDelete() {
		autenticarUsuari("user");

		pagadorCieService.delete(createPagadorCie.getId());
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplDelete() {
		autenticarUsuari("apl");
		procedimentService.delete(1L, 1L);
	}
	
	
	
	
	private void comprobarPagadorCieService(
			PagadorCieDto original,
			PagadorCieDto perComprovar) {
		assertEquals(
				original.getDir3codi(),
				perComprovar.getDir3codi());
		assertEquals(
				original.getContracteDataVig(),
				perComprovar.getContracteDataVig());
		
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



	
	
	

