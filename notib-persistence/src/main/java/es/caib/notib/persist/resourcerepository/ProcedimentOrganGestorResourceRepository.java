package es.caib.notib.persist.resourcerepository;

import es.caib.notib.persist.base.repository.BaseRepository;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;
import es.caib.notib.persist.resourceentity.ProcedimentOrganGestorResourceEntity;
import es.caib.notib.persist.resourceentity.ProcedimentResourceEntity;

import java.util.Optional;

/**
 * Repositori per a la gestió d'entitats de tipus relació procediment - òrgan gestor.
 *
 * @author Límit Tecnologies
 */
public interface ProcedimentOrganGestorResourceRepository extends BaseRepository<ProcedimentOrganGestorResourceEntity, Long> {

	Optional<ProcedimentOrganGestorResourceEntity> findByProcedimentAndOrganGestor(
		ProcedimentResourceEntity procediment,
		OrganGestorResourceEntity organGestor);

}
