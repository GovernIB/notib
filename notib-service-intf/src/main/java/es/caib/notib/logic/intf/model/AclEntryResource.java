package es.caib.notib.logic.intf.model;

import es.caib.comanda.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Base64;

/**
 * Informació d'un entrada a les taules ACL.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(
		accessConstraints = {
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { BaseConfig.ROLE_ADMIN },
						grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE }
				),
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { BaseConfig.ROLE_CONSULTA },
						grantedPermissions = { PermissionEnum.READ }
				)
		}
)
public class AclEntryResource extends BaseResource<String> {

	private boolean grantedAuthority;
	@NotBlank
	@Size(max = 128)
	private String sidName;
	@NotNull
	private AclEntryResourceType resourceType;
	@NotNull
	private Serializable resourceId;
	private boolean readAllowed;
	private boolean writeAllowed;
	private boolean createAllowed;
	private boolean deleteAllowed;
	private boolean adminAllowed;
	private boolean perm0Allowed;
	private boolean perm1Allowed;
	private boolean perm2Allowed;
	private boolean perm3Allowed;
	private boolean perm4Allowed;
	private boolean perm5Allowed;
	private boolean perm6Allowed;
	private boolean perm7Allowed;
	private boolean perm8Allowed;
	private boolean perm9Allowed;

	private String subjectName;

	@Getter
	@RequiredArgsConstructor
	public static class AclEntryPk {
		private final AclEntryResourceType resourceType;
		private final Serializable resourceId;
		private final boolean sidPrincipal;
		private final String sidName;
		public String serializeToString() {
			String joined = resourceType + "|" + resourceId + "|" + (sidPrincipal ? 0 : 1) + "|" + sidName;
			return Base64.getEncoder().encodeToString(joined.getBytes());
		}
		public static AclEntryPk deserializeFromString(String str) {
			String decoded = new String(Base64.getDecoder().decode(str));
			String[] parts = decoded.split("\\|");
			return new AclEntryPk(
					AclEntryResourceType.valueOf(parts[0]),
					Long.parseLong(parts[1]),
					"1".equals(parts[2]),
					parts[3]);
		}
	}

}
