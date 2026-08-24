package es.caib.notib.persist.resourcerepository;

import es.caib.notib.persist.base.repository.BaseRepository;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.domain.Specification;

/**
 * Repositori per a la gestió de notificacions.
 *
 * @author Límit Tecnologies
 */
public interface NotificacioResourceRepository extends BaseRepository<NotificacioResourceEntity, Long> {

	/**
	 * Sobreescrit únicament per adjuntar l'EntityGraph de "taula" (la vista de només lectura de
	 * not_notificacio_table, veure NotificacioResourceEntity.taula): sense això, el @OneToOne EAGER es carrega amb
	 * un SELECT addicional per fila (N+1) en lloc d'un JOIN en la mateixa consulta de la pàgina.
	 * Spring Data ja s'encarrega d'ometre l'EntityGraph a la consulta de COUNT (paginació).
	 */
	@EntityGraph(attributePaths = "taula")
	@Override
	Page<NotificacioResourceEntity> findAll(Specification<NotificacioResourceEntity> spec, Pageable pageable);

}
