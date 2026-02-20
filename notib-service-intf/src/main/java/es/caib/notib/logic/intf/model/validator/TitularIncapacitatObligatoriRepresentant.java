package es.caib.notib.logic.intf.model.validator;

import es.caib.notib.logic.intf.base.validation.CustomValidator;
import es.caib.notib.logic.intf.model.NotificacioEnviamentResource;

import javax.validation.ConstraintValidatorContext;

/**
 * Valida que si el titular de l'enviament està incapacitat s'ha d'especificar obligatòriament un representant.
 *
 * @author Límit Tecnologies
 */
public class TitularIncapacitatObligatoriRepresentant implements CustomValidator<NotificacioEnviamentResource> {

	@Override
	public boolean validate(NotificacioEnviamentResource value, ConstraintValidatorContext context) {
		boolean valid = true;
		if (value.getTitularInfo() != null && value.getTitularInfo().isIncapacitat()) {
			valid = value.getRepresentantsInfo() != null && !value.getRepresentantsInfo().isEmpty();
		}
		return valid;
	}

}
