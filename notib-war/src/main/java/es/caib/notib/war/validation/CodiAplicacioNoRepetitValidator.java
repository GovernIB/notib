/**
 * 
 */
package es.caib.notib.war.validation;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import es.caib.notib.core.api.dto.AplicacioDto;
import es.caib.notib.core.api.service.UsuariAplicacioService;
import es.caib.notib.war.command.AplicacioCommand;
import es.caib.notib.war.helper.MessageHelper;
import es.caib.notib.war.helper.MissatgesHelper;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi d'entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CodiAplicacioNoRepetitValidator implements ConstraintValidator<CodiAplicacioNoRepetit, AplicacioCommand> {

	private HttpServletRequest request;

	@Autowired
	private UsuariAplicacioService usuariAplicacioService;
	
	
	@Override
	public void initialize(final CodiAplicacioNoRepetit constraintAnnotation) {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		request = attr.getRequest();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isValid(
			final AplicacioCommand command, 
			final ConstraintValidatorContext context) {
		try {
			
			final Long id = command.getId();
			final String usuariCodi = command.getUsuariCodi();
			final Long entitatId = command.getEntitatId();
			
			boolean valid = true;
			
			// Comprovar codi no repetit
//			AplicacioDto aplicacio = usuariAplicacioService.findByUsuariCodi(usuariCodi);
			AplicacioDto aplicacio = usuariAplicacioService.findByEntitatAndUsuariCodi(entitatId, usuariCodi);
			if (aplicacio != null) {
				if (id == null) {
					valid = false;
				} else {
					valid = ( id.longValue() == aplicacio.getId().longValue() );
				}
				if (!valid) {
					context.disableDefaultConstraintViolation();
					context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("aplicacio.validation.codi.repetit")).addNode("usuariCodi").addConstraintViolation();
				}
			}
			
			return valid;
			
        } catch (final Exception ex) {
        	LOGGER.error("Error al validar si el codi de l'aplicació és únic", ex);
        	MissatgesHelper.error(request, "Error inesperat en la validació del codi de l'aplicació.");
        }
        return false;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CodiAplicacioNoRepetitValidator.class);

}
