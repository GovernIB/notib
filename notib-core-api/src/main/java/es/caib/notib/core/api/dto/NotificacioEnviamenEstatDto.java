/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació sobre l'estat d'un enviament d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificacioEnviamenEstatDto {

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
	private String seuRegistreNumero;
	private Date seuRegistreData;
	private Date seuDataFi;
	private SeuEstatEnumDto seuEstat;
	private boolean seuError;
	private Date seuErrorData;
	private String seuErrorDescripcio;
	private Date seuDataEnviament;
	private int seuReintentsEnviament;
	private Date seuDataEstat;
	private Date seuDataNotificaInformat;
	private Date seuDataNotificaDarreraPeticio;

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
	public String getSeuRegistreNumero() {
		return seuRegistreNumero;
	}
	public void setSeuRegistreNumero(String seuRegistreNumero) {
		this.seuRegistreNumero = seuRegistreNumero;
	}
	public Date getSeuRegistreData() {
		return seuRegistreData;
	}
	public void setSeuRegistreData(Date seuRegistreData) {
		this.seuRegistreData = seuRegistreData;
	}
	public Date getSeuDataFi() {
		return seuDataFi;
	}
	public void setSeuDataFi(Date seuDataFi) {
		this.seuDataFi = seuDataFi;
	}
	public SeuEstatEnumDto getSeuEstat() {
		return seuEstat;
	}
	public void setSeuEstat(SeuEstatEnumDto seuEstat) {
		this.seuEstat = seuEstat;
	}
	public boolean isSeuError() {
		return seuError;
	}
	public void setSeuError(boolean seuError) {
		this.seuError = seuError;
	}
	public Date getSeuErrorData() {
		return seuErrorData;
	}
	public void setSeuErrorData(Date seuErrorData) {
		this.seuErrorData = seuErrorData;
	}
	public String getSeuErrorDescripcio() {
		return seuErrorDescripcio;
	}
	public void setSeuErrorDescripcio(String seuErrorDescripcio) {
		this.seuErrorDescripcio = seuErrorDescripcio;
	}
	public Date getSeuDataEnviament() {
		return seuDataEnviament;
	}
	public void setSeuDataEnviament(Date seuDataEnviament) {
		this.seuDataEnviament = seuDataEnviament;
	}
	public int getSeuReintentsEnviament() {
		return seuReintentsEnviament;
	}
	public void setSeuReintentsEnviament(int seuReintentsEnviament) {
		this.seuReintentsEnviament = seuReintentsEnviament;
	}
	public Date getSeuDataEstat() {
		return seuDataEstat;
	}
	public void setSeuDataEstat(Date seuDataEstat) {
		this.seuDataEstat = seuDataEstat;
	}
	public Date getSeuDataNotificaInformat() {
		return seuDataNotificaInformat;
	}
	public void setSeuDataNotificaInformat(Date seuDataNotificaInformat) {
		this.seuDataNotificaInformat = seuDataNotificaInformat;
	}
	public Date getSeuDataNotificaDarreraPeticio() {
		return seuDataNotificaDarreraPeticio;
	}
	public void setSeuDataNotificaDarreraPeticio(Date seuDataNotificaDarreraPeticio) {
		this.seuDataNotificaDarreraPeticio = seuDataNotificaDarreraPeticio;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
