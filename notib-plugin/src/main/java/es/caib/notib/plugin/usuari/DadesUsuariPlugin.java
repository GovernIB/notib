/**
 * 
 */
package es.caib.notib.plugin.usuari;

import java.util.List;

import es.caib.notib.plugin.SistemaExternException;


/**
 * Plugin per a consultar les dades d'una font d'usuaris externa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DadesUsuariPlugin {

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
	 * @param usuariNif
	 *            Codi del grup que es vol consultar.
	 * @return La llista d'usuaris del grup.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les dades de l'usuari.
	 */
	public List<DadesUsuari> consultarAmbGrup(String grupCodi) throws SistemaExternException;

}