package es.caib.notib.plugin.registre;

import java.util.List;

/**
 * Registre d'entrada
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreEntrada {

	private DadesOficina dadesOficina;
	private DadesInteressat dadesInteressat;
	private DadesRepresentat dadesRepresentat;
	private DadesAnotacio dadesAssumpte;
	private List<DocumentRegistre_llorenc> documents;

	public DadesOficina getDadesOficina() {
		return dadesOficina;
	}
	public void setDadesOficina(DadesOficina dadesOficina) {
		this.dadesOficina = dadesOficina;
	}
	public DadesInteressat getDadesInteressat() {
		return dadesInteressat;
	}
	public void setDadesInteressat(DadesInteressat dadesInteressat) {
		this.dadesInteressat = dadesInteressat;
	}
	public DadesRepresentat getDadesRepresentat() {
		return dadesRepresentat;
	}
	public void setDadesRepresentat(DadesRepresentat dadesRepresentat) {
		this.dadesRepresentat = dadesRepresentat;
	}
	public DadesAnotacio getDadesAssumpte() {
		return dadesAssumpte;
	}
	public void setDadesAssumpte(DadesAnotacio dadesAssumpte) {
		this.dadesAssumpte = dadesAssumpte;
	}
	public List<DocumentRegistre_llorenc> getDocuments() {
		return documents;
	}
	public void setDocuments(List<DocumentRegistre_llorenc> documents) {
		this.documents = documents;
	}

}
