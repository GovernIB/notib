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
public interface GestorDocumentalAdministratiuPlugin {
	
	/**
	 * Retorna la llista de tots els procediments definits al Gestor documental administratiu.
	 * 
	 * @return La llista de procediments.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar els procediments.
	 */
	public List<GdaProcediment> getAllProcediments() throws SistemaExternException;
	
	/**
	 * Retorna una unitat administrativa donat el seu codi.
	 * 
	 * @return La unitat administrativa.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar la unitat administrativa.
	 */
	public GdaUnitatAdministrativa getUnitatAdministrativa(String codi) throws SistemaExternException;

}