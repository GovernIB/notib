/**
 * 
 */
package es.caib.notib.core.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.caib.notib.core.entity.NotificacioEventEntity;

/**
 * Consultes necessàries per als events de les notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioEventRepository extends JpaRepository<NotificacioEventEntity, Long> {

	List<NotificacioEventEntity> findByNotificacioIdOrderByDataAsc(
			Long notificacioId);

	List<NotificacioEventEntity> findByNotificacioIdOrEnviamentIdOrderByDataAsc(
			Long notificacioId,
			Long enviamentId);

	/** Recupera la llista de notificacions pendents */
	@Query("select ne.id " + 
		   "from NotificacioEventEntity ne " +
	       "where ne.callbackEstat = es.caib.notib.core.api.dto.CallbackEstatEnumDto.PENDENT ")
	List<Long> findEventsPendentsIds(Pageable page);

}
