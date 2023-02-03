package es.caib.notib.back.validation;

import es.caib.notib.back.command.EntregapostalCommand;
import es.caib.notib.back.helper.MessageHelper;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Constraint de validació que controla que camp email és obligatori si està habilitada l'entrega a la Direcció Electrònica Hablitada (DEH)
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class ValidEntregaPostalValidator implements ConstraintValidator<ValidEntregaPostal, EntregapostalCommand> {

	@Override
	public void initialize(final ValidEntregaPostal constraintAnnotation) {
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isValid(final EntregapostalCommand entregaPostal, final ConstraintValidatorContext context) {

		if (!entregaPostal.isActiva()) {
			return true;
		}
		try {
			// Validacions per tipus de entrega postal
			switch (entregaPostal.getDomiciliConcretTipus()) {
				case NACIONAL:
					return validNacional(entregaPostal, context);
				case ESTRANGER:
					return validEstranger(entregaPostal, context);
				case APARTAT_CORREUS:
					return validApartatCorreus(entregaPostal, context);
				case SENSE_NORMALITZAR:
					return validSenseNormalitzar(entregaPostal, context);
				default:
					return true;
			}
		} catch (final Exception ex) {
			log.error("S'ha produït un error inesperat al validar la notificació. "
					+ "Si l'error es continua donant en properes intents, posis en contacte amb els administradors de l'aplicació.", ex);
			return false;
		}
	}

	public boolean validNacional(final EntregapostalCommand entregaPostal, final ConstraintValidatorContext context) {

		var valid = true;
		var nacionalNotEmpty = MessageHelper.getInstance().getMessage("entregapostal.form.valid.nacional.notempty");
		if (entregaPostal.getViaTipus() == null) {
			valid = false;
			context.buildConstraintViolationWithTemplate(nacionalNotEmpty).addNode("tipusVia").addConstraintViolation();
		}
		if (entregaPostal.getViaNom() == null || entregaPostal.getViaNom().isEmpty()) {
			valid = false;
			context.buildConstraintViolationWithTemplate(nacionalNotEmpty).addNode("viaNom").addConstraintViolation();
		}
		if ((entregaPostal.getPuntKm() == null || entregaPostal.getPuntKm().isEmpty())
				&& (entregaPostal.getNumeroCasa() == null || entregaPostal.getNumeroCasa().isEmpty())) {
			valid = false;
			var nacionalPuntKm = MessageHelper.getInstance().getMessage("entregapostal.form.valid.nacional.puntkm.numcasa");
			context.buildConstraintViolationWithTemplate(nacionalPuntKm).addNode("numeroCasa").addConstraintViolation();
			context.buildConstraintViolationWithTemplate(nacionalPuntKm).addNode("puntKm").addConstraintViolation();
		}
		if (entregaPostal.getMunicipiCodi() == null || entregaPostal.getMunicipiCodi().isEmpty()) {
			valid = false;
			context.buildConstraintViolationWithTemplate(nacionalNotEmpty).addNode("municipiCodi").addConstraintViolation();
		}
		if (entregaPostal.getProvincia() == null || entregaPostal.getProvincia().isEmpty()) {
			valid = false;
			context.buildConstraintViolationWithTemplate(nacionalNotEmpty).addNode("provincia").addConstraintViolation();
		}
		if (entregaPostal.getPoblacio() == null || entregaPostal.getPoblacio().isEmpty()) {
			valid = false;
			context.buildConstraintViolationWithTemplate(nacionalNotEmpty).addNode("poblacio").addConstraintViolation();
		}
		if (entregaPostal.getCodiPostal() == null || entregaPostal.getCodiPostal().isEmpty()) {
			valid = false;
			var msg = MessageHelper.getInstance().getMessage("entregapostal.form.valid.codi.postal.buit");
			context.buildConstraintViolationWithTemplate(msg).addNode("codiPostal").addConstraintViolation();
		}
		return valid;
	}

	public boolean validEstranger(final EntregapostalCommand entregaPostal, final ConstraintValidatorContext context) {

		var valid = true;
		var estrangerNotEmpty = MessageHelper.getInstance().getMessage("entregapostal.form.valid.estranger.notempty");
		if (entregaPostal.getViaNom() == null || entregaPostal.getViaNom().isEmpty()) {
			valid = false;
			context.buildConstraintViolationWithTemplate(estrangerNotEmpty).addNode("viaNom").addConstraintViolation();
		}
		if (entregaPostal.getPaisCodi() == null || entregaPostal.getPaisCodi().isEmpty()) {
			valid = false;
			context.buildConstraintViolationWithTemplate(estrangerNotEmpty).addNode("getPaisCodi").addConstraintViolation();
		}
		if (entregaPostal.getPoblacio() == null || entregaPostal.getPoblacio().isEmpty()) {
			valid = false;
			context.buildConstraintViolationWithTemplate(estrangerNotEmpty).addNode("poblacio").addConstraintViolation();
		}
		if (entregaPostal.getCodiPostal() == null || entregaPostal.getCodiPostal().isEmpty()) {
			valid = false;
			var msg = MessageHelper.getInstance().getMessage("entregapostal.form.valid.codi.postal.estranger.buit");
			context.buildConstraintViolationWithTemplate(msg).addNode("codiPostal").addConstraintViolation();
		}
		return valid;
	}

	public boolean validApartatCorreus(final EntregapostalCommand entregaPostal, final ConstraintValidatorContext context) {

		var valid = true;
		var apCorreuNotEmpty = MessageHelper.getInstance().getMessage("entregapostal.form.valid.apcorreu.notempty");
		if (entregaPostal.getApartatCorreus() == null || entregaPostal.getApartatCorreus().isEmpty()) {
			valid = false;
			context.buildConstraintViolationWithTemplate(apCorreuNotEmpty).addNode("apartatCorreus").addConstraintViolation();
		}
		if (entregaPostal.getMunicipiCodi() == null || entregaPostal.getMunicipiCodi().isEmpty()) {
			valid = false;
			context.buildConstraintViolationWithTemplate(apCorreuNotEmpty).addNode("municipiCodi").addConstraintViolation();
		}
		if (entregaPostal.getProvincia() == null || entregaPostal.getProvincia().isEmpty()) {
			valid = false;
			context.buildConstraintViolationWithTemplate(apCorreuNotEmpty).addNode("provincia").addConstraintViolation();
		}
		if (entregaPostal.getPoblacio() == null || entregaPostal.getPoblacio().isEmpty()) {
			valid = false;
			context.buildConstraintViolationWithTemplate(apCorreuNotEmpty).addNode("poblacio").addConstraintViolation();
		}
		if (entregaPostal.getCodiPostal() == null || entregaPostal.getCodiPostal().isEmpty()) {
			valid = false;
			var msg = MessageHelper.getInstance().getMessage("entregapostal.form.valid.codi.postal.buit");
			context.buildConstraintViolationWithTemplate(msg).addNode("codiPostal").addConstraintViolation();
		}
		return valid;
	}

	public boolean validSenseNormalitzar(final EntregapostalCommand entregaPostal, final ConstraintValidatorContext context) {

		var valid = true;
		var noNormalitzatNotEmpty = MessageHelper.getInstance().getMessage("entregapostal.form.valid.no.normalitzat.notempty");
		if (entregaPostal.getLinea1() == null || entregaPostal.getLinea1().isEmpty()) {
			valid = false;
			context.buildConstraintViolationWithTemplate(noNormalitzatNotEmpty).addNode("linea1").addConstraintViolation();
		}
		if (entregaPostal.getLinea2() == null || entregaPostal.getLinea2().isEmpty()) {
			valid = false;
			context.buildConstraintViolationWithTemplate(noNormalitzatNotEmpty).addNode("linea2").addConstraintViolation();
		}
		if (entregaPostal.getCodiPostalNorm() == null || entregaPostal.getCodiPostalNorm().isEmpty()) {
			valid = false;
			var msg = MessageHelper.getInstance().getMessage("entregapostal.form.valid.codi.postal.buit");
			context.buildConstraintViolationWithTemplate(msg).addNode("codiPostalNorm").addConstraintViolation();
		}
		return valid;
	}

}
