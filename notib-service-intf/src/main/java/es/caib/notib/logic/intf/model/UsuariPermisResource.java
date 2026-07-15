package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceArtifact;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.ResourceArtifactType;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;

@Builder
@Getter
@Setter
@AllArgsConstructor
@FieldNameConstants
@ResourceConfig(
	descriptionField = ActiveMqResource.Fields.descripcio,
	accessConstraints = { @ResourceAccessConstraint(
		type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
		roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA, BaseConfig.ROLE_ORGAN },
		grantedPermissions = { PermissionEnum.READ }
	)},
	artifacts = {
		@ResourceArtifact(
			type = ResourceArtifactType.FILTER,
			code = UsuariPermisResource.FILTER_CODE,
			formClass = UsuariPermisResource.UsuariPermisResourceFilter.class),
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = UsuariPermisResource.ACTION_GET_PERMISOS_USUARI,
			formClass = UsuariPermisResource.PermisUsuariForm.class,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA, BaseConfig.ROLE_ORGAN }
				)
			}
		)
	}

)
public class UsuariPermisResource extends UsuariResource {

	public static final String FILTER_CODE = "FILTER_USUARI_PERMIS";
	public static final String ACTION_GET_PERMISOS_USUARI = "GET_PERMISOS_USUARI";


	@Getter
	@Setter
	@NoArgsConstructor
	public static class UsuariPermisResourceFilter implements Serializable {

		private String codi;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class PermisUsuariForm implements Serializable {

		private String codi;
	}
}
