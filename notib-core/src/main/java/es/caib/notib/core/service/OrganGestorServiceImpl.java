package es.caib.notib.core.service;

import com.codahale.metrics.Timer;
import com.google.common.base.Strings;
import es.caib.notib.core.api.dto.AccioParam;
import es.caib.notib.core.api.dto.Arbre;
import es.caib.notib.core.api.dto.ArbreNode;
import es.caib.notib.core.api.dto.CodiValorDto;
import es.caib.notib.core.api.dto.CodiValorEstatDto;
import es.caib.notib.core.api.dto.CodiValorOrganGestorComuDto;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.core.api.dto.IntegracioInfo;
import es.caib.notib.core.api.dto.LlibreDto;
import es.caib.notib.core.api.dto.OficinaDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.PermisEnum;
import es.caib.notib.core.api.dto.ProgresActualitzacioDto;
import es.caib.notib.core.api.dto.ProgresActualitzacioDto.TipusInfo;
import es.caib.notib.core.api.dto.RolEnumDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.core.api.dto.organisme.OrganGestorFiltreDto;
import es.caib.notib.core.api.dto.organisme.OrganismeDto;
import es.caib.notib.core.api.dto.organisme.PrediccioSincronitzacio;
import es.caib.notib.core.api.dto.organisme.UnitatOrganitzativaDto;
import es.caib.notib.core.api.exception.NoPermisosException;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.exception.SistemaExternException;
import es.caib.notib.core.api.service.OrganGestorService;
import es.caib.notib.core.api.service.PermisosService;
import es.caib.notib.core.cacheable.OrganGestorCachable;
import es.caib.notib.core.cacheable.PermisosCacheable;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.GrupEntity;
import es.caib.notib.core.entity.OficinaEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.ProcSerEntity;
import es.caib.notib.core.entity.ProcSerOrganEntity;
import es.caib.notib.core.entity.cie.EntregaCieEntity;
import es.caib.notib.core.entity.cie.PagadorCieEntity;
import es.caib.notib.core.entity.cie.PagadorPostalEntity;
import es.caib.notib.core.helper.CacheHelper;
import es.caib.notib.core.helper.ConfigHelper;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.IntegracioHelper;
import es.caib.notib.core.helper.MessageHelper;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.helper.OrganGestorHelper;
import es.caib.notib.core.helper.OrganigramaHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.helper.ProcSerSyncHelper;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.EntregaCieRepository;
import es.caib.notib.core.repository.GrupRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.repository.OficinaRepository;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.core.repository.PagadorCieRepository;
import es.caib.notib.core.repository.PagadorPostalRepository;
import es.caib.notib.core.repository.ProcSerRepository;
import es.caib.notib.plugin.unitat.NodeDir3;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.bind.ValidationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementació del servei de gestió de òrgans gestors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Slf4j
@Service
public class OrganGestorServiceImpl implements OrganGestorService {

	@Autowired
	private EntitatRepository entitatRepository;

	@Autowired
	private PermisosService permisosService;
	@Resource
	private OrganGestorRepository organGestorRepository;
	@Resource
	private ProcSerRepository procSerRepository;
	@Resource
	private OficinaRepository oficinaRepository;
	@Resource
	private NotificacioRepository notificacioRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private CacheHelper cacheHelper;
	@Resource
	private OrganigramaHelper organigramaHelper;
	@Resource
	private MetricsHelper metricsHelper;
	@Resource
	private GrupRepository grupReposity;
	@Resource
	private PagadorPostalRepository pagadorPostalReposity;
	@Resource
	private PagadorCieRepository pagadorCieReposity;
	@Resource
	private OrganGestorHelper organGestorHelper;
	@Resource
	private ProcSerSyncHelper procSerSyncHelper;
	@Autowired
	private OrganGestorCachable organGestorCachable;
	@Resource
	private PermisosCacheable permisosCacheable;
	@Resource
	private ConfigHelper configHelper;
	@Autowired
	private EntregaCieRepository entregaCieRepository;
	@Autowired
	private PluginHelper pluginHelper;
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private IntegracioHelper integracioHelper;

	private List<OrganGestorDto> sotredOrgans = new ArrayList<>();


	private List<String> codisAmbPermis = new ArrayList<>();
	private boolean isAdminOrgan;
	private OrganGestorDto organActual;
	public static Map<String, ProgresActualitzacioDto> progresActualitzacio = new HashMap<String, ProgresActualitzacioDto>();

	@Getter
	private List<OrganGestorDto> organsList;

//	@Override
//	@Transactional
//	public OrganGestorDto create(OrganGestorDto dto) {
//
//		Timer.Context timer = metricsHelper.iniciMetrica();
//		try {
//
//			//TODO: Si es tothom comprovar que és administrador d'Organ i
//			//		que l'Organ que crea es fill d'almenys un dels Organs que administra
//
//			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(dto.getEntitatId());
//			OrganGestorEstatEnum estat = dto.getEstat() != null ? dto.getEstat() : OrganGestorEstatEnum.V;
//			Map<String, OrganismeDto> arbreUnitats = cacheHelper.findOrganigramaNodeByEntitat(entitat.getDir3Codi());
//			OrganismeDto node = arbreUnitats.get(dto.getCodi());
//			String codiPare = node != null ? node.getPare() : null;
//			OrganGestorEntity.OrganGestorEntityBuilder organGestorBuilder = OrganGestorEntity.builder(
//					dto.getCodi(),
//					dto.getNom(),
//					codiPare,
//					entitat,
//					dto.getLlibre(),
//					dto.getLlibreNom(),
//					dto.getOficina() != null ? dto.getOficina().getCodi() : null,
//					dto.getOficina() != null ? dto.getOficina().getNom() : null,
//					estat,
//					dto.getSir());
//			if (dto.isEntregaCieActiva()) {
//				EntregaCieEntity entregaCie = new EntregaCieEntity(dto.getCieId(), dto.getOperadorPostalId());
//				organGestorBuilder.entregaCie(entregaCieRepository.save(entregaCie));
//			}
//			return conversioTipusHelper.convertir(organGestorRepository.save(organGestorBuilder.build()), OrganGestorDto.class);
//		} finally {
//			metricsHelper.fiMetrica(timer);
//		}
//	}
//
//	@Override
//	@Transactional
//	public OrganGestorDto delete(
//			Long entitatId,
//			Long organId) {
//		Timer.Context timer = metricsHelper.iniciMetrica();
//		try {
//
//			//TODO: Si es tothom comprovar que és administrador d'Organ i
//			//		verificar que almenys un dels organs que administra es pare del que vol eliminar.
//
//			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
//					entitatId);
////					true,
////					false,
////					false);
//
//			OrganGestorEntity organGestorEntity = entityComprovarHelper.comprovarOrganGestor(
//					entitat,
//					organId);
//
//			// Eliminar permisos de l'òrgan
//			permisosHelper.deleteAcl(
//					organId,
//					OrganGestorEntity.class);
//			// Eliminar organ
//			organGestorRepository.delete(organGestorEntity);
//
//			return conversioTipusHelper.convertir(
//					organGestorEntity,
//					OrganGestorDto.class);
//		} finally {
//			metricsHelper.fiMetrica(timer);
//		}
//	}

