package es.caib.notib.war.validation;


import es.caib.notib.core.api.dto.InteressatTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.notificacio.TipusEnviamentEnumDto;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.war.command.EnviamentCommand;
import es.caib.notib.war.command.NotificacioCommand;
import es.caib.notib.war.command.PersonaCommand;
import es.caib.notib.war.helper.MessageHelper;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.internet.InternetAddress;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Constraint de validació que controla que camp email és obligatori si està habilitada l'entrega a la Direcció Electrònica Hablitada (DEH)
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidNotificacioValidator implements ConstraintValidator<ValidNotificacio, NotificacioCommand> {

	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private ProcedimentService procedimentService;
	
	@Override
	public void initialize(final ValidNotificacio constraintAnnotation) {
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isValid(final NotificacioCommand notificacio, final ConstraintValidatorContext context) {
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
			// TODO: Aquesta validació no té molt de sentit ara que hem dividit els formularis
			if (notificacio.getEnviamentTipus() == TipusEnviamentEnumDto.COMUNICACIO ||
					notificacio.getEnviamentTipus() == TipusEnviamentEnumDto.COMUNICACIO_SIR) {
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
			
			// Procediment
			if (notificacio.getEnviamentTipus() == TipusEnviamentEnumDto.NOTIFICACIO) {
				if (notificacio.getProcedimentId() == null) {
					valid = false;
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("notificacio.form.valid.procediment"))
					.addNode("procedimentId")
					.addConstraintViolation();
				}
			}
			if (notificacio.getProcedimentId() != null) {
				boolean procedimentAmbGrups = procedimentService.procedimentAmbGrups(notificacio.getProcedimentId());
				if (procedimentAmbGrups && notificacio.getGrupId() == null) {
					valid = false;
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("notificacio.form.valid.grup"))
					.addNode("grupId")
					.addConstraintViolation();
				}
			}
			
			// Validació caducitat
			if (notificacio.getEnviamentTipus() == TipusEnviamentEnumDto.NOTIFICACIO) {
				if (notificacio.getCaducitat() == null) {
					context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotNull"))
							.addNode("caducitat")
							.addConstraintViolation();
				}
				else if (!notificacio.getCaducitat().after(new Date())) {
					valid = false;
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("notificacio.form.valid.caducitat"))
					.addNode("caducitat")
					.addConstraintViolation();
				}
			}
			
			// Validació de documents
			Long fileMaxSize = 10485760L; //10MB
			Long fileTotalMaxSize = 15728640L; // 15MB
			List<String> formatsDisponibles = Arrays.asList(new String[] {"application/pdf", "application/zip", "application/x-zip-compressed"});
			List<String> extensionsDisponibles = Arrays.asList(new String[] {"jpg", "jpeg", "odt", "odp", "ods", "odg", "docx", "xlsx", "pptx", "pdf", "png", "rtf", "svg", "tiff", "txt", "xml", "xsig", "csig", "html", "csv"});;
			if (aplicacioService.propertyGet("es.caib.notib.notificacio.document.size") != null)
				fileMaxSize = Long.valueOf(aplicacioService.propertyGet("es.caib.notib.notificacio.document.size"));
			Long fileTotalSize = 0L;

			for (int i = 0; i < 5; i++) {
				if(notificacio.getTipusDocument()[i] != null) {
					switch (notificacio.getTipusDocument()[i]) {
						case ARXIU:
							if (i == 0 && ((notificacio.getContingutArxiu(i) == null || notificacio.getContingutArxiu(i).length == 0 || notificacio.getDocuments()[i].getArxiuGestdocId() == null)
									&& (notificacio.getDocuments()[i].getArxiuGestdocId() == null || notificacio.getDocuments()[i].getArxiuGestdocId().isEmpty()))) {
								valid = false;
								context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty"))
										.addNode("arxiu[" + i + "]")
										.addConstraintViolation();
							}
//							if ((notificacio.getContingutArxiu(i) != null && notificacio.getContingutArxiu(i).length != 0)) {
								if (comunicacioAmbAdministracio) {
									String extensio = FilenameUtils.getExtension(notificacio.getArxiu()[i].getOriginalFilename());
									if (!extensionsDisponibles.contains(extensio)) {
										valid = false;
										context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("notificacio.form.valid.document.format"))
												.addNode("arxiu[" + i + "]")
												.addConstraintViolation();
									}
								} else {
									if (!formatsDisponibles.contains(notificacio.getArxiu()[i].getContentType())) {
										valid = false;
										context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("notificacio.form.valid.document.format"))
												.addNode("arxiu[" + i + "]")
												.addConstraintViolation();
									}
								}
//							}
							Long fileSize = notificacio.getArxiu()[i].getSize();
							fileTotalSize += fileSize;
							if ((notificacio.getContingutArxiu(i) != null && notificacio.getContingutArxiu(i).length != 0) && fileSize > fileMaxSize) {
								valid = false;
								context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("notificacio.form.valid.document.size"))
										.addNode("arxiu[" + i + "]")
										.addConstraintViolation();
							}
							break;
						case URL:
							if (i == 0 && (notificacio.getDocumentArxiuUrl()[i] == null || notificacio.getDocumentArxiuUrl()[i].isEmpty())) {
								valid = false;
								context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty"))
										.addNode("documentArxiuUrl[" + i + "]")
										.addConstraintViolation();
							}
							break;
						case CSV:
							if (i == 0 && (notificacio.getDocumentArxiuCsv()[i] == null || notificacio.getDocumentArxiuCsv()[i].isEmpty())) {
								valid = false;
								context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty"))
										.addNode("documentArxiuCsv[" + i + "]")
										.addConstraintViolation();
							}
							break;
						case UUID:
							if (i == 0 && (notificacio.getDocumentArxiuUuid()[i] == null || notificacio.getDocumentArxiuUuid()[i].isEmpty())) {
								valid = false;
								context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty"))
										.addNode("documentArxiuUuid[" + i + "]")
										.addConstraintViolation();
							}
							break;
					}
				}
			}
			if (fileTotalSize > fileTotalMaxSize) {
				valid = false;
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("notificacio.form.valid.document.total.size"));
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
							.addNode("enviaments["+envCount+"].titular.nif")
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
