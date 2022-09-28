package es.caib.notib.logic.intf.dto;

public class RegistreInteressatDto {

	private String tipus;
	private RegistreInteressatDocumentTipusDtoEnum documentTipus;
	private String documentNumero;
	private String email;
	private String telefon;
	private String nom;
	private String llinatge1;
	private String llinatge2;
	private String raoSocial;
	private String paisCodi;
	private String provinciaCodi;
	private String municipiCodi;
	private String codiPostal;
	private String adressa;
	
	private RegistreInteressatDto representant;
	
	public String getTipus() {
		return tipus;
	}
	public void setTipus(
			String tipus) {
		this.tipus = tipus;
	}
	public RegistreInteressatDocumentTipusDtoEnum getDocumentTipus() {
		return documentTipus;
	}
	public void setDocumentTipus(
			RegistreInteressatDocumentTipusDtoEnum documentTipus) {
		this.documentTipus = documentTipus;
	}
	public String getDocumentNumero() {
		return documentNumero;
	}
	public void setDocumentNumero(
			String documentNumero) {
		this.documentNumero = documentNumero;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(
			String email) {
		this.email = email;
	}
	public String getTelefon() {
		return telefon;
	}
	public void setTelefon(
			String telefon) {
		this.telefon = telefon;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(
			String nom) {
		this.nom = nom;
	}
	public String getLlinatge1() {
		return llinatge1;
	}
	public void setLlinatge1(
			String llinatge1) {
		this.llinatge1 = llinatge1;
	}
	public String getLlinatge2() {
		return llinatge2;
	}
	public void setLlinatge2(
			String llinatge2) {
		this.llinatge2 = llinatge2;
	}
	public String getRaoSocial() {
		return raoSocial;
	}
	public void setRaoSocial(
			String raoSocial) {
		this.raoSocial = raoSocial;
	}
	public String getPaisCodi() {
		return paisCodi;
	}
	public void setPaisCodi(
			String paisCodi) {
		this.paisCodi = paisCodi;
	}
	public String getProvinciaCodi() {
		return provinciaCodi;
	}
	public void setProvinciaCodi(
			String provinciaCodi) {
		this.provinciaCodi = provinciaCodi;
	}
	public String getMunicipiCodi() {
		return municipiCodi;
	}
	public void setMunicipiCodi(
			String municipiCodi) {
		this.municipiCodi = municipiCodi;
	}
	public String getCodiPostal() {
		return codiPostal;
	}
	public void setCodiPostal(
			String codiPostal) {
		this.codiPostal = codiPostal;
	}
	public String getAdressa() {
		return adressa;
	}
	public void setAdressa(
			String adressa) {
		this.adressa = adressa;
	}
	public RegistreInteressatDto getRepresentant() {
		return representant;
	}
	public void setRepresentant(
			RegistreInteressatDto representant) {
		this.representant = representant;
	}
	
}
