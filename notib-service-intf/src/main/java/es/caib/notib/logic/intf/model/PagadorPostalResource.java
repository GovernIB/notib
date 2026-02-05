package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
			roles = {BaseConfig.ROLE_ADMIN},
			grantedPermissions = {PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE}
		),
		@ResourceAccessConstraint(
			type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
			roles = {BaseConfig.ROLE_ADMIN_LECTURA},
			grantedPermissions = {PermissionEnum.READ}
		)
	}
)
public class PagadorPostalResource extends BaseResource<Long> {

	@NotNull
	@Size(max = 20)
	private String contracteNum;
	private Date contracteDataVig;
	@NotNull
	@Size(max = 20)
	private String facturacioClientCodi;
	@NotNull
	@Size(max = 100)
	private String nom;
	private ResourceReference<EntitatResource, Long> entitat;
	@NotNull
	private ResourceReference<OrganGestorResource, Long> organGestor;

	private Integer aclEntryCount;

}
