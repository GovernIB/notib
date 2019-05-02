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
	 * Llista els tipus d'assumpte d'una entitat
	 * 
	 * @param entitatcodi	Codi de l'entitat de la qual es volen llistar els tipus d'assumpte
	 * @return
	 * 		Llistat dels tipus d'assumpte
	 * @throws RegistrePluginException
	 */
	public List<TipusAssumpte> llistarTipusAssumpte(String entitatcodi) throws RegistrePluginException;
	
	/**
	 * Llista els codis d'assumpte d'una entitat i un tipus d'assumpte
	 * 
	 * @param entitatCodi	Codi Dir3 de l'entitat
	 * @param tipusAssumpte	Codi del tipus d'assumpte del qual es vol consultar els codis
	 * @return
	 * 		Retorna una llista dels codis d'assumpte
	 * @throws RegistrePluginException
	 */
	public List<CodiAssumpte> llistarCodisAssumpte(
			String entitatCodi, 
			String tipusAssumpte) throws RegistrePluginException;
	
	/**
	 * Llista les oficines d'una entitat depenent dels permisos
	 * 
	 * @param entitatCodi	Codi de l'entitat de la qual es volen llistar les oficines
	 * @param autoritzacioValor	Tipus de registre del que es volen llistar les oficines	
	 * @return
	 * 		Retorna una llista amb les oficines de l'entitat
	 * @throws RegistrePluginException 
	 */
	public List<Oficina> llistarOficines(
			String entitatCodi, 
			Long autoritzacioValor) throws RegistrePluginException;
	
	/**
	 * Llista els llibres a partir del codi d'entitat i la autorització
	 * 
	 * @param entitatCodi	Codi de l'entitat de la qual es volen llistar els llibres
	 * @param oficina		Codi de l'oficina de la qual es volen llistar els llibres
	 * @param autoritzacioValor	Tipus de registre del que es volen llistar els llibres	
	 * @return
	 * 		Retorna una llista dels llibres de l'entitat
	 * @throws RegistrePluginException 
	 */
	public List<Llibre> llistarLlibres(
			String entitatCodi, 
			String oficina,
			Long autoritzacioValor) throws RegistrePluginException;
	
	/**
	 * Llista les oficines i llibres d'una entitat
	 * 
	 * @param entitatCodi	Codi de l'entitat de la qual es volen llistar els llibres i oficines
	 * @param usuariCodi	Codi de l'usuari del que es vol consultar
	 * @param tipusRegistre	Tipus de registre (sortida/entrada)
	 * @return
	 * 		Una llista de les oficines i els seus llibres
	 * @throws RegistrePluginException 
	 */
	public List<LlibreOficina> llistarLlibresOficines(
			String entitatCodi, 
			String usuariCodi,
			Long tipusRegistre);
	
	/**
	 * Llista els òrgans d'una entitat
	 * 
	 * @param entitatCodi	Codi de l'entitat de la qual es volen llistar els organismes
	 * @return
	 * 		Retorna una llista dels organismes d'una entitat
	 * @throws RegistrePluginException 
	 */
	public List<Organisme> llistarOrganismes(
			String entitatCodi) throws RegistrePluginException;
}
