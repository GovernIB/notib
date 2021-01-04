package es.caib.notib.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.ProcedimentOrganEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus procediment òrgan.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ProcedimentOrganRepository extends JpaRepository<ProcedimentOrganEntity, Long> {

	public ProcedimentOrganEntity findByProcedimentIdAndOrganGestorId(Long id, Long organGestorId);
	public ProcedimentOrganEntity findByProcedimentIdAndOrganGestorCodi(Long id, String organGestorCodi);

	public List<ProcedimentOrganEntity> findByProcedimentId(Long procedimentId);
	
	@Query(	"select po " +
			"from ProcedimentOrganEntity po " +
			"left outer join po.procediment pro " +
			"where pro.entitat = :entitat " +
			"  and (pro.agrupar = false " +
			"  	or (pro.agrupar = true " +
			"  and pro in (select distinct gp.procediment " +
			"		from GrupProcedimentEntity gp " +
			"		left outer join gp.grup g " +
			"		where g.entitat = :entitat " +
			"		  and g.codi in (:grups))) ) " +
			"order by pro.nom asc")
	public List<ProcedimentOrganEntity> findProcedimentsOrganByEntitatAndGrup(
			@Param("entitat") EntitatEntity entitat,
			@Param("grups") List<String> grups);

}
