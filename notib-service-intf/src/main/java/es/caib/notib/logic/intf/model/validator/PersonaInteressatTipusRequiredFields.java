package es.caib.notib.logic.intf.model.validator;

import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.logic.intf.base.util.I18nUtil;
import es.caib.notib.logic.intf.base.validation.CustomValidator;
import es.caib.notib.logic.intf.model.PersonaResource;

import javax.validation.ConstraintValidatorContext;

/**
 * Valida els camps obligatoris en funció del valor del camp interessatTipus.
 *
 * @author Límit Tecnologies
 */
public class PersonaInteressatTipusRequiredFields implements CustomValidator<PersonaResource> {

	@Override
	public boolean validate(PersonaResource value, ConstraintValidatorContext context) {
		boolean valid = true;
		if (value.getInteressatTipus() != null) {
			if (value.getInteressatTipus().equals(InteressatTipus.FISICA)) {
				valid = validateNotNull(PersonaResource.Fields.nif, value.getNif(), valid, context);
				valid = validateNotNull(PersonaResource.Fields.nom, value.getNom(), valid, context);
				valid = validateNotNull(PersonaResource.Fields.llinatge1, value.getLlinatge1(), valid, context);
			} else if (value.getInteressatTipus().equals(InteressatTipus.ADMINISTRACIO)) {
				valid = validateNotNull(PersonaResource.Fields.nif, value.getNif(), valid, context);
				valid = validateNotNull(PersonaResource.Fields.dir3Codi, value.getDir3Codi(), valid, context);
			} else if (value.getInteressatTipus().equals(InteressatTipus.JURIDICA)) {
				valid = validateNotNull(PersonaResource.Fields.nif, value.getNif(), valid, context);
				valid = validateNotNull(PersonaResource.Fields.raoSocial, value.getRaoSocial(), valid, context);
			} else if (value.getInteressatTipus().equals(InteressatTipus.FISICA_SENSE_NIF)) {
				valid = validateNotNull(PersonaResource.Fields.nom, value.getNom(), valid, context);
				valid = validateNotNull(PersonaResource.Fields.llinatge1, value.getLlinatge1(), valid, context);
				valid = validateNotNull(PersonaResource.Fields.email, value.getEmail(), valid, context);
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
