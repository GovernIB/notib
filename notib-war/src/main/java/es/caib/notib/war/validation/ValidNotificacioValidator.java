package es.caib.notib.war.validation;



import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.notib.core.api.dto.InteressatTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.war.command.EnviamentCommand;
import es.caib.notib.war.command.NotificacioCommandV2;
import es.caib.notib.war.helper.MessageHelper;

/**
 * Constraint de validació que controla que camp email és obligatori si està habilitada l'entrega a la Direcció Electrònica Hablitada (DEH)
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidNotificacioValidator implements ConstraintValidator<ValidNotificacio, NotificacioCommandV2> {

	@Override
	public void initialize(final ValidNotificacio constraintAnnotation) {
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isValid(final NotificacioCommandV2 notificacio, final ConstraintValidatorContext context) {
		boolean valid = true;
		boolean comunicacioAmbAdministracio = false;
		boolean comunicacioSenseAdministracio = false;
		
		try {
			
			// Validació del Concepte
			if (notificacio.getConcepte() != null && !notificacio.getConcepte().isEmpty()) {
				if (!validFormat(notificacio.getConcepte())) {
					valid = false;
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("notificacio.form.valid.concepte"))
					.addNode("concepte")
					.addConstraintViolation();
			    }
			}
			
			// Validació de la Descripció
			if (notificacio.getDescripcio() != null && !notificacio.getDescripcio().isEmpty()) {
				if (!validFormat(notificacio.getDescripcio())) {
					valid = false;
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("notificacio.form.valid.concepte"))
					.addNode("descripcio")
					.addConstraintViolation();
			    }
			}
						
			//Validar si és comunicació
			if (notificacio.getEnviamentTipus() == NotificaEnviamentTipusEnumDto.COMUNICACIO) {
				if (notificacio.getEnviaments() != null) {
					for (EnviamentCommand enviament : notificacio.getEnviaments()) {
						if (enviament.getTitular().getInteressatTipus() == InteressatTipusEnumDto.ADMINISTRACIO) {
							comunicacioAmbAdministracio = true;
						}
						if ((enviament.getTitular().getInteressatTipus() == InteressatTipusEnumDto.FISICA) || (enviament.getTitular().getInteressatTipus() == InteressatTipusEnumDto.JURIDICA)) {
							comunicacioSenseAdministracio = true;
						}
					}
				}
			}
			if (comunicacioAmbAdministracio && comunicacioSenseAdministracio) {
				valid = false;
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("notificacio.form.comunicacio")).addConstraintViolation();
			} 
			
			// Validació de document
			switch (notificacio.getTipusDocument()) {
			case ARXIU:
				if (notificacio.getContingutArxiu() == null || notificacio.getContingutArxiu().length == 0) {
					valid = false;
					context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty"))
					.addNode("arxiu")
					.addConstraintViolation();
				}
				break;
			case URL:
				if (notificacio.getDocumentArxiuUrl() == null || notificacio.getDocumentArxiuUrl().isEmpty()) {
					valid = false;
					context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty"))
					.addNode("documentArxiuUrl")
					.addConstraintViolation();
				}
				break;
			case CSV:
				if (notificacio.getDocumentArxiuCsv() == null || notificacio.getDocumentArxiuCsv().isEmpty()) {
					valid = false;
					context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty"))
					.addNode("documentArxiuCsv")
					.addConstraintViolation();
				}
				break;
			case UUID:
				if (notificacio.getDocumentArxiuUuid() == null || notificacio.getDocumentArxiuUuid().isEmpty()) {
					valid = false;
					context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty"))
					.addNode("documentArxiuUuid")
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
	
	private boolean validFormat(String value) {
		String CONTROL_CARACTERS = " aàáäbcçdeèéëfghiìíïjklmnñoòóöpqrstuùúüvwxyzAÀÁÄBCÇDEÈÉËFGHIÌÍÏJKLMNÑOÒÓÖPQRSTUÙÚÜVWXYZ0123456789-_'\"/:().,¿?!¡;";
		char[] chars = value.toCharArray();
		
		boolean esCaracterValid = true;
		for (int i = 0; esCaracterValid && i < chars.length; i++) {
			esCaracterValid = !(CONTROL_CARACTERS.indexOf(chars[i]) < 0);
			if (!esCaracterValid) {
				break;
			}
	    }
		return esCaracterValid;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ValidDocumentValidator.class);

}
