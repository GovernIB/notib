package es.caib.notib.logic.service;

import com.google.common.base.Strings;
import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.EntityComprovarHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.helper.OrganigramaHelper;
import es.caib.notib.logic.helper.PaginacioHelper;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.IdentificadorTextDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalDataDto;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalDto;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalFiltreDto;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalTableItemDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.service.OperadorPostalService;
import es.caib.notib.logic.intf.service.PermisosService;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.cie.PagadorPostalEntity;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.PagadorPostalRepository;
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
 * Implementació del servei de gestió de pagadors postals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
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
	@Resource
	private PermisosService permisosService;

	private static final String LOG_MSG = "Consulta de tots els pagadors postals";
	
	@Override
	@Transactional
	public OperadorPostalDto upsert(Long entitatId, OperadorPostalDataDto postal) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Creant un nou pagador postal (pagador=" + postal + ")");
			var entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			OrganGestorEntity organGestor = null;
			if (!Strings.isNullOrEmpty(postal.getOrganismePagadorCodi())) {
				organGestor = entityComprovarHelper.comprovarOrganGestor(entitat, postal.getOrganismePagadorCodi());
			}
			PagadorPostalEntity pagador;
			if (postal.getId() == null) {
				pagador = PagadorPostalEntity.builder().contracteNum(postal.getContracteNum()).nom(postal.getNom()).contracteDataVig(postal.getContracteDataVig())
						.facturacioClientCodi(postal.getFacturacioClientCodi()).entitat(entitat).organGestor(organGestor).build();
			} else {
				log.debug("Actualitzant pagador postal (pagador=" + postal + ")");
				pagador = entityComprovarHelper.comprovarPagadorPostal(postal.getId());
				if (organGestor != null) {
					pagador.setOrganGestor(organGestor);
				}
				pagador.setNom(postal.getNom());
				pagador.setContracteNum(postal.getContracteNum());
				pagador.setContracteDataVig(postal.getContracteDataVig());
				pagador.setFacturacioClientCodi(postal.getFacturacioClientCodi());
			}
 			var pagadorPostalEntity = pagadorPostalReposity.save(pagador);
			return conversioTipusHelper.convertir(pagadorPostalEntity, OperadorPostalDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public OperadorPostalDto delete(Long id) throws NotFoundException {

		var timer = metricsHelper.iniciMetrica();
		try {
			//TODO: Si es tothom comprovar que és administrador d'Organ i que l'usuari es administrador de l'Organ associat al pagadorPostal a eliminar.
			var pagadorPostalEntity = entityComprovarHelper.comprovarPagadorPostal(id);
			pagadorPostalReposity.deleteById(id);
			return conversioTipusHelper.convertir(pagadorPostalEntity, OperadorPostalDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public OperadorPostalDto findById(Long id) {

		var timer = metricsHelper.iniciMetrica();
		try {
			var pagadorPostalEntity = entityComprovarHelper.comprovarPagadorPostal(id);
			return conversioTipusHelper.convertir(pagadorPostalEntity, OperadorPostalDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<OperadorPostalTableItemDto> findAmbFiltrePaginat(Long entitatId, OperadorPostalFiltreDto filtre, PaginacioParamsDto paginacioParams) {

		var timer = metricsHelper.iniciMetrica();
		try {
			entityComprovarHelper.comprovarPermisos(null, true, true, true);
			var entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<>();
			mapeigPropietatsOrdenacio.put("organismePagador", new String[] {"organGestor"});
			var pageable = paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio);
			Page<PagadorPostalEntity> pageOpearadorsPostals;
			List<String> organsFills = null;
			var organismePagadorNull = Strings.isNullOrEmpty(filtre.getOrganismePagador());
			var contracteNull = Strings.isNullOrEmpty(filtre.getContracteNum());
			if (filtre.getOrganGestorId() != null) {
				var organGestor = entityComprovarHelper.comprovarOrganGestor(entitat, filtre.getOrganGestorId());
				organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitat.getDir3Codi(), organGestor.getCodi());
				pageOpearadorsPostals = pagadorPostalReposity.findByCodiDir3AndNumContacteNotNullFiltrePaginatAndEntitatWithOrgan(
						organismePagadorNull,
						filtre.getOrganismePagador(),
						contracteNull,
						filtre.getContracteNum(),
						organsFills,
						entitat,
						pageable);
			} else {
				pageOpearadorsPostals = pagadorPostalReposity.findByCodiDir3AndNumContacteNotNullFiltrePaginatAndEntitat(
						organismePagadorNull,
						filtre.getOrganismePagador(),
						contracteNull,
						filtre.getContracteNum(),
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

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug(LOG_MSG);
			entityComprovarHelper.comprovarPermisos(null, true, true, false);
			return conversioTipusHelper.convertirList(pagadorPostalReposity.findAll(), OperadorPostalDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<IdentificadorTextDto> findAllIdentificadorText() {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug(LOG_MSG);
			entityComprovarHelper.comprovarPermisos(null, true, true, false);
			return conversioTipusHelper.convertirList(pagadorPostalReposity.findByContracteDataVigGreaterThanEqual(new Date()), IdentificadorTextDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<IdentificadorTextDto> findPagadorsByEntitat(EntitatDto entitat) {

		var e = entityComprovarHelper.comprovarEntitat(entitat.getId());
		var pagadors = findNoCaducatsByEntitat(entitat);
		var pagador = pagadorPostalReposity.obtenirPagadorsEntitat(e);
		if (pagador == null) {
			return pagadors;
		}
		var i = conversioTipusHelper.convertir(pagador, IdentificadorTextDto.class);
		if (pagadors.contains(i)) {
			return pagadors;
		}
		i.setIcona("fa fa-warning text-danger");
		pagadors.add(0, i);
		return pagadors;
	}

	@Override
	@Transactional(readOnly = true)
	public List<IdentificadorTextDto> findByEntitat(EntitatDto entitat) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug(LOG_MSG);
			var e = entityComprovarHelper.comprovarEntitat(entitat.getId());
			var p = pagadorPostalReposity.findByEntitat(e);
			return conversioTipusHelper.convertirList(p, IdentificadorTextDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<IdentificadorTextDto> findNoCaducatsByEntitat(EntitatDto entitat) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug(LOG_MSG);
			var e = entityComprovarHelper.comprovarEntitat(entitat.getId());
			var p = pagadorPostalReposity.findByEntitatAndContracteDataVigGreaterThanEqual(e, new Date());
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
			log.debug(LOG_MSG);
			var e = entityComprovarHelper.comprovarEntitat(entitat.getId());
			var o = organGestorRepository.findByEntitatAndCodi(e, organCodi);
			var pagadors = pagadorPostalReposity.findByEntitatAndOrganGestorAndContracteDataVigGreaterThanEqual(e, o, new Date());
			if (!e.getDir3Codi().equals(organCodi)) {
				var pagadorsPare = findOperadorsPareNoCaducats(entitat, o.getCodiPare());
				pagadors.addAll(pagadorsPare);
			}
			var usr = SecurityContextHolder.getContext().getAuthentication().getName();
			List<PagadorPostalEntity> p = new ArrayList<>();
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

	private List<PagadorPostalEntity> findOperadorsPareNoCaducats(EntitatDto entitat, String codi) {

		List<PagadorPostalEntity> operadors = new ArrayList<>();
		var e = entityComprovarHelper.comprovarEntitat(entitat.getId());
		var o = organGestorRepository.findByEntitatAndCodi(e, codi);
		var p = pagadorPostalReposity.findByEntitatAndOrganGestorAndContracteDataVigGreaterThanEqual(e, o, new Date());
		if (!Strings.isNullOrEmpty(o.getCodiPare()) && !o.getCodi().equals(entitat.getDir3Codi()) && !"A99999999".equals(o.getCodiPare())) {
			operadors = findOperadorsPareNoCaducats(entitat, o.getCodiPare());
		}
		p.addAll(operadors);
		return p;
	}

	@Override
	public List<IdentificadorTextDto> findByEntitatAndOrgan(EntitatDto entitat, String organCodi, boolean isAdminOrgan) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de tots els pagadors postals");
			var e = entityComprovarHelper.comprovarEntitat(entitat.getId());
			var o = organGestorRepository.findByEntitatAndCodi(e, organCodi);
			var pagadors = pagadorPostalReposity.findByEntitatAndOrganGestor(e, o);
			if (!e.getDir3Codi().equals(organCodi)) {
				var pagadorsPare = findOperadorsPare(entitat, o.getCodiPare());
				pagadors.addAll(pagadorsPare);
			}
			var usr = SecurityContextHolder.getContext().getAuthentication().getName();
			List<PagadorPostalEntity> p = new ArrayList<>();
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

	private List<PagadorPostalEntity> findOperadorsPare(EntitatDto entitat, String codi) {

		List<PagadorPostalEntity> operadors = new ArrayList<>();
		var e = entityComprovarHelper.comprovarEntitat(entitat.getId());
		var o = organGestorRepository.findByEntitatAndCodi(e, codi);
		var p = pagadorPostalReposity.findByEntitatAndOrganGestor(e, o);
		if (!Strings.isNullOrEmpty(o.getCodiPare()) && !o.getCodi().equals(entitat.getDir3Codi()) && !"A99999999".equals(o.getCodiPare())) {
			operadors = findOperadorsPare(entitat, o.getCodiPare());
		}
		p.addAll(operadors);
		return p;
	}

	@Override
	@Transactional(readOnly = true)
	public List<OperadorPostalDto> findByEntitat(Long entitatId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta els pagadors postal de l'entitat: " + entitatId);
			var entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			var pagadorsPostal = pagadorPostalReposity.findByEntitat(entitat);
			return conversioTipusHelper.convertirList(pagadorsPostal, OperadorPostalDto.class);
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
			var pagadorsPostal = pagadorPostalReposity.findByEntitatIdAndOrganGestorCodiIn(entitat.getId(), organsFills);
			return conversioTipusHelper.convertirList(pagadorsPostal, OperadorPostalDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public PaginaDto<OperadorPostalDto> findAllPaginat(PaginacioParamsDto paginacioParams) {
		return null;
	}
	

}
