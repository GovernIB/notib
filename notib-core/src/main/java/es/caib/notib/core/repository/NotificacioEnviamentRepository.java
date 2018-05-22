/**
 * 
 */
package es.caib.notib.core.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioEnviamentRepository extends JpaRepository<NotificacioEnviamentEntity, Long> {

	List<NotificacioEnviamentEntity> findByNotificacioId(
			Long notificacioId);

	NotificacioEnviamentEntity findByNotificaReferencia(
			String notificaReferencia);

	NotificacioEnviamentEntity findByNotificacioAndNotificaReferencia(
			NotificacioEntity notificacio,
			String notificaReferencia);

	NotificacioEnviamentEntity findByNotificacioEntitatAndNotificaIdentificador(
			EntitatEntity entitat,
			String notificaIdentificador);
	
	@Query(
			"select env " +
			"  from	NotificacioEnviamentEntity env " +
			"  left	join env.notificacio as noti " +
			" where	env.seuEstat = es.caib.notib.core.api.dto.SeuEstatEnumDto.PENDENT " +
			"   and noti.estat = es.caib.notib.core.api.dto.NotificacioEstatEnumDto.ENVIADA " +
			"   and	(noti.enviamentDataProgramada is null or current_date() >= noti.enviamentDataProgramada) " +
			"   and	env.intentNum < :maxReintents " +
			"   and	env.seuIntentData is not null " +
			" order	by env.seuIntentData ASC")
	List<NotificacioEnviamentEntity> findBySeuEstatPendent(@Param("maxReintents")Integer maxReintents, Pageable pageable);
	
	@Query(
			"  from	NotificacioEnviamentEntity " +
			" where	seuEstat = es.caib.notib.core.api.dto.SeuEstatEnumDto.ENVIADA " +
			// Excloem les notificacions ja processades per Notific@
			"   and notificaEstat != es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto.NOTIFICADA " +
			"   and notificaEstat != es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto.LLEGIDA " +
			"   and notificaEstat != es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto.EXPIRADA " +
			"   and notificaEstat != es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto.REBUTJADA " +
			"   and	seuIntentData is not null " +
			" order	by seuIntentData ASC")
	List<NotificacioEnviamentEntity> findBySeuEstatEnviat(Pageable pageable);
	
	@Query(
			"  from	NotificacioEnviamentEntity " +
			" where	seuEstat != es.caib.notib.core.api.dto.SeuEstatEnumDto.ENVIADA " +
			"   and	seuDataNotificaInformat is null " +
			"   and	seuIntentData is not null " +
			" order	by seuIntentData ASC")
	List<NotificacioEnviamentEntity> findBySeuEstatModificat(Pageable pageable);
	
	@Query(	"from " +
			"    NotificacioEnviamentEntity " +
			"where " +
			"    seuEstat in (:seuEstat) " +
			"and intentNum < :maxReintents " +
			"order by " +
			"    seuIntentData asc")
	List<NotificacioEnviamentEntity> findBySeuEstatInAndMaxReintentsOrderBySeuDataNotificaDarreraPeticioAsc(
			@Param("seuEstat") NotificacioEnviamentEstatEnumDto[] seuEstat,
			@Param("maxReintents") int maxReintents,
			Pageable pageable);

	List<NotificacioEnviamentEntity> findBySeuEstatInOrderBySeuDataNotificaDarreraPeticioAsc(
			NotificacioEnviamentEstatEnumDto[] seuEstats);

	@Query(	"from " +
			"    NotificacioEnviamentEntity " +
			"where " +
			"    notificaEstatFinal = false " +
			"order by " +
			"    notificaEstatDataActualitzacio asc nulls first, " +
			"    id asc")
	List<NotificacioEnviamentEntity> findByNotificaEstatFinalFalseOrderByEstatDataActualitzacioAsc(
			Pageable pageable);

}
