package es.caib.notib.persist.repository;

import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.ProcedimentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus procediment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ProcedimentRepository extends JpaRepository<ProcedimentEntity, Long> {

	@Query(
			"from ProcedimentEntity pro " +
			"where pro.entitat = :entitat " +
			"  and pro.agrupar = false ")
//			"  and pro.id not in (select distinct p.id " +
//			"		from GrupProcedimentEntity gp " +
//			"		left outer join gp.procediment p " +
//			"		where p.entitat = :entitat) ")
	public List<ProcedimentEntity> findProcedimentsSenseGrupsByEntitat(@Param("entitat") EntitatEntity entitat);
	
	@Query(
			"from ProcedimentEntity pro " +
			"where pro.entitat = :entitat " +
			"  and pro.agrupar = true " +
			"  and pro in (select distinct gp.procSer " +
			"		from GrupProcSerEntity gp " +
			"		left outer join gp.grup g " +
			"		where g.entitat = :entitat " +
			"		  and g.codi in (:grups)) ")
	public List<ProcedimentEntity> findProcedimentsAmbGrupsByEntitatAndGrup(
			@Param("entitat") EntitatEntity entitat,
			@Param("grups") List<String> grups);
	
	@Query(
			"from ProcedimentEntity pro " +
			"where pro.entitat = :entitat " +
			"  and (pro.agrupar = false " +
			"  	or (pro.agrupar = true " +
			"  and pro in (select distinct gp.procSer " +
			"		from GrupProcSerEntity gp " +
			"		left outer join gp.grup g " +
			"		where g.entitat = :entitat " +
			"		  and g.codi in (:grups))) ) " +
			"order by pro.nom asc")
	public List<ProcedimentEntity> findProcedimentsByEntitatAndGrup(
			@Param("entitat") EntitatEntity entitat,
			@Param("grups") List<String> grups);

	@Query("from " +
			"	ProcedimentEntity pro " +
			"where " +
			"		pro.entitat = :entitat " +
			"	and pro.id in (:ids)" +
			"  	and (pro.agrupar = false " +
			"  		or (pro.agrupar = true " +
			"  			and pro in (select distinct gp.procSer " +
			"						from GrupProcSerEntity gp " +
			"						left outer join gp.grup g " +
			"						where g.entitat = :entitat " +
			"		  					  and g.codi in (:grups))" +
			"			) " +
			"		) " +
			"order by pro.nom asc")
	List<ProcedimentEntity> findProcedimentsByEntitatAndGrupAndIds(
			@Param("entitat") EntitatEntity entitat,
			@Param("grups") List<String> grups,
			@Param("ids") List<Long> ids);

	@Query( "select distinct pro " +
			"from ProcedimentEntity pro " +
			"     left outer join pro.organGestor og " +
			"where pro.entitat = :entitat " + 
			"  and og.id = :organGestorId " +
			"  and (pro.agrupar = false " +
			"  	or (pro.agrupar = true " +
			"  and pro in (select distinct gp.procSer " +
			"		from GrupProcSerEntity gp " +
			"		left outer join gp.grup g " +
			"		where g.entitat = :entitat " +
			"		  and g.codi in (:grups))) ) " +
			"order by pro.nom asc")
	public List<ProcedimentEntity> findProcedimentsByOrganGestorAndGrup(
			@Param("entitat") EntitatEntity entitat,
			@Param("organGestorId") Long organGestorId,
			@Param("grups") List<String> grups);
	
	public List<ProcedimentEntity> findByComuTrue();

	List<ProcedimentEntity> findByEntitatAndComuTrue(EntitatEntity entitat);

	Set<ProcedimentEntity> findByEntitatAndComuTrueAndRequireDirectPermissionIsFalse(EntitatEntity entitat);

	List<ProcedimentEntity> findByEntitatActiva(boolean activa);
	
	@Query(
			"from " +
			"    ProcedimentEntity pro " +
			"where pro.entitat = (:entitatActual)")
	Page<ProcedimentEntity> findByEntitatActual(
			@Param("entitatActual") EntitatEntity entitatActiva,
			Pageable paginacio);
	
	@Query(
			"from " +
			"    ProcedimentEntity pro " +
			"where pro.entitat = (:entitatActual) and " + 
			"lower(pro.codi) = (lower(:codiProcediment))")
	ProcedimentEntity findByEntitatAndCodiProcediment(
			@Param("entitatActual") EntitatEntity entitat,
			@Param("codiProcediment") String codiProcediment);
	
	ProcedimentEntity findByIdAndEntitat(
			Long procedimentId, 
			EntitatEntity entitat);
	
//	ProcedimentEntity findById(Long procedimentId);
	
	ProcedimentEntity findByCodi(String codi);
	
	ProcedimentEntity findByCodiAndEntitat(String codi, EntitatEntity entitat);
	
	@Query(
			"from " +
			"    ProcedimentEntity pro " +
			"where pro.entitat in (:entitatActiva)")
	Page<ProcedimentEntity> findByEntitatActiva(
			@Param("entitatActiva") List<EntitatEntity> entitatActiva,
			Pageable paginacio);
	
	List<ProcedimentEntity> findByEntitat(
			EntitatEntity entitat);
	
	List<ProcedimentEntity> findByEntitatOrderByNomAsc(
			EntitatEntity entitat);
	
	Page<ProcedimentEntity> findByEntitat(
			EntitatEntity entitat,
			Pageable paginacio);


	@Query(	"from " +
			"    ProcedimentEntity pro " +
			"where (:isCodiNull = true or lower(pro.codi) like lower('%'||:codi||'%'))" +
			" and (:isNomNull = true or lower(pro.nom) like lower('%'||:nom||'%'))")
	public Page<ProcedimentEntity> findAmbEntitatAndFiltre(
			@Param("isCodiNull") boolean isCodiNull,
			@Param("codi") String codi,
			@Param("isNomNull") boolean isNomNull,
			@Param("nom") String nom,
			Pageable paginacio);
	
	@Query(	"from " +
			"    ProcedimentEntity pro " +
			"where ((:isCodiNull = true) or (lower(pro.codi) like lower('%'||:codi||'%')))" + 
			" and ((:isNomNull = true) or (lower(pro.nom) like lower('%'||:nom||'%')))")
	public Page<ProcedimentEntity> findAmbFiltre(
			@Param("isCodiNull") boolean isCodiNull,
			@Param("codi") String codi,
			@Param("isNomNull") boolean isNomNull,
			@Param("nom") String nom,
			Pageable paginacio);

	@Query(	"select distinct pro.organGestor " +
			"  from ProcedimentEntity pro " +
			" where pro.entitat = :entitat")
	public List<String> findOrgansGestorsCodisByEntitat(@Param("entitat") EntitatEntity entitat);
	
	List<ProcedimentEntity> findByOrganGestorId(Long organGestorId);

	@Query(
			"from ProcedimentEntity pro " +
			"where pro.organGestor.codi in (:organsCodis) " +
			"  and pro.requireDirectPermission = false" +
			"  and (pro.agrupar = false " +
			"  	or (pro.agrupar = true " +
			"  and pro in (select distinct gp.procSer " +
			"		from GrupProcSerEntity gp " +
			"		left outer join gp.grup g " +
			"		where g.codi in (:grups))) ) " +
			"order by pro.nom asc")
	List<ProcedimentEntity> findProcedimentsAccesiblesPerOrganGestor(
			@Param("organsCodis") List<String> organsCodis,
			@Param("grups") List<String> grups);

	public List<ProcedimentEntity> findByOrganGestorCodiIn(List<String> organsFills);

	@Query(
			"from ProcedimentEntity pro " +
			"where (pro.organGestor.codi in (:organsCodis) " +
			"  	or pro.comu = true) " +
			"  and pro.entitat in (:entitat) " +
			"order by pro.nom asc")
	public List<ProcedimentEntity> findByOrganGestorCodiInOrComu(
			@Param("organsCodis") List<String> organsCodis,
			@Param("entitat") EntitatEntity entitat);
	
	@Query(
			"from " +
			"    ProcedimentEntity pro " +
			"where pro.entitat = (:entitatActual) and " + 
			"lower(pro.nom) = (lower(:nomProcediment))")
	List<ProcedimentEntity> findByNomAndEntitat(
			@Param("nomProcediment") String nomProcediment,
			@Param("entitatActual") EntitatEntity entitat);

	Integer countByEntitatIdAndOrganNoSincronitzatTrue(Long entitatId);

	@Query(	"select distinct pro.codi " +
			"  from ProcedimentEntity pro " +
			" where pro.actiu = true")
	public List<String> findCodiActius();

	@Modifying
	@Query("update ProcedimentEntity pro set pro.actiu = :actiu where pro.codi = :codi")
	public void updateActiu(@Param("codi") String codi, @Param("actiu") boolean actiu);
}
