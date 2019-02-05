package es.caib.notib.core.api.ws.registre;

/**
 * Informació sobre la persona representada en una anotació
 * de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesRepresentat {

	private String nif;
	private String nomAmbCognoms;

	public String getNif() {
		return nif;
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

}
