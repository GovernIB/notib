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

	public static final String ERROR_CODI_OK = "OK";
	public static final String ERROR_CODI_ERROR = "ERROR";

	private String errorCodi;
	private String errorDescripcio;

	public boolean isOk() {
		return ERROR_CODI_OK.equals(errorCodi);
	}
	public boolean isError() {
		return ERROR_CODI_ERROR.equals(errorCodi);
	}

}
