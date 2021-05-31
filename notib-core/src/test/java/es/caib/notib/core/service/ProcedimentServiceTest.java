package es.caib.notib.core.service;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.codahale.metrics.Timer;

import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.ProcedimentFiltreDto;
import es.caib.notib.core.api.dto.ProcedimentFormDto;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.entity.ProcedimentFormEntity;
import es.caib.notib.core.entity.ProcedimentOrganEntity;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.helper.OrganigramaHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.core.helper.PropertiesHelper;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.ProcedimentFormRepository;
import es.caib.notib.core.repository.ProcedimentOrganRepository;

@RunWith(MockitoJUnitRunner.class)
public class ProcedimentServiceTest {

	@Mock
	private MetricsHelper metricsHelper;
	@Mock
	private EntityComprovarHelper entityComprovarHelper;
	@Mock
	private EntitatRepository entitatRepository;
	@Mock
	private PaginacioHelper paginacioHelper;
	@Mock
	private OrganigramaHelper organigramaHelper;
	@Mock
	private ProcedimentFormRepository procedimentFormRepository;
	@Mock
	private PermisosHelper permisosHelper;
	@Mock
	private GrupService grupService;
	@Mock
	private EntitatEntity entitatEntityMock; 
	@Mock
	private Pageable pageableMock;
	@Mock
	private ProcedimentOrganRepository procedimentOrganRepository;
	
	@InjectMocks
	ProcedimentService procedimentService = new ProcedimentServiceImpl();
	
	@Before
	public void setUp() {
		Mockito.doNothing().when(metricsHelper).fiMetrica(Mockito.nullable(Timer.Context.class));
		Mockito.when(metricsHelper.iniciMetrica()).thenReturn(null);
	}
	
	//
	@Test
	public void whenFindAmbFiltrePaginatAdminEntitatWithFiltre_thenReturn() {
		
		// Given	
		Long entitatId = 1L;
		Page<ProcedimentFormEntity> procediments = null;
		
		PaginaDto<ProcedimentFormDto> procedimentsPage = initProcedimentsPage();
		
		List<EntitatEntity> entitatEntityList = new ArrayList<EntitatEntity>();
		List<PermisDto> permisos = new ArrayList<PermisDto>();
		Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();
//		List<String> organsFills = new ArrayList<String>();
		List<GrupDto> grup = new ArrayList<GrupDto>();
		ProcedimentFiltreDto filtre = new ProcedimentFiltreDto();
		filtre.setCodi(null);
		filtre.setNom(null);
		filtre.setOrganGestor(null);
		filtre.setComu(null);
		
		PaginacioParamsDto paginacioParams = new PaginacioParamsDto();
		
		List<ProcedimentOrganEntity> procedimentOrgans = new ArrayList<ProcedimentOrganEntity>();
		EntitatEntity entitat = EntitatEntity.getBuilder("codi", 
				"nom", 
				null, 
				"dir3Codi", 
				"dir3CodiReg", 
				"apiKey", 
				false, 
				false, 
				null, 
				null, 
				"colorFons", 
				"colorLletra", 
				null, 
				"oficina", 
				"nomOficinaVirtual", 
				false, 
				"llibre", 
				"llibreNom", 
				false)
				.build();
		ProcedimentEntity procediment = ProcedimentEntity.getBuilder(
				"",
				"",
				Integer.parseInt(PropertiesHelper.getProperties().getProperty("es.caib.notib.procediment.alta.auto.retard", "10")),
				Integer.parseInt(PropertiesHelper.getProperties().getProperty("es.caib.notib.procediment.alta.auto.caducitat", "15")),
				entitat,
				null,
				null,
				false,
				null, // organGestor
				null,
				null,
				null,
				null,
				false).build();
		OrganGestorEntity organGestor = OrganGestorEntity.getBuilder(null, null, entitat, null, null, null, null).build();
		ProcedimentOrganEntity procedimentOrgan = ProcedimentOrganEntity.getBuilder(procediment, organGestor).build();
		procedimentOrgans.add(procedimentOrgan);
		
		
		Mockito.when(entityComprovarHelper.comprovarEntitat(Mockito.anyLong(), Mockito.eq(false), Mockito.eq(false), Mockito.eq(false))).thenReturn(entitatEntityMock);
		Mockito.when(entityComprovarHelper.comprovarEntitat(Mockito.anyLong())).thenReturn(entitatEntityMock);
		Mockito.when(entitatRepository.findByActiva(Mockito.anyBoolean())).thenReturn(entitatEntityList);		
		Mockito.when(paginacioHelper.toSpringDataPageable(Mockito.any(PaginacioParamsDto.class), Mockito.eq(mapeigPropietatsOrdenacio))).thenReturn(pageableMock);	
//		Mockito.when(organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(Mockito.anyString(), Mockito.anyString())).thenReturn(organsFills);
//		Mockito.when(procedimentFormRepository.findAmbEntitatActual(Mockito.anyLong(), Mockito.any(Pageable.class))).thenReturn(procediments);
//		Mockito.when(procedimentFormRepository.findAmbEntitatActiva(Mockito.anyListOf(Long.class), Mockito.any(Pageable.class))).thenReturn(procediments);
//		Mockito.when(procedimentFormRepository.findAmbOrganGestorActualOrComu(Mockito.anyLong(), Mockito.anyListOf(String.class), Mockito.any(Pageable.class))).thenReturn(procediments);
		Mockito.when(procedimentFormRepository.findAmbEntitatAndFiltre(Mockito.nullable(Long.class), Mockito.nullable(Boolean.class), Mockito.nullable(String.class), 
				Mockito.nullable(Boolean.class), Mockito.nullable(String.class), Mockito.nullable(Boolean.class), Mockito.nullable(String.class), 
				Mockito.nullable(Boolean.class), Mockito.nullable(Pageable.class)))
			.thenReturn(procediments);
//		Mockito.when(procedimentFormRepository.findAmbFiltre(Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyBoolean(), 
//				Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.any(Pageable.class))).thenReturn(procediments);
//		Mockito.when(procedimentFormRepository.findAmbOrganGestorOrComuAndFiltre(Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyBoolean(), 
//				Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyListOf(String.class), Mockito.anyBoolean(), Mockito.any(Pageable.class))).thenReturn(procediments);
		Mockito.when(permisosHelper.findPermisos(Mockito.anyLong(), Mockito.eq(ProcedimentEntity.class))).thenReturn(permisos);
		Mockito.when(grupService.findGrupsByProcediment(Mockito.anyLong())).thenReturn(grup);
		Mockito.when(paginacioHelper.toPaginaDto(Mockito.eq(procediments), Mockito.eq(ProcedimentFormDto.class))).thenReturn(procedimentsPage);
		Mockito.when(procedimentOrganRepository.findByProcedimentId(Mockito.anyLong())).thenReturn(procedimentOrgans);
//		Mockito.when(organigramaHelper.getCodisOrgansGestorsFillsByOrgan(Mockito.anyString(), Mockito.anyString())).thenReturn(organsFills);		
		
		// When	
		PaginaDto<ProcedimentFormDto> pagina = procedimentService.findAmbFiltrePaginat(entitatId, false, true, false, null, filtre, paginacioParams);
		
		// Then
		assertNotNull(pagina);
		//verifica que se ha llamado 1 vez a este método
		Mockito.verify(procedimentFormRepository).findAmbEntitatAndFiltre(Mockito.nullable(Long.class), Mockito.nullable(Boolean.class), Mockito.nullable(String.class), 
				Mockito.nullable(Boolean.class), Mockito.nullable(String.class), Mockito.nullable(Boolean.class), Mockito.nullable(String.class), 
				Mockito.nullable(Boolean.class), Mockito.nullable(Pageable.class));		
	}
	
