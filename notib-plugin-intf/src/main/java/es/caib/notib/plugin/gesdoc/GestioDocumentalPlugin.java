/**
 * 
 */
package es.caib.notib.plugin.gesdoc;

import es.caib.notib.plugin.SalutPlugin;
import es.caib.notib.plugin.SistemaExternException;

import java.io.InputStream;
import java.io.OutputStream;


/**
 * Interfície per accedir al gestor documental.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface GestioDocumentalPlugin extends SalutPlugin {

	/**
	 * Puja un document a la gestió documental.
	 * 
	 * @param agrupacio Nom de l'agrupacio.
	 * @param contingutIn Stream d'entrada des d'on llegir el contingut de l'arxiu.
	 * @return L'identificador del document.
	 * @throws SistemaExternException
	 *             Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	String create(String agrupacio, InputStream contingutIn) throws SistemaExternException;

	/**
	 * Actualitza un document ja existent a la gestió documental.
	 * 
	 * @param id Identificador del document.
	 * @param agrupacio Nom de l'agrupacio.
	 * @param contingut Contingut de l'arxiu.
	 * @throws SistemaExternException Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	void update(String id, String agrupacio, InputStream contingut) throws SistemaExternException;

	/**
	 * Esborra un document ja existent a la gestió documental.
	 * 
	 * @param id Identificador del document.
	 * @param agrupacio Nom de l'agrupacio.
	 * @throws SistemaExternException Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	void delete(String id, String agrupacio) throws SistemaExternException;

	/**
	 * Obté un document de la gestió documental.
	 * 
	 * @param id Identificador del document.
	 * @param agrupacio Nom de l'agrupacio.
	 * @param contingutOut Stream de sortida a on escriure el contingut de l'arxiu.
	 * @return La informació del document. @throws SistemaExternException Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	void get(String id, String agrupacio, OutputStream contingutOut, boolean isZip) throws SistemaExternException;

}
