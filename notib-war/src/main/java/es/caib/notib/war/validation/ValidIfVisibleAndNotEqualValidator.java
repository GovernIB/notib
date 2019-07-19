/**
 * 
 */
package es.caib.notib.war.validation;

import java.lang.reflect.InvocationTargetException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;

import es.caib.notib.core.api.dto.NotificaDomiciliConcretTipusEnumDto;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi d'entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidIfVisibleAndNotEqualValidator implements ConstraintValidator<ValidIfVisibleAndNotEqual, Object> {

	private String fieldName;
    private String expectedFieldValue;
    private String dependFieldName;
    private String noDependFieldName;
    private NotificaDomiciliConcretTipusEnumDto noExpectedFieldValue;
	
	@Override
	public void initialize(ValidIfVisibleAndNotEqual annotation) {
		 fieldName          = annotation.fieldName();
	     expectedFieldValue = annotation.fieldValue();
	     dependFieldName    = annotation.dependFieldName();
	     noDependFieldName = annotation.noDependFieldName();
	     noExpectedFieldValue = annotation.noExpectedFieldValue();
	}

	@Override
	public boolean isValid(
			final Object value, 
			final ConstraintValidatorContext context) {
		
		if (value == null) {
            return true;
        }
		try {
			String fieldValue       = BeanUtils.getProperty(value, fieldName);
			String noDependFieldValue = BeanUtils.getProperty(value, noDependFieldName);
            String dependFieldValue = BeanUtils.getProperty(value, dependFieldName);
			
            if (expectedFieldValue.equals(fieldValue) && 
            		dependFieldValue.isEmpty() &&
            		!noDependFieldValue.equalsIgnoreCase(noExpectedFieldValue.name())) {
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
