/**
 * 
 */
package es.caib.notib.war.validation;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.war.helper.SessioHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import es.caib.notib.core.api.dto.AplicacioDto;
import es.caib.notib.core.api.service.UsuariAplicacioService;
import es.caib.notib.war.command.AplicacioCommand;
import es.caib.notib.war.helper.MessageHelper;
import es.caib.notib.war.helper.MissatgesHelper;

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

	
	@Override
	public void initialize(final CodiAplicacioNoRepetit constraintAnnotation) {

		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		request = attr.getRequest();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isValid(final AplicacioCommand command, final ConstraintValidatorContext context) {

		try {
			Locale locale = new Locale(SessioHelper.getIdioma(aplicacioService));
			final Long id = command.getId();
			final String usuariCodi = command.getUsuariCodi();
			final Long entitatId = command.getEntitatId();

			if (usuariCodi == null || entitatId == null) {
				return false;
			}

			//Comprovar que l'usuari existeixi a SEYCON
			if (!aplicacioService.existeixUsuariSeycon(usuariCodi)) {
				context.disableDefaultConstraintViolation();
				String msg = MessageHelper.getInstance().getMessage("aplicacio.validation.codi.inexistent", null, locale);
				context.buildConstraintViolationWithTemplate(msg).addNode("usuariCodi").addConstraintViolation();
				return false;
			}

			// Comprovar codi no repetit
			AplicacioDto aplicacio = usuariAplicacioService.findByEntitatAndUsuariCodi(entitatId, usuariCodi);
			if (aplicacio != null && id == null || aplicacio != null && id.longValue() != aplicacio.getId().longValue()) {
				context.disableDefaultConstraintViolation();
				String msg = MessageHelper.getInstance().getMessage("aplicacio.validation.codi.repetit", null, locale);
				context.buildConstraintViolationWithTemplate(msg).addNode("usuariCodi").addConstraintViolation();
				return false;
			}
			return true;
			
        } catch (final Exception ex) {
			String msg = "Error inesperat en la validació del codi de l'aplicació.";
        	log.error(msg, ex);
        	MissatgesHelper.error(request, msg);
			return false;
		}
	}

}
