package es.caib.notib.core.repository;

import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
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

	List<NotificacioEventEntity> findByNotificacioIdOrEnviamentIdOrderByDataAsc(Long notificacioId, Long enviamentId);

	List<NotificacioEventEntity> findByEnviamentIdOrderByDataAsc(Long enviamentId);

	List<NotificacioEventEntity> findByEnviamentIdOrderByIdAsc(Long enviamentId);

	List<NotificacioEventEntity> findByEnviamentIdAndTipus(Long enviamentId, NotificacioEventTipusEnumDto tipus);

//	long countByEnviamentIdAndCallbackEstat(Long enviamentId, CallbackEstatEnumDto callbackEstat);

//	@Modifying
//	@Query( " delete from " +
//			"	NotificacioEventEntity ne " +
//			" where " +
//			"		ne.notificacio = :notificacio " +
//			"	and ((ne.error = true and ne.tipus != es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.EMAIL_ENVIAMENT) " +
//			"  		 or (ne.tipus not in (es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE, " +
//			"							  es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT," +
//			"							  es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO," +
//			"							  es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.EMAIL_ENVIAMENT)" +
//			"			)" +
//			"		  or ne.callbackEstat <> es.caib.notib.core.api.dto.CallbackEstatEnumDto.PENDENT " +
//			") "
//	)
//	void deleteOldUselessEvents(@Param("notificacio") NotificacioEntity notificacio);

//	@Modifying
//	@Query( " delete from " +
//			"	NotificacioEventEntity ne " +
//			" where " +
//			"		ne.notificacio = :notificacio " +
//			"	and (ne.error = true " +
//			"  		 or (ne.tipus not in (es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE, " +
//			"							  es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT," +
//			"							  es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO," +
//			"							  es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.EMAIL_ENVIAMENT)" +
//			"			)" +
//			"		  or ne.callbackEstat <> es.caib.notib.core.api.dto.CallbackEstatEnumDto.PENDENT " +
//			") "
//	)
//	void deleteOldNotificaUselessEvents(@Param("notificacio") NotificacioEntity notificacio);

	void deleteByEnviament(NotificacioEnviamentEntity enviament);

	void deleteByNotificacioAndTipusAndError(NotificacioEntity notificacio, NotificacioEventTipusEnumDto tipus, boolean error);

	void deleteByNotificacio(NotificacioEntity notificacio);

	List<NotificacioEventEntity> findByNotificacioAndTipusAndErrorOrderByDataDescIdDesc(NotificacioEntity notificacio, NotificacioEventTipusEnumDto tipus, boolean error);
	List<NotificacioEventEntity> findByEnviamentAndTipusAndError(NotificacioEnviamentEntity enviament, NotificacioEventTipusEnumDto tipus, boolean error);

	List<NotificacioEventEntity> findByNotificacioAndTipusAndErrorAndEnviamentIsNullOrderByDataDescIdDesc(NotificacioEntity notificacio, NotificacioEventTipusEnumDto tipus, boolean error);

	List<NotificacioEventEntity> findByEnviamentAndTipusAndErrorOrderByDataDescIdDesc(NotificacioEnviamentEntity enviament, NotificacioEventTipusEnumDto tipus, boolean error);

	List<NotificacioEventEntity> findByNotificacio(NotificacioEntity notificacio);

	/** Recupera la llista de notificacions pendents */
