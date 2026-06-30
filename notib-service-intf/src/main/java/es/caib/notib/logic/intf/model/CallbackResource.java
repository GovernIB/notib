package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.AccioMassivaParams;
import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceArtifact;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceArtifactType;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import es.caib.notib.logic.intf.dto.CallbackEstatEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.util.Date;

/**
 * Callbacks pendents d'una remesa
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
		roles = { BaseConfig.ROLE_ADMIN }, // TODO REPASSAR ACCES PER ADMIN LECTURA I ORGAN
		grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.DELETE }
	),
	artifacts = {
		@ResourceArtifact(
			type = ResourceArtifactType.FILTER,
			code = CallbackResource.FILTER_CODE,
			formClass = CallbackResource.CallbackPendentsFilter.class
		),
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = CallbackResource.ACTION_ENVIAR_CALLBACK_PENDENT,
			requiresId = true,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = {BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ORGAN}
				)
			}
		),
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = CallbackResource.ACTION_PAUSAR_CALLBACK_PENDENT,
			requiresId = true,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = {BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ORGAN}
				)
			}
		),
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = CallbackResource.ACTION_ACTIVAR_CALLBACK_PENDENT,
			requiresId = true,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = {BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ORGAN}
				)
			}
		),
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = CallbackResource.ACTION_ENVIAR_CALLBACK_PENDENT_MASSIU,
			formClass = AccioMassivaParams.class,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = { BaseConfig.ROLE_ADMIN }
				)
			}
		),
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = CallbackResource.ACTION_PAUSAR_CALLBACK_PENDENT_MASSIU,
			formClass = AccioMassivaParams.class,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = { BaseConfig.ROLE_ADMIN }
				)
			}
		),
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = CallbackResource.ACTION_ACTIVAR_CALLBACK_PENDENT_MASSIU,
			formClass = AccioMassivaParams.class,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = { BaseConfig.ROLE_ADMIN }
				)
			}
		),
	}
)
public class CallbackResource extends BaseResource<Long> {

	public static final String FILTER_CODE = "FILTER_CALLBACK_PENDENTS";

	public static final String ACTION_ENVIAR_CALLBACK_PENDENT = "ENVIAR_CALLBACK_PENDENT";
	public static final String ACTION_PAUSAR_CALLBACK_PENDENT = "PAUSAR_CALLBACK_PENDENT";
	public static final String ACTION_ACTIVAR_CALLBACK_PENDENT = "ACTIVAR_CALLBACK_PENDENT";
	public static final String ACTION_ENVIAR_CALLBACK_PENDENT_MASSIU = "ENVIAR_CALLBACK_PENDENT_MASSIU";
	public static final String ACTION_PAUSAR_CALLBACK_PENDENT_MASSIU = "PAUSAR_CALLBACK_PENDENT_MASSIU";
	public static final String ACTION_ACTIVAR_CALLBACK_PENDENT_MASSIU = "ACTIVAR_CALLBACK_PENDENT_MASSIU";

	private String usuariCodi;
	private Long notificacioId;
	private Long enviamentId;
	private Date dataCreacio;
	private Date ultimIntent;
	private Date properIntent;
	private Date data;
	private boolean error;
	private String errorDesc;
	private CallbackEstatEnumDto estat;
	private int intents;
	private boolean pausat;
	private String endpoint;
	private String notificacioReferencia;
	private int maxIntents;


	@Getter
	@Setter
	@NoArgsConstructor
	public static class CallbackPendentsFilter implements Serializable {

		private String usuariCodi;
		private Date dataCreacioInici;
		private Date dataCreacioFinal;
		private Date dataUltimIntentInici;
		private Date dataUltimIntentFinal;
		private CallbackEstatEnumDto estat;
		private boolean fiReintents;
	}
}
