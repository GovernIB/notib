package es.caib.notib.back.validation;

import es.caib.notib.back.helper.MessageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Valida que el concepte d'una notificació sigui vàlid.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class ValidDescripcioValidator implements ConstraintValidator<ValidDescripcio, Object> {
	
	private String fieldName;
	
	@Override
	public void initialize(final ValidDescripcio constraintAnnotation) {
		fieldName = constraintAnnotation.fieldName();
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {

		boolean valid;
		try {
			var fieldValue = BeanUtils.getProperty(value,  fieldName);
			if (fieldValue == null || fieldValue.isEmpty()) {
				return true;
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
//	private static final String CONTROL_CARAC_OLD = " aàáäbcçdeèéëfghiìíïjklmnñoòóöpqrstuùúüvwxyzAÀÁÄBCÇDEÈÉËFGHIÌÍÏJKLMNÑOÒÓÖPQRSTUÙÚÜVWXYZ0123456789-_'\"/:().,¿?!¡;";
	private static final String CONTROL_CARACTERS = " aàáäbcçdeèéëfghiìíïjklmnñoòóöpqrstuùúüvwxyzAÀÁÄBCÇDEÈÉËFGHIÌÍÏJKLMNÑOÒÓÖPQRSTUÙÚÜVWXYZ0123456789-–_/:().,¿?!¡;ºª";

	@SuppressWarnings("deprecation")
	private static boolean validacioConcepte(String concepte, final ConstraintValidatorContext context) {

		var concepteChars = concepte.toCharArray();
		var esCaracterValid = true;
		for (var i = 0; esCaracterValid && i < concepteChars.length; i++) {
			esCaracterValid = CONTROL_CARACTERS.indexOf(concepteChars[i]) >= 0;
			if (!esCaracterValid) {
				var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.concepte");
				context.buildConstraintViolationWithTemplate(msg).addNode("descripcio").addConstraintViolation();
				break;
			}
	    }
		return esCaracterValid;
	}

}
