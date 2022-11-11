package es.caib.notib.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.notib.core.entity.ProcedimentFormEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus procediment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ProcedimentFormRepository extends JpaRepository<ProcedimentFormEntity, Long> {

	@Query(
			"from " +
			"    ProcedimentFormEntity pro " + 
			" where (pro.entitat_id = :entitatId)")
	Page<ProcedimentFormEntity> findAmbEntitatActual(
			@Param("entitatId") Long entitatId,
			Pageable paginacio);
	
	@Query(
			"from " +
			"    ProcedimentFormEntity pro " + 
			" where (pro.entitat_id in (:entitatsActivesId))")
	Page<ProcedimentFormEntity> findAmbEntitatActiva(
			@Param("entitatsActivesId") List<Long> entitatActiveId,
			Pageable paginacio);
	
	@Query(
			"from " +
			"    ProcedimentFormEntity pro " + 
			"where (pro.entitat_id = :entitatId) " +
			" and ((pro.organGestor in (:organsGestors)) or pro.comu = true)")
	Page<ProcedimentFormEntity> findAmbOrganGestorActualOrComu(
			@Param("entitatId") Long entitatId,
			@Param("organsGestors") List<String> organsGestors,
			Pageable paginacio);
	
	@Query(	"from " +
			"    ProcedimentFormEntity pro " +
			"where (pro.entitat_id = :entitatId)" +
			" and (:isCodiNull = true or lower(pro.codi) like lower('%'||:codi||'%'))" +
			" and (:isNomNull = true or lower(pro.nom) like lower('%'||:nom||'%'))" +
			" and (:isOrganGestorNull = true or pro.organGestor like :organ)" +
			" and (:isActiuNull = true or pro.actiu = :isActiu)" +
			" and (:isComu = false or pro.comu = true)" +
			" and (:isEntregaCieActiva = false or pro.entregaCieActiva != 0)")
	Page<ProcedimentFormEntity> findAmbEntitatAndFiltre(
			@Param("entitatId") Long entitatId,
			@Param("isCodiNull") boolean isCodiNull,
			@Param("codi") String codi,
			@Param("isNomNull") boolean isNomNull,
			@Param("nom") String nom,
			@Param("isOrganGestorNull") boolean isOrganGestorNull,
			@Param("organ") String organGestor,
			@Param("isComu") boolean isComu,
			@Param("isEntregaCieActiva") boolean isEntregaCieActiva,
			@Param("isActiuNull") boolean isActiuNull,
			@Param("isActiu") Boolean isActiu,
			Pageable paginacio);
	
	@Query(	"from " +
			"    ProcedimentFormEntity pro " +
			"where ((:isCodiNull = true) or (lower(pro.codi) like lower('%'||:codi||'%')))" + 
			" and ((:isNomNull = true) or (lower(pro.nom) like lower('%'||:nom||'%')))" +
			" and (:isOrganGestorNull = true or pro.organGestor like :organ)" +
			" and (:isActiuNull = true or pro.actiu = :isActiu)" +
			" and (:isComu = false or pro.comu = true)" +
			" and (:isEntregaCieActiva = false or pro.entregaCieActiva != 0)")
	Page<ProcedimentFormEntity> findAmbFiltre(
			@Param("isCodiNull") boolean isCodiNull,
			@Param("codi") String codi,
			@Param("isNomNull") boolean isNomNull,
			@Param("nom") String nom,
			@Param("isOrganGestorNull") boolean isOrganGestorNull,
			@Param("organ") String organGestor,
			@Param("isComu") boolean isComu,
			@Param("isEntregaCieActiva") boolean isEntregaCieActiva,
			@Param("isActiuNull") boolean isActiuNull,
			@Param("isActiu") Boolean isActiu,
			Pageable paginacio);

	@Query(	"from " +
			"    ProcedimentFormEntity pro " +
			"where (pro.entitat_id = :entitatId)" +
			" and (:isCodiNull = true or lower(pro.codi) like lower('%'||:codi||'%'))" +
			" and (:isNomNull = true or lower(pro.nom) like lower('%'||:nom||'%'))" +
			" and (:isOrganGestorNull = true or pro.organGestor like :organ)" +
			" and ((pro.organGestor in (:organsGestors)) or pro.comu = true)" +
			" and (:isActiuNull = true or pro.actiu = :isActiu)" +
			" and (:isComu = false or pro.comu = true)" +
			" and (:isEntregaCieActiva = false or pro.entregaCieActiva != 0)")
	Page<ProcedimentFormEntity> findAmbOrganGestorOrComuAndFiltre(
			@Param("entitatId") Long entitatId,
			@Param("isCodiNull") boolean isCodiNull,
			@Param("codi") String codi,
			@Param("isNomNull") boolean isNomNull,
			@Param("nom") String nom,
			@Param("isOrganGestorNull") boolean isOrganGestorNull,
			@Param("organ") String organGestor,
			@Param("organsGestors") List<String> organsGestors,
			@Param("isComu") boolean isComu,
			@Param("isEntregaCieActiva") boolean isEntregaCieActiva,
			@Param("isActiuNull") boolean isActiuNull,
			@Param("isActiu") Boolean isActiu,
			Pageable paginacio);

}
