/**
 * 
 */
package es.caib.notib.core.repository.auditoria;

import es.caib.notib.core.entity.auditoria.NotificacioEnviamentAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus enviaments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioEnviamentAuditRepository extends JpaRepository<NotificacioEnviamentAudit, Long> {

	@Query(	" from NotificacioEnviamentAudit e " +
			"where e.enviamentId = :enviamentId " +
			"  and e.id = (select max(id) from NotificacioEnviamentAudit where enviamentId = :enviamentId)")
	NotificacioEnviamentAudit findLastAudit(@Param("enviamentId") Long enviamentId);

	List<NotificacioEnviamentAudit> findByEnviamentIdOrderByCreatedDateAsc(Long enviamentId);

//	NotificacioEnviamentAudit findFirstByEnviamentIdOrderByIdDesc(Long notificacioId);

}
