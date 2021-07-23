package es.caib.notib.core.service;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.cie.OperadorPostalDto;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class OperadorPostalServiceTest extends BaseServiceTest{

	
	private EntitatDto entitatCreate;
	private PermisDto permisAdmin;
	private OperadorPostalDto crearPagadorPostal;
	private OperadorPostalDto updatePagadorPostal;
	

	@Autowired
	PermisosHelper permisosHelper;

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
		
		permisAdmin = new PermisDto();
		permisAdmin.setAdministration(true);
		permisAdmin.setAdministradorEntitat(true);
		permisAdmin.setTipus(TipusEnumDto.USUARI);
		permisAdmin.setPrincipal("admin");
		
		entitatCreate.setPermisos(Arrays.asList(permisAdmin));
		
		crearPagadorPostal = new OperadorPostalDto();
		crearPagadorPostal.setOrganismePagadorCodi("A04027005");
		crearPagadorPostal.setContracteNum("00000001");
		
		updatePagadorPostal=new OperadorPostalDto();
		updatePagadorPostal.setOrganismePagadorCodi("A04026968");
		updatePagadorPostal.setContracteNum("00000002");
	
	}
	
	@Test
	public void create() throws NotFoundException, Exception {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws Exception {
					EntitatDto entitatCreate = (EntitatDto)elementsCreats.get(0);
					OperadorPostalDto pagadorPostalCreat = (OperadorPostalDto)elementsCreats.get(1);
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
					OperadorPostalDto pagadorPostalCreat = (OperadorPostalDto)elementsCreats.get(1);
					autenticarUsuari("admin");
					
					updatePagadorPostal.setId(pagadorPostalCreat.getId());
					OperadorPostalDto pagadorModificat = operadorPostalService.update(updatePagadorPostal);
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
					OperadorPostalDto pagadorCreat = (OperadorPostalDto)elementsCreats.get(1);
					autenticarUsuari("admin");
					OperadorPostalDto esborrada = operadorPostalService.delete(pagadorCreat.getId());
					comprobarPagadorPostal(
							crearPagadorPostal,
							esborrada);
					try {
						operadorPostalService.findById(pagadorCreat.getId());
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
					OperadorPostalDto pagadorCreat = (OperadorPostalDto)elementsCreats.get(1);
					autenticarUsuari("admin");
					
					OperadorPostalDto trobat = operadorPostalService.findById(pagadorCreat.getId());
					
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
		operadorPostalService.create(entitatCreate.getId(),crearPagadorPostal);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplUpdate() {
		autenticarUsuari("apl");
		operadorPostalService.update(crearPagadorPostal);
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplDelete() {
		autenticarUsuari("apl");
		operadorPostalService.delete(crearPagadorPostal.getId());
	}

	private void comprobarPagadorPostal(
			OperadorPostalDto original,
			OperadorPostalDto perComprovar) {

		assertEquals(
				original.getOrganismePagadorCodi(),
				perComprovar.getOrganismePagadorCodi());
				
	}
	
}