	@Override
	@Transactional
	public OrganGestorDto update(OrganGestorDto dto) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			entityComprovarHelper.comprovarEntitat(dto.getEntitatId(), false, true, false);
			OrganGestorEntity organGestor = organGestorRepository.findOne(dto.getId());
			organGestor.updateOficina(dto.getOficina().getCodi(), dto.getOficina().getNom());
			EntregaCieEntity entregaCie = organGestor.getEntregaCie();
			if (dto.isEntregaCieActiva()) {
				if (entregaCie == null) {
					entregaCie = entregaCieRepository.save(new EntregaCieEntity(dto.getCieId(), dto.getOperadorPostalId()));
				} else {
					entregaCie.update(dto.getCieId(), dto.getOperadorPostalId());
				}
			}
			organGestor.updateEntregaCie(dto.isEntregaCieActiva() ? entregaCie : null);
			if (!dto.isEntregaCieActiva() && entregaCie != null) {
				entregaCieRepository.delete(entregaCie);
			}
			return conversioTipusHelper.convertir(organGestor, OrganGestorDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean organGestorEnUs(Long organId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			//Compravacions en ús
			OrganGestorEntity organGestor = organGestorRepository.findOne(organId);
			if (organGestor == null)
				return true; // No en ús pq no existeix!!

			if (OrganGestorEstatEnum.V.equals(organGestor.getEstat()))
				return true;

			if (notificacioRepository.countByOrganGestor(organGestor) > 0)
				return true;

			List<ProcSerEntity> procedimentsOrganGestor = procSerRepository.findByOrganGestorId(organId);
			if (procedimentsOrganGestor != null && !procedimentsOrganGestor.isEmpty())
				return true;

			List<GrupEntity> grupsByOrganGestor = grupReposity.findByOrganGestorId(organId);
			if (grupsByOrganGestor != null && !grupsByOrganGestor.isEmpty())
				return true;

			List<PagadorCieEntity> pagCiesByOrganGestor = pagadorCieReposity.findByOrganGestor(organGestor);
			if (pagCiesByOrganGestor != null && !pagCiesByOrganGestor.isEmpty())
				return true;

			List<PagadorPostalEntity> pagPostalByOrganGestor = pagadorPostalReposity.findByOrganGestorId(organId);
			if (pagPostalByOrganGestor != null && !pagPostalByOrganGestor.isEmpty())
				return true;

			return false;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findAll() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<OrganGestorEntity> organs = organGestorRepository.findAll();
			return conversioTipusHelper.convertirList(
					organs,
					OrganGestorDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findByEntitat(Long entitatId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			List<OrganGestorEntity> organs = organGestorRepository.findByEntitat(entitat);
			return conversioTipusHelper.convertirList(
					organs,
					OrganGestorDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CodiValorEstatDto> findOrgansGestorsCodiByEntitat(Long entitatId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			List<CodiValorEstatDto> organsGestors = new ArrayList<CodiValorEstatDto>();
			List<OrganGestorEntity> organs = organGestorRepository.findByEntitat(entitat);
			for (OrganGestorEntity organ: organs) {
				organsGestors.add(CodiValorEstatDto.builder().codi(organ.getCodi()).valor(organ.getCodi() + " - " + organ.getNom()).estat(organ.getEstat()).build());
			}
			return organsGestors;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findByProcedimentIds(List<Long> procedimentIds) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			return conversioTipusHelper.convertirList(
					organGestorRepository.findByProcedimentIds(procedimentIds),
					OrganGestorDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findByCodisAndEstat(List<String> codisOrgans, OrganGestorEstatEnum estat) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<OrganGestorEntity> organs = new ArrayList<>();
			int chunkSize = 100;
			for (int foo = 0; foo < codisOrgans.size(); foo=foo+chunkSize) {
				int indexFinal = foo + chunkSize;
				indexFinal = indexFinal <= codisOrgans.size() ? indexFinal : codisOrgans.size();
				List<OrganGestorEntity> organsChunk = organGestorRepository.findByEstatAndCodiIn(codisOrgans.subList(foo, indexFinal), estat);
				organs.addAll(organsChunk);
			}
			return conversioTipusHelper.convertirList(organs, OrganGestorDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findDescencentsByCodi(
			Long entitatId,
			String organCodi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			List<String> organs = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitat.getDir3Codi(), organCodi);
			return conversioTipusHelper.convertirList(
					organGestorRepository.findByCodiIn(organs),
					OrganGestorDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<OrganGestorDto> findAmbFiltrePaginat(Long entitatId, String organActualCodiDir3, OrganGestorFiltreDto filtre, PaginacioParamsDto paginacioParams) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {

			Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();
			mapeigPropietatsOrdenacio.put("llibreCodiNom", new String[] {"llibre"});
//			mapeigPropietatsOrdenacio.put("entregaCieActiva", new String[] {"entregaCie"}); // causa problemes al paginar
			mapeigPropietatsOrdenacio.put("oficinaNom", new String[] {"entitat.oficina"});
			mapeigPropietatsOrdenacio.put("oficinaCodiNom", new String[] {"oficina"});
			Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio);

			Page<OrganGestorEntity> organs = null;

			//Cas d'Administrador d'Entitat
			//	Tots els organs fills de l'Entitat
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			if (organActualCodiDir3 == null) {
				organs = findAmbFiltrePaginatByAdminEntitat(entitat, filtre, pageable);

			//Cas d'Administrador d'Organ
			//	Només el l'Organ de l'administrador, i els seus fills (tant de primer nivell com següents)
			}else{
				organs = findAmbFiltrePaginatByAdminOrgan(entitat, organActualCodiDir3, filtre, pageable);
			}
			PaginaDto<OrganGestorDto> paginaOrgans = paginacioHelper.toPaginaDto(organs, OrganGestorDto.class);
			Map<String, OrganismeDto> organigrama = cacheHelper.findOrganigramaNodeByEntitat(entitat.getDir3Codi());
			for (OrganGestorDto organ: paginaOrgans.getContingut()) {
				List<PermisDto> permisos = permisosHelper.findPermisos(organ.getId(), OrganGestorEntity.class);
				organ.setPermisos(permisos);
				OrganismeDto node = organigrama.get(organ.getCodiPare());
				organ.setNomPare(node != null ? node.getNom() : "");
			}
			return paginaOrgans;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private Page<OrganGestorEntity> findAmbFiltrePaginatByAdminEntitat(EntitatEntity entitat, OrganGestorFiltreDto filtre, Pageable pageable) {

		log.debug("Consulta taula òrgans gestors per administrador d'entitat");
		if (filtre == null) {
			return organGestorRepository.findByEntitat(entitat, pageable);
		}
		OrganGestorEstatEnum estat = filtre.getEstat();
		boolean isEstatNull = estat == null;
		return organGestorRepository.findByEntitatAndFiltre(
				entitat,
				filtre.getCodi() == null || filtre.getCodi().isEmpty(),
				filtre.getCodi() == null ? "" : filtre.getCodi(),
				filtre.getNom() == null || filtre.getNom().isEmpty(),
				filtre.getNom() == null ? "" : filtre.getNom(),
				filtre.getOficina() == null || filtre.getOficina().isEmpty(),
				filtre.getOficina() == null ? "" : filtre.getOficina(),
				isEstatNull,
				estat,
				filtre.isEntregaCie(),
				filtre.getCodiPare() == null || filtre.getCodiPare().isEmpty(),
				filtre.getCodiPare() == null ? "" : filtre.getCodiPare(),
				pageable);
	}

	private Page<OrganGestorEntity> findAmbFiltrePaginatByAdminOrgan(EntitatEntity entitat, String organActualCodiDir3, OrganGestorFiltreDto filtre, Pageable pageable) {

		log.debug("Consulta taula òrgans gestors per administrador d'òrgan");
		//Comprovació permisos organ
		entityComprovarHelper.comprovarPermisosOrganGestor(organActualCodiDir3);
		//OrganGestorEntity organGestor = entityComprovarHelper.comprovarOrganGestor(entitat,organActualId);
		List<String> organGestorsListCodisDir3 = organigramaHelper.getCodisOrgansGestorsFillsByOrgan(entitat.getDir3Codi(), organActualCodiDir3);
		if (filtre == null) {
			return organGestorRepository.findByEntitatAndOrganGestor(entitat, organGestorsListCodisDir3, pageable);
		}
		OrganGestorEstatEnum estat = filtre.getEstat();
		boolean isEstatNull = estat == null;
		return organGestorRepository.findByEntitatAndOrganGestorAndFiltre(
				entitat,
				organGestorsListCodisDir3,
				filtre.getCodi() == null || filtre.getCodi().isEmpty(),
				filtre.getCodi() == null ? "" : filtre.getCodi(),
				filtre.getNom() == null || filtre.getNom().isEmpty(),
				filtre.getNom() == null ? "" : filtre.getNom(),
				filtre.getOficina() == null || filtre.getOficina().isEmpty(),
				filtre.getOficina() == null ? "" : filtre.getOficina(),
				isEstatNull,
				estat,
				filtre.getCodiPare() == null || filtre.getCodiPare().isEmpty(),
				filtre.getCodiPare() == null ? "" : filtre.getCodiPare(),
				pageable);
	}
	
//	@Transactional
//	@Override
//	public void updateOne(Long entitatId, String organGestorCodi) {
//
//		Timer.Context timer = metricsHelper.iniciMetrica();
//		try {
//			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
//			OrganGestorEntity organGestor = organGestorRepository.findByCodi(organGestorCodi);
//			if (!updateNom(entitat, organGestor)) {
//				throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, "No s'ha pogut obtenir la denominació de l'organ gestor");
//			}
//			if (!updateLlibre(entitat, organGestor)) {
//				log.debug(String.format(
//						"No s'ha pogut actualitzar el llibre de l'òrgan gestor %s, segurament l'òrgan no estigui donat d'alta al registre",
//						organGestorCodi));
//			}
//
//			Map<String, OrganismeDto> arbreUnitats = cacheHelper.findOrganigramaNodeByEntitat(entitat.getDir3Codi());
//			if (!updateOficina(entitat, organGestor, arbreUnitats)) {
//				throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, "No s'ha pogut obtenir l'oficina de l'organ gestor");
//			}
//			if (!updateEstat(organGestor, arbreUnitats)) {
//				throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, "No s'ha pogut obtenir l'estat de l'organ gestor");
//			}
//		} finally {
//			metricsHelper.fiMetrica(timer);
//		}
//	}

	public boolean isUpdatingOrgans(EntitatDto entitatDto) {
		ProgresActualitzacioDto progres = progresActualitzacio.get(entitatDto.getDir3Codi());
		return progres != null && (progres.getProgres() > 0 && progres.getProgres() < 100) && !progres.isError();
	}

	@Transactional
	public void deleteHistoricSincronitzacio() {
		organGestorRepository.deleteHistoricSincronitzacio();
	}

	@Override
	@Transactional(timeout = 3600)
	@SuppressWarnings("unchecked")
	public Object[] syncDir3OrgansGestors(EntitatDto entitatDto) throws Exception {

		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatDto.getId(), false, true, false);
		String prefix = "[SYNC-ORGANS] ";
		log.debug(prefix + "Inici sync organs gestors");
		String msg = "";
		if (entitat.getDir3Codi() == null || entitat.getDir3Codi().isEmpty()) {
			msg = "L'entitat actual no té cap codi DIR3 associat";
			log.debug(prefix + msg);
			throw new Exception(msg);
		}

		// Comprova si hi ha una altre instància del procés en execució
		ProgresActualitzacioDto progres = progresActualitzacio.get(entitat.getDir3Codi());
		if (progres != null && (progres.getProgres() > 0 && progres.getProgres() < 100) && !progres.isError()) {
			msg = "[ORGANS GESTORS] Ja existeix un altre procés que està executant l'actualització";
			log.debug(prefix + msg);
			return null;	// Ja existeix un altre procés que està executant l'actualització.
		}

		// inicialitza el seguiment del progrés d'actualització
		progres = new ProgresActualitzacioDto();
		progresActualitzacio.put(entitat.getDir3Codi(), progres);

		progres.setNumOperacions(100);
		progres.addInfo(TipusInfo.TITOL, messageHelper.getMessage("organgestor.actualitzar.titol" ));
//		progres.incrementOperacionsRealitzades();	// 1%
		progres.setProgres(1);

		Long ti = System.currentTimeMillis();

		List<OrganGestorEntity> obsoleteUnitats = new ArrayList<>();
		List<OrganGestorEntity> organsDividits = new ArrayList<>();
		List<OrganGestorEntity> organsFusionats = new ArrayList<>();
		List<OrganGestorEntity> organsSubstituits = new ArrayList<>();

		Exception e = null;
		try {
			// 0. Buidar cache de l'organigrama
			log.debug(prefix + "Buidant caches");

			// 1. Obtenir canvis a l'organigrama
			progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.obtenir.canvis"));
			log.debug(prefix + "Obtenint unitats organitzatives");
			List<NodeDir3> unitatsWs = pluginHelper.unitatsOrganitzativesFindByPare(
					entitat.getCodi(),
					entitat.getDir3Codi(),
					entitat.getDataActualitzacio(),
					entitat.getDataSincronitzacio());

			Map<String, List<NodeDir3>> mapVersionsUnitats = getMapVersionsUnitats(unitatsWs);

			log.debug(prefix + "nombre d'unitats obtingutdes: " + unitatsWs.size());
			progres.setProgres(2);
			Long tf = System.currentTimeMillis();
			List<String> codis = new ArrayList<>();
			for (NodeDir3 u : unitatsWs) {
				codis.add(u.getCodi());
			}
			log.debug(prefix + "calculant unitats extingides");
			obsoleteUnitats = calcularExtingides(entitat.getCodi(), codis);
			log.debug(prefix + "nombre d'unitats extingides: " + obsoleteUnitats.size());

			progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[]{(tf - ti)}));
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("organgestor.actualitzacio.obtenir.canis.fi.resultat", new Object[]{unitatsWs.size()}));
			progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.obtenir.canis.fi"));

			// 2. Sincronitzar òrgans
			ti = tf;
			progres.setFase(1);
			progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.sincronitzar"));
			organGestorHelper.sincronitzarOrgans(entitat.getId(), unitatsWs, obsoleteUnitats, organsDividits, organsFusionats, organsSubstituits, progres);
			progres.setProgres(27);
			tf = System.currentTimeMillis();

			progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[]{(tf - ti)}));
			progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.sincronitzar.fi"));

			// 3. Actualitzar permisos
			log.debug(prefix + "Actualitzant permisos");
			progres.setFase(2);
			ti = tf;
			progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.permisos"));
			permisosHelper.actualitzarPermisosOrgansObsolets(unitatsWs, organsDividits, organsFusionats, organsSubstituits, progres);
			progres.setProgres(45);
			tf = System.currentTimeMillis();

			progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[]{(tf - ti)}));
			progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.permisos.fi"));


			// 4. Actualitzar procediments
			log.debug(prefix + "Actualitzant procediments");
			ti = tf;
			progres.setFase(3);
			progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.procediments"));
			try {
				procSerSyncHelper.actualitzaProcediments(entitatDto);
			} catch (Exception ex) {
				progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediments.actualitzacio.error.rolsac"));
			}
			ProgresActualitzacioDto progresProc = ProcedimentServiceImpl.progresActualitzacio.get(entitat.getDir3Codi());
			if (progresProc != null && progresProc.getInfo() != null && !progresProc.getInfo().isEmpty()) {
				progres.getInfo().addAll(ProcedimentServiceImpl.progresActualitzacio.get(entitat.getDir3Codi()).getInfo());
			}
			progres.setProgres(63);
			tf = System.currentTimeMillis();

			progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[]{(tf - ti)}));
			progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.procediments.fi"));

			// 5. Actualitzar serveis
			log.debug(prefix + "Actualitzant serveis");
			ti = tf;
			progres.setFase(4);
			progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.serveis"));
			try {
				procSerSyncHelper.actualitzaServeis(entitatDto);
			} catch (Exception ex) {
				progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("serveis.actualitzacio.error.rolsac"));
			}
			ProgresActualitzacioDto progresSer = ServeiServiceImpl.progresActualitzacioServeis.get(entitat.getDir3Codi());
			if (progresSer != null && progresSer.getInfo() != null && !progresSer.getInfo().isEmpty()) {
				progres.getInfo().addAll(ServeiServiceImpl.progresActualitzacioServeis.get(entitat.getDir3Codi()).getInfo());
			}
			progres.setFase(4);
			progres.setProgres(81);
			tf = System.currentTimeMillis();

			progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[]{(tf - ti)}));
			progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.serveis.fi"));


			// 6. Eliminar òrgans no utilitzats
			log.debug(prefix + "Eliminant òrgans no utilitzats");
			ti = tf;
			progres.setFase(5);
			progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.eliminar"));
//			organGestorHelper.deleteExtingitsNoUtilitzats(obsoleteUnitats, progres);
			progres.setProgres(90);
			tf = System.currentTimeMillis();

			progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[]{(tf - ti)}));
			progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.eliminar.fi"));

			// 7. Actualitzar oficines
			log.debug(prefix + "Actualitzant oficines");
			progres.setFase(6);
			ti = tf;
			progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.oficines"));
			syncOficines(entitat.getDir3Codi(), progres);
			progres.setProgres(45);
			tf = System.currentTimeMillis();

			progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[]{(tf - ti)}));
			progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.oficines.fi"));

			cacheHelper.clearAllCaches();
			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("organgestor.actualitzacio.sincronitzar.fi"));
		} catch (Exception ex) {
			e = ex;
			progres.addInfo(TipusInfo.ERROR, messageHelper.getMessage("organgestor.actualitzacio.error") + ex.getMessage());
			throw ex;
		} finally {
			progres.setProgres(100);
			progres.setFinished(true);
			IntegracioInfo info = new IntegracioInfo(IntegracioHelper.INTCODI_UNITATS, "Actualització d'òrgans gestors",
					IntegracioAccioTipusEnumDto.PROCESSAR, new AccioParam("Codi Dir3 de l'entitat", entitatDto.getDir3Codi()));
			info.setCodiEntitat(entitatDto.getCodi());
			for (ProgresActualitzacioDto.ActualitzacioInfo inf: progres.getInfo()) {
				if (inf.getText() != null)
					info.getParams().add(new AccioParam("Msg. procés:", inf.getText()));

			}
			if (e != null) {
				integracioHelper.addAccioError(info, "Error actualitzant procediments: ", e);
			} else {
				integracioHelper.addAccioOk(info);
			}
		}

		return new ArrayList[]{(ArrayList) obsoleteUnitats, (ArrayList) organsDividits, (ArrayList) organsFusionats, (ArrayList) organsSubstituits};
	}

	private Map<String, List<NodeDir3>> getMapVersionsUnitats(List<NodeDir3> unitatsWs) {
		Map<String, List<NodeDir3>> unitats = new HashMap<>();
		for (NodeDir3 unitat: unitatsWs) {
			if (!unitats.containsKey(unitat.getCodi())) {
				unitats.put(unitat.getCodi(), new ArrayList<NodeDir3>());
			}
			unitats.get(unitat.getCodi()).add(unitat);
		}
		for (Map.Entry<String, List<NodeDir3>> entry: unitats.entrySet()) {
			Collections.sort(entry.getValue(), new Comparator<NodeDir3>() {
				@Override
				public int compare(NodeDir3 o1, NodeDir3 o2) {
					if (o1.getVersio() == null) {
						return -1;
					}
					return o1.getVersio().compareTo(o2.getVersio());
				}
			});
		}
		return unitats;
	}

	@SuppressWarnings({"deprecation", "unchecked"})
	@Override
	@Transactional(readOnly = true)
	public PrediccioSincronitzacio predictSyncDir3OrgansGestors(Long entitatId) {

 		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false);
		boolean isFirstSincronization = entitat.getDataSincronitzacio() == null;
		List<UnitatOrganitzativaDto> unitatsVigents = new ArrayList<>();

		if (isFirstSincronization) {
			return predictFirstSynchronization(entitat);
		}
		try {
			// Obtenir lista de canvis del servei web
			List<NodeDir3> unitatsWS = pluginHelper.unitatsOrganitzativesFindByPare(
					entitat.getCodi(),
					entitat.getDir3Codi(),
					entitat.getDataActualitzacio(),
					entitat.getDataSincronitzacio());

			Map<String, List<NodeDir3>> mapVersionsUnitats = getMapVersionsUnitats(unitatsWS);

			// Obtenir els òrgans vigents a la BBDD
			List<OrganGestorEntity> organsVigents = organGestorRepository.findByEntitatIdAndEstat(entitat.getId(), OrganGestorEstatEnum.V);
//			log.debug("Consulta d'unitats vigents a DB");
//			for(OrganGestorEntity organVigent: organsVigents){
//				log.debug(organVigent.toString());
//			}

			// Obtenir unitats actualment vigents en BBDD, però marcades com a obsoletes en la sincronització
			List<UnitatOrganitzativaDto> unitatsVigentObsoleteDto = getObsoletesFromWS(unitatsWS, mapVersionsUnitats);
			List<UnitatOrganitzativaDto> unitatsExtingides = new ArrayList<>();

			// Distinció entre divisió i (substitució o fusió)
			MultiMap splitMap = new MultiHashMap();
			MultiMap mergeOrSubstMap = new MultiHashMap();

			for (UnitatOrganitzativaDto vigentObsolete : unitatsVigentObsoleteDto) {
				// Comprovam que no estigui extingida
				int transicionsVigents = 0;
				if (!vigentObsolete.getLastHistoricosUnitats().isEmpty()) {
					boolean extingit = true;
					for (UnitatOrganitzativaDto hist: vigentObsolete.getLastHistoricosUnitats()) {
						if (OrganGestorEstatEnum.V.name().equals(hist.getEstat())) {
							transicionsVigents++;
						}
					}
				}

				// En cas de no estar extingida comprovam el tipus de operació
//				if (vigentObsolete.getLastHistoricosUnitats().size() > 1) {
				if (transicionsVigents > 1) {
					for (UnitatOrganitzativaDto hist : vigentObsolete.getLastHistoricosUnitats()) {
						splitMap.put(vigentObsolete, hist);
					}
//				} else if (vigentObsolete.getLastHistoricosUnitats().size() == 1) {
				} else if (transicionsVigents == 1) {
					// check if the map already contains key with this codi
					UnitatOrganitzativaDto mergeOrSubstKeyWS = vigentObsolete.getLastHistoricosUnitats().get(0);
					UnitatOrganitzativaDto keyWithTheSameCodi = null;
					Set<UnitatOrganitzativaDto> keysMergeOrSubst = mergeOrSubstMap.keySet();
					for (UnitatOrganitzativaDto mergeOrSubstKeyMap : keysMergeOrSubst) {
						if (mergeOrSubstKeyMap.getCodi().equals(mergeOrSubstKeyWS.getCodi())) {
							keyWithTheSameCodi = mergeOrSubstKeyMap;
						}
					}
					// if it contains already key with the same codi, assign found key
					if (keyWithTheSameCodi != null) {
						mergeOrSubstMap.put(keyWithTheSameCodi, vigentObsolete);
					} else {
						mergeOrSubstMap.put(mergeOrSubstKeyWS, vigentObsolete);
					}
				} else if (transicionsVigents == 0) {
					unitatsExtingides.add(vigentObsolete);
				}
			}

			// Distinció entre substitució i fusió
			Set<UnitatOrganitzativaDto> keysMergeOrSubst = mergeOrSubstMap.keySet();
			MultiMap mergeMap = new MultiHashMap();
			MultiMap substMap = new MultiHashMap();
			List<UnitatOrganitzativaDto> values;
			for (UnitatOrganitzativaDto mergeOrSubstKey : keysMergeOrSubst) {
				values = (List<UnitatOrganitzativaDto>) mergeOrSubstMap.get(mergeOrSubstKey);
				if (values.size() <= 1) {
				// ==================== SUBSTITUCIONS ===================
					substMap.put(mergeOrSubstKey, values.get(0));
					continue;
				}
				for (UnitatOrganitzativaDto value : values) {
					if (isAlreadyAddedToMap(mergeMap, mergeOrSubstKey, value)) {
						//normally this shoudn't duplicate, it is added to deal with the result of call to WS DIR3 PRE in day 2023-06-21 with fechaActualizacion=[2023-06-15] which was probably incorrect
						log.info("Detected duplication of organs in prediction of fusion. Unitat" + value.getCodi() + "already added to fusion into " + mergeOrSubstKey.getCodi() + ". Probably caused by error in DIR3");
						continue;
					}
					mergeMap.put(mergeOrSubstKey, value);
				}
			}

			// Obtenir llistat d'unitats que ara estan vigents en BBDD, i després de la sincronització continuen vigents, però amb les propietats canviades
			// ====================  CANVIS EN ATRIBUTS ===================
			unitatsVigents = getVigentsFromWebService(entitat, unitatsWS, organsVigents);

			// Obtenir el llistat d'unitats que son totalment noves (no existeixen en BBDD): Creació
			// ====================  NOUS ===================
			List<UnitatOrganitzativaDto> unitatsNew = getNewFromWS(mapVersionsUnitats, splitMap, substMap, mergeMap);

			return PrediccioSincronitzacio.builder()
					.unitatsVigents(unitatsVigents)
					.unitatsNew(unitatsNew)
					.unitatsExtingides(unitatsExtingides)
					.splitMap(splitMap)
					.substMap(substMap)
					.mergeMap(mergeMap)
					.build();

		} catch (SistemaExternException sex) {
			throw sex;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, "No ha estat possible obtenir la predicció de canvis de unitats organitzatives", ex);
		}
	}

	@SuppressWarnings("unchecked")
	private boolean isAlreadyAddedToMap(MultiMap mergeMap, UnitatOrganitzativaDto key, UnitatOrganitzativaDto value) {

		List<UnitatOrganitzativaDto> values = (List<UnitatOrganitzativaDto>) mergeMap.get(key);
		if (values == null) {
			return false;
		}
		boolean contains = false;
		for (UnitatOrganitzativaDto unitat : values) {
			if (unitat.getCodi().equals(value.getCodi())) {
				contains = true;
			}
		}
		return contains;
	}

	private PrediccioSincronitzacio predictFirstSynchronization(EntitatEntity entitat) throws SistemaExternException {

		List<NodeDir3> unitatsVigentsWS = pluginHelper.unitatsOrganitzativesFindByPare(entitat.getCodi(), entitat.getDir3Codi(), entitat.getDataActualitzacio(), entitat.getDataSincronitzacio());
		List<UnitatOrganitzativaDto> vigents = conversioTipusHelper.convertirList(unitatsVigentsWS, UnitatOrganitzativaDto.class);
		List<String> codis = new ArrayList<>();
		List<UnitatOrganitzativaDto> noves = new ArrayList<>();
		OrganGestorEntity o;
		for (UnitatOrganitzativaDto u : vigents) {
			o = organGestorRepository.findByCodi(u.getCodi());
			if (o == null) {
				noves.add(u);
				continue;
			}
			codis.add(u.getCodi());
		}
		List<OrganGestorEntity> extingides = calcularExtingides(entitat.getCodi(), codis);
		List<UnitatOrganitzativaDto> ex = conversioTipusHelper.convertirList(extingides, UnitatOrganitzativaDto.class);
		List<UnitatOrganitzativaDto> n = conversioTipusHelper.convertirList(noves, UnitatOrganitzativaDto.class);
		return PrediccioSincronitzacio.builder().isFirstSincronization(true).unitatsVigents(vigents).unitatsNew(n).unitatsExtingides(ex).build();
	}

	private List<OrganGestorEntity> calcularExtingides(String entitatCodi, List<String> codis) {
		List<OrganGestorEntity> extingides = new ArrayList<>();
		List<String> organsAExtingir = organGestorRepository.findCodiActiusByEntitat(entitatCodi);
		if (organsAExtingir.isEmpty())
			return extingides;

		for (String codi: codis) {
			organsAExtingir.remove(codi);
		}
		if (organsAExtingir.isEmpty())
			return extingides;

		int maxInSize = 1000;
		int nParts = (organsAExtingir.size() / maxInSize) + 1;
		int inici = 0;
		int fi = organsAExtingir.size() - maxInSize > 0 ? maxInSize : organsAExtingir.size();
		List<String>  subList;
		for (int foo= 0; foo < nParts; foo++) {
			subList = organsAExtingir.subList(inici, fi);
			if (!subList.isEmpty()) {
				extingides.addAll(organGestorRepository.findByEntitatCodiAndCodiIn(entitatCodi, subList));
			}
			inici = fi + 1 ;
			fi = organsAExtingir.size() - inici > maxInSize ? maxInSize : organsAExtingir.size();
		}
		return extingides;
	}

	@Override
	public ProgresActualitzacioDto getProgresActualitzacio(String dir3Codi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			ProgresActualitzacioDto progres = progresActualitzacio.get(dir3Codi);
			if (progres != null && progres.isFinished()) {
				progresActualitzacio.remove(dir3Codi);
			}
			return progres;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private List<UnitatOrganitzativaDto> getObsoletesFromWS(List<NodeDir3> unitatsWS, Map<String,List<NodeDir3>> unitats) {

		List<UnitatOrganitzativaDto> extingides = new ArrayList<>();
		List<NodeDir3> nodes;
		NodeDir3 node;
		for (Map.Entry<String, List<NodeDir3>> entry : unitats.entrySet()){
			nodes = entry.getValue();
			node = nodes.get(nodes.size()-1);
			if (organGestorRepository.findByCodi(entry.getKey()) != null && "E".equals(node.getEstat())) {
				node.setLastHistoricosUnitats(getLastHistoricos(node, unitatsWS));
				extingides.add(conversioTipusHelper.convertir(node, UnitatOrganitzativaDto.class));
			}
		}
		return extingides;
	}

	// Obtenir unitats que no fan cap transició a cap altre unitat, però a la que se'ls canvia alguna propietat
	private List<UnitatOrganitzativaDto> getVigentsFromWebService(
			EntitatEntity entitat,
			List<NodeDir3> unitatsWS,
			List<OrganGestorEntity> organsVigents){
		// list of vigent unitats from webservice
		List<NodeDir3> unitatsVigentsWithChangedAttributes = new ArrayList<>();
//		for (OrganGestorEntity unitatV : organsVigents) {
			for (NodeDir3 unitatWS : unitatsWS) {
//				if (unitatV.getCodi().equals(unitatWS.getCodi()) && unitatWS.getEstat().equals("V")
//						&& (unitatWS.getHistoricosUO() == null || unitatWS.getHistoricosUO().isEmpty())
//						&& !unitatV.getCodi().equals(entitat.getDir3Codi())) {
				if (unitatWS.getEstat().equals("V") && (unitatWS.getHistoricosUO() == null || unitatWS.getHistoricosUO().isEmpty())
						&& !unitatWS.getCodi().equals(entitat.getDir3Codi())) {
					unitatsVigentsWithChangedAttributes.add(unitatWS);
				}
			}
//		}
		// converting from UnitatOrganitzativa to UnitatOrganitzativaDto
		List<UnitatOrganitzativaDto> unitatsVigentsWithChangedAttributesDto = new ArrayList<>();
		for(NodeDir3 vigent : unitatsVigentsWithChangedAttributes){

			UnitatOrganitzativaDto unitatOrganitzativaDto = conversioTipusHelper.convertir(vigent, UnitatOrganitzativaDto.class);
			OrganGestorEntity org = organGestorRepository.findByCodi(unitatOrganitzativaDto.getCodi());
			if (org == null) {
				continue;
			}
			unitatOrganitzativaDto.setOldDenominacio(org.getNom());
			unitatsVigentsWithChangedAttributesDto.add(unitatOrganitzativaDto);
		}
		return unitatsVigentsWithChangedAttributesDto;
	}

	// Obtenir unitats organitzatives noves (No provenen de cap transició d'una altre unitat)
	private List<UnitatOrganitzativaDto> getNewFromWS(Map<String, List<NodeDir3>> unitats, MultiMap splitMap,  MultiMap substMap, MultiMap mergeMap){

//		// converting from UnitatOrganitzativa to UnitatOrganitzativaDto
		List<UnitatOrganitzativaDto> newUnitatsDto = new ArrayList<>();
		List<NodeDir3> nodes;
		NodeDir3 node;
		boolean inMaps;
		UnitatOrganitzativaDto unitat;
		for (Map.Entry<String, List<NodeDir3>> entry : unitats.entrySet()){
			nodes = entry.getValue();
			node = nodes.get(nodes.size()-1);
			if (organGestorRepository.findByCodi(entry.getKey()) != null || "E".equals(node.getEstat())) {
				continue;
			}
			unitat = conversioTipusHelper.convertir(node, UnitatOrganitzativaDto.class);
			if(contains(entry.getKey(), splitMap) || contains(entry.getKey(), substMap) || contains(entry.getKey(), mergeMap)) {
				continue;
			}
			newUnitatsDto.add(unitat);
		}
		return newUnitatsDto;
	}

	private boolean contains(String key, MultiMap map) {

		Set<UnitatOrganitzativaDto> keys = map.keySet();
		for (UnitatOrganitzativaDto u : keys) {
			if (key.equals(u.getCodi())) {
				return true;
			}
		}
		return false;
	}

	// Retorna la/les unitat/s a la que un organ obsolet ha fet la transició
	// Inici de mètode recursiu
	private List<NodeDir3> getLastHistoricos(
			NodeDir3 unitat,
			List<NodeDir3> unitatsFromWebService){

		List<NodeDir3> lastHistorcos = new ArrayList<>();
		getLastHistoricosRecursive(
				unitat,
				unitatsFromWebService,
				lastHistorcos);
		return lastHistorcos;
	}

	private void getLastHistoricosRecursive(
			NodeDir3 unitat,
			List<NodeDir3> unitatsFromWebService,
			List<NodeDir3> lastHistorics) {

		log.info("Coloca historics recursiu(" + "unitatCodi=" + unitat.getCodi() + ")");

		if (unitat.getHistoricosUO() == null || unitat.getHistoricosUO().isEmpty()) {
			lastHistorics.add(unitat);
		} else {
			for (String historicCodi : unitat.getHistoricosUO()) {
				NodeDir3 unitatFromCodi = getUnitatFromCodi(historicCodi, unitatsFromWebService);
				if (unitatFromCodi == null) {
					// Looks for historico in database
					OrganGestorEntity entity = organGestorRepository.findByCodi(historicCodi);
					if (entity != null) {
						NodeDir3 uo = conversioTipusHelper.convertir(entity, NodeDir3.class);
						lastHistorics.add(uo);
					} else {
						String errorMissatge = "Error en la sincronització amb DIR3. La unitat orgánica (" + unitat.getCodi()
								+ ") té l'estat (" + unitat.getEstat() + ") i l'històrica (" + historicCodi
								+ ") però no s'ha retornat la unitat orgánica (" + historicCodi
								+ ") en el resultat de la consulta del WS ni en la BBDD.";
						throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorMissatge);
					}
				} else if (historicCodi.equals(unitat.getCodi())) {
					// EXAMPLE:
					//A04032359
					//-A04032359
					//-A04068486
					// if it is transitioning to itself don't add it as last historic
					//this probably shoudn't happen, it is added to deal with the result of call to WS made in PRE in day 2023-06-21 with fechaActualizacion=[2023-06-15] which was probably incorrect
					log.info("Detected organ division with transitioning to itself : " + historicCodi + ". Probably caused by error in DIR3");
				} else {
					if (!unitatFromCodi.equals(unitat)) {
						getLastHistoricosRecursive(unitatFromCodi, unitatsFromWebService, lastHistorics);
					} else {
						lastHistorics.add(unitat);
					}
				}
			}
		}
	}

	private NodeDir3 getUnitatFromCodi(
			String codi,
			List<NodeDir3> allUnitats){

		for (NodeDir3 unitatWS : allUnitats) {
			if (unitatWS.getCodi().equals(codi)) {
				return unitatWS;
			}
		}
		return null;
	}

