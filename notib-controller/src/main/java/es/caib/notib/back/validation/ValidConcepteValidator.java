package es.caib.notib.back.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

import es.caib.notib.back.helper.MessageHelper;

/**
 * Valida que el concepte d'una notificació sigui vàlid.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class ValidConcepteValidator implements ConstraintValidator<ValidConcepte, Object> {
	
	private String fieldName;
	
	@Override
	public void initialize(final ValidConcepte constraintAnnotation) {
		fieldName = constraintAnnotation.fieldName();
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {

		boolean valid = true;
		try {
			var fieldValue = BeanUtils.getProperty(value,  fieldName);
			if (fieldValue == null || fieldValue.isEmpty()) {
				valid = true;
			}
			valid = validacioConcepte(fieldValue,context);
		} catch (final Exception ex) {
			log.error("Error en la validació del concepte", ex);
			valid = false;
		}
		if (!valid) {
			context.disableDefaultConstraintViolation();
		}
		return valid;
	}

	// Validació del concepte
	private static final String CONTROL_CARACTERS = " aàáäbcçdeèéëfghiìíïjklmnñoòóöpqrstuùúüvwxyzAÀÁÄBCÇDEÈÉËFGHIÌÍÏJKLMNÑOÒÓÖPQRSTUÙÚÜVWXYZ0123456789-_'\"/:().,¿?!¡;";
	
	@SuppressWarnings("deprecation")
	private static boolean validacioConcepte(String concepte, final ConstraintValidatorContext context) {

		var concepte_chars = concepte.toCharArray();
		var esCaracterValid = true;
		for (var i = 0; esCaracterValid && i < concepte_chars.length; i++) {
			esCaracterValid = !(CONTROL_CARACTERS.indexOf(concepte_chars[i]) < 0);
			var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.concepte");
			if (!esCaracterValid) {
				context.buildConstraintViolationWithTemplate(msg).addNode("concepte").addConstraintViolation();
				break;
			}
	    }
		return esCaracterValid;
	}
}
