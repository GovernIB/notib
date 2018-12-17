package es.caib.notib.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.ProcedimentEntity;

public interface ProcedimentRepository extends JpaRepository<ProcedimentEntity, Long> {

	ProcedimentEntity findByCodi(String codi);
	
	List<ProcedimentEntity> findByEntitat(
			EntitatEntity entitat);
	
	Page<ProcedimentEntity> findByEntitat(
			EntitatEntity entitat,
			Pageable paginacio);
	
	@Query(	"from " +
			"    ProcedimentEntity eu " +
			"where " +
			"    lower(eu.codi) like concat('%', lower(:filtre), '%') " +
			" or lower(eu.nom) like concat('%', lower(:filtre), '%') " +
			" or lower(eu.codisia) like concat('%', lower(:filtre), '%') ")
	public Page<ProcedimentEntity> findByFiltre(
			@Param("filtre") String filtre,
			Pageable paginacio);
}
