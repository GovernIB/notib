package es.caib.notib.core.repository.auditoria;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.notib.core.entity.auditoria.GrupAudit;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus grup.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface GrupAuditRepository extends JpaRepository<GrupAudit, Long> {

}
