package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceArtifact;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.annotation.ResourceField;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceArtifactType;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import es.caib.notib.logic.intf.base.validation.CustomValidation;
import es.caib.notib.logic.intf.dto.ProcSerTipusEnum;
import es.caib.notib.logic.intf.model.validator.procediment.CodiProcedimentNoRepetit;
import es.caib.notib.logic.intf.model.validator.procediment.NomProcedimentNoRepetit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * Informació d'un procediment.
 * Permisos:
 *   - READ: consulta
 *   - PERM4: processar
 *   - ADMIN: gestionar
 *   - PERM5: notificacions
 *   - PERM8: comunicacions
 *   - PERM7: comunicacions SIR
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
	descriptionField = ProcedimentResource.Fields.codiNom,
	quickFilterFields = { ProcedimentResource.Fields.codi, ProcedimentResource.Fields.nom },
	accessConstraints = {
		@ResourceAccessConstraint(
			type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
			roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ORGAN},
			grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE }
		),
		@ResourceAccessConstraint(
			type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
			roles = { BaseConfig.ROLE_ADMIN_LECTURA },
			grantedPermissions = { PermissionEnum.READ }
		),
	},
	artifacts = {
		@ResourceArtifact(
			type = ResourceArtifactType.FILTER,
			code = ProcedimentResource.FILTER_CODE,
			formClass = ProcedimentResource.ProcedimentResourceFilter.class)
	}
)
@CustomValidation.List({
	@CustomValidation(
		customValidatorType = CodiProcedimentNoRepetit.class,
		targetFields = ProcedimentResource.Fields.codi,
		springBean = true,
		message = "{es.caib.notib.validation.CodiProcedimentNoRepetit.message}"),
	@CustomValidation(
		customValidatorType = NomProcedimentNoRepetit.class,
		targetFields = ProcedimentResource.Fields.nom,
		springBean = true,
		message = "{es.caib.notib.validation.NomProcedimentNoRepetit.message}")
})
public class ProcedimentResource extends BaseResource<Long> {

	public static final String FILTER_CODE = "FILTER_PROCEDIMENT";

	@NotNull
	private ProcSerTipusEnum tipus;
	@NotNull
	@Size(max = 64)
	private String codi;
	@NotNull
	@Size(max = 256)
	private String nom;
	private Integer retard;
	private Integer caducitat;
	@Size(max = 255)
	private String tipusAssumpte;
	@Size(max = 255)
	private String tipusAssumpteNom;
	@Size(max = 255)
	private String codiAssumpte;
	@Size(max = 255)
	private String codiAssumpteNom;
	private boolean agrupar;
	@ResourceField(onChangeActive = true)
	private boolean comu;
	private boolean requireDirectPermission;
	private boolean manual;
	private boolean organNoSincronitzat;
	private boolean actiu;
	private Date ultimaActualitzacio;
	private boolean entregaCieActiva;

	private ResourceReference<EntitatResource, Long> entitat;
	@NotNull
	private ResourceReference<OrganGestorResource, Long> organGestor;
	private ResourceReference<EntregaCieResource, Long> entregaCie;

	// Camps calculats
	private Integer grupCount;
	private Integer aclEntryCount;
	private boolean fieldOrganGestorDisabled;
	private boolean fieldEntregaCieHidden;
	private String codiNom;

	// Camps per emplenar els valors del formulari referent a la entrega CIE
	private ResourceReference<PagadorCieResource, Long> entregaCiePagadorCie;
	private ResourceReference<PagadorPostalResource, Long> entregaCiePagadorPostal;

	public boolean isEntregaCieActiva() {
		return entregaCieActiva || entregaCie != null;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class ProcedimentResourceFilter implements Serializable {
		private String codi;
		private String nom;
		private boolean comu;
		private boolean requireDirectPermission;
		private boolean manual;
		private boolean actiu;
		private ResourceReference<OrganGestorResource, Long> organGestor;
		private boolean entregaCieActiva;
	}

}
