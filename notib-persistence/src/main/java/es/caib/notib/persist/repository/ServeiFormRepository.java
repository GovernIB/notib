package es.caib.notib.persist.repository;

import es.caib.notib.persist.entity.ServeiFormEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ServeiFormRepository extends JpaRepository<ServeiFormEntity, Long> {

	@Query("from ServeiFormEntity pro  where (pro.entitat_id = :entitatId)")
	Page<ServeiFormEntity> findAmbEntitatActual(@Param("entitatId") Long entitatId, Pageable paginacio);
	
	@Query("from ServeiFormEntity pro  where (pro.entitat_id in (:entitatsActivesId))")
	Page<ServeiFormEntity> findAmbEntitatActiva(@Param("entitatsActivesId") List<Long> entitatActiveId, Pageable paginacio);
	
	@Query("from ServeiFormEntity pro where (pro.entitat_id = :entitatId)  and ((pro.organGestor in (:organsGestors)) or pro.comu = true)")
	Page<ServeiFormEntity> findAmbOrganGestorActualOrComu(@Param("entitatId") Long entitatId, @Param("organsGestors") List<String> organsGestors, Pageable paginacio);
	
	@Query(	"from " +
			"    ServeiFormEntity pro " +
			"where (pro.entitat_id = :entitatId)" +
			" and (:isCodiNull = true or lower(pro.codi) like lower('%'||:codi||'%'))" +
			" and (:isNomNull = true or lower(pro.nom) like lower('%'||:nom||'%'))" +
			" and (:isOrganGestorNull = true or pro.organGestor like :organ)" +
			" and (:isEstatNull = true or pro.actiu = :estat)" +
			" and (:isComu = false or pro.comu = true)" +
			" and (:isManual = false or pro.manual = true)" +
			" and (:isEntregaCieActiva = false or pro.entregaCieActiva != 0)")
	Page<ServeiFormEntity> findAmbEntitatAndFiltre(
            @Param("entitatId") Long entitatId,
            @Param("isCodiNull") boolean isCodiNull,
            @Param("codi") String codi,
            @Param("isNomNull") boolean isNomNull,
            @Param("nom") String nom,
            @Param("isOrganGestorNull") boolean isOrganGestorNull,
            @Param("organ") String organGestor,
			@Param("isEstatNull") boolean isEstatNull,
			@Param("estat") Boolean estat,
            @Param("isComu") boolean isComu,
            @Param("isEntregaCieActiva") boolean isEntregaCieActiva,
			@Param("isManual") boolean isManual,
            Pageable paginacio);
	
	@Query(	"from " +
			"    ServeiFormEntity pro " +
			"where ((:isCodiNull = true) or (lower(pro.codi) like lower('%'||:codi||'%')))" + 
			" and ((:isNomNull = true) or (lower(pro.nom) like lower('%'||:nom||'%')))" +
			" and (:isOrganGestorNull = true or pro.organGestor like :organ)" +
			" and (:isEstatNull = true or pro.actiu = :estat)" +
			" and (:isComu = false or pro.comu = true)" +
			" and (:isManual = false or pro.manual = true)" +
			" and (:isEntregaCieActiva = false or pro.entregaCieActiva != 0)")
	Page<ServeiFormEntity> findAmbFiltre(
            @Param("isCodiNull") boolean isCodiNull,
            @Param("codi") String codi,
            @Param("isNomNull") boolean isNomNull,
            @Param("nom") String nom,
            @Param("isOrganGestorNull") boolean isOrganGestorNull,
            @Param("organ") String organGestor,
			@Param("isEstatNull") boolean isEstatNull,
			@Param("estat") Boolean estat,
            @Param("isComu") boolean isComu,
            @Param("isEntregaCieActiva") boolean isEntregaCieActiva,
			@Param("isManual") boolean isManual,
            Pageable paginacio);

	@Query(	"from " +
			"    ServeiFormEntity pro " +
			"where (pro.entitat_id = :entitatId)" +
			" and (:isCodiNull = true or lower(pro.codi) like lower('%'||:codi||'%'))" +
			" and (:isNomNull = true or lower(pro.nom) like lower('%'||:nom||'%'))" +
			" and (:isOrganGestorNull = true or pro.organGestor like :organ)" +
			" and ((pro.organGestor in (:organsGestors)) or pro.comu = true)" +
			" and (:isEstatNull = true or pro.actiu = :estat)" +
			" and (:isComu = false or pro.comu = true)" +
			" and (:isManual = false or pro.manual = true)" +
			" and (:isEntregaCieActiva = false or pro.entregaCieActiva != 0)")
	Page<ServeiFormEntity> findAmbOrganGestorOrComuAndFiltre(
            @Param("entitatId") Long entitatId,
            @Param("isCodiNull") boolean isCodiNull,
            @Param("codi") String codi,
            @Param("isNomNull") boolean isNomNull,
            @Param("nom") String nom,
            @Param("isOrganGestorNull") boolean isOrganGestorNull,
            @Param("organ") String organGestor,
            @Param("organsGestors") List<String> organsGestors,
			@Param("isEstatNull") boolean isEstatNull,
			@Param("estat") Boolean estat,
            @Param("isComu") boolean isComu,
            @Param("isEntregaCieActiva") boolean isEntregaCieActiva,
			@Param("isManual") boolean isManual,
            Pageable paginacio);

}