//	@Transactional
//	@Override
//	public void updateAll(Long entitatId, String organActualCodiDir3) {
//		Timer.Context timer = metricsHelper.iniciMetrica();
//		try {
//			log.info("Actualitzant noms dels òrgans gestors");
//
//			//TODO: verificació de permisos per administrador entitat i per administrador d'Organ
//			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
//
//			// Comprova si hi ha una altre instància del procés en execució
//			ProgresActualitzacioDto progres = progresActualitzacio.get(entitat.getDir3Codi());
//			if (progres != null && (progres.getProgres() > 0 && progres.getProgres() < 100) && !progres.isError()) {
//				log.debug("[PROCEDIMENTS] Ja existeix un altre procés que està executant l'actualització");
//				return;	// Ja existeix un altre procés que està executant l'actualització.
//			}
//
//			// inicialitza el seguiment del progrés d'actualització
//			progres = new ProgresActualitzacioDto();
//			progresActualitzacio.put(entitat.getDir3Codi(), progres);
//
//			List<OrganGestorEntity> organsGestors;
//			if (organActualCodiDir3 == null) {
//				organsGestors = organGestorRepository.findByEntitat(entitat);
//			} else {
//				List<String> organGestorsListCodisDir3 = organigramaHelper.getCodisOrgansGestorsFillsByOrgan(entitat.getDir3Codi(), organActualCodiDir3);
//				organsGestors = organGestorRepository.findByEntitatAndOrgansGestors(entitat, organGestorsListCodisDir3);
//			}
//
//			progres.setNumProcediments(organsGestors.size());
//			progres.addInfo(ProgresActualitzacioDto.TipusInfo.TITOL, messageHelper.getMessage("organgestor.actualitzar.titol" ));
//
////			organGestorRepository.updateAllStatus(OrganGestorEstatEnum.ALTRES);
//			Map<String, NodeDir3> arbreUnitats = cacheHelper.findOrganigramaNodeByEntitat(entitat.getDir3Codi());
//			for(OrganGestorEntity organGestor: organsGestors) {
//
//				progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBINFO,
//						messageHelper.getMessage("organgestor.actualitzacio.organ.actual") + " " + organGestor.getCodi());
//				boolean status = updateNom(entitat, organGestor);
//				log.info("Fi - updateNom del òrgan gestor: " + organGestor);
//				if (!status) {
//					log.error(String.format("Actualització òrgan (%s): Error actualitzant nom ", organGestor.getCodi()));
//					progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBINFO, messageHelper.getMessage("organgestor.actualitzacio.organ.error.actualtizar.nom"));
//				}
//				status = updateLlibre(entitat, organGestor);
//				log.info("Fi - updateLlibre del òrgan gestor: " + organGestor);
//				if (!status) {
//					log.error(String.format("Actualització òrgan (%s): Error actualitzant llibre ", organGestor.getCodi()));
//					progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBINFO, messageHelper.getMessage("organgestor.actualitzacio.organ.error.actualtizar.llibre"));
//				}
//
//				status = updateOficina(entitat, organGestor, arbreUnitats);
//				log.info("Fi - updateOficina del òrgan gestor: " + organGestor);
//				if (!status) {
//					log.error(String.format("Actualització òrgan (%s): Error actualitzant oficina ", organGestor.getCodi()));
//					progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBINFO, messageHelper.getMessage("organgestor.actualitzacio.organ.error.actualtizar.oficina"));
//				}
//
//				status = updateEstat(organGestor, arbreUnitats);
//				log.info("Fi - updateEstat del òrgan gestor: " + organGestor);
//				if (!status) {
//					log.error(String.format("Actualització òrgan (%s): Error actualitzant estat ", organGestor.getCodi()));
//					progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBINFO, messageHelper.getMessage("organgestor.actualitzacio.organ.error.actualtizar.estat"));
//				}
//				NodeDir3 node = arbreUnitats.get(organGestor.getCodi());
//				String codiPare = node != null ? node.getSuperior().split("-")[0].trim() : null;
//				organGestor.updateCodiPare(codiPare);
//				organGestorRepository.saveAndFlush(organGestor);
//				progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBINFO, messageHelper.getMessage("organgestor.actualitzacio.organs.ok"));
//				progres.incrementProcedimentsActualitzats();
//			}
//			progres.setProgres(100);
//			progres.setFinished(true);
//			log.info("Antes de Update de las tablas correspondientes a las datatables");
//			// Update de las tablas correspondientes a las datatables de notificaciones y envíos
//			progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBINFO, messageHelper.getMessage("organgestor.actualitzacio.actualitzar.notificacions.enviaments"));
//			notificacioTableViewRepository.updateOrganGestorEstat();
//			enviamentTableRepository.updateOrganGestorEstat();
//			progres.addInfo(ProgresActualitzacioDto.TipusInfo.TITOL, messageHelper.getMessage("organgestor.actualitzacio.organ.proces.complet"));
//
//		} finally {
//			metricsHelper.fiMetrica(timer);
//		}
//	}

