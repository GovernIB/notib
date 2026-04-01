package es.caib.notib.logic.intf.model.validator;

import es.caib.notib.logic.intf.base.exception.ResourceNotFoundException;
import es.caib.notib.logic.intf.base.validation.CustomValidator;
import es.caib.notib.logic.intf.model.AclEntryResource;
import es.caib.notib.logic.intf.model.ProcedimentResource;
import es.caib.notib.logic.intf.resourceservice.ProcedimentResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidatorContext;

/**
 * Valida que s'hagi especificat un valor pel camp organGestor si el permís és per a un procediment comú.
 *
 * @author Límit Tecnologies
 */
@Component
@RequiredArgsConstructor
public class OrganGestorNotNullEnProcedimentComu implements CustomValidator<AclEntryResource> {

	private final ProcedimentResourceService procedimentResourceService;

	@Override
	public boolean validate(AclEntryResource value, ConstraintValidatorContext context) {
		if (esProcedimentComu(value)) {
			return value.getOrganGestor() != null;
		} else {
			return true;
		}
	}

	@Override
	public String getFieldMessage() {
		return "{javax.validation.constraints.NotNull.message}";
	}

	private boolean esProcedimentComu(AclEntryResource aclEntryResource) {
		boolean esProcediment = "procedimentResource".equals(aclEntryResource.getResourceName());
		if (esProcediment && aclEntryResource.getResourceId() != null) {
			Long idAsLong = Long.parseLong(aclEntryResource.getResourceId().toString());
			try {
				ProcedimentResource procediment = procedimentResourceService.getOne(idAsLong, null);
				return procediment.isComu();
			} catch (ResourceNotFoundException ex) {
				return false;
			}
		} else {
			return false;
		}
	}

}
