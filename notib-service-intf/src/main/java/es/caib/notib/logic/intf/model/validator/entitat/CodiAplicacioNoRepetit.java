package es.caib.notib.logic.intf.model.validator.entitat;

import es.caib.notib.logic.intf.base.validation.CustomValidator;
import es.caib.notib.logic.intf.model.AplicacioResource;
import es.caib.notib.logic.intf.model.EntitatResource;
import es.caib.notib.logic.intf.resourceservice.AplicacioResourceService;
import es.caib.notib.logic.intf.resourceservice.EntitatResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidatorContext;

@Component
@RequiredArgsConstructor
public class CodiAplicacioNoRepetit implements CustomValidator<AplicacioResource>  {

	private final AplicacioResourceService plicacioResourceService;

	@Override
	public boolean validate(AplicacioResource resource, ConstraintValidatorContext context) {
		return plicacioResourceService.validarCodiNoRepetit(resource.getId(), resource.getUsuariCodi(), resource.getEntitat().getId());
	}
}
