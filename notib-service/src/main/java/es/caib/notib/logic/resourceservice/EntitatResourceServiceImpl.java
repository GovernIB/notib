package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.AclHelper;
import es.caib.notib.logic.intf.base.model.FileReference;
import es.caib.notib.logic.intf.model.EntitatResource;
import es.caib.notib.logic.intf.resourceservice.EntitatResourceService;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

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

	@PostConstruct
	public void init() {
		register(EntitatResource.Fields.logoCapsalera, new EntitatResourceServiceImpl.LogoCapsaleraFieldFileManager());
	}

	@Override
	protected void afterConversion(EntitatResourceEntity entity, EntitatResource resource) {
		resource.setAclEntryCount(
				aclHelper.count(EntitatEntity.class, entity.getId(), null));
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
