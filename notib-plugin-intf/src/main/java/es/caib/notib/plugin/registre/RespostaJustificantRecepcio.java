package es.caib.notib.plugin.registre;

/**
 * Resposta a una petició per obtenir justificant de recepció
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RespostaJustificantRecepcio extends RespostaBase{

	private byte[] justificant;

	public byte[] getJustificant() {
		return justificant;
	}
	public void setJustificant(byte[] justificant) {
		this.justificant = justificant;
	}

}
