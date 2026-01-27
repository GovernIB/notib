package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import es.caib.notib.logic.intf.dto.config.EntitatConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
	descriptionField = ConfigResource.Fields.description,
	quickFilterFields = {ConfigResource.Fields.key, ConfigResource.Fields.description},
	accessConstraints = @ResourceAccessConstraint(
		type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
		roles = {BaseConfig.ROLE_SUPER},
		grantedPermissions = {PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE}
	)
)
public class ConfigResource extends BaseResource<Long> {

	private String key;
	private String value;
	private String description;
	private boolean jbossProperty;
	private List<EntitatConfig> entitatsConfig;
	private String entitatCodi;
	private String entitatValue;
	private boolean configurable;

	private String typeCode;
	private List<String> validValues;

}
