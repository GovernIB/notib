package es.caib.notib.persist.resourcerepository;

import es.caib.notib.persist.base.repository.BaseRepository;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import es.caib.notib.persist.resourceentity.GrupResourceEntity;

import java.util.Optional;

/**
 * Repositori per a la gestió de grups.
 *
 * @author Límit Tecnologies
 */
public interface GrupResourceRepository extends BaseRepository<GrupResourceEntity, Long> {

	Optional<GrupResourceEntity> findByEntitatAndCodi(EntitatResourceEntity entitat, String codi);
}
