package es.caib.notib.logic.service;

import com.google.common.base.Strings;
import es.caib.notib.logic.cacheable.OrganGestorCachable;
import es.caib.notib.logic.cacheable.PermisosCacheable;
import es.caib.notib.logic.helper.CacheHelper;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.EntityComprovarHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.helper.OrganGestorHelper;
import es.caib.notib.logic.helper.OrganigramaHelper;
import es.caib.notib.logic.helper.PaginacioHelper;
import es.caib.notib.logic.helper.PermisosHelper;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.logic.helper.ProcSerSyncHelper;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.Arbre;
import es.caib.notib.logic.intf.dto.ArbreNode;
import es.caib.notib.logic.intf.dto.CodiValorEstatDto;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.LlibreDto;
import es.caib.notib.logic.intf.dto.OficinaDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.PermisDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioDto;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioDto.TipusInfo;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.TipusEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorFiltreDto;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.logic.intf.dto.organisme.PrediccioSincronitzacio;
import es.caib.notib.logic.intf.dto.organisme.UnitatOrganitzativaDto;
import es.caib.notib.logic.intf.exception.NoPermisosException;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.logic.intf.service.PermisosService;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.OficinaEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.cie.EntregaCieEntity;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.EntregaCieRepository;
import es.caib.notib.persist.repository.GrupRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.persist.repository.OficinaRepository;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.PagadorCieRepository;
import es.caib.notib.persist.repository.PagadorPostalRepository;
import es.caib.notib.persist.repository.ProcSerRepository;
import es.caib.notib.plugin.unitat.NodeDir3;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import java.util.stream.Collectors;

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

	private static Long permisosEntitatsModificatsInstant;
	protected static Map<String, ProgresActualitzacioDto> progresActualitzacio = new HashMap<>();
	private static final String AUTO_TEMPS_TEXT = "procediment.actualitzacio.auto.temps";

	private List<OrganGestorDto> sotredOrgans = new ArrayList<>();
	private List<String> codisAmbPermis = new ArrayList<>();
	private boolean isAdminOrgan;
	private OrganGestorDto organActual;

	@Getter
	private List<OrganGestorDto> organsList;

	private static void updateOrgansSessio() {
		permisosEntitatsModificatsInstant = System.currentTimeMillis();
	}

	@Override
	public Long getLastPermisosModificatsInstant() {
		return OrganGestorServiceImpl.permisosEntitatsModificatsInstant;
	}

	@Override
	@Transactional
	public OrganGestorDto update(OrganGestorDto dto) {

		var timer = metricsHelper.iniciMetrica();
		try {
			entityComprovarHelper.comprovarEntitat(dto.getEntitatId(), false, true, false);
			OrganGestorEntity organGestor = organGestorRepository.findById(dto.getId()).orElseThrow();
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

		var timer = metricsHelper.iniciMetrica();
		try {
			//Compravacions en ús
			var organGestor = organGestorRepository.findById(organId).orElse(null);
			if (organGestor == null) {
				return true; // No en ús pq no existeix!!
			}
			if (OrganGestorEstatEnum.V.equals(organGestor.getEstat())) {
				return true;
			}
			if (notificacioRepository.countByOrganGestor(organGestor) > 0) {
				return true;
			}
			var procedimentsOrganGestor = procSerRepository.findByOrganGestorId(organId);
			if (procedimentsOrganGestor != null && !procedimentsOrganGestor.isEmpty()) {
				return true;
			}
			var grupsByOrganGestor = grupReposity.findByOrganGestorId(organId);
			if (grupsByOrganGestor != null && !grupsByOrganGestor.isEmpty()) {
				return true;
			}
			var pagCiesByOrganGestor = pagadorCieReposity.findByOrganGestor(organGestor);
			if (pagCiesByOrganGestor != null && !pagCiesByOrganGestor.isEmpty()) {
				return true;
			}
			var pagPostalByOrganGestor = pagadorPostalReposity.findByOrganGestorId(organId);
			return pagPostalByOrganGestor != null && !pagPostalByOrganGestor.isEmpty();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findAll() {
		var timer = metricsHelper.iniciMetrica();
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
		var timer = metricsHelper.iniciMetrica();
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
		var timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			List<CodiValorEstatDto> organsGestors = new ArrayList<>();
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
		var timer = metricsHelper.iniciMetrica();
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

		var timer = metricsHelper.iniciMetrica();
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
		var timer = metricsHelper.iniciMetrica();
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

		var timer = metricsHelper.iniciMetrica();
		try {

			Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<>();
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
		var estat = filtre.getEstat();
		var isEstatNull = estat == null;
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
		var organGestorsListCodisDir3 = organigramaHelper.getCodisOrgansGestorsFillsByOrgan(entitat.getDir3Codi(), organActualCodiDir3);
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
	
	public boolean isUpdatingOrgans(EntitatDto entitatDto) {

		var progres = progresActualitzacio.get(entitatDto.getDir3Codi());
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

		var entitat = entityComprovarHelper.comprovarEntitat(entitatDto.getId(), false, true, false);
		var prefix = "[SYNC-ORGANS] ";
		log.debug(prefix + "Inici sync organs gestors");
		var msg = "";
		if (entitat.getDir3Codi() == null || entitat.getDir3Codi().isEmpty()) {
			msg = "L'entitat actual no té cap codi DIR3 associat";
			log.debug(prefix + msg);
			throw new Exception(msg);
		}
		// Comprova si hi ha una altre instància del procés en execució
		var progres = progresActualitzacio.get(entitat.getDir3Codi());
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
			var unitatsWs = pluginHelper.unitatsOrganitzativesFindByPare(entitat.getCodi(), entitat.getDir3Codi(), entitat.getDataActualitzacio(), entitat.getDataSincronitzacio());
			Map<String, List<NodeDir3>> mapVersionsUnitats = getMapVersionsUnitats(unitatsWs);
			log.debug(prefix + "nombre d'unitats obtingutdes: " + unitatsWs.size());
			progres.setProgres(2);
			var tf = System.currentTimeMillis();
			List<String> codis = new ArrayList<>();
			for (NodeDir3 u : unitatsWs) {
				codis.add(u.getCodi());
			}
			log.debug(prefix + "calculant unitats extingides");
			obsoleteUnitats = calcularExtingides(entitat.getCodi(), codis);
			log.debug(prefix + "nombre d'unitats extingides: " + obsoleteUnitats.size());
			progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage(AUTO_TEMPS_TEXT, new Object[]{(tf - ti)}));
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("organgestor.actualitzacio.obtenir.canis.fi.resultat", new Object[]{unitatsWs.size()}));
			progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.obtenir.canis.fi"));
			// 2. Sincronitzar òrgans
			ti = tf;
			progres.setFase(1);
			progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.sincronitzar"));
			organGestorHelper.sincronitzarOrgans(entitat.getId(), unitatsWs, obsoleteUnitats, organsDividits, organsFusionats, organsSubstituits, progres);
			progres.setProgres(27);
			tf = System.currentTimeMillis();
			progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage(AUTO_TEMPS_TEXT, new Object[]{(tf - ti)}));
			progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.sincronitzar.fi"));
			// 3. Actualitzar permisos
			log.debug(prefix + "Actualitzant permisos");
			progres.setFase(2);
			ti = tf;
			progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.permisos"));
			permisosHelper.actualitzarPermisosOrgansObsolets(unitatsWs, organsDividits, organsFusionats, organsSubstituits, progres);
			progres.setProgres(45);
			tf = System.currentTimeMillis();
			progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage(AUTO_TEMPS_TEXT, new Object[]{(tf - ti)}));
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
			var progresProc = ProcedimentServiceImpl.getProgresActualitzacio().get(entitat.getDir3Codi());
			if (progresProc != null && progresProc.getInfo() != null && !progresProc.getInfo().isEmpty()) {
				progres.getInfo().addAll(ProcedimentServiceImpl.getProgresActualitzacio().get(entitat.getDir3Codi()).getInfo());
			}
			progres.setProgres(63);
			tf = System.currentTimeMillis();
			progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage(AUTO_TEMPS_TEXT, new Object[]{(tf - ti)}));
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
			ProgresActualitzacioDto progresSer = ServeiServiceImpl.getProgresActualitzacioServeis().get(entitat.getDir3Codi());
			if (progresSer != null && progresSer.getInfo() != null && !progresSer.getInfo().isEmpty()) {
				progres.getInfo().addAll(ServeiServiceImpl.getProgresActualitzacioServeis().get(entitat.getDir3Codi()).getInfo());
			}
			progres.setFase(4);
			progres.setProgres(81);
			tf = System.currentTimeMillis();
			progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage(AUTO_TEMPS_TEXT, new Object[]{(tf - ti)}));
			progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.serveis.fi"));
			// 6. Eliminar òrgans no utilitzats
			log.debug(prefix + "Eliminant òrgans no utilitzats");
			ti = tf;
			progres.setFase(5);
			progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.eliminar"));
