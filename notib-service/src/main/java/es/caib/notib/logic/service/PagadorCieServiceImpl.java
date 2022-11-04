package es.caib.notib.logic.service;

import com.codahale.metrics.Timer;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.IdentificadorTextDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.cie.CieDataDto;
import es.caib.notib.logic.intf.dto.cie.CieDto;
import es.caib.notib.logic.intf.dto.cie.CieFiltreDto;
import es.caib.notib.logic.intf.dto.cie.CieTableItemDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.service.PagadorCieService;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.cie.PagadorCieEntity;
import es.caib.notib.logic.helper.*;
import es.caib.notib.persist.entity.cie.PagadorPostalEntity;
import es.caib.notib.persist.repository.PagadorCieRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementació del servei de gestió de pagadors cie.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class PagadorCieServiceImpl implements PagadorCieService{

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PagadorCieRepository pagadorCieReposity;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private OrganigramaHelper organigramaHelper;
	@Resource
	private MetricsHelper metricsHelper;
	
	@Override
	@Transactional
	public CieDto create(Long entitatId, CieDataDto cie) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Creant un nou pagador cie (pagador=" + cie + ")");
			//TODO: Si es tothom comprovar que és administrador d'Organ i que indica Organ al pagadorCIE i que es administrador de l'organ indicat
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			PagadorCieEntity p = PagadorCieEntity.builder(cie.getOrganismePagadorCodi(), cie.getNom(), cie.getContracteDataVig(), entitat).build();
			PagadorCieEntity pagadorCieEntity = pagadorCieReposity.save(p);
			return conversioTipusHelper.convertir(pagadorCieEntity, CieDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public CieDto update(CieDataDto cie) throws NotFoundException {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Actualitzant pagador cie (pagador=" + cie + ")");
			//TODO: Si es tothom comprovar que és administrador d'Organ i que indica Organ al pagadorCIE i que es administrador de l'organ indicat
			PagadorCieEntity pagadorCieEntity = entityComprovarHelper.comprovarPagadorCie(cie.getId());
			pagadorCieEntity.update(cie.getOrganismePagadorCodi(), cie.getContracteDataVig());
			pagadorCieReposity.save(pagadorCieEntity);
			return conversioTipusHelper.convertir(pagadorCieEntity, CieDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public CieDto delete(Long id) throws NotFoundException {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			//TODO: Si es tothom comprovar que és administrador d'Organ i que l'usuari es administrador de l'Organ associat al pagadorCIE a eliminar.
			PagadorCieEntity pagadorCieEntity = entityComprovarHelper.comprovarPagadorCie(id);
			pagadorCieReposity.deleteById(id);
			return conversioTipusHelper.convertir(pagadorCieEntity, CieDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public CieDto findById(Long id) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			PagadorCieEntity pagadorCieEntity = entityComprovarHelper.comprovarPagadorCie(id);
			return conversioTipusHelper.convertir(pagadorCieEntity, CieDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<CieTableItemDto> findAmbFiltrePaginat(Long entitatId, CieFiltreDto filtre, PaginacioParamsDto paginacioParams) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			entityComprovarHelper.comprovarPermisos(null, true, true, true);
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			Page<PagadorCieEntity> pagadorCie = null;
			Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();
			mapeigPropietatsOrdenacio.put("organismePagador", new String[] {"organismePagadorCodi"});
			Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio);
			List<String> organsFills = null;
			if (filtre.getOrganGestorId() != null) {
				OrganGestorEntity organGestor = entityComprovarHelper.comprovarOrganGestor(entitat, filtre.getOrganGestorId());
				organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitat.getDir3Codi(), organGestor.getCodi());
				pagadorCie = pagadorCieReposity.findByCodiDir3NotNullFiltrePaginatAndEntitatWithOrgan(
						filtre.getOrganismePagadorCodi() == null || filtre.getOrganismePagadorCodi().isEmpty(),
						filtre.getOrganismePagadorCodi(),
						organsFills,
						entitat,
						pageable);
			} else {
				pagadorCie = pagadorCieReposity.findByCodiDir3NotNullFiltrePaginatAndEntitat(
						filtre.getOrganismePagadorCodi() == null || filtre.getOrganismePagadorCodi().isEmpty(),
						filtre.getOrganismePagadorCodi(),
						entitat,
						pageable);
			}
			return paginacioHelper.toPaginaDto(pagadorCie, CieTableItemDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CieDto> findAll() {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de tots els pagadors cie");
			entityComprovarHelper.comprovarPermisos(null, true, true, false);
			return conversioTipusHelper.convertirList(pagadorCieReposity.findAll(), CieDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	@Override
	@Transactional(readOnly = true)
	public List<IdentificadorTextDto> findAllIdentificadorText() {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de tots els pagadors cie");
			entityComprovarHelper.comprovarPermisos(null, true, true, false);
			var pagadors = pagadorCieReposity.findByContracteDataVigGreaterThanEqual(new Date());
			return conversioTipusHelper.convertirList(pagadors, IdentificadorTextDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<IdentificadorTextDto> findNoCaducatsByEntitat(EntitatDto entitat) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de tots els pagadors postals");
			EntitatEntity e = entityComprovarHelper.comprovarEntitat(entitat.getId());
			entityComprovarHelper.comprovarPermisos(entitat.getId(), true, true, false);
			List<PagadorPostalEntity> p = pagadorCieReposity.findByEntitatAndContracteDataVigGreaterThanEqual(e, new Date());
			return conversioTipusHelper.convertirList(p, IdentificadorTextDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CieDto> findByEntitat(Long entitatId) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta els pagadors postal de l'entitat: " + entitatId);
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			List<PagadorCieEntity> pagadorsCie = pagadorCieReposity.findByEntitat(entitat);
			return conversioTipusHelper.convertirList(pagadorsCie, CieDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Object findByEntitatAndOrganGestor(EntitatDto entitat, OrganGestorDto organGestor) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta els pagadors postal de l'entitat: " + entitat.getId() + " i òrgan gestor: " + organGestor.getCodi());
			List<String> organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitat.getDir3Codi(), organGestor.getCodi());
			List<PagadorCieEntity> pagadorsCie = pagadorCieReposity.findByEntitatIdAndOrganGestorCodiIn(entitat.getId(), organsFills);
			return conversioTipusHelper.convertirList(pagadorsCie, CieDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public PaginaDto<CieDto> findAllPaginat(PaginacioParamsDto paginacioParams) {
		return null;
	}
}
