package es.caib.notib.persist.resourcerepository;

import es.caib.notib.persist.base.repository.BaseRepository;
import es.caib.notib.persist.entity.AplicacioEntity;
import es.caib.notib.persist.resourceentity.AplicacioResourceEntity;

/**
 * Repositori per a la gestió d'aplicacions.
 *
 * @author Límit Tecnologies
 */
public interface AplicacioResourceRepository extends BaseRepository<AplicacioResourceEntity, Long> {

	AplicacioResourceEntity findByUsuariCodiAndEntitatId(String usuariCodi, Long entitatId);

}
