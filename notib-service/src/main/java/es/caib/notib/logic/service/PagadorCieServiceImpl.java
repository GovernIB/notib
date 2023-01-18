package es.caib.notib.logic.service;

import com.codahale.metrics.Timer;
import com.google.common.base.Strings;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.IdentificadorTextDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.cie.CieDataDto;
import es.caib.notib.logic.intf.dto.cie.CieDto;
import es.caib.notib.logic.intf.dto.cie.CieFiltreDto;
import es.caib.notib.logic.intf.dto.cie.CieTableItemDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.service.PagadorCieService;
import es.caib.notib.logic.intf.service.PermisosService;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.cie.PagadorCieEntity;
import es.caib.notib.logic.helper.*;
import es.caib.notib.persist.entity.cie.PagadorPostalEntity;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.PagadorCieRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
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
	private OrganGestorRepository organGestorRepository;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private OrganigramaHelper organigramaHelper;
	@Resource
	private MetricsHelper metricsHelper;
	@Resource
	private PermisosService permisosService;

	@Override
	@Transactional
	public CieDto upsert(Long entitatId, CieDataDto cie) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Creant un nou pagador cie (pagador=" + cie + ")");
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			OrganGestorEntity organGestor = null;

			if (!Strings.isNullOrEmpty(cie.getOrganismePagadorCodi())) {
				organGestor = entityComprovarHelper.comprovarOrganGestor(entitat, cie.getOrganismePagadorCodi());
			}
			PagadorCieEntity p;
			if (cie.getId() == null) {
				p = PagadorCieEntity.builder().organGestor(organGestor).nom(cie.getNom()).contracteDataVig(cie.getContracteDataVig()).entitat(entitat).build();
			} else {
				log.debug("Actualitzant pagador cie (pagador=" + cie + ")");
				p = entityComprovarHelper.comprovarPagadorCie(cie.getId());
				if (organGestor != null) {
					p.setOrganGestor(organGestor);
				}
				p.setContracteDataVig(cie.getContracteDataVig());
			}
			PagadorCieEntity pagadorCieEntity = pagadorCieReposity.save(p);
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
	public List<IdentificadorTextDto> findPagadorsByEntitat(EntitatDto entitat) {

		EntitatEntity e = entityComprovarHelper.comprovarEntitat(entitat.getId());
		List<IdentificadorTextDto> pagadors = findNoCaducatsByEntitat(entitat);
		PagadorCieEntity pagador = pagadorCieReposity.obtenirPagadorsEntitat(e);
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

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de tots els pagadors postals");
			var e = entityComprovarHelper.comprovarEntitat(entitat.getId());
//			entityComprovarHelper.comprovarPermisos(entitat.getId(), true, true, false);
			var p = pagadorCieReposity.findByEntitatAndContracteDataVigGreaterThanEqual(e, new Date());
			return conversioTipusHelper.convertirList(p, IdentificadorTextDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<IdentificadorTextDto> findNoCaducatsByEntitatAndOrgan(EntitatDto entitat, String organCodi, boolean isAdminOrgan) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de tots els pagadors postals");
			EntitatEntity e = entityComprovarHelper.comprovarEntitat(entitat.getId());
			OrganGestorEntity o = organGestorRepository.findByCodi(organCodi);
//			entityComprovarHelper.comprovarPermisos(entitat.getId(), true, true, false);
			List<PagadorCieEntity> pagadors = pagadorCieReposity.findByEntitatAndOrganGestorAndContracteDataVigGreaterThanEqual(e, o, new Date());
			if (!e.getDir3Codi().equals(organCodi)) {
				List<PagadorCieEntity> pagadorsPare = findOperadorsPare(entitat, o.getCodiPare());
				pagadors.addAll(pagadorsPare);
			}
			String usr = SecurityContextHolder.getContext().getAuthentication().getName();
			List<PagadorCieEntity> p = new ArrayList<>();
			for (PagadorCieEntity pagador : pagadors) {
				if (isAdminOrgan && !permisosService.hasUsrPermisOrgan(entitat.getId(), usr, pagador.getOrganGestor().getCodi(), PermisEnum.ADMIN)) {
					continue;
				}
				p.add(pagador);
			}

			return conversioTipusHelper.convertirList(p, IdentificadorTextDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private List<PagadorCieEntity> findOperadorsPare(EntitatDto entitat, String codi) {

		List<PagadorCieEntity> operadors = new ArrayList<>();
		OrganGestorEntity o = organGestorRepository.findByCodi(codi);
		EntitatEntity e = entityComprovarHelper.comprovarEntitat(entitat.getId());
		List<PagadorCieEntity> p = pagadorCieReposity.findByEntitatAndOrganGestorAndContracteDataVigGreaterThanEqual(e, o, new Date());
		if (!Strings.isNullOrEmpty(o.getCodiPare()) && !o.getCodi().equals(entitat.getDir3Codi()) && !"A99999999".equals(o.getCodiPare())) {
			operadors = findOperadorsPare(entitat, o.getCodiPare());
		}
		p.addAll(operadors);
		return p;
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
