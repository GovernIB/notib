package es.caib.notib.war.validation;



import es.caib.notib.war.command.PersonaCommand;
import es.caib.notib.war.helper.MessageHelper;
import es.caib.notib.war.helper.NifHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Constraint de validació que controla que camp email és obligatori si està habilitada l'entrega a la Direcció Electrònica Hablitada (DEH)
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidPersonaValidator implements ConstraintValidator<ValidPersona, PersonaCommand> {

	private final int MAX_SIZE_NOM = 80;

	@Override
	public void initialize(final ValidPersona constraintAnnotation) {
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isValid(final PersonaCommand persona, final ConstraintValidatorContext context) {
		boolean valid = true;
		
		try {
			
			// Validació del Concepte
			if (persona.getNif() != null && !persona.getNif().isEmpty()) {
				if (!NifHelper.isvalid(persona.getNif())) {
					valid = false;
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("notificacio.form.valid.persona.nif"))
					.addNode("nif")
					.addConstraintViolation();
			    }
			}
			
			// Validacions per tipus de persona
			switch (persona.getInteressatTipus()) {
			case FISICA:
				valid = validarNom(persona, context, "notificacio.form.valid.fisica.nom");
				if (persona.getLlinatge1() == null || persona.getLlinatge1().isEmpty()) {
					valid = false;
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("notificacio.form.valid.fisica.llinatge1"))
					.addNode("llinatge1")
					.addConstraintViolation();
				}
				if (persona.getNif() == null || persona.getNif().isEmpty()) {
					valid = false;
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("notificacio.form.valid.fisica.nif"))
					.addNode("nif")
					.addConstraintViolation();
				}
				break;
			case JURIDICA:
				valid = validarNom(persona, context, "notificacio.form.valid.juridica.rao");
				if (persona.getNif() == null || persona.getNif().isEmpty()) {
					valid = false;
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("notificacio.form.valid.juridica.nif"))
					.addNode("nif")
					.addConstraintViolation();
				}
				break;
			case ADMINISTRACIO:
				if (persona.getNom() == null || persona.getNom().isEmpty()) {
					valid = false;
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("notificacio.form.valid.administracio.nom"))
					.addNode("nom")
					.addConstraintViolation();
				}
				if (persona.getDir3Codi() == null || persona.getDir3Codi().isEmpty()) {
					valid = false;
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("notificacio.form.valid.administracio.dir3"))
					.addNode("dir3Codi")
					.addConstraintViolation();
				}
				break;
			}
		
		} catch (final Exception ex) {
//        	LOGGER.error("Una comunicació no pot estar dirigida a una administració i a una persona física/jurídica a la vegada.", ex);
			LOGGER.error("S'ha produït un error inesperat al validar la notificació. "
					+ "Si l'error es continua donant en properes intents, posis en contacte amb els administradors de l'aplicació.", ex);
        	valid = false;
        }
		
		
		return valid;
	}

	private boolean validarNom(final PersonaCommand persona, final ConstraintValidatorContext context, String messageKey) {
		if (persona.getNom() == null || persona.getNom().isEmpty() || persona.getNom().length() > MAX_SIZE_NOM) {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(messageKey))
					.addNode("nom")
					.addConstraintViolation();
			return false;
		}
		return true;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ValidPersonaValidator.class);

}
