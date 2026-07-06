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
	accessConstraints = {
		@ResourceAccessConstraint(
			type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
			roles = { BaseConfig.ROLE_SUPER },
			grantedPermissions = { PermissionEnum.READ }
		)
	},
	artifacts = {
		@ResourceArtifact(
			type = ResourceArtifactType.REPORT,
			code = MetriquesResource.REPORT_DESCARREGAR_METRIQUES_JSON,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = { BaseConfig.ROLE_SUPER }
				)
			}
		)
	}
)
public class MetriquesResource extends BaseResource<String> {

	public static final String REPORT_DESCARREGAR_METRIQUES_JSON = "DESCARREGAR_METRIQUES_JSON";

	private String metriques;
}