	// TODO: Falta generar más casos de test para admin d'organ y para superusuari con sus listas de permisos, etc. También sin filtre.
	// Los Mocks comentados en el test anterior no han sido borrados porque servirán para estos casos de pruebas futuros.
	
	@After
	public void tearDown() {
		Mockito.reset(entityComprovarHelper);
		Mockito.reset(entitatRepository);
		Mockito.reset(paginacioHelper);
		Mockito.reset(permisosHelper);
		Mockito.reset(grupService);		
	}
		
	private PaginaDto<ProcedimentFormDto> initProcedimentsPage() {
		
		PaginaDto<ProcedimentFormDto> procedimentsPage = new PaginaDto<ProcedimentFormDto>();
		List<ProcedimentFormDto> procList = new ArrayList<ProcedimentFormDto>();
		
		ProcedimentFormDto proc1 = new ProcedimentFormDto();
		proc1.setAgrupar(false);
		proc1.setCodi("962793");
		proc1.setComu(false);
		proc1.setCreatedBy(null);
		proc1.setCreatedDate(null);
		proc1.setEntitatNom("Govern de les Illes Balears");
		proc1.setGrups(null);
		proc1.setId(Long.valueOf(12390));
		proc1.setLastModifiedBy(null);
		proc1.setLastModifiedDate(null);
		proc1.setNom("Establiment, renovació o modificació de concerts educatius per a la prestació del servei públic de l'educació a les Illes Balears curs 2018-2019 [educació concertada]");
		proc1.setOrganGestor("A04013522");
		proc1.setOrganGestorNom("Dirección General de Planificación, Ordenación y Centros");
		proc1.setPagadorcie(null);
		proc1.setPagadorpostal(null);
		proc1.setPermisos(null);
		proc1.setRetard(0);
		
		ProcedimentFormDto proc2 = new ProcedimentFormDto();
		proc2.setAgrupar(false);
		proc2.setCodi("879427");
		proc2.setComu(true);
		proc2.setCreatedBy(null);
		proc2.setCreatedDate(null);
		proc2.setEntitatNom("Govern de les Illes Balears");
		proc2.setGrups(null);
		proc2.setId(Long.valueOf(12658));
		proc2.setLastModifiedBy(null);
		proc2.setLastModifiedDate(null);
		proc2.setNom("Recurs d'alçada");
		proc2.setOrganGestor("A04003003");
		proc2.setOrganGestorNom("Gobierno de las Islas Baleares");
		proc2.setPagadorcie(null);
		proc2.setPagadorpostal(null);
		proc2.setPermisos(null);
		proc2.setRetard(0);
		for (int i=0; i < 5; i++) {
			procList.add(proc1);
			procList.add(proc2);
		}
		procedimentsPage.setContingut(procList);
		
		procedimentsPage.setDarrera(false);
		procedimentsPage.setElementsTotal(1169);
		procedimentsPage.setNumero(0);
		procedimentsPage.setPosteriors(true);
		procedimentsPage.setPrimera(true);
		procedimentsPage.setTamany(10);
		procedimentsPage.setTotal(117);
		
		return procedimentsPage;
	}
}
