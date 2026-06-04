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
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
	//descriptionField = NotificacioResource.Fields.codi,
	//quickFilterFields = { NotificacioResource.Fields.codi, NotificacioResource.Fields.nom },
	accessConstraints = {
		@ResourceAccessConstraint(
			type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
			roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA, BaseConfig.ROLE_ORGAN,  },
			grantedPermissions = { PermissionEnum.READ, PermissionEnum.CREATE}
		),
		@ResourceAccessConstraint(
			type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
			roles = { BaseConfig.ROLE_USER },
			grantedPermissions = { PermissionEnum.CREATE }
		),
	},
	artifacts = {

	}
)
public class AccioMassivaElementResource extends BaseResource<Long>  {

	private Long elementId;
	private Date dataExecucio;
	private String errorDescripcio;
	private String excepcioStackTrace;
	private ResourceReference<AccioMassivaResource, Long> accioMassiva;
	private String referencia;
	private boolean executadaOk;
	private boolean pendent;

	public boolean isExecutadaOk() {
		return dataExecucio != null && StringUtils.isEmpty(errorDescripcio) && StringUtils.isEmpty(excepcioStackTrace);
	}

	public boolean isPendent() {
		return dataExecucio == null;
	}
}
