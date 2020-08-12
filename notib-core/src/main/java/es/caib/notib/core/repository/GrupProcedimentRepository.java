package es.caib.notib.core.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.GrupEntity;
import es.caib.notib.core.entity.GrupProcedimentEntity;
import es.caib.notib.core.entity.ProcedimentEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus grups procediment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface GrupProcedimentRepository extends JpaRepository<GrupProcedimentEntity, Long> {

	public List<GrupProcedimentEntity> findByGrup(GrupEntity grup);
	public List<GrupProcedimentEntity> findByProcediment(ProcedimentEntity procediment);
	public List<GrupProcedimentEntity> findByProcediment(ProcedimentEntity procediment, Pageable paginacio);
	public GrupProcedimentEntity findByGrupAndProcediment(GrupEntity grup, ProcedimentEntity procediment);
	public List<GrupProcedimentEntity> findByProcedimentEntitat(EntitatEntity entitat);
	
	@Query(
			"select distinct g.codi " +
			"  from GrupProcedimentEntity gp " +
			"  left outer join gp.procediment p " +
			"  left outer join gp.grup g " +
			" where p.codi in (:procedimentCodis) " +
			"	and p.entitat = :entitat ")
	public List<String> getGrupCodisByProcedimentsIds(
			@Param("procedimentCodis") List<String> procedimentCodis,
			@Param("entitat") EntitatEntity entitat);
}
