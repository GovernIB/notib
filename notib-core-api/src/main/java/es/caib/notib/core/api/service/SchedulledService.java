/**
 * 
 */
package es.caib.notib.core.api.service;

import es.caib.notib.core.api.exception.RegistreNotificaException;

/**
 * Declaració dels mètodes per a la consulta de notificacions i dels
 * destinataris i events associats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface SchedulledService {

	/**
	 * Mètode d'execució periòdica per a fer els enviaments pendents
	 * a Notific@.
	 */
	public void notificaEnviamentsRegistrats();
	
	/**
	 * Mètode d'execució periòdica per a fer els enviaments pendents
	 * al registre.
	 * @throws RegistreNotificaException 
	 */
	public void registrarEnviamentsPendents() throws RegistreNotificaException;

	/**
	 * Mètode d'execució periòdica per a refrescar l'estat dels enviaments fets a
	 * Notific@.
	 */
	public void enviamentRefrescarEstatPendents();
	
	/**
	 * Mètode d'execució periòdica per a refrescar l'estat dels enviaments fets a
	 * Registre (comunicació SIR)
	 */
	public void enviamentRefrescarEstatEnviatSir();
	
	/**
	 * Mètode d'execució periòdica per a actualitzar els procediments a partir de les dades de Rolsac
	 */
	public void actualitzarProcediments();

}
