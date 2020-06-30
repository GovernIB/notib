package es.caib.notib.war.validation;



import java.util.ArrayList;
import java.util.Arrays;

import javax.mail.internet.InternetAddress;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import es.caib.notib.core.api.dto.InteressatTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.war.command.EnviamentCommand;
import es.caib.notib.war.command.NotificacioCommandV2;
import es.caib.notib.war.command.PersonaCommand;
import es.caib.notib.war.helper.MessageHelper;

/**
 * Constraint de validació que controla que camp email és obligatori si està habilitada l'entrega a la Direcció Electrònica Hablitada (DEH)
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidNotificacioValidator implements ConstraintValidator<ValidNotificacio, NotificacioCommandV2> {

	@Autowired
	private AplicacioService aplicacioService;
	
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
				if (!validFormat(notificacio.getConcepte()).isEmpty()) {
					valid = false;
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("notificacio.form.valid.concepte", new Object[] {listToString(validFormat(notificacio.getConcepte()))}))
					.addNode("concepte")
					.addConstraintViolation();
			    }
			}
			
			// Validació de la Descripció
			if (notificacio.getDescripcio() != null && !notificacio.getDescripcio().isEmpty()) {
				if (!validFormat(notificacio.getDescripcio()).isEmpty()) {
					valid = false;
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("notificacio.form.valid.descripcio", new Object[] {listToString(validFormat(notificacio.getDescripcio()))}))
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
				Long fileMaxSize = 10635049L; //10MB
				String [] formatsDisponibles = {"application/pdf", "application/zip", "application/x-zip-compressed"};
				if (aplicacioService.propertyGet("es.caib.notib.notificacio.document.size") != null)
					fileMaxSize = Long.valueOf(aplicacioService.propertyGet("es.caib.notib.notificacio.document.size"));
				
				if (notificacio.getContingutArxiu() == null || notificacio.getContingutArxiu().length == 0) {
					valid = false;
					context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty"))
					.addNode("arxiu")
					.addConstraintViolation();
				}
				if ((notificacio.getContingutArxiu() != null && notificacio.getContingutArxiu().length != 0) && !Arrays.asList(formatsDisponibles).contains(notificacio.getArxiu().getContentType())) {
					valid = false;
					context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("notificacio.form.valid.document.format"))
					.addNode("arxiu")
					.addConstraintViolation();
				}
				if ((notificacio.getContingutArxiu() != null && notificacio.getContingutArxiu().length != 0) && notificacio.getArxiu().getSize() > fileMaxSize) {
					valid = false;
					context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("notificacio.form.valid.document.size"))
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
		
			// ENVIAMENTS
			if (notificacio.getEnviaments() != null) {
				int envCount = 0;
				for (EnviamentCommand enviament: notificacio.getEnviaments()) {
					
					// Incapacitat -> Destinataris no null
					if (enviament.getTitular() != null && enviament.getTitular().isIncapacitat()) {
						if (enviament.getDestinataris() == null || enviament.getDestinataris().isEmpty()) {
							valid = false;
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage("notificacio.form.valid.titular.incapacitat", new Object[] {envCount + 1}))
							.addConstraintViolation();
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage("notificacio.form.valid.titular.incapacitat", new Object[] {envCount + 1}))
									.addNode("enviaments["+envCount+"].titular.incapacitat")
							.addConstraintViolation();
						}
					}
					if (NotificaEnviamentTipusEnumDto.NOTIFICACIO.equals(notificacio.getEnviamentTipus())) {
						boolean senseNif = true;
						if (enviament.getTitular() != null && enviament.getTitular().getNif() != null && !enviament.getTitular().getNif().isEmpty()) {
							senseNif = false;
						}
						if (senseNif && enviament.getDestinataris() != null) {
							for (PersonaCommand destinatari: enviament.getDestinataris()) {
								if (destinatari.getNif() != null && !destinatari.getNif().isEmpty()) {
									senseNif = false;
								}
							}
						}
						if (senseNif) {
							valid = false;
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage("notificacio.form.valid.notificacio.sensenif", new Object[] {envCount + 1}))
							.addConstraintViolation();
						}
					}
					
//					if (enviament.isEntregaPostalActiva()) {
//						
//					}
					if (enviament.getEntregaDeh() != null && enviament.getEntregaDeh().isActiva()) {
						if (enviament.getTitular() == null || enviament.getTitular().getNif() == null || enviament.getTitular().getNif().isEmpty()) {
							valid = false;
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage("entregadeh.form.valid.sensenif"))
							.addNode("enviaments["+envCount+"].titular.nif")
							.addConstraintViolation();
//							context.buildConstraintViolationWithTemplate(
//									MessageHelper.getInstance().getMessage("entregadeh.form.valid.sensenif"))
//							.addNode("enviaments["+envCount+"].entregaDeh.emisorNif")
//							.addConstraintViolation();
						}
					}
					
					if (enviament.getTitular() != null && enviament.getTitular().getEmail() != null && !enviament.getTitular().getEmail().isEmpty() && !isEmailValid(enviament.getTitular().getEmail())) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage("entregadeh.form.valid.valid.email"))
						.addNode("enviaments["+envCount+"].titular.email")
						.addConstraintViolation();
					}
					if (enviament.getDestinataris() != null) {
						int destCount = 0;
						for (PersonaCommand destinatari: enviament.getDestinataris()) {
							if (destinatari.getEmail() != null && !destinatari.getEmail().isEmpty() && !isEmailValid(destinatari.getEmail())) {
								valid = false;
								context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage("entregadeh.form.valid.valid.email"))
								.addNode("enviaments["+envCount+"].destinataris[" + destCount +"].email")
								.addConstraintViolation();
							}
							destCount++;
						}
					}
					envCount++;
				}
			}
		} catch (final Exception ex) {
//        	LOGGER.error("Una comunicació no pot estar dirigida a una administració i a una persona física/jurídica a la vegada.", ex);
			LOGGER.error("S'ha produït un error inesperat al validar la notificació. "
					+ "Si l'error es continua donant en properes intents, posis en contacte amb els administradors de l'aplicació.", ex);
        	valid = false;
        }
		
		
		return valid;
	}
	
	private ArrayList<Character> validFormat(String value) {
		String CONTROL_CARACTERS = " aàáäbcçdeèéëfghiìíïjklmnñoòóöpqrstuùúüvwxyzAÀÁÄBCÇDEÈÉËFGHIÌÍÏJKLMNÑOÒÓÖPQRSTUÙÚÜVWXYZ0123456789-_'\"/:().,¿?!¡;·";
		ArrayList<Character> charsNoValids = new ArrayList<Character>();
		char[] chars = value.replace("\n", "").replace("\r", "").toCharArray();
		
		boolean esCaracterValid = true;
		for (int i = 0; i < chars.length; i++) {
			esCaracterValid = !(CONTROL_CARACTERS.indexOf(chars[i]) < 0);
			if (!esCaracterValid) {
				charsNoValids.add(chars[i]);
			}
	    }
		return charsNoValids;
	}

	private StringBuilder listToString(ArrayList<?> list) {
	    StringBuilder str = new StringBuilder();
	    for (int i = 0; i < list.size(); i++) {
	    	str.append(list.get(i));
	    }
	    return str;
	}
	
	private boolean isEmailValid(String email) {
		boolean valid = true;
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (Exception e) {
			valid = false; //no vàlid
		}
		return valid;
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ValidDocumentValidator.class);

}
