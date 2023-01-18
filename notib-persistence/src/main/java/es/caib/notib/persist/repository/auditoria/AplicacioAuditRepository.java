/**
 * 
 */
package es.caib.notib.persist.repository.auditoria;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.notib.persist.entity.auditoria.AplicacioAudit;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AplicacioAuditRepository extends JpaRepository<AplicacioAudit, Long> {
	
}