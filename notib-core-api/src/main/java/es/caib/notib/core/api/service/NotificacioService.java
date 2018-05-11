/**
 * 
 */
package es.caib.notib.core.api.service;


import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.notib.core.api.dto.ArxiuDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.NotificacioEnviamenEstatDto;
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
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_REP')")
	public NotificacioEnviamentDto enviamentFindAmbId(
			Long enviamentId);

	/**
	 * Consulta dels events d'una notificació.
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @return els events trobats.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_REP')")
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
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_REP')")
	public List<NotificacioEventDto> eventFindAmbNotificacioIEnviament(
			Long notificacioId,
			Long enviamentId);
	
	/**
	 * Retorna l'arxiu del document de la notificació.
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @return el fitxer associat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_REP')")
	public ArxiuDto getDocumentArxiu(
			Long notificacioId);

	/**
	 * Retorna l'arxiu de la certificació d'un enviament.
	 * 
	 * @param enviamentId
	 *            Atribut id de l'enviament.
	 * @return el fitxer de certificació associat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_REP')")
	public ArxiuDto enviamentGetCertificacioArxiu(
			Long enviamentId);

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
	 * Refresca l'estat d'un enviament (datat i certificació).
	 * 
	 * @param enviamentId
	 *            Atribut id de l'enviament.
	 * @return l'estat de l'enviament.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public NotificacioEnviamenEstatDto enviamentRefrescarEstat(
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
	 * Enviar a Notific@ la certificació d'un enviament.
	 * 
	 * @param enviamentId
	 *            Atribut id de l'enviament.
	 * @param certificacioArxiu
	 *            Arxiu amb el certificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public boolean enviamentCertificacioSeu(
			Long enviamentId,
			ArxiuDto certificacioArxiu);

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
