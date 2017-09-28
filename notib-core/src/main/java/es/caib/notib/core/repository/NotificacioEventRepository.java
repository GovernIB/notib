/**
 * 
 */
package es.caib.notib.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.notib.core.entity.NotificacioEventEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioEventRepository extends JpaRepository<NotificacioEventEntity, Long> {

	List<NotificacioEventEntity> findByNotificacioIdOrderByDataDesc(
			Long notificacioId);

	List<NotificacioEventEntity> findByEnviamentIdOrderByDataDesc(
			Long enviamentId);

}
