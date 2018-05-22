package es.caib.notib.plugin.seu;

import java.util.List;

import es.caib.notib.plugin.SistemaExternException;

/**
 * Plugin per a la comunicació amb la seu electrònica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface SeuPlugin {

	/**
	 * Creu un nou expedient a la zona personal del ciutadà si encara no ha
	 * estat creat. També es verifica si la zona personal està creada i la
	 * crea si no hi està.
	 * 
	 * @param expedientIdentificador
	 * @param unitatAdministrativa
	 * @param identificadorProcedimiento
	 * @param idioma
	 * @param descripcio
	 * @param destinatari
	 * @param representat
	 * @param bantelNumeroEntrada
	 * @param avisosHabilitats
	 * @param avisEmail
	 * @param avisMobil
	 * @return true si l'expedient s'ha creat o false si ja existia
	 * @throws SistemaExternException
	 *             Si hi ha hagut algun problema per a crear l'expedient
	 */
	public boolean comprovarExpedientCreat(
			String expedientIdentificador,
			String unitatAdministrativa,
			String identificadorProcedimiento,
			String idioma,
			String descripcio,
			SeuPersona destinatari,
			SeuPersona representat,
			String bantelNumeroEntrada,
			boolean avisosHabilitats,
			String avisEmail,
			String avisMobil) throws SistemaExternException;

	/**
	 * Crea un nou avis per a un expedient de la zona personal del ciutadà.
	 * 
	 * @param expedientIdentificador
	 * @param unitatAdministrativa
	 * @param titol
	 * @param text
	 * @param textSms
	 * @param annexos
	 * @throws SistemaExternException
	 *             Si hi ha hagut algun problema per a crear l'avis
	 */
	public void avisCrear(
			String expedientIdentificador,
			String unitatAdministrativa,
			String titol,
			String text,
			String textSms,
			List<SeuDocument> annexos) throws SistemaExternException;

	/**
	 * Envia una notificació telemàtica al ciutadà.
	 * 
	 * @param expedientIdentificador
	 * @param unitatAdministrativa
	 * @param registreOficinaCodi
	 * @param registreOrganCodi
	 * @param destinatari
	 * @param representat
	 * @param idioma
	 * @param oficiTitol
	 * @param oficiText
	 * @param avisTitol
	 * @param avisText
	 * @param avisTextSms
	 * @param confirmarRecepcio
	 * @param annexos
	 * @return les dades de la notificació creada
	 * @throws SistemaExternException
	 *             Si hi ha hagut algun problema per a crear la notificació
	 */
	public SeuNotificacioResultat notificacioCrear(
			String expedientIdentificador,
			String unitatAdministrativa,
			String registreLlibre,
			String registreOficinaCodi,
			String registreOrganCodi,
			SeuPersona destinatari,
			SeuPersona representat,
			String idioma,
			String oficiTitol,
			String oficiText,
			String avisTitol,
			String avisText,
			String avisTextSms,
			boolean confirmarRecepcio,
			List<SeuDocument> annexos) throws SistemaExternException;

	/**
	 * Obté l'estat d'una notificació telemàtica enviada al ciutadà.
	 * 
	 * @param registreNumero
	 * @return la informació sobre l'estat de la notificació
	 * @throws SistemaExternException
	 *             Si hi ha hagut algun problema per a obtenir l'estat de la notificació
	 */
	public SeuNotificacioEstat notificacioObtenirJustificantRecepcio(
			String registreNumero) throws SistemaExternException;

}
