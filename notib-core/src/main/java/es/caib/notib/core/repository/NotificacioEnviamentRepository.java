/**
 * 
 */
package es.caib.notib.core.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.UsuariEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus enviaments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioEnviamentRepository extends JpaRepository<NotificacioEnviamentEntity, Long> {

	List<NotificacioEnviamentEntity> findByNotificacioId(
			Long notificacioId);
	
	NotificacioEnviamentEntity findById(Long id);
	
	@Query(value = "FROM NotificacioEnviamentEntity n WHERE n.notificacio = :notificacio")
	List<NotificacioEnviamentEntity> findByNotificacio(
			@Param("notificacio") NotificacioEntity notificacio);

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
			"and (:isEstatNull = true or lower(n.notificacio.estat) like lower('%'||:estat||'%')) " +
			"and (:entitat = n.notificacio.entitat) " +
			"and (:esDataEnviamentFiNull = true or n.createdDate <= :dataEnviamentFi) " +
			"and (:esCodiNotificaNull = true or lower(n.notificaIdentificador) like lower('%'||:codiNotifica||'%')) " +
			"and (:esCreatedbyNull = true or lower(n.createdBy) like lower('%'||:createdBy||'%')) " +
			"and (:esNifTitularNull = true or lower(n.titular.nif) like lower('%'||:nifTitular||'%')) " +
			"and (:esNomTitularNull = true or lower(concat('[', n.titular.llinatge1, ' ', n.titular.llinatge2, ', ', n.titular.nom,']')) like lower('%'||:nomTitular||'%')) " +
			"and (:esEmailTitularNull = true or n.titular.email = :emailTitular) " +
			"and (:esDir3CodiNull = true or lower(n.notificacio.emisorDir3Codi) like lower('%'||:dir3Codi||'%')) " +
			//"and   (:isDestinatarisNull = true or lower(c.destinatariNom) like lower('%'||:destinataris||'%'))" +
			//"and  	or lower(c.destinatariLlinatge1) like lower('%'||:destinataris||'%'))" +
			//"and 	or lower(c.destinatariLlinatge2) like lower('%'||:destinataris||'%'))" +
			//"and   (:isCodiNotibNull = true or n.titularEmail = :codiNotib) " +
			"and (:isNumeroCertCorreusNull = true or n.notificaCertificacioNumSeguiment like lower('%'||:numeroCertCorreus||'%')) " +
			"and (:isUsuariNull = true or n.notificacio.usuariCodi like lower('%'||:usuari||'%')) "+
			"and (:isLlibreRegistreNull = true or n.notificacio.llibre like lower('%'||:llibreRegistre||'%')) " +
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
			@Param("isLlibreRegistreNull") boolean isLlibreRegistreNull,
			@Param("llibreRegistre") String llibreRegistre,
			@Param("isNumeroRegistreNull") boolean isNumeroRegistreNull,
			@Param("numeroRegistre") String numeroRegistre,
			@Param("esDataRegistreIniciNull") boolean esDataRegistreIniciNull,
			@Param("dataRegistreInici") Date dataRegistreInici,
			@Param("esDataRegistreFiNull") boolean esDataRegistreFiNull,
			@Param("dataRegistreFi") Date dataRegistreFi);
	
	

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
			"and (:isEstatNull = true or lower(n.notificacio.estat) like lower('%'||:estat||'%')) " +
			"and (:entitat = n.notificacio.entitat) " +
			"and (:esDataEnviamentFiNull = true or n.createdDate <= :dataEnviamentFi) " +
			"and (:esCodiNotificaNull = true or lower(n.notificaIdentificador) like lower('%'||:codiNotifica||'%')) " +
			"and (:esCreatedbyNull = true or lower(n.createdBy) like lower('%'||:createdBy||'%')) " +
			"and (:esNifTitularNull = true or lower(n.titular.nif) like lower('%'||:nifTitular||'%')) " +
			"and (:esNomTitularNull = true or lower(concat('[', n.titular.llinatge1, ' ', n.titular.llinatge2, ', ', n.titular.nom,']')) like lower('%'||:nomTitular||'%')) " +
			"and (:esEmailTitularNull = true or n.titular.email = :emailTitular) " +
			"and (:esDir3CodiNull = true or lower(n.notificacio.emisorDir3Codi) like lower('%'||:dir3Codi||'%')) " +
			//"and   (:isDestinatarisNull = true or lower(c.destinatariNom) like lower('%'||:destinataris||'%'))" +
			//"and  	or lower(c.destinatariLlinatge1) like lower('%'||:destinataris||'%'))" +
			//"and 	or lower(c.destinatariLlinatge2) like lower('%'||:destinataris||'%'))" +
			//"and   (:isCodiNotibNull = true or n.titularEmail = :codiNotib) " +
			"and (:isNumeroCertCorreusNull = true or n.notificaCertificacioNumSeguiment like lower('%'||:numeroCertCorreus||'%')) " +
			"and (:isUsuariNull = true or n.notificacio.usuariCodi like lower('%'||:usuari||'%')) "+
			"and (:isLlibreRegistreNull = true or n.notificacio.llibre like lower('%'||:llibreRegistre||'%')) " +
			"and (:isNumeroRegistreNull = true or n.notificacio.registreNumero like lower('%'||:numeroRegistre||'%')) "+
			"and (:esDataRegistreIniciNull = true or n.notificacio.registreData >= :dataRegistreInici) " +
			"and (:esDataRegistreFiNull = true or n.notificacio.registreData <= :dataRegistreFi) " )
	Page<NotificacioEnviamentEntity> findByNotificacio(
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
			@Param("isLlibreRegistreNull") boolean isLlibreRegistreNull,
			@Param("llibreRegistre") String llibreRegistre,
			@Param("isNumeroRegistreNull") boolean isNumeroRegistreNull,
			@Param("numeroRegistre") String numeroRegistre,
			@Param("esDataRegistreIniciNull") boolean esDataRegistreIniciNull,
			@Param("dataRegistreInici") Date dataRegistreInici,
			@Param("esDataRegistreFiNull") boolean esDataRegistreFiNull,
			@Param("dataRegistreFi") Date dataRegistreFi,
			Pageable pageable);
	
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
			" where	notificaEstatFinal = false " +
			"   and notificaEstat != es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto.NOTIB_PENDENT " +
			" order by notificaEstatDataActualitzacio asc")
	List<NotificacioEnviamentEntity> findByNotificaRefresc(
			Pageable pageable);

}
