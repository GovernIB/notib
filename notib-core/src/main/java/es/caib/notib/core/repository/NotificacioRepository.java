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
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.ProcedimentEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus notificacio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioRepository extends JpaRepository<NotificacioEntity, Long> {

	NotificacioEntity findById(Long id);
	
	List<NotificacioEntity> findByTipusUsuari(TipusUsuariEnumDto tipusUsuari);
	
	@Query(
			"from " +
			"    NotificacioEntity ntf " +
			"where (ntf.procedimentCodiNotib in (:procedimentsCodisNotib)) " + 
			"and (ntf.entitat = :entitat) " +
			"and (ntf.grupCodi = null) ")
	Page<NotificacioEntity> findByProcedimentCodiNotibAndEntitat(
			@Param("procedimentsCodisNotib") List<? extends String> procedimentsCodisNotib,
			@Param("entitat") EntitatEntity entitat,
			Pageable paginacio);
	
	@Query(
			"from " +
			"    NotificacioEntity ntf " +
			"where (ntf.procedimentCodiNotib in (:procedimentsCodisNotib)) " + 
			"and ntf.grupCodi = null or (ntf.grupCodi in (:grupsProcedimentCodisNotib)) " +
			"and (ntf.entitat = :entitat) " )
	Page<NotificacioEntity> findByProcedimentCodiNotibAndGrupsCodiNotibAndEntitat(
			@Param("procedimentsCodisNotib") List<? extends String> procedimentsCodisNotib,
			@Param("grupsProcedimentCodisNotib") List<? extends String> grupsProcedimentCodisNotib,
			@Param("entitat") EntitatEntity entitat,
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
			"	 (:isCodiProcedimentNull = true or lower(n.procedimentCodiNotib) like lower('%'||:codiProcediment||'%')) " +
			"and (:isGrupNull = true or lower(n.grupCodi) like lower('%'||:grup||'%')) " +
			"and (:isConcepteNull = true or lower(n.concepte) like lower('%'||:concepte||'%')) " +
			"and (:isDescripcioNull = true or lower(n.descripcio) like lower('%'||:descripcio||'%')) " +
			"and (:isDataProgramadaDisposicioIniciNull = true or n.enviamentDataProgramada >= :dataProgramadaDisposicioInici) " +
			"and (:isDataProgramadaDisposicioFiNull = true or n.enviamentDataProgramada <= :dataProgramadaDisposicioFi) " +
			"and (:isDataCaducitatIniciNull = true or n.caducitat >= :dataCaducitatInici) " +
			"and (:isDataCaducitatFiNull = true or n.caducitat <= :dataCaducitatFi) " +
			"and (:isTipusEnviamentNull = true or lower(n.enviamentTipus) like lower('%'||:tipusEnviament||'%')) " +
			"and (:isCsvNull = true or lower(n.document.csv) like lower('%'||:csv||'%')) " +
			"and (:isEstatNull = true or lower(n.estat) like lower('%'||:estat||'%')) " +
			"and (:entitat = n.entitat) ")
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
			@Param("tipusEnviament") int tipusEnviament,
			@Param("isCsvNull") boolean isCsvNull,
			@Param("csv") String csv,
			@Param("isEstatNull") boolean isEstatNull,
			@Param("estat") int estat,
			@Param("entitat") EntitatEntity entitat);
	
	List<NotificacioEntity> findByEstatOrderByCreatedDateAsc(
			NotificacioEstatEnumDto estat,
			Pageable pageable);

	@Query(
			"from " +
			"    NotificacioEntity " +
			"where " +
			"    estat = es.caib.notib.core.api.dto.NotificacioEstatEnumDto.PENDENT " +
			"and registreEnviamentIntent < :maxReintents " +
			//"and registreData is not null " +
			"order by " +
			"    notificaEnviamentData ASC")
	List<NotificacioEntity> findByNotificaEstatPendent(
			@Param("maxReintents")Integer maxReintents, 
			Pageable pageable);

	@Query(
			"from " +
			"    NotificacioEntity " +
			"where " +
			"    estat = es.caib.notib.core.api.dto.NotificacioEstatEnumDto.REGISTRADA " +
			"and notificaEnviamentIntent < :maxReintents " +
			"and notificaEnviamentData is not null " +
			"order by " +
			"    notificaEnviamentData ASC")
	List<NotificacioEntity> findByNotificaEstatRegistrada(
			@Param("maxReintents")Integer maxReintents, 
			Pageable pageable);

	@Query(	"from " +
			"     NotificacioEntity ntf " +
			"where " +
			"    (:isEntitatIdNull = true or ntf.entitat.id = :entitatId) " +
			"and (ntf.procedimentCodiNotib in (:procedimentsCodisNotib))" +
			"and (:entitat = ntf.entitat) " +
			"and (:isEnviamentTipusNull = true or ntf.enviamentTipus = :enviamentTipus) " +
			"and (:isConcepteNull = true or lower(ntf.concepte) like concat('%', lower(:concepte), '%')) " +
			"and (:isEstatNull = true or ntf.estat = :estat) " +
			"and (:isDataIniciNull = true or ntf.createdDate >= :dataInici) " +
			"and (:isDataFiNull = true or ntf.createdDate <= :dataFi) "+
			"and (:isProcedimentNull = true or ntf.procediment = :procediment) " +
			"and (:isTitularNull = true or (" +
			"    select count(env.id) " +
			"    from ntf.enviaments env " +
			"    where " +
			"       lower(concat(env.titular.nom, ' ', env.titular.llinatge1)) like concat('%', lower(:titular), '%') " +
			"    or lower(env.titular.nif) like concat('%', lower(:titular), '%') " +
			"    ) > 0) " + 
			"and (:isTipusUsuariNull = true or ntf.tipusUsuari = :tipusUsuari) " +
			"and (ntf.grupCodi = null) " + 
			"and (:isNumExpedientNull = true or ntf.numExpedient = :numExpedient)")
	public Page<NotificacioEntity> findAmbFiltreAndProcedimentCodiNotib(
			@Param("isEntitatIdNull") boolean isEntitatIdNull,
			@Param("entitatId") Long entitatId,
			@Param("procedimentsCodisNotib") List<String> procedimentsCodisNotib,
			@Param("isEnviamentTipusNull") boolean isEnviamentTipusNull,
			@Param("enviamentTipus") NotificaEnviamentTipusEnumDto enviamentTipus,
			@Param("isConcepteNull") boolean isConcepteNull,
			@Param("concepte") String concepte,
			@Param("isEstatNull") boolean isEstatNull,
			@Param("estat") NotificacioEstatEnumDto estat,
			@Param("isDataIniciNull") boolean isDataIniciNull,
			@Param("dataInici") Date dataInici,
			@Param("isDataFiNull") boolean isDataFiNull,
			@Param("dataFi") Date dataFi,
			@Param("isTitularNull") boolean isTitularNull,
			@Param("titular") String titular,
			@Param("entitat") EntitatEntity entitat,
			@Param("isProcedimentNull") boolean isProcedimentNull,
			@Param("procediment") ProcedimentEntity procediment,
			@Param("isTipusUsuariNull") boolean isTipusUsuariNull,
			@Param("tipusUsuari") TipusUsuariEnumDto tipusUsuar,
			@Param("isNumExpedientNull") boolean isNumExpedientNull,
			@Param("numExpedient") String numExpedient,
			Pageable paginacio);
	
	@Query(	"from " +
			"     NotificacioEntity ntf " +
			"where " +
			"    (:isEntitatIdNull = true or ntf.entitat.id = :entitatId) " +
			"and (ntf.procedimentCodiNotib in (:procedimentsCodisNotib)) " +
			"and (ntf.grupCodi = null or (ntf.grupCodi in (:grupsProcedimentCodisNotib))) " +
			"and (:entitat = ntf.entitat) " +
			"and (:isEnviamentTipusNull = true or ntf.enviamentTipus = :enviamentTipus) " +
			"and (:isConcepteNull = true or lower(ntf.concepte) like concat('%', lower(:concepte), '%')) " +
			"and (:isEstatNull = true or ntf.estat = :estat) " +
			"and (:isDataIniciNull = true or ntf.createdDate >= :dataInici) " +
			"and (:isDataFiNull = true or ntf.createdDate <= :dataFi) "+
			"and (:isProcedimentNull = true or ntf.procediment = :procediment) " +
			"and (:isTitularNull = true or (" +
			"    select count(env.id) " +
			"    from ntf.enviaments env " +
			"    where " +
			"       lower(concat(env.titular.nom, ' ', env.titular.llinatge1)) like concat('%', lower(:titular), '%') " +
			"    or lower(env.titular.nif) like concat('%', lower(:titular), '%') " +
			"    ) > 0) " + 
			"and (:isTipusUsuariNull = true or ntf.tipusUsuari = :tipusUsuari) " + 
			"and (:isNumExpedientNull = true or ntf.numExpedient = :numExpedient)")
	public Page<NotificacioEntity> findAmbFiltreAndProcedimentCodiNotibAndGrupsCodiNotib(
			@Param("isEntitatIdNull") boolean isEntitatIdNull,
			@Param("entitatId") Long entitatId,
			@Param("procedimentsCodisNotib") List<String> procedimentsCodisNotib,
			@Param("grupsProcedimentCodisNotib") List<String> grupsProcedimentCodisNotib,
			@Param("isEnviamentTipusNull") boolean isEnviamentTipusNull,
			@Param("enviamentTipus") NotificaEnviamentTipusEnumDto enviamentTipus,
			@Param("isConcepteNull") boolean isConcepteNull,
			@Param("concepte") String concepte,
			@Param("isEstatNull") boolean isEstatNull,
			@Param("estat") NotificacioEstatEnumDto estat,
			@Param("isDataIniciNull") boolean isDataIniciNull,
			@Param("dataInici") Date dataInici,
			@Param("isDataFiNull") boolean isDataFiNull,
			@Param("dataFi") Date dataFi,
			@Param("isTitularNull") boolean isTitularNull,
			@Param("titular") String titular,
			@Param("entitat") EntitatEntity entitat,
			@Param("isProcedimentNull") boolean isProcedimentNull,
			@Param("procediment") ProcedimentEntity procediment,
			@Param("isTipusUsuariNull") boolean isTipusUsuariNull,
			@Param("tipusUsuari") TipusUsuariEnumDto tipusUsuar,
			@Param("isNumExpedientNull") boolean isNumExpedientNull,
			@Param("numExpedient") String numExpedient,
			Pageable paginacio);
	
	@Query(	"from " +
			"     NotificacioEntity ntf " +
			"where " +
			"    (:isEntitatIdNull = true or ntf.entitat.id = :entitatId) " +
			"and (:isEnviamentTipusNull = true or ntf.enviamentTipus = :enviamentTipus) " +
			"and (:isConcepteNull = true or lower(ntf.concepte) like concat('%', lower(:concepte), '%')) " +
			"and (:isEstatNull = true or ntf.estat = :estat) " +
			"and (:isDataIniciNull = true or ntf.createdDate >= :dataInici) " +
			"and (:isDataFiNull = true or ntf.createdDate <= :dataFi) "+
			"and (:isProcedimentNull = true or ntf.procediment = :procediment) " +
			"and (:isTitularNull = true or (" +
			"    select count(env.id) " +
			"    from ntf.enviaments env " +
			"    where " +
			"       lower(concat(env.titular.nom, ' ', env.titular.llinatge1)) like concat('%', lower(:titular), '%') " +
			"    or lower(env.titular.nif) like concat('%', lower(:titular), '%') " +
			"    ) > 0) " +
			"and (:isTipusUsuariNull = true or ntf.tipusUsuari = :tipusUsuari) " + 
			"and (:isNumExpedientNull = true or ntf.numExpedient = :numExpedient)")
	public Page<NotificacioEntity> findAmbFiltre(
			@Param("isEntitatIdNull") boolean isEntitatIdNull,
			@Param("entitatId") Long entitatId,
			@Param("isEnviamentTipusNull") boolean isEnviamentTipusNull,
			@Param("enviamentTipus") NotificaEnviamentTipusEnumDto enviamentTipus,
			@Param("isConcepteNull") boolean isConcepteNull,
			@Param("concepte") String concepte,
			@Param("isEstatNull") boolean isEstatNull,
			@Param("estat") NotificacioEstatEnumDto estat,
			@Param("isDataIniciNull") boolean isDataIniciNull,
			@Param("dataInici") Date dataInici,
			@Param("isDataFiNull") boolean isDataFiNull,
			@Param("dataFi") Date dataFi,
			@Param("isTitularNull") boolean isTitularNull,
			@Param("titular") String titular,
			@Param("isProcedimentNull") boolean isProcedimentNull,
			@Param("procediment") ProcedimentEntity procediment,
			@Param("isTipusUsuariNull") boolean isTipusUsuariNull,
			@Param("tipusUsuari") TipusUsuariEnumDto tipusUsuari,
			@Param("isNumExpedientNull") boolean isNumExpedientNull,
			@Param("numExpedient") String numExpedient,
			Pageable paginacio);
	
	@Query(	"select " +
			"    c " +
			"from " +
			"    NotificacioEntity c " +
			"where " +
			"    c.id in (:ids)")
	public Page<NotificacioEntity> findNotificacioMassiuByIdsPaginat(
			@Param("ids") List<Long> ids,
			Pageable pageable);

}
