/**
 * 
 */
package es.caib.notib.core.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.notib.core.entity.NotificacioEnviamentEntity;
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
	
	List<NotificacioEventEntity> findByEnviamentIdOrderByIdAsc(
			Long enviamentId);
	
	void deleteByEnviament(NotificacioEnviamentEntity enviament);

	/** Recupera la llista de notificacions pendents */
	@Query("select ne.id " + 
		   "  from NotificacioEventEntity ne " +
	       " where ne.callbackEstat = es.caib.notib.core.api.dto.CallbackEstatEnumDto.PENDENT " +
	       " order by ne.callbackData asc nulls first, data asc")
	List<Long> findEventsPendentsIds(Pageable page);

	@Query("select ne " + 
			   "  from NotificacioEventEntity ne " +
		       " where ne.id = ( " +
			   "		select max(e.id) " +
			   "		from NotificacioEventEntity e " +
			   "			left outer join e.notificacio n " +
			   "		where n.id = :notificacioId " +
			   "		  and e.tipus = es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.CALLBACK_CLIENT" +
		       "	   )" +
		       " order by ne.callbackData asc nulls first, data asc")
	NotificacioEventEntity findUltimEventByNotificacioId(@Param("notificacioId")Long notificacioId);
	
	@Query("select ne " + 
			   "  from NotificacioEventEntity ne " +
		       " where ne.id = ( " +
			   "		select max(e.id) " +
			   "		from NotificacioEventEntity e " +
			   "			left outer join e.notificacio n " +
			   "		where n.id = :notificacioId " +
			   "		  and e.tipus = es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE" +
		       "	   )" +
		       " order by ne.callbackData asc nulls first, data asc")
	NotificacioEventEntity findUltimEventRegistreByNotificacioId(@Param("notificacioId")Long notificacioId);
}
