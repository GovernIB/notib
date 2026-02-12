package es.caib.notib.logic.helper;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.notib.logic.intf.base.exception.ResourceNotUpdatedException;
import es.caib.notib.logic.intf.base.model.Resource;
import es.caib.notib.logic.intf.base.permission.ExtendedPermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Helper encarregat de verificar els permisos sobre una entitat.
 *
 * @author Limit Tecnologies
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EntitatPermissionHelper {

	private final AclHelper aclHelper;
	private final AuthenticationHelper authenticationHelper;

	/**
	 * Crea una expressió Spring Filter per a consultar únicament les entitats sobre les que es tenen permisos d'usuari.
	 *
	 * @param filterProperty
	 *            la propietat Spring Filter sobre la que s'ha de fer el filtre.
	 * @return l'expressió Spring Filter a aplicar.
	 */
	public String additionalSpringFilter(String filterProperty) {
		// Restringeix la consulta si no es te el rol NOT_SUPER o si no es tenen permisos d'usuari sobre l'entitat del
		// tipus de document.
		boolean isRoleSuper = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_SUPER);
		if (!isRoleSuper) {
			Set<Serializable> allowedIds = aclHelper.findIdsWithAnyPermission(
				AclHelper.ENTITAT_CLASS,
				List.of(ExtendedPermission.PERM0), // Permís d'usuari
				aclHelper.getCurrentUserSids().toArray(Sid[]::new));
			String joinedIds = allowedIds.stream()
				.map(String::valueOf)
				.collect(Collectors.joining(","));
			if (!joinedIds.isEmpty()) {
				return filterProperty + " in (" + joinedIds + ")";
			} else {
				return filterProperty + " is null";
			}
		} else {
			return null;
		}
	}

	/**
	 * Verifica si es tenen permisos per a administrar l'entitat.
	 *
	 * @param resourceClass
	 *            la classe del recurs.
	 * @param id
	 *            l'id del recurs.
	 * @param entitatId
	 *            l'id de l'entitat.
	 * @param permission
	 *            el permís que s'està comprovant (només per a crear l'excepció).
	 */
	public void checkEntitatAdminPermission(
		Class<? extends Resource<?>> resourceClass,
		Long id,
		Long entitatId,
		Permission permission) {
		boolean isRoleSuper = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_SUPER);
		if (!isRoleSuper) {
			boolean permissionGranted = false;
			if (entitatId != null) {
				permissionGranted = aclHelper.anyPermissionGranted(
					AclHelper.ENTITAT_CLASS,
					entitatId,
					List.of(ExtendedPermission.PERM2), // Permís d'administrador d'entitat
					aclHelper.getCurrentUserSids().toArray(Sid[]::new));
			}
			if (!permissionGranted) {
				if (Objects.equals(permission, BasePermission.CREATE)) {
					throw new ResourceNotCreatedException(
						resourceClass,
						"Not allowed to create " + resourceClass.getSimpleName());
				} else {
					String permissionText = Objects.equals(permission, BasePermission.DELETE) ? "delete" : "update";
					throw new ResourceNotUpdatedException(
						resourceClass,
						"" + id,
						"Not allowed to " + permissionText + " " + resourceClass.getSimpleName());
				}
			}
		}
	}

}
