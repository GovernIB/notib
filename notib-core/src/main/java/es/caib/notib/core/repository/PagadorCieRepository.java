package es.caib.notib.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.PagadorCieEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus pagador cie.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PagadorCieRepository extends JpaRepository<PagadorCieEntity, Long> {

	
	@Query(	"from " +
			"    PagadorCieEntity b " +
			"where " +
			"(:esNullFiltreDir3Codi = true or b.dir3codi = :dir3codi) " + 
			"and b.entitat = :entitat")
	public Page<PagadorCieEntity> findByCodiDir3NotNullFiltrePaginatAndEntitat(
			@Param("esNullFiltreDir3Codi") boolean esNullFiltreDir3codi,
			@Param("dir3codi") String dir3codi, 
			@Param("entitat") EntitatEntity entitat,
			Pageable paginacio);
	
	List<PagadorCieEntity> findByEntitat(EntitatEntity entitat);
}
