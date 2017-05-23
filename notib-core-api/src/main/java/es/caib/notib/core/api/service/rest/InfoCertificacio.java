package es.caib.notib.core.api.service.rest;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@XmlRootElement
@JsonAutoDetect
public class InfoCertificacio {

	public String referencia;			// Referència interna
	public String certificatPdf;		// Certificat PDF en base 64
	public String certificatXml;		// Certificat XML en base 64
	public String certificacioTipus;	// Tipus d'acús o sobre
	public Date dataActualitzacio;		// Data d'arribada de l'acús
	public String ncc; 					// Número de seguiment de correus
//	public String respostaCodi;			// Code de tres dígits associat al resultat
//	public String respostaDescripcio;	// Descripció de la operació
	
	
	public InfoCertificacio(
			String referencia,
			String certificatPdf,
			String certificatXml,
			String certificacioTipus,
			Date dataActualitzacio,
			String ncc
//			String respostaCodi,
//			String respostaDescripcio
			) {
		
		super();
		this.referencia = referencia;
		this.certificatPdf = certificatPdf;
		this.certificatXml = certificatXml;
		this.certificacioTipus = certificacioTipus;
		this.dataActualitzacio = dataActualitzacio;
		this.ncc = ncc;
//		this.respostaCodi = respostaCodi;
//		this.respostaDescripcio = respostaDescripcio;
	}
	
	
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	
	public String getCertificatPdf() {
		return certificatPdf;
	}
	public void setCertificatPdf(String certificatPdf) {
		this.certificatPdf = certificatPdf;
	}
	
	public String getCertificatXml() {
		return certificatXml;
	}
	public void setCertificatXml(String certificatXml) {
		this.certificatXml = certificatXml;
	}
	
	public String getCertificacioTipus() {
		return certificacioTipus;
	}
	public void setCertificacioTipus(String certificacioTipus) {
		this.certificacioTipus = certificacioTipus;
	}
	
	public Date getDataActualitzacio() {
		return dataActualitzacio;
	}
	public void setDataActualitzacio(Date dataActualitzacio) {
		this.dataActualitzacio = dataActualitzacio;
	}
	
	public String getNcc() {
		return ncc;
	}
	public void setNcc(String ncc) {
		this.ncc = ncc;
	}
	
//	public String getRespostaCodi() {
//		return respostaCodi;
//	}
//	public void setRespostaCodi(String respostaCodi) {
//		this.respostaCodi = respostaCodi;
//	}
//	
//	public String getRespostaDescripcio() {
//		return respostaDescripcio;
//	}
//	public void setRespostaDescripcio(String respostaDescripcio) {
//		this.respostaDescripcio = respostaDescripcio;
//	}
	
	
}
