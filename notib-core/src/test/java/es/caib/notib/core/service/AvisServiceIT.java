package es.caib.notib.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.core.api.dto.AvisDto;
import es.caib.notib.core.api.dto.AvisNivellEnumDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import es.caib.notib.core.api.service.AvisService;
import es.caib.notib.core.repository.AvisRepository;
import es.caib.notib.core.test.AuthenticationTest;
import lombok.Getter;
import lombok.Setter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class AvisServiceIT {
	
	@Autowired
	AuthenticationTest authenticationTest;
	
	@Autowired
	AvisService avisService;
	
	@Autowired
	AvisRepository avisRepository;
	
	
	private AvisDto avis;
	
	@Before
	public void setUp() {
		
		avis = new AvisDto();
		avis.setAssumpte("Aviso nivel Información");
		avis.setMissatge("Se ha desplegado una nueva versión de NOTIB");
		avis.setDataInici(new Date()); // fecha actual
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 5);  // se añaden 5 días a la fecha actual
		avis.setDataFinal(c.getTime());
		avis.setAvisNivell(AvisNivellEnumDto.INFO);
		
	}

	@Test
	public void whenCreateAvis_thenCreateTableItem() {
		
		authenticationTest.autenticarUsuari("super");
		
		// Given: AvisDto ya creado en setUp()
		
		// When
		AvisDto avisCreated = avisService.create(avis);
		
		// Then
		assertNotNull(avisCreated);
		assertNotNull(avisCreated.getId());
		comprobarAvisCoincide(avis, avisCreated);
		assertEquals(true, avisCreated.getActiu());
		
		// Borrado de los elementos creados
		avisService.delete(avisCreated.getId());
			
	}
	
	@Test
	public void whenUpdateAvis_thenUpdateTableItem() {
		
		authenticationTest.autenticarUsuari("super");
		
		// Given: un aviso existente
		AvisDto avisCreated = avisService.create(avis);
		avis.setId(avisCreated.getId());
		
		// When
		AvisDto avisUpdated = avisService.update(avis);
		
		// Then
		assertNotNull(avisUpdated);
		assertNotNull(avisUpdated.getId());
		assertEquals(avisCreated.getId(), avisUpdated.getId());
		comprobarAvisCoincide(avis, avisUpdated);
		assertEquals(true, avisUpdated.getActiu());

		// Borrado de los elementos creados
		avisService.delete(avisCreated.getId());
			
	}
	
	@Test
	public void whenUpdateActivaAvisDesactivar_thenUpdateActivaFalseTableItem() {
		
		authenticationTest.autenticarUsuari("super");
		
		// Given: un aviso existente y activo
		AvisDto avisCreated = avisService.create(avis);
		avis.setId(avisCreated.getId());
		
		// When: lo desactivo
		AvisDto avisUpdated = avisService.updateActiva(avis.getId(), false);
		
		// Then
		assertNotNull(avisUpdated);
		assertNotNull(avisUpdated.getId());
		assertEquals(avisCreated.getId(), avisUpdated.getId());
		comprobarAvisCoincide(avis, avisUpdated);
		assertEquals(false, avisUpdated.getActiu());
		
		// Borrado de los elementos creados
		avisService.delete(avisCreated.getId());
	
	}
	
	@Test
	public void whenDeleteAvis_thenDeleteTableItem() {
		
		authenticationTest.autenticarUsuari("super");
		
		// Given: un aviso existente
		AvisDto avisCreated = avisService.create(avis);
		
		// When
		AvisDto avisDeleted = avisService.delete(avisCreated.getId());
		
		// Then
		comprobarAvisCoincide(avis, avisDeleted);
		authenticationTest.autenticarUsuari("user");
		AvisDto avisFound = avisService.findById(avisCreated.getId());
		assertNull(avisFound);
	
	}
	
	@Test
	public void whenFindByIdAvis_thenReturnTableItem() {
		
		authenticationTest.autenticarUsuari("super");
		
		// Given: un aviso existente
		AvisDto avisCreated = avisService.create(avis);
		
		authenticationTest.autenticarUsuari("user");
		// When
		AvisDto avisFound = avisService.findById(avisCreated.getId());
		
		// Then
		assertNotNull(avisFound);
		assertNotNull(avisFound.getId());
		comprobarAvisCoincide(avis, avisFound);
		
		// Borrado de los elementos creados
		authenticationTest.autenticarUsuari("super");
		avisService.delete(avisCreated.getId());
			
	}
	
	@Test
	public void whenFindPaginat_thenReturnPaginaDtoWithTableItems() {
		
		authenticationTest.autenticarUsuari("super");
		// Given:
		AvisDto avisCreated1 = avisService.create(avis);
		
		AvisDto avis2 = new AvisDto();
		avis2.setAssumpte("Aviso nivel Advertencia");
		avis2.setMissatge("Se ha desplegado una nueva versión de NOTIB");
		avis2.setDataInici(new Date()); // fecha actual
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 5);  // se añaden 5 días a la fecha actual
		avis2.setDataFinal(c.getTime());
		avis2.setAvisNivell(AvisNivellEnumDto.WARNING);
		AvisDto avisCreated2 = avisService.create(avis2);
		
		// Se ha establecido orden ascendente por la columna/campo "Assumpte"
		PaginacioParamsDto paginacioParams = getPaginacioDtoFromRequest(null, null);
		
		authenticationTest.autenticarUsuari("user");
		// When
		PaginaDto<AvisDto> paginaDeAvisos = avisService.findPaginat(paginacioParams);
		
		// Then
		assertNotNull(paginaDeAvisos);
		assertNotNull(paginaDeAvisos.getContingut());
		assertEquals(2, paginaDeAvisos.getContingut().size());
		// Se comprueba que ha ordenado correctamente: 1º elemento es avis2 y 2º es avis
		comprobarAvisCoincide(avis2, paginaDeAvisos.getContingut().get(0));
		comprobarAvisCoincide(avis, paginaDeAvisos.getContingut().get(1));
		assertNotNull(paginaDeAvisos.getElementsNombre());
		assertNotNull(paginaDeAvisos.getElementsTotal());
		assertEquals(2,paginaDeAvisos.getElementsTotal());
		assertNotNull(paginaDeAvisos.getNumero());
		assertNotNull(paginaDeAvisos.getTamany());
		assertNotNull(paginaDeAvisos.getTotal());
		
		// Borrado de los elementos creados
		authenticationTest.autenticarUsuari("super");
		avisService.delete(avisCreated1.getId());
		avisService.delete(avisCreated2.getId());
		
	}
	
	@Test
	public void whenFindActiveAvis_thenReturnActiveAndUnexpiredTableItems() {
		
		authenticationTest.autenticarUsuari("super");
		// Given:
		// Aviso activo y no expirado
		AvisDto avisCreated = avisService.create(avis);
		
		// Otro Aviso activo y no expirado
		AvisDto avisActive = new AvisDto(); 
		avisActive.setAssumpte("Aviso nivel Warning");
		avisActive.setMissatge("Se ha desplegado una nueva versión de NOTIB");
		avisActive.setDataInici(new Date());
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 5);
		avisActive.setDataFinal(c.getTime());
		avisActive.setAvisNivell(AvisNivellEnumDto.WARNING);
		AvisDto avisActiveCreated = avisService.create(avisActive);
		
		// Aviso no activo y no expirado
		AvisDto avisNoActive = new AvisDto(); 
		avisNoActive.setAssumpte("Aviso nivel Error 1");
		avisNoActive.setMissatge("Se ha desplegado una nueva versión de NOTIB con errores");
		avisNoActive.setDataInici(new Date());
		avisNoActive.setDataFinal(c.getTime());
		avisNoActive.setAvisNivell(AvisNivellEnumDto.ERROR);
		AvisDto avisNoActiveCreated = avisService.create(avisNoActive); 
		avisNoActiveCreated = avisService.updateActiva(avisNoActiveCreated.getId(), false);
		
		// Aviso expirado
		AvisDto avisExpired = new AvisDto();
		avisExpired.setAssumpte("Aviso nivel Error 2");
		avisExpired.setMissatge("Se ha desplegado una nueva versión de NOTIB con errores");
		c.add(Calendar.DATE, -10);
		avisExpired.setDataInici(c.getTime());
		avisExpired.setDataFinal(c.getTime());
		avisExpired.setAvisNivell(AvisNivellEnumDto.ERROR);
		AvisDto avisExpiredCreated = avisService.create(avisExpired);
		
		authenticationTest.autenticarUsuari("user");
		// When
		List<AvisDto> avisosFound = avisService.findActive();
		
		// Then: devuelve los avisos activos que no hayan han expirado
		assertNotNull(avisosFound);
		assertEquals(2, avisosFound.size());
		comprobarAvisCoincide(avis, avisosFound.get(0));
		comprobarAvisCoincide(avisActive, avisosFound.get(1));
		
		// Borrado de los elementos creados
		authenticationTest.autenticarUsuari("super");
		avisService.delete(avisCreated.getId());
		avisService.delete(avisActiveCreated.getId());
		avisService.delete(avisNoActiveCreated.getId());
		avisService.delete(avisExpiredCreated.getId());
			
	}
	
	private void comprobarAvisCoincide(AvisDto esperado, AvisDto actual) {
		assertEquals(esperado.getAssumpte(), actual.getAssumpte());
		assertEquals(esperado.getMissatge(), actual.getMissatge());
		assertEquals(esperado.getDataInici(), actual.getDataInici());
		assertEquals(esperado.getDataFinal(), actual.getDataFinal());
		assertEquals(esperado.getAvisNivell(), actual.getAvisNivell());			
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserCreate() {
		authenticationTest.autenticarUsuari("user");
		avisService.create(avis);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminCreate() {
		authenticationTest.autenticarUsuari("admin");
		avisService.create(avis);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplCreate() {
		authenticationTest.autenticarUsuari("apl");
		avisService.create(avis);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserUpdate() {
		authenticationTest.autenticarUsuari("user");
		avisService.update(avis);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminUpdate() {
		authenticationTest.autenticarUsuari("admin");
		avisService.update(avis);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplUpdate() {
		authenticationTest.autenticarUsuari("apl");
		avisService.update(avis);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserUpdateActiva() {
		authenticationTest.autenticarUsuari("user");
		avisService.updateActiva(avis.getId(), false);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminUpdateActiva() {
		authenticationTest.autenticarUsuari("admin");
		avisService.updateActiva(avis.getId(), false);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplUpdateActiva() {
		authenticationTest.autenticarUsuari("apl");
		avisService.updateActiva(avis.getId(), false);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesUserDelete() {
		authenticationTest.autenticarUsuari("user");
		avisService.delete(avis.getId());
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminDelete() {
		authenticationTest.autenticarUsuari("admin");
		avisService.delete(avis.getId());
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplDelete() {
		authenticationTest.autenticarUsuari("apl");
		avisService.delete(avis.getId());
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesSuperFindById() {
		authenticationTest.autenticarUsuari("super");
		avisService.findById(avis.getId());
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminFindById() {
		authenticationTest.autenticarUsuari("admin");
		avisService.findById(avis.getId());
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplFindById() {
		authenticationTest.autenticarUsuari("apl");
		avisService.findById(avis.getId());
	}

	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesSuperFindPaginat() {
		authenticationTest.autenticarUsuari("super");
		avisService.findPaginat(null);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminFindPaginat() {
		authenticationTest.autenticarUsuari("admin");
		avisService.findPaginat(null);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplFindPaginat() {
		authenticationTest.autenticarUsuari("apl");
		avisService.findPaginat(null);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesSuperFindActive() {
		authenticationTest.autenticarUsuari("super");
		avisService.findActive();
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAdminFindActive() {
		authenticationTest.autenticarUsuari("admin");
		avisService.findActive();
	}
	
	@Test(expected = AccessDeniedException.class)
	public void errorSiAccesAplFindActive() {
		authenticationTest.autenticarUsuari("apl");
		avisService.findActive();
	}
	
	private static PaginacioParamsDto getPaginacioDtoFromRequest(
			Map<String, String[]> mapeigFiltres,
			Map<String, String[]> mapeigOrdenacions) {
		DatatablesParams params = new DatatablesParams();
		logger.debug("Informació de la pàgina obtingudes de datatables (" +
				"draw=" + params.getDraw() + ", " +
				"start=" + params.getStart() + ", " +
				"length=" + params.getLength() + ")");
		PaginacioParamsDto paginacio = new PaginacioParamsDto();
		int paginaNum = params.getStart() / params.getLength();
		paginacio.setPaginaNum(paginaNum);
		if (params.getLength() != null && params.getLength().intValue() == -1) {
			paginacio.setPaginaTamany(Integer.MAX_VALUE);
		} else {
			paginacio.setPaginaTamany(params.getLength());
		}
		paginacio.setFiltre(params.getSearchValue());
		for (int i = 0; i < params.getColumnsSearchValue().size(); i++) {
			String columna = params.getColumnsData().get(i);
			String[] columnes = new String[] {columna};
			if (mapeigFiltres != null && mapeigFiltres.get(columna) != null) {
				columnes = mapeigFiltres.get(columna);
			}
			for (String col: columnes) {
				if (!"<null>".equals(col)) {
					paginacio.afegirFiltre(
							col,
							params.getColumnsSearchValue().get(i));
					logger.debug("Afegit filtre a la paginació (" +
							"columna=" + col + ", " +
							"valor=" + params.getColumnsSearchValue().get(i) + ")");
				}
			}
		}
		for (int i = 0; i < params.getOrderColumn().size(); i++) {
			int columnIndex = params.getOrderColumn().get(i);
			String columna = params.getColumnsData().get(columnIndex);
			OrdreDireccioDto direccio;
			if ("asc".equals(params.getOrderDir().get(i)))
				direccio = OrdreDireccioDto.ASCENDENT;
			else
				direccio = OrdreDireccioDto.DESCENDENT;
			String[] columnes = new String[] {columna};
			if (mapeigOrdenacions != null && mapeigOrdenacions.get(columna) != null) {
				columnes = mapeigOrdenacions.get(columna);
			}
			for (String col: columnes) {
				paginacio.afegirOrdre(col, direccio);
				logger.debug("Afegida ordenació a la paginació (columna=" + columna + ", direccio=" + direccio + ")");
			}
		}
		logger.debug("Informació de la pàgina sol·licitada (paginaNum=" + paginacio.getPaginaNum() + ", paginaTamany=" + paginacio.getPaginaTamany() + ")");
		return paginacio;
	}
	
	
	//arreglarlo para avisos, estaba para listado de procediments
	@Getter @Setter
	protected static class DatatablesParams {
		private Integer draw;
		private Integer start;
		private Integer length;
		private String searchValue;
		private Boolean searchRegex;
		private List<Integer> orderColumn = new ArrayList<Integer>();
		private List<String> orderDir = new ArrayList<String>();
		private List<String> columnsData = new ArrayList<String>();
		private List<String> columnsName = new ArrayList<String>();
		private List<Boolean> columnsSearchable = new ArrayList<Boolean>();
		private List<Boolean> columnsOrderable = new ArrayList<Boolean>();
		private List<String> columnsSearchValue = new ArrayList<String>();
		private List<Boolean> columnsSearchRegex = new ArrayList<Boolean>();
		protected DatatablesParams() {
			draw = 1;
			start = 0;
			length = 10;
			searchValue = "";
			searchRegex = null;
			orderColumn.add(1);
			orderDir.add("asc");
			columnsData = Arrays.asList("id", "assumpte", "dataInici", "dataFinal", "actiu", "avisNivell", "id");
			columnsName = Arrays.asList(null, null, null, null, null, null, null);
			columnsSearchable = Arrays.asList(false, false, false, false, false, false, false);
			columnsOrderable = Arrays.asList(false, false, false, false, false, false, false);
			columnsSearchValue = Arrays.asList(null, null, null, null, null, null, null);
			columnsSearchRegex = Arrays.asList(false, false, false, false, false, false, false);	
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(AvisServiceIT.class);
}
