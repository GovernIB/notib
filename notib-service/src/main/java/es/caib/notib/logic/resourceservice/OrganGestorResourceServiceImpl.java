package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.*;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.ExtendedPermission;
import es.caib.notib.logic.intf.model.OrganGestorDir3Sync;
import es.caib.notib.logic.intf.model.OrganGestorResource;
import es.caib.notib.logic.intf.model.PagadorPostalResource;
import es.caib.notib.logic.intf.resourceservice.OrganGestorResourceService;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import es.caib.notib.persist.resourceentity.EntregaCieResourceEntity;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;
import es.caib.notib.persist.resourcerepository.EntitatResourceRepository;
import es.caib.notib.persist.resourcerepository.EntregaCieResourceRepository;
import es.caib.notib.persist.resourcerepository.PagadorCieResourceRepository;
import es.caib.notib.persist.resourcerepository.PagadorPostalResourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementació del servei de gestió d'òrgans gestors.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class OrganGestorResourceServiceImpl
	extends BaseAdminEntitatResourceServiceImpl<OrganGestorResource, OrganGestorResourceEntity>
	implements OrganGestorResourceService {

	private final AclHelper aclHelper;
	private final OrganGestorSyncHelper organGestorSyncHelper;
	private final EntitatResourceRepository entitatResourceRepository;
	private final PagadorPostalResourceRepository pagadorPostalResourceRepository;
	private final PagadorCieResourceRepository pagadorCieResourceRepository;
	private final EntregaCieResourceRepository entregaCieResourceRepository;

	public OrganGestorResourceServiceImpl(
		UserSessionHelper userSessionHelper,
		AuthenticationHelper authenticationHelper,
		NotibPermissionHelper notibPermissionHelper,
		AclHelper aclHelper,
		OrganGestorSyncHelper organGestorSyncHelper,
		EntitatResourceRepository entitatResourceRepository, PagadorPostalResourceRepository pagadorPostalResourceRepository, PagadorCieResourceRepository pagadorCieResourceRepository, EntregaCieResourceRepository entregaCieResourceRepository) {

		super(userSessionHelper, authenticationHelper, notibPermissionHelper);
		this.aclHelper = aclHelper;
		this.organGestorSyncHelper = organGestorSyncHelper;
		this.entitatResourceRepository = entitatResourceRepository;
		this.pagadorPostalResourceRepository = pagadorPostalResourceRepository;
		this.pagadorCieResourceRepository = pagadorCieResourceRepository;
		this.entregaCieResourceRepository = entregaCieResourceRepository;
	}

	@PostConstruct
	public void init() {
		register(OrganGestorResource.DIR3_SYNC_ACTION_CODE, new Dir3SyncActionExecutor());
	}

	/*
	 * Si l'usuari actual no és un superadministrador, només es mostren els recursos amb la mateixa entitat que la
	 * seleccionada a la sessió.
	 */
	@Override
	protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {

		var superFilter = super.additionalSpringFilter(currentSpringFilter, namedQueries);
		var isRoleAdmin = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN);
		if (isRoleAdmin) {
			return superFilter;
		}
		var filter = superFilter;
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
	protected void beforeUpdateSave(OrganGestorResourceEntity entity, OrganGestorResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {

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

	public class Dir3SyncActionExecutor implements ActionExecutor<OrganGestorResourceEntity, OrganGestorResource.OrganGestorDir3SyncForm, OrganGestorDir3Sync> {

		@Override
		public OrganGestorDir3Sync exec(String code, OrganGestorResourceEntity entity, OrganGestorResource.OrganGestorDir3SyncForm params) throws ActionExecutionException {

			var entitat = entitatResourceRepository.findById(userSessionHelper.getCurrentEntitatId());
			if (!entitat.isPresent()) {
				throw new ActionExecutionException(OrganGestorResource.class, null, code, "Couldn't find current entitat in user session");
			}
			return organGestorSyncHelper.sincronitzar(entitat.get(), params.getSimular() != null && params.getSimular());
		}

		@Override
		public void onChange(Serializable id, OrganGestorResource.OrganGestorDir3SyncForm previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, OrganGestorResource.OrganGestorDir3SyncForm target) {
		}
	}

	private String addIdsWithPermissionFilterExpression(Permission permission, String filter) {

		var ids = notibPermissionHelper.organGestorIdsWithPermissionRecursive(permission);
		if (ids.isEmpty()) {
			return filter;
		}
		var joinedIds = ids.stream().map(Object::toString).collect(Collectors.joining(","));
		return concatenaFiltresAnd(filter, "id in (" + joinedIds + ")");
	}

	private String concatenaFiltresAnd(String... filtres) {

		return Arrays.stream(filtres).
			filter(f -> f != null && !f.isEmpty()).
			map(f -> "(" + f + ")").
			collect(Collectors.joining(" and "));
	}

}
