/**
 * 
 */
package es.caib.notib.logic.intf.service;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Declaració dels mètodes per a la consulta d'arxius al sistema de fitxers
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface GestioDocumentalService {

	/**
	 * Guarda un document temporal a partir de la codificació del document en un String en base64
	 * 
	 * @return el id
	 */
	@PreAuthorize("isAuthenticated()")
	String guardarArxiuTemporal(String string);

	@PreAuthorize("isAuthenticated()")
	byte[] obtenirArxiuTemporal(String arxiuGestdocId);

	@PreAuthorize("isAuthenticated()")
	byte[] obtenirArxiuNotificacio(String arxiuGestdocId);

}