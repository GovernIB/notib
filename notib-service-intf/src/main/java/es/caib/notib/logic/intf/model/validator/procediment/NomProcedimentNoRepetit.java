package es.caib.notib.logic.intf.model.validator.procediment;

import es.caib.notib.logic.intf.base.validation.CustomValidator;
import es.caib.notib.logic.intf.model.ProcedimentResource;
import es.caib.notib.logic.intf.resourceservice.ProcedimentResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidatorContext;

@Component
@RequiredArgsConstructor
public class NomProcedimentNoRepetit implements CustomValidator<ProcedimentResource>  {

	private final ProcedimentResourceService procedimentResourceService;

	@Override
	public boolean validate(ProcedimentResource resource, ConstraintValidatorContext context) {
		return procedimentResourceService.validarNomNoRepetit(resource.getId(), resource.getNom());
	}
}
