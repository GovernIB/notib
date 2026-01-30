package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;

/**
 * Informació de tipus de camp de configuració.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        descriptionField = ConfigTypeResource.Fields.code,
        quickFilterFields = { ConfigTypeResource.Fields.code, ConfigTypeResource.Fields.value },
        accessConstraints = @ResourceAccessConstraint(
                type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
                roles = {  BaseConfig.ROLE_SUPER   },
                grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE }
        )
)
public class ConfigTypeResource extends BaseResource<Long> {

    private String code;
    private String value;
}
