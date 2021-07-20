package es.caib.notib.war.validation;

import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.war.command.ProcedimentCommand;
import es.caib.notib.war.helper.MessageHelper;
import es.caib.notib.war.helper.MissatgesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ValidProcedimentValidator implements ConstraintValidator<ValidProcediment, ProcedimentCommand> {

	private HttpServletRequest request;

	@Autowired
	private ProcedimentService procedimentService;
	
	
	@Override
	public void initialize(final ValidProcediment constraintAnnotation) {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		request = attr.getRequest();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isValid(
			final ProcedimentCommand command, 
			final ConstraintValidatorContext context) {

		boolean valid = true;
		
		try {
			// Comprovar codi no repetit
			if (!checkCodi(command)) {
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("procediment.validation.codi.repetit")).addNode("codi").addConstraintViolation();
				valid = false;
			}
			
			// Comprovar nom no repetit
			if (!checkNom(command)) {
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("procediment.validation.nom.repetit")).addNode("nom").addConstraintViolation();
				valid = false;
			}
			
        } catch (final Exception ex) {
			LOGGER.error("S'ha produït un error inesperat al validar el procediment. "
					+ "Si l'error es continua donant en properes intents, posis en contacte amb els administradors de l'aplicació.", ex);
			MissatgesHelper.error(request, "Error inesperat en la validació del codi y nom del procediment.");
			valid = false;
        }
		
       	if (!valid)
       		context.disableDefaultConstraintViolation();
       	
       	return valid;
	}

	private boolean checkCodi(
			final ProcedimentCommand command
	) {
		boolean valid = true;
		final Long id = command.getId();
		final String procedimentCodi = command.getCodi();
		ProcedimentDto procediment = procedimentService.findByCodi(command.getEntitatId(), procedimentCodi);
		if (procediment != null) {
			if (id == null) {
				valid = false;
			} else {
				valid = ( id.longValue() == procediment.getId().longValue() );
			}
		}
		return valid;
	}
	private boolean checkNom(
			final ProcedimentCommand command
	) {
		boolean valid = true;
		final Long id = command.getId();
		ProcedimentDto procediment = procedimentService.findByNom(command.getEntitatId(), command.getNom());
		if (procediment != null) {
			if (id == null) {
				valid = false;
			} else {
				valid = ( id.longValue() == procediment.getId().longValue() );
			}
		}
		return valid;
	}
	private static final Logger LOGGER = LoggerFactory.getLogger(ValidProcedimentValidator.class);

}
