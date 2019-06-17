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
public class ValidIfVisibleAndNormalitzatValidator implements ConstraintValidator<ValidIfVisibleAndNormalitzat, Object> {

	private String fieldNameVisible;
	private String expectedFieldValueVisible;
	
	private String fieldName;
    private NotificaDomiciliConcretTipusEnumDto expectedFieldValue;
    private String dependFieldName;
	
	@Override
	public void initialize(ValidIfVisibleAndNormalitzat annotation) {
		fieldNameVisible   = annotation.fieldNameVisible();
		expectedFieldValueVisible = annotation.fieldValueVisble();
		
		fieldName          = annotation.fieldName();
	    expectedFieldValue = annotation.fieldValue();
	    dependFieldName    = annotation.dependFieldName();
	}

	@Override
	public boolean isValid(
			final Object value, 
			final ConstraintValidatorContext context) {
		
		if (value == null) {
            return true;
        }
		try {
			String fieldValueVisible       = BeanUtils.getProperty(value, fieldNameVisible);
            
			String fieldValue       = BeanUtils.getProperty(value, fieldName);
            String dependFieldValue = BeanUtils.getProperty(value, dependFieldName);
            dependFieldValue = dependFieldValue.replaceAll(",", "");
            if (expectedFieldValueVisible.equals(fieldValueVisible) && 
            		fieldValue.equalsIgnoreCase(expectedFieldValue.name()) && 
            		dependFieldValue.isEmpty()) {
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
