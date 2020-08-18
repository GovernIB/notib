/**
 * 
 */
package es.caib.notib.plugin.gesconadm;

import java.util.List;

import es.caib.notib.plugin.SistemaExternException;


/**
 * Plugin per a obtenir informació dels procediments del Gestor de Continguts Administratiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface GestorContingutsAdministratiuPlugin {
	
	/**
	 * Retorna la llista de tots els procediments definits al Gestor documental administratiu.
	 * 
	 * @return La llista de procediments.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar els procediments.
	 */
	public List<GcaProcediment> getAllProcediments() throws SistemaExternException;
	
	/**
	 * Retorna la llista de tots els procediments definits al Gestor documental administratiu per a una unitat administrativa.
	 * 
	 * @param codi CodiDir3 de la unitat administrativa
	 * @return La llista de procediments.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar els procediments.
	 */
	public List<GcaProcediment> getProcedimentsByUnitat(String codi) throws SistemaExternException;
	
	/**
	 * Retorna una unitat administrativa donat el seu codi.
	 * 
	 * @param codi CodiDir3 de la unitat administrativa
	 * @return La unitat administrativa.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar la unitat administrativa.
	 */
	public String getUnitatAdministrativa(String codi) throws SistemaExternException;

}