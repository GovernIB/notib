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

import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.UsuariEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioRepository extends JpaRepository<NotificacioEntity, Long> {

	@Query(
			"from " +
			"    NotificacioEntity ntf " +
			"where ntf.entitat = :entitatActual " +
			"and (ntf.procedimentCodiNotib in (:procedimentsCodisNotib))")
	Page<NotificacioEntity> findByEntitatActualAndProcedimentCodiNotib(
			@Param("entitatActual") EntitatEntity entitatActiva,
			@Param("procedimentsCodisNotib") List<? extends String> procedimentsCodisNotib,
			Pageable paginacio);

	@Query(
			"from " +
			"    NotificacioEntity ntf " +
			"where ntf.entitat = (:entitatActual)")
	Page<NotificacioEntity> findByEntitatActual(
			@Param("entitatActual") EntitatEntity entitatActiva,
			Pageable paginacio);
	
	@Query(
			"from " +
			"    NotificacioEntity ntf " +
			"where ntf.entitat in (:entitatActiva)")
	Page<NotificacioEntity> findByEntitatActiva(
			@Param("entitatActiva") List<EntitatEntity> entitatActiva,
			Pageable paginacio);
	
	List<NotificacioEntity> findByEntitatId(
			Long entitatId);

	@Query(	"from " +
			"    NotificacioEntity n " +
			"where " + 
			"	 (:isCodiProcedimentNull = true or n.procedimentCodiNotib like lower('%'||:codiProcediment||'%')) " +
			"and (:isGrupNull = true or n.grupCodi like lower('%'||:grup||'%')) " +
			"and (:isConcepteNull = true or n.concepte like lower('%'||:concepte||'%')) " +
			"and (:isDescripcioNull = true or n.descripcio like lower('%'||:descripcio||'%')) " +
			"and (:isDataProgramadaDisposicioIniciNull = true or n.notificaEnviamentData >= :dataProgramadaDisposicioInici) " +
			"and (:isDataProgramadaDisposicioFiNull = true or n.notificaEnviamentData <= :dataProgramadaDisposicioFi) " +
			"and (:isDataCaducitatIniciNull = true or n.caducitat >= :dataCaducitatInici) " +
			"and (:isDataCaducitatFiNull = true or n.caducitat <= :dataCaducitatFi) " +
			"and (:isTipusEnviamentNull = true or n.enviamentTipus like lower('%'||:tipusEnviament||'%')) " +
			"and (:isCsvNull = true or n.csv_uuid like lower('%'||:csv||'%')) " +
			"and (:isEstatNull = true or n.estat like lower('%'||:estat||'%')) " +
			"and (:usuari = n.createdBy) ")
	List<NotificacioEntity> findNotificacioByFiltre(
			@Param("isCodiProcedimentNull") boolean isCodiProcedimentNull,
			@Param("codiProcediment") String codiProcediment,
			@Param("isGrupNull") boolean isGrupNull,
			@Param("grup") String grup,
			@Param("isConcepteNull") boolean isConcepteNull,
			@Param("concepte") String concepte,
			@Param("isDescripcioNull") boolean isDescripcioNull,
			@Param("descripcio") String descripcio,
			@Param("isDataProgramadaDisposicioIniciNull") boolean isDataProgramadaDisposicioIniciNull,
			@Param("dataProgramadaDisposicioInici") Date dataProgramadaDisposicioInici,
			@Param("isDataProgramadaDisposicioFiNull") boolean isDataProgramadaDisposicioFiNull,
			@Param("dataProgramadaDisposicioFi") Date dataProgramadaDisposicioFi,
			@Param("isDataCaducitatIniciNull") boolean isDataCaducitatIniciNull,
			@Param("dataCaducitatInici") Date dataCaducitatInici,
			@Param("isDataCaducitatFiNull") boolean dataCaducitatFiNull,
			@Param("dataCaducitatFi") Date dataCaducitatFi,
			@Param("isTipusEnviamentNull") boolean isTipusEnviamentNull,
			@Param("tipusEnviament") NotificaEnviamentTipusEnumDto tipusEnviament,
			@Param("isCsvNull") boolean isCsvNull,
			@Param("csv") String csv,
			@Param("isEstatNull") boolean isEstatNull,
			@Param("estat") NotificacioEstatEnumDto estat,
			@Param("usuari") UsuariEntity usuari);
	
	List<NotificacioEntity> findByEstatOrderByCreatedDateAsc(
			NotificacioEstatEnumDto estat,
			Pageable pageable);

	@Query(
			"from " +
			"    NotificacioEntity " +
			"where " +
			"    comunicacioTipus = es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto.ASINCRON " +
			"and estat = es.caib.notib.core.api.dto.NotificacioEstatEnumDto.PENDENT " +
			"and notificaEnviamentIntent < :maxReintents " +
			"and notificaEnviamentData is not null " +
			"order by " +
			"    notificaEnviamentData ASC")
	List<NotificacioEntity> findByNotificaEstatPendent(@Param("maxReintents")Integer maxReintents, Pageable pageable);

	@Query(	"from " +
			"     NotificacioEntity ntf " +
			"where " +
			"    (:isEntitatIdNull = true or ntf.entitat.id = :entitatId) " +
			"and (ntf.procedimentCodiNotib in (:procedimentsCodisNotib))" +
			"and (:isComunicacioTipusNull = true or ntf.comunicacioTipus = :comunicacioTipus) " +
			"and (:isEnviamentTipusNull = true or ntf.enviamentTipus = :enviamentTipus) " +
			"and (:isConcepteNull = true or lower(ntf.concepte) like concat('%', lower(:concepte), '%')) " +
			"and (:isEstatNull = true or ntf.estat = :estat) " +
			"and (:isDatesNull = true or ntf.createdDate between :dataInici and :dataFi) " +
			"and (:isTitularNull = true or (" +
			"    select count(env.id) " +
			"    from ntf.enviaments env " +
			"    where " +
			"       lower(concat(env.titularNom, ' ', env.titularLlinatge1, ' ', env.destinatariLlinatge2)) like concat('%', lower(:titular), '%') " +
			"    or lower(env.titularNif) like concat('%', lower(:titular), '%') " +
			"    ) > 0) ")
	public Page<NotificacioEntity> findAmbFiltreAndProcedimentCodiNotib(
			@Param("isEntitatIdNull") boolean isEntitatIdNull,
			@Param("entitatId") Long entitatId,
			@Param("isComunicacioTipusNull") boolean isComunicacioTipusNull,
			@Param("comunicacioTipus") NotificacioComunicacioTipusEnumDto comunicacioTipus,
			@Param("isEnviamentTipusNull") boolean isEnviamentTipusNull,
			@Param("enviamentTipus") NotificaEnviamentTipusEnumDto enviamentTipus,
			@Param("isConcepteNull") boolean isConcepteNull,
			@Param("concepte") String concepte,
			@Param("isEstatNull") boolean isEstatNull,
			@Param("estat") NotificacioEstatEnumDto estat,
			@Param("isDatesNull") boolean isDatesNull,
			@Param("dataInici") Date dataInici,
			@Param("dataFi") Date dataFi,
			@Param("isTitularNull") boolean isTitularNull,
			@Param("titular") String titular,
			@Param("procedimentsCodisNotib") List<? extends String> procedimentsCodisNotib,
			Pageable paginacio);
	
	@Query(	"from " +
			"     NotificacioEntity ntf " +
			"where " +
			"    (:isEntitatIdNull = true or ntf.entitat.id = :entitatId) " +
			"and (:isComunicacioTipusNull = true or ntf.comunicacioTipus = :comunicacioTipus) " +
			"and (:isEnviamentTipusNull = true or ntf.enviamentTipus = :enviamentTipus) " +
			"and (:isConcepteNull = true or lower(ntf.concepte) like concat('%', lower(:concepte), '%')) " +
			"and (:isEstatNull = true or ntf.estat = :estat) " +
			"and (:isDatesNull = true or ntf.createdDate between :dataInici and :dataFi) " +
			"and (:isTitularNull = true or (" +
			"    select count(env.id) " +
			"    from ntf.enviaments env " +
			"    where " +
			"       lower(concat(env.titularNom, ' ', env.titularLlinatge1, ' ', env.destinatariLlinatge2)) like concat('%', lower(:titular), '%') " +
			"    or lower(env.titularNif) like concat('%', lower(:titular), '%') " +
			"    ) > 0) ")
	public Page<NotificacioEntity> findAmbFiltre(
			@Param("isEntitatIdNull") boolean isEntitatIdNull,
			@Param("entitatId") Long entitatId,
			@Param("isComunicacioTipusNull") boolean isComunicacioTipusNull,
			@Param("comunicacioTipus") NotificacioComunicacioTipusEnumDto comunicacioTipus,
			@Param("isEnviamentTipusNull") boolean isEnviamentTipusNull,
			@Param("enviamentTipus") NotificaEnviamentTipusEnumDto enviamentTipus,
			@Param("isConcepteNull") boolean isConcepteNull,
			@Param("concepte") String concepte,
			@Param("isEstatNull") boolean isEstatNull,
			@Param("estat") NotificacioEstatEnumDto estat,
			@Param("isDatesNull") boolean isDatesNull,
			@Param("dataInici") Date dataInici,
			@Param("dataFi") Date dataFi,
			@Param("isTitularNull") boolean isTitularNull,
			@Param("titular") String titular,
			Pageable paginacio);

}
