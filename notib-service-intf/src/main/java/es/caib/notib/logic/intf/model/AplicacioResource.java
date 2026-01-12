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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalTime;

/**
 * Informació d'una aplicació.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		descriptionField = AplicacioResource.Fields.callbackUrl,
		accessConstraints = @ResourceAccessConstraint(
				type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
				roles = { BaseConfig.ROLE_SUPER },
				grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE }
		)
)
public class AplicacioResource extends BaseResource<Long> {

	@NotNull
	@Size(max = 64)
	private String usuariCodi;
	@NotNull
	@Size(max = 256)
	private String callbackUrl;
	private Boolean activa = true;
	private Boolean headerCsrf;
	@NotNull
	private LocalTime horariLaboralInici;
	@NotNull
	private LocalTime horariLaboralFi;
	@NotNull
	private Integer maxEnviamentsMinutLaboral;
	@NotNull
	private Integer maxEnviamentsMinutNoLaboral;
	@NotNull
	private Integer maxEnviamentsDiaLaboral;
	@NotNull
	private Integer maxEnviamentsDiaNoLaboral;

	@NotNull
	private ResourceReference<EntitatResource, Long> entitat;

}
