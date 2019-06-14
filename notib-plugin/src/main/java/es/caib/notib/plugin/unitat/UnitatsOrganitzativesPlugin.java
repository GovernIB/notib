/**
 * 
 */
package es.caib.notib.plugin.unitat;

import java.util.List;

import es.caib.notib.plugin.SistemaExternException;


/**
 * Plugin per a obtenir l'arbre d'unitats organitzatives.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface UnitatsOrganitzativesPlugin {
	

	/**
	 * Retorna la llista d'unitats organitzatives filles donat un filtre.
	 * 
	 * @param codi
	 *            Codi de la unitat.
	 * @param denominacio
	 *            Denominació de la unitat de la unitat
	 * @param nivellAdministracio
	 *            Nivell de administració de la unitat.
	 * @param comunitatAutonoma
	 *            Codi de la comunitat de la unitat.
	 * @param ambOficines
	 *            Indica si les unitats retornades tenen oficines.
	 * @param esUnitatArrel
	 *            Indica si les unitats retornades són unitats arrel.
	 * @param provincia
	 *            Codi de la provincia de la unitat.
	 * @param localitat
	 *            Codi de la localitat de la unitat.
	 *            
	 * @return La llista d'unitats organitzatives.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les unitats organitzatives.
	 */
	public List<NodeDir3> cercaUnitats(
			String codi, 
			String denominacio,
			Long nivellAdministracio, 
			Long comunitatAutonoma, 
			Boolean ambOficines, 
			Boolean esUnitatArrel,
			Long provincia, 
			String municipi) throws SistemaExternException;

	
	/**
	 * Retorna la llista d'unitats organitzatives filles donat un filtre.
	 * 
	 * @param codi
	 *            Codi de la oficina.
	 * @param denominacio
	 *            Denominació de la oficina
	 * @param nivellAdministracio
	 *            Nivell de administració de la administració de les oficines que cercam.
	 * @param comunitatAutonoma
	 *            Codi de la comunitat de la oficina.
	 * @param provincia
	 *            Codi de la provincia de la oficina.
	 * @param localitat
	 *            Codi de la localitat de la oficina.
	 *            
	 * @return La llista d'unitats organitzatives.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les unitats organitzatives.
	 */
	public List<NodeDir3> cercaOficines(
			String codi, 
			String denominacio,
			Long nivellAdministracio, 
			Long comunitatAutonoma, 
			Long provincia, 
			String municipi) throws SistemaExternException;
	
	/**
	 * @return la llista de nivells d'administració
	 * 
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar els nivells d'administració.
	 */
	public List<CodiValor> nivellsAdministracio() throws SistemaExternException;
	
	/**
	 * @return la llista de comunitats autònomes
	 * 
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les comunitats autònomes.
	 */
	public List<CodiValor> comunitatsAutonomes() throws SistemaExternException;
	
	/**
	 * @return la llista de provincies d'una comunitat autònoma
	 * 
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les provincies d'una comunitat autònoma.
	 */
	public List<CodiValor> provincies() throws SistemaExternException;
	
	/**
	 * @return la llista de provincies d'una comunitat autònoma
	 * 
	 * @param codiCA
	 * 			Codi de la comunitat autònoma
	 * 
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les provincies d'una comunitat autònoma.
	 */
	public List<CodiValor> provincies(
			String codiCA) throws SistemaExternException;
	
	/**
	 * @return la llista de localitats d'una província
	 * 
	 * @param codiProvincia
	 * 			Codi de la comunitat autònoma
	 * 
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les localitats d'una província.
	 */
	public List<CodiValor> localitats(
			String codiProvincia) throws SistemaExternException;
}