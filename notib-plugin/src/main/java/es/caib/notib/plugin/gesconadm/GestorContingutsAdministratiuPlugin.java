/**
 * 
 */
package es.caib.notib.plugin.gesconadm;

import es.caib.notib.plugin.SistemaExternException;

import java.util.List;


/**
 * Plugin per a obtenir informació dels procediments del Gestor de Continguts Administratiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface GestorContingutsAdministratiuPlugin {

	// Procediments

	/**
	 * Busca el procediment amb codiSia definit al Gestor documental administratiu.
	 * @param codiSia
	 * @return procediment amb codi SIA especificat per parametre.
	 * @throws SistemaExternException Si es produeix un error al consultar els procediments.
	 */
	GcaProcediment getProcedimentByCodiSia(String codiSia) throws SistemaExternException;

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
	 * @param numPagina indica el número de pàgina a recuperar
	 * @return La llista de procediments.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar els procediments.
	 */
	public List<GcaProcediment> getProcedimentsByUnitat(
			String codi,
			int numPagina) throws SistemaExternException;
	
	/**
	 * Retorna una unitat administrativa donat el seu codi.
	 * 
	 * @param codi CodiDir3 de la unitat administrativa
	 * @return La unitat administrativa.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar la unitat administrativa.
	 */
	public String getUnitatAdministrativa(String codi) throws SistemaExternException;
	
	/**
	 * Retorna el total de procediments per una entitat.
	 * 
	 * @param codi CodiDir3 de la unitat administrativa
	 * @return La llista de procediments.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar els procediments.
	 */
	public int getTotalProcediments(String codi) throws SistemaExternException;


	// Serveis

	/**
	 * Retorna la llista de tots els serveis definits al Gestor documental administratiu.
	 *
	 * @return La llista de serveis.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar els serveis.
	 */
	public List<GcaServei> getAllServeis() throws SistemaExternException;

	/**
	 * Retorna la llista de tots els serveis definits al Gestor documental administratiu per a una unitat administrativa.
	 *
	 * @param codi CodiDir3 de la unitat administrativa
	 * @param numPagina indica el número de pàgina a recuperar
	 * @return La llista de serveis.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar els serveis.
	 */
	public List<GcaServei> getServeisByUnitat(
			String codi,
			int numPagina) throws SistemaExternException;

	/**
	 * Retorna el total de serveis per una entitat.
	 *
	 * @param codi CodiDir3 de la unitat administrativa
	 * @return El nombre de serveis.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar els serveis.
	 */
	public int getTotalServeis(String codi) throws SistemaExternException;

}