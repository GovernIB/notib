/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'un destinatari d'una anotació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificacioEnviamentDto extends AuditoriaDto {

	private Long id;
	private NotificacioDto notificacio;
	private PersonaDto titular;
	private List<PersonaDto> destinataris;
	private EntregaPostalDto entregaPostal;
	private EntregaDehDto entregaDeh;
	private ServeiTipusEnumDto serveiTipus;
	private String titularNomLlinatge;
	private String referencia;
	private String usuari;
	private Date notificaErrorData;
	private String notificaErrorDescripcio;
	private String notificaCertificacioArxiuNom;
	private Date notificaCertificacioData;
	private NotificacioEnviamentEstatEnumDto notificaEstat;
	private Date notificaEstatData;
	private String notificaDatatErrorDescripcio;
	private String notificaDatatOrigen;
	private String notificaDatatReceptorNif;
	private String notificaDatatReceptorNom;
	private String notificaDatatNumSeguiment;
	private String notificaCertificacioMime;
	private String notificaCertificacioOrigen;
	private String notificaCertificacioMetadades;
	private String notificaCertificacioCsv;
	private String notificaReferencia;
	private String notificaIdentificador;
	private NotificaCertificacioTipusEnumDto notificaCertificacioTipus;
	private NotificaCertificacioArxiuTipusEnumDto notificaCertificacioArxiuTipus;
	private String notificaCertificacioNumSeguiment;
	
	private Date registreData;
	private String registreNumeroFormatat;
	private NotificacioRegistreEstatEnumDto registreEstat;
	
	public NotificacioRegistreEstatEnumDto getRegistreEstat() {
		return registreEstat;
	}
	public void setRegistreEstat(NotificacioRegistreEstatEnumDto registreEstat) {
		this.registreEstat = registreEstat;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public NotificacioDto getNotificacio() {
		return notificacio;
	}
	public void setNotificacio(NotificacioDto notificacio) {
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
	public String getTitularNomLlinatge() {
		return titularNomLlinatge;
	}
	public void setTitularNomLlinatge(String titularNomLlinatge) {
		this.titularNomLlinatge = titularNomLlinatge;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public void setTitular(PersonaDto titular) {
		this.titular = titular;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public String getUsuari() {
		return usuari;
	}
	public void setUsuari(String usuari) {
		this.usuari = usuari;
	}
	public Date getNotificaErrorData() {
		return notificaErrorData;
	}
	public void setNotificaErrorData(Date notificaErrorData) {
		this.notificaErrorData = notificaErrorData;
	}
	public String getNotificaErrorDescripcio() {
		return notificaErrorDescripcio;
	}
	public void setNotificaErrorDescripcio(String notificaErrorDescripcio) {
		this.notificaErrorDescripcio = notificaErrorDescripcio;
	}
	public String getNotificaCertificacioArxiuNom() {
		return notificaCertificacioArxiuNom;
	}
	public void setNotificaCertificacioArxiuNom(String notificaCertificacioArxiuNom) {
		this.notificaCertificacioArxiuNom = notificaCertificacioArxiuNom;
	}
	public Date getNotificaCertificacioData() {
		return notificaCertificacioData;
	}
	public String getNotificaReferencia() {
		return notificaReferencia;
	}
	public void setNotificaReferencia(String notificaReferencia) {
		this.notificaReferencia = notificaReferencia;
	}
	public String getNotificaIdentificador() {
		return notificaIdentificador;
	}
	public void setNotificaIdentificador(String notificaIdentificador) {
		this.notificaIdentificador = notificaIdentificador;
	}
	public void setNotificaCertificacioData(Date notificaCertificacioData) {
		this.notificaCertificacioData = notificaCertificacioData;
	}
	public NotificacioEnviamentEstatEnumDto getNotificaEstat() {
		return notificaEstat;
	}
	public Date getNotificaEstatData() {
		return notificaEstatData;
	}
	public void setNotificaEstatData(Date notificaEstatData) {
		this.notificaEstatData = notificaEstatData;
	}
	public void setNotificaEstat(NotificacioEnviamentEstatEnumDto notificaEstat) {
		this.notificaEstat = notificaEstat;
	}
	public String getNotificaDatatErrorDescripcio() {
		return notificaDatatErrorDescripcio;
	}
	public void setNotificaDatatErrorDescripcio(String notificaDatatErrorDescripcio) {
		this.notificaDatatErrorDescripcio = notificaDatatErrorDescripcio;
	}
	public String getNotificaDatatOrigen() {
		return notificaDatatOrigen;
	}
	public void setNotificaDatatOrigen(String notificaDatatOrigen) {
		this.notificaDatatOrigen = notificaDatatOrigen;
	}
	public String getNotificaDatatReceptorNif() {
		return notificaDatatReceptorNif;
	}
	public void setNotificaDatatReceptorNif(String notificaDatatReceptorNif) {
		this.notificaDatatReceptorNif = notificaDatatReceptorNif;
	}
	public String getNotificaDatatReceptorNom() {
		return notificaDatatReceptorNom;
	}
	public void setNotificaDatatReceptorNom(String notificaDatatReceptorNom) {
		this.notificaDatatReceptorNom = notificaDatatReceptorNom;
	}
	public String getNotificaDatatNumSeguiment() {
		return notificaDatatNumSeguiment;
	}
	public void setNotificaDatatNumSeguiment(String notificaDatatNumSeguiment) {
		this.notificaDatatNumSeguiment = notificaDatatNumSeguiment;
	}
	public String getNotificaCertificacioMime() {
		return notificaCertificacioMime;
	}
	public void setNotificaCertificacioMime(String notificaCertificacioMime) {
		this.notificaCertificacioMime = notificaCertificacioMime;
	}
	public String getNotificaCertificacioOrigen() {
		return notificaCertificacioOrigen;
	}
	public void setNotificaCertificacioOrigen(String notificaCertificacioOrigen) {
		this.notificaCertificacioOrigen = notificaCertificacioOrigen;
	}
	public String getNotificaCertificacioMetadades() {
		return notificaCertificacioMetadades;
	}
	public void setNotificaCertificacioMetadades(String notificaCertificacioMetadades) {
		this.notificaCertificacioMetadades = notificaCertificacioMetadades;
	}
	public String getNotificaCertificacioCsv() {
		return notificaCertificacioCsv;
	}
	public void setNotificaCertificacioCsv(String notificaCertificacioCsv) {
		this.notificaCertificacioCsv = notificaCertificacioCsv;
	}
	public NotificaCertificacioTipusEnumDto getNotificaCertificacioTipus() {
		return notificaCertificacioTipus;
	}
	public void setNotificaCertificacioTipus(NotificaCertificacioTipusEnumDto notificaCertificacioTipus) {
		this.notificaCertificacioTipus = notificaCertificacioTipus;
	}
	public NotificaCertificacioArxiuTipusEnumDto getNotificaCertificacioArxiuTipus() {
		return notificaCertificacioArxiuTipus;
	}
	public void setNotificaCertificacioArxiuTipus(NotificaCertificacioArxiuTipusEnumDto notificaCertificacioArxiuTipus) {
		this.notificaCertificacioArxiuTipus = notificaCertificacioArxiuTipus;
	}
	public String getNotificaCertificacioNumSeguiment() {
		return notificaCertificacioNumSeguiment;
	}
	public void setNotificaCertificacioNumSeguiment(String notificaCertificacioNumSeguiment) {
		this.notificaCertificacioNumSeguiment = notificaCertificacioNumSeguiment;
	}
	public Date getRegistreData() {
		return registreData;
	}
	public void setRegistreData(Date registreData) {
		this.registreData = registreData;
	}
	public String getRegistreNumeroFormatat() {
		return registreNumeroFormatat;
	}
	public void setRegistreNumeroFormatat(String registreNumeroFormatat) {
		this.registreNumeroFormatat = registreNumeroFormatat;
	}
	public String getTitularLlinatges() {
		return concatenarLlinatges(
				titular.getLlinatge1(),
				titular.getLlinatge2());
	}
	public String getTitularNomLlinatges() {
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
	
	public PersonaDto getTitular() {
		return this.titular;
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

	private static final long serialVersionUID = -139254994389509932L;

}
