package es.caib.notib.core.service;

import com.codahale.metrics.Timer;
import com.google.common.base.Strings;
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
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.core.repository.PagadorPostalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
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
	private OrganGestorRepository organGestorRepository;
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
	public OperadorPostalDto upsert(Long entitatId, OperadorPostalDataDto postal) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Creant un nou pagador postal (pagador=" + postal + ")");
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			OrganGestorEntity organGestor = null;
			if (!Strings.isNullOrEmpty(postal.getOrganismePagadorCodi())) {
				organGestor = entityComprovarHelper.comprovarOrganGestor(entitat, postal.getOrganismePagadorCodi());
			}
			PagadorPostalEntity pagador;
			if (postal.getId() == null) {
				pagador = PagadorPostalEntity.builder().contracteNum(postal.getContracteNum()).nom(postal.getNom()).contracteDataVig(postal.getContracteDataVig())
						.facturacioClientCodi(postal.getFacturacioClientCodi()).entitat(entitat).organGestor(organGestor).build();
			} else {
				logger.debug("Actualitzant pagador postal (pagador=" + postal + ")");
				pagador = entityComprovarHelper.comprovarPagadorPostal(postal.getId());
				if (organGestor != null) {
					pagador.setOrganGestor(organGestor);
				}
				pagador.setContracteNum(postal.getContracteNum());
				pagador.setContracteDataVig(postal.getContracteDataVig());
				pagador.setFacturacioClientCodi(pagador.getFacturacioClientCodi());
			}
 			PagadorPostalEntity pagadorPostalEntity = pagadorPostalReposity.save(pagador);
			return conversioTipusHelper.convertir(pagadorPostalEntity, OperadorPostalDto.class);
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
	public PaginaDto<OperadorPostalTableItemDto> findAmbFiltrePaginat(Long entitatId, OperadorPostalFiltreDto filtre, PaginacioParamsDto paginacioParams) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			entityComprovarHelper.comprovarPermisos(null, true, true, true);
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();
			mapeigPropietatsOrdenacio.put("organismePagador", new String[] {"organismePagadorCodi"});
			Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio);
			Page<PagadorPostalEntity> pageOpearadorsPostals;
	
			List<String> organsFills = null;
			if (filtre.getOrganGestorId() != null) {
				OrganGestorEntity organGestor = entityComprovarHelper.comprovarOrganGestor(entitat, filtre.getOrganGestorId());
				organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitat.getDir3Codi(), organGestor.getCodi());
				
				pageOpearadorsPostals = pagadorPostalReposity.findByCodiDir3AndNumContacteNotNullFiltrePaginatAndEntitatWithOrgan(
						filtre.getOrganismePagador() == null || filtre.getOrganismePagador().isEmpty(),
						filtre.getOrganismePagador(),
						filtre.getContracteNum() == null || filtre.getContracteNum().isEmpty(),
						filtre.getContracteNum(),
						organsFills,
						entitat,
						pageable);
			} else {
				pageOpearadorsPostals = pagadorPostalReposity.findByCodiDir3AndNumContacteNotNullFiltrePaginatAndEntitat(
						filtre.getOrganismePagador() == null || filtre.getOrganismePagador().isEmpty(),
						filtre.getOrganismePagador() != null ? filtre.getOrganismePagador() : "",
						filtre.getContracteNum() == null || filtre.getContracteNum().isEmpty(),
						filtre.getContracteNum() != null ? filtre.getContracteNum() : "",
						entitat,
						pageable);
			}
			return paginacioHelper.toPaginaDto(pageOpearadorsPostals, OperadorPostalTableItemDto.class);
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
					pagadorPostalReposity.findByContracteDataVigGreaterThanEqual(new Date()),
					IdentificadorTextDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<IdentificadorTextDto> findPagadorsByEntitat(EntitatDto entitat) {

		EntitatEntity e = entityComprovarHelper.comprovarEntitat(entitat.getId());
		List<IdentificadorTextDto> pagadors = findNoCaducatsByEntitat(entitat);
		PagadorPostalEntity pagador = pagadorPostalReposity.obtenirPagadorsEntitat(e);
		if (pagador == null) {
			return pagadors;
		}
		IdentificadorTextDto i = conversioTipusHelper.convertir(pagador, IdentificadorTextDto.class);
		if (!pagadors.contains(i)) {
			i.setIcona("fa fa-warning text-danger");
			pagadors.add(0, i);
		}
		return pagadors;
	}

	@Override
	@Transactional(readOnly = true)
	public List<IdentificadorTextDto> findNoCaducatsByEntitat(EntitatDto entitat) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de tots els pagadors postals");
			EntitatEntity e = entityComprovarHelper.comprovarEntitat(entitat.getId());
//			entityComprovarHelper.comprovarPermisos(entitat.getId(), true, true, false);
			List<PagadorPostalEntity> p = pagadorPostalReposity.findByEntitatAndContracteDataVigGreaterThanEqual(e, new Date());
			return conversioTipusHelper.convertirList(p, IdentificadorTextDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<IdentificadorTextDto> findNoCaducatsByEntitatAndOrgan(EntitatDto entitat, String organCodi) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de tots els pagadors postals");
			EntitatEntity e = entityComprovarHelper.comprovarEntitat(entitat.getId());
			OrganGestorEntity o = organGestorRepository.findByCodi(organCodi);

//			entityComprovarHelper.comprovarPermisos(entitat.getId(), true, true, false);
 			List<PagadorPostalEntity> p = pagadorPostalReposity.findByEntitatAndOrganGestorAndContracteDataVigGreaterThanEqual(e, o, new Date());
			List<PagadorPostalEntity> pagadorsPare = findOperadorsPare(entitat, o.getCodiPare());
			p.addAll(pagadorsPare);
			return conversioTipusHelper.convertirList(p, IdentificadorTextDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private List<PagadorPostalEntity> findOperadorsPare(EntitatDto entitat, String codi) {


		List<PagadorPostalEntity> operadors = new ArrayList<>();
		OrganGestorEntity o = organGestorRepository.findByCodi(codi);
		EntitatEntity e = entityComprovarHelper.comprovarEntitat(entitat.getId());
		List<PagadorPostalEntity> p = pagadorPostalReposity.findByEntitatAndOrganGestorAndContracteDataVigGreaterThanEqual(e, o, new Date());
		if (!Strings.isNullOrEmpty(o.getCodiPare()) && !o.getCodi().equals(entitat.getDir3Codi()) ) {
			operadors = findOperadorsPare(entitat, o.getCodiPare());
		}
		p.addAll(operadors);
		return p;
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
