package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.AclHelper;
import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.helper.PaginacioHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.ExtendedPermission;
import es.caib.notib.logic.intf.dto.CodiValorOrganGestorComuDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.model.ProcedimentResource;
import es.caib.notib.logic.intf.resourceservice.ProcedimentResourceService;
import es.caib.notib.logic.intf.service.GrupService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.logic.intf.service.ProcedimentService;
import es.caib.notib.logic.intf.service.ServeiService;
import es.caib.notib.logic.procSer.ComuOnChangeLogicProcessor;
import es.caib.notib.logic.procediments.DadesProcedimentActionExecutor;
import es.caib.notib.logic.procediments.NetejerCacheActionExecutor;
import es.caib.notib.logic.procediments.ProcedimentActivarActionExecutor;
import es.caib.notib.logic.procediments.ProcedimentActualitzarActionExecutor;
import es.caib.notib.logic.procediments.ProcedimentDesactivarActionExecutor;
import es.caib.notib.logic.procediments.ProcedimentSyncAutoActionExecutor;
import es.caib.notib.logic.procediments.ProcedimentSyncManualActionExecutor;
import es.caib.notib.logic.procediments.ProcedimentsSyncActionExecutor;
import es.caib.notib.logic.procediments.ServeisSyncActionExecutor;
import es.caib.notib.persist.resourceentity.EntregaCieResourceEntity;
import es.caib.notib.persist.resourceentity.ProcedimentResourceEntity;
import es.caib.notib.persist.resourcerepository.EntitatResourceRepository;
import es.caib.notib.persist.resourcerepository.EntregaCieResourceRepository;
import es.caib.notib.persist.resourcerepository.OrganGestorResourceRepository;
import es.caib.notib.persist.resourcerepository.PagadorCieResourceRepository;
import es.caib.notib.persist.resourcerepository.PagadorPostalResourceRepository;
import es.caib.notib.persist.resourcerepository.ProcedimentOrganGestorResourceRepository;
import es.caib.notib.persist.resourcerepository.ProcedimentResourceRepository;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implementació del servei de gestió de procediments.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class ProcedimentResourceServiceImpl extends BaseAdminEntitatResourceServiceImpl<ProcedimentResource, ProcedimentResourceEntity> implements ProcedimentResourceService {

	private final AclHelper aclHelper;
	private final PagadorPostalResourceRepository pagadorPostalResourceRepository;
	private final EntitatResourceRepository entitatResourceRepository;
	private final PagadorCieResourceRepository pagadorCieResourceRepository;
	private final EntregaCieResourceRepository entregaCieResourceRepository;
	private final ProcedimentResourceRepository procedimentResourceRepository;
	private final OrganGestorResourceRepository organGestorResourceRepository;
	private final ProcedimentOrganGestorResourceRepository procedimentOrganGestorResourceRepository;
	private final PaginacioHelper paginacioHelper;
	private final ProcedimentService procedimentService;
	private final ServeiService serveiService;
	private final OrganGestorService organGestorService;
	private final GrupService grupService;

	@PostConstruct
	public void init() {

		var resourceClass = getResourceClass();
		register(ProcedimentResource.PROCEDIMENTS_SYNC_ACTION_CODE, new ProcedimentsSyncActionExecutor(entitatResourceRepository, userSessionHelper, procedimentService, resourceClass));
		register(ProcedimentResource.SERVEIS_SYNC_ACTION_CODE, new ServeisSyncActionExecutor(entitatResourceRepository, userSessionHelper, serveiService, resourceClass));
		register(ProcedimentResource.PROCEDIMENTS_NETEJAR_CACHE_ACTION_CODE, new NetejerCacheActionExecutor(entitatResourceRepository, userSessionHelper, procedimentService, resourceClass));
		register(ProcedimentResource.PROCEDIMENT_ACTIVAR_ACTION_CODE, new ProcedimentActivarActionExecutor(procedimentService));
		register(ProcedimentResource.PROCEDIMENT_DESACTIVAR_ACTION_CODE, new ProcedimentDesactivarActionExecutor(procedimentService));
		register(ProcedimentResource.PROCEDIMENT_ACTUALITZAR_ACTION_CODE, new ProcedimentActualitzarActionExecutor(entitatResourceRepository, userSessionHelper, procedimentService));
		register(ProcedimentResource.PROCEDIMENT_SYNC_MANUAL_ACTION_CODE, new ProcedimentSyncManualActionExecutor(procedimentService));
		register(ProcedimentResource.PROCEDIMENT_SYNC_AUTO_ACTION_CODE, new ProcedimentSyncAutoActionExecutor(procedimentService));
		register(ProcedimentResource.PROCEDIMENT_DADES_PROCEDIMENT_ACTION_CODE, new DadesProcedimentActionExecutor(organGestorService, grupService, userSessionHelper));
		register(ProcedimentResource.Fields.comu, new ComuOnChangeLogicProcessor(organGestorResourceRepository, userSessionHelper));

	}

	public ProcedimentResourceServiceImpl(
		UserSessionHelper userSessionHelper,
		AuthenticationHelper authenticationHelper,
		NotibPermissionHelper notibPermissionHelper,
		AclHelper aclHelper,
		PagadorPostalResourceRepository pagadorPostalResourceRepository,
		EntitatResourceRepository entitatResourceRepository,
		PagadorCieResourceRepository pagadorCieResourceRepository,
		EntregaCieResourceRepository entregaCieResourceRepository,
		ProcedimentResourceRepository procedimentResourceRepository,
		OrganGestorResourceRepository organGestorResourceRepository,
		ProcedimentOrganGestorResourceRepository procedimentOrganGestorResourceRepository,
		PaginacioHelper paginacioHelper,
		ProcedimentService procedimentService,
		ServeiService serveiService,
		OrganGestorService organGestorService,
		GrupService grupService) {

		super(userSessionHelper, authenticationHelper, notibPermissionHelper);
		this.aclHelper = aclHelper;
		this.pagadorPostalResourceRepository = pagadorPostalResourceRepository;
		this.entitatResourceRepository = entitatResourceRepository;
		this.pagadorCieResourceRepository = pagadorCieResourceRepository;
		this.entregaCieResourceRepository = entregaCieResourceRepository;
		this.procedimentResourceRepository = procedimentResourceRepository;
		this.organGestorResourceRepository = organGestorResourceRepository;
		this.procedimentOrganGestorResourceRepository = procedimentOrganGestorResourceRepository;
		this.paginacioHelper = paginacioHelper;
		this.procedimentService = procedimentService;
		this.serveiService = serveiService;
		this.organGestorService = organGestorService;
		this.grupService = grupService;
	}

	@Override
	protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {

		var superFilter = super.additionalSpringFilter(currentSpringFilter, namedQueries);
		var isRoleAdminOrgan = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ORGAN);
		if (isRoleAdminOrgan && notibPermissionHelper.currentOrganGestorPermissionAllowed(BasePermission.ADMINISTRATION)) {
			var permisComuns = notibPermissionHelper.currentOrganGestorPermissionAllowed(ExtendedPermission.PERM3);
			superFilter += " and (organGestor.id:" + userSessionHelper.getCurrentOrganGestorId()
							+ (permisComuns ? " or comu:true)" : "");
		}
		return superFilter;
	}

	@Override
	protected void completeResource(ProcedimentResource resource) {

//		if (!resource.isComu()) {
//			return;
//		}
//		var entitat = userSessionHelper.getCurrentEntitat();
//
//		resource.setOrganGestor(null);
	}

	@Override
	protected Page<ProcedimentResourceEntity> entityRepositoryFindEntities(String quickFilter, String filter, String[] namedQueries, Pageable pageable) {

		var userRoles = authenticationHelper.getCurrentUserRoles();
		if (userRoles == null || userRoles.length == 0) {
			return new PageImpl<>(new ArrayList<>(), pageable, 0);
		}
		var entitat = userSessionHelper.getCurrentEntitatId();
		Sort processedSort = toProcessedSort(pageable.getSort());
		List<ProcedimentResourceEntity> procediments;
		if (authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN) || authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN_LECTURA)) {
			Specification<ProcedimentResourceEntity> specification = toFindProcessedSpecification(quickFilter, filter, namedQueries);
			procediments = procedimentResourceRepository.findAll(specification, processedSort);
		} else if (authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ORGAN)) {
			var organ = userSessionHelper.getCurrentOrganGestor();
			if (organ != null) {
				// "organGestor" (sense ".id") és una propietat d'entitat (ManyToOne): spring-filter
				// intenta convertir el literal a un OrganGestorResourceEntity i falla amb
				// InternalFilterException. Cal filtrar per l'id, no per l'entitat sencera.
				filter += " and organGestor.id: " + organ.getId();
			}
			Specification<ProcedimentResourceEntity> specification = toFindProcessedSpecification(quickFilter, filter, namedQueries);
			procediments = procedimentResourceRepository.findAll(specification, processedSort);
		} else if (authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_USER)) {
			var isProcediment = !Strings.isNullOrEmpty(filter) && filter.contains("tipus:'PROCEDIMENT'");
			var isServei = filter.contains("tipus:'SERVEI'");
			Long filtreOrgan = null;
			try {
				var organId = Optional.ofNullable(Pattern.compile("organGestor\\s*:\\s*(\\d+)").matcher(filter)).filter(Matcher::find).map(matcher -> Long.parseLong(matcher.group(1)));
				filtreOrgan = organId.orElse(null);
			} catch (Exception ex) {
				log.error("[ProcedimentResourceServiceImpl.entityRepositoryFindEntities] Error aplicant el filtre organGestor id " + filter, ex);
			}
			var codisValor = isProcediment ? procedimentService.getProcedimentsOrgan(entitat, null, filtreOrgan, RolEnumDto.valueOf(BaseConfig.ROLE_USER), PermisEnum.CONSULTA)
								: isServei ? serveiService.getServeisOrgan(entitat, null, filtreOrgan, RolEnumDto.valueOf(BaseConfig.ROLE_USER), PermisEnum.CONSULTA) : null;
			if (codisValor == null) {
				return new PageImpl<>(new ArrayList<>(), pageable, 0);
			}
			procediments = procedimentResourceRepository.findAllById(codisValor.stream().map(CodiValorOrganGestorComuDto::getId).collect(Collectors.toList()));
			// Al desplegable de l'alta de notificacions/remeses (que demana explícitament "actiu:true") no s'han de
			// mostrar els procediments/serveis inactius; als filtres de cerca es mantenen visibles per poder
			// consultar notificacions antigues.
			if (!Strings.isNullOrEmpty(filter) && filter.contains("actiu:true")) {
				procediments = procediments.stream().filter(ProcedimentResourceEntity::isActiu).collect(Collectors.toList());
			}
			if (!Strings.isNullOrEmpty(quickFilter)) {
				// Cal fer minúscules també el quickFilter (no només el nom): si l'usuari escriu alguna
				// majúscula (p.ex. "Llicència") mai coincidia amb el nom ja convertit a minúscules. I cal
				// dividir per paraules i exigir que hi siguin totes (en qualsevol ordre/posició), no que hi
				// aparegui tot el text cercat com un únic substring contigu: amb noms llargs de procediment,
				// cercar per diverses paraules del nom no trobava mai res.
				var quickFilterTokens = quickFilter.toLowerCase().trim().split("\\s+");
				procediments = procediments.stream().
						filter(p -> !Strings.isNullOrEmpty(p.getNom())).
						filter(p -> {
							var nom = p.getNom().toLowerCase();
							return Arrays.stream(quickFilterTokens).allMatch(nom::contains);
						}).
						collect(Collectors.toList());
			}
		} else {
			return new PageImpl<>(new ArrayList<>(), pageable, 0);
		}
		if (pageable.isUnpaged()) {
			return new PageImpl<>(procediments, pageable, procediments.size());
		}
		Pageable processedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), processedSort);
		return paginacioHelper.toPage(procediments, processedPageable);
	}

	@Override
	protected void afterConversion(ProcedimentResourceEntity entity, ProcedimentResource resource) {

		var count = aclHelper.count(AclHelper.PROCEDIMENT_CLASS, resource.getId(), null);
		if (resource.isComu()) {
			var procSerIds = procedimentOrganGestorResourceRepository.findProcOrganIdByProcediment(resource.getId());
			for (var procSerId : procSerIds) {
				count += aclHelper.count(AclHelper.PROCEDIMENT_ORGAN_CLASS, procSerId, null);
			}
		}
		resource.setAclEntryCount(count);
		if (entity.getEntregaCie() == null) {
			return;
		}
		var pagadorCie = entity.getEntregaCie().getPagadorCie();
		resource.setEntregaCiePagadorCie(ResourceReference.toResourceReference(pagadorCie.getId(), pagadorCie.getNom()));
		var pagadorPostal = entity.getEntregaCie().getPagadorPostal();
		resource.setEntregaCiePagadorPostal(ResourceReference.toResourceReference(pagadorPostal.getId(), pagadorPostal.getNomContracteNum()));
	}

	@Override
	protected void beforeUpdateSave(ProcedimentResourceEntity entity, ProcedimentResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {

		if (!resource.isEntregaCieActiva()) {
			entity.setEntregaCie(null);
			return;
		}
		if (resource.getEntregaCiePagadorPostal() == null || resource.getEntregaCiePagadorCie() == null) {
			return;
		}
		var entregaCie = entity.getEntregaCie();
		var pagadorPostal = pagadorPostalResourceRepository.findById(resource.getEntregaCiePagadorPostal().getId());
		var pagadorCie = pagadorCieResourceRepository.findById(resource.getEntregaCiePagadorCie().getId());
		if (pagadorPostal.isEmpty() || pagadorCie.isEmpty()) {
			return;
		}
		if (entregaCie == null) {
			entregaCie = EntregaCieResourceEntity.builder().build();
			entity.setEntregaCie(entregaCie);
		}
		entregaCie.setPagadorPostal(pagadorPostal.get());
		entregaCie.setPagadorCie(pagadorCie.get());
		entregaCieResourceRepository.save(entregaCie);
	}

	@Override
	protected void beforeDelete(ProcedimentResourceEntity entity, Map<String, AnswerRequiredException.AnswerValue> answers) {

		var procSerOrgans = procedimentOrganGestorResourceRepository.findProcOrganIdByProcediment(entity.getId());
		procedimentOrganGestorResourceRepository.deleteAllById(procSerOrgans);
	}

	@Override
	public boolean validarCodiNoRepetit(Long id, String codi) {

		var procediments =  id != null ? procedimentResourceRepository.findByIdNotLikeAndCodi(id, codi) : procedimentResourceRepository.findByCodi(codi);
		return procediments.isEmpty();
	}

	@Override
	public boolean validarNomNoRepetit(Long id, String nom) {

		var procediments =  id != null ? procedimentResourceRepository.findByIdNotLikeAndNom(id, nom) : procedimentResourceRepository.findByNom(nom);
		return procediments.isEmpty();
	}
}
