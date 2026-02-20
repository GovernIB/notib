package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceArtifact;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceArtifactType;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import es.caib.notib.logic.intf.dto.AvisNivellEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * Informació d'un avís.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		descriptionField = AvisResource.Fields.assumpte,
		quickFilterFields = AvisResource.Fields.assumpte,
		accessConstraints = @ResourceAccessConstraint(
				type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
				roles = { BaseConfig.ROLE_SUPER },
				grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE }
		),
		artifacts = {
			@ResourceArtifact(
				type = ResourceArtifactType.FILTER,
				code = AvisResource.FILTER_CODE,
				formClass = AvisResource.AvisResourceFilter.class)
		}
)
public class AvisResource extends BaseResource<Long> {

	public static final String FILTER_CODE = "FILTER_AVIS";

	@NotNull
	@Size(max = 256)
	private String assumpte;
	@NotNull
	@Size(max = 2048)
	private String missatge;
	@NotNull
	private Date dataInici;
	private Date dataFinal;
	@NotNull
	private AvisNivellEnumDto avisNivell = AvisNivellEnumDto.INFO;
	private boolean actiu = true;
	private boolean avisAdministrador;

	@NotNull
	private ResourceReference<EntitatResource, Long> entitat;

	@Getter
	@Setter
	@NoArgsConstructor
	public static class AvisResourceFilter implements Serializable {
		private String assumpte;
		private Date dataInici;
		private Date dataFinal;
		private AvisNivellEnumDto avisNivell;
		private boolean actiu;
		private ResourceReference<EntitatResource, Long> entitat;
	}

}
