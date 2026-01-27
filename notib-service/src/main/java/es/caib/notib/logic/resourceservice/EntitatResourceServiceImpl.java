package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.AclHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.FileReference;
import es.caib.notib.logic.intf.base.permission.ExtendedPermission;
import es.caib.notib.logic.intf.model.EntitatResource;
import es.caib.notib.logic.intf.resourceservice.EntitatResourceService;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementació del servei de gestió d'entitats.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EntitatResourceServiceImpl extends BaseMutableResourceService<EntitatResource, Long, EntitatResourceEntity> implements EntitatResourceService {

	private final AclHelper aclHelper;
	private final AuthenticationHelper authenticationHelper;

	@PostConstruct
	public void init() {
		register(EntitatResource.Fields.logoCapsalera, new EntitatResourceServiceImpl.LogoCapsaleraFieldFileManager());
	}

	@Override
	protected void afterConversion(EntitatResourceEntity entity, EntitatResource resource) {
		resource.setAclEntryCount(
				aclHelper.count(EntitatEntity.class, entity.getId(), null));
	}

	@Override
	protected String additionalSpringFilter(
			String currentSpringFilter,
			String[] namedQueries) {
		boolean isRoleSuper = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_SUPER);
		if (!isRoleSuper) {
			Set<Serializable> allowedIds = aclHelper.findIdsWithAnyPermission(
					EntitatEntity.class,
					List.of(ExtendedPermission.PERM0),
					aclHelper.getCurrentUserSids().toArray(Sid[]::new));
			String joinedIds = allowedIds.stream()
					.map(String::valueOf)
					.collect(Collectors.joining(","));
			if (!joinedIds.isEmpty()) {
				return "id in (" + joinedIds + ")";
			} else {
				return "id is null";
			}
		} else {
			return null;
		}
	}

	public static class LogoCapsaleraFieldFileManager implements FieldFileManager<EntitatResourceEntity> {
		@Override
		public FileReference read(
				EntitatResourceEntity entity,
				String fieldName) {
			byte[] content = entity.getLogoCapsalera();
			if (content != null) {
				return new FileReference(
						"logo.jpg",
						content,
						"image/jpeg",
						content.length);
			} else {
				return null;
			}
		}
		@Override
		public void save(EntitatResourceEntity entity, String fieldName, FileReference fileReference) {
			entity.setLogoCapsalera(fileReference != null ? fileReference.getContent() : null);
		}
		@Override
		public void delete(EntitatResourceEntity entity, String fieldName) {
			entity.setLogoCapsalera(null);
		}
	}

}
