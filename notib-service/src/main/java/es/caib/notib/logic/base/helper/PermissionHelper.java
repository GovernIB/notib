package es.caib.notib.logic.base.helper;

import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.model.ResourceArtifactType;
import es.caib.notib.logic.intf.base.service.PermissionEvaluatorService;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Mètodes per a la comprovació de permisos.
 *
 * @author Límit Tecnologies
 */
@Component
public class PermissionHelper extends BasePermissionHelper {

	@Override
	protected boolean checkCustomResourceAccessConstraint(
			Authentication auth,
			Serializable resourceId,
			Class<?> resourceClass,
			ResourceAccessConstraint resourceAccessConstraint,
			PermissionEvaluatorService.RestApiOperation restapiOperation,
			BasePermission[] permissions) {
		return false;
	}

	@Override
	protected boolean checkCustomResourceArtifactAccessConstraint(
			Authentication auth,
			Class<?> resourceClass,
			ResourceArtifactType type,
			String code,
			ResourceAccessConstraint resourceAccessConstraint) {
		return false;
	}

}
