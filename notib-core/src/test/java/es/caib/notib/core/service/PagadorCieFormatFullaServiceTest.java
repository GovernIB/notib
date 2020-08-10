package es.caib.notib.core.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.EntitatTipusEnumDto;
import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.PagadorCieDto;
import es.caib.notib.core.api.dto.PagadorCieFormatFullaDto;
import es.caib.notib.core.api.dto.PagadorPostalDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.TipusDocumentDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.PagadorCieFormatFullaService;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.core.service.BaseServiceTest.TestAmbElementsCreats;





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
		
		entitatCreate.setPermisos(Arrays.asList(permisUser));
		
		
		createPagadorCie=new PagadorCieDto();
		createPagadorCie.setDir3codi("12345");
		createPagadorCie.setContracteDataVig(new Date());
		
		createPagadorCieFormatFulla=new PagadorCieFormatFullaDto();
		createPagadorCieFormatFulla.setCodi("122");
		createPagadorCieFormatFulla.setPagadorCieId(createPagadorCieFormatFulla.getId());
		
		
		
		updatePagadorCieFormatFulla=new PagadorCieFormatFullaDto();
		updatePagadorCieFormatFulla.setCodi("122");
		updatePagadorCieFormatFulla.setPagadorCieId(createPagadorCieFormatFulla.getId());
		
		

		
		
	}
	@Test
	public void create() {
		
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws Exception {
					EntitatDto entitatCreate = (EntitatDto)elementsCreats.get(0);
					PagadorCieDto createPagadorCie = (PagadorCieDto)elementsCreats.get(1);
					PagadorCieFormatFullaDto createPagadorFormatFulla = (PagadorCieFormatFullaDto)elementsCreats.get(2);
					
					
					autenticarUsuari("admin");
//					PagadorCieFormatFullaDto PagadorCieCreat= pagadorCieFormatFullaService.create(
//							createPagadorCieFormatFulla.getId(), createPagador);
					
				
					
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
					
					
					EntitatDto entitatCreate = (EntitatDto)elementsCreats.get(0);
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
					
					EntitatDto entitatCreata=(EntitatDto)elementsCreats.get(0);
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
					
					EntitatDto entitatCreata=(EntitatDto)elementsCreats.get(0);
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
	
	private void comprobarPagadorCieFormatFulla(
			PagadorCieFormatFullaDto original,
			PagadorCieFormatFullaDto perComprovar) {
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

