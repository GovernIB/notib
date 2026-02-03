package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import es.caib.notib.logic.intf.dto.ProcSerTipusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Informació d'un procediment.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
	descriptionField = ProcedimentResource.Fields.nom,
	quickFilterFields = { ProcedimentResource.Fields.codi, ProcedimentResource.Fields.nom },
	accessConstraints = @ResourceAccessConstraint(
		type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
		roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_SUPER },
		grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE }
	)
)
public class ProcedimentResource extends BaseResource<Long> {

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
	private boolean comu;
	private boolean requireDirectPermission;
	private boolean manual;
	private boolean organNoSincronitzat;
	private boolean actiu;
	private Date ultimaActualitzacio;

	private ResourceReference<EntitatResource, Long> entitat;
	private ResourceReference<OrganGestorResource, Long> organGestor;
	// private ResourceReference<EntregaCieResource, Long> entregaCie;

	private Integer grupCount;
	private Integer aclEntryCount;

}
