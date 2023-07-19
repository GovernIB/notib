/**
 * 
 */
package es.caib.notib.logic.intf.service;


/**
 * Declaració dels mètodes per a la consulta de notificacions i dels
 * destinataris i events associats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface SchedulledService {

	/**
	 * Mètode d'execució periòdica per a actualitzar els procediments a partir de les dades de Rolsac
	 */
	void actualitzarProcediments();

	/**
	 * Mètode d'execució periòdica per a refrescar totes les notificacions expirades
	 */
	void refrescarNotificacionsExpirades();

	/**
	 * Mètode d'execució periòdica per a refrescar totes les notificacions DEH finalitzades sense certificació (cas pooling)
	 */
	void enviamentRefrescarEstatDEH();
	
	/**
	 * Mètode d'execució periòdica per a refrescar totes les notificacions CIE finalitzades sense certificació (cas pooling)
	 */
	void enviamentRefrescarEstatCIE();

	/**
	 * Mètode d'execució periòdica per a refrescar totes les notificacions CIE finalitzades sense certificació (cas pooling)
	 */
	void eliminarDocumentsTemporals();

	/**
	 * Mètode d'execució periòdica per a actualitzar els serveis a partir de les dades de Rolsac
	 */
	void actualitzarServeis();

	void consultaCanvisOrganigrama();

	void monitorIntegracionsEliminarAntics();

	void actualitzarEstatOrgansEnviamentTable();
}
