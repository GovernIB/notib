package es.caib.notib.back.command;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Command per al manteniment d'enviaments de l'interfície rest.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificacioEnviamentCommand {
	
	private Long id;
	private Date dataEnviament;
	private NotificacioCommand notificacio;
	private String codiNotifica;
	private String codiProcediment;
	private String usuari;
	private String nifTitular;
	private String nomTitular;
	private String emailTitular;
	private String destinataris;
	private String llibreRegistre;
	private String numeroRegistre;
	private Date dataRegistre;
	private Date dataCaducitat;
	private String codiNotib;
	private String numeroCertCorreus;
	private String csv;
	private String estat;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getDataEnviament() {
		return dataEnviament;
	}
	public void setDataEnviament(Date dataEnviament) {
		this.dataEnviament = dataEnviament;
	}
	public String getCodiNotifica() {
		return codiNotifica;
	}
	public void setCodiNotifica(String codiNotifica) {
		this.codiNotifica = codiNotifica;
	}
	public String getCodiProcediment() {
		return codiProcediment;
	}
	public void setCodiProcediment(String codiProcediment) {
		this.codiProcediment = codiProcediment;
	}
	public String getUsuari() {
		return usuari;
	}
	public void setUsuari(String usuari) {
		this.usuari = usuari;
	}
	public String getNifTitular() {
		return nifTitular;
	}
	public void setNifTitular(String nifTitular) {
		this.nifTitular = nifTitular;
	}
	public String getNomTitular() {
		return nomTitular;
	}
	public void setNomTitular(String nomTitular) {
		this.nomTitular = nomTitular;
	}
	public String getEmailTitular() {
		return emailTitular;
	}
	public void setEmailTitular(String emailTitular) {
		this.emailTitular = emailTitular;
	}
	public String getDestinataris() {
		return destinataris;
	}
	public void setDestinataris(String destinataris) {
		this.destinataris = destinataris;
	}
	public String getLlibreRegistre() {
		return llibreRegistre;
	}
	public void setLlibreRegistre(String llibreRegistre) {
		this.llibreRegistre = llibreRegistre;
	}
	public String getNumeroRegistre() {
		return numeroRegistre;
	}
	public void setNumeroRegistre(String numeroRegistre) {
		this.numeroRegistre = numeroRegistre;
	}
	public Date getDataRegistre() {
		return dataRegistre;
	}
	public void setDataRegistre(Date dataRegistre) {
		this.dataRegistre = dataRegistre;
	}
	public Date getDataCaducitat() {
		return dataCaducitat;
	}
	public void setDataCaducitat(Date dataCaducitat) {
		this.dataCaducitat = dataCaducitat;
	}
	public String getCodiNotib() {
		return codiNotib;
	}
	public void setCodiNotib(String codiNotib) {
		this.codiNotib = codiNotib;
	}
	public String getNumeroCertCorreus() {
		return numeroCertCorreus;
	}
	public void setNumeroCertCorreus(String numeroCertCorreus) {
		this.numeroCertCorreus = numeroCertCorreus;
	}
	public String getCsv() {
		return csv;
	}
	public void setCsv(String csv) {
		this.csv = csv;
	}
	public String getEstat() {
		return estat;
	}
	public void setEstat(String estat) {
		this.estat = estat;
	}	
	public NotificacioCommand getNotificacio() {
		return notificacio;
	}
	public void setNotificacio(NotificacioCommand notificacio) {
		this.notificacio = notificacio;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
