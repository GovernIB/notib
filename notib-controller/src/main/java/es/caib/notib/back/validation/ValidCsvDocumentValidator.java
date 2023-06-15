package es.caib.notib.back.validation;



import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.notib.logic.intf.dto.TipusDocumentEnumDto;
import es.caib.notib.back.helper.MessageHelper;

/**
 * Constraint de validació que controla que camp email és obligatori si està habilitada l'entrega a la Direcció Electrònica Hablitada (DEH)
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class ValidCsvDocumentValidator implements ConstraintValidator<ValidCsvDocument, Object> {

	String fieldName;
	String dependFieldName;
	
	@Override
	public void initialize(final ValidCsvDocument constraintAnnotation) {

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
			if (TipusDocumentEnumDto.CSV.name().equals(fieldType) && (dependFieldValue == null || dependFieldValue.isEmpty())) {
				var msg = MessageHelper.getInstance().getMessage("NotEmpty");
				context.buildConstraintViolationWithTemplate(msg).addNode("documentArxiuCsv").addConstraintViolation();
				valid = false;
			} 
		} catch (final Exception ex) {
        	log.error("Ha d'informar el email quan hi ha entrega DEH", ex);
        	return false;
        }
		if (!valid) {
			context.disableDefaultConstraintViolation();
		}
		return valid;
	}

}
