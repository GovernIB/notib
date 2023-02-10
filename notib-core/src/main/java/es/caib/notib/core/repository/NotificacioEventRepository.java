package es.caib.notib.core.repository;

import es.caib.notib.core.api.dto.CallbackEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Consultes necessàries per als events de les notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioEventRepository extends JpaRepository<NotificacioEventEntity, Long> {

	List<NotificacioEventEntity> findByNotificacioIdAndErrorIsTrue(Long notificacioId);

	List<NotificacioEventEntity> findByNotificacioIdOrderByDataAsc(Long notificacioId);

	List<NotificacioEventEntity> findByNotificacioIdOrEnviamentIdOrderByDataAsc(Long notificacioId,	Long enviamentId);
	
	List<NotificacioEventEntity> findByEnviamentIdOrderByIdAsc(Long enviamentId);

	long countByEnviamentIdAndCallbackEstat(Long enviamentId, CallbackEstatEnumDto callbackEstat);

	@Modifying
	@Query( " delete from " +
			"	NotificacioEventEntity ne " +
			" where " +
			"		ne.notificacio = :notificacio " +
			"	and ((ne.error = true and ne.tipus != es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.EMAIL_ENVIAMENT) " +
			"  		 or (ne.tipus not in (es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE, " +
			"							  es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT," +
			"							  es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO," +
			"							  es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.EMAIL_ENVIAMENT)" +
			"			)" +
			"		  or ne.callbackEstat <> es.caib.notib.core.api.dto.CallbackEstatEnumDto.PENDENT " +
			") "
	)
	void deleteOldUselessEvents(@Param("notificacio") NotificacioEntity notificacio);

	@Modifying
	@Query( " delete from " +
			"	NotificacioEventEntity ne " +
			" where " +
			"		ne.notificacio = :notificacio " +
			"	and (ne.error = true " +
			"  		 or (ne.tipus not in (es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE, " +
			"							  es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT," +
			"							  es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO," +
			"							  es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.EMAIL_ENVIAMENT)" +
			"			)" +
			"		  or ne.callbackEstat <> es.caib.notib.core.api.dto.CallbackEstatEnumDto.PENDENT " +
			") "
	)
	void deleteOldNotificaUselessEvents(@Param("notificacio") NotificacioEntity notificacio);

	void deleteByEnviament(NotificacioEnviamentEntity enviament);

	void deleteByNotificacioAndTipusAndError(NotificacioEntity notificacio, NotificacioEventTipusEnumDto tipus, boolean error);

	void deleteByNotificacio(NotificacioEntity notificacio);

	List<NotificacioEventEntity> findByNotificacioAndTipusAndErrorOrderByDataDescIdDesc(NotificacioEntity notificacio, NotificacioEventTipusEnumDto tipus, boolean error);

	List<NotificacioEventEntity> findByNotificacioAndTipusAndErrorAndEnviamentIsNullOrderByDataDescIdDesc(NotificacioEntity notificacio, NotificacioEventTipusEnumDto tipus, boolean error);

	List<NotificacioEventEntity> findByEnviamentAndTipusAndErrorOrderByDataDescIdDesc(NotificacioEnviamentEntity enviament, NotificacioEventTipusEnumDto tipus, boolean error);

	List<NotificacioEventEntity> findByNotificacio(NotificacioEntity notificacio);

	/** Recupera la llista de notificacions pendents */
	@Query("select ne.id " + 
		   "  from NotificacioEventEntity ne " +
	       " where ne.callbackEstat = es.caib.notib.core.api.dto.CallbackEstatEnumDto.PENDENT " +
	       " order by ne.callbackData asc nulls first, data asc")
	List<Long> findEventsAmbCallbackPendentIds(Pageable page);

	@Query("  from NotificacioEventEntity ne " +
			" where ne.callbackEstat = es.caib.notib.core.api.dto.CallbackEstatEnumDto.PENDENT " +
			" order by ne.callbackData asc nulls first, data asc")
	List<NotificacioEventEntity> findEventsAmbCallbackPendent(Pageable page);

	@Query("  from NotificacioEventEntity ne " +
			" where ne.callbackEstat = es.caib.notib.core.api.dto.CallbackEstatEnumDto.PENDENT " +
			" order by ne.callbackData asc nulls first, data asc")
	List<NotificacioEventEntity> findEventsAmbCallbackPendent();

	@Query(" select ne.id from NotificacioEventEntity ne " +
			" where ne.callbackEstat = es.caib.notib.core.api.dto.CallbackEstatEnumDto.PENDENT " +
			"and ne.notificacio.id = :notificacioId " +
			"order by ne.callbackData asc nulls first, data asc")
	List<Long> findEventsAmbCallbackPendentByNotificacioId(@Param("notificacioId") Long notificacioId);

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
	NotificacioEventEntity findUltimEventByNotificacioId(@Param("notificacioId") Long notificacioId);
	
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
	NotificacioEventEntity findUltimEventRegistreByNotificacioId(@Param("notificacioId") Long notificacioId);

		@Query( "select ne " +
				"from " +
				"	NotificacioEventEntity ne " +
				"where ne.id = ( " +
				"		select " +
				"			max(e.id) " +
				"		from " +
				"			NotificacioEventEntity e left outer join e.notificacio n " +
				"		where " +
				"			n.id = :notificacioId and e.errorTipus is not null " +
				"	   ) ")
	NotificacioEventEntity findLastErrorEventByNotificacioId(@Param("notificacioId") Long notificacioId);

	@Query("select ne " +
			"  from NotificacioEventEntity ne " +
			" where ne.id = ( " +
			"		select max(e.id) " +
			"		from NotificacioEventEntity e " +
			"			left outer join e.notificacio n " +
			"		where n.id = :notificacioId " +
			"		  and e.tipus = es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.EMAIL_ENVIAMENT" +
			"	   )" +
			" order by ne.callbackData asc nulls first, data asc")
	NotificacioEventEntity findUltimEventEmailByNotificacioId(@Param("notificacioId")Long notificacioId);
}
