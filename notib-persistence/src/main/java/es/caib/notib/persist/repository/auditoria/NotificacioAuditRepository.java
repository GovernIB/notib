/**
 * 
 */
package es.caib.notib.persist.repository.auditoria;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.notib.persist.entity.auditoria.NotificacioAudit;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus notificacio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioAuditRepository extends JpaRepository<NotificacioAudit, Long> {

	@Modifying
	@Query("update NotificacioAudit nte set nte.referencia =" +
			" (select net.referencia from NotificacioEntity net where net.id = nte.id) where nte.referencia is null")
	void updateReferenciesNules();

	@Query(	" from NotificacioAudit n " +
			"where n.notificacioId = :notificacioId " +
			"  and n.id = (select max(id) from NotificacioAudit where notificacioId = :notificacioId)")
	NotificacioAudit findLastAudit(@Param("notificacioId") Long notificacioId);

    List<NotificacioAudit> findByNotificacioIdOrderByCreatedDateAsc(Long notificacioId);

//	NotificacioAudit findFirstByNotificacioIdOrderByIdDesc(Long notificacioId);

}
