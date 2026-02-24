package es.caib.notib.logic.intf.model.validator;

import es.caib.notib.logic.intf.base.validation.CustomValidator;
import es.caib.notib.logic.intf.model.Dir3Resource;

import javax.validation.ConstraintValidatorContext;

/**
 * Valida que s'hagi especificat un valor pel camp codi DIR3 o un valor pel camp denominacio.
 *
 * @author Límit Tecnologies
 */
public class CodiODenominacioNotNull implements CustomValidator<Dir3Resource.Dir3ResourceFilter> {

	@Override
	public boolean validate(Dir3Resource.Dir3ResourceFilter value, ConstraintValidatorContext context) {
		return value.getCodi() != null || value.getDenominacio() != null;
	}

}
