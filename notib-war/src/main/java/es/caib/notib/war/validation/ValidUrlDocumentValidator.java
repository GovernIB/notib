package es.caib.notib.war.validation;



import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.war.helper.MessageHelper;

/**
 * Constraint de validació que controla que camp email és obligatori si està habilitada l'entrega a la Direcció Electrònica Hablitada (DEH)
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidUrlDocumentValidator implements ConstraintValidator<ValidUrlDocument, Object> {

	String fieldName;
	String dependFieldName;
	
	@Override
	public void initialize(final ValidUrlDocument constraintAnnotation) {
		fieldName 		= constraintAnnotation.fieldName();
		dependFieldName = constraintAnnotation.dependFieldName();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {
			
			String FieldType = BeanUtils.getProperty(value, fieldName);
			String dependFieldValue = BeanUtils.getProperty(value, dependFieldName);
			boolean valid = true;
			
			if (FieldType == TipusDocumentEnumDto.URL.name() && (dependFieldValue == null || dependFieldValue.isEmpty())) {
				context
				.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("notificacio.form.valid.document"))
				.addNode("documentArxiuUrl")
				.addConstraintViolation();
				valid = false;
			} 

			return valid;
		} catch (final Exception ex) {
        	LOGGER.error("Ha d'informar el email quan hi ha entrega DEH", ex);
        	return false;
        }
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ValidDocumentValidator.class);

}
