package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.EntitatPermissionHelper;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.model.EntitatTipusDocumentResource;
import es.caib.notib.logic.intf.resourceservice.EntitatTipusDocumentResourceService;
import es.caib.notib.persist.resourceentity.EntitatTipusDocumentResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementació del servei de gestió de tipus de documents associats a una entitat.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EntitatTipusDocumentResourceServiceImpl
	extends BaseMutableResourceService<EntitatTipusDocumentResource, Long, EntitatTipusDocumentResourceEntity>
	implements EntitatTipusDocumentResourceService {

	private final EntitatPermissionHelper entitatPermissionHelper;

	@Override
	protected String additionalSpringFilter(
		String currentSpringFilter,
		String[] namedQueries) {
		return entitatPermissionHelper.additionalSpringFilter("entitat.id");
	}

	@Override
	protected void beforeCreateEntity(
		EntitatTipusDocumentResourceEntity entity,
		EntitatTipusDocumentResource resource,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		entitatPermissionHelper.checkEntitatAdminPermission(
			getResourceClass(),
			null,
			resource.getEntitat().getId(),
			BasePermission.CREATE);
	}

	@Override
	protected void beforeUpdateEntity(
		EntitatTipusDocumentResourceEntity entity,
		EntitatTipusDocumentResource resource,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		entitatPermissionHelper.checkEntitatAdminPermission(
			getResourceClass(),
			resource.getId(),
			resource.getEntitat().getId(),
			BasePermission.WRITE);
	}

	@Override
	protected void beforeDelete(
		EntitatTipusDocumentResourceEntity entity,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		entitatPermissionHelper.checkEntitatAdminPermission(
			getResourceClass(),
			entity.getId(),
			entity.getEntitat().getId(),
			BasePermission.DELETE);
	}

}
