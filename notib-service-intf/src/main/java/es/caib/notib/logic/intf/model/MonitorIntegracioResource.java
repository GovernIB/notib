package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceArtifact;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceArtifactType;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import es.caib.notib.logic.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.validation.constraints.Size;
import java.io.Serializable;
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
	),
	artifacts = @ResourceArtifact(
		type = ResourceArtifactType.REPORT,
		code = MonitorIntegracioResource.REPORT_AGRUPACIONS
	)
)
public class MonitorIntegracioResource extends BaseResource<Long> {

	public static final String REPORT_AGRUPACIONS = "AGRUPACIONS";

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
	@Size(max = 64)
	private String aplicacio;
	private String codiEntitat;
	private Long notificacioId;
	private String codiUsuari;

	private ResourceReference<MonitorIntegracioParamResource, Long> parametres;

	@Getter
	@Setter
	@AllArgsConstructor
	public static class MonitorIntegracioAgrupacioItem implements Serializable {
		private IntegracioCodi grup;
		private long countOk;
		private long countWarn;
		private long countError;
		public long getCountTotal() {
			return countOk + countWarn + countError;
		}
	}

}
