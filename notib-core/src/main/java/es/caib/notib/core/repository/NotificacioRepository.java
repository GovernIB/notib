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
			"     NotificacioEntity ne " +
			"where " +
			"    (:isEntitatIdNull = true OR ne.entitat.id = :entitatId) " +
			"and lower(ne.concepte) like concat('%', lower(:concepte), '%') " +
			"and ne.enviamentDataProgramada BETWEEN :dataInici AND :dataFi " +
			"and ne.entitat.activa = true " +
			"and ( " +
			"        :isDestinatariNull = true or (" +
			"            select count(d.id) " +
			"            from ne.destinataris d " +
			"            where " +
			"               lower(d.destinatariNom) like concat('%', lower(:destinatari), '%') " +
			"            or lower(d.destinatariLlinatges) like concat('%', lower(:destinatari), '%') " +
			"            or lower(d.destinatariNif) like concat('%', lower(:destinatari), '%') " +
			"        ) > 0) "
		  )
	public Page<NotificacioEntity> findFilteredByEntitatId(
			@Param("concepte") String concepte,
			@Param("dataInici") Date dataInici,
			@Param("dataFi") Date dataFi,
			@Param("isDestinatariNull") boolean isDestinatariNull,
			@Param("destinatari") String destinatari,
			@Param("isEntitatIdNull") boolean isEntitatIdNull,
			@Param("entitatId") Long entitatId,
			Pageable paginacio
			);
	
	@Query(   "FROM "
			+ "     NotificacioEntity ne "
			+ "WHERE "
			+ "		ne.entitat.id = :entitatId "
//			+ "AND "
//			+ "		lower(ne.titolEnviament) like concat('%', lower(:titolEnviament), '%') "
//			+ "AND "
//			+ "		lower(ne.estat) like concat('%', lower(:estat), '%') "
//			+ "AND "
//			+ "		lower(ne.tipusEnviament) like concat('%', lower(:tipusEnviament), '%') "
//			+ "AND "
//			+ "		lower(ne.procediment) like concat('%', lower(:procediment), '%') "
//			+ "AND "
//			+ "		lower(ne.destinatariNom) like concat('%', lower(:destinatariNom), '%') "
//			+ "AND "
//			+ "		ne.data BETWEEN :dataInici AND :dataFi "
		  )
	public Page<NotificacioEntity> findFilteredByEntitat(
		@Param("entitatId") long entitatId,
//		@Param("titolEnviament") String titolEnviament,
//		@Param("estat") String estat,
//		@Param("tipusEnviament") String tipusEnviament,
//		@Param("procediment") String procediment,
//		@Param("destinatariNom") String destinatariNom,
//		@Param("dataInici") Date dataInici,
//		@Param("dataFi") Date dataFi,
		Pageable paginacio);
	@Query(   
			  "FROM "
			+ "     NotificacioEntity ne "
			+ "WHERE "
			+ "		ne.id = ( SELECT d.notificacio.id "
			+ "			  FROM ne.destinataris d "
			+ "			  WHERE "
			+ "				d.referencia = :referencia "
			+ "			) "
		  )
	public NotificacioEntity findByDestinatariReferencia(
			@Param("referencia") String referencia );

}
