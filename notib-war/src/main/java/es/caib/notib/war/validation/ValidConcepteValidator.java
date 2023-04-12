package es.caib.notib.war.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.notib.war.helper.MessageHelper;

/**
 * Valida que el concepte d'una notificació sigui vàlid.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidConcepteValidator implements ConstraintValidator<ValidConcepte, Object> {
	
	private String fieldName;
	
	@Override
	public void initialize(final ValidConcepte constraintAnnotation) {
		fieldName = constraintAnnotation.fieldName();
	}

	@Override
	public boolean isValid(
			final Object value,
			final ConstraintValidatorContext context) {
		boolean valid = true;
		try {
			String fieldValue = BeanUtils.getProperty(value,  fieldName);
			if (fieldValue == null || fieldValue.isEmpty())
				valid = true;
			valid = validacioConcepte(fieldValue,context);
		} catch (final Exception ex) {
			LOGGER.error("Error en la validació del concepte", ex);
			valid = false;
		}
		if (!valid)
			context.disableDefaultConstraintViolation();
		
		return valid;
	}
	// Validació del concepte
	private static final String CONTROL_CARACTERS = " aàáäbcçdeèéëfghiìíïjklmnñoòóöpqrstuùúüvwxyzAÀÁÄBCÇDEÈÉËFGHIÌÍÏJKLMNÑOÒÓÖPQRSTUÙÚÜVWXYZ0123456789-_'\"/:().,¿?!¡;";
	
	@SuppressWarnings("deprecation")
	private static boolean validacioConcepte(String concepte, final ConstraintValidatorContext context) {
		char[] concepte_chars = concepte.toCharArray();
		
		boolean esCaracterValid = true;
		for (int i = 0; esCaracterValid && i < concepte_chars.length; i++) {
			esCaracterValid = !(CONTROL_CARACTERS.indexOf(concepte_chars[i]) < 0);
			if (!esCaracterValid) {
				context
				.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("notificacio.form.valid.concepte"))
				.addNode("concepte")
				.addConstraintViolation();
				break;
			}
	    }
		return esCaracterValid;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ValidConcepteValidator.class);	
}