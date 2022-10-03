/**
 * 
 */
package es.caib.notib.back.validation;

import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.back.command.OrganGestorCommand;
import es.caib.notib.back.helper.MessageHelper;
import es.caib.notib.back.helper.MissatgesHelper;
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

			if (command.getEntitatId() == null) {
				valid = false;
				LOGGER.error("La entitat no pot estar buida");
				throw new Exception();
			}
			
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

			if (command.isEntregaCieActiva()) {
				if (command.getOperadorPostalId() == null) {
					valid = false;
					context.disableDefaultConstraintViolation();
					context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty"))
							.addNode("operadorPostalId").addConstraintViolation();
				}

				if (command.getCieId() == null) {
					valid = false;
					context.disableDefaultConstraintViolation();
					context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty"))
							.addNode("cieId").addConstraintViolation();
				}
			}
			
        } catch (final Exception ex) {
        	valid = false;
        	LOGGER.error("Error al validar si el codi de l'òrgan gestor és únic", ex);
        	MissatgesHelper.error(request, "Error inesperat en la validació del codi de l'òrgan gestor.");
        }
       	if (!valid) {
			context.disableDefaultConstraintViolation();
		}
       	
       	return valid;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(OrganNoRepetitValidator.class);

}
