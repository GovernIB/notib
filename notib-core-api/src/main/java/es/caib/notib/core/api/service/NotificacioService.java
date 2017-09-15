/**
 * 
 */
package es.caib.notib.core.api.service;


import java.util.Date;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.notib.core.api.dto.FitxerDto;
import es.caib.notib.core.api.dto.NotificaCertificacioArxiuTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaCertificacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaRespostaEstatDto;
import es.caib.notib.core.api.dto.NotificacioDestinatariDto;
import es.caib.notib.core.api.dto.NotificacioDestinatariEstatEnumDto;
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
	 * Dona d'alta una notificació.
	 * 
	 * @param entitatDir3Codi
	 *            Codi Dir3 de l'entitat emisora de la notificació.
	 * @param notificacio
	 *            La informació de la notificació.
	 * @return La notificació creada.
	 */
	@PreAuthorize("hasRole('NOT_APL')")
	public NotificacioDto alta(
			String entitatDir3Codi,
			NotificacioDto notificacio);

	/**
	 * Consulta l'estat d'un enviament.
	 * 
	 * @param referencia
	 *            Referència retornada per l'alta de la notificació.
	 * @return La notificació amb l'enviament especificat.
	 */
	@PreAuthorize("hasRole('NOT_APL')")
	public NotificacioDto consulta(
			String referencia);

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
	 * Consulta el fitxer associat a una notificació
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @return el fitxer associat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')  or hasRole('NOT_REP')")
	public FitxerDto findFitxer(
			Long notificacioId);
	
	/**
	 * Consulta la certificació associada a un enviament
	 * 
	 * @param referencia
	 *            referencia d'un enviament.
	 * @return el fitxer de certificació associat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')  or hasRole('NOT_REP')")
	public FitxerDto findCertificacio(
			String referencia);
	
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
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public void updateDestinatariEstat(
			String referencia,
			NotificacioDestinatariEstatEnumDto notificaEstat,
			Date notificaEstatData,
			String notificaEstatReceptorNom,
			String notificaEstatReceptorNif,
			String notificaEstatOrigen,
			String notificaEstatNumSeguiment,
			NotificacioDestinatariEstatEnumDto seuEstat);
	
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
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public void updateCertificacio(
			String referencia,
			NotificaCertificacioTipusEnumDto notificaCertificacioTipus,
			NotificaCertificacioArxiuTipusEnumDto notificaCertificacioArxiuTipus,
			String notificaCertificacioArxiuId,
			String notificaCertificacioNumSeguiment,
			Date notificaCertificacioDataActualitzacio);

	/**
	 * Prova de fer l'enviament d'una notificació pendent.
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public void enviar(
			Long notificacioId);

	/**
	 * Consulta l'estat d'un enviament a Notific@.
	 * 
	 * @param referencia
	 *            Referencia de l'enviament.
	 * @return l'estat de l'enviament.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public NotificaRespostaEstatDto consultarEstat(
			String referencia);

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
