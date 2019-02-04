package es.caib.notib.core.api.ws.registre;

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
	private DadesAssumpte dadesAssumpte;
	private List<DocumentRegistre> documents;

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
	public DadesAssumpte getDadesAssumpte() {
		return dadesAssumpte;
	}
	public void setDadesAssumpte(DadesAssumpte dadesAssumpte) {
		this.dadesAssumpte = dadesAssumpte;
	}
	public List<DocumentRegistre> getDocuments() {
		return documents;
	}
	public void setDocuments(List<DocumentRegistre> documents) {
		this.documents = documents;
	}

}
