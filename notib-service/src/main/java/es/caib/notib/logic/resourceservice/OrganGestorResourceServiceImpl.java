package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.AclHelper;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.helper.OrganGestorFullSyncHelper;
import es.caib.notib.logic.helper.OrganGestorSyncHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.PerspectiveApplicationException;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.ExtendedPermission;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.model.OrganGestorDir3Sync;
import es.caib.notib.logic.intf.model.OrganGestorResource;
import es.caib.notib.logic.intf.model.SseEvent;
import es.caib.notib.logic.intf.resourceservice.OrganGestorResourceService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.logic.organs.AdminOrgansAmbPermisActionExecutor;
import es.caib.notib.logic.organs.OficinesSyncActionExecutor;
import es.caib.notib.logic.organs.OrganGestorDir3SyncJsonReportGenerator;
import es.caib.notib.logic.organs.OrgansProcedimentsSyncActionExecutor;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
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
import java.util.Set;
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
	private final OrganGestorFullSyncHelper organGestorFullSyncHelper;
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
		OrganGestorFullSyncHelper organGestorFullSyncHelper,
		EntitatResourceRepository entitatResourceRepository,
		OrganGestorResourceRepository organGestorResourceRepository,
		PagadorPostalResourceRepository pagadorPostalResourceRepository,
		PagadorCieResourceRepository pagadorCieResourceRepository,
		EntregaCieResourceRepository entregaCieResourceRepository,
		OrganGestorService organGestorService) {

		super(userSessionHelper, authenticationHelper, notibPermissionHelper);
		this.aclHelper = aclHelper;
		this.organGestorSyncHelper = organGestorSyncHelper;
		this.organGestorFullSyncHelper = organGestorFullSyncHelper;
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
		register(OrganGestorResource.ORGANS_PROCEDIMENTS_SYNC_ACTION_CODE, new OrgansProcedimentsSyncActionExecutor(entitatResourceRepository, userSessionHelper, organGestorFullSyncHelper, resourceClass));
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
			return concatenaFiltresAnd(superFilter, entitatRootDescendantsFilterExpression());
		}
		String filter = superFilter;
		var namedQueriesList = Arrays.asList(namedQueries);
		if (namedQueriesList.contains(OrganGestorResource.NAMED_QUERY_PERM_READ) || namedQueriesList.contains(OrganGestorResource.NAMED_QUERY_PERM_READ_VIGENT)) {
			filter = addIdsWithPermissionFilterExpression(ExtendedPermission.READ, filter);
			if (namedQueriesList.contains(OrganGestorResource.NAMED_QUERY_PERM_READ_VIGENT)) {
				// Al desplegable de l'alta de notificacions/remeses no s'han de mostrar els òrgans no vigents;
				// als filtres de cerca (NAMED_QUERY_PERM_READ) es mantenen visibles per poder consultar
				// notificacions antigues.
				filter = concatenaFiltresAnd(filter, "estat: '" + OrganGestorEstatEnum.V.name() + "'");
			}
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
		return concatenaFiltresAnd(filter, entitatRootDescendantsFilterExpression());
	}

	/*
	 * Restringeix el llistat d'òrgans gestors únicament a l'òrgan arrel de l'entitat actual (el que té el
	 * mateix codi que el codiDir3 de l'entitat) i als seus descendents. Els òrgans que no en depenen (p.ex.
	 * òrgans obsolets sense pare real conegut a DIR3) no s'han de mostrar mai.
	 */
	private String entitatRootDescendantsFilterExpression() {

		var entitatId = userSessionHelper.getCurrentEntitatId();
		var dir3Codi = entitatDir3Codi(entitatId);
		if (dir3Codi == null) {
			return null;
		}
		List<Object[]> paresAll = organGestorResourceRepository.findParesByEntitatIdAndId(entitatId, null);
		Set<Long> eligibleIds = paresAll.stream().
			collect(Collectors.groupingBy(p -> ((Number)p[0]).longValue())).
			entrySet().stream().
			filter(e -> e.getValue().stream().anyMatch(p -> dir3Codi.equals(p[2]))).
			map(Map.Entry::getKey).
			collect(Collectors.toSet());
		if (eligibleIds.isEmpty()) {
			return "id: -1";
		}
		return "id in (" + eligibleIds.stream().map(Object::toString).collect(Collectors.joining(",")) + ")";
	}

	private String entitatDir3Codi(Long entitatId) {

		if (entitatId == null) {
			return null;
		}
		return entitatResourceRepository.findById(entitatId).map(EntitatResourceEntity::getDir3Codi).orElse(null);
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
				boolean simular = params.getSimular() != null && params.getSimular();
				if (simular) {
					// La previsualització només mostra els canvis d'òrgans pendents (és l'únic pas
					// amb un "diff" gràfic). terminal=false: no ha de tancar el flux SSE (emitter.
					// complete() al DONE), perquè l'usuari revisa els canvis i després fa una segona
					// crida (aquesta vegada la sincronització completa) des del mateix diàleg; si es
					// tanqués aquí, el frontend hauria de reconnectar l'EventSource i podria perdre's
					// el progrés de la sincronització real.
					return organGestorSyncHelper.sincronitzar(entitat.get(), true, SseEvent.SseEventName.DIR3_SYNC, false);
				}
				// Un cop confirmada la previsualització, la sincronització real no es limita als
				// òrgans: s'executa la sincronització completa (òrgans, permisos, procediments,
				// serveis i oficines SIR), publicant el progrés sota el mateix event DIR3_SYNC que ja
				// escolta el frontend des de la previsualització.
				ConfigHelper.setEntitatCodi(entitat.get().getCodi());
				return organGestorFullSyncHelper.sincronitzarTot(entitat.get(), SseEvent.SseEventName.DIR3_SYNC);
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

			var entitatId = resource.getEntitat().getId();
			List<Object[]> paresAll = organGestorResourceRepository.findParesByEntitatIdAndId(entitatId, null);
			emplenarCamps(paresAll, resource, entitatDir3Codi(entitatId));
		}

		@Override
		public boolean applyMultiple(String code, List<OrganGestorResourceEntity> entities, List<OrganGestorResource> resources) throws PerspectiveApplicationException {

			if (resources.isEmpty()) {
			return true;
			}
			var entitatId = resources.get(0).getEntitat().getId();
			List<Object[]> paresAll = organGestorResourceRepository.findParesByEntitatIdAndId(entitatId, null);
			var entitatDir3Codi = entitatDir3Codi(entitatId);
			for (var resource: resources) {
				emplenarCamps(paresAll, resource, entitatDir3Codi);
			}
			return true;
		}

		private void emplenarCamps(List<Object[]> paresAll, OrganGestorResource resource, String entitatDir3Codi) {

			List<Object[]> paresResource = paresAll.stream().filter(p -> ((Number)p[0]).longValue() == resource.getId()).
				collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
						Collections.reverse(list);
						return list;
					}
				));
			// Talla la cadena d'ancestres a l'òrgan arrel de l'entitat (el que té el codi DIR3 de l'entitat), per
			// evitar que a l'arbre aparegui per damunt seu un pare "artificial" (p.ex. un òrgan obsolet usat com
			// a pare provisional a les dades de DIR3 per a òrgans sense pare real conegut).
			if (entitatDir3Codi != null) {
				var rootIndex = -1;
				for (var i = 0; i < paresResource.size(); i++) {
					if (entitatDir3Codi.equals(paresResource.get(i)[2])) {
						rootIndex = i;
						break;
					}
				}
				if (rootIndex > 0) {
					paresResource = paresResource.subList(rootIndex, paresResource.size());
				}
			}
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
