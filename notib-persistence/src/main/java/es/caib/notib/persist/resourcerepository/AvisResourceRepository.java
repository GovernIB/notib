package es.caib.notib.persist.resourcerepository;

import es.caib.notib.persist.base.repository.BaseRepository;
import es.caib.notib.persist.resourceentity.AvisResourceEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Repositori per a la gestió d'avisos.
 *
 * @author Límit Tecnologies
 */
public interface AvisResourceRepository extends BaseRepository<AvisResourceEntity, Long> {


	@Query("from AvisResourceEntity a where a.actiu = true and a.dataInici <= :currentDate and (a.dataFinal is null or a.dataFinal >= :currentDate)")
	List<AvisResourceEntity> findActius(@Param("currentDate") Date currentDate);
}
