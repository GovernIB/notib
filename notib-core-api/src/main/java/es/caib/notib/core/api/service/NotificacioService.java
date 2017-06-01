/**
 * 
 */
package es.caib.notib.core.api.service;


import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.notib.core.api.dto.NotificacioDestinatariDto;
import es.caib.notib.core.api.dto.NotificacioDto;
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
	 * Consulta una entitat donat el seu id.
	 * 
	 * @param id
	 *            Atribut id de la notificació.
	 * @return La notificació amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_REP')")
	public NotificacioDto findById(Long id);

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
	public PaginaDto<NotificacioDto> findByFiltrePaginat(
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
	public PaginaDto<NotificacioDto> findByEntitatIFiltrePaginat(
			Long entitatId,
			NotificacioFiltreDto filtre,
			PaginacioParamsDto paginacioParams);

	/**
	 * Consulta dels destinataris d'una notificació.
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return la pàgina amb els destinataris trobats.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_REP')")
	public PaginaDto<NotificacioDestinatariDto> destinatariFindByNotificacioPaginat(
			Long notificacioId,
			PaginacioParamsDto paginacioParams);

	/**
	 * Consulta dels destinataris d'una notificació.
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @return els destinataris trobats.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_REP')")
	public List<NotificacioDestinatariDto> destinatariFindByNotificacio(
			Long notificacioId);

	/**
	 * Consulta d'un destinatari donat el seu id.
	 * 
	 * @param destinatariId
	 *            Atribut id del destinatari.
	 * @return el destinatari trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')  or hasRole('NOT_REP')")
	public NotificacioDestinatariDto destinatariFindById(
			Long destinatariId);

	/**
	 * Consulta d'un destinatari donada la seva referència.
	 * 
	 * @param referencia
	 *            Referència que identifica al destinatari.
	 * @return el destinatari trobat.
	 */
	@PreAuthorize("hasRole('NOT_APL')")
	public NotificacioDestinatariDto destinatariFindByReferencia(
			String referencia);

	/**
	 * Consulta dels events d'una notificació.
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @return els events trobats.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')  or hasRole('NOT_REP')")
	public List<NotificacioEventDto> eventFindByNotificacio(
			Long notificacioId);

	/**
	 * Consulta dels events del destinatari d'una notificació.
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @param destinatariId
	 *            Atribut id del destinatari.
	 * @return els destinataris trobats.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')  or hasRole('NOT_REP')")
	public List<NotificacioEventDto> eventFindByNotificacioIDestinatari(
			Long notificacioId,
			Long destinatariId);

	/**
	 * Mètode d'execució periòdica per a fer els enviaments pendents
	 * a la seu.
	 */
	public void seuEnviamentsPendents();

	/**
	 * Mètode d'execució periòdica per a refrescar l'estat de les notificacions
	 * pendents a la seu.
	 */
	public void seuJustificantsPendents();

	/**
	 * Mètode d'execució periòdica per a comunicar a Notifica els canvis d'estat
	 * de les notificacions de la seu.
	 */
	public void seuNotificaComunicarEstatPendents();

}
