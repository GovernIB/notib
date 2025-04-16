/**
 * 
 */
package es.caib.notib.back.validation;

import com.google.common.base.Strings;
import es.caib.notib.back.command.UsuariCodiCommand;
import es.caib.notib.back.helper.MessageHelper;
import es.caib.notib.logic.intf.service.UsuariService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Comprova que el codi d'entorn no estigui repetit.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UsuariExistsValidator implements ConstraintValidator<UsuariExists, UsuariCodiCommand> {

	private UsuariExists anotacio;
	@Autowired
	private UsuariService usuariService;

	@Override
	public void initialize(UsuariExists anotacio) {
		this.anotacio = anotacio;
	}

	@Override
	public boolean isValid(UsuariCodiCommand command, ConstraintValidatorContext context) {

		var valid = true;
		if (Strings.isNullOrEmpty(command.getCodiNou())) {
			context.disableDefaultConstraintViolation();
			var msg = MessageHelper.getInstance().getMessage(anotacio.message() + ".not.null");
			context.buildConstraintViolationWithTemplate(msg).addNode("codiNou").addConstraintViolation();
			valid = false;
		}
		if (Strings.isNullOrEmpty(command.getCodiNou())) {
			context.disableDefaultConstraintViolation();
			var msg = MessageHelper.getInstance().getMessage(anotacio.message() + ".not.null");
			context.buildConstraintViolationWithTemplate(msg).addNode("codiAntic").addConstraintViolation();
			valid = false;
		}
		// comprova que el nom sigui únic
		if (!Strings.isNullOrEmpty(command.getCodiAntic())) {
			var usuari = usuariService.findByCodi(command.getCodiAntic());
			if (usuari == null) {
				context.disableDefaultConstraintViolation();
				var msg = MessageHelper.getInstance().getMessage(anotacio.message() + ".not.found");
				context.buildConstraintViolationWithTemplate(msg).addNode("codiAntic").addConstraintViolation();
				valid = false;
			}
		}
		return valid;
	}

}
