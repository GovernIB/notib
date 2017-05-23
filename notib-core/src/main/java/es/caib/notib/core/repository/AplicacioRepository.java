/**
 * 
 */
package es.caib.notib.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.notib.core.entity.AplicacioEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AplicacioRepository extends JpaRepository<AplicacioEntity, Long> {
	
	AplicacioEntity findByUsuariCodi(String usuariCodi);

}
