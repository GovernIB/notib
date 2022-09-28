/**
 * 
 */
package es.caib.notib.back.validation;

import java.lang.reflect.InvocationTargetException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;

import es.caib.notib.back.helper.MessageHelper;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi d'entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidNifIfFisicValidator implements ConstraintValidator<ValidNifIfFisic, Object> {

	private String fieldName2;
    private String expectedFieldValue;
    private String fieldName;
    private String expectedFieldValue2;
    private String dependFieldName;
	
	@Override
	public void initialize(ValidNifIfFisic annotation) {
		 fieldName           = annotation.fieldName();
		 fieldName2          = annotation.fieldName2();
	     expectedFieldValue  = annotation.fieldValue();
	     expectedFieldValue2 = annotation.fieldValue2();
	     dependFieldName     = annotation.dependFieldName();
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
			
            String fieldValue2       = BeanUtils.getProperty(value, fieldName2);
            
            if ((expectedFieldValue.equals(fieldValue) && dependFieldValue.isEmpty()) && (expectedFieldValue2.equals(fieldValue2))) {
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
