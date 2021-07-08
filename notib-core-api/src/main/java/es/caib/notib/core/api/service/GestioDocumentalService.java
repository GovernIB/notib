/**
 * 
 */
package es.caib.notib.core.api.service;

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
	@PreAuthorize("hasRole('tothom')")
	String guardarArxiuTemporal(String string);

	@PreAuthorize("hasRole('tothom')")
	byte[] obtenirArxiuTemporal(String arxiuGestdocId);

	@PreAuthorize("hasRole('tothom')")
	byte[] obtenirArxiuNotificacio(String arxiuGestdocId);

}