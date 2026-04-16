package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
	descriptionField = EventResource.Fields.tipus,
	quickFilterFields = EventResource.Fields.tipus,
	accessConstraints = @ResourceAccessConstraint(
		type = ResourceAccessConstraint.ResourceAccessConstraintType.AUTHENTICATED,
		grantedPermissions = { PermissionEnum.READ, PermissionEnum.CREATE }
	)
)
public class EventResource extends BaseResource<Long>  {

	private NotificacioEventTipusEnumDto tipus;
	private Date data = new Date();
	private boolean error = false;
	private String errorDescripcio;
	protected Boolean fiReintents;
	protected int intents;

	@NotNull
	private ResourceReference<NotificacioResource, Long> notificacio;
	@NotNull
	private ResourceReference<NotificacioEnviamentResource, Long>  enviament;


}
