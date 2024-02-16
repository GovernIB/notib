/**
 * 
 */
package es.caib.notib.logic.intf.service;

import es.caib.notib.client.domini.consulta.RespostaConsultaV2;
import es.caib.notib.logic.intf.dto.ApiConsulta;
import es.caib.notib.logic.intf.dto.FitxerDto;
import es.caib.notib.logic.intf.dto.NotificacioEnviamentDto;
import es.caib.notib.logic.intf.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.logic.intf.dto.NotificacioEnviamentFiltreDto;
import es.caib.notib.logic.intf.dto.NotificacioEventDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.notenviament.ColumnesDto;
import es.caib.notib.logic.intf.dto.notenviament.NotEnviamentTableItemDto;
import es.caib.notib.logic.intf.dto.notenviament.NotificacioEnviamentDatatableDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.rest.consulta.Resposta;
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
	@PreAuthorize("isAuthenticated()")
	List<Long> findIdsAmbFiltre(Long entitatId, RolEnumDto rol, String organGestorCodi, String usuariCodi, NotificacioEnviamentFiltreDto filtre) throws NotFoundException, ParseException;

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
	@PreAuthorize("isAuthenticated()")
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
	@PreAuthorize("isAuthenticated()")
	List<NotificacioEnviamentDatatableDto> enviamentFindAmbNotificacio(Long notificacioId);

	/**
	 * Consulta els identificadors dels enviaments d'un conjunt de notificacions
	 *
	 * @param notificacionsIds  Atribut id de la notificació.
	 * @return els identificadors dels enviaments.
	 */
	@PreAuthorize("isAuthenticated()")
	Set<Long> findIdsByNotificacioIds(Collection<Long> notificacionsIds);

	/**
	 * Consulta d'un enviament donat el seu id.
	 * 
	 * @param enviamentId
	 *            Atribut id de l'enviament.
	 * @return el destinatari trobat.
	 */
	@PreAuthorize("isAuthenticated()")
	NotificacioEnviamentDto enviamentFindAmbId(Long enviamentId);

	/**
	 * Consulta dels events d'una notificació.
	 * 
	 * @param notificacioId
	 *            Atribut id de la notificació.
	 * @return els events trobats.
	 */
	@PreAuthorize("isAuthenticated()")
	List<NotificacioEventDto> eventFindAmbNotificacio(Long notificacioId);


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
	@PreAuthorize("isAuthenticated()")
	FitxerDto exportacio(Long entitatId, Collection<Long> enviamentIds, String format) throws IOException, NotFoundException, ParseException;
	
	@PreAuthorize("isAuthenticated()")
	NotificacioEnviamentDtoV2 getOne(Long enviamentId);

	/**
	 * Obté les el justificant del registre.
	 * 
	 * @param enviamentId
	 *            id de l'enviament registrat.
	 * @return document justificant descarregat.
	 */
	@PreAuthorize("isAuthenticated()")
	byte[] getDocumentJustificant(Long enviamentId);

	/**
	 * Reactiva les consultes d'estat a Notific@
	 * 
	 * @param enviaments
	 *            Llistat de atributs id dels enviaments
	 */
	@PreAuthorize("isAuthenticated()")
	void reactivaConsultes(Set<Long> enviaments);
	
	/**
	 * Reactiva les consultes d'estat a SIR
	 * 
	 * @param enviaments
	 *            Llistat de atributs id dels enviaments
	 */
	@PreAuthorize("isAuthenticated()")
	void reactivaSir(Set<Long> enviaments);
	
	@PreAuthorize("hasRole('NOT_CARPETA') or hasRole('NOT_SUPER')")
	Resposta findEnviaments(ApiConsulta consulta);

	@PreAuthorize("hasRole('NOT_CARPETA') or hasRole('NOT_SUPER')")
	RespostaConsultaV2 findEnviamentsV2(ApiConsulta consulta);
	/**
	 * Actualitza l'estat de l'enviament indicat i reinicia el comptador de reintents.
	 *
	 * @param enviamentId
	 *            id de l'enviament.
	 */
	@PreAuthorize("isAuthenticated()")
	void actualitzarEstat(Long enviamentId);

	/**
	 * Activa un event de callback de l'enviament indicat
	 *
	 * @param enviamentId
	 *            id de l'enviament.
	 */
	@PreAuthorize("isAuthenticated()")
	void activarCallback(Long enviamentId);

	/**
	 * Envia un event de callback de als enviament indicat
	 *
	 * @param notificacions
	 *            id de l'enviament.
	 */
	@PreAuthorize("isAuthenticated()")
	List<Long> enviarCallback(Set<Long> notificacions) throws Exception;

}
