package es.caib.notib.plugin.registre;

import lombok.Getter;
import lombok.Setter;

/**
 * Resposta a una petició per obtenir justificant de recepció
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class RespostaJustificantRecepcio extends RespostaBase{

	private byte[] justificant;

}
