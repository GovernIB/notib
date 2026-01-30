package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.List;

/**
 * Informació d'un grup de propietats de configuració.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
	descriptionField = ConfigResource.Fields.description,
	quickFilterFields = { ConfigResource.Fields.key, ConfigResource.Fields.description },
	accessConstraints = @ResourceAccessConstraint(
		type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
		roles = {BaseConfig.ROLE_SUPER},
		grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE }))
public class ConfigGroupResource extends BaseResource<Long> {

	private String key;
	private ResourceReference<ConfigGroupResource, Long> parent;
	private int position;
	private String description;
}
