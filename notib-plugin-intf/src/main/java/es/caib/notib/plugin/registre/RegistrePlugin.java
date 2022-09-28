package es.caib.notib.plugin.registre;

import es.caib.notib.logic.intf.dto.AsientoRegistralBeanDto;

import java.util.List;

public interface RegistrePlugin {
	
//	/**
//	 * Crea un registre de sortida
//	 *
//	 * @param registreSortida	Objecte registreSortida amb les dades a enviar de l'anotació
//	 * @param aplicacion	Aplicació que realitza el registre de sortida
//	 * @return
//	 * 		Retorna un objecte amb la resposta del regweb (data, numero i numero formatejat)
//	 *
//	 * @throws RegistrePluginException
//	 */
//	public RespostaAnotacioRegistre registrarSalida(
//			RegistreSortida registreSortida,
//			String aplicacion) throws RegistrePluginException;
	
	/**
	 * Crea un assentament registral
	 * 
	 * @param codiDir3Entitat	codi DIR3 de l'entitat
	 * @param arb	Aplicació que realitza el registre de sortida
	 * @param tipusOperacio	indicar si és un registre d'entrada o sortida
	 * @return
	 * 		Retorna un objecte amb la resposta del regweb (data, numero i numero formatejat)
	 * @throws RegistrePluginException
	 */
	RespostaConsultaRegistre salidaAsientoRegistral(
			String codiDir3Entitat, 
			AsientoRegistralBeanDto arb, 
			Long tipusOperacio,
			boolean generarJustificant);
	
	/**
	 * Recupera un registre de sortida
	 * 
	 * @param codiDir3Entitat	codi DIR3 de l'entitat
	 * @param numeroRegistreFormatat	número de l'assentament que es vol recuperar
	 * @param tipusOperacio	indicar si és un registre d'entrada o sortida
	 * @param ambAnnexos	indicar si s'han de recuperar els annexos
	 * @return
	 * 		Retorna un objecte amb la resposta del regweb (data, numero i numero formatejat)
	 * @throws RegistrePluginException
	 */
	RespostaConsultaRegistre obtenerAsientoRegistral(
			String codiDir3Entitat, 
			String numeroRegistreFormatat,
			Long tipusOperacio,
			boolean ambAnnexos);
	
	
	/**
	 * Recupera el justificant
	 * 
	 * @param codiDir3Entitat	codi DIR3 de l'entitat
	 * @param numeroRegistreFormatat	número de l'assentament que es vol recuperar
	 * @param tipusRegistre	indicar si és un registre d'entrada o sortida
	 * @return
	 * 		Retorna un objecte amb la resposta del regweb (data, numero i numero formatejat)
	 * @throws RegistrePluginException
	 */
	RespostaJustificantRecepcio obtenerJustificante(
			String codiDir3Entitat, 
			String numeroRegistreFormatat,
			long tipusRegistre);
	
	/**
	 * Recupera l'ofici extern
	 * 
	 * @param codiDir3Entitat	codi DIR3 de l'entitat
	 * @param numeroRegistreFormatat	número de l'assentament que es vol recuperar
	 * @return
	 * 		Retorna un objecte amb la resposta del regweb (data, numero i numero formatejat)
	 * @throws RegistrePluginException
	 */
	RespostaJustificantRecepcio obtenerOficioExterno(
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
	List<TipusAssumpte> llistarTipusAssumpte(String entitatcodi) throws RegistrePluginException;
	
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
	 * Retorn l'oficina virtual d'una entitat
	 * 
	 * @param entitatCodi	Codi de l'entitat de la qual es volen llistar les oficines
	 * @param nomOficinaVirtual	Nom de l'oficina virtual a recuperar
	 * @param autoritzacioValor	Tipus de registre del que es volen llistar les oficines	
	 * @return
	 * 		Retorna una llista amb les oficines de l'entitat
	 * @throws RegistrePluginException 
	 */
	public Oficina llistarOficinaVirtual(
			String entitatCodi, 
			String nomOficinaVirtual,
			Long autoritzacioValor) throws RegistrePluginException;
	
	/** Llista les oficines d'una entitat depenent dels permisos
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
	 * Llista el llibre d'una oficina
	 * 
	 * @param entitatCodi	Codi de l'entitat de la qual es vol consultar el llibre
	 * @param organismeCodi	Codi de l'organisme del que es vol consultar el llibre
	 * @return
	 * 		El llibre de l'organisme
	 * @throws RegistrePluginException 
	 */
	public Llibre llistarLlibreOrganisme(
			String entitatCodi, 
			String organismeCodi) throws RegistrePluginException;
	
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

	public static class TipusOperacio {
		public static final long NOTIFIFICACIO = 1L;
		public static final long COMUNICACIO = 2L;
	}
}
