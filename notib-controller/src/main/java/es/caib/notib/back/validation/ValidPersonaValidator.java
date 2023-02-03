package es.caib.notib.back.validation;


import com.google.common.base.Strings;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.back.command.PersonaCommand;
import es.caib.notib.back.helper.MessageHelper;
import es.caib.notib.back.helper.NifHelper;
import es.caib.notib.back.helper.SessioHelper;
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
	private AplicacioService aplicacioService;

	private Locale locale;
	private PersonaCommand persona;
	private ConstraintValidatorContext context;

	@Override
	public void initialize(final ValidPersona constraintAnnotation) {
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isValid(final PersonaCommand persona, final ConstraintValidatorContext context) {

		boolean valid = true;
		try {
			this.persona = persona;
			this.context = context;
			locale = new Locale(SessioHelper.getIdioma(aplicacioService));
			// Validació del NIF/NIE/CIF
			if (persona.getNif() != null && !persona.getNif().isEmpty() && !InteressatTipus.FISICA_SENSE_NIF.equals(persona.getInteressatTipus())) {
				if (!NifHelper.isvalid(persona.getNif())) {
					valid = false;
					var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.persona.nif", null, locale);
					context.buildConstraintViolationWithTemplate(msg).addNode("nif").addConstraintViolation();
			    }
			}
			// Validacions per tipus de persona
			switch (persona.getInteressatTipus()) {
				case FISICA:
					return valid && validFisica();
				case FISICA_SENSE_NIF:
					return valid && validFisicaSenseNif();
				case JURIDICA:
					return valid && validJuridica();
				case ADMINISTRACIO:
					return valid && validAdministracio();
				default:
					return false;
			}

		} catch (final Exception ex) {
//        	LOGGER.error("Una comunicació no pot estar dirigida a una administració i a una persona física/jurídica a la vegada.", ex);
			log.error("S'ha produït un error inesperat al validar la notificació. "
					+ "Si l'error es continua donant en properes intents, posis en contacte amb els administradors de l'aplicació.", ex);
			return false;
		}
	}

	private boolean validFisica() {

		var valid = true;
		valid = validarNom(persona, context);
		String llinatge1Interessat = persona.getLlinatge1();
		String llinatge2Interessat = persona.getLlinatge2();

		if (Strings.isNullOrEmpty(llinatge1Interessat)) {
			valid = false;
			var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.fisica.llinatge1", null, locale);
			context.buildConstraintViolationWithTemplate(msg).addNode("llinatge1").addConstraintViolation();
		}
		if (!Strings.isNullOrEmpty(llinatge1Interessat)) {
			var llinatge1InteressatSize = llinatge1Interessat.length();
			var llinatge2InteressatSize = llinatge2Interessat != null ? llinatge2Interessat.length() : 0;
			if ((llinatge1InteressatSize + llinatge2InteressatSize) < MIN_SIZE_LLINATGES) {
				valid = false;
				var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.fisica.llinatges.size", new Object[] {MIN_SIZE_LLINATGES}, locale);
				context.buildConstraintViolationWithTemplate(msg).addNode("llinatge1").addConstraintViolation();
			}
		}
		if (Strings.isNullOrEmpty(persona.getNif())) {
			valid = false;
			var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.fisica.nif", null, locale);
			context.buildConstraintViolationWithTemplate(msg).addNode("nif").addConstraintViolation();
		}
		if (!Strings.isNullOrEmpty(persona.getNif()) && !NifHelper.isValidNifNie(persona.getNif())) {
			valid = false;
			var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.fisica.tipoDocumentoIncorrecto", null, locale);
			context.buildConstraintViolationWithTemplate(msg).addNode("nif").addConstraintViolation();
		}
		return valid;
	}

	private boolean validFisicaSenseNif() {

		var valid = true;
		valid = validarNom(persona, context);
		if (persona.getLlinatge1() == null || persona.getLlinatge1().isEmpty()) {
			valid = false;
			var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.fisica.llinatge1", null, locale);
			context.buildConstraintViolationWithTemplate(msg).addNode("llinatge1").addConstraintViolation();
		}
		var llinatge1 = persona.getLlinatge1();
		var llinatge2 = persona.getLlinatge2();
		var llinatge1Size = llinatge1.length();
		var llinatge2Size = llinatge2 != null ? llinatge2.length() : 0;
		if ((llinatge1Size + llinatge2Size) < MIN_SIZE_LLINATGES) {
			valid = false;
			var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.fisica.llinatges.size", new Object[] {MIN_SIZE_LLINATGES}, locale);
			context.buildConstraintViolationWithTemplate(msg).addNode("llinatge1").addConstraintViolation();
		}
//				if (persona.getNif() != null && !persona.getNif().isEmpty() && !NifHelper.isValidNifNie(persona.getNif())) {
//					valid = false;
//					context.buildConstraintViolationWithTemplate(
//									MessageHelper.getInstance().getMessage("notificacio.form.valid.fisica.tipoDocumentoIncorrecto", null, locale))
//							.addNode("nif")
//							.addConstraintViolation();
//				}
		return valid;
	}

	private boolean validJuridica() {

		var valid = true;
		valid = validarNom(persona, context);
		if (persona.getNif() == null || persona.getNif().isEmpty()) {
			valid = false;
			context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("notificacio.form.valid.juridica.cif", null, locale))
					.addNode("nif")
					.addConstraintViolation();
		}
		if (persona.getNif() != null && !persona.getNif().isEmpty() && !NifHelper.isValidCif(persona.getNif())) {
			valid = false;
			var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.juridica.tipoDocumentoIncorrecto", null, locale);
			context.buildConstraintViolationWithTemplate(msg).addNode("nif").addConstraintViolation();
		}
		return valid;
	}

	private boolean validAdministracio() {

		var valid = true;
		if (Strings.isNullOrEmpty(persona.getRaoSocialInput())) {
			valid = false;
			var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.administracio.nom", null, locale);
			context.buildConstraintViolationWithTemplate(msg).addNode("nom").addConstraintViolation();
		}
		if (Strings.isNullOrEmpty(persona.getDir3Codi())) {
			valid = false;
			var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.administracio.dir3", null, locale);
			context.buildConstraintViolationWithTemplate(msg).addNode("dir3Codi").addConstraintViolation();
		}
		return valid;
	}

	private boolean validarNom(final PersonaCommand persona, final ConstraintValidatorContext context) {

		var locale = new Locale(SessioHelper.getIdioma(aplicacioService));
		var isJuridica = InteressatTipus.JURIDICA.equals(persona.getInteressatTipus());
		var msgKey = "";
		var ok = true;
		Object [] vars = null;
		var raoSocialInteressat = persona.getRaoSocialInput();
		var nomInteressat = persona.getNomInput();
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
