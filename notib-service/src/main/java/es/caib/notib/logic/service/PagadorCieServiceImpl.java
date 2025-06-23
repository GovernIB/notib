package es.caib.notib.logic.service;

import com.google.common.base.Strings;
import es.caib.notib.logic.helper.ConfigHelper;
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
import es.caib.notib.logic.intf.dto.cie.CieDataDto;
import es.caib.notib.logic.intf.dto.cie.CieDto;
import es.caib.notib.logic.intf.dto.cie.CieFiltreDto;
import es.caib.notib.logic.intf.dto.cie.CieTableItemDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.service.PagadorCieService;
import es.caib.notib.logic.intf.service.PermisosService;
import es.caib.notib.logic.objectes.StringEncriptat;
import es.caib.notib.logic.utils.EncryptionUtil;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.cie.PagadorCieEntity;
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
public class PagadorCieServiceImpl implements PagadorCieService {

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PagadorCieRepository pagadorCieReposity;
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
    @Resource
    private ConfigHelper configHelper;

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
			OrganGestorEntity organEmisor = null;
			if (!Strings.isNullOrEmpty(cie.getOrganismePagadorCodi())) {
				organEmisor = entityComprovarHelper.comprovarOrganGestor(entitat, cie.getOrganismeEmisorCodi());
			}
			String apiKey = cie.getApiKey();
			StringEncriptat encriptat = null;
			if (!Strings.isNullOrEmpty(apiKey)) {
				var encryptor = new EncryptionUtil(configHelper.getConfig("es.caib.notib.plugin.cie.encriptor.key"));
				encriptat = encryptor.encrypt(apiKey);
			}
			PagadorCieEntity p;
			if (cie.getId() == null) {
				p = PagadorCieEntity.builder()
						.organGestor(organGestor)
						.nom(cie.getNom())
						.contracteDataVig(cie.getContracteDataVig())
						.entitat(entitat)
						.cieExtern(cie.isCieExtern())
						.apiKey(encriptat != null ? encriptat.getString() : null)
						.salt(encriptat != null ? encriptat.getSalt() : null)
						.organEmisor(organEmisor)
						.build();
			} else {
				log.debug("Actualitzant pagador cie (pagador=" + cie + ")");
				p = entityComprovarHelper.comprovarPagadorCie(cie.getId());
				p.setNom(!Strings.isNullOrEmpty(cie.getNom()) ? cie.getNom() : p.getNom());
				p.setOrganGestor(organGestor != null ? organGestor : p.getOrganGestor());
				p.setOrganEmisor(organEmisor != null ? organEmisor : p.getOrganGestor());
				if (cie.isCieExtern()) {
					p.setCieExtern(true);
					p.setApiKey(encriptat != null ? encriptat.getString() : p.getApiKey());
					p.setSalt(encriptat != null ? encriptat.getSalt() : p.getSalt());
				} else {
					p.setCieExtern(false);
					p.setApiKey(null);
					p.setSalt(null);
				}
				var contracteDataVig = cie.getContracteDataVig() != null ? cie.getContracteDataVig() : p.getContracteDataVig();
				p.setContracteDataVig(contracteDataVig);
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
			var cie = conversioTipusHelper.convertir(pagadorCieEntity, CieDto.class);
			if (!Strings.isNullOrEmpty(pagadorCieEntity.getApiKey())) {
				var encript = new EncryptionUtil(configHelper.getConfig("es.caib.notib.plugin.cie.encriptor.key"), pagadorCieEntity.getSalt());
				cie.setApiKey(encript.decrypt(pagadorCieEntity.getApiKey()));
			}
			return cie;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<CieTableItemDto> findAmbFiltrePaginat(Long entitatId, CieFiltreDto filtre, PaginacioParamsDto paginacioParams) {

		var timer = metricsHelper.iniciMetrica();
		try {
			entityComprovarHelper.comprovarPermisos(null, true, true, true, true);
			var entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			Page<PagadorCieEntity> pagadorCie = null;
			Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<>();
			mapeigPropietatsOrdenacio.put("organismePagador", new String[] {"organGestor"});
			mapeigPropietatsOrdenacio.put("organismeEmisor", new String[] {"organEmisor"});
			var pageable = paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio);
			List<String> organsFills;
			if (filtre.getOrganGestorId() != null) {
				OrganGestorEntity organGestor = entityComprovarHelper.comprovarOrganGestor(entitat, filtre.getOrganGestorId());
				organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitat.getDir3Codi(), organGestor.getCodi());
				pagadorCie = pagadorCieReposity.findByCodiDir3NotNullFiltrePaginatAndEntitatWithOrgan(
						filtre.getOrganismePagadorCodi() == null || filtre.getOrganismePagadorCodi().isEmpty(),
						filtre.getOrganismePagadorCodi(), organsFills, entitat, pageable);
			} else {
				pagadorCie = pagadorCieReposity.findByCodiDir3NotNullFiltrePaginatAndEntitat(
						filtre.getOrganismePagadorCodi() == null || filtre.getOrganismePagadorCodi().isEmpty(),
						filtre.getOrganismePagadorCodi(), entitat, pageable);
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
			entityComprovarHelper.comprovarPermisos(null, true, true, false, true);
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
			entityComprovarHelper.comprovarPermisos(null, true, true, false, true);
			return conversioTipusHelper.convertirList(pagadorCieReposity.findByContracteDataVigGreaterThanEqual(new Date()), IdentificadorTextDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<IdentificadorTextDto> findPagadorsByEntitat(EntitatDto entitat) {

		var e = entityComprovarHelper.comprovarEntitat(entitat.getId());
		var pagadors = findNoCaducatsByEntitat(entitat);
		var pagador = pagadorCieReposity.obtenirPagadorsEntitat(e);
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
			var e = entityComprovarHelper.comprovarEntitat(entitat.getId());
			var p = pagadorCieReposity.findByEntitat(e);
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
			var e = entityComprovarHelper.comprovarEntitat(entitat.getId());
			var p = pagadorCieReposity.findByEntitatAndContracteDataVigGreaterThanEqual(e, new Date());
			return conversioTipusHelper.convertirList(p, IdentificadorTextDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<IdentificadorTextDto> findByEntitatAndOrgan(EntitatDto entitat, String organCodi, boolean isAdminOrgan) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de tots els pagadors postals");
			var e = entityComprovarHelper.comprovarEntitat(entitat.getId());
			var o = organGestorRepository.findByEntitatAndCodi(e, organCodi);
			var pagadors = pagadorCieReposity.findByEntitatAndOrganGestor(e, o);
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
		var o = organGestorRepository.findByEntitatAndCodi(e, codi);
		var p = pagadorCieReposity.findByEntitatAndOrganGestor(e, o);
		if (!Strings.isNullOrEmpty(o.getCodiPare()) && !o.getCodi().equals(entitat.getDir3Codi()) && !"A99999999".equals(o.getCodiPare())) {
			operadors = findOperadorsPare(entitat, o.getCodiPare());
		}
		p.addAll(operadors);
		return p;
	}

	@Override
	@Transactional(readOnly = true)
	public List<IdentificadorTextDto> findNoCaducatsByEntitatAndOrgan(EntitatDto entitat, String organCodi, boolean isAdminOrgan) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de tots els pagadors postals");
			var e = entityComprovarHelper.comprovarEntitat(entitat.getId());
			var o = organGestorRepository.findByEntitatAndCodi(e, organCodi);
			var pagadors = pagadorCieReposity.findByEntitatAndOrganGestorAndContracteDataVigGreaterThanEqual(e, o, new Date());
			if (!e.getDir3Codi().equals(organCodi)) {
				var pagadorsPare = findOperadorsPareNoCaducats(entitat, o.getCodiPare());
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

	private List<PagadorCieEntity> findOperadorsPareNoCaducats(EntitatDto entitat, String codi) {

		List<PagadorCieEntity> operadors = new ArrayList<>();
		var e = entityComprovarHelper.comprovarEntitat(entitat.getId());
		var o = organGestorRepository.findByEntitatAndCodi(e, codi);
		var p = pagadorCieReposity.findByEntitatAndOrganGestorAndContracteDataVigGreaterThanEqual(e, o, new Date());
		if (!Strings.isNullOrEmpty(o.getCodiPare()) && !o.getCodi().equals(entitat.getDir3Codi()) && !"A99999999".equals(o.getCodiPare())) {
			operadors = findOperadorsPareNoCaducats(entitat, o.getCodiPare());
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

//	@Transactional(readOnly = true)
//	@Override
//	public boolean existeixCieByEntitatAndOrganGestor(String organGestor) {
//
//		var organ = organGestorRepository.findByCodi(organGestor);
//		var cie = pagadorCieReposity.findByEntitatAndOrganGestor(organ.getEntitat(), organ);
//		return cie != null && !cie.isEmpty();
//	}

	@Override
	public PaginaDto<CieDto> findAllPaginat(PaginacioParamsDto paginacioParams) {
		return null;
	}

}
