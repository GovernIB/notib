/**
 * 
 */
package es.caib.notib.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.entity.NotificacioEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioRepository extends JpaRepository<NotificacioEntity, Long> {

	Page<NotificacioEntity> findByEntitatActiva(
			boolean activa,
			Pageable paginacio);

	Page<NotificacioEntity> findByEntitatId(
			Long entitatId,
			Pageable paginacio);

	List<NotificacioEntity> findByEstatOrderByCreatedDateAsc(
			NotificacioEstatEnumDto estat,
			Pageable pageable);

	@Query(
			"from " +
			"    NotificacioEntity " +
			"where " +
			"    comunicacioTipus = es.caib.notib.core.api.dto.NotificaComunicacioTipusEnumDto.ASINCRON " +
			"and estat = es.caib.notib.core.api.dto.NotificacioEstatEnumDto.PENDENT " +
			"and notificaEnviamentIntent < 3 " +
			"order by " +
			"    notificaEnviamentData ASC")
	List<NotificacioEntity> findByNotificaEstatPendent(Pageable pageable);

	@Query(	"from " +
			"     NotificacioEntity nf " +
			"where " +
			"    (:isEntitatIdNull = true OR nf.entitat.id = :entitatId) " +
			"and lower(nf.concepte) like concat('%', lower(:concepte), '%') " +
			"and nf.enviamentDataProgramada BETWEEN :dataInici AND :dataFi " +
			"and nf.entitat.activa = true " +
			"and ( " +
			"        :isDestinatariNull = true or (" +
			"            select count(env.id) " +
			"            from nf.enviaments env " +
			"            where " +
			"               lower(env.destinatariNom) like concat('%', lower(:destinatari), '%') " +
			"            or lower(concat(env.destinatariLlinatge1, ' ', env.destinatariLlinatge2)) like concat('%', lower(:destinatari), '%') " +
			"            or lower(env.destinatariNif) like concat('%', lower(:destinatari), '%') " +
			"        ) > 0) ")
	public Page<NotificacioEntity> findAmbFiltre(
			@Param("concepte") String concepte,
			@Param("dataInici") Date dataInici,
			@Param("dataFi") Date dataFi,
			@Param("isDestinatariNull") boolean isDestinatariNull,
			@Param("destinatari") String destinatari,
			@Param("isEntitatIdNull") boolean isEntitatIdNull,
			@Param("entitatId") Long entitatId,
			Pageable paginacio);

}
