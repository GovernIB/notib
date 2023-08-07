package es.caib.notib.core.service;

import com.codahale.metrics.Timer;
import es.caib.notib.core.api.dto.CodiValorOrganGestorComuDto;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.PermisEnum;
import es.caib.notib.core.api.dto.ProcSerTipusEnum;
import es.caib.notib.core.api.dto.ProgresActualitzacioDto;
import es.caib.notib.core.api.dto.RolEnumDto;
import es.caib.notib.core.api.dto.notificacio.TipusEnviamentEnumDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.procediment.ProcSerDataDto;
import es.caib.notib.core.api.dto.procediment.ProcSerDto;
import es.caib.notib.core.api.dto.procediment.ProcSerFiltreDto;
import es.caib.notib.core.api.dto.procediment.ProcSerFormDto;
import es.caib.notib.core.api.dto.procediment.ProcSerGrupDto;
import es.caib.notib.core.api.dto.procediment.ProcSerSimpleDto;
import es.caib.notib.core.api.dto.procediment.ProcedimentEstat;
import es.caib.notib.core.api.dto.procediment.ProgresActualitzacioProcSer;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.exception.PermissionDeniedException;
import es.caib.notib.core.api.service.AuditService.TipusEntitat;
import es.caib.notib.core.api.service.AuditService.TipusObjecte;
import es.caib.notib.core.api.service.AuditService.TipusOperacio;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.api.service.PermisosService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.core.api.service.ServeiService;
import es.caib.notib.core.aspect.Audita;
import es.caib.notib.core.cacheable.OrganGestorCachable;
import es.caib.notib.core.cacheable.PermisosCacheable;
import es.caib.notib.core.cacheable.ProcSerCacheable;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.GrupProcSerEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.ProcSerOrganEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.entity.ServeiEntity;
import es.caib.notib.core.entity.ServeiFormEntity;
import es.caib.notib.core.entity.cie.EntregaCieEntity;
import es.caib.notib.core.entity.cie.PagadorCieEntity;
import es.caib.notib.core.entity.cie.PagadorPostalEntity;
import es.caib.notib.core.helper.CacheHelper;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.helper.OrganigramaHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.helper.ProcSerHelper;
import es.caib.notib.core.helper.ProcSerSyncHelper;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.EntregaCieRepository;
import es.caib.notib.core.repository.EnviamentTableRepository;
import es.caib.notib.core.repository.GrupProcSerRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.repository.NotificacioTableViewRepository;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.core.repository.ProcSerOrganRepository;
import es.caib.notib.core.repository.ServeiFormRepository;
import es.caib.notib.core.repository.ServeiRepository;
import es.caib.notib.plugin.unitat.NodeDir3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
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
@Service
public class
ServeiServiceImpl implements ServeiService{

	@Autowired
	private PermisosService permisosService;
	@Resource
	private ServeiRepository serveiRepository;
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
	private ProcSerCacheable serveisCacheable;
	@Autowired
	private ProcSerSyncHelper procSerSyncHelper;
	@Autowired
	private EntregaCieRepository entregaCieRepository;
	@Autowired
	private EnviamentTableRepository enviamentTableRepository;
	@Autowired
	private NotificacioTableViewRepository notificacioTableViewRepository;
	@Autowired
	private PermisosCacheable permisosCacheable;

	@Autowired
	private ProcedimentService procedimentService;

	public static final String SERVEI_ORGAN_NO_SYNC = "Hi ha serveis que pertanyen a òrgans no existents en l'organigrama actual";
	public static Map<String, ProgresActualitzacioProcSer> progresActualitzacioServeis = new HashMap<>();
	public static Map<Long, Integer> serveisAmbOrganNoSincronitzat = new HashMap<>();
	
	@Audita(entityType = TipusEntitat.SERVEI, operationType = TipusOperacio.CREATE, returnType = TipusObjecte.DTO)
	@Override
	@Transactional
	public ProcSerDto create(
			Long entitatId,
			ProcSerDataDto servei) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Creant un nou servei ("
					+ "servei=" + servei + ")");
			
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);

			// Organ gestor
			OrganGestorEntity organGestor = organGestorRepository.findByCodi(servei.getOrganGestor()); 
			if (organGestor == null) {
//				organGestor = organGestorHelper.crearOrganGestor(entitat, servei.getOrganGestor());
				throw new NotFoundException(servei.getOrganGestor(), OrganGestorEntity.class);
			}
			
			ServeiEntity.ServeiEntityBuilder serveiEntityBuilder =
					ServeiEntity.getBuilder(
							servei.getCodi(),
							servei.getNom(),
							servei.getRetard(),
							servei.getCaducitat(),
							entitat,
							servei.isAgrupar(),
							organGestor,
							servei.getTipusAssumpte(),
							servei.getTipusAssumpteNom(),
							servei.getCodiAssumpte(),
							servei.getCodiAssumpteNom(),
							servei.isComu(),
							servei.isRequireDirectPermission(),
							servei.isManual());

			if (servei.isEntregaCieActiva()) {
				EntregaCieEntity entregaCie = new EntregaCieEntity(servei.getCieId(), servei.getOperadorPostalId());
				serveiEntityBuilder.entregaCie(entregaCieRepository.save(entregaCie));
			}
			cacheHelper.evictFindProcedimentServeisWithPermis();
			return conversioTipusHelper.convertir(
					serveiRepository.save(serveiEntityBuilder.build()),
					ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Audita(entityType = TipusEntitat.SERVEI, operationType = TipusOperacio.UPDATE, returnType = TipusObjecte.DTO)
	@Override
	@Transactional
	public ProcSerDto update(
			Long entitatId,
			ProcSerDataDto servei,
			boolean isAdmin,
			boolean isAdminEntitat) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Actualitzant servei ("
					+ "servei=" + servei + ")");
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (!isAdminEntitat && servei.isComu()) {
				throw new PermissionDeniedException(
						servei.getId(),
						ServeiEntity.class,
						auth.getName(),
						"ADMINISTRADORENTITAT");
			}
			
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					false,
					false,
					false);
			ServeiEntity serveiEntity = null;
			PagadorPostalEntity pagadorPostal = null;
			PagadorCieEntity pagadorCie = null;
			if(!isAdmin) {
				serveiEntity = (ServeiEntity) entityComprovarHelper.comprovarProcediment(
						entitat,
						servei.getId());
			} else {
				serveiEntity = serveiRepository.findOne(servei.getId());
			}

			EntregaCieEntity entregaCie = serveiEntity.getEntregaCie();
			if (servei.isEntregaCieActiva()) {
				if (entregaCie == null) {
					entregaCie = entregaCieRepository.save(
							new EntregaCieEntity(servei.getCieId(), servei.getOperadorPostalId())
					);
				} else {
					entregaCie.update(servei.getCieId(), servei.getOperadorPostalId());
				}
			}
			
			List<GrupProcSerEntity> grupsServei = grupServeiRepository.findByProcSer(serveiEntity);
			
			if (!servei.isAgrupar()) {
				grupServeiRepository.delete(grupsServei);
			}
			
			//#271 Check canvi codi SIA, si es modifica s'han de modificar tots els enviaments pendents
			if (!servei.getCodi().equals(serveiEntity.getCodi())) {
				//Obtenir notificacions pendents.
				List<NotificacioEntity> notificacionsPendentsNotificar = notificacioRepository.findNotificacionsPendentsDeNotificarByProcedimentId(serveiEntity.getId());
				for (NotificacioEntity notificacioEntity : notificacionsPendentsNotificar) {
					//modificar el codi SIA i activar per tal que scheduled ho torni a agafar
					notificacioEntity.updateCodiSia(servei.getCodi());
					notificacioEntity.resetIntentsNotificacio();
					notificacioRepository.save(notificacioEntity);
				}
			}
			
			// Organ gestor
			OrganGestorEntity organGestor = organGestorRepository.findByCodi(servei.getOrganGestor()); 
			if (organGestor == null) {
//				organGestor = organGestorHelper.crearOrganGestor(entitat, servei.getOrganGestor());
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

			notificacioTableViewRepository.updateProcediment(serveiEntity.isComu(),
					serveiEntity.getNom(),
					serveiEntity.isRequireDirectPermission(),
					serveiEntity.getCodi());
			enviamentTableRepository.updateProcediment(serveiEntity.isComu(),
					serveiEntity.isRequireDirectPermission(),
					serveiEntity.getCodi());

			return conversioTipusHelper.convertir(
					serveiEntity, 
					ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

    @Override
	@Transactional
    public ProcSerDto updateActiu(Long id, boolean actiu) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Actualitzant propietat actiu d'un procediment existent (id=" + id + ", activ=" + actiu + ")");
			ServeiEntity serveiEntity = serveiRepository.findOne(id);
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
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Actualitzant propietat manual d'un servei existent (id=" + id + ", manual=" + manual + ")");
			ServeiEntity serveiEntity = serveiRepository.findOne(id);
			serveiEntity.updateManual(manual);
			return conversioTipusHelper.convertir(serveiEntity, ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Audita(entityType = TipusEntitat.SERVEI, operationType = TipusOperacio.DELETE, returnType = TipusObjecte.DTO)
	@Override
	@Transactional
	public ProcSerDto delete(
			Long entitatId,
			Long id,
			boolean isAdminEntitat) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					false,
					false,
					false);
			
			ServeiEntity serveiEntity = (ServeiEntity) entityComprovarHelper.comprovarProcediment(
					entitat, 
					id);
			if (!isAdminEntitat && serveiEntity.isComu()) {
				throw new PermissionDeniedException(
						serveiEntity.getId(),
						ServeiEntity.class,
						auth.getName(),
						"ADMINISTRADORENTITAT");
			}
			//Eliminar grups del servei
			List<GrupProcSerEntity> grupsDelServei = grupServeiRepository.findByProcSer(serveiEntity);
			for (GrupProcSerEntity grupServeiEntity : grupsDelServei) {
				grupServeiRepository.delete(grupServeiEntity);
			}
			//Eliminar servei
			serveiRepository.delete(serveiEntity);
			permisosHelper.revocarPermisosEntity(id, ProcedimentEntity.class);
			
			//TODO: Decidir si mantenir l'Organ Gestor encara que no hi hagi serveis o no
			//		Recordar que ara l'Organ té més coses assignades: Administrador, grups, pagadors ...
			//		Es pot mirar si esta en ús amb la funció organGestorService.organGestorEnUs(organId);
//			OrganGestorEntity organGestor = serveiEntity.getOrganGestor();
//			if (organGestor != null) {
//				List<ServeiEntity> serveisOrganGestorAntic = serveiRepository.findByOrganGestorId(organGestor.getId());
//				if (serveisOrganGestorAntic == null || serveisOrganGestorAntic.isEmpty()) {
//					organGestorRepository.delete(organGestor);
//				}
//			}
			
			return conversioTipusHelper.convertir(
					serveiEntity, 
					ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean serveiEnUs(Long serveiId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			//Compravacions en ús
			boolean serveiEnUs=false;
				//1) Si té notificacions
				List<NotificacioEntity> notificacionsByServei = notificacioRepository.findByProcedimentId(serveiId);
				serveiEnUs=notificacionsByServei != null && !notificacionsByServei.isEmpty();
			
			return serveiEnUs;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean serveiAmbGrups(Long serveiId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			//Compravar si agrupar
			ServeiEntity servei = serveiRepository.findById(serveiId);
			return servei.isAgrupar();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	public boolean isUpdatingServeis(EntitatDto entitatDto) {
		ProgresActualitzacioDto progres = progresActualitzacioServeis.get(entitatDto.getDir3Codi());
		return progres != null && (progres.getProgres() > 0 && progres.getProgres() < 100) && !progres.isError();
	}

	@Override
	public boolean actualitzarServei(String codiSia, EntitatDto entitat) {

		try {
			ProcSerDto proc = pluginHelper.getProcSerByCodiSia(codiSia, true);
			if (proc == null) {
				EntitatEntity entity = entityComprovarHelper.comprovarEntitat(entitat.getId(), false, false, false);
				ServeiEntity servei = serveiRepository.findByCodiAndEntitat(codiSia, entity);
				if (servei != null) {
					servei.updateActiu(false);
					serveiRepository.save(servei);
				}
				return false;
			}
			ProgresActualitzacioProcSer progres = new ProgresActualitzacioProcSer();
			List<OrganGestorEntity> organsModificats = new ArrayList<>();
			Map<String, String[]> avisosServeisOrgans = new HashMap<>();
//			Map<String, OrganismeDto> organigrama = organGestorCachable.findOrganigramaByEntitat(entitat.getDir3Codi());
			List<NodeDir3> unitatsWs = pluginHelper.unitatsOrganitzativesFindByPare(entitat.getCodi(), entitat.getDir3Codi(), null, null);
			List<String> codiOrgansGda = new ArrayList<>();
			for (NodeDir3 unitat: unitatsWs) {
				codiOrgansGda.add(unitat.getCodi());
			}
			EntitatEntity entity = entityComprovarHelper.comprovarEntitat(entitat.getId(), false, false, false);
			serveiHelper.actualitzarServeiFromGda(progres, proc, entity, codiOrgansGda, true, organsModificats, avisosServeisOrgans);

			if (avisosServeisOrgans.size() > 0) {
				if (serveisAmbOrganNoSincronitzat.containsKey(entitat)) {
					serveisAmbOrganNoSincronitzat.put(entitat.getId(), serveisAmbOrganNoSincronitzat.get(entitat.getId()) + avisosServeisOrgans.size());
				} else {
					serveisAmbOrganNoSincronitzat.put(entitat.getId(), avisosServeisOrgans.size());
				}
				procSerSyncHelper.addAvisosSyncServeis(avisosServeisOrgans, entitat.getId());
			}

			boolean eliminarOrgans = procSerSyncHelper.isActualitzacioServeisEliminarOrgansProperty();
			if (!eliminarOrgans) {
				return true;
			}
			for (OrganGestorEntity organGestorAntic: organsModificats) {
				//#260 Modificació passar la funcionalitat del for dins un procediment, ja que pel temps de transacció fallava
				serveiHelper.eliminarOrganSiNoEstaEnUs(progres,organGestorAntic);
			}
			return true;
		} catch (Exception ex) {
			logger.error("Error actualitzant el procediment", ex);
			throw ex;
		}
	}

	@Override
	//@Transactional(timeout = 300)
	public void actualitzaServeis(EntitatDto entitatDto) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			procSerSyncHelper.actualitzaServeis(entitatDto);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public ProgresActualitzacioDto getProgresActualitzacio(String dir3Codi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
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
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta del servei ("
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
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta del servei ("
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
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta del servei ("
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
		Timer.Context timer = metricsHelper.iniciMetrica();
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
	public PaginaDto<ProcSerFormDto> findAmbFiltrePaginat(
			Long entitatId,
			boolean isUsuari,
			boolean isUsuariEntitat,
			boolean isAdministrador,
			OrganGestorDto organGestorActual,
			ProcSerFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			entityComprovarHelper.comprovarEntitat(
					entitatId,
					false,
					false,
					false);
			
			EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(entitatId);
			List<EntitatEntity> entitatsActiva = entitatRepository.findByActiva(true);
			List<Long> entitatsActivaId = new ArrayList<>();
			
			for (EntitatEntity entitatActiva : entitatsActiva) {
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
			Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio);
			
			List<String> organsFills = new ArrayList<>();
			if (organGestorActual != null) { // Administrador d'òrgan
				organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(
						entitatActual.getDir3Codi(), 
						organGestorActual.getCodi());
			}

			if (filtre == null) {
				if (isUsuariEntitat) {
					serveis = serveiFormRepository.findAmbEntitatActual(
							entitatActual.getId(),
							pageable);
				} else if (isAdministrador) {
					serveis = serveiFormRepository.findAmbEntitatActiva(entitatsActivaId, pageable);
				} else if (organGestorActual != null) { // Administrador d'òrgan
					serveis = serveiFormRepository.findAmbOrganGestorActualOrComu(
							entitatActual.getId(),
							organsFills,
							pageable);
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
			for (ProcSerFormDto servei: serveisPage.getContingut()) {
				List<PermisDto> permisos = permisosHelper.findPermisos(
						servei.getId(),
						ProcedimentEntity.class);

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
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de tots els serveis");
			
			entityComprovarHelper.comprovarPermisos(
					null,
					true,
					true,
					false);
			return conversioTipusHelper.convertirList(
						serveiRepository.findAll(),
						ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProcSerGrupDto> findAllGrups() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de tots els serveis");
			
			entityComprovarHelper.comprovarPermisos(
					null,
					true,
					true,
					false);
			
			List<GrupProcSerEntity> grupsServeis = grupServeiRepository.findAll();
			return conversioTipusHelper.convertirList(
						grupsServeis,
						ProcSerGrupDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProcSerGrupDto> findGrupsByEntitat(Long entitatId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de tots els serveis d'una entitat");
			
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					true,
					false,
					false);
			
			List<GrupProcSerEntity> grupsServeis = grupServeiRepository.findByProcSerEntitat(entitat);
			return conversioTipusHelper.convertirList(
						grupsServeis,
						ProcSerGrupDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProcSerDto> findServeis(Long entitatId, List<String> grups) {
		Timer.Context timer = metricsHelper.iniciMetrica();
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
		Timer.Context timer = metricsHelper.iniciMetrica();
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
		Timer.Context timer = metricsHelper.iniciMetrica();
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
	
//	@Override
//	@Transactional(readOnly = true)
////	@Cacheable(value = "serveisPermis", key="#entitatId.toString().concat('-').concat(#usuariCodi).concat('-').concat(#permis.name())")
//	public List<ProcSerSimpleDto> findServeisWithPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
//		Timer.Context timer = metricsHelper.iniciMetrica();
//		try {
//			List<ProcSerSimpleDto> procedimentsAmbPermis = new ArrayList<>();
//			for (ProcSerSimpleDto procSer : procedimentService.findProcedimentServeisWithPermis(entitatId, usuariCodi, permis)) {
//				if (ProcSerTipusEnum.SERVEI.equals(procSer.getTipus())) {
//					procedimentsAmbPermis.add(procSer);
//				}
//			}
//			return procedimentsAmbPermis;
//		} finally {
//			metricsHelper.fiMetrica(timer);
//		}
//	}

	@Override
	@Transactional(readOnly = true)
	public List<ProcSerDto> findServeisByOrganGestor(String organGestorCodi) {
		Timer.Context timer = metricsHelper.iniciMetrica();
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

//	@Override
//	@Transactional(readOnly = true)
//	public List<ProcSerDto> findServeisByOrganGestorWithPermis(
//			Long entitatId,
//			String organGestorCodi,
//			List<String> grups,
//			PermisEnum permis) {
//		Timer.Context timer = metricsHelper.iniciMetrica();
//		try {
//			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
//					entitatId,
//					true,
//					false,
//					false);
//			Permission[] permisos = entityComprovarHelper.getPermissionsFromName(permis);
//			OrganGestorEntity organGestor = entityComprovarHelper.comprovarOrganGestor(entitat, organGestorCodi);
//			// 1. Obtenim tots els serveis de l'òrgan gestor
//			List<ServeiEntity> serveis = serveiRepository.findServeisByOrganGestorAndGrup(entitat, organGestor.getId(), grups);
//
//			// 2. Si tenim permis a sobre de l'òrgan o un dels pares, llavors tenim permís a sobre tots els serveis de l'òrgan
//			List<OrganGestorEntity> organsGestors = organigramaHelper.getOrgansGestorsParesExistentsByOrgan(entitat.getDir3Codi(), organGestorCodi);
//			permisosHelper.filterGrantedAny(
//					organsGestors,
//					new ObjectIdentifierExtractor<OrganGestorEntity>() {
//						public Long getObjectIdentifier(OrganGestorEntity organGestor) {
//							return organGestor.getId();
//						}
//					},
//					OrganGestorEntity.class,
//					permisos,
//					auth);
//			if (organsGestors.isEmpty()) {
//				// 3. Si no tenim permis sobre òrgan, llavors miram els permisos sobre el servei
//				permisosHelper.filterGrantedAny(
//						serveis,
//						new ObjectIdentifierExtractor<ServeiEntity>() {
//							public Long getObjectIdentifier(ServeiEntity servei) {
//								return servei.getId();
//							}
//						},
//						ProcedimentEntity.class,
//						permisos,
//						auth);
//			}
//
//			// 4. Serveis comuns
//			List<ServeiEntity> serveisComuns = serveiRepository.findByComuTrue();
//			permisosHelper.filterGrantedAny(
//					serveisComuns,
//					new ObjectIdentifierExtractor<ServeiEntity>() {
//						public Long getObjectIdentifier(ServeiEntity servei) {
//							return servei.getId();
//						}
//					},
//					ProcedimentEntity.class,
//					permisos,
//					auth);
//			serveisComuns.removeAll(serveis);
//			serveis.addAll(serveisComuns);
//
//			return conversioTipusHelper.convertirList(
//					serveis,
//					ProcSerDto.class);
//		} finally {
//			metricsHelper.fiMetrica(timer);
//		}
//	}

	@Override
	@Transactional(readOnly = true)
	public List<CodiValorOrganGestorComuDto> getServeisOrgan(
			Long entitatId,
			String organCodi,
			Long organFiltre,
			RolEnumDto rol,
			PermisEnum permis) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			List<CodiValorOrganGestorComuDto> serveis = new ArrayList<>();
			String organFiltreCodi = null;

			if (organFiltre != null) {
				OrganGestorEntity organGestorEntity = organGestorRepository.findOne(organFiltre);
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
							List<String> organsFills = organGestorCachable.getCodisOrgansGestorsFillsByOrgan(
									entitat.getDir3Codi(),
									organCodi);
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
			Collections.sort(serveis, new Comparator<CodiValorOrganGestorComuDto>() {
				@Override
				public int compare(CodiValorOrganGestorComuDto p1, CodiValorOrganGestorComuDto p2) {
					return p1.getValor().compareTo(p2.getValor());
				}
			});
			return serveis;

		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public List<CodiValorOrganGestorComuDto> getServeisOrganNotificables(Long entitatId, String organCodi, RolEnumDto rol, TipusEnviamentEnumDto enviamentTipus) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
		List<ServeiEntity> serveis;
		if (RolEnumDto.NOT_ADMIN.equals(rol)) {
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
		} else {
			PermisEnum permis = TipusEnviamentEnumDto.COMUNICACIO_SIR.equals(enviamentTipus) ? PermisEnum.COMUNICACIO_SIR :
					TipusEnviamentEnumDto.COMUNICACIO.equals(enviamentTipus) ? PermisEnum.COMUNICACIO :
							PermisEnum.NOTIFICACIO;
			return recuperarServeiAmbPermis(entitat, permis, organCodi);
		}
	}

//	@Override
//	public boolean hasServeisComunsAndNotificacioPermission(Long entitatId, TipusEnviamentEnumDto enviamentTipus) {
//		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		Permission[] permisos = new Permission[]{};
//		if (TipusEnviamentEnumDto.COMUNICACIO_SIR.equals(enviamentTipus)){
//			permisos = new Permission[]{
//					ExtendedPermission.COMUNS,
//					ExtendedPermission.COMUNICACIO_SIR
//			};
//		} else if (TipusEnviamentEnumDto.COMUNICACIO.equals(enviamentTipus)) {
//			permisos = new Permission[]{
//					ExtendedPermission.COMUNS,
//					ExtendedPermission.COMUNICACIO
//			};
//		} else {
//			permisos = new Permission[]{
//					ExtendedPermission.COMUNS,
//					ExtendedPermission.NOTIFICACIO
//			};
//		}
//
//		List<OrganGestorEntity> organGestorsAmbPermis = permisosCacheable.findOrgansGestorsWithPermisDirecte(entitat, auth, permisos);
//		return organGestorsAmbPermis != null && !organGestorsAmbPermis.isEmpty();
//	}

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

	private List<ServeiEntity> recuperarServeiSensePermis(
			EntitatEntity entitat,
			String organCodi){

		if (organCodi == null) {
			return serveiRepository.findByEntitat(entitat);
		}else {
			List<String> organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(
					entitat.getDir3Codi(),
					organCodi);
			return serveiRepository.findByOrganGestorCodiInOrComu(organsFills, entitat);
		}
	}

	private List<CodiValorOrganGestorComuDto> recuperarServeiAmbPermis(EntitatEntity entitat, PermisEnum permis, String organFiltre) {
		List<CodiValorOrganGestorComuDto> serveisAmbPermis = new ArrayList<>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<CodiValorOrganGestorComuDto> serveis = permisosService.getServeisAmbPermis(entitat.getId(), auth.getName(), permis);
		if (serveis == null || serveis.isEmpty()) {
			return serveisAmbPermis;
		}
		if (organFiltre == null) {
			serveisAmbPermis.addAll(serveis);
			return serveisAmbPermis;
		}
		List<String> organsFills = organGestorCachable.getCodisOrgansGestorsFillsByOrgan(entitat.getDir3Codi(), organFiltre);
		for (CodiValorOrganGestorComuDto servei: serveis) {
			if (organsFills.contains(servei.getOrganGestor())) {
				serveisAmbPermis.add(servei);
			}
		}
		return serveisAmbPermis;
	}


	@Override
	@Transactional(readOnly = true)
	public boolean hasAnyServeisWithPermis(Long entitatId, List<String> grups, PermisEnum permis) {
		Timer.Context timer = metricsHelper.iniciMetrica();
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
					new ObjectIdentifierExtractor<ServeiEntity>() {
						public Long getObjectIdentifier(ServeiEntity servei) {
							return servei.getId();
						}
					},
					ProcedimentEntity.class,
					entityComprovarHelper.getPermissionsFromName(permis),
					SecurityContextHolder.getContext().getAuthentication());
			return !serveis.isEmpty();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	private List<PermisDto> findPermisServeiOrganByServei(
			Long serveiId,
			String organGestor) {
		List<ProcSerOrganEntity> serveiOrgans = serveiOrganRepository.findByProcSerId(serveiId);
		List<PermisDto> permisos = new ArrayList<PermisDto>();
		List<String> organsAmbPermis = new ArrayList<String>();
		if (serveiOrgans != null && !serveiOrgans.isEmpty()) {

			if (organGestor != null) {
				organsAmbPermis = organigramaHelper.getCodisOrgansGestorsFillsByOrgan(
						serveiOrgans.get(0).getProcSer().getEntitat().getDir3Codi(),
						organGestor);
			}
			for (ProcSerOrganEntity serveiOrgan: serveiOrgans) {
				List<PermisDto> permisosProcOrgan = permisosHelper.findPermisos(
						serveiOrgan.getId(),
						ProcSerOrganEntity.class);
				if (permisosProcOrgan != null && !permisosProcOrgan.isEmpty()) {
					String organ = serveiOrgan.getOrganGestor().getCodi();
					String organNom = serveiOrgan.getOrganGestor().getNom();
					boolean tePermis = true;

					if (organGestor != null)
						tePermis = organsAmbPermis.contains(organ);

					if (tePermis) {
						for (PermisDto permis : permisosProcOrgan) {
							permis.setOrgan(organ);
							permis.setOrganNom(organNom);
							permis.setPermetEdicio(tePermis);
						}
						permisos.addAll(permisosProcOrgan);
					}
				}
			}
		}
		return permisos;
	}
//
//	private List<PermisDto> findPermisServeiOrgan(
//			Long serveiId,
//			String organ,
//			String organActual) {
//		ProcSerOrganEntity serveiOrgan = serveiOrganRepository.findByProcSerIdAndOrganGestorCodi(serveiId, organ);
//		List<PermisDto> permisos = permisosHelper.findPermisos(
//				serveiOrgan.getId(),
//				ProcSerOrganEntity.class);
//		for (PermisDto permis: permisos) {
//			permis.setOrgan(organ);
//			permis.setOrganNom(serveiOrgan.getOrganGestor().getNom());
//		}
//		return permisos;
//	}

//	@Transactional
//	@Override
//	public void permisUpdate(
//			Long entitatId,
//			Long organGestorId,
//			Long id,
//			PermisDto permis) {
//		Timer.Context timer = metricsHelper.iniciMetrica();
//		try {
//			logger.debug("Modificació del permis del servei ("
//					+ "entitatId=" + entitatId +  ", "
//					+ "id=" + id + ", "
//					+ "permis=" + permis + ")");
//
//			if (TipusEnumDto.ROL.equals(permis.getTipus())) {
//				if (permis.getPrincipal().equalsIgnoreCase("tothom")) {
//					permis.setPrincipal(permis.getPrincipal().toLowerCase());
//				} else {
//					permis.setPrincipal(permis.getPrincipal().toUpperCase());
//				}
//			} else {
//				if (TipusEnumDto.USUARI.equals(permis.getTipus())) {
//					permis.setPrincipal(permis.getPrincipal().toLowerCase());
//				}
//			}
//
//			entityComprovarHelper.comprovarPermisAdminEntitatOAdminOrgan(entitatId,organGestorId);
//			ProcSerEntity servei = entityComprovarHelper.comprovarProcediment(entitatId, id);
//			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
//			// Permís a servei comú no global
//			if (servei.isComu() && permis.getOrgan() != null && !permis.getOrgan().isEmpty() && !entitat.getDir3Codi().equals(permis.getOrgan())) {
//				ProcSerOrganEntity serveiOrgan = serveiOrganRepository.findByProcSerIdAndOrganGestorCodi(servei.getId(), permis.getOrgan());
//				if (serveiOrgan == null) {
//					OrganGestorEntity organGestor = organGestorRepository.findByCodi(permis.getOrgan());
//					if (organGestor == null) {
//						organGestor = organGestorHelper.crearOrganGestor(entitat, permis.getOrgan());
//					}
//					serveiOrgan = serveiOrganRepository.save(ProcSerOrganEntity.getBuilder(servei, organGestor).build());
//				}
//				permisosHelper.updatePermis(
//						serveiOrgan.getId(),
//						ProcSerOrganEntity.class,
//						permis);
//			} else {
//				permisosHelper.updatePermis(
//						id,
//						ProcedimentEntity.class,
//						permis);
//			}
//			cacheHelper.evictFindProcedimentServeisWithPermis();
//			cacheHelper.evictFindProcedimentsOrganWithPermis();
//		} finally {
//			metricsHelper.fiMetrica(timer);
//		}
//	}
//
//	@Override
//	@Transactional
//	public void permisDelete(
//			Long entitatId,
//			Long organGestorId,
//			Long serveiId,
//			String organCodi,
//			Long permisId,
//			TipusPermis tipus) {
//		Timer.Context timer = metricsHelper.iniciMetrica();
//		try {
//			logger.debug("Eliminació del permis del servei ("
//					+ "entitatId=" + entitatId +  ", "
//					+ "serveiId=" + serveiId + ", "
//					+ "organCodi=" + organCodi + ", "
//					+ "permisId=" + permisId + ")");
//
//			entityComprovarHelper.comprovarPermisAdminEntitatOAdminOrgan(entitatId,organGestorId);
//
//			if (TipusPermis.PROCEDIMENT_ORGAN.equals(tipus)) {
//				ServeiOrganEntity serveiOrgan = serveiOrganRepository.findByServeiIdAndOrganGestorCodi(serveiId, organCodi);
//				entityComprovarHelper.comprovarServei(entitatId, serveiOrgan.getServei().getId());
//				permisosHelper.deletePermis(
//						serveiOrgan.getId(),
//						ProcSerOrganEntity.class,
//						permisId);
//			} else {
//				entityComprovarHelper.comprovarServei(entitatId, serveiId);
//				permisosHelper.deletePermis(
//						serveiId,
//						ProcedimentEntity.class,
//						permisId);
//			}
//			cacheHelper.evictFindServeisWithPermis();
//			cacheHelper.evictFindServeisOrganWithPermis();
//		} finally {
//			metricsHelper.fiMetrica(timer);
//		}
//	}

	// PROCEDIMENT-GRUP
	// ==========================================================
	
//	@Audita(entityType = TipusEntitat.PROCEDIMENT_GRUP, operationType = TipusOperacio.CREATE, returnType = TipusObjecte.DTO)
//	@Transactional(readOnly = true)
//	@Override
//	public ServeiGrupDto grupCreate(
//			Long entitatId,
//			Long id,
//			ServeiGrupDto serveiGrup) throws NotFoundException {
//		Timer.Context timer = metricsHelper.iniciMetrica();
//		try {
//			logger.debug("Modificació del grup del servei ("
//					+ "entitatId=" + entitatId +  ", "
//					+ "id=" + id + ", "
//					+ "permis=" + serveiGrup + ")");
//
//			//TODO: en cas de tothom, comprovar que sigui administrador d'Organ i que tant el grup com el servei son de l'Organ.
//
//			ServeiEntity servei = entityComprovarHelper.comprovarServei(
//					entitatId,
//					id,
//					false,
//					false,
//					false,
//					false);
//			GrupEntity grup = entityComprovarHelper.comprovarGrup(serveiGrup.getGrup().getId());
//
//			GrupProcSerEntity grupServeiEntity = GrupServeiEntity.getBuilder(
//					servei,
//					grup).build();
//
//			grupServeiEntity = grupServeiRepository.saveAndFlush(grupServeiEntity);
//			cacheHelper.evictFindServeisWithPermis();
//			return conversioTipusHelper.convertir(
//					grupServeiEntity,
//					ServeiGrupDto.class);
//		} finally {
//			metricsHelper.fiMetrica(timer);
//		}
//	}
//
//	@Audita(entityType = TipusEntitat.PROCEDIMENT_GRUP, operationType = TipusOperacio.UPDATE, returnType = TipusObjecte.DTO)
//	@Transactional(readOnly = true)
//	@Override
//	public ServeiGrupDto grupUpdate(
//			Long entitatId,
//			Long id,
//			ServeiGrupDto serveiGrup) throws NotFoundException {
//		Timer.Context timer = metricsHelper.iniciMetrica();
//		try {
//			logger.debug("Modificació del grup del servei ("
//					+ "entitatId=" + entitatId +  ", "
//					+ "id=" + id + ", "
//					+ "permis=" + serveiGrup + ")");
//
//			//TODO: en cas de tothom, comprovar que sigui administrador d'Organ i que tant el grup com el servei son de l'Organ.
//
//			ServeiEntity servei = entityComprovarHelper.comprovarServei(
//					entitatId,
//					id,
//					false,
//					false,
//					false,
//					false);
//			GrupEntity grup = entityComprovarHelper.comprovarGrup(serveiGrup.getGrup().getId());
//
//			GrupServeiEntity grupServeiEntity = entityComprovarHelper.comprovarGrupServei(
//					serveiGrup.getId());
//
//			grupServeiEntity.update(servei, grup);
//
//			grupServeiEntity = grupServeiRepository.saveAndFlush(grupServeiEntity);
//			cacheHelper.evictFindServeisWithPermis();
//			return conversioTipusHelper.convertir(
//					grupServeiEntity,
//					ServeiGrupDto.class);
//		} finally {
//			metricsHelper.fiMetrica(timer);
//		}
//	}
//
//	@Audita(entityType = TipusEntitat.PROCEDIMENT_GRUP, operationType = TipusOperacio.DELETE, returnType = TipusObjecte.DTO)
//	@Transactional
//	@Override
//	public ServeiGrupDto grupDelete(
//			Long entitatId,
//			Long serveiGrupId) throws NotFoundException {
//		Timer.Context timer = metricsHelper.iniciMetrica();
//		try {
//			logger.debug("Modificació del grup del servei ("
//					+ "entitatId=" + entitatId +  ", "
//					+ "serveiGrupID=" + serveiGrupId + ")");
//
//			//TODO: en cas de tothom, comprovar que sigui administrador d'Organ i que tant el grup com el servei son de l'Organ.
//			entityComprovarHelper.comprovarEntitat(
//					entitatId,
//					false,
//					false,
//					false);
//
//			GrupServeiEntity grupServeiEntity = grupServeiRepository.findOne(serveiGrupId);
//
//			grupServeiRepository.delete(grupServeiEntity);
//			cacheHelper.evictFindServeisWithPermis();
//			return conversioTipusHelper.convertir(
//					grupServeiEntity,
//					ServeiGrupDto.class);
//		} finally {
//			metricsHelper.fiMetrica(timer);
//		}
//	}
//
//	@Override
//	@Transactional(readOnly = true)
//	public List<TipusAssumpteDto> findTipusAssumpte(EntitatDto entitat) {
//		Timer.Context timer = metricsHelper.iniciMetrica();
//		try {
//			List<TipusAssumpteDto> tipusAssumpte = new ArrayList<TipusAssumpteDto>();
//
//			try {
//				List<TipusAssumpte> tipusAssumpteRegistre = pluginHelper.llistarTipusAssumpte(entitat.getDir3Codi());
//
//				if (tipusAssumpteRegistre != null)
//					for (TipusAssumpte assumpteRegistre : tipusAssumpteRegistre) {
//						TipusAssumpteDto assumpte = new TipusAssumpteDto();
//						assumpte.setCodi(assumpteRegistre.getCodi());
//						assumpte.setNom(assumpteRegistre.getNom());
//
//						tipusAssumpte.add(assumpte);
//					}
//			} catch (SistemaExternException e) {
//				String errorMessage = "No s'han pogut recuperar els codis d'assumpte de l'entitat: " + entitat.getDir3Codi();
//				logger.error(
//						errorMessage,
//						e.getMessage());
//			}
//			return tipusAssumpte;
//		} finally {
//			metricsHelper.fiMetrica(timer);
//		}
//	}
//
//	@Override
//	@Transactional(readOnly = true)
//	public List<CodiAssumpteDto> findCodisAssumpte(
//			EntitatDto entitat,
//			String codiTipusAssumpte) {
//		Timer.Context timer = metricsHelper.iniciMetrica();
//		try {
//			List<CodiAssumpteDto> codiAssumpte = new ArrayList<CodiAssumpteDto>();
//			try {
//				List<CodiAssumpte> tipusAssumpteRegistre = pluginHelper.llistarCodisAssumpte(
//						entitat.getDir3Codi(),
//						codiTipusAssumpte);
//
//				if (tipusAssumpteRegistre != null)
//					for (CodiAssumpte assumpteRegistre : tipusAssumpteRegistre) {
//						CodiAssumpteDto assumpte = new CodiAssumpteDto();
//						assumpte.setCodi(assumpteRegistre.getCodi());
//						assumpte.setNom(assumpteRegistre.getNom());
//						assumpte.setTipusAssumpte(assumpteRegistre.getTipusAssumpte());
//
//						codiAssumpte.add(assumpte);
//					}
//			} catch (SistemaExternException e) {
//				String errorMessage = "No s'han pogut recuperar els codis d'assumpte del tipus d'assumpte: " + codiTipusAssumpte;
//				logger.error(
//						errorMessage,
//						e.getMessage());
//			}
//			return codiAssumpte;
//		} finally {
//			metricsHelper.fiMetrica(timer);
//		}
//	}
//
//	@Transactional(readOnly = true)
//	@Override
//	public void refrescarCache(EntitatDto entitat) {
//		Timer.Context timer = metricsHelper.iniciMetrica();
//		try {
//			logger.debug("Preparant per buidar la informació en cache dels serveis...");
//
////			cacheHelper.evictFindByGrupAndPermisServeisUsuariActualAndEntitat(entitat.getId());
////			cacheHelper.evictFindByPermisServeisUsuariActual(entitat.getId());
////			cacheHelper.evictFindPermisServeisUsuariActualAndEntitat(entitat.getId());
////			cacheHelper.evictFindOrganismesByEntitat(entitat.getDir3Codi());
////			cacheHelper.evictFindOrganigramaByEntitat(entitat.getDir3Codi());
//			cacheHelper.evictFindServeisWithPermis();
//			cacheHelper.evictFindServeisOrganWithPermis();
//		} finally {
//			metricsHelper.fiMetrica(timer);
//		}
//	}
//
	private static final Logger logger = LoggerFactory.getLogger(EntitatServiceImpl.class);

}
