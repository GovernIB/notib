package es.caib.notib.logic.service;

import com.google.common.base.Strings;
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
import es.caib.notib.logic.intf.dto.CodiAssumpteDto;
import es.caib.notib.logic.intf.dto.CodiValorOrganGestorComuDto;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.PermisDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.ProcSerTipusEnum;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioDto;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.TipusAssumpteDto;
import es.caib.notib.logic.intf.dto.TipusEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.TipusEnviamentEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerCacheDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDataDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerFiltreDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerFormDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerGrupDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerOrganCacheDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerOrganDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerSimpleDto;
import es.caib.notib.logic.intf.dto.procediment.ProcedimentEstat;
import es.caib.notib.logic.intf.dto.procediment.ProgresActualitzacioProcSer;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.exception.PermissionDeniedException;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.logic.intf.service.AuditService.TipusEntitat;
import es.caib.notib.logic.intf.service.AuditService.TipusObjecte;
import es.caib.notib.logic.intf.service.AuditService.TipusOperacio;
import es.caib.notib.logic.intf.service.GrupService;
import es.caib.notib.logic.intf.service.PermisosService;
import es.caib.notib.logic.intf.service.ProcedimentService;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.GrupProcSerEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.ProcSerEntity;
import es.caib.notib.persist.entity.ProcSerOrganEntity;
import es.caib.notib.persist.entity.ProcedimentEntity;
import es.caib.notib.persist.entity.ProcedimentFormEntity;
import es.caib.notib.persist.entity.cie.EntregaCieEntity;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.EntregaCieRepository;
import es.caib.notib.persist.repository.EnviamentTableRepository;
import es.caib.notib.persist.repository.GrupProcSerRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.persist.repository.NotificacioTableViewRepository;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.ProcSerOrganRepository;
import es.caib.notib.persist.repository.ProcSerRepository;
import es.caib.notib.persist.repository.ProcedimentFormRepository;
import es.caib.notib.persist.repository.ProcedimentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementació del servei de gestió de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
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
	private ProcSerSyncHelper procSerSyncHelper;
	@Autowired
	private EntregaCieRepository entregaCieRepository;
	@Autowired
	private EnviamentTableRepository enviamentTableRepository;
	@Autowired
	private NotificacioTableViewRepository notificacioTableViewRepository;
	@Autowired
	private PermisosService permisosService;

	public static final String PROCEDIMENT_ORGAN_NO_SYNC = "Hi ha procediments que pertanyen a òrgans no existents en l'organigrama actual";
	public static Map<String, ProgresActualitzacioProcSer> progresActualitzacio = new HashMap<>();
	public static Map<Long, Integer> procedimentsAmbOrganNoSincronitzat = new HashMap<>();
	
	@Audita(entityType = TipusEntitat.PROCEDIMENT, operationType = TipusOperacio.CREATE, returnType = TipusObjecte.DTO)
	@Override
	@Transactional
	public ProcSerDto create(Long entitatId, ProcSerDataDto procediment) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Creant un nou procediment (procediment=" + procediment + ")");
			var entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			// Organ gestor
			var organGestor = organGestorRepository.findByCodi(procediment.getOrganGestor());
			if (organGestor == null) {
//				organGestor = organGestorHelper.crearOrganGestor(entitat, procediment.getOrganGestor());
				throw new NotFoundException(procediment.getOrganGestor(), OrganGestorEntity.class);
			}
			var procedimentEntityBuilder = ProcedimentEntity.getBuilder(procediment.getCodi(), procediment.getNom(),
							procediment.getRetard(), procediment.getCaducitat(), entitat, procediment.isAgrupar(), organGestor, procediment.getTipusAssumpte(),
							procediment.getTipusAssumpteNom(), procediment.getCodiAssumpte(), procediment.getCodiAssumpteNom(), procediment.isComu(),
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
	public ProcSerDto update(Long entitatId, ProcSerDataDto procediment, boolean isAdmin, boolean isAdminEntitat) throws NotFoundException {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Actualitzant procediment (procediment=" + procediment + ")");
			var auth = SecurityContextHolder.getContext().getAuthentication();
			if (!isAdminEntitat && procediment.isComu()) {
				throw new PermissionDeniedException(procediment.getId(), ProcedimentEntity.class, auth.getName(), "ADMINISTRADORENTITAT");
			}
			var entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false);
			ProcedimentEntity procedimentEntity;
			procedimentEntity = !isAdmin ? (ProcedimentEntity) entityComprovarHelper.comprovarProcediment(entitat, procediment.getId())
								: procedimentRepository.findById(procediment.getId()).orElseThrow();

			var entregaCie = procedimentEntity.getEntregaCie();
			if (procediment.isEntregaCieActiva()) {
				if (entregaCie == null) {
					entregaCie = entregaCieRepository.save(new EntregaCieEntity(procediment.getCieId(), procediment.getOperadorPostalId()));
				} else {
					entregaCie.update(procediment.getCieId(), procediment.getOperadorPostalId());
				}
			}
			var grupsProcediment = grupProcedimentRepository.findByProcSer(procedimentEntity);
			if (!procediment.isAgrupar()) {
				grupProcedimentRepository.deleteAll(grupsProcediment);
			}
			//#271 Check canvi codi SIA, si es modifica s'han de modificar tots els enviaments pendents
			if (!procediment.getCodi().equals(procedimentEntity.getCodi())) {
				//Obtenir notificacions pendents.
				var notificacionsPendentsNotificar = notificacioRepository.findNotificacionsPendentsDeNotificarByProcediment(procedimentEntity);
				for (var notificacioEntity : notificacionsPendentsNotificar) {
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
			procedimentEntity.update(procediment.getCodi(), procediment.getNom(), entitat, procediment.isEntregaCieActiva() ? entregaCie : null, procediment.getRetard(),
									procediment.getCaducitat(), procediment.isAgrupar(), organGestor, procediment.getTipusAssumpte(), procediment.getTipusAssumpteNom(),
									procediment.getCodiAssumpte(), procediment.getCodiAssumpteNom(), procediment.isComu(), procediment.isRequireDirectPermission());
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
			notificacioTableViewRepository.updateProcediment(procedimentEntity.isComu(), procedimentEntity.getNom(), procedimentEntity.isRequireDirectPermission(), procedimentEntity.getCodi());
			enviamentTableRepository.updateProcediment(procedimentEntity.isComu(), procedimentEntity.isRequireDirectPermission(), procedimentEntity.getCodi());
			return conversioTipusHelper.convertir(procedimentEntity, ProcSerDto.class);
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
			var procedimentEntity = procSerRepository.findById(id).orElseThrow(() -> new NotFoundException(id, ProcedimentEntity.class));
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
	public ProcSerDto delete(Long entitatId, Long id, boolean isAdminEntitat) throws NotFoundException {

		var timer = metricsHelper.iniciMetrica();
		try {
			var auth = SecurityContextHolder.getContext().getAuthentication();
			var entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false);
			var procedimentEntity = entityComprovarHelper.comprovarProcediment(entitat, id);
			if (!isAdminEntitat && procedimentEntity.isComu()) {
				throw new PermissionDeniedException(procedimentEntity.getId(), ProcedimentEntity.class, auth.getName(), "ADMINISTRADORENTITAT");
			}
			//Eliminar grups del procediment
			var grupsDelProcediment = grupProcedimentRepository.findByProcSer(procedimentEntity);
			for (var grupProcedimentEntity : grupsDelProcediment) {
				grupProcedimentRepository.delete(grupProcedimentEntity);
			}
			//Eliminar procediment
			procedimentRepository.deleteById(procedimentEntity.getId());
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
			
			return conversioTipusHelper.convertir(procedimentEntity, ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean procedimentEnUs(Long procedimentId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			//1) Si té notificacions
			var notificacionsByProcediment = notificacioRepository.findByProcedimentId(procedimentId);
			return notificacionsByProcediment != null && !notificacionsByProcediment.isEmpty();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean procedimentAmbGrups(Long procedimentId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			//Compravar si agrupar
			var procediment = procSerRepository.findById(procedimentId).orElseThrow();
			return procediment.isAgrupar();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public boolean procedimentActiu(Long procedimentId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			//Compravar si agrupar
			var procediment = procSerRepository.findById(procedimentId).orElseThrow(() -> new NotFoundException(procedimentId, ProcedimentEntity.class));
			return procediment.isActiu();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	public boolean isUpdatingProcediments(EntitatDto entitatDto) {

		var progres = progresActualitzacio.get(entitatDto.getDir3Codi());
		return progres != null && (progres.getProgres() > 0 && progres.getProgres() < 100) && !progres.isError();
	}

	@Override
	public boolean actualitzarProcediment(String codiSia, EntitatDto entitat) {

		try {
			var proc = pluginHelper.getProcSerByCodiSia(codiSia, false);
			if (proc == null) {
				var entity = entityComprovarHelper.comprovarEntitat(entitat.getId(), false, false, false);
				var procediment = procedimentRepository.findByCodiAndEntitat(codiSia, entity);
				if (procediment != null) {
					procediment.updateActiu(false);
					procedimentRepository.save(procediment);
//					procedimentRepository.updateActiu(procediment.getCodi(), false);
				}
				return false;
			}
			var progres = new ProgresActualitzacioProcSer();
			List<OrganGestorEntity> organsModificats = new ArrayList<>();
			Map<String, String[]> avisosProcedimentsOrgans = new HashMap<>();
			var unitatsWs = pluginHelper.unitatsOrganitzativesFindByPare(entitat.getCodi(), entitat.getDir3Codi(), null, null);
			List<String> codiOrgansGda = new ArrayList<>();
			for (var unitat: unitatsWs) {
				codiOrgansGda.add(unitat.getCodi());
			}
			var entity = entityComprovarHelper.comprovarEntitat(entitat.getId(), false, false, false);
			procedimentHelper.actualitzarProcedimentFromGda(progres, proc, entity, codiOrgansGda, true, organsModificats, avisosProcedimentsOrgans);

			if (avisosProcedimentsOrgans.size() > 0) {
				if (procedimentsAmbOrganNoSincronitzat.containsKey(entitat)) {
					procedimentsAmbOrganNoSincronitzat.put(entitat.getId(), procedimentsAmbOrganNoSincronitzat.get(entitat.getId()) + avisosProcedimentsOrgans.size());
				} else {
					procedimentsAmbOrganNoSincronitzat.put(entitat.getId(), avisosProcedimentsOrgans.size());
				}
				procSerSyncHelper.addAvisosSyncProcediments(avisosProcedimentsOrgans, entitat.getId());
			}
			var eliminarOrgans = procSerSyncHelper.isActualitzacioProcedimentsEliminarOrgansProperty();
			if (!eliminarOrgans) {
				return true;
			}
			for (var organGestorAntic: organsModificats) {
				//#260 Modificació passar la funcionalitat del for dins un procediment, ja que pel temps de transacció fallava
				procedimentHelper.eliminarOrganSiNoEstaEnUs(progres,organGestorAntic);
			}
			return true;
		} catch (Exception ex) {
			log.error("Error actualitzant el procediment", ex);
			throw ex;
		}
	}

	@Override
	//@Transactional(timeout = 300)
	public void actualitzaProcediments(EntitatDto entitatDto) {
		
		var timer = metricsHelper.iniciMetrica();
		try {
			procSerSyncHelper.actualitzaProcediments(entitatDto);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
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

	@Transactional(readOnly = true)
	@Override
	public ProcSerDto findById(Long entitatId, boolean isAdministrador, Long procedimentId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta del procediment (entitatId=" + entitatId + ", procedimentId=" + procedimentId + ")");
			if (entitatId != null && !isAdministrador) {
				entityComprovarHelper.comprovarEntitat(entitatId, false, false, false);
			}
			var procediment = entityComprovarHelper.comprovarProcediment(entitatId, procedimentId);
			var resposta = conversioTipusHelper.convertir(procediment, ProcSerDto.class);
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
	public ProcSerDto findByCodi(Long entitatId, String codiProcediment) throws NotFoundException {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta del procediment (entitatId=" + entitatId + ", codi=" + codiProcediment + ")");
			EntitatEntity entitat = null;
			if (entitatId != null) {
				entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false);
			}
			var procediment = procedimentRepository.findByCodiAndEntitat(codiProcediment, entitat);
			return conversioTipusHelper.convertir(procediment, ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public ProcSerDto findByNom(Long entitatId, String nomProcediment) throws NotFoundException {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta del procediment (entitatId=" + entitatId + ", nom=" + nomProcediment + ")");
			EntitatEntity entitat = null;
			if (entitatId != null) {
				entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false);
			}
			var procediments = procedimentRepository.findByNomAndEntitat(nomProcediment, entitat);
			return procediments != null && !procediments.isEmpty() ? conversioTipusHelper.convertir(procediments.get(0), ProcSerDto.class) : null;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public Integer getProcedimentsAmbOrganNoSincronitzat(Long entitatId) {

		var organsNoSincronitzats = procedimentsAmbOrganNoSincronitzat.get(entitatId);
		if (organsNoSincronitzats == null) {
			organsNoSincronitzats = procedimentRepository.countByEntitatIdAndOrganNoSincronitzatTrue(entitatId);
			procedimentsAmbOrganNoSincronitzat.put(entitatId, organsNoSincronitzats);
		}
		return organsNoSincronitzats;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProcSerSimpleDto> findByEntitat(Long entitatId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			var entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			var procediment = procedimentRepository.findByEntitat(entitat);
			return conversioTipusHelper.convertirList(procediment, ProcSerSimpleDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProcSerSimpleDto> findByOrganGestorIDescendents(Long entitatId, OrganGestorDto organGestor) {

		var entitat = entityComprovarHelper.comprovarEntitat(entitatId);
		var organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitat.getDir3Codi(), organGestor.getCodi());
		return conversioTipusHelper.convertirList(procedimentRepository.findByOrganGestorCodiIn(organsFills), ProcSerSimpleDto.class);
	}

	@Override
	@Transactional
	public List<ProcSerDto> findByOrganGestorIDescendentsAndComu(Long entitatId, OrganGestorDto organGestor) {

		var entitat = entityComprovarHelper.comprovarEntitat(entitatId);
		var organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitat.getDir3Codi(), organGestor.getCodi());
		return conversioTipusHelper.convertirList(procedimentRepository.findByOrganGestorCodiInOrComu(organsFills, entitat), ProcSerDto.class);
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
			Page<ProcedimentFormEntity> procediments = null;
			PaginaDto<ProcSerFormDto> procedimentsPage;
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
				procediments = isUsuariEntitat ? procedimentFormRepository.findAmbEntitatActual(entitatActual.getId(), pageable) :
								isAdministrador ? procedimentFormRepository.findAmbEntitatActiva(entitatsActivaId, pageable) :
								organGestorActual != null ? procedimentFormRepository.findAmbOrganGestorActualOrComu(entitatActual.getId(), organsFills, pageable) // Administrador d'òrgan
								: null;
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
							filtre.getEstat() == null,
							filtre.getEstat() == null ? null : ProcedimentEstat.ACTIU.equals(filtre.getEstat()),
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
							filtre.getEstat() == null,
							filtre.getEstat() == null ? null : ProcedimentEstat.ACTIU.equals(filtre.getEstat()),
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
							filtre.getEstat() == null,
							filtre.getEstat() == null ? null : ProcedimentEstat.ACTIU.equals(filtre.getEstat()),
							filtre.isComu(),
							filtre.isEntregaCieActiva(),
							pageable);
				}
			}
//			if (procediments != null) {
				procedimentsPage = paginacioHelper.toPaginaDto(procediments, ProcSerFormDto.class);
//			}
			assert procedimentsPage != null;
			List<PermisDto> permisos;
			for (var procediment: procedimentsPage.getContingut()) {

				permisos = permisosHelper.findPermisos(procediment.getId(), ProcedimentEntity.class);
				if (procediment.isComu()) {
					String organActual = null;
					if (organGestorActual != null) {
						organActual = organGestorActual.getCodi();
					}
					permisos.addAll(findPermisProcedimentOrganByProcediment(procediment.getId(), organActual));
				}
				var grups = grupService.findGrupsByProcSer(procediment.getId());
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

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de tots els procediments");
			entityComprovarHelper.comprovarPermisos(null, true, true, false);
			return conversioTipusHelper.convertirList(procedimentRepository.findAll(), ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProcSerGrupDto> findAllGrups() {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de tots els procediments");
			entityComprovarHelper.comprovarPermisos(null, true, true, false);
			var grupsProcediments = grupProcedimentRepository.findAll();
			return conversioTipusHelper.convertirList(grupsProcediments, ProcSerGrupDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProcSerGrupDto> findGrupsByEntitat(Long entitatId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de tots els procediments d'una entitat");
			var entitat = entityComprovarHelper.comprovarEntitat(entitatId, true, false, false);
			var grupsProcediments = grupProcedimentRepository.findByProcSerEntitat(entitat);
			return conversioTipusHelper.convertirList(grupsProcediments, ProcSerGrupDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProcSerDto> findProcediments(Long entitatId, List<String> grups) {

		var timer = metricsHelper.iniciMetrica();
		try {	
			var entitat = entityComprovarHelper.comprovarEntitat(entitatId, true, false, false);
			return conversioTipusHelper.convertirList(procedimentRepository.findProcedimentsByEntitatAndGrup(entitat, grups), ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProcSerDto> findProcedimentsAmbGrups(Long entitatId, List<String> grups) {

		var timer = metricsHelper.iniciMetrica();
		try {
			var entitat = entityComprovarHelper.comprovarEntitat(entitatId, true, false, false);
			return conversioTipusHelper.convertirList(procedimentRepository.findProcedimentsAmbGrupsByEntitatAndGrup(entitat, grups), ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProcSerDto> findProcedimentsSenseGrups(Long entitatId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			var entitat = entityComprovarHelper.comprovarEntitat(entitatId, true, false, false);
			return conversioTipusHelper.convertirList(procedimentRepository.findProcedimentsSenseGrupsByEntitat(entitat), ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
//	@Override
//	@Transactional(readOnly = true)
//	public List<ProcSerCacheDto> findProcedimentsWithPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
//
//		var timer = metricsHelper.iniciMetrica();
//		try {
//			List<ProcSerCacheDto> procedimentsAmbPermis = new ArrayList<>();
//			for (var procSer : findProcedimentServeisWithPermis(entitatId, usuariCodi, permis)) {
//				if (ProcSerTipusEnum.PROCEDIMENT.equals(procSer.getTipus())) {
//					procedimentsAmbPermis.add(procSer);
//				}
//			}
//			return procedimentsAmbPermis;
//		} finally {
//			metricsHelper.fiMetrica(timer);
//		}
//	}
//
//	@Override
//	@Transactional(readOnly = true)
//	@Cacheable(value = "procsersPermis", key="#entitatId.toString().concat('-').concat(#usuariCodi).concat('-').concat(#permis.name())")
//	public List<ProcSerCacheDto> findProcedimentServeisWithPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
//
//		var timer = metricsHelper.iniciMetrica();
//		try {
//			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId,true,false,false);
//			Permission[] permisos = entityComprovarHelper.getPermissionsFromName(permis);
//			return procedimentsCacheable.getProcedimentsWithPermis(usuariCodi, entitat, permisos);
//		} finally {
//			metricsHelper.fiMetrica(timer);
//		}
//	}
//
//	@Override
//	@Transactional(readOnly = true)
//	@Cacheable(value = "procsersPermisMenu", key="#entitatId.toString().concat('-').concat(#usuariCodi).concat('-').concat(#permis.name())")
//	public List<ProcSerCacheDto> findProcedimentServeisWithPermisMenu(Long entitatId, String usuariCodi, PermisEnum permis) {
//
//		var timer = metricsHelper.iniciMetrica();
//		try {
//			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId,true,false,false);
//			Permission[] permisos = entityComprovarHelper.getPermissionsFromName(permis);
//			return procedimentsCacheable.getProcedimentsWithPermisMenu(usuariCodi, entitat, permisos);
//		} finally {
//			metricsHelper.fiMetrica(timer);
//		}
//	}

	@Override
	@Transactional(readOnly = true)
	public List<ProcSerDto> findProcedimentsByOrganGestor(String organGestorCodi) {

		var timer = metricsHelper.iniciMetrica();
		try {
			var organGestor = organGestorRepository.findByCodi(organGestorCodi);
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
	public List<ProcSerDto> findProcedimentsByOrganGestorWithPermis(Long entitatId, String organGestorCodi, List<String> grups, PermisEnum permis) {

		var timer = metricsHelper.iniciMetrica();
		try {
			var auth = SecurityContextHolder.getContext().getAuthentication();
			var entitat = entityComprovarHelper.comprovarEntitat(entitatId, true, false, false);
			var permisos = entityComprovarHelper.getPermissionsFromName(permis);
			var organGestor = entityComprovarHelper.comprovarOrganGestor(entitat, organGestorCodi);
			// 1. Obtenim tots els procediments de l'òrgan gestor
			var procediments = procedimentRepository.findProcedimentsByOrganGestorAndGrup(entitat, organGestor.getId(), grups);
			
			// 2. Si tenim permis a sobre de l'òrgan o un dels pares, llavors tenim permís a sobre tots els procediments de l'òrgan
			var organsGestors = organigramaHelper.getOrgansGestorsParesExistentsByOrgan(entitat.getDir3Codi(), organGestorCodi);
			var o = new ObjectIdentifierExtractor<OrganGestorEntity>() {
				public Long getObjectIdentifier(OrganGestorEntity organGestor) {
					return organGestor.getId();
				}
			};
			permisosHelper.filterGrantedAny(organsGestors, o, OrganGestorEntity.class, permisos, auth);
			if (organsGestors.isEmpty()) {
				// 3. Si no tenim permis sobre òrgan, llavors miram els permisos sobre el procediment
				var p = new ObjectIdentifierExtractor<ProcedimentEntity>() {
					public Long getObjectIdentifier(ProcedimentEntity procediment) {
						return procediment.getId();
					}
				};
				permisosHelper.filterGrantedAny(procediments, p, ProcedimentEntity.class, permisos, auth);
			}
			
			// 4. Procediments comuns
			var procedimentsComuns = procedimentRepository.findByComuTrue();
			var p = new ObjectIdentifierExtractor<ProcedimentEntity>() {
				public Long getObjectIdentifier(ProcedimentEntity procediment) {
					return procediment.getId();
				}
			};
			permisosHelper.filterGrantedAny(procedimentsComuns, p, ProcedimentEntity.class, permisos, auth);
			procedimentsComuns.removeAll(procediments);
			procediments.addAll(procedimentsComuns);
			return conversioTipusHelper.convertirList(procediments, ProcSerDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CodiValorOrganGestorComuDto> getProcedimentsOrgan(Long entitatId, String organCodi, Long organFiltre, RolEnumDto rol, PermisEnum permis) {

		var timer = metricsHelper.iniciMetrica();
		try {
			var entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			List<CodiValorOrganGestorComuDto> procediments = new ArrayList<>();
			String organFiltreCodi = null;

			if (organFiltre != null) {
				var organGestorEntity = organGestorRepository.findById(organFiltre);
				if (organGestorEntity != null && organGestorEntity.isPresent())
					organFiltreCodi = organGestorEntity.get().getCodi();
			}

			if (RolEnumDto.tothom.equals(rol)) {
				procediments = recuperarProcedimentAmbPermis(entitat, permis, organFiltreCodi);
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				if (auth != null) {
					List<String> grups = cacheHelper.findRolsUsuariAmbCodi(auth.getName());
					procediments.addAll(permisosService.getProcSerComuns(entitat.getId(), grups, true, ProcSerTipusEnum.PROCEDIMENT));
				}
			} else {
				List<ProcedimentEntity> procedimentsEntitat = new ArrayList<>();
				if (organFiltreCodi != null) {
					var procedimentsDisponibles = procedimentRepository.findByEntitat(entitat);
					if (procedimentsDisponibles != null) {
						for (var proc : procedimentsDisponibles) {
							if (proc.isComu() || (proc.getOrganGestor() != null && organFiltreCodi.equalsIgnoreCase(proc.getOrganGestor().getCodi()))) {
								procedimentsEntitat.add(proc);
							}
						}
					}
				} else {
					if (RolEnumDto.NOT_SUPER.equals(rol)) {
						procedimentsEntitat = procedimentRepository.findAll();
					} else if (RolEnumDto.NOT_ADMIN.equals(rol)) {
						procedimentsEntitat = procedimentRepository.findByEntitat(entitat);
					} else if (RolEnumDto.NOT_ADMIN_ORGAN.equals(rol)) {
						if (organCodi != null) {
							var organsFills = organGestorCachable.getCodisOrgansGestorsFillsByOrgan(entitat.getDir3Codi(), organCodi);
							procedimentsEntitat = procedimentRepository.findByOrganGestorCodiInOrComu(organsFills, entitat);
						}
					}
				}
				for (ProcedimentEntity procediment: procedimentsEntitat) {
					procediments.add(CodiValorOrganGestorComuDto.builder()
							.id(procediment.getId())
							.codi(procediment.getCodi())
							.valor(procediment.getCodi() + ((procediment.getNom() != null && !procediment.getNom().isEmpty()) ? " - " + procediment.getNom() : ""))
							.organGestor(procediment.getOrganGestor() != null ? procediment.getOrganGestor().getCodi() : "")
							.comu(procediment.isComu())
							.build());
				}
			}

			Collections.sort(procediments, new Comparator<CodiValorOrganGestorComuDto>() {
				@Override
				public int compare(CodiValorOrganGestorComuDto p1, CodiValorOrganGestorComuDto p2) {
					return p1.getValor().compareTo(p2.getValor());
				}
			});
			return procediments;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private String getProcedimentCodiNom(String codi, String nom) {
		return codi + (nom != null && !nom.isBlank() ? " - " + nom : "");
	}

	@Override
	@Transactional(readOnly = true)
	public List<CodiValorOrganGestorComuDto> getProcedimentsOrganNotificables(Long entitatId, String organCodi, RolEnumDto rol, TipusEnviamentEnumDto enviamentTipus) {

		var entitat = entityComprovarHelper.comprovarEntitat(entitatId);
		List<ProcedimentEntity> procediments;
		if (RolEnumDto.NOT_ADMIN.equals(rol)) {
			procediments = recuperarProcedimentSensePermis(entitat, organCodi);
			// Eliminam els procediments inactius
			var it = procediments.iterator();
			while (it.hasNext()) {
				ProcedimentEntity curr = it.next();
				if (!curr.isActiu()) {
					it.remove();
				}
			}
			return procedimentsToCodiValorOrganGestorComuDto(procediments);
		} else {
			var permis = TipusEnviamentEnumDto.COMUNICACIO_SIR.equals(enviamentTipus) ? PermisEnum.COMUNICACIO_SIR :
					TipusEnviamentEnumDto.COMUNICACIO.equals(enviamentTipus) ? PermisEnum.COMUNICACIO : PermisEnum.NOTIFICACIO;
			return recuperarProcedimentAmbPermis(entitat, permis, organCodi);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public boolean hasProcedimentsComunsAndNotificacioPermission(Long entitatId, TipusEnviamentEnumDto enviamentTipus) {

		var auth = SecurityContextHolder.getContext().getAuthentication();
		var permis = TipusEnviamentEnumDto.COMUNICACIO_SIR.equals(enviamentTipus) ? PermisEnum.COMUNICACIO_SIR :
				TipusEnviamentEnumDto.COMUNICACIO.equals(enviamentTipus) ? PermisEnum.COMUNICACIO : PermisEnum.NOTIFICACIO;

//		List<OrganGestorEntity> organGestorsAmbPermis = permisosCacheable.findOrgansGestorsWithPermisDirecte(entitat, auth, permisos);
		var organGestorsAmbPermis = permisosService.getOrgansAmbPermis(entitatId, auth.getName(), PermisEnum.COMUNS);
		organGestorsAmbPermis.addAll(permisosService.getOrgansAmbPermis(entitatId, auth.getName(), permis));
		return organGestorsAmbPermis != null && !organGestorsAmbPermis.isEmpty();
	}

	private List<CodiValorOrganGestorComuDto> procedimentsToCodiValorOrganGestorComuDto(List<ProcedimentEntity> procediments) {

		List<CodiValorOrganGestorComuDto> response = new ArrayList<>();
		String nom, organCodi;
		for (var procediment : procediments) {
			nom = procediment.getCodi();
			if (procediment.getNom() != null && !procediment.getNom().isEmpty()) {
				nom += " - " + procediment.getNom();
			}
			organCodi = procediment.getOrganGestor() != null ? procediment.getOrganGestor().getCodi() : "";
			response.add(CodiValorOrganGestorComuDto.builder().id(procediment.getId()).codi(procediment.getCodi()).valor(nom).organGestor(organCodi).comu(procediment.isComu()).build());
		}
		return response;
	}

	private String getOrganCodi(ProcSerCacheDto procediment) {
		return procediment.getOrganGestor() != null ? procediment.getOrganGestor().getCodi() : "";
	}

	private List<ProcedimentEntity> recuperarProcedimentSensePermis(EntitatEntity entitat, String organCodi){

		if (organCodi == null) {
			return procedimentRepository.findByEntitat(entitat);
		}
		var organsFills = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitat.getDir3Codi(), organCodi);
		return procedimentRepository.findByOrganGestorCodiInOrComu(organsFills, entitat);
	}

	private List<CodiValorOrganGestorComuDto> recuperarProcedimentAmbPermis(EntitatEntity entitat, PermisEnum permis, String organFiltre) {

		var auth = SecurityContextHolder.getContext().getAuthentication();
		var procediments = permisosService.getProcedimentsAmbPermis(entitat.getId(), auth.getName(), permis);
		if (organFiltre == null) {
			return procediments;
		}
		List<CodiValorOrganGestorComuDto> procedimentsAmbPermis = new ArrayList<>();
		var organsFills = organGestorCachable.getCodisOrgansGestorsFillsByOrgan(entitat.getDir3Codi(), organFiltre);
		for (var procediment: procediments) {
			if (organsFills.contains(procediment.getOrganGestor()) || procediment.isComu()) {
				procedimentsAmbPermis.add(procediment);
			}
		}
		return procedimentsAmbPermis;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean hasAnyProcedimentsWithPermis(Long entitatId, List<String> grups, PermisEnum permis) {

		var timer = metricsHelper.iniciMetrica();
		try {
			var entitat = entityComprovarHelper.comprovarEntitat(entitatId, true, false, false);
			var procediments = procedimentRepository.findProcedimentsByEntitatAndGrup(entitat, grups);
			if (procediments == null || procediments.isEmpty()) {
				return false;
			}
			var o = new ObjectIdentifierExtractor<ProcedimentEntity>() {
				public Long getObjectIdentifier(ProcedimentEntity procediment) {
					return procediment.getId();
				}
			};
			var auth = SecurityContextHolder.getContext().getAuthentication();
			permisosHelper.filterGrantedAny(procediments, o, ProcedimentEntity.class, entityComprovarHelper.getPermissionsFromName(permis), auth);
			return !procediments.isEmpty();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean hasPermisProcediment(Long procedimentId, PermisEnum permis) {

		var timer = metricsHelper.iniciMetrica();
		try {
			return entityComprovarHelper.hasPermisProcediment(procedimentId, permis);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
//	@Transactional(readOnly = true)
//	@Cacheable(value = "procedimentsOrganPermis", key="#entitatId.toString().concat('-').concat(#usuariCodi).concat('-').concat(#permis.name())")
//	@Override
//	public List<ProcSerOrganCacheDto> findProcedimentsOrganWithPermis(Long entitatId, String usuariCodi, PermisEnum permis) {
//
//		var timer = metricsHelper.iniciMetrica();
//		try {
//			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, true, false, false);
//			Permission[] permisos = entityComprovarHelper.getPermissionsFromName(permis);
//			return procedimentsCacheable.getProcedimentOrganWithPermis(auth, entitat, permisos);
//		} finally {
//			metricsHelper.fiMetrica(timer);
//		}
//	}

	@Transactional(readOnly = true)
	@Override
	public List<ProcSerOrganDto> findProcedimentsOrganWithPermisByOrgan(String organGestor, String entitatCodi, List<ProcSerOrganDto> procedimentsOrgans) {
		
		if(procedimentsOrgans == null || procedimentsOrgans.isEmpty()) {
			return new ArrayList<>();
		}
		List<ProcSerOrganDto> procedimentsOrgansAmbPermis = new ArrayList<>();
		var organsFills = organigramaHelper.getCodisOrgansGestorsFillsByOrgan(entitatCodi, organGestor);
		for (var procedimentOrgan: procedimentsOrgans) {
			if (organsFills.contains(procedimentOrgan.getOrganGestor().getCodi())) {
				procedimentsOrgansAmbPermis.add(procedimentOrgan);
			}
		}
		return procedimentsOrgansAmbPermis;
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<String> findProcedimentsOrganCodiWithPermisByProcediment(ProcSerDto procediment, String entitatCodi, List<ProcSerOrganCacheDto> procedimentsOrgans) {
		
		Set<String> organsDisponibles = new HashSet<>();
		if(procedimentsOrgans.isEmpty()) {
			return new ArrayList<>(organsDisponibles);
		}
		for (var procedimentOrgan: procedimentsOrgans) {
			if (!procedimentOrgan.getProcSer().equals(procediment)) {
				continue;
			}
			String organ = procedimentOrgan.getOrganGestor().getCodi();
			if (organsDisponibles.contains(organ)) {
				continue;
			}
			organsDisponibles.addAll(organigramaHelper.getCodisOrgansGestorsFillsByOrgan(entitatCodi, organ));
		}
		return new ArrayList<>(organsDisponibles);
	}


	@Transactional
	@Override
	public List<PermisDto> permisFind(Long entitatId, boolean isAdministrador, Long procedimentId, String organ, String organActual, TipusPermis tipus, PaginacioParamsDto paginacioParams) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta dels permisos del procediment (entitatId=" + entitatId +  ", procedimentId=" + procedimentId + ", tipus=" + tipus + ")");
			EntitatEntity entitat = null;
			if (entitatId != null && !isAdministrador) {
				entitat = entityComprovarHelper.comprovarEntitat(entitatId, false,false,false);
			}
			var procediment = entityComprovarHelper.comprovarProcediment(entitat, procedimentId);
			var adminOrgan = organActual != null;
			List<PermisDto> permisos;
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

	private List<PermisDto> findPermisProcediment(ProcSerEntity procediment, boolean adminOrgan, String organ) {

		var permisos = permisosHelper.findPermisos(procediment.getId(), ProcedimentEntity.class);
		var isAdministradorOrganAndNoComuOrAdminEntitat = (adminOrgan && !procediment.isComu()) || //administrador òrgan i procediment no comú
				(adminOrgan && procediment.isComu() && (procediment.getOrganGestor().getCodi().equals(organ))) ||  //administrador òrgan, procediment comú però del mateix òrgan
				!adminOrgan; //administrador entitat
		for (var permis: permisos) {
			permis.setPermetEdicio(isAdministradorOrganAndNoComuOrAdminEntitat);
		}
		return permisos;
	}
	
	private List<PermisDto> findPermisProcedimentOrganByProcediment(Long procedimentId, String organGestor) {

		var procedimentOrgans = procedimentOrganRepository.findByProcSerId(procedimentId);
		if (procedimentOrgans == null || procedimentOrgans.isEmpty()) {
			return new ArrayList<>();
		}
		List<PermisDto> permisos = new ArrayList<>();
		List<String> organsAmbPermis = new ArrayList<>();
		if (organGestor != null) {
			organsAmbPermis = organigramaHelper.getCodisOrgansGestorsFillsByOrgan(procedimentOrgans.get(0).getProcSer().getEntitat().getDir3Codi(), organGestor);
		}
		List<PermisDto> permisosProcOrgan;
		String organ;
		String organNom;
		boolean tePermis;
		for (var procedimentOrgan: procedimentOrgans) {
			permisosProcOrgan = permisosHelper.findPermisos(procedimentOrgan.getId(), ProcSerOrganEntity.class);
			if (permisosProcOrgan == null || permisosProcOrgan.isEmpty()) {
				continue;
			}
			organ = procedimentOrgan.getOrganGestor().getCodi();
			organNom = procedimentOrgan.getOrganGestor().getNom();
			tePermis = true;

			if (organGestor != null) {
				tePermis = organsAmbPermis.contains(organ);
			}
			if (!tePermis) {
				continue;
			}
			for (var permis : permisosProcOrgan) {
				permis.setOrgan(organ);
				permis.setOrganNom(organNom);
				permis.setPermetEdicio(true);
			}
			permisos.addAll(permisosProcOrgan);
		}
		return permisos;
	}
	
	private List<PermisDto> findPermisProcedimentOrgan(Long procedimentId, String organ, String organActual) {

		var procedimentOrgan = procedimentOrganRepository.findByProcSerIdAndOrganGestorCodi(procedimentId, organ);
		var permisos = permisosHelper.findPermisos(procedimentOrgan.getId(), ProcSerOrganEntity.class);
		for (var permis: permisos) {
			permis.setOrgan(organ);
			permis.setOrganNom(procedimentOrgan.getOrganGestor().getNom());
		}
		return permisos;
	}

	@Transactional
	@Override
	public void permisUpdate(Long entitatId, Long organGestorId, Long id, PermisDto permis) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Modificació del permis del procediment (entitatId=" + entitatId +  ", " + "id=" + id + ", " + "permis=" + permis + ")");
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
			var procediment = entityComprovarHelper.comprovarProcediment(entitatId, id);
			var entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			OrganGestorEntity organGestor = null;
			// Permís a procediment comú no global
			if (procediment.isComu() && permis.getOrgan() != null && !permis.getOrgan().isEmpty() && !entitat.getDir3Codi().equals(permis.getOrgan())) {
				var procedimentOrgan = procedimentOrganRepository.findByProcSerIdAndOrganGestorCodi(procediment.getId(), permis.getOrgan());
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
			permisosService.evictGetOrgansAmbPermis();
			cacheHelper.evictFindOrgansGestorWithPermis();
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

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Eliminació del permis del procediment (entitatId=" + entitatId +  ", " + "procedimentId=" + procedimentId
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

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Modificació del grup del procediment (entitatId=" + entitatId +  ", " + "id=" + id + ", " + "permis=" + procedimentGrup + ")");
			//TODO: en cas de tothom, comprovar que sigui administrador d'Organ i que tant el grup com el procediment son de l'Organ.
			var procediment = entityComprovarHelper.comprovarProcediment(entitatId, id,false,false,false,false, false);
			var grup = entityComprovarHelper.comprovarGrup(procedimentGrup.getGrup().getId());
			var grupProcedimentEntity = GrupProcSerEntity.getBuilder(procediment, grup).build();
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

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Modificació del grup del procediment (entitatId=" + entitatId +  ", " + "id=" + id + ", " + "permis=" + procedimentGrup + ")");
			//TODO: en cas de tothom, comprovar que sigui administrador d'Organ i que tant el grup com el procediment son de l'Organ.
			var procediment = entityComprovarHelper.comprovarProcediment(entitatId, id,false,false,false,false, false);
			var grup = entityComprovarHelper.comprovarGrup(procedimentGrup.getGrup().getId());
			var grupProcedimentEntity = entityComprovarHelper.comprovarGrupProcediment(procedimentGrup.getId());
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

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Modificació del grup del procediment (entitatId=" + entitatId +  ", " + "procedimentGrupID=" + procedimentGrupId + ")");
			//TODO: en cas de tothom, comprovar que sigui administrador d'Organ i que tant el grup com el procediment son de l'Organ.
			entityComprovarHelper.comprovarEntitat(entitatId,false,false,false);
			var grupProcedimentEntity = grupProcedimentRepository.findById(procedimentGrupId).orElseThrow();
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

		var timer = metricsHelper.iniciMetrica();
		try {
			List<TipusAssumpteDto> tipusAssumpte = new ArrayList<>();
			try {
				var tipusAssumpteRegistre = pluginHelper.llistarTipusAssumpte(entitat.getDir3Codi());
				if (tipusAssumpteRegistre == null) {
					return tipusAssumpte;
				}
				for (var assumpteRegistre : tipusAssumpteRegistre) {
					tipusAssumpte.add(TipusAssumpteDto.builder().codi(assumpteRegistre.getCodi()).nom(assumpteRegistre.getNom()).build());
				}
			} catch (SistemaExternException e) {
				String errorMessage = "No s'han pogut recuperar els codis d'assumpte de l'entitat: " + entitat.getDir3Codi();
				log.error(errorMessage, e.getMessage());
			}
			return tipusAssumpte;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CodiAssumpteDto> findCodisAssumpte(EntitatDto entitat, String codiTipusAssumpte) {

		var timer = metricsHelper.iniciMetrica();
		try {
			List<CodiAssumpteDto> codiAssumpte = new ArrayList<>();
			try {
				var tipusAssumpteRegistre = pluginHelper.llistarCodisAssumpte(entitat.getDir3Codi(), codiTipusAssumpte);
				if (tipusAssumpteRegistre == null) {
					return codiAssumpte;
				}
				for (var assumpteRegistre : tipusAssumpteRegistre) {
					codiAssumpte.add(CodiAssumpteDto.builder().codi(assumpteRegistre.getCodi()).nom(assumpteRegistre.getNom()).tipusAssumpte(assumpteRegistre.getTipusAssumpte()).build());
				}
			} catch (SistemaExternException e) {
				String errorMessage = "No s'han pogut recuperar els codis d'assumpte del tipus d'assumpte: " + codiTipusAssumpte;
				log.error(errorMessage, e.getMessage());
			}
			return codiAssumpte;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional(readOnly = true)
	@Override
	public void refrescarCache(EntitatDto entitat) {
		
		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Preparant per buidar la informació en cache dels procediments...");
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
}