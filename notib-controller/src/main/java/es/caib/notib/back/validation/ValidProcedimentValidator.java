package es.caib.notib.back.validation;

import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.intf.service.ProcedimentService;
import es.caib.notib.back.command.ProcSerCommand;
import es.caib.notib.back.helper.MessageHelper;
import es.caib.notib.back.helper.MissatgesHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi de procediment ni el nom.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class ValidProcedimentValidator implements ConstraintValidator<ValidProcediment, ProcSerCommand> {

	private HttpServletRequest request;

	@Autowired
	private ProcedimentService procedimentService;
	
	
	@Override
	public void initialize(final ValidProcediment constraintAnnotation) {

		var attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		request = attr.getRequest();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isValid(final ProcSerCommand command, final ConstraintValidatorContext context) {

		boolean valid = true;
		try {
			// Comprovar codi no repetit
			if (!checkCodi(command)) {
				context.disableDefaultConstraintViolation();
				var msg = MessageHelper.getInstance().getMessage("procediment.validation.codi.repetit");
				context.buildConstraintViolationWithTemplate(msg).addNode("codi").addConstraintViolation();
				valid = false;
			}
			
			// Comprovar nom no repetit
			if (!checkNom(command)) {
				context.disableDefaultConstraintViolation();
				var msg = MessageHelper.getInstance().getMessage("procediment.validation.nom.repetit");
				context.buildConstraintViolationWithTemplate(msg).addNode("nom").addConstraintViolation();
				valid = false;
			}

			if (command.isEntregaCieActiva()) {
				if (command.getOperadorPostalId() == null) {
					valid = false;
					context.disableDefaultConstraintViolation();
					var msg = MessageHelper.getInstance().getMessage("NotEmpty");
					context.buildConstraintViolationWithTemplate(msg).addNode("operadorPostalId").addConstraintViolation();
				}

				if (command.getCieId() == null) {
					valid = false;
					context.disableDefaultConstraintViolation();
					var msg = MessageHelper.getInstance().getMessage("NotEmpty");
					context.buildConstraintViolationWithTemplate(msg).addNode("cieId").addConstraintViolation();
				}
			}
			
        } catch (final Exception ex) {
			log.error("S'ha produït un error inesperat al validar el procediment. "
					+ "Si l'error es continua donant en properes intents, posis en contacte amb els administradors de l'aplicació.", ex);
			MissatgesHelper.error(request, "Error inesperat en la validació del codi y nom del procediment.");
			valid = false;
        }
		
       	if (!valid)
       		context.disableDefaultConstraintViolation();
       	
       	return valid;
	}

	private boolean checkCodi(final ProcSerCommand command) {

		final var id = command.getId();
		final var procedimentCodi = command.getCodi();
		var procediment = procedimentService.findByCodi(command.getEntitatId(), procedimentCodi);
		if (procediment == null) {
			return true;
		}
		return id != null && id.longValue() == procediment.getId().longValue();
	}

	private boolean checkNom(final ProcSerCommand command) {

		final var id = command.getId();
		var procediment = procedimentService.findByNom(command.getEntitatId(), command.getNom());
		if (procediment == null) {
			return true;
		}
		return id != null && (id.longValue() == procediment.getId().longValue());
	}

}
