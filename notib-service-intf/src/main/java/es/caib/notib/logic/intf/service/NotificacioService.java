/**
 * 
 */
package es.caib.notib.logic.intf.service;

import es.caib.notib.logic.intf.dto.ArxiuDto;
import es.caib.notib.logic.intf.dto.CodiValorDto;
import es.caib.notib.logic.intf.dto.DocCieValid;
import es.caib.notib.logic.intf.dto.DocumentDto;
import es.caib.notib.logic.intf.dto.LocalitatsDto;
import es.caib.notib.logic.intf.dto.NotificacioAuditDto;
import es.caib.notib.logic.intf.dto.NotificacioEnviamenEstatDto;
import es.caib.notib.logic.intf.dto.NotificacioEnviamentAuditDto;
import es.caib.notib.logic.intf.dto.NotificacioErrorCallbackFiltreDto;
import es.caib.notib.logic.intf.dto.NotificacioEventDto;
import es.caib.notib.logic.intf.dto.NotificacioRegistreErrorFiltreDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.PaisosDto;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioCertificacioDto;
import es.caib.notib.logic.intf.dto.ProvinciesDto;
import es.caib.notib.logic.intf.dto.RespostaAccio;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.SignatureInfoDto;
import es.caib.notib.logic.intf.dto.notificacio.Notificacio;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDtoV2;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioFiltreDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioInfoDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioTableItemDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.IOException;
import java.util.List;
import java.util.Set;

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
	 * @param notificacio Informació de la notificació a crear
	 * @return La notificació amb l'id especificat.
	 * @throws RegistreNotificaException
	 */
	@PreAuthorize("isAuthenticated()")
	Notificacio create(Long entitatId, Notificacio notificacio) throws RegistreNotificaException;

	/**
	 * Esborra la notificació indicada per paràmetre
	 * 
	 * @param entitatId Id de l'entitat actual
	 * @param notificacioId Id de la notificació a eliminar
	 *            
	 * @return La llista de notificacions actualitzada
	 * @throws NotFoundException Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	void delete(Long entitatId, Long notificacioId) throws NotFoundException;

	/**
	 * Recupera la notificació indicada per paràmetre
	 *
	 * @param entitatId Id de l'entitat actual
	 * @param notificacioId Id de la notificació a eliminar
	 *
	 * @return La llista de notificacions actualitzada
	 * @throws NotFoundException Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	void restore(Long entitatId, Long notificacioId) throws NotFoundException, Exception;
	
	/**
	 * Actualitza la informació de la notificacio que tengui el mateix
	 * id que l'especificat per paràmetre.
	 * 
	 * @param entitatId Id de l'entitat actual
	 * @param notificacio Informació de la notificació a modificar
	 *            
	 * @return La notificacio amb les dades actualitzades
	 * @throws NotFoundException Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws RegistreNotificaException Si hi ha hagut un error en el procés de registra/notificar
	 */
	@PreAuthorize("isAuthenticated()")
	Notificacio update(Long entitatId, Notificacio notificacio, boolean isAdministradorEntitat) throws NotFoundException, RegistreNotificaException;
	
	/**
	 * Consulta una notificació donat el seu id.
	 * 
	 * @param id
	 *            Atribut id de la notificació.
	 * @return La notificació amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	NotificacioDtoV2 findAmbId(Long id, boolean isAdministrador);

	/**
	 * Consulta una notificació donat el seu id.
	 *
	 * @param id
	 *            Atribut id de la notificació.
	 * @return La notificació amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	NotificacioInfoDto findNotificacioInfo(Long id, boolean isAdministrador);

	/**
	 * Consulta de les notificacions segons els paràmetres del filtre.
	 * 
	 * @param filtre
	 *            Paràmetres per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina amb les notificacions.
	 */
	@PreAuthorize("isAuthenticated()")
	PaginaDto<NotificacioTableItemDto> findAmbFiltrePaginat(Long entitatId, RolEnumDto rol, String organGestorCodi, String usuariCodi, NotificacioFiltreDto filtre, PaginacioParamsDto paginacioParams);

	@PreAuthorize("isAuthenticated()")
	List<Long> findIdsAmbFiltre(Long entitatId, RolEnumDto rol, String organGestorCodi, String usuariCodi, NotificacioFiltreDto filtre);
	
	/**
	 * Consulta els nivells d'administració disponibles dins DIR3.
	 * 
	 * @return Una llista amb el codi i el nom de la l'administració.
	 */
	@PreAuthorize("isAuthenticated()")
	List<CodiValorDto> llistarNivellsAdministracions();
	
	
	/**
	 * Consulta les comunitats autònomes disponibles dins DIR3.
	 * 
	 * @return Una llista amb el codi i el nom de la comunitat autònoma.
	 */
	@PreAuthorize("isAuthenticated()")
	List<CodiValorDto> llistarComunitatsAutonomes();
	
	/**
	 * Consulta els paisos disponibles dins DIR3.
	 * 
	 * @return Una llista amb el codi i el nom de la localitat.
	 */
	@PreAuthorize("isAuthenticated()")
	List<PaisosDto> llistarPaisos();
	
	
	/**
	 * Consulta les provincies.
	 * 
	 * @return Una llista amb el codi i el nom de la provincia.
	 */
	@PreAuthorize("isAuthenticated()")
	List<ProvinciesDto> llistarProvincies();
	
	/**
	 * Consulta les provincies.
	 * 
	 *  @param codiCA Codi de la comunitat autònoma.
	 *  
	 * @return Una llista amb el codi i el nom de la provincia.
	 */
	@PreAuthorize("isAuthenticated()")
	List<ProvinciesDto> llistarProvincies(String codiCA);
	
	/**
	 * Consulta les localitats d'una provincia.
	 * 
	 * @param codiProvincia 
	 * 				Codi de la provincia de la que es vol recuperar les localitats
	 * @return Una llista amb el codi i el nom de la localitat.
	 */
	@PreAuthorize("isAuthenticated()")
	List<LocalitatsDto> llistarLocalitats(String codiProvincia);
	
	
	
	/**
	 * Consulta dels events d'una notificació.
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @return els events trobats.
	 */
	@PreAuthorize("isAuthenticated()")
	List<NotificacioEventDto> eventFindAmbNotificacio(Long entitatId, Long notificacioId);

	@PreAuthorize("isAuthenticated()")
	List<NotificacioAuditDto> historicFindAmbNotificacio(Long entitatId, Long notificacioId);
	
	/**
	 * Consulta l'últim event de callback d'una d'una notificació.
	 * 
	 * @param notificacioId Atribut id de la notificació.
	 * @return últim event de la notificació.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER')")
	NotificacioEventDto findUltimEventCallbackByNotificacio(Long notificacioId);

	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER')")
	NotificacioEventDto findUltimEventRegistreByNotificacio(Long notificacioId);
	
	/**
	 * Consulta dels events del destinatari d'una notificació.
	 * 
	 * @param notificacioId Atribut id de la notificació.
	 * @param enviamentId Atribut id de l'enviament.
	 * @return els destinataris trobats.
	 */
	@PreAuthorize("isAuthenticated()")
	List<NotificacioEventDto> eventFindAmbEnviament(Long entitatId, Long notificacioId, Long enviamentId);

	@PreAuthorize("isAuthenticated()")
	List<NotificacioEnviamentAuditDto> historicFindAmbEnviament(Long entitatId, Long notificacioId, Long enviamentId);

	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER')")
	byte[] getDiagramaMaquinaEstats() throws IOException;
	
	/**
	 * Retorna l'arxiu del document de la notificació.
	 * 
	 * @param notificacioId Atribut id de la notificació.
	 * @return el fitxer associat.
	 */
	@PreAuthorize("isAuthenticated()")
	ArxiuDto getDocumentArxiu(Long notificacioId);

	/**
	 * Retorna l'arxiu del document de la notificació.
	 *
	 * @param notificacioId Atribut id de la notificació.
	 * @param documentId Atribut id del document.
	 * @return el fitxer associat.
	 */
	@PreAuthorize("isAuthenticated()")
	ArxiuDto getDocumentArxiu(Long notificacioId, Long documentId);
	
	/**
	 * Retorna l'arxiu de la certificació d'un enviament.
	 * 
	 * @param enviamentId Atribut id de l'enviament.
	 * @return el fitxer de certificació associat.
	 */
	@PreAuthorize("isAuthenticated()")
	ArxiuDto enviamentGetCertificacioArxiu(Long enviamentId);

	@PreAuthorize("isAuthenticated()")
    void refrescarEstatEnviamentASir(Long enviamentId, boolean retry);

    /**
	 * Prova de fer l'enviament d'una notificació pendent.
	 * 
	 * @param notificacioId Atribut id de la notificació.
	 * @return true si la notificació s'ha pogut enviar o false en cas contrari.
	 */
	@PreAuthorize("isAuthenticated()")
	boolean enviarNotificacioANotifica(Long notificacioId, boolean retry);

	/**
	 * Reseteja els intents de les consultes a Notifica@ o SIR.
	 *
	 * @param ids Identificadors dels enviaments.
	 * @return true si la notificació s'ha pogut enviar o false en cas contrari.
	 */
	@PreAuthorize("isAuthenticated()")
	boolean resetConsultaEstat(Set<Long> ids);
	
	/**
	 * Registra i notifica una notificació
	 * 
	 * @param notificacioId Atribut id de la notificació.
	 * @return true si la notificació s'ha pogut enviar o false en cas contrari.
	 * @throws RegistreNotificaException 
	 */
	@PreAuthorize("isAuthenticated()")
	RespostaAccio<String> enviarNotificacioARegistre(Long notificacioId, boolean retry) throws RegistreNotificaException;

	/**
	 * Reseteja els intents de regitre.
	 * Registra i notifica una notificació
	 *
	 * @param notificacioId Atribut id de la notificació.
	 * @return true si la notificació s'ha pogut enviar o false en cas contrari.
	 */
	@PreAuthorize("isAuthenticated()")
	RespostaAccio<String> resetNotificacioARegistre(Long notificacioId);

	/**
	 * Refresca l'estat d'un enviament (datat i certificació).
	 * 
	 * @param enviamentId Atribut id de l'enviament.
	 * @return l'estat de l'enviament.
	 */
	@PreAuthorize("isAuthenticated()")
	NotificacioEnviamenEstatDto enviamentRefrescarEstat(Long entitatId, Long enviamentId);

	/**
	 * Marca com a processada una notificació de forma manual.
	 * 
	 * @param notificacioId Atribut id de la notificació que es vol processar.
	 * @param motiu el motiu per el que es vol marcar la notificació com a processada.
	 * @param isAdministrador Indica si l'usuari actual és administrador d'entitat
	 * @return l'estat de l'enviament.
	 * @throws Exception
	 */
	@PreAuthorize("isAuthenticated()")
	String marcarComProcessada(Long notificacioId, String motiu, boolean isAdministrador) throws Exception;

	@PreAuthorize("hasRole('NOT_SUPER')")
	PaginaDto<NotificacioDto> findWithCallbackError(NotificacioErrorCallbackFiltreDto filtre, PaginacioParamsDto paginacioParams);
	/**
	 * Reactiva les consultes d'estat a Notifica.
	 * 
	 * @param notificacioId Atribut id de la notificació.
	 * @return true si les consultes d'estat a notifica s'ha pogut reactivar o false en cas contrari.
	 */
	@PreAuthorize("isAuthenticated()")
	boolean reactivarConsulta(Long notificacioId);
	
	/**
	 * Reactiva les consultes d'estat a SIR.
	 * 
	 * @param notificacioId Atribut id de la notificació.
	 * @return true si les consultes d'estat a SIR s'ha pogut reactivar o false en cas contrari.
	 */
	@PreAuthorize("isAuthenticated()")
	boolean reactivarSir(Long notificacioId);
	
	

	// Mètodes per cridar des de l'schedulled
	void notificacioEnviar(Long notificacioId);
	void enviamentRefrescarEstat(Long notificacioId);
	void enviamentRefrescarEstatRegistre(Long enviamentId);
	Boolean enviamentRefrescarEstatSir(Long enviamentId);

	List<Long> getNotificacionsPendentsRegistrar();
	List<Long> getNotificacionsPendentsEnviar();
	List<Long> getNotificacionsPendentsRefrescarEstat();
	List<Long> getNotificacionsDEHPendentsRefrescarCert();
	List<Long> getNotificacionsCIEPendentsRefrescarCert();
	List<Long> getNotificacionsPendentsRefrescarEstatRegistre();

	@PreAuthorize("hasRole('NOT_ADMIN')")
	PaginaDto<NotificacioDto> findNotificacionsAmbErrorRegistre(Long entitatId, NotificacioRegistreErrorFiltreDto filtre, PaginacioParamsDto paginacioDtoFromRequest);
	
	@PreAuthorize("hasRole('NOT_ADMIN')")
	List<Long> findNotificacionsIdAmbErrorRegistre(Long entitatId, NotificacioRegistreErrorFiltreDto filtre);

	@PreAuthorize("hasRole('NOT_ADMIN')")
	void reactivarRegistre(Long notificacioId);

	@PreAuthorize("hasRole('NOT_ADMIN')")
	void reenviarNotificaionsMovil(Long notificacioId);

	/**
	 * Consulta les administracions disponibles dins DIR3 a partir del codi.
	 * 
	 * @param text Text per la cerca
	 * @return Una llista amb les administracions cercades.
	 */
	@PreAuthorize("isAuthenticated()")
	List<OrganGestorDto> unitatsPerCodi(String text);

	/**
	 * Consulta les administracions disponibles dins DIR3 a partir de la denominació.
	 * 
	 * @param denominacio denominacio per a filtrar
	 * @return Una llista amb les administracions cercades.
	 */
	@PreAuthorize("isAuthenticated()")
	List<OrganGestorDto> unitatsPerDenominacio(String denominacio);
	
	
	/**
	 * Consulta les administracions disponibles dins DIR3 a partir de tots els camps disponibles.
	 * 
	 * @return Una llista amb les administracions cercades.
	 */
	@PreAuthorize("isAuthenticated()")
	List<OrganGestorDto> cercaUnitats(String codi, String denominacio, Long nivellAdministracio, Long comunitatAutonoma,
			Boolean ambOficines, Boolean esUnitatArrel, Long provincia, String municipi);

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
	ProgresActualitzacioCertificacioDto actualitzacioEnviamentsEstat();

	@PreAuthorize("isAuthenticated()")
	DocumentDto consultaDocumentIMetadades(String identificador, Boolean esUuid);
	
	@PreAuthorize("isAuthenticated()")
	boolean validarIdCsv (String idCsv);

	@PreAuthorize("isAuthenticated()")
	boolean validarFormatCsv (String csv);

	/**
	 * Actualitza el camp referencies de la taula NOT_NOTIFICACIO si es null
	 * Actualitza el camp NOTIFICA_REFERENCIA de la taula NOT_NOTIFICACIO_ENV si es null
	 * @return true si ok false altrament.
	 */
	@PreAuthorize("hasRole('NOT_SUPER')")
	void actualitzarReferencies();

	@PreAuthorize("isAuthenticated()")
	boolean reenviarNotificacioAmbErrors(Long notificacioId);

	@PreAuthorize("isAuthenticated()")
	boolean reactivarNotificacioAmbErrors(Set<Long> notificacioId);

	@PreAuthorize("isAuthenticated()")
	boolean reactivarNotificacioAmbErrors(Long notificacioId);

	@PreAuthorize("isAuthenticated()")
	SignatureInfoDto checkIfSignedAttached(byte[] contingut, String nom, String contentType);

	@PreAuthorize("isAuthenticated()")
	DocCieValid validateDocCIE(byte[] bytes) throws IOException;

    @PreAuthorize("isAuthenticated()")
	void updateEstatList(Long notificacioId);
}