/**
 * 
 */
package es.caib.notib.core.repository.auditoria;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.notib.core.entity.auditoria.NotificacioAudit;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus notificacio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioAuditRepository extends JpaRepository<NotificacioAudit, Long> {

	@Query(	" from NotificacioAudit n " +
			"where n.notificacioId = :notificacioId " +
			"  and n.id = (select max(id) from NotificacioAudit where notificacioId = :notificacioId)")
	NotificacioAudit findLastAudit(@Param("notificacioId") Long notificacioId);
	
//	NotificacioAudit findFirstByNotificacioIdOrderByIdDesc(Long notificacioId);

}
