package es.caib.notib.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.GrupEntity;

public interface GrupRepository extends JpaRepository<GrupEntity, Long> {

	public List<GrupEntity> findByEntitat(EntitatEntity entitat);
	
	public Page<GrupEntity> findByEntitat(
			EntitatEntity entitat,
			Pageable pageable);
	
	@Query(	"from " +
			"    GrupEntity b " +
			"where (:esNullFiltreCodi = true or b.codi = :codi) " + 
			"and b.entitat = :entitat")
	public Page<GrupEntity> findByCodiNotNullFiltrePaginat(
			@Param("esNullFiltreCodi") boolean esNullFiltrecodi,
			@Param("codi") String codi, 
			@Param("entitat") EntitatEntity entitat,
			Pageable paginacio);
}
