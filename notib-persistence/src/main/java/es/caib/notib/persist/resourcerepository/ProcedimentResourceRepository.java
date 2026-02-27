package es.caib.notib.persist.resourcerepository;

import es.caib.notib.logic.intf.dto.ProcSerTipusEnum;
import es.caib.notib.persist.base.repository.BaseRepository;
import es.caib.notib.persist.resourceentity.ProcedimentResourceEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

/**
 * Repositori per a la gestió d'entitats de tipus procediment.
 *
 * @author Límit Tecnologies
 */
public interface ProcedimentResourceRepository extends BaseRepository<ProcedimentResourceEntity, Long> {

	@Query("SELECT p.id " +
		"FROM ProcedimentResourceEntity p " +
		"WHERE " +
		"    p.entitat.id = :entitatId " +
		"AND (:tipus IS NULL OR p.tipus = :tipus) " +
		"AND p.comu = true " +
		"AND p.requireDirectPermission = false")
	List<Long> findIdsByEntitatIdAndTipusAndComuTrueAndPermisDirecteFalse(
		@Param("entitatId") Long entitatId,
		@Param("tipus") ProcSerTipusEnum tipus);

	@Query("SELECT p.id " +
		"FROM ProcedimentResourceEntity p " +
		"WHERE " +
		"    p.entitat.id = :entitatId " +
		"AND (:tipus IS NULL OR p.tipus = :tipus) " +
		"AND p.id IN (:ids) " +
		"AND p.comu = false")
	List<Long> findIdsByEntitatIdAndTipusAndIdInAndComuFalse(
		@Param("entitatId") Long entitatId,
		@Param("tipus") ProcSerTipusEnum tipus,
		@Param("ids") Set<Long> ids);

}
