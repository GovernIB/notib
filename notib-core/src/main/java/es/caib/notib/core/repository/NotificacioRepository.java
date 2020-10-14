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
import es.caib.notib.core.entity.OrganGestorEntity;
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
	
	List<NotificacioEntity> findByProcedimentId(Long id);
	
	@Query( "select ntf " +
			"from " +
			"    NotificacioEntity ntf " +
			"     left outer join ntf.procediment pro " +
			"where " +
			"   ((:isProcNull = false and ntf.procedimentCodiNotib is not null and ntf.procedimentCodiNotib in (:procedimentsCodisNotib))" +
			"   or (ntf.procedimentCodiNotib is null and ntf.usuariCodi = :usuariCodi)) " + 
			"and (ntf.grupCodi = null or (ntf.grupCodi in (:grupsProcedimentCodisNotib))) " +
			"and (ntf.entitat = :entitat) " )
	Page<NotificacioEntity> findByProcedimentCodiNotibAndGrupsCodiNotibAndEntitat(
			@Param("isProcNull") boolean isProcNull,
			@Param("procedimentsCodisNotib") List<? extends String> procedimentsCodisNotib,
			@Param("grupsProcedimentCodisNotib") List<? extends String> grupsProcedimentCodisNotib,
			@Param("entitat") EntitatEntity entitat,
			@Param("usuariCodi") String usuariCodi,
			Pageable paginacio);
	
	@Query( "select ntf " +
			"from " +
			"    NotificacioEntity ntf " +
			"     left outer join ntf.procediment pro " +
			"where " +
			"   ((:isProcNull = false and ntf.procedimentCodiNotib is not null and ntf.procedimentCodiNotib in (:procedimentsCodisNotib))" +
			"   or (ntf.procedimentCodiNotib is null and ntf.organGestor is not null and ntf.organGestor in (:organs))) " + 
			"and (ntf.entitat = :entitat) ")
