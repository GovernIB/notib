/**
 * 
 */
package es.caib.notib.core.repository;

import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto;
import es.caib.notib.core.api.dto.notenviament.NotEnviamentTableItemDto;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.UsuariEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus enviaments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioEnviamentRepository extends JpaRepository<NotificacioEnviamentEntity, Long> {

	List<NotificacioEnviamentEntity> findByNotificacioId(
			Long notificacioId);
	
	@Query(value = "FROM NotificacioEnviamentEntity n WHERE n.id = :notificacioId ORDER BY n.notificaEstatData DESC, n.notificaEstatDataActualitzacio DESC")
	List<NotificacioEnviamentEntity> findByNotificacioIdOrderByNotificaEstatDataAndOrderByNotificaEstatDataActualitzacioDesc(
			@Param("notificacioId")  Long notificacioId);
	
	NotificacioEnviamentEntity findById(Long id);
	
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
//			"   		and notificaEstat != es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto.ENVIADA" +
//			"   		and notificaEstat != es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto.NOTIB_ENVIADA" +
//			"   		and notificaEstat != es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto.ENVIAT_SIR)" +
//			" order by notificaEstatDataActualitzacio asc nulls first")
	@Query(	" from NotificacioEnviamentEntity " +
			" where	notificacio = :notificacio " + 
			"	and (notificaEstat = es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto.NOTIB_PENDENT" +
			"   		or notificaEstat = es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto.REGISTRADA)" +
			" order by notificaEstatDataActualitzacio asc nulls first")
	List<NotificacioEnviamentEntity> findEnviamentsPendentsNotificaByNotificacio(@Param("notificacio") NotificacioEntity notificacio);
	
//	@Query(	" from NotificacioEnviamentEntity " +
//			" where	notificacio = :notificacio " + 
//			"	and (notificaEstatFinal = false " + 
//			"   		and notificaEstat != es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto.REGISTRADA" +
//			"   		and notificaEstat != es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto.ENVIADA" +
//			"   		and notificaEstat != es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto.NOTIB_ENVIADA" +
//			"   		and notificaEstat != es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto.ENVIAT_SIR)" +
//			" order by notificaEstatDataActualitzacio asc nulls first")
	@Query(	" from NotificacioEnviamentEntity " +
			" where	notificacio.id = :notificacioId " +
			"	and (notificaEstatFinal = false " + 
			"   		and notificaEstat = es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto.NOTIB_PENDENT)" +
			" order by notificaEstatDataActualitzacio asc nulls first")
	List<NotificacioEnviamentEntity> findEnviamentsPendentsByNotificacioId(@Param("notificacioId") Long notificacioId);

	NotificacioEnviamentEntity findByNotificacioEntitatAndNotificaIdentificador(
			EntitatEntity entitat,
			String notificaIdentificador);
	
	NotificacioEnviamentEntity findByNotificaIdentificador(
			String notificaIdentificador);
	
	@Query(	" select id from NotificacioEnviamentEntity " +
			" where	notificaEstat = es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto.EXPIRADA " +
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
			@Param("notificaEstat") NotificacioEnviamentEstatEnumDto notificaEstat,
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
	
	@Query(	"  from	NotificacioEnviamentEntity " +
			" where	(notificaEstatFinal = false " +
			"   	and notificaEstat != es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto.NOTIB_PENDENT " +
			"   	and notificaEstat != es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto.REGISTRADA " +
			"   	and notificaEstat != es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto.FINALITZADA " +
			"   	and notificaEstat != es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto.PROCESSADA) " +
			"	or " +
			" 		(notificaEstatFinal = true " + 
			"		and notificaEstat = es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto.EXPIRADA " +
			"		and notificaCertificacioData is null)" +
			"   and notificaIntentNum < :maxReintents " +
			" order by notificaEstatDataActualitzacio asc nulls first")
	List<NotificacioEnviamentEntity> findByNotificaRefresc(
			@Param("maxReintents")Integer maxReintents,
			Pageable pageable);
	
	@Query(	"  from	NotificacioEnviamentEntity " +
			" where	registreEstatFinal = false " +
			"   and registreEstat != es.caib.notib.core.api.dto.NotificacioRegistreEstatEnumDto.OFICI_ACCEPTAT " +
			"   and registreEstat != es.caib.notib.core.api.dto.NotificacioRegistreEstatEnumDto.REBUTJAT " +
			"   and notificaEstat = es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto.ENVIAT_SIR " +
			"   and sirConsultaIntent < :maxReintents " +
			" order by sirConsultaData asc nulls first")
	List<NotificacioEnviamentEntity> findByRegistreRefresc(
			@Param("maxReintents")Integer maxReintents,
			Pageable pageable);

	
	
	// API CARPETA
	// --------------------------------------------------------------------------------------------------------------------
	
	@Query( " select count(distinct ne) " +
			" from NotificacioEnviamentEntity ne " +
			" left outer join ne.destinataris d " +
		    " where ne.notificacio.enviamentTipus = :tipus " +
		    "   and (ne.notificacio.estat = es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto.ENVIADA " +
		    "    or ne.notificacio.estat = es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto.FINALITZADA " +
		    "    or ne.notificacio.estat = es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto.PROCESSADA) " +
		    "   and (:esEstatFinalNull = true or ne.notificaEstatFinal = :estatFinal) " +
		    "   and ((ne.titular.incapacitat = false and upper(ne.titular.nif) = :nif) " +
		    "   or (upper(d.nif) = :nif)) ")
	Integer countEnviamentsByNif(
			@Param("nif") String dniTitular,
			@Param("tipus") NotificaEnviamentTipusEnumDto tipus,
			@Param("esEstatFinalNull") boolean esEstatFinalNull,
			@Param("estatFinal") Boolean estatFinal);
	
	@Query( " select distinct ne " +
			" from NotificacioEnviamentEntity ne " +
			" left outer join ne.destinataris d " +
		    " where ne.notificacio.enviamentTipus =  :tipus " +
		    "   and (ne.notificacio.estat = es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto.ENVIADA " +
		    "    or ne.notificacio.estat = es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto.FINALITZADA " +
		    "    or ne.notificacio.estat = es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto.PROCESSADA) " +
		    "   and (:esEstatFinalNull = true or ne.notificaEstatFinal = :estatFinal) " +
		    "   and ((ne.titular.incapacitat = false and upper(ne.titular.nif) = :nif) " +
		    "   or (upper(d.nif) = :nif)) ")
	Page<NotificacioEnviamentEntity> findEnviamentsByNif(
			@Param("nif") String dniTitular,
			@Param("tipus") NotificaEnviamentTipusEnumDto tipus,
			@Param("esEstatFinalNull") boolean esEstatFinalNull,
			@Param("estatFinal") Boolean estatFinal,
			Pageable pageable);
	
}
