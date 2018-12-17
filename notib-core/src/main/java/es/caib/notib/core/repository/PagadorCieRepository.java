package es.caib.notib.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import es.caib.notib.core.entity.PagadorCieEntity;

public interface PagadorCieRepository extends JpaRepository<PagadorCieEntity, Long> {

	
	@Query(	"from " +
			"    PagadorCieEntity b " +
			"where " +
			"(:esNullFiltreDir3Codi = true or b.dir3codi = :dir3codi)")
	public Page<PagadorCieEntity> findByCodiDir3NotNullFiltrePaginat(
			@Param("esNullFiltreDir3Codi") boolean esNullFiltreDir3codi,
			@Param("dir3codi") String dir3codi, 
			Pageable paginacio);
}
