package es.caib.notib.persist.resourcerepository;

import es.caib.notib.persist.base.repository.BaseRepository;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;

/**
 * Repositori per a la gestió de notificacions.
 *
 * Els llistats carreguen la vista not_notificacio_table (NotificacioResourceEntity.taula) amb un
 * INNER JOIN a la mateixa consulta: vegeu NotificacioResourceServiceImpl.additionalSpecification.
 *
 * @author Límit Tecnologies
 */
public interface NotificacioResourceRepository extends BaseRepository<NotificacioResourceEntity, Long> {

}
