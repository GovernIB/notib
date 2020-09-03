package es.caib.notib.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
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
import es.caib.notib.core.api.dto.PagadorPostalDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.TipusDocumentDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.helper.PermisosHelper;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class PagadorPostalServiceTest extends BaseServiceTest{

	
	private EntitatDto entitatCreate;
	private PermisDto permisAdmin;
	private PagadorPostalDto crearPagadorPostal;
	private PagadorPostalDto updatePagadorPostal;
	

	@Autowired
	PermisosHelper permisosHelper;
//	@Autowired
//	private EntityManager entityManager;

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
		
		crearPagadorPostal=new PagadorPostalDto();
		crearPagadorPostal.setDir3codi("A04027005");
		crearPagadorPostal.setContracteNum("00000001");
		
		updatePagadorPostal=new PagadorPostalDto();
		updatePagadorPostal.setDir3codi("A04026968");
		updatePagadorPostal.setContracteNum("00000002");
	
	}
	
	@Test
	public void create() throws NotFoundException, Exception {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws Exception {
					EntitatDto entitatCreate = (EntitatDto)elementsCreats.get(0);
					PagadorPostalDto pagadorPostalCreat = (PagadorPostalDto)elementsCreats.get(1);
					autenticarUsuari("admin");
					assertNotNull(pagadorPostalCreat);
					assertNotNull(pagadorPostalCreat.getId());
					comprobarPagadorPostal(
							crearPagadorPostal,
							pagadorPostalCreat);
					assertEquals(entitatCreate.getId(), pagadorPostalCreat.getEntitatId());
				}
			}, 
			"Create PAGADOR POSTAL", 
			entitatCreate,
			crearPagadorPostal);
	}
	
	@Test
	public void update() throws NotFoundException, Exception {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws NotFoundException{
					EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
					PagadorPostalDto pagadorPostalCreat = (PagadorPostalDto)elementsCreats.get(1);
					autenticarUsuari("admin");
					
					updatePagadorPostal.setId(pagadorPostalCreat.getId());
					PagadorPostalDto pagadorModificat = pagadorPostalService.update(updatePagadorPostal);
					assertNotNull(pagadorModificat);
					assertNotNull(pagadorModificat.getId());
					assertEquals(pagadorPostalCreat.getId(), pagadorModificat.getId());
					comprobarPagadorPostal(
							updatePagadorPostal,
							pagadorModificat);
					assertEquals(entitatCreada.getId(), pagadorModificat.getEntitatId());
				}
			},
			"Update PAGADOR POSTAL",
			entitatCreate,
			crearPagadorPostal);
	}

	@Test
	public void delete() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws NotFoundException{
//					EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
					PagadorPostalDto pagadorCreat = (PagadorPostalDto)elementsCreats.get(1);
					autenticarUsuari("admin");
					PagadorPostalDto esborrada = pagadorPostalService.delete(pagadorCreat.getId());
					comprobarPagadorPostal(
							crearPagadorPostal,
							esborrada);
					try {
						pagadorPostalService.findById(pagadorCreat.getId());
						fail("El Pagador postal esborrat no s'hauria d'haver trobat");
					}catch(NotFoundException expected) {
					}
					elementsCreats.remove(pagadorCreat);
				}	
			},
			"Delete PAGADOR POSTAL",
			entitatCreate,
			crearPagadorPostal);
	}
				
	@Test
	public void findById() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats)throws NotFoundException{
//					EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
					PagadorPostalDto pagadorCreat = (PagadorPostalDto)elementsCreats.get(1);
					autenticarUsuari("admin");
					
					PagadorPostalDto trobat = pagadorPostalService.findById(pagadorCreat.getId());
					
					assertNotNull(trobat);
					assertNotNull(trobat.getId());
					comprobarPagadorPostal(
							crearPagadorPostal,
							trobat);
				}
			},
			"FindById PAGADOR POSTAL",
			entitatCreate,
			crearPagadorPostal);
	
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplCreate() {
		autenticarUsuari("apl");
		pagadorPostalService.create(entitatCreate.getId(),crearPagadorPostal);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplUpdate() {
		autenticarUsuari("apl");
		pagadorPostalService.update(crearPagadorPostal);
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

