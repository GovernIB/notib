package es.caib.notib.persist.repository;

import es.caib.notib.logic.intf.dto.ProcSerTipusEnum;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.ProcSerEntity;
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

	@Query("select count(pro.id) " +
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
			@Param("tipus") ProcSerTipusEnum tipus);

	@Query("select count(pro.id) " +
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
			@Param("tipus") ProcSerTipusEnum tipus);

	@Query(	"select count(pro.id) " +
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
			@Param("tipus") ProcSerTipusEnum tipus);

	@Query(	"select count(pro.id) " +
			" from ProcSerEntity pro " +
			" left outer join pro.organGestor og " +
			"where (og.codi in (:organsCodis) " +
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
			@Param("tipus") ProcSerTipusEnum tipus);

	ProcSerEntity findByCodiAndEntitat(String codi, EntitatEntity entitat);

	@Query(
			"from " +
					"    ProcedimentEntity pro " +
					"where pro.entitat = (:entitatActual) and " +
					"lower(pro.codi) = (lower(:codiProcediment))")
	ProcSerEntity findByEntitatAndCodiProcediment(
			@Param("entitatActual") EntitatEntity entitat,
			@Param("codiProcediment") String codiProcediment);

	@Query(	"select count(p.id) " +
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
			@Param("tipus") ProcSerTipusEnum tipus
	);

	@Query(	"select count(p.id) " +
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
			@Param("tipus") ProcSerTipusEnum tipus
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
