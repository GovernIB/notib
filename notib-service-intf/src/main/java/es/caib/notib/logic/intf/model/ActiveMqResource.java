package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
	descriptionField = ActiveMqResource.Fields.descripcio,
	accessConstraints = @ResourceAccessConstraint(
		type = ResourceAccessConstraint.ResourceAccessConstraintType.AUTHENTICATED,
		grantedPermissions = { PermissionEnum.READ }
	)
)
public class ActiveMqResource extends BaseResource<String> {

	private String nom;
	private String descripcio;
	private long mida;
	private long consumersCount;
	private long enqueueCount;
	private long dequeueCount;
	private long forwardCount;
	private long inFlightCount;
	private long expiredCount;
	private long storeMessageSize;
}
