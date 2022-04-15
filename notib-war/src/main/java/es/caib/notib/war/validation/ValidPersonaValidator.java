package es.caib.notib.war.validation;


import es.caib.notib.client.domini.InteressatTipusEnumDto;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.war.command.PersonaCommand;
import es.caib.notib.war.helper.MessageHelper;
import es.caib.notib.war.helper.NifHelper;
import es.caib.notib.war.helper.SessioHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Locale;

/**
 * Constraint de validació que controla que camp email és obligatori si està habilitada l'entrega a la Direcció Electrònica Hablitada (DEH)
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidPersonaValidator implements ConstraintValidator<ValidPersona, PersonaCommand> {

	private final int MAX_SIZE_NOM = 80;

	@Autowired
	private AplicacioService aplicacioService;

	@Override
	public void initialize(final ValidPersona constraintAnnotation) {
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isValid(final PersonaCommand persona, final ConstraintValidatorContext context) {
		boolean valid = true;
		
		try {
			Locale locale = new Locale(SessioHelper.getIdioma(aplicacioService));
			// Validació del NIF/NIE/CIF
			if (persona.getNif() != null && !persona.getNif().isEmpty() && !InteressatTipusEnumDto.FISICA_SENSE_NIF.equals(persona.getInteressatTipus())) {
				if (!NifHelper.isvalid(persona.getNif())) {
					valid = false;
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("notificacio.form.valid.persona.nif", null, locale))
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
							MessageHelper.getInstance().getMessage("notificacio.form.valid.fisica.llinatge1", null, locale))
					.addNode("llinatge1")
					.addConstraintViolation();
				}
				if (persona.getNif() == null || persona.getNif().isEmpty()) {
					valid = false;
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("notificacio.form.valid.fisica.nif", null, locale))
					.addNode("nif")
					.addConstraintViolation();
				}
				if (persona.getNif() != null && !persona.getNif().isEmpty() && !NifHelper.isValidNifNie(persona.getNif())) {
					valid = false;
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("notificacio.form.valid.fisica.tipoDocumentoIncorrecto", null, locale))
					.addNode("nif")
					.addConstraintViolation();
				}
				break;
			case FISICA_SENSE_NIF:
				valid = validarNom(persona, context, "notificacio.form.valid.fisica.nom");
				if (persona.getLlinatge1() == null || persona.getLlinatge1().isEmpty()) {
					valid = false;
					context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage("notificacio.form.valid.fisica.llinatge1", null, locale))
							.addNode("llinatge1")
							.addConstraintViolation();
				}
//				if (persona.getNif() != null && !persona.getNif().isEmpty() && !NifHelper.isValidNifNie(persona.getNif())) {
//					valid = false;
//					context.buildConstraintViolationWithTemplate(
//									MessageHelper.getInstance().getMessage("notificacio.form.valid.fisica.tipoDocumentoIncorrecto", null, locale))
//							.addNode("nif")
//							.addConstraintViolation();
//				}
				break;
			case JURIDICA:
				valid = validarNom(persona, context, "notificacio.form.valid.juridica.rao");
				if (persona.getNif() == null || persona.getNif().isEmpty()) {
					valid = false;
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("notificacio.form.valid.juridica.cif", null, locale))
					.addNode("nif")
					.addConstraintViolation();
				}
				if (persona.getNif() != null && !persona.getNif().isEmpty() && !NifHelper.isValidCif(persona.getNif())) {
					valid = false;
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("notificacio.form.valid.juridica.tipoDocumentoIncorrecto", null, locale))
					.addNode("nif")
					.addConstraintViolation();
				}
				break;
			case ADMINISTRACIO:
				if (persona.getNom() == null || persona.getNom().isEmpty()) {
					valid = false;
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("notificacio.form.valid.administracio.nom", null, locale))
					.addNode("nom")
					.addConstraintViolation();
				}
				if (persona.getDir3Codi() == null || persona.getDir3Codi().isEmpty()) {
					valid = false;
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("notificacio.form.valid.administracio.dir3", null, locale))
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
		Locale locale = new Locale(SessioHelper.getIdioma(aplicacioService));
		if (persona.getNom() == null || persona.getNom().isEmpty() || persona.getNom().length() > MAX_SIZE_NOM) {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(messageKey, null ,locale))
					.addNode("nom")
					.addConstraintViolation();
			return false;
		}
		return true;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ValidPersonaValidator.class);

}
