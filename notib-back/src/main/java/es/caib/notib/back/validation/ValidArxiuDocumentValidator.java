package es.caib.notib.back.validation;


import es.caib.notib.back.helper.MessageHelper;
import es.caib.notib.logic.intf.dto.TipusDocumentEnumDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Constraint de validació que controla que camp email és obligatori si està habilitada l'entrega a la Direcció Electrònica Hablitada (DEH)
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class ValidArxiuDocumentValidator implements ConstraintValidator<ValidArxiuDocument, Object> {

	String fieldName;
	String dependFieldName;
	
	@Override
	public void initialize(final ValidArxiuDocument constraintAnnotation) {

		fieldName = constraintAnnotation.fieldName();
		dependFieldName = constraintAnnotation.dependFieldName();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {

		try {
			var valid = true;
			var fieldType = BeanUtils.getProperty(value, fieldName);
			var dependFieldValue = BeanUtils.getProperty(value, dependFieldName);
			if (TipusDocumentEnumDto.ARXIU.name().equals(fieldType) && (dependFieldValue == null || dependFieldValue.isEmpty())){
				var msg = MessageHelper.getInstance().getMessage("NotEmpty");
				context.buildConstraintViolationWithTemplate(msg).addNode("arxiu").addConstraintViolation();
				valid = false;
			}
			if (!valid) {
				context.disableDefaultConstraintViolation();
			}
			return valid;
		} catch (final Exception ex) {
        	log.error("Ha d'informar el email quan hi ha entrega DEH", ex);
        	return false;
        }
	}

}
