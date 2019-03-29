package es.caib.notib.plugin.registre;

/**
 * Informació sobre l'interessat d'una anotació de registre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesInteressat {

	private String entitatCodi;
	private boolean autenticat;
	private Long tipusInteressat;
	private String nif;
	private String nom;
	private String cognom1;
	private String cognom2;
	private String nomAmbCognoms;
	private String paisCodi;
	private String paisNom;
	private String provinciaCodi;
	private String provinciaNom;
	private String municipiCodi;
	private String municipiNom;

	public String getEntitatCodi() {
		return entitatCodi;
	}
	public void setEntitatCodi(String entitatCodi) {
		this.entitatCodi = entitatCodi;
	}
	public Long getTipusInteressat() {
		return tipusInteressat;
	}
	public void setTipusInteressat(Long tipusInteressat) {
		this.tipusInteressat = tipusInteressat;
	}
	public boolean isAutenticat() {
		return autenticat;
	}
	public void setAutenticat(boolean autenticat) {
		this.autenticat = autenticat;
	}
	public String getNif() {
		return nif;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getCognom1() {
		return cognom1;
	}
	public void setCognom1(String cognom1) {
		this.cognom1 = cognom1;
	}
	public String getCognom2() {
		return cognom2;
	}
	public void setCognom2(String cognom2) {
		this.cognom2 = cognom2;
	}
	public void setNif(String nif) {
		this.nif = nif;
	}
	public String getNomAmbCognoms() {
		return nomAmbCognoms;
	}
	public void setNomAmbCognoms(String nomAmbCognoms) {
		this.nomAmbCognoms = nomAmbCognoms;
	}
	public String getPaisCodi() {
		return paisCodi;
	}
	public void setPaisCodi(String paisCodi) {
		this.paisCodi = paisCodi;
	}
	public String getPaisNom() {
		return paisNom;
	}
	public void setPaisNom(String paisNom) {
		this.paisNom = paisNom;
	}
	public String getProvinciaCodi() {
		return provinciaCodi;
	}
	public void setProvinciaCodi(String provinciaCodi) {
		this.provinciaCodi = provinciaCodi;
	}
	public String getProvinciaNom() {
		return provinciaNom;
	}
	public void setProvinciaNom(String provinciaNom) {
		this.provinciaNom = provinciaNom;
	}
	public String getMunicipiCodi() {
		return municipiCodi;
	}
	public void setMunicipiCodi(String municipiCodi) {
		this.municipiCodi = municipiCodi;
	}
	public String getMunicipiNom() {
		return municipiNom;
	}
	public void setMunicipiNom(String municipiNom) {
		this.municipiNom = municipiNom;
	}

}
