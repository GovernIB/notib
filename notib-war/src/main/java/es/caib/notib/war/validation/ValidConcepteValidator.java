package es.caib.notib.war.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Valida que el concepte d'una notificació sigui vàlid.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidConcepteValidator implements ConstraintValidator<ValidConcepte, String> {
	
	@Override
	public void initialize(final ValidConcepte constraintAnnotation) {
	}

	@Override
	public boolean isValid(
			final String value,
			final ConstraintValidatorContext context) {
		try {
			if (value == null || value.isEmpty())
				return true;
			return validacioConcepte(value);
		} catch (final Exception ex) {
			LOGGER.error("Error en la validació del concepte", ex);
			return false;
		}
	}
	// Validació del concepte
	private static final String CONTROL_CARACTERS = " aàáäbcçdeèéëfghiìíïjklmnñoòóöpqrstuùúüvwxyzAÀÁÄBCÇDEÈÉËFGHIÌÍÏJKLMNÑOÒÓÖPQRSTUÙÚÜVWXYZ0123456789-\\u2013_'\"/:().,¿?!¡;";
	private static boolean validacioConcepte(String concepte) {
		char[] concepte_chars = concepte.toCharArray();
		
		boolean esCaracterValid = true;
		for (int i = 0; esCaracterValid && i < concepte_chars.length; i++) {
			esCaracterValid = !(CONTROL_CARACTERS.indexOf(concepte_chars[i]) < 0);
			if (!esCaracterValid) {
				break;
			}
	    }
		return esCaracterValid;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ValidConcepteValidator.class);	
}
