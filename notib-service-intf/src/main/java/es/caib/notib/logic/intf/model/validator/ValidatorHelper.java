package es.caib.notib.logic.intf.model.validator;

import es.caib.notib.logic.intf.base.util.I18nUtil;

import javax.validation.ConstraintValidatorContext;

public abstract class ValidatorHelper {

	protected String getMessage(String message) {
		return "{" + message + "}";
	}

	protected boolean validateNotNull(String fieldName, Object value, boolean valid, ConstraintValidatorContext context) {
		return validateNotNull(fieldName, value, valid, context, "javax.validation.constraints.NotEmpty.message");
	}
	protected boolean validateNotNull(String fieldName, Object value, boolean valid, ConstraintValidatorContext context, String message) {

		if (value != null) {
			return valid;
		}
		var msg = I18nUtil.getInstance().getI18nMessage(message);
		context.buildConstraintViolationWithTemplate(msg).addPropertyNode(fieldName).addConstraintViolation();
		return false;
	}

	protected boolean addMessage(String fieldName, ConstraintValidatorContext context, String message, Object... vars) {

		var msg = I18nUtil.getInstance().getI18nMessage(message, vars);
		context.buildConstraintViolationWithTemplate(msg).addPropertyNode(fieldName).addConstraintViolation();
		return false;
	}
}
