package es.caib.notib.core.repository;

import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.cie.PagadorCieEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus pagador cie.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PagadorCieRepository extends JpaRepository<PagadorCieEntity, Long> {

	List<PagadorCieEntity> findByEntitat(EntitatEntity entitat);
	public List<PagadorCieEntity> findByEntitatIdAndOrganGestorCodiIn(Long entitatId, List<String> organsFills);
	public List<PagadorCieEntity> findByOrganGestorId(Long organGestorId);
	
	@Query(	"from " +
			"    PagadorCieEntity b " +
			"where " +
			"(:esNullFiltreOrganismePagador = true or b.organismePagadorCodi = :organismePagador) " +
			"and b.entitat = :entitat")
	public Page<PagadorCieEntity> findByCodiDir3NotNullFiltrePaginatAndEntitat(
			@Param("esNullFiltreOrganismePagador") boolean esNullFiltreOrganismePagador,
			@Param("organismePagador") String organismePagador,
			@Param("entitat") EntitatEntity entitat,
			Pageable paginacio);
	
	@Query(	"from " +
			"    PagadorCieEntity b " +
			"where " +
			"(:esNullFiltreOrganismePagador = true or b.organismePagadorCodi = :organismePagador) " +
			"and (b.organGestor.codi in (:organsGestors)) " +
			"and b.entitat = :entitat")
	public Page<PagadorCieEntity> findByCodiDir3NotNullFiltrePaginatAndEntitatWithOrgan(
			@Param("esNullFiltreOrganismePagador") boolean esNullFiltreOrganismePagador,
			@Param("organismePagador") String organismePagador,
			@Param("organsGestors") List<String> organsGestors,
			@Param("entitat") EntitatEntity entitat,
			Pageable paginacio);
	
	
}
