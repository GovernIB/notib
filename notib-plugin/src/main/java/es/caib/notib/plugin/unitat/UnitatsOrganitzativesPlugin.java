/**
 * 
 */
package es.caib.notib.plugin.unitat;

import es.caib.notib.core.api.dto.organisme.OrganismeDto;
import es.caib.notib.plugin.SistemaExternException;

import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Plugin per a obtenir l'arbre d'unitats organitzatives.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface UnitatsOrganitzativesPlugin {
	
	/**
	 * Retorna la llista d'unitats organitzatives filles donat un codi d'entitat.
	 * 
	 * @param codi
	 *            Codi dir3 de la unitat pare.
	 * @param inclourePare
	 * 			  Indica si el llistat ha d'incloure l'entitat pare
	 *            
	 * @return La llista d'unitats organitzatives.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les unitats organitzatives.
	 */
	public Map<String, NodeDir3> organigramaPerEntitat(String codiEntitat) throws SistemaExternException;
	
	public Map<String, NodeDir3> organigramaPerEntitatWs(
			String pareCodi,
			Timestamp fechaActualizacion,
			Timestamp fechaSincronizacion) throws SistemaExternException;

	/**
	 * Retorna la llista d'unitats organitzatives filles donada
	 * una unitat pare.
	 * If you put fechaActualizacion==null and fechaSincronizacion==null it returns all unitats that are now vigent (current tree)
	 * If you put fechaActualizacion!=null and fechaSincronizacion!=null it returns all the changes in unitats from the time of last syncronization (@param fechaActualizacion) to now
	 *
	 * @param pareCodi
	 *            Codi de la unitat pare. It doesnt have to be arrel
	 * @param dataActualitzacio
	 *            Data de la darrera actualització.
	 * @param dataSincronitzacio
	 *            Data de la primera sincronització.
	 * @return La llista d'unitats organitzatives.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les unitats organitzatives.
	 */
	public List<NodeDir3> findAmbPare(
			String pareCodi,
			Date dataActualitzacio,
			Date dataSincronitzacio) throws SistemaExternException;

	/**
	 * Retorna la unitat organtizativa donat el pareCodi
	 *
	 * @param pareCodi
	 *            Codi de la unitat pare.
	 * @param dataActualitzacio
	 *            Data de la darrera actualització.
	 * @param dataSincronitzacio
	 *            Data de la primera sincronització.
	 * @return La unitat organitzativa trobada.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les unitats organitzatives.
	 */
	public NodeDir3 findAmbCodi(
			String pareCodi,
			Date dataActualitzacio,
			Date dataSincronitzacio) throws MalformedURLException;

	/**
	 * Retorna la llista d'unitats organitzatives filles donat un codi d'entitat.
	 * 
	 * @param codi
	 *            Codi dir3 de la unitat pare.
	 * @param inclourePare
	 * 			  Indica si el llistat ha d'incloure l'entitat pare
	 *            
	 * @return La llista d'unitats organitzatives.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les unitats organitzatives.
	 */
	public List<ObjetoDirectorio> unitatsPerEntitat(String codiEntitat, boolean inclourePare) throws SistemaExternException;

	/**
	 * Retorna la denominació  d'una unitats organitzativa.
	 * 
	 * @param codi
	 *            Codi dir3 de la unitat organitzativa.
	 *            
	 * @return La denominació de la unitat organitzativa.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar la denominació de la unitat organitzativa.
	 */
	public String unitatDenominacio(String codiDir3) throws SistemaExternException;
	
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
	 * Retorna la llista d'unitats organitzatives a partir d'un text comparant amb la denominació.
	 * 
	 * @param denominacio
	 *            Text corresponent a la denominació
	 *            
	 * @return La llista d'unitats organitzatives.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les unitats organitzatives.
	 */
	public List<ObjetoDirectorio> unitatsPerDenominacio(String denominacio) throws SistemaExternException;
	
	
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
	 * @return la llista del paisos disponibles dins DIR3.
	 * 
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar els nivells d'administració.
	 */
	public List<CodiValorPais> paisos() throws SistemaExternException;
	
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
	
	/**
	 * @return recupera el llistat de les oficines SIR d'una unitata organitzativa
	 * 
	 * @param unitat
	 * 			Codi de la unitat
	 * @param arbreUnitats 
	 * 			Arbre unitat actual
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les oficines SIR
	 */
	public List<OficinaSIR> oficinesSIRUnitat(
			String unitat, 
			Map<String, OrganismeDto> arbreUnitats) throws SistemaExternException;

	/**
	 * @return recupera el llistat de les oficines SIR d'una entitat
	 * 
	 * @param entitat
	 * 			Codi de la entitat
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les oficines SIR
	 */
	public List<OficinaSIR> getOficinesSIREntitat(String entitat) throws SistemaExternException;
}