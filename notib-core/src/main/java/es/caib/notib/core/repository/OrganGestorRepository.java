package es.caib.notib.core.repository;

import java.util.List;

import es.caib.notib.core.api.dto.organisme.OrganGestorEstatEnum;
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

	List<OrganGestorEntity> findByEntitat(EntitatEntity entitat);
	public List<OrganGestorEntity> findByEntitatAndEstat(EntitatEntity entitat, OrganGestorEstatEnum estat);
	public Page<OrganGestorEntity> findByEntitat(EntitatEntity entitat, Pageable paginacio);
	public OrganGestorEntity findByCodi(String codi);

	@Query("from " +
			"    OrganGestorEntity og " +
			" where " +
			"     (og.entitat = :entitat)" +
			" 	and og.id in (:ids)")
	List<OrganGestorEntity> findByEntitatAndIds(@Param("entitat") EntitatEntity entitat, @Param("ids") List<Long> ids);


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
			" and (:isNomNull = true or lower(og.nom) like lower('%'||:nom||'%'))" +
			" and (:isOficinaNull = true or lower(og.oficina) like lower('%'||:oficina||'%'))")
	public Page<OrganGestorEntity> findByEntitatAndFiltre(
			@Param("entitat") EntitatEntity entitat,
			@Param("isCodiNull") boolean isCodiNull,
			@Param("codi") String codi,
			@Param("isNomNull") boolean isNomNull,
			@Param("nom") String nom,
			@Param("isOficinaNull") boolean isOficinaNull,
			@Param("oficina") String oficina,
			Pageable paginacio);
	
	@Query( "select distinct og " +
			"from OrganGestorEntity og " +
			"where (og.entitat = :entitat)" +
			"and og.codi in (:organsIds)")
	public Page<OrganGestorEntity> findByEntitatAndOrganGestor(
			@Param("entitat") EntitatEntity entitat,
			@Param("organsIds") List<String> organs,
			Pageable paginacio);
	
	@Query( "select distinct og " +
			"from OrganGestorEntity og " +
			"where (og.entitat = :entitat)" +
			"and og.codi in (:organsIds)")
	public List<OrganGestorEntity> findByEntitatAndOrgansGestors(
			@Param("entitat") EntitatEntity entitat,
			@Param("organsIds") List<String> organs);
	
	@Query(	"from " +
			"    OrganGestorEntity og " +
			"where (og.entitat = :entitat)" + 
			"and og.codi in (:organsIds)" +
			" and (:isCodiNull = true or lower(og.codi) like lower('%'||:codi||'%'))" +
			" and (:isNomNull = true or lower(og.nom) like lower('%'||:nom||'%'))" +
			" and (:isOficinaNull = true or lower(og.entitat.oficina) like lower('%'||:oficina||'%'))")
	public Page<OrganGestorEntity> findByEntitatAndOrganGestorAndFiltre(
			@Param("entitat") EntitatEntity entitat,
			@Param("organsIds") List<String> organs,
			@Param("isCodiNull") boolean isCodiNull,
			@Param("codi") String codi,
			@Param("isNomNull") boolean isNomNull,
			@Param("nom") String nom,
			@Param("isOficinaNull") boolean isOficinaNull,
			@Param("oficina") String oficina,
			Pageable paginacio);
	
	@Query(	"select distinct og.codi " +
			"from OrganGestorEntity og " +
			"     left outer join og.entitat e " + 
			"where e.dir3Codi = :entitatCodiDir3")
	public List<String> findCodisByEntitatDir3(@Param("entitatCodiDir3") String entitatCodiDir3);
	public List<OrganGestorEntity> findByCodiIn(List<String> organs);

	@Query(	" select " +
			"	CASE WHEN count(og) > 0 THEN true ELSE false END " +
			" from " +
			"     OrganGestorEntity og join og.entitat e " +
			" where " +
			"     og.id in (:organsIds) " +
			" and e.id = :entitatId ")
	boolean isAnyOfEntitat(@Param("organsIds") List<Long> organsIds,
						   @Param("entitatId") Long entitatId);
}
