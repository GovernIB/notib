package es.caib.notib.persist.resourcerepository;

import es.caib.notib.persist.base.repository.BaseRepository;
import es.caib.notib.persist.resourceentity.MonitorIntegracioResourceEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repositori per a la gestió d'entitats de tipus monitor integracio.
 *
 * @author Límit Tecnologies
 */
public interface MonitorIntegracioResourceRepository extends BaseRepository<MonitorIntegracioResourceEntity, Long>  {

	@Query("SELECT mi.codi, mi.estat, COUNT(mi) FROM MonitorIntegracioResourceEntity mi GROUP BY mi.codi, mi.estat")
	List<Object[]> countByCodiAndEstat();

}
