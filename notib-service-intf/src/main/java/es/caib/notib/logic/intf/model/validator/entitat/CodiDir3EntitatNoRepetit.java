package es.caib.notib.logic.intf.model.validator.entitat;

import es.caib.notib.logic.intf.base.validation.CustomValidator;
import es.caib.notib.logic.intf.model.EntitatResource;
import es.caib.notib.logic.intf.resourceservice.EntitatResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidatorContext;

@Component
@RequiredArgsConstructor
public class CodiDir3EntitatNoRepetit implements CustomValidator<EntitatResource>  {

	private final EntitatResourceService entitatResourceService;

	@Override
	public boolean validate(EntitatResource resource, ConstraintValidatorContext context) {
		return entitatResourceService.validarCodiDir3NoRepetit(resource.getId(), resource.getDir3Codi());
	}
}
