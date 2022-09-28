package es.caib.notib.logic.intf.dto;

import java.util.Date;



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
	
	private RegistreTipusDocumentDtoEnum tipusDocument;
	private RegistreTipusDocumentalDtoEnum tipusDocumental;
	private RegistreOrigenDtoEnum origen;
	private RegistreModeFirmaDtoEnum modeFirma;
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
	public RegistreTipusDocumentDtoEnum getTipusDocument() {
		return tipusDocument;
	}
	public void setTipusDocument(RegistreTipusDocumentDtoEnum tipusDocument) {
		this.tipusDocument = tipusDocument;
	}
	public RegistreTipusDocumentalDtoEnum getTipusDocumental() {
		return tipusDocumental;
	}
	public void setTipusDocumental(RegistreTipusDocumentalDtoEnum tipusDocumental) {
		this.tipusDocumental = tipusDocumental;
	}
	public RegistreOrigenDtoEnum getOrigen() {
		return origen;
	}
	public void setOrigen(RegistreOrigenDtoEnum origen) {
		this.origen = origen;
	}
	public RegistreModeFirmaDtoEnum getModeFirma() {
		return modeFirma;
	}
	public void setModeFirma(RegistreModeFirmaDtoEnum modeFirma) {
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
