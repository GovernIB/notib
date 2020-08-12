package es.caib.notib.core.service;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Date;
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
import es.caib.notib.core.api.dto.PagadorCieDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.TipusDocumentDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.service.BaseServiceTest.TestAmbElementsCreats;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class OrganosGestoresServiceTest extends BaseServiceTest{

	
	private EntitatDto entitatCreate;
	private EntitatDto entitatUpdate;
	private PermisDto permisUser;
	private PermisDto permisAdmin;
	private OrganGestorDto organoGestorCreate;
	private OrganGestorDto organoGestorUpdate;
	
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
		organoGestorCreate.setOrganGestor("A00000000");
		
		
		organoGestorUpdate= new OrganGestorDto();
		organoGestorUpdate.setCodi("234567890");
		organoGestorUpdate.setNom("Procedimiento 2");
		organoGestorUpdate.setOrganGestor("A00000000");
	}
	
	
	
	@Test
	public void create() {
		
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws Exception {
					EntitatDto entitatCreate = (EntitatDto)elementsCreats.get(0);
					
			
					autenticarUsuari("admin");
					OrganoGestorDto organoGestorCreate1 = organoGestorService.create(
							entitatCreate.getId(), 
							organoGestorCreate);
		
					
					
					assertNotNull(organoGestorCreate1);
					assertNotNull(organoGestorCreate1.getId());
					
					comprovarOrganoGestor(
							organoGestorCreate,
							organoGestorCreate1
						);
					assertEquals(entitatCreate.getId(), organoGestorCreado.getEntitat().getId());
				}

			
			}, 
			"Entitat Create", 
			entitatCreate);
	}
	
	
	
	@Test
	public void update() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws NotFoundException{
					
					EntitatDto entitatCreada=(EntitatDto)elementsCreats.get(0);	
					OrganoGestorDto organoGestorCreado=(OrganoGestorDto)elementsCreats.get(1);
					
					
					autenticarUsuari("admin");
//					ProcedimentDto procedimentoCreado = procedimentService.create(
//							entitatCreada.getId(), createProcediment);
					
					
					organoGestorService.setId(organoGestorCreado.getId());
					OrganoGestorDto organoGestorUpdate1=organoGestorService.update(
							entitatCreada.getId(), organoGestorCreado, true);
					
					
					
					assertNotNull(organoGestorUpdate1);
					assertNotNull(organoGestorUpdate1.getId());
					
					assertEquals(
							organoGestorUpdate1.getId(),
							organoGestorUpdate1.getId());
					
					comprovarOrganoGestor(
							
							
							organoGestorUpdate1,
							organoGestorUpdate
							
							);
					
					assertEquals(entitatCreada.getId(), organoGestorUpdate1.getEntitat().getId());

				}
			},
			
			entitatCreate,
			organoGestorUpdate);
	}
	
	
	
	@Test
	public void delete() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws NotFoundException{
					 EntitatDto entitatCreada=(EntitatDto)elementsCreats.get(0);
					 OrganoGestorDto organoGestorCreat=(OrganoGestorDto)elementsCreats.get(1);
					 
					autenticarUsuari("admin");
					
					OrganoGestorDto organoGestorDelete = organoGestorService.delete(
							entitatCreada.getId(), 
							organoGestorCreat.getId());
					
							comprovarOrganoGestor(
							
							
							organoGestorUpdate1,
							organoGestorUpdate
							
							);
					
					try {						
						organoGestorService.findById();
								
						
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
					OrganoGestorDto  encontrarOrganoGestorPorId=(OrganoGestorDto)elementsCreats.get(1);
							
					autenticarUsuari("admin");
					
					organoGestorCreate.setId(encontrarPorId.getId());
					OrganoGestorDto encontrarOrganoGestorPorId= organoGestorService.findById();
						
							
						comprovarOrganoGestor(
							
							
							organoGestorUpdate1,
							organoGestorUpdate
							
							);
					
					assertNotNull(encontrado);
					assertNotNull(encontrado.getId());
					
					
					
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
					
					OrganoGestorDto  encontrarOrganoGestorPorCodi=(OrganoGestorDto)elementsCreats.get(1);
					
					
				
					autenticarUsuari("super");
//					ProcedimentDto encontratPorCodi = procedimentService.create(entitatCreada.getId(), createProcediment);
							
					createProcediment.setId(encontrarPorCodi.getId());	
					
					
					ProcedimentDto organoGestorEncontadoCode= organoGestorService.findByCodi();
					
					
					assertNotNull(encontrarOrganoGestorPorCodi);
					assertNotNull(encontrarOrganoGestorPorCodi.getId());
					
					comprovarOrganoGestor(
							
							
							encontrarOrganoGestorPorCodi,
							organoGestorCreate
							
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
		organoGestorService.create();
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserCreate() {
		autenticarUsuari("user");
		organoGestorService.create();
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplCreate() {
		autenticarUsuari("apl");
		organoGestorService.create();
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserUpdate() {
		autenticarUsuari("user");
		organoGestorService.update();
	}
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplUpdate() {
		autenticarUsuari("apl");
		organoGestorService.update();
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesSuperUpdate() {
		autenticarUsuari("super");
		organoGestorService.update();
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserDelete() {
		autenticarUsuari("user");
		organoGestorService.delete();
	}
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplDelete() {
		autenticarUsuari("apl");
		organoGestorService.delete();
	}
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesSuperDelete() {
		autenticarUsuari("super");
		organoGestorService.delete();
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserFinById() {
		autenticarUsuari("user");
		organoGestorService.findById();
	}
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesSuperFinById() {
		autenticarUsuari("super");
		organoGestorService.findById();
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplFinById() {
		autenticarUsuari("apl");
		organoGestorService.findById();
	}
	
	private void comprovarOrganoGestor(
			OrganoGestorDto original,
			OrganoGestorDto perComprovar) {
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
