/**
 * 
 */
package es.caib.notib.back.validation;

import es.caib.notib.back.helper.MessageHelper;
import org.apache.commons.beanutils.BeanUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi d'entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidIfVisibleValidator implements ConstraintValidator<ValidIfVisible, Object> {

	private String fieldName;
    private String expectedFieldValue;
    private String dependFieldName;
	
	@Override
	public void initialize(ValidIfVisible annotation) {

		fieldName = annotation.fieldName();
		expectedFieldValue = annotation.fieldValue();
 		dependFieldName = annotation.dependFieldName();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {

		try {
			var valid = true;
			var fieldValue = BeanUtils.getProperty(value, fieldName);
            var dependFieldValue = BeanUtils.getProperty(value, dependFieldName);
            if (expectedFieldValue.equals(fieldValue) && dependFieldValue.isEmpty()) {
				var msg = MessageHelper.getInstance().getMessage("NotEmpty");
            	context.buildConstraintViolationWithTemplate(msg).addNode(dependFieldName).addConstraintViolation();
				valid = false;
            }
			if (!valid) {
				context.disableDefaultConstraintViolation();
			}
			return valid;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
	}
}
