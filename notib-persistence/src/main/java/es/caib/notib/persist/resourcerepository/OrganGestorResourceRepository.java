package es.caib.notib.persist.resourcerepository;

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

}
