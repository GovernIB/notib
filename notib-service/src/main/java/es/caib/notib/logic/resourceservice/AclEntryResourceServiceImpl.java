package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.AclHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.permission.ExtendedPermission;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import es.caib.notib.logic.intf.base.util.StringUtil;
import es.caib.notib.logic.intf.model.AclEntryResource;
import es.caib.notib.logic.intf.model.EntitatResource;
import es.caib.notib.logic.intf.resourceservice.AclEntryResourceService;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.resourceentity.AclEntryResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.domain.*;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AclEntryResourceServiceImpl extends BaseMutableResourceService<AclEntryResource, String, AclEntryResourceEntity> implements AclEntryResourceService {

	private final AclHelper aclHelper;

	@Override
	protected boolean isEntityRepositoryOptional() {
		return true;
	}

	@Override
	protected Optional<AclEntryResourceEntity> entityRepositoryFindOne(String id) {
		AclEntryResource.AclEntryPk pk = AclEntryResource.AclEntryPk.deserializeFromString(id);
		Class<?> resourceClass = getClassFromResourceName(pk.getResourceName());
		if (resourceClass != null) {
			Acl acl = aclHelper.get(
					resourceClass,
					pk.getResourceId(),
					null);
			if (acl != null) {
				List<AclEntryResourceEntity> entries = toAclEntries(acl).stream().
						filter(e -> {
							AclEntryResource.AclEntryPk epk = AclEntryResource.AclEntryPk.deserializeFromString(e.getId());
							return epk.getSidName().equals(pk.getSidName()) && epk.isSidGrantedAuthority() == pk.isSidGrantedAuthority();
						}).
						collect(Collectors.toList());
				if (!entries.isEmpty()) {
					return Optional.of(entries.get(0));
				}
			}
		}
		return Optional.empty();
	}

	@Override
	protected Page<AclEntryResourceEntity> entityRepositoryFindEntities(
			String quickFilter,
			String filter,
			String[] namedQueries,
			Pageable pageable) {
		boolean filterContainsOr = filter != null && filter.contains(" or ");
		List<String[]> filterTriplets = extractFilterTriplets(filter);
		String filterResourceName = filterTriplets.stream().
				filter(t -> t[0].equals("resourceName") && t[1].equals(":")).
				findFirst().
				map(t-> t[2]).
				orElse(null);
		String filterResourceId = filterTriplets.stream().
				filter(t -> t[0].equals("resourceId") && t[1].equals(":")).
				findFirst().
				map(t-> t[2]).
				orElse(null);
		boolean filterOk = filter != null && !filterContainsOr && filterResourceName != null && filterResourceId != null;
		if (filterOk) {
			Class<?> resourceClass = getClassFromResourceName(filterResourceName);
			Long resourceId = Long.parseLong(filterResourceId);
			Acl acl = aclHelper.get(
					resourceClass,
					resourceId,
					null);
			if (acl != null) {
				List<AclEntryResourceEntity> resultList = toAclEntries(acl);
				return new PageImpl<>(
						pageable.getSort().isUnsorted() ? resultList : resultList.stream().
								sorted(createGetterBasedComparator(pageable.getSort())).
								collect(Collectors.toList()),
						pageable,
						resultList.size());
			} else {
				return Page.empty();
			}
		} else {
			throw new RuntimeException("Filtre no suportat");
		}
	}

	@Override
	protected AclEntryResourceEntity entitySaveFlushAndRefresh(AclEntryResourceEntity entity) {
		AclEntryResource resource = entity.getResource();
		List<PermissionEnum> permissionsGranted = new ArrayList<>();
		if (resource.isReadAllowed()) permissionsGranted.add(PermissionEnum.READ);
		if (resource.isWriteAllowed()) permissionsGranted.add(PermissionEnum.WRITE);
		if (resource.isCreateAllowed()) permissionsGranted.add(PermissionEnum.CREATE);
		if (resource.isDeleteAllowed()) permissionsGranted.add(PermissionEnum.DELETE);
		if (resource.isAdminAllowed()) permissionsGranted.add(PermissionEnum.ADMINISTRATION);
		if (resource.isPerm0Allowed()) permissionsGranted.add(PermissionEnum.PERM0);
		if (resource.isPerm1Allowed()) permissionsGranted.add(PermissionEnum.PERM1);
		if (resource.isPerm2Allowed()) permissionsGranted.add(PermissionEnum.PERM2);
		if (resource.isPerm3Allowed()) permissionsGranted.add(PermissionEnum.PERM3);
		if (resource.isPerm4Allowed()) permissionsGranted.add(PermissionEnum.PERM4);
		if (resource.isPerm5Allowed()) permissionsGranted.add(PermissionEnum.PERM5);
		if (resource.isPerm6Allowed()) permissionsGranted.add(PermissionEnum.PERM6);
		if (resource.isPerm7Allowed()) permissionsGranted.add(PermissionEnum.PERM7);
		if (resource.isPerm8Allowed()) permissionsGranted.add(PermissionEnum.PERM8);
		if (resource.isPerm9Allowed()) permissionsGranted.add(PermissionEnum.PERM9);
		if (resource.isPermXAllowed()) permissionsGranted.add(PermissionEnum.PERMX);
		aclHelper.set(
				getClassFromResourceName(resource.getResourceName()),
				resource.getResourceId(),
				resource.getSidName(),
				resource.isSidGrantedAuthority(),
				permissionsGranted);
		return entity;
	}

	@Override
	protected AclEntryResource entityDetachConvertAndMerge(
			AclEntryResourceEntity entity,
			Map<String, AnswerRequiredException.AnswerValue> answers,
			boolean create) {
		AclEntryResource response = entityToResource(entity);
		entityAfterMergeLogic(response, entity, answers, create);
		return response;
	}

	@Override
	protected void entityRepositoryDelete(AclEntryResourceEntity entity) {
		AclEntryResource resource = entity.getResource();
		aclHelper.delete(
				getClassFromResourceName(resource.getResourceName()),
				resource.getResourceId(),
				resource.getSidName(),
				resource.isSidGrantedAuthority());
	}

	@Override
	protected void entityRepositoryFlush() {
	}

	@Override
	protected AclEntryResource entityToResource(AclEntryResourceEntity entity) {
		return entity.getResource();
	}

	@Override
	protected AclEntryResourceEntity resourceToEntity(
			AclEntryResource resource,
			String pk,
			Map<String, Persistable<?>> referencedEntities) {
		return AclEntryResourceEntity.builder().
				id(pk).
				resource(resource).
				build();
	}

	@Override
	protected void beforeUpdateSave(
			AclEntryResourceEntity entity,
			AclEntryResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
		AclEntryResource.AclEntryPk pk = AclEntryResource.AclEntryPk.deserializeFromString(resource.getId());
		if (pk.isSidGrantedAuthority() != resource.isSidGrantedAuthority() || !pk.getSidName().equals(resource.getSidName())) {
			aclHelper.delete(
					getClassFromResourceName(pk.getResourceName()),
					pk.getResourceId(),
					pk.getSidName(),
					pk.isSidGrantedAuthority());
		}
	}

	@Override
	protected void updateEntityWithResource(
			AclEntryResourceEntity entity,
			AclEntryResource resource,
			Map<String, Persistable<?>> referencedEntities) {
		entity.setResource(resource);
	}

	private List<AclEntryResourceEntity> toAclEntries(Acl acl) {
		List<AccessControlEntry> accessControlEntries = acl.getEntries();
		if (accessControlEntries == null) {
			return Collections.emptyList();
		}
		Map<Sid, List<AccessControlEntry>> entriesBySid = accessControlEntries.stream().
				collect(Collectors.groupingBy(AccessControlEntry::getSid));
		String resourceClassName = acl.getObjectIdentity().getType();
		Serializable resourceId = acl.getObjectIdentity().getIdentifier();
		return entriesBySid.entrySet().stream().
				map(entry -> formAccessControlEntryToAclEntryEntity(
						resourceClassName,
						resourceId,
						entry.getKey(),
						entry.getValue())).
				collect(Collectors.toList());
	}

	private AclEntryResourceEntity formAccessControlEntryToAclEntryEntity(
			String resourceClassName,
			Serializable resourceId,
			Sid sid,
			List<AccessControlEntry> aces) {
		AclEntryResource aclEntry = new AclEntryResource();
		if (sid instanceof PrincipalSid) {
			aclEntry.setSidGrantedAuthority(false);
			aclEntry.setSidName(((PrincipalSid)sid).getPrincipal());
		} else if (sid instanceof GrantedAuthoritySid) {
			aclEntry.setSidGrantedAuthority(true);
			aclEntry.setSidName(((GrantedAuthoritySid)sid).getGrantedAuthority());
		}
		String resourceName = getResourceNameFromClassName(resourceClassName);
		aclEntry.setResourceName(resourceName);
		aclEntry.setResourceId(resourceId);
		AclEntryResource.AclEntryPk pk = new AclEntryResource.AclEntryPk(
				resourceName,
				resourceId,
				aclEntry.isSidGrantedAuthority(),
				aclEntry.getSidName());
		aclEntry.setId(pk.serializeToString());
		aces.forEach(a -> {
			int mask = a.getPermission().getMask();
			if ((mask & BasePermission.READ.getMask()) != 0) aclEntry.setReadAllowed(true);
			if ((mask & BasePermission.WRITE.getMask()) != 0) aclEntry.setWriteAllowed(true);
			if ((mask & BasePermission.CREATE.getMask()) != 0) aclEntry.setCreateAllowed(true);
			if ((mask & BasePermission.DELETE.getMask()) != 0) aclEntry.setDeleteAllowed(true);
			if ((mask & BasePermission.ADMINISTRATION.getMask()) != 0) aclEntry.setAdminAllowed(true);
			if ((mask & ExtendedPermission.PERM0.getMask()) != 0) aclEntry.setPerm0Allowed(true);
			if ((mask & ExtendedPermission.PERM1.getMask()) != 0) aclEntry.setPerm1Allowed(true);
			if ((mask & ExtendedPermission.PERM2.getMask()) != 0) aclEntry.setPerm2Allowed(true);
			if ((mask & ExtendedPermission.PERM3.getMask()) != 0) aclEntry.setPerm3Allowed(true);
			if ((mask & ExtendedPermission.PERM4.getMask()) != 0) aclEntry.setPerm4Allowed(true);
			if ((mask & ExtendedPermission.PERM5.getMask()) != 0) aclEntry.setPerm5Allowed(true);
			if ((mask & ExtendedPermission.PERM6.getMask()) != 0) aclEntry.setPerm6Allowed(true);
			if ((mask & ExtendedPermission.PERM7.getMask()) != 0) aclEntry.setPerm7Allowed(true);
			if ((mask & ExtendedPermission.PERM8.getMask()) != 0) aclEntry.setPerm8Allowed(true);
			if ((mask & ExtendedPermission.PERM9.getMask()) != 0) aclEntry.setPerm9Allowed(true);
			if ((mask & ExtendedPermission.PERMX.getMask()) != 0) aclEntry.setPermXAllowed(true);
		});
		return resourceToEntity(
				aclEntry,
				pk.serializeToString(),
				null);
	}

	private static final Pattern TRIPLET_PATTERN = Pattern.compile(
			"([a-zA-Z_][a-zA-Z0-9_\\.]*)" +
					"\\s*" +
					"(:|=|!=|>=|<=|>|<|~|!~|\\bin\\b)" +
					"\\s*" +
					"(\\[[^\\]]*\\]|'[^']*'|\"[^\"]*\"|\\d+\\.?\\d*|[^\\s\\)]+)",
			Pattern.CASE_INSENSITIVE);

	private static final List<Map.Entry<Class<?>, Class<?>>> aclClassMapping = List.of(
			Map.entry(EntitatResource.class, EntitatEntity.class));

	private List<String[]> extractFilterTriplets(String filter) {
		Matcher matcher = TRIPLET_PATTERN.matcher(filter);
		List<String[]> triplets = new ArrayList<>();
		while (matcher.find()) {
			String field = matcher.group(1);
			String op = matcher.group(2);
			String value = matcher.group(3);
			if (value.startsWith("'") && value.endsWith("'")) {
				value = value.substring(1, value.length() - 1);
			}
			triplets.add(new String[]{field, op, value});
		}
		return triplets;
	}

	private Class<?> getClassFromResourceName(String resourceName) {
		if (resourceName != null) {
			try {
				ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
				String basePath = BaseConfig.BASE_PACKAGE.replace(".", "/");
				org.springframework.core.io.Resource[] resources = resolver.getResources(
						"classpath*:" + basePath + "/**/" +
								StringUtil.capitalize(resourceName) +
								".class");
				for (org.springframework.core.io.Resource resource: resources) {
					String path = resource.getURL().getPath();
					String className = path.
							substring(path.indexOf(basePath)).
							replace("/", ".").
							replace(".class", "");
					return getAclClassMappingForKey(Class.forName(className));
				}
			} catch (IOException | ClassNotFoundException ex) {
				log.warn("Couldn't find class for resource name {}", resourceName, ex);
			}
		}
		return null;
	}

	private String getResourceNameFromClassName(String className) {
		if (className != null) {
			try {
				Class<?> clazz = getAclClassMappingForValue(Class.forName(className));
				return StringUtil.decapitalize(clazz.getSimpleName());
			} catch (ClassNotFoundException ex) {
				log.warn("Couldn't find class for {}", className, ex);
			}
		}
		return null;
	}

	private Class<?> getAclClassMappingForKey(Class<?> key) {
		for (Map.Entry<Class<?>, Class<?>> mapping: aclClassMapping) {
			if (mapping.getKey().equals(key)) {
				return mapping.getValue();
			}
		}
		return key;
	}

	private Class<?> getAclClassMappingForValue(Class<?> value) {
		for (Map.Entry<Class<?>, Class<?>> mapping: aclClassMapping) {
			if (mapping.getValue().equals(value)) {
				return mapping.getKey();
			}
		}
		return value;
	}

	private <T> Comparator<T> createGetterBasedComparator(Sort sort) {
		return sort.stream().
				map(this::<T>createComparatorForOrder).
				reduce(Comparator::thenComparing).
				orElse((a, b) -> 0);
	}
	@SuppressWarnings("unchecked")
	private <T> Comparator<T> createComparatorForOrder(Sort.Order order) {
		switch (order.getProperty()) {
			case "subjectType":
				return (Comparator<T>)createComparator(AclEntryResourceEntity::getSidGrantedAuthority, order.getDirection());
			case "subjectValue":
				return (Comparator<T>)createComparator(AclEntryResourceEntity::getSidName, order.getDirection());
			default:
				return (a, b) -> 0;
		}
	}
	private <T, U extends Comparable<U>> Comparator<T> createComparator(
			Function<T, U> extractor, Sort.Direction direction) {
		return (a, b) -> {
			U valueA = extractor.apply(a);
			U valueB = extractor.apply(b);
			if (valueA == null && valueB == null) return 0;
			if (valueA == null) return direction == Sort.Direction.ASC ? -1 : 1;
			if (valueB == null) return direction == Sort.Direction.ASC ? 1 : -1;
			int result = valueA.compareTo(valueB);
			return direction == Sort.Direction.ASC ? result : -result;
		};
	}

}
