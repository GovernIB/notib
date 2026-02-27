package es.caib.notib.persist.resourcerepository;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.persist.base.repository.BaseRepository;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repositori per a la gestió d'entitats de tipus òrgan gestor.
 *
 * @author Límit Tecnologies
 */
public interface OrganGestorResourceRepository extends BaseRepository<OrganGestorResourceEntity, Long> {

	List<OrganGestorResourceEntity> findByEntitat(EntitatResourceEntity entitat);

	Optional<OrganGestorResourceEntity> findByEntitatAndCodi(EntitatResourceEntity entitat, String codi);

	@Query("SELECT og.codi FROM OrganGestorResourceEntity og WHERE og.entitat = :entitat AND og.codi IN :codis")
	List<String> findCodisByEntitatAndCodiIn(
		@Param("entitat") EntitatResourceEntity entitat,
		@Param("codis") Set<String> codis);

	@Query("SELECT og.codi FROM OrganGestorResourceEntity og WHERE og.id IN :ids")
	List<String> findCodisByIdsIn(
		@Param("ids") Set<Long> ids);

	// Per millorar el rendiment de la següent consulta es recomana crear els següents índexos:
	//   CREATE INDEX orgges_entitat_codi_idx ON not_organ_gestor(entitat, codi);
	//   CREATE INDEX orgges_entitat_pare_idx ON not_organ_gestor(entitat, codi_pare);
	@Query(
		value = "SELECT DISTINCT o.id " +
			"FROM " + BaseConfig.DB_PREFIX + "organ_gestor o " +
			"WHERE o.entitat = :entitatId " +
			"AND (" +
			"    o.codi IN (:codis)" +
			"    OR " +
			"        o.codi_pare IN (:codis)" +
			"    OR o.codi_pare IN (" +
			"        SELECT o1.codi" +
			"        FROM " + BaseConfig.DB_PREFIX + "organ_gestor o1" +
			"        WHERE o1.entitat = :entitatId" +
			"        AND o1.codi_pare IN (:codis)" +
			"    )" +
			"    OR o.codi_pare IN (" +
			"        SELECT o2.codi" +
			"        FROM " + BaseConfig.DB_PREFIX + "organ_gestor o2" +
			"        WHERE o2.entitat = :entitatId" +
			"        AND o2.codi_pare IN (" +
			"            SELECT o1.codi" +
			"            FROM " + BaseConfig.DB_PREFIX + "organ_gestor o1" +
			"            WHERE o1.entitat = :entitatId" +
			"            AND o1.codi_pare IN (:codis)" +
			"        )" +
			"    )" +
			"    OR o.codi_pare IN (" +
			"        SELECT o3.codi" +
			"        FROM " + BaseConfig.DB_PREFIX + "organ_gestor o3" +
			"        WHERE o3.entitat = :entitatId" +
			"        AND o3.codi_pare IN (" +
			"            SELECT o2.codi" +
			"            FROM " + BaseConfig.DB_PREFIX + "organ_gestor o2" +
			"            WHERE o2.entitat = :entitatId" +
			"            AND o2.codi_pare IN (" +
			"                SELECT o1.codi" +
			"                FROM " + BaseConfig.DB_PREFIX + "organ_gestor o1" +
			"                WHERE o1.entitat = :entitatId" +
			"                AND o1.codi_pare IN (:codis)" +
			"            )" +
			"        )" +
			"    )" +
			")",
		nativeQuery = true)
	List<Long> findIdsByEntitatIdAndCodisRecursiveL4(
		@Param("entitatId") Long entitatId,
		@Param("codis") List<String> codis);

}
