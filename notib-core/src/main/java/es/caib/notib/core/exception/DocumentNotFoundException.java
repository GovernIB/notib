package es.caib.notib.core.exception;

import es.caib.notib.core.api.exception.SistemaExternException;
import es.caib.notib.core.helper.IntegracioHelper;

/**
 * Excepció que es llança quan hi ha un error consultant un document a l'arxiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentNotFoundException extends SistemaExternException {

	private final String tipusCodi;
	private final String codi;

	public DocumentNotFoundException(String tipusCodi, String codi, Throwable cause) {
		super(IntegracioHelper.INTCODI_ARXIU,
				String.format("Error al plugin d'arxiu digital: no s'ha pogut obtenir el document amb el codi %s: %s", tipusCodi, codi),
				cause);

		this.tipusCodi = tipusCodi;
		this.codi = codi;
	}
}
