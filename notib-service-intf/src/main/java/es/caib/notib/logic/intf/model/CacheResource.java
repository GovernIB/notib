package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceArtifact;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceArtifactType;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
	descriptionField = CacheResource.Fields.descripcio,
	accessConstraints = { @ResourceAccessConstraint(
		type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
		roles = { BaseConfig.ROLE_SUPER },
		grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE }
	)},
	artifacts = {
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = CacheResource.ACTION_BUIDAR_CACHE,
			formClass = SeleccioStringForm.class,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = {BaseConfig.ROLE_SUPER}
				)
			}
		),
	}
)
public class CacheResource extends BaseResource<String> {

	public static final String ACTION_BUIDAR_CACHE = "BUIDAR_CACHE";

	private String codi;
	private String descripcio;
	private long localHeapSize;
}
