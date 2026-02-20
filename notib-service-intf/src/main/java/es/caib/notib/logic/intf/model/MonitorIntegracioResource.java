package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import es.caib.notib.logic.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
	descriptionField = ConfigResource.Fields.description,
	quickFilterFields = { ConfigResource.Fields.key, ConfigResource.Fields.description },
	accessConstraints = @ResourceAccessConstraint(
		type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
		roles = { BaseConfig.ROLE_SUPER},
		grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE }
	)
)
public class MonitorIntegracioResource extends BaseResource<Long> {

	private IntegracioCodi codi;
	private Date data;
	@Size(max = 1024)
	private String descripcio;
	private IntegracioAccioTipusEnumDto tipus;
	private Long tempsResposta;
	private IntegracioAccioEstatEnumDto estat = IntegracioAccioEstatEnumDto.OK;
	@Size(max = 1024)
	private String errorDescripcio;
	@Size(max = 1024)
	private String excepcioMessage;
	@Size(max = 2048)
	private String excepcioStacktrace;

	private ResourceReference<AplicacioResource, Long> aplicacio;
	private ResourceReference<NotificacioResource, Long> notificacio;
	private ResourceReference<EntitatResource, Long> entitat;
	private ResourceReference<UsuariResource, String> usuari;
	private ResourceReference<MonitorIntegracioParamResource, Long> parametres;
}
