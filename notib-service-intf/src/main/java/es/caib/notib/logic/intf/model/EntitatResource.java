package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceArtifact;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.FileReference;
import es.caib.notib.logic.intf.base.model.ResourceArtifactType;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import es.caib.notib.logic.intf.base.validation.CustomValidation;
import es.caib.notib.logic.intf.dto.EntitatTipusEnumDto;
import es.caib.notib.logic.intf.dto.TipusDocumentEnumDto;
import es.caib.notib.logic.intf.dto.entitat.EntitatActionParams;
import es.caib.notib.logic.intf.model.validator.entitat.CodiDir3EntitatNoRepetit;
import es.caib.notib.logic.intf.model.validator.entitat.CodiEntitatNoRepetit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Informació d'una entitat.
 * Permisos:
 *   - PERM0: usuari
 *   - PERM2: administrador
 *   - PERM3: aplicació
 *   - PERMX: administrador només lectura
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		descriptionField = EntitatResource.Fields.nom,
		quickFilterFields = { EntitatResource.Fields.codi, EntitatResource.Fields.nom },
		accessConstraints = {
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { BaseConfig.ROLE_SUPER },
						grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE }
				),
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { BaseConfig.ROLE_ADMIN },
						grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE }
				),
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { BaseConfig.ROLE_USER },
						grantedPermissions = { PermissionEnum.READ }
				),
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = { BaseConfig.ROLE_ADMIN_LECTURA },
					grantedPermissions = { PermissionEnum.READ }
				),
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = { BaseConfig.ROLE_ORGAN },
					grantedPermissions = { PermissionEnum.READ }
				),
		},
		artifacts = {
			@ResourceArtifact(
				type = ResourceArtifactType.FILTER,
				code = EntitatResource.FILTER_CODE,
				formClass = EntitatResource.EntitatResourceFilter.class
			),
			@ResourceArtifact(
				type = ResourceArtifactType.PERSPECTIVE,
				code = EntitatResource.PERSPECTIVE_PERMISSIONS
			),
			@ResourceArtifact(
				type = ResourceArtifactType.ACTION,
				code = EntitatResource.ACTION_LLIBRE_ENTITAT,
				formClass = EntitatActionParams.class,
				accessConstraints = {
					@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { BaseConfig.ROLE_SUPER }
					)
				}
			),
			@ResourceArtifact(
				type = ResourceArtifactType.ACTION,
				code = EntitatResource.ACTION_OFICINA_ENTITAT,
				formClass = EntitatActionParams.class,
				accessConstraints = {
					@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { BaseConfig.ROLE_SUPER }
					)
				}
			)
		}

)
@CustomValidation.List({
	@CustomValidation(
		customValidatorType = CodiEntitatNoRepetit.class,
		targetFields = EntitatResource.Fields.codi,
		springBean = true,
		message = "{es.caib.notib.validation.CodiEntitatNoRepetit.message}"),
	@CustomValidation(
		customValidatorType = CodiDir3EntitatNoRepetit.class,
		targetFields = EntitatResource.Fields.dir3Codi,
		springBean = true,
		message = "{es.caib.notib.validation.CodiDir3EntitatNoRepetit.message}")
})
public class EntitatResource extends BaseResource<Long> {

	public static final String FILTER_CODE = "FILTER_ENTITAT";
	public static final String PERSPECTIVE_PERMISSIONS = "PERMISSIONS";
	public static final String ACTION_LLIBRE_ENTITAT = "LLIBRE_ENTITAT";
	public static final String ACTION_OFICINA_ENTITAT = "OFICINES_ENTITAT";

	@NotNull
	@Size(max = 64)
	@EqualsAndHashCode.Include
	private String codi;
	@NotNull
	@Size(max = 256)
	private String nom;
	@NotNull
	private EntitatTipusEnumDto tipus;
	@NotNull
	@Size(max = 9)
	@EqualsAndHashCode.Include
	private String dir3Codi;
	@Size(max = 9)
	private String dir3CodiReg;
	@NotNull
	@Size(max = 64)
	private String apiKey;
	private boolean ambEntregaDeh;
	@Size(max = 1024)
	private String descripcio;
	private boolean activa = true;
	@Size(max = 1024)
	private String colorFons;
	@Size(max = 1024)
	private String colorLletra;
	private TipusDocumentEnumDto tipusDocDefault;
	private boolean llibreEntitat;
	@Size(max = 255)
	protected String llibre;
	@Size(max = 255)
	protected String llibreNom;
	private boolean oficinaEntitat;
	@Size(max = 255)
	private String oficina;
	@Size(max = 255)
	private String nomOficinaVirtual;

	private FileReference logoCapsalera;
	//private boolean eliminarLogoCap;
	//private byte[] logoPeuBytes;
	//private boolean eliminarLogoPeu;

	private boolean entregaCieActiva;
	private ResourceReference<EntregaCieResource, Long> entregaCie;
	// Camps per emplenar els valors del formulari referent a la entrega CIE
	private ResourceReference<PagadorCieResource, Long> entregaCiePagadorCie;
	private ResourceReference<PagadorPostalResource, Long> entregaCiePagadorPostal;

	// Camps calculats
	private Integer tipusDocCount;
	private Integer aplicacioCount;
	private Integer aclEntryCount;
	private boolean crearNotificacions;
	private boolean crearComunicacions;
	private boolean crearSir;

	public String getLlibreCodiNom() {
		return llibre + " " + llibreNom;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class EntitatResourceFilter implements Serializable {
		private String codi;
		private String nom;
		private EntitatTipusEnumDto tipus;
		private String dir3Codi;
		private boolean activa;
	}

}
