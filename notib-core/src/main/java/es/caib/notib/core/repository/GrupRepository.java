package es.caib.notib.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import es.caib.notib.core.entity.GrupEntity;
import es.caib.notib.core.entity.ProcedimentEntity;

public interface GrupRepository extends JpaRepository<GrupEntity, Long> {

	
	@Query(	"from " +
			"    GrupEntity b " +
			"where " +
			"(b.procediment = :procediment)")
	public List<GrupEntity> findByProcediment(@Param("procediment") ProcedimentEntity procediment);
	
	@Query(	"from " +
			"    GrupEntity b " +
			"where (b.procediment = :procediment) " + 
			"and b.id NOT IN :grupsId")
	public List<GrupEntity> findByIdProcedimentAndInGrupsId(
			@Param("procediment") ProcedimentEntity procediment,
			@Param("grupsId") List<Long> grupsId);
	
	@Query(	"from " +
			"    GrupEntity b " +
			"where " +
			"(:esNullFiltreCodi = true or b.codi = :codi)")
	public Page<GrupEntity> findByCodiNotNullFiltrePaginat(
			@Param("esNullFiltreCodi") boolean esNullFiltrecodi,
			@Param("codi") String codi, 
			Pageable paginacio);
}
