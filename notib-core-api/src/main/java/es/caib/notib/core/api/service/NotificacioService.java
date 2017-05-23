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
 * Declaració dels mètodes per a la gestió d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioService {


	/**
	 * Consulta una entitat donat el seu id.
	 * 
	 * @param id
	 *            Atribut id de l'entitat a trobar.
	 * @return L'entitat amb l'id especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_REP')")
	public NotificacioDto findById(Long id);

	/**
	 * Llistat amb totes les entitats paginades.
	 * 
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina d'Entitats.
	 */
	
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public PaginaDto<NotificacioDto> findFilteredByEntitatAndUsuari(
			NotificacioFiltreDto filtre,
			PaginacioParamsDto paginacioParams
			);
	
	@PreAuthorize("hasRole('NOT_REP')")
	public PaginaDto<NotificacioDto> findByEntitat(
			Long entitatId,
			NotificacioFiltreDto filtre,
			PaginacioParamsDto paginacioParams
			);
	
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_REP')")
	public PaginaDto<NotificacioDestinatariDto> findDestinatarisByNotificacioId(
			Long notificacioId,
			PaginacioParamsDto paginacioParams
			);
	
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_REP')")
	public List<NotificacioDestinatariDto> findDestinatarisByNotificacioId(
			Long notificacioId
			);
	
	@PreAuthorize("hasRole('NOT_ADMIN')  or hasRole('NOT_REP')")
	public List<NotificacioEventDto> findEventsByNotificacioId(
			Long notificacioId
			);
	
	@PreAuthorize("hasRole('NOT_ADMIN')  or hasRole('NOT_REP')")
	public List<NotificacioEventDto> findEventsByDestinatariId(
			Long destinatariId
			);
	
	@PreAuthorize("hasRole('NOT_ADMIN')  or hasRole('NOT_REP')")
	public NotificacioDestinatariDto findDestinatariById(
			Long destinatariId
			);
	
	@PreAuthorize("hasRole('NOT_APL')")
	public NotificacioDestinatariDto findDestinatariByReferencia(
			String referencia
			);

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
