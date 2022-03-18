/**
 * 
 */
package es.caib.notib.core.repository;

import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioMassivaEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus notificacio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioRepository extends JpaRepository<NotificacioEntity, Long> {

	@Query("select id from NotificacioEntity where referencia is null")
	List<Long> findIdsSenseReferencia();

	NotificacioEntity findById(Long id);
	
	List<NotificacioEntity> findByTipusUsuari(TipusUsuariEnumDto tipusUsuari);
	
	List<NotificacioEntity> findByProcedimentId(Long id);

	List<NotificacioEntity> findByEntitatId(Long entitatId);

	List<NotificacioEntity> findByNotificacioMassivaEntityId(Long NotificacioMassivaEntityId);

	@Modifying
	@Query("update NotificacioEntity nt set nt.referencia = :referencia where nt.id = :id")
	void updateReferencia(@Param("id") Long id, @Param("referencia") String referencia);

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
			"    estat = es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto.PENDENT " +
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
			"    estat in (es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto.REGISTRADA, " +
					"es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS) " +
			"and notificaEnviamentIntent < :maxReintents " +
			"and notificaEnviamentData is not null " +
			"order by " +
			"    notificaEnviamentData ASC")
	List<NotificacioEntity> findByNotificaEstatRegistradaAmbReintentsDisponibles(
			@Param("maxReintents")Integer maxReintents, 
			Pageable pageable);

	@Query(
			"from " +
			"    NotificacioEntity " +
			"where " +
			"estat in (es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto.PENDENT," +
			"es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto.REGISTRADA) " +
			"and procediment = :procediment " + 
			"order by " +
			"    notificaEnviamentData ASC")
	List<NotificacioEntity> findNotificacionsPendentsDeNotificarByProcediment(
			@Param("procediment") ProcedimentEntity procediment);

	@Query(
			"from " +
					"    NotificacioEntity " +
					"where " +
					"estat in (es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto.PENDENT," +
					"es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto.REGISTRADA) " +
					"and procediment.id = :procedimentId " +
					"order by " +
					"    notificaEnviamentData ASC")
	List<NotificacioEntity> findNotificacionsPendentsDeNotificarByProcedimentId(
			@Param("procedimentId") Long procediment);

	@Query("  from NotificacioEntity n " +
			" where n.tipusUsuari = es.caib.notib.core.api.dto.TipusUsuariEnumDto.APLICACIO " +
			"   and n.errorLastCallback = true")
	Page<NotificacioEntity> findNotificacioLastEventAmbError(Pageable pageable);
	
	@Query(    "  from NotificacioEntity n " +
		       " where n.tipusUsuari = es.caib.notib.core.api.dto.TipusUsuariEnumDto.APLICACIO " +
			   "   and n.errorLastCallback = true " +
		       " order by n.id")
	List<NotificacioEntity> findNotificacioLastEventAmbError();

	@Query(    "  from NotificacioEntity n " +
		       " where n.tipusUsuari = es.caib.notib.core.api.dto.TipusUsuariEnumDto.APLICACIO " +
			   "   and n.errorLastCallback = true " +
			   "   and (:isProcedimentNull = true or n.procediment = :procediment) " +
		       "   and (:isDataIniciNull = true or n.createdDate >= :dataInici) " +
		       "   and (:isDataFiNull = true or n.createdDate <= :dataFi) " +
		       "   and (:isConcepteNull = true or lower(n.concepte) like concat('%', lower(:concepte), '%')) " +
		       "   and (:isEstatNull = true or n.estat = :estat or (" +
			   "    select count(env.id) " +
			   "    from n.enviaments env " +
			   "    where env.notificaEstat = :notificaEstat" +
			   "    ) > 0 ) " +
			   "   and (:isUsuariNull = true or n.createdBy.codi = :usuariCodi)")
	Page<NotificacioEntity> findNotificacioLastEventAmbErrorAmbFiltre(
			@Param("isProcedimentNull") boolean isProcedimentNull,
			@Param("procediment") ProcedimentEntity procediment, 
			@Param("isDataIniciNull") boolean isDataIniciNull, 
			@Param("dataInici") Date dataInici, 
			@Param("isDataFiNull") boolean isDataFiNull, 
			@Param("dataFi") Date dataFi, 
			@Param("isConcepteNull") boolean isConcepteNull, 
			@Param("concepte") String concepte,
			@Param("isEstatNull") boolean isEstatNull, 
			@Param("estat") NotificacioEstatEnumDto estat, 
			@Param("notificaEstat") NotificacioEnviamentEstatEnumDto notificaEstat,
			@Param("isUsuariNull") boolean isUsuariNull, 
			@Param("usuariCodi") String usuariCodi, 
			Pageable springDataPageable);

	@Query(	"select " +
			"    c.estat " +
			"from " +
			"    NotificacioEntity c " +
			"where " +
			"    c.id = :id")
	NotificacioEstatEnumDto getEstatNotificacio(@Param("id") Long id);
	
	@Query(
			"from " +
			"    NotificacioEntity n " +
			" where " +
			"    n.entitat.id = :entitatId " +
			"   and n.estat = es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto.PENDENT " +
			"   and n.registreEnviamentIntent >= :maxReintents ")
	Page<NotificacioEntity> findByNotificaEstatPendentSenseReintentsDisponibles(
			@Param("entitatId")Long entitatId, 
			@Param("maxReintents")Integer maxReintents, 
			Pageable pageable);
	
	@Query(    "  from NotificacioEntity n " +
		       " where " +
		       "    n.entitat.id = :entitatId " +
		       "   and n.estat = es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto.PENDENT " +
		       "   and n.registreEnviamentIntent >= :maxReintents " +
			   "   and (:isProcedimentNull = true or n.procediment = :procediment) " +
		       "   and (:isDataIniciNull = true or n.createdDate >= :dataInici) " +
		       "   and (:isDataFiNull = true or n.createdDate <= :dataFi) " +
		       "   and (:isConcepteNull = true or lower(n.concepte) like concat('%', lower(:concepte), '%')) " +
			   "   and (:isUsuariNull = true or n.createdBy.codi = :usuariCodi)")
	Page<NotificacioEntity> findByNotificaEstatPendentSenseReintentsDisponiblesAmbFiltre(
			@Param("entitatId")Long entitatId, 
			@Param("isProcedimentNull") boolean isProcedimentNull,
			@Param("procediment") ProcedimentEntity procediment, 
			@Param("isDataIniciNull") boolean isDataIniciNull, 
			@Param("dataInici") Date dataInici, 
			@Param("isDataFiNull") boolean isDataFiNull, 
			@Param("dataFi") Date dataFi, 
			@Param("isConcepteNull") boolean isConcepteNull, 
			@Param("concepte") String concepte,
			@Param("isUsuariNull") boolean isUsuariNull, 
			@Param("usuariCodi") String usuariCodi, 
			@Param("maxReintents")Integer maxReintents, 
			Pageable springDataPageable);
	
	@Query( " select n.id " +
			"from " +
			"    NotificacioEntity n " +
			" where " +
			"    n.entitat.id = :entitatId " +
			"   and n.estat = es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto.PENDENT " +
			"   and n.registreEnviamentIntent >= :maxReintents ")
	List<Long> findIdsByNotificaEstatPendentSenseReintentsDisponibles(
			@Param("entitatId")Long entitatId, 
			@Param("maxReintents")Integer maxReintents);
	
	@Query( " select n.id " +   
			"  from NotificacioEntity n " +
		    " where " +
		    "    n.entitat.id = :entitatId " +
		    "   and n.estat = es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto.PENDENT " +
		    "   and n.registreEnviamentIntent >= :maxReintents " +
			"   and (:isProcedimentNull = true or n.procediment = :procediment) " +
		    "   and (:isDataIniciNull = true or n.createdDate >= :dataInici) " +
		    "   and (:isDataFiNull = true or n.createdDate <= :dataFi) " +
		    "   and (:isConcepteNull = true or lower(n.concepte) like concat('%', lower(:concepte), '%')) " +
			"   and (:isUsuariNull = true or n.createdBy.codi = :usuariCodi)")
	List<Long> findIdsByNotificaEstatPendentSenseReintentsDisponiblesAmbFiltre(
			@Param("entitatId")Long entitatId, 
			@Param("isProcedimentNull") boolean isProcedimentNull,
			@Param("procediment") ProcedimentEntity procediment, 
			@Param("isDataIniciNull") boolean isDataIniciNull, 
			@Param("dataInici") Date dataInici, 
			@Param("isDataFiNull") boolean isDataFiNull, 
			@Param("dataFi") Date dataFi, 
			@Param("isConcepteNull") boolean isConcepteNull, 
			@Param("concepte") String concepte,
			@Param("isUsuariNull") boolean isUsuariNull, 
			@Param("usuariCodi") String usuariCodi, 
			@Param("maxReintents")Integer maxReintents);

}
