package es.caib.notib.plugin.registre;

/**
 * Informació sobre l'interessat d'una anotació de registre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesInteressat {

	private Interessat interessat;
	private Interessat representat;
	
	public Interessat getInteressat() {
		return interessat;
	}
	public void setInteressat(Interessat interessat) {
		this.interessat = interessat;
	}
	public Interessat getRepresentat() {
		return representat;
	}
	public void setRepresentat(Interessat representat) {
		this.representat = representat;
	}
	
	
}
