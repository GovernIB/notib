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
public class ValidDocumentValidator implements ConstraintValidator<ValidDocument, Object> {

	String fieldName;
	String dependFieldName;
	
	@Override
	public void initialize(final ValidDocument constraintAnnotation) {

		fieldName = constraintAnnotation.fieldName();
		dependFieldName = constraintAnnotation.dependFieldName();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {

		var valid = true;
		try {
			var fieldType = BeanUtils.getProperty(value, fieldName);
			var dependFieldValue = BeanUtils.getProperty(value, dependFieldName);
			var msg = MessageHelper.getInstance().getMessage("NotEmpty");
			if (TipusDocumentEnumDto.UUID.name().equals(fieldType)  && (dependFieldValue == null || dependFieldValue.isEmpty())) {
				context.buildConstraintViolationWithTemplate(msg).addNode("documentArxiuUuid").addConstraintViolation();
				valid = false;
			} else if (TipusDocumentEnumDto.ARXIU.name().equals(fieldType) && (dependFieldValue == null || dependFieldValue.isEmpty())){
				context.buildConstraintViolationWithTemplate(msg).addNode("arxiu").addConstraintViolation();
				valid = false;
			} else if (TipusDocumentEnumDto.CSV.name().equals(fieldType) && (dependFieldValue == null || dependFieldValue.isEmpty())){
				context.buildConstraintViolationWithTemplate(msg).addNode("documentArxiuCsv").addConstraintViolation();
				valid = false;
			} 
		} catch (final Exception ex) {
        	log.error("Ha d'informar el email quan hi ha entrega DEH", ex);
        	valid = false;
        }
		if (!valid) {
			context.disableDefaultConstraintViolation();
		}
		return valid;
	}

}
