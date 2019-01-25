package es.caib.notib.core.api.dto;

import java.util.Date;

public class NotificaDto {
	
	private String notificaReferencia;
	private String notificaIdentificador;
	private Date notificaDataCreacio;
	private Date notificaDataDisposicio;
	private Date notificaDataCaducitat;
	private String notificaEmisorDir3;
	private String notificaEmisorDescripcio;
	private String notificaEmisorNif;
	private String notificaArrelDir3;
	private String notificaArrelDescripcio;
	private String notificaArrelNif;
	private NotificacioEnviamentEstatEnumDto notificaEstat;
	private Date notificaEstatData;
	private String notificaEstatDescripcio;
	private String notificaDatatOrigen;
	private String notificaDatatReceptorNif;
	private String notificaDatatReceptorNom;
	private String notificaDatatNumSeguiment;
	private String notificaDatatErrorDescripcio;
	private Date notificaCertificacioData;
	private String notificaCertificacioArxiuId;
	private String notificaCertificacioArxiuNom;
	private String notificaCertificacioHash;
	private String notificaCertificacioOrigen;
	private String notificaCertificacioMetadades;
	private String notificaCertificacioCsv;
	private String notificaCertificacioMime;
	private Integer notificaCertificacioTamany;
	private NotificaCertificacioTipusEnumDto notificaCertificacioTipus;
	private NotificaCertificacioArxiuTipusEnumDto notificaCertificacioArxiuTipus;
	private String notificaCertificacioNumSeguiment;
	private boolean notificaError;
	private Date notificaErrorData;
	private String notificaErrorDescripcio;
	
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
	public Date getNotificaDataCreacio() {
		return notificaDataCreacio;
	}
	public void setNotificaDataCreacio(Date notificaDataCreacio) {
		this.notificaDataCreacio = notificaDataCreacio;
	}
	public Date getNotificaDataDisposicio() {
		return notificaDataDisposicio;
	}
	public void setNotificaDataDisposicio(Date notificaDataDisposicio) {
		this.notificaDataDisposicio = notificaDataDisposicio;
	}
	public Date getNotificaDataCaducitat() {
		return notificaDataCaducitat;
	}
	public void setNotificaDataCaducitat(Date notificaDataCaducitat) {
		this.notificaDataCaducitat = notificaDataCaducitat;
	}
	public String getNotificaEmisorDir3() {
		return notificaEmisorDir3;
	}
	public void setNotificaEmisorDir3(String notificaEmisorDir3) {
		this.notificaEmisorDir3 = notificaEmisorDir3;
	}
	public String getNotificaEmisorDescripcio() {
		return notificaEmisorDescripcio;
	}
	public void setNotificaEmisorDescripcio(String notificaEmisorDescripcio) {
		this.notificaEmisorDescripcio = notificaEmisorDescripcio;
	}
	public String getNotificaEmisorNif() {
		return notificaEmisorNif;
	}
	public void setNotificaEmisorNif(String notificaEmisorNif) {
		this.notificaEmisorNif = notificaEmisorNif;
	}
	public String getNotificaArrelDir3() {
		return notificaArrelDir3;
	}
	public void setNotificaArrelDir3(String notificaArrelDir3) {
		this.notificaArrelDir3 = notificaArrelDir3;
	}
	public String getNotificaArrelDescripcio() {
		return notificaArrelDescripcio;
	}
	public void setNotificaArrelDescripcio(String notificaArrelDescripcio) {
		this.notificaArrelDescripcio = notificaArrelDescripcio;
	}
	public String getNotificaArrelNif() {
		return notificaArrelNif;
	}
	public void setNotificaArrelNif(String notificaArrelNif) {
		this.notificaArrelNif = notificaArrelNif;
	}
	public NotificacioEnviamentEstatEnumDto getNotificaEstat() {
		return notificaEstat;
	}
	public void setNotificaEstat(NotificacioEnviamentEstatEnumDto notificaEstat) {
		this.notificaEstat = notificaEstat;
	}
	public Date getNotificaEstatData() {
		return notificaEstatData;
	}
	public void setNotificaEstatData(Date notificaEstatData) {
		this.notificaEstatData = notificaEstatData;
	}
	public String getNotificaEstatDescripcio() {
		return notificaEstatDescripcio;
	}
	public void setNotificaEstatDescripcio(String notificaEstatDescripcio) {
		this.notificaEstatDescripcio = notificaEstatDescripcio;
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
	public String getNotificaDatatErrorDescripcio() {
		return notificaDatatErrorDescripcio;
	}
	public void setNotificaDatatErrorDescripcio(String notificaDatatErrorDescripcio) {
		this.notificaDatatErrorDescripcio = notificaDatatErrorDescripcio;
	}
	public Date getNotificaCertificacioData() {
		return notificaCertificacioData;
	}
	public void setNotificaCertificacioData(Date notificaCertificacioData) {
		this.notificaCertificacioData = notificaCertificacioData;
	}
	public String getNotificaCertificacioArxiuId() {
		return notificaCertificacioArxiuId;
	}
	public void setNotificaCertificacioArxiuId(String notificaCertificacioArxiuId) {
		this.notificaCertificacioArxiuId = notificaCertificacioArxiuId;
	}
	public String getNotificaCertificacioArxiuNom() {
		return notificaCertificacioArxiuNom;
	}
	public void setNotificaCertificacioArxiuNom(String notificaCertificacioArxiuNom) {
		this.notificaCertificacioArxiuNom = notificaCertificacioArxiuNom;
	}
	public String getNotificaCertificacioHash() {
		return notificaCertificacioHash;
	}
	public void setNotificaCertificacioHash(String notificaCertificacioHash) {
		this.notificaCertificacioHash = notificaCertificacioHash;
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
	public String getNotificaCertificacioMime() {
		return notificaCertificacioMime;
	}
	public void setNotificaCertificacioMime(String notificaCertificacioMime) {
		this.notificaCertificacioMime = notificaCertificacioMime;
	}
	public Integer getNotificaCertificacioTamany() {
		return notificaCertificacioTamany;
	}
	public void setNotificaCertificacioTamany(Integer notificaCertificacioTamany) {
		this.notificaCertificacioTamany = notificaCertificacioTamany;
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
	public boolean isNotificaError() {
		return notificaError;
	}
	public void setNotificaError(boolean notificaError) {
		this.notificaError = notificaError;
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
	
}
