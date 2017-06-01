/**
 * 
 */
package es.caib.notib.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.notib.core.entity.AplicacioEntity;
import es.caib.notib.core.entity.EntitatEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AplicacioRepository extends JpaRepository<AplicacioEntity, Long> {
	
	AplicacioEntity findByUsuariCodi(String usuariCodi);
	
	@Query(
			  "FROM "
			+ "     AplicacioEntity a "
			+ "WHERE "
			+ "		lower(a.usuariCodi) like concat('%', lower(:filtre), '%') "
			+ "OR "
			+ "		lower(a.callbackUrl) like concat('%', lower(:filtre), '%')"
			)
	public Page<AplicacioEntity> findAllFiltrat(
			@Param("filtre") String filtre,
			Pageable paginacio
			);

}
