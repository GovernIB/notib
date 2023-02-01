package es.caib.notib.logic.service;

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
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.cie.PagadorCieEntity;
import es.caib.notib.logic.helper.*;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.PagadorCieRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Creant un nou pagador cie (pagador=" + cie + ")");
			var entitat = entityComprovarHelper.comprovarEntitat(entitatId);
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
			var pagadorCieEntity = pagadorCieReposity.save(p);
			return conversioTipusHelper.convertir(pagadorCieEntity, CieDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public CieDto delete(Long id) throws NotFoundException {

		var timer = metricsHelper.iniciMetrica();
		try {
			//TODO: Si es tothom comprovar que és administrador d'Organ i que l'usuari es administrador de l'Organ associat al pagadorCIE a eliminar.
			var pagadorCieEntity = entityComprovarHelper.comprovarPagadorCie(id);
			pagadorCieReposity.deleteById(id);
			return conversioTipusHelper.convertir(pagadorCieEntity, CieDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public CieDto findById(Long id) {

		var timer = metricsHelper.iniciMetrica();
		try {
			var pagadorCieEntity = entityComprovarHelper.comprovarPagadorCie(id);
			return conversioTipusHelper.convertir(pagadorCieEntity, CieDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<CieTableItemDto> findAmbFiltrePaginat(Long entitatId, CieFiltreDto filtre, PaginacioParamsDto paginacioParams) {

		var timer = metricsHelper.iniciMetrica();
		try {
			entityComprovarHelper.comprovarPermisos(null, true, true, true);
			var entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			Page<PagadorCieEntity> pagadorCie;
			Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<>();
			mapeigPropietatsOrdenacio.put("organismePagador", new String[] {"organismePagadorCodi"});
			var pageable = paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio);
			List<String> organsFills;
			if (filtre.getOrganGestorId() != null) {
				var organGestor = entityComprovarHelper.comprovarOrganGestor(entitat, filtre.getOrganGestorId());
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

		var timer = metricsHelper.iniciMetrica();
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

		var timer = metricsHelper.iniciMetrica();
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

		var pagadors = findNoCaducatsByEntitat(entitat);
		var e = entityComprovarHelper.comprovarEntitat(entitat.getId());
		var pagador = pagadorCieReposity.obtenirPagadorsEntitat(e);
		if (pagador == null) {
			return pagadors;
		}
		var i = conversioTipusHelper.convertir(pagador, IdentificadorTextDto.class);
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

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de tots els pagadors postals");
			var e = entityComprovarHelper.comprovarEntitat(entitat.getId());
			var o = organGestorRepository.findByCodi(organCodi);
//			entityComprovarHelper.comprovarPermisos(entitat.getId(), true, true, false);
			var pagadors = pagadorCieReposity.findByEntitatAndOrganGestorAndContracteDataVigGreaterThanEqual(e, o, new Date());
			if (!e.getDir3Codi().equals(organCodi)) {
				var pagadorsPare = findOperadorsPare(entitat, o.getCodiPare());
				pagadors.addAll(pagadorsPare);
			}
			var usr = SecurityContextHolder.getContext().getAuthentication().getName();
			List<PagadorCieEntity> p = new ArrayList<>();
			for (var pagador : pagadors) {
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
		var e = entityComprovarHelper.comprovarEntitat(entitat.getId());
		var o = organGestorRepository.findByCodi(codi);
		var p = pagadorCieReposity.findByEntitatAndOrganGestorAndContracteDataVigGreaterThanEqual(e, o, new Date());
		if (!Strings.isNullOrEmpty(o.getCodiPare()) && !o.getCodi().equals(entitat.getDir3Codi()) && !"A99999999".equals(o.getCodiPare())) {
			operadors = findOperadorsPare(entitat, o.getCodiPare());
		}
		p.addAll(operadors);
		return p;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CieDto> findByEntitat(Long entitatId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta els pagadors postal de l'entitat: " + entitatId);
			var entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			var pagadorsCie = pagadorCieReposity.findByEntitat(entitat);
			return conversioTipusHelper.convertirList(pagadorsCie, CieDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Object findByEntitatAndOrganGestor(EntitatDto entitat, OrganGestorDto organGestor) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta els pagadors postal de l'entitat: " + entitat.getId() + " i òrgan gestor: " + organGestor.getCodi());
			var organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitat.getDir3Codi(), organGestor.getCodi());
			var pagadorsCie = pagadorCieReposity.findByEntitatIdAndOrganGestorCodiIn(entitat.getId(), organsFills);
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
