package es.caib.notib.core.service;

import com.codahale.metrics.Timer;
import com.google.common.base.Strings;
import es.caib.notib.core.api.dto.CodiAssumpteDto;
import es.caib.notib.core.api.dto.CodiValorComuDto;
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
import es.caib.notib.core.api.dto.TipusAssumpteDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.dto.notificacio.TipusEnviamentEnumDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.procediment.ProcSerDataDto;
import es.caib.notib.core.api.dto.procediment.ProcSerDto;
import es.caib.notib.core.api.dto.procediment.ProcSerFiltreDto;
import es.caib.notib.core.api.dto.procediment.ProcSerFormDto;
import es.caib.notib.core.api.dto.procediment.ProcSerGrupDto;
import es.caib.notib.core.api.dto.procediment.ProcSerOrganDto;
import es.caib.notib.core.api.dto.procediment.ProcSerSimpleDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.exception.PermissionDeniedException;
import es.caib.notib.core.api.exception.SistemaExternException;
import es.caib.notib.core.api.service.AuditService.TipusEntitat;
import es.caib.notib.core.api.service.AuditService.TipusObjecte;
import es.caib.notib.core.api.service.AuditService.TipusOperacio;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.core.aspect.Audita;
import es.caib.notib.core.cacheable.OrganGestorCachable;
import es.caib.notib.core.cacheable.PermisosCacheable;
import es.caib.notib.core.cacheable.ProcSerCacheable;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.GrupEntity;
import es.caib.notib.core.entity.GrupProcSerEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.ProcSerEntity;
import es.caib.notib.core.entity.ProcSerOrganEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.entity.ProcedimentFormEntity;
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
import es.caib.notib.core.repository.ProcSerRepository;
import es.caib.notib.core.repository.ProcedimentFormRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.core.security.ExtendedPermission;
import es.caib.notib.plugin.registre.CodiAssumpte;
import es.caib.notib.plugin.registre.TipusAssumpte;
import es.caib.notib.plugin.unitat.NodeDir3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.model.Permission;
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
import java.util.Set;

