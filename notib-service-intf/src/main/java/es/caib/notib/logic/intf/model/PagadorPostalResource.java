package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceArtifact;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceArtifactType;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * Informació d'un pagador postal.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
	descriptionField = PagadorPostalResource.Fields.nom,
	quickFilterFields = {PagadorPostalResource.Fields.nom},
	accessConstraints = {
		@ResourceAccessConstraint(
			type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
			roles = { BaseConfig.ROLE_ADMIN },
			grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE }
		),
		@ResourceAccessConstraint(
			type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
			roles = { BaseConfig.ROLE_ADMIN_LECTURA },
			grantedPermissions = { PermissionEnum.READ }
		)
	},
	artifacts = {
		@ResourceArtifact(
			type = ResourceArtifactType.FILTER,
			code = PagadorPostalResource.FILTER_CODE,
			formClass = PagadorPostalResource.PagadorPostalResourceFilter.class)
	}
)
public class PagadorPostalResource extends BaseResource<Long> {

	public static final String FILTER_CODE = "FILTER_PAGADOR_POSTAL";

	@NotNull
	@Size(max = 256)
	private String nom;
	@NotNull
	@Size(max = 20)
	private String contracteNum;
	@NotNull
	@Size(max = 20)
	private String facturacioClientCodi;
	private Date contracteDataVig;

	private ResourceReference<EntitatResource, Long> entitat;
	@NotNull
	private ResourceReference<OrganGestorResource, Long> organGestor;

	@Getter
	@Setter
	@NoArgsConstructor
	public static class PagadorPostalResourceFilter implements Serializable {
		private String nom;
		private String contracteNum;
		private String facturacioClientCodi;
		private Date contracteDataVigInici;
		private Date contracteDataVigFinal;
		private ResourceReference<OrganGestorResource, Long> organGestor;
	}

}
