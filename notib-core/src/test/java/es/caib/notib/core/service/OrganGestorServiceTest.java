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
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.TipusDocumentDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.exception.NotFoundException;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class OrganGestorServiceTest extends BaseServiceTest{

	
	private EntitatDto entitatCreate;
//	private PermisDto permisUser;
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
		
		permisAdmin = new PermisDto();
		permisAdmin.setAdministration(true);
		permisAdmin.setAdministradorEntitat(true);
		permisAdmin.setTipus(TipusEnumDto.USUARI);
		permisAdmin.setPrincipal("admin");
		
//		permisUser = new PermisDto();
//		permisUser.setUsuari(true);
//		permisUser.setTipus(TipusEnumDto.USUARI);
//		permisUser.setPrincipal("user");
		
		entitatCreate.setPermisos(Arrays.asList(permisAdmin));
		
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
					autenticarUsuari("admin");
					EntitatDto entitatCreate = (EntitatDto)elementsCreats.get(0);
					OrganGestorDto creat = (OrganGestorDto)elementsCreats.get(1);
					
					assertNotNull(creat);
					assertNotNull(creat.getId());
					
					comprovarOrganoGestor(
							organoGestorCreate,
							creat);

					assertEquals(entitatCreate.getId(), creat.getEntitatId());
				}
			}, 
			"Create ORGAN GESTOR", 
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
					OrganGestorDto organGestorCreat=(OrganGestorDto)elementsCreats.get(1);

					autenticarUsuari("admin");
					OrganGestorDto esborrada = organGestorService.delete(
							entitatCreada.getId(), 
							organGestorCreat.getId());
					comprovarOrganoGestor(
							organoGestorCreate,
							esborrada);

					try {						
						organGestorService.findById(
								entitatCreada.getId(),
								organGestorCreat.getId());
						fail("L'òrgan gestor esborrat no s'hauria d'haver trobat");												
					}catch(NotFoundException expected) {
					}
					elementsCreats.remove(organGestorCreat);
				}
			},
			"Delete ORGAN GESTOR",
			entitatCreate,
			organoGestorCreate);
	}
	
	@Test
	public void findById() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats)throws NotFoundException{
					autenticarUsuari("admin");
					EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
					OrganGestorDto organCreat = (OrganGestorDto)elementsCreats.get(1);
					
					OrganGestorDto trobat = organGestorService.findById(
							entitatCreada.getId(),
							organCreat.getId());

					assertNotNull(trobat);
					assertNotNull(trobat.getId());
					comprovarOrganoGestor(
							organoGestorCreate,
							trobat);
				}
			},
			"FindById ORGAN GESTOR",
			entitatCreate,
			organoGestorCreate);
	}
	
	@Test
	public void findByCodi() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats)throws NotFoundException{
					autenticarUsuari("admin");
					EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
					OrganGestorDto organCreat = (OrganGestorDto)elementsCreats.get(1);
							
					OrganGestorDto trobat = organGestorService.findByCodi(
							entitatCreada.getId(), 
							organCreat.getCodi());
					assertNotNull(trobat);
					assertNotNull(trobat.getId());
					comprovarOrganoGestor(
							organoGestorCreate,
							trobat);
				}
			},
			"FindByCodi ORGAN GESTOR",
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
	public void errorSiAccesAplCreate() {
		autenticarUsuari("apl");
		organGestorService.create(organoGestorCreate);
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
