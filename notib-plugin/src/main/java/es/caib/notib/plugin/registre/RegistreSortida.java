package es.caib.notib.plugin.registre;

import java.util.ArrayList;
import java.util.List;

/**
 * Registre de sortida o notificació
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreSortida {

	private String codiEntitat;
	private String codiUsuari;
	private DadesOficina dadesOficina;
	private List<DadesInteressat> dadesInteressat = new ArrayList<DadesInteressat>();
	private DadesRepresentat dadesRepresentat;
	private DadesAnotacio dadesAnotacio;
	private List<DocumentRegistre> documents;
	private String versioNotib;
	private String aplicacio;
	
	public String getCodiEntitat() {
		return codiEntitat;
	}
	public void setCodiEntitat(String codiEntitat) {
		this.codiEntitat = codiEntitat;
	}
	public String getCodiUsuari() {
		return codiUsuari;
	}
	public void setCodiUsuari(String codiUsuari) {
		this.codiUsuari = codiUsuari;
	}
	public DadesOficina getDadesOficina() {
		return dadesOficina;
	}
	public void setDadesOficina(DadesOficina dadesOficina) {
		this.dadesOficina = dadesOficina;
	}
	public List<DadesInteressat> getDadesInteressat() {
		return dadesInteressat;
	}
	public void setDadesInteressat(List<DadesInteressat> dadesInteressat) {
		this.dadesInteressat = dadesInteressat;
	}
	public DadesRepresentat getDadesRepresentat() {
		return dadesRepresentat;
	}
	public void setDadesRepresentat(DadesRepresentat dadesRepresentat) {
		this.dadesRepresentat = dadesRepresentat;
	}
	public DadesAnotacio getDadesAnotacio() {
		return dadesAnotacio;
	}
	public void setDadesAnotacio(DadesAnotacio dadesAnotacio) {
		this.dadesAnotacio = dadesAnotacio;
	}
	public List<DocumentRegistre> getDocuments() {
		return documents;
	}
	public void setDocuments(List<DocumentRegistre> documents) {
		this.documents = documents;
	}
	public String getVersioNotib() {
		return versioNotib;
	}
	public void setVersioNotib(String versioNotib) {
		this.versioNotib = versioNotib;
	}
	public String getAplicacio() {
		return aplicacio;
	}
	public void setAplicacio(String aplicacio) {
		this.aplicacio = aplicacio;
	}
}
