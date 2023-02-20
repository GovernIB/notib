package es.caib.notib.persist.repository;

import es.caib.notib.logic.intf.dto.ProcSerTipusEnum;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.ProcSerEntity;
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
	List<ProcSerOrganEntity> findProcedimentsOrganByEntitatAndGrup(@Param("entitat") EntitatEntity entitat, @Param("grups") List<String> grups);

    List<ProcSerOrganEntity> findByProcSerIdAndOrganGestorCodiIn(Long procSerId, List<String> unitatsEntitat);

	@Query(	"select count(pog.id) from ProcSerOrganEntity pog where pog.organGestor = :organGestor")
	Integer countByOrganGestor(@Param("organGestor") OrganGestorEntity organGestor);

	@Query(	"select count(po.procSer) " +
			"from ProcSerOrganEntity po " +
			"left outer join po.procSer pro " +
			"where pro.entitat = :entitat " +
			"  and po.id in (:ids)" +
			"  and (pro.agrupar = false " +
			"  	or (pro.agrupar = true " +
			"  and pro in (select distinct gp.procSer " +
			"		from GrupProcSerEntity gp " +
			"		left outer join gp.grup g " +
			"		where g.entitat = :entitat " +
			"		  and g.codi in (:grups))) ) " +
			"order by pro.nom asc")
	public Long countProcedimentsByEntitatAndGrupAndIds(@Param("entitat") EntitatEntity entitat, @Param("grups") List<String> grups, @Param("ids") List<Long> ids);

	@Query(	"select po " +
			"from ProcSerOrganEntity po " +
			"left outer join po.procSer pro " +
			"where pro.entitat = :entitat " +
			"  and po.id in (:ids)" +
			"  and (:isTipusNull = true or pro.tipus = :tipus) " +
			"  and (pro.agrupar = false " +
			"  	or (pro.agrupar = true " +
			"  and pro in (select distinct gp.procSer " +
			"		from GrupProcSerEntity gp " +
			"		left outer join gp.grup g " +
			"		where g.entitat = :entitat " +
			"		  and g.codi in (:grups))) ) " +
			"order by pro.nom asc")
	public List<ProcSerOrganEntity> findProcedimentsByEntitatAndGrupAndIds(
			@Param("entitat") EntitatEntity entitat,
			@Param("grups") List<String> grups,
			@Param("ids") List<Long> ids,
			@Param("isTipusNull") boolean isTipusNull,
			@Param("tipus") ProcSerTipusEnum tipus);

	@Query(	"from ProcSerOrganEntity po " +
			"left outer join po.procSer pro " +
			"where pro.entitat = :entitat " +
			"  and pro.id in (:ids)" +
			"  and (:isTipusNull = true or pro.tipus = :tipus) " +
			"  and (pro.agrupar = false " +
			"  	or (pro.agrupar = true " +
			"  and pro in (select distinct gp.procSer " +
			"		from GrupProcSerEntity gp " +
			"		left outer join gp.grup g " +
			"		where g.entitat = :entitat " +
			"		  and g.codi in (:grups))) ) " +
			"order by pro.nom asc")
	public List<ProcSerOrganEntity> findByEntitatAndGrupAndIds(
			@Param("entitat") EntitatEntity entitat,
			@Param("grups") List<String> grups,
			@Param("ids") List<Long> ids,
			@Param("isTipusNull") boolean isTipusNull,
			@Param("tipus") ProcSerTipusEnum tipus);

	@Query(	"select count(po.procSer) " +
			"from ProcSerOrganEntity po " +
			"left outer join po.procSer pro " +
			"where pro.entitat = :entitat " +
			"  and pro.actiu = true " +
			"  and po.id in (:ids)" +
			"  and (pro.agrupar = false " +
			"  	or (pro.agrupar = true " +
			"  and pro in (select distinct gp.procSer " +
			"		from GrupProcSerEntity gp " +
			"		left outer join gp.grup g " +
			"		where g.entitat = :entitat " +
			"		  and g.codi in (:grups))) ) " +
			"order by pro.nom asc")
	public Long countProcedimentsActiusByEntitatAndGrupAndIds(@Param("entitat") EntitatEntity entitat, @Param("grups") List<String> grups, @Param("ids") List<Long> ids);

	@Query(	"select po " +
			"from ProcSerOrganEntity po " +
			"left outer join po.procSer pro " +
			"where pro.entitat = :entitat " +
			"  and pro.actiu = true " +
			"  and (:isTipusNull = true or pro.tipus = :tipus) " +
			"  and po.id in (:ids)" +
			"  and (pro.agrupar = false " +
			"  	or (pro.agrupar = true " +
			"  and pro in (select distinct gp.procSer " +
			"		from GrupProcSerEntity gp " +
			"		left outer join gp.grup g " +
			"		where g.entitat = :entitat " +
			"		  and g.codi in (:grups))) ) " +
			"order by pro.nom asc")
	public List<ProcSerOrganEntity> findProcedimentsActiusByEntitatAndGrupAndIds(
			@Param("entitat") EntitatEntity entitat,
			@Param("grups") List<String> grups,
			@Param("ids") List<Long> ids,
			@Param("isTipusNull") boolean isTipusNull,
			@Param("tipus") ProcSerTipusEnum tipus);

	@Query(	"select po " +
			"from ProcSerOrganEntity po " +
			"left outer join po.procSer pro " +
			"where pro.entitat = :entitat " +
			"  and pro.actiu = true " +
			"  and (:isTipusNull = true or pro.tipus = :tipus) " +
			"  and po.id in (:ids)" +
			"  and (pro.agrupar = false " +
			"  	or (pro.agrupar = true " +
			"  and pro in (select distinct gp.procSer " +
			"		from GrupProcSerEntity gp " +
			"		left outer join gp.grup g " +
			"		where g.entitat = :entitat " +
			"		  and g.codi in (:grups))) ) " +
			"order by pro.nom asc")
	public List<ProcSerOrganEntity> findActiusByEntitatAndGrupAndIds(
			@Param("entitat") EntitatEntity entitat,
			@Param("grups") List<String> grups,
			@Param("ids") List<Long> ids,
			@Param("isTipusNull") boolean isTipusNull,
			@Param("tipus") ProcSerTipusEnum tipus);
}
