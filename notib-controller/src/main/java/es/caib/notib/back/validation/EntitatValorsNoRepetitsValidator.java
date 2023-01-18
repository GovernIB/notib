/**
 * 
 */
package es.caib.notib.back.validation;

import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.back.command.EntitatCommand;
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
 * el codi d'entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntitatValorsNoRepetitsValidator implements ConstraintValidator<EntitatValorsNoRepetits, EntitatCommand> {

	private HttpServletRequest request;

	@Autowired
	private EntitatService entitatService;



	@Override
	public void initialize(final EntitatValorsNoRepetits constraintAnnotation) {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		request = attr.getRequest();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isValid(final EntitatCommand entitatCommand, final ConstraintValidatorContext context) {

		try {
			final Long id = entitatCommand.getId();
			final String codi = entitatCommand.getCodi();
			final String dir3 = entitatCommand.getDir3Codi();
			boolean valid = true;

			// Comprovar codi no repetit
			EntitatDto entitat = entitatService.findByCodi(codi);
			if (entitat != null  && (id != null && !id.equals(entitat.getId()) && codi.equals(entitat.getCodi()) || id == null && codi.equals(entitat.getCodi()))) {
				valid = false;
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("entitat.validation.codi.repetit")).addNode("codi").addConstraintViolation();
			}
			// Comprovar cif no repetit
			entitat = entitatService.findByDir3codi(dir3);
			if (entitat != null && (id != null && !id.equals(entitat.getId()) && dir3.equals(entitat.getDir3Codi()) || id == null && dir3.equals(entitat.getDir3Codi()))) {
				valid = false;
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("entitat.validation.dir3.repetit"))
						.addNode("dir3Codi").addConstraintViolation();
			}
			// validar si és oficina per entitat
			if (entitatCommand.isOficinaEntitat() && (entitatCommand.getOficina() == null || entitatCommand.getOficina().isEmpty())) {
				valid = false;
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty"))
						.addNode("oficina").addConstraintViolation();
			}
			if (!entitatCommand.isEntregaCieActiva()) {
				return valid;
			}
			if (entitatCommand.getOperadorPostalId() == null) {
				valid = false;
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty"))
						.addNode("operadorPostalId").addConstraintViolation();
			}

			if (entitatCommand.getCieId() == null) {
				valid = false;
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty"))
						.addNode("cieId").addConstraintViolation();
			}
			return valid;
        } catch (Exception ex) {
        	LOGGER.error("Error al validar si el codi d'entitat és únic", ex);
        	MissatgesHelper.error(request, "Error inesperat en la validació de les dades de l'entitat.");
        }
        return false;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(EntitatValorsNoRepetitsValidator.class);

}