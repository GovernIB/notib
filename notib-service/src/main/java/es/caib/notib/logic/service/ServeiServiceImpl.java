package es.caib.notib.logic.service;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.aspect.Audita;
import es.caib.notib.logic.cacheable.OrganGestorCachable;
import es.caib.notib.logic.helper.CacheHelper;
import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.EntityComprovarHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.helper.OrganigramaHelper;
import es.caib.notib.logic.helper.PaginacioHelper;
import es.caib.notib.logic.helper.PermisosHelper;
import es.caib.notib.logic.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.logic.helper.ProcSerHelper;
import es.caib.notib.logic.helper.ProcSerSyncHelper;
import es.caib.notib.logic.intf.dto.CodiValorOrganGestorComuDto;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.GrupDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.PermisDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.ProcSerTipusEnum;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioDto;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDataDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerFiltreDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerFormDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerGrupDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerSimpleDto;
import es.caib.notib.logic.intf.dto.procediment.ProcedimentEstat;
import es.caib.notib.logic.intf.dto.procediment.ProgresActualitzacioProcSer;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.exception.PermissionDeniedException;
import es.caib.notib.logic.intf.service.AuditService.TipusEntitat;
import es.caib.notib.logic.intf.service.AuditService.TipusObjecte;
import es.caib.notib.logic.intf.service.AuditService.TipusOperacio;
import es.caib.notib.logic.intf.service.GrupService;
import es.caib.notib.logic.intf.service.PermisosService;
import es.caib.notib.logic.intf.service.ServeiService;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.GrupProcSerEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.ProcSerOrganEntity;
import es.caib.notib.persist.entity.ProcedimentEntity;
import es.caib.notib.persist.entity.ServeiEntity;
import es.caib.notib.persist.entity.ServeiFormEntity;
import es.caib.notib.persist.entity.cie.EntregaCieEntity;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.EntregaCieRepository;
import es.caib.notib.persist.repository.EnviamentTableRepository;
import es.caib.notib.persist.repository.GrupProcSerRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.persist.repository.NotificacioTableViewRepository;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.ProcSerOrganRepository;
import es.caib.notib.persist.repository.ServeiFormRepository;
import es.caib.notib.persist.repository.ServeiRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Implementació del servei de gestió de serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class ServeiServiceImpl implements ServeiService {

	@Autowired
	private PermisosService permisosService;
	@Resource
	private ServeiRepository serveiRepository;
	@Resource
	private ProcSerOrganRepository procSerOrganRepository;
	@Resource
	private ServeiFormRepository serveiFormRepository;
	@Resource
	private OrganGestorRepository organGestorRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private ProcSerHelper serveiHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private GrupService grupService;
	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private GrupProcSerRepository grupServeiRepository;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private CacheHelper cacheHelper;
	@Resource
	private OrganigramaHelper organigramaHelper;
	@Resource
	private MetricsHelper metricsHelper;
	@Resource
	private NotificacioRepository notificacioRepository;
	@Resource
	private ProcSerOrganRepository serveiOrganRepository;
	@Autowired
	private OrganGestorCachable organGestorCachable;

	@Autowired
	private ProcSerSyncHelper procSerSyncHelper;
	@Autowired
	private EntregaCieRepository entregaCieRepository;
	@Autowired
	private EnviamentTableRepository enviamentTableRepository;
	@Autowired
	private NotificacioTableViewRepository notificacioTableViewRepository;

	public static final String SERVEI_ORGAN_NO_SYNC = "Hi ha serveis que pertanyen a òrgans no existents en l'organigrama actual";
	@Getter
	private static Map<String, ProgresActualitzacioProcSer> progresActualitzacioServeis = new HashMap<>();
	@Getter
	private static Map<Long, Integer> serveisAmbOrganNoSincronitzat = new HashMap<>();
	
	@Audita(entityType = TipusEntitat.SERVEI, operationType = TipusOperacio.CREATE, returnType = TipusObjecte.DTO)
	@Override
	@Transactional
	public ProcSerDto create(Long entitatId, ProcSerDataDto servei) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Creant un nou servei (servei=" + servei + ")");
			var entitat = entityComprovarHelper.comprovarEntitat(entitatId);

			// Organ gestor
			var organGestor = organGestorRepository.findByCodi(servei.getOrganGestor());
			if (organGestor == null) {
				throw new NotFoundException(servei.getOrganGestor(), OrganGestorEntity.class);
			}
			
			var serveiEntityBuilder = ServeiEntity.getBuilder(servei.getCodi(), servei.getNom(), servei.getRetard(), servei.getCaducitat(), entitat,
										servei.isAgrupar(), organGestor, servei.getTipusAssumpte(), servei.getTipusAssumpteNom(), servei.getCodiAssumpte(),
										servei.getCodiAssumpteNom(), servei.isComu(), servei.isRequireDirectPermission(), servei.isManual());

			if (servei.isEntregaCieActiva()) {
				EntregaCieEntity entregaCie = new EntregaCieEntity(servei.getCieId(), servei.getOperadorPostalId());
				serveiEntityBuilder.entregaCie(entregaCieRepository.save(entregaCie));
			}
			cacheHelper.evictFindProcedimentServeisWithPermis();
			return conversioTipusHelper.convertir(serveiRepository.save(serveiEntityBuilder.build()), ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Audita(entityType = TipusEntitat.SERVEI, operationType = TipusOperacio.UPDATE, returnType = TipusObjecte.DTO)
	@Override
	@Transactional
	public ProcSerDto update(Long entitatId, ProcSerDataDto servei, boolean isAdmin, boolean isAdminEntitat) throws NotFoundException {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Actualitzant servei (servei=" + servei + ")");
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (!isAdminEntitat && servei.isComu()) {
				throw new PermissionDeniedException(servei.getId(), ServeiEntity.class, auth.getName(), "ADMINISTRADORENTITAT");
			}
			
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false);
			ServeiEntity serveiEntity;
			if(!isAdmin) {
				serveiEntity = (ServeiEntity) entityComprovarHelper.comprovarProcediment(entitat, servei.getId());
			} else {
				serveiEntity = serveiRepository.findById(servei.getId()).orElseThrow();
			}

			var entregaCie = serveiEntity.getEntregaCie();
			if (servei.isEntregaCieActiva()) {
				if (entregaCie == null) {
					entregaCie = entregaCieRepository.save(new EntregaCieEntity(servei.getCieId(), servei.getOperadorPostalId()));
				} else {
					entregaCie.update(servei.getCieId(), servei.getOperadorPostalId());
				}
			}
			
			var grupsServei = grupServeiRepository.findByProcSer(serveiEntity);
			
			if (!servei.isAgrupar()) {
				grupServeiRepository.deleteAll(grupsServei);
			}
			
			//#271 Check canvi codi SIA, si es modifica s'han de modificar tots els enviaments pendents
			if (!servei.getCodi().equals(serveiEntity.getCodi())) {
				//Obtenir notificacions pendents.
				var notificacionsPendentsNotificar = notificacioRepository.findNotificacionsPendentsDeNotificarByProcedimentId(serveiEntity.getId());
				for (var notificacioEntity : notificacionsPendentsNotificar) {
					//modificar el codi SIA i activar per tal que scheduled ho torni a agafar
					notificacioEntity.updateCodiSia(servei.getCodi());
					notificacioEntity.resetIntentsNotificacio();
					notificacioRepository.save(notificacioEntity);
				}
			}
			
			// Organ gestor
			OrganGestorEntity organGestor = organGestorRepository.findByCodi(servei.getOrganGestor()); 
			if (organGestor == null) {
				throw new NotFoundException(servei.getOrganGestor(), OrganGestorEntity.class);
			}
			// Si canviam l'organ gestor, i aquest no s'utilitza en cap altre servei, l'eliminarem (1)
			OrganGestorEntity organGestorAntic = null;
			if (serveiEntity.getOrganGestor() != null && !serveiEntity.getOrganGestor().getCodi().equals(servei.getOrganGestor())) {
				organGestorAntic = serveiEntity.getOrganGestor();
			}
			// Si hi ha hagut qualque canvi a un d'aquests camps
			if ((servei.isComu() != serveiEntity.isComu()) || (servei.isAgrupar() != serveiEntity.isAgrupar()) ||
					(servei.isRequireDirectPermission() != serveiEntity.isRequireDirectPermission())) {

				cacheHelper.evictFindProcedimentServeisWithPermis();
				cacheHelper.evictFindProcedimentsOrganWithPermis();
			}
			serveiEntity.update(
						servei.getCodi(),
						servei.getNom(),
						entitat,
						servei.isEntregaCieActiva() ? entregaCie : null,
						servei.getRetard(),
						servei.getCaducitat(),
						servei.isAgrupar(),
						organGestor,
						servei.getTipusAssumpte(),
						servei.getTipusAssumpteNom(),
						servei.getCodiAssumpte(),
						servei.getCodiAssumpteNom(),
						servei.isComu(),
						servei.isRequireDirectPermission(),
						servei.isManual());
			serveiRepository.save(serveiEntity);

			if (!servei.isEntregaCieActiva() && entregaCie != null) {
				entregaCieRepository.delete(entregaCie);
			}
			// Si canviam l'organ gestor, i aquest no s'utilitza en cap altre servei, l'eliminarem (2)
			if (organGestorAntic != null) {
				List<ServeiEntity> serveisOrganGestorAntic = serveiRepository.findByOrganGestorId(organGestorAntic.getId());
				if (serveisOrganGestorAntic == null || serveisOrganGestorAntic.isEmpty()) {
					organGestorRepository.delete(organGestorAntic);
				}
			}
			notificacioTableViewRepository.updateProcediment(serveiEntity.isComu(), serveiEntity.getNom(), serveiEntity.isRequireDirectPermission(), serveiEntity.getCodi());
			enviamentTableRepository.updateProcediment(serveiEntity.isComu(), serveiEntity.isRequireDirectPermission(), serveiEntity.getCodi());
			return conversioTipusHelper.convertir(serveiEntity, ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

    @Override
	@Transactional
    public ProcSerDto updateActiu(Long id, boolean actiu) throws NotFoundException {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Actualitzant propietat actiu d'un procediment existent (id=" + id + ", activ=" + actiu + ")");
			ServeiEntity serveiEntity = serveiRepository.findById(id).orElseThrow();
			serveiEntity.updateActiu(actiu);
			cacheHelper.evictFindProcedimentServeisWithPermis();
			cacheHelper.evictFindProcedimentsOrganWithPermis();
			return conversioTipusHelper.convertir(serveiEntity, ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
    }

	@Override
	@Transactional
	public ProcSerDto updateManual(Long id, boolean manual) throws NotFoundException {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Actualitzant propietat manual d'un servei existent (id=" + id + ", manual=" + manual + ")");
			var serveiEntity = serveiRepository.findById(id).orElseThrow();
			serveiEntity.updateManual(manual);
			return conversioTipusHelper.convertir(serveiEntity, ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

    @Audita(entityType = TipusEntitat.SERVEI, operationType = TipusOperacio.DELETE, returnType = TipusObjecte.DTO)
	@Override
	@Transactional
	public ProcSerDto delete(Long entitatId, Long id, boolean isAdminEntitat) throws NotFoundException {
		
		var timer = metricsHelper.iniciMetrica();
		try {
			var auth = SecurityContextHolder.getContext().getAuthentication();
			var entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false);
			var serveiEntity = (ServeiEntity) entityComprovarHelper.comprovarProcediment(entitat, id);
			if (!isAdminEntitat && serveiEntity.isComu()) {
				throw new PermissionDeniedException(serveiEntity.getId(), ServeiEntity.class, auth.getName(), "ADMINISTRADORENTITAT");
			}
			//Eliminar grups del servei
			var grupsDelServei = grupServeiRepository.findByProcSer(serveiEntity);
			for (var grupServeiEntity : grupsDelServei) {
				grupServeiRepository.delete(grupServeiEntity);
			}
			procSerOrganRepository.deleteByProcSerId(id);
			//Eliminar servei
			serveiRepository.delete(serveiEntity);
			permisosHelper.revocarPermisosEntity(id, ProcedimentEntity.class);
			return conversioTipusHelper.convertir(serveiEntity, ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean serveiEnUs(Long serveiId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			//Compravacions en ús
				//1) Si té notificacions
			var notificacionsByServei = notificacioRepository.findByProcedimentId(serveiId);
			return notificacionsByServei != null && !notificacionsByServei.isEmpty();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean serveiAmbGrups(Long serveiId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			//Compravar si agrupar
			var servei = serveiRepository.findById(serveiId).orElseThrow();
			return servei.isAgrupar();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	public boolean isUpdatingServeis(EntitatDto entitatDto) {

		var progres = progresActualitzacioServeis.get(entitatDto.getDir3Codi());
		return progres != null && (progres.getProgres() > 0 && progres.getProgres() < 100) && !progres.isError();
	}

	@Override
	public boolean actualitzarServei(String codiSia, EntitatDto entitat) {

		try {
			var proc = pluginHelper.getProcSerByCodiSia(codiSia, true);
			if (proc == null) {
				var entity = entityComprovarHelper.comprovarEntitat(entitat.getId(), false, false, false);
				var servei = serveiRepository.findByCodiAndEntitat(codiSia, entity);
				if (servei != null) {
					servei.updateActiu(false);
					serveiRepository.save(servei);
				}
				return false;
			}
			var progres = new ProgresActualitzacioProcSer();
			List<OrganGestorEntity> organsModificats = new ArrayList<>();
			Map<String, String[]> avisosServeisOrgans = new HashMap<>();
			var unitatsWs = pluginHelper.unitatsOrganitzativesFindByPare(entitat.getCodi(), entitat.getDir3Codi(), null, null);
			List<String> codiOrgansGda = new ArrayList<>();
			for (var unitat: unitatsWs) {
				codiOrgansGda.add(unitat.getCodi());
			}
			var entity = entityComprovarHelper.comprovarEntitat(entitat.getId(), false, false, false);
			serveiHelper.actualitzarServeiFromGda(progres, proc, entity, codiOrgansGda, true, organsModificats, avisosServeisOrgans);
			if (avisosServeisOrgans.size() > 0) {
				if (serveisAmbOrganNoSincronitzat.containsKey(entitat)) {
					serveisAmbOrganNoSincronitzat.put(entitat.getId(), serveisAmbOrganNoSincronitzat.get(entitat.getId()) + avisosServeisOrgans.size());
				} else {
					serveisAmbOrganNoSincronitzat.put(entitat.getId(), avisosServeisOrgans.size());
				}
				procSerSyncHelper.addAvisosSyncServeis(avisosServeisOrgans, entitat.getId());
			}
			var eliminarOrgans = procSerSyncHelper.isActualitzacioServeisEliminarOrgansProperty();
			if (!eliminarOrgans) {
				return true;
			}
			for (var organGestorAntic: organsModificats) {
				//#260 Modificació passar la funcionalitat del for dins un procediment, ja que pel temps de transacció fallava
				serveiHelper.eliminarOrganSiNoEstaEnUs(progres,organGestorAntic);
			}
			return true;
		} catch (Exception ex) {
			log.error("Error actualitzant el procediment", ex);
			throw ex;
		}
	}

	@Override
	//@Transactional(timeout = 300)
	public void actualitzaServeis(EntitatDto entitatDto) {

		var timer = metricsHelper.iniciMetrica();
		try {
			procSerSyncHelper.actualitzaServeis(entitatDto);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public ProgresActualitzacioDto getProgresActualitzacio(String dir3Codi) {
		var timer = metricsHelper.iniciMetrica();
		try {
			ProgresActualitzacioDto progres = progresActualitzacioServeis.get(dir3Codi);
			if (progres != null && progres.isFinished()) {
				progresActualitzacioServeis.remove(dir3Codi);
			}
			return progres;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public ProcSerDto findById(
			Long entitatId,
			boolean isAdministrador,
			Long serveiId) {
		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta del servei ("
					+ "entitatId=" + entitatId + ", "
					+ "serveiId=" + serveiId + ")");
				
			if (entitatId != null && !isAdministrador)
				entityComprovarHelper.comprovarEntitat(
						entitatId, 
						false, 
						false, 
						false);
	
			ServeiEntity servei = (ServeiEntity) entityComprovarHelper.comprovarProcediment(
					entitatId, 
					serveiId);
			ProcSerDto resposta = conversioTipusHelper.convertir(
					servei,
					ProcSerDto.class);
			
			if (resposta != null) {
				serveiHelper.omplirPermisos(resposta, false);
			}
			
			return resposta;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional(readOnly = true)
	@Override
	public ProcSerDto findByCodi(
			Long entitatId,
			String codiServei) throws NotFoundException {
		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta del servei ("
					+ "entitatId=" + entitatId + ", "
					+ "codi=" + codiServei + ")");
			EntitatEntity entitat = null;
				
			if (entitatId != null)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId, 
						false, 
						false, 
						false);
			
			ServeiEntity servei = serveiRepository.findByCodiAndEntitat(codiServei, entitat);
			
			return conversioTipusHelper.convertir(
					servei, 
					ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public ProcSerDto findByNom(
			Long entitatId,
			String nomServei) throws NotFoundException {
		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta del servei ("
					+ "entitatId=" + entitatId + ", "
					+ "nom=" + nomServei + ")");
			EntitatEntity entitat = null;
				
			if (entitatId != null)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId, 
						false, 
						false, 
						false);
			
			List<ServeiEntity> serveis = serveiRepository.findByNomAndEntitat(nomServei, entitat);
			if (serveis != null && !serveis.isEmpty()) {
				return conversioTipusHelper.convertir(
						serveis.get(0),
						ProcSerDto.class);

			} else {
				return null;
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public Integer getServeisAmbOrganNoSincronitzat(Long entitatId) {
		Integer organsNoSincronitzats = serveisAmbOrganNoSincronitzat.get(entitatId);
		if (organsNoSincronitzats == null) {
			organsNoSincronitzats = serveiRepository.countByEntitatIdAndOrganNoSincronitzatTrue(entitatId);
			serveisAmbOrganNoSincronitzat.put(entitatId, organsNoSincronitzats);
		}
		return organsNoSincronitzats;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProcSerSimpleDto> findByEntitat(Long entitatId) {
		var timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			
			List<ServeiEntity> servei = serveiRepository.findByEntitat(entitat);
			
			return conversioTipusHelper.convertirList(
					servei,
					ProcSerSimpleDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProcSerSimpleDto> findByOrganGestorIDescendents(
			Long entitatId, 
			OrganGestorDto organGestor) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
		List<String> organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(
				entitat.getDir3Codi(), 
				organGestor.getCodi());
		return conversioTipusHelper.convertirList(
				serveiRepository.findByOrganGestorCodiIn(organsFills),
				ProcSerSimpleDto.class);
	}

	@Override
	public List<ProcSerDto> findByOrganGestorIDescendentsAndComu(Long entitatId, OrganGestorDto organGestor) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
		List<String> organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(
				entitat.getDir3Codi(),
				organGestor.getCodi());
		return conversioTipusHelper.convertirList(
				serveiRepository.findByOrganGestorCodiInOrComu(organsFills, entitat),
				ProcSerDto.class);
	}

	@Override
	@Transactional
	public PaginaDto<ProcSerFormDto> findAmbFiltrePaginat(Long entitatId, boolean isUsuari, boolean isUsuariEntitat, boolean isAdministrador,
														  OrganGestorDto organGestorActual, ProcSerFiltreDto filtre, PaginacioParamsDto paginacioParams) {

		var timer = metricsHelper.iniciMetrica();
		try {
			entityComprovarHelper.comprovarEntitat(entitatId, false, false, false);
			var entitatActual = entityComprovarHelper.comprovarEntitat(entitatId);
			var entitatsActiva = entitatRepository.findByActiva(true);
			List<Long> entitatsActivaId = new ArrayList<>();
			
			for (var entitatActiva : entitatsActiva) {
				entitatsActivaId.add(entitatActiva.getId());
			}
			Page<ServeiFormEntity> serveis = null;
			PaginaDto<ProcSerFormDto> serveisPage = null;
			Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<>();
			mapeigPropietatsOrdenacio.put("organGestorDesc", new String[] {"organGestor"});
			// Evitar problema quan s'ordena per actiu
			if (paginacioParams.getOrdres().size() == 1 && "actiu".equals(paginacioParams.getOrdres().get(0).getCamp())) {
				paginacioParams.getOrdres().add(new PaginacioParamsDto.OrdreDto("nom", PaginacioParamsDto.OrdreDireccioDto.ASCENDENT));
			}
			var pageable = paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio);
			List<String> organsFills = new ArrayList<>();
			if (organGestorActual != null) { // Administrador d'òrgan
				organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitatActual.getDir3Codi(), organGestorActual.getCodi());
			}

			if (filtre == null) {
				if (isUsuariEntitat) {
					serveis = serveiFormRepository.findAmbEntitatActual(entitatActual.getId(), pageable);
				} else if (isAdministrador) {
					serveis = serveiFormRepository.findAmbEntitatActiva(entitatsActivaId, pageable);
				} else if (organGestorActual != null) { // Administrador d'òrgan
					serveis = serveiFormRepository.findAmbOrganGestorActualOrComu(entitatActual.getId(), organsFills, pageable);
				}
			} else {
				if (isUsuariEntitat) {
					serveis = serveiFormRepository.findAmbEntitatAndFiltre(
							entitatActual.getId(),
							filtre.getCodi() == null || filtre.getCodi().isEmpty(), 
							filtre.getCodi() == null ? "" : filtre.getCodi(),
							filtre.getNom() == null || filtre.getNom().isEmpty(),
							filtre.getNom() == null ? "" : filtre.getNom(),
							filtre.getOrganGestor() == null || filtre.getOrganGestor().isEmpty(),
							filtre.getOrganGestor() == null ? "" : filtre.getOrganGestor(),
							filtre.getEstat() == null,
							filtre.getEstat() == null ? null : ProcedimentEstat.ACTIU.equals(filtre.getEstat()),
							filtre.isComu(),
							filtre.isEntregaCieActiva(),
							filtre.isManual(),
							pageable);

				} else if (isAdministrador) {
					serveis = serveiFormRepository.findAmbFiltre(
							filtre.getCodi() == null || filtre.getCodi().isEmpty(), 
							filtre.getCodi() == null ? "" : filtre.getCodi(),
							filtre.getNom() == null || filtre.getNom().isEmpty(),
							filtre.getNom() == null ? "" : filtre.getNom(),
							filtre.getOrganGestor() == null || filtre.getOrganGestor().isEmpty(),
							filtre.getOrganGestor() == null ? "" : filtre.getOrganGestor(),
							filtre.getEstat() == null,
							filtre.getEstat() == null ? null : ProcedimentEstat.ACTIU.equals(filtre.getEstat()),
							filtre.isComu(),
							filtre.isEntregaCieActiva(),
							filtre.isManual(),
							pageable);

				} else if (organGestorActual != null) { // Administrador d'òrgan
					serveis = serveiFormRepository.findAmbOrganGestorOrComuAndFiltre(
							entitatActual.getId(),
							filtre.getCodi() == null || filtre.getCodi().isEmpty(), 
							filtre.getCodi() == null ? "" : filtre.getCodi(),
							filtre.getNom() == null || filtre.getNom().isEmpty(),
							filtre.getNom() == null ? "" : filtre.getNom(),
							filtre.getOrganGestor() == null || filtre.getOrganGestor().isEmpty(),
							filtre.getOrganGestor() == null ? "" : filtre.getOrganGestor(),
							organsFills,
							filtre.getEstat() == null,
							filtre.getEstat() == null ? null : ProcedimentEstat.ACTIU.equals(filtre.getEstat()),
							filtre.isComu(),
							filtre.isEntregaCieActiva(),
							filtre.isManual(),
							pageable);

				}
			}
			serveisPage = paginacioHelper.toPaginaDto(serveis, ProcSerFormDto.class);
			assert serveisPage != null;
			for (var servei: serveisPage.getContingut()) {
				var permisos = permisosHelper.findPermisos(servei.getId(), ProcedimentEntity.class);
				if (servei.isComu()) {
					String organActual = null;
					if (organGestorActual != null) {
						organActual = organGestorActual.getCodi();
					}
					permisos.addAll(findPermisServeiOrganByServei(servei.getId(), organActual));
				}
				List<GrupDto> grups = grupService.findGrupsByProcSer(servei.getId());
				servei.setGrups(grups);
				servei.setPermisos(permisos);
			}
			return serveisPage;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProcSerDto> findAll() {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de tots els serveis");
			entityComprovarHelper.comprovarPermisos(null, true, true, false);
			return conversioTipusHelper.convertirList(serveiRepository.findAll(), ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProcSerGrupDto> findAllGrups() {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de tots els serveis");
			entityComprovarHelper.comprovarPermisos(null, true, true, false);
			List<GrupProcSerEntity> grupsServeis = grupServeiRepository.findAll();
			return conversioTipusHelper.convertirList(grupsServeis, ProcSerGrupDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProcSerGrupDto> findGrupsByEntitat(Long entitatId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de tots els serveis d'una entitat");
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, true, false, false);
			List<GrupProcSerEntity> grupsServeis = grupServeiRepository.findByProcSerEntitat(entitat);
			return conversioTipusHelper.convertirList(grupsServeis, ProcSerGrupDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProcSerDto> findServeis(Long entitatId, List<String> grups) {
		var timer = metricsHelper.iniciMetrica();
		try {	
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					true,
					false,
					false);
			return conversioTipusHelper.convertirList(
					serveiRepository.findServeisByEntitatAndGrup(entitat, grups),
					ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProcSerDto> findServeisAmbGrups(Long entitatId, List<String> grups) {
		var timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					true,
					false,
					false);
			return conversioTipusHelper.convertirList(
					serveiRepository.findServeisAmbGrupsByEntitatAndGrup(entitat, grups),
					ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProcSerDto> findServeisSenseGrups(Long entitatId) {
		var timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					true,
					false,
					false);
			return conversioTipusHelper.convertirList(
					serveiRepository.findServeisSenseGrupsByEntitat(entitat),
					ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProcSerDto> findServeisByOrganGestor(String organGestorCodi) {
		var timer = metricsHelper.iniciMetrica();
		try {
			OrganGestorEntity organGestor = organGestorRepository.findByCodi(organGestorCodi);
			if (organGestor == null) {
				throw new NotFoundException(
						organGestorCodi,
						OrganGestorEntity.class);
			}
			return conversioTipusHelper.convertirList(
					serveiRepository.findByOrganGestorId(organGestor.getId()),
					ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CodiValorOrganGestorComuDto> getServeisOrgan(
			Long entitatId,
			String organCodi,
			Long organFiltre,
			RolEnumDto rol,
			PermisEnum permis) {

		var timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			List<CodiValorOrganGestorComuDto> serveis = new ArrayList<>();
			String organFiltreCodi = null;

			if (organFiltre != null) {
				OrganGestorEntity organGestorEntity = organGestorRepository.findById(organFiltre).orElse(null);
				if (organGestorEntity != null)
					organFiltreCodi = organGestorEntity.getCodi();
			}

			if (RolEnumDto.tothom.equals(rol)) {
				serveis = recuperarServeiAmbPermis(entitat, permis, organFiltreCodi);
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				if (auth != null) {
					List<String> grups = cacheHelper.findRolsUsuariAmbCodi(auth.getName());
					serveis.addAll(permisosService.getProcSerComuns(entitat.getId(), grups, true, ProcSerTipusEnum.SERVEI));
				}
			} else {
				List<ServeiEntity> serveisEntitat = new ArrayList<>();
				if (organFiltreCodi != null) {
					List<ServeiEntity> serveisDisponibles = serveiRepository.findByEntitat(entitat);
					if (serveisDisponibles != null) {
						for (ServeiEntity servei : serveisDisponibles) {
							if (servei.isComu() || (servei.getOrganGestor() != null && organFiltreCodi.equalsIgnoreCase(servei.getOrganGestor().getCodi()))) {
								serveisEntitat.add(servei);
							}
						}
					}
				} else {
					if (RolEnumDto.NOT_SUPER.equals(rol)) {
						serveisEntitat = serveiRepository.findAll();
					} else if (RolEnumDto.NOT_ADMIN.equals(rol)) {
						serveisEntitat = serveiRepository.findByEntitat(entitat);
					} else if (RolEnumDto.NOT_ADMIN_ORGAN.equals(rol)) {
						if (organCodi != null) {
							List<String> organsFills = organGestorCachable.getCodisOrgansGestorsFillsByOrgan(entitat.getDir3Codi(), organCodi);
							serveisEntitat = serveiRepository.findByOrganGestorCodiInOrComu(organsFills, entitat);
						}
					}
				}
				for (ServeiEntity servei: serveisEntitat) {
					serveis.add(CodiValorOrganGestorComuDto.builder()
							.id(servei.getId())
							.codi(servei.getCodi())
							.valor(servei.getCodi() + ((servei.getNom() != null && !servei.getNom().isEmpty()) ? " - " + servei.getNom() : ""))
							.organGestor(servei.getOrganGestor() != null ? servei.getOrganGestor().getCodi() : "")
							.comu(servei.isComu())
							.build());
				}
			}

			serveis = new ArrayList<>(new HashSet<>(serveis));
			serveis.sort(Comparator.comparing(CodiValorOrganGestorComuDto::getValor));
			return serveis;

		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public List<CodiValorOrganGestorComuDto> getServeisOrganNotificables(Long entitatId, String organCodi, RolEnumDto rol, EnviamentTipus enviamentTipus) {

		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
		List<ServeiEntity> serveis;
		if (!RolEnumDto.NOT_ADMIN.equals(rol)) {
			PermisEnum permis = EnviamentTipus.SIR.equals(enviamentTipus) ? PermisEnum.COMUNICACIO_SIR :
					EnviamentTipus.COMUNICACIO.equals(enviamentTipus) ? PermisEnum.COMUNICACIO : PermisEnum.NOTIFICACIO;
			return recuperarServeiAmbPermis(entitat, permis, organCodi);
		}
		serveis = recuperarServeiSensePermis(entitat, organCodi);
		// Eliminam els procediments inactius
		Iterator<ServeiEntity> it = serveis.iterator();
		while (it.hasNext()) {
			ServeiEntity curr = it.next();
			if (!curr.isActiu()) {
				it.remove();
			}
		}
		return serveisToCodiValorOrganGestorComuDto(serveis);
	}

	private List<CodiValorOrganGestorComuDto> serveisToCodiValorOrganGestorComuDto(List<ServeiEntity> serveis) {
		List<CodiValorOrganGestorComuDto> response = new ArrayList<>();
		for (ServeiEntity servei : serveis) {
			String nom = servei.getCodi();
			if (servei.getNom() != null && !servei.getNom().isEmpty()) {
				nom += " - " + servei.getNom();
			}
			String organCodi = servei.getOrganGestor() != null ? servei.getOrganGestor().getCodi() : "";
			response.add(CodiValorOrganGestorComuDto.builder()
					.id(servei.getId())
					.codi(servei.getCodi())
					.valor(nom)
					.organGestor(organCodi)
					.comu(servei.isComu())
					.build());
		}
		return response;
	}

	private List<ServeiEntity> recuperarServeiSensePermis(EntitatEntity entitat, String organCodi){

		if (organCodi == null) {
			return serveiRepository.findByEntitat(entitat);
		}
		var organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitat.getDir3Codi(), organCodi);
		return serveiRepository.findByOrganGestorCodiInOrComu(organsFills, entitat);

	}

	private List<CodiValorOrganGestorComuDto> recuperarServeiAmbPermis(EntitatEntity entitat, PermisEnum permis, String organFiltre) {

		List<CodiValorOrganGestorComuDto> serveisAmbPermis = new ArrayList<>();
		var auth = SecurityContextHolder.getContext().getAuthentication();
		var serveis = permisosService.getServeisAmbPermis(entitat.getId(), auth.getName(), permis);
		if (serveis == null || serveis.isEmpty()) {
			return serveisAmbPermis;
		}
		if (organFiltre == null) {
			serveisAmbPermis.addAll(serveis);
			return serveisAmbPermis;
		}
		var organsFills = organGestorCachable.getCodisOrgansGestorsFillsByOrgan(entitat.getDir3Codi(), organFiltre);
		for (var servei: serveis) {
			if (organsFills.contains(servei.getOrganGestor())) {
				serveisAmbPermis.add(servei);
			}
		}
		return serveisAmbPermis;
	}


	@Override
	@Transactional(readOnly = true)
	public boolean hasAnyServeisWithPermis(Long entitatId, List<String> grups, PermisEnum permis) {
		var timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					true,
					false,
					false);
			List<ServeiEntity> serveis = serveiRepository.findServeisByEntitatAndGrup(entitat, grups);
			if (serveis == null || serveis.isEmpty())
				return false;
			
			permisosHelper.filterGrantedAny(
					serveis,
					(ObjectIdentifierExtractor<ServeiEntity>) servei -> servei.getId(),
					ProcedimentEntity.class,
					entityComprovarHelper.getPermissionsFromName(permis),
					SecurityContextHolder.getContext().getAuthentication());
			return !serveis.isEmpty();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	private List<PermisDto> findPermisServeiOrganByServei(Long serveiId, String organGestor) {

		List<ProcSerOrganEntity> serveiOrgans = serveiOrganRepository.findByProcSerId(serveiId);
		List<String> organsAmbPermis = new ArrayList<>();
		if (serveiOrgans == null || serveiOrgans.isEmpty()) {
			return new ArrayList<>();
		}
		List<PermisDto> permisos = new ArrayList<>();
		if (organGestor != null) {
			organsAmbPermis = organigramaHelper.getCodisOrgansGestorsFillsByOrgan(serveiOrgans.get(0).getProcSer().getEntitat().getDir3Codi(), organGestor);
		}
		for (var serveiOrgan: serveiOrgans) {
			List<PermisDto> permisosProcOrgan = permisosHelper.findPermisos(serveiOrgan.getId(), ProcSerOrganEntity.class);
			if (permisosProcOrgan == null || permisosProcOrgan.isEmpty()) {
				continue;
			}
			String organ = serveiOrgan.getOrganGestor().getCodi();
			String organNom = serveiOrgan.getOrganGestor().getNom();
			boolean tePermis = true;

			if (organGestor != null) {
				tePermis = organsAmbPermis.contains(organ);
			}
			if (!tePermis) {
				continue;
			}
			for (var permis : permisosProcOrgan) {
				permis.setOrgan(organ);
				permis.setOrganNom(organNom);
				permis.setPermetEdicio(tePermis);
			}
			permisos.addAll(permisosProcOrgan);
		}
		return permisos;
	}

}
