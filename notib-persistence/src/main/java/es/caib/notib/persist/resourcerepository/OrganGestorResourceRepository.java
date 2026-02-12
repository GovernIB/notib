package es.caib.notib.persist.resourcerepository;

import es.caib.notib.persist.base.repository.BaseRepository;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;

import java.util.List;
import java.util.Optional;

/**
 * Repositori per a la gestió d'entitats de tipus òrgan gestor.
 *
 * @author Límit Tecnologies
 */
public interface OrganGestorResourceRepository extends BaseRepository<OrganGestorResourceEntity, Long> {

	List<OrganGestorResourceEntity> findByEntitat(EntitatResourceEntity entitat);

	Optional<OrganGestorResourceEntity> findByEntitatAndCodi(EntitatResourceEntity entitat, String codi);

}
