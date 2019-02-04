package es.caib.notib.core.helper;

import java.util.List;

import es.caib.notib.core.api.ws.registre.RegistreEntrada;
import es.caib.notib.core.api.ws.registre.RegistreNotificacio;
import es.caib.notib.core.api.ws.registre.RegistreSortida;
import es.caib.notib.core.api.ws.registre.RespostaAnotacioRegistre;
import es.caib.notib.core.api.ws.registre.RespostaConsulta;
import es.caib.notib.core.api.ws.registre.RespostaJustificantRecepcio;
import es.caib.notib.core.api.ws.registre.TipusDocumentRegistre;
import es.caib.notib.plugin.registre.sortida.RegistrePluginException;


/**
 * Interfície per a la integració amb el registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RegistrePlugin {

	/**
	 * Crea un registre d'entrada
	 * 
	 * @param dadesRegistre
	 * @return
	 * @throws RegistrePluginException
	 */
	public RespostaAnotacioRegistre registrarEntrada(
			RegistreEntrada registreEntrada) throws RegistrePluginException;

	/**
	 * Consulta un registre d'entrada
	 * 
	 * @param numeroRegistre
	 * @return
	 * @throws RegistrePluginException
	 */
	public RespostaConsulta consultarEntrada(
			String organCodi,
			String oficinaCodi,
			String numeroRegistre) throws RegistrePluginException;

	/**
	 * Crea un registre de sortida
	 * 
	 * @param dadesRegistre
	 * @return
	 * @throws RegistrePluginException
	 */
	public RespostaAnotacioRegistre registrarSortida(
			RegistreSortida registreSortida) throws RegistrePluginException;

	/**
	 * Consulta un registre de sortida
	 * 
	 * @param numeroRegistre
	 * @return
	 * @throws RegistrePluginException
	 */
	public RespostaConsulta consultarSortida(
			String organCodi,
			String oficinaCodi,
			String numeroRegistre) throws RegistrePluginException;

	/**
	 * Crea una notificació telemàtica
	 * 
	 * @param dadesRegistre
	 * @return
	 * @throws RegistrePluginException
	 */
	public RespostaAnotacioRegistre registrarNotificacio(
			RegistreNotificacio registreNotificacio) throws RegistrePluginException;

	/**
	 * Obté l'acus de rebut per a una notificació telemàtica
	 * 
	 * @param numeroRegistre
	 * @return la data del justificant de recepció o null si encara no s'ha justificat
	 * @throws RegistrePluginException
	 */
	public RespostaJustificantRecepcio obtenirJustificantRecepcio(
			String numeroRegistre) throws RegistrePluginException;

	/**
	 * Obté el nom de l'oficina a partir del codi de l'oficina
	 * 
	 * @param oficinaCodi
	 * @return el nom de l'oficina
	 * @throws RegistrePluginException
	 */
	public String obtenirNomOficina(String oficinaCodi) throws RegistrePluginException;

	/**
	 * Obté els tipus de documents de registre
	 * @return
	 * @throws RegistrePluginException
	 */
	public List<TipusDocumentRegistre> obtenirTipusDocumentsRegistre() throws RegistrePluginException;
	
}
