package es.caib.notib.logic.base.helper;

import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

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
			Class<?> resourceClass,
			BasePermission permission) {
		return false;
	}

}
