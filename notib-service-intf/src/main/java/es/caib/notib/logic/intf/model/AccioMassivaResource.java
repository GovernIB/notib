package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceArtifact;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceArtifactType;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaElementEstat;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus;
import es.caib.notib.logic.intf.dto.accioMassiva.SeleccioTipus;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaEstatDto;
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
		@ResourceArtifact(
			type = ResourceArtifactType.FILTER,
			code = AccioMassivaResource.FILTER_CODE,
			formClass = AccioMassivaResource.AccioMassivaResourceFilter.class
		),
	}
)
public class AccioMassivaResource extends BaseResource<Long>  {

	public static final String FILTER_CODE = "FILTER_ACCIO_MASSIVA";
	public static final String ALTA_ACCIO_MASSIVA = "ALTA_ACCIO_MASSIVA";

	private AccioMassivaTipus tipus;
	private String createdBy;
	private String usuariNomComplet;
	private LocalDateTime createdDate;
	private Date dataInici;
	private Date dataFi;
	private Boolean error;
	private int numOk;
	private int numErrors;
	private int numPendent;
	private String errorDescripcio;
	private String excepcioStacktrace;
	private SeleccioTipus tipusElementSeleccionat;
	private String motiu;
	private boolean adminEntitat;
	private int dies;
	private String estat;
	private ResourceReference<AccioMassivaElementResource, Long> elements;


	@Getter
	@Setter
	@NoArgsConstructor
	public static class AccioMassivaResourceFilter implements Serializable {

		private AccioMassivaTipus tipus;
		private Date dataIniciInici;
		private Date dataIniciFi;
		private String createdBy;
		private AccioMassivaElementEstat estat;
	}
}
