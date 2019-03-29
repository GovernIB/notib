package es.caib.notib.core.api.dto;

import java.util.List;



/**
 * DTO amb informació d'una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreAnotacioDtoV2 {

	private RegistreIdDto id;
	private String organ;
	private String llibre;
	private String oficina;
	private String entitatCodi;
	private String unitatAdministrativa;

	private String expedientNumero;
	
	private String assumpteIdiomaCodi;
	private String assumpteTipus;
	private String assumpteCodi;
	private String assumpteExtracte;
	private String assumpteRegistreNumero;
	private String assumpteRegistreAny;
	
	private String exposa;
	private String solicita;
	
	private RegistreDocumentacioFisicaEnumDto documentacioFisica;
	
	private String observacions;

	private List<RegistreAnnexDto> annexos;
	private List<RegistreInteressatDto> interessats;



	public RegistreIdDto getId() {
		return id;
	}
	public void setId(RegistreIdDto id) {
		this.id = id;
	}
	public String getOrgan() {
		return organ;
	}
	public void setOrgan(String organ) {
		this.organ = organ;
	}
	public String getLlibre() {
		return llibre;
	}
	public void setLlibre(String llibre) {
		this.llibre = llibre;
	}
	public String getOficina() {
		return oficina;
	}
	public void setOficina(String oficina) {
		this.oficina = oficina;
	}
	public String getEntitatCodi() {
		return entitatCodi;
	}
	public void setEntitatCodi(String entitatCodi) {
		this.entitatCodi = entitatCodi;
	}
	public String getUnitatAdministrativa() {
		return unitatAdministrativa;
	}
	public void setUnitatAdministrativa(String unitatAdministrativa) {
		this.unitatAdministrativa = unitatAdministrativa;
	}
	public String getAssumpteIdiomaCodi() {
		return assumpteIdiomaCodi;
	}
	public void setAssumpteIdiomaCodi(String assumpteIdiomaCodi) {
		this.assumpteIdiomaCodi = assumpteIdiomaCodi;
	}
	public String getAssumpteTipus() {
		return assumpteTipus;
	}
	public void setAssumpteTipus(String assumpteTipus) {
		this.assumpteTipus = assumpteTipus;
	}
	public String getAssumpteExtracte() {
		return assumpteExtracte;
	}
	public void setAssumpteExtracte(String assumpteExtracte) {
		this.assumpteExtracte = assumpteExtracte;
	}
	public String getAssumpteRegistreNumero() {
		return assumpteRegistreNumero;
	}
	public void setAssumpteRegistreNumero(String assumpteRegistreNumero) {
		this.assumpteRegistreNumero = assumpteRegistreNumero;
	}
	public String getAssumpteRegistreAny() {
		return assumpteRegistreAny;
	}
	public void setAssumpteRegistreAny(String assumpteRegistreAny) {
		this.assumpteRegistreAny = assumpteRegistreAny;
	}
	public List<RegistreAnnexDto> getAnnexos() {
		return annexos;
	}
	public void setAnnexos(List<RegistreAnnexDto> annexos) {
		this.annexos = annexos;
	}
	public String getExpedientNumero() {
		return expedientNumero;
	}
	public void setExpedientNumero(
			String expedientNumero) {
		this.expedientNumero = expedientNumero;
	}
	public String getAssumpteCodi() {
		return assumpteCodi;
	}
	public void setAssumpteCodi(
			String assumpteCodi) {
		this.assumpteCodi = assumpteCodi;
	}
	public RegistreDocumentacioFisicaEnumDto getDocumentacioFisica() {
		return documentacioFisica;
	}
	public void setDocumentacioFisica(
			RegistreDocumentacioFisicaEnumDto documentacioFisica) {
		this.documentacioFisica = documentacioFisica;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(
			String observacions) {
		this.observacions = observacions;
	}
	public List<RegistreInteressatDto> getInteressats() {
		return interessats;
	}
	public void setInteressats(
			List<RegistreInteressatDto> interessats) {
		this.interessats = interessats;
	}
	public String getExposa() {
		return exposa;
	}
	public void setExposa(
			String exposa) {
		this.exposa = exposa;
	}
	public String getSolicita() {
		return solicita;
	}
	public void setSolicita(
			String solicita) {
		this.solicita = solicita;
	}

}