//	@Query("select ne.id " +
//		   "  from NotificacioEventEntity ne " +
//	       " where ne.callbackEstat = es.caib.notib.core.api.dto.CallbackEstatEnumDto.PENDENT " +
//	       " order by ne.callbackData asc nulls first, data asc")
//	List<Long> findEventsAmbCallbackPendentIds(Pageable page);
//
//	@Query("  from NotificacioEventEntity ne " +
//			" where ne.callbackEstat = es.caib.notib.core.api.dto.CallbackEstatEnumDto.PENDENT " +
//			" order by ne.callbackData asc nulls first, data asc")
//	List<NotificacioEventEntity> findEventsAmbCallbackPendent(Pageable page);
//
//	@Query("  from NotificacioEventEntity ne " +
//			" where ne.callbackEstat = es.caib.notib.core.api.dto.CallbackEstatEnumDto.PENDENT " +
//			" order by ne.callbackData asc nulls first, data asc")
//	List<NotificacioEventEntity> findEventsAmbCallbackPendent();
//
//	@Query(" select ne.id from NotificacioEventEntity ne " +
//			" where ne.callbackEstat = es.caib.notib.core.api.dto.CallbackEstatEnumDto.PENDENT " +
//			"and ne.notificacio.id = :notificacioId " +
//			"order by ne.callbackData asc nulls first, data asc")
//	List<Long> findEventsAmbCallbackPendentByNotificacioId(@Param("notificacioId") Long notificacioId);


	@Query("select ne " + 
			   "  from NotificacioEventEntity ne " +
		       " where ne.id = ( " +
			   "		select max(e.id) " +
			   "		from NotificacioEventEntity e " +
			   "			left outer join e.notificacio n " +
			   "		where n.id = :notificacioId " +
			   "		  and e.tipus = es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.CALLBACK_ENVIAMENT)")
	NotificacioEventEntity findUltimEventByNotificacioId(@Param("notificacioId") Long notificacioId);
	
	@Query("select ne " + 
			   "  from NotificacioEventEntity ne " +
		       " where ne.id = ( " +
			   "		select max(e.id) " +
			   "		from NotificacioEventEntity e " +
			   "			left outer join e.notificacio n " +
			   "		where n.id = :notificacioId " +
			   "		  and (e.tipus = es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.REGISTRE_ENVIAMENT or " +
			   "			   e.tipus = es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.SIR_ENVIAMENT))")
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
			"			n.id = :notificacioId " +
			"			and e.error = true " +
			"			and e.tipus != es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.CALLBACK_ENVIAMENT " +
			"			and e.tipus != es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.API_CARPETA " +
			"	   ) ")
	NotificacioEventEntity findLastErrorEventByNotificacioId(@Param("notificacioId") Long notificacioId);

	@Query( "select ne " +
			"from " +
			"	NotificacioEventEntity ne " +
			"where ne.id = ( " +
			"		select " +
			"			max(e.id) " +
			"		from " +
			"			NotificacioEventEntity e left outer join e.enviament n " +
			"		where " +
			"			n.id = :enviamentId " +
			"			and e.tipus = es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.API_CARPETA " +
			"	   ) ")
	NotificacioEventEntity findLastApiCarpetaByEnviamentId(@Param("enviamentId") Long enviamentId);

	@Query( "select ne from NotificacioEventEntity ne " +
			" where ne.notificacio.id = :notificacioId " +
			" and ne.fiReintents = true " +
			" and ne.tipus != es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.CALLBACK_ENVIAMENT")
	List<NotificacioEventEntity> findEventsAmbFiReintentsByNotificacioId(@Param("notificacioId") Long notificacioId);

	@Query( "select count(ne.id) from NotificacioEventEntity ne " +
			" where ne.notificacio.id = :notificacioId " +
			" and ne.fiReintents = true " +
			" and ne.tipus = es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.CALLBACK_ENVIAMENT")
	int countEventCallbackAmbFiReintentsByNotificacioId(@Param("notificacioId") Long notificacioId);

	@Query( "from NotificacioEventEntity ne " +
			" where ne.enviament.id = :enviamentId " +
			" and ne.fiReintents = true " +
			" and ne.tipus = es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.CALLBACK_ENVIAMENT")
	NotificacioEventEntity findEventCallbackAmbFiReintentsByEnviamentId(@Param("enviamentId") Long enviamentId);

	@Modifying
	@Query("update NotificacioEventEntity n set n.fiReintents = false, n.intents = 0" +
			" where n.id = :notId and n.fiReintents = true and n.tipus != es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.CALLBACK_ENVIAMENT ")
	void resetIntentsByNotId(@Param("notId") Long notId);

	@Query("select ne " +
			"  from NotificacioEventEntity ne " +
			" where ne.id = ( " +
			"		select max(e.id) " +
			"		from NotificacioEventEntity e " +
			"			left outer join e.notificacio n " +
			"		where n.id = :notificacioId " +
			"		  and e.tipus = es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.EMAIL_ENVIAMENT)")
	NotificacioEventEntity findUltimEventEmailByNotificacioId(@Param("notificacioId")Long notificacioId);

	@Query("select ne.notificacio.id " +
			"  from NotificacioEventEntity ne " +
			" where ne.id = :eventId")
	Long findNotificacioIdByEventId(@Param("eventId") Long eventId);

    List<NotificacioEventEntity> findByEnviamentAndTipusOrderByIdDesc(NotificacioEnviamentEntity enviament, NotificacioEventTipusEnumDto tipus);
}
