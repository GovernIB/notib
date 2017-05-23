/**
 * 
 */
package es.caib.notib.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.notib.core.entity.EntitatEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntitatRepository extends JpaRepository<EntitatEntity, Long> {
	
	EntitatEntity findById(long id);

	EntitatEntity findByCodi(String codi);

	EntitatEntity findByDir3Codi(String dir3Codi);
	
	EntitatEntity findByCif(String cif);

	List<EntitatEntity> findByActiva(boolean activa);
	
	List<EntitatEntity> findByEntitatUsuarisUsuariCodi(String usuariCodi);
	
	EntitatEntity findById(Long entitatId);
	
	List<EntitatEntity> findListById(Long entitatId);
	
	
	@Query(
			  "FROM "
			+ "     EntitatEntity eu "
			+ "WHERE "
			+ "		lower(eu.codi) like concat('%', lower(:filtre), '%') "
			+ "OR "
			+ "		lower(eu.nom) like concat('%', lower(:filtre), '%')"
			+ "OR "
			+ "		lower(eu.cif) like concat('%', lower(:filtre), '%')"
			)
	public Page<EntitatEntity> findByFiltre(
			@Param("filtre") String filtre,
			Pageable paginacio
			);
}
