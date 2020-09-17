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
import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.dto.PagadorPostalDto;
import es.caib.notib.core.api.dto.PagadorPostalFiltreDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.PagadorPostalService;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.PagadorPostalEntity;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.helper.OrganigramaHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.repository.PagadorPostalRepository;

/**
 * Implementació del servei de gestió de pagadors postals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class PagadorPostalServiceImpl implements PagadorPostalService{

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PagadorPostalRepository pagadorPostalReposity;
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
	public PagadorPostalDto create(
			Long entitatId,
			PagadorPostalDto postal) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Creant un nou pagador postal ("
					+ "pagador=" + postal + ")");
			
			//TODO: Si es tothom comprovar que és administrador d'Organ i que indica Organ al pagadorPostal i que es administrador de l'organ indicat
			
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			OrganGestorEntity organGestor = null;
			if (postal.getOrganGestorId() != null) {
				organGestor = entityComprovarHelper.comprovarOrganGestor(
						entitat, 
						postal.getOrganGestorId());
			}
			
			PagadorPostalEntity pagadorPostalEntity = pagadorPostalReposity.save(
					PagadorPostalEntity.getBuilder(
							postal.getDir3codi(),
							postal.getContracteNum(),
							postal.getContracteDataVig(),
							postal.getFacturacioClientCodi(),
							entitat,
							organGestor).build());
			
			return conversioTipusHelper.convertir(
					pagadorPostalEntity, 
					PagadorPostalDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public PagadorPostalDto update(PagadorPostalDto postal) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Actualitzant pagador postal ("
					+ "pagador=" + postal + ")");
					
			//TODO: Si es tothom comprovar que és administrador d'Organ i que indica Organ al pagadorPostal i que es administrador de l'organ indicat

			PagadorPostalEntity pagadorPostalEntity = entityComprovarHelper.comprovarPagadorPostal(postal.getId());
			pagadorPostalEntity.update(
							postal.getDir3codi(),
							postal.getContracteNum(),
							postal.getContracteDataVig(),
							postal.getFacturacioClientCodi());
			
			pagadorPostalReposity.save(pagadorPostalEntity);
			
			return conversioTipusHelper.convertir(
					pagadorPostalEntity, 
					PagadorPostalDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public PagadorPostalDto delete(Long id) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {

			//TODO: Si es tothom comprovar que és administrador d'Organ i que l'usuari es administrador de l'Organ associat al pagadorPostal a eliminar.

			PagadorPostalEntity pagadorPostalEntity = entityComprovarHelper.comprovarPagadorPostal(id);
			pagadorPostalReposity.delete(id);
			return conversioTipusHelper.convertir(
					pagadorPostalEntity, 
					PagadorPostalDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PagadorPostalDto findById(Long id) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			PagadorPostalEntity pagadorPostalEntity = entityComprovarHelper.comprovarPagadorPostal(id);
			
			return conversioTipusHelper.convertir(
					pagadorPostalEntity, 
					PagadorPostalDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<PagadorPostalDto> findAmbFiltrePaginat(Long entitatId, PagadorPostalFiltreDto filtre,
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
			Page<PagadorPostalEntity> pagadorPostal = null;
	
			List<String> organsFills = null;
			if (filtre.getOrganGestorId() != null) {
				OrganGestorEntity organGestor = entityComprovarHelper.comprovarOrganGestor(
						entitat, 
						filtre.getOrganGestorId());
				organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(
						entitat.getDir3Codi(), 
						organGestor.getCodi());
				
				pagadorPostal = pagadorPostalReposity.findByCodiDir3AndNumContacteNotNullFiltrePaginatAndEntitatWithOrgan(
						filtre.getDir3codi() == null || filtre.getDir3codi().isEmpty(),
						filtre.getDir3codi(),
						filtre.getContracteNum() == null || filtre.getContracteNum().isEmpty(),
						filtre.getContracteNum(),
						organsFills,
						entitat,
						paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio));
			}else {
				pagadorPostal = pagadorPostalReposity.findByCodiDir3AndNumContacteNotNullFiltrePaginatAndEntitat(
						filtre.getDir3codi() == null || filtre.getDir3codi().isEmpty(),
						filtre.getDir3codi(),
						filtre.getContracteNum() == null || filtre.getContracteNum().isEmpty(),
						filtre.getContracteNum(),
						entitat,
						paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio));
			}
			
			return paginacioHelper.toPaginaDto(
					pagadorPostal,
					PagadorPostalDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<PagadorPostalDto> findAll() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de tots els pagadors postals");
			entityComprovarHelper.comprovarPermisos(
					null,
					true,
					true,
					false);
			return conversioTipusHelper.convertirList(
						pagadorPostalReposity.findAll(),
						PagadorPostalDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<PagadorPostalDto> findByEntitat(Long entitatId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta els pagadors postal de l'entitat: " + entitatId);
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			List<PagadorPostalEntity> pagadorsPostal = pagadorPostalReposity.findByEntitat(entitat);
			
			return conversioTipusHelper.convertirList(
					pagadorsPostal,
					PagadorPostalDto.class);
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
			List<PagadorPostalEntity> pagadorsPostal = pagadorPostalReposity.findByEntitatIdAndOrganGestorCodiIn(entitat.getId(), organsFills);
			
			return conversioTipusHelper.convertirList(
					pagadorsPostal,
					PagadorPostalDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	

	@Override
	public PaginaDto<PagadorPostalDto> findAllPaginat(PaginacioParamsDto paginacioParams) {
		return null;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(EntitatServiceImpl.class);

}
