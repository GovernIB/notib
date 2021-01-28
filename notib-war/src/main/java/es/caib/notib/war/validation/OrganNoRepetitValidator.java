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

import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.OrganGestorService;
import es.caib.notib.war.command.OrganGestorCommand;
import es.caib.notib.war.helper.MessageHelper;
import es.caib.notib.war.helper.MissatgesHelper;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi de procediment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class OrganNoRepetitValidator implements ConstraintValidator<OrganNoRepetit, OrganGestorCommand> {

	private HttpServletRequest request;

	@Autowired
	private OrganGestorService organGestorService;
	@Autowired
	private EntitatService entitatService;
	
	
	@Override
	public void initialize(final OrganNoRepetit constraintAnnotation) {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		request = attr.getRequest();
	}

	@Override
	public boolean isValid(
			final OrganGestorCommand command, 
			final ConstraintValidatorContext context) {

		boolean valid = true;
		
		try {
			
			final String codi = command.getCodi();
			final Long id = command.getId();
			
			// Comprovar codi no repetit
			OrganGestorDto organGestor = null;
			try {
				organGestor = organGestorService.findByCodi(command.getEntitatId(), codi);
			} catch (NotFoundException e) {	}
			
			if (organGestor != null && id == null) {
				valid = false;
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("organgestor.validation.codi.repetit")).addConstraintViolation();
			}
			
			// Si el llibre es desa a l'òrgan, llavors comprovar que s'ha informat
			if (!entitatService.findById(command.getEntitatId()).isLlibreEntitat() && id == null) {
				if (command.getLlibre() == null || command.getLlibre().isEmpty()) {
					valid = false;
					context.disableDefaultConstraintViolation();
					context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("organgestor.validation.llibre.buit")).addConstraintViolation();
				}
			}
			
        } catch (final Exception ex) {
        	valid = false;
        	LOGGER.error("Error al validar si el codi de l'òrgan gestor és únic", ex);
        	MissatgesHelper.error(request, "Error inesperat en la validació del codi de l'òrgan gestor.");
        }
       	if (!valid)
       		context.disableDefaultConstraintViolation();
       	
       	return valid;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(OrganNoRepetitValidator.class);

}
