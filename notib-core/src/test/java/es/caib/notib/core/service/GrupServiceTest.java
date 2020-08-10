package es.caib.notib.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.EntitatTipusEnumDto;
import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.TipusDocumentDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.helper.PermisosHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class GrupServiceTest extends BaseServiceTest{
	
	@Autowired
	PermisosHelper permisosHelper;
	@Autowired
	private EntityManager entityManager;

	
	private EntitatDto entitatCreate;
	private EntitatDto entitatUpdate;
	private PermisDto permisUser;
	private PermisDto permisAdmin;
	private GrupDto grupCreate;
	private GrupDto grupUpdate;
	
	
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
		
		List<PermisDto> permisos = new ArrayList<PermisDto>();
		permisos.add(permisAdmin);
		entitatCreate.setPermisos(permisos);
		
		grupCreate = new GrupDto();
		grupCreate.setCodi("Rol_1");
		grupCreate.setNom("Grupo1");
		
		grupUpdate = new GrupDto();
		grupUpdate.setCodi("Rol_2");
		grupUpdate.setNom("Grupo2");
		
	}

	
	
	
	@Test
    public void create() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws Exception {
					EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
					//GrupDto grupCreat = (GrupDto)elementsCreats.get(1);
					
					autenticarUsuari("admin");
					GrupDto grupCreat = grupService.create(
							entitatCreada.getId(),
							grupCreate);
					
					assertNotNull(grupCreat);
					assertNotNull(grupCreat.getId());
					
					comprobarGroup(
							grupCreate,
							grupCreat);
					assertEquals(entitatCreada.getId(), grupCreat.getEntitatId());
				}
			}, 
			"Alta de ENTITAT", 
			entitatCreate);
			//grupCreate);
		
	}
	
	@Test
	public void update() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws NotFoundException{
					EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
					GrupDto grupCreat = (GrupDto)elementsCreats.get(1);
					autenticarUsuari("admin");
					
					grupUpdate.setId(grupCreat.getId());
					GrupDto grupModificat = grupService.update(grupUpdate);
					
					assertNotNull(grupModificat);
					assertNotNull(grupModificat.getId());
					assertEquals(
							grupCreat.getId(),
							grupModificat.getId());
					
					comprobarGroup(
							grupUpdate,
							grupModificat);
					assertEquals(entitatCreada.getId(), grupModificat.getEntitatId());
				}
			},
			"Modificación de ENTITAT",
			entitatCreate,	// elementsCreats.get(0)
			grupCreate);	// elementsCreats.get(1)
	}
	
	
	@Test
	public void delete() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws NotFoundException{
					EntitatDto entitatCreada=(EntitatDto)elementsCreats.get(0);
					GrupDto grupCreat =(GrupDto)elementsCreats.get(1);
					autenticarUsuari("admin");
																
					GrupDto esborrada = grupService.delete(grupCreat.getId());
					comprobarGroup(
							grupCreate,
							esborrada);
					try {
						grupService.findById(
								entitatCreada.getId(), 
								grupCreat.getId());
						fail("El grup esborrat no s'hauria d'haver trobat");
					} catch (NotFoundException expected) {
					}
					elementsCreats.remove(grupCreat);
					
				}
			},
		"Delete  ENTITAT",
		entitatCreate,
		grupCreate);
	}
			
	
	
	@Test
	public void findById() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats)throws NotFoundException{
					
//					autenticarUsuari("super");
//					EntitatDto entitatCreada=(EntitatDto)elementsCreats.get(0);
//					GrupDto trobada = grupService.findById(entitatCreada.getId(),grupCreate.getId()());
					
					
					EntitatDto entitatCreada=(EntitatDto)elementsCreats.get(0);
					GrupDto encontrarGrupPorId=(GrupDto)elementsCreats.get(1);
					
					autenticarUsuari("admin");
//					GrupDto  encontrarGrupPorId= grupService.create(entitatCreada.getId(),grupCreate);
					
					grupCreate.setId(encontrarGrupPorId.getId());
					
					GrupDto grupEncontrado= grupService.findById(
							entitatCreada.getId(), 
							encontrarGrupPorId.getId());
					
					comprobarGroup(
							grupCreate,
							grupEncontrado);
					
					assertNotNull(grupEncontrado);
					assertNotNull(grupEncontrado.getId());
					
							
				}
			},
			"FindById  ENTITAT",
			entitatCreate,
			grupCreate);
	
	}
	
	
	
	
	@Test
	public void findByCodi() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) {
					
					

					
				EntitatDto entitatCreada=(EntitatDto)elementsCreats.get(0);
				GrupDto encontradoPorCode=(GrupDto)elementsCreats.get(1);
				
					autenticarUsuari("admin");
//					GrupDto entontradaCode = grupService.create(entitatCreada.getId(),grupCreate);
					grupCreate.setCodi(encontradoPorCode.getCodi());
		
				
				GrupDto CodeEncontrado= grupService.findByCodi(encontradoPorCode.getCodi(), entitatCreada.getId());
					
				
					
					assertNotNull(CodeEncontrado);
					assertNotNull(CodeEncontrado.getId());
				
					
					
					
					
				}
					
			},
			
			entitatCreate,
			grupCreate);
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

	
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminCreate() {
		autenticarUsuari("admin");
		grupService.create(entitatCreate.getId(), grupCreate);
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserCreate() {
		autenticarUsuari("user");
		grupService.create(entitatCreate.getId(),grupCreate);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplCreate() {
		autenticarUsuari("apl");
		grupService.create(entitatCreate.getId(),grupCreate);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminUpdate() {
		autenticarUsuari("admin");
		grupService.create(entitatCreate.getId(),grupCreate);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserUpdate() {
		autenticarUsuari("user");
		grupService.update(grupUpdate); 
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplUpdate() {
		autenticarUsuari("apl");
		grupService.update(grupUpdate);
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminDelete() {
		autenticarUsuari("admin");
		grupService.update(grupUpdate);
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserDelete() {
		autenticarUsuari("user");
		grupService.delete(grupCreate.getId());
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplDelete() {
		autenticarUsuari("apl");
		grupService.delete(grupCreate.getId());
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
	

	
	
	private void comprobarGroup(
			GrupDto original,
			GrupDto perComprovar) {
		assertEquals(
				original.getCodi(),
				perComprovar.getCodi());
		assertEquals(
				original.getNom(),
				perComprovar.getNom());
	}
	

}
