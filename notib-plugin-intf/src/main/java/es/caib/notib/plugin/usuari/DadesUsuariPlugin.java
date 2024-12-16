/**
 * 
 */
package es.caib.notib.plugin.usuari;

import es.caib.notib.plugin.SalutPlugin;
import es.caib.notib.plugin.SistemaExternException;

import java.util.List;


/**
 * Plugin per a consultar les dades d'una font d'usuaris externa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DadesUsuariPlugin extends SalutPlugin {

	/**
	 * Retorna la informació d'un usuari donat el codi d'usuari.
	 * 
	 * @param usuariCodi
	 *            Codi de l'usuari que es vol consultar.
	 * @return la informació de l'usuari o null si no se troba.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les dades de l'usuari.
	 */
	public List<String> consultarRolsAmbCodi(String usuariCodi) throws SistemaExternException;
	
	/**
	 * Retorna la informació d'un usuari donat el codi d'usuari.
	 * 
	 * @param usuariCodi
	 *            Codi de l'usuari que es vol consultar.
	 * @return la informació de l'usuari o null si no se troba.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les dades de l'usuari.
	 */
	public DadesUsuari consultarAmbCodi(String usuariCodi) throws SistemaExternException;

	/**
	 * Retorna la llista d'usuaris d'un grup.
	 * 
	 * @param grupCodi
	 *            Codi del grup que es vol consultar.
	 * @return La llista d'usuaris del grup.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les dades de l'usuari.
	 */
	public List<DadesUsuari> consultarAmbGrup(String grupCodi) throws SistemaExternException;

}
