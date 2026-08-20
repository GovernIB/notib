package es.caib.notib.persist.resourcerepository;

import es.caib.notib.logic.intf.dto.ProcSerTipusEnum;
import es.caib.notib.persist.base.repository.BaseRepository;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;
import es.caib.notib.persist.resourceentity.ProcedimentOrganGestorResourceEntity;
import es.caib.notib.persist.resourceentity.ProcedimentResourceEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repositori per a la gestió d'entitats de tipus relació procediment - òrgan gestor.
 *
 * @author Límit Tecnologies
 */
public interface ProcedimentOrganGestorResourceRepository extends BaseRepository<ProcedimentOrganGestorResourceEntity, Long> {

	Optional<ProcedimentOrganGestorResourceEntity> findByProcedimentAndOrganGestor(ProcedimentResourceEntity procediment, OrganGestorResourceEntity organGestor);

	@Query("SELECT pog.id FROM ProcedimentOrganGestorResourceEntity pog WHERE pog.procediment.id = :procedimentId")
	Set<Long> findProcOrganIdByProcediment(@Param("procedimentId") Long procedimentId);

	@Query("SELECT pog.id " +
		"FROM ProcedimentOrganGestorResourceEntity pog " +
		"WHERE " +
		"    pog.procediment.entitat.id = :entitatId " +
		"AND pog.organGestor.entitat.id = :entitatId " +
		"AND pog.procediment.actiu = true " +
		"AND pog.organGestor.estat = 'VIGENT' " +
		"AND (:tipus IS NULL OR pog.procediment.tipus = :tipus) " +
		"AND (:requireDirectPermission IS NULL OR pog.procediment.requireDirectPermission = :requireDirectPermission) " +
		"AND (:comu IS NULL OR pog.procediment.comu = :comu) " +
		"AND pog.organGestor.id IN (:organGestorIds) " +
//		"AND (:organGestorIds IS NULL OR pog.organGestor.id IN (:organGestorIds)) " +
		"AND pog.id IN (:ids)")
	Set<Long> findIdsComprovacioPermisos(
		@Param("entitatId") Long entitatId,
		@Param("tipus") ProcSerTipusEnum tipus,
		@Param("requireDirectPermission") Boolean requireDirectPermission,
		@Param("comu") Boolean comu,
		@Param("organGestorIds") Set<Long> organGestorIds,
		@Param("ids") Set<Long> ids);

}
