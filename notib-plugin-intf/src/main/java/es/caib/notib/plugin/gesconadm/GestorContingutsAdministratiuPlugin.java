/**
 * 
 */
package es.caib.notib.plugin.gesconadm;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.notib.plugin.SistemaExternException;

import java.util.List;


/**
 * Plugin per a obtenir informació dels procediments del Gestor de Continguts Administratiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface GestorContingutsAdministratiuPlugin {

	GesconAdm getProcSerByCodiSia(String codiSia, boolean isService) throws SistemaExternException;

	GcaServei getServeiByCodiSia(ObjectMapper mapper, String json) throws Exception;

	GcaProcediment getProcedimentByCodiSia(ObjectMapper mapper, String json) throws Exception;

	// Procediments

	/**
	 * Retorna la llista de tots els procediments definits al Gestor documental administratiu.
	 *
	 * @return La llista de procediments.
	 * @throws SistemaExternException Si es produeix un error al consultar els procediments.
	 */
	public List<GcaProcediment> getAllProcediments() throws SistemaExternException;

	/**
	 * Retorna la llista de tots els procediments definits al Gestor documental administratiu per a una unitat administrativa.
	 * 
	 * @param codi CodiDir3 de la unitat administrativa
	 * @param numPagina indica el número de pàgina a recuperar
	 * @return La llista de procediments.
	 * @throws SistemaExternException Si es produeix un error al consultar els procediments.
	 */
	List<GcaProcediment> getProcedimentsByUnitat(String codi, int numPagina) throws SistemaExternException;

	List<GcaProcediment> getProcedimentsByUnitat(String codi) throws SistemaExternException;
	
	/**
	 * Retorna una unitat administrativa donat el seu codi.
	 * 
	 * @param codi CodiDir3 de la unitat administrativa
	 * @return La unitat administrativa.
	 * @throws SistemaExternException Si es produeix un error al consultar la unitat administrativa.
	 */
	public String getUnitatAdministrativa(String codi) throws SistemaExternException;
	
	/**
	 * Retorna el total de procediments per una entitat.
	 * 
	 * @param codi CodiDir3 de la unitat administrativa
	 * @return La llista de procediments.
	 * @throws SistemaExternException Si es produeix un error al consultar els procediments.
	 */
	int getTotalProcediments(String codi) throws SistemaExternException;


	// Serveis

	/**
	 * Retorna la llista de tots els serveis definits al Gestor documental administratiu.
	 *
	 * @return La llista de serveis.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar els serveis.
	 */
	List<GcaServei> getAllServeis() throws SistemaExternException;

	/**
	 * Retorna la llista de tots els serveis definits al Gestor documental administratiu per a una unitat administrativa.
	 *
	 * @param codi CodiDir3 de la unitat administrativa
	 * @param numPagina indica el número de pàgina a recuperar
	 * @return La llista de serveis.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar els serveis.
	 */
	List<GcaServei> getServeisByUnitat(String codi, int numPagina) throws SistemaExternException;

	List<GcaServei> getServeisByUnitat(String codi) throws SistemaExternException;

	/**
	 * Retorna el total de serveis per una entitat.
	 *
	 * @param codi CodiDir3 de la unitat administrativa
	 * @return El nombre de serveis.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar els serveis.
	 */
	int getTotalServeis(String codi) throws SistemaExternException;

}