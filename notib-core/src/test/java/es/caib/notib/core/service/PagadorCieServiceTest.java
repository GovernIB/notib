package es.caib.notib.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

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
		updatePagadorCie.setDir3codi("0700333");
		updatePagadorCie.setContracteDataVig(new Date());
		
			
	}
	

	@Test
	public void create() {
		
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws Exception {
					EntitatDto entitatCreate = (EntitatDto)elementsCreats.get(0);
					
					autenticarUsuari("admin");
					PagadorCieDto pagadorCreateCie=(PagadorCieDto)elementsCreats.get(1);
							// pagadorCieService.create(entitatCreate.getId(), createPagadorCie);
					
				
					
					assertNotNull(pagadorCreateCie);
					assertNotNull(pagadorCreateCie.getId());
					

					
					assertEquals(entitatCreate.getId(), pagadorCreateCie.getEntitatId());
					
				}


			
			}, 
			"Entitat Create", 
			entitatCreate,
			createPagadorCie);
	}
	
	
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
								
					
					
					
					assertNotNull(PagadorCieCreado);
					assertNotNull(PagadorCieCreado.getId());
					
					assertEquals(
							PagadorCieCreado.getId(),
							PagadorCieUpdate.getId());
					
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
					
					
					PagadorCieDto pagadorCieDelete=(PagadorCieDto)elementsCreats.get(2);
					
					 
					autenticarUsuari("admin");
					
							
					PagadorCieDto pagadorCieDeleteSeborra = pagadorCieService.delete(
							pagadorCieDelete.getId());
					
					comprobarPagadorCieService(
							
							pagadorCieDelete,
							pagadorCieDeleteSeborra);

										
					try{
						pagadorCieService.findById(
								pagadorCieDelete.getId());
						fail("El Pagador esborrat no s `hauria d'haver trobat");		
					}catch(NotFoundException expected) {
					}
					
					elementsCreats.remove(pagadorCieDelete);
					
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
					
					
					PagadorCieDto pagadorCieEncontrado=(PagadorCieDto)elementsCreats.get(1);
							
					autenticarUsuari("admin");
					
					
					PagadorCieDto encontradoCie= pagadorCieService.findById(
							pagadorCieEncontrado.getId());
					
					comprobarPagadorCieService(
							createPagadorCie,
							encontradoCie);
					
					assertNotNull(encontradoCie);
					assertNotNull(encontradoCie.getId());
					
					
					
					
				}
			},
			entitatCreate,
			createPagadorCie);
	
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
		
		
	}
	
	
	
	
	}



	
	
	

