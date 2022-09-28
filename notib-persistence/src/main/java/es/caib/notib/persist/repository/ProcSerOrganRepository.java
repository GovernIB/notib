package es.caib.notib.persist.repository;

import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.ProcSerOrganEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus procediment òrgan.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ProcSerOrganRepository extends JpaRepository<ProcSerOrganEntity, Long> {

	public ProcSerOrganEntity findByProcSerIdAndOrganGestorId(Long id, Long organGestorId);
	public ProcSerOrganEntity findByProcSerIdAndOrganGestorCodi(Long id, String organGestorCodi);

	public List<ProcSerOrganEntity> findByProcSerId(Long procedimentId);
	
	@Query(	"select po " +
			"from ProcSerOrganEntity po " +
			"left outer join po.procSer pro " +
			"where pro.entitat = :entitat " +
			"  and (pro.agrupar = false " +
			"  	or (pro.agrupar = true " +
			"  and pro in (select distinct gp.procSer " +
			"		from GrupProcSerEntity gp " +
			"		left outer join gp.grup g " +
			"		where g.entitat = :entitat " +
			"		  and g.codi in (:grups))) ) " +
			"order by pro.nom asc")
	public List<ProcSerOrganEntity> findProcedimentsOrganByEntitatAndGrup(
			@Param("entitat") EntitatEntity entitat,
			@Param("grups") List<String> grups);

    List<ProcSerOrganEntity> findByProcSerIdAndOrganGestorCodiIn(Long procSerId, List<String> unitatsEntitat);

	@Query(	"select count(pog.id) from ProcSerOrganEntity pog where pog.organGestor = :organGestor")
	Integer countByOrganGestor(@Param("organGestor") OrganGestorEntity organGestor);
}