//	private boolean updateNom(EntitatEntity entitat, OrganGestorEntity organGestor)
//	{
//		try {
//			String denominacio = cacheHelper.findDenominacioOrganisme(organGestor.getCodi());
//			if (denominacio != null && !denominacio.isEmpty())
//				organGestor.update(denominacio);
//			else
//				return false;
//		} catch (Exception e) {
//			log.error(String.format("La denominacio de l'òrgan gestor %s de l'entitat %s no s'ha pogut actualitzar",
//					organGestor.getCodi(),
//					entitat.getDir3Codi()));
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//	}

	private boolean updateLlibre(EntitatEntity entitat, OrganGestorEntity organGestor)
	{
		try {
			LlibreDto llibreOrgan = cacheHelper.getLlibreOrganGestor(
					entitat.getDir3Codi(),
					organGestor.getCodi());
			if (llibreOrgan != null)
				organGestor.updateLlibre(llibreOrgan.getCodi(), llibreOrgan.getNomLlarg());
			else
				return false;
		} catch (Exception e) {
			log.error(String.format("El llibre de l'òrgan gestor %s de l'entitat %s no s'ha pogut actualitzar",
					organGestor.getCodi(),
					entitat.getDir3Codi()));
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean updateOficina(EntitatEntity entitat,
							   OrganGestorEntity organGestor,
							   Map<String, OrganismeDto> arbreUnitats) {
		try {
			// Oficina SIR òrgan gestor
			List<OficinaDto> oficinesSIR = cacheHelper.getOficinesSIRUnitat(
					arbreUnitats,
					organGestor.getCodi());
			if (oficinesSIR != null && !oficinesSIR.isEmpty())
				organGestor.updateOficina(oficinesSIR.get(0).getCodi(), oficinesSIR.get(0).getNom());
			else
				log.debug(String.format("L'òrgan gestor %s no disposa de cap oficina", organGestor.getCodi()));
		} catch (Exception e) {
			log.error(String.format("L'oficina de l'òrgan gestor %s de l'entitat %s no s'ha pogut actualitzar",
					organGestor.getCodi(),
					entitat.getDir3Codi()));
			e.printStackTrace();
			return false;
		}
		return true;
	}

//	private boolean updateEstat(OrganGestorEntity organGestor, Map<String, OrganismeDto> arbreUnitats) {
//
//		log.info("Ini - updateEstat del òrgan gestor: " + organGestor.getCodi() + "-" + organGestor.getNom());
//		if (!arbreUnitats.containsKey(organGestor.getCodi())) {
//			log.trace(String.format("Organ Gestor (%s) no trobat a l'organigrama", organGestor.getCodi()));
//			organGestor.updateEstat(OrganGestorEstatEnum.E);
//			return true;
//		}
//		OrganismeDto nodeOrgan = arbreUnitats.get(organGestor.getCodi());
//		organGestor.updateEstat(organGestorHelper.getEstatOrgan(nodeOrgan));
//		return true;
//	}

	@Transactional(readOnly = true)
	@Override
	public OrganGestorDto findById(
			Long entitatId,
			Long id) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de l'organ gestor ("
					+ "entitatId=" + entitatId + ", "
					+ "id=" + id + ")");
			EntitatEntity entitat = null;

			//TODO: verificació de permisos per administrador entitat i per administrador d'Organ
			if (entitatId != null)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId);
