package es.caib.notib.logic.helper;

import com.google.common.base.Strings;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodiEnum;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.plugin.usuari.DadesUsuari;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Mètodes per a l'enviament de correus.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class EmailNotificacioHelper extends EmailHelper<NotificacioEntity> {

	@Resource ProcSerHelper procSerHelper;
	@Resource IntegracioHelper integracioHelper;

	public String prepararEnvioEmailNotificacio(NotificacioEntity notificacio) throws Exception {

		var destinataris = obtenirCodiDestinatarisPerProcediment(notificacio);
		if (destinataris == null || destinataris.isEmpty()) {
			log.info(String.format("La notificació (Id= %d) no té candidats per a enviar el correu electrònic", notificacio.getId()));
			return "No s'han trobat destinataris";
		}
		// TODO: Optimitzar per enviar un únic email
		int numEnviamentsErronis = 0;
		for (var usuariDto : destinataris) {
			var info = new IntegracioInfo(
					IntegracioCodiEnum.EMAIL,
					"Enviament de email per notificació a un destinatari",
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					new AccioParam("Identificador de la notificacio", String.valueOf(notificacio.getId())),
					new AccioParam("Destinatari", usuariDto.getNom()),
					new AccioParam("Correu electrònic", usuariDto.getEmail()));
			info.setCodiEntitat(notificacio.getEntitat().getCodi());
			if (usuariDto.getEmail() == null || usuariDto.getEmail().isEmpty()) {
				log.error("usuari sense email. Codi: " + usuariDto.getCodi());
				integracioHelper.addAccioError(info, "Destinatari " + usuariDto.getNom() + " no té email");
				numEnviamentsErronis++;
				continue;
			}

			try {
				var email = !Strings.isNullOrEmpty(usuariDto.getEmailAlt()) ? usuariDto.getEmailAlt() : usuariDto.getEmail();
				email = email.replaceAll("\\s+","");
				log.info(String.format("Enviant correu notificació (Id= %d) a %s", notificacio.getId(), email));
				sendEmailNotificacio(email, notificacio);
				integracioHelper.addAccioOk(info);
			} catch (Exception ex) {
				log.error("No s'ha pogut avisar per correu electrònic a: " + usuariDto.getNom(), ex);
				integracioHelper.addAccioError(info, "Error enviant email", ex);
				numEnviamentsErronis++;
			}
		}
		return numEnviamentsErronis > 0 ? numEnviamentsErronis + " emails de " + destinataris.size() + " han produït error" : "Tots els emails enviats correctament";
	}

	private List<UsuariDto> obtenirCodiDestinatarisPerProcediment(NotificacioEntity notificacio) {

		List<UsuariDto> destinataris = new ArrayList<>();
		var usuaris = procSerHelper.findUsuaris(notificacio);
		DadesUsuari dadesUsuari;
		for (var usuari: usuaris) {
			dadesUsuari = cacheHelper.findUsuariAmbCodi(usuari);
			if (dadesUsuari == null || Strings.isNullOrEmpty(dadesUsuari.getEmail())) {
				continue;
			}
			var user = usuariRepository.findById(usuari).orElse(null);
			if (user == null || (user.isRebreEmailsNotificacio() && (!user.isRebreEmailsNotificacioCreats()
				|| user.isRebreEmailsNotificacioCreats() && usuari.equals(notificacio.getCreatedBy().orElseThrow().getCodi())))) {

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
		var t = "\t\t\t\t";
		return messageHelper.getMessage("notificacio.email.notificacio")+
						t+ Objects.toString(notificacio.getId(), "")+"\n"+
						"\t"+messageHelper.getMessage("notificacio.email.notificacio.concepte") +
						t+ Objects.toString(notificacio.getConcepte(), "") +"\n"+
						"\t"+messageHelper.getMessage("notificacio.email.procediment")+
						t+ Objects.toString(notificacio.getProcediment() != null ? notificacio.getProcediment().getNom() : "----", "")+"\n"+
						"\t"+messageHelper.getMessage("notificacio.email.entitat")+
						t+ Objects.toString(notificacio.getEmisorDir3Codi(), "")+"\n"+
						"\t"+messageHelper.getMessage("notificacio.email.estat.nou") +
						t+ Objects.toString(notificacio.getEstat().name(), "") +"\n"+
						"\t"+messageHelper.getMessage("notificacio.email.estat.motiu") +
						t+ Objects.toString(notificacio.getMotiu(), "") +"\n"+
						"\t"+messageHelper.getMessage("notificacio.email.notificacio.info") +
						t+ "<a href=\""+appBaseUrl+"/notificacio/"+notificacio.getId()+"/info\">"+ messageHelper.getMessage("notificacio.email.notificacio.detall") + "</a> \n"+
						"";
	}
	@Override
	protected String getMailSubject() {
		return "Canvis d'estat a les notificacions";
	}
}
