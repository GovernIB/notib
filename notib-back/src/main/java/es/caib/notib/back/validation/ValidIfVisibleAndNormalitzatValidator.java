/**
 * 
 */
package es.caib.notib.back.validation;

import es.caib.notib.back.helper.MessageHelper;
import es.caib.notib.client.domini.NotificaDomiciliConcretTipus;
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
public class ValidIfVisibleAndNormalitzatValidator implements ConstraintValidator<ValidIfVisibleAndNormalitzat, Object> {

	private String fieldNameVisible;
	private String expectedFieldValueVisible;
	private String fieldName;
    private NotificaDomiciliConcretTipus expectedFieldValue;
    private String dependFieldName;
    private String dependFieldNameSecond;
	
	@Override
	public void initialize(ValidIfVisibleAndNormalitzat annotation) {

		fieldNameVisible = annotation.fieldNameVisible();
		expectedFieldValueVisible = annotation.fieldValueVisble();
		fieldName = annotation.fieldName();
	    expectedFieldValue = annotation.fieldValue();
	    dependFieldName = annotation.dependFieldName();
	    dependFieldNameSecond = annotation.dependFieldNameSecond();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		
		var valid = true;
		try {
			var dependFieldValueEmpty = true;
			var validarDependFieldNameSecondValue = true;
			var fieldValueVisible = BeanUtils.getProperty(value, fieldNameVisible);
			var fieldValue = BeanUtils.getProperty(value, fieldName);
			var dependFieldValue = BeanUtils.getProperty(value, dependFieldName);
			var dependFieldNameSecondValue = BeanUtils.getProperty(value, dependFieldNameSecond);
            if (dependFieldValue != null) {
            	dependFieldValue = dependFieldValue.replaceAll(",", "");
            	dependFieldValueEmpty  = dependFieldValue.isEmpty();
            }
            
            if (dependFieldValue != null && dependFieldNameSecondValue != null) {
	            if (fieldValue.equals("NACIONAL") && (dependFieldName.equals("puntKm") && dependFieldValue.isEmpty())
					&& (dependFieldNameSecond.equals("numeroCasa") && !dependFieldNameSecondValue.isEmpty())) {

					validarDependFieldNameSecondValue = false;
	            }
	            
	            if (fieldValue.equals("NACIONAL") && (dependFieldName.equals("numeroCasa") && dependFieldValue.isEmpty())
					&& (dependFieldNameSecond.equals("puntKm") && !dependFieldNameSecondValue.isEmpty())) {

					validarDependFieldNameSecondValue = false;
	            }
            }
            
            if (expectedFieldValueVisible.equals(fieldValueVisible) && fieldValue.equalsIgnoreCase(expectedFieldValue.name())
				&& dependFieldValueEmpty && validarDependFieldNameSecondValue) {

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
