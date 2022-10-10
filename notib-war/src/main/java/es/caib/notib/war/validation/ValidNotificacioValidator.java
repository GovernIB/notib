package es.caib.notib.war.validation;


import com.google.common.base.Strings;
import es.caib.notib.client.domini.InteressatTipusEnumDto;
import es.caib.notib.core.api.dto.notificacio.TipusEnviamentEnumDto;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.war.command.EnviamentCommand;
import es.caib.notib.war.command.NotificacioCommand;
import es.caib.notib.war.command.PersonaCommand;
import es.caib.notib.war.helper.EmailValidHelper;
import es.caib.notib.war.helper.MessageHelper;
import es.caib.notib.war.helper.SessioHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
/**
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
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
		Locale locale = new Locale(SessioHelper.getIdioma(aplicacioService));
		context.disableDefaultConstraintViolation();
		String maxSizeError = "";
		try {

			// Validació del Concepte
			if (notificacio.getConcepte() != null && !notificacio.getConcepte().isEmpty()) {
				if (!validFormat(notificacio.getConcepte()).isEmpty()) {
					valid = false;
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("notificacio.form.valid.concepte", new Object[] {listToString(validFormat(notificacio.getConcepte()))}, locale))
					.addNode("concepte")
					.addConstraintViolation();
			    }
			}
			
			// Validació de la Descripció
			if (notificacio.getDescripcio() != null && !notificacio.getDescripcio().isEmpty()) {
				if (!validFormat(notificacio.getDescripcio()).isEmpty()) {
					valid = false;
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("notificacio.form.valid.descripcio", new Object[] {listToString(validFormat(notificacio.getDescripcio()))}, locale))
					.addNode("descripcio")
					.addConstraintViolation();
			    }
			}
						
			//Validar si és comunicació
			// TODO: Aquesta validació no té molt de sentit ara que hem dividit els formularis
			if (notificacio.getEnviamentTipus() == TipusEnviamentEnumDto.COMUNICACIO || notificacio.getEnviamentTipus() == TipusEnviamentEnumDto.COMUNICACIO_SIR) {
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
						MessageHelper.getInstance().getMessage("notificacio.form.comunicacio", null, locale)).addConstraintViolation();
			}
			
			// Procediment
			boolean useProcediment = "PROCEDIMENT".equals(notificacio.getTipusProcSer());
			Long procSer = useProcediment ? notificacio.getProcedimentId() : notificacio.getServeiId();

			if (notificacio.getEnviamentTipus() == TipusEnviamentEnumDto.NOTIFICACIO) {
				if (procSer == null) {
					valid = false;
					if (useProcediment) {
						context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage("notificacio.form.valid.procediment", null, locale))
								.addNode("procedimentId")
								.addConstraintViolation();
					} else {
						context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage("notificacio.form.valid.servei", null, locale))
								.addNode("serveiId")
								.addConstraintViolation();
					}
				}
			}
			if (procSer != null) {
				boolean procedimentActiu = procedimentService.procedimentActiu(procSer);
				if (!procedimentActiu) {
					valid = false;
					if (useProcediment) {
						context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage("notificacio.form.valid.procediment.inactiu", null, locale))
								.addNode("procedimentId")
								.addConstraintViolation();
					} else {
						context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage("notificacio.form.valid.servei.inactiu", null, locale))
								.addNode("serveiId")
								.addConstraintViolation();
					}
				}

				boolean procedimentAmbGrups = procedimentService.procedimentAmbGrups(procSer);
				if (procedimentAmbGrups && notificacio.getGrupId() == null) {
					valid = false;
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("notificacio.form.valid.grup", null, locale))
					.addNode("grupId")
					.addConstraintViolation();
				}
			}
			
			// Validació caducitat
			if (notificacio.getEnviamentTipus() == TipusEnviamentEnumDto.NOTIFICACIO) {
				if (notificacio.getCaducitat() == null) {
					context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotNull", null, locale))
							.addNode("caducitat")
							.addConstraintViolation();
				}
				else if (!notificacio.getCaducitat().after(new Date())) {
					valid = false;
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("notificacio.form.valid.caducitat", null, locale))
					.addNode("caducitat")
					.addConstraintViolation();
				}
			}
			
			// Validació de documents
			Long fileMaxSize = 10485760L; //10MB
			Long fileTotalMaxSize = 15728640L; // 15MB
			List<String> formatsDisponibles = Arrays.asList(new String[] {"application/pdf", "application/zip", "application/x-zip-compressed"});
			List<String> extensionsDisponibles = Arrays.asList(new String[] {"jpg", "jpeg", "odt", "odp", "ods", "odg", "docx", "xlsx", "pptx", "pdf", "png", "rtf", "svg", "tiff", "txt", "xml", "xsig"});
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
								context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty", null, locale))
										.addNode("arxiu[" + i + "]")
										.addConstraintViolation();
							}
							if ((notificacio.getContingutArxiu(i) != null && notificacio.getContingutArxiu(i).length != 0) ||
									(notificacio.getDocuments()[i].getArxiuGestdocId() != null && !notificacio.getDocuments()[i].getArxiuGestdocId().trim().isEmpty())) {
								String extensio;
								String contentType;
								Long fileSize;
								if (notificacio.getDocuments()[i].getArxiuGestdocId() != null && !notificacio.getDocuments()[i].getArxiuGestdocId().trim().isEmpty()) {
									extensio = FilenameUtils.getExtension(notificacio.getDocuments()[i].getArxiuNom());
									contentType = notificacio.getDocuments()[i].getMediaType();
									fileSize = notificacio.getDocuments()[i].getMida();
								} else {
									extensio = FilenameUtils.getExtension(notificacio.getArxiu()[i].getOriginalFilename());
									contentType = notificacio.getArxiu()[i].getContentType();
									fileSize = notificacio.getArxiu()[i].getSize();
								}
								log.info("NOTIFICACIO-VAL: Validant format de document a notificar");
								boolean formatValid = true;
								if (comunicacioAmbAdministracio) {
									log.info("NOTIFICACIO-VAL: > Extensió: '{}'", extensio);
									if (!extensionsDisponibles.contains(extensio.toLowerCase())) {
										log.info("NOTIFICACIO-VAL: > Extensió no vàlida!");
										formatValid = false;
										valid = false;
										context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("notificacio.form.valid.document.format", null, locale))
												.addNode("arxiu[" + i + "]")
												.addConstraintViolation();
									}
								} else {
									log.info("NOTIFICACIO-VAL: > ContentType: '{}'", contentType);
									if (!formatsDisponibles.contains(contentType)) {
										log.info("NOTIFICACIO-VAL: > ContentType no vàlid!!");
										formatValid = false;
										valid = false;
										context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("notificacio.form.valid.document.format", null, locale))
												.addNode("arxiu[" + i + "]")
												.addConstraintViolation();
									}
								}
								if (formatValid)
									log.info("NOTIFICACIO-VAL: > Format de document vàlid");

								fileTotalSize += fileSize;
								if (fileSize > fileMaxSize) {
									valid = false;
									context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("notificacio.form.valid.document.size", null, locale))
											.addNode("arxiu[" + i + "]")
											.addConstraintViolation();
								}
							}
							break;
						case URL:
							if (i == 0 && (notificacio.getDocumentArxiuUrl()[i] == null || notificacio.getDocumentArxiuUrl()[i].trim().isEmpty())) {
								valid = false;
								context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty", null, locale))
										.addNode("documentArxiuUrl[" + i + "]")
										.addConstraintViolation();
							}
							break;
						case CSV:
							if (i == 0 && (notificacio.getDocumentArxiuCsv()[i] == null || notificacio.getDocumentArxiuCsv()[i].trim().isEmpty())) {
								valid = false;
								context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty", null, locale))
										.addNode("documentArxiuCsv[" + i + "]")
										.addConstraintViolation();
							}
							break;
						case UUID:
							if (i == 0 && (notificacio.getDocumentArxiuUuid()[i] == null || notificacio.getDocumentArxiuUuid()[i].trim().isEmpty())) {
								valid = false;
								context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty", null, locale))
										.addNode("documentArxiuUuid[" + i + "]")
										.addConstraintViolation();
							}
							break;
					}
				}
			}

			if (fileTotalSize > fileTotalMaxSize) {
				valid = false;
				maxSizeError = MessageHelper.getInstance().getMessage("notificacio.form.valid.document.total.size", null, locale);
				context.buildConstraintViolationWithTemplate(maxSizeError);
			}

			// ENVIAMENTS
			if (notificacio.getEnviaments() != null) {
				int envCount = 0;
				List<String> nifs = new ArrayList<>();
				for (EnviamentCommand enviament: notificacio.getEnviaments()) {

//					if (TipusEnviamentEnumDto.NOTIFICACIO.equals(notificacio.getEnviamentTipus()) && InteressatTipusEnumDto.ADMINISTRACIO.equals(enviament.getTitular().getInteressatTipus())) {
//						valid = false;
//						String msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.interessat.tipus", new Object[] {envCount + 1}, locale);
//						context.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
//					}

					// Incapacitat -> Destinataris no null
					if (enviament.getTitular() != null && enviament.getTitular().isIncapacitat()) {
						if (enviament.getDestinataris() == null || enviament.getDestinataris().isEmpty()) {
							valid = false;
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage("notificacio.form.valid.titular.incapacitat", new Object[] {envCount + 1}, locale))
							.addConstraintViolation();
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage("notificacio.form.valid.titular.incapacitat", new Object[] {envCount + 1}, locale))
									.addNode("enviaments["+envCount+"].titular.incapacitat")
							.addConstraintViolation();
						}
					}
					if (!notificacio.isComunicacioSIR()) {
						boolean senseNif = true;
						if (!InteressatTipusEnumDto.FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus()) && senseNif) {
							if (enviament.getTitular() != null && enviament.getTitular().getNif() != null && !enviament.getTitular().getNif().isEmpty()) {
								senseNif = false;
							}
						}
						if (senseNif && enviament.getDestinataris() != null) {
							for (PersonaCommand destinatari: enviament.getDestinataris()) {
								if (destinatari.getNif() != null && !destinatari.getNif().isEmpty()) {
									senseNif = false;
								}
							}
						}

						if (!InteressatTipusEnumDto.FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus()) && senseNif) {
							valid = false;
							context.buildConstraintViolationWithTemplate(
											MessageHelper.getInstance().getMessage("notificacio.form.valid.notificacio.sensenif", new Object[]{envCount + 1}, locale))
									.addNode("enviaments[" + envCount + "].titular.nif")
									.addConstraintViolation();
						}

						// SI ES UNA PERSONA SENSE NIF I NO TÉ CAP DESTINATARI NI ENVIAMENT PER ENTREGA POSTAL ACTIVA -> EMAIL OBLIGATORI
						if (InteressatTipusEnumDto.FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus())) {
							if(senseNif && (enviament.getEntregaPostal() == null || !enviament.getEntregaPostal().isActiva()) && Strings.isNullOrEmpty(enviament.getTitular().getEmail())) {
								// Email obligatori si no té destinataris amb nif o enviament postal
								valid = false;
								context.buildConstraintViolationWithTemplate(
												MessageHelper.getInstance().getMessage("notificacio.form.valid.fisica.sense.nif.email", null, locale))
										.addNode("enviaments[" + envCount + "].titular.email")
										.addConstraintViolation();
							}
						}

						if (!Strings.isNullOrEmpty(enviament.getTitular().getNif()) && !InteressatTipusEnumDto.FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus())) {
							String nif = enviament.getTitular().getNif().toLowerCase();
							if (nifs.contains(nif)) {
								valid = false;
								String msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.nif.repetit");
								context.buildConstraintViolationWithTemplate(msg).addNode("enviaments[" + envCount + "].titular.nif").addConstraintViolation();
							} else {
								nifs.add(nif);
							}
						}
					}

					if (enviament.getEntregaDeh() != null && enviament.getEntregaDeh().isActiva()) {
						if (InteressatTipusEnumDto.FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus())) {
							valid = false;
							context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("entregadeh.form.valid.persona.sense.nif", null, locale))
									.addNode("enviaments["+envCount+"].entregaDeh.activa").addConstraintViolation();
						}
						if (enviament.getTitular() == null || enviament.getTitular().getNif() == null || enviament.getTitular().getNif().isEmpty()) {
							valid = false;
							context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("entregadeh.form.valid.sensenif", null, locale))
							.addNode("enviaments["+envCount+"].titular.nif").addConstraintViolation();
						}
					}
					
					if (enviament.getTitular() != null && enviament.getTitular().getEmail() != null && !enviament.getTitular().getEmail().isEmpty() && !EmailValidHelper.isEmailValid(enviament.getTitular().getEmail())) {
						valid = false;
						context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("entregadeh.form.valid.valid.email", null, locale))
						.addNode("enviaments["+envCount+"].titular.email").addConstraintViolation();
					}
					if (enviament.getDestinataris() != null) {
						int destCount = 0;
						for (PersonaCommand destinatari: enviament.getDestinataris()) {
							if (!Strings.isNullOrEmpty(destinatari.getEmail()) && !EmailValidHelper.isEmailValid(destinatari.getEmail())) {
								valid = false;
								context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage("entregadeh.form.valid.valid.email", null, locale))
								.addNode("enviaments["+envCount+"].destinataris[" + destCount +"].email")
								.addConstraintViolation();
							}
							String nif = destinatari.getNif();
							if (!Strings.isNullOrEmpty(nif)) {
								nif = nif.toLowerCase();
								if (nifs.contains(nif)) {
									valid = false;
									context.buildConstraintViolationWithTemplate(
													MessageHelper.getInstance().getMessage("notificacio.form.valid.nif.repetit", null, locale))
											.addNode("enviaments["+envCount+"].destinataris[" + destCount +"].nif")
											.addConstraintViolation();
								} else {
									nifs.add(nif);
								}
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
		if (!valid) {
			String msg = TipusEnviamentEnumDto.NOTIFICACIO.equals(notificacio.getEnviamentTipus())
					? "notificacio.form.errors.validacio.notificacio" : "notificacio.form.errors.validacio.comunicacio";
			msg = MessageHelper.getInstance().getMessage(msg, null, locale) + (!Strings.isNullOrEmpty(maxSizeError) ? " - " + maxSizeError : "");
			context.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
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

	private static final Logger LOGGER = LoggerFactory.getLogger(ValidDocumentValidator.class);

}
