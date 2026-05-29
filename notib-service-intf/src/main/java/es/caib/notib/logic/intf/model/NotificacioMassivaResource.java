package es.caib.notib.logic.intf.model;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceArtifact;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceArtifactType;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaEstatDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaInfoDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

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
			roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA, BaseConfig.ROLE_ORGAN },
			grantedPermissions = { PermissionEnum.READ }
		),
		@ResourceAccessConstraint(
			type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
			roles = { BaseConfig.ROLE_USER },
			grantedPermissions = { PermissionEnum.READ, PermissionEnum.CREATE }
		),
	},
	artifacts = {
		@ResourceArtifact(
			type = ResourceArtifactType.FILTER,
			code = NotificacioMassivaResource.FILTER_CODE,
			formClass = NotificacioMassivaResource.NotificacioMassivaResourceFilter.class
		),
		@ResourceArtifact(
			type = ResourceArtifactType.REPORT,
			code = NotificacioMassivaResource.REPORT_DESCARREGAR_CSV_NOTIFICACIO_MASSIVA,
			requiresId = true
		),
		@ResourceArtifact(
			type = ResourceArtifactType.REPORT,
			code = NotificacioMassivaResource.REPORT_DESCARREGAR_ZIP_NOTIFICACIO_MASSIVA,
			requiresId = true
		),
		@ResourceArtifact(
			type = ResourceArtifactType.REPORT,
			code = NotificacioMassivaResource.REPORT_DESCARREGAR_RESUM_NOTIFICACIO_MASSIVA,
			requiresId = true
		),
		@ResourceArtifact(
			type = ResourceArtifactType.REPORT,
			code = NotificacioMassivaResource.REPORT_DESCARREGAR_ERRORS_VALIDACIO_NOTIFICACIO_MASSIVA,
			requiresId = true
		),
		@ResourceArtifact(
			type = ResourceArtifactType.REPORT,
			code = NotificacioMassivaResource.REPORT_DESCARREGAR_ERRORS_EXECUCIO_NOTIFICACIO_MASSIVA,
			requiresId = true
		),
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = NotificacioMassivaResource.ACTION_POSPOSAR_NOTIFICACIO_MASSIVA,
			requiresId = true

		),
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = NotificacioMassivaResource.ACTION_REACTIVAR_NOTIFICACIO_MASSIVA,
			requiresId = true
		),
		@ResourceArtifact(
			type = ResourceArtifactType.PERSPECTIVE,
			code = NotificacioMassivaResource.PERSPECTIVE_RESUM_NOTIFACIO_MASSIVA
		),
	}
)
//@CustomValidation.List({
//	@CustomValidation(
//		customValidatorType = PrimerEnviamentCodiDir3ObligatoriEnviamentTipusSir.class),
//	@CustomValidation(
//		customValidatorType = NotificacioProcedimentNotNull.class,
//		targetFields = NotificacioResource.Fields.procediment,
//		springBean = true),
//})
public class NotificacioMassivaResource extends BaseResource<Long>  {

	public static final String FILTER_CODE = "FILTER_NOTIFICACIO_MASSIVA";
	public static final String REPORT_DESCARREGAR_CSV_NOTIFICACIO_MASSIVA = "DESCARREGAR_FITXER_CSV_NOTIFICACIO_MASSIVA";
	public static final String REPORT_DESCARREGAR_ZIP_NOTIFICACIO_MASSIVA = "DESCARREGAR_FITXER_ZIP_NOTIFICACIO_MASSIVA";
	public static final String REPORT_DESCARREGAR_RESUM_NOTIFICACIO_MASSIVA = "DESCARREGAR_FITXER_RESUM_NOTIFICACIO_MASSIVA";
	public static final String REPORT_DESCARREGAR_ERRORS_VALIDACIO_NOTIFICACIO_MASSIVA = "DESCARREGAR_FITXER_ERRORS_VALIDACIO_NOTIFICACIO_MASSIVA";
	public static final String REPORT_DESCARREGAR_ERRORS_EXECUCIO_NOTIFICACIO_MASSIVA = "DESCARREGAR_FITXER_ERRORS_EXECUCIO_NOTIFICACIO_MASSIVA";
	public static final String ACTION_POSPOSAR_NOTIFICACIO_MASSIVA = "POSPOSAR_NOTIFICACIO_MASSIVA";
	public static final String ACTION_REACTIVAR_NOTIFICACIO_MASSIVA = "REACTIVAR_NOTIFICACIO_MASSIVA";
	public static final String PERSPECTIVE_RESUM_NOTIFACIO_MASSIVA = "RESUM_NOTIFACIO_MASSIVA";

	//	private final Object procesLock = new Object();
	private LocalDateTime createdDate;
	private String createdBy;
	private Integer progress = 0;
	private String csvFilename;
	private String zipFilename;
	private String csvGesdocId;
	private String zipGesdocId;
	private String resumGesdocId;
	private String errorsGesdocId;
	protected Date caducitat;
	private String email;
	private NotificacioMassivaEstatDto estatValidacio;
	private NotificacioMassivaEstatDto estatProces;
	private Integer totalNotificacions;
	private Integer notificacionsValidades;
	private Integer notificacionsProcessades;
	private Integer notificacionsProcessadesAmbError;
	private Integer notificacionsCancelades;

	private ResourceReference<EntitatResource, Long> entitat;
	private ResourceReference<PagadorPostalResource, Long> pagadorPostal;
	private List<NotificacioResource> notificacions;
	private List<NotificacioMassivaInfoDto.NotificacioInfo> resum;

	@Getter
	@Setter
	@NoArgsConstructor
	public static class NotificacioMassivaResourceFilter implements Serializable {

		private Date dataIniciInici;
		private Date dataIniciFi;
		private NotificacioMassivaEstatDto estatProces;
		private String createdBy;
	}
}
