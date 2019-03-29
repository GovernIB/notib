package es.caib.notib.plugin.registre;

import java.util.Date;
import java.util.List;

/**
 * Resposta a una consulta de registre d'entrada
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RespostaConsulta extends RespostaBase {

	private String registreNumero;
	private Date registreData;
	private DadesOficina dadesOficina;
	private DadesInteressat dadesInteressat;
	private DadesRepresentat dadesRepresentat;
	private DadesAnotacio dadesAssumpte;
	private List<DocumentRegistre_llorenc> documents;

	public String getRegistreNumero() {
		return registreNumero;
	}
	public void setRegistreNumero(String registreNumero) {
		this.registreNumero = registreNumero;
	}
	public Date getRegistreData() {
		return registreData;
	}
	public void setRegistreData(Date registreData) {
		this.registreData = registreData;
	}
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
	public DadesAnotacio getDadesAnotacio() {
		return dadesAssumpte;
	}
	public void setDadesAnotacio(DadesAnotacio dadesAssumpte) {
		this.dadesAssumpte = dadesAssumpte;
	}
	public List<DocumentRegistre_llorenc> getDocuments() {
		return documents;
	}
	public void setDocuments(List<DocumentRegistre_llorenc> documents) {
		this.documents = documents;
	}

}
