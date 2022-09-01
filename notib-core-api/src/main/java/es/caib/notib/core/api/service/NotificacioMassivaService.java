package es.caib.notib.core.api.service;

import es.caib.notib.core.api.dto.FitxerDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.RolEnumDto;
import es.caib.notib.core.api.dto.notificacio.*;
import es.caib.notib.core.api.exception.RegistreNotificaException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

/**
 * Definició del servei per a interactuar amb les notificacions massives.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioMassivaService {

	/**
	 * Consulta una notificació massiva amb el seu id
	 *
	 * @param id id de la notificacio massiva.
	 *
	 * @return La notificacio massiva l'id especificat o null si no s'ha trobat.
	 */
	NotificacioMassivaDataDto findById(Long entitatId, Long id);

	/**
	 * Consulta els detalls d'una notificació massiva amb el seu id.
	 * Retorna les dades de la notificacio massiva i un resum amb els resultats del processament del fitxer CSV
	 * que inclou els errors que s'han produït durant el seu processament.
	 *
	 * @param entitatId Identificador de l'entitat actual
	 * @param notificacioMassivaId Identificador de la notificacio massiva que es vol consultar.
	 *
	 * @return La notificacio massiva l'id especificat o null si no s'ha trobat.
	 */
	NotificacioMassivaInfoDto getNotificacioMassivaInfo(Long entitatId, Long notificacioMassivaId);

	/**
	 * Consulta les notificacions d'una notificacio massiva de forma paginada.
	 *
	 * @param entitatId Identificador de l'entitat actual.
	 * @param notificacioMassivaId Identificador de la notificacio massiva que es vol consultar.
	 * @param filtre Objecte amb les dades amb les que es dessitja filtrar el resultat.
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 *
	 * @return La pàgina amb les notificacions de la notificació massiva
	 */
	PaginaDto<NotificacioTableItemDto> findNotificacions(Long entitatId, Long notificacioMassivaId,
														 NotificacioFiltreDto filtre,
														 PaginacioParamsDto paginacioParams);

	/**
	 * Dona d'alta una notificació massiva
	 *
	 * @param entitatId Entitat actual
	 * @param usuariCodi Usuari que dóna d'alta la notificació massiva
	 * @param notificacioMassiu Dades de la notificació massiva que es vol donar d'alta
	 * @throws RegistreNotificaException
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom') or hasRole('NOT_APL')")
	NotificacioMassivaDataDto create(
			Long entitatId,
			String usuariCodi,
			NotificacioMassivaDto notificacioMassiu) throws RegistreNotificaException;

	/**
	 * Esborra una notificació massiva
	 *
	 * @param entitatId Entitat actual
	 * @param notificacioMassivaId Identificador de la notificació massiva a esborrar
	 */
	@PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom') or hasRole('NOT_APL')")
	void delete(
			Long entitatId,
			Long notificacioMassivaId);

	/**
	 * Consulta les notificacions massives que s'han donat d'alta al sistema de manera paginada.
	 *
	 * @param entitatId Identificador de l'entitat actual
	 * @param filtre Objecte per a filtrar les notificacions massives consultades.
	 * @param rol Rol de l'usuari amb que s'està fent la consulta. Depenent del rol es mostren uns resultats diferents.
	 *            	- Usuari:
	 *            	- Administrador d'òrgan:
	 *            	- Administrador d'entitat:
	 * @param paginacioParams Objecte amb la configuració de la paginació (nombre d'elements per pàgina, pàgina consultada,
	 *                        ordre dels registres, ...)
	 * @return La página consultada.
	 */
	@PreAuthorize("hasRole('tothom')")
	PaginaDto<NotificacioMassivaTableItemDto> findAmbFiltrePaginat(
			Long entitatId,
			NotificacioMassivaFiltreDto filtre,
			RolEnumDto rol,
			PaginacioParamsDto paginacioParams);

	/**
	 * Baixa la prioritat de la notificació massiva indicada.
	 * Aplaça les notificacions d'una notificacio massiva per a que la resta de notificacions tenguin
	 * preferència en la coa del registre.
	 *
	 * @param entitatId Identificador de l'entitat actual
	 * @param notificacioMassivaId Identificador de la notificacio massiva que es vol postposar
	 */
	void posposar(Long entitatId, Long notificacioMassivaId);

	/**
	 * Reactiva la prioritat de la notificacio massiva.
	 * Reactiva les notificacions d'una notificacio massiva per a que es col·loquin a la coa del registre com una
	 * notificacio normal.
	 *
	 * @param entitatId Identificador de l'entitat actual
	 * @param notificacioMassivaId Identificador de la notificacio massiva que es vol postposar
	 */
	void reactivar(Long entitatId, Long notificacioMassivaId);

	/**
	 * Consulta el document CSV utilitzat per a donar d'alta la notificacio massiva indicada per paràmetre.
	 *
	 * @param entitatId Entitat actual
	 * @param notificacioMassivaId Identificador de la notificació massiva a consultar
	 *
	 * @return L'objecte amb la informació del fitxer consultat
	 */
	FitxerDto getCSVFile(Long entitatId, Long notificacioMassivaId);

	/**
	 * Consulta el document ZIP utilitzat per a donar d'alta la notificacio massiva indicada per paràmetre.
	 *
	 * @param entitatId Entitat actual
	 * @param notificacioMassivaId Identificador de la notificació massiva a consultar
	 *
	 * @return L'objecte amb la informació del fitxer consultat
	 */
	FitxerDto getZipFile(Long entitatId, Long notificacioMassivaId);


	/**
	 * Consulta el document CSV amb el resum de l'alta de la notificacio massiva indicada per paràmetre.
	 *
	 * @param entitatId Entitat actual
	 * @param notificacioMassivaId Identificador de la notificació massiva
	 *
	 * @return L'objecte amb la informació del fitxer consultat
	 */
	FitxerDto getResumFile(Long entitatId, Long notificacioMassivaId);


	/**
	 * Consulta el document CSV amb els errors que han tingut lloc durant el procés
	 * d'alta la notificacio massiva indicada per paràmetre.
	 *
	 * @param entitatId Entitat actual
	 * @param notificacioMassivaId Identificador de la notificació massiva
	 *
	 * @return L'objecte amb la informació del fitxer consultat
	 */
	FitxerDto getErrorsValidacioFile(Long entitatId, Long notificacioMassivaId);


	/**
	 * Consulta el document CSV amb els errors que han tingut lloc durant el procés
	 * d'execució la notificacio massiva indicada per paràmetre.
	 *
	 * @param entitatId Entitat actual
	 * @param notificacioMassivaId Identificador de la notificació massiva
	 *
	 * @return L'objecte amb la informació del fitxer consultat
	 */
	FitxerDto getErrorsExecucioFile(Long entitatId, Long notificacioMassivaId);

	/**
	 * Obté un fitxer CSV d'exemple amb el format que ha de tenir per a carregar dades massives.
	 *
	 * @return El fitxer d'exemple
	 * @throws NoSuchFileException Si no es troba el fitxer
	 * @throws IOException Si no es pot llegir el fitxer
	 */
	@PreAuthorize("hasRole('tothom')")
	byte[] getModelDadesCarregaMassiuCSV() throws NoSuchFileException, IOException;

	/**
	 * Cancelar la notificacio massiva.
	 *
	 * @param entitatId Identificador de l'entitat actual
	 * @param notificacioMassivaId Identificador de la notificacio massiva que es vol cancelar
	 */
	void cancelar(Long entitatId, Long notificacioMassivaId) throws Exception;
}