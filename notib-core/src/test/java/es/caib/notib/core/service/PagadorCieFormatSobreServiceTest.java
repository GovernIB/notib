package es.caib.notib.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import es.caib.notib.core.api.dto.PagadorCieDto;
import es.caib.notib.core.api.dto.PagadorCieFormatSobreDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.TipusDocumentDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.helper.PermisosHelper;




@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class PagadorCieFormatSobreServiceTest extends BaseServiceTest{

	private EntitatDto entitatCreate;
	private EntitatDto entitatUpdate;
	private PermisDto permisUser;
	private PermisDto permisAdmin;
	private PagadorCieDto createPagadorCie;
	private PagadorCieFormatSobreDto createPagadorCieFormatSobre;
	private PagadorCieFormatSobreDto updatePagadorCieFormatSobre;
	
	 
	
	

	@Autowired
	PermisosHelper permisosHelper;
	@Autowired
	private EntityManager entityManager;
	
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
		
				
		List<PermisDto> permisos = new ArrayList<PermisDto>();
		permisos.add(permisAdmin);
		entitatCreate.setPermisos(permisos);
		
		
		createPagadorCie=new PagadorCieDto();
		createPagadorCie.setDir3codi("12345");
		createPagadorCie.setContracteDataVig(new Date());
		
		createPagadorCieFormatSobre= new PagadorCieFormatSobreDto();
		createPagadorCieFormatSobre.setCodi("12345");
		//createPagadorCieFormatSobre.setPagadorCieId(createPagadorCieFormatSobre.getId());
		
		
		updatePagadorCieFormatSobre= new PagadorCieFormatSobreDto();
		updatePagadorCieFormatSobre.setCodi("23456");
		//updatePagadorCieFormatSobre.setPagadorCieId(createPagadorCieFormatSobre.getId());
		
		

		
		
	}
	@Test
	public void create() {
		
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats){
					EntitatDto entitatCreate = (EntitatDto)elementsCreats.get(0);
					PagadorCieDto pagadorCieCreate = (PagadorCieDto)elementsCreats.get(1);
					PagadorCieFormatSobreDto pagadorCieFormatSobreCreat = (PagadorCieFormatSobreDto)elementsCreats.get(2);
					
					autenticarUsuari("admin");
//					PagadorCieFormatSobreDto PagadorCieFormatSobreCreat= pagadorCieFormatSobreService.create(
//					pagadorCieCreate.getId(),
//					createPagadorCieFormatSobre);
					
					createPagadorCieFormatSobre.setId(pagadorCieFormatSobreCreat.getId());
					
					assertNotNull(pagadorCieFormatSobreCreat);
					assertNotNull(pagadorCieFormatSobreCreat.getId());
					
					comprobarPagadorCieFormatSobre(
							createPagadorCieFormatSobre,
							pagadorCieFormatSobreCreat);
					assertEquals(pagadorCieCreate.getId(), pagadorCieFormatSobreCreat.getPagadorCieId());
				}

			
			}, 
			"createPagadorCieFormatSobre Create", 
			entitatCreate,
			createPagadorCie,
			createPagadorCieFormatSobre);
	}
	
	
	@Test
	public void update() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws NotFoundException{
					
					EntitatDto entitatCreada=(EntitatDto)elementsCreats.get(0);
					PagadorCieDto pagadorCieUpdtate = (PagadorCieDto)elementsCreats.get(1);
					PagadorCieFormatSobreDto pagadorCieFormatSobreServiceUpdate=(PagadorCieFormatSobreDto)elementsCreats.get(2);
					
					autenticarUsuari("admin");
					
					createPagadorCieFormatSobre.setCodi(pagadorCieFormatSobreServiceUpdate.getCodi());
//					PagadorCieFormatSobreDto pagadorCieFormatSobreCreat = pagadorCieFormatSobreService.create(
//							createPagadorCieFormatSobre.getId(), createPagadorCieFormatSobre);
					
					
//					updatePagadorCieFormatSobre.setId(pagadorCieFormatSobreServiceCreado.getId());
//					PagadorCieFormatSobreDto PagadorCieFormatSobre=pagadorCieFormatSobreService.update(pagadorCieFormatSobreServiceCreado);

					
					assertNotNull(pagadorCieFormatSobreServiceUpdate);
					assertNotNull(pagadorCieFormatSobreServiceUpdate.getId());
					
					comprobarPagadorCieFormatSobre(
							createPagadorCieFormatSobre,
							pagadorCieFormatSobreServiceUpdate);
					
					assertEquals( pagadorCieUpdtate.getId(), pagadorCieFormatSobreServiceUpdate.getPagadorCieId());
				
					

				}
			},
			entitatCreate,
			createPagadorCie,
			updatePagadorCieFormatSobre);
 }


	@Test
	public void delete() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws NotFoundException{
					 EntitatDto entitatCreada=(EntitatDto)elementsCreats.get(0);
					 PagadorCieDto deletePagadorCie=(PagadorCieDto)elementsCreats.get(1);
					 PagadorCieFormatSobreDto pagadorCieFormatSobreBorrar=(PagadorCieFormatSobreDto)elementsCreats.get(2);
					 
					autenticarUsuari("admin");
//					PagadorCieFormatSobreDto deletePagadorCieFormatSobre1 = 
//							pagadorCieFormatSobreService.create(deletePagadorCieFormatSobre.getId(),deletePagadorCieFormatSobre);
					
							
					createPagadorCieFormatSobre.setId(pagadorCieFormatSobreBorrar.getId());
					
					assertNotNull(pagadorCieFormatSobreBorrar);
					assertNotNull(pagadorCieFormatSobreBorrar.getId());
					
					assertEquals( deletePagadorCie.getId(), pagadorCieFormatSobreBorrar.getId());
					
					comprobarPagadorCieFormatSobre(
							createPagadorCieFormatSobre,
							pagadorCieFormatSobreBorrar);
					
					try {						
						pagadorCieFormatSobreService.findById(
								pagadorCieFormatSobreBorrar.getId());
						
						fail("El PagadorCieSobre esborrat no s `hauria d'haver trobat");												
						}catch(NotFoundException expected) {
						
						}
					
					elementsCreats.remove(pagadorCieFormatSobreBorrar);
					
	

					
					}
				},
			entitatCreate,
			createPagadorCie,
			createPagadorCieFormatSobre);
	}
				

	
	@Test
	public void findById() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats)throws NotFoundException{
							
					 EntitatDto entitatCreada=(EntitatDto)elementsCreats.get(0);
					 PagadorCieDto pagadorCieSobreFindById=(PagadorCieDto)elementsCreats.get(1);
					 PagadorCieFormatSobreDto pagadorCieFormatSobreFinById=(PagadorCieFormatSobreDto)elementsCreats.get(2);
					 
					autenticarUsuari("admin");
					
//					PagadorCieFormatSobreDto  encontrarCieFormatPorId= pagadorCieFormatSobreService.create(findByIdPagadorCieFormatSobre.getId(),findByIdPagadorCieFormatSobre);
					
					createPagadorCieFormatSobre.setId(pagadorCieFormatSobreFinById.getId());
					
//					PagadorCieFormatFullaDto encontradoPagadorCie= pagadorCieFormatFullaService.findById(encontrarCieFormatPorId.getId());
					
					
					
					assertNotNull(pagadorCieFormatSobreFinById);
					assertNotNull(pagadorCieFormatSobreFinById.getId());
					
					assertEquals(pagadorCieSobreFindById.getId(), pagadorCieFormatSobreFinById.getId());
				}
			},
			
			entitatCreate,
			createPagadorCie,
			createPagadorCieFormatSobre
			
			
			);
	
	}
	
	
	
	
	
	
	private void comprobarPagadorCieFormatSobre(
			PagadorCieFormatSobreDto original,
			PagadorCieFormatSobreDto perComprovar) {
		assertEquals(
				original.getCodi(),
				perComprovar.getCodi());
	}

	
}
	

