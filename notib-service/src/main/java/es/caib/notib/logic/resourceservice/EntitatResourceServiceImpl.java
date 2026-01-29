package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.AclHelper;
import es.caib.notib.logic.helper.EntitatPermissionHelper;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.model.FileReference;
import es.caib.notib.logic.intf.model.EntitatResource;
import es.caib.notib.logic.intf.resourceservice.EntitatResourceService;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

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
	private final EntitatPermissionHelper entitatPermissionHelper;

	@PostConstruct
	public void init() {
		register(EntitatResource.Fields.logoCapsalera, new EntitatResourceServiceImpl.LogoCapsaleraFieldFileManager());
	}

	@Override
	protected void afterConversion(EntitatResourceEntity entity, EntitatResource resource) {
		resource.setAclEntryCount(
				aclHelper.count(AclHelper.ENTITAT_CLASS, entity.getId(), null));
	}

	@Override
	protected String additionalSpringFilter(
		String currentSpringFilter,
		String[] namedQueries) {
		return entitatPermissionHelper.additionalSpringFilter("id");
	}

	@Override
	protected void beforeCreateEntity(
		EntitatResourceEntity entity,
		EntitatResource resource,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		entitatPermissionHelper.checkEntitatAdminPermission(
			getResourceClass(),
			null,
			null,
			BasePermission.CREATE);
	}

	@Override
	protected void beforeUpdateEntity(
		EntitatResourceEntity entity,
		EntitatResource resource,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		entitatPermissionHelper.checkEntitatAdminPermission(
			getResourceClass(),
			resource.getId(),
			resource.getId(),
			BasePermission.WRITE);
	}

	@Override
	protected void beforeDelete(
		EntitatResourceEntity entity,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		entitatPermissionHelper.checkEntitatAdminPermission(
			getResourceClass(),
			entity.getId(),
			entity.getId(),
			BasePermission.DELETE);
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
