package es.caib.notib.core.api.service.rest;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@XmlRootElement
@JsonAutoDetect
public class InfoDatat {

	public String referencia;			// Referència interna
//	public List<String> estats;			// Llista del estats del datat
	public String descripcio;			// Descipció del datat
	public Date dataDatat;				// Data del datat
	public String estatActual;			// Estat actual del datat
	public String estatDescripcio;		// Descipció de l'estat del datat
	public Date dataActualitzacio;		// Data de la actualització del datat
	public String ncc; 					// Número de seguiment de correus
	public String respostaCodi;			// Code de tres dígits associat al resultat
	public String respostaDescripcio;	// Descripció de la operació
	
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public Date getDataDatat() {
		return dataDatat;
	}
	public void setDataDatat(Date dataDatat) {
		this.dataDatat = dataDatat;
	}
	public String getEstatActual() {
		return estatActual;
	}
	public void setEstatActual(String estatActual) {
		this.estatActual = estatActual;
	}
	public String getEstatDescripcio() {
		return estatDescripcio;
	}
	public void setEstatDescripcio(String estatDescripcio) {
		this.estatDescripcio = estatDescripcio;
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
	public String getRespostaCodi() {
		return respostaCodi;
	}
	public void setRespostaCodi(String respostaCodi) {
		this.respostaCodi = respostaCodi;
	}
	public String getRespostaDescripcio() {
		return respostaDescripcio;
	}
	public void setRespostaDescripcio(String respostaDescripcio) {
		this.respostaDescripcio = respostaDescripcio;
	}
	
}
