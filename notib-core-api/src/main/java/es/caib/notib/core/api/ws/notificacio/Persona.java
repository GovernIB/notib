/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import es.caib.notib.core.api.dto.InteressatTipusEnumDto;

/**
 * Informació d'una persona per a un enviament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@JsonAutoDetect
public class Persona {

	private Long id;
	private boolean incapacitat;
	private InteressatTipusEnumDto interessatTipus;
	private String nom;
	private String llinatge1;
	private String llinatge2;
	private String nif;
	private String telefon;
	private String email;
	private String raoSocial;
	private String dir3Codi;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public boolean isIncapacitat() {
		return incapacitat;
	}
	public void setIncapacitat(boolean incapacitat) {
		this.incapacitat = incapacitat;
	}
	public InteressatTipusEnumDto getInteressatTipus() {
		return interessatTipus;
	}
	public void setInteressatTipus(InteressatTipusEnumDto interessatTipus) {
		this.interessatTipus = interessatTipus;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getLlinatge1() {
		return llinatge1;
	}
	public void setLlinatge1(String llinatge1) {
		this.llinatge1 = llinatge1;
	}
	public String getLlinatge2() {
		return llinatge2;
	}
	public void setLlinatge2(String llinatge2) {
		this.llinatge2 = llinatge2;
	}
	public String getNif() {
		return nif;
	}
	public void setNif(String nif) {
		this.nif = nif;
	}
	public String getTelefon() {
		return telefon;
	}
	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRaoSocial() {
		return raoSocial;
	}
	public void setRaoSocial(String raoSocial) {
		this.raoSocial = raoSocial;
	}
	public String getDir3Codi() {
		return dir3Codi;
	}
	public void setDir3Codi(String dir3Codi) {
		this.dir3Codi = dir3Codi;
	}
}
