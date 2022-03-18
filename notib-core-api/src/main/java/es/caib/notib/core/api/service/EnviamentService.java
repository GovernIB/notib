/**
 * 
 */
package es.caib.notib.core.api.service;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.notenviament.ColumnesDto;
import es.caib.notib.core.api.dto.notenviament.NotEnviamentTableItemDto;
import es.caib.notib.core.api.dto.notenviament.NotificacioEnviamentDatatableDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.rest.consulta.Resposta;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Declaració dels mètodes per a la consulta de notificacions i dels
 * destinataris i events associats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EnviamentService {


	/**
	 * Consulta la llista d'ids dels enviaments segons el filtre.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param filtre
	 *            Filtre per a la consulta.
	 * @return La llista amb els ids dels expedients.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_APL')")
	public List<Long> findIdsAmbFiltre(
			Long entitatId,
			NotificacioEnviamentFiltreDto filtre) throws NotFoundException, ParseException;

	/**
	 * Consulta dels enviaments d'una entitat realitzats d'una notificació.
	 * 
	 * @param entitatId
	 * 			Identificador de l'entitat de la que es vol consultar els enviaments
	 * @param filtre
	 * 			Filtre per a la consulta
	 * @param paginacio
	 * @return Pàgina d'enviaments
	 * 
	 * @throws ParseException
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	PaginaDto<NotEnviamentTableItemDto> enviamentFindByEntityAndFiltre(
			Long entitatId,
			RolEnumDto rol,
			String organGestorCodi,
			String usuariCodi,
			NotificacioEnviamentFiltreDto filtre,
			PaginacioParamsDto paginacio) throws ParseException;
	
	/**
	 * Consulta dels enviaments d'una notificació.
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @return els destinataris trobats.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_APL')")
	List<NotificacioEnviamentDatatableDto> enviamentFindAmbNotificacio(Long notificacioId);

	/**
	 * Consulta els identificadors dels enviaments d'un conjunt de notificacions
	 *
	 * @param notificacionsIds  Atribut id de la notificació.
	 * @return els identificadors dels enviaments.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_APL')")
	Set<Long> findIdsByNotificacioIds(Collection<Long> notificacionsIds);

	/**
	 * Consulta d'un enviament donat el seu id.
	 * 
	 * @param enviamentId
	 *            Atribut id de l'enviament.
	 * @return el destinatari trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_APL')")
	NotificacioEnviamentDto enviamentFindAmbId(Long enviamentId);

	/**
	 * Consulta dels events d'una notificació.
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @return els events trobats.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	List<NotificacioEventDto> eventFindAmbNotificacio(Long notificacioId);
	
	/**
	 * Reintenta un callback fallat
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @return els events trobats.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	boolean reintentarCallback(Long eventId);

	/**
	 * Genera un fitxer d'exportació amb la informació dels expedients.
	 * 
	 * @param entitatId 
	 *            Atribut id de l'entitat.
	 * @param format
	 *            Format pel fitxer d'exportació ("ODS" o "CSV").
	 * @return El fitxer resultant de l'exportació.
	 * @throws IOException
	 *             Si ha sorgit algun problema exportant les dades.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	FitxerDto exportacio(
			Long entitatId,
			Collection<Long> enviamentIds,
			String format) throws IOException, NotFoundException, ParseException;
	
	/**
	 * Crea les columnes s'han de mostrar
	 * 
	 * @param columnes
	 *            Attribut amb les columnes a visualitzar.
	 * @return columnes que s'han de visualitzar.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	public void columnesCreate(
			UsuariDto usuaris,
			Long entitatId,
			ColumnesDto columnes);
	
	/**
	 * Actualitza les columnes s'han de mostrar
	 * 
	 * @param columnes
	 *            Attribut amb les columnes a visualitzar.
	 * @return columnes que s'han de visualitzar.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	public void columnesUpdate(
			Long entitatId,
			ColumnesDto columnes);
	
	/**
	 * Obté les columnes visibles per un usuari i entitat
	 * 
	 * @param columnes
	 *            Attribut amb les columnes a visualitzar.
	 * @return columnes que s'han de visualitzar.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	public ColumnesDto getColumnesUsuari(
			Long entitatId,
			UsuariDto usuari);
	
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	NotificacioEnviamentDtoV2 getOne(Long enviamentId);

	/**
	 * Obté les el justificant del registre.
	 * 
	 * @param enviamentId
	 *            id de l'enviament registrat.
	 * @return document justificant descarregat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_CARPETA')")
	byte[] getDocumentJustificant(Long enviamentId);

	/**
	 * Reactiva les consultes d'estat a Notific@
	 * 
	 * @param enviaments
	 *            Llistat de atributs id dels enviaments
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	void reactivaConsultes(Set<Long> enviaments);
	
	/**
	 * Reactiva les consultes d'estat a SIR
	 * 
	 * @param enviaments
	 *            Llistat de atributs id dels enviaments
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	void reactivaSir(Set<Long> enviaments);
	
	@PreAuthorize("hasRole('NOT_CARPETA') or hasRole('NOT_SUPER')")
	Resposta findEnviamentsByNif(
			String dniTitular,
			NotificaEnviamentTipusEnumDto tipus,
			Boolean estatFinal,
			String basePath, 
			Integer pagina, 
			Integer mida);

	/**
	 * Actualitza l'estat de l'enviament indicat i reinicia el comptador de reintents.
	 *
	 * @param enviamentId
	 *            id de l'enviament.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	void actualitzarEstat(Long enviamentId);

	/**
	 * Activa un event de callback de l'enviament indicat
	 *
	 * @param enviamentId
	 *            id de l'enviament.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	void activarCallback(Long enviamentId);

	/**
	 * Envia un event de callback de l'enviament indicat
	 *
	 * @param enviamentId
	 *            id de l'enviament.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	void enviarCallback(Long enviamentId) throws Exception;
}
