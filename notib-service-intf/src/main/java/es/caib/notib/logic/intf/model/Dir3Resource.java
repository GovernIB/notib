package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceArtifact;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.annotation.ResourceField;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceArtifactType;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import es.caib.notib.logic.intf.base.validation.CustomValidation;
import es.caib.notib.logic.intf.model.validator.CodiODenominacioNotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;

/**
 * Informació d'unitats organitzatives que provenen directament de DIR3.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		descriptionField = Dir3Resource.Fields.denominacio,
		accessConstraints = @ResourceAccessConstraint(
			type = ResourceAccessConstraint.ResourceAccessConstraintType.AUTHENTICATED,
			grantedPermissions = { PermissionEnum.READ }
		),
	artifacts = {
		@ResourceArtifact(
			type = ResourceArtifactType.FILTER,
			code = Dir3Resource.FILTER_CODE,
			formClass = Dir3Resource.Dir3ResourceFilter.class)
	}
)
public class Dir3Resource extends BaseResource<String> {

	public static final String FILTER_CODE = "FILTER_DIR3";

	private String codi;
	private String denominacio;
	private String denominacionCooficial;
	private String estat;
	private Integer versio;
	private String cif;
	private boolean sir;
	private boolean permetreSir;

	// Camps calculats
	private boolean selectable;
	private boolean noCif;
	private boolean noSir;
	private boolean viaValib;

	@Getter
	@Setter
	@FieldNameConstants
	@NoArgsConstructor
	@CustomValidation.List({
		@CustomValidation(
			customValidatorType = CodiODenominacioNotNull.class,
			targetFields = { Dir3ResourceFilter.Fields.codi, Dir3ResourceFilter.Fields.denominacio }
		)
	})
	public static class Dir3ResourceFilter implements Serializable {
		private String codi;
		private String denominacio;
		@ResourceField(enumType = true)
		private Long nivellAdministracio;
		@ResourceField(enumType = true)
		private Long comunitatAutonoma;
		@ResourceField(enumType = true)
		private String provincia;
		@ResourceField(enumType = true)
		private String municipi;
		private Boolean ambOficines;
		private Boolean esUnitatArrel;
	}

}
