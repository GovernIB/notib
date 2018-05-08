/**
 * 
 */
package es.caib.notib.core.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.notib.core.api.dto.NotificacioDestinatariEstatEnumDto;
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

	@Query("FROM NotificacioEnviamentEntity "
			+ "WHERE seuEstat in (:seuEstat) "
			+ "  AND seuReintentsEnviament < :maxReintents "
			+ "ORDER BY seuDataEnviament ASC")
	List<NotificacioEnviamentEntity> findBySeuEstatInAndMaxReintentsOrderBySeuDataNotificaDarreraPeticioAsc(
			@Param("seuEstat") NotificacioDestinatariEstatEnumDto[] seuEstat,
			@Param("maxReintents") int maxReintents,
			Pageable pageable);
	
	/*@Query("FROM NotificacioEnviamentEntity "
			+ "WHERE seuEstat = es.caib.notib.core.api.dto.NotificacioDestinatariEstatEnumDto.NOTIB_PENDENT "
			+ "  AND seuReintentsEnviament < 3 "
			+ "ORDER BY seuDataEnviament ASC")
	List<NotificacioEnviamentEntity> findBySeuEstatPendent(Pageable pageable);
	
	@Query("FROM NotificacioEnviamentEntity "
			+ "WHERE seuEstat = es.caib.notib.core.api.dto.NotificacioDestinatariEstatEnumDto.NOTIB_ENVIADA "
			+ "ORDER BY seuDataEnviament ASC")
	List<NotificacioEnviamentEntity> findBySeuEstatEnviada(Pageable pageable);

	@Query("FROM NotificacioEnviamentEntity "
			+ "WHERE seuEstat = es.caib.notib.core.api.dto.NotificacioDestinatariEstatEnumDto.LLEGIDA "
			+ "   OR seuEstat = es.caib.notib.core.api.dto.NotificacioDestinatariEstatEnumDto.REBUTJADA "
			+ "ORDER BY seuDataEnviament ASC")
	List<NotificacioEnviamentEntity> findBySeuEstatTramitada(PageRequest pageRequest);*/
	
	List<NotificacioEnviamentEntity> findBySeuEstatInOrderBySeuDataNotificaDarreraPeticioAsc(
			NotificacioDestinatariEstatEnumDto[] seuEstats);

}
