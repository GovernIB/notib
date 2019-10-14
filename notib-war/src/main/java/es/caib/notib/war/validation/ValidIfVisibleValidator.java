/**
 * 
 */
package es.caib.notib.war.validation;

import java.lang.reflect.InvocationTargetException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;

import es.caib.notib.war.helper.MessageHelper;

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
		 fieldName          = annotation.fieldName();
	     expectedFieldValue = annotation.fieldValue();
	     dependFieldName    = annotation.dependFieldName();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isValid(
			final Object value, 
			final ConstraintValidatorContext context) {
		boolean valid = true;
		
		try {
			String fieldValue       = BeanUtils.getProperty(value, fieldName);
            String dependFieldValue = BeanUtils.getProperty(value, dependFieldName);
			
            if (expectedFieldValue.equals(fieldValue) && dependFieldValue.isEmpty()) {
            	context.buildConstraintViolationWithTemplate(
            			MessageHelper.getInstance().getMessage("NotEmpty"))
                    .addNode(dependFieldName)
                    .addConstraintViolation();
                    valid = false;
            }
			
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
		
		if (!valid)
			context.disableDefaultConstraintViolation();
		
        return valid;
	}

}
