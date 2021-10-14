package es.caib.notib.core.service;

import com.codahale.metrics.Timer;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.IdentificadorTextDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.cie.OperadorPostalDataDto;
import es.caib.notib.core.api.dto.cie.OperadorPostalDto;
import es.caib.notib.core.api.dto.cie.OperadorPostalFiltreDto;
import es.caib.notib.core.api.dto.cie.OperadorPostalTableItemDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.OperadorPostalService;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.cie.PagadorPostalEntity;
import es.caib.notib.core.helper.*;
import es.caib.notib.core.repository.PagadorPostalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementació del servei de gestió de pagadors postals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class OperadorPostalServiceImpl implements OperadorPostalService {

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
	public OperadorPostalDto create(
			Long entitatId,
			OperadorPostalDataDto postal) {
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
					PagadorPostalEntity.builder(
							postal.getOrganismePagadorCodi(),
							postal.getNom(),
							postal.getContracteNum(),
							postal.getContracteDataVig(),
							postal.getFacturacioClientCodi(),
							entitat)
							.organGestor(organGestor).build());
			
			return conversioTipusHelper.convertir(
					pagadorPostalEntity, 
					OperadorPostalDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public OperadorPostalDto update(OperadorPostalDataDto postal) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Actualitzant pagador postal ("
					+ "pagador=" + postal + ")");
					
			//TODO: Si es tothom comprovar que és administrador d'Organ i que indica Organ al pagadorPostal i que es administrador de l'organ indicat

			PagadorPostalEntity pagadorPostalEntity = entityComprovarHelper.comprovarPagadorPostal(postal.getId());

			pagadorPostalEntity.update(
							postal.getOrganismePagadorCodi(),
							postal.getContracteNum(),
							postal.getContracteDataVig(),
							postal.getFacturacioClientCodi());
			
			pagadorPostalReposity.save(pagadorPostalEntity);
			
			return conversioTipusHelper.convertir(
					pagadorPostalEntity, 
					OperadorPostalDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public OperadorPostalDto delete(Long id) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {

			//TODO: Si es tothom comprovar que és administrador d'Organ i que l'usuari es administrador de l'Organ associat al pagadorPostal a eliminar.

			PagadorPostalEntity pagadorPostalEntity = entityComprovarHelper.comprovarPagadorPostal(id);
			pagadorPostalReposity.delete(id);
			return conversioTipusHelper.convertir(
					pagadorPostalEntity, 
					OperadorPostalDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public OperadorPostalDto findById(Long id) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			PagadorPostalEntity pagadorPostalEntity = entityComprovarHelper.comprovarPagadorPostal(id);
			
			return conversioTipusHelper.convertir(
					pagadorPostalEntity, 
					OperadorPostalDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<OperadorPostalTableItemDto> findAmbFiltrePaginat(Long entitatId, OperadorPostalFiltreDto filtre,
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
			mapeigPropietatsOrdenacio.put("organismePagador", new String[] {"organismePagadorCodi"});
			Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio);
			Page<PagadorPostalEntity> pageOpearadorsPostals;
	
			List<String> organsFills = null;
			if (filtre.getOrganGestorId() != null) {
				OrganGestorEntity organGestor = entityComprovarHelper.comprovarOrganGestor(
						entitat, 
						filtre.getOrganGestorId());
				organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(
						entitat.getDir3Codi(), 
						organGestor.getCodi());
				
				pageOpearadorsPostals = pagadorPostalReposity.findByCodiDir3AndNumContacteNotNullFiltrePaginatAndEntitatWithOrgan(
						filtre.getOrganismePagador() == null || filtre.getOrganismePagador().isEmpty(),
						filtre.getOrganismePagador(),
						filtre.getContracteNum() == null || filtre.getContracteNum().isEmpty(),
						filtre.getContracteNum(),
						organsFills,
						entitat,
						pageable);
			}else {
				pageOpearadorsPostals = pagadorPostalReposity.findByCodiDir3AndNumContacteNotNullFiltrePaginatAndEntitat(
						filtre.getOrganismePagador() == null || filtre.getOrganismePagador().isEmpty(),
						filtre.getOrganismePagador(),
						filtre.getContracteNum() == null || filtre.getContracteNum().isEmpty(),
						filtre.getContracteNum(),
						entitat,
						pageable);
			}
			
			return paginacioHelper.toPaginaDto(
					pageOpearadorsPostals,
					OperadorPostalTableItemDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<OperadorPostalDto> findAll() {
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
						OperadorPostalDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<IdentificadorTextDto> findAllIdentificadorText() {
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
					IdentificadorTextDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<OperadorPostalDto> findByEntitat(Long entitatId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta els pagadors postal de l'entitat: " + entitatId);
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			List<PagadorPostalEntity> pagadorsPostal = pagadorPostalReposity.findByEntitat(entitat);
			
			return conversioTipusHelper.convertirList(
					pagadorsPostal,
					OperadorPostalDto.class);
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
					OperadorPostalDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	

	@Override
	public PaginaDto<OperadorPostalDto> findAllPaginat(PaginacioParamsDto paginacioParams) {
		return null;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(EntitatServiceImpl.class);

}
