/**
 * 
 */
package es.caib.notib.core.api.service;

import java.util.List;

import javax.mail.MessagingException;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.notib.core.api.dto.ArxiuDto;
import es.caib.notib.core.api.dto.LocalitatsDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.NotificacioDtoV2;
import es.caib.notib.core.api.dto.NotificacioEnviamenEstatDto;
import es.caib.notib.core.api.dto.NotificacioErrorCallbackFiltreDto;
import es.caib.notib.core.api.dto.NotificacioEventDto;
import es.caib.notib.core.api.dto.NotificacioFiltreDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PaisosDto;
import es.caib.notib.core.api.dto.ProvinciesDto;
import es.caib.notib.core.api.dto.RegistreIdDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.exception.RegistreNotificaException;
/**
 * Declaració dels mètodes per a la consulta de notificacions i dels
 * destinataris i events associats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioService {

	/**
	 * Crea una nova notificació.
	 * 
	 * @param notificacio
	 *            Informació de la notificació a crear
	 * @return La notificació amb l'id especificat.
	 * @throws RegistreNotificaException 
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom') or hasRole('NOT_APL')")
	public List<NotificacioDto> create(
			Long entitatId,
			NotificacioDtoV2 notificacio) throws RegistreNotificaException;
	

	/**
	 * Actualitza la informació de la notificacio que tengui el mateix
	 * id que l'especificat per paràmetre.
	 * 
	 * @param procediment
	 *            Informació del procediment a modificar.
	 * @return El procediment modificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom') or hasRole('NOT_APL')")
	public NotificacioDtoV2 update(
			Long entitatId,
			NotificacioDtoV2 notificacio) throws NotFoundException;
	
	/**
	 * Consulta una notificació donat el seu id.
	 * 
	 * @param id
	 *            Atribut id de la notificació.
	 * @return La notificació amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_APL')")
	public NotificacioDtoV2 findAmbId(
			Long id,
			boolean isAdministrador);

	/**
	 * Consulta de les notificacions segons els paràmetres del filtre.
	 * 
	 * @param filtre
	 *            Paràmetres per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina amb les notificacions.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	public PaginaDto<NotificacioDto> findAmbFiltrePaginat(
			Long entitatId,
			boolean isUsuari,
			boolean isUsuariEntitat,
			boolean isSuperUsuari,
			boolean isAdministradorOrgan,
			List<String> codisProcedimentsDisponibles,
			List<String> codisProcedimentsProcessables,
			NotificacioFiltreDto filtre,
			PaginacioParamsDto paginacioParams);
	
	/**
	 * Consulta les provincies.
	 * 
	 * @return Una llista amb el codi i el nom de la provincia.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_APL')")
	public List<ProvinciesDto> llistarProvincies();
	
	/**
	 * Consulta les localitats d'una provincia.
	 * 
	 * @param codiProvincia 
	 * 				Codi de la provincia de la que es vol recuperar les localitats
	 * @return Una llista amb el codi i el nom de la localitat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_APL')")
	public List<LocalitatsDto> llistarLocalitats(String codiProvincia);
	
	/**
	 * Consulta els paisos disponibles dins DIR3.
	 * 
	 * @param codiProvincia 
	 * 				Codi de la provincia de la que es vol recuperar les localitats
	 * @return Una llista amb el codi i el nom de la localitat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_APL')")
	public List<PaisosDto> llistarPaisos();
	
	/**
	 * Consulta dels events d'una notificació.
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @return els events trobats.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	public List<NotificacioEventDto> eventFindAmbNotificacio(
			Long entitatId,
			Long notificacioId);
	
	/**
	 * Consulta l'últim event de callback d'una d'una notificació.
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @return últim event de la notificació.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER')")
	public NotificacioEventDto findUltimEventCallbackByNotificacio(Long notificacioId);

	/**
	 * Consulta dels events del destinatari d'una notificació.
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @param enviamentId
	 *            Atribut id de l'enviament.
	 * @return els destinataris trobats.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	public List<NotificacioEventDto> eventFindAmbEnviament(
			Long entitatId,
			Long notificacioId,
			Long enviamentId);
	
	/**
	 * Retorna l'arxiu del document de la notificació.
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @return el fitxer associat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	public ArxiuDto getDocumentArxiu(
			Long notificacioId);
	
	/**
	 * Retorna l'arxiu de la certificació d'un enviament.
	 * 
	 * @param enviamentId
	 *            Atribut id de l'enviament.
	 * @return el fitxer de certificació associat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	public ArxiuDto enviamentGetCertificacioArxiu(
			Long enviamentId);

	/**
	 * Prova de fer l'enviament d'una notificació pendent.
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @return true si la notificació s'ha pogut enviar o false en cas contrari.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_APL')")
	public boolean enviar(Long notificacioId);
	
	/**
	 * Registra i notifica una notificació
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @return true si la notificació s'ha pogut enviar o false en cas contrari.
	 * @throws RegistreNotificaException 
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_APL')")
	public List<RegistreIdDto> registrarNotificar(Long notificacioId) throws RegistreNotificaException;

	/**
	 * Refresca l'estat d'un enviament (datat i certificació).
	 * 
	 * @param enviamentId
	 *            Atribut id de l'enviament.
	 * @return l'estat de l'enviament.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	public NotificacioEnviamenEstatDto enviamentRefrescarEstat(
			Long entitatId,
			Long enviamentId);

	/**
	 * Marca com a processada una notificació de forma manual.
	 * 
	 * @param notificacioId
	 *            	Atribut id de la notificació que es vol processar.
	 * @param motiu
	 *         		el motiu per el que es vol marcar la notificació com a processada.
	 * @return l'estat de l'enviament.
	 * @throws MessagingException 
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	public String marcarComProcessada(
			Long notificacioId,
			String motiu) throws MessagingException;
	

	@PreAuthorize("hasRole('NOT_SUPER')")
	PaginaDto<NotificacioDto> findWithCallbackError(
			NotificacioErrorCallbackFiltreDto filtre,
			PaginacioParamsDto paginacioParams);
	
	/**
	 * Reactiva les consultes d'estat a Notifica.
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @return true si les consultes d'estat a notifica s'ha pogut reactivar o false en cas contrari.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	public boolean reactivarConsulta(Long notificacioId);
	
	/**
	 * Reactiva les consultes d'estat a SIR.
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @return true si les consultes d'estat a SIR s'ha pogut reactivar o false en cas contrari.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	public boolean reactivarSir(Long notificacioId);
	
	

	// Mètodes per cridar des de l'schedulled
	void notificacioRegistrar(Long notificacioId) throws RegistreNotificaException;
	void notificacioEnviar(Long notificacioId);
	void enviamentRefrescarEstat(Long notificacioId);
	void enviamentRefrescarEstatRegistre(Long notificacioId);

	@SuppressWarnings("rawtypes")
	List getNotificacionsPendentsRegistrar();
	@SuppressWarnings("rawtypes")
	List getNotificacionsPendentsEnviar();
	@SuppressWarnings("rawtypes")
	List getNotificacionsPendentsRefrescarEstat();
	@SuppressWarnings("rawtypes")
	List getNotificacionsPendentsRefrescarEstatRegistre();


//	void registrarEnviamentsPendents();
//	void notificaEnviamentsRegistrats();
//	void enviamentRefrescarEstatPendents();
//	void enviamentRefrescarEstatEnviatSir();
	

}
