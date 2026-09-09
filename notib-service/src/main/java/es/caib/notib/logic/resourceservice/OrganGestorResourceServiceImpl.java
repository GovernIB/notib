package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.AclHelper;
import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.helper.OrganGestorSyncHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.PerspectiveApplicationException;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.ExtendedPermission;
import es.caib.notib.logic.intf.model.OrganGestorDir3Sync;
import es.caib.notib.logic.intf.model.OrganGestorResource;
import es.caib.notib.logic.intf.resourceservice.OrganGestorResourceService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.logic.organs.AdminOrgansAmbPermisActionExecutor;
import es.caib.notib.logic.organs.OficinesSyncActionExecutor;
import es.caib.notib.logic.organs.OrganGestorDir3SyncJsonReportGenerator;
import es.caib.notib.persist.resourceentity.EntregaCieResourceEntity;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;
import es.caib.notib.persist.resourcerepository.EntitatResourceRepository;
import es.caib.notib.persist.resourcerepository.EntregaCieResourceRepository;
import es.caib.notib.persist.resourcerepository.OrganGestorResourceRepository;
import es.caib.notib.persist.resourcerepository.PagadorCieResourceRepository;
import es.caib.notib.persist.resourcerepository.PagadorPostalResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementació del servei de gestió d'òrgans gestors.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class OrganGestorResourceServiceImpl extends BaseAdminEntitatResourceServiceImpl<OrganGestorResource, OrganGestorResourceEntity> implements OrganGestorResourceService {

	private final AclHelper aclHelper;
	private final OrganGestorSyncHelper organGestorSyncHelper;
	private final EntitatResourceRepository entitatResourceRepository;
	private final OrganGestorResourceRepository organGestorResourceRepository;
	private final PagadorPostalResourceRepository pagadorPostalResourceRepository;
	private final PagadorCieResourceRepository pagadorCieResourceRepository;
	private final EntregaCieResourceRepository entregaCieResourceRepository;
	private final OrganGestorService organGestorService;

	public OrganGestorResourceServiceImpl(
		UserSessionHelper userSessionHelper,
		AuthenticationHelper authenticationHelper,
		NotibPermissionHelper notibPermissionHelper,
		AclHelper aclHelper,
		OrganGestorSyncHelper organGestorSyncHelper,
		EntitatResourceRepository entitatResourceRepository,
		OrganGestorResourceRepository organGestorResourceRepository,
		PagadorPostalResourceRepository pagadorPostalResourceRepository,
		PagadorCieResourceRepository pagadorCieResourceRepository,
		EntregaCieResourceRepository entregaCieResourceRepository,
		OrganGestorService organGestorService) {

		super(userSessionHelper, authenticationHelper, notibPermissionHelper);
		this.aclHelper = aclHelper;
		this.organGestorSyncHelper = organGestorSyncHelper;
		this.entitatResourceRepository = entitatResourceRepository;
		this.organGestorResourceRepository = organGestorResourceRepository;
		this.pagadorPostalResourceRepository = pagadorPostalResourceRepository;
		this.pagadorCieResourceRepository = pagadorCieResourceRepository;
		this.entregaCieResourceRepository = entregaCieResourceRepository;
		this.organGestorService = organGestorService;
	}

	@PostConstruct
	public void init() {
		var resourceClass = getResourceClass();
		register(OrganGestorResource.PERSPECTIVE_TREE, new OrganGestorResourceTreePerspectiveApplicator());
		register(OrganGestorResource.DIR3_SYNC_ACTION_CODE, new Dir3SyncActionExecutor());
		register(OrganGestorResource.OFICINES_SYNC_ACTION_CODE, new OficinesSyncActionExecutor(entitatResourceRepository, userSessionHelper, organGestorService, resourceClass));
		register(OrganGestorResource.ACTION_ADMIN_ORGANS_AMB_PERMIS, new AdminOrgansAmbPermisActionExecutor(organGestorService, userSessionHelper));
		register(OrganGestorResource.REPORT_DESCARREGAR_DIR3_JSON, new OrganGestorDir3SyncJsonReportGenerator(organGestorService, userSessionHelper, authenticationHelper));
	}

	/*
	 * Si l'usuari actual no és un superadministrador, només es mostren els recursos amb la mateixa entitat que la
	 * seleccionada a la sessió.
	 */
	@Override
	protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {

		var superFilter = super.additionalSpringFilter(currentSpringFilter, namedQueries);
		var isRoleAdmin = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN);
		var isRoleAdminLectura = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN_LECTURA);
		var isRoleAdminOrgan = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ORGAN);
		if (isRoleAdminOrgan && notibPermissionHelper.currentOrganGestorPermissionAllowed(BasePermission.ADMINISTRATION)) {
			var currentOrganGestorId = userSessionHelper.getCurrentOrganGestorId();
			return superFilter + " and id: " + currentOrganGestorId;
		}
		if (isRoleAdmin || isRoleAdminLectura || isRoleAdminOrgan) {
			return superFilter;
		}
		String filter = superFilter;
		if (Arrays.asList(namedQueries).contains(OrganGestorResource.NAMED_QUERY_PERM_READ)) {
			filter = addIdsWithPermissionFilterExpression(ExtendedPermission.READ, filter);
		}
		if (Arrays.asList(namedQueries).contains(OrganGestorResource.NAMED_QUERY_PERM_NOT)) {
			filter = addIdsWithPermissionFilterExpression(ExtendedPermission.PERM4, filter);
		}
		if (Arrays.asList(namedQueries).contains(OrganGestorResource.NAMED_QUERY_PERM_COM)) {
			filter = addIdsWithPermissionFilterExpression(ExtendedPermission.PERM5, filter);
		}
		if (Arrays.asList(namedQueries).contains(OrganGestorResource.NAMED_QUERY_PERM_SIR)) {
			filter = addIdsWithPermissionFilterExpression(ExtendedPermission.PERM6, filter);
		}
		return filter;
	}

	@Override
	protected void afterConversion(OrganGestorResourceEntity entity, OrganGestorResource resource) {

		resource.setAclEntryCount(aclHelper.count(AclHelper.ORGAN_GESTOR_CLASS, entity.getId(), null));
		if (entity.getEntregaCie() == null) {
			return;
		}
		var pagadorCie = entity.getEntregaCie().getPagadorCie();
		resource.setEntregaCiePagadorCie(ResourceReference.toResourceReference(pagadorCie.getId(), pagadorCie.getNom()));
		var pagadorPostal = entity.getEntregaCie().getPagadorPostal();
		resource.setEntregaCiePagadorPostal(ResourceReference.toResourceReference(pagadorPostal.getId(), pagadorPostal.getNomContracteNum()));
	}

	@Override
	protected void beforeCreateSave(OrganGestorResourceEntity entity, OrganGestorResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
		beforeCreateUpdate(entity, resource);
	}

	@Override
	protected void beforeUpdateSave(OrganGestorResourceEntity entity, OrganGestorResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
		beforeCreateUpdate(entity, resource);
	}

	/**
	 * Acció per a sincronitzar els òrgans gestors amb la informació de DIR3.
	 */
	public class Dir3SyncActionExecutor implements ActionExecutor<OrganGestorResourceEntity, OrganGestorResource.OrganGestorDir3SyncForm, OrganGestorDir3Sync> {

		@Override
		public OrganGestorDir3Sync exec(String code, OrganGestorResourceEntity entity, OrganGestorResource.OrganGestorDir3SyncForm params) throws ActionExecutionException {

			var entitat = entitatResourceRepository.findById(userSessionHelper.getCurrentEntitatId());
			if (entitat.isEmpty()) {
				throw new ActionExecutionException(OrganGestorResource.class, null, code, "Couldn't find current entitat in user session");
			}
			try {
				return organGestorSyncHelper.sincronitzar(entitat.get(), params.getSimular() != null && params.getSimular());
			} catch (Exception ex) {
				var msg = "Error al sincronitzar les unitats DIR3";
				log.error(msg, ex);
				msg += ex.getMessage();
				throw new ActionExecutionException(getResourceClass(), entity != null ? entity.getId() : null, code, msg, ex);
			}
		}
		@Override
		public void onChange(Serializable id, OrganGestorResource.OrganGestorDir3SyncForm previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, OrganGestorResource.OrganGestorDir3SyncForm target) {
		}
	}

	/**
	 * Perspectiva per a emplenar els camps per a mostrar els òrgans en forma d'arbre.
	 */
	@RequiredArgsConstructor
	public class OrganGestorResourceTreePerspectiveApplicator implements PerspectiveApplicator<OrganGestorResourceEntity, OrganGestorResource> {

		@Override
		public void applySingle(String code, OrganGestorResourceEntity entity, OrganGestorResource resource) throws PerspectiveApplicationException {

			List<Object[]> paresAll = organGestorResourceRepository.findParesByEntitatIdAndId(resource.getEntitat().getId(), null);
			emplenarCamps(paresAll, resource);
		}

		@Override
		public boolean applyMultiple(String code, List<OrganGestorResourceEntity> entities, List<OrganGestorResource> resources) throws PerspectiveApplicationException {

			if (resources.isEmpty()) {
			return true;
			}
			List<Object[]> paresAll = organGestorResourceRepository.findParesByEntitatIdAndId(resources.get(0).getEntitat().getId(), null);
			for (var resource: resources) {
				emplenarCamps(paresAll, resource);
			}
			return true;
		}

		private void emplenarCamps(List<Object[]> paresAll, OrganGestorResource resource) {

			List<Object[]> paresResource = paresAll.stream().filter(p -> ((Number)p[0]).longValue() == resource.getId()).
				collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
						Collections.reverse(list);
						return list;
					}
				));
			ResourceReference<?, ?>[] path = paresResource.stream().map(p ->
				ResourceReference.toResourceReference(((Number)p[1]).longValue(), p[2] + ", " + p[3])).toArray(ResourceReference[]::new);
			resource.setPath((ResourceReference<OrganGestorResource, Long>[]) path);
			resource.setChildCount(paresAll.stream().filter(p -> ((Number)p[1]).longValue() == resource.getId()).count() - 1);
		}
	}

	private void beforeCreateUpdate(OrganGestorResourceEntity entity, OrganGestorResource resource) {

		// Gestiona el codi del pare
		entity.setCodiPare(entity.getPare() != null ? entity.getPare().getCodi() : null);
		// Gestiona la entrega CIE
		if (!resource.isEntregaCieActiva()) {
			if (entity.getEntregaCie() != null) {
				entregaCieResourceRepository.delete(entity.getEntregaCie());
				entity.setEntregaCie(null);
			}
			return;
		}
		if (resource.getEntregaCiePagadorPostal() == null || resource.getEntregaCiePagadorCie() == null) {
			return;
		}
		var pagadorPostal = pagadorPostalResourceRepository.findById(resource.getEntregaCiePagadorPostal().getId());
		var pagadorCie = pagadorCieResourceRepository.findById(resource.getEntregaCiePagadorCie().getId());
		if (pagadorPostal.isEmpty() || pagadorCie.isEmpty()) {
			return;
		}
		if (entity.getEntregaCie() == null) {
			var entregaCie = EntregaCieResourceEntity.builder().pagadorPostal(pagadorPostal.get()).pagadorCie(pagadorCie.get()).build();
			entity.setEntregaCie(entregaCieResourceRepository.save(entregaCie));
			return;
		}
		entity.getEntregaCie().setPagadorPostal(pagadorPostal.get());
		entity.getEntregaCie().setPagadorCie(pagadorCie.get());
	}

	private String addIdsWithPermissionFilterExpression(Permission permission, String filter) {

		List<Long> ids = notibPermissionHelper.organGestorIdsWithPermissionRecursive(permission);
		if (ids.isEmpty()) {
			return filter;
		}
		String joinedIds = ids.stream().map(Object::toString).collect(Collectors.joining(","));
		return concatenaFiltresAnd(filter, "id in (" + joinedIds + ")");
	}

	private String concatenaFiltresAnd(String... filtres) {
		return Arrays.stream(filtres).filter(f -> f != null && !f.isEmpty()).map(f -> "(" + f + ")").collect(Collectors.joining(" and "));
	}

}
