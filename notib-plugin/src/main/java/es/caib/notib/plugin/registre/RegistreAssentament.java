package es.caib.notib.plugin.registre;

import java.util.List;

public class RegistreAssentament {

	private String organisme;
	private String oficina;
	private String llibre;
	private String extracte;
	private String assumpteTipus;
	private String assumpteCodi;
	private String idioma;
	private List<RegistreAssentamentInteressat> interessats;
	private DocumentRegistre_llorenc document;
	
	private String documentacioFisicaCodi;
	
	
	
	public String getOrganisme() {
		return organisme;
	}
	public void setOrganisme(String organisme) {
		this.organisme = organisme;
	}
	
	public String getOficina() {
		return oficina;
	}
	public void setOficina(String oficina) {
		this.oficina = oficina;
	}
	
	public String getLlibre() {
		return llibre;
	}
	public void setLlibre(String llibre) {
		this.llibre = llibre;
	}
	
	public String getExtracte() {
		return extracte;
	}
	public void setExtracte(String extracte) {
		this.extracte = extracte;
	}
	
	public String getAssumpteTipus() {
		return assumpteTipus;
	}
	public void setAssumpteTipus(String assumpteTipus) {
		this.assumpteTipus = assumpteTipus;
	}
	
	public String getAssumpteCodi() {
		return assumpteCodi;
	}
	public void setAssumpteCodi(String assumpteCodi) {
		this.assumpteCodi = assumpteCodi;
	}
	
	public String getIdioma() {
		return idioma;
	}
	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}
	
	public List<RegistreAssentamentInteressat> getInteressats() {
		return interessats;
	}
	public void setInteressats(List<RegistreAssentamentInteressat> interessats) {
		this.interessats = interessats;
	}
	
	public DocumentRegistre_llorenc getDocument() {
		return document;
	}
	public void setDocument(DocumentRegistre_llorenc documents) {
		this.document = documents;
	}
	
	public String getDocumentacioFisicaCodi() {
		return documentacioFisicaCodi;
	}
	public void setDocumentacioFisicaCodi(String documentacioFisicaCodi) {
		this.documentacioFisicaCodi = documentacioFisicaCodi;
	}
	
	
}

