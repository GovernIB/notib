package es.caib.notib.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.ProcedimentEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus procediment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ProcedimentRepository extends JpaRepository<ProcedimentEntity, Long> {

	@Query(
			"from " + 
			"	ProcedimentEntity pro " +
			"where pro not in (:procediments)")
	public List<ProcedimentEntity> findProcedimentsSenseGrups(
			@Param("procediments") List<ProcedimentEntity> procediments);
	
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
	
	ProcedimentEntity findById(
			Long procedimentId);
	
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
	
}
