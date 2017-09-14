/**
 * 
 */
package es.caib.notib.core.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.notib.core.api.dto.NotificacioDestinatariEstatEnumDto;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioDestinatariEntity;
import es.caib.notib.core.entity.NotificacioEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioDestinatariRepository extends JpaRepository<NotificacioDestinatariEntity, Long> {

	List<NotificacioDestinatariEntity> findByNotificacioId(
			Long notificacioId);

	NotificacioDestinatariEntity findByReferencia(
			String referencia);

	NotificacioDestinatariEntity findByNotificacioAndReferencia(
			NotificacioEntity notificacio,
			String referencia);

	NotificacioDestinatariEntity findByNotificacioEntitatAndNotificaIdentificador(
			EntitatEntity entitat,
			String notificaIdentificador);

	List<NotificacioDestinatariEntity> findBySeuEstatInOrderBySeuDataNotificaDarreraPeticioAsc(
			NotificacioDestinatariEstatEnumDto[] seuEstats,
			Pageable pageable);

	List<NotificacioDestinatariEntity> findBySeuEstatInOrderBySeuDataNotificaDarreraPeticioAsc(
			NotificacioDestinatariEstatEnumDto[] seuEstats);

}
