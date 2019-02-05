package es.caib.notib.core.api.dto;

import java.util.Date;

import es.caib.notib.core.api.ws.registre.RegistreModeFirmaEnum;
import es.caib.notib.core.api.ws.registre.RegistreOrigenEnum;
import es.caib.notib.core.api.ws.registre.RegistreTipusDocumentEnum;
import es.caib.notib.core.api.ws.registre.RegistreTipusDocumentalEnum;



/**
 * DTO amb informació d'un annex a una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreAnnexDto {

	private String nom;
	private Date data;
	private String idiomaCodi;
	private String arxiuNom;
	private byte[] arxiuContingut;
	
	private RegistreTipusDocumentEnum tipusDocument;
	private RegistreTipusDocumentalEnum tipusDocumental;
	private RegistreOrigenEnum origen;
	private RegistreModeFirmaEnum modeFirma;
	private String observacions;

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public String getIdiomaCodi() {
		return idiomaCodi;
	}
	public void setIdiomaCodi(String idiomaCodi) {
		this.idiomaCodi = idiomaCodi;
	}
	public String getArxiuNom() {
		return arxiuNom;
	}
	public void setArxiuNom(String arxiuNom) {
		this.arxiuNom = arxiuNom;
	}
	public byte[] getArxiuContingut() {
		return arxiuContingut;
	}
	public void setArxiuContingut(byte[] arxiuContingut) {
		this.arxiuContingut = arxiuContingut;
	}
	public RegistreTipusDocumentEnum getTipusDocument() {
		return tipusDocument;
	}
	public void setTipusDocument(
			RegistreTipusDocumentEnum tipusDocument) {
		this.tipusDocument = tipusDocument;
	}
	public RegistreTipusDocumentalEnum getTipusDocumental() {
		return tipusDocumental;
	}
	public void setTipusDocumental(
			RegistreTipusDocumentalEnum tipusDocumental) {
		this.tipusDocumental = tipusDocumental;
	}
	public RegistreOrigenEnum getOrigen() {
		return origen;
	}
	public void setOrigen(
			RegistreOrigenEnum origen) {
		this.origen = origen;
	}
	public RegistreModeFirmaEnum getModeFirma() {
		return modeFirma;
	}
	public void setModeFirma(
			RegistreModeFirmaEnum modeFirma) {
		this.modeFirma = modeFirma;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(
			String observacions) {
		this.observacions = observacions;
	}

}