/**
 * Implementació del servei de gestió de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ProcedimentServiceImpl implements ProcedimentService{

	@Resource
	private ProcedimentRepository procedimentRepository;
	@Resource
	private ProcSerRepository procSerRepository;
	@Resource
	private ProcedimentFormRepository procedimentFormRepository;
	@Resource
	private OrganGestorRepository organGestorRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private ProcSerHelper procedimentHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private GrupService grupService;
	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private GrupProcSerRepository grupProcedimentRepository;
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
	private ProcSerOrganRepository procedimentOrganRepository;
	@Autowired
	private OrganGestorCachable organGestorCachable;
	@Autowired
	private ProcSerCacheable procedimentsCacheable;
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

	public static final String PROCEDIMENT_ORGAN_NO_SYNC = "Hi ha procediments que pertanyen a òrgans no existents en l'organigrama actual";
	public static Map<String, ProgresActualitzacioDto> progresActualitzacio = new HashMap<>();
	public static Map<Long, Integer> procedimentsAmbOrganNoSincronitzat = new HashMap<>();
	
	@Audita(entityType = TipusEntitat.PROCEDIMENT, operationType = TipusOperacio.CREATE, returnType = TipusObjecte.DTO)
	@Override
	@Transactional
	public ProcSerDto create(Long entitatId, ProcSerDataDto procediment) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Creant un nou procediment (procediment=" + procediment + ")");
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			// Organ gestor
			OrganGestorEntity organGestor = organGestorRepository.findByCodi(procediment.getOrganGestor()); 
			if (organGestor == null) {
//				organGestor = organGestorHelper.crearOrganGestor(entitat, procediment.getOrganGestor());
				throw new NotFoundException(procediment.getOrganGestor(), OrganGestorEntity.class);
			}
			
			ProcedimentEntity.ProcedimentEntityBuilder procedimentEntityBuilder =
					ProcedimentEntity.getBuilder(
							procediment.getCodi(),
							procediment.getNom(),
							procediment.getRetard(),
							procediment.getCaducitat(),
							entitat,
							procediment.isAgrupar(),
							organGestor,
							procediment.getTipusAssumpte(),
							procediment.getTipusAssumpteNom(),
							procediment.getCodiAssumpte(),
							procediment.getCodiAssumpteNom(),
							procediment.isComu(),
							procediment.isRequireDirectPermission());

			if (procediment.isEntregaCieActiva()) {
				EntregaCieEntity entregaCie = new EntregaCieEntity(procediment.getCieId(), procediment.getOperadorPostalId());
				procedimentEntityBuilder.entregaCie(entregaCieRepository.save(entregaCie));
			}
			cacheHelper.evictFindProcedimentServeisWithPermis();
			return conversioTipusHelper.convertir(procedimentRepository.save(procedimentEntityBuilder.build()), ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Audita(entityType = TipusEntitat.PROCEDIMENT, operationType = TipusOperacio.UPDATE, returnType = TipusObjecte.DTO)
	@Override
	@Transactional
	public ProcSerDto update(
			Long entitatId,
			ProcSerDataDto procediment,
			boolean isAdmin,
			boolean isAdminEntitat) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Actualitzant procediment ("
					+ "procediment=" + procediment + ")");
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (!isAdminEntitat && procediment.isComu()) {
				throw new PermissionDeniedException(
						procediment.getId(),
						ProcedimentEntity.class,
						auth.getName(),
						"ADMINISTRADORENTITAT");
			}
			
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					false,
					false,
					false);
			ProcedimentEntity procedimentEntity = null;
			PagadorPostalEntity pagadorPostal = null;
			PagadorCieEntity pagadorCie = null;
			if(!isAdmin) {
				procedimentEntity = (ProcedimentEntity) entityComprovarHelper.comprovarProcediment(
						entitat,
						procediment.getId());
			} else {
				procedimentEntity = procedimentRepository.findOne(procediment.getId());
			}

			EntregaCieEntity entregaCie = procedimentEntity.getEntregaCie();
			if (procediment.isEntregaCieActiva()) {
				if (entregaCie == null) {
					entregaCie = entregaCieRepository.save(
							new EntregaCieEntity(procediment.getCieId(), procediment.getOperadorPostalId())
					);
				} else {
					entregaCie.update(procediment.getCieId(), procediment.getOperadorPostalId());
				}
			}
			
			List<GrupProcSerEntity> grupsProcediment = grupProcedimentRepository.findByProcSer(procedimentEntity);
			
			if (!procediment.isAgrupar()) {
				grupProcedimentRepository.delete(grupsProcediment);
			}
			
			//#271 Check canvi codi SIA, si es modifica s'han de modificar tots els enviaments pendents
			if (!procediment.getCodi().equals(procedimentEntity.getCodi())) {
				//Obtenir notificacions pendents.
				List<NotificacioEntity> notificacionsPendentsNotificar = notificacioRepository.findNotificacionsPendentsDeNotificarByProcediment(procedimentEntity);
				for (NotificacioEntity notificacioEntity : notificacionsPendentsNotificar) {
					//modificar el codi SIA i activar per tal que scheduled ho torni a agafar
					notificacioEntity.updateCodiSia(procediment.getCodi());
					notificacioEntity.resetIntentsNotificacio();
					notificacioRepository.save(notificacioEntity);
				}
			}
			
			// Organ gestor
			OrganGestorEntity organGestor = organGestorRepository.findByCodi(procediment.getOrganGestor()); 
			if (organGestor == null) {
//				organGestor = organGestorHelper.crearOrganGestor(entitat, procediment.getOrganGestor());
				throw new NotFoundException(procediment.getOrganGestor(), OrganGestorEntity.class);
			}
			// Si canviam l'organ gestor, i aquest no s'utilitza en cap altre procediment, l'eliminarem (1)
			OrganGestorEntity organGestorAntic = null;
			if (procedimentEntity.getOrganGestor() != null && !procedimentEntity.getOrganGestor().getCodi().equals(procediment.getOrganGestor())) {
				organGestorAntic = procedimentEntity.getOrganGestor();
			}
			// Si hi ha hagut qualque canvi a un d'aquests camps
			if ((procediment.isComu() != procedimentEntity.isComu()) || (procediment.isAgrupar() != procedimentEntity.isAgrupar()) ||
					(procediment.isRequireDirectPermission() != procedimentEntity.isRequireDirectPermission())) {
				cacheHelper.evictFindProcedimentServeisWithPermis();
				cacheHelper.evictFindProcedimentsOrganWithPermis();
				if (organGestor != null && !Strings.isNullOrEmpty(organGestor.getCodi())) {
					cacheHelper.evictFindUsuarisAmbPermis(procediment.getId() + "", organGestor.getCodi());
				}
			}
			procedimentEntity.update(
						procediment.getCodi(),
						procediment.getNom(),
						entitat,
						procediment.isEntregaCieActiva() ? entregaCie : null,
						procediment.getRetard(),
						procediment.getCaducitat(),
						procediment.isAgrupar(),
						organGestor,
						procediment.getTipusAssumpte(),
						procediment.getTipusAssumpteNom(),
						procediment.getCodiAssumpte(),
						procediment.getCodiAssumpteNom(),
						procediment.isComu(),
						procediment.isRequireDirectPermission());
			procedimentRepository.save(procedimentEntity);

			if (!procediment.isEntregaCieActiva() && entregaCie != null) {
				entregaCieRepository.delete(entregaCie);
			}
			// Si canviam l'organ gestor, i aquest no s'utilitza en cap altre procediment, l'eliminarem (2)
			if (organGestorAntic != null) {
				List<ProcedimentEntity> procedimentsOrganGestorAntic = procedimentRepository.findByOrganGestorId(organGestorAntic.getId());
				if (procedimentsOrganGestorAntic == null || procedimentsOrganGestorAntic.isEmpty()) {
					organGestorRepository.delete(organGestorAntic);
				}
			}

			notificacioTableViewRepository.updateProcediment(procedimentEntity.isComu(),
					procedimentEntity.getNom(),
					procedimentEntity.isRequireDirectPermission(),
					procedimentEntity.getCodi());
			enviamentTableRepository.updateProcediment(procedimentEntity.isComu(),
					procedimentEntity.isRequireDirectPermission(),
					procedimentEntity.getCodi());

			return conversioTipusHelper.convertir(
					procedimentEntity, 
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
			ProcedimentEntity procedimentEntity = procedimentRepository.findOne(id);
			procedimentEntity.updateActiu(actiu);
			cacheHelper.evictFindProcedimentServeisWithPermis();
			cacheHelper.evictFindProcedimentsOrganWithPermis();
			return conversioTipusHelper.convertir(procedimentEntity, ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
    }

    @Audita(entityType = TipusEntitat.PROCEDIMENT, operationType = TipusOperacio.DELETE, returnType = TipusObjecte.DTO)
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

			ProcSerEntity procedimentEntity = entityComprovarHelper.comprovarProcediment(
					entitat, 
					id);
			if (!isAdminEntitat && procedimentEntity.isComu()) {
				throw new PermissionDeniedException(
						procedimentEntity.getId(),
						ProcedimentEntity.class,
						auth.getName(),
						"ADMINISTRADORENTITAT");
			}
			//Eliminar grups del procediment
			List<GrupProcSerEntity> grupsDelProcediment = grupProcedimentRepository.findByProcSer(procedimentEntity);
			for (GrupProcSerEntity grupProcedimentEntity : grupsDelProcediment) {
				grupProcedimentRepository.delete(grupProcedimentEntity);
			}
			//Eliminar procediment
			procedimentRepository.delete(procedimentEntity.getId());
			permisosHelper.revocarPermisosEntity(id,ProcedimentEntity.class);
			
			//TODO: Decidir si mantenir l'Organ Gestor encara que no hi hagi procediments o no
			//		Recordar que ara l'Organ té més coses assignades: Administrador, grups, pagadors ...
			//		Es pot mirar si esta en ús amb la funció organGestorService.organGestorEnUs(organId);
//			OrganGestorEntity organGestor = procedimentEntity.getOrganGestor();
//			if (organGestor != null) {
//				List<ProcedimentEntity> procedimentsOrganGestorAntic = procedimentRepository.findByOrganGestorId(organGestor.getId());
//				if (procedimentsOrganGestorAntic == null || procedimentsOrganGestorAntic.isEmpty()) {
//					organGestorRepository.delete(organGestor);
//				}
//			}
			
			return conversioTipusHelper.convertir(
					procedimentEntity, 
					ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean procedimentEnUs(Long procedimentId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			//Compravacions en ús
			boolean procedimentEnUs=false;
				//1) Si té notificacions
				List<NotificacioEntity> notificacionsByProcediment = notificacioRepository.findByProcedimentId(procedimentId);
				procedimentEnUs=notificacionsByProcediment != null && !notificacionsByProcediment.isEmpty();
			
			return procedimentEnUs;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean procedimentAmbGrups(Long procedimentId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			//Compravar si agrupar
			ProcSerEntity procediment = procSerRepository.findById(procedimentId);
			return procediment.isAgrupar();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public boolean procedimentActiu(Long procedimentId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			//Compravar si agrupar
			ProcSerEntity procediment = procSerRepository.findById(procedimentId);
			return procediment.isActiu();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	public boolean isUpdatingProcediments(EntitatDto entitatDto) {
		ProgresActualitzacioDto progres = progresActualitzacio.get(entitatDto.getDir3Codi());
		return progres != null && (progres.getProgres() > 0 && progres.getProgres() < 100) && !progres.isError();
	}

	@Override
	public boolean actualitzarProcediment(String codiSia, EntitatDto entitat) {
		try {
			ProcSerDto proc = pluginHelper.getProcSerByCodiSia(codiSia, false);
			if (proc == null) {
				EntitatEntity entity = entityComprovarHelper.comprovarEntitat(entitat.getId(), false, false, false);
				ProcedimentEntity procediment = procedimentRepository.findByCodiAndEntitat(codiSia, entity);
				if (procediment != null) {
					procediment.updateActiu(false);
					procedimentRepository.save(procediment);
//					procedimentRepository.updateActiu(procediment.getCodi(), false);
				}
				return false;
			}
			ProgresActualitzacioDto progres = new ProgresActualitzacioDto();
			List<OrganGestorEntity> organsModificats = new ArrayList<>();
			Map<String, String[]> avisosProcedimentsOrgans = new HashMap<>();
			List<NodeDir3> unitatsWs = pluginHelper.unitatsOrganitzativesFindByPare(entitat, entitat.getDir3Codi(), null, null);
			List<String> codiOrgansGda = new ArrayList<>();
			for (NodeDir3 unitat: unitatsWs) {
				codiOrgansGda.add(unitat.getCodi());
			}
			EntitatEntity entity = entityComprovarHelper.comprovarEntitat(entitat.getId(), false, false, false);
			procedimentHelper.actualitzarProcedimentFromGda(progres, proc, entity, codiOrgansGda, true, organsModificats, avisosProcedimentsOrgans);

			if (avisosProcedimentsOrgans.size() > 0) {
				if (procedimentsAmbOrganNoSincronitzat.containsKey(entitat)) {
					procedimentsAmbOrganNoSincronitzat.put(entitat.getId(), procedimentsAmbOrganNoSincronitzat.get(entitat.getId()) + avisosProcedimentsOrgans.size());
				} else {
					procedimentsAmbOrganNoSincronitzat.put(entitat.getId(), avisosProcedimentsOrgans.size());
				}
				procSerSyncHelper.addAvisosSyncProcediments(avisosProcedimentsOrgans, entitat.getId());
			}

			boolean eliminarOrgans = procSerSyncHelper.isActualitzacioProcedimentsEliminarOrgansProperty();
			if (!eliminarOrgans) {
				return true;
			}
			for (OrganGestorEntity organGestorAntic: organsModificats) {
				//#260 Modificació passar la funcionalitat del for dins un procediment, ja que pel temps de transacció fallava
				procedimentHelper.eliminarOrganSiNoEstaEnUs(progres,organGestorAntic);
			}
			return true;
		} catch (Exception ex) {
			logger.error("Error actualitzant el procediment", ex);
			throw ex;
		}
	}

	@Override
	//@Transactional(timeout = 300)
	public void actualitzaProcediments(EntitatDto entitatDto) {
		
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			procSerSyncHelper.actualitzaProcediments(entitatDto);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
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

	@Transactional(readOnly = true)
	@Override
	public ProcSerDto findById(
			Long entitatId,
			boolean isAdministrador,
			Long procedimentId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta del procediment ("
					+ "entitatId=" + entitatId + ", "
					+ "procedimentId=" + procedimentId + ")");
				
			if (entitatId != null && !isAdministrador)
				entityComprovarHelper.comprovarEntitat(
						entitatId, 
						false, 
						false, 
						false);

			ProcSerEntity procediment = entityComprovarHelper.comprovarProcediment(
					entitatId, 
					procedimentId);
			ProcSerDto resposta = conversioTipusHelper.convertir(
					procediment,
					ProcSerDto.class);
			
			if (resposta != null) {
				procedimentHelper.omplirPermisos(resposta, false);
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
			String codiProcediment) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta del procediment ("
					+ "entitatId=" + entitatId + ", "
					+ "codi=" + codiProcediment + ")");
			EntitatEntity entitat = null;
				
			if (entitatId != null)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId, 
						false, 
						false, 
						false);
			
			ProcedimentEntity procediment = procedimentRepository.findByCodiAndEntitat(codiProcediment, entitat);
			
			return conversioTipusHelper.convertir(
					procediment, 
					ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public ProcSerDto findByNom(
			Long entitatId,
			String nomProcediment) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta del procediment ("
					+ "entitatId=" + entitatId + ", "
					+ "nom=" + nomProcediment + ")");
			EntitatEntity entitat = null;
				
			if (entitatId != null)
				entitat = entityComprovarHelper.comprovarEntitat(
						entitatId, 
						false, 
						false, 
						false);
			
			List<ProcedimentEntity> procediments = procedimentRepository.findByNomAndEntitat(nomProcediment, entitat);
			if (procediments != null && !procediments.isEmpty()) {
				return conversioTipusHelper.convertir(
						procediments.get(0),
						ProcSerDto.class);

			} else {
				return null;
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public Integer getProcedimentsAmbOrganNoSincronitzat(Long entitatId) {
		Integer organsNoSincronitzats = procedimentsAmbOrganNoSincronitzat.get(entitatId);
		if (organsNoSincronitzats == null) {
			organsNoSincronitzats = procedimentRepository.countByEntitatIdAndOrganNoSincronitzatTrue(entitatId);
			procedimentsAmbOrganNoSincronitzat.put(entitatId, organsNoSincronitzats);
		}
		return organsNoSincronitzats;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProcSerSimpleDto> findByEntitat(Long entitatId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			
			List<ProcedimentEntity> procediment = procedimentRepository.findByEntitat(entitat);
			
			return conversioTipusHelper.convertirList(
					procediment,
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
				procedimentRepository.findByOrganGestorCodiIn(organsFills),
				ProcSerSimpleDto.class);
	}

	@Override
	@Transactional
	public List<ProcSerDto> findByOrganGestorIDescendentsAndComu(Long entitatId, OrganGestorDto organGestor) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
		List<String> organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(
				entitat.getDir3Codi(),
				organGestor.getCodi());
		return conversioTipusHelper.convertirList(
				procedimentRepository.findByOrganGestorCodiInOrComu(organsFills, entitat),
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
			List<Long> entitatsActivaId = new ArrayList<Long>();
			
			for (EntitatEntity entitatActiva : entitatsActiva) {
				entitatsActivaId.add(entitatActiva.getId());
			}
			Page<ProcedimentFormEntity> procediments = null;
			PaginaDto<ProcSerFormDto> procedimentsPage = null;
			Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();
			mapeigPropietatsOrdenacio.put("organGestorDesc", new String[] {"organGestor"});
			Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio);
			
			List<String> organsFills = new ArrayList<String>();
			if (organGestorActual != null) { // Administrador d'òrgan
				organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(
						entitatActual.getDir3Codi(), 
						organGestorActual.getCodi());
			}

			if (filtre == null) {
				if (isUsuariEntitat) {
					procediments = procedimentFormRepository.findAmbEntitatActual(
							entitatActual.getId(),
							pageable);
				} else if (isAdministrador) {
					procediments = procedimentFormRepository.findAmbEntitatActiva(entitatsActivaId, pageable);
				} else if (organGestorActual != null) { // Administrador d'òrgan
					procediments = procedimentFormRepository.findAmbOrganGestorActualOrComu(
							entitatActual.getId(),
							organsFills,
							pageable);
				}
			} else {
			
				if (isUsuariEntitat) {
					procediments = procedimentFormRepository.findAmbEntitatAndFiltre(
							entitatActual.getId(),
							filtre.getCodi() == null || filtre.getCodi().isEmpty(), 
							filtre.getCodi() == null ? "" : filtre.getCodi(),
							filtre.getNom() == null || filtre.getNom().isEmpty(),
							filtre.getNom() == null ? "" : filtre.getNom(),
							filtre.getOrganGestor() == null || filtre.getOrganGestor().isEmpty(),
							filtre.getOrganGestor() == null ? "" : filtre.getOrganGestor(),
							filtre.isComu(),
							filtre.isEntregaCieActiva(),
							pageable);

				} else if (isAdministrador) {
					procediments = procedimentFormRepository.findAmbFiltre(
							filtre.getCodi() == null || filtre.getCodi().isEmpty(), 
							filtre.getCodi() == null ? "" : filtre.getCodi(),
							filtre.getNom() == null || filtre.getNom().isEmpty(),
							filtre.getNom() == null ? "" : filtre.getNom(),
							filtre.getOrganGestor() == null || filtre.getOrganGestor().isEmpty(),
							filtre.getOrganGestor() == null ? "" : filtre.getOrganGestor(),
							filtre.isComu(),
							filtre.isEntregaCieActiva(),
							pageable);

				} else if (organGestorActual != null) { // Administrador d'òrgan
					procediments = procedimentFormRepository.findAmbOrganGestorOrComuAndFiltre(
							entitatActual.getId(),
							filtre.getCodi() == null || filtre.getCodi().isEmpty(), 
							filtre.getCodi() == null ? "" : filtre.getCodi(),
							filtre.getNom() == null || filtre.getNom().isEmpty(),
							filtre.getNom() == null ? "" : filtre.getNom(),
							filtre.getOrganGestor() == null || filtre.getOrganGestor().isEmpty(),
							filtre.getOrganGestor() == null ? "" : filtre.getOrganGestor(),
							organsFills,
							filtre.isComu(),
							filtre.isEntregaCieActiva(),
							pageable);

				}
			}
//			if (procediments != null) {
				procedimentsPage = paginacioHelper.toPaginaDto(procediments, ProcSerFormDto.class);
//			}
			assert procedimentsPage != null;
			for (ProcSerFormDto procediment: procedimentsPage.getContingut()) {
				List<PermisDto> permisos = permisosHelper.findPermisos(
						procediment.getId(),
						ProcedimentEntity.class);

				if (procediment.isComu()) {
					String organActual = null;
					if (organGestorActual != null) {
						organActual = organGestorActual.getCodi();
//					} else {
//						organActual = entitatActual.getDir3Codi();
					}
					permisos.addAll(findPermisProcedimentOrganByProcediment(procediment.getId(), organActual));
				}

				List<GrupDto> grups = grupService.findGrupsByProcSer(procediment.getId());
				procediment.setGrups(grups);
				procediment.setPermisos(permisos);
			}
			return procedimentsPage;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProcSerDto> findAll() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de tots els procediments");
			
			entityComprovarHelper.comprovarPermisos(
					null,
					true,
					true,
					false);
			return conversioTipusHelper.convertirList(
						procedimentRepository.findAll(),
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
			logger.debug("Consulta de tots els procediments");
			
			entityComprovarHelper.comprovarPermisos(
					null,
					true,
					true,
					false);
			
			List<GrupProcSerEntity> grupsProcediments = grupProcedimentRepository.findAll();
			return conversioTipusHelper.convertirList(
						grupsProcediments,
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
			logger.debug("Consulta de tots els procediments d'una entitat");
			
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					true,
					false,
					false);
			
			List<GrupProcSerEntity> grupsProcediments = grupProcedimentRepository.findByProcSerEntitat(entitat);
			return conversioTipusHelper.convertirList(
						grupsProcediments,
						ProcSerGrupDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProcSerDto> findProcediments(Long entitatId, List<String> grups) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {	
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					true,
					false,
					false);
			return conversioTipusHelper.convertirList(
					procedimentRepository.findProcedimentsByEntitatAndGrup(entitat, grups),
					ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProcSerDto> findProcedimentsAmbGrups(Long entitatId, List<String> grups) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					true,
					false,
					false);
			return conversioTipusHelper.convertirList(
					procedimentRepository.findProcedimentsAmbGrupsByEntitatAndGrup(entitat, grups),
					ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProcSerDto> findProcedimentsSenseGrups(Long entitatId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					true,
					false,
					false);
			return conversioTipusHelper.convertirList(
					procedimentRepository.findProcedimentsSenseGrupsByEntitat(entitat),
					ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProcSerSimpleDto> findProcedimentsWithPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<ProcSerSimpleDto> procedimentsAmbPermis = new ArrayList<>();
			for (ProcSerSimpleDto procSer : findProcedimentServeisWithPermis(entitatId, usuariCodi, permis)) {
				if (ProcSerTipusEnum.PROCEDIMENT.equals(procSer.getTipus())) {
					procedimentsAmbPermis.add(procSer);
				}
			}
			return procedimentsAmbPermis;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "procsersPermis", key="#entitatId.toString().concat('-').concat(#usuariCodi).concat('-').concat(#permis.name())")
	public List<ProcSerSimpleDto> findProcedimentServeisWithPermis(Long entitatId, String usuariCodi, PermisEnum permis) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId,true,false,false);
			Permission[] permisos = entityComprovarHelper.getPermissionsFromName(permis);
			List<ProcSerEntity> procediments = procedimentsCacheable.getProcedimentsWithPermis(usuariCodi, entitat, permisos);

			// 7. Convertim els procediments/serveis a dto
			return conversioTipusHelper.convertirList(procediments, ProcSerSimpleDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "procsersPermisMenu", key="#entitatId.toString().concat('-').concat(#usuariCodi).concat('-').concat(#permis.name())")
	public List<ProcSerSimpleDto> findProcedimentServeisWithPermisMenu(Long entitatId, String usuariCodi, PermisEnum permis) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId,true,false,false);
			Permission[] permisos = entityComprovarHelper.getPermissionsFromName(permis);
			List<ProcSerEntity> procediments = procedimentsCacheable.getProcedimentsWithPermisMenu(usuariCodi, entitat, permisos);

			// 7. Convertim els procediments/serveis a dto
			return conversioTipusHelper.convertirList(procediments, ProcSerSimpleDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProcSerDto> findProcedimentsByOrganGestor(String organGestorCodi) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			OrganGestorEntity organGestor = organGestorRepository.findByCodi(organGestorCodi);
			if (organGestor == null) {
				throw new NotFoundException(organGestorCodi, OrganGestorEntity.class);
			}
			return conversioTipusHelper.convertirList(procedimentRepository.findByOrganGestorId(organGestor.getId()), ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProcSerDto> findProcedimentsByOrganGestorWithPermis(
			Long entitatId,
			String organGestorCodi, 
			List<String> grups,
			PermisEnum permis) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId, 
					true, 
					false, 
					false);
			Permission[] permisos = entityComprovarHelper.getPermissionsFromName(permis);
			OrganGestorEntity organGestor = entityComprovarHelper.comprovarOrganGestor(entitat, organGestorCodi);
			// 1. Obtenim tots els procediments de l'òrgan gestor
			List<ProcedimentEntity> procediments = procedimentRepository.findProcedimentsByOrganGestorAndGrup(entitat, organGestor.getId(), grups);
			
			// 2. Si tenim permis a sobre de l'òrgan o un dels pares, llavors tenim permís a sobre tots els procediments de l'òrgan
			List<OrganGestorEntity> organsGestors = organigramaHelper.getOrgansGestorsParesExistentsByOrgan(entitat.getDir3Codi(), organGestorCodi);
			permisosHelper.filterGrantedAny(
					organsGestors,
					new ObjectIdentifierExtractor<OrganGestorEntity>() {
						public Long getObjectIdentifier(OrganGestorEntity organGestor) {
							return organGestor.getId();
						}
					},
					OrganGestorEntity.class,
					permisos,
					auth);
			if (organsGestors.isEmpty()) {
				// 3. Si no tenim permis sobre òrgan, llavors miram els permisos sobre el procediment
				permisosHelper.filterGrantedAny(
						procediments,
						new ObjectIdentifierExtractor<ProcedimentEntity>() {
							public Long getObjectIdentifier(ProcedimentEntity procediment) {
								return procediment.getId();
							}
						},
						ProcedimentEntity.class,
						permisos,
						auth);
			}
			
			// 4. Procediments comuns
			List<ProcedimentEntity> procedimentsComuns = procedimentRepository.findByComuTrue();
			permisosHelper.filterGrantedAny(
					procedimentsComuns,
					new ObjectIdentifierExtractor<ProcedimentEntity>() {
						public Long getObjectIdentifier(ProcedimentEntity procediment) {
							return procediment.getId();
						}
					},
					ProcedimentEntity.class,
					permisos,
					auth);
			procedimentsComuns.removeAll(procediments);
			procediments.addAll(procedimentsComuns);
			
			return conversioTipusHelper.convertirList(
					procediments,
					ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CodiValorComuDto> getProcedimentsOrgan(
			Long entitatId,
			String organCodi,
			Long organFiltre,
			RolEnumDto rol,
			PermisEnum permis) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			List<CodiValorComuDto> procedimentsOrgan = new ArrayList<>();
			List<ProcedimentEntity> procediments = new ArrayList<>();
			String organFiltreCodi = null;

			if (organFiltre != null) {
				OrganGestorEntity organGestorEntity = organGestorRepository.findOne(organFiltre);
				if (organGestorEntity != null)
					organFiltreCodi = organGestorEntity.getCodi();
			}

			if (RolEnumDto.tothom.equals(rol)) {
				procediments = recuperarProcedimentAmbPermis(entitat, permis, organFiltreCodi);
				Set<ProcedimentEntity> auxSet = procedimentRepository.findByEntitatAndComuTrueAndRequireDirectPermissionIsFalse(entitat);
				auxSet.addAll(procediments);
				procediments = new ArrayList<>(auxSet);
			} else {

				if (organFiltreCodi != null) {
					List<ProcedimentEntity> procedimentsDisponibles = procedimentRepository.findByEntitat(entitat);
					if (procedimentsDisponibles != null) {
						for (ProcedimentEntity proc : procedimentsDisponibles) {
							if (proc.isComu() || (proc.getOrganGestor() != null && organFiltreCodi.equalsIgnoreCase(proc.getOrganGestor().getCodi()))) {
								procediments.add(proc);
							}
						}
					}
				} else {
					if (RolEnumDto.NOT_SUPER.equals(rol)) {
						procediments = procedimentRepository.findAll();
					} else if (RolEnumDto.NOT_ADMIN.equals(rol)) {
						procediments = procedimentRepository.findByEntitat(entitat);
					} else if (RolEnumDto.NOT_ADMIN_ORGAN.equals(rol)) {
						if (organCodi != null) {
							List<String> organsFills = organGestorCachable.getCodisOrgansGestorsFillsByOrgan(
									entitat.getDir3Codi(),
									organCodi);
							procediments = procedimentRepository.findByOrganGestorCodiInOrComu(organsFills, entitat);
						}
					}
				}
			}

			Collections.sort(procediments, new Comparator<ProcedimentEntity>() {
				@Override
				public int compare(ProcedimentEntity p1, ProcedimentEntity p2) {
					return p1.getNom().compareTo(p2.getNom());
				}
			});

			for (ProcedimentEntity procediment : procediments) {
				String nom = procediment.getCodi();
				if (procediment.getNom() != null && !procediment.getNom().isEmpty()) {
					nom += " - " + procediment.getNom();
				}
				procedimentsOrgan.add(new CodiValorComuDto(procediment.getId().toString(), nom, procediment.isComu()));
			}

			return procedimentsOrgan;

		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CodiValorOrganGestorComuDto> getProcedimentsOrganNotificables(Long entitatId, String organCodi, RolEnumDto rol, TipusEnviamentEnumDto enviamentTipus) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
		List<ProcedimentEntity> procediments;
		if (RolEnumDto.NOT_ADMIN.equals(rol)) {
			procediments = recuperarProcedimentSensePermis(entitat, organCodi);
			// Eliminam els procediments inactius
			Iterator<ProcedimentEntity> it = procediments.iterator();
			while (it.hasNext()) {
				ProcedimentEntity curr = it.next();
				if (!curr.isActiu()) {
					it.remove();
				}
			}
		} else if (TipusEnviamentEnumDto.COMUNICACIO_SIR.equals(enviamentTipus)){
			procediments = recuperarProcedimentAmbPermis(entitat, PermisEnum.COMUNIACIO_SIR, organCodi);
		} else {
			procediments = recuperarProcedimentAmbPermis(entitat, PermisEnum.NOTIFICACIO, organCodi);
		}

		return procedimentsToCodiValorOrganGestorComuDto(procediments);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean hasProcedimentsComunsAndNotificacioPermission(Long entitatId, TipusEnviamentEnumDto enviamentTipus) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Permission[] permisos = null;
		if (TipusEnviamentEnumDto.COMUNICACIO_SIR.equals(enviamentTipus)) {
			permisos = new Permission[]{
					ExtendedPermission.COMUNS,
					ExtendedPermission.COMUNICACIO_SIR
			};
		} else {
			permisos = new Permission[]{
					ExtendedPermission.COMUNS,
					ExtendedPermission.NOTIFICACIO
			};
		}

		List<OrganGestorEntity> organGestorsAmbPermis = permisosCacheable.findOrgansGestorsWithPermis(entitat, auth, permisos);
		return organGestorsAmbPermis != null && !organGestorsAmbPermis.isEmpty();
	}

	private List<CodiValorOrganGestorComuDto> procedimentsToCodiValorOrganGestorComuDto(List<ProcedimentEntity> procediments) {
		List<CodiValorOrganGestorComuDto> response = new ArrayList<>();
		for (ProcedimentEntity procediment : procediments) {
			String nom = procediment.getCodi();
			if (procediment.getNom() != null && !procediment.getNom().isEmpty()) {
				nom += " - " + procediment.getNom();
			}
			String organCodi = procediment.getOrganGestor() != null ? procediment.getOrganGestor().getCodi() : "";
			response.add(new CodiValorOrganGestorComuDto(procediment.getId().toString(), nom, organCodi,
					procediment.isComu()));
		}
		return response;
	}

	private List<ProcedimentEntity> recuperarProcedimentSensePermis(
			EntitatEntity entitat,
			String organCodi){

		if (organCodi == null) {
			return procedimentRepository.findByEntitat(entitat);
		}else {
			List<String> organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(
					entitat.getDir3Codi(),
					organCodi);
			return procedimentRepository.findByOrganGestorCodiInOrComu(organsFills, entitat);
		}
	}

	private List<ProcedimentEntity> recuperarProcedimentAmbPermis(EntitatEntity entitat, PermisEnum permis, String organFiltre) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Permission[] permisos = entityComprovarHelper.getPermissionsFromName(permis);
		List<ProcSerEntity> procediments = procedimentsCacheable.getProcedimentsWithPermis(auth.getName(), entitat, permisos);
		List<ProcSerOrganEntity> procedimentsOrgans = procedimentsCacheable.getProcedimentOrganWithPermis(auth, entitat, permisos);
		List<ProcSerEntity> procSerAmbPermis;
		if (organFiltre != null) {
			List<ProcSerOrganEntity> procedimentsOrgansAmbPermis = new ArrayList<>();
			if(procedimentsOrgans != null && !procedimentsOrgans.isEmpty()) {

				List<String> organsFills = organGestorCachable.getCodisOrgansGestorsFillsByOrgan(entitat.getDir3Codi(), organFiltre);
				for (ProcSerOrganEntity procedimentOrgan: procedimentsOrgans) {
					if (organsFills.contains(procedimentOrgan.getOrganGestor().getCodi())) {
						procedimentsOrgansAmbPermis.add(procedimentOrgan);
					}
				}
			}
			procSerAmbPermis = addProcedimentsOrgan(procediments, procedimentsOrgansAmbPermis, organFiltre);
			boolean hasComunsPermission = hasPermisProcedimentsComuns(entitat.getDir3Codi(), organFiltre);
			if (hasComunsPermission && (PermisEnum.NOTIFICACIO.equals(permis) || PermisEnum.COMUNIACIO_SIR.equals(permis))) {
				List<ProcedimentEntity> procedimentsComuns = procedimentRepository.findByEntitatAndComuTrue(entitat);
				for (ProcedimentEntity procediment: procedimentsComuns) {
					if (!procSerAmbPermis.contains(procediment) && procediment.isActiu()) {
						procSerAmbPermis.add(procediment);
					}
				}
			}
			return filtraProcediments(procSerAmbPermis);
		}

		Set<ProcSerEntity> setProcediments = new HashSet<>();
		if (procediments != null) {
			setProcediments = new HashSet<>(procediments);
		}
		if (procedimentsOrgans != null && !procedimentsOrgans.isEmpty()) {
			for (ProcSerOrganEntity procedimentOrgan : procedimentsOrgans) {
				setProcediments.add(procedimentOrgan.getProcSer());
			}
		}
		procSerAmbPermis = new ArrayList<>(setProcediments);
		return filtraProcediments(procSerAmbPermis);
	}

	private List<ProcedimentEntity> filtraProcediments(List<ProcSerEntity> procSerAmbPermis) {
		List<ProcedimentEntity> procedimentsAmbPermis = new ArrayList<>();
		for (ProcSerEntity procSer : procSerAmbPermis) {
			if (ProcSerTipusEnum.PROCEDIMENT.equals(procSer.getTipus())) {
				procedimentsAmbPermis.add((ProcedimentEntity) procSer);
			}
		}
		return procedimentsAmbPermis;
	}

	private boolean hasPermisProcedimentsComuns(String codiEntitat, String codiOrgan) {
		List<String> organsPares = organGestorCachable.getCodisAncestors(codiEntitat, codiOrgan);
		for (String codiDir3 : organsPares) {
			OrganGestorEntity organGestorEntity = organGestorRepository.findByCodi(codiDir3);
			if(organGestorEntity != null && permisosHelper.hasPermission(
					organGestorEntity.getId(),
					OrganGestorEntity.class,
					new Permission[]{ExtendedPermission.COMUNS})
			) {
				return true;
			}
		}
		return false;
	}

	private List<ProcSerEntity> addProcedimentsOrgan(
			List<ProcSerEntity> procediments,
			List<ProcSerOrganEntity> procedimentsOrgans,
			String organFiltre) {

		Set<ProcSerEntity> setProcediments = new HashSet<>();
		if (organFiltre != null) {
			if (procediments != null) {
				for (ProcSerEntity proc : procediments) {
					if (proc.isComu() || (proc.getOrganGestor() != null && organFiltre.equalsIgnoreCase(proc.getOrganGestor().getCodi()))) {
						setProcediments.add(proc);
					}
				}
			}
			if (procedimentsOrgans != null && !procedimentsOrgans.isEmpty()) {
				for (ProcSerOrganEntity procedimentOrgan : procedimentsOrgans) {
					setProcediments.add(procedimentOrgan.getProcSer());
				}
			}
		}
		return new ArrayList<>(setProcediments);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean hasAnyProcedimentsWithPermis(Long entitatId, List<String> grups, PermisEnum permis) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					true,
					false,
					false);
			List<ProcedimentEntity> procediments = procedimentRepository.findProcedimentsByEntitatAndGrup(entitat, grups);
			if (procediments == null || procediments.isEmpty())
				return false;
			
			permisosHelper.filterGrantedAny(
					procediments,
					new ObjectIdentifierExtractor<ProcedimentEntity>() {
						public Long getObjectIdentifier(ProcedimentEntity procediment) {
							return procediment.getId();
						}
					},
					ProcedimentEntity.class,
					entityComprovarHelper.getPermissionsFromName(permis),
					SecurityContextHolder.getContext().getAuthentication());
			return !procediments.isEmpty();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean hasPermisProcediment(
			Long procedimentId,
			PermisEnum permis) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			return entityComprovarHelper.hasPermisProcediment(procedimentId, permis);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional(readOnly = true)
	@Cacheable(value = "procedimentsOrganPermis", key="#entitatId.toString().concat('-').concat(#usuariCodi).concat('-').concat(#permis.name())")
	@Override
	public List<ProcSerOrganDto> findProcedimentsOrganWithPermis(
			Long entitatId, 
			String usuariCodi, 
			PermisEnum permis) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					true,
					false,
					false);
			
			Permission[] permisos = entityComprovarHelper.getPermissionsFromName(permis);

			List<ProcSerOrganEntity> procedimentOrgansAmbPermis = procedimentsCacheable.getProcedimentOrganWithPermis(
					auth,
					entitat,
					permisos);

			// 2. Convertim els procediments a dto
			return conversioTipusHelper.convertirList(
					procedimentOrgansAmbPermis,
					ProcSerOrganDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<ProcSerOrganDto> findProcedimentsOrganWithPermisByOrgan(
			String organGestor, 
			String entitatCodi,
			List<ProcSerOrganDto> procedimentsOrgans) {
		
		List<ProcSerOrganDto> procedimentsOrgansAmbPermis = new ArrayList<ProcSerOrganDto>();
		if(procedimentsOrgans != null && !procedimentsOrgans.isEmpty()) {

			List<String> organsFills = organigramaHelper.getCodisOrgansGestorsFillsByOrgan(entitatCodi, organGestor);
			for (ProcSerOrganDto procedimentOrgan: procedimentsOrgans) {
				if (organsFills.contains(procedimentOrgan.getOrganGestor().getCodi()))
					procedimentsOrgansAmbPermis.add(procedimentOrgan);
			}
		}
		return procedimentsOrgansAmbPermis;
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<String> findProcedimentsOrganCodiWithPermisByProcediment(
			ProcSerDto procediment,
			String entitatCodi,
			List<ProcSerOrganDto> procedimentsOrgans) {
		
		Set<String> organsDisponibles = new HashSet<String>();
		if(!procedimentsOrgans.isEmpty()) {
			for (ProcSerOrganDto procedimentOrgan: procedimentsOrgans) {
				if (procedimentOrgan.getProcSer().equals(procediment)) {
					String organ = procedimentOrgan.getOrganGestor().getCodi(); 
					if (!organsDisponibles.contains(organ))
						organsDisponibles.addAll(organigramaHelper.getCodisOrgansGestorsFillsByOrgan(
								entitatCodi, 
								organ));
				}
			}
		}
		return new ArrayList<String>(organsDisponibles);
	}


	@Transactional
	@Override
	public List<PermisDto> permisFind(
			Long entitatId,
			boolean isAdministrador,
			Long procedimentId,
			String organ,
			String organActual,
			TipusPermis tipus,
			PaginacioParamsDto paginacioParams) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta dels permisos del procediment (entitatId=" + entitatId +  ", "
					+ "procedimentId=" + procedimentId + ", "
					+ "tipus=" + tipus + ")"); 

			List<PermisDto> permisos = new ArrayList<PermisDto>();
			EntitatEntity entitat = null;
			if (entitatId != null && !isAdministrador) {
				entitat = entityComprovarHelper.comprovarEntitat(entitatId, false,false,false);
			}
			ProcSerEntity procediment = entityComprovarHelper.comprovarProcediment(entitat, procedimentId);
			boolean adminOrgan = organActual != null;

			if (tipus == null) {
				permisos = findPermisProcediment(procediment, adminOrgan, organActual);
				permisos.addAll(findPermisProcedimentOrganByProcediment(procedimentId, organActual));
				permisosHelper.ordenarPermisos(paginacioParams, permisos);
				return permisos;
			}

			if (TipusPermis.PROCEDIMENT.equals(tipus)) {
				permisos = findPermisProcediment(procediment, adminOrgan, organActual);
				permisosHelper.ordenarPermisos(paginacioParams, permisos);
				return permisos;
			}

			permisos =  (organ == null) ? findPermisProcedimentOrganByProcediment(procedimentId, organActual)
					: findPermisProcedimentOrgan(procedimentId, organ, organActual);
			permisosHelper.ordenarPermisos(paginacioParams, permisos);
			return permisos;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private List<PermisDto> findPermisProcediment(
			ProcSerEntity procediment,
			boolean adminOrgan,
			String organ) {
		List<PermisDto> permisos = permisosHelper.findPermisos(
				procediment.getId(),
				ProcedimentEntity.class);
		boolean isAdministradorOrganAndNoComuOrAdminEntitat = (adminOrgan && !procediment.isComu()) || //administrador òrgan i procediment no comú
				(adminOrgan && procediment.isComu() && (procediment.getOrganGestor().getCodi().equals(organ))) ||  //administrador òrgan, procediment comú però del mateix òrgan
				!adminOrgan; //administrador entitat
		for (PermisDto permis: permisos)
			permis.setPermetEdicio(isAdministradorOrganAndNoComuOrAdminEntitat);
		return permisos;
	}
	
	private List<PermisDto> findPermisProcedimentOrganByProcediment(
			Long procedimentId,
			String organGestor) {
		List<ProcSerOrganEntity> procedimentOrgans = procedimentOrganRepository.findByProcSerId(procedimentId);
		List<PermisDto> permisos = new ArrayList<PermisDto>();
		List<String> organsAmbPermis = new ArrayList<String>();
		if (procedimentOrgans != null && !procedimentOrgans.isEmpty()) {
			
			if (organGestor != null) {
				organsAmbPermis = organigramaHelper.getCodisOrgansGestorsFillsByOrgan(
						procedimentOrgans.get(0).getProcSer().getEntitat().getDir3Codi(),
						organGestor);
			}
			for (ProcSerOrganEntity procedimentOrgan: procedimentOrgans) {
				List<PermisDto> permisosProcOrgan = permisosHelper.findPermisos(
						procedimentOrgan.getId(),
						ProcSerOrganEntity.class);
				if (permisosProcOrgan != null && !permisosProcOrgan.isEmpty()) {
					String organ = procedimentOrgan.getOrganGestor().getCodi();
					String organNom = procedimentOrgan.getOrganGestor().getNom();
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
	
	private List<PermisDto> findPermisProcedimentOrgan(Long procedimentId, String organ, String organActual) {

		ProcSerOrganEntity procedimentOrgan = procedimentOrganRepository.findByProcSerIdAndOrganGestorCodi(procedimentId, organ);
		List<PermisDto> permisos = permisosHelper.findPermisos(procedimentOrgan.getId(), ProcSerOrganEntity.class);
		for (PermisDto permis: permisos) {
			permis.setOrgan(organ);
			permis.setOrganNom(procedimentOrgan.getOrganGestor().getNom());
		}
		return permisos;
	}

	@Transactional
	@Override
	public void permisUpdate(Long entitatId, Long organGestorId, Long id, PermisDto permis) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Modificació del permis del procediment (entitatId=" + entitatId +  ", " + "id=" + id + ", " + "permis=" + permis + ")");
			if (TipusEnumDto.ROL.equals(permis.getTipus())) {
				if (permis.getPrincipal().equalsIgnoreCase("tothom")) {
					permis.setPrincipal(permis.getPrincipal().toLowerCase());					
				} else {
					permis.setPrincipal(permis.getPrincipal().toUpperCase());
				}
			} else {
				if (TipusEnumDto.USUARI.equals(permis.getTipus())) {
					permis.setPrincipal(permis.getPrincipal().toLowerCase());
				}
			}
			
			entityComprovarHelper.comprovarPermisAdminEntitatOAdminOrgan(entitatId,organGestorId);
			ProcSerEntity procediment = entityComprovarHelper.comprovarProcediment(entitatId, id);
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			OrganGestorEntity organGestor = null;
			// Permís a procediment comú no global
			if (procediment.isComu() && permis.getOrgan() != null && !permis.getOrgan().isEmpty() && !entitat.getDir3Codi().equals(permis.getOrgan())) {
				ProcSerOrganEntity procedimentOrgan = procedimentOrganRepository.findByProcSerIdAndOrganGestorCodi(procediment.getId(), permis.getOrgan());
				if (procedimentOrgan == null) {
					organGestor = organGestorRepository.findByCodi(permis.getOrgan());
					if (organGestor == null) {
//						organGestor = organGestorHelper.crearOrganGestor(entitat, permis.getOrgan());
						throw new NotFoundException(procediment.getOrganGestor(), OrganGestorEntity.class);
					}
					procedimentOrgan = procedimentOrganRepository.save(ProcSerOrganEntity.getBuilder(procediment, organGestor).build());
				}
				permisosHelper.updatePermis(procedimentOrgan.getId(), ProcSerOrganEntity.class, permis);
			} else {
				permisosHelper.updatePermis(id, ProcedimentEntity.class, permis);
			}
			cacheHelper.evictFindProcedimentServeisWithPermis();
			cacheHelper.evictFindProcedimentsOrganWithPermis();
			if (organGestor != null && !Strings.isNullOrEmpty(organGestor.getCodi())) {
				cacheHelper.evictFindUsuarisAmbPermis(procediment.getId() + "", organGestor.getCodi());
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional
	public void permisDelete(Long entitatId, Long organGestorId, Long procedimentId, String organCodi, Long permisId, TipusPermis tipus) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Eliminació del permis del procediment (entitatId=" + entitatId +  ", " + "procedimentId=" + procedimentId
					+ ", " + "organCodi=" + organCodi + ", " + "permisId=" + permisId + ")");
			
			entityComprovarHelper.comprovarPermisAdminEntitatOAdminOrgan(entitatId,organGestorId);
			
			if (TipusPermis.PROCEDIMENT_ORGAN.equals(tipus)) {
				ProcSerOrganEntity procedimentOrgan = procedimentOrganRepository.findByProcSerIdAndOrganGestorCodi(procedimentId, organCodi);
				entityComprovarHelper.comprovarProcediment(entitatId, procedimentOrgan.getProcSer().getId());
				permisosHelper.deletePermis(procedimentOrgan.getId(), ProcSerOrganEntity.class, permisId);
			} else {
				entityComprovarHelper.comprovarProcediment(entitatId, procedimentId);
				permisosHelper.deletePermis(procedimentId, ProcedimentEntity.class, permisId);
			}
			cacheHelper.evictFindProcedimentServeisWithPermis();
			cacheHelper.evictFindProcedimentsOrganWithPermis();
			if (!Strings.isNullOrEmpty(organCodi)) {
				cacheHelper.evictFindUsuarisAmbPermis(procedimentId + "", organCodi);
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	// PROCEDIMENT-GRUP
	// ==========================================================
	
	@Audita(entityType = TipusEntitat.PROCEDIMENT_GRUP, operationType = TipusOperacio.CREATE, returnType = TipusObjecte.DTO)
	@Transactional(readOnly = true)
	@Override
	public ProcSerGrupDto grupCreate(Long entitatId, Long id, ProcSerGrupDto procedimentGrup) throws NotFoundException {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Modificació del grup del procediment (entitatId=" + entitatId +  ", " + "id=" + id + ", " + "permis=" + procedimentGrup + ")");
			
			//TODO: en cas de tothom, comprovar que sigui administrador d'Organ i que tant el grup com el procediment son de l'Organ.

			ProcSerEntity procediment = entityComprovarHelper.comprovarProcediment(entitatId, id,false,false,false,false, false);
			GrupEntity grup = entityComprovarHelper.comprovarGrup(procedimentGrup.getGrup().getId());
			GrupProcSerEntity grupProcedimentEntity = GrupProcSerEntity.getBuilder(procediment, grup).build();
			grupProcedimentEntity = grupProcedimentRepository.saveAndFlush(grupProcedimentEntity);
			cacheHelper.evictFindProcedimentServeisWithPermis();
			return conversioTipusHelper.convertir(grupProcedimentEntity, ProcSerGrupDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Audita(entityType = TipusEntitat.PROCEDIMENT_GRUP, operationType = TipusOperacio.UPDATE, returnType = TipusObjecte.DTO)
	@Transactional(readOnly = true)
	@Override
	public ProcSerGrupDto grupUpdate(Long entitatId, Long id, ProcSerGrupDto procedimentGrup) throws NotFoundException {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Modificació del grup del procediment (entitatId=" + entitatId +  ", " + "id=" + id + ", " + "permis=" + procedimentGrup + ")");
			//TODO: en cas de tothom, comprovar que sigui administrador d'Organ i que tant el grup com el procediment son de l'Organ.
			ProcSerEntity procediment = entityComprovarHelper.comprovarProcediment(entitatId, id,false,false,false,false, false);
			GrupEntity grup = entityComprovarHelper.comprovarGrup(procedimentGrup.getGrup().getId());
			GrupProcSerEntity grupProcedimentEntity = entityComprovarHelper.comprovarGrupProcediment(procedimentGrup.getId());
			grupProcedimentEntity.update(procediment, grup);
			grupProcedimentEntity = grupProcedimentRepository.saveAndFlush(grupProcedimentEntity);
			cacheHelper.evictFindProcedimentServeisWithPermis();
			return conversioTipusHelper.convertir(grupProcedimentEntity, ProcSerGrupDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Audita(entityType = TipusEntitat.PROCEDIMENT_GRUP, operationType = TipusOperacio.DELETE, returnType = TipusObjecte.DTO)
	@Transactional
	@Override
	public ProcSerGrupDto grupDelete(Long entitatId, Long procedimentGrupId) throws NotFoundException {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Modificació del grup del procediment (entitatId=" + entitatId +  ", " + "procedimentGrupID=" + procedimentGrupId + ")");
			//TODO: en cas de tothom, comprovar que sigui administrador d'Organ i que tant el grup com el procediment son de l'Organ.
			entityComprovarHelper.comprovarEntitat(entitatId,false,false,false);
			GrupProcSerEntity grupProcedimentEntity = grupProcedimentRepository.findOne(procedimentGrupId);
			grupProcedimentRepository.delete(grupProcedimentEntity);
			cacheHelper.evictFindProcedimentServeisWithPermis();
			return conversioTipusHelper.convertir(grupProcedimentEntity, ProcSerGrupDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<TipusAssumpteDto> findTipusAssumpte(EntitatDto entitat) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<TipusAssumpteDto> tipusAssumpte = new ArrayList<TipusAssumpteDto>();
		
			try {
				List<TipusAssumpte> tipusAssumpteRegistre = pluginHelper.llistarTipusAssumpte(entitat.getDir3Codi());
				
				if (tipusAssumpteRegistre != null) {
					for (TipusAssumpte assumpteRegistre : tipusAssumpteRegistre) {
						TipusAssumpteDto assumpte = new TipusAssumpteDto();
						assumpte.setCodi(assumpteRegistre.getCodi());
						assumpte.setNom(assumpteRegistre.getNom());

						tipusAssumpte.add(assumpte);
					}
				}
			} catch (SistemaExternException e) {
				String errorMessage = "No s'han pogut recuperar els codis d'assumpte de l'entitat: " + entitat.getDir3Codi();
				logger.error(
						errorMessage, 
						e.getMessage());
			}
			return tipusAssumpte;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CodiAssumpteDto> findCodisAssumpte(
			EntitatDto entitat,
			String codiTipusAssumpte) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<CodiAssumpteDto> codiAssumpte = new ArrayList<CodiAssumpteDto>();
			try {
				List<CodiAssumpte> tipusAssumpteRegistre = pluginHelper.llistarCodisAssumpte(
						entitat.getDir3Codi(),
						codiTipusAssumpte);
				
				if (tipusAssumpteRegistre != null)
					for (CodiAssumpte assumpteRegistre : tipusAssumpteRegistre) {
						CodiAssumpteDto assumpte = new CodiAssumpteDto();
						assumpte.setCodi(assumpteRegistre.getCodi());
						assumpte.setNom(assumpteRegistre.getNom());
						assumpte.setTipusAssumpte(assumpteRegistre.getTipusAssumpte());
						
						codiAssumpte.add(assumpte);
					}
			} catch (SistemaExternException e) {
				String errorMessage = "No s'han pogut recuperar els codis d'assumpte del tipus d'assumpte: " + codiTipusAssumpte;
				logger.error(
						errorMessage, 
						e.getMessage());
			}
			return codiAssumpte;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional(readOnly = true)
	@Override
	public void refrescarCache(EntitatDto entitat) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Preparant per buidar la informació en cache dels procediments...");
			
//			cacheHelper.evictFindByGrupAndPermisProcedimentsUsuariActualAndEntitat(entitat.getId());
//			cacheHelper.evictFindByPermisProcedimentsUsuariActual(entitat.getId());
//			cacheHelper.evictFindPermisProcedimentsUsuariActualAndEntitat(entitat.getId());
//			cacheHelper.evictFindOrganismesByEntitat(entitat.getDir3Codi());
//			cacheHelper.evictFindOrganigramaByEntitat(entitat.getDir3Codi());
			cacheHelper.evictFindProcedimentServeisWithPermis();
			cacheHelper.evictFindProcedimentsOrganWithPermis();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	private static final Logger logger = LoggerFactory.getLogger(EntitatServiceImpl.class);

}
