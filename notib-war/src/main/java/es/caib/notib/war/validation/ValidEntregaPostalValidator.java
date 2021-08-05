package es.caib.notib.war.validation;



import es.caib.notib.war.command.EntregapostalCommand;
import es.caib.notib.war.helper.MessageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Constraint de validació que controla que camp email és obligatori si està habilitada l'entrega a la Direcció Electrònica Hablitada (DEH)
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidEntregaPostalValidator implements ConstraintValidator<ValidEntregaPostal, EntregapostalCommand> {

	@Override
	public void initialize(final ValidEntregaPostal constraintAnnotation) {
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isValid(final EntregapostalCommand entregaPostal, final ConstraintValidatorContext context) {
		boolean valid = true;
		
		if (entregaPostal.isActiva()) {
			try {
				// Validacions per tipus de entrega postal
				switch (entregaPostal.getDomiciliConcretTipus()) {
				case NACIONAL:
					if (entregaPostal.getViaTipus() == null) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage("entregapostal.form.valid.nacional.notempty"))
						.addNode("tipusVia")
						.addConstraintViolation();
					}
					if (entregaPostal.getViaNom() == null || entregaPostal.getViaNom().isEmpty()) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage("entregapostal.form.valid.nacional.notempty"))
						.addNode("viaNom")
						.addConstraintViolation();
					}
					if ((entregaPostal.getPuntKm() == null || entregaPostal.getPuntKm().isEmpty()) 
							&& (entregaPostal.getNumeroCasa() == null || entregaPostal.getNumeroCasa().isEmpty())) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage("entregapostal.form.valid.nacional.puntkm.numcasa"))
						.addNode("numeroCasa")
						.addConstraintViolation();
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage("entregapostal.form.valid.nacional.puntkm.numcasa"))
						.addNode("puntKm")
						.addConstraintViolation();
					}
					if (entregaPostal.getMunicipiCodi() == null || entregaPostal.getMunicipiCodi().isEmpty()) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage("entregapostal.form.valid.nacional.notempty"))
						.addNode("municipiCodi")
						.addConstraintViolation();
					}
					if (entregaPostal.getProvincia() == null || entregaPostal.getProvincia().isEmpty()) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage("entregapostal.form.valid.nacional.notempty"))
						.addNode("provincia")
						.addConstraintViolation();
					}
					if (entregaPostal.getPoblacio() == null || entregaPostal.getPoblacio().isEmpty()) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage("entregapostal.form.valid.nacional.notempty"))
						.addNode("poblacio")
						.addConstraintViolation();
					}
					if (entregaPostal.getCodiPostal() == null || entregaPostal.getCodiPostal().isEmpty()) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage("entregapostal.form.valid.codi.postal.buit"))
						.addNode("codiPostal")
						.addConstraintViolation();
					}
					break;
				case ESTRANGER:
					if (entregaPostal.getViaNom() == null || entregaPostal.getViaNom().isEmpty()) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage("entregapostal.form.valid.estranger.notempty"))
						.addNode("viaNom")
						.addConstraintViolation();
					}
					if (entregaPostal.getPaisCodi() == null || entregaPostal.getPaisCodi().isEmpty()) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage("entregapostal.form.valid.estranger.notempty"))
						.addNode("getPaisCodi")
						.addConstraintViolation();
					}
					if (entregaPostal.getPoblacio() == null || entregaPostal.getPoblacio().isEmpty()) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage("entregapostal.form.valid.estranger.notempty"))
						.addNode("poblacio")
						.addConstraintViolation();
					}
					if (entregaPostal.getCodiPostal() == null || entregaPostal.getCodiPostal().isEmpty()) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage("entregapostal.form.valid.codi.postal.estranger.buit"))
						.addNode("codiPostal")
						.addConstraintViolation();
					}
					break;
				case APARTAT_CORREUS:
					if (entregaPostal.getApartatCorreus() == null || entregaPostal.getApartatCorreus().isEmpty()) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage("entregapostal.form.valid.apcorreu.notempty"))
						.addNode("apartatCorreus")
						.addConstraintViolation();
					}
					if (entregaPostal.getMunicipiCodi() == null || entregaPostal.getMunicipiCodi().isEmpty()) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage("entregapostal.form.valid.apcorreu.notempty"))
						.addNode("municipiCodi")
						.addConstraintViolation();
					}
					if (entregaPostal.getProvincia() == null || entregaPostal.getProvincia().isEmpty()) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage("entregapostal.form.valid.apcorreu.notempty"))
						.addNode("provincia")
						.addConstraintViolation();
					}
					if (entregaPostal.getPoblacio() == null || entregaPostal.getPoblacio().isEmpty()) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage("entregapostal.form.valid.apcorreu.notempty"))
						.addNode("poblacio")
						.addConstraintViolation();
					}
					if (entregaPostal.getCodiPostal() == null || entregaPostal.getCodiPostal().isEmpty()) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage("entregapostal.form.valid.codi.postal.buit"))
						.addNode("codiPostal")
						.addConstraintViolation();
					}
					break;
				case SENSE_NORMALITZAR:
					if (entregaPostal.getLinea1() == null || entregaPostal.getLinea1().isEmpty()) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage("entregapostal.form.valid.no.normalitzat.notempty"))
						.addNode("linea1")
						.addConstraintViolation();
					}
					if (entregaPostal.getLinea2() == null || entregaPostal.getLinea2().isEmpty()) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage("entregapostal.form.valid.no.normalitzat.notempty"))
						.addNode("linea2")
						.addConstraintViolation();
					}
					if (entregaPostal.getCodiPostalNorm() == null || entregaPostal.getCodiPostalNorm().isEmpty()) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage("entregapostal.form.valid.codi.postal.buit"))
						.addNode("codiPostalNorm")
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
		}
		
		return valid;
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ValidEntregaPostalValidator.class);

}
