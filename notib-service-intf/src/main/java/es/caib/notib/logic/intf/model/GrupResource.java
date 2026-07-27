package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Informació d'un grup.
 *
 * @author Límit Tecnologies
 */
@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
	descriptionField = GrupResource.Fields.codi,
	quickFilterFields = { GrupResource.Fields.codi, GrupResource.Fields.nom },
	accessConstraints = {
		@ResourceAccessConstraint(
			type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
			roles = {BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_SUPER, BaseConfig.ROLE_ORGAN},
			grantedPermissions = {PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE}
		),
		@ResourceAccessConstraint(
			type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
			roles = { BaseConfig.ROLE_ADMIN_LECTURA },
			grantedPermissions = { PermissionEnum.READ }
		)
	}
)
public class GrupResource extends BaseResource<Long> {

	@NotNull
	@Size(max = 64)
	@EqualsAndHashCode.Include
	private String codi;
	@NotNull
	@Size(max = 100)
	private String nom;

	private ResourceReference<EntitatResource, Long> entitat;
	private ResourceReference<OrganGestorResource, Long> organGestor;

}
