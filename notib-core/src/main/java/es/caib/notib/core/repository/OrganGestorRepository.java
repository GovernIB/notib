package es.caib.notib.core.repository;

import es.caib.notib.core.api.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
	OrganGestorEntity findByCodi(String codi);

	@Modifying
	@Query( " update " +
			"    OrganGestorEntity og " +
			" set " +
			"     og.estat = :estat")
	void updateAllStatus(@Param("estat") OrganGestorEstatEnum estat);

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
	List<OrganGestorEntity> findByProcedimentIds(@Param("procedimentIds") List<Long> procedimentIds);

	@Query(	"select distinct og " +
			"from " +
			"    ProcedimentEntity p " +
			"    left outer join p.organGestor og " +
			"where " +
			"	p.id in (:procedimentIds) " +
			"	and :estat = og.estat")
	List<OrganGestorEntity> findByEstatAndProcedimentIds(
			@Param("procedimentIds") List<Long> procedimentIds,
			@Param("estat") OrganGestorEstatEnum estat);

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
			" and (:isOficinaNull = true or lower(og.oficina) like lower('%'||:oficina||'%'))"+
			" and (:isEstatNull = true or og.estat = :estat)" +
			" and (:isEntregaCieActiva = false or og.entregaCie is not null)")
	Page<OrganGestorEntity> findByEntitatAndFiltre(
			@Param("entitat") EntitatEntity entitat,
			@Param("isCodiNull") boolean isCodiNull,
			@Param("codi") String codi,
			@Param("isNomNull") boolean isNomNull,
			@Param("nom") String nom,
			@Param("isOficinaNull") boolean isOficinaNull,
			@Param("oficina") String oficina,
			@Param("isEstatNull") boolean isEstatNull,
			@Param("estat") OrganGestorEstatEnum estat,
			@Param("isEntregaCieActiva") boolean isEntregaCieActiva,
			Pageable paginacio);
	
	@Query( "select distinct og " +
			"from OrganGestorEntity og " +
			"where (og.entitat = :entitat)" +
			"and og.codi in (:organsIds)")
	Page<OrganGestorEntity> findByEntitatAndOrganGestor(
			@Param("entitat") EntitatEntity entitat,
			@Param("organsIds") List<String> organs,
			Pageable paginacio);
	
	@Query( "select distinct og " +
			"from OrganGestorEntity og " +
			"where (og.entitat = :entitat)" +
			"and og.codi in (:organsIds)")
	List<OrganGestorEntity> findByEntitatAndOrgansGestors(
			@Param("entitat") EntitatEntity entitat,
			@Param("organsIds") List<String> organs);
	
	@Query(	"from " +
			"    OrganGestorEntity og " +
			"where (og.entitat = :entitat)" + 
			"and og.codi in (:organsIds)" +
			" and (:isCodiNull = true or lower(og.codi) like lower('%'||:codi||'%'))" +
			" and (:isNomNull = true or lower(og.nom) like lower('%'||:nom||'%'))" +
			" and (:isOficinaNull = true or lower(og.entitat.oficina) like lower('%'||:oficina||'%'))" +
			" and (:isEstatNull = true or og.estat = :estat)")
	Page<OrganGestorEntity> findByEntitatAndOrganGestorAndFiltre(
			@Param("entitat") EntitatEntity entitat,
			@Param("organsIds") List<String> organs,
			@Param("isCodiNull") boolean isCodiNull,
			@Param("codi") String codi,
			@Param("isNomNull") boolean isNomNull,
			@Param("nom") String nom,
			@Param("isOficinaNull") boolean isOficinaNull,
			@Param("oficina") String oficina,
			@Param("isEstatNull") boolean isEstatNull,
			@Param("estat") OrganGestorEstatEnum estat,
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
