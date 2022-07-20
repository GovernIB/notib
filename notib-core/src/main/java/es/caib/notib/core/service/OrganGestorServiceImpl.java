package es.caib.notib.core.service;

import com.codahale.metrics.Timer;
import com.google.common.base.Strings;
import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.core.api.dto.organisme.OrganGestorFiltreDto;
import es.caib.notib.core.api.dto.organisme.OrganismeDto;
import es.caib.notib.core.api.dto.organisme.PrediccioSincronitzacio;
import es.caib.notib.core.api.dto.organisme.UnitatOrganitzativaDto;
import es.caib.notib.core.api.exception.NoPermisosException;
import es.caib.notib.core.api.exception.SistemaExternException;
import es.caib.notib.core.api.service.OrganGestorService;
import es.caib.notib.core.cacheable.OrganGestorCachable;
import es.caib.notib.core.cacheable.PermisosCacheable;
import es.caib.notib.core.cacheable.ProcSerCacheable;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.GrupEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.ProcSerEntity;
import es.caib.notib.core.entity.ProcSerOrganEntity;
import es.caib.notib.core.entity.cie.EntregaCieEntity;
import es.caib.notib.core.entity.cie.PagadorCieEntity;
import es.caib.notib.core.entity.cie.PagadorPostalEntity;
import es.caib.notib.core.helper.*;
import es.caib.notib.core.repository.AvisRepository;
import es.caib.notib.core.repository.EntregaCieRepository;
import es.caib.notib.core.repository.GrupRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.core.repository.PagadorCieRepository;
import es.caib.notib.core.repository.PagadorPostalRepository;
import es.caib.notib.core.repository.ProcSerRepository;
import es.caib.notib.plugin.unitat.NodeDir3;
import lombok.Getter;
import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.model.Permission;
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
@Service
public class OrganGestorServiceImpl implements OrganGestorService{

	@Resource
	private OrganGestorRepository organGestorRepository;
	@Resource
	private ProcSerRepository procSerRepository;
	@Resource
	private NotificacioRepository notificacioRepository;
	@Resource
	private AvisRepository avisRepository;
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
	private ProcSerCacheable procedimentsCacheable;
	@Resource
	private ConfigHelper configHelper;
	@Autowired
	private EntregaCieRepository entregaCieRepository;
	@Autowired
	private PluginHelper pluginHelper;
	@Resource
	private MessageHelper messageHelper;

	List<OrganGestorDto> sotredOrgans = new ArrayList<>();

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
			entityComprovarHelper.comprovarEntitat(
					dto.getEntitatId(), 
					false, 
					true, 
					false);
			
			OrganGestorEntity organGestor = organGestorRepository.findOne(dto.getId());
			organGestor.updateOficina(
					dto.getOficina().getCodi(),
					dto.getOficina().getNom());

			EntregaCieEntity entregaCie = organGestor.getEntregaCie();
			if (dto.isEntregaCieActiva()) {
				if (entregaCie == null) {
					entregaCie = entregaCieRepository.save(
							new EntregaCieEntity(dto.getCieId(), dto.getOperadorPostalId())
					);
				} else {
					entregaCie.update(dto.getCieId(), dto.getOperadorPostalId());
				}
			}

			organGestor.updateEntregaCie(dto.isEntregaCieActiva() ? entregaCie : null);
			if (!dto.isEntregaCieActiva() && entregaCie != null) {
				entregaCieRepository.delete(entregaCie);
			}

			return conversioTipusHelper.convertir(
					organGestor,
					OrganGestorDto.class);
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

			List<PagadorCieEntity> pagCiesByOrganGestor = pagadorCieReposity.findByOrganGestorId(organId);
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
				organsGestors.add(new CodiValorEstatDto(organ.getCodi(), organ.getCodi() + " - " + organ.getNom(), organ.getEstat()));
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

		logger.debug("Consulta taula òrgans gestors per administrador d'entitat");
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

