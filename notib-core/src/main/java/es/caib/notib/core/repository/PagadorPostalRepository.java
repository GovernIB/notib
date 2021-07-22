package es.caib.notib.core.repository;

import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.cie.PagadorPostalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus pagador postal.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PagadorPostalRepository extends JpaRepository<PagadorPostalEntity, Long> {

	PagadorPostalEntity findByOrganismePagadorCodi(String organismePagador);
	List<PagadorPostalEntity> findByEntitat(EntitatEntity entitat);
	List<PagadorPostalEntity> findByEntitatIdAndOrganGestorCodiIn(Long entitatId, List<String> organsFills);
	List<PagadorPostalEntity> findByOrganGestorId(Long organGestorId);
	
	@Query(	"from " +
			"    PagadorPostalEntity b " +
			"where " +
			"(:esNullFiltreOrganismePagador = true or b.organismePagadorCodi = :organismePagadorCodi) " +
			"and (:esNullFiltreNumContracte = true or b.contracteNum = :numContracte) " + 
			"and b.entitat = :entitat")
	Page<PagadorPostalEntity> findByCodiDir3AndNumContacteNotNullFiltrePaginatAndEntitat(
			@Param("esNullFiltreOrganismePagador") boolean esNullFiltreOrganismePagador,
			@Param("organismePagadorCodi") String organismePagadorCodi,
			@Param("esNullFiltreNumContracte") boolean esNullFiltreNumContracte,
			@Param("numContracte") String filtreNumContracte,
			@Param("entitat") EntitatEntity entitat,
			Pageable paginacio);
	
	@Query(	"from " +
			"    PagadorPostalEntity b " +
			"where " +
			"(:esNullFiltreOrganismePagador = true or b.organismePagadorCodi = :organismePagadorCodi) " +
			"and (:esNullFiltreNumContracte = true or b.contracteNum = :numContracte) " + 
			"and (b.organGestor.codi in (:organsGestors)) " +
			"and b.entitat = :entitat")
	Page<PagadorPostalEntity> findByCodiDir3AndNumContacteNotNullFiltrePaginatAndEntitatWithOrgan(
			@Param("esNullFiltreOrganismePagador") boolean esNullFiltreOrganismePagador,
			@Param("organismePagadorCodi") String organismePagadorCodi,
			@Param("esNullFiltreNumContracte") boolean esNullFiltreNumContracte,
			@Param("numContracte") String filtreNumContracte,
			@Param("organsGestors") List<String> organsGestors,
			@Param("entitat") EntitatEntity entitat,
			Pageable paginacio);
}
