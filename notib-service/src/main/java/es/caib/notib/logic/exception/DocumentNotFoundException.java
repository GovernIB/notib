package es.caib.notib.logic.exception;

import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.exception.SistemaExternException;

/**
 * Excepció que es llança quan hi ha un error consultant un document a l'arxiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentNotFoundException extends SistemaExternException {

	private final String tipusCodi;
	private final String codi;

	public DocumentNotFoundException(String tipusCodi, String codi, Throwable cause) {

		super(IntegracioCodi.ARXIU.name(), String.format("Error al plugin d'arxiu digital: no s'ha pogut obtenir el document amb el codi %s: %s", tipusCodi, codi), cause);
		this.tipusCodi = tipusCodi;
		this.codi = codi;
	}
}
