package es.caib.notib.persist.resourcerepository;

import es.caib.notib.persist.base.repository.BaseRepository;
import es.caib.notib.persist.resourceentity.NotificacioEnviamentResourceEntity;

import java.util.Optional;

/**
 * Repositori per a la gestió d'enviaments d'una notificació.
 *
 * @author Límit Tecnologies
 */
public interface NotificacioEnviamentResourceRepository extends BaseRepository<NotificacioEnviamentResourceEntity, Long> {

	Optional<NotificacioEnviamentResourceEntity> findByNotificaReferencia(String notificaReferencia);

}
