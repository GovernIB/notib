package es.caib.notib.persist.resourcerepository;

import es.caib.notib.persist.base.repository.BaseRepository;
import es.caib.notib.persist.resourceentity.EventResourceEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repositori per a la gestió d'events.
 *
 * @author Límit Tecnologies
 */
public interface EventResourceRepository extends BaseRepository<EventResourceEntity, Long>  {

	@Query( "SELECT ne " +
		"FROM "
//			+ " NotificacioEventEntity ne LEFT JOIN ne.enviament n "
		+ " NotificacioEventEntity ne "
		+ "WHERE "
		+ " ne.enviament.id = :enviamentId "
		+ " AND ne.tipus = es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto.API_CARPETA "
		+ " ORDER BY ne.id DESC")
	List<EventResourceEntity> findLastApiCarpetaByEnviamentId(@Param("enviamentId") Long enviamentId);

}
