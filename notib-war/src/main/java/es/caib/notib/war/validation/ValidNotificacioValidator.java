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

	@Override
	public boolean isValid(final NotificacioCommandV2 cmd, final ConstraintValidatorContext context) {
		boolean valid = true;
		boolean comunicacioAmbAdministracio = false;
		boolean comunicacioSenseAdministracio = false;
		
		try {
			//Validar si és comunicació
			if (cmd.getEnviaments() != null) {
				for (EnviamentCommand enviament : cmd.getEnviaments()) {
					if (cmd.getEnviamentTipus() == NotificaEnviamentTipusEnumDto.COMUNICACIO) {
						if (enviament.getTitular().getInteressatTipus() == InteressatTipusEnumDto.ADMINISTRACIO) {
							comunicacioAmbAdministracio = true;
						}
						if ((enviament.getTitular().getInteressatTipus() == InteressatTipusEnumDto.FISICA) || (enviament.getTitular().getInteressatTipus() == InteressatTipusEnumDto.FISICA)) {
							comunicacioSenseAdministracio = true;
						}
					}
				}
			}
				
			if (comunicacioAmbAdministracio && comunicacioSenseAdministracio) {
				valid = false;
			} 

		} catch (final Exception ex) {
        	LOGGER.error("Una comunicació no pot estar dirigida a una administració i a una persona física/jurídica a la vegada.", ex);
        	valid = false;
        }
		
		if (!valid)
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage("notificacio.form.comunicacio")).addConstraintViolation();
		
		return valid;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ValidDocumentValidator.class);

}
