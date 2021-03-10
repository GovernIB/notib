package es.caib.notib.core.repository;

import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Consultes necessàries per als events de les notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioEventRepository extends JpaRepository<NotificacioEventEntity, Long> {

	@Override
	void delete(Long aLong);

	List<NotificacioEventEntity> findByNotificacioIdOrderByDataAsc(
			Long notificacioId);

	List<NotificacioEventEntity> findByNotificacioIdOrEnviamentIdOrderByDataAsc(
			Long notificacioId,
			Long enviamentId);
	
	List<NotificacioEventEntity> findByEnviamentIdOrderByIdAsc(
			Long enviamentId);

	@Modifying
	@Query( " delete from " +
			"	NotificacioEventEntity ne " +
			" where " +
			"		ne.notificacio = :notificacio " +
			"	and (ne.error = true " +
			"  		 or (ne.error = false " +
			"		 	 and  ne.tipus not in (es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE, " +
			"								   es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT," +
			"								   es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO)" +
			")) "
	)
	void deleteOldUselessEvents(@Param("notificacio") NotificacioEntity notificacio);

	void deleteByEnviament(NotificacioEnviamentEntity enviament);

	void deleteByNotificacioAndTipusAndError(NotificacioEntity notificacio,
									 NotificacioEventTipusEnumDto tipus,
									 boolean error);

	List<NotificacioEventEntity> findByNotificacioAndTipusAndErrorOrderByDataAsc(NotificacioEntity notificacio,
																   NotificacioEventTipusEnumDto tipus,
																   boolean error);
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
