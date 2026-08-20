package es.caib.notib.persist.resourcerepository;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.persist.base.repository.BaseRepository;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;
import org.springframework.data.jpa.repository.Modifying;
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
	List<String> findCodisByEntitatAndCodiIn(@Param("entitat") EntitatResourceEntity entitat, @Param("codis") Set<String> codis);

	@Query("SELECT og.codi FROM OrganGestorResourceEntity og WHERE og.id IN :ids")
	List<String> findCodisByIdsIn(@Param("ids") Set<Long> ids);

	Optional<OrganGestorResourceEntity> findByCodiAndEntitatAndEstat(String codi, EntitatResourceEntity entitat, OrganGestorEstatEnum estat);

	// Per millorar el rendiment de la següent consulta es recomana crear els següents índexos:
	//   CREATE INDEX orgges_entitat_codi_idx ON not_organ_gestor(entitat, codi);
	//   CREATE INDEX orgges_entitat_pare_idx ON not_organ_gestor(entitat, codi_pare);
	@Query(
		value = "SELECT DISTINCT o.id " +
			"FROM " + BaseConfig.DB_PREFIX + "organ_gestor o " +
			"WHERE o.entitat = :entitatId " +
			"AND o.estat = 'V' " +
			"AND (" +
			"    o.codi IN (:codis) " +
			"    OR " +
			"        o.codi_pare IN (:codis) " +
			"    OR o.codi_pare IN (" +
			"        SELECT o1.codi " +
			"        FROM " + BaseConfig.DB_PREFIX + "organ_gestor o1 " +
			"        WHERE o1.entitat = :entitatId " +
			"        AND o1.estat = 'V' " +
			"        AND o1.codi_pare IN (:codis) " +
			"    )" +
			"    OR o.codi_pare IN (" +
			"        SELECT o2.codi " +
			"        FROM " + BaseConfig.DB_PREFIX + "organ_gestor o2 " +
			"        WHERE o2.entitat = :entitatId " +
			"        AND o2.estat = 'V' " +
			"        AND o2.codi_pare IN (" +
			"            SELECT o1.codi " +
			"            FROM " + BaseConfig.DB_PREFIX + "organ_gestor o1 " +
			"            WHERE o1.entitat = :entitatId " +
			"            AND o1.estat = 'V' " +
			"            AND o1.codi_pare IN (:codis) " +
			"        )" +
			"    )" +
			"    OR o.codi_pare IN (" +
			"        SELECT o3.codi " +
			"        FROM " + BaseConfig.DB_PREFIX + "organ_gestor o3 " +
			"        WHERE o3.entitat = :entitatId " +
			"        AND o3.estat = 'V' " +
			"        AND o3.codi_pare IN (" +
			"            SELECT o2.codi " +
			"            FROM " + BaseConfig.DB_PREFIX + "organ_gestor o2 " +
			"            WHERE o2.entitat = :entitatId " +
			"            AND o2.estat = 'V' " +
			"            AND o2.codi_pare IN (" +
			"                SELECT o1.codi " +
			"                FROM " + BaseConfig.DB_PREFIX + "organ_gestor o1 " +
			"                WHERE o1.entitat = :entitatId " +
			"                AND o1.estat = 'V' " +
			"                AND o1.codi_pare IN (:codis) " +
			"            )" +
			"        )" +
			"    )" +
			")",
		nativeQuery = true)
	List<Long> findIdsByEntitatIdAndCodisRecursiveL4(@Param("entitatId") Long entitatId, @Param("codis") List<String> codis);

	// Retorna la llista dels pares d'una entitat determinada i un id determinat. Si el paràmetre id és null retorna
	// els pares de tots els òrgans gestors de l'entitat.
	// Exemple:
	//    | organ_id | node_id | node_codi | node_nom                                                            | level |
	//    |----------|---------|-----------|---------------------------------------------------------------------|-------|
	//    | 43228    | 43228   | A04005601 | Oficina Balear de la Infància i l'Adolescència                      | 1     |
	//    | 43228    | 43033   | A04026929 | Conselleria de Famílies, Benestar Social i Atenció a la Dependència | 2     |
	//    | 43228    | 42978   | A04003003 | Govern de les Illes Balears                                         | 3     |
	@Query(
		value =
			"SELECT " +
			"    CONNECT_BY_ROOT id AS organ_id, " +
			"    id AS node_id, " +
			"    codi AS node_codi, " +
			"    nom AS node_nom, " +
			"    LEVEL " +
			"FROM not_organ_gestor " +
			"START WITH " +
			"    ( :id IS NULL AND entitat = :entitatId ) " +
			" OR ( :id IS NOT NULL AND id = :id AND entitat = :entitatId ) " +
			"CONNECT BY PRIOR organ_pare = id " +
			"ORDER BY organ_id, level",
		nativeQuery = true)
	List<Object[]> findParesByEntitatIdAndId(
		@Param("entitatId") Long entitatId,
		@Param("id") Long id);

}
