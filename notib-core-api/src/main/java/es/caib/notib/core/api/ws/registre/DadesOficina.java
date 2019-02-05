package es.caib.notib.core.api.ws.registre;

/**
 * Informació sobre l'oficina de registre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesOficina {

	private String organ;
	private String oficina;
	private String oficinaFisica;

	public String getOrgan() {
		return organ;
	}
	public void setOrgan(String organ) {
		this.organ = organ;
	}
	public String getOficina() {
		return oficina;
	}
	public void setOficina(String oficina) {
		this.oficina = oficina;
	}
	public String getOficinaFisica() {
		return oficinaFisica;
	}
	public void setOficinaFisica(String oficinaFisica) {
		this.oficinaFisica = oficinaFisica;
	}

}
