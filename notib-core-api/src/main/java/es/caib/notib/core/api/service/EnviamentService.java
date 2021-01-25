/**
 * 
 */
package es.caib.notib.core.api.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import es.caib.notib.core.api.dto.*;
import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.rest.consulta.Resposta;

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
	 * @param entitat
	 * 			Entitat de la que es vol consultar els enviaments
	 * @param filtre
	 * 			Filtre per a la consulta
	 * @param paginacio
	 * @return Pàgina d'enviaments
	 * 
	 * @throws ParseException
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	public PaginaDto<NotificacioEnviamentDtoV2> enviamentFindByEntityAndFiltre(
			EntitatDto entitat,
			boolean isUsuari,
			boolean isUsuariEntitat,
			boolean isAdminOrgan,
			List<String> codisProcedimentsDisponibles,
			List<String> codisOrgansGestorsDisponibles,
			List<Long> codisProcedimentOrgansDisponibles,
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
	public List<NotificacioEnviamentDatatableDto> enviamentFindAmbNotificacio(
			Long notificacioId);

	/**
	 * Consulta d'un enviament donat el seu id.
	 * 
	 * @param enviamentId
	 *            Atribut id de l'enviament.
	 * @return el destinatari trobat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_APL')")
	public NotificacioEnviamentDto enviamentFindAmbId(
			Long enviamentId);

	/**
	 * Consulta dels events d'una notificació.
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @return els events trobats.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	public List<NotificacioEventDto> eventFindAmbNotificacio(
			Long notificacioId);
	
	/**
	 * Reintenta un callback fallat
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @return els events trobats.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	public boolean reintentarCallback(
			Long eventId);

	/**
	 * Genera un fitxer d'exportació amb la informació dels expedients.
	 * 
	 * @param entitatId 
	 *            Atribut id de l'entitat.
	 * @param metaExpedientId 
	 *            Atribut id del meta-expedient.
	 * @param expedientIds
	 *            Atribut id dels expedients a exportar.
	 * @param format
	 *            Format pel fitxer d'exportació ("ODS" o "CSV").
	 * @return El fitxer resultant de l'exportació.
	 * @throws IOException
	 *             Si ha sorgit algun problema exportant les dades.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	public FitxerDto exportacio(
			Long entitatId,
			Collection<Long> enviamentIds,
			String format,
			NotificacioEnviamentFiltreDto filtreCommand) throws IOException, NotFoundException, ParseException;
	
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
	
	/**
	 * Obté les columnes visibles per un usuari i entitat
	 * 
	 * @param columnes
	 *            Attribut amb les columnes a visualitzar.
	 * @return columnes que s'han de visualitzar.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	public NotificacioEnviamentDtoV2 getOne(
			Long entitatId);

	/**
	 * Obté les el justificant del registre.
	 * 
	 * @param enviamentId
	 *            id de l'enviament registrat.
	 * @return document justificant descarregat.
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom') or hasRole('NOT_CARPETA')")
	public byte[] getDocumentJustificant(
			Long enviamentId);

	/**
	 * Reactiva les consultes d'estat a Notific@
	 * 
	 * @param enviaments
	 *            Llistat de atributs id dels enviaments
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	public void reactivaConsultes(Set<Long> enviaments);
	
	/**
	 * Reactiva les consultes d'estat a SIR
	 * 
	 * @param enviaments
	 *            Llistat de atributs id dels enviaments
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('NOT_SUPER') or hasRole('tothom')")
	public void reactivaSir(Set<Long> enviaments);
	
	@PreAuthorize("hasRole('NOT_CARPETA') or hasRole('NOT_SUPER')")
	public Resposta findEnviamentsByNif(
			String dniTitular,
			NotificaEnviamentTipusEnumDto tipus,
			Boolean estatFinal,
			String basePath, 
			Integer pagina, 
			Integer mida);
	
}
