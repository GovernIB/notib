package es.caib.notib.core.service;

import static org.hamcrest.CoreMatchers.is;



import static org.junit.Assert.*;

import java.sql.Date;
import java.util.Arrays;
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
import es.caib.notib.core.api.dto.PagadorPostalDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
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
public class PagadorPostalServiceTest extends BaseServiceTest{

	
	private EntitatDto entitatCreate;
	private EntitatDto entitatUpdate;
	private PermisDto permisUser;
	private PermisDto permisAdmin;
	private PagadorPostalDto crearPagadorPostal;
	private PagadorPostalDto updatePagadorPostal;
	
	
	
	
	

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
		
		entitatCreate.setPermisos(Arrays.asList(permisUser));
		
		crearPagadorPostal=new PagadorPostalDto();
		crearPagadorPostal.setDir3codi("23599771E");
	
		
		
		updatePagadorPostal=new PagadorPostalDto();
		updatePagadorPostal.setDir3codi("23599771E");
		
		

	
	}
	
	@Test
	public void create() throws NotFoundException, Exception {
		
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws Exception {
					EntitatDto entitatCreate = (EntitatDto)elementsCreats.get(0);
					
					autenticarUsuari("admin");
					PagadorPostalDto pagadorPostalCreado=pagadorPostalService.create(
							entitatCreate.getId(), crearPagadorPostal);
				
					
					assertNotNull(pagadorPostalCreado);
					assertNotNull(pagadorPostalCreado.getId());
					
					comprobarPagadorPostal(
							crearPagadorPostal,
							pagadorPostalCreado);
					assertEquals( entitatCreate.getId(), pagadorPostalCreado.getEntitat().getId());
				}
					
				

			
			}, 
			"Entitat Create", 
			entitatCreate);
	}
	
	
	
	@Test
	public void update() throws NotFoundException, Exception {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws NotFoundException{
					
					EntitatDto entitatCreada=(EntitatDto)elementsCreats.get(0);
					PagadorPostalDto pagadorPostalCreado=(PagadorPostalDto)elementsCreats.get(1);
					
					autenticarUsuari("admin");
//					PagadorPostalDto procedimentoCreado = pagadorPostalService.create(
//							entitatCreada.getId(), crearPagadorPostal);
					
					
					updatePagadorPostal.setId(pagadorPostalCreado.getId());
					PagadorPostalDto pagadorPostalUpdate=pagadorPostalService.update(pagadorPostalCreado);
							
					
					
					assertNotNull(pagadorPostalUpdate);
					assertNotNull(pagadorPostalUpdate.getId());
					
					assertEquals(entitatCreada.getId(),pagadorPostalUpdate.getEntitat().getId());
					
					
					comprobarPagadorPostal(
							crearPagadorPostal,
							pagadorPostalUpdate
							
							
							);
					assertEquals(entitatCreada.getId(), pagadorPostalCreado.getId());
					
				}
			},
			 "Pagador Actualizado",
			entitatCreate,
			crearPagadorPostal);
	}
	


	@Test
	public void delete() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws NotFoundException{
					 
					EntitatDto entitatCreada=(EntitatDto)elementsCreats.get(0);
					 PagadorPostalDto deletePagador=(PagadorPostalDto)elementsCreats.get(1);
					 
					
					autenticarUsuari("admin");
					
					
					crearPagadorPostal.setId(deletePagador.getId());
					
//					PagadorPostalDto ProcedimientoDelete= pagadorPostalService.delete(
//							deletePagador.getId());
					
							
//					crearPagadorPostal.setDir3codi(deletePagador.getDir3codi());
//					PagadorPostalDto borradoPagadorPostal = pagadorPostalService.delete(deletePagador.getId());3.
					
					
					assertNotNull(deletePagador);
					assertNotNull(deletePagador.getId());
					
					
					
					assertEquals( entitatCreada.getId(), deletePagador.getEntitat().getId());
					
					comprobarPagadorPostal(
							crearPagadorPostal,
							deletePagador);
					
					try {
						
						pagadorPostalService.findById(deletePagador.getId());
						
						fail("El Pagador postal  esborrat no s `hauria d'haver trobat");
						
					
					}catch(NotFoundException expected) {
											
						
						}
					
					elementsCreats.remove(deletePagador);

					}	
				},
				
				"Delete pagadorPOstal",
			entitatCreate,
			crearPagadorPostal);
	}
				

	
	@Test
	public void findById() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats)throws NotFoundException{
					
					EntitatDto entitatCreada=(EntitatDto)elementsCreats.get(0);
					PagadorPostalDto encontrarPagador=(PagadorPostalDto)elementsCreats.get(1);
							
					autenticarUsuari("admin");
					
					PagadorPostalDto  encontrarPorId= pagadorPostalService.create(entitatCreada.getId(), crearPagadorPostal);
					
					crearPagadorPostal.setId(encontrarPorId.getId());
					 
					PagadorPostalDto encontradoPagadorPostal= pagadorPostalService.findById(crearPagadorPostal.getId());
					
					
					
					assertNotNull(encontradoPagadorPostal);
					assertNotNull(encontradoPagadorPostal.getId());
					
					assertEquals(encontrarPorId.getId(),encontradoPagadorPostal.getId());
					
				}
			},
			
			entitatCreate,
			crearPagadorPostal);
	
	}
	



	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserCreate() {
		autenticarUsuari("user");
		pagadorPostalService.create(entitatCreate.getId(),crearPagadorPostal);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplCreate() {
		autenticarUsuari("apl");
		pagadorPostalService.create(entitatCreate.getId(),crearPagadorPostal);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserUpdate() {
		autenticarUsuari("user");
		pagadorPostalService.update(crearPagadorPostal);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplUpdate() {
		autenticarUsuari("apl");
		pagadorPostalService.update(crearPagadorPostal);
	}
	

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserDelete() {
		autenticarUsuari("user");
		pagadorPostalService.delete(crearPagadorPostal.getId());
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplDelete() {
		autenticarUsuari("apl");
		pagadorPostalService.delete(crearPagadorPostal.getId());
	}


	
	
	
	private void comprobarPagadorPostal(
			PagadorPostalDto original,
			PagadorPostalDto perComprovar) {

		assertEquals(
				original.getDir3codi(),
				perComprovar.getDir3codi());
	

				
	}
	

	

	
}

