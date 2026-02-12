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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Informació d'un òrgan gestor.
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
	artifacts = {
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = OrganGestorResource.DIR3_SYNC_ACTION_CODE,
			formClass = OrganGestorResource.OrganGestorDir3SyncForm.class,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = { BaseConfig.ROLE_ADMIN })
			})
	},
	accessConstraints = {
		@ResourceAccessConstraint(
			type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
			roles = { BaseConfig.ROLE_ADMIN},
			grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE}
		),
		@ResourceAccessConstraint(
			type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
			roles = { BaseConfig.ROLE_ADMIN_LECTURA},
			grantedPermissions = { PermissionEnum.READ }
		),
		@ResourceAccessConstraint(
			type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
			roles = { BaseConfig.ROLE_ORGAN},
			grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE }
		)
	}
	)
public class OrganGestorResource extends BaseResource<Long> {

	public static final String DIR3_SYNC_ACTION_CODE = "DIR3_SYNC";

	@NotEmpty
	@Size(max = 64)
	private String codi;
	@Size(max = 64)
	private String codiPare;
	@NotEmpty
	@Size(max = 1000)
	private String nom;
	@Size(max = 1000)
	private String nomEs;
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
	private boolean sobrescriureCieOrganEmisor;

	private ResourceReference<EntitatResource, Long> entitat;
	private ResourceReference<EntregaCieResource, Long> entregaCie;

	// Camps calculats
	private String codiNom;
	private Integer aclEntryCount;

	@Getter
	@Setter
	@NoArgsConstructor
	public static class OrganGestorDir3SyncForm implements Serializable {
		private Boolean simular;
	}

}
