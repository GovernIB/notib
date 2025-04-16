package es.caib.notib.back.validation;


import com.google.common.base.Strings;
import es.caib.notib.back.command.PersonaCommand;
import es.caib.notib.back.config.scopedata.SessionScopedContext;
import es.caib.notib.back.helper.MessageHelper;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.logic.intf.util.EidasValidator;
import es.caib.notib.logic.intf.util.NifHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Locale;

/**
 * Constraint de validació que controla que camp email és obligatori si està habilitada l'entrega a la Direcció Electrònica Hablitada (DEH)
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class ValidPersonaValidator implements ConstraintValidator<ValidPersona, PersonaCommand> {

	public static final int MAX_SIZE_NOM = 30;
	public static final int MAX_SIZE_RAO_SOCIAL = 80;
	private static final int MIN_SIZE_LLINATGES = 2;
	private static final int MIN_SIZE_NOM_RAO = 2;

	@Autowired
	private SessionScopedContext sessionScopedContext;

	@Override
	public void initialize(final ValidPersona constraintAnnotation) {
		// init
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isValid(final PersonaCommand persona, final ConstraintValidatorContext context) {
		boolean valid = true;

		try {
			Locale locale = new Locale(sessionScopedContext.getIdiomaUsuari());
//			// Validació del NIF/NIE/CIF
//			if (!Strings.isNullOrEmpty(persona.getNif()) && !InteressatTipus.FISICA_SENSE_NIF.equals(persona.getInteressatTipus()) && !NifHelper.isvalid(persona.getNif())) {
//				valid = false;
//				String msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.persona.nif", null, locale);
//				context.buildConstraintViolationWithTemplate(msg).addNode("nif").addConstraintViolation();
//			}

			// Validacions per tipus de persona
			switch (persona.getInteressatTipus()) {
				case FISICA:
					valid = validarNom(persona, context, locale);
					String llinatge1Interessat = persona.getLlinatge1();
					String llinatge2Interessat = persona.getLlinatge2();

					if (Strings.isNullOrEmpty(llinatge1Interessat)) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage("notificacio.form.valid.fisica.llinatge1", null, locale))
								.addNode("llinatge1")
								.addConstraintViolation();
					}
					if (!Strings.isNullOrEmpty(llinatge1Interessat)) {
						int llinatge1InteressatSize = llinatge1Interessat.length();
						int llinatge2InteressatSize = llinatge2Interessat != null ? llinatge2Interessat.length() : 0;
						if ((llinatge1InteressatSize + llinatge2InteressatSize) < MIN_SIZE_LLINATGES) {
							valid = false;
							context.buildConstraintViolationWithTemplate(
											MessageHelper.getInstance().getMessage("notificacio.form.valid.fisica.llinatges.size", new Object[] {MIN_SIZE_LLINATGES}, locale))
									.addNode("llinatge1").addConstraintViolation();
						}
					}
					if (Strings.isNullOrEmpty(persona.getNif())) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage("notificacio.form.valid.fisica.nif", null, locale))
								.addNode("nif")
								.addConstraintViolation();
					}
					if (!Strings.isNullOrEmpty(persona.getNif()) && !NifHelper.isValidNifNie(persona.getNif()) && !EidasValidator.validateEidas(persona.getNif())) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage("notificacio.form.valid.fisica.tipoDocumentoIncorrecto", null, locale))
								.addNode("nif")
								.addConstraintViolation();
					}
					break;
				case FISICA_SENSE_NIF:
					valid = validarNom(persona, context, locale);
					if (persona.getLlinatge1() == null || persona.getLlinatge1().isEmpty()) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage("notificacio.form.valid.fisica.llinatge1", null, locale))
								.addNode("llinatge1")
								.addConstraintViolation();
					}
					String llinatge1 = persona.getLlinatge1();
					String llinatge2 = persona.getLlinatge2();
					int llinatge1Size = llinatge1.length();
					int llinatge2Size = llinatge2 != null ? llinatge2.length() : 0;
					if ((llinatge1Size + llinatge2Size) < MIN_SIZE_LLINATGES) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage("notificacio.form.valid.fisica.llinatges.size", new Object[] {MIN_SIZE_LLINATGES}, locale))
								.addNode("llinatge1").addConstraintViolation();
					}
					break;
				case JURIDICA:
					valid = validarNom(persona, context, locale);
					if (persona.getNif() == null || persona.getNif().isEmpty()) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage("notificacio.form.valid.juridica.cif", null, locale))
								.addNode("nif")
								.addConstraintViolation();
					}
					if (persona.getNif() != null && !persona.getNif().isEmpty() && !NifHelper.isValidCif(persona.getNif()) && !EidasValidator.validateEidas(persona.getNif())) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage("notificacio.form.valid.juridica.tipoDocumentoIncorrecto", null, locale))
								.addNode("nif")
								.addConstraintViolation();
					}
					break;
				case ADMINISTRACIO:
					if (Strings.isNullOrEmpty(persona.getRaoSocialInput())) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage("notificacio.form.valid.administracio.nom", null, locale))
								.addNode("nom")
								.addConstraintViolation();
					}
					if (!Strings.isNullOrEmpty(persona.getRaoSocialInput()) && persona.getRaoSocialInput().length() > MAX_SIZE_RAO_SOCIAL) {
						valid = false;
						var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.juridica.rao.max.length", new Object[] {MAX_SIZE_RAO_SOCIAL}, locale);
						context.buildConstraintViolationWithTemplate(msg).addNode("raoSocial").addConstraintViolation();
						context.buildConstraintViolationWithTemplate(msg).addNode("dir3CodiInput").addConstraintViolation();
					}
					if (Strings.isNullOrEmpty(persona.getDir3Codi())) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage("notificacio.form.valid.administracio.dir3", null, locale))
								.addNode("dir3Codi")
								.addConstraintViolation();
//						context.buildConstraintViolationWithTemplate(
//										MessageHelper.getInstance().getMessage("notificacio.form.valid.administracio.dir3", null, locale))
//								.addNode("dir3CodiInput")
//								.addConstraintViolation();
					}

//					if (Strings.isNullOrEmpty(persona.getNif())) {
//						valid = false;
//						context.buildConstraintViolationWithTemplate(
//										MessageHelper.getInstance().getMessage("notificacio.form.valid.nif.obligatori", null, locale))
//								.addNode("nif")
//								.addConstraintViolation();
//						context.buildConstraintViolationWithTemplate(
//										MessageHelper.getInstance().getMessage("notificacio.form.valid.nif.obligatori", null, locale))
//								.addNode("dir3CodiInput")
//								.addConstraintViolation();
//					}
					if (!Strings.isNullOrEmpty(persona.getNif()) && !NifHelper.isvalid(persona.getNif())) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage("notificacio.form.valid.nif.error", null, locale))
								.addNode("nif")
								.addConstraintViolation();
					}

					break;
			}

		} catch (final Exception ex) {
 			log.error("S'ha produït un error inesperat al validar la notificació. "
					+ "Si l'error es continua donant en properes intents, posis en contacte amb els administradors de l'aplicació.", ex);
			valid = false;
		}


		return valid;
	}

	private boolean validarNom(final PersonaCommand persona, final ConstraintValidatorContext context, final Locale locale) {
		boolean isJuridica = InteressatTipus.JURIDICA.equals(persona.getInteressatTipus());
		String msgKey = "";
		boolean ok = true;
		Object [] vars = null;
		String raoSocialInteressat = persona.getRaoSocialInput();
		String nomInteressat = persona.getNomInput();
		if (Strings.isNullOrEmpty(raoSocialInteressat) && isJuridica) {
			ok = false;
			msgKey = "notificacio.form.valid.juridica.rao";
		}
		if (Strings.isNullOrEmpty(nomInteressat) && !isJuridica) {
			ok = false;
			msgKey = "notificacio.form.valid.fisica.nom";
		}
		if (isJuridica && raoSocialInteressat.length() > MAX_SIZE_RAO_SOCIAL) {
			ok = false;
			msgKey = "notificacio.form.valid.juridica.rao.max.length";
			vars = new Object[] {MAX_SIZE_RAO_SOCIAL};
		}
		if (isJuridica && !raoSocialInteressat.isEmpty() && raoSocialInteressat.length() < MIN_SIZE_NOM_RAO) {
			ok = false;
			msgKey = "notificacio.form.valid.juridica.rao.min.length";
			vars = new Object[] {MIN_SIZE_NOM_RAO};
		}
		if (!isJuridica && nomInteressat.length() > MAX_SIZE_NOM) {
			ok = false;
			msgKey = "notificacio.form.valid.fisica.nom.max.length";
			vars = new Object[] {MAX_SIZE_NOM};
		}
		if (!isJuridica && !nomInteressat.isEmpty() && nomInteressat.length() < MIN_SIZE_NOM_RAO) {
			ok = false;
			msgKey = "notificacio.form.valid.fisica.nom.min.length";
			vars = new Object[] {MIN_SIZE_NOM_RAO};
		}
		if (!ok) {
			String node = isJuridica ? "raoSocialInput" : "nomInput";
			String msg = MessageHelper.getInstance().getMessage(msgKey, vars, locale);
			context.buildConstraintViolationWithTemplate(msg).addNode(node).addConstraintViolation();
		}
		return ok;
	}
}
