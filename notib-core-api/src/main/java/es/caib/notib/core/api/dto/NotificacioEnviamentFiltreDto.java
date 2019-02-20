/**
 * 
 */
package es.caib.notib.core.api.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'un destinatari d'una anotació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificacioEnviamentFiltreDto extends AuditoriaDto {

	private Long id;
	private String dataEnviamentInici;
	private String dataEnviamentFi;
	private String dataProgramadaDisposicioInici;
	private String dataProgramadaDisposicioFi;
	private String codiNotifica;
	private String codiProcediment;
	private String grup;
	private String usuari;
	private NotificacioTipusEnviamentEnumDto enviamentTipus;
	private String concepte;
	private String descripcio;
	private String nifTitular;
	private String nomTitular;
	private String emailTitular;
	private String destinataris;
	private String llibreRegistre;
	private String numeroRegistre;
	private String dataRegistreInici;
	private String dataRegistreFi;
	private String dataCaducitatInici;
	private String dataCaducitatFi;
	private String codiNotib;
	private String numeroCertCorreus;
	private String csv;
	private NotificacioEstatEnumDto estat;
	private String dir3Codi;
	private String titularNomLlinatge;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDataEnviamentInici() {
		return dataEnviamentInici;
	}
	public void setDataEnviamentInici(String dataEnviamentInici) {
		this.dataEnviamentInici = dataEnviamentInici;
	}
	public String getDataEnviamentFi() {
		return dataEnviamentFi;
	}
	public void setDataEnviamentFi(String dataEnviamentFi) {
		this.dataEnviamentFi = dataEnviamentFi;
	}
	public String getDataProgramadaDisposicioInici() {
		return dataProgramadaDisposicioInici;
	}
	public void setDataProgramadaDisposicioInici(String dataProgramadaDisposicioInici) {
		this.dataProgramadaDisposicioInici = dataProgramadaDisposicioInici;
	}
	public String getDataProgramadaDisposicioFi() {
		return dataProgramadaDisposicioFi;
	}
	public void setDataProgramadaDisposicioFi(String dataProgramadaDisposicioFi) {
		this.dataProgramadaDisposicioFi = dataProgramadaDisposicioFi;
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
	public String getGrup() {
		return grup;
	}
	public void setGrup(String grup) {
		this.grup = grup;
	}
	public String getUsuari() {
		return usuari;
	}
	public void setUsuari(String usuari) {
		this.usuari = usuari;
	}
	public NotificacioTipusEnviamentEnumDto getEnviamentTipus() {
		return enviamentTipus;
	}
	public void setEnviamentTipus(NotificacioTipusEnviamentEnumDto enviamentTipus) {
		this.enviamentTipus = enviamentTipus;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getConcepte() {
		return concepte;
	}
	public void setConcepte(String concepte) {
		this.concepte = concepte;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
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
	public String getDataRegistreInici() {
		return dataRegistreInici;
	}
	public void setDataRegistreInici(String dataRegistreInici) {
		this.dataRegistreInici = dataRegistreInici;
	}
	public String getDataRegistreFi() {
		return dataRegistreFi;
	}
	public void setDataRegistreFi(String dataRegistreFi) {
		this.dataRegistreFi = dataRegistreFi;
	}
	public String getDataCaducitatInici() {
		return dataCaducitatInici;
	}
	public void setDataCaducitatInici(String dataCaducitatInici) {
		this.dataCaducitatInici = dataCaducitatInici;
	}
	public String getDataCaducitatFi() {
		return dataCaducitatFi;
	}
	public void setDataCaducitatFi(String dataCaducitatFi) {
		this.dataCaducitatFi = dataCaducitatFi;
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
	public NotificacioEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(NotificacioEstatEnumDto estat) {
		this.estat = estat;
	}
	public String getDir3Codi() {
		return dir3Codi;
	}
	public void setDir3Codi(String dir3Codi) {
		this.dir3Codi = dir3Codi;
	}
	public String getTitularNomLlinatge() {
		return titularNomLlinatge;
	}
	public void setTitularNomLlinatge(String titularNomLlinatge) {
		this.titularNomLlinatge = titularNomLlinatge;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	private static final long serialVersionUID = -139254994389509932L;

}
