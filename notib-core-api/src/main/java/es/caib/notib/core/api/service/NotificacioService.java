/**
 * 
 */
package es.caib.notib.core.api.service;

import java.util.List;

import javax.mail.MessagingException;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.notib.core.api.dto.ArxiuDto;
import es.caib.notib.core.api.dto.DocumentDto;
import es.caib.notib.core.api.dto.FitxerDto;
import es.caib.notib.core.api.dto.LocalitatsDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.NotificacioDtoV2;
import es.caib.notib.core.api.dto.NotificacioEnviamenEstatDto;
import es.caib.notib.core.api.dto.NotificacioErrorCallbackFiltreDto;
import es.caib.notib.core.api.dto.NotificacioEventDto;
import es.caib.notib.core.api.dto.NotificacioFiltreDto;
import es.caib.notib.core.api.dto.NotificacioRegistreErrorFiltreDto;
import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PaisosDto;
import es.caib.notib.core.api.dto.ProgresDescarregaDto;
import es.caib.notib.core.api.dto.ProgresActualitzacioCertificacioDto;
import es.caib.notib.core.api.dto.ProvinciesDto;
import es.caib.notib.core.api.dto.RegistreIdDto;
import es.caib.notib.core.api.exception.JustificantException;
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
	public NotificacioDtoV2 create(
			Long entitatId,
			NotificacioDtoV2 notificacio) throws RegistreNotificaException;
	
	/**
	 * Esborra la notificació indicada per paràmetre
	 * 
	 * @param entitatId
	 *            Id de l'entitat actual
	 * @param notificacioId
	 *            Id de la notificació a eliminar
	 *            
	 * @return La llista de notificacions actualitzada
	 * @throws NotFoundException
	 *              Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void delete(
			Long entitatId,
			Long notificacioId) throws NotFoundException;
	
	/**
	 * Actualitza la informació de la notificacio que tengui el mateix
	 * id que l'especificat per paràmetre.
	 * 
	 * @param entitatId
	 *            Id de l'entitat actual
	 * @param notificacio
	 *            Informació de la notificació a modificar
	 *            
	 * @return La llista de notificacions actualitzada
	 * @throws NotFoundException
	 *              Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws RegistreNotificaException
	 * 				Si hi ha hagut un error en el procés de registra/notificar
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<NotificacioDto> update(
			Long entitatId,
			NotificacioDtoV2 notificacio) throws NotFoundException, RegistreNotificaException;
	
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
			List<String> codisOrgansGestorsDisponibles,
			List<Long> codisProcedimentOrgansDisponibles,
			String organGestorCodi,
			String usuariCodi,
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

	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER')")
	public NotificacioEventDto findUltimEventRegistreByNotificacio(Long notificacioId);
	
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
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_CARPETA')")
	public ArxiuDto getDocumentArxiu(
			Long notificacioId);
	
	/**
	 * Retorna l'arxiu de la certificació d'un enviament.
	 * 
	 * @param enviamentId
	 *            Atribut id de l'enviament.
	 * @return el fitxer de certificació associat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_CARPETA')")
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
			String motiu) throws Exception;
	

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

	@PreAuthorize("hasRole('NOT_ADMIN')")
	public PaginaDto<NotificacioDto> findNotificacionsAmbErrorRegistre(
			Long entitatId,
			NotificacioRegistreErrorFiltreDto filtre,
			PaginacioParamsDto paginacioDtoFromRequest);
	
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public List<Long> findNotificacionsIdAmbErrorRegistre(
			Long entitatId,
			NotificacioRegistreErrorFiltreDto filtre);

	@PreAuthorize("hasRole('NOT_ADMIN')")
	public void reactivarRegistre(Long notificacioId);

	/**
	 * Genera un justificant d'enviament
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @return el justificant firmat
	 * @throws JustificantException
	 */
	@PreAuthorize("hasRole('tothom')")
	public FitxerDto recuperarJustificant(Long notificacioId, Long entitatId) throws JustificantException;

	/**
	 * Recuperar l'estat de la generació del justificant
	 * 
	 * @return el justificant firmat
	 * @throws JustificantException
	 */
	@PreAuthorize("hasRole('tothom')")
	public ProgresDescarregaDto justificantEstat() throws JustificantException;

	/**
	 * Consulta les administracions disponibles dins DIR3 a partir del codi.
	 * 
	 * @param text 
	 * 				Text per la cerca
	 * @return Una llista amb les administracions cercades.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_APL')")
	List<OrganGestorDto> unitatsPerCodi(String text);

	/**
	 * Consulta les administracions disponibles dins DIR3 a partir de la denominació.
	 * 
	 * @param text 
	 * 				Text per la cerca
	 * @return Una llista amb les administracions cercades.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_APL')")
	List<OrganGestorDto> unitatsPerDenominacio(String denominacio);
	

//	void registrarEnviamentsPendents();
//	void notificaEnviamentsRegistrats();
//	void enviamentRefrescarEstatPendents();
//	void enviamentRefrescarEstatEnviatSir();

	/**
	 * Actualitza enviaments expirats sense certificació
	 * 
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public void enviamentsRefrescarEstat();
	
	/**
	 * Recupera l'estat actual del progrés
	 * 
	 * @return el progrés d'actualització
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public ProgresActualitzacioCertificacioDto actualitzacioEnviamentsEstat();

	@PreAuthorize("hasRole('tothom')")
	public String guardarArxiuTemporal(String string);


	

}
