/**
 * 
 */
package es.caib.notib.core.api.dto;
import java.util.List;

/**
 * Informació d'un destinatari d'una anotació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificacioEnviamentDtoV2 extends AuditoriaDto {

	private Long id;
	private NotificacioDtoV2 notificacio;
	private PersonaDto titular;
	private List<PersonaDto> destinataris;
	private EntregaPostalDto entregaPostal;
	private EntregaDehDto entregaDeh;
	private ServeiTipusEnumDto serveiTipus;
	private String titularNomLlinatge;
	private String usuari;
	private String referencia;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public NotificacioDtoV2 getNotificacio() {
		return notificacio;
	}
	public void setNotificacio(NotificacioDtoV2 notificacio) {
		this.notificacio = notificacio;
	}
	public List<PersonaDto> getDestinataris() {
		return destinataris;
	}
	public void setDestinataris(List<PersonaDto> destinataris) {
		this.destinataris = destinataris;
	}
	public EntregaPostalDto getEntregaPostal() {
		return entregaPostal;
	}
	public void setEntregaPostal(EntregaPostalDto entregaPostal) {
		this.entregaPostal = entregaPostal;
	}
	public EntregaDehDto getEntregaDeh() {
		return entregaDeh;
	}
	public void setEntregaDeh(EntregaDehDto entregaDeh) {
		this.entregaDeh = entregaDeh;
	}
	public ServeiTipusEnumDto getServeiTipus() {
		return serveiTipus;
	}
	public void setServeiTipus(ServeiTipusEnumDto serveiTipus) {
		this.serveiTipus = serveiTipus;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public void setTitular(PersonaDto titular) {
		this.titular = titular;
	}
	public void setTitularNomLlinatge(String titularNomLlinatge) {
		this.titularNomLlinatge = titularNomLlinatge;
	}
	public String getUsuari() {
		return usuari;
	}
	public void setUsuari(String usuari) {
		this.usuari = usuari;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public String getTitularNomLlinatge() {
		titularNomLlinatge = concatenarNomLlinatges(
				getTitularLlinatges(),
				titular.getNom(),
				titular.getRaoSocial(),
				null);
		
		return titularNomLlinatge;
	}
	
	public String getTitularLlinatges() {
		return concatenarLlinatges(
				titular.getLlinatge1(),
				titular.getLlinatge2());
	}
	
	public String getTitular() {
		StringBuilder sb = new StringBuilder();
		sb.append(titular.getNom());
		String llinatges = getTitularLlinatges();
		if (llinatges != null && !llinatges.isEmpty()) {
			sb.append(" ");
			sb.append(llinatges);
		}
		sb.append(" (");
		sb.append(titular.getNif());
		sb.append(")");
		return sb.toString();
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
