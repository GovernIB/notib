/**
 * 
 */
package es.caib.notib.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.notib.core.api.dto.NotificacioSeuEstatEnumDto;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioDestinatariEntity;
import es.caib.notib.core.entity.NotificacioEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioDestinatariRepository extends JpaRepository<NotificacioDestinatariEntity, Long> {

	Page<NotificacioEntity> findByNotificacioId(
			Long notificacioId,
			Pageable pageable);
	
	List<NotificacioEntity> findByNotificacioId(
			Long notificacioId);

	NotificacioDestinatariEntity findByReferencia(
			String referencia);

	NotificacioDestinatariEntity findByNotificacioEntitatAndNotificaIdentificador(
			EntitatEntity entitat,
			String notificaIdentificador);
			
	List<NotificacioDestinatariEntity> findBySeuEstatInOrderBySeuEstatAscSeuDarreraPeticioDataAsc(
			NotificacioSeuEstatEnumDto[] seuEstats,
			Pageable pageable);

//	List<NotificacioDestinatariEntity> findBySeuEstatOrSeuEstatOrSeuEstatOrderBySeuEstatAscSeuDarreraPeticioDataAsc(
//			NotificacioSeuEstatEnumDto seuEstat1,
//			NotificacioSeuEstatEnumDto seuEstat2,
//			NotificacioSeuEstatEnumDto seuEstat3,
//			Pageable pageable);

}
