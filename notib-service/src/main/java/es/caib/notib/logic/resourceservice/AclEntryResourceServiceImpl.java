package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.cacheable.PermisosCacheable;
import es.caib.notib.logic.helper.AclHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ResourceNotUpdatedException;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.ExtendedPermission;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import es.caib.notib.logic.intf.base.util.StringUtil;
import es.caib.notib.logic.intf.dto.TipusEnumDto;
import es.caib.notib.logic.intf.model.AclEntryResource;
import es.caib.notib.logic.intf.model.EntitatResource;
import es.caib.notib.logic.intf.model.OrganGestorResource;
import es.caib.notib.logic.intf.model.ProcedimentResource;
import es.caib.notib.logic.intf.resourceservice.AclEntryResourceService;
import es.caib.notib.persist.resourceentity.AclEntryResourceEntity;
import es.caib.notib.persist.resourceentity.ProcedimentOrganGestorResourceEntity;
import es.caib.notib.persist.resourcerepository.OrganGestorResourceRepository;
import es.caib.notib.persist.resourcerepository.ProcedimentOrganGestorResourceRepository;
import es.caib.notib.persist.resourcerepository.ProcedimentResourceRepository;
import joptsimple.internal.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.domain.Sort;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implementació del servei per a gestionar entrades ACL.
 * Aquest servei simula que les ACLs son un recurs gestionat per BaseMutableResourceService però per darrera gestiona
 * les ACLs mitjançant la classe MutableAclService de Spring Security.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AclEntryResourceServiceImpl extends BaseMutableResourceService<AclEntryResource, String, AclEntryResourceEntity> implements AclEntryResourceService {

	private final AclHelper aclHelper;
	private final AuthenticationHelper authenticationHelper;
	private final UserSessionHelper userSessionHelper;
	private final PermisosCacheable permisosCacheable;
	private final ProcedimentResourceRepository procedimentResourceRepository;
	private final OrganGestorResourceRepository organGestorResourceRepository;
	private final ProcedimentOrganGestorResourceRepository procedimentOrganGestorResourceRepository;

	@Override
	protected boolean isEntityRepositoryOptional() {
		return true;
	}

	/*
	 * Consulta la informació d'un AclEntryResource utilitzant el MutableAclService.
	 */
	@Override
	protected Optional<AclEntryResourceEntity> entityRepositoryFindOne(String id) {

		AclEntryResource.AclEntryPk pk = AclEntryResource.AclEntryPk.deserializeFromString(id);
		Class<?> resourceClass = getClassFromResourceName(pk.getResourceName());
		if (resourceClass == null) {
			return Optional.empty();
		}
		Acl acl = aclHelper.get(resourceClass, pk.getResourceId(), null);
		if (acl == null) {
			return Optional.empty();
		}
		List<AclEntryResourceEntity> entries = toAclEntries(acl).stream().filter(e -> {
				var epk = AclEntryResource.AclEntryPk.deserializeFromString(e.getId());
				return epk.getSidName().equals(pk.getSidName()) && epk.isSidGrantedAuthority() == pk.isSidGrantedAuthority();
		}).collect(Collectors.toList());
		if (entries.isEmpty()) {
			return Optional.empty();
		}
		var procSer = procedimentOrganGestorResourceRepository.findById((Long)(pk.getResourceId())).orElse(null);
		if (procSer == null) {
			return Optional.of(entries.get(0));
		}
		var entry = entries.get(0);
		var organ = procSer.getOrganGestor();
		ResourceReference<OrganGestorResource, Long> resource = ResourceReference.toResourceReference(organ.getId(), organ.getCodi() + ", " + organ.getNom());
		entry.getResource().setOrganGestor(resource);
		return Optional.of(entry);
	}

	/*
	 * Consulta pàginada dels AclEntryResource utilitzant el MutableAclService.
	 */
	@Override
	protected Page<AclEntryResourceEntity> entityRepositoryFindEntities(String quickFilter, String filter, String[] namedQueries, Pageable pageable) {

		var filterContainsOr = filter != null && filter.contains(" or ");
		var filterTriplets = extractFilterTriplets(filter);
		var filterResourceName = filterTriplets.stream().filter(t -> t[0].equals("resourceName") && t[1].equals(":"))
									.findFirst().map(t -> t[2]).orElse(null);
		var filterResourceId = filterTriplets.stream().filter(t -> t[0].equals("resourceId") && t[1].equals(":")).
									findFirst().map(t -> t[2]).orElse(null);
		var filterOk = filter != null && !filterContainsOr && filterResourceName != null && filterResourceId != null;
		if (!filterOk) {
			throw new RuntimeException("Filtre no suportat");
		}
		Class<?> resourceClass = getClassFromResourceName(filterResourceName);
		Long resourceId = Long.parseLong(filterResourceId);
		Acl acl = aclHelper.get(resourceClass, resourceId, null);
		var procSerComuns = getAclProcSerComu(filterResourceName, resourceId);
		if (acl == null) {
			var page = pageable.getSort().isUnsorted() ? procSerComuns : procSerComuns.stream().sorted(createGetterBasedComparator(pageable.getSort())).collect(Collectors.toList());
			return new PageImpl<>(page, pageable, procSerComuns.size());
		}
		List<AclEntryResourceEntity> resultList = toAclEntries(acl);
		resultList.addAll(procSerComuns);
		var page = pageable.getSort().isUnsorted() ? resultList : resultList.stream().sorted(createGetterBasedComparator(pageable.getSort())).collect(Collectors.toList());
		return new PageImpl<>(page, pageable, resultList.size());
	}

	private List<AclEntryResourceEntity> getAclProcSerComu(String resourceName, Long resourceId) {

		if (!"procedimentResource".equals(resourceName) && !"serveiResource".equals(resourceName)) {
			return new ArrayList<>();
		}
		List<AclEntryResourceEntity> resultList = new ArrayList<>();
		var procSerOrgans = procedimentOrganGestorResourceRepository.findProcOrganIdByProcediment(resourceId);
		Acl acl;
		for (var procSerOrgan : procSerOrgans) {
			acl = aclHelper.get(getClassFromResourceName("procSerOrganEntity"), procSerOrgan, null);
			resultList.addAll(toAclEntries(acl));
		}
		return resultList;
	}

	/*
	 * Desa els canvis d'un AclEntryResource utilitzant el MutableAclService.
	 */
	@Override
	protected AclEntryResourceEntity entitySaveFlushAndRefresh(AclEntryResourceEntity entity) {

		var resource = entity.getResource();
		List<PermissionEnum> permissionsGranted = new ArrayList<>();
		if (resource.isReadAllowed()) {
			permissionsGranted.add(PermissionEnum.READ);
		}
		if (resource.isWriteAllowed()) {
			permissionsGranted.add(PermissionEnum.WRITE);
		}
		if (resource.isCreateAllowed()) {
			permissionsGranted.add(PermissionEnum.CREATE);
		}
		if (resource.isDeleteAllowed()) {
			permissionsGranted.add(PermissionEnum.DELETE);
		}
		if (resource.isAdminAllowed()) {
			permissionsGranted.add(PermissionEnum.ADMINISTRATION);
		}
		if (resource.isPerm0Allowed()) {
			permissionsGranted.add(PermissionEnum.PERM0);
		}
		if (resource.isPerm1Allowed()) {
			permissionsGranted.add(PermissionEnum.PERM1);
		}
		if (resource.isPerm2Allowed()) {
			permissionsGranted.add(PermissionEnum.PERM2);
		}
		if (resource.isPerm3Allowed()) {
			permissionsGranted.add(PermissionEnum.PERM3);
		}
		if (resource.isPerm4Allowed()) {
			permissionsGranted.add(PermissionEnum.PERM4);
		}
		if (resource.isPerm5Allowed()) {
			permissionsGranted.add(PermissionEnum.PERM5);
		}
		if (resource.isPerm6Allowed()) {
			permissionsGranted.add(PermissionEnum.PERM6);
		}
		if (resource.isPerm7Allowed()) {
			permissionsGranted.add(PermissionEnum.PERM7);
		}
		if (resource.isPerm8Allowed()) {
			permissionsGranted.add(PermissionEnum.PERM8);
		}
		if (resource.isPerm9Allowed()) {
			permissionsGranted.add(PermissionEnum.PERM9);
		}
		if (resource.isPermXAllowed()) {
			permissionsGranted.add(PermissionEnum.PERMX);
		}
		if (!saveProcedimentComu(resource, permissionsGranted)) {
			var classe = getClassFromResourceName(resource.getResourceName());
			aclHelper.set(classe, resource.getResourceId(), resource.getSidName(), resource.isSidGrantedAuthority(), permissionsGranted);
		}
		evictPermisosCaches();
		return entity;
	}

	/*
	 * Converteix una ACL retornat per MutableAclService un AclEntryResource.
	 */
	@Override
	protected AclEntryResource entityDetachConvertAndMerge(AclEntryResourceEntity entity, Map<String, AnswerRequiredException.AnswerValue> answers, boolean create) {

		var response = entityToResource(entity);
		entityAfterMergeLogic(response, entity, answers, create);
		return response;
	}

	/*
	 * Esborra un AclEntryResource utilitzant el MutableAclService.
	 */
	@Override
	protected void entityRepositoryDelete(AclEntryResourceEntity entity) {

		var resource = entity.getResource();
		aclHelper.delete(getClassFromResourceName(resource.getResourceName()), resource.getResourceId(), resource.getSidName(), resource.isSidGrantedAuthority());
		evictPermisosCaches();
	}

	// Els permisos concedits/revocats aquí (entitat, òrgan gestor, procediment) es reflecteixen a les
	// caches de permisos de tots els usuaris (getPermisosEntitatsUsuariActual, entitats i òrgans gestors
	// accessibles), perquè el canvi sigui visible sense haver de tornar a iniciar sessió.
	private void evictPermisosCaches() {
		permisosCacheable.evictAllPermisosEntitatsUsuariActual();
		permisosCacheable.evictAllFindEntitatsAccessiblesUsuari();
		permisosCacheable.evictAllFindOrgansGestorsAccessiblesUsuari();
	}

	/*
	 * Converteix una ACL retornat per MutableAclService un AclEntryResource.
	 */
	@Override
	protected AclEntryResource entityToResource(AclEntryResourceEntity entity) {
		return entity.getResource();
	}

	/*
	 * Converteix AclEntryResource a una ACL de MutableAclService.
	 */
	@Override
	protected AclEntryResourceEntity resourceToEntity(AclEntryResource resource, String pk, Map<String, Persistable<?>> referencedEntities) {
		return AclEntryResourceEntity.builder().id(pk).resource(resource).build();
	}

	@Override
	protected void entityRepositoryFlush() {
	}

	@Override
	protected void beforeCreateEntity(AclEntryResourceEntity entity, AclEntryResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {

		resource.setSidGrantedAuthority(TipusEnumDto.ROL.equals(resource.getTipus()));
		checkAclPermissionOnResource(resource);
	}

	@Override
	protected void beforeUpdateEntity(AclEntryResourceEntity entity, AclEntryResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {

		resource.setSidGrantedAuthority(TipusEnumDto.ROL.equals(resource.getTipus()));
		checkAclPermissionOnResource(resource);
	}

	@Override
	protected void beforeUpdateSave(AclEntryResourceEntity entity, AclEntryResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {

		AclEntryResource.AclEntryPk pk = AclEntryResource.AclEntryPk.deserializeFromString(resource.getId());
		if (pk.isSidGrantedAuthority() != resource.isSidGrantedAuthority() || !pk.getSidName().equals(resource.getSidName())) {
			aclHelper.delete(getClassFromResourceName(pk.getResourceName()), pk.getResourceId(), pk.getSidName(), pk.isSidGrantedAuthority());
		}
	}

	@Override
	protected void updateEntityWithResource(AclEntryResourceEntity entity, AclEntryResource resource, Map<String, Persistable<?>> referencedEntities) {
		entity.setResource(resource);
	}

	private List<AclEntryResourceEntity> toAclEntries(Acl acl) {

		List<AccessControlEntry> accessControlEntries = acl.getEntries();
		if (accessControlEntries == null) {
			return Collections.emptyList();
		}
		Map<Sid, List<AccessControlEntry>> entriesBySid = accessControlEntries.stream().collect(Collectors.groupingBy(AccessControlEntry::getSid));
		String resourceClassName = acl.getObjectIdentity().getType();
		Serializable resourceId = acl.getObjectIdentity().getIdentifier();
		return entriesBySid.entrySet().stream().
			map(entry -> formAccessControlEntryToAclEntryEntity(resourceClassName, resourceId, entry.getKey(), entry.getValue())).
			collect(Collectors.toList());
	}

	private AclEntryResourceEntity formAccessControlEntryToAclEntryEntity(String resourceClassName, Serializable resourceId, Sid sid, List<AccessControlEntry> aces) {

		var aclEntry = new AclEntryResource();
		if (sid instanceof PrincipalSid) {
			aclEntry.setSidGrantedAuthority(false);
			aclEntry.setSidName(((PrincipalSid) sid).getPrincipal());
		} else if (sid instanceof GrantedAuthoritySid) {
			aclEntry.setSidGrantedAuthority(true);
			aclEntry.setSidName(((GrantedAuthoritySid) sid).getGrantedAuthority());
		}
		var resourceName = getResourceNameFromClassName(resourceClassName);
		aclEntry.setResourceName(resourceName);
		aclEntry.setResourceId(resourceId);
		var pk = new AclEntryResource.AclEntryPk(resourceName, resourceId, aclEntry.isSidGrantedAuthority(), aclEntry.getSidName());
		aclEntry.setId(pk.serializeToString());
		aces.forEach(a -> {
			int mask = a.getPermission().getMask();
			if ((mask & BasePermission.READ.getMask()) != 0)  {
				aclEntry.setReadAllowed(true);
			}
			if ((mask & BasePermission.WRITE.getMask()) != 0) {
				aclEntry.setWriteAllowed(true);
			}
			if ((mask & BasePermission.CREATE.getMask()) != 0) {
				aclEntry.setCreateAllowed(true);
			}
			if ((mask & BasePermission.DELETE.getMask()) != 0) {
				aclEntry.setDeleteAllowed(true);
			}
			if ((mask & BasePermission.ADMINISTRATION.getMask()) != 0) {
				aclEntry.setAdminAllowed(true);
			}
			if ((mask & ExtendedPermission.PERM0.getMask()) != 0) {
				aclEntry.setPerm0Allowed(true);
			}
			if ((mask & ExtendedPermission.PERM1.getMask()) != 0) {
				aclEntry.setPerm1Allowed(true);
			}
			if ((mask & ExtendedPermission.PERM2.getMask()) != 0) {
				aclEntry.setPerm2Allowed(true);
			}
			if ((mask & ExtendedPermission.PERM3.getMask()) != 0) {
				aclEntry.setPerm3Allowed(true);
			}
			if ((mask & ExtendedPermission.PERM4.getMask()) != 0) {
				aclEntry.setPerm4Allowed(true);
			}
			if ((mask & ExtendedPermission.PERM5.getMask()) != 0) {
				aclEntry.setPerm5Allowed(true);
			}
			if ((mask & ExtendedPermission.PERM6.getMask()) != 0) {
				aclEntry.setPerm6Allowed(true);
			}
			if ((mask & ExtendedPermission.PERM7.getMask()) != 0) {
				aclEntry.setPerm7Allowed(true);
			}
			if ((mask & ExtendedPermission.PERM8.getMask()) != 0) {
				aclEntry.setPerm8Allowed(true);
			}
			if ((mask & ExtendedPermission.PERM9.getMask()) != 0) {
				aclEntry.setPerm9Allowed(true);
			}
			if ((mask & ExtendedPermission.PERMX.getMask()) != 0) {
				aclEntry.setPermXAllowed(true);
			}
		});
		return resourceToEntity(aclEntry, pk.serializeToString(), null);
	}

	private static final Pattern TRIPLET_PATTERN = Pattern.compile(
		"([a-zA-Z_][a-zA-Z0-9_\\.]*)" +
			"\\s*" +
			"(:|=|!=|>=|<=|>|<|~|!~|\\bin\\b)" +
			"\\s*" +
			"(\\[[^\\]]*\\]|'[^']*'|\"[^\"]*\"|\\d+\\.?\\d*|[^\\s\\)]+)",
		Pattern.CASE_INSENSITIVE);

	private static final List<Map.Entry<Class<?>, Class<?>>> aclClassMapping = List.of(
		Map.entry(EntitatResource.class, AclHelper.ENTITAT_CLASS),
		Map.entry(OrganGestorResource.class, AclHelper.ORGAN_GESTOR_CLASS),
		Map.entry(ProcedimentResource.class, AclHelper.PROCEDIMENT_CLASS));

	private List<String[]> extractFilterTriplets(String filter) {

		Matcher matcher = TRIPLET_PATTERN.matcher(filter);
		List<String[]> triplets = new ArrayList<>();
		String field;
		String op;
		String value;
		while (matcher.find()) {
			field = matcher.group(1);
			op = matcher.group(2);
			value = matcher.group(3);
			if (value.startsWith("'") && value.endsWith("'")) {
				value = value.substring(1, value.length() - 1);
			}
			triplets.add(new String[]{field, op, value});
		}
		return triplets;
	}

	private Class<?> getClassFromResourceName(String resourceName) {

		if (resourceName == null) {
			return null;
		}
		try {
			var resolver = new PathMatchingResourcePatternResolver();
			String basePath = BaseConfig.BASE_PACKAGE.replace(".", "/");
			var resources = resolver.getResources("classpath*:" + basePath + "/**/" + StringUtil.capitalize(resourceName) + ".class");
			String path;
			String className;
			// TODO cal fer un for per un element?
			for (var resource : resources) {
				path = resource.getURL().getPath();
				className = path.substring(path.indexOf(basePath)).replace("/", ".").replace(".class", "");
				return getAclClassMappingForKey(Class.forName(className));
			}
		} catch (IOException | ClassNotFoundException ex) {
			log.warn("Couldn't find class for resource name {}", resourceName, ex);
		}
		return null;
	}

	private String getResourceNameFromClassName(String className) {

		if (Strings.isNullOrEmpty(className)) {
			return null;
		}
		try {
			Class<?> clazz = getAclClassMappingForValue(Class.forName(className));
			return StringUtil.decapitalize(clazz.getSimpleName());
		} catch (ClassNotFoundException ex) {
			log.warn("Couldn't find class for {}", className, ex);
			return null;
		}
	}

	private Class<?> getAclClassMappingForKey(Class<?> key) {

		for (Map.Entry<Class<?>, Class<?>> mapping : aclClassMapping) {
			if (mapping.getKey().equals(key)) {
				return mapping.getValue();
			}
		}
		return key;
	}

	private Class<?> getAclClassMappingForValue(Class<?> value) {

		for (Map.Entry<Class<?>, Class<?>> mapping : aclClassMapping) {
			if (mapping.getValue().equals(value)) {
				return mapping.getKey();
			}
		}
		return value;
	}

	private void checkAclPermissionOnResource(AclEntryResource resource) {

		Class<?> resourceClass = getClassFromResourceName(resource.getResourceName());
		if (Objects.equals(resourceClass, AclHelper.ENTITAT_CLASS)) {
			var permissionGranted = isAclPermissionGrantedForEntitat(resource.getResourceId());
			if (permissionGranted) {
				return;
			}
		} else if (Objects.equals(resourceClass, AclHelper.ORGAN_GESTOR_CLASS) || Objects.equals(resourceClass, AclHelper.PROCEDIMENT_CLASS)) {
			var currentEntitatId = userSessionHelper.getCurrentEntitatId();
			var permissionGranted = isAclPermissionGrantedForEntitat(currentEntitatId);
			if (permissionGranted) {
				return;
			}
		}
		var msg = "You're not allowed to update ACLs on " + resource.getResourceName() + " resources";
		throw new ResourceNotUpdatedException(AclEntryResource.class, resource.getId(), msg);
	}

	private boolean isAclPermissionGrantedForEntitat(Serializable entitatId) {

		// Per a poder modificar les ACLs d'una entitat s'ha de tenir el rol NOT_SUPER o ser administrador d'entitat
		// (rol NOT_ADMIN) amb permisos d'administració (PERM2) sobre l'entitat especificada.
		var isRoleSuper = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_SUPER);
		if (isRoleSuper) {
			return true;
		}
		var isRoleAdmin = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN);
		var isRoleAdminOrgan = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ORGAN);
		if (!isRoleAdmin && !isRoleAdminOrgan) {
			return false;
		}
		var sids = aclHelper.getCurrentUserSids().toArray(Sid[]::new);
		return aclHelper.anyPermissionGranted(AclHelper.ENTITAT_CLASS, entitatId, List.of(ExtendedPermission.PERM2), sids);
	}

	/*
	 * Si el resource pertany a un procediment comú desa els canvis als ACLs i retorna true. Si no retorna false.
	 */
	private boolean saveProcedimentComu(AclEntryResource resource, List<PermissionEnum> permissionsGranted) {

		var isProcediment = AclHelper.PROCEDIMENT_CLASS.equals(getClassFromResourceName(resource.getResourceName()));
		if (!isProcediment) {
			return false;
		}
		var procediment = procedimentResourceRepository.findById(Long.parseLong(resource.getResourceId().toString()));
		if (procediment.isEmpty() || !procediment.get().isComu()) {
			return false;
		}
		// Es mira si ja existeix un registre pel procediment - òrgan gestor i, si no existeix, en crea un de nou.
		var organGestor = organGestorResourceRepository.findById(resource.getOrganGestor().getId());
		if (organGestor.isEmpty()) {
			return false;
		}
		var procedimentOrganGestor = procedimentOrganGestorResourceRepository.findByProcedimentAndOrganGestor(procediment.get(), organGestor.get());
		Long procedimentOrganGestorId;
		if (procedimentOrganGestor.isPresent()) {
			procedimentOrganGestorId = procedimentOrganGestor.get().getId();
		} else {
			var proc = ProcedimentOrganGestorResourceEntity.builder().procediment(procediment.get()).organGestor(organGestor.get()).build();
			var creat = procedimentOrganGestorResourceRepository.saveAndFlush(proc);
			procedimentOrganGestorId = creat.getId();
		}
		aclHelper.set(AclHelper.PROCEDIMENT_ORGAN_CLASS, procedimentOrganGestorId, resource.getSidName(), resource.isSidGrantedAuthority(), permissionsGranted);
		return true;
	}

	private <T> Comparator<T> createGetterBasedComparator(Sort sort) {
		return sort.stream().map(this::<T>createComparatorForOrder).reduce(Comparator::thenComparing).orElse((a, b) -> 0);
	}

	@SuppressWarnings("unchecked")
	private <T> Comparator<T> createComparatorForOrder(Sort.Order order) {
		switch (order.getProperty()) {
			case "subjectType":
				return (Comparator<T>) createComparator(AclEntryResourceEntity::getSidGrantedAuthority, order.getDirection());
			case "subjectValue":
				return (Comparator<T>) createComparator(AclEntryResourceEntity::getSidName, order.getDirection());
			default:
				return (a, b) -> 0;
		}
	}

	private <T, U extends Comparable<U>> Comparator<T> createComparator(Function<T, U> extractor, Sort.Direction direction) {
		return (a, b) -> {
			U valueA = extractor.apply(a);
			U valueB = extractor.apply(b);
			if (valueA == null && valueB == null) {
				return 0;
			}
			if (valueA == null) {
				return direction == Sort.Direction.ASC ? -1 : 1;
			}
			if (valueB == null) {
				return direction == Sort.Direction.ASC ? 1 : -1;
			}
			int result = valueA.compareTo(valueB);
			return direction == Sort.Direction.ASC ? result : -result;
		};
	}

}
