/**
 * 
 */
package es.caib.notib.core.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.entity.UsuariEntity;
import es.caib.notib.core.repository.UsuariRepository;
import es.caib.notib.plugin.usuari.DadesUsuari;

/**
 * Mètodes per a l'enviament de correus.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class EmailHelper {

	private static final String PREFIX_NOTIB = "[NOTIB]";
	@Resource
	private ProcedimentHelper procedimentHelper;
	@Resource
	private CacheHelper cacheHelper;
	@Resource
	private UsuariRepository usuariRepository;
	@Resource
	private JavaMailSender mailSender;
	@Resource
	private MessageHelper messageHelper;
	
	public void prepararEnvioEmailNotificacio(NotificacioEntity notificacio) throws MessagingException {
		logger.info("Desant emails del procediment (" + notificacio.getProcediment().getId() + ") per a l'enviament");
		List<UsuariDto> destinataris = obtenirCodiDestinatarisPerProcediment(notificacio.getProcediment());
		
		if (destinataris != null && !destinataris.isEmpty()) {
			for (UsuariDto usuariDto : destinataris) {
				sendEmailBustiaPendentContingut(
						usuariDto.getEmail(),
						notificacio);
			}
		}	
	}
	
	public void sendEmailBustiaPendentContingut(
			String emailDestinatari,
			NotificacioEntity notificacio) throws MessagingException {
		logger.debug("Enviament emails nou contenidor a bústies");

		MimeMessage missatge = mailSender.createMimeMessage();
		missatge.setHeader("Content-Type", "text/html charset=UTF-8");
		MimeMessageHelper helper;
		helper = new MimeMessageHelper(missatge, true);
		helper.setTo(emailDestinatari);
		helper.setFrom(getRemitent());
		helper.setSubject(PREFIX_NOTIB + " Canvis d'estat a les notificacions");
		//Html text
		String htmlText = "";		
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
					"			<td>"+ notificacio.getProcediment().getNom() + "</td>"+
					"		</tr>"+	
					"		<tr>"+
					"			<th>"+ messageHelper.getMessage("notificacio.email.entitat") +"</th>"+
					"			<td>"+ notificacio.getEntitat().getNom() + "</td>"+
					"		</tr>"+
					"		<tr>"+
					"			<th>"+ messageHelper.getMessage("notificacio.email.estat.nou") +"</th>"+
					"			<td>"+ messageHelper.getMessage("notificacio.estat.enum.FINALITZADA") + "</td>"+
					"		</tr>"+	
					"		<tr>"+
					"			<th>"+ messageHelper.getMessage("notificacio.email.estat.motiu") +"</th>"+
					"			<td>"+ notificacio.getMotiu() + "</td>"+
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
		String plainText = 
				messageHelper.getMessage("notificacio.email.notificacio")+
				"\t\t\t\t"+ Objects.toString(notificacio.getId(), "")+"\n"+
				"\t"+messageHelper.getMessage("notificacio.email.notificacio.concepte") + 
				"\t\t\t\t"+ Objects.toString(notificacio.getConcepte(), "") +"\n"+
				"\t"+messageHelper.getMessage("notificacio.email.procediment")+
				"\t\t\t\t"+ Objects.toString(notificacio.getProcediment().getNom(), "")+"\n"+
				"\t"+messageHelper.getMessage("notificacio.email.entitat")+
				"\t\t\t\t"+ Objects.toString(notificacio.getEmisorDir3Codi(), "")+"\n"+
				"\t"+messageHelper.getMessage("notificacio.email.estat.nou") + 
				"\t\t\t\t"+ Objects.toString(notificacio.getEstat().name(), "") +"\n"+		
				"\t"+messageHelper.getMessage("notificacio.email.estat.motiu") + 
				"\t\t\t\t"+ Objects.toString(notificacio.getMotiu(), "") +"\n"+
				"";
		
		helper.setText(plainText, htmlText);
		mailSender.send(missatge);
	}
	
	private List<UsuariDto> obtenirCodiDestinatarisPerProcediment(ProcedimentEntity procediment) {
		List<UsuariDto> destinataris = new ArrayList<UsuariDto>();
		Set<String> usuaris = procedimentHelper.findUsuarisAmbPermisReadPerProcediment(procediment);
		for (String usuari: usuaris) {
			DadesUsuari dadesUsuari = cacheHelper.findUsuariAmbCodi(usuari);
			if (dadesUsuari != null && dadesUsuari.getEmail() != null) {
				UsuariEntity user = usuariRepository.findOne(usuari);
				if (user == null || user.isRebreEmailsNotificacio()) {
					UsuariDto u = new UsuariDto();
					u.setCodi(usuari);
					u.setEmail(dadesUsuari.getEmail());
					destinataris.add(u);
				}
			}
		}
		return destinataris;
	}
	
	public String getRemitent() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.email.remitent");
	}
	
	public String getEmailFooter() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.email.footer");
	}
	
	private static final Logger logger = LoggerFactory.getLogger(EmailHelper.class);

	public void setMessageHelper(MessageHelper messageHelper) {
		this.messageHelper = messageHelper;		
	}
}
