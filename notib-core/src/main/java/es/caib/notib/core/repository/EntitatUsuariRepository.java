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

import es.caib.notib.core.entity.EntitatUsuariEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntitatUsuariRepository extends JpaRepository<EntitatUsuariEntity, Long> {
	
	public EntitatUsuariEntity findById(Long id);
	
	public EntitatUsuariEntity findByEntitatIdAndUsuariCodi(
			Long entitatId,
			String usuariCodi);

	@Query(
			  "FROM "
			+ "     EntitatUsuariEntity eu "
			+ "WHERE "
			+ "		eu.entitat.id = :entitatId "
			+ "AND "
			+ "("
			+ "		lower(eu.usuari.codi) like concat('%', lower(:filtre), '%') "
			+ "OR "
			+ "		lower(eu.usuari.nom) like concat('%', lower(:filtre), '%')"
			+ ")"
			)
	public Page<EntitatUsuariEntity> findFilteredByEntitatId(
			@Param("entitatId") Long entitatId,
			@Param("filtre") String filtre,
			Pageable paginacio
			);
	
	public Page<EntitatUsuariEntity> findByEntitatId(Long idEntitat, Pageable paginacio);
	
	public List<EntitatUsuariEntity> findByUsuariCodi( String usuariCodi );

}
