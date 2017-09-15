/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació de la resposta de Notifica a una consulta d'estat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificaRespostaEstatDto extends NotificaRespostaDto {

	private Date data;
	private String estatCodi;
	private String estatDescripcio;
	private String numSeguiment;
	private boolean certificacioDisponible;
	private String certificacioContingut;
	private String certificacioHash;
	private String certificacioCsv;
	private int certificacioTamany;
	private Date certificacioData;
	private String certificacioOrigen;
	private String certificacioMetadades;
	private String certificacioTipusMime;

	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public String getEstatCodi() {
		return estatCodi;
	}
	public void setEstatCodi(String estatCodi) {
		this.estatCodi = estatCodi;
	}
	public String getEstatDescripcio() {
		return estatDescripcio;
	}
	public void setEstatDescripcio(String estatDescripcio) {
		this.estatDescripcio = estatDescripcio;
	}
	public String getNumSeguiment() {
		return numSeguiment;
	}
	public void setNumSeguiment(String numSeguiment) {
		this.numSeguiment = numSeguiment;
	}
	public boolean isCertificacioDisponible() {
		return certificacioDisponible;
	}
	public void setCertificacioDisponible(boolean certificacioDisponible) {
		this.certificacioDisponible = certificacioDisponible;
	}
	public String getCertificacioContingut() {
		return certificacioContingut;
	}
	public void setCertificacioContingut(String certificacioContingut) {
		this.certificacioContingut = certificacioContingut;
	}
	public String getCertificacioHash() {
		return certificacioHash;
	}
	public void setCertificacioHash(String certificacioHash) {
		this.certificacioHash = certificacioHash;
	}
	public String getCertificacioCsv() {
		return certificacioCsv;
	}
	public void setCertificacioCsv(String certificacioCsv) {
		this.certificacioCsv = certificacioCsv;
	}
	public int getCertificacioTamany() {
		return certificacioTamany;
	}
	public void setCertificacioTamany(int certificacioTamany) {
		this.certificacioTamany = certificacioTamany;
	}
	public Date getCertificacioData() {
		return certificacioData;
	}
	public void setCertificacioData(Date certificacioData) {
		this.certificacioData = certificacioData;
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
	public String getCertificacioTipusMime() {
		return certificacioTipusMime;
	}
	public void setCertificacioTipusMime(String certificacioTipusMime) {
		this.certificacioTipusMime = certificacioTipusMime;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
