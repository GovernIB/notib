package es.caib.notib.logic.intf.model.validator;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.intf.base.util.I18nUtil;
import es.caib.notib.logic.intf.base.validation.CustomValidator;
import es.caib.notib.logic.intf.model.NotificacioResource;

import javax.validation.ConstraintValidatorContext;

/**
 * Valida que s'hagi especificat el codi DIR3 en el primer enviament si el tipus d'enviament és SIR.
 *
 * @author Límit Tecnologies
 */
public class PrimerEnviamentCodiDir3ObligatoriEnviamentTipusSir implements CustomValidator<NotificacioResource> {

	@Override
	public boolean validate(NotificacioResource value, ConstraintValidatorContext context) {
		if (EnviamentTipus.SIR.equals(value.getEnviamentTipus())) {
			String message = I18nUtil.getInstance().getI18nMessage("javax.validation.constraints.NotNull.message");
			context.buildConstraintViolationWithTemplate(message).
				addPropertyNode("enviamentsInfo[0].sirTitularDir3Codi").
				addConstraintViolation();
			context.disableDefaultConstraintViolation();
			return false;
		} else {
			return true;
		}
	}

}
