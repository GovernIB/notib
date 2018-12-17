package es.caib.notib.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import es.caib.notib.core.entity.PagadorPostalEntity;

public interface PagadorPostalRepository extends JpaRepository<PagadorPostalEntity, Long> {

	PagadorPostalEntity findByDir3codi(String dir3codi);
	
	@Query(	"from " +
			"    PagadorPostalEntity b " +
			"where " +
			"(:esNullFiltreCodi = true or b.dir3codi = :dir3codi) " +
			"and (:esNullFiltreNumContracte = true or lower(b.contracteNum) like lower('%'||:filtreNumContracte||'%'))")
	Page<PagadorPostalEntity> findByCodiDir3AndNumContacteNotNullFiltrePaginat(
			@Param("esNullFiltreCodi") boolean esNullFiltreDir3codi,
			@Param("dir3codi") String dir3codi, 
			@Param("esNullFiltreNumContracte") boolean esNullFiltreNumContracte,
			@Param("filtreNumContracte") String filtreNumContracte,	
			Pageable paginacio);
	
}
