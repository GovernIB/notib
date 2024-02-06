/**
 * 
 */
package es.caib.notib.back.validation;

import es.caib.notib.back.command.AplicacioCommand;
import es.caib.notib.back.config.scopedata.SessionScopedContext;
import es.caib.notib.back.helper.MessageHelper;
import es.caib.notib.back.helper.MissatgesHelper;
import es.caib.notib.logic.intf.dto.AplicacioDto;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.UsuariAplicacioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Locale;

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
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private SessionScopedContext sessionScopedContext;
	
	
	@Override
	public void initialize(final CodiAplicacioNoRepetit constraintAnnotation) {

		var attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		request = attr.getRequest();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isValid(final AplicacioCommand command, final ConstraintValidatorContext context) {

		try {
			final var locale = new Locale(sessionScopedContext.getIdiomaUsuari());
			final var id = command.getId();
			final var usuariCodi = command.getUsuariCodi();
			final var entitatId = command.getEntitatId();

			if (usuariCodi == null || entitatId == null) {
				return false;
			}

			//Comprovar que l'usuari existeixi a SEYCON
			if (!aplicacioService.existeixUsuariSeycon(usuariCodi)) {
				context.disableDefaultConstraintViolation();
				var msg = MessageHelper.getInstance().getMessage("aplicacio.validation.codi.inexistent", null, locale);
				context.buildConstraintViolationWithTemplate(msg).addNode("usuariCodi").addConstraintViolation();
				return false;
			}

			// Comprovar codi no repetit
			AplicacioDto aplicacio = usuariAplicacioService.findByEntitatAndUsuariCodi(entitatId, usuariCodi);
			if (aplicacio != null && id == null || aplicacio != null && id.longValue() != aplicacio.getId().longValue()) {
				context.disableDefaultConstraintViolation();
				var msg = MessageHelper.getInstance().getMessage("aplicacio.validation.codi.repetit", null, locale);
				context.buildConstraintViolationWithTemplate(msg).addNode("usuariCodi").addConstraintViolation();
				return false;
			}
			return true;
			
        } catch (final Exception ex) {
			var msg = "Error inesperat en la validació del codi de l'aplicació.";
			log.error(msg, ex);
			MissatgesHelper.error(request, msg);
			return false;
		}
	}

}