//						true, 
//						false, 
//						false);

			OrganGestorEntity organGestor = entityComprovarHelper.comprovarOrganGestor(
					entitat,
					id);
			OrganGestorDto resposta = conversioTipusHelper.convertir(
					organGestor,
					OrganGestorDto.class);

			return resposta;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public OrganGestorDto findByCodi(Long entitatId, String codi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de l'organ gestor (entitatId=" + entitatId + ", codi=" + codi + ")");
//			EntitatEntity entitat = null;

			//TODO: verificació de permisos per administrador entitat i per administrador d'Organ
//			if (entitatId != null)
//				entitat = entityComprovarHelper.comprovarEntitat(
//						entitatId); 
//						true, 
//						false, 
//						false);

//			OrganGestorEntity organGestor = entityComprovarHelper.comprovarOrganGestor(
//					entitat, 
//					codi);

			OrganGestorEntity organGestor = organGestorRepository.findByCodi(codi);
			if (organGestor == null) {
				return null;
			}
			OrganGestorDto resposta = conversioTipusHelper.convertir(organGestor, OrganGestorDto.class);
			return resposta;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findAccessiblesByUsuariActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return permisosCacheable.findOrgansGestorsAccessiblesUsuari(auth);
	}

	@Transactional
	@Override
	public List<PermisDto> permisFind(Long entitatId, Long id) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			if (entitatId == null || id == null) {
				return new ArrayList<>();
			}
			log.debug("Consulta dels permisos de l'organ gestor (entitatId=" + entitatId +  ", id=" + id +  ")");
			//TODO: verificació de permisos per administrador entitat i per administrador d'Organ
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			entityComprovarHelper.comprovarOrganGestor(entitat, id);
			return  permisosHelper.findPermisos(id, OrganGestorEntity.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public List<PermisDto> permisFind(Long entitatId, Long id,  PaginacioParamsDto paginacioParams) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta dels permisos de l'organ gestor ("
					+ "entitatId=" + entitatId +  ", "
					+ "id=" + id +  ")");
			EntitatEntity entitat = null;
			//TODO: verificació de permisos per administrador entitat i per administrador d'Organ
			if (entitatId != null) {
				entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			}
			entityComprovarHelper.comprovarOrganGestor(entitat, id);
			List<PermisDto> permisos =  permisosHelper.findPermisos(id, OrganGestorEntity.class);
			permisosHelper.ordenarPermisos(paginacioParams, permisos);
			return permisos;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public void permisUpdate(
			Long entitatId,
			Long id,
			boolean isAdminOrgan,
			PermisDto permisDto) throws ValidationException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Modificació del permis de l'organ gestor ("
					+ "entitatId=" + entitatId +  ", "
					+ "id=" + id + ", "
					+ "permis=" + permisDto + ")");

			if (TipusEnumDto.ROL.equals(permisDto.getTipus())) {
				if (permisDto.getPrincipal().equalsIgnoreCase("tothom")) {
					permisDto.setPrincipal(permisDto.getPrincipal().toLowerCase());
				} else {
					permisDto.setPrincipal(permisDto.getPrincipal().toUpperCase());
				}
			} else {
				if (TipusEnumDto.USUARI.equals(permisDto.getTipus())) {
					permisDto.setPrincipal(permisDto.getPrincipal().toLowerCase());
				}
			}
			
			EntitatEntity entitat = null;
			//TODO: verificació de permisos per administrador entitat i per administrador d'Organ
			if (entitatId != null)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId);
//						true,
//						false,
//						false);
			entityComprovarHelper.comprovarOrganGestor(entitat, id);
			
			PermisDto permis = permisosHelper.findPermis(
					id,
					OrganGestorEntity.class,
					permisDto.getId());	
			if (permis != null && isAdminOrgan && ((permis.getId() == null && permis.isAdministrador()) ||
					(permis.getId() != null && (permis.isAdministrador() != permisDto.isAdministrador())))) {
				throw new ValidationException("Un administrador d'òrgan no pot gestionar el permís d'admministrador d'òrgans gestors");
			}
			
			permisosHelper.updatePermis(
					id,
					OrganGestorEntity.class,
					permisDto);
			permisosCacheable.evictAllFindOrgansGestorsAccessiblesUsuari();
			permisosCacheable.evictAllFindEntitatsAccessiblesUsuari();
			cacheHelper.evictFindProcedimentServeisWithPermis();
			cacheHelper.evictFindOrgansGestorWithPermis();
			permisosCacheable.evictAllPermisosEntitatsUsuariActual();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional
	public void permisDelete(
			Long entitatId,
			Long id,
			Long permisId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Eliminació del permis de l'organ gestor ("
					+ "entitatId=" + entitatId +  ", "
					+ "id=" + id + ", "
					+ "permisId=" + permisId + ")");
			EntitatEntity entitat = null;
			
			//TODO: verificació de permisos per administrador entitat i per administrador d'Organ
			if (entitatId != null)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId);
//						true,
//						false,
//						false);
			
			entityComprovarHelper.comprovarOrganGestor(entitat, id);
			permisosHelper.deletePermis(
					id,
					OrganGestorEntity.class,
					permisId);
			permisosCacheable.evictAllFindOrgansGestorsAccessiblesUsuari();
			permisosCacheable.evictAllFindEntitatsAccessiblesUsuari();
			cacheHelper.evictFindProcedimentServeisWithPermis();
			cacheHelper.evictFindOrgansGestorWithPermis();
			permisosCacheable.evictAllPermisosEntitatsUsuariActual();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<OrganismeDto> findOrganismes(EntitatDto entitat) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<OrganismeDto> organismes = new ArrayList<>();
			try {
				organismes = organGestorCachable.findOrganismesByEntitat(entitat.getDir3Codi());
			} catch (Exception e) {
				String errorMessage = "No s'han pogut recuperar els organismes de l'entitat: " + entitat.getDir3Codi();
				log.error(errorMessage, e.getMessage());
			}
			return organismes;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<OrganismeDto> findOrganismes(EntitatDto entitat, OrganGestorDto organGestor) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<OrganismeDto> organismes = new ArrayList<OrganismeDto>();
			try {
				organismes = organigramaHelper.getOrganismesFillsByOrgan(entitat.getDir3Codi(), organGestor.getCodi());
			} catch (Exception e) {
				String errorMessage = "No s'han pogut recuperar els organismes de l'entitat: " + entitat.getDir3Codi() + " i òrgan gestor: " + organGestor.getCodi();
				log.error(errorMessage, e.getMessage());
			}
			return organismes;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public LlibreDto getLlibreOrganisme(Long entitatId, String organGestorDir3Codi) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, true, false, false);
			LlibreDto llibre = new LlibreDto();
			try {
				//Recupera el llibre de l'òrgan gestor especificat (organisme)
				llibre = cacheHelper.getLlibreOrganGestor(entitat.getDir3Codi(), organGestorDir3Codi);
	 		} catch (Exception e) {
	 			String errorMessage = "No s'ha pogut recuperar el llibre de l'òrgan gestor: " + organGestorDir3Codi;
				log.error(errorMessage, e.getMessage());
			}
			return llibre;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
//	@Override
//	@Transactional(readOnly = true)
//	public List<OrganGestorDto> findOrgansGestorsWithPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
//		Timer.Context timer = metricsHelper.iniciMetrica();
//		try {
//			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
//					entitatId,
//					true,
//					false,
//					false);
//			Permission[] permisos = entityComprovarHelper.getPermissionsFromName(permis);
//
//			return conversioTipusHelper.convertirList(
//					permisosCacheable.findOrgansGestorsWithPermisDirecte(entitat, auth, permisos),
//					OrganGestorDto.class);
//
//		} finally {
//			metricsHelper.fiMetrica(timer);
//		}
//	}

    @Override
	@Transactional(readOnly = true)
    public List<CodiValorEstatDto> getOrgansGestorsDisponiblesConsulta(
    		Long entitatId,
			String usuari,
			RolEnumDto rol,
			String organ) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<CodiValorEstatDto> organsGestors = new ArrayList<>();
			List<OrganGestorEntity> organsGestorsDisponibles = new ArrayList<>();

			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);

			if (RolEnumDto.NOT_SUPER.equals(rol)) {
				organsGestorsDisponibles = organGestorRepository.findAll();
			} else if (RolEnumDto.NOT_ADMIN.equals(rol)) {
				organsGestorsDisponibles = organGestorRepository.findByEntitat(entitat);
			} else if (RolEnumDto.NOT_ADMIN_ORGAN.equals(rol)) {
				List<String> organs = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitat.getDir3Codi(), organ);
				organsGestorsDisponibles = organGestorRepository.findByCodiIn(organs);
			} else if (RolEnumDto.tothom.equals(rol)) {
				organsGestorsDisponibles = recuperarOrgansPerProcedimentAmbPermis(
						usuari,
						entitat,
						PermisEnum.CONSULTA);
			}
			Collections.sort(organsGestorsDisponibles, new Comparator<OrganGestorEntity>() {
				@Override
				public int compare(OrganGestorEntity p1, OrganGestorEntity p2) {
					return p1.getNom().compareTo(p2.getNom());
				}
			});

			for (OrganGestorEntity organGestor : organsGestorsDisponibles) {
				String nom = organGestor.getCodi();
				if (organGestor.getNom() != null && !organGestor.getNom().isEmpty()) {
					nom += " - " + organGestor.getNom();
				}
				organsGestors.add(CodiValorEstatDto.builder()
						.id(organGestor.getId())
						.codi(organGestor.getCodi())
						.valor(nom)
						.estat(organGestor.getEstat())
						.build());
			}
