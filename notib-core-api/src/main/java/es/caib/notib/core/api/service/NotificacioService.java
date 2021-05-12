/**
 * 
 */
package es.caib.notib.core.api.service;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.notificacio.NotificacioDatabaseDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioDtoV2;
import es.caib.notib.core.api.dto.notificacio.NotificacioTableItemDto;
import es.caib.notib.core.api.exception.JustificantException;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.exception.RegistreNotificaException;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.mail.MessagingException;
import java.util.List;

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
	NotificacioDatabaseDto create(
			Long entitatId,
			NotificacioDatabaseDto notificacio) throws RegistreNotificaException;
	
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
	@PreAuthorize("hasRole('tothom') or hasRole('NOT_ADMIN')")
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
	 * @return La notificacio amb les dades actualitzades
	 * @throws NotFoundException
	 *              Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws RegistreNotificaException
	 * 				Si hi ha hagut un error en el procés de registra/notificar
	 */
	@PreAuthorize("hasRole('tothom') or hasRole('NOT_ADMIN')")
	NotificacioDatabaseDto update(
			Long entitatId,
			NotificacioDatabaseDto notificacio,
			boolean isAdministradorEntitat) throws NotFoundException, RegistreNotificaException;
	
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
	PaginaDto<NotificacioTableItemDto> findAmbFiltrePaginat(
			Long entitatId,
			RolEnumDto rol,
			List<String> codisProcedimentsDisponibles,
			List<String> codisOrgansGestorsDisponibles,
			List<Long> codisProcedimentOrgansDisponibles,
			String organGestorCodi,
			String usuariCodi,
			NotificacioFiltreDto filtre,
			PaginacioParamsDto paginacioParams);
	
	
	/**
	 * Consulta els nivells d'administració disponibles dins DIR3.
	 * 
	 * @return Una llista amb el codi i el nom de la l'administració.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_APL')")
	public List<CodiValorDto> llistarNivellsAdministracions();
	
	
	/**
	 * Consulta les comunitats autònomes disponibles dins DIR3.
	 * 
	 * @return Una llista amb el codi i el nom de la comunitat autònoma.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_APL')")
	public List<CodiValorDto> llistarComunitatsAutonomes();
	
	/**
	 * Consulta els paisos disponibles dins DIR3.
	 * 
	 * @return Una llista amb el codi i el nom de la localitat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_APL')")
	public List<PaisosDto> llistarPaisos();
	
	
	/**
	 * Consulta les provincies.
	 * 
	 * @return Una llista amb el codi i el nom de la provincia.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_APL')")
	public List<ProvinciesDto> llistarProvincies();
	
	/**
	 * Consulta les provincies.
	 * 
	 *  @param codiCA Codi de la comunitat autònoma.
	 *  
	 * @return Una llista amb el codi i el nom de la provincia.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_APL')")
	List<ProvinciesDto> llistarProvincies(String codiCA);
	
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
	 * Retorna l'arxiu del document de la notificació.
	 *
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @param documentId
	 *            Atribut id del document.
	 * @return el fitxer associat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_CARPETA')")
	public ArxiuDto getDocumentArxiu(
			Long notificacioId,
			Long documentId);
	
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
	void enviamentRefrescarEstatRegistre(Long enviamentId);

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
	@PreAuthorize("hasRole('tothom') or hasRole('NOT_ADMIN')")
	public FitxerDto recuperarJustificant(Long notificacioId, Long entitatId, String sequence) throws JustificantException;

	/**
	 * Recuperar l'estat de la generació del justificant
	 * 
	 * @return el justificant firmat
	 * @throws JustificantException
	 */
	@PreAuthorize("hasRole('tothom') or hasRole('NOT_ADMIN')")
	public ProgresDescarregaDto justificantEstat(String sequence) throws JustificantException;

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
	 * @param denominacio
	 * 				denominacio per a filtrar
	 * @return Una llista amb les administracions cercades.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_APL')")
	List<OrganGestorDto> unitatsPerDenominacio(String denominacio);
	
	
	/**
	 * Consulta les administracions disponibles dins DIR3 a partir de tots els camps disponibles.
	 * 
	 * @return Una llista amb les administracions cercades.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_APL')")
	public List<OrganGestorDto> cercaUnitats(String codi, String denominacio, Long nivellAdministracio, Long comunitatAutonoma,
			Boolean ambOficines, Boolean esUnitatArrel, Long provincia, String municipi);

	

//	void registrarEnviamentsPendents();
//	void notificaEnviamentsRegistrats();
//	void enviamentRefrescarEstatPendents();
//	void enviamentRefrescarEstatEnviatSir();

	/**
	 * Actualitza enviaments expirats sense certificació
	 * 
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	void refrescarEnviamentsExpirats();
	
	/**
	 * Recupera l'estat actual del progrés
	 * 
	 * @return el progrés d'actualització
	 */
	@PreAuthorize("hasRole('NOT_ADMIN')")
	public ProgresActualitzacioCertificacioDto actualitzacioEnviamentsEstat();

	
	/**
	 * Guarda un document a partir del string de bytes
	 * 
	 * @return el id
	 */
	@PreAuthorize("hasRole('tothom')")
	public String guardarArxiuTemporal(String string);

	@PreAuthorize("hasRole('tothom')")
	byte[] obtenirArxiuTemporal(String arxiuGestdocId);
	@PreAuthorize("hasRole('tothom')")
	byte[] obtenirArxiuNotificacio(String arxiuGestdocId);

	@PreAuthorize("hasRole('tothom')")
	public DocumentDto consultaDocumentIMetadades(String identificador, Boolean esUuid);
	
	@PreAuthorize("hasRole('tothom')")
	public boolean validarIdCsv (String idCsv);

}