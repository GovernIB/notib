/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació sobre l'estat de Notifica d'un destinatari de
 * la notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificaRespostaEstatDto extends NotificaRespostaDto {

	private NotificacioEnviamentEstatEnumDto estat;
	private Date estatData;
	private String estatDescripcio;
	private String datatOrigen;
	private String datatReceptorNif;
	private String datatReceptorNom;
	private String datatNumSeguiment;
	private String datatErrorDescripcio;
	private Date certificacioData;
	private String certificacioArxiuId;
	private String certificacioHash;
	private String certificacioOrigen;
	private String certificacioMetadades;
	private String certificacioCsv;
	private String certificacioMime;
	private Integer certificacioTamany;
	private NotificaCertificacioTipusEnumDto certificacioTipus;
	private NotificaCertificacioArxiuTipusEnumDto certificacioArxiuTipus;
	private String certificacioNumSeguiment;

	public NotificacioEnviamentEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(NotificacioEnviamentEstatEnumDto estat) {
		this.estat = estat;
	}
	public Date getEstatData() {
		return estatData;
	}
	public void setEstatData(Date estatData) {
		this.estatData = estatData;
	}
	public String getEstatDescripcio() {
		return estatDescripcio;
	}
	public void setEstatDescripcio(String estatDescripcio) {
		this.estatDescripcio = estatDescripcio;
	}
	public String getDatatOrigen() {
		return datatOrigen;
	}
	public void setDatatOrigen(String datatOrigen) {
		this.datatOrigen = datatOrigen;
	}
	public String getDatatReceptorNif() {
		return datatReceptorNif;
	}
	public void setDatatReceptorNif(String datatReceptorNif) {
		this.datatReceptorNif = datatReceptorNif;
	}
	public String getDatatReceptorNom() {
		return datatReceptorNom;
	}
	public void setDatatReceptorNom(String datatReceptorNom) {
		this.datatReceptorNom = datatReceptorNom;
	}
	public String getDatatNumSeguiment() {
		return datatNumSeguiment;
	}
	public void setDatatNumSeguiment(String datatNumSeguiment) {
		this.datatNumSeguiment = datatNumSeguiment;
	}
	public String getDatatErrorDescripcio() {
		return datatErrorDescripcio;
	}
	public void setDatatErrorDescripcio(String datatErrorDescripcio) {
		this.datatErrorDescripcio = datatErrorDescripcio;
	}
	public Date getCertificacioData() {
		return certificacioData;
	}
	public void setCertificacioData(Date certificacioData) {
		this.certificacioData = certificacioData;
	}
	public String getCertificacioArxiuId() {
		return certificacioArxiuId;
	}
	public void setCertificacioArxiuId(String certificacioArxiuId) {
		this.certificacioArxiuId = certificacioArxiuId;
	}
	public String getCertificacioHash() {
		return certificacioHash;
	}
	public void setCertificacioHash(String certificacioHash) {
		this.certificacioHash = certificacioHash;
	}
	public String getCertificacioOrigen() {
		return certificacioOrigen;
	}
	public void setCertificacioOrigen(String certificacioOrigen) {
		this.certificacioOrigen = certificacioOrigen;
	}
	public String getCertificacioMetadades() {
		return certificacioMetadades;
	}
	public void setCertificacioMetadades(String certificacioMetadades) {
		this.certificacioMetadades = certificacioMetadades;
	}
	public String getCertificacioCsv() {
		return certificacioCsv;
	}
	public void setCertificacioCsv(String certificacioCsv) {
		this.certificacioCsv = certificacioCsv;
	}
	public String getCertificacioMime() {
		return certificacioMime;
	}
	public void setCertificacioMime(String certificacioMime) {
		this.certificacioMime = certificacioMime;
	}
	public Integer getCertificacioTamany() {
		return certificacioTamany;
	}
	public void setCertificacioTamany(Integer certificacioTamany) {
		this.certificacioTamany = certificacioTamany;
	}
	public NotificaCertificacioTipusEnumDto getCertificacioTipus() {
		return certificacioTipus;
	}
	public void setCertificacioTipus(NotificaCertificacioTipusEnumDto certificacioTipus) {
		this.certificacioTipus = certificacioTipus;
	}
	public NotificaCertificacioArxiuTipusEnumDto getCertificacioArxiuTipus() {
		return certificacioArxiuTipus;
	}
	public void setCertificacioArxiuTipus(NotificaCertificacioArxiuTipusEnumDto certificacioArxiuTipus) {
		this.certificacioArxiuTipus = certificacioArxiuTipus;
	}
	public String getCertificacioNumSeguiment() {
		return certificacioNumSeguiment;
	}
	public void setCertificacioNumSeguiment(String certificacioNumSeguiment) {
		this.certificacioNumSeguiment = certificacioNumSeguiment;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
