/**
 * 
 */
package es.caib.notib.core.api.dto;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'un destinatari d'una anotació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificacioEnviamentDtoV2 extends AuditoriaDto {

	private Long id;
	private Date createdDate;
	private String notificaIdentificador;
	private String usuari;
	private NotificacioDtoV2 notificacio;
	private String titularNom;
	private String titularNomLlinatge;
	private String titularLlinatge1;
	private String titularLlinatge2;
	private String titularNif;
	private String titularEmail;
	private String titularRaoSocial;
	private String destinatariNom;
	private String destinatariNomLlinatges;
	private String destinatariLlinatge1;
	private String destinatariLlinatge2;
	private String destinatariRaoSocial;
	private String destinatariNif;
	private String destinatariEmail;
	private NotificaDto notifica;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public String getNotificaIdentificador() {
		return notificaIdentificador;
	}
	public void setNotificaIdentificador(String notificaIdentificador) {
		this.notificaIdentificador = notificaIdentificador;
	}
	public String getUsuari() {
		return usuari;
	}
	public void setUsuari(String usuari) {
		this.usuari = usuari;
	}
	public NotificacioDtoV2 getNotificacio() {
		return notificacio;
	}
	public void setNotificacio(NotificacioDtoV2 notificacio) {
		this.notificacio = notificacio;
	}
	public String getTitularNom() {
		return titularNom;
	}
	public void setTitularNom(String titularNom) {
		this.titularNom = titularNom;
	}
	public String getTitularLlinatge1() {
		return titularLlinatge1;
	}
	public void setTitularLlinatge1(String titularLlinatge1) {
		this.titularLlinatge1 = titularLlinatge1;
	}
	public String getTitularLlinatge2() {
		return titularLlinatge2;
	}
	public void setTitularLlinatge2(String titularLlinatge2) {
		this.titularLlinatge2 = titularLlinatge2;
	}
	public String getTitularNif() {
		return titularNif;
	}
	public void setTitularNif(String titularNif) {
		this.titularNif = titularNif;
	}
	public String getTitularRaoSocial() {
		return titularRaoSocial;
	}
	public void setTitularRaoSocial(String titularRaoSocial) {
		this.titularRaoSocial = titularRaoSocial;
	}
	public String getDestinatariNom() {
		return destinatariNom;
	}
	public void setDestinatariNom(String destinatariNom) {
		this.destinatariNom = destinatariNom;
	}
	public String getDestinatariLlinatge1() {
		return destinatariLlinatge1;
	}
	public void setDestinatariLlinatge1(String destinatariLlinatge1) {
		this.destinatariLlinatge1 = destinatariLlinatge1;
	}
	public String getDestinatariLlinatge2() {
		return destinatariLlinatge2;
	}
	public void setDestinatariLlinatge2(String destinatariLlinatge2) {
		this.destinatariLlinatge2 = destinatariLlinatge2;
	}
	public String getDestinatariRaoSocial() {
		return destinatariRaoSocial;
	}
	public void setDestinatariRaoSocial(String destinatariRaoSocial) {
		this.destinatariRaoSocial = destinatariRaoSocial;
	}
	public String getDestinatariNif() {
		return destinatariNif;
	}
	public void setDestinatariNif(String destinatariNif) {
		this.destinatariNif = destinatariNif;
	}
	public NotificaDto getNotifica() {
		return notifica;
	}
	public void setNotifica(NotificaDto notifica) {
		this.notifica = notifica;
	}
	public String getTitularEmail() {
		return titularEmail;
	}
	public void setTitularEmail(String titularEmail) {
		this.titularEmail = titularEmail;
	}
	public String getDestinatariEmail() {
		return destinatariEmail;
	}
	public void setDestinatariEmail(String destinatariEmail) {
		this.destinatariEmail = destinatariEmail;
	}
	public void setTitularNomLlinatge(String titularNomLlinatge) {
		this.titularNomLlinatge = titularNomLlinatge;
	}
	public String getTitularNomLlinatge() {
		titularNomLlinatge = concatenarNomLlinatges(
				getTitularLlinatges(),
				titularNom,
				titularRaoSocial,
				null);
		
		return titularNomLlinatge;
	}
	public String getDestinatariNomLlinatges() {
		destinatariNomLlinatges = concatenarNomLlinatges(
				getDestinatariLlinatges(),
				destinatariNom,
				destinatariRaoSocial,
				destinatariNif);
		
		return destinatariNomLlinatges;
	}
	public void setDestinatariNomLlinatges(String destinatariNomLlinatges) {
		this.destinatariNomLlinatges = destinatariNomLlinatges;
	}
	public String getTitularLlinatges() {
		return concatenarLlinatges(
				titularLlinatge1,
				titularLlinatge2);
	}
	public String getTitular() {
		StringBuilder sb = new StringBuilder();
		sb.append(titularNom);
		String llinatges = getTitularLlinatges();
		if (llinatges != null && !llinatges.isEmpty()) {
			sb.append(" ");
			sb.append(llinatges);
		}
		sb.append(" (");
		sb.append(titularNif);
		sb.append(")");
		return sb.toString();
	}

	public String getDestinatariLlinatges() {
		return concatenarLlinatges(
				destinatariLlinatge1,
				destinatariLlinatge2);
	}
	public String getDestinatari() {
		StringBuilder sb = new StringBuilder();
		sb.append(destinatariNom);
		String llinatges = getDestinatariLlinatges();
		if (llinatges != null && !llinatges.isEmpty()) {
			sb.append(" ");
			sb.append(llinatges);
		}
		sb.append(" (");
		sb.append(destinatariNif);
		sb.append(")");
		
		return sb.toString();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private String concatenarLlinatges(
			String llinatge1,
			String llinatge2) {
		if (llinatge1 == null && llinatge2 == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(llinatge1);
		if (llinatge2 != null && !llinatge2.isEmpty()) {
			sb.append(" ");
			sb.append(llinatge2);
		}
		return sb.toString();
	}
	
	private String concatenarNomLlinatges(
			String llinatges,
			String nom,
			String raoSocial,
			String destinatariNif) {
		StringBuilder sb = new StringBuilder();
		
		if (destinatariNif != null) {
			sb.append(destinatariNif);
			sb.append(" - ");
		}
		if (llinatges != null && !llinatges.isEmpty()) {
			sb.append("[");
			sb.append(llinatges);
		}
		
		if (nom != null && !nom.isEmpty()) {
			sb.append(", ");
			sb.append(nom);
			
			if (raoSocial == null) {
				sb.append("]");
			}
		}
		if (raoSocial != null && !raoSocial.isEmpty()) {
			sb.append(" | ");
			sb.append(raoSocial);
			sb.append("]");
		}
		return sb.toString();
	}

	private static final long serialVersionUID = -139254994389509932L;

}
