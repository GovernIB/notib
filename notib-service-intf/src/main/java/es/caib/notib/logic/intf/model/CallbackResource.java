package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import es.caib.notib.logic.intf.dto.CallbackEstatEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.Date;

/**
 * Callbacks d'una remesa
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
	accessConstraints = @ResourceAccessConstraint(
		type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
		roles = { BaseConfig.ROLE_USER, BaseConfig.ROLE_ADMIN }, // TODO REPASSAR ACCES PER ADMIN LECTURA I ORGAN
		grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE }
	)
)
public class CallbackResource extends BaseResource<Long> {

	private String usuariCodi;
	private Long notificacioId;
	private Long enviamentId;
	private Date dataCreacio;
	private Date ultimIntent;
	private Date data;
	private boolean error;
	private String errorDesc;
	private CallbackEstatEnumDto estat;
	private int intents;
	private boolean pausat;
}
