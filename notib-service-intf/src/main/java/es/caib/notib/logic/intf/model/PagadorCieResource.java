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
 * Informació d'un grup.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
	descriptionField = PagadorCieResource.Fields.organGestorPagador,
	quickFilterFields = { PagadorCieResource.Fields.organGestorPagador },
	accessConstraints = @ResourceAccessConstraint(
		type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
		roles = { BaseConfig.ROLE_ADMIN},
		grantedPermissions = {PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE}
	)
)
public class PagadorCieResource extends BaseResource<Long> {

	@Size(max = 256)
	private String nom;
	@Size(max = 255)
	private String apiKey;
	private boolean cieExtern;
	private Date contracteDataVig;

	@NotNull
	private ResourceReference<EntitatResource, Long> entitat;
	@NotNull
	private ResourceReference<OrganGestorResource, Long> organGestorEmissor;
	@NotNull
	private ResourceReference<OrganGestorResource, Long> organGestorPagador;

}
