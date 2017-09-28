/**
 * 
 */
package es.caib.notib.core.api.service;


import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.notib.core.api.dto.ArxiuDto;
import es.caib.notib.core.api.dto.NotificaRespostaEstatDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDto;
import es.caib.notib.core.api.dto.NotificacioEventDto;
import es.caib.notib.core.api.dto.NotificacioFiltreDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;

/**
 * Declaració dels mètodes per a la consulta de notificacions i dels
 * destinataris i events associats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioService {

	/**
	 * Dona d'alta una notificació.
	 * 
	 * @param entitatDir3Codi
	 *            Codi Dir3 de l'entitat emisora de la notificació.
	 * @param notificacio
	 *            La informació de la notificació.
	 * @return La notificació creada.
	 */
	/*@PreAuthorize("hasRole('NOT_APL')")
	public NotificacioDto alta(
			Long entitatId,
			NotificacioDto notificacio);*/

	/**
	 * Consulta la informació de la notificació associada a un enviament.
	 * 
	 * @param referencia
	 *            Referencia retornada per l'alta de la notificació.
	 * @return La notificació amb l'enviament especificat.
	 */
	/*@PreAuthorize("hasRole('NOT_APL')")
	public NotificacioDto findAmbEnviamentId(
			Long enviamentId);*/

	/**
	 * Consulta una notificació donat el seu id.
	 * 
	 * @param id
	 *            Atribut id de la notificació.
	 * @return La notificació amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_REP')")
	public NotificacioDto findAmbId(Long id);

	/**
	 * Consulta de les notificacions segons els paràmetres del filtre.
	 * 
	 * @param filtre
	 *            Paràmetres per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina amb les notificacions.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public PaginaDto<NotificacioDto> findAmbFiltrePaginat(
			NotificacioFiltreDto filtre,
			PaginacioParamsDto paginacioParams);

	/**
	 * Consulta de les notificacions d'una entitat segons els paràmetres
	 * del filtre.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param filtre
	 *            Paràmetres per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return la pàgina amb les notificacions trobades.
	 */
	@PreAuthorize("hasRole('NOT_REP')")
	public PaginaDto<NotificacioDto> findAmbEntitatIFiltrePaginat(
			Long entitatId,
			NotificacioFiltreDto filtre,
			PaginacioParamsDto paginacioParams);

	/**
	 * Consulta dels enviaments d'una notificació.
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @return els destinataris trobats.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_REP')")
	public List<NotificacioEnviamentDto> enviamentFindAmbNotificacio(
			Long notificacioId);

	/**
	 * Consulta d'un enviament donat el seu id.
	 * 
	 * @param enviamentId
	 *            Atribut id de l'enviament.
	 * @return el destinatari trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')  or hasRole('NOT_REP')")
	public NotificacioEnviamentDto enviamentFindAmbId(
			Long enviamentId);

	/**
	 * Consulta d'un enviament donada la seva referència.
	 * 
	 * @param referencia
	 *            Referència de l'enviament retornat per l'alta notificació.
	 * @return el destinatari trobat.
	 */
	@PreAuthorize("hasRole('NOT_APL')")
	public NotificacioEnviamentDto enviamentFindAmbReferencia(
			String referencia);

	/**
	 * Consulta dels events d'una notificació.
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @return els events trobats.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')  or hasRole('NOT_REP')")
	public List<NotificacioEventDto> eventFindAmbNotificacio(
			Long notificacioId);

	/**
	 * Consulta dels events del destinatari d'una notificació.
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @param enviamentId
	 *            Atribut id de l'enviament.
	 * @return els destinataris trobats.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')  or hasRole('NOT_REP')")
	public List<NotificacioEventDto> eventFindAmbNotificacioIEnviament(
			Long notificacioId,
			Long enviamentId);
	
	/**
	 * Consulta el fitxer associat a una notificació
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @return el fitxer associat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')  or hasRole('NOT_REP')")
	public ArxiuDto getDocumentArxiu(
			Long notificacioId);

	/**
	 * Consulta la certificació associada a un enviament
	 * 
	 * @param enviamentId
	 *            Atribut id de l'enviament.
	 * @return el fitxer de certificació associat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')  or hasRole('NOT_REP')")
	public ArxiuDto enviamentGetCertificacioArxiu(
			Long enviamentId);

	/**
	 * Actualitza l'estat d'un enviament
	 * 
	 * @param referencia
	 * 				Referencia identificadora del enviament al que volem actualitzar l'estat
	 * @param notificaEstat
	 *            	Estat de notifica.
	 * @param notificaEstatData
	 * 				Data de la darrera actualització del estat de notifica
	 * @param notificaEstatReceptorNom
	 * 				Nom del destinatari del enviament.
	 * @param notificaEstatReceptorNif
	 * 				Nif del destinatari del enviament.
	 * @param notificaEstatOrigen
	 * 				Origen del esta de notifica.
	 * @param notificaEstatNumSeguiment
	 * 				Número de seguiment del estat de notifica.
	 * @param seuEstat
	 * 				Esta del enviament en la seu.
	 */
	/*@PreAuthorize("hasRole('NOT_ADMIN')")
	public void updateDestinatariEstat(
			String referencia,
			NotificacioDestinatariEstatEnumDto notificaEstat,
			Date notificaEstatData,
			String notificaEstatReceptorNom,
			String notificaEstatReceptorNif,
			String notificaEstatOrigen,
			String notificaEstatNumSeguiment,
			NotificacioDestinatariEstatEnumDto seuEstat);*/
	
	/**
	 * Actualitza el certificat d'un enviament
	 * 
	 * @param referencia
	 * 				Referencia identificadora del enviament al que volem actualitzar l'estat.
	 * @param notificaCertificacioTipus
	 * 				El tipus de certificació.
	 * @param notificaCertificacioArxiuTipus
	 * 				El format del fitxer de certificació.
	 * @param notificaCertificacioArxiuId
	 * 				Identificador del fitxer de certificació.
	 * @param notificaCertificacioNumSeguiment
	 * 				Número de seguiment de la certificació.
	 * @param notificaCertificacioDataActualitzacio
	 * 				Data de actualització de la certificació.
	 */
	/*@PreAuthorize("hasRole('NOT_ADMIN')")
	public void updateCertificacio(
			String referencia,
			NotificaCertificacioTipusEnumDto notificaCertificacioTipus,
			NotificaCertificacioArxiuTipusEnumDto notificaCertificacioArxiuTipus,
			String notificaCertificacioArxiuId,
			String notificaCertificacioNumSeguiment,
			Date notificaCertificacioDataActualitzacio);*/

	/**
	 * Prova de fer l'enviament d'una notificació pendent.
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @return true si la notificació s'ha pogut enviar o false en cas contrari.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public boolean enviar(
			Long notificacioId);

	/**
	 * Refresca l'estat d'un enviament.
	 * 
	 * @param enviamentId
	 *            Atribut id de l'enviament.
	 * @return l'estat de l'enviament.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public NotificaRespostaEstatDto enviamentRefrescarEstat(
			Long enviamentId);

	/**
	 * Comunica a Notific@ la lectura en seu d'un enviament.
	 * 
	 * @param enviamentId
	 *            Atribut id de l'enviament.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public boolean enviamentComunicacioSeu(
			Long enviamentId);

	/**
	 * Mètode d'execució periòdica per a fer els enviaments pendents
	 * a Notific@.
	 */
	public void notificaEnviamentsPendents();

	/**
	 * Mètode d'execució periòdica per a fer els enviaments pendents
	 * a la seu.
	 */
	public void seuEnviamentsPendents();

	/**
	 * Mètode d'execució periòdica per a refrescar l'estat de les notificacions
	 * pendents a la seu.
	 */
	public void seuNotificacionsPendents();

	/**
	 * Mètode d'execució periòdica per a comunicar a Notifica els canvis d'estat
	 * de les notificacions de la seu.
	 */
	public void seuNotificaComunicarEstatPendents();

}
