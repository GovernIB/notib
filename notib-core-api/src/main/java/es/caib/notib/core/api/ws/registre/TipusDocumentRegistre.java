package es.caib.notib.core.api.ws.registre;

public class TipusDocumentRegistre {

	private String nom;
	private String codi;

	public TipusDocumentRegistre(String codi, String nom) {
		this.codi = codi;
		this.nom = nom;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getCodi() {
		return codi;
	}

	public void setCodi(String codi) {
		this.codi = codi;
	}

}
