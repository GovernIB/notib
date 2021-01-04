package es.caib.notib.core.repository.auditoria;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.notib.core.entity.auditoria.ProcedimentAudit;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus procediment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ProcedimentAuditRepository extends JpaRepository<ProcedimentAudit, Long> {

}