//		// Eliminam l'òrgan gestor entitat  --> Per ara el mantenim, ja que hi ha notificacions realitzades a l'entitat
//		OrganGestorDto organEntitat = organGestorService.findByCodi(entitatActual.getId(), entitatActual.getDir3Codi());
//		organsGestorsDisponibles.remove(organEntitat);
        	return organsGestors;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
    }

	@Override
	@Transactional
	public List<OrganGestorDto> getOrgansAsList(EntitatDto entitat) {

		if (organsList != null) {
			return organsList;
		}
		Map<String, OrganismeDto> organs = cacheHelper.findOrganigramaNodeByEntitat(entitat.getDir3Codi());
		organsList = new ArrayList<>();
		for (OrganismeDto node : organs.values()) {
			organsList.add(conversioTipusHelper.convertir(node, OrganGestorDto.class));
		}
		return organsList;
	}

	@Override
	public List<OrganGestorDto> getOrgansAsList() {
		return organsList;
	}

	@Override
	@Transactional(readOnly = true)
	public Arbre<OrganGestorDto> generarArbreOrgans(EntitatDto entitat, OrganGestorFiltreDto filtres, boolean isAdminOrgan, OrganGestorDto organActual) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			Map<String, OrganismeDto> organs = cacheHelper.findOrganigramaNodeByEntitat(entitat.getDir3Codi());
			Arbre<OrganGestorDto> arbre = new Arbre<>(true);
			if (organs.isEmpty()) {
				return arbre;
			}
			sotredOrgans = findByEntitat(entitat.getId());
			organsList = new ArrayList<>();
			this.isAdminOrgan = isAdminOrgan;
			this.organActual = organActual;
			String codiArrel = isAdminOrgan ? organActual.getCodi() : entitat.getDir3Codi();
			if (isAdminOrgan) {
				addCodisOrgansAmbPermis(entitat);
			}
			ArbreNode<OrganGestorDto> arrel = new ArbreNode<>(null, conversioTipusHelper.convertir(organs.get(codiArrel), OrganGestorDto.class));
			arbre.setArrel(arrel);
			arrel.setFills(generarFillsArbre(organs, arrel, codiArrel, filtres));
			if (!Strings.isNullOrEmpty(filtres.getCodiPare())) {
				filtres.filtrarOrganPare(arbre.getArrel());
			}
			if (!filtres.isEmpty() && !filtres.filtrar(arbre.getArrel())) {
				arrel.setFills(new ArrayList<ArbreNode<OrganGestorDto>>());
			}
			return arbre;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private List<ArbreNode<OrganGestorDto>> generarFillsArbre(Map<String, OrganismeDto> organs, ArbreNode<OrganGestorDto> pare,
															  String codiEntitat, OrganGestorFiltreDto filtres) {

		OrganismeDto organ = organs.get(codiEntitat);
		List<ArbreNode<OrganGestorDto>> nodes = new ArrayList<>();
		if (organ == null) {
			return nodes;
		}
		OrganGestorDto organExistent = buscarOrgan(organ.getCodi());
		OrganGestorDto o = organExistent != null ? organExistent : conversioTipusHelper.convertir(organ, OrganGestorDto.class);
		organsList.add(o);
		List<String> fills = organ.getFills();
		if (fills == null || fills.isEmpty()) {
			return nodes;
		}

		for (String fill: fills) {
			OrganismeDto node = organs.get(fill);
			organExistent = buscarOrgan(node.getCodi());
			o = organExistent != null ? organExistent : conversioTipusHelper.convertir(node, OrganGestorDto.class);
			ArbreNode<OrganGestorDto> actual = new ArbreNode<>(pare, o);
			boolean ok = filtres.filtresOk(o);
			if (!filtres.isEmpty() && ok) {
				actual.setRetornatFiltre(true);
			}
			List<ArbreNode<OrganGestorDto>> nets = generarFillsArbre(organs, actual, node.getCodi(), filtres);
			actual.setFills(nets);
			if (nets != null && nets.isEmpty() && !ok) {
				continue;
			}
			if (isAdminOrgan && !checkPermisOrgan(actual)) {
				continue;
			}
			nodes.add(actual);
		}

		return nodes;
	}

	private boolean checkPermisOrgan(ArbreNode<OrganGestorDto>  node) {

		if (node.getFills().isEmpty()) {
			return codisAmbPermis.contains(node.dades.getCodi());
		}

		boolean ok = false;
		List<ArbreNode<OrganGestorDto>> fills = node.getFills();
		for (ArbreNode<OrganGestorDto> fill : fills) {
			ok = codisAmbPermis.contains(fill.getDades().getCodi()) || checkPermisOrgan(fill);
		}
		return ok;
	}

	private void addCodisOrgansAmbPermis(EntitatDto entitat) {

		codisAmbPermis  = organigramaHelper.getCodisOrgansGestorsFillsByOrgan(entitat.getDir3Codi(), organActual.getCodi());
	}
	private OrganGestorDto buscarOrgan(String codi) {

		for (OrganGestorDto o : sotredOrgans) {
			if (codi.equals(o.getCodi())) {
				return o;
			}
		}
		return null;
	}

	@Override
	@Transactional
	public OrganGestorDto getOrganNou(String codiSia) {

		List<OrganGestorDto> organs = pluginHelper.unitatsPerCodi(codiSia);
		return organs != null && !organs.isEmpty() ? organs.get(0) : new OrganGestorDto();
	}

	@Override
	public boolean hasPermisOrgan(Long entitatId, String organCodi, PermisEnum permis) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			String user = SecurityContextHolder.getContext().getAuthentication().getName();
			List<CodiValorDto> codis = permisosService.getOrgansAmbPermis(entitatId, user, permis);
			for (CodiValorDto c : codis) {
				if (c.equals(organCodi)) {
					return true;
				}
			}
			return false;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private void syncOficines(String entitatDir3Codi, ProgresActualitzacioDto progres) {
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_UNITATS,
				"Actualització d'oficines SIR per l'entitat " + entitatDir3Codi,
				IntegracioAccioTipusEnumDto.PROCESSAR);
		try {
			// Obtenim l'entitat
			EntitatEntity entitat = entitatRepository.findByDir3Codi(entitatDir3Codi);
			if (entitat == null) {
				throw new NotFoundException(entitatDir3Codi, EntitatEntity.class);
			}
			info.setCodiEntitat(entitat.getCodi());
			addInfo(progres, info, messageHelper.getMessage("organgestor.actualitzacio.oficines"), TipusInfo.TITOL);
			// Obtenim totes les oficines vigents de l'entitat
			List<OficinaDto> oficines = pluginHelper.oficinesEntitat(entitatDir3Codi);

			addInfo(progres, info, messageHelper.getMessage("organgestor.actualitzacio.oficines.get"));
			progres.setProgres(93);

			// Eliminam totes les oficines desades a la BBDD
			oficinaRepository.deleteAll();

			// Cream les noves oficines
			for (OficinaDto ofi: oficines) {
				OrganGestorEntity organGestor = organGestorRepository.findByCodi(ofi.getOrganCodi());
				OficinaEntity oficina = OficinaEntity.builder()
						.codi(ofi.getCodi())
						.nom(ofi.getNom())
						.sir(ofi.isSir())
						.actiu(true)
						.organGestor(organGestor)
						.entitat(entitat)
						.build();
				oficinaRepository.save(oficina);
			}

			addInfo(progres, info, messageHelper.getMessage("organgestor.actualitzacio.oficines.saved"));
			progres.setProgres(95);

			// Consultam els òrgans amb oficines configurades no existents
			List<OrganGestorEntity> organsAmbOficinaInexistent = organGestorRepository.findByEntitatAndOficinaInexistent(entitat);
			if (organsAmbOficinaInexistent != null && !organsAmbOficinaInexistent.isEmpty()) {
				Map<String, OrganismeDto> arbreUnitats = cacheHelper.findOrganigramaNodeByEntitat(entitatDir3Codi);
				for (OrganGestorEntity organ : organsAmbOficinaInexistent) {
					String oficinaOriginal = organ.getOficina();
					List<OficinaDto> oficinesOrgan = cacheHelper.getOficinesSIRUnitat(arbreUnitats, organ.getCodi());
					if ((oficinesOrgan == null || oficinesOrgan.isEmpty()) && oficinaOriginal != null) {
						organ.setOficina(null);
						organ.setOficinaNom(null);
						addInfo(progres, info, messageHelper.getMessage("organgestor.actualitzacio.oficines.organ.buid", new Object[] {organ.getCodi(), oficinaOriginal}),"Organ " + organ.getCodi(), TipusInfo.INFO);
						return;
					} else if (oficinesOrgan != null && !oficinesOrgan.isEmpty()){
						organ.setOficina(oficinesOrgan.get(0).getCodi());
						organ.setOficinaNom(oficinesOrgan.get(0).getNom());
						addInfo(progres, info, messageHelper.getMessage("organgestor.actualitzacio.oficines.organ.nova", new Object[] {organ.getCodi(), oficinaOriginal, oficinesOrgan.get(0).getCodi()}), "Organ " + organ.getCodi(), TipusInfo.INFO);
					}
				}
			}

			progres.setProgres(99);
			log.info(">>> [ACT_OFI]: Fi de l'actualització de les oficines");
			integracioHelper.addAccioOk(info, false);
			cacheHelper.evictCercaOficines();
		} catch (Exception ex) {
			log.error(">>> [ACT_OFI]: Error actualitzant les oficines", ex);
			integracioHelper.addAccioError(info, "Error actualitzant les oficines", ex, false);
			throw ex;
		}
	}

	private void addInfo(ProgresActualitzacioDto progres, IntegracioInfo info, String msg) {
		addInfo(progres, info, msg, null, TipusInfo.INFO);
	}
	private void addInfo(ProgresActualitzacioDto progres, IntegracioInfo info, String msg, TipusInfo tipus) {
		addInfo(progres, info, msg, null, tipus);
	}
	private void addInfo(ProgresActualitzacioDto progres, IntegracioInfo info, String msg, String param, TipusInfo tipus) {
		progres.addInfo(tipus, msg);
		if (param != null)
			info.addParam(param, msg);
		log.info(">>> [ACT_OFI]: " + msg);
	}

	@Override
	@Transactional(timeout = 3600)
	public void syncOficinesSIR(Long entitatId) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		EntitatEntity entity = entityComprovarHelper.comprovarEntitat(entitatId);
		log.info("OFISYNC - Iniciant procés d'actualització d'oficines de l'entitat amb id {}", entitatId);
		try {
			syncOficines(entity.getDir3Codi(), new ProgresActualitzacioDto());
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<OficinaDto> getOficinesSIR(Long entitatId, String dir3codi, boolean isFiltre) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, true, false, false);
			List<OficinaDto> oficines = new ArrayList<OficinaDto>();
			try {
				if (!isFiltre) {
					Map<String, OrganismeDto> arbreUnitats = cacheHelper.findOrganigramaNodeByEntitat(entitat.getDir3Codi());
					oficines = cacheHelper.getOficinesSIRUnitat(arbreUnitats, dir3codi);
					return oficines;
				}
				oficines = cacheHelper.getOficinesSIREntitat(dir3codi);
			} catch (Exception e) {
	 			String errorMessage = "No s'han pogut recuperar les oficines SIR [dir3codi=" + dir3codi + "]";
				log.error(errorMessage, e.getMessage());
			}
			return oficines;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private List<OrganGestorEntity> recuperarOrgansPerProcedimentAmbPermis(
			String usuari,
			EntitatEntity entitat,
			PermisEnum permis) {

		// 1-recuperam els òrgans dels procediments disponibles (amb permís)
		List<OrganGestorEntity> organsGestorsProcediments = new ArrayList<>();
		List<Long> procedimentsDisponiblesIds = new ArrayList<>();
		List<CodiValorOrganGestorComuDto> procedimentsDisponibles = permisosService.getProcedimentsAmbPermis(entitat.getId(), usuari, permis);
		for (CodiValorOrganGestorComuDto pro : procedimentsDisponibles) {
			procedimentsDisponiblesIds.add(pro.getId());
		}
		if (!procedimentsDisponiblesIds.isEmpty())
			organsGestorsProcediments = organGestorRepository.findByProcedimentIds(procedimentsDisponiblesIds);

		List<OrganGestorEntity> organsGestorsAmbPermis = new ArrayList<>();
		List<String> organsCodis = new ArrayList<>();

		// 2-recuperam els òrgans de procediments comuns
		List<String> procsOrgans = permisosService.getProcedimentsOrgansAmbPermis(entitat.getId(), usuari, permis);
		for(String procOrgan: procsOrgans) {
			if (!Strings.isNullOrEmpty(procOrgan)) {
				String[] splProcOrgan = procOrgan.split("-");
				if (splProcOrgan.length > 1)
					organsCodis.add(procOrgan.split("-")[1]);
			}
		}

		// 3-recuperam els òrgans amb permís
		List<CodiValorDto> organsAmbPermisDirecte = permisosService.getOrgansAmbPermis(entitat.getId(), usuari, permis);
		for (CodiValorDto org : organsAmbPermisDirecte) {
			organsCodis.add(org.getCodi());
		}
		// 4-Els òrgans ammb permís comú també es poden consultar
		List<CodiValorDto> organsAmbPermisComuns = permisosService.getOrgansAmbPermis(entitat.getId(), usuari, PermisEnum.COMUNS);
		for (CodiValorDto org : organsAmbPermisComuns) {
			organsCodis.add(org.getCodi());
		}
		if (!organsCodis.isEmpty())
			organsGestorsAmbPermis = organGestorRepository.findByEntitatCodiAndCodiIn(entitat.getCodi(), organsCodis);

		// 3-juntam tots els òrgans i ordenam per nom
		List<OrganGestorEntity> organsGestors;
		Set<OrganGestorEntity> setOrgansGestors = new HashSet<>(organsGestorsProcediments);
		setOrgansGestors.addAll(organsGestorsAmbPermis);
		organsGestors = new ArrayList<>(setOrgansGestors);
		if (!configHelper.getAsBoolean("es.caib.notib.notifica.dir3.entitat.permes")) {
			organsGestors.remove(organGestorRepository.findByCodi(entitat.getDir3Codi()));
		}
		if (procedimentsDisponibles.isEmpty() && organsGestors.isEmpty()) {
			throw new NoPermisosException("Usuari sense permios assignats");
		}
		return organsGestors;
	}

	private List<ProcSerEntity> mergeProcedimentsWithProcedimentsOrgans(
			List<ProcSerEntity> procedimentsDisponibles,
			List<ProcSerOrganEntity> procedimentsOrgansDisponibles) {
		if (procedimentsOrgansDisponibles != null && !procedimentsOrgansDisponibles.isEmpty()) {
			// Empleam un set per no afegir duplicats
			Set<ProcSerEntity> setProcediments = new HashSet<>(procedimentsDisponibles);
			for (ProcSerOrganEntity procedimentOrgan : procedimentsOrgansDisponibles) {
				setProcediments.add(procedimentOrgan.getProcSer());
			}
			procedimentsDisponibles = new ArrayList<ProcSerEntity>(setProcediments);
		}
		return procedimentsDisponibles;
	}

	// Sync Testing:
	@Override
	public void setServicesForSynctest(Object procSerSyncHelper, Object pluginHelper, Object integracioHelper) {
		this.procSerSyncHelper = (ProcSerSyncHelper)procSerSyncHelper;
		this.pluginHelper = (PluginHelper)pluginHelper;
		this.integracioHelper = (IntegracioHelper) integracioHelper;
	}

	@Override
	@Transactional
	public void sincronitzarOrganNomMultidioma(List<Long> ids) {

		try {
			List<EntitatEntity> entitats = ids != null ? entitatRepository.findByIds(ids) : entitatRepository.findAll();
			List<NodeDir3> nodesDir3;
			OrganGestorEntity organ;
			for (EntitatEntity entitat : entitats) {
				try {
					nodesDir3 = pluginHelper.getOrganNomMultidioma(entitat);
					if (nodesDir3 == null || nodesDir3.isEmpty()) {
						continue;
					}
					for (NodeDir3 node : nodesDir3) {
						organ = organGestorRepository.findByCodi(node.getCodi());
						if (organ == null) {
							continue;
						}
						organ.setNomEs(node.getDenominacio());
						String nom = !Strings.isNullOrEmpty(node.getDenominacionCooficial()) ? node.getDenominacionCooficial() : node.getDenominacio();
						organ.setNom(nom);
					}
				} catch (Exception ex) {
					log.error("Error sincronitzant els nom de l'entiat " + entitat.getCodi(), ex);
				}
			}
		} catch (Exception ex) {
			log.error("Error sincronitzant els noms dels òrgans gestors", ex);
		}
 	}
}
