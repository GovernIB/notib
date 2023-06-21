package es.caib.notib.logic.helper;

import es.caib.notib.persist.entity.NotificacioMassivaEntity;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * Mètodes per a l'enviament de correus.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class EmailNotificacioMassivaHelper extends EmailHelper<NotificacioMassivaEntity> {
	@Resource
	private MessageHelper messageHelper;

	public String sendMail(@NonNull NotificacioMassivaEntity item, @NonNull String email, @NonNull byte[] fileResumContent, @NonNull byte[] fileErrorsContent) throws Exception {

		try {
			email = email.replaceAll("\\s+","");
			sendEmailNotificacio(email, item, Arrays.asList(new Attachment("resum.csv", fileResumContent), new Attachment("errors.csv", fileErrorsContent)));
		} catch (Exception ex) {
			var errorDescripcio = "No s'ha pogut avisar per correu electrònic: " + ex;
			log.error(errorDescripcio);
			return errorDescripcio;
		}
		return null;
	}
	@Override
	protected String getMailHtmlBody(NotificacioMassivaEntity item) {

		var htmlText = "";
		htmlText += "<!DOCTYPE html>"+
				"<html>"+
				"<head>"+
				"<style>"+
				"body {"+
				"	margin: 0px;"+
				"	font-family: \"Helvetica Neue\", Helvetica, Arial, sans-serif;"+
				"	font-size: 14px;"+
				"	color: #333;"+
				"}"+
				"table {"+
				"	"+
				"	border-radius: 4px;"+
				"	width: 100%;"+
				"	border-collapse: collapse;"+
				"	margin-bottom: 10px;"+
				"}"+

				"td, th {"+
				"	border-bottom: solid 0.5px #ddd;"+
				"	height: 38px;"+
				"	border: 1px solid #ddd;"+
				"	padding-left: 8px;"+
				"	padding-right: 8px;"+
				"}"+

				".tableHeader {"+
				"	background-color: #f5f5f5;"+
				"	border-top-left-radius: 4px;"+
				"	border-top-righ-radius: 4px;"+
				"}"+

				".header {"+
				"	height: 30px;"+
				"	background-color: #ff9523;"+
				"	height: 90px;"+
				"	text-align: center;"+
				"	line-height: 100px;"+
				"}"+
				".content {"+
				"	margin: auto;"+
				"	width: 70%;"+
				"	padding: 10px;"+
				"}"+

				".footer {"+
				"	height: 30px;"+
				"	background-color: #ff9523;"+
				"	text-align: center;"+

				"}"+

				".headerText {"+
				"    font-weight: bold;"+
				"    font-family: \"Trebuchet MS\", Helvetica, sans-serif;"+
				"    color: white;"+
				"    font-size: 30px;"+
				"	display: inline-block;"+
				"	vertical-align: middle;"+
				"	line-height: normal; "+
				"}"+

				".footerText {"+
				"    font-weight: bold;"+
				"    font-family: \"Trebuchet MS\", Helvetica, sans-serif;"+
				"    color: white;"+
				"    font-size: 13px;"+
				"	display: inline-block;"+
				"	vertical-align: middle;"+
				"	line-height: normal; "+
				"}"+
				"</style>"+
				"</head>"+

				"<body>"+
				"<div class=\"header\">"+
				"	<span class=\"headerText\">"+ messageHelper.getMessage("notificacio.massiva.email.titol").toUpperCase()+"</span> "+
				"</div>"+
				"<div class=\"content\">" +
				"	<p>" + messageHelper.getMessage("notificacio.massiva.email.body") + "</p>" +
				" 	<ul>" +
				"		<li>" + messageHelper.getMessage("notificacio.massiva.email.body.fitxer1") + "</li>" +
				"		<li>" + messageHelper.getMessage("notificacio.massiva.email.body.fitxer2") + "</li>" +
				"	</ul>" +
				"</div>" +
				"<div class=\"footer\">"+
				"	<span class=\"footerText\">"+
				getEmailFooter() +
				"	</span>"+
				"</div>"+
				"</body>"+
				"</html>";
		return htmlText;
	}

	@Override
	protected String getMailPlainTextBody(NotificacioMassivaEntity item) {

		var appBaseUrl = configHelper.getConfig("es.caib.notib.app.base.url");
		return 	"\t" + messageHelper.getMessage("notificacio.massiva.email.body") +
				"\t\t\t\t" + messageHelper.getMessage("notificacio.massiva.email.body.fitxer1") + "\n" +
				"\t\t\t\t" + messageHelper.getMessage("notificacio.massiva.email.body.fitxer2") + "\n" +
				// TODO: posar url als detalls de la notificacio massiva
				"\t"+ "<a href=\""+appBaseUrl+"/notificacio/\">"+ messageHelper.getMessage("notificacio.email.notificacio.detall") + "</a> \n";
	}

	@Override
	protected String getMailSubject() {
		return "Resultat de l'enviament massiu: ";
	}
}
