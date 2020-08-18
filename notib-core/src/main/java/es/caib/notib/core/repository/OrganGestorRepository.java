package es.caib.notib.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.ProcedimentEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus procediment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface OrganGestorRepository extends JpaRepository<OrganGestorEntity, Long> {

	public List<OrganGestorEntity> findByEntitat(EntitatEntity entitat);
	public List<OrganGestorEntity> findByEntitatId(Long entitatId);
	public Page<OrganGestorEntity> findByEntitat(EntitatEntity entitat, Pageable paginacio);
	public OrganGestorEntity findByCodi(String codi);
	
	@Query(	"select distinct og " +
			"from " +
			"    ProcedimentEntity p " +
			"    left outer join p.organGestor og " +
			"where p.id in (:procedimentIds)")
	public List<OrganGestorEntity> findByProcedimentIds(@Param("procedimentIds") List<Long> procedimentIds);
	
	@Query( "select distinct og " +
			"from ProcedimentEntity pro " +
			"	  left outer join pro.organGestor og " +
			"where pro.entitat = :entitat " +
			"  and (pro.agrupar = false " +
			"  	or (pro.agrupar = true " +
			"  and pro in (select distinct gp.procediment " +
			"		from GrupProcedimentEntity gp " +
			"		left outer join gp.grup g " +
			"		where g.entitat = :entitat " +
			"		  and g.codi in (:grups))) ) " +
			"order by pro.nom asc")
	public List<ProcedimentEntity> findByEntitatAndGrup(
			@Param("entitat") EntitatEntity entitat,
			@Param("grups") List<String> grups);
	
	@Query(	"from " +
			"    OrganGestorEntity og " +
			"where (og.entitat = :entitat)" +
			" and (:isCodiNull = true or lower(og.codi) like lower('%'||:codi||'%'))" +
			" and (:isNomNull = true or lower(og.nom) like lower('%'||:nom||'%'))")
	public Page<OrganGestorEntity> findByEntitatAndFiltre(
			@Param("entitat") EntitatEntity entitat,
			@Param("isCodiNull") boolean isCodiNull,
			@Param("codi") String codi,
			@Param("isNomNull") boolean isNomNull,
			@Param("nom") String nom,
			Pageable paginacio);
	
	@Query(	"select distinct og.codi " +
			"from OrganGestorEntity og " +
			"     left outer join og.entitat e " + 
			"where e.dir3Codi = :entitatCodiDir3")
	public List<String> findCodisByEntitatDir3(@Param("entitatCodiDir3") String entitatCodiDir3);
}
