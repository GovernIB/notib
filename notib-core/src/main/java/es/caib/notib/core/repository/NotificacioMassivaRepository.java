/**
 * 
 */
package es.caib.notib.core.repository;

import es.caib.notib.core.api.dto.notificacio.NotificacioMassivaEstatDto;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioMassivaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus notificacio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioMassivaRepository extends JpaRepository<NotificacioMassivaEntity, Long> {

	@Query("from NotificacioMassivaEntity nm where nm.id = :id")
	NotificacioMassivaEntity findById(Long id);

	@Query(	"from " +
			"    NotificacioMassivaEntity nm " +
			"where " +
			"		nm.entitat = :entitat " +
			"	and nm.createdBy.codi = :createdByCodi" +
			"	and (:isDataIniciNull = true or nm.createdDate >= :dataInici) " +
			"	and (:isDataFiNull = true or nm.createdDate <= :dataFi) " +
			"	and (:isEstatNull = true or :estatProces = nm.estatProces) " +
			"")
	Page<NotificacioMassivaEntity> findUserRolePage(
			@Param("entitat") EntitatEntity entitat,
			@Param("createdByCodi") String createdByCodi,
			@Param("isDataIniciNull") boolean isDataIniciNull,
			@Param("dataInici") Date dataInici,
			@Param("isDataFiNull") boolean isDataFiNull,
			@Param("dataFi") Date dataFi,
			@Param("isEstatNull") boolean isEstatNull,
			@Param("estatProces") NotificacioMassivaEstatDto estatProces,
			Pageable pageable);

	@Query(	"from " +
			"    NotificacioMassivaEntity nm " +
			"where " +
			"		nm.entitat = :entitat " +
			"	and (:isCreatedByCodiNull = true or nm.createdBy.codi = :createdByCodi)" +
			"	and (:isDataIniciNull = true or nm.createdDate >= :dataInici) " +
			"	and (:isDataFiNull = true or nm.createdDate <= :dataFi) " +
			"	and (:isEstatNull = true or :estatProces = nm.estatProces) " +
			"")
	Page<NotificacioMassivaEntity> findEntitatAdminRolePage(
			@Param("entitat") EntitatEntity entitat,
			@Param("isDataIniciNull") boolean isDataIniciNull,
			@Param("dataInici") Date dataInici,
			@Param("isDataFiNull") boolean isDataFiNull,
			@Param("dataFi") Date dataFi,
			@Param("isEstatNull") boolean isEstatNull,
			@Param("estatProces") NotificacioMassivaEstatDto estatProces,
			@Param("isCreatedByCodiNull") boolean isCreatedByCodiNull,
			@Param("createdByCodi") String createdByCodi,
			Pageable pageable);

	@Query( " select count(distinct n) " +
			" from NotificacioEntity n " +
			" where n.notificacioMassivaEntity = :notificacioMassivaEntity and n.estat <> es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto.PENDENT")
	Integer countNotificacionsNoPendents(@Param("notificacioMassivaEntity") NotificacioMassivaEntity notificacioMassivaEntity);
}
