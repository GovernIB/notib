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
	private String destinatarisNomLlinatges;
	private String titularEmail;
	private String titularNif;
	private String usuari;
	private String referencia;
	private String notificaIdentificador;
	private String numeroCertCorreus;
	private String csvUuid;
	private String csv;
	private String uuid;
	private String notificaCertificacioNumSeguiment;
	private String detalls;
	
	
	
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
	public String getNotificaIdentificador() {
		return notificaIdentificador;
	}
	public void setNotificaIdentificador(String notificaCodi) {
		this.notificaIdentificador = notificaCodi;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public String getTitularEmail() {
		return titularEmail;
	}
	public void setTitularEmail(String titularEmail) {
		this.titularEmail = titularEmail;
	}
	public String getTitularNif() {
		return titularNif;
	}
	public void setTitularNif(String titularNif) {
		this.titularNif = titularNif;
	}
	public String getDestinatarisNomLlinatges() {
		destinatarisNomLlinatges = "";
		for(PersonaDto destinatari: destinataris) {
			destinatarisNomLlinatges += concatenarNomLlinatges(llinatgesDestinatari(destinatari), destinatari.getNom(), destinatari.getRaoSocial(), null)+"</br>";
		}
		return destinatarisNomLlinatges;
	}
	public void setDestinatarisNomLlinatges(String destinatarisNomLlinatge) {
		this.destinatarisNomLlinatges = destinatarisNomLlinatge;
	}
	public String getNumeroCertCorreus() {
		return numeroCertCorreus;
	}
	public void setNumeroCertCorreus(String numeroCertCorreus) {
		this.numeroCertCorreus = numeroCertCorreus;
	}
	public String getCsvUuid() {
		if(notificacio.getDocument().getUuid() != null) {
			this.setCsvUuid(notificacio.getDocument().getUuid());
		}
		if(notificacio.getDocument().getCsv() != null) {
			this.setCsvUuid(notificacio.getDocument().getCsv());
		}
		return this.csvUuid;
	}
	public void setCsvUuid(String csvUuid) {
		this.csvUuid = csvUuid;
	}
	public String getTitularNomLlinatge() {
		if(this.titularNomLlinatge != null) {
			return this.titularNomLlinatge;
		}else {
			if(titular != null) {
				titularNomLlinatge = concatenarNomLlinatges(
						getTitularLlinatges(),
						titular.getNom(),
						titular.getRaoSocial(),
						null);
			}
			return titularNomLlinatge;
		}
	}
	
	public String getTitularLlinatges() {
		return concatenarLlinatges(
				titular.getLlinatge1(),
				titular.getLlinatge2());
	}
	
	public String llinatgesDestinatari(PersonaDto destinatari) {
		return concatenarLlinatges(
				destinatari.getLlinatge1(),
				destinatari.getLlinatge2());
	}
	
//	public String getTitular() {
//		StringBuilder sb = new StringBuilder();
//		sb.append(titular.getNom());
//		String llinatges = getTitularLlinatges();
//		if (llinatges != null && !llinatges.isEmpty()) {
//			sb.append(" ");
//			sb.append(llinatges);
//		}
//		sb.append(" (");
//		sb.append(titular.getNif());
//		sb.append(")");
//		return sb.toString();
//	}
	
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
	
	public PersonaDto getTitular() {
		return titular;
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

	public String getCsv() {
		return csv;
	}
	public void setCsv(String csv) {
		this.csv = csv;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getNotificaCertificacioNumSeguiment() {
		return notificaCertificacioNumSeguiment;
	}
	public void setNotificaCertificacioNumSeguiment(String notificaCertificacioNumSeguiment) {
		this.notificaCertificacioNumSeguiment = notificaCertificacioNumSeguiment;
	}
	public String getDetalls() {
		return detalls;
	}
	public void setDetalls(String detalls) {
		this.detalls = detalls;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