//			"and (ntf.grupCodi = null) ")
	Page<NotificacioEntity> findByProcedimentCodiNotibAndEntitat(
			@Param("isProcNull") boolean isProcNull,
			@Param("procedimentsCodisNotib") List<? extends String> procedimentsCodisNotib,
			@Param("entitat") EntitatEntity entitat,
			@Param("organs") List<String> organs,
			Pageable paginacio);

	@Query( "select ntf " +
			"from " +
			"    NotificacioEntity ntf " +
			"     left outer join ntf.procediment pro " +
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
	List<NotificacioEntity> findByNotificaEstatRegistradaAmbReintentsDisponibles(
			@Param("maxReintents")Integer maxReintents, 
			Pageable pageable);
	
	@Query(
			"from " +
			"    NotificacioEntity " +
			"where " +
			"estat in (es.caib.notib.core.api.dto.NotificacioEstatEnumDto.PENDENT," +
			"es.caib.notib.core.api.dto.NotificacioEstatEnumDto.REGISTRADA) " +
			"and procediment = :procediment " + 
			"order by " +
			"    notificaEnviamentData ASC")
	List<NotificacioEntity> findNotificacionsPendentsDeNotificarByProcediment(
			@Param("procediment") ProcedimentEntity procediment);

	// TODO: Provar: Afegir notificacions sense procediment realitzades a un organ disponible per l'administrador d'òrgan actual
	@Query(	"select ntf " +
			"from " +
			"     NotificacioEntity ntf " +
			"     left outer join ntf.procediment pro " +
			"where " +
			"    (:isEntitatIdNull = true or ntf.entitat.id = :entitatId) " +
			"and ((:isProcNull = false and ntf.procedimentCodiNotib is not null and ntf.procedimentCodiNotib in (:procedimentsCodisNotib))" +
			"   or (ntf.procedimentCodiNotib is null and ntf.organGestor is not null and ntf.organGestor in (:organs))) " + 
			"and (:entitat = ntf.entitat) " +
			"and (:isEnviamentTipusNull = true or ntf.enviamentTipus = :enviamentTipus) " +
			"and (:isConcepteNull = true or lower(ntf.concepte) like concat('%', lower(:concepte), '%')) " +
			"and (:isEstatNull = true or ntf.estat = :estat) " +
			"and (:isDataIniciNull = true or ntf.createdDate >= :dataInici) " +
			"and (:isDataFiNull = true or ntf.createdDate <= :dataFi) "+
			"and (:isOrganGestorNull = true or ntf.organGestor = :organGestor) " +
			"and (:isProcedimentNull = true or ntf.procediment = :procediment) " +
			"and (:isTitularNull = true or (" +
			"    select count(env.id) " +
			"    from ntf.enviaments env " +
			"    where " +
			"       lower(concat(env.titular.nom, ' ', env.titular.llinatge1)) like concat('%', lower(:titular), '%') " +
			"    or lower(env.titular.nif) like concat('%', lower(:titular), '%') " +
			"    ) > 0) " +
			"and (:isTipusUsuariNull = true or ntf.tipusUsuari = :tipusUsuari) " + 
			"and (:isNumExpedientNull = true or ntf.numExpedient = :numExpedient)" +
			"and (:isCreadaPerNull = true or ntf.createdBy.codi = :creadaPer) " +
			"and (:isIdentificadorNull = true or " +
			"		(ntf.id = (select notificacio.id"
			+ "				from NotificacioEnviamentEntity env"
			+ "				where env.notificaIdentificador = :identificador)))")
	public Page<NotificacioEntity> findAmbFiltreAndProcedimentCodiNotib(
			@Param("isEntitatIdNull") boolean isEntitatIdNull,
			@Param("entitatId") Long entitatId,
			@Param("isProcNull") boolean isProcNull,
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
			@Param("isOrganGestorNull") boolean isOrganGestorNull,
			@Param("organGestor") OrganGestorEntity organGestor,
			@Param("isProcedimentNull") boolean isProcedimentNull,
			@Param("procediment") ProcedimentEntity procediment,
			@Param("isTipusUsuariNull") boolean isTipusUsuariNull,
			@Param("tipusUsuari") TipusUsuariEnumDto tipusUsuar,
			@Param("isNumExpedientNull") boolean isNumExpedientNull,
			@Param("numExpedient") String numExpedient,
			@Param("isCreadaPerNull") boolean isCreadaPerNull,
			@Param("creadaPer") String creadaPer,
			@Param("isIdentificadorNull") boolean isIdentificadorNull,
			@Param("identificador") String identificador,
			@Param("organs") List<String> organs,
			Pageable paginacio);
	
	// TODO: Afegir notificacions sense procediment realitzades per l'usuari actual
	@Query(	"select ntf " +
			"from " +
			"     NotificacioEntity ntf " +
			"     left outer join ntf.procediment pro " +
			"where " +
			"    (:isEntitatIdNull = true or ntf.entitat.id = :entitatId) " +
			"and ((:isProcNull = false and ntf.procedimentCodiNotib is not null and ntf.procedimentCodiNotib in (:procedimentsCodisNotib))" +
			"   or (ntf.procedimentCodiNotib is null and ntf.usuariCodi = :usuariCodi)) " + 
			"and (ntf.grupCodi = null or (ntf.grupCodi in (:grupsProcedimentCodisNotib))) " +
			"and (:entitat = ntf.entitat) " +
			"and (:isEnviamentTipusNull = true or ntf.enviamentTipus = :enviamentTipus) " +
			"and (:isConcepteNull = true or lower(ntf.concepte) like concat('%', lower(:concepte), '%')) " +
			"and (:isEstatNull = true or ntf.estat = :estat) " +
			"and (:isDataIniciNull = true or ntf.createdDate >= :dataInici) " +
			"and (:isDataFiNull = true or ntf.createdDate <= :dataFi) "+
			"and (:isOrganGestorNull = true or ntf.organGestor = :organGestor) " +
			"and (:isProcedimentNull = true or ntf.procediment = :procediment) " +
			"and (:isTitularNull = true or (" +
			"    select count(env.id) " +
			"    from ntf.enviaments env " +
			"    where " +
			"       lower(concat(env.titular.nom, ' ', env.titular.llinatge1)) like concat('%', lower(:titular), '%') " +
			"    or lower(env.titular.nif) like concat('%', lower(:titular), '%') " +
			"    ) > 0) " + 
			"and (:isTipusUsuariNull = true or ntf.tipusUsuari = :tipusUsuari) " + 
			"and (:isNumExpedientNull = true or ntf.numExpedient = :numExpedient)" +
			"and (:isCreadaPerNull = true or ntf.createdBy.codi = :creadaPer) " +
			"and (:isIdentificadorNull = true or " +
			"		(ntf.id = (select notificacio.id" +
			"				from NotificacioEnviamentEntity env" +
			"				where env.notificaIdentificador = :identificador))) " + 
			"and (:nomesAmbErrors = false or " + 
			"		(ntf.id in (select notificacio.id" +
			"				from NotificacioEnviamentEntity env" + 
			"				where env.notificaError = true)))")
	public Page<NotificacioEntity> findAmbFiltreAndProcedimentCodiNotibAndGrupsCodiNotib(
			@Param("isEntitatIdNull") boolean isEntitatIdNull,
			@Param("entitatId") Long entitatId,
			@Param("isProcNull") boolean isProcNull,
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
			@Param("isOrganGestorNull") boolean isOrganGestorNull,
			@Param("organGestor") OrganGestorEntity organGestor,
			@Param("isProcedimentNull") boolean isProcedimentNull,
			@Param("procediment") ProcedimentEntity procediment,
			@Param("isTipusUsuariNull") boolean isTipusUsuariNull,
			@Param("tipusUsuari") TipusUsuariEnumDto tipusUsuar,
			@Param("isNumExpedientNull") boolean isNumExpedientNull,
			@Param("numExpedient") String numExpedient,
			@Param("isCreadaPerNull") boolean isCreadaPerNull,
			@Param("creadaPer") String creadaPer,
			@Param("isIdentificadorNull") boolean isIdentificadorNull,
			@Param("identificador") String identificador,
			@Param("usuariCodi") String usuariCodi,
			@Param("nomesAmbErrors") boolean nomesAmbErrors,
			Pageable paginacio);
	
	@Query(	"select ntf " +
			"from " +
			"     NotificacioEntity ntf " +
			"     left outer join ntf.procediment pro " +
			"where " +
			"    (:isEntitatIdNull = true or ntf.entitat.id = :entitatId) " +
			"and (:isEnviamentTipusNull = true or ntf.enviamentTipus = :enviamentTipus) " +
			"and (:isConcepteNull = true or lower(ntf.concepte) like concat('%', lower(:concepte), '%')) " +
			"and (:isEstatNull = true or ntf.estat = :estat) " +
			"and (:isDataIniciNull = true or ntf.createdDate >= :dataInici) " +
			"and (:isDataFiNull = true or ntf.createdDate <= :dataFi) "+
			"and (:isOrganGestorNull = true or ntf.organGestor = :organGestor) " +
			"and (:isProcedimentNull = true or ntf.procediment = :procediment) " +
			"and (:isTitularNull = true or (" +
			"    select count(env.id) " +
			"    from ntf.enviaments env " +
			"    where " +
			"       lower(concat(env.titular.nom, ' ', env.titular.llinatge1)) like concat('%', lower(:titular), '%') " +
			"    or lower(env.titular.nif) like concat('%', lower(:titular), '%') " +
			"    ) > 0) " +
			"and (:isTipusUsuariNull = true or ntf.tipusUsuari = :tipusUsuari) " + 
			"and (:isNumExpedientNull = true or ntf.numExpedient = :numExpedient)" +
			"and (:isCreadaPerNull = true or ntf.createdBy.codi = :creadaPer) " +
			"and (:isIdentificadorNull = true or " +
			"		(ntf.id = (select notificacio.id" + 
			"				from NotificacioEnviamentEntity env" +
			"				where env.notificaIdentificador = :identificador)))" +
			"and (:nomesAmbErrors = false or " + 
			"		(ntf.id in (select notificacio.id" +
			"				from NotificacioEnviamentEntity env" + 
			"				where env.notificaError = true)))")
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
			@Param("isOrganGestorNull") boolean isOrganGestorNull,
			@Param("organGestor") OrganGestorEntity organGestor,
			@Param("isProcedimentNull") boolean isProcedimentNull,
			@Param("procediment") ProcedimentEntity procediment,
			@Param("isTipusUsuariNull") boolean isTipusUsuariNull,
			@Param("tipusUsuari") TipusUsuariEnumDto tipusUsuari,
			@Param("isNumExpedientNull") boolean isNumExpedientNull,
			@Param("numExpedient") String numExpedient,
			@Param("isCreadaPerNull") boolean isCreadaPerNull,
			@Param("creadaPer") String creadaPer,
			@Param("isIdentificadorNull") boolean isIdentificadorNull,
			@Param("identificador") String identificador,
			@Param("nomesAmbErrors") boolean nomesAmbErrors,
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

//	@Query("select n " + 
//			   "  from NotificacioEventEntity ne " +
//			   " left outer join ne.notificacio n " +
//		       " where n.tipusUsuari = es.caib.notib.core.api.dto.TipusUsuariEnumDto.APLICACIO " +
//			   "   and ne.error = true " +
//		       "   and ne.tipus in (" +
//		       "		es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.CALLBACK_CLIENT," +
//		       "		es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT," +
//		       "		es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO," +
//		       "		es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE," +
//		       "		es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT) " +
//		       " and ne.id in (select max(ne1.id) from NotificacioEventEntity ne1 " +
//		       " 				left outer join ne1.notificacio n1 " +
//		       "				group by n1.id)")
	@Query(    "  from NotificacioEntity n " +
		       " where n.tipusUsuari = es.caib.notib.core.api.dto.TipusUsuariEnumDto.APLICACIO " +
			   "   and n.errorLastCallback = true")
	Page<NotificacioEntity> findNotificacioLastEventAmbError(Pageable pageable);
	
//	@Query("select n " + 
//			   "  from NotificacioEventEntity ne " +
//			   " left outer join ne.notificacio n " +
//		       " where n.tipusUsuari = es.caib.notib.core.api.dto.TipusUsuariEnumDto.APLICACIO " +
//			   "   and ne.error = true " +
//		       "   and ne.tipus in (" +
//		       "		es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.CALLBACK_CLIENT," +
//		       "		es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT," +
//		       "		es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO," +
//		       "		es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE," +
//		       "		es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT) " +
//		       " and ne.id in (select max(ne1.id) from NotificacioEventEntity ne1 " +
//		       " 				left outer join ne1.notificacio n1 " +
//		       "				group by n1.id)" +
//		       " order by n.id")
	@Query(    "  from NotificacioEntity n " +
		       " where n.tipusUsuari = es.caib.notib.core.api.dto.TipusUsuariEnumDto.APLICACIO " +
			   "   and n.errorLastCallback = true " +
		       " order by n.id")
	List<NotificacioEntity> findNotificacioLastEventAmbError();

//	@Query("select n " + 
//			   "  from NotificacioEventEntity ne " +
//			   " left outer	join ne.notificacio n " +
//		       " where n.tipusUsuari = es.caib.notib.core.api.dto.TipusUsuariEnumDto.APLICACIO " +
//		       " and (:isProcedimentNull = true or n.procediment = :procediment) " +
//		       " and (:isDataIniciNull = true or n.createdDate >= :dataInici) " +
//		       " and (:isDataFiNull = true or n.createdDate <= :dataFi) " +
//		       " and (:isConcepteNull = true or lower(n.concepte) like concat('%', lower(:concepte), '%')) " +
//		       " and (:isEstatNull = true or n.estat = :estat) " +
//			   " and (:isUsuariNull = true or n.createdBy.codi = :usuariCodi) " +
//			   "   and ne.error = true " +
//		       "   and ne.tipus in (" +
//		       "		es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.CALLBACK_CLIENT," +
//		       "		es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT," +
//		       "		es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO," +
//		       "		es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE," +
//		       "		es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT) " +
//		       " and ne.id in (select max(ne1.id) from NotificacioEventEntity ne1 " +
//		       " 				left outer join ne1.notificacio n1 " +
//		       "				group by n1.id) ")
	@Query(    "  from NotificacioEntity n " +
		       " where n.tipusUsuari = es.caib.notib.core.api.dto.TipusUsuariEnumDto.APLICACIO " +
			   "   and n.errorLastCallback = true " +
			   "   and (:isProcedimentNull = true or n.procediment = :procediment) " +
		       "   and (:isDataIniciNull = true or n.createdDate >= :dataInici) " +
		       "   and (:isDataFiNull = true or n.createdDate <= :dataFi) " +
		       "   and (:isConcepteNull = true or lower(n.concepte) like concat('%', lower(:concepte), '%')) " +
		       "   and (:isEstatNull = true or n.estat = :estat) " +
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
			"   and n.estat = es.caib.notib.core.api.dto.NotificacioEstatEnumDto.PENDENT " +
			"   and n.registreEnviamentIntent >= :maxReintents ")
	Page<NotificacioEntity> findByNotificaEstatPendentSenseReintentsDisponibles(
			@Param("entitatId")Long entitatId, 
			@Param("maxReintents")Integer maxReintents, 
			Pageable pageable);
	
	@Query(    "  from NotificacioEntity n " +
		       " where " +
		       "    n.entitat.id = :entitatId " +
		       "   and n.estat = es.caib.notib.core.api.dto.NotificacioEstatEnumDto.PENDENT " +
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
			"   and n.estat = es.caib.notib.core.api.dto.NotificacioEstatEnumDto.PENDENT " +
			"   and n.registreEnviamentIntent >= :maxReintents ")
	List<Long> findIdsByNotificaEstatPendentSenseReintentsDisponibles(
			@Param("entitatId")Long entitatId, 
			@Param("maxReintents")Integer maxReintents);
	
	@Query( " select n.id " +   
			"  from NotificacioEntity n " +
		    " where " +
		    "    n.entitat.id = :entitatId " +
		    "   and n.estat = es.caib.notib.core.api.dto.NotificacioEstatEnumDto.PENDENT " +
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
