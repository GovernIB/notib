package es.caib.notib.core.api.service.rest;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@XmlRootElement
@JsonAutoDetect
public class InfoEstat {

	public String referencia;			// Referència interna
	public String estat;				// Estat de l'enviament
	public String ncc; 					// Número de seguiment de correus
//	public String respostaCodi;			// Code de tres dígits associat al resultat
//	public String respostaDescripcio;	// Descripció de la operació
	
	
	public InfoEstat(
			String referencia,
			String estat,
			String ncc
//			String respostaCodi,
//			String respostaDescripcio
			) {
		
		super();
		this.referencia = referencia;
		this.estat = estat;
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
	
	public String getEstat() {
		return estat;
	}
	public void setEstat(String estat) {
		this.estat = estat;
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
