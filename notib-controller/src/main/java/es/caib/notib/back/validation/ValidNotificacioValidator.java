package es.caib.notib.back.validation;


import com.google.common.base.Strings;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.logic.intf.dto.notificacio.TipusEnviamentEnumDto;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.logic.intf.service.ProcedimentService;
import es.caib.notib.back.command.NotificacioCommand;
import es.caib.notib.back.command.PersonaCommand;
import es.caib.notib.back.helper.EmailValidHelper;
import es.caib.notib.back.helper.MessageHelper;
import es.caib.notib.back.helper.SessioHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
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
	@Autowired
	private OrganGestorService organService;
	@Autowired
	private EntitatService entitatService;

	private String maxSizeError;
	private Locale locale;
	private boolean comunicacioAmbAdministracio;
	private boolean comunicacioSenseAdministracio;
	private NotificacioCommand notificacio;
	private ConstraintValidatorContext context;


	@Override
	public void initialize(final ValidNotificacio constraintAnnotation) {
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isValid(final NotificacioCommand notificacio, final ConstraintValidatorContext context) {

		var valid = true;
		locale = new Locale(SessioHelper.getIdioma(aplicacioService));
		context.disableDefaultConstraintViolation();
		this.notificacio = notificacio;
		this.context = context;
		try {
			// Validació del Concepte
			if (notificacio.getConcepte() != null && !notificacio.getConcepte().isEmpty()) {
				if (!validFormat(notificacio.getConcepte()).isEmpty()) {
					valid = false;
					var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.concepte",
								new Object[] {listToString(validFormat(notificacio.getConcepte()))}, locale);
					context.buildConstraintViolationWithTemplate(msg).addNode("concepte").addConstraintViolation();
			    }
			}
			
			// Validació de la Descripció
			if (notificacio.getDescripcio() != null && !notificacio.getDescripcio().isEmpty()) {
				if (!validFormat(notificacio.getDescripcio()).isEmpty()) {
					valid = false;
					var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.descripcio",
								new Object[] {listToString(validFormat(notificacio.getDescripcio()))}, locale);
					context.buildConstraintViolationWithTemplate(msg).addNode("descripcio").addConstraintViolation();
			    }
			}
						
			//Validar si és comunicació
			valid = valid && validarComunicacio();
			
			// Procediment
			valid = valid && validarProcediment();
			
			// Validació caducitat
			if (notificacio.getEnviamentTipus() == TipusEnviamentEnumDto.NOTIFICACIO) {
				if (notificacio.getCaducitat() == null) {
					var msg = MessageHelper.getInstance().getMessage("NotNull", null, locale);
					context.buildConstraintViolationWithTemplate(msg).addNode("caducitat").addConstraintViolation();
				} else if (!notificacio.getCaducitat().after(new Date())) {
					valid = false;
					var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.caducitat", null, locale);
					context.buildConstraintViolationWithTemplate(msg).addNode("caducitat").addConstraintViolation();
				}
			}
			
			// Validació de documents
			valid = valid && validarDocuments();

			// ENVIAMENTS
			valid = valid && validarEnviaments();

		} catch (final Exception ex) {
//        	LOGGER.error("Una comunicació no pot estar dirigida a una administració i a una persona física/jurídica a la vegada.", ex);
			log.error("S'ha produït un error inesperat al validar la notificació. "
					+ "Si l'error es continua donant en properes intents, posis en contacte amb els administradors de l'aplicació.", ex);
        	valid = false;
        }
		if (!valid) {
			var msg = TipusEnviamentEnumDto.NOTIFICACIO.equals(notificacio.getEnviamentTipus())
					? "notificacio.form.errors.validacio.notificacio" : "notificacio.form.errors.validacio.comunicacio";
			msg = MessageHelper.getInstance().getMessage(msg, null, locale) + (!Strings.isNullOrEmpty(maxSizeError) ? " - " + maxSizeError : "");
			context.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
		}
		return valid;
	}

	private boolean validarComunicacio() {

		var valid = true;
		// TODO: Aquesta validació no té molt de sentit ara que hem dividit els formularis
		if (notificacio.getEnviamentTipus() == TipusEnviamentEnumDto.COMUNICACIO || notificacio.getEnviamentTipus() == TipusEnviamentEnumDto.COMUNICACIO_SIR) {
			if (notificacio.getEnviaments() != null) {
				for (var enviament : notificacio.getEnviaments()) {
					if (enviament.getTitular().getInteressatTipus() == InteressatTipus.ADMINISTRACIO) {
						comunicacioAmbAdministracio = true;
					}
					if ((enviament.getTitular().getInteressatTipus() == InteressatTipus.FISICA) || (enviament.getTitular().getInteressatTipus() == InteressatTipus.JURIDICA)) {
						comunicacioSenseAdministracio = true;
					}
				}
			}
		}

		if (TipusEnviamentEnumDto.COMUNICACIO_SIR.equals(notificacio.getEnviamentTipus())) {
			var organ = notificacio.getOrganGestor();
			var o = organService.findByCodi(null, organ);
			var entitat = entitatService.findById(o.getEntitatId());
			valid = entitat.isOficinaEntitat() || o.getOficina() != null && !Strings.isNullOrEmpty(o.getOficina().getCodi());
			if (!valid) {
				var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.organ.sense.oficina", null, locale);
				context.buildConstraintViolationWithTemplate(msg).addNode("organGestor").addConstraintViolation();
			}
		}
		if (comunicacioAmbAdministracio && comunicacioSenseAdministracio) {
			valid = false;
			context.disableDefaultConstraintViolation();
			var msg = MessageHelper.getInstance().getMessage("notificacio.form.comunicacio", null, locale);
			context.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
		}
		return valid;
	}

	private boolean validarProcediment() {

		var valid = false;
		var useProcediment = "PROCEDIMENT".equals(notificacio.getTipusProcSer());
		var procSer = useProcediment ? notificacio.getProcedimentId() : notificacio.getServeiId();
		if (notificacio.getEnviamentTipus() == TipusEnviamentEnumDto.NOTIFICACIO) {
			if (procSer == null) {
				valid = false;
				if (useProcediment) {
					var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.procediment", null, locale);
					context.buildConstraintViolationWithTemplate(msg).addNode("procedimentId").addConstraintViolation();
				} else {
					var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.servei", null, locale);
					context.buildConstraintViolationWithTemplate(msg).addNode("serveiId").addConstraintViolation();
				}
			}
		}
		if (procSer == null) {
			return valid;
		}
		var procedimentActiu = procedimentService.procedimentActiu(procSer);
		if (!procedimentActiu) {
			valid = false;
			if (useProcediment) {
				var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.procediment.inactiu", null, locale);
				context.buildConstraintViolationWithTemplate(msg).addNode("procedimentId").addConstraintViolation();
			} else {
				var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.servei.inactiu", null, locale);
				context.buildConstraintViolationWithTemplate(msg).addNode("serveiId").addConstraintViolation();
			}
		}
		var procedimentAmbGrups = procedimentService.procedimentAmbGrups(procSer);
		if (procedimentAmbGrups && notificacio.getGrupId() == null) {
			valid = false;
			var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.servei.inactiu", null, locale);
			context.buildConstraintViolationWithTemplate(msg).addNode("grupId").addConstraintViolation();
		}
		return valid;
	}

	private boolean validarDocuments() {

		var valid = true;

		Long fileMaxSize = 10485760L; //10MB
		Long fileTotalMaxSize = 15728640L; // 15MB
		List<String> formatsDisponibles = Arrays.asList(new String[] {"application/pdf", "application/zip", "application/x-zip-compressed"});
		List<String> extensionsDisponibles = Arrays.asList(new String[] {"jpg", "jpeg", "odt", "odp", "ods", "odg", "docx", "xlsx", "pptx", "pdf", "png", "rtf", "svg", "tiff", "txt", "xml", "xsig"});
		if (aplicacioService.propertyGet("es.caib.notib.notificacio.document.size") != null) {
			fileMaxSize = Long.valueOf(aplicacioService.propertyGet("es.caib.notib.notificacio.document.size"));
		}
		Long fileTotalSize = 0L;
		for (var i = 0; i < 5; i++) {
			if(notificacio.getTipusDocument()[i] == null) {
				continue;
			}
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
						var formatValid = true;
						if (comunicacioAmbAdministracio) {
							log.info("NOTIFICACIO-VAL: > Extensió: '{}'", extensio);
							if (!extensionsDisponibles.contains(extensio.toLowerCase())) {
								log.info("NOTIFICACIO-VAL: > Extensió no vàlida!");
								formatValid = false;
								valid = false;
								var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.document.format", null, locale);
								context.buildConstraintViolationWithTemplate(msg).addNode("arxiu[" + i + "]").addConstraintViolation();
							}
						} else {
							log.info("NOTIFICACIO-VAL: > ContentType: '{}'", contentType);
							if (!formatsDisponibles.contains(contentType)) {
								log.info("NOTIFICACIO-VAL: > ContentType no vàlid!!");
								formatValid = false;
								valid = false;
								var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.document.format", null, locale);
								context.buildConstraintViolationWithTemplate(msg).addNode("arxiu[" + i + "]").addConstraintViolation();
							}
						}
						if (formatValid) {
							log.info("NOTIFICACIO-VAL: > Format de document vàlid");
						}
						fileTotalSize += fileSize;
						if (fileSize > fileMaxSize) {
							valid = false;
							var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.document.size", null, locale);
							context.buildConstraintViolationWithTemplate(msg).addNode("arxiu[" + i + "]").addConstraintViolation();
						}
					}
					break;
				case URL:
					if (i == 0 && (notificacio.getDocumentArxiuUrl()[i] == null || notificacio.getDocumentArxiuUrl()[i].trim().isEmpty())) {
						valid = false;
						var msg = MessageHelper.getInstance().getMessage("NotEmpty", null, locale);
						context.buildConstraintViolationWithTemplate(msg).addNode("documentArxiuUrl[" + i + "]").addConstraintViolation();
					}
					break;
				case CSV:
					if (i == 0 && (notificacio.getDocumentArxiuCsv()[i] == null || notificacio.getDocumentArxiuCsv()[i].trim().isEmpty())) {
						valid = false;
						var msg = MessageHelper.getInstance().getMessage("NotEmpty", null, locale);
						context.buildConstraintViolationWithTemplate(msg).addNode("documentArxiuCsv[" + i + "]").addConstraintViolation();
					}
					break;
				case UUID:
					if (i == 0 && (notificacio.getDocumentArxiuUuid()[i] == null || notificacio.getDocumentArxiuUuid()[i].trim().isEmpty())) {
						valid = false;
						var msg = MessageHelper.getInstance().getMessage("NotEmpty", null, locale);
						context.buildConstraintViolationWithTemplate(msg).addNode("documentArxiuUuid[" + i + "]").addConstraintViolation();
					}
					break;
			}
		}

		if (fileTotalSize > fileTotalMaxSize) {
			valid = false;
			maxSizeError = MessageHelper.getInstance().getMessage("notificacio.form.valid.document.total.size", null, locale);
			context.buildConstraintViolationWithTemplate(maxSizeError);
		}
		return valid;
	}

	private boolean validarEnviaments() {

		if (notificacio.getEnviaments() == null) {
			return true;
		}
		var valid = true;
		int envCount = 0;
		List<String> nifs = new ArrayList<>();
		for (var enviament: notificacio.getEnviaments()) {

//					if (TipusEnviamentEnumDto.NOTIFICACIO.equals(notificacio.getEnviamentTipus()) && InteressatTipusEnumDto.ADMINISTRACIO.equals(enviament.getTitular().getInteressatTipus())) {
//						valid = false;
//						String msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.interessat.tipus", new Object[] {envCount + 1}, locale);
//						context.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
//					}

			// Incapacitat -> Destinataris no null
			if (enviament.getTitular() != null && enviament.getTitular().isIncapacitat()) {
				if (enviament.getDestinataris() == null || enviament.getDestinataris().isEmpty()) {
					valid = false;
					var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.titular.incapacitat", new Object[] {envCount + 1}, locale);
					context.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
					context.buildConstraintViolationWithTemplate(msg).addNode("enviaments["+envCount+"].titular.incapacitat").addConstraintViolation();
				}
			}
			if (!notificacio.isComunicacioSIR()) {
				var senseNif = true;
				if (!InteressatTipus.FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus()) && senseNif) {
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

				if (!InteressatTipus.FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus()) && senseNif) {
					valid = false;
					var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.notificacio.sensenif", new Object[]{envCount + 1}, locale);
					context.buildConstraintViolationWithTemplate(msg).addNode("enviaments[" + envCount + "].titular.nif").addConstraintViolation();
				}

				// SI ES UNA PERSONA SENSE NIF I NO TÉ CAP DESTINATARI NI ENVIAMENT PER ENTREGA POSTAL ACTIVA -> EMAIL OBLIGATORI
				if (InteressatTipus.FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus())) {
					if(senseNif && (enviament.getEntregaPostal() == null || !enviament.getEntregaPostal().isActiva()) && Strings.isNullOrEmpty(enviament.getTitular().getEmail())) {
						// Email obligatori si no té destinataris amb nif o enviament postal
						valid = false;
						var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.fisica.sense.nif.email", null, locale);
						context.buildConstraintViolationWithTemplate(msg).addNode("enviaments[" + envCount + "].titular.email").addConstraintViolation();
					}
				}

				if (!Strings.isNullOrEmpty(enviament.getTitular().getNif()) && !InteressatTipus.FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus())) {
					var nif = enviament.getTitular().getNif().toLowerCase();
					if (nifs.contains(nif)) {
						valid = false;
						var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.nif.repetit");
						context.buildConstraintViolationWithTemplate(msg).addNode("enviaments[" + envCount + "].titular.nif").addConstraintViolation();
					} else {
						nifs.add(nif);
					}
				}
			}

			if (enviament.getEntregaDeh() != null && enviament.getEntregaDeh().isActiva()) {
				if (InteressatTipus.FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus())) {
					valid = false;
					var msg = MessageHelper.getInstance().getMessage("entregadeh.form.valid.persona.sense.nif", null, locale);
					context.buildConstraintViolationWithTemplate(msg).addNode("enviaments["+envCount+"].entregaDeh.activa").addConstraintViolation();
				}
				if (enviament.getTitular() == null || enviament.getTitular().getNif() == null || enviament.getTitular().getNif().isEmpty()) {
					valid = false;
					var msg = MessageHelper.getInstance().getMessage("entregadeh.form.valid.sensenif", null, locale);
					context.buildConstraintViolationWithTemplate(msg).addNode("enviaments["+envCount+"].titular.nif").addConstraintViolation();
				}
			}

			if (enviament.getTitular() != null && enviament.getTitular().getEmail() != null && !enviament.getTitular().getEmail().isEmpty() && !EmailValidHelper.isEmailValid(enviament.getTitular().getEmail())) {
				valid = false;
				var msg = MessageHelper.getInstance().getMessage("entregadeh.form.valid.valid.email", null, locale);
				context.buildConstraintViolationWithTemplate(msg).addNode("enviaments["+envCount+"].titular.email").addConstraintViolation();
			}
			if (enviament.getDestinataris() != null) {
				var destCount = 0;
				for (var destinatari: enviament.getDestinataris()) {
					if (!Strings.isNullOrEmpty(destinatari.getEmail()) && !EmailValidHelper.isEmailValid(destinatari.getEmail())) {
						valid = false;
						var msg = MessageHelper.getInstance().getMessage("entregadeh.form.valid.valid.email", null, locale);
						context.buildConstraintViolationWithTemplate(msg).addNode("enviaments["+envCount+"].destinataris[" + destCount +"].email").addConstraintViolation();
					}
					var nif = destinatari.getNif();
					if (!Strings.isNullOrEmpty(nif)) {
						nif = nif.toLowerCase();
						if (nifs.contains(nif)) {
							valid = false;
							var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.nif.repetit", null, locale);
							context.buildConstraintViolationWithTemplate(msg).addNode("enviaments["+envCount+"].destinataris[" + destCount +"].nif").addConstraintViolation();
						} else {
							nifs.add(nif);
						}
					}
					destCount++;
				}
			}
			envCount++;
		}
		return valid;
	}
	
	private ArrayList<Character> validFormat(String value) {

		var CONTROL_CARACTERS = " aàáäbcçdeèéëfghiìíïjklmnñoòóöpqrstuùúüvwxyzAÀÁÄBCÇDEÈÉËFGHIÌÍÏJKLMNÑOÒÓÖPQRSTUÙÚÜVWXYZ0123456789-_'\"/:().,¿?!¡;·";
		ArrayList<Character> charsNoValids = new ArrayList<>();
		var chars = value.replace("\n", "").replace("\r", "").toCharArray();
		var esCaracterValid = true;
		for (var i = 0; i < chars.length; i++) {
			esCaracterValid = !(CONTROL_CARACTERS.indexOf(chars[i]) < 0);
			if (!esCaracterValid) {
				charsNoValids.add(chars[i]);
			}
	    }
		return charsNoValids;
	}

	private StringBuilder listToString(ArrayList<?> list) {

	    var str = new StringBuilder();
	    for (var i = 0; i < list.size(); i++) {
	    	str.append(list.get(i));
	    }
	    return str;
	}

}
