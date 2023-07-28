/**
 * 
 */
package es.caib.notib.persist.repository;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.UsuariEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus enviaments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioEnviamentRepository extends JpaRepository<NotificacioEnviamentEntity, Long> {

	List<NotificacioEnviamentEntity> findByNotificacioId(Long notificacioId);
	List<NotificacioEnviamentEntity> findByIdIn(Collection<Long> ids);

	@Query("select id from NotificacioEnviamentEntity where notificaReferencia is null")
	List<Long> findIdsSenseReferencia();

	@Modifying
	@Query("update NotificacioEnviamentEntity net set net.notificaReferencia = :referencia where net.id = :id")
	void updateReferencia(@Param("id") Long id, @Param("referencia") String referencia);


	@Query("select env.id from NotificacioEnviamentEntity env where env.notificacio.id in (:notificacioIdList)")
	List<Long> findIdByNotificacioIdIn(@Param("notificacioIdList")  Collection<Long> notificacioIdList);

	@Query(value = "FROM NotificacioEnviamentEntity n WHERE n.id = :notificacioId ORDER BY n.notificaEstatData DESC, n.notificaEstatDataActualitzacio DESC")
	List<NotificacioEnviamentEntity> findByNotificacioIdOrderByNotificaEstatDataAndOrderByNotificaEstatDataActualitzacioDesc(
			@Param("notificacioId")  Long notificacioId);
	
//	NotificacioEnviamentEntity findById(Long id);
	
	@Query(value = "FROM NotificacioEnviamentEntity n WHERE n.notificacio = :notificacio")
	List<NotificacioEnviamentEntity> findByNotificacio(
			@Param("notificacio") NotificacioEntity notificacio);

	NotificacioEnviamentEntity findByNotificaReferencia(
			String notificaReferencia);

	NotificacioEnviamentEntity findByNotificacioAndNotificaReferencia(
			NotificacioEntity notificacio,
			String notificaReferencia);
	
//	@Query(	" from NotificacioEnviamentEntity " +
//			" where	notificacio = :notificacio " + 
//			"	and (notificaEstatFinal = false " + 
//			"   		and notificaEstat != es.caib.notib.client.domini.EnviamentEstat.ENVIADA" +
//			"   		and notificaEstat != es.caib.notib.client.domini.EnviamentEstat.NOTIB_ENVIADA" +
//			"   		and notificaEstat != es.caib.notib.client.domini.EnviamentEstat.ENVIAT_SIR)" +
//			" order by notificaEstatDataActualitzacio asc nulls first")
	@Query(	" from NotificacioEnviamentEntity " +
			" where	notificacio = :notificacio " + 
			"	and (notificaEstat = es.caib.notib.client.domini.EnviamentEstat.NOTIB_PENDENT" +
			"   		or notificaEstat = es.caib.notib.client.domini.EnviamentEstat.REGISTRADA)" +
			" order by notificaEstatDataActualitzacio asc nulls first")
	List<NotificacioEnviamentEntity> findEnviamentsPendentsNotificaByNotificacio(@Param("notificacio") NotificacioEntity notificacio);

	@Query(	" from NotificacioEnviamentEntity " +
			" where	notificacio.id = :notificacioId " +
			"	and (notificaEstatFinal = false " + 
			"   		and notificaEstat = es.caib.notib.client.domini.EnviamentEstat.NOTIB_PENDENT)" +
			" order by notificaEstatDataActualitzacio asc nulls first")
	List<NotificacioEnviamentEntity> findEnviamentsPendentsByNotificacioId(@Param("notificacioId") Long notificacioId);

	@Query(	"select CASE WHEN count(notificacio.id) > 0 then true else false END from NotificacioEnviamentEntity " +
			" where	notificacio.id = :notificacioId " +
			"	and (notificaEstatFinal = false " +
			"   		and notificaEstat = es.caib.notib.client.domini.EnviamentEstat.NOTIB_PENDENT)")
	boolean hasEnviamentsPendentsByNotificacioId(@Param("notificacioId") Long notificacioId);

	NotificacioEnviamentEntity findByNotificacioEntitatAndNotificaIdentificador(
			EntitatEntity entitat,
			String notificaIdentificador);
	
	NotificacioEnviamentEntity findByNotificaIdentificador(
			String notificaIdentificador);
	
	@Query(	" select id from NotificacioEnviamentEntity " +
			" where	notificaEstat = es.caib.notib.client.domini.EnviamentEstat.EXPIRADA " +
			"		and notificaCertificacioData is null" +
			" order by notificaEstatDataActualitzacio asc nulls first")
	List<Long> findIdExpiradesAndNotificaCertificacioDataNull();
	
	@Query(	"from" +
			"    NotificacioEnviamentEntity n " +
			"where " +
			"    (:esDataEnviamentIniciNull = true or n.createdDate >= :dataEnviamentInici) " +
			"and (:isCodiProcedimentNull = true or lower(n.notificacio.procedimentCodiNotib) like lower('%'||:codiProcediment||'%')) " +
			"and (:isGrupNull = true or lower(n.notificacio.grupCodi) like lower('%'||:grup||'%')) " +
			"and (:isConcepteNull = true or lower(n.notificacio.concepte) like lower('%'||:concepte||'%')) " +
			"and (:isDescripcioNull = true or lower(n.notificacio.descripcio) like lower('%'||:descripcio||'%')) " +
			"and (:isDataProgramadaDisposicioIniciNull = true or n.notificacio.enviamentDataProgramada >= :dataProgramadaDisposicioInici) " +
			"and (:isDataProgramadaDisposicioFiNull = true or n.notificacio.enviamentDataProgramada <= :dataProgramadaDisposicioFi) " +
			"and (:isDataCaducitatIniciNull = true or n.notificacio.caducitat >= :dataCaducitatInici) " +
			"and (:isDataCaducitatFiNull = true or n.notificacio.caducitat <= :dataCaducitatFi) " +
			"and (:isTipusEnviamentNull = true or lower(n.notificacio.enviamentTipus) like lower('%'||:tipusEnviament||'%')) " +
			"and (:isCsvNull = true or lower(concat(n.notificacio.document.uuid, n.notificacio.document.csv)) like lower('%'||:csv||'%')) " +
			"and (:isEstatNull = true or lower(n.notificacio.estat) like lower('%'||:estat||'%') or n.notificaEstat like lower('%'||:notificaEstat||'%'))" +
			"and (:entitat = n.notificacio.entitat) " +
			"and (:esDataEnviamentFiNull = true or n.createdDate <= :dataEnviamentFi) " +
			"and (:esCodiNotificaNull = true or lower(n.notificaIdentificador) like lower('%'||:codiNotifica||'%')) " +
			"and (:esCreatedbyNull = true or lower(n.createdBy) like lower('%'||:createdBy||'%')) " +
			"and (:esNifTitularNull = true or lower(n.titular.nif) like lower('%'||:nifTitular||'%')) " +
			"and (:esNomTitularNull = true or lower(concat('[', n.titular.llinatge1, ' ', n.titular.llinatge2, ', ', n.titular.nom,']')) like lower('%'||:nomTitular||'%')) " +
			"and (:esEmailTitularNull = true or n.titular.email = :emailTitular) " +
			"and (:esDir3CodiNull = true or lower(n.notificacio.emisorDir3Codi) like lower('%'||:dir3Codi||'%')) " +
			"and (:isNumeroCertCorreusNull = true or n.notificaCertificacioNumSeguiment like lower('%'||:numeroCertCorreus||'%')) " +
			"and (:isUsuariNull = true or n.notificacio.usuariCodi like lower('%'||:usuari||'%')) "+
			"and (:isNumeroRegistreNull = true or n.notificacio.registreNumero like lower('%'||:numeroRegistre||'%')) "+
			"and (:esDataRegistreIniciNull = true or n.notificacio.registreData >= :dataRegistreInici) " +
			"and (:esDataRegistreFiNull = true or n.notificacio.registreData <= :dataRegistreFi) " )
	List<NotificacioEnviamentEntity> findByNotificacio(
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
			@Param("notificaEstat") EnviamentEstat notificaEstat,
			@Param("entitat") EntitatEntity entitat,
			@Param("esDataEnviamentIniciNull") boolean esDataEnviamentIniciNull,
			@Param("dataEnviamentInici") Date dataEnviamentInici,
			@Param("esDataEnviamentFiNull") boolean esDataEnviamentFiNull,
			@Param("dataEnviamentFi") Date dataEnviamentFi,
			@Param("esCodiNotificaNull") boolean esCodiNotificaNull,
			@Param("codiNotifica") String codiNotifica,
			@Param("esCreatedbyNull") boolean esCreatedbyNull,
			@Param("createdBy") UsuariEntity createdBy,
			@Param("esNifTitularNull") boolean esNifTitularNull,
			@Param("nifTitular") String nifTitular,
			@Param("esNomTitularNull") boolean esNomTitularNull,
			@Param("nomTitular") String nomTitular,
			@Param("esEmailTitularNull") boolean esEmailTitularNull,
			@Param("emailTitular") String emailTitular,
			@Param("esDir3CodiNull") boolean esdir3CodiNull,
			@Param("dir3Codi") String dir3Codi,
			@Param("isNumeroCertCorreusNull") boolean isNumeroCertCorreusNull,
			@Param("numeroCertCorreus") String numeroCertCorreus,
			@Param("isUsuariNull") boolean isUsuariNull,
			@Param("usuari") String usuari,
			@Param("isNumeroRegistreNull") boolean isNumeroRegistreNull,
			@Param("numeroRegistre") String numeroRegistre,
			@Param("esDataRegistreIniciNull") boolean esDataRegistreIniciNull,
			@Param("dataRegistreInici") Date dataRegistreInici,
			@Param("esDataRegistreFiNull") boolean esDataRegistreFiNull,
			@Param("dataRegistreFi") Date dataRegistreFi);



	@Query(	"from" +
			"    NotificacioEnviamentEntity n " +
			"where " +
			"	 (:esNullCodiNotifica = true or n.notificaIdentificador = :notificaIdentificador) " +
			"and n.notificacio = :notificacio")
	List<Long> findIdByEntitatAndFiltre (
			@Param("esNullCodiNotifica") boolean esNullCodiNotifica,
			@Param("notificaIdentificador") String codiNotifica,
			@Param("notificacio") NotificacioEntity notificacio);

	List<NotificacioEnviamentEntity>findByNotificacioAndIdInOrderByIdAsc (
			NotificacioEntity notificacio,
			Collection<Long> id);
	
	@Query(	"select id " +
			"  from	NotificacioEnviamentEntity " +
			" where	(notificaEstatFinal = false " +
			"   	and notificaEstat != es.caib.notib.client.domini.EnviamentEstat.NOTIB_PENDENT " +
			"   	and notificaEstat != es.caib.notib.client.domini.EnviamentEstat.REGISTRADA " +
			"   	and notificaEstat != es.caib.notib.client.domini.EnviamentEstat.FINALITZADA " +
			"   	and notificaEstat != es.caib.notib.client.domini.EnviamentEstat.PROCESSADA) " +
			"	or " +
			" 		(notificaEstatFinal = true " + 
			"		and notificaEstat = es.caib.notib.client.domini.EnviamentEstat.EXPIRADA " +
			"		and notificaCertificacioData is null)" +
			"   and notificaIntentNum < :maxReintents " +
			" order by notificaEstatDataActualitzacio asc nulls first")
	List<Long> findByNotificaRefresc(
			@Param("maxReintents")Integer maxReintents,
			Pageable pageable);
	
	@Query(	"select id " +
			"  from	NotificacioEnviamentEntity " +
			" where	registreEstatFinal = false " +
			"   and registreEstat != es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto.OFICI_ACCEPTAT " +
			"   and registreEstat != es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto.REBUTJAT " +
			"   and notificaEstat = es.caib.notib.client.domini.EnviamentEstat.ENVIAT_SIR " +
			"   and sirConsultaIntent < :maxReintents " +
			" order by sirConsultaData asc nulls first")
	List<Long> findByRegistreRefresc(
			@Param("maxReintents")Integer maxReintents,
			Pageable pageable);
	
	
	// Recupera enviaments amb DEH finalitzats sense certificació
	@Query(	"select id " +
			"from NotificacioEnviamentEntity " +
			" where	notificaEstatFinal = true " + 
			"	and notificaCertificacioData is null" +
			"	and dehNif is not null" +
			"	and dehCertIntentNum < :maxReintents " +
			" order by notificaEstatDataActualitzacio asc nulls first")
	List<Long> findByDEHAndEstatFinal(
			@Param("maxReintents")Integer maxReintents,
			Pageable pageable);
	
	// Recupera enviaments a CIE finalitzats sense certificació
	@Query(	"select id " +
			"from NotificacioEnviamentEntity " +
			" where	notificaEstatFinal = true " + 
			"	and notificaCertificacioData is null" +
			"	and entregaPostal is not null" +
			"	and entregaPostal.domiciliCodiPostal is not null" +
			"	and cieCertIntentNum < :maxReintents " +
			" order by notificaEstatDataActualitzacio asc nulls first")
	List<Long> findByCIEAndEstatFinal(
			@Param("maxReintents")Integer maxReintents,
			Pageable pageable);

	
	
	// API CARPETA
	// --------------------------------------------------------------------------------------------------------------------

	@Query( " select count(distinct ne) " +
			" from NotificacioEnviamentEntity ne " +
			" left outer join ne.destinataris d " +
		    " where ne.notificacio.enviamentTipus = :tipus " +
			"   and (ne.notificacio.estat = es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.ENVIADA " +
			"    or ne.notificacio.estat = es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS " +
			"    or ne.notificacio.estat = es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.FINALITZADA " +
			"    or ne.notificacio.estat = es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS " +
			"    or ne.notificacio.estat = es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.PROCESSADA) " +
		    "   and (:esEstatFinalNull = true or ne.notificaEstatFinal = :estatFinal) " +
		    "   and ne.notificaEstat <> es.caib.notib.client.domini.EnviamentEstat.REGISTRADA " +
			"   and ((ne.titular.incapacitat = false and upper(ne.titular.nif) = upper(:dniTitular)) or (upper(d.nif) = upper(:dniTitular)))" +
			"	and (:esDataInicialNull = true or ne.notificacio.notificaEnviamentData >= :dataInicial) " +
			"	and (:esDataFinalNull = true or ne.notificacio.notificaEnviamentData <= :dataFinal) " +
			"   and (:esVisibleCarpetaNull = true or :visibleCarpeta = false " +
			"		or (ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.PENDENT " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.PENDENT_ENVIAMENT " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.PENDENT_SEU " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.PENDENT_CIE " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.PENDENT_DEH " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.ENVIADA " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.ENVIADA_CI " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.ENVIADA_DEH " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.ENTREGADA_OP " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.REBUTJADA " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.LLEGIDA " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.NOTIFICADA " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.EXPIRADA " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.ABSENT " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.ADRESA_INCORRECTA " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.DESCONEGUT " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.MORT " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.EXTRAVIADA " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.SENSE_INFORMACIO))")
	Integer countEnviaments(
			@Param("dniTitular") String dniTitular,
			@Param("esDataInicialNull") boolean esDataInicialNull,
			@Param("dataInicial") Date dataInicial,
			@Param("esDataFinalNull") boolean esDataFinalNull,
			@Param("dataFinal") Date dataFinal,
			@Param("tipus") EnviamentTipus tipus,
			@Param("esEstatFinalNull") boolean esEstatFinalNull,
			@Param("estatFinal") Boolean estatFinal,
			@Param("esVisibleCarpetaNull") boolean esVisibleCarpetaNull,
			@Param("visibleCarpeta") Boolean visibleCarpeta);

	@Query( " select distinct ne " +
			" from NotificacioEnviamentEntity ne " +
			" left outer join ne.destinataris d " +
		    " where ne.notificacio.enviamentTipus =  :tipus " +
			"   and (ne.notificacio.estat = es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.ENVIADA " +
			"    or ne.notificacio.estat = es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS " +
			"    or ne.notificacio.estat = es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.FINALITZADA " +
			"    or ne.notificacio.estat = es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS " +
			"    or ne.notificacio.estat = es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto.PROCESSADA) " +
		    "   and (:esEstatFinalNull = true or ne.notificaEstatFinal = :estatFinal) " +
			"   and ne.notificaEstat <> es.caib.notib.client.domini.EnviamentEstat.REGISTRADA " +
			"   and ((ne.titular.incapacitat = false and upper(ne.titular.nif) = upper(:dniTitular)) or (upper(d.nif) = upper(:dniTitular))) " +
			"	and (:esDataInicialNull = true or ne.notificacio.notificaEnviamentData >= :dataInicial) " +
			"	and (:esDataFinalNull = true or ne.notificacio.notificaEnviamentData <= :dataFinal) " +
			"   and (:esVisibleCarpetaNull = true or :visibleCarpeta = false " +
			"		or (ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.PENDENT " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.PENDENT_ENVIAMENT " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.PENDENT_SEU " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.PENDENT_CIE " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.PENDENT_DEH " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.ENVIADA " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.ENVIADA_CI " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.ENVIADA_DEH " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.ENTREGADA_OP " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.REBUTJADA " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.LLEGIDA " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.NOTIFICADA " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.EXPIRADA " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.ABSENT " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.ADRESA_INCORRECTA " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.DESCONEGUT " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.MORT " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.EXTRAVIADA " +
			"		or ne.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.SENSE_INFORMACIO))")
	Page<NotificacioEnviamentEntity> findEnviaments(
			@Param("dniTitular") String dniTitular,
			@Param("esDataInicialNull") boolean esDataInicialNull,
			@Param("dataInicial") Date dataInicial,
			@Param("esDataFinalNull") boolean esDataFinalNull,
			@Param("dataFinal") Date dataFinal,
			@Param("tipus") EnviamentTipus tipus,
			@Param("esEstatFinalNull") boolean esEstatFinalNull,
			@Param("estatFinal") Boolean estatFinal,
			@Param("esVisibleCarpetaNull") boolean esVisibleCarpetaNull,
			@Param("visibleCarpeta") Boolean visibleCarpeta,
			Pageable pageable);

	@Query(value = "from NotificacioEnviamentEntity where notificaReferencia = :enviamentUuid")
	Optional<NotificacioEnviamentEntity> findByUuid(@Param("enviamentUuid") String enviamentUuid);

//	@Query(value = "select count(e.id) = 0 from not_notificacio e where e.notificacio.id = :notificacioId and e.registre_data is null", nativeQuery = true)
	@Query(value = "select CASE WHEN count(e.id) = 0 THEN 1 ELSE 0 END from NOT_NOTIFICACIO_ENV e where e.NOTIFICACIO_ID = :notificacioId and e.registre_data is NULL", nativeQuery = true)
	int areEnviamentsRegistrats(@Param("notificacioId") Long notificacioId);

}
