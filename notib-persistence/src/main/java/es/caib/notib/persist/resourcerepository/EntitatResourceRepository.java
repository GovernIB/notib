package es.caib.notib.persist.resourcerepository;

import es.caib.notib.persist.base.repository.BaseRepository;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;

import java.util.List;

/**
 * Repositori per a la gestió d'entitats.
 *
 * @author Límit Tecnologies
 */
public interface EntitatResourceRepository extends BaseRepository<EntitatResourceEntity, Long> {

	List<EntitatResourceEntity> findByCodi(String codi);

	List<EntitatResourceEntity> findByIdNotLikeAndCodi(Long id, String codi);

	List<EntitatResourceEntity> findByIdNotLikeAndDir3Codi(Long id, String codi);

	List<EntitatResourceEntity> findByDir3Codi(String codiDir3);
}
