package es.caib.notib.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.Timer;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.PagadorCieDto;
import es.caib.notib.core.api.dto.PagadorCieFiltreDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.PagadorCieService;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.PagadorCieEntity;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.helper.OrganigramaHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.repository.PagadorCieRepository;

/**
 * Implementació del servei de gestió de pagadors cie.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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
	public PagadorCieDto create(
			Long entitatId,
			PagadorCieDto cie) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Creant un nou pagador cie ("
					+ "pagador=" + cie + ")");
			
			//TODO: Si es tothom comprovar que és administrador d'Organ i que indica Organ al pagadorCIE i que es administrador de l'organ indicat
			
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			OrganGestorEntity organGestor = null;
			if (cie.getOrganGestorId() != null) {
				organGestor = entityComprovarHelper.comprovarOrganGestor(
						entitat, 
						cie.getOrganGestorId());
			}
			
			PagadorCieEntity pagadorCieEntity = pagadorCieReposity.save(
					PagadorCieEntity.getBuilder(
							cie.getDir3codi(),
							cie.getContracteDataVig(),
							entitat,
							organGestor).build());
			
			return conversioTipusHelper.convertir(
					pagadorCieEntity, 
					PagadorCieDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public PagadorCieDto update(PagadorCieDto cie) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Actualitzant pagador cie ("
					+ "pagador=" + cie + ")");
			
			//TODO: Si es tothom comprovar que és administrador d'Organ i que indica Organ al pagadorCIE i que es administrador de l'organ indicat
			
			PagadorCieEntity pagadorCieEntity = entityComprovarHelper.comprovarPagadorCie(cie.getId());
			pagadorCieEntity.update(
							cie.getDir3codi(),
							cie.getContracteDataVig());
			
			pagadorCieReposity.save(pagadorCieEntity);
			
			return conversioTipusHelper.convertir(
					pagadorCieEntity, 
					PagadorCieDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public PagadorCieDto delete(Long id) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			
			//TODO: Si es tothom comprovar que és administrador d'Organ i que l'usuari es administrador de l'Organ associat al pagadorCIE a eliminar.
			
			PagadorCieEntity pagadorCieEntity = entityComprovarHelper.comprovarPagadorCie(id);
			pagadorCieReposity.delete(id);
			return conversioTipusHelper.convertir(
					pagadorCieEntity, 
					PagadorCieDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PagadorCieDto findById(Long id) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			PagadorCieEntity pagadorCieEntity = entityComprovarHelper.comprovarPagadorCie(id);
			
			return conversioTipusHelper.convertir(
					pagadorCieEntity, 
					PagadorCieDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<PagadorCieDto> findAmbFiltrePaginat(
			Long entitatId, 
			PagadorCieFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			entityComprovarHelper.comprovarPermisos(
					null,
					true,
					true,
					true);
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();
			Page<PagadorCieEntity> pagadorCie = null;
	
			List<String> organsFills = null;
			if (filtre.getOrganGestorId() != null) {
				OrganGestorEntity organGestor = entityComprovarHelper.comprovarOrganGestor(
						entitat, 
						filtre.getOrganGestorId());
				organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(
						entitat.getDir3Codi(), 
						organGestor.getCodi());
				
				pagadorCie = pagadorCieReposity.findByCodiDir3NotNullFiltrePaginatAndEntitatWithOrgan(
						filtre.getDir3codi() == null || filtre.getDir3codi().isEmpty(),
						filtre.getDir3codi(),
						organsFills,
						entitat,
						paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio));
			}else{
				pagadorCie = pagadorCieReposity.findByCodiDir3NotNullFiltrePaginatAndEntitat(
						filtre.getDir3codi() == null || filtre.getDir3codi().isEmpty(),
						filtre.getDir3codi(),
						entitat,
						paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio));
			}
			
			
			
			return paginacioHelper.toPaginaDto(
					pagadorCie,
					PagadorCieDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<PagadorCieDto> findAll() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de tots els pagadors cie");
			entityComprovarHelper.comprovarPermisos(
					null,
					true,
					true,
					false);
			return conversioTipusHelper.convertirList(
						pagadorCieReposity.findAll(),
						PagadorCieDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<PagadorCieDto> findByEntitat(Long entitatId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta els pagadors postal de l'entitat: " + entitatId);
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			List<PagadorCieEntity> pagadorsCie = pagadorCieReposity.findByEntitat(entitat);
			
			return conversioTipusHelper.convertirList(
					pagadorsCie,
					PagadorCieDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Object findByEntitatAndOrganGestor(EntitatDto entitat, OrganGestorDto organGestor) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta els pagadors postal de l'entitat: " + entitat.getId() + " i òrgan gestor: " + organGestor.getCodi());
			List<String> organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(
					entitat.getDir3Codi(), 
					organGestor.getCodi());
			List<PagadorCieEntity> pagadorsCie = pagadorCieReposity.findByEntitatIdAndOrganGestorCodiIn(entitat.getId(), organsFills);
			
			return conversioTipusHelper.convertirList(
					pagadorsCie,
					PagadorCieDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public PaginaDto<PagadorCieDto> findAllPaginat(PaginacioParamsDto paginacioParams) {
		return null;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(EntitatServiceImpl.class);

}
