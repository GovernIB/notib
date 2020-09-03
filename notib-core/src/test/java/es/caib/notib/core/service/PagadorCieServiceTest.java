package es.caib.notib.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Date;
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
import es.caib.notib.core.api.dto.PagadorCieDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.TipusDocumentDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.helper.PermisosHelper;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class PagadorCieServiceTest extends BaseServiceTest{

	@Autowired
	PermisosHelper permisosHelper;
//	@Autowired
//	private EntityManager entityManager;
	
	private PermisDto permisAdmin;
	private EntitatDto entitatCreate;
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
		
		permisAdmin = new PermisDto();
		permisAdmin.setAdministration(true);
		permisAdmin.setAdministradorEntitat(true);
		permisAdmin.setTipus(TipusEnumDto.USUARI);
		permisAdmin.setPrincipal("admin");
		entitatCreate.setPermisos(Arrays.asList(permisAdmin));
		
		createPagadorCie=new PagadorCieDto();
		createPagadorCie.setDir3codi("A04027005");
		createPagadorCie.setContracteDataVig(new Date());
		
		updatePagadorCie=new PagadorCieDto();
		updatePagadorCie.setDir3codi("A04026968");
		updatePagadorCie.setContracteDataVig(new Date());
	}
	

	@Test
	public void create() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws Exception {
					EntitatDto entitatCreate = (EntitatDto)elementsCreats.get(0);
					PagadorCieDto pagadorCreateCie=(PagadorCieDto)elementsCreats.get(1);
					autenticarUsuari("admin");
					assertNotNull(pagadorCreateCie);
					assertNotNull(pagadorCreateCie.getId());
					
					compararPagadorCie(
							createPagadorCie,
							pagadorCreateCie);
					assertEquals(entitatCreate.getId(), pagadorCreateCie.getEntitatId());
				}
			}, 
			"Create PAGADOR CIE", 
			entitatCreate,
			createPagadorCie);
	}
	
	
	@Test
	public void update() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws NotFoundException{
					EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);	
					PagadorCieDto pagadorCieCreat = (PagadorCieDto)elementsCreats.get(1);
					autenticarUsuari("admin");
				
					updatePagadorCie.setId(pagadorCieCreat.getId());
					PagadorCieDto pagadorCieModificat = pagadorCieService.update(updatePagadorCie);
					
					assertNotNull(pagadorCieModificat);
					assertNotNull(pagadorCieModificat.getId());
					
					assertEquals(
							pagadorCieCreat.getId(),
							pagadorCieModificat.getId());
					compararPagadorCie(
							updatePagadorCie,
							pagadorCieModificat);
					assertEquals(entitatCreada.getId(), pagadorCieModificat.getEntitatId());
				}
			},
			"Update PAGADOR CIE",
			entitatCreate,
			createPagadorCie);
	}
	
	
	@Test
	public void delete() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws NotFoundException{
					PagadorCieDto pagadorCieCreat = (PagadorCieDto)elementsCreats.get(1);
					autenticarUsuari("admin");
							
					PagadorCieDto esborrada = pagadorCieService.delete(pagadorCieCreat.getId());
					compararPagadorCie(
							createPagadorCie,
							esborrada);
					try{
						pagadorCieService.findById(pagadorCieCreat.getId());
						fail("El Pagador esborrat no s'hauria d'haver trobat");		
					}catch(NotFoundException expected) {
					}
					elementsCreats.remove(pagadorCieCreat);
				}
			},
			"Delete PAGADOR CIE",
			entitatCreate,
			createPagadorCie);
	}
	
	@Test
	public void findById() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats)throws NotFoundException{
					PagadorCieDto pagadorCieCreat = (PagadorCieDto)elementsCreats.get(1);
					autenticarUsuari("admin");
					
					PagadorCieDto trobat= pagadorCieService.findById(
							pagadorCieCreat.getId());
					
					assertNotNull(trobat);
					assertNotNull(trobat.getId());
					compararPagadorCie(
							createPagadorCie,
							trobat);
				}
			},
			"FindById PAGADOR CIE",
			entitatCreate,
			createPagadorCie);
	
	}
	
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplCreate() {
		autenticarUsuari("apl");
		pagadorCieService.create(1L, createPagadorCie);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplUpdate() {
		autenticarUsuari("apl");
		pagadorCieService.update(createPagadorCie);
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplDelete() {
		autenticarUsuari("apl");
		pagadorCieService.delete(1L);
	}
	
	private void compararPagadorCie(
			PagadorCieDto original,
			PagadorCieDto perComprovar) {
		assertEquals(
				original.getDir3codi(),
				perComprovar.getDir3codi());
	}
	
}



	
	
	

