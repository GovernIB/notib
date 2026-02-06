package es.caib.notib.persist.resourcerepository;

import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.persist.base.repository.BaseRepository;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;
import liquibase.pro.packaged.E;

import java.util.List;

/**
 * Repositori per a la gestió d'entitats de tipus òrgan gestor.
 *
 * @author Límit Tecnologies
 */
public interface OrganGestorResourceRepository extends BaseRepository<OrganGestorResourceEntity, Long> {

	List<OrganGestorResourceEntity> findByEntitat(EntitatResourceEntity entitat);
	List<OrganGestorResourceEntity> findByEntitatAndEstat(EntitatResourceEntity entitat, OrganGestorEstatEnum estat);

}
