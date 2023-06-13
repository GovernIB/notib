package es.caib.notib.logic.helper;

import com.google.common.base.Strings;
import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.UsuariEntity;
import es.caib.notib.plugin.usuari.DadesUsuari;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * Mètodes per a l'enviament de correus.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class EmailNotificacioHelper extends EmailHelper<NotificacioEntity> {

//	@Resource
//	private MessageHelper messageHelper;
	@Resource ProcSerHelper procSerHelper;

	public String prepararEnvioEmailNotificacio(NotificacioEntity notificacio) throws Exception {

		try {
			var destinataris = obtenirCodiDestinatarisPerProcediment(notificacio);
			if (destinataris == null || destinataris.isEmpty()) {
				log.info(String.format("La notificació (Id= %d) no té candidats per a enviar el correu electrònic", notificacio.getId()));
				return null;
			}
			for (var usuariDto : destinataris) {
				if (usuariDto.getEmail() == null || usuariDto.getEmail().isEmpty()) {
					log.error("usuari sense email. Codi: " + usuariDto.getCodi());
					continue;
				}
				var email = !Strings.isNullOrEmpty(usuariDto.getEmailAlt()) ? usuariDto.getEmailAlt() : usuariDto.getEmail();
				email = email.replaceAll("\\s+","");
				log.info(String.format("Enviant correu notificació (Id= %d) a %s", notificacio.getId(), email));
				sendEmailNotificacio(email, notificacio);
			}
			return null;
		} catch (Exception ex) {
			var errorDescripcio = "No s'ha pogut avisar per correu electrònic: " + ex;
			log.error(errorDescripcio);
			return errorDescripcio;
		}
	}

	private List<UsuariDto> obtenirCodiDestinatarisPerProcediment(NotificacioEntity notificacio) {

		List<UsuariDto> destinataris = new ArrayList<>();
//		ProcSerEntity proc = notificacio.getProcediment();

		var usuaris = procSerHelper.findUsuaris(notificacio);
		DadesUsuari dadesUsuari;
		for (var usuari: usuaris) {
			dadesUsuari = cacheHelper.findUsuariAmbCodi(usuari);
			if (dadesUsuari == null || Strings.isNullOrEmpty(dadesUsuari.getEmail())) {
				continue;
			}
			var user = usuariRepository.findById(usuari).orElse(null);
			if (user == null || (user.isRebreEmailsNotificacio() && (!user.isRebreEmailsNotificacioCreats()
				|| user.isRebreEmailsNotificacioCreats() && usuari.equals(notificacio.getCreatedBy().get().getCodi())))) {

				var u = new UsuariDto();
				u.setCodi(usuari);
				u.setEmail((user != null && !Strings.isNullOrEmpty(user.getEmailAlt())) ? user.getEmailAlt() : dadesUsuari.getEmail());
				destinataris.add(u);
			}
		}
		return destinataris;
	}

	@Override
	protected String getMailHtmlBody(NotificacioEntity notificacio) {

		var appBaseUrl = configHelper.getConfig("es.caib.notib.app.base.url");
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
				"	<span class=\"headerText\">"+ messageHelper.getMessage("notificacio.titol").toUpperCase()+"</span> "+
				"</div>"+
				"<div class=\"content\">"+
				"	<table>"+
				"		<tr>"+
				"			<th class=\"tableHeader\" colspan=\"2\">" + messageHelper.getMessage("notificacio.email.titol") + notificacio.getId() + "</th>"+
				"		</tr>"+
				"		<tr>"+
				"			<th>"+ messageHelper.getMessage("notificacio.email.notificacio") +"</th>"+
				"			<td>"+ notificacio.getId() + "</td>"+
				"		</tr>"+
				"		<tr>"+
				"			<th>"+ messageHelper.getMessage("notificacio.email.notificacio.concepte") +"</th>"+
				"			<td>"+ notificacio.getConcepte() + "</td>"+
				"		</tr>"+
				"		<tr>"+
				"			<th>"+ messageHelper.getMessage("notificacio.email.procediment") +"</th>"+
				"			<td>"+ (notificacio.getProcediment() != null ? notificacio.getProcediment().getNom() : "----") + "</td>"+
				"		</tr>"+
				"		<tr>"+
				"			<th>"+ messageHelper.getMessage("notificacio.email.entitat") +"</th>"+
				"			<td>"+ notificacio.getEntitat().getNom() + "</td>"+
				"		</tr>"+
				"		<tr>"+
				"			<th>"+ messageHelper.getMessage("notificacio.email.estat.nou") +"</th>"+
				"			<td>"+ messageHelper.getMessage("notificacio.estat.enum." + notificacio.getEstat()) + "</td>"+
				"		</tr>"+
				"		<tr>"+
				"			<th>"+ messageHelper.getMessage("notificacio.email.estat.motiu") +"</th>"+
				"			<td>"+ notificacio.getMotiu() + "</td>"+
				"		</tr>"+
				"		<tr>"+
				"			<th>"+ messageHelper.getMessage("notificacio.email.notificacio.info") + "</th>"+
				"			<td> <a href=\""+appBaseUrl+"/notificacio/"+notificacio.getId()+ "/info\">"+ messageHelper.getMessage("notificacio.email.notificacio.detall") + "</a>" +
				"			</td>"+
				"		</tr>"+
				"	</table>" +
				"</div>"+
				"<div class=\"footer\">"+
				"	<span class=\"footerText\">"+
				getEmailFooter() +
				"	</span>"+
				"</div>"+
				"</body>"+
				"</html>";
		return htmlText;
	}
	protected String getMailPlainTextBody(NotificacioEntity notificacio) {

		var appBaseUrl = configHelper.getConfig("es.caib.notib.app.base.url");
		return
				messageHelper.getMessage("notificacio.email.notificacio")+
						"\t\t\t\t"+ Objects.toString(notificacio.getId(), "")+"\n"+
						"\t"+messageHelper.getMessage("notificacio.email.notificacio.concepte") +
						"\t\t\t\t"+ Objects.toString(notificacio.getConcepte(), "") +"\n"+
						"\t"+messageHelper.getMessage("notificacio.email.procediment")+
						"\t\t\t\t"+ Objects.toString(notificacio.getProcediment() != null ? notificacio.getProcediment().getNom() : "----", "")+"\n"+
						"\t"+messageHelper.getMessage("notificacio.email.entitat")+
						"\t\t\t\t"+ Objects.toString(notificacio.getEmisorDir3Codi(), "")+"\n"+
						"\t"+messageHelper.getMessage("notificacio.email.estat.nou") +
						"\t\t\t\t"+ Objects.toString(notificacio.getEstat().name(), "") +"\n"+
						"\t"+messageHelper.getMessage("notificacio.email.estat.motiu") +
						"\t\t\t\t"+ Objects.toString(notificacio.getMotiu(), "") +"\n"+
						"\t"+messageHelper.getMessage("notificacio.email.notificacio.info") +
						"\t\t\t\t"+ "<a href=\""+appBaseUrl+"/notificacio/"+notificacio.getId()+"/info\">"+ messageHelper.getMessage("notificacio.email.notificacio.detall") + "</a> \n"+
						"";
	}
	@Override
	protected String getMailSubject() {
		return "Canvis d'estat a les notificacions";
	}
}
