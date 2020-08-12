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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.EntitatTipusEnumDto;
import es.caib.notib.core.api.dto.PagadorCieDto;
import es.caib.notib.core.api.dto.PagadorCieFormatFullaDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.TipusDocumentDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.helper.PermisosHelper;




@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional

public class PagadorCieFormatFullaServiceTest extends BaseServiceTest{

	private EntitatDto entitatCreate;
	private EntitatDto entitatUpdate;
	private PermisDto permisUser;
	private PermisDto permisAdmin;
	private PagadorCieDto createPagadorCie;
	private PagadorCieFormatFullaDto createPagadorCieFormatFulla;
	private PagadorCieFormatFullaDto updatePagadorCieFormatFulla;
	
	
	 
	
	

	@Autowired
	PermisosHelper permisosHelper;
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
		createPagadorCie.setDir3codi("12345");
		createPagadorCie.setContracteDataVig(new Date());
		
		createPagadorCieFormatFulla=new PagadorCieFormatFullaDto();
		createPagadorCieFormatFulla.setCodi("122");
		
	
		
		updatePagadorCieFormatFulla=new PagadorCieFormatFullaDto();
		updatePagadorCieFormatFulla.setCodi("12333");
		
		
		

		
		
	}
	@Test
	public void create() {
		
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws Exception {
					PagadorCieDto createPagadorCie = (PagadorCieDto)elementsCreats.get(1);
					PagadorCieFormatFullaDto createPagadorFormatFulla = (PagadorCieFormatFullaDto)elementsCreats.get(2);
					
					
					autenticarUsuari("admin");
//					PagadorCieFormatFullaDto PagadorCieCreat= pagadorCieFormatFullaService.create(
//							createPagadorCieFormatFulla.getId(), createPagador);
					
					createPagadorCieFormatFulla.setId(createPagadorFormatFulla.getId());
					
					assertNotNull(createPagadorFormatFulla);
					assertNotNull(createPagadorFormatFulla.getId());
					
					comprobarPagadorCieFormatFulla(
							createPagadorCieFormatFulla,
							createPagadorFormatFulla);
					
					assertEquals( createPagadorCie.getId(), createPagadorFormatFulla.getId());
				}

			
			}, 
			"Procediment Create", 
			entitatCreate,
			createPagadorCie,
			createPagadorCieFormatFulla);
	}
	
	
	
	@Test
	public void update() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws NotFoundException{
					
					
					PagadorCieDto updatePagadorCie = (PagadorCieDto)elementsCreats.get(1);
					PagadorCieFormatFullaDto updatePagadorFormatFulla = (PagadorCieFormatFullaDto)elementsCreats.get(2);
					
					
					
						
					autenticarUsuari("admin");
//						PagadorCieFormatFullaDto pagadorCierCreat = pagadorCieFormatFullaService
//								.create(entitatCreada.getId(), createPagador);
					
					
					
								
					assertNotNull(updatePagadorFormatFulla);
					assertNotNull(updatePagadorFormatFulla.getId());
					
					
					assertEquals( updatePagadorCie.getId(), updatePagadorFormatFulla.getId());
					
					
					comprobarPagadorCieFormatFulla(
							updatePagadorCieFormatFulla,
							updatePagadorFormatFulla);
					
				
				}
			},
			
			entitatCreate,
			createPagadorCie,
			updatePagadorCieFormatFulla);
	}
	
	@Test
	public void delete() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws NotFoundException{
					
					PagadorCieDto deletePagadorCie = (PagadorCieDto)elementsCreats.get(1);
					PagadorCieFormatFullaDto deletePagadorFormatFulla = (PagadorCieFormatFullaDto)elementsCreats.get(2);
					 
					autenticarUsuari("admin");
//					PagadorCieFormatFullaDto deletePagadorCie1 = 
//							pagadorCieFormatFullaService.create(entitatCreada.getId(), deletePagadorCie);
					
							
					createPagadorCieFormatFulla.setId(deletePagadorFormatFulla.getId());
//					PagadorCieFormatFullaDto borradoPagadorCie = pagadorCieFormatFullaService.delete(deletePagadorCie.getId());
					
					assertNotNull(deletePagadorFormatFulla);
					assertNotNull(deletePagadorFormatFulla.getId());
					
					assertEquals( deletePagadorCie.getId(), deletePagadorFormatFulla.getId());
					
					comprobarPagadorCieFormatFulla(
							
							createPagadorCieFormatFulla,
							deletePagadorFormatFulla
							
							);
					
					
					try {						
						pagadorCieFormatSobreService.findById(
								deletePagadorFormatFulla.getId());
						
						fail("El PagadorCieSobre esborrat no s `hauria d'haver trobat");												
						}catch(NotFoundException expected) {
						
						}
					
					elementsCreats.remove(deletePagadorFormatFulla);
				}
				},
			entitatCreate,
			createPagadorCie,
			createPagadorCieFormatFulla);
	}
				

	
	@Test
	public void findById() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats)throws NotFoundException{
					
					PagadorCieDto createPagadorCie = (PagadorCieDto)elementsCreats.get(1);
					PagadorCieFormatFullaDto deletePagadorFormatFulla = (PagadorCieFormatFullaDto)elementsCreats.get(2);
							
					autenticarUsuari("admin");
					
//					PagadorCieFormatFullaDto  encontrarPorId= pagadorCieFormatFullaService.create(createPagador.getId(),createPagador);
					
					createPagadorCieFormatFulla.setId(deletePagadorFormatFulla.getId());
					
//					PagadorCieFormatFullaDto encontradoPagadorCie= pagadorCieFormatFullaService.findById(findByIdPagadorCie.getId());
					
					
					assertNotNull(deletePagadorFormatFulla);
					assertNotNull(deletePagadorFormatFulla.getId());
					
					assertEquals(createPagadorCie.getId(), deletePagadorFormatFulla.getId());
					
					comprobarPagadorCieFormatFulla(
							createPagadorCieFormatFulla,
							deletePagadorFormatFulla);
				}
			},
			
			entitatCreate,
			createPagadorCie,
			createPagadorCieFormatFulla);
	
	}
	
		


	private void comprobarPagadorCieFormatFulla(
			PagadorCieFormatFullaDto original,
			PagadorCieFormatFullaDto perComprovar) {

		assertEquals(
				original.getCodi(),
				perComprovar.getCodi());
	
		
	}

}

