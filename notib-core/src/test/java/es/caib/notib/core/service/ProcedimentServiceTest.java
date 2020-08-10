package es.caib.notib.core.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

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
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.TipusDocumentDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.plugin.SistemaExternException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class ProcedimentServiceTest extends BaseServiceTest{
	
	

	@Autowired
	PermisosHelper permisosHelper;
	@Autowired
	private EntityManager entityManager;
	 
	
	private PermisDto permisUser;
	private PermisDto permisAdmin;
	private EntitatDto entitatCreate;
	private EntitatDto entitatUpdate;
	private ProcedimentDto createProcediment;
	private ProcedimentDto updateProcediment;
	
	
		
	@Before
	public void setUp() throws SistemaExternException {
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
		
		createProcediment= new ProcedimentDto();
		createProcediment.setCodi("123456789");
		createProcediment.setNom("Procedimiento 1");
		createProcediment.setOrganGestor("A00000000");
		
		updateProcediment= new ProcedimentDto();
		updateProcediment.setCodi("234567890");
		updateProcediment.setNom("Procedimiento 2");
		updateProcediment.setOrganGestor("A00000001");
		
		configureMockUnitatsOrganitzativesPlugin();
		
	}
	
	

	
	@Test
	public void create() {
		
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws Exception {
					EntitatDto entitatCreate = (EntitatDto)elementsCreats.get(0);
					
			
					autenticarUsuari("admin");
					ProcedimentDto procedimentCreat = procedimentService.create(
							entitatCreate.getId(), 
							createProcediment);
		
					
					
					assertNotNull(procedimentCreat);
					assertNotNull(procedimentCreat.getId());
					
					comprovarProcedimentCoincideix(
							procedimentCreat,
							createProcediment);
					assertEquals(entitatCreate.getId(), procedimentCreat.getEntitat().getId());
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
					ProcedimentDto procedimentoCreado=(ProcedimentDto)elementsCreats.get(1);
					
					
					autenticarUsuari("admin");
//					ProcedimentDto procedimentoCreado = procedimentService.create(
//							entitatCreada.getId(), createProcediment);
					
					
					updateProcediment.setId(procedimentoCreado.getId());
					ProcedimentDto ProcedimientoModificado=procedimentService.update(
							entitatCreada.getId(), updateProcediment, true);
					
					
					
					assertNotNull(ProcedimientoModificado);
					assertNotNull(ProcedimientoModificado.getId());
					
					assertEquals(
							procedimentoCreado.getId(),
							ProcedimientoModificado.getId());
					
					comprovarProcedimentCoincideix(
							procedimentoCreado,
							ProcedimientoModificado);
					
					assertEquals(entitatCreada.getId(), ProcedimientoModificado.getEntitat().getId());

				}
			},
			
			entitatCreate,
			updateProcediment);
	}
	
	
	
	@Test
	public void delete() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws NotFoundException{
					 EntitatDto entitatCreada=(EntitatDto)elementsCreats.get(0);
					 ProcedimentDto procedimentCreat1=(ProcedimentDto)elementsCreats.get(1);
					 
					autenticarUsuari("admin");							
					ProcedimentDto borrado = procedimentService.delete(
							entitatCreada.getId(), 
							procedimentCreat1.getId());
					
					comprovarProcedimentCoincideix(
							
							createProcediment,
							borrado);
					
					try {						
						procedimentService.findById(
								entitatCreada.getId(),
								true,
								procedimentCreat1.getId());
						
						fail("El procediment esborrat no s `hauria d'haver trobat");												
						}catch(NotFoundException expected) {
						
						}
					
					elementsCreats.remove(procedimentCreat1);
					
					

					}
				},
					"Delete Procediment",
			entitatCreate,
			createProcediment);
	}
				

	
	@Test
	public void findById() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats)throws NotFoundException{
					
					EntitatDto entitatCreada=(EntitatDto)elementsCreats.get(0);
					ProcedimentDto  encontrarPorId=(ProcedimentDto)elementsCreats.get(1);
							
					autenticarUsuari("admin");
					
					createProcediment.setId(encontrarPorId.getId());
					ProcedimentDto encontrado= procedimentService.findById(
							entitatCreada.getId(), 
							true,
							encontrarPorId.getId() );
							
							comprovarProcedimentCoincideix(
							
							createProcediment,
							encontrado);
					
					
					assertNotNull(encontrado);
					assertNotNull(encontrado.getId());
					
					
					
				}
			},
			entitatCreate,
			createProcediment);
			
			
	
	}
	
	
	
	
	@Test
	public void findByCodi() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats)throws NotFoundException{
					
					EntitatDto entitatCreada=(EntitatDto)elementsCreats.get(0);
					
					ProcedimentDto  encontrarPorCodi=(ProcedimentDto)elementsCreats.get(1);
					
					
				
					autenticarUsuari("super");
//					ProcedimentDto encontratPorCodi = procedimentService.create(entitatCreada.getId(), createProcediment);
							
					createProcediment.setId(encontrarPorCodi.getId());	
					
					
					ProcedimentDto encontadoCode= procedimentService.findByCodi(
							entitatCreada.getId(), 
							createProcediment.getCodi());
					
					
					assertNotNull(encontadoCode);
					assertNotNull(encontadoCode.getId());
					
					

			}
				
				
			},
			entitatCreate,
			createProcediment
			);
	}
	
	/*
	
	@Test
	public void managePermisAdmin() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) {
					EntitatDto creada = (EntitatDto)elementsCreats.get(0);
					autenticarUsuari("user");
					List<EntitatDto> entitatsAccessibles = entitatService.findAccessiblesUsuariActual("NOT_ADMIN");
					assertThat(
							entitatsAccessibles.size(),
							is(0));
					autenticarUsuari("super");
					List<PermisDto> permisos = permisosHelper.findPermisos(creada.getId(), EntitatEntity.class);
					assertThat(
							permisos.size(),
							is(0));
					procedimentService.permisUpdate(
							creada.getId(), 
							createProcediment.getId(), 
							permisUser, false);
							
					permisos = permisosHelper.findPermisos(creada.getId(), EntitatEntity.class);
					assertThat(
							permisos.size(),
							is(1));
					comprovarPermisCoincideix(
							permisUser,
							permisos.get(0));
					autenticarUsuari("user");
					entitatsAccessibles = entitatService.findAccessiblesUsuariActual("NOT_USER");
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
	
	@Test
	public void findTipusDocument() {
		// TODO
	}
	
	@Test
	public void findTipusDocumentDefault() {
		// TODO
	}
	
	@Test
	public void errorSiCodiDuplicat() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) {
					autenticarUsuari("super");
					try {
						entitatService.create(entitatCreate);
						fail("L'execució no ha donat l'error de violació d'integritat per clau única repetida");
					} catch (DataIntegrityViolationException ex) {
						// Excepció esperada
						entityManager.clear();
					}
				}
			},
			entitatCreate);
	}
	
	*/
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminCreate() {
		autenticarUsuari("admin");
		procedimentService.create(entitatCreate.getId(),createProcediment);
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserCreate() {
		autenticarUsuari("user");
		procedimentService.create(entitatCreate.getId(),createProcediment);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplCreate() {
		autenticarUsuari("apl");
		procedimentService.create(entitatCreate.getId(),createProcediment);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminUpdate() {
		autenticarUsuari("admin");
		procedimentService.create(entitatCreate.getId(),createProcediment);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserUpdate() {
		autenticarUsuari("user");
		procedimentService.update(entitatCreate.getId(),updateProcediment,false);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplUpdate() {
		autenticarUsuari("apl");
		procedimentService.update(entitatCreate.getId(), updateProcediment, false);
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminDelete() {
		autenticarUsuari("admin");
		procedimentService.update(entitatCreate.getId(),updateProcediment,  true);
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserDelete() {
		autenticarUsuari("user");
		procedimentService.delete(entitatCreate.getId(), createProcediment.getId());
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplDelete() {
		autenticarUsuari("apl");
		procedimentService.delete(entitatCreate.getId(), createProcediment.getId());
	}
	
	

	private void comprovarProcedimentCoincideix(
			ProcedimentDto original,
			ProcedimentDto perComprovar) {
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


