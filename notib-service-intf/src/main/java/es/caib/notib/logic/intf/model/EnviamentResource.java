package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(
        descriptionField = "id",
        accessConstraints = @ResourceAccessConstraint(
                type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
                roles = { BaseConfig.ROLE_ADMIN },
                grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE }
        )
)
public class EnviamentResource extends BaseResource<Long> {
    private Boolean entregaPostalActiva = true;
    @Transient
    private Boolean perEmail = true;
    @Transient
    @NotNull
    private String campProva;
}