		logger.debug("Consulta taula òrgans gestors per administrador d'òrgan");
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
//				logger.debug(String.format(
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

	@Override
	public Object[] syncDir3OrgansGestors(Long entitatId) throws Exception {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false);
		EntitatDto entitatDto = conversioTipusHelper.convertir(entitat, EntitatDto.class);

		if (entitat.getDir3Codi() == null || entitat.getDir3Codi().isEmpty()) {
			throw new Exception("L'entitat actual no té cap codi DIR3 associat");
		}

		// Comprova si hi ha una altre instància del procés en execució
		ProgresActualitzacioDto progres = progresActualitzacio.get(entitat.getDir3Codi());
		if (progres != null && (progres.getProgres() > 0 && progres.getProgres() < 100) && !progres.isError()) {
			logger.debug("[PROCEDIMENTS] Ja existeix un altre procés que està executant l'actualització");
			return null;	// Ja existeix un altre procés que està executant l'actualització.
		}

		// inicialitza el seguiment del progrés d'actualització
		progres = new ProgresActualitzacioDto();
		progresActualitzacio.put(entitat.getDir3Codi(), progres);

		progres.setNumOperacions(6);
		progres.addInfo(ProgresActualitzacioDto.TipusInfo.TITOL, messageHelper.getMessage("organgestor.actualitzar.titol" ));

		Long ti = System.currentTimeMillis();