//			organGestorHelper.deleteExtingitsNoUtilitzats(obsoleteUnitats, progres);
			progres.setProgres(90);
			tf = System.currentTimeMillis();
			progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage(AUTO_TEMPS_TEXT, new Object[]{(tf - ti)}));
			progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.eliminar.fi"));
			// 7. Actualitzar oficines
			log.debug(prefix + "Actualitzant oficines");
			progres.setFase(6);
			ti = tf;
			progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.oficines"));
			syncOficines(entitat.getDir3Codi(), progres);
			progres.setProgres(45);
			tf = System.currentTimeMillis();
			progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage(AUTO_TEMPS_TEXT, new Object[]{(tf - ti)}));
			progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.oficines.fi"));
			cacheHelper.clearAllCaches();
			updateOrgansSessio();
			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("organgestor.actualitzacio.sincronitzar.fi"));
		} catch (Exception ex) {
			e = ex;
			progres.addInfo(TipusInfo.ERROR, messageHelper.getMessage("organgestor.actualitzacio.error") + ex.getMessage());
			throw ex;
		} finally {
			progres.setProgres(100);
			progres.setFinished(true);
			var info = new IntegracioInfo(IntegracioHelper.INTCODI_UNITATS, "Actualització d'òrgans gestors",
					IntegracioAccioTipusEnumDto.PROCESSAR, new AccioParam("Codi Dir3 de l'entitat", entitatDto.getDir3Codi()));
			info.setCodiEntitat(entitatDto.getCodi());
			for (var inf: progres.getInfo()) {
				if (inf.getText() != null) {
					info.getParams().add(new AccioParam("Msg. procés:", inf.getText()));
				}
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

 		var entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false);
		var isFirstSincronization = entitat.getDataSincronitzacio() == null;
		List<UnitatOrganitzativaDto> unitatsVigents;
		if (isFirstSincronization) {
			return predictFirstSynchronization(entitat);
		}
		try {
			// Obtenir lista de canvis del servei web
			var unitatsWS = pluginHelper.unitatsOrganitzativesFindByPare(entitat.getCodi(), entitat.getDir3Codi(), entitat.getDataActualitzacio(), entitat.getDataSincronitzacio());
			// Obtenir els òrgans vigents a la BBDD
			List<OrganGestorEntity> organsVigents = organGestorRepository.findByEntitatIdAndEstat(entitat.getId(), OrganGestorEstatEnum.V);
//			log.debug("Consulta d'unitats vigents a DB");
//			for(OrganGestorEntity organVigent: organsVigents){
//				log.debug(organVigent.toString());
//			}
			// Obtenir unitats actualment vigents en BBDD, però marcades com a obsoletes en la sincronització
			Map<String, List<NodeDir3>> mapVersionsUnitats = getMapVersionsUnitats(unitatsWS);
			var unitatsVigentObsoleteDto = getObsoletesFromWS(entitat, unitatsWS, mapVersionsUnitats, organsVigents);
			List<UnitatOrganitzativaDto> unitatsExtingides = new ArrayList<>();
			// Distinció entre divisió i (substitució o fusió)
			MultiValuedMap splitMap = new ArrayListValuedHashMap();
			MultiValuedMap mergeOrSubstMap = new ArrayListValuedHashMap();
			int transicionsVigents;
			for (var vigentObsolete : unitatsVigentObsoleteDto) {
				// Comprovam que no estigui extingida
				transicionsVigents = 0;
				if (!vigentObsolete.getLastHistoricosUnitats().isEmpty()) {
					for (var hist: vigentObsolete.getLastHistoricosUnitats()) {
						if (OrganGestorEstatEnum.V.name().equals(hist.getEstat())) {
							transicionsVigents++;
						}
					}
				}
				// En cas de no estar extingida comprovam el tipus de operació
				if (transicionsVigents > 1) {
					for (UnitatOrganitzativaDto hist : vigentObsolete.getLastHistoricosUnitats()) {
						splitMap.put(vigentObsolete, hist);
					}
				} else if (transicionsVigents == 1) {
					// check if the map already contains key with this codi
					var mergeOrSubstKeyWS = vigentObsolete.getLastHistoricosUnitats().get(0);
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
			MultiValuedMap mergeMap = new ArrayListValuedHashMap();
			MultiValuedMap substMap = new ArrayListValuedHashMap();
			List<UnitatOrganitzativaDto> values;
			for (UnitatOrganitzativaDto mergeOrSubstKey : keysMergeOrSubst) {
				values = (List<UnitatOrganitzativaDto>) mergeOrSubstMap.get(mergeOrSubstKey);
				if (values.size() <= 1) {
					substMap.put(mergeOrSubstKey, values.get(0));
					continue;
				}
				for (var value : values) {
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
			List<UnitatOrganitzativaDto> unitatsNew = getNewFromWS(entitat, unitatsWS, mapVersionsUnitats, organsVigents);
			return PrediccioSincronitzacio.builder().unitatsVigents(unitatsVigents).unitatsNew(unitatsNew).unitatsExtingides(unitatsExtingides).splitMap(splitMap)
					.substMap(substMap).mergeMap(mergeMap).build();

		} catch (SistemaExternException sex) {
			throw sex;
		} catch (Exception ex) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, "No ha estat possible obtenir la predicció de canvis de unitats organitzatives", ex);
		}
	}

	@SuppressWarnings("unchecked")
	private boolean isAlreadyAddedToMap(MultiValuedMap mergeMap, UnitatOrganitzativaDto key, UnitatOrganitzativaDto value) {

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

		var unitatsVigentsWS = pluginHelper.unitatsOrganitzativesFindByPare(entitat.getCodi(), entitat.getDir3Codi(), entitat.getDataActualitzacio(), entitat.getDataSincronitzacio());
		var vigents = conversioTipusHelper.convertirList(unitatsVigentsWS, UnitatOrganitzativaDto.class);
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
		var extingides = calcularExtingides(entitat.getCodi(), codis);
		var ex = conversioTipusHelper.convertirList(extingides, UnitatOrganitzativaDto.class);
		var n = conversioTipusHelper.convertirList(noves, UnitatOrganitzativaDto.class);
		return PrediccioSincronitzacio.builder().isFirstSincronization(true).unitatsVigents(vigents).unitatsNew(n).unitatsExtingides(ex).build();
	}

	private List<OrganGestorEntity> calcularExtingides(String entitatCodi, List<String> codis) {

		List<OrganGestorEntity> extingides = new ArrayList<>();
		var organsAExtingir = organGestorRepository.findCodiActiusByEntitat(entitatCodi);
		if (organsAExtingir.isEmpty()) {
			return extingides;
		}
		for (var codi: codis) {
			organsAExtingir.remove(codi);
		}
		if (organsAExtingir.isEmpty())
			return extingides;

		var maxInSize = 1000;
		var nParts = (organsAExtingir.size() / maxInSize) + 1;
		var inici = 0;
		var fi = organsAExtingir.size() - maxInSize > 0 ? maxInSize : organsAExtingir.size();
		List<String>  subList;
		for (var foo= 0; foo < nParts; foo++) {
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

		var timer = metricsHelper.iniciMetrica();
		try {
			var progres = progresActualitzacio.get(dir3Codi);
			if (progres != null && progres.isFinished()) {
				progresActualitzacio.remove(dir3Codi);
			}
			return progres;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private List<UnitatOrganitzativaDto> getObsoletesFromWS(EntitatEntity entitat, List<NodeDir3> unitatsWS, Map<String,List<NodeDir3>> unitats, List<OrganGestorEntity> organsVigents) {

		List<UnitatOrganitzativaDto> extingides = new ArrayList<>();
		List<NodeDir3> nodes;
		NodeDir3 node;
		for (var entry : unitats.entrySet()){
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
	private List<UnitatOrganitzativaDto> getVigentsFromWebService(EntitatEntity entitat, List<NodeDir3> unitatsWS, List<OrganGestorEntity> organsVigents){

		// list of vigent unitats from webservice
		List<NodeDir3> unitatsVigentsWithChangedAttributes = new ArrayList<>();
//		for (var unitatV : organsVigents) {
			for (var unitatWS : unitatsWS) {
//				if (unitatV.getCodi().equals(unitatWS.getCodi()) && unitatWS.getEstat().equals("V")
//						&& (unitatWS.getHistoricosUO() == null || unitatWS.getHistoricosUO().isEmpty()) && !unitatV.getCodi().equals(entitat.getDir3Codi())) {

					unitatsVigentsWithChangedAttributes.add(unitatWS);
//				}
			}
//		}
		// converting from UnitatOrganitzativa to UnitatOrganitzativaDto
		List<UnitatOrganitzativaDto> unitatsVigentsWithChangedAttributesDto = new ArrayList<>();
		UnitatOrganitzativaDto unitatOrganitzativaDto;
		OrganGestorEntity org;
		for(var vigent : unitatsVigentsWithChangedAttributes){
			unitatOrganitzativaDto = conversioTipusHelper.convertir(vigent, UnitatOrganitzativaDto.class);
			org = organGestorRepository.findByCodi(unitatOrganitzativaDto.getCodi());
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
	private List<NodeDir3> getLastHistoricos(NodeDir3 unitat, List<NodeDir3> unitatsFromWebService){

		List<NodeDir3> lastHistorcos = new ArrayList<>();
		getLastHistoricosRecursive(unitat, unitatsFromWebService, lastHistorcos);
		return lastHistorcos;
	}

	private void getLastHistoricosRecursive(NodeDir3 unitat, List<NodeDir3> unitatsFromWebService, List<NodeDir3> lastHistorics) {

		log.info("Coloca historics recursiu(" + "unitatCodi=" + unitat.getCodi() + ")");

		if (unitat.getHistoricosUO() == null || unitat.getHistoricosUO().isEmpty()) {
			lastHistorics.add(unitat);
			return;
		}
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

	private NodeDir3 getUnitatFromCodi(String codi, List<NodeDir3> allUnitats){

		for (var unitatWS : allUnitats) {
			if (unitatWS.getCodi().equals(codi)) {
				return unitatWS;
			}
		}
		return null;
	}

	@Transactional(readOnly = true)
	@Override
	public OrganGestorDto findById(Long entitatId, Long id) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de l'organ gestor (entitatId=" + entitatId + ", id=" + id + ")");EntitatEntity entitat = null;
			//TODO: verificació de permisos per administrador entitat i per administrador d'Organ
			if (entitatId != null) {
				entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			}
			var organGestor = entityComprovarHelper.comprovarOrganGestor(entitat, id);
			return conversioTipusHelper.convertir(organGestor, OrganGestorDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public OrganGestorDto findByCodi(Long entitatId, String codi) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de l'organ gestor (entitatId=" + entitatId + ", codi=" + codi + ")");
			//TODO: verificació de permisos per administrador entitat i per administrador d'Organ
			var organGestor = organGestorRepository.findByCodi(codi);
			if (organGestor == null) {
				return null;
			}
			return conversioTipusHelper.convertir(organGestor, OrganGestorDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findAccessiblesByUsuariActual() {

		var auth = SecurityContextHolder.getContext().getAuthentication();
		return permisosCacheable.findOrgansGestorsAccessiblesUsuari(auth);
	}

    @Override
	@Transactional(readOnly = true)
    public List<OrganGestorDto> findAccessiblesByUsuariAndEntitatActual(Long entitatId) {
		var auth = SecurityContextHolder.getContext().getAuthentication();
		var organsAccessibles = permisosCacheable.findOrgansGestorsAccessiblesUsuari(auth);

		return organsAccessibles != null ?
				organsAccessibles.stream().filter(o -> entitatId.equals(o.getEntitatId())).collect(Collectors.toList()) :
				new ArrayList<>();
    }

    @Transactional
	@Override
	public List<PermisDto> permisFind(Long entitatId, Long id) {

		var timer = metricsHelper.iniciMetrica();
		try {
			if (entitatId == null || id == null) {
				return new ArrayList<>();
			}
			log.debug("Consulta dels permisos de l'organ gestor (entitatId=" + entitatId +  ", id=" + id +  ")");
			//TODO: verificació de permisos per administrador entitat i per administrador d'Organ
			var entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			entityComprovarHelper.comprovarOrganGestor(entitat, id);
			return  permisosHelper.findPermisos(id, OrganGestorEntity.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public List<PermisDto> permisFind(Long entitatId, Long id,  PaginacioParamsDto paginacioParams) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta dels permisos de l'organ gestor (entitatId=" + entitatId +  ", id=" + id +  ")");
			EntitatEntity entitat = null;
			//TODO: verificació de permisos per administrador entitat i per administrador d'Organ
			if (entitatId != null) {
				entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			}
			entityComprovarHelper.comprovarOrganGestor(entitat, id);
			var permisos =  permisosHelper.findPermisos(id, OrganGestorEntity.class);
			permisosHelper.ordenarPermisos(paginacioParams, permisos);
			return permisos;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public void permisUpdate(Long entitatId, Long id, boolean isAdminOrgan, PermisDto permisDto) throws ValidationException {
		var timer = metricsHelper.iniciMetrica();

		try {
			log.debug("Modificació del permis de l'organ gestor (entitatId=" + entitatId +  ", id=" + id + ", permis=" + permisDto + ")");
			if (TipusEnumDto.ROL.equals(permisDto.getTipus())) {
				var ignoreCase = permisDto.getPrincipal().equalsIgnoreCase("tothom");
				permisDto.setPrincipal(ignoreCase ? permisDto.getPrincipal().toLowerCase() : permisDto.getPrincipal().toUpperCase());
			} else if (TipusEnumDto.USUARI.equals(permisDto.getTipus())) {
					permisDto.setPrincipal(permisDto.getPrincipal().toLowerCase());
			}
			EntitatEntity entitat = null;
			//TODO: verificació de permisos per administrador entitat i per administrador d'Organ
			if (entitatId != null) {
				entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			}
			entityComprovarHelper.comprovarOrganGestor(entitat, id);
			PermisDto permis = permisosHelper.findPermis(id, OrganGestorEntity.class, permisDto.getId());
			if (permis != null && isAdminOrgan && ((permis.getId() == null && permis.isAdministrador()) ||
					(permis.getId() != null && (permis.isAdministrador() != permisDto.isAdministrador())))) {

				throw new ValidationException("Un administrador d'òrgan no pot gestionar el permís d'admministrador d'òrgans gestors");
			}
			permisosHelper.updatePermis(id, OrganGestorEntity.class, permisDto);
			permisosCacheable.evictAllFindOrgansGestorsAccessiblesUsuari();
			permisosCacheable.evictAllFindEntitatsAccessiblesUsuari();
			cacheHelper.evictFindProcedimentServeisWithPermis();
			cacheHelper.evictFindOrgansGestorWithPermis();
			permisosCacheable.evictAllPermisosEntitatsUsuariActual();
			updateOrgansSessio();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional
	public void permisDelete(Long entitatId, Long id, Long permisId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Eliminació del permis de l'organ gestor (entitatId=" + entitatId +  ", id=" + id + ", permisId=" + permisId + ")");
			EntitatEntity entitat = null;
			//TODO: verificació de permisos per administrador entitat i per administrador d'Organ
			if (entitatId != null) {
				entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			}
			entityComprovarHelper.comprovarOrganGestor(entitat, id);
			permisosHelper.deletePermis(id, OrganGestorEntity.class, permisId);
			permisosCacheable.evictAllFindOrgansGestorsAccessiblesUsuari();
			permisosCacheable.evictAllFindEntitatsAccessiblesUsuari();
			cacheHelper.evictFindProcedimentServeisWithPermis();
			cacheHelper.evictFindOrgansGestorWithPermis();
			permisosCacheable.evictAllPermisosEntitatsUsuariActual();
			updateOrgansSessio();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<OrganismeDto> findOrganismes(EntitatDto entitat) {

		var timer = metricsHelper.iniciMetrica();
		try {
			try {
				return organGestorCachable.findOrganismesByEntitat(entitat.getDir3Codi());
			} catch (Exception e) {
				var errorMessage = "No s'han pogut recuperar els organismes de l'entitat: " + entitat.getDir3Codi();
				log.error(errorMessage, e.getMessage());
				return new ArrayList<>();
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<OrganismeDto> findOrganismes(EntitatDto entitat, OrganGestorDto organGestor) {

		var timer = metricsHelper.iniciMetrica();
		try {
			try {
				return organigramaHelper.getOrganismesFillsByOrgan(entitat.getDir3Codi(), organGestor.getCodi());
			} catch (Exception e) {
				var errorMessage = "No s'han pogut recuperar els organismes de l'entitat: " + entitat.getDir3Codi() + " i òrgan gestor: " + organGestor.getCodi();
				log.error(errorMessage, e.getMessage());
				return new ArrayList<>();
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public LlibreDto getLlibreOrganisme(Long entitatId, String organGestorDir3Codi) {

		var timer = metricsHelper.iniciMetrica();
		try {
			var entitat = entityComprovarHelper.comprovarEntitat(entitatId, true, false, false);
			try {
				//Recupera el llibre de l'òrgan gestor especificat (organisme)
				return cacheHelper.getLlibreOrganGestor(entitat.getDir3Codi(), organGestorDir3Codi);
	 		} catch (Exception e) {
	 			String errorMessage = "No s'ha pogut recuperar el llibre de l'òrgan gestor: " + organGestorDir3Codi;
				log.error(errorMessage, e.getMessage());
				return new LlibreDto();
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

    @Override
	@Transactional(readOnly = true)
    public List<CodiValorEstatDto> getOrgansGestorsDisponiblesConsulta(Long entitatId, String usuari, RolEnumDto rol, String organ) {

		var timer = metricsHelper.iniciMetrica();
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
				organsGestorsDisponibles = recuperarOrgansPerProcedimentAmbPermis(usuari, entitat, PermisEnum.CONSULTA);
			}
			organsGestorsDisponibles.sort(Comparator.comparing(OrganGestorEntity::getNom));
			String nom;
			for (var organGestor : organsGestorsDisponibles) {
				nom = organGestor.getCodi();
				if (organGestor.getNom() != null && !organGestor.getNom().isEmpty()) {
					nom += " - " + organGestor.getNom();
				}
				organsGestors.add(CodiValorEstatDto.builder().id(organGestor.getId()).codi(organGestor.getCodi()).valor(nom).estat(organGestor.getEstat()).build());
			}
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
		var organs = cacheHelper.findOrganigramaNodeByEntitat(entitat.getDir3Codi());
		organsList = new ArrayList<>();
		for (var node : organs.values()) {
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

		var timer = metricsHelper.iniciMetrica();
		try {
			var organs = cacheHelper.findOrganigramaNodeByEntitat(entitat.getDir3Codi());
			var arbre = new Arbre<OrganGestorDto>(true);
			if (organs.isEmpty()) {
				return arbre;
			}
			sotredOrgans = findByEntitat(entitat.getId());
			organsList = new ArrayList<>();
			this.isAdminOrgan = isAdminOrgan;
			this.organActual = organActual;
			var codiArrel = isAdminOrgan ? organActual.getCodi() : entitat.getDir3Codi();
			if (isAdminOrgan) {
				addCodisOrgansAmbPermis(entitat);
			}
			var arrel = new ArbreNode<>(null, conversioTipusHelper.convertir(organs.get(codiArrel), OrganGestorDto.class));
			arbre.setArrel(arrel);
			arrel.setFills(generarFillsArbre(organs, arrel, codiArrel, filtres));
			if (!Strings.isNullOrEmpty(filtres.getCodiPare())) {
				filtres.filtrarOrganPare(arbre.getArrel());
			}
			if (!filtres.isEmpty() && !filtres.filtrar(arbre.getArrel())) {
				arrel.setFills(new ArrayList<>());
			}
			return arbre;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private List<ArbreNode<OrganGestorDto>> generarFillsArbre(Map<String, OrganismeDto> organs, ArbreNode<OrganGestorDto> pare, String codiEntitat, OrganGestorFiltreDto filtres) {

		var organ = organs.get(codiEntitat);
		List<ArbreNode<OrganGestorDto>> nodes = new ArrayList<>();
		if (organ == null) {
			return nodes;
		}
		var organExistent = buscarOrgan(organ.getCodi());
		var o = organExistent != null ? organExistent : conversioTipusHelper.convertir(organ, OrganGestorDto.class);
		organsList.add(o);
		List<String> fills = organ.getFills();
		if (fills == null || fills.isEmpty()) {
			return nodes;
		}
		OrganismeDto node;
		ArbreNode<OrganGestorDto> actual;
		boolean ok;
		List<ArbreNode<OrganGestorDto>> nets;
		for (var fill: fills) {
			node = organs.get(fill);
			organExistent = buscarOrgan(node.getCodi());
			o = organExistent != null ? organExistent : conversioTipusHelper.convertir(node, OrganGestorDto.class);
			actual = new ArbreNode<>(pare, o);
			ok = filtres.filtresOk(o);
			if (!filtres.isEmpty() && ok) {
				actual.setRetornatFiltre(true);
			}
			nets = generarFillsArbre(organs, actual, node.getCodi(), filtres);
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
			return codisAmbPermis.contains(node.getDades().getCodi());
		}
		var ok = false;
		var fills = node.getFills();
		for (var fill : fills) {
			ok = codisAmbPermis.contains(fill.getDades().getCodi()) || checkPermisOrgan(fill);
		}
		return ok;
	}

	private void addCodisOrgansAmbPermis(EntitatDto entitat) {
		codisAmbPermis  = organigramaHelper.getCodisOrgansGestorsFillsByOrgan(entitat.getDir3Codi(), organActual.getCodi());
	}
	private OrganGestorDto buscarOrgan(String codi) {
		for (var o : sotredOrgans) {
			if (codi.equals(o.getCodi())) {
				return o;
			}
		}
		return null;
	}

	@Override
	@Transactional
	public OrganGestorDto getOrganNou(String codiSia) {

		var organs = pluginHelper.unitatsPerCodi(codiSia);
		return organs != null && !organs.isEmpty() ? organs.get(0) : new OrganGestorDto();
	}

	@Override
	public boolean hasPermisOrgan(Long entitatId, String organCodi, PermisEnum permis) {

		var timer = metricsHelper.iniciMetrica();
		try {
			var user = SecurityContextHolder.getContext().getAuthentication().getName();
			var codis = permisosService.getOrgansAmbPermis(entitatId, user, permis);
			for (var c : codis) {
				if (c.getCodi().equals(organCodi)) {
					return true;
				}
			}
			return false;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private void syncOficines(String entitatDir3Codi, ProgresActualitzacioDto progres) {

		var desc = "Actualització d'oficines SIR per l'entitat " + entitatDir3Codi;
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_UNITATS, desc, IntegracioAccioTipusEnumDto.PROCESSAR);
		try {
			// Obtenim l'entitat
			var entitat = entitatRepository.findByDir3Codi(entitatDir3Codi);
			if (entitat == null) {
				throw new NotFoundException(entitatDir3Codi, EntitatEntity.class);
			}
			info.setCodiEntitat(entitat.getCodi());
			addInfo(progres, info, messageHelper.getMessage("organgestor.actualitzacio.oficines"), TipusInfo.TITOL);
			// Obtenim totes les oficines vigents de l'entitat
			var oficines = pluginHelper.oficinesEntitat(entitatDir3Codi);
			addInfo(progres, info, messageHelper.getMessage("organgestor.actualitzacio.oficines.get"));
			progres.setProgres(93);
			// Eliminam totes les oficines desades a la BBDD
			oficinaRepository.deleteAll();
			// Cream les noves oficines
			OrganGestorEntity organGestor;
			OficinaEntity oficina;
			for (OficinaDto ofi: oficines) {
				organGestor = organGestorRepository.findByCodi(ofi.getOrganCodi());
				oficina = OficinaEntity.builder().codi(ofi.getCodi()).nom(ofi.getNom()).sir(ofi.isSir()).actiu(true).organGestor(organGestor).entitat(entitat).build();
				oficinaRepository.save(oficina);
			}
			addInfo(progres, info, messageHelper.getMessage("organgestor.actualitzacio.oficines.saved"));
			progres.setProgres(95);
			// Consultam els òrgans amb oficines configurades no existents
			var organsAmbOficinaInexistent = organGestorRepository.findByEntitatAndOficinaInexistent(entitat);
			if (organsAmbOficinaInexistent != null && !organsAmbOficinaInexistent.isEmpty()) {
				var arbreUnitats = cacheHelper.findOrganigramaNodeByEntitat(entitatDir3Codi);
				String oficinaOriginal;
				List<OficinaDto> oficinesOrgan;
				for (var organ : organsAmbOficinaInexistent) {
					oficinaOriginal = organ.getOficina();
					oficinesOrgan = cacheHelper.getOficinesSIRUnitat(arbreUnitats, organ.getCodi());
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
		if (param != null) {
			info.addParam(param, msg);
		}
		log.info(">>> [ACT_OFI]: " + msg);
	}

	@Override
	@Transactional(timeout = 3600)
	public void syncOficinesSIR(Long entitatId) {

		var timer = metricsHelper.iniciMetrica();
		var entity = entityComprovarHelper.comprovarEntitat(entitatId);
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

		var timer = metricsHelper.iniciMetrica();
		try {
			var entitat = entityComprovarHelper.comprovarEntitat(entitatId, true, false, false);
			List<OficinaDto> oficines = new ArrayList<>();
			try {
				if (!isFiltre) {
					var arbreUnitats = cacheHelper.findOrganigramaNodeByEntitat(entitat.getDir3Codi());
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

	private List<OrganGestorEntity> recuperarOrgansPerProcedimentAmbPermis(String usuari, EntitatEntity entitat, PermisEnum permis) {

		// 1-recuperam els òrgans dels procediments disponibles (amb permís)
		List<OrganGestorEntity> organsGestorsProcediments = new ArrayList<>();
		List<Long> procedimentsDisponiblesIds = new ArrayList<>();
		var procedimentsDisponibles = permisosService.getProcedimentsAmbPermis(entitat.getId(), usuari, permis);
		for (var pro : procedimentsDisponibles) {
			procedimentsDisponiblesIds.add(pro.getId());
		}
		if (!procedimentsDisponiblesIds.isEmpty()) {
			organsGestorsProcediments = organGestorRepository.findByProcedimentIds(procedimentsDisponiblesIds);
		}
		List<OrganGestorEntity> organsGestorsAmbPermis = new ArrayList<>();
		List<String> organsCodis = new ArrayList<>();
		// 2-recuperam els òrgans de procediments comuns
		var procsOrgans = permisosService.getProcedimentsOrgansAmbPermis(entitat.getId(), usuari, permis);
		String[] splProcOrgan;
		for(var procOrgan: procsOrgans) {
			if (Strings.isNullOrEmpty(procOrgan)) {
				continue;
			}
			splProcOrgan = procOrgan.split("-");
			if (splProcOrgan.length > 1) {
				organsCodis.add(procOrgan.split("-")[1]);
			}
		}
		// 3-recuperam els òrgans amb permís
		var organsAmbPermisDirecte = permisosService.getOrgansAmbPermis(entitat.getId(), usuari, permis);
		for (var org : organsAmbPermisDirecte) {
			organsCodis.add(org.getCodi());
		}
		// 4-Els òrgans ammb permís comú també es poden consultar
		var organsAmbPermisComuns = permisosService.getOrgansAmbPermis(entitat.getId(), usuari, PermisEnum.COMUNS);
		for (var org : organsAmbPermisComuns) {
			organsCodis.add(org.getCodi());
		}
		if (!organsCodis.isEmpty()) {
			organsGestorsAmbPermis = organGestorRepository.findByEntitatCodiAndCodiIn(entitat.getCodi(), organsCodis);
		}
		// 3-juntam tots els òrgans i ordenam per nom
		List<OrganGestorEntity> organsGestors;
		Set<OrganGestorEntity> setOrgansGestors = new HashSet<>(organsGestorsProcediments);
		setOrgansGestors.addAll(organsGestorsAmbPermis);
		organsGestors = new ArrayList<>(setOrgansGestors);
		if (Boolean.FALSE.equals(configHelper.getConfigAsBoolean("es.caib.notib.notifica.dir3.entitat.permes"))) {
			organsGestors.remove(organGestorRepository.findByCodi(entitat.getDir3Codi()));
		}
		if (procedimentsDisponibles.isEmpty() && organsGestors.isEmpty()) {
			throw new NoPermisosException("Usuari sense permios assignats");
		}
		return organsGestors;
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
			var entitats = ids != null ? entitatRepository.findByIds(ids) : entitatRepository.findAll();
			for (var entitat : entitats) {
				actualtizarNomM(entitat);
			}
		} catch (Exception ex) {
			log.error("Error sincronitzant els noms dels òrgans gestors", ex);
		}
 	}

	 public void actualtizarNomM(EntitatEntity entitat) {

		 try {
			 var nodesDir3 = pluginHelper.getOrganNomMultidioma(entitat);
			 if (nodesDir3 == null || nodesDir3.isEmpty()) {
				 return;
			 }
			 OrganGestorEntity organ;
			 String nom;
			 for (var node : nodesDir3) {
				 organ = organGestorRepository.findByCodi(node.getCodi());
				 if (organ == null) {
					 continue;
				 }
				 organ.setNomEs(node.getDenominacio());
				 nom = !Strings.isNullOrEmpty(node.getDenominacionCooficial()) ? node.getDenominacionCooficial() : node.getDenominacio();
				 organ.setNom(nom);
			 }
		 } catch (Exception ex) {
			 log.error("Error sincronitzant els nom de l'entiat " + entitat.getCodi(), ex);
		 }
	 }
}
