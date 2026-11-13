package es.caib.notib.logic.intf.model.validator;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.intf.base.validation.CustomValidator;
import es.caib.notib.logic.intf.model.NotificacioResource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidatorContext;

/**
 * Valida si el camp de procediment ha de ser NotNull. En les notificacions a on l'usuari te permís de "comunicacions
 * sense procediment" sobre l'òrgan gestor seleccionat el camp procediment és opcional.
 *
 * @author Límit Tecnologies
 */
@Component
@RequiredArgsConstructor
public class NotificacioProcedimentNotNull implements CustomValidator<NotificacioResource> {

	@Override
	public boolean validate(NotificacioResource value, ConstraintValidatorContext context) {
		if (EnviamentTipus.NOTIFICACIO.equals(value.getEnviamentTipus())) {
			return value.getProcediment() != null;
		} else {
			return true;
		}
	}

	@Override
	public String getFieldMessage() {
		return "{javax.validation.constraints.NotNull.message}";
	}

}
