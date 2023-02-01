package es.caib.notib.logic.helper;

import es.caib.notib.client.domini.Idioma;
import es.caib.notib.logic.intf.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.exception.ValidationException;
import es.caib.notib.logic.intf.service.AuditService;
import es.caib.notib.logic.aspect.Audita;
import es.caib.notib.logic.aspect.UpdateNotificacioTable;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.logic.helper.EmailHelper.Attachment;
import es.caib.notib.persist.repository.NotificacioRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Mètodes per a l'enviament de correus.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class EmailNotificacioSenseNifHelper {

	@Autowired
	NotificacioRepository notificacioRepository;
	@Autowired
	PluginHelper pluginHelper;
	@Autowired
	AuditNotificacioHelper auditNotificacioHelper;
	@Autowired
	AuditEnviamentHelper auditEnviamentHelper;
	@Autowired
	NotificacioEventHelper notificacioEventHelper;
	@Autowired
	private DocumentHelper documentHelper;
	@Autowired
	protected ConfigHelper configHelper;
	@Resource
	protected JavaMailSender mailSender;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;


	@UpdateNotificacioTable
	@Audita(entityType = AuditService.TipusEntitat.NOTIFICACIO, operationType = AuditService.TipusOperacio.UPDATE)
	public NotificacioEntity notificacioEnviarEmail(List<NotificacioEnviamentEntity> enviamentsSenseNif, boolean totsEmail) {

		var notificacio = notificacioRepository.findById(enviamentsSenseNif.get(0).getNotificacio().getId()).orElseThrow();
		log.info(" [NOT] Inici enviament notificació [Id: " + notificacio.getId() + ", Estat: " + notificacio.getEstat() + "]");
		if (totsEmail) {
			notificacio.updateNotificaNouEnviament(pluginHelper.getNotificaReintentsPeriodeProperty());
			notificacio.updateNotificaEnviamentData();
		}

		if (totsEmail && !NotificacioEstatEnumDto.REGISTRADA.equals(notificacio.getEstat()) && !NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(notificacio.getEstat())) {
			log.error(" [NOT] la notificació no té l'estat REGISTRADA o ENVIADA_AMB_ERRORS.");
			var msg = "La notificació no te l'estat " + NotificacioEstatEnumDto.REGISTRADA + " o " + NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS;
			throw new ValidationException(notificacio.getId(), NotificacioEntity.class, msg);
		}

		var hasErrors = false;
		String error;
		for (var enviament : enviamentsSenseNif) {
			error = sendEmailInfoEnviamentSenseNif(enviament);
			notificacioEventHelper.addNotificacioEmailEvent(notificacio, enviament,error != null, error);
			hasErrors = hasErrors || error != null;
		}

		// Event Notificació x envaiment per email
		if (hasErrors) {
			notificacioEventHelper.addEnviamentEmailErrorEvent(notificacio);
			auditNotificacioHelper.updateNotificacioEnviadaAmbErrors(notificacio);
		} else {
			notificacioEventHelper.addEnviamentEmailOKEvent(notificacio);
		}

		// Estat de la notificació
		var notificacioMixta = notificacio.hasEnviamentsNotifica();
		var totsElsEnviamentsEnviats = !notificacio.hasEnviamentsNoEnviats();
		if (totsElsEnviamentsEnviats) {
			if (notificacioMixta) {
				auditNotificacioHelper.updateNotificacioEnviada(notificacio);
			} else {
				auditNotificacioHelper.updateNotificacioEnviadaEmail(notificacio);
			}
			return notificacio;
		}
		if (notificacio.hasEnviamentsEnviats()) {
			var fiReintents = notificacio.getNotificaEnviamentIntent() >= pluginHelper.getNotificaReintentsMaxProperty();
			// Si tots els enviaments son via email, o tots els enviats a notifica s'han finalitzat --> la marcam com a Finalitzada amb errors
			if (fiReintents && (!notificacioMixta || notificacio.allEnviamentsNotificaFinalitzats())) {
				auditNotificacioHelper.updateNotificacioFinalitzadaAmbErrors(notificacio);
			} else {
				auditNotificacioHelper.updateNotificacioEnviadaAmbErrors(notificacio);
			}
		}
		return notificacio;
	}

	public String sendEmailInfoEnviamentSenseNif(NotificacioEnviamentEntity enviament) {

		String resposta;
		try {
			var email = enviament.getTitular().getEmail();
			if (email == null || email.isEmpty()) {
				var errorDescripció = "L'interessat no té correu electrònic per a enviar-li la comunicació.";
				log.error(errorDescripció);
				return errorDescripció;
			}
			resposta = NotificaEnviamentTipusEnumDto.NOTIFICACIO.equals(enviament.getNotificacio().getEnviamentTipus()) ?
						sendEmailInfoEnviamentNotificacioSenseNif(enviament) : sendEmailInfoEnviamentComunicacioSenseNif(enviament);

		} catch (Exception ex) {
			String errorDescripció = "No s'ha pogut avisar per correu electrònic: " + ex;
			log.error(errorDescripció);
			resposta = errorDescripció;
		}
		return resposta;
	}

	public String sendEmailInfoEnviamentComunicacioSenseNif(NotificacioEnviamentEntity enviament) throws Exception {

		var entitat = enviament.getNotificacio().getEntitat();
		var email = enviament.getTitular().getEmail();
		log.info("Enviant correu enviament comunicació (Id= {}) a {}", enviament.getId(), email);

		if (configHelper.getEntitatActualCodi() == null) {
			configHelper.setEntitatCodi(entitat.getCodi());
		}
		var document = enviament.getNotificacio().getDocument();
		var arxiu = documentHelper.documentToArxiuDto(document.getArxiuNom(), document);
		configHelper.setEntitatCodi(null);
		List<Attachment> attachments = new ArrayList<>(Arrays.asList(new Attachment(arxiu.getNom(), arxiu.getContingut())));
		var htmlBody = getComunicacioMailHtmlBody(enviament);
		var textBody = getComunicacioMailPlainTextBody(enviament);
		var subject = "Nova comunicació / Nueva comunicación";
		sendEmail(email, subject, htmlBody, textBody, entitat.getNom(), attachments, entitat.getLogoCapBytes(), entitat.getLogoPeuBytes());
		return null;
	}

	public String sendEmailInfoEnviamentNotificacioSenseNif(NotificacioEnviamentEntity enviament) throws Exception {

		var entitat = enviament.getNotificacio().getEntitat();
		var email = enviament.getTitular().getEmail();
		log.info("Enviant correu enviament notificació (Id= {}) a {}", enviament.getId(), email);
		var htmlBody = getNotificacioMailHtmlBody(enviament);
		var textBody = getNotificacioMailPlainTextBody(enviament);
		var subject = "Avís de nova notificació / Aviso de nueva notificación";
		sendEmail(enviament.getTitular().getEmail(), subject, htmlBody, textBody, entitat.getNom(), null, entitat.getLogoCapBytes(), entitat.getLogoPeuBytes());
		return null;
	}

	private void sendEmail(String emailDestinatari, String subject, String htmlBody, String textBody, String entitatNom, List<Attachment> attatchments,
						   byte[] logoEntitat, byte[] logoPeu) throws MessagingException, IOException {

		var missatge = mailSender.createMimeMessage();
//		MimeMessage missatge = mailSender.createMimeMessage();
		missatge.setHeader("Content-Type", "text/html charset=UTF-8");
		var helper = new MimeMessageHelper(missatge, true, StandardCharsets.UTF_8.name());
		helper.setTo(emailDestinatari);
		helper.setFrom(getRemitent());
		helper.setSubject(configHelper.getPrefix() + " " + subject);
		// Contingut del missatge
		var teLogoPeu = (logoPeu != null && logoPeu.length > 0) || getPeuLogo() != null;
		helper.setText(textBody, getHeader() + htmlBody + getFooter(entitatNom, teLogoPeu));
		// Documents adjunts
		if (attatchments != null) {
			for (var attachment: attatchments) {
				helper.addAttachment(attachment.filename, new ByteArrayResource(attachment.content));
			}
		}

		// LOGOS
		// Logo entitat
		if (logoEntitat != null && logoEntitat.length > 0) {
			helper.addInline("logo-entitat", new ByteArrayResource(logoEntitat), getImageByteArrayMimeType(logoEntitat));
		} else {
			var pathLogoEntitat = getCapsaleraLogoEntitat();
			if (pathLogoEntitat == null) {
				logoEntitat = IOUtils.toByteArray(getCapsaleraDefaultLogoEntitat());
				helper.addInline("logo-entitat", new ByteArrayResource(logoEntitat), getImageByteArrayMimeType(logoEntitat));
			} else {
				helper.addInline("logo-entitat", new File(pathLogoEntitat));
			}
		}
		// Logo Notib
		var logoNotib = IOUtils.toByteArray(getCapsaleraLogoNotib());
		helper.addInline("logo-notib", new ByteArrayResource(logoNotib), getImageByteArrayMimeType(logoNotib));
		// Logo peu
		if (logoPeu != null && logoPeu.length > 0) {
			helper.addInline("logo-peu", new ByteArrayResource(logoPeu), getImageByteArrayMimeType(logoPeu));
		} else {
			var peuLogo = getPeuLogo();
			if (peuLogo != null) {
				helper.addInline("logo-peu", new File(getPeuLogo()));
			}
		}
//		if ("test@limit.es".equals(emailDestinatari))
		mailSender.send(missatge);
	}

	private String getImageByteArrayMimeType(byte[] bytes) throws IOException {

		var byteArrayInputStream = new ByteArrayInputStream(bytes);
		var mimeType = URLConnection.guessContentTypeFromStream(byteArrayInputStream);
		if (mimeType == null) {
			mimeType = "image/x-png";
		}
		byteArrayInputStream.close();
		return mimeType;
	}

	private String getNotificacioMailHtmlBody(NotificacioEnviamentEntity enviament) {

		var entitat = enviament.getNotificacio().getEntitat();
		var organ = enviament.getNotificacio().getOrganGestor();
		var idioma = enviament.getNotificacio().getIdioma();
		var htmlText = "<div class=\"content\">" +
				"<br/>" +
				(Idioma.ES.equals(idioma) ?
					"<h2>Aviso de nueva notificación</h2>" +
							"<p>Le informamos que en breve recibirá una nueva notificación como INTERESADO, procedente del organismo <b>" + organ.getNom() + "</b> (" + entitat.getNom() + ")' con los siguientes datos:</p>" +
							getInformacioEnviamentHtml(enviament, false) +
							"<p>Usted recibirá esta notificación por vía postal en los próximos dias.</p>"
					:
					"<h2>Avís de nova notificació</h2>" +
							"<p>L'informam que en breu rebrà una nova notificació com a INTERESSAT, procedent de l'organisme <b>" + organ.getNom() + "</b> (" + entitat.getNom() + ")' amb les següents dades:</p>" +
							getInformacioEnviamentHtml(enviament, true) +
							"<p>Vosté rebrà aquesta notificació per via postal en els propers dies.</p>"
					)
				+ "</div>";
		return htmlText;
	}
	private String getNotificacioMailPlainTextBody(NotificacioEnviamentEntity enviament) {

		var entitat = enviament.getNotificacio().getEntitat();
		var organ = enviament.getNotificacio().getOrganGestor();
		var idioma = enviament.getNotificacio().getIdioma();
		var textBody = (Idioma.ES.equals(idioma) ?
				"AVISO DE PRÓXIMA NOTIFICACIÓN\n" +
				"\n" +
				"Le informamos que en breve recibirá una nueva notificación como INTERESADO, procedente del organismo '" + organ.getNom() + " (" + entitat.getNom() + ")' con los siguientes datos: \n" +
				"\n" +
				getInformacioEnviamentPlainText(enviament, false) +
				"\n" +
				"Usted recibirá esta notificación por vía postal en los próximos dias. \n" +
				"\n" +
				"\n"
				:
				"AVÍS DE PROPERA NOTIFICACIÓ\n" +
				"\n" +
				"L'informam que en breu rebrà una nova notificació com a INTERESSAT, procedent de l'organisme '" + organ.getNom() + " (" + entitat.getNom() + ")' amb les següents dades: \n" +
				"\n" +
				getInformacioEnviamentPlainText(enviament, true) +
				"\n" +
				"Vosté rebrà aquesta notificació per via postal en els propers dies. \n" +
				"\n" +
				"\n")
				+ entitat.getNom().toUpperCase() + "\n";
		return textBody;
	}

	private String getComunicacioMailHtmlBody(NotificacioEnviamentEntity enviament) {

		var entitat = enviament.getNotificacio().getEntitat();
		var organ = enviament.getNotificacio().getOrganGestor();
		var idioma = enviament.getNotificacio().getIdioma();
		var htmlText = "<div class=\"content\">" +
				"<br/>" +
				(Idioma.ES.equals(idioma) ?
					"<h2>Aviso de nueva comunicación</h2>" +
							"<p>Nos ponemos en contacto con usted para hacerle llegar una nueva comunicación como INTERESADO, procedente del organismo <b>" + organ.getNom() + "</b> (" + entitat.getNom() + ")' con los siguientes datos:</p>" +
							getInformacioEnviamentHtml(enviament, false) +
							"<p>La documentación de esta comunicación se ha adjuntado a este correo electrónico.</p>"

					:
					"<h2>Avís de nova comunicació</h2>" +
							"<p>Ens posam en contacte amb vosté per fer-li arribar una nova comunicació com a INTERESSAT, procedent de l'organisme <b>" + organ.getNom() + "</b> (" + entitat.getNom() + ")' amb les següents dades:</p>" +
							getInformacioEnviamentHtml(enviament, true) +
							"<p>La documentació d'aquesta comunicació s'ha adjuntat a aquest correu electrònic.</p>")
							+ "</div>";
		return htmlText;
	}

	private String getComunicacioMailPlainTextBody(NotificacioEnviamentEntity enviament) {

		var entitat = enviament.getNotificacio().getEntitat();
		var organ = enviament.getNotificacio().getOrganGestor();
		var idioma = enviament.getNotificacio().getIdioma();
		var textBody = (Idioma.ES.equals(idioma) ?
				"NOVA COMUNICACIÓ\n" +
				"\n" +
				"Ens posam en contacte amb vosté per fer-li arribar una nova comunicació com a INTERESSAT, procedent de l'organisme '" + organ.getNom() + " (" + entitat.getNom() + ")' amb les següents dades: \n" +
				"\n" +
				getInformacioEnviamentPlainText(enviament, true) +
				"\n" +
				"La documentació d'aquesta comunicació s'ha adjuntat a aquest correu electrònic. \n" +
				"\n" +
				"\n"
				:
				"NUEVA COMUNICACIÓN\n" +
				"\n" +
				"Nos ponemos en contacto con usted para hacerle llegar una nueva comunicación como INTERESADO, procedente del organismo '" + organ.getNom() + " (" + entitat.getNom() + ")' con los siguientes datos: \n" +
				"\n" +
				getInformacioEnviamentPlainText(enviament, false) +
				"\n" +
				"La documentación de esta comunicación se ha adjuntado a este correo electrónico. \n" +
				"\n" +
				"\n")
				+ entitat.getNom().toUpperCase() + "\n";
		return textBody;
	}

	private String getHeader() {

		var header = "<!DOCTYPE html>"+
				"<html>"+
				"<head>" +
				"<meta charset='UTF-8'>"+
				"<style>"+

				"body {"+
				"	margin: 0px;"+
				"	font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;"+
				"	font-size: 14px;"+
				"	color: #333;"+
				"}"+

				".header {"+
				"	height: 90px;"+
				"	text-align: center;"+
				"	line-height: 100px;"+
				"	margin: auto;"+
				"	padding-top: 10px;" +
				"	width: 90%;" +
				"	border-bottom: solid 2px;"+
				"}"+

				".left-header {"+
				"	float: left;"+
				"}"+

				".right-header {"+
				"	float: right;"+
				"}"+

				".content {"+
				"	margin: auto;"+
				"	width: 90%;"+
				"	padding: 10px;"+
				"}"+

				".footer {"+
				"	text-align: center;"+
				"	height: 30px;"+
				"	margin: auto;"+
				"	padding-top: 10px;"+
				"	color: #999;" +
				"	width: 90%;" +
				"	border-top: solid 2px;"+
				"}"+

				".left-footer {"+
				"	float: left;"+
				"}"+

				".right-footer {"+
				"	float: right;"+
				"}"+

				".info-titol {"+
				"	display: inline-block;"+
				"	font-weight: bold;"+
				"	width: 150px;"+
				"	min-width: 160px;"+
				"}"+

				"</style>"+
				"</head>"+

				"<body>"+
				"<div class=\"header\">" +
				"	<div class=\"left-header\"><img src=\"cid:logo-entitat\" height=\"80\"></div>" +
				"	<div class=\"right-header\"><img src=\"cid:logo-notib\" height=\"80\"></div>" +
				"</div>";
		return header;
	}

	private String getFooter(String entitatNon, boolean tePeuLogo) {

		var footer = "<div class=\"footer\"><div class=\"left-footer\">Notib - " + entitatNon + "</div>";
		if (tePeuLogo) {
			footer += "	<div class=\"right-footer\"><img src=\"cid:logo-peu\" height=\"60\"></div>";
		}
		footer += "</div></body></html>";
		return footer;
	}

	private String getInformacioEnviamentPlainText(NotificacioEnviamentEntity enviament, boolean catala) {

		var desc = enviament.getNotificacio().getDescripcio();
		desc = desc != null ? desc : catala ? "Sense informació adicional" : "Sin información adicional";
		var procedimentNom = enviament.getNotificacio().getProcediment() != null ? enviament.getNotificacio().getProcediment().getNom() : "";
		return catala ?  "\t- Destinatari: " + enviament.getTitular().getNomSencer() + "\n" +
				"\t- Identificador: " + enviament.getNotificaReferencia() + "\n" +
				"\t- Procediment: " + procedimentNom + "\n" +
				"\t- Concepte: " + enviament.getNotificacio().getConcepte() + "\n" +
				"\t- Informació addicional: " + desc + "\n"
				:
				"\t- Destinatario: " + enviament.getTitular().getNomSencer() + "\n" +
				"\t- Identificador: " + enviament.getNotificaReferencia() + "\n" +
				"\t- Procedimiento: " + procedimentNom + "\n" +
				"\t- Concepto: " + enviament.getNotificacio().getConcepte() + "\n" +
				"\t- Información adicional: " + desc + "\n";
	}

	private String getInformacioEnviamentHtml(NotificacioEnviamentEntity enviament, boolean catala) {

		var desc = enviament.getNotificacio().getDescripcio();
		desc = desc != null ? desc : catala ? "Sense informació adicional" : "Sin información adicional";
		var procedimentNom = enviament.getNotificacio().getProcediment() != null ? enviament.getNotificacio().getProcediment().getNom() : "";
		return  catala ?
			"<ul>" +
				"<li><span class='info-titol'>Destinatari:</span><span class='info-desc'>" + enviament.getTitular().getNomSencer() + "</span></li>" +
				"<li><span class='info-titol'>Identificador:</span><span class='info-desc'>" + enviament.getNotificaReferencia() + "</span></li>" +
				"<li><span class='info-titol'>Procediment:</span><span class='info-desc'>" + procedimentNom + "</span></li>" +
				"<li><span class='info-titol'>Concepte:</span><span class='info-desc'>" + enviament.getNotificacio().getConcepte() + "</span></li>" +
				"<li><span class='info-titol'>Informació addicional:</span><span class='info-desc'>" + desc + "</span></li>" +
			"</ul>"
			:
			 "<ul>" +
				"<li><span class='info-titol'>Destinatario:</span><span class='info-desc'>" + enviament.getTitular().getNomSencer() + "</span></li>" +
				"<li><span class='info-titol'>Identificador:</span><span class='info-desc'>" + enviament.getNotificaReferencia() + "</span></li>" +
				"<li><span class='info-titol'>Procedimiento:</span><span class='info-desc'>" + procedimentNom + "</span></li>" +
				"<li><span class='info-titol'>Concepto:</span><span class='info-desc'>" + enviament.getNotificacio().getConcepte() + "</span></li>" +
				"<li><span class='info-titol'>Información adicional:</span><span class='info-desc'>" + desc + "</span></li>" +
			"</ul>";
	}

	private String getRemitent() {
		return configHelper.getConfig("es.caib.notib.email.remitent");
	}

	private InputStream getCapsaleraLogoNotib() {
		return getClass().getResourceAsStream("/es/caib/notib/logic/justificant/logo.png");
	}

	private String getPeuLogo() {
		return getNotBlankProperty(configHelper.getConfig("es.caib.notib.justificant.peu.logo"));
	}

	private String getCapsaleraLogoEntitat() {
		return getNotBlankProperty(configHelper.getConfig("es.caib.notib.justificant.capsalera.logo"));
	}

	private InputStream getCapsaleraDefaultLogoEntitat() {
		return getClass().getResourceAsStream("/es/caib/notib/logic/justificant/govern-logo.png");
	}

	private String getNotBlankProperty(String property) {
		if (property == null || property.trim().isEmpty()) {
			return null;
		}
		return property.trim();
	}
}
