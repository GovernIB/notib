package es.caib.notib.back.validation;


import com.google.common.base.Strings;
import es.caib.notib.back.command.NotificacioCommand;
import es.caib.notib.back.command.PersonaCommand;
import es.caib.notib.back.config.scopedata.SessionScopedContext;
import es.caib.notib.back.helper.EmailValidHelper;
import es.caib.notib.back.helper.MessageHelper;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.ConfigService;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.logic.intf.service.ProcedimentService;
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
	@Autowired
	private SessionScopedContext sessionScopedContext;
	@Autowired
	private ConfigService configService;

	private static boolean apostrofPermes;

	@Override
	public void initialize(final ValidNotificacio constraintAnnotation) {
		//init
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isValid(final NotificacioCommand notificacio, final ConstraintValidatorContext context) {

		boolean valid = true;
		boolean comunicacioAmbAdministracio = false;
		boolean comunicacioSenseAdministracio = false;
		Locale locale = new Locale(sessionScopedContext.getIdiomaUsuari());
		context.disableDefaultConstraintViolation();
		String maxSizeError = "";
		apostrofPermes = Boolean.parseBoolean(configService.getPropertyValue("es.caib.notib.notifica.apostrof.permes"));
		try {

			// Validació del Concepte
			if (notificacio.getConcepte() != null && !notificacio.getConcepte().isEmpty() && (!validFormat(notificacio.getConcepte()).isEmpty())) {
				valid = false;
				var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.concepte", new Object[] {listToString(validFormat(notificacio.getConcepte()))}, locale);
				context.buildConstraintViolationWithTemplate(msg).addNode("concepte").addConstraintViolation();

			}

			// Validació de la Descripció
			if (notificacio.getDescripcio() != null && !notificacio.getDescripcio().isEmpty() && (!validFormat(notificacio.getDescripcio()).isEmpty())) {
				valid = false;
				var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.descripcio", new Object[] {listToString(validFormat(notificacio.getDescripcio()))}, locale);
				context.buildConstraintViolationWithTemplate(msg).addNode("descripcio").addConstraintViolation();
			}

			//Validar si és comunicació
			// TODO: Aquesta validació no té molt de sentit ara que hem dividit els formularis
			if (notificacio.getEnviamentTipus() == EnviamentTipus.COMUNICACIO || notificacio.getEnviamentTipus() == EnviamentTipus.SIR && (notificacio.getEnviaments() != null)) {
				for (var enviament : notificacio.getEnviaments()) {
					if (enviament.getTitular().getInteressatTipus() == InteressatTipus.ADMINISTRACIO) {
						comunicacioAmbAdministracio = true;
					}
					if ((enviament.getTitular().getInteressatTipus() == InteressatTipus.FISICA) || (enviament.getTitular().getInteressatTipus() == InteressatTipus.JURIDICA)) {
						comunicacioSenseAdministracio = true;
					}
				}
			}

			Long organId;
            try {
                organId = Long.parseLong(notificacio.getOrganGestor());
            } catch (Exception ex) {
				var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.organ.buit", null, locale);
				context.buildConstraintViolationWithTemplate(msg).addNode("organGestor").addConstraintViolation();//				var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.organ.buit", null, locale);
				context.buildConstraintViolationWithTemplate(msg).addNode("organGestor").addConstraintViolation();
                return false;
            }
			var entitat = entitatService.findByDir3codi(notificacio.getEmisorDir3Codi());
			var organ = organService.findById(entitat.getId(), organId);
			if (EnviamentTipus.SIR.equals(notificacio.getEnviamentTipus())) {
				if (organ != null) {
					valid = entitat.isOficinaEntitat() || organ.getOficina() != null && !Strings.isNullOrEmpty(organ.getOficina().getCodi());
					if (!valid) {
						var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.organ.sense.oficina", null, locale);
						context.buildConstraintViolationWithTemplate(msg).addNode("organGestor").addConstraintViolation();
					}
				}
			}
			if (comunicacioAmbAdministracio && comunicacioSenseAdministracio) {
				valid = false;
				context.disableDefaultConstraintViolation();
				var msg = MessageHelper.getInstance().getMessage("notificacio.form.comunicacio", null, locale);
				context.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
			}

			// Procediment
			boolean useProcediment = "PROCEDIMENT".equals(notificacio.getTipusProcSer());
			Long procSer = useProcediment ? notificacio.getProcedimentId() : notificacio.getServeiId();

			if (notificacio.getEnviamentTipus() == EnviamentTipus.NOTIFICACIO && (procSer == null)) {
				valid = false;
				if (useProcediment) {
					var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.procediment", null, locale);
					context.buildConstraintViolationWithTemplate(msg).addNode("procedimentId").addConstraintViolation();
				} else {
					var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.servei", null, locale);
					context.buildConstraintViolationWithTemplate(msg).addNode("serveiId").addConstraintViolation();
				}
			}
			ProcSerDto procediment = null;
			if (procSer != null) {

				procediment = procedimentService.findById(null, false, procSer);
				if (!procediment.isActiu()) {
					valid = false;
					if (useProcediment) {
						var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.procediment.inactiu", null, locale);
						context.buildConstraintViolationWithTemplate(msg).addNode("procedimentId").addConstraintViolation();
					} else {
						var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.servei.inactiu", null, locale);
						context.buildConstraintViolationWithTemplate(msg).addNode("serveiId").addConstraintViolation();
					}
				}

				if (procediment.isAgrupar() && notificacio.getGrupCodi() == null) {
					valid = false;
					var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.grup", null, locale);
					context.buildConstraintViolationWithTemplate(msg).addNode("grupCodi").addConstraintViolation();
				}
			}

			// Validació caducitat
			if (notificacio.getEnviamentTipus() == EnviamentTipus.NOTIFICACIO) {
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
			var fileMaxSize = 10485760L; //10MB
			var fileTotalMaxSize = 15728640L; // 15MB
			List<String> formatsDisponibles = Arrays.asList("application/pdf", "application/zip", "application/x-zip-compressed");
			List<String> extensionsDisponibles = Arrays.asList("jpg", "jpeg", "odt", "odp", "ods", "odg", "docx", "xlsx", "pptx", "pdf", "png", "rtf", "svg", "tiff", "txt", "xml", "xsig");
			var docSize = aplicacioService.propertyGet("es.caib.notib.notificacio.document.size");
			if (docSize != null) {
				fileMaxSize = Long.parseLong(docSize);
			}
			var maxSize = aplicacioService.propertyGet("es.caib.notib.notificacio.document.total.size");
			if (maxSize != null) {
				fileTotalMaxSize = Long.parseLong(maxSize);
			}
			var fileTotalSize = 0L;
			for (int i = 0; i < 5; i++) {
				if(notificacio.getTipusDocument()[i] == null) {
					continue;
				}
				switch (notificacio.getTipusDocument()[i]) {
					case ARXIU:
						if (i == 0 && ((notificacio.getContingutArxiu(i) == null || notificacio.getContingutArxiu(i).length == 0 || notificacio.getDocuments()[i].getArxiuGestdocId() == null)
								&& (notificacio.getDocuments()[i].getArxiuGestdocId() == null || notificacio.getDocuments()[i].getArxiuGestdocId().isEmpty()))) {
							valid = false;
							var msg = MessageHelper.getInstance().getMessage("NotEmpty", null, locale);
							context.buildConstraintViolationWithTemplate(msg).addNode("arxiu[" + i + "]").addConstraintViolation();
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

			if (!notificacio.isComunicacioSIR() && fileTotalSize > fileTotalMaxSize) {
				valid = false;
				maxSizeError = MessageHelper.getInstance().getMessage("notificacio.form.valid.document.total.size", null, locale);
				context.buildConstraintViolationWithTemplate(maxSizeError);
			}

			// ENVIAMENTS
			if (notificacio.getEnviaments() != null) {
				int envCount = 0;
				List<String> nifs = new ArrayList<>();
				var cieActiu = false;
				for (var enviament : notificacio.getEnviaments()) {

					cieActiu = cieActiu || enviament.getEntregaPostal().isActiva();

					if (cieActiu && organ.isEntregaCieDesactivada()) {
						var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.entregapostal.desactivada", null, locale);
						context.buildConstraintViolationWithTemplate(msg).addNode("enviaments["+envCount+"].entregaPostal.activa").addConstraintViolation();
					}

					// Incapacitat -> Destinataris no null
					if (enviament.getTitular() != null && enviament.getTitular().isIncapacitat() && (enviament.getDestinataris() == null || enviament.getDestinataris().isEmpty())) {
						valid = false;
						var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.titular.incapacitat", new Object[] {envCount + 1}, locale);
						context.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
						msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.titular.incapacitat", new Object[] {envCount + 1}, locale);
						context.buildConstraintViolationWithTemplate(msg).addNode("enviaments["+envCount+"].titular.incapacitat").addConstraintViolation();

					}
					if (!notificacio.isComunicacioSIR()) {
						boolean senseNif = true;
						if (!InteressatTipus.FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus()) && senseNif
								&& (enviament.getTitular() != null && enviament.getTitular().getNif() != null && !enviament.getTitular().getNif().isEmpty())) {
								senseNif = false;

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
							context.buildConstraintViolationWithTemplate(
											MessageHelper.getInstance().getMessage("notificacio.form.valid.notificacio.sensenif", new Object[]{envCount + 1}, locale))
									.addNode("enviaments[" + envCount + "].titular.nif")
									.addConstraintViolation();
						}

						// SI ES UNA PERSONA SENSE NIF I NO TÉ CAP DESTINATARI NI ENVIAMENT PER ENTREGA POSTAL ACTIVA -> EMAIL OBLIGATORI
						if (InteressatTipus.FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus()) &&
								(senseNif && (enviament.getEntregaPostal() == null || !enviament.getEntregaPostal().isActiva())
										&& Strings.isNullOrEmpty(enviament.getTitular().getEmail()))) {

								// Email obligatori si no té destinataris amb nif o enviament postal
								valid = false;
								var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.fisica.sense.nif.email", null, locale);
								context.buildConstraintViolationWithTemplate(msg).addNode("enviaments[" + envCount + "].titular.email").addConstraintViolation();
						}

						if (!Strings.isNullOrEmpty(enviament.getTitular().getNif()) && !InteressatTipus.FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus())) {
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
						int destCount = 0;
						for (var destinatari: enviament.getDestinataris()) {
							if (!Strings.isNullOrEmpty(destinatari.getEmail()) && !EmailValidHelper.isEmailValid(destinatari.getEmail())) {
								valid = false;
								var msg = MessageHelper.getInstance().getMessage("entregadeh.form.valid.valid.email", null, locale);
								var node = "enviaments["+envCount+"].destinataris[" + destCount +"].email";
								context.buildConstraintViolationWithTemplate(msg).addNode(node).addConstraintViolation();
							}
							String nif = destinatari.getNif();
							if (!Strings.isNullOrEmpty(nif)) {
								nif = nif.toLowerCase();
								if (nifs.contains(nif)) {
									valid = false;
									var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.nif.repetit", null, locale);
									var node = "enviaments["+envCount+"].destinataris[" + destCount +"].nif";
									context.buildConstraintViolationWithTemplate(msg).addNode(node).addConstraintViolation();
								} else {
									nifs.add(nif);
								}
							}
							destCount++;
						}
					}
					envCount++;
				}
				if (notificacio.getRetard() > 0 && !cieActiu) {
					var msg = MessageHelper.getInstance().getMessage("notificacio.form.valid.retard.no.cie", null, locale);
					context.buildConstraintViolationWithTemplate(msg).addNode("retard").addConstraintViolation();
				}
			}
		} catch (final Exception ex) {
			log.error("S'ha produït un error inesperat al validar la notificació. Si l'error es continua donant en properes intents, posis en contacte amb els administradors de l'aplicació.", ex);
			valid = false;
		}
		return valid;
	}

	public static ArrayList<Character> validFormat(String value) {

		String controlCaracters = " aàáäbcçdeèéëfghiìíïjklmnñoòóöpqrstuùúüvwxyzAÀÁÄBCÇDEÈÉËFGHIÌÍÏJKLMNÑOÒÓÖPQRSTUÙÚÜVWXYZ0123456789-–_/:().,¿?!¡;ºª";
		controlCaracters += apostrofPermes ? "'" : "";
		ArrayList<Character> charsNoValids = new ArrayList<>();
		char[] chars = value.replace("\n", "").replace("\r", "").toCharArray();
		boolean esCaracterValid = true;
		for (char aChar : chars) {
			esCaracterValid = controlCaracters.indexOf(aChar) >= 0;
			if (!esCaracterValid && !charsNoValids.contains(aChar)) {
				charsNoValids.add(aChar);
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

}
