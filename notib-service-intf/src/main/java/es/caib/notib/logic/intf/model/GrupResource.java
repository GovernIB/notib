package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        descriptionField = GrupResource.Fields.codi,
        quickFilterFields = {GrupResource.Fields.codi, GrupResource.Fields.nom},
        accessConstraints = @ResourceAccessConstraint(
                type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
                roles = { BaseConfig.ROLE_ADMIN },
                grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE }
        )
)
public class GrupResource extends BaseResource<Long> {

        @NotNull
        @Size(max = 64)
        @EqualsAndHashCode.Include
        private String codi;
        @NotNull
        @Size(max = 100)
        private String nom;
        private Long entitatId;
        private Long organGestorId;
        private String organGestorCodi;

        public String getNomIRol() {
                return nom + " (" + codi + ")";
        }
}
