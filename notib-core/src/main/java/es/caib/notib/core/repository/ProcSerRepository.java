package es.caib.notib.core.repository;

import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.ProcSerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus procediment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ProcSerRepository extends JpaRepository<ProcSerEntity, Long> {

//	@Query(
//			"from ProcSerEntity pro " +
//			"where pro.entitat = :entitat " +
//			"  and pro.agrupar = false ")
////			"  and pro.id not in (select distinct p.id " +
////			"		from GrupProcedimentEntity gp " +
////			"		left outer join gp.procediment p " +
////			"		where p.entitat = :entitat) ")
//	public List<ProcedimentEntity> findProcedimentsSenseGrupsByEntitat(@Param("entitat") EntitatEntity entitat);
//
//	@Query(
//			"from ProcedimentEntity pro " +
//			"where pro.entitat = :entitat " +
//			"  and pro.agrupar = true " +
//			"  and pro in (select distinct gp.procser " +
//			"		from GrupProcSerEntity gp " +
//			"		left outer join gp.grup g " +
//			"		where g.entitat = :entitat " +
//			"		  and g.codi in (:grups)) ")
//	public List<ProcedimentEntity> findProcedimentsAmbGrupsByEntitatAndGrup(
//            @Param("entitat") EntitatEntity entitat,
//            @Param("grups") List<String> grups);
//
//	@Query(
//			"from ProcedimentEntity pro " +
//			"where pro.entitat = :entitat " +
//			"  and (pro.agrupar = false " +
//			"  	or (pro.agrupar = true " +
//			"  and pro in (select distinct gp.procser " +
//			"		from GrupProcSerEntity gp " +
//			"		left outer join gp.grup g " +
//			"		where g.entitat = :entitat " +
//			"		  and g.codi in (:grups))) ) " +
//			"order by pro.nom asc")
//	public List<ProcedimentEntity> findProcedimentsByEntitatAndGrup(
//            @Param("entitat") EntitatEntity entitat,
//            @Param("grups") List<String> grups);

	@Query("select count(pro) " +
			"from " +
			"	ProcSerEntity pro " +
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
	Long countProcedimentsByEntitatAndGrupAndIds(
			@Param("entitat") EntitatEntity entitat,
			@Param("grups") List<String> grups,
			@Param("ids") List<Long> ids);

	@Query("from " +
			"	ProcSerEntity pro " +
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
	List<ProcSerEntity> findProcedimentsByEntitatAndGrupAndIds(
            @Param("entitat") EntitatEntity entitat,
            @Param("grups") List<String> grups,
            @Param("ids") List<Long> ids);
	@Query("from " +
			"	ProcSerEntity pro " +
			"where " +
			"		pro.entitat = :entitat " +
			"	and	(:isTipusNull = true or pro.tipus = :tipus) " +
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
	List<ProcSerEntity> findProcedimentsByEntitatAndGrupAndIds(
			@Param("entitat") EntitatEntity entitat,
			@Param("grups") List<String> grups,
			@Param("ids") List<Long> ids,
			@Param("isTipusNull") boolean isTipusNull,
			@Param("tipus") String tipus);

	@Query("select count(pro) " +
			"from " +
			"	ProcSerEntity pro " +
			"where " +
			"		pro.entitat = :entitat " +
			"	and pro.actiu = true " +
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
	Long countProcedimentsActiusByEntitatAndGrupAndIds(
			@Param("entitat") EntitatEntity entitat,
			@Param("grups") List<String> grups,
			@Param("ids") List<Long> ids);

	@Query("from " +
			"	ProcSerEntity pro " +
			"where " +
			"		pro.entitat = :entitat " +
			"	and	(:isTipusNull = true or pro.tipus = :tipus) " +
			"	and pro.actiu = true " +
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
	List<ProcSerEntity> findProcedimentsActiusByEntitatAndGrupAndIds(
			@Param("entitat") EntitatEntity entitat,
			@Param("grups") List<String> grups,
			@Param("ids") List<Long> ids,
			@Param("isTipusNull") boolean isTipusNull,
			@Param("tipus") String tipus);

//	@Query( "select distinct pro " +
//			"from ProcedimentEntity pro " +
//			"     left outer join pro.organGestor og " +
//			"where pro.entitat = :entitat " +
//			"  and og.id = :organGestorId " +
//			"  and (pro.agrupar = false " +
//			"  	or (pro.agrupar = true " +
//			"  and pro in (select distinct gp.procser " +
//			"		from GrupProcSerEntity gp " +
//			"		left outer join gp.grup g " +
//			"		where g.entitat = :entitat " +
//			"		  and g.codi in (:grups))) ) " +
//			"order by pro.nom asc")
//	public List<ProcedimentEntity> findProcedimentsByOrganGestorAndGrup(
//            @Param("entitat") EntitatEntity entitat,
//            @Param("organGestorId") Long organGestorId,
//            @Param("grups") List<String> grups);
//
//	public List<ProcedimentEntity> findByComuTrue();
//
//	List<ProcedimentEntity> findByEntitatAndComuTrue(EntitatEntity entitat);
//
//	Set<ProcedimentEntity> findByEntitatAndComuTrueAndRequireDirectPermissionIsFalse(EntitatEntity entitat);
//
//	List<ProcedimentEntity> findByEntitatActiva(boolean activa);
//
//	@Query(
//			"from " +
//			"    ProcedimentEntity pro " +
//			"where pro.entitat = (:entitatActual)")
//	Page<ProcedimentEntity> findByEntitatActual(
//            @Param("entitatActual") EntitatEntity entitatActiva,
//            Pageable paginacio);
//
//	ProcedimentEntity findByIdAndEntitat(
//            Long procedimentId,
//            EntitatEntity entitat);

	ProcSerEntity findById(Long procserId);

//	ProcedimentEntity findByCodi(String codi);
//
//	@Query(
//			"from " +
//			"    ProcedimentEntity pro " +
//			"where pro.entitat in (:entitatActiva)")
//	Page<ProcedimentEntity> findByEntitatActiva(
//            @Param("entitatActiva") List<EntitatEntity> entitatActiva,
//            Pageable paginacio);
//
//	List<ProcedimentEntity> findByEntitat(
//            EntitatEntity entitat);
//
//	List<ProcedimentEntity> findByEntitatOrderByNomAsc(
//            EntitatEntity entitat);
//
//	Page<ProcedimentEntity> findByEntitat(
//            EntitatEntity entitat,
//            Pageable paginacio);
//
//
//	@Query(	"from " +
//			"    ProcedimentEntity pro " +
//			"where (:isCodiNull = true or lower(pro.codi) like lower('%'||:codi||'%'))" +
//			" and (:isNomNull = true or lower(pro.nom) like lower('%'||:nom||'%'))")
//	public Page<ProcedimentEntity> findAmbEntitatAndFiltre(
//            @Param("isCodiNull") boolean isCodiNull,
//            @Param("codi") String codi,
//            @Param("isNomNull") boolean isNomNull,
//            @Param("nom") String nom,
//            Pageable paginacio);
//
//	@Query(	"from " +
//			"    ProcedimentEntity pro " +
//			"where ((:isCodiNull = true) or (lower(pro.codi) like lower('%'||:codi||'%')))" +
//			" and ((:isNomNull = true) or (lower(pro.nom) like lower('%'||:nom||'%')))")
//	public Page<ProcedimentEntity> findAmbFiltre(
//            @Param("isCodiNull") boolean isCodiNull,
//            @Param("codi") String codi,
//            @Param("isNomNull") boolean isNomNull,
//            @Param("nom") String nom,
//            Pageable paginacio);
//
//	@Query(	"select distinct pro.organGestor " +
//			"  from ProcedimentEntity pro " +
//			" where pro.entitat = :entitat")
//	public List<String> findOrgansGestorsCodisByEntitat(@Param("entitat") EntitatEntity entitat);
//
//	List<ProcedimentEntity> findByOrganGestorId(Long organGestorId);

	@Query(	"select count(pro) " +
			" from ProcSerEntity pro " +
			"where (pro.organGestor.codi in (:organsCodis) " +
			"  and pro.entitat = :entitat " +
			"  and pro.requireDirectPermission = false" +
			"  and (pro.agrupar = false " +
			"  	or (pro.agrupar = true " +
			"  and pro in (select distinct gp.procSer " +
			"		from GrupProcSerEntity gp " +
			"		left outer join gp.grup g " +
			"		where g.codi in (:grups))) ) " +
			") " +
			"order by pro.nom asc")
	Long countProcedimentsAccesiblesPerOrganGestor(
			@Param("entitat") EntitatEntity entitat,
			@Param("organsCodis") List<String> organsCodis,
			@Param("grups") List<String> grups);
	@Query(
			"from ProcSerEntity pro " +
			"where (pro.organGestor.codi in (:organsCodis) " +
			"  and pro.entitat = :entitat " +
			"  and pro.requireDirectPermission = false" +
			"  and (pro.agrupar = false " +
			"  	or (pro.agrupar = true " +
			"  and pro in (select distinct gp.procSer " +
			"		from GrupProcSerEntity gp " +
			"		left outer join gp.grup g " +
			"		where g.codi in (:grups))) ) " +
				") " +
			"order by pro.nom asc")
	List<ProcSerEntity> findProcedimentsAccesiblesPerOrganGestor(
			@Param("entitat") EntitatEntity entitat,
            @Param("organsCodis") List<String> organsCodis,
            @Param("grups") List<String> grups);
	@Query(
			"from ProcSerEntity pro " +
					"where (pro.organGestor.codi in (:organsCodis) " +
					"  and pro.entitat = :entitat " +
					"  and pro.requireDirectPermission = false" +
					"  and (:isTipusNull = true or pro.tipus = :tipus) " +
					"  and (pro.agrupar = false " +
					"  	or (pro.agrupar = true " +
					"  and pro in (select distinct gp.procSer " +
					"		from GrupProcSerEntity gp " +
					"		left outer join gp.grup g " +
					"		where g.codi in (:grups))) ) " +
					") " +
					"order by pro.nom asc")
	List<ProcSerEntity> findProcedimentsAccesiblesPerOrganGestor(
			@Param("entitat") EntitatEntity entitat,
			@Param("organsCodis") List<String> organsCodis,
			@Param("grups") List<String> grups,
			@Param("isTipusNull") boolean isTipusNull,
			@Param("tipus") String tipus);

	@Query(	"select count(pro) " +
			" from ProcSerEntity pro " +
			"where (pro.organGestor.codi in (:organsCodis) " +
			"  and pro.entitat = :entitat " +
			"  and pro.requireDirectPermission = false" +
			"  and pro.actiu = true " +
			"  and (pro.agrupar = false " +
			"  	or (pro.agrupar = true " +
			"  and pro in (select distinct gp.procSer " +
			"		from GrupProcSerEntity gp " +
			"		left outer join gp.grup g " +
			"		where g.codi in (:grups))) ) " +
			") " +
			"order by pro.nom asc")
	Long countProcedimentsActiusAccesiblesPerOrganGestor(
			@Param("entitat") EntitatEntity entitat,
			@Param("organsCodis") List<String> organsCodis,
			@Param("grups") List<String> grups);
	@Query(
			"from ProcSerEntity pro " +
			"where (pro.organGestor.codi in (:organsCodis) " +
			"  and pro.entitat = :entitat " +
			"  and pro.requireDirectPermission = false" +
			"  and pro.actiu = true " +
			"  and (:isTipusNull = true or pro.tipus = :tipus) " +
			"  and (pro.agrupar = false " +
			"  	or (pro.agrupar = true " +
			"  and pro in (select distinct gp.procSer " +
			"		from GrupProcSerEntity gp " +
			"		left outer join gp.grup g " +
			"		where g.codi in (:grups))) ) " +
			") " +
			"order by pro.nom asc")
	List<ProcSerEntity> findProcedimentsActiusAccesiblesPerOrganGestor(
			@Param("entitat") EntitatEntity entitat,
			@Param("organsCodis") List<String> organsCodis,
			@Param("grups") List<String> grups,
			@Param("isTipusNull") boolean isTipusNull,
			@Param("tipus") String tipus);

	ProcSerEntity findByCodiAndEntitat(String codi, EntitatEntity entitat);

	@Query(
			"from " +
					"    ProcedimentEntity pro " +
					"where pro.entitat = (:entitatActual) and " +
					"lower(pro.codi) = (lower(:codiProcediment))")
	ProcSerEntity findByEntitatAndCodiProcediment(
			@Param("entitatActual") EntitatEntity entitat,
			@Param("codiProcediment") String codiProcediment);

	@Query(	"select count(p) " +
			"from ProcSerEntity p " +
			"where p.entitat = :entitat " +
			"  and p.requireDirectPermission = false " +
			"  and p.comu = true " +
			"  and (p.agrupar = false " +
			"  	or (p.agrupar = true " +
			"  and p in (select distinct gp.procSer " +
			"		from GrupProcSerEntity gp " +
			"		left outer join gp.grup g " +
			"		where g.entitat = :entitat " +
			"		  and g.codi in (:grups))) ) " +
			"order by p.nom asc")
	Long countProcedimentsComusByEntitatSenseAccesDirecte(
			@Param("entitat") EntitatEntity entitat,
			@Param("grups") List<String> grups
	);
	@Query(	"select p " +
			"from ProcSerEntity p " +
			"where p.entitat = :entitat " +
			"  and p.requireDirectPermission = false " +
			"  and p.comu = true " +
			"  and (:isTipusNull = true or p.tipus = :tipus) " +
			"  and (p.agrupar = false " +
			"  	or (p.agrupar = true " +
			"  and p in (select distinct gp.procSer " +
			"		from GrupProcSerEntity gp " +
			"		left outer join gp.grup g " +
			"		where g.entitat = :entitat " +
			"		  and g.codi in (:grups))) ) " +
			"order by p.nom asc")
	List<ProcSerEntity> findProcedimentsComusByEntitatSenseAccesDirecte(
			@Param("entitat") EntitatEntity entitat,
			@Param("grups") List<String> grups,
			@Param("isTipusNull") boolean isTipusNull,
			@Param("tipus") String tipus
	);

	@Query(	"select count(p) " +
			"from ProcSerEntity p " +
			"where p.entitat = :entitat " +
			"  and p.requireDirectPermission = false " +
			"  and p.comu = true " +
			"  and p.actiu = true " +
			"  and (p.agrupar = false " +
			"  	or (p.agrupar = true " +
			"  and p in (select distinct gp.procSer " +
			"		from GrupProcSerEntity gp " +
			"		left outer join gp.grup g " +
			"		where g.entitat = :entitat " +
			"		  and g.codi in (:grups))) ) " +
			"order by p.nom asc")
	Long countProcedimentsComusActiusByEntitatSenseAccesDirecte(
			@Param("entitat") EntitatEntity entitat,
			@Param("grups") List<String> grups
	);
	@Query(	"select p " +
			"from ProcSerEntity p " +
			"where p.entitat = :entitat " +
			"  and p.requireDirectPermission = false " +
			"  and p.comu = true " +
			"  and p.actiu = true " +
			"  and (:isTipusNull = true or p.tipus = :tipus) " +
			"  and (p.agrupar = false " +
			"  	or (p.agrupar = true " +
			"  and p in (select distinct gp.procSer " +
			"		from GrupProcSerEntity gp " +
			"		left outer join gp.grup g " +
			"		where g.entitat = :entitat " +
			"		  and g.codi in (:grups))) ) " +
			"order by p.nom asc")
	List<ProcSerEntity> findProcedimentsComusActiusByEntitatSenseAccesDirecte(
			@Param("entitat") EntitatEntity entitat,
			@Param("grups") List<String> grups,
			@Param("isTipusNull") boolean isTipusNull,
			@Param("tipus") String tipus
	);

    List<ProcSerEntity> findByOrganGestorId(Long organId);

//	public List<ProcedimentEntity> findByOrganGestorCodiIn(List<String> organsFills);
//
//	@Query(
//			"from ProcedimentEntity pro " +
//			"where (pro.organGestor.codi in (:organsCodis) " +
//			"  	or pro.comu = true) " +
//			"  and pro.entitat in (:entitat) " +
//			"order by pro.nom asc")
//	public List<ProcedimentEntity> findByOrganGestorCodiInOrComu(
//            @Param("organsCodis") List<String> organsCodis,
//            @Param("entitat") EntitatEntity entitat);
//
//	@Query(
//			"from " +
//			"    ProcedimentEntity pro " +
//			"where pro.entitat = (:entitatActual) and " +
//			"lower(pro.nom) = (lower(:nomProcediment))")
//	List<ProcedimentEntity> findByNomAndEntitat(
//            @Param("nomProcediment") String nomProcediment,
//            @Param("entitatActual") EntitatEntity entitat);
	
}
