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
 * de dades del tipus entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioEnviamentRepository extends JpaRepository<NotificacioEnviamentEntity, Long> {

	List<NotificacioEnviamentEntity> findByNotificacioId(
			Long notificacioId);

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
			"and (:esDataEnviamentFiNull = true or n.createdDate <= :dataEnviamentFi) " +
			"and (:esCodiNotificaNull = true or n.notificaIdentificador like lower('%'||:codiNotifica||'%')) " +
			"and (:esCreatedbyNull = true or n.createdBy like lower('%'||:createdBy||'%')) " +
			"and (:esNifTitularNull = true or n.titularNif like lower('%'||:nifTitular||'%')) " +
			"and (:esNomTitularNull = true or n.titularNom like lower('%'||:nomTitular||'%')) " +
			"and (:esEmailTitularNull = true or n.titularEmail = :emailTitular) " +
			//"and   (:isDestinatarisNull = true or lower(c.destinatariNom) like lower('%'||:destinataris||'%'))" +
			//"and  	or lower(c.destinatariLlinatge1) like lower('%'||:destinataris||'%'))" +
			//"and 	or lower(c.destinatariLlinatge2) like lower('%'||:destinataris||'%'))" +
			//"and   (:isCodiNotibNull = true or n.titularEmail = :codiNotib) " +
			"and (:isNumeroCertCorreusNull = true or n.notificaCertificacioArxiuId like lower('%'||:numeroCertCorreus||'%')) " +
			"and n.notificacio = :notificacio")
	Page<NotificacioEnviamentEntity> findByNotificacio(
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
			//@Param("isDestinatarisNull") boolean isDestinatarisNull,
			//@Param("destinataris") String destinataris,
			//@Param("isCodiNotibNull") boolean isCodiNotibNull,
			//@Param("codiNotib") String codiNotib,
			@Param("isNumeroCertCorreusNull") boolean isNumeroCertCorreusNull,
			@Param("numeroCertCorreus") String numeroCertCorreus,
			@Param("notificacio") NotificacioEntity notificacio,
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
