/**
 * 
 */
package es.caib.notib.back.validation;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import es.caib.notib.logic.intf.dto.AplicacioDto;
import es.caib.notib.logic.intf.service.UsuariAplicacioService;
import es.caib.notib.back.command.AplicacioCommand;
import es.caib.notib.back.helper.MessageHelper;
import es.caib.notib.back.helper.MissatgesHelper;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi d'entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class CodiAplicacioNoRepetitValidator implements ConstraintValidator<CodiAplicacioNoRepetit, AplicacioCommand> {

	private HttpServletRequest request;
	@Autowired
	private UsuariAplicacioService usuariAplicacioService;
	
	
	@Override
	public void initialize(final CodiAplicacioNoRepetit constraintAnnotation) {

		var attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		request = attr.getRequest();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isValid(final AplicacioCommand command, final ConstraintValidatorContext context) {

		try {
			final var id = command.getId();
			final var usuariCodi = command.getUsuariCodi();
			final var entitatId = command.getEntitatId();
			var valid = true;
			
			// Comprovar codi no repetit
//			AplicacioDto aplicacio = usuariAplicacioService.findByUsuariCodi(usuariCodi);
			AplicacioDto aplicacio = usuariAplicacioService.findByEntitatAndUsuariCodi(entitatId, usuariCodi);
			if (aplicacio == null) {
				return valid;
			}
			valid = id != null ? id.longValue() == aplicacio.getId().longValue() : false;
			if (!valid) {
				context.disableDefaultConstraintViolation();
				var msg = MessageHelper.getInstance().getMessage("aplicacio.validation.codi.repetit");
				context.buildConstraintViolationWithTemplate(msg).addNode("usuariCodi").addConstraintViolation();
			}
			return valid;
			
        } catch (final Exception ex) {
        	log.error("Error al validar si el codi de l'aplicació és únic", ex);
        	MissatgesHelper.error(request, "Error inesperat en la validació del codi de l'aplicació.");
			return false;
		}
	}

}
