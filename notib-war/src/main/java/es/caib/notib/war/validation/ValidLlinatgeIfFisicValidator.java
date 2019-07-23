/**
 * 
 */
package es.caib.notib.war.validation;

import java.lang.reflect.InvocationTargetException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi d'entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidLlinatgeIfFisicValidator implements ConstraintValidator<ValidLlinatgeIfFisic, Object> {

	private String fieldName2;
    private String expectedFieldValue;
    private String fieldName;
    private String expectedFieldValue2;
    private String dependFieldName;
	
	@Override
	public void initialize(ValidLlinatgeIfFisic annotation) {
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
		
		if (value == null) {
            return true;
        }
		try {
			String fieldValue       = BeanUtils.getProperty(value, fieldName);
            String dependFieldValue = BeanUtils.getProperty(value, dependFieldName);
			
            String fieldValue2       = BeanUtils.getProperty(value, fieldName2);
            
            if ((expectedFieldValue.equals(fieldValue) && dependFieldValue.isEmpty()) && (expectedFieldValue2.equals(fieldValue2))) {
            	context.disableDefaultConstraintViolation();
            	context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addNode(dependFieldName)
                    .addConstraintViolation();
                    return false;
            }
			
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
        return true;
	}

}
