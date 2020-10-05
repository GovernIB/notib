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

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AplicacioRepository extends JpaRepository<AplicacioEntity, Long> {
	
	AplicacioEntity findByUsuariCodi(String usuariCodi);
	AplicacioEntity findByUsuariCodiAndEntitatId(String usuariCodi,Long entitatId);
	AplicacioEntity findByEntitatIdAndId(Long entitatId, Long id);
	AplicacioEntity findByEntitatIdAndUsuariCodi(Long entitatId, String usuariCodi);
	
	@Query(	  "FROM AplicacioEntity a "
			+ "WHERE lower(a.usuariCodi) like concat('%', lower(:filtre), '%') "
			+ "   OR lower(a.callbackUrl) like concat('%', lower(:filtre), '%')")
	public Page<AplicacioEntity> findAllFiltrat(
			@Param("filtre") String filtre,
			Pageable paginacio
			);
	
	@Query(	  "FROM AplicacioEntity a "
			+ "WHERE a.entitat.id = :entitatId "
			+ "  AND (lower(a.usuariCodi) like concat('%', lower(:filtre), '%') "
			+ "   OR lower(a.callbackUrl) like concat('%', lower(:filtre), '%'))")
	public Page<AplicacioEntity> findByEntitatIdFiltrat(
			@Param("entitatId") Long entitatId,
			@Param("filtre") String filtre,
			Pageable paginacio
			);
	
	@Query("SELECT count(a) FROM AplicacioEntity a WHERE a.entitat.id = :entitatId")
	public Long countByEntitatId(@Param("entitatId") Long entitatId);
	
	@Query(  "FROM AplicacioEntity a "
			+ "WHERE lower(a.usuariCodi) like concat('%', lower(:text), '%') "
			+ "ORDER BY a.usuariCodi desc")
	public AplicacioEntity findByText(@Param("text") String text);

}
