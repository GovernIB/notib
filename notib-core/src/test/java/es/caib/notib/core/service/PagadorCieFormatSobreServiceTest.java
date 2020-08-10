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
import org.springframework.security.access.AccessDeniedException;
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
import es.caib.notib.core.api.service.PagadorCieFormatSobreService;
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
		updatePagadorCieFormatSobre.setCodi("12345");
		updatePagadorCieFormatSobre.setPagadorCieId(createPagadorCieFormatSobre.getId());
		
		

		
		
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
					
//					PagadorCieFormatSobreDto pagadorCieFormatSobreCreat = pagadorCieFormatSobreService.create(
//							createPagadorCieFormatSobre.getId(), createPagadorCieFormatSobre);
					
					
//					updatePagadorCieFormatSobre.setId(pagadorCieFormatSobreServiceCreado.getId());
//					PagadorCieFormatSobreDto PagadorCieFormatSobre=pagadorCieFormatSobreService.update(pagadorCieFormatSobreServiceCreado);

					
					assertNotNull(pagadorCieUpdtate);
					assertNotNull(pagadorCieUpdtate.getId());
					
					
					
					
					assertEquals(createPagadorCie.getId(), pagadorCieUpdtate.getId());
					
					
					comprobarPagadorCieFormatSobre(
							createPagadorCieFormatSobre,
							pagadorCieFormatSobreServiceUpdate);
					assertEquals( pagadorCieUpdtate.getId(), pagadorCieFormatSobreServiceUpdate.getPagadorCieId());
				
					

				}
			},
			entitatCreate,
			createPagadorCie,
			createPagadorCieFormatSobre);
 }


	@Test
	public void delete() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws NotFoundException{
					 EntitatDto entitatCreada=(EntitatDto)elementsCreats.get(0);
					 PagadorCieDto pagadorCieSobreBorrar=(PagadorCieDto)elementsCreats.get(1);
					 PagadorCieFormatSobreDto pagadorCieFormatSobreBorrar=(PagadorCieFormatSobreDto)elementsCreats.get(2);
					 
					autenticarUsuari("admin");
//					PagadorCieFormatSobreDto deletePagadorCieFormatSobre1 = 
//							pagadorCieFormatSobreService.create(deletePagadorCieFormatSobre.getId(),deletePagadorCieFormatSobre);
					
							
					createPagadorCieFormatSobre.setId(pagadorCieFormatSobreBorrar.getId());
							
					assertNotNull(pagadorCieFormatSobreBorrar);
					assertNotNull(pagadorCieFormatSobreBorrar.getId());
					
					assertEquals( pagadorCieFormatSobreBorrar.getId(), pagadorCieFormatSobreBorrar.getId());
				
					comprobarPagadorCieFormatSobre(
							createPagadorCieFormatSobre,
							pagadorCieFormatSobreBorrar);
					
					try {						
						pagadorCieFormatSobreService.findById(
								pagadorCieSobreBorrar.getId());
						
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
	
	
	@Test
	public void managePermisAdmin() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) {
					EntitatDto creada = (EntitatDto)elementsCreats.get(0);
					autenticarUsuari("user");
					List<EntitatDto> entitatsAccessibles = entitatService.findAccessiblesUsuariActual("NOT_USER");
					assertThat(
							entitatsAccessibles.size(),
							is(0));
					autenticarUsuari("super");
					List<PermisDto> permisos = permisosHelper.findPermisos(creada.getId(), EntitatEntity.class);
					assertThat(
							permisos.size(),
							is(0));
					pagadorCieFormatFullaService.permisUpdate(
							creada.getId(), 
							createPagador.getId(), 
							permisUser, false);
							
					permisos = permisosHelper.findPermisos(creada.getId(), EntitatEntity.class);
					assertThat(
							permisos.size(),
							is(1));
					comprovarPermisCoincideix(
							permisUser,
							permisos.get(0));
					autenticarUsuari("user");
					entitatsAccessibles = pagadorCieFormatFullaService.findAll()("NOT_USER");
					assertThat(
							entitatsAccessibles.size(),
							is(1));
					assertThat(
							entitatsAccessibles.get(0).getId(),
							is(creada.getId()));
					autenticarUsuari("super");
					entitatService.permisUpdate(
							creada.getId(),
							permisAdmin);
					permisos = permisosHelper.findPermisos(creada.getId(), EntitatEntity.class);
					PermisDto permisPerUser = null;
					for (PermisDto permis: permisos) {
						if ("user".equals(permis.getPrincipal())) {
							permisPerUser = permis;
							break;
						}
					}
					assertNotNull(permisUser);
					assertThat(
							permisos.size(),
							is(2));
					comprovarPermisCoincideix(
							permisUser,
							permisPerUser);
					autenticarUsuari("user");
					entitatsAccessibles = entitatService.findAccessiblesUsuariActual("NOT_USER");
					assertThat(
							entitatsAccessibles.size(),
							is(1));
					assertThat(
							entitatsAccessibles.get(0).getId(),
							is(creada.getId()));
					autenticarUsuari("super");
					entitatService.permisDelete(
							creada.getId(),
							permisPerUser.getId());
					permisos = permisosHelper.findPermisos(creada.getId(), EntitatEntity.class);
					assertThat(
							permisos.size(),
							is(1));
					autenticarUsuari("user");
					entitatsAccessibles = entitatService.findAccessiblesUsuariActual("NOT_USER");
					assertThat(
							entitatsAccessibles.size(),
							is(0));
					autenticarUsuari("super");
					entitatService.permisDelete(
							creada.getId(),
							permisos.get(0).getId());
					permisos = permisosHelper.findPermisos(creada.getId(), EntitatEntity.class);
					assertThat(
							permisos.size(),
							is(0));
					autenticarUsuari("user");
					entitatsAccessibles = entitatService.findAccessiblesUsuariActual("NOT_USER");
					assertThat(
							entitatsAccessibles.size(),
							is(0));
				}
			},
			entitatCreate);
	}
	
	*/
	
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminCreate() {
		autenticarUsuari("admin");
		pagadorCieFormatSobreService.create(createPagadorCie.getId(),createPagadorCieFormatSobre);
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserCreate() {
		autenticarUsuari("user");
		pagadorCieFormatSobreService.create(createPagadorCie.getId(),createPagadorCieFormatSobre);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplCreate() {
		autenticarUsuari("apl");
		pagadorCieFormatSobreService.create(createPagadorCie.getId(),createPagadorCieFormatSobre);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminUpdate() {
		autenticarUsuari("admin");
		pagadorCieFormatSobreService.create(createPagadorCie.getId(),createPagadorCieFormatSobre);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserUpdate() {
		autenticarUsuari("user");
		pagadorCieFormatSobreService.update(updatePagadorCieFormatSobre);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplUpdate() {
		autenticarUsuari("apl");
		pagadorCieFormatSobreService.update(updatePagadorCieFormatSobre);
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminDelete() {
		autenticarUsuari("admin");
		pagadorCieFormatSobreService.update(updatePagadorCieFormatSobre);
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserDelete() {
		autenticarUsuari("user");
		pagadorCieFormatSobreService.delete(updatePagadorCieFormatSobre.getId());
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplDelete() {
		autenticarUsuari("apl");
		pagadorCieFormatSobreService.delete(updatePagadorCieFormatSobre.getId());
	}
	
	private void comprobarPagadorCieFormatSobre(
			PagadorCieFormatSobreDto original,
			PagadorCieFormatSobreDto perComprovar) {
		assertEquals(
				original.getId(),
				perComprovar.getId());
		assertEquals(
				original.getCodi(),
				perComprovar.getCodi());
		assertEquals(
				original.getPagadorCieId(),
				perComprovar.getPagadorCieId());
		
	}

	
	
	private void comprovarPermisCoincideix(
			PermisDto original,
			PermisDto perComprovar) {
		assertEquals(
				original.getPrincipal(),
				perComprovar.getPrincipal());
		assertEquals(
				original.getTipus(),
				perComprovar.getTipus());
		assertEquals(
				original.isRead(),
				perComprovar.isRead());
		assertEquals(
				original.isWrite(),
				perComprovar.isWrite());
		assertEquals(
				original.isCreate(),
				perComprovar.isCreate());
		assertEquals(
				original.isDelete(),
				perComprovar.isDelete());
		assertEquals(
				original.isAdministration(),
				perComprovar.isAdministration());
	}
}

