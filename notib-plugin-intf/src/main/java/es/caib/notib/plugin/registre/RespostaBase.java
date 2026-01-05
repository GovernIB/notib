package es.caib.notib.plugin.registre;

import lombok.Getter;
import lombok.Setter;

/**
 * Resposta base del plugin de registre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class RespostaBase {

	private String errorCodi;
	private String errorDescripcio;

	public boolean isError() {
		return !Strings.isNullOrEmpty(errorCodi);
	}

}
