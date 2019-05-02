package es.caib.notib.plugin.registre;

import java.util.List;

import es.caib.notib.core.api.dto.AsientoRegistralBeanDto;

public interface RegistrePlugin {
	
	/**
	 * Crea un registre de sortida
	 * 
	 * @param registreSortida	Objecte registreSortida amb les dades a enviar de l'anotació
	 * @param aplicacion	Aplicació que realitza el registre de sortida
	 * @return
	 * 		Retorna un objecte amb la resposta del regweb (data, numero i numero formatejat)
	 * 
	 * @throws RegistrePluginException
	 */
	public RespostaAnotacioRegistre registrarSalida(
			RegistreSortida registreSortida,
			String aplicacion) throws RegistrePluginException;
	
	/**
	 * Crea un registre de sortida
	 * 
	 * @param registreSortida	Objecte registreSortida amb les dades a enviar de l'anotació
	 * @param aplicacion	Aplicació que realitza el registre de sortida
	 * @return
	 * 		Retorna un objecte amb la resposta del regweb (data, numero i numero formatejat)
	 * @throws RegistrePluginException
	 */
	public RespostaConsultaRegistre comunicarAsientoRegistral(
			String codiDir3Entitat, 
			AsientoRegistralBeanDto arb, 
			Long tipusOperacio);
	
	/**
	 * Crea un registre de sortida
	 * 
	 * @param registreSortida	Objecte registreSortida amb les dades a enviar de l'anotació
	 * @param aplicacion	Aplicació que realitza el registre de sortida
	 * @return
	 * 		Retorna un objecte amb la resposta del regweb (data, numero i numero formatejat)
	 * @throws RegistrePluginException
	 */
	public RespostaConsultaRegistre salidaAsientoRegistral(
			String codiDir3Entitat, 
			AsientoRegistralBeanDto arb, 
			Long tipusOperacio);
	
	/**
	 * Crea un registre de sortida
	 * 
	 * @param registreSortida	Objecte registreSortida amb les dades a enviar de l'anotació
	 * @param aplicacion	Aplicació que realitza el registre de sortida
	 * @return
	 * 		Retorna un objecte amb la resposta del regweb (data, numero i numero formatejat)
	 * @throws RegistrePluginException
	 */
	public RespostaJustificantRecepcio obtenerJustificante(
			String codiDir3Entitat, 
			String numeroRegistreFormatat,
			long tipusRegistre);
	
	/**
	 * Crea un registre de sortida
	 * 
	 * @param registreSortida	Objecte registreSortida amb les dades a enviar de l'anotació
	 * @param aplicacion	Aplicació que realitza el registre de sortida
	 * @return
	 * 		Retorna un objecte amb la resposta del regweb (data, numero i numero formatejat)
	 * @throws RegistrePluginException
	 */
	public RespostaJustificantRecepcio obtenerOficioExterno(
			String codiDir3Entitat, 
			String numeroRegistreFormatat);
	
	/**
	 * 
	 * @param entitatcodi
	 * @return
	 * @throws RegistrePluginException
	 */
	public List<TipusAssumpte> llistarTipusAssumpte(String entitatcodi) throws RegistrePluginException;
	
	/**
	 * 
	 * @param entitatCodi
	 * @param tipusAssumpte
	 * @return
	 * @throws RegistrePluginException
	 */
	public List<CodiAssumpte> llistarCodisAssumpte(
			String entitatCodi, 
			String tipusAssumpte) throws RegistrePluginException;
	
	/**
	 * 
	 * @param entitatCodi
	 * @param autoritzacioValor
	 * @return
	 * @throws RegistrePluginException 
	 */
	public List<Oficina> llistarOficines(
			String entitatCodi, 
			Long autoritzacioValor) throws RegistrePluginException;
	
	/**
	 * 
	 * @param entitatCodi
	 * @param oficina
	 * @param autoritzacioValor
	 * @return
	 * @throws RegistrePluginException 
	 */
	public List<Llibre> llistarLlibres(
			String entitatCodi, 
			String oficina,
			Long autoritzacioValor) throws RegistrePluginException;
	
	/**
	 * 
	 * @param entitatCodi
	 * @return
	 * @throws RegistrePluginException 
	 */
	public List<Organisme> llistarOrganismes(
			String entitatCodi) throws RegistrePluginException;
}