		// 1. Obtenir canvis a l'organigrama
		progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBINFO, messageHelper.getMessage("organgestor.actualitzacio.obtenir.canvis"));
		List<NodeDir3> unitatsWs = pluginHelper.unitatsOrganitzativesFindByPare(
				entitatDto,
				entitat.getDir3Codi(),
				entitat.getDataActualitzacio(),
				entitat.getDataSincronitzacio());
		progres.incrementOperacionsRealitzades();
		Long tf = System.currentTimeMillis();

		progres.addInfo(ProgresActualitzacioDto.TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[] {(tf - ti)}));
		progres.addInfo(ProgresActualitzacioDto.TipusInfo.INFO, messageHelper.getMessage("organgestor.actualitzacio.obtenir.canis.fi.resultat", new Object[] {unitatsWs.size()}));
		progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.obtenir.canis.fi"));

		List<OrganGestorEntity> obsoleteUnitats = new ArrayList<>();
		List<OrganGestorEntity> organsDividits = new ArrayList<>();
		List<OrganGestorEntity> organsFusionats = new ArrayList<>();
		List<OrganGestorEntity> organsSubstituits = new ArrayList<>();

		// 2. Sincronitzar òrgans
		ti = tf;
		progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBINFO, messageHelper.getMessage("organgestor.actualitzacio.sincronitzar"));
		organGestorHelper.sincronitzarOrgans(entitatId, unitatsWs, obsoleteUnitats, organsDividits, organsFusionats, organsSubstituits);
		progres.incrementOperacionsRealitzades();
		tf = System.currentTimeMillis();

		progres.addInfo(ProgresActualitzacioDto.TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[] {(tf - ti)}));
		progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.sincronitzar.fi"));

		// 3. Actualitzar procediments
		ti = tf;
		progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBINFO, messageHelper.getMessage("organgestor.actualitzacio.procediments"));
		procSerSyncHelper.actualitzaProcediments(entitatDto);
		progres.incrementOperacionsRealitzades();
		tf = System.currentTimeMillis();

		progres.addInfo(ProgresActualitzacioDto.TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[] {(tf - ti)}));
		progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.procediments.fi"));

		// 4. Actualitzar serveis
		ti = tf;
		progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBINFO, messageHelper.getMessage("organgestor.actualitzacio.serveis"));
		procSerSyncHelper.actualitzaServeis(entitatDto);
		progres.incrementOperacionsRealitzades();
		tf = System.currentTimeMillis();

		progres.addInfo(ProgresActualitzacioDto.TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[] {(tf - ti)}));
		progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.serveis.fi"));

		// 5. Actualitzar permisos
		ti = tf;
		progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBINFO, messageHelper.getMessage("organgestor.actualitzacio.permisos"));
		permisosHelper.actualitzarPermisosOrgansObsolets(unitatsWs, organsDividits, organsFusionats, organsSubstituits);
		progres.incrementOperacionsRealitzades();
		tf = System.currentTimeMillis();

		progres.addInfo(ProgresActualitzacioDto.TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[] {(tf - ti)}));
		progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.permisos.fi"));

		// 6. Eliminar òrgans no utilitzats
		ti = tf;
		progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBINFO, messageHelper.getMessage("organgestor.actualitzacio.eliminar"));
		organGestorHelper.deleteExtingitsNoUtilitzats(obsoleteUnitats);
		progres.incrementOperacionsRealitzades();
		tf = System.currentTimeMillis();

		progres.addInfo(ProgresActualitzacioDto.TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[] {(tf - ti)}));
		progres.addInfo(ProgresActualitzacioDto.TipusInfo.INFO, messageHelper.getMessage("organgestor.actualitzacio.eliminar.fi.resultat", new Object[] {unitatsWs.size()}));
		progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBTITOL, messageHelper.getMessage("organgestor.actualitzacio.eliminar.fi"));

		cacheHelper.evictFindOrgansGestorWithPermis();

		progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBINFO, messageHelper.getMessage("organgestor.actualitzacio.obtenir.canvis"));

		progres.setProgres(100);
		progres.setFinished(true);

		return new ArrayList[]{(ArrayList) obsoleteUnitats, (ArrayList) organsDividits, (ArrayList) organsFusionats, (ArrayList) organsSubstituits};
	}

	@SuppressWarnings({"deprecation", "unchecked"})
	@Override
	public PrediccioSincronitzacio predictSyncDir3OrgansGestors(Long entitatId) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false);

		boolean isFirstSincronization = entitat.getDataSincronitzacio() == null;
		List<UnitatOrganitzativaDto> unitatsVigents = new ArrayList<>();

		if (isFirstSincronization) {
			return PrediccioSincronitzacio.builder()
					.isFirstSincronization(isFirstSincronization)
					.unitatsVigents(predictFirstSynchronization(entitat))
					.build();
		}

		try {
			// Obtenir lista de canvis del servei web
			List<NodeDir3> unitatsWS = pluginHelper.unitatsOrganitzativesFindByPare(
					conversioTipusHelper.convertir(entitat, EntitatDto.class),
					entitat.getDir3Codi(),
					entitat.getDataActualitzacio(),
					entitat.getDataSincronitzacio());

			// Obtenir els òrgans vigents a la BBDD
			List<OrganGestorEntity> organsVigents = organGestorRepository.findByEntitatIdAndEstat(entitat.getId(), OrganGestorEstatEnum.V);
			logger.debug("Consulta d'unitats vigents a DB");
			for(OrganGestorEntity organVigent: organsVigents){
				logger.debug(organVigent.toString());
			}

			// Obtenir unitats actualment vigents en BBDD, però marcades com a obsoletes en la sincronització
			List<UnitatOrganitzativaDto> unitatsVigentObsoleteDto = getObsoletesFromWS(entitat, unitatsWS, organsVigents);
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
			for (UnitatOrganitzativaDto mergeOrSubstKey : keysMergeOrSubst) {
				List<UnitatOrganitzativaDto> values = (List<UnitatOrganitzativaDto>) mergeOrSubstMap
						.get(mergeOrSubstKey);
				if (values.size() > 1) {
					for (UnitatOrganitzativaDto value : values) {
						mergeMap.put(mergeOrSubstKey, value);
					}
				} else {
					substMap.put(mergeOrSubstKey, values.get(0));
				}
			}

			// Obtenir llistat d'unitats que ara estan vigents en BBDD, i després de la sincronització continuen vigents, però amb les propietats canviades
			unitatsVigents = getVigentsFromWebService(entitat, unitatsWS, organsVigents);

			// Obtenir el llistat d'unitats que son totalment noves (no existeixen en BBDD): Creació
			List<UnitatOrganitzativaDto> unitatsNew = getNewFromWS(entitat, unitatsWS, organsVigents);

			return PrediccioSincronitzacio.builder()
					.unitatsVigents(unitatsVigents)
					.unitatsNew(unitatsNew)
					.unitatsExtingides(unitatsExtingides)
					.splitMap(splitMap)
					.substMap(substMap)
					.mergeMap(mergeMap)
					.build();

		} catch (Exception ex) {
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_UNITATS,
					"No ha estat possible obtenir la predicció de canvis de unitats organitzatives",
					ex);
		}
	}

	private List<UnitatOrganitzativaDto> predictFirstSynchronization(EntitatEntity entitat) throws SistemaExternException {
		List<NodeDir3> unitatsVigentsWS = pluginHelper.unitatsOrganitzativesFindByPare(
				conversioTipusHelper.convertir(entitat, EntitatDto.class),
				entitat.getDir3Codi(),
				entitat.getDataActualitzacio(),
				entitat.getDataSincronitzacio());
		return conversioTipusHelper.convertirList(unitatsVigentsWS, UnitatOrganitzativaDto.class);
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

	private List<UnitatOrganitzativaDto> getObsoletesFromWS(
			EntitatEntity entitat,
			List<NodeDir3> unitatsWS,
			List<OrganGestorEntity> organsVigents) {

		// Llista d'òrgans obsolets des del servei web, que eren vignets a la última sincronització (vigent a BBDD i obsolet al servei web)
		// No obtenim la llista d'òrgans obsolets directament de BBDD degut a que hi pot haver canvis acumulats:
		// si a la darrere sincrocització la unitat A cavia a B, i després a C, llavors en la BBDD tindrem A(vigent) però des del servei web tindrem: A(Extingit) -> B(Extingit) -> C(Vigent)
		// Només volem retornar A (no volem B) perquè la predicció ha de mostrar la transició (A -> C) [entre A (vigent a BBDD) i C (vigent al servei web)]
		List<NodeDir3> organsVigentObsolete = new ArrayList<>();
		for (OrganGestorEntity organVigent : organsVigents) {
			for (NodeDir3 unitatWS : unitatsWS) {
				if (organVigent.getCodi().equals(unitatWS.getCodi()) && !unitatWS.getEstat().equals("V")
						&& !organVigent.getCodi().equals(entitat.getDir3Codi())) {
					organsVigentObsolete.add(unitatWS);
				}
			}
		}
		logger.debug("Consulta unitats obsolete ");
		for (NodeDir3 vigentObsolete : organsVigentObsolete) {
			logger.debug(vigentObsolete.getCodi()+" "+vigentObsolete.getEstat()+" "+vigentObsolete.getHistoricosUO());
		}
		for (NodeDir3 vigentObsolete : organsVigentObsolete) {

			// Fer que un òrgan obsolet apunti a l'últim òrgan/s al que ha fet la transició
			// El nom del camp historicosUO és totalment erroni, ja que el camp mostra unitats futures, no històric. Però així és com s'anomena al servei web, i no ho podem canviar.
			// El camp lastHistoricosUnitats hauria d'apuntar a la darrera unitat a la que ha fet la trasició. Necessitem trobar la darrera unitat de forma recursiva, perquè és possible que hi hagi canvis acumulats:
			// Si la darrera sincronització de la unitat A canvia a B, i després a C, des del servei web tindrés la unitat A apuntant a B (A -> B) i la unitat B apuntant a C (B -> C)
			// El que volem és afegir un punter directe des de la unitat A a la unitat C (A -> C)
			vigentObsolete.setLastHistoricosUnitats(getLastHistoricos(vigentObsolete, unitatsWS));
		}
		// converting from UnitatOrganitzativa to UnitatOrganitzativaDto
		List<UnitatOrganitzativaDto> unitatsVigentObsoleteDto = new ArrayList<>();
		for(NodeDir3 vigentObsolete : organsVigentObsolete){
			unitatsVigentObsoleteDto.add(conversioTipusHelper.convertir(
					vigentObsolete,
					UnitatOrganitzativaDto.class));
		}
		return unitatsVigentObsoleteDto;
	}

	// Obtenir unitats que no fan cap transició a cap altre unitat, però a la que se'ls canvia alguna propietat
	private List<UnitatOrganitzativaDto> getVigentsFromWebService(
			EntitatEntity entitat,
			List<NodeDir3> unitatsWS,
			List<OrganGestorEntity> organsVigents){
		// list of vigent unitats from webservice
		List<NodeDir3> unitatsVigentsWithChangedAttributes = new ArrayList<>();
		for (OrganGestorEntity unitatV : organsVigents) {
			for (NodeDir3 unitatWS : unitatsWS) {
				if (unitatV.getCodi().equals(unitatWS.getCodi()) && unitatWS.getEstat().equals("V")
						&& (unitatWS.getHistoricosUO() == null || unitatWS.getHistoricosUO().isEmpty())
						&& !unitatV.getCodi().equals(entitat.getDir3Codi())) {
					unitatsVigentsWithChangedAttributes.add(unitatWS);
				}
			}
		}
		// converting from UnitatOrganitzativa to UnitatOrganitzativaDto
		List<UnitatOrganitzativaDto> unitatsVigentsWithChangedAttributesDto = new ArrayList<>();
		for(NodeDir3 vigent : unitatsVigentsWithChangedAttributes){
			unitatsVigentsWithChangedAttributesDto.add(conversioTipusHelper.convertir(
					vigent,
					UnitatOrganitzativaDto.class));
		}
		return unitatsVigentsWithChangedAttributesDto;
	}

	// Obtenir unitats organitzatives noves (No provenen de cap transició d'una altre unitat)
	private List<UnitatOrganitzativaDto> getNewFromWS(
			EntitatEntity entitat,
			List<NodeDir3> unitatsWS,
			List<OrganGestorEntity> organsVigents){
		//List of new unitats that are vigent
		List<NodeDir3> vigentUnitatsWS = new ArrayList<>();
		//List of new unitats that are vigent and does not exist in database
		List<NodeDir3> vigentNotInDBUnitatsWS = new ArrayList<>();
		//List of new unitats (that are vigent, not pointed by any obsolete unitat and does not exist in database)
		List<NodeDir3> newUnitatsWS = new ArrayList<>();
		//Filtering to only obtain vigents
		for (NodeDir3 unitatWS : unitatsWS) {
			if (unitatWS.getEstat().equals("V") && !unitatWS.getCodi().equals(entitat.getDir3Codi())) {
				vigentUnitatsWS.add(unitatWS);
			}
		}
		// Filtering to only obtain vigents that does not already exist in database
		for (NodeDir3 vigentUnitat : vigentUnitatsWS) {
			boolean found = false;
			for (OrganGestorEntity vigentUnitatDB : organsVigents) {
				if (vigentUnitatDB.getCodi().equals(vigentUnitat.getCodi())) {
					found = true;
					break;
				}
			}
			if (found == false) {
				vigentNotInDBUnitatsWS.add(vigentUnitat);
			}
		}
		// Filtering to obtain unitats that are vigent, not pointed by any obsolete unitat and does not already exist in database
		for (NodeDir3 vigentNotInDBUnitatWS : vigentNotInDBUnitatsWS) {
			boolean pointed = false;
			for (NodeDir3 unitatWS : unitatsWS) {
				if(unitatWS.getHistoricosUO()!=null){
					for(String novaCodi: unitatWS.getHistoricosUO()){
						if(novaCodi.equals(vigentNotInDBUnitatWS.getCodi())){
							pointed = true;
							break;
						}
					}
				}
				if (pointed) break;
			}
			if (pointed == false) {
				newUnitatsWS.add(vigentNotInDBUnitatWS);
			}
		}
		// converting from UnitatOrganitzativa to UnitatOrganitzativaDto
		List<UnitatOrganitzativaDto> newUnitatsDto = new ArrayList<>();
		for (NodeDir3 vigent : newUnitatsWS){
			newUnitatsDto.add(conversioTipusHelper.convertir(
					vigent,
					UnitatOrganitzativaDto.class));
		}
		return newUnitatsDto;
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

		logger.debug("Coloca historics recursiu(" + "unitatCodi=" + unitat.getCodi() + ")");

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
				} else {
					getLastHistoricosRecursive(
							unitatFromCodi,
							unitatsFromWebService,
							lastHistorics);
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
//			logger.info("Actualitzant noms dels òrgans gestors");
//
//			//TODO: verificació de permisos per administrador entitat i per administrador d'Organ
//			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
//
//			// Comprova si hi ha una altre instància del procés en execució
//			ProgresActualitzacioDto progres = progresActualitzacio.get(entitat.getDir3Codi());
//			if (progres != null && (progres.getProgres() > 0 && progres.getProgres() < 100) && !progres.isError()) {
//				logger.debug("[PROCEDIMENTS] Ja existeix un altre procés que està executant l'actualització");
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
//				logger.info("Fi - updateNom del òrgan gestor: " + organGestor);
//				if (!status) {
//					logger.error(String.format("Actualització òrgan (%s): Error actualitzant nom ", organGestor.getCodi()));
//					progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBINFO, messageHelper.getMessage("organgestor.actualitzacio.organ.error.actualtizar.nom"));
//				}
//				status = updateLlibre(entitat, organGestor);
//				logger.info("Fi - updateLlibre del òrgan gestor: " + organGestor);
//				if (!status) {
//					logger.error(String.format("Actualització òrgan (%s): Error actualitzant llibre ", organGestor.getCodi()));
//					progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBINFO, messageHelper.getMessage("organgestor.actualitzacio.organ.error.actualtizar.llibre"));
//				}
//
//				status = updateOficina(entitat, organGestor, arbreUnitats);
//				logger.info("Fi - updateOficina del òrgan gestor: " + organGestor);
//				if (!status) {
//					logger.error(String.format("Actualització òrgan (%s): Error actualitzant oficina ", organGestor.getCodi()));
//					progres.addInfo(ProgresActualitzacioDto.TipusInfo.SUBINFO, messageHelper.getMessage("organgestor.actualitzacio.organ.error.actualtizar.oficina"));
//				}
//
//				status = updateEstat(organGestor, arbreUnitats);
//				logger.info("Fi - updateEstat del òrgan gestor: " + organGestor);
//				if (!status) {
//					logger.error(String.format("Actualització òrgan (%s): Error actualitzant estat ", organGestor.getCodi()));
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
//			logger.info("Antes de Update de las tablas correspondientes a las datatables");
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
//			logger.error(String.format("La denominacio de l'òrgan gestor %s de l'entitat %s no s'ha pogut actualitzar",
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
			logger.error(String.format("El llibre de l'òrgan gestor %s de l'entitat %s no s'ha pogut actualitzar",
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
				logger.debug(String.format("L'òrgan gestor %s no disposa de cap oficina", organGestor.getCodi()));
		} catch (Exception e) {
			logger.error(String.format("L'oficina de l'òrgan gestor %s de l'entitat %s no s'ha pogut actualitzar",
					organGestor.getCodi(),
					entitat.getDir3Codi()));
			e.printStackTrace();
			return false;
		}
		return true;
	}

//	private boolean updateEstat(OrganGestorEntity organGestor, Map<String, OrganismeDto> arbreUnitats) {
//
//		logger.info("Ini - updateEstat del òrgan gestor: " + organGestor.getCodi() + "-" + organGestor.getNom());
//		if (!arbreUnitats.containsKey(organGestor.getCodi())) {
//			logger.trace(String.format("Organ Gestor (%s) no trobat a l'organigrama", organGestor.getCodi()));
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
			logger.debug("Consulta de l'organ gestor ("
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
			logger.debug("Consulta de l'organ gestor (entitatId=" + entitatId + ", codi=" + codi + ")");
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
			logger.debug("Consulta dels permisos de l'organ gestor (entitatId=" + entitatId +  ", id=" + id +  ")");
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
			logger.debug("Consulta dels permisos de l'organ gestor ("
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
			logger.debug("Modificació del permis de l'organ gestor ("
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
			logger.debug("Eliminació del permis de l'organ gestor ("
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
			List<OrganismeDto> organismes = new ArrayList<OrganismeDto>();
			try {
				organismes = organGestorCachable.findOrganismesByEntitat(entitat.getDir3Codi());
			} catch (Exception e) {
				String errorMessage = "No s'han pogut recuperar els organismes de l'entitat: " + entitat.getDir3Codi();
				logger.error(
						errorMessage, 
						e.getMessage());
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
				logger.error(
						errorMessage, 
						e.getMessage());
			}
			return organismes;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public LlibreDto getLlibreOrganisme(
			Long entitatId,
			String organGestorDir3Codi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId, 
					true, 
					false, 
					false);
			LlibreDto llibre = new LlibreDto();
			try {
				//Recupera el llibre de l'òrgan gestor especificat (organisme)
				llibre = cacheHelper.getLlibreOrganGestor(
						entitat.getDir3Codi(),
						organGestorDir3Codi);
	 		} catch (Exception e) {
	 			String errorMessage = "No s'ha pogut recuperar el llibre de l'òrgan gestor: " + organGestorDir3Codi;
				logger.error(
						errorMessage, 
						e.getMessage());
			}
			return llibre;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findOrgansGestorsWithPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId, 
					true,
					false, 
					false);
			Permission[] permisos = entityComprovarHelper.getPermissionsFromName(permis);

			return conversioTipusHelper.convertirList(
					permisosCacheable.findOrgansGestorsWithPermis(entitat, auth, permisos),
					OrganGestorDto.class);

		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

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
				organsGestors.add(new CodiValorEstatDto(organGestor.getId().toString(), nom, organGestor.getEstat()));
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
	public Arbre<OrganGestorDto> generarArbreOrgans(EntitatDto entitat, OrganGestorFiltreDto filtres) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			Map<String, OrganismeDto> organs = cacheHelper.findOrganigramaNodeByEntitat(entitat.getDir3Codi());
			Arbre<OrganGestorDto> arbre = new Arbre<>(true);
			if (organs.isEmpty()) {
				return arbre;
			}
			sotredOrgans = findByEntitat(entitat.getId());
			organsList = new ArrayList<>();
			ArbreNode<OrganGestorDto> arrel = new ArbreNode<>(null, conversioTipusHelper.convertir(organs.get(entitat.getDir3Codi()), OrganGestorDto.class));
			arbre.setArrel(arrel);
			arrel.setFills(generarFillsArbre(organs, arrel, entitat.getDir3Codi(), filtres));
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

	public List<ArbreNode<OrganGestorDto>> generarFillsArbre(Map<String, OrganismeDto> organs, ArbreNode<OrganGestorDto> pare,
															  String codiEntitat, OrganGestorFiltreDto filtres) {

		OrganismeDto organ = organs.get(codiEntitat);
		List<ArbreNode<OrganGestorDto>> nodes = new ArrayList<>();
		if (organ == null) {
			return nodes;
		}
		OrganGestorDto organExsitent = buscarOrgan(organ.getCodi());
		OrganGestorDto o = organExsitent != null ? organExsitent : conversioTipusHelper.convertir(organ, OrganGestorDto.class);
		organsList.add(o);
		List<String> fills = organ.getFills();
		if (fills == null || fills.isEmpty()) {
			return nodes;
		}

		for (String fill: fills) {
			OrganismeDto node = organs.get(fill);
			organExsitent = buscarOrgan(node.getCodi());
			o = organExsitent != null ? organExsitent : conversioTipusHelper.convertir(node, OrganGestorDto.class);
			ArbreNode<OrganGestorDto> actual = new ArbreNode<>(pare, o);
			if (!filtres.isEmpty() && filtres.filtresOk(o)) {
				actual.setRetornatFiltre(true);
			}
			List<ArbreNode<OrganGestorDto>> nets = generarFillsArbre(organs, actual, node.getCodi(), filtres);
			actual.setFills(nets);
			nodes.add(actual);
		}

		return nodes;
	}

	private OrganGestorDto buscarOrgan(String codi) {
		for (OrganGestorDto o : sotredOrgans) {
			if (codi.equals(o.getCodi())) {
				return o;
			}
		}
		return null;
	}

	public OrganGestorDto getOrganNou(String codiSia) {

		List<OrganGestorDto> organs = pluginHelper.unitatsPerCodi(codiSia);
		return organs != null && !organs.isEmpty() ? organs.get(0) : new OrganGestorDto();
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
				logger.error(errorMessage, e.getMessage());
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

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Permission[] permisos = entityComprovarHelper.getPermissionsFromName(permis);
		List<ProcSerEntity> procedimentsDisponibles = procedimentsCacheable.getProcedimentsWithPermis(
				usuari,
				entitat,
				permisos);
		List<ProcSerOrganEntity> procedimentsOrgansDisponibles = procedimentsCacheable.getProcedimentOrganWithPermis(
				auth,
				entitat,
				permisos);

		procedimentsDisponibles = mergeProcedimentsWithProcedimentsOrgans(procedimentsDisponibles, procedimentsOrgansDisponibles);

		List<OrganGestorEntity> organsGestorsProcediments = new ArrayList<>();
		List<Long> procedimentsDisponiblesIds = new ArrayList<>();
		for (ProcSerEntity pro : procedimentsDisponibles) {
			procedimentsDisponiblesIds.add(pro.getId());
		}
		// 1-recuperam els òrgans dels procediments disponibles (amb permís)
		if (!procedimentsDisponiblesIds.isEmpty())
			organsGestorsProcediments = organGestorRepository.findByProcedimentIds(procedimentsDisponiblesIds);
		// 2-recuperam els òrgans amb permís
		List<OrganGestorEntity> organsGestorsAmbPermis = organGestorHelper.getOrgansGestorsWithPermis(usuari, auth, entitat, permisos);
		// 3-juntam tots els òrgans i ordenam per nom
		List<OrganGestorEntity> organsGestors;
		Set<OrganGestorEntity> setOrgansGestors = new HashSet<>(organsGestorsProcediments);
		setOrgansGestors.addAll(organsGestorsAmbPermis);
		if (procedimentsOrgansDisponibles != null) {
			for (ProcSerOrganEntity procedimentOrgan : procedimentsOrgansDisponibles) {
				setOrgansGestors.add(procedimentOrgan.getOrganGestor());
			}
		}
		organsGestors = new ArrayList<>(setOrgansGestors);
		if (!configHelper.getAsBooleanByEntitat("es.caib.notib.notifica.dir3.entitat.permes")) {
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

	private static final Logger logger = LoggerFactory.getLogger(OrganGestorServiceImpl.class);

	// Sync Testing:
	@Override
	public void setServicesForSynctest(Object procSerSyncHelper, Object pluginHelper) {
		this.procSerSyncHelper = (ProcSerSyncHelper)procSerSyncHelper;
		this.pluginHelper = (PluginHelper)pluginHelper;
	}
}
