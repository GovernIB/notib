package es.caib.notib.core.service;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.helper.PermisosHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class GrupServiceTest extends BaseServiceTest{
	
	@Autowired
	PermisosHelper permisosHelper;
	
	private EntitatDto entitatCreate;
	private PermisDto permisAdmin;
	private GrupDto grupCreate;
	private GrupDto grupUpdate;
	
	
	@Before
	public void setUp() {
		addConfig("es.caib.notib.metriques.generar", "false");
		entitatCreate = new EntitatDto();
		entitatCreate.setCodi("LIMIT");
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
		TipusDocumentDto tipusDocDefault2 = new TipusDocumentDto();
		tipusDocDefault2.setTipusDocEnum(TipusDocumentEnumDto.CSV);
		
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
					GrupDto grupCreat = (GrupDto)elementsCreats.get(1);
					
					assertNotNull(grupCreat);
					assertNotNull(grupCreat.getId());
					
					comprobarGroup(
							grupCreate,
							grupCreat);
					assertEquals(entitatCreada.getId(), grupCreat.getEntitatId());
				}
				
			}, 
			"Alta de GRUP", 
			entitatCreate,
			grupCreate);
		
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
			"Modificación de GRUP",
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
		"Delete  GRUP",
		entitatCreate,
		grupCreate);
	}
			
	
	@Test
	public void findById() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats)throws NotFoundException{
					autenticarUsuari("admin");
					EntitatDto entitatCreada=(EntitatDto)elementsCreats.get(0);
					GrupDto grupCreat = (GrupDto)elementsCreats.get(1);
					
					GrupDto trobat = grupService.findById(
							entitatCreada.getId(), 
							grupCreat.getId());

					assertNotNull(trobat);
					assertNotNull(trobat.getId());
					comprobarGroup(
							grupCreate,
							trobat);
				}
			},
			"FindById GRUP",
			entitatCreate,
			grupCreate);
	}

	
	@Test
	public void findByCodi() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) {
					autenticarUsuari("admin");
					EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
					GrupDto grupCreat = (GrupDto)elementsCreats.get(1);
				
					GrupDto trobat = grupService.findByCodi(
							grupCreat.getCodi(), 
							entitatCreada.getId());
					
					assertNotNull(trobat);
					assertNotNull(trobat.getId());
					comprobarGroup(
							grupCreate,
							trobat);
				}
			},
			"findByCodi GRUP",
			entitatCreate,
			grupCreate);
	}
	
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplCreate() {
		autenticarUsuari("Apl");
		grupService.create(entitatCreate.getId(),grupCreate);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesSuperCreate() {
		autenticarUsuari("super");
		grupService.create(entitatCreate.getId(),grupCreate);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplUpdate() {
		autenticarUsuari("apl");
		grupService.update(grupUpdate);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesSuserUpdate() {
		autenticarUsuari("super");
		grupService.update(grupUpdate); 
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesSuperDelete() {
		autenticarUsuari("super");
		grupService.delete(grupCreate.getId()); 
	}
	
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplDelete() {
		autenticarUsuari("apl");
		grupService.delete(grupCreate.getId()); 
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesSuperFindById() {
		autenticarUsuari("super");
		grupService.findById(entitatCreate.getId(), grupCreate.getId()); 
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


