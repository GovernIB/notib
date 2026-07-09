package es.caib.notib.persist.resourcerepository;

import es.caib.notib.persist.base.repository.BaseRepository;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.resourceentity.NotificacioEnviamentResourceEntity;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repositori per a la gestió d'enviaments d'una notificació.
 *
 * @author Límit Tecnologies
 */
public interface NotificacioEnviamentResourceRepository extends BaseRepository<NotificacioEnviamentResourceEntity, Long> {

	Optional<NotificacioEnviamentResourceEntity> findByNotificaReferencia(String notificaReferencia);

	@Query(value = "from NotificacioEnviamentResourceEntity where notificaReferencia = :enviamentUuid")
	Optional<NotificacioEnviamentEntity> findByUuid(@Param("enviamentUuid") String enviamentUuid);


	@Query(	" from NotificacioEnviamentResourceEntity e" +
		" where	e.notificacio = :notificacio " +
		"	and (e.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.NOTIB_PENDENT" +
		"   		or e.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.REGISTRADA)" +
		" order by e.notificaEstatDataActualitzacio asc nulls first")
	List<NotificacioEnviamentResourceEntity> findEnviamentsPendentsNotificaByNotificacio(@Param("notificacio") NotificacioResourceEntity notificacio);

}
