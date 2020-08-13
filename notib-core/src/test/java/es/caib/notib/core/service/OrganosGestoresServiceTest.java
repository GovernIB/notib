package es.caib.notib.core.service;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.EntitatTipusEnumDto;
import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.TipusDocumentDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.exception.NotFoundException;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class OrganosGestoresServiceTest extends BaseServiceTest{

	
	private EntitatDto entitatCreate;
	private EntitatDto entitatUpdate;
	private PermisDto permisUser;
	private PermisDto permisAdmin;
	private OrganGestorDto organoGestorCreate;
	
	
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
		
		organoGestorCreate= new OrganGestorDto();
		organoGestorCreate.setCodi("123456789");
		organoGestorCreate.setNom("Procedimiento 1");
		
		
		
		
		
	}
	
	
	
	@Test
	public void create() {
		
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats){
					EntitatDto entitatCreate = (EntitatDto)elementsCreats.get(0);
					OrganGestorDto organoGestorCreate=(OrganGestorDto)elementsCreats.get(1);
					
			
					autenticarUsuari("admin");
					
					assertNotNull(organoGestorCreate);
					assertNotNull(organoGestorCreate.getId());
					
					OrganGestorDto organoGestorCreate1 = organGestorService.create(
							organoGestorCreate);
		
					assertNotNull(organoGestorCreate1);
					assertNotNull(organoGestorCreate1.getId());
					
					
					comprovarOrganoGestor(
							organoGestorCreate,
							organoGestorCreate1
				
							);

					assertEquals(entitatCreate.getId(), organoGestorCreate.getEntitatId());
				}

			
			}, 
			"Entitat Create", 
			entitatCreate,
			organoGestorCreate
			);
	}
	

	
	@Test
	public void delete() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats){
					 EntitatDto entitatCreada=(EntitatDto)elementsCreats.get(0);
					 OrganGestorDto organoGestorCreat=(OrganGestorDto)elementsCreats.get(1);
					 
					autenticarUsuari("admin");
					
					OrganGestorDto organoGestorDelete = organGestorService.delete(
							entitatCreada.getId(), 
							organoGestorCreat.getId());
					
							comprovarOrganoGestor(
							
							
							organoGestorCreat,
						organoGestorDelete
							
							);
					
					try {						
						organGestorService.findById(
								entitatCreada.getId(),
								organoGestorCreat.getId());
								
						
						fail("El procediment esborrat no s `hauria d'haver trobat");												
						}catch(NotFoundException expected) {
						
						}
					
					elementsCreats.remove(organoGestorCreat);
					
					

					}
				},
					"Delete Procediment",
			entitatCreate,
			organoGestorCreate);
	}
				

	
	@Test
	public void findById() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats)throws NotFoundException{
					
					EntitatDto entitatCreada=(EntitatDto)elementsCreats.get(0);
					OrganGestorDto  encontrarOrganoGestor=(OrganGestorDto)elementsCreats.get(1);
							
					autenticarUsuari("admin");
					
					
					OrganGestorDto encontrarOrganoGestorPorId= organGestorService.findById(
							entitatCreada.getId(),
							encontrarOrganoGestor.getId());
						
							
						comprovarOrganoGestor(
							
							
								encontrarOrganoGestor,
								encontrarOrganoGestorPorId
							
							);
					
					assertNotNull(encontrarOrganoGestorPorId);
					assertNotNull(encontrarOrganoGestorPorId.getId());
					
					
					
				}
			},
			entitatCreate,
			organoGestorCreate);
			
			
	
	}
	
	
	
	
	@Test
	public void findByCodi() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats)throws NotFoundException{
					
					EntitatDto entitatCreada=(EntitatDto)elementsCreats.get(0);
					
					OrganGestorDto  encontrarOrganoGestor=(OrganGestorDto)elementsCreats.get(1);
					
					
				
					autenticarUsuari("super");

							
					OrganGestorDto  encontrarOrganoGestorPorCodi=organGestorService.findByCodi(
							entitatCreada.getId(), encontrarOrganoGestor.getCodi());
					
					
					
					
					assertNotNull(encontrarOrganoGestorPorCodi);
					assertNotNull(encontrarOrganoGestorPorCodi.getId());
					
					comprovarOrganoGestor(
							
							
							encontrarOrganoGestor,
							encontrarOrganoGestorPorCodi
							
							);

			}
				
				
			},
			entitatCreate,
			organoGestorCreate
			);
	}
	
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesSuperCreate() {
		autenticarUsuari("super");
		organGestorService.create(organoGestorCreate);
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserCreate() {
		autenticarUsuari("user");
		organGestorService.create(organoGestorCreate);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplCreate() {
		autenticarUsuari("apl");
		organGestorService.create(organoGestorCreate);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserDelete() {
		autenticarUsuari("user");
		organGestorService.delete(
				entitatCreate.getId(), 
				organoGestorCreate.getId());
	}
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplDelete() {
		autenticarUsuari("apl");
		organGestorService.delete(
				entitatCreate.getId(), 
				organoGestorCreate.getId());
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesSuperDelete() {
		autenticarUsuari("super");
		organGestorService.delete(
				entitatCreate.getId(), 
				organoGestorCreate.getId());
	}
	
	
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserFinById() {
		autenticarUsuari("user");
		organGestorService.findById(entitatCreate.getId(),
				organoGestorCreate.getId());
	}
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesSuperFinById() {
		autenticarUsuari("super");
		organGestorService.findById(entitatCreate.getId(),
				organoGestorCreate.getId());
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplFinById() {
		autenticarUsuari("apl");
		organGestorService.findById(entitatCreate.getId(),
				organoGestorCreate.getId());
	}
	
	
	
	
	private void comprovarOrganoGestor(
			OrganGestorDto original,
			OrganGestorDto perComprovar) {
		assertEquals(
				original.getCodi(),
				perComprovar.getCodi());
		assertEquals(
				original.getNom(),
				perComprovar.getNom());
		
		
	}
	
	
	
	


}
