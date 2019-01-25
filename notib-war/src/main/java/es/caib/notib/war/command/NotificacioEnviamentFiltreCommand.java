package es.caib.notib.war.command;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentFiltreDto;
import es.caib.notib.war.helper.ConversioTipusHelper;

public class NotificacioEnviamentFiltreCommand {
	
	private Long id;
	private Date dataEnviamentInici;
	private Date dataEnviamentFi;
	private Date dataProgramadaDisposicioInici;
	private Date dataProgramadaDisposicioFi;
	private String codiNotifica;
	private String codiProcediment;
	private String grup;
	private String usuari;
	private NotificaEnviamentTipusEnumDto tipusEnviament;
	private String concepte;
	private String descripcio;
	private String nifTitular;
	private String nomLlinatgeTitular;
	private String emailTitular;
	private String dir3Codi;
	private String destinataris;
	private String llibreRegistre;
	private String numeroRegistre;
	private Date dataRegistreInici;
	private Date dataRegistreFi;
	private Date dataCaducitatInici;
	private Date dataCaducitatFi;
	private String codiNotibEnviament;
	private String numeroCertCorreus;
	private String csvUuid;
	private String estat;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getDataEnviamentInici() {
		return dataEnviamentInici;
	}
	public void setDataEnviamentInici(Date dataEnviamentInici) {
		this.dataEnviamentInici = dataEnviamentInici;
	}
	public Date getDataEnviamentFi() {
		return dataEnviamentFi;
	}
	public void setDataEnviamentFi(Date dataEnviamentFi) {
		this.dataEnviamentFi = dataEnviamentFi;
	}
	public Date getDataProgramadaDisposicioInici() {
		return dataProgramadaDisposicioInici;
	}
	public void setDataProgramadaDisposicioInici(Date dataProgramadaDisposicioInici) {
		this.dataProgramadaDisposicioInici = dataProgramadaDisposicioInici;
	}
	public Date getDataProgramadaDisposicioFi() {
		return dataProgramadaDisposicioFi;
	}
	public void setDataProgramadaDisposicioFi(Date dataProgramadaDisposicioFi) {
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
	public NotificaEnviamentTipusEnumDto getTipusEnviament() {
		return tipusEnviament;
	}
	public void setTipusEnviament(NotificaEnviamentTipusEnumDto tipusEnviament) {
		this.tipusEnviament = tipusEnviament;
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
	public String getNomLlinatgeTitular() {
		return nomLlinatgeTitular;
	}
	public void setNomLlinatgeTitular(String nomLlinatgeTitular) {
		this.nomLlinatgeTitular = nomLlinatgeTitular;
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
	public Date getDataRegistreInici() {
		return dataRegistreInici;
	}
	public void setDataRegistreInici(Date dataRegistreInici) {
		this.dataRegistreInici = dataRegistreInici;
	}
	public Date getDataRegistreFi() {
		return dataRegistreFi;
	}
	public void setDataRegistreFi(Date dataRegistreFi) {
		this.dataRegistreFi = dataRegistreFi;
	}
	public Date getDataCaducitatInici() {
		return dataCaducitatInici;
	}
	public void setDataCaducitatInici(Date dataCaducitatInici) {
		this.dataCaducitatInici = dataCaducitatInici;
	}
	public Date getDataCaducitatFi() {
		return dataCaducitatFi;
	}
	public void setDataCaducitatFi(Date dataCaducitatFi) {
		this.dataCaducitatFi = dataCaducitatFi;
	}
	public String getCodiNotibEnviament() {
		return codiNotibEnviament;
	}
	public void setCodiNotibEnviament(String codiNotibEnviament) {
		this.codiNotibEnviament = codiNotibEnviament;
	}
	public String getNumeroCertCorreus() {
		return numeroCertCorreus;
	}
	public void setNumeroCertCorreus(String numeroCertCorreus) {
		this.numeroCertCorreus = numeroCertCorreus;
	}
	public String getCsvUuid() {
		return csvUuid;
	}
	public void setCsvUuid(String csvUuid) {
		this.csvUuid = csvUuid;
	}
	public String getEstat() {
		return estat;
	}
	public void setEstat(String estat) {
		this.estat = estat;
	}
	public String getDir3Codi() {
		return dir3Codi;
	}
	public void setDir3Codi(String dir3Codi) {
		this.dir3Codi = dir3Codi;
	}
	
	public static NotificacioEnviamentFiltreCommand asCommand(NotificacioEnviamentFiltreDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				NotificacioEnviamentFiltreCommand.class);
	}
	public static NotificacioEnviamentFiltreDto asDto(NotificacioEnviamentFiltreCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				NotificacioEnviamentFiltreDto.class);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
