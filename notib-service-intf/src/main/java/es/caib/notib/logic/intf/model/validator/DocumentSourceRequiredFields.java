package es.caib.notib.logic.intf.model.validator;

import es.caib.notib.logic.intf.base.util.I18nUtil;
import es.caib.notib.logic.intf.base.validation.CustomValidator;
import es.caib.notib.logic.intf.model.DocumentResource;

import javax.validation.ConstraintValidatorContext;

/**
 * Valida els camps obligatoris en funció del valor del camp source.
 *
 * @author Límit Tecnologies
 */
public class DocumentSourceRequiredFields implements CustomValidator<DocumentResource> {

	@Override
	public boolean validate(DocumentResource value, ConstraintValidatorContext context) {
		boolean valid = true;
		if (value.getSource() != null) {
			if (value.getSource().equals(DocumentResource.DocumentSource.UUID)) {
				valid = validateNotNull(DocumentResource.Fields.uuid, value.getUuid(), valid, context);
			} else if (value.getSource().equals(DocumentResource.DocumentSource.CSV)) {
				valid = validateNotNull(DocumentResource.Fields.csv, value.getCsv(), valid, context);
			} else if (value.getSource().equals(DocumentResource.DocumentSource.ATTACHED)) {
				valid = validateNotNull(DocumentResource.Fields.attachment, value.getAttachment(), valid, context);
			}
		}
		context.disableDefaultConstraintViolation();
		return valid;
	}

	public String getMessage() {
		return "{javax.validation.constraints.NotEmpty.message}";
	}

	private boolean validateNotNull(
		String fieldName,
		Object value,
		boolean valid,
		ConstraintValidatorContext context) {
		if (value == null) {
			context.
				buildConstraintViolationWithTemplate(
					I18nUtil.getInstance().getI18nMessage(
						getMessage().substring(1, getMessage().length() - 1).trim())).
				addPropertyNode(fieldName).
				addConstraintViolation();
			return false;
		} else {
			return valid;
		}
	}

}
