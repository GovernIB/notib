package es.caib.notib.core.repository;

import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.ServeiEntity;
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
 * de dades del tipus servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ServeiRepository extends JpaRepository<ServeiEntity, Long> {

	@Query(
			"from ServeiEntity pro " +
			"where pro.entitat = :entitat " +
			"  and pro.agrupar = false ")
//			"  and pro.id not in (select distinct p.id " +
//			"		from GrupServeiEntity gp " +
//			"		left outer join gp.servei p " +
//			"		where p.entitat = :entitat) ")
	public List<ServeiEntity> findServeisSenseGrupsByEntitat(@Param("entitat") EntitatEntity entitat);
	
	@Query(
			"from ServeiEntity pro " +
			"where pro.entitat = :entitat " +
			"  and pro.agrupar = true " +
			"  and pro in (select distinct gp.procSer " +
			"		from GrupProcSerEntity gp " +
			"		left outer join gp.grup g " +
			"		where g.entitat = :entitat " +
			"		  and g.codi in (:grups)) ")
	public List<ServeiEntity> findServeisAmbGrupsByEntitatAndGrup(
            @Param("entitat") EntitatEntity entitat,
            @Param("grups") List<String> grups);
	
	@Query(
			"from ServeiEntity pro " +
			"where pro.entitat = :entitat " +
			"  and (pro.agrupar = false " +
			"  	or (pro.agrupar = true " +
			"  and pro in (select distinct gp.procSer " +
			"		from GrupProcSerEntity gp " +
			"		left outer join gp.grup g " +
			"		where g.entitat = :entitat " +
			"		  and g.codi in (:grups))) ) " +
			"order by pro.nom asc")
	public List<ServeiEntity> findServeisByEntitatAndGrup(
            @Param("entitat") EntitatEntity entitat,
            @Param("grups") List<String> grups);

	@Query("from " +
			"	ServeiEntity pro " +
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
	List<ServeiEntity> findServeisByEntitatAndGrupAndIds(
            @Param("entitat") EntitatEntity entitat,
            @Param("grups") List<String> grups,
            @Param("ids") List<Long> ids);

	@Query( "select distinct pro " +
			"from ServeiEntity pro " +
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
	public List<ServeiEntity> findServeisByOrganGestorAndGrup(
            @Param("entitat") EntitatEntity entitat,
            @Param("organGestorId") Long organGestorId,
            @Param("grups") List<String> grups);
	
	public List<ServeiEntity> findByComuTrue();

	List<ServeiEntity> findByEntitatAndComuTrue(EntitatEntity entitat);

	Set<ServeiEntity> findByEntitatAndComuTrueAndRequireDirectPermissionIsFalse(EntitatEntity entitat);

	List<ServeiEntity> findByEntitatActiva(boolean activa);
	
	@Query(
			"from " +
			"    ServeiEntity pro " +
			"where pro.entitat = (:entitatActual)")
	Page<ServeiEntity> findByEntitatActual(
            @Param("entitatActual") EntitatEntity entitatActiva,
            Pageable paginacio);
	
	@Query(
			"from " +
			"    ServeiEntity pro " +
			"where pro.entitat = (:entitatActual) and " + 
			"lower(pro.codi) = (lower(:codiServei))")
	ServeiEntity findByEntitatAndCodiServei(
            @Param("entitatActual") EntitatEntity entitat,
            @Param("codiServei") String codiServei);
	
	ServeiEntity findByIdAndEntitat(
            Long serveiId,
            EntitatEntity entitat);
	
	ServeiEntity findById(
            Long serveiId);
	
	ServeiEntity findByCodi(String codi);
	
	ServeiEntity findByCodiAndEntitat(String codi, EntitatEntity entitat);
	
	@Query(
			"from " +
			"    ServeiEntity pro " +
			"where pro.entitat in (:entitatActiva)")
	Page<ServeiEntity> findByEntitatActiva(
            @Param("entitatActiva") List<EntitatEntity> entitatActiva,
            Pageable paginacio);
	
	List<ServeiEntity> findByEntitat(
            EntitatEntity entitat);
	
	List<ServeiEntity> findByEntitatOrderByNomAsc(
            EntitatEntity entitat);
	
	Page<ServeiEntity> findByEntitat(
            EntitatEntity entitat,
            Pageable paginacio);


	@Query(	"from " +
			"    ServeiEntity pro " +
			"where (:isCodiNull = true or lower(pro.codi) like lower('%'||:codi||'%'))" +
			" and (:isNomNull = true or lower(pro.nom) like lower('%'||:nom||'%'))")
	public Page<ServeiEntity> findAmbEntitatAndFiltre(
            @Param("isCodiNull") boolean isCodiNull,
            @Param("codi") String codi,
            @Param("isNomNull") boolean isNomNull,
            @Param("nom") String nom,
            Pageable paginacio);
	
	@Query(	"from " +
			"    ServeiEntity pro " +
			"where ((:isCodiNull = true) or (lower(pro.codi) like lower('%'||:codi||'%')))" + 
			" and ((:isNomNull = true) or (lower(pro.nom) like lower('%'||:nom||'%')))")
	public Page<ServeiEntity> findAmbFiltre(
            @Param("isCodiNull") boolean isCodiNull,
            @Param("codi") String codi,
            @Param("isNomNull") boolean isNomNull,
            @Param("nom") String nom,
            Pageable paginacio);

	@Query(	"select distinct pro.organGestor " +
			"  from ServeiEntity pro " +
			" where pro.entitat = :entitat")
	public List<String> findOrgansGestorsCodisByEntitat(@Param("entitat") EntitatEntity entitat);
	
	List<ServeiEntity> findByOrganGestorId(Long organGestorId);

	@Query(
			"from ServeiEntity pro " +
			"where pro.organGestor.codi in (:organsCodis) " +
			"  and pro.requireDirectPermission = false" +
			"  and (pro.agrupar = false " +
			"  	or (pro.agrupar = true " +
			"  and pro in (select distinct gp.procSer " +
			"		from GrupProcSerEntity gp " +
			"		left outer join gp.grup g " +
			"		where g.codi in (:grups))) ) " +
			"order by pro.nom asc")
	List<ServeiEntity> findServeisAccesiblesPerOrganGestor(
            @Param("organsCodis") List<String> organsCodis,
            @Param("grups") List<String> grups);

	public List<ServeiEntity> findByOrganGestorCodiIn(List<String> organsFills);

	@Query(
			"from ServeiEntity pro " +
			"where (pro.organGestor.codi in (:organsCodis) " +
			"  	or pro.comu = true) " +
			"  and pro.entitat in (:entitat) " +
			"order by pro.nom asc")
	public List<ServeiEntity> findByOrganGestorCodiInOrComu(
            @Param("organsCodis") List<String> organsCodis,
            @Param("entitat") EntitatEntity entitat);
	
	@Query(
			"from " +
			"    ServeiEntity pro " +
			"where pro.entitat = (:entitatActual) and " + 
			"lower(pro.nom) = (lower(:nomServei))")
	List<ServeiEntity> findByNomAndEntitat(
            @Param("nomServei") String nomServei,
            @Param("entitatActual") EntitatEntity entitat);

    Integer countByEntitatIdAndOrganNoSincronitzatTrue(Long entitatId);

	Integer countByEntitatId(Long entitatId);
	Integer countByEntitatIdAndActiuTrue(Long entitatId);
	Integer countByEntitatIdAndActiuFalse(Long entitatId);

	@Query(	"select distinct pro.codi " +
			"  from ServeiEntity pro " +
			" where pro.entitat.codi = :entitatCodi and pro.actiu = true")
	public List<String> findCodiActiusByEntitat(@Param("entitatCodi") String entitatCodi);

	@Modifying
	@Query("update ServeiEntity pro set pro.actiu = :actiu where pro.codi = :codi")
	public void updateActiu(@Param("codi") String codi, @Param("actiu") boolean actiu);

}
