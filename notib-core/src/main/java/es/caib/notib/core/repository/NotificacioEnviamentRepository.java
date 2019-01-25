/**
 * 
 */
package es.caib.notib.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioEnviamentRepository extends JpaRepository<NotificacioEnviamentEntity, Long> {

	List<NotificacioEnviamentEntity> findByNotificacioId(
			Long notificacioId);

	NotificacioEnviamentEntity findByNotificaReferencia(
			String notificaReferencia);

	NotificacioEnviamentEntity findByNotificacioAndNotificaReferencia(
			NotificacioEntity notificacio,
			String notificaReferencia);

	NotificacioEnviamentEntity findByNotificacioEntitatAndNotificaIdentificador(
			EntitatEntity entitat,
			String notificaIdentificador);
	

	@Query(	"from" +
			"    NotificacioEnviamentEntity n " +
			"where " +
			"    (:esNullCodiNotifica = true or n.notificaIdentificador = :notificaIdentificador) " +
			"and n.notificacio = :notificacio")
	Page<NotificacioEnviamentEntity> findByNotificacio(
			@Param("esNullCodiNotifica") boolean esNullCodiNotifica,
			@Param("notificaIdentificador") String codiNotifica,
			@Param("notificacio") NotificacioEntity notificacio,
			Pageable pageable);
	
	@Query(	"  from	NotificacioEnviamentEntity " +
			" where	notificaEstatFinal = false " +
			"   and notificaEstat != es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto.NOTIB_PENDENT " +
			" order by notificaEstatDataActualitzacio asc")
	List<NotificacioEnviamentEntity> findByNotificaRefresc(
			Pageable pageable);

}
