/**
 * 
 */
package es.caib.notib.war.validation;

import es.caib.notib.client.domini.NotificaDomiciliConcretTipusEnumDto;
import es.caib.notib.war.helper.MessageHelper;
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

	@SuppressWarnings("deprecation")
	@Override
	public boolean isValid(
			final Object value, 
			final ConstraintValidatorContext context) {
		boolean valid = true;
		
		try {
			String fieldValue       = BeanUtils.getProperty(value, fieldName);
			String noDependFieldValue = BeanUtils.getProperty(value, noDependFieldName);
            String dependFieldValue = BeanUtils.getProperty(value, dependFieldName);
			
            if (expectedFieldValue.equals(fieldValue) && 
            		dependFieldValue.isEmpty() &&
            		!noDependFieldValue.equalsIgnoreCase(noExpectedFieldValue.name())) {
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
