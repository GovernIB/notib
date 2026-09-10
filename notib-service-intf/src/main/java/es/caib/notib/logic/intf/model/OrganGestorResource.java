package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceArtifact;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceArtifactType;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.dto.organisme.TipusTransicioEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Informació d'un òrgan gestor.
 * Permisos:
 *   - ADMIN, READ: com sempre
 *   - PERM1: processar (poden marcar una notificació com a processada)
 *   - PERM2: gestionar (poden editar)
 *   - PERM3: procediments/serveis comuns
 *   - PERM4: notificacions
 *   - PERM5: comunicacions
 *   - PERM6: comunicacions SIR
 *   - PERM7: comunicacions sense procediment
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
	descriptionField = OrganGestorResource.Fields.codiNom,
	quickFilterFields = { OrganGestorResource.Fields.codi, OrganGestorResource.Fields.nom },
	accessConstraints = {
		@ResourceAccessConstraint(
			type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
			roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ORGAN },
			grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE }
		),
		@ResourceAccessConstraint(
			type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
			roles = { BaseConfig.ROLE_ADMIN_LECTURA },
			grantedPermissions = { PermissionEnum.READ }
		)
	},
	artifacts = {
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = OrganGestorResource.DIR3_SYNC_ACTION_CODE,
			formClass = OrganGestorResource.OrganGestorDir3SyncForm.class,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = { BaseConfig.ROLE_ADMIN })
			}
		),
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = OrganGestorResource.OFICINES_SYNC_ACTION_CODE,
			formClass = OrganGestorResource.OrganGestorDir3SyncForm.class,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = { BaseConfig.ROLE_ADMIN })
			}
		),
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = OrganGestorResource.ORGANS_PROCEDIMENTS_SYNC_ACTION_CODE,
			formClass = OrganGestorResource.OrganGestorDir3SyncForm.class,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = { BaseConfig.ROLE_ADMIN })
			}
		),
		@ResourceArtifact(
			type = ResourceArtifactType.FILTER,
			code = OrganGestorResource.FILTER_CODE,
			formClass = OrganGestorResource.OrganGestorResourceFilter.class
		),
		@ResourceArtifact(
			type = ResourceArtifactType.PERSPECTIVE,
			code = OrganGestorResource.PERSPECTIVE_TREE,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ORGAN, BaseConfig.ROLE_ADMIN_LECTURA }
				)
			}
		),
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = OrganGestorResource.ACTION_ADMIN_ORGANS_AMB_PERMIS,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = { BaseConfig.ROLE_ORGAN }
				)
			}
		),
		@ResourceArtifact(
			type = ResourceArtifactType.REPORT,
			code = OrganGestorResource.REPORT_DESCARREGAR_DIR3_JSON,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = { BaseConfig.ROLE_ADMIN })
			}
		),
	}
)
public class OrganGestorResource extends BaseResource<Long> {

	public static final String PERSPECTIVE_TREE = "TREE";
	public static final String ACTION_ADMIN_ORGANS_AMB_PERMIS = "ADMIN_ORGANS_AMB_PERMIS";

	public static final String DIR3_SYNC_ACTION_CODE = "DIR3_SYNC";
	public static final String OFICINES_SYNC_ACTION_CODE = "OFICINES_SYNC";
	public static final String ORGANS_PROCEDIMENTS_SYNC_ACTION_CODE = "ORGANS_PROCEDIMENTS_SYNC";
	public static final String REPORT_DESCARREGAR_DIR3_JSON = "REPORT_DESCARREGAR_DIR3_JSON";
	public static final String FILTER_CODE = "FILTER_ORGAN_GESTOR";
	public static final String NAMED_QUERY_PERM_READ = "PERM_READ";
	// Com NAMED_QUERY_PERM_READ, però només retorna els òrgans vigents; s'usa al desplegable de l'alta de
	// notificacions/remeses, on no s'han de poder seleccionar òrgans no vigents.
	public static final String NAMED_QUERY_PERM_READ_VIGENT = "PERM_READ_VIGENT";
	public static final String NAMED_QUERY_PERM_NOT = "PERM_NOT";
	public static final String NAMED_QUERY_PERM_COM = "PERM_COM";
	public static final String NAMED_QUERY_PERM_SIR = "PERM_SIR";

	@NotNull
	@Size(max = 64)
	private String codi;
	@Size(max = 64)
	private String codiPare;
	@NotNull
	@Size(max = 1000)
	private String nom;
	@Size(max = 1000)
	private String nomEs;
	@NotNull
	private OrganGestorEstatEnum estat;
	private String llibre;
	private String llibreNom;
	private String oficina;
	private String oficinaNom;
	private Boolean sir;
	private boolean permetreSir;
	private TipusTransicioEnumDto tipusTransicio;
	private Boolean noVigent;
	private boolean entregaCieDesactivada;
	private boolean entregaCieActiva;
	private boolean sobrescriureCieOrganEmisor;

	private ResourceReference<EntitatResource, Long> entitat;
	private ResourceReference<EntregaCieResource, Long> entregaCie;
	private ResourceReference<OrganGestorResource, Long> pare;

	// Camps calculats
	private String codiNom;
	private String nomPare;
	private Integer aclEntryCount;

	// Camps de la perspectiva TREE
	private ResourceReference<OrganGestorResource, Long>[] path;
	private Long childCount;

	// Camps per emplenar els valors del formulari referent a la entrega CIE
	private ResourceReference<PagadorCieResource, Long> entregaCiePagadorCie;
	private ResourceReference<PagadorPostalResource, Long> entregaCiePagadorPostal;

	@Getter
	@Setter
	@NoArgsConstructor
	public static class OrganGestorDir3SyncForm implements Serializable {
		private Boolean simular;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class OrganGestorResourceFilter implements Serializable {

		private String codi;
		private String codiPare;
		private ResourceReference<OrganGestorResource, Long> pare;
		private String nom;
		private OrganGestorEstatEnum estat;
		private String llibre;
		private boolean permetreSir;
		private boolean entregaCieActiva;
	}

}
