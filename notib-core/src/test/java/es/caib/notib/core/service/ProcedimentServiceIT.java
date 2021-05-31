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
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.ProcedimentFiltreDto;
import es.caib.notib.core.api.dto.ProcedimentFormDto;
import es.caib.notib.core.api.dto.TipusDocumentDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.plugin.SistemaExternException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class ProcedimentServiceIT extends BaseServiceTest{
	
	

	@Autowired
	PermisosHelper permisosHelper;
	
	private PermisDto permisAdmin;
	private EntitatDto entitatCreate;
	private ProcedimentDto createProcediment;
	private ProcedimentDto updateProcediment;
	private ProcedimentDto proc1;
	private ProcedimentDto proc2;
		
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
		
		permisAdmin = new PermisDto();
		permisAdmin.setAdministration(true);
		permisAdmin.setAdministradorEntitat(true);
		permisAdmin.setTipus(TipusEnumDto.USUARI);
		permisAdmin.setPrincipal("admin");

		entitatCreate.setPermisos(Arrays.asList(permisAdmin));
		
		createProcediment= new ProcedimentDto();
		createProcediment.setCodi("123456789");
		createProcediment.setNom("Procedimiento 1");
		createProcediment.setOrganGestor("A00000000");
		
		updateProcediment= new ProcedimentDto();
		updateProcediment.setCodi("234567890");
		updateProcediment.setNom("Procedimiento 2");
		updateProcediment.setOrganGestor("A00000001");
		
		configureMockUnitatsOrganitzativesPlugin();
		
		proc1 = new ProcedimentDto();
		proc1.setAgrupar(false);
		proc1.setCodi("962793");
		proc1.setComu(false);
		proc1.setCreatedBy(null);
		proc1.setCreatedDate(null);
		proc1.setGrups(null);
		proc1.setId(12390L);
		proc1.setLastModifiedBy(null);
		proc1.setLastModifiedDate(null);
		proc1.setNom("Establiment, renovació o modificació de concerts educatius per a la prestació del servei públic de l'educació a les Illes Balears curs 2018-2019 [educació concertada]");
		proc1.setOrganGestor("A04013522");
		proc1.setPagadorcie(null);
		proc1.setPagadorpostal(null);
		proc1.setPermisos(null);
		proc1.setRetard(0);
		
		proc2 = new ProcedimentDto();
		proc2.setAgrupar(false);
		proc2.setCodi("879427");
		proc2.setComu(true);
		proc2.setCreatedBy(null);
		proc2.setCreatedDate(null);
		proc2.setGrups(null);
		proc2.setId(12658L);
		proc2.setLastModifiedBy(null);
		proc2.setLastModifiedDate(null);
		proc2.setNom("Recurs d'alçada");
		proc2.setOrganGestor("A04003003");
		proc2.setPagadorcie(null);
		proc2.setPagadorpostal(null);
		proc2.setPermisos(null);
		proc2.setRetard(0);
		
	}
	

	
	@Test
	public void create() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws Exception {
					EntitatDto entitatCreate = (EntitatDto)elementsCreats.get(0);
					ProcedimentDto procedimentCreat = (ProcedimentDto)elementsCreats.get(1);
			
					autenticarUsuari("admin");
					assertNotNull(procedimentCreat);
					assertNotNull(procedimentCreat.getId());
					comprovarProcedimentCoincideix(
							procedimentCreat,
							createProcediment);
					assertEquals(entitatCreate.getId(), procedimentCreat.getEntitat().getId());
				}
			}, 
			"Create PROCEDIMENT", 
			entitatCreate,
			createProcediment);
	}
	
	@Test
	public void update() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws NotFoundException{
					EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);	
					ProcedimentDto procedimentCreat = (ProcedimentDto)elementsCreats.get(1);
					autenticarUsuari("admin");
					updateProcediment.setId(procedimentCreat.getId());
					ProcedimentDto modificat = procedimentService.update(
							entitatCreada.getId(), 
							updateProcediment, 
							true,
							true);
					
					assertNotNull(modificat);
					assertNotNull(modificat.getId());
					assertEquals(
							procedimentCreat.getId(),
							modificat.getId());
					comprovarProcedimentCoincideix(
							updateProcediment,
							modificat);
					
					assertEquals(entitatCreada.getId(), modificat.getEntitat().getId());
				}
			},
			"UPdate PROCEDIMENT",
			entitatCreate,
			createProcediment);
	}
	
	@Test
	public void delete() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws NotFoundException{
					 EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
					 ProcedimentDto procedimentCreat = (ProcedimentDto)elementsCreats.get(1);
					 autenticarUsuari("admin");
					 ProcedimentDto borrat = procedimentService.delete(
							entitatCreada.getId(), 
							procedimentCreat.getId(),
							true);
					
					comprovarProcedimentCoincideix(
							createProcediment,
							borrat);
					
					try {						
						procedimentService.findById(
								entitatCreada.getId(),
								true,
								procedimentCreat.getId());
						fail("El procediment esborrat no s'hauria d'haver trobat");												
					}catch(NotFoundException expected) {
					}catch (Exception ex) {
						ex.printStackTrace();
					}
					elementsCreats.remove(procedimentCreat);
				}
			},
			"Delete PROCEDIMENT",
			entitatCreate,
			createProcediment);
	}
				
	@Test
	public void findById() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats)throws NotFoundException{
					EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
					ProcedimentDto procedimentCreat = (ProcedimentDto)elementsCreats.get(1);
					autenticarUsuari("admin");
					
					ProcedimentDto trobat = procedimentService.findById(
							entitatCreada.getId(), 
							true,
							procedimentCreat.getId() );
							
					assertNotNull(trobat);
					assertNotNull(trobat.getId());
					comprovarProcedimentCoincideix(
							createProcediment,
							trobat);
					
				}
			},
			"FindById PROCEDIMENT",
			entitatCreate,
			createProcediment);
	
	}
	
	
	
	
	@Test
	public void findByCodi() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats)throws NotFoundException{
					EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
					autenticarUsuari("admin");
					
					ProcedimentDto trobat = procedimentService.findByCodi(
							entitatCreada.getId(), 
							createProcediment.getCodi());
					assertNotNull(trobat);
					assertNotNull(trobat.getId());
					comprovarProcedimentCoincideix(
							createProcediment,
							trobat);
				}
			},
			"FindByCodi PROCEDIMENT",
			entitatCreate,
			createProcediment
			);
	}
	
	@Test
	public void whenFindAmbFiltrePaginatPerAdminEntitatTots_thenReturnTots() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats)throws NotFoundException{
					EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
					autenticarUsuari("admin");
					
					ProcedimentFiltreDto procedimentFiltreCreado = buildProcedimentFiltreDto(null);
					
					PaginacioParamsDto paginacioParamsDto = getPaginacioDtoFromRequest(null, null);
					
					PaginaDto<ProcedimentFormDto> pagina = procedimentService.findAmbFiltrePaginat(
									entitatCreada.getId(),
									false,
									true, //admin d'entitat
									false,
									null,
									procedimentFiltreCreado,
									paginacioParamsDto);
					
					assertNotNull(pagina);
					
					assertNotNull(pagina.getContingut());
					assertEquals(pagina.getContingut().size(), 2);
					assertEquals(proc1.getCodi(), pagina.getContingut().get(0).getCodi());
					assertEquals(proc1.getNom(), pagina.getContingut().get(0).getNom());
					assertEquals(proc1.getOrganGestor(), pagina.getContingut().get(0).getOrganGestor());
					assertEquals(proc2.getCodi(), pagina.getContingut().get(1).getCodi());
					assertEquals(proc2.getNom(), pagina.getContingut().get(1).getNom());
					assertEquals(proc2.getOrganGestor(), pagina.getContingut().get(1).getOrganGestor());
					
					assertNotNull(pagina.getElementsNombre());
					assertNotNull(pagina.getElementsTotal());
					assertNotNull(pagina.getNumero());
					assertNotNull(pagina.getTamany());
					assertNotNull(pagina.getTotal());
				}
			},
			"findAmbFiltrePaginat per admin d'entitat PROCEDIMENT",
			entitatCreate,
			proc1,
			proc2
			);
	}
	
	@Test
	public void whenFindAmbFiltrePaginatPerAdminEntitatNomesComuns_thenReturnComuns() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats)throws NotFoundException{
					EntitatDto entitatCreada = (EntitatDto)elementsCreats.get(0);
					autenticarUsuari("admin");
					
					ProcedimentFiltreDto procedimentFiltreCreado = buildProcedimentFiltreDto(true);
					
					PaginacioParamsDto paginacioParamsDto = getPaginacioDtoFromRequest(null, null);
					
					PaginaDto<ProcedimentFormDto> pagina = procedimentService.findAmbFiltrePaginat(
									entitatCreada.getId(),
									false,
									true, //admin d'entitat
									false,
									null,
									procedimentFiltreCreado,
									paginacioParamsDto);
					
					assertNotNull(pagina);
					
					assertNotNull(pagina.getContingut());
					assertEquals(pagina.getContingut().size(), 1);
					assertEquals(proc2.getCodi(), pagina.getContingut().get(0).getCodi());
					assertEquals(proc2.getNom(), pagina.getContingut().get(0).getNom());
					assertEquals(proc2.getOrganGestor(), pagina.getContingut().get(0).getOrganGestor());
					
					assertNotNull(pagina.getElementsNombre());
					assertNotNull(pagina.getElementsTotal());
					assertNotNull(pagina.getNumero());
					assertNotNull(pagina.getTamany());
					assertNotNull(pagina.getTotal());
				}
			},
			"findAmbFiltrePaginat per admin d'entitat només comuns PROCEDIMENT",
			entitatCreate,
			proc1,
			proc2
			);
	}
	
	private ProcedimentFiltreDto buildProcedimentFiltreDto(Boolean nomesComuns) {
		ProcedimentFiltreDto procedimentFiltreCreado = new ProcedimentFiltreDto();
		procedimentFiltreCreado.setCodi(null);
//		procedimentFiltreCreado.setCodi("962793");
		procedimentFiltreCreado.setNom(null);
		procedimentFiltreCreado.setOrganGestor(null);
//		procedimentFiltreCreado.setOrganGestor("A04013522");
		procedimentFiltreCreado.setComu(nomesComuns);
//		procedimentFiltreCreado.setComu(true);
		procedimentFiltreCreado.setCreatedBy(null);
		procedimentFiltreCreado.setCreatedDate(null);
		procedimentFiltreCreado.setEntitatId(null);
		procedimentFiltreCreado.setId(null);
		procedimentFiltreCreado.setLastModifiedBy(null);
		procedimentFiltreCreado.setLastModifiedDate(null);
		return procedimentFiltreCreado;
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
					List<EntitatDto> entitatsAccessibles = procedimentService.findAccessiblesUsuariActual("NOT_ADMIN");
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
					entitatsAccessibles = entitatService.findAccessiblesUsuariActual("tothom");
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
					entitatsAccessibles = entitatService.findAccessiblesUsuariActual("tothom");
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
					entitatsAccessibles = entitatService.findAccessiblesUsuariActual("tothom");
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
					entitatsAccessibles = entitatService.findAccessiblesUsuariActual("tothom");
					assertThat(
							entitatsAccessibles.size(),
							is(0));
				}
			},
			entitatCreate);
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
	public void errorSiAccesSuperCreate() {
		autenticarUsuari("super");
		procedimentService.create(entitatCreate.getId(),createProcediment);
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplCreate() {
		autenticarUsuari("apl");
		procedimentService.create(entitatCreate.getId(),createProcediment);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplUpdate() {
		autenticarUsuari("apl");
		procedimentService.update(entitatCreate.getId(),createProcediment,false,false);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesSuperUpdate() {
		autenticarUsuari("super");
		procedimentService.update(entitatCreate.getId(),createProcediment,false,false);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplDelete() {
		autenticarUsuari("apl");
		procedimentService.delete(entitatCreate.getId(),createProcediment.getId(),false);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesSuperDelete() {
		autenticarUsuari("super");
		procedimentService.delete(entitatCreate.getId(),createProcediment.getId(),false);
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
	
}


