package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceArtifact;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceArtifactType;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.Date;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
	descriptionField = ActiveMqResource.Fields.descripcio,
	accessConstraints = { @ResourceAccessConstraint(
		type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
		roles = { BaseConfig.ROLE_SUPER },
		grantedPermissions = { PermissionEnum.READ }
	)},
	artifacts = {
		@ResourceArtifact(
			type = ResourceArtifactType.REPORT,
			code = ActiveMqResource.REPORT_DESCARREGAR_JOB_SCHEDULER_JSON,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = { BaseConfig.ROLE_SUPER }
				)
			}
		),
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = ActiveMqDetailResource.ACTION_ESBORRAR_MISSATGE,
			formClass = SeleccioStringForm.class,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = {BaseConfig.ROLE_SUPER}
				)
			}
		)
	}
)
public class ActiveMqDetailResource extends BaseResource<String> {

	public static final String ACTION_ESBORRAR_MISSATGE = "ESBORRAR_MISSATGE";

	private String id;
	private String uuid;
	private String notificacioUuId;
	private Date data;
}
