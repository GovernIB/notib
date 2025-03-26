package es.caib.notib.logic.helper;

import com.google.common.base.Strings;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.logic.mapper.NotificacioTableMapperImpl;
import es.caib.notib.logic.objectes.LoggingTipus;
import es.caib.notib.logic.utils.NotibLogger;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.PersonaEntity;
import es.caib.notib.plugin.usuari.DadesUsuari;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Mètodes per a l'enviament de correus.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class EmailNotificacioHelper extends EmailHelper<NotificacioEnviamentEntity> {

	@Resource ProcSerHelper procSerHelper;
	@Resource IntegracioHelper integracioHelper;
    @Autowired
    private NotificacioTableMapperImpl notificacioTableMapperImpl;

	public String prepararEnvioEmailNotificacio(NotificacioEnviamentEntity enviament) throws Exception {


		var info = new IntegracioInfo(IntegracioCodi.EMAIL, "Enviament de emails per notificació " + notificacio.getId(), IntegracioAccioTipusEnumDto.ENVIAMENT);
		info.setCodiEntitat(notificacio.getEntitat().getCodi());
		info.setNotificacioId(notificacio.getId());
		info.setAplicacio(notificacio.getTipusUsuari(), notificacio.getCreatedBy().get().getCodi());
		info.addParam("Identificador de la notificacio", String.valueOf(notificacio.getId()));
		var destinataris = obtenirCodiDestinataris(notificacio, info);
		if (destinataris == null || destinataris.isEmpty()) {
			var msg = String.format("La notificació (Id= %d) no té candidats per a enviar el correu electrònic", notificacio.getId());
			log.info(msg);
			info.addParam("Resultat", msg);
			integracioHelper.addAccioOk(info);
			return msg;
		}
		// TODO: Optimitzar per enviar un únic email
		int numEnviamentsErronis = 0;
		StringBuilder error = new StringBuilder();
		List<Exception> exceptions = new ArrayList<>();
		for (var usuariDto : destinataris) {
			if (usuariDto.getEmail() == null || usuariDto.getEmail().isEmpty()) {
				var msg = "Usuari sense email. Codi: " + usuariDto.getCodi();
				log.error(msg);
				info.addParam("Error", msg);
//				integracioHelper.addAccioError(info, "Destinatari " + usuariDto.getNom() + " no té email");
				numEnviamentsErronis++;
				continue;
			}
			var email = !Strings.isNullOrEmpty(usuariDto.getEmailAlt()) ? usuariDto.getEmailAlt() : usuariDto.getEmail();
			email = email.replaceAll("\\s+","");
			try {
				NotibLogger.getInstance().printInfoSistema(log, LoggingTipus.METRIQUES_SISTEMA);
				log.info(String.format("Enviant correu notificació (Id= %d) a %s", notificacio.getId(), email));
				info.addParam(usuariDto.getCodi(), "Enviant correu electrònic a " + usuariDto.getNom() + " - " + usuariDto.getEmail());
				sendEmailNotificacio(email, notificacio);
			} catch (Exception ex) {
				var msg = "Error enviant email. No s'ha pogut avisar per correu electrònic a: " + email;
				log.error(msg, ex);
				exceptions.add(ex);
				error.append(msg).append("<br>");
				numEnviamentsErronis++;
			}
		}
		var resultat = numEnviamentsErronis > 0 ? numEnviamentsErronis + " emails de " + destinataris.size() + " han produït error" : "Tots els emails enviats correctament";
		info.addParam("Resultat", resultat);
		if (Strings.isNullOrEmpty(error.toString())) {
			integracioHelper.addAccioOk(info);
//		} else if (!Strings.isNullOrEmpty(error.toString()) && numEnviamentsErronis == destinataris.size()) {
//			integracioHelper.addAccioError(info, error.toString());
		} else {
			var msg = "";
			for (var ex : exceptions) {
				msg += ex.getMessage() + "&#13;&#10;&#13;&#10;";
			}
			integracioHelper.addAccioWarn(info, error.toString(), new Exception(msg));
		}
		return resultat;
	}

	private List<UsuariDto> obtenirCodiDestinataris(NotificacioEntity notificacio, IntegracioInfo info) {

		List<UsuariDto> destinataris = new ArrayList<>();
		Set<String> usuaris;
		try {
			usuaris = notificacio.getProcediment() != null ? procSerHelper.findUsuaris(notificacio) : procSerHelper.findUsuarisAmbPermisReadPerOrgan(notificacio);
		} catch (Exception ex) {
			log.error("[EMAIL] Error al buscar els usuaris amb permisos ", ex);
			usuaris = new HashSet<>();
			usuaris.add(notificacio.getUsuariCodi());
		}

		if (usuaris.isEmpty()) {
			log.error("[EMAIL] No s'han trobat usuaris amb permisos, possible error de Keycloak. Afegit usuari de la notificacio. Usuari: " + notificacio.getUsuariCodi());
			usuaris.add(notificacio.getUsuariCodi());
		}
		if (!usuaris.contains(notificacio.getUsuariCodi())) {
			usuaris.add(notificacio.getUsuariCodi());
		}
		DadesUsuari dadesUsuari = null;
		for (var usuari: usuaris) {
			try {
				dadesUsuari = cacheHelper.findUsuariAmbCodi(usuari);
			} catch (Exception ex) {
				log.error("[EMAIL] Error al consultar l'usuari", ex);
			}
			var user = usuariRepository.findById(usuari).orElse(null);
			var usr = notificacio.getCreatedBy().orElse(null);
			var codi = usr != null ? usr.getCodi() : null;
			if (user == null && usuari.equals(codi) || (user != null && user.isRebreEmailsNotificacio() && (!user.isRebreEmailsNotificacioCreats() || user.isRebreEmailsNotificacioCreats() && usuari.equals(codi)))) {
				var u = new UsuariDto();
				u.setCodi(usuari);
				var nom = user != null && !Strings.isNullOrEmpty(user.getNomSencer()) ? user.getNomSencer() : dadesUsuari != null ? dadesUsuari.getNomSencer() : "";
				var email = user != null && !Strings.isNullOrEmpty(user.getEmailAlt()) ? user.getEmailAlt() : dadesUsuari != null ? dadesUsuari.getEmail() : usuari + "@caib.es";
				u.setNom(nom);
				u.setEmail(email);
				destinataris.add(u);
			} else {
				info.addParam(usuari, "No té activat l'enviament per correu electrònic");
			}
		}
		return destinataris;
	}

	@Override
	protected String getMailHtmlBody(NotificacioEnviamentEntity enviament) {

		var appBaseUrl = configHelper.getConfig("es.caib.notib.app.base.url");
		var notificacio = enviament.getNotificacio();
		var enviamenTipus = notificacio.getEnviamentTipus();
		var enviamentTipusString = messageHelper.getMessage(EnviamentTipus.NOTIFICACIO.equals(enviamenTipus) ? "notificacio"
									: EnviamentTipus.COMUNICACIO.equals(enviamenTipus) ? "comunicacio" : "comunicacio.sir");
		var nif = "";
		var interessat = "";
		var persona = enviament.getTitular();
		nif = !Strings.isNullOrEmpty(persona.getNif()) ? "(" + persona.getNif() + ")" : persona.getRaoSocial();
		interessat += "<tr>" +
				"			<th>" + messageHelper.getMessage("notificacio.email.enviament.interessat") + "</th>" +
				"			<td>" + persona.getNomSencer() + nif + "</td>" +
				"		</tr>";
		for (var destinatari : enviament.getDestinataris()) {
			nif = !Strings.isNullOrEmpty(destinatari.getNif()) ? "(" + nif + ")" : destinatari.getRaoSocial();
			interessat += "<tr>" +
					"			<th>" + messageHelper.getMessage("notificacio.email.enviament.representat") + "</th>" +
					"			<td>" + destinatari.getNomSencer() + nif + "</td>" +
					"		</tr>";
		}

		var enviadaEl = enviament.getNotificaEstatData() != null ? enviament.getNotificaEstatData()
						: EnviamentTipus.SIR.equals(enviamenTipus) ? enviament.getSirRegDestiData() != null ? enviament.getSirRegDestiData() : null
						: enviament.getRegistreData() != null ? enviament.getRegistreData() : null;

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
				"			<th class=\"tableHeader\" colspan=\"2\">" + messageHelper.getMessage("notificacio.email.titol") + enviamentTipusString + "</th>"+
				"		</tr>"+
				"		<tr>"+
				"			<th>"+ messageHelper.getMessage("notificacio.email.identificador") +"</th>"+
				"			<td>"+ notificacio.getReferencia() + "</td>"+
				"		</tr>"+
				"		<tr>"+
				"			<th>"+ messageHelper.getMessage("notificacio.email.entitat") +"</th>"+
				"			<td>"+ notificacio.getEntitat().getNom() + "</td>"+
				"		</tr>"+
				"		<tr>"+
				"			<th>"+ messageHelper.getMessage("notificacio.email.notificacio.organ") +"</th>"+
				"			<td>"+ notificacio.getOrganGestor().getCodi() + " - " + notificacio.getOrganGestor().getNom()+ "</td>"+
				"		</tr>"+
				(notificacio.getProcediment() != null ?
				"		<tr>"+
				"			<th>"+ messageHelper.getMessage("notificacio.email.procediment") +"</th>"+
				"			<td>"+ notificacio.getProcediment().getCodi() + " - " + notificacio.getProcediment().getNom() + "</td>"+
				"		</tr>" : "") +
				(!Strings.isNullOrEmpty(notificacio.getNumExpedient()) ?
				"		<tr>"+
				"			<th>"+ messageHelper.getMessage("notificacio.email.num.expedient") +"</th>"+
				"			<td>"+ notificacio.getNumExpedient() + "</td>"+
				"		</tr>" : "") +
				"		<tr>"+
				"			<th>"+ messageHelper.getMessage("notificacio.email.notificacio.concepte") +"</th>"+
				"			<td>"+ notificacio.getConcepte() + "</td>"+
				"		</tr>"
				+ interessat +
				"		<tr>"+
				"			<th>"+ messageHelper.getMessage("notificacio.email.enviament.creada.el") +"</th>"+
				"			<td>"+ notificacio.getCreatedDate() + "</td>"+
				"		</tr>"+
				"		<tr>"+
				"			<th>"+ messageHelper.getMessage("notificacio.email.enviament.enviada.el") +"</th>"+
				"			<td>"+ enviadaEl + "</td>"+
				"		</tr>"+
				(!Strings.isNullOrEmpty(notificacio.getRegistreNumeroFormatat()) ?
				"		<tr>"+
				"			<th>"+ messageHelper.getMessage("notificacio.email.enviament.num.registre") +"</th>"+
				"			<td>"+ enviament.getRegistreNumeroFormatat() + "</td>"+
				"		</tr>" : "") +
				"		<tr>"+
				"			<th>"+ messageHelper.getMessage("notificacio.email.estat.nou") +"</th>"+
				"			<td>"+ messageHelper.getMessage("notificacio.estat.enum." + notificacio.getEstat()) + "</td>"+
				"		</tr>" +
				(!Strings.isNullOrEmpty(notificacio.getMotiu()) ?
					"		<tr>" +
							"			<th>" + messageHelper.getMessage("notificacio.email.estat.motiu") + "</th>" +
							"			<td>" + notificacio.getMotiu() + "</td>" +
							"		</tr>" +
							"		<tr>" : "") +
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
