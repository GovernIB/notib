package es.caib.notib.persist.repository;

import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.persist.filtres.FiltreNotificacio;
import es.caib.notib.persist.entity.NotificacioTableEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.cdi.Eager;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus notificacio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Eager
public interface NotificacioTableViewRepository extends JpaRepository<NotificacioTableEntity, Long> {

	@Modifying
	@Query("update NotificacioTableEntity nte set nte.referencia =" +
			" (select net.referencia from NotificacioEntity net where net.id = nte.id) where nte.referencia is null")
	void updateReferenciesNules();

	/**
	 * Consulta de la taula de remeses d
	 */
	@Query(	"select ntf " +
			"from " +
			"     NotificacioTableEntity ntf " +
			"where " +
			"    (ntf.notificacioMassiva = :#{#filtre.notificacioMassiva}) " +
			"and (:#{#filtre.entitatIdNull} = true or ntf.entitat.id = :#{#filtre.entitatId}) " +
			"and (:#{#filtre.enviamentTipusNull} = true or ntf.enviamentTipus = :#{#filtre.enviamentTipus}) " +
			"and (:#{#filtre.concepteNull} = true or lower(ntf.concepte) like concat('%', lower(:#{#filtre.concepte}), '%')) " +
			"and (:#{#filtre.estatNull} = true or bitand(ntf.estatMask, :#{#filtre.estatMask}) <> 0) " +
			"and (:#{#filtre.dataIniciNull} = true or ntf.createdDate >= :#{#filtre.dataInici}) " +
			"and (:#{#filtre.dataFiNull} = true or ntf.createdDate <= :#{#filtre.dataFi}) "+
			"and (:#{#filtre.organCodiNull} = true or ntf.organCodi = :#{#filtre.organCodi}) " +
			"and (:#{#filtre.procedimentNull} = true or ntf.procedimentCodi = :#{#filtre.procedimentCodi}) " +
			"and (:#{#filtre.titularNull} = true or lower(ntf.titular) like concat('%', lower(:#{#filtre.titular}), '%'))" +
			"and (:#{#filtre.tipusUsuariNull} = true or ntf.tipusUsuari = :#{#filtre.tipusUsuari}) " +
			"and (:#{#filtre.numExpedientNull} = true or lower(ntf.numExpedient) like concat('%', lower(:#{#filtre.numExpedient}), '%')) " +
			"and (:#{#filtre.creadaPerNull} = true or ntf.createdBy.codi = :#{#filtre.creadaPer}) " +
			"and (:#{#filtre.identificadorNull} = true or ntf.notificaIds like concat('%', :#{#filtre.identificador}, '%')) " +
			"and (:#{#filtre.nomesSenseErrors} = false or ntf.notificaErrorData is null) " +
			"and (:#{#filtre.nomesAmbErrors} = false or ntf.notificaErrorData is not null)")
	Page<NotificacioTableEntity> findAmbFiltreByNotificacioMassiva(FiltreNotificacio filtre, Pageable paginacio);

	@Modifying
	@Query("update NotificacioTableEntity nt " +
			"set " +
			" nt.procedimentIsComu = :procedimentComu, " +
			" nt.procedimentNom = :procedimentNom, " +
			" nt.procedimentRequirePermission = :procedimentRequirePermission " +
			"where nt.procedimentCodi = :procedimentCodi ")
	void updateProcediment(@Param("procedimentComu") boolean procedimentComu,
						   @Param("procedimentNom") String procedimentNom,
						   @Param("procedimentRequirePermission") boolean procedimentRequireDirectPermission,
						   @Param("procedimentCodi") String procedimentCodi);

	@Modifying
	@Query("update NotificacioTableEntity nt set nt.organEstat = :estat where nt.organCodi = :organCodi")
	void updateOrganEstat(@Param("organCodi") String organCodi, @Param("estat") OrganGestorEstatEnum estat);

	@Query("select ntf.id from NotificacioTableEntity ntf " +
			"where " +
			"    (:#{#filtre.entitatIdNull} = true or ntf.entitat.id = :#{#filtre.entitatId}) " +
			" and  (" +
			// PERMISOS
			// Iniciada pel propi usuari
			" :#{#filtre.isUsuariEntitat} = true or " +
			" :#{#filtre.isSuperAdmin} = true or " +
			" ntf.usuariCodi = :#{#filtre.usuariCodi} " +
			// Té permís consulta sobre el procediment
			"	or (:#{#filtre.procedimentsCodisNotibNull} = false and ntf.procedimentCodiNotib is not null " +
			"			and (ntf.procedimentCodiNotib in (:#{#filtre.procedimentsCodisNotibSplit[0]}) " +
			"					or ntf.procedimentCodiNotib in (:#{#filtre.procedimentsCodisNotibSplit[1]}) " +
			"					or ntf.procedimentCodiNotib in (:#{#filtre.procedimentsCodisNotibSplit[2]}) " +
			"					or ntf.procedimentCodiNotib in (:#{#filtre.procedimentsCodisNotibSplit[3]})) " +
			"			and ntf.procedimentIsComu = false) " +
			// Té permís consulta sobre l'òrgan
			"	or (:#{#filtre.organsGestorsCodisNotibNull} = false and ntf.organCodi is not null " +
			"			and (ntf.procedimentIsComu = false or ntf.procedimentRequirePermission = false) " +
			"			and ntf.organCodi in (:#{#filtre.organsGestorsCodisNotib})) " +
			// Procediment comú amb permís comú sobre l'òrgan
			"	or (:#{#filtre.esOrgansGestorsComunsCodisNotibNull} = false and ntf.organCodi is not null " +
			"			and ntf.procedimentIsComu = true and ntf.procedimentRequirePermission = false " +
			"			and ntf.organCodi in (:#{#filtre.organsGestorsComunsCodisNotib})) " +
			// Procediment comú amb permís de procediment-òrgan
			"   or (:#{#filtre.procedimentOrgansIdsNotibNull} = false and ntf.procedimentCodiNotib is not null " +
			"			and CONCAT(ntf.procedimentCodiNotib, '-', ntf.organCodi) in (:#{#filtre.procedimentOrgansIdsNotib})" +
			"		) " +
			//
//			"		(:esProcedimentsCodisNotibNull = false and ntf.procedimentCodiNotib is not null and ntf.procedimentCodiNotib in (:procedimentsCodisNotib) and ntf.procedimentIsComu = false) " +			 // Té permís sobre el procediment
//			"	or	(:esOrgansGestorsCodisNotibNull = false and ntf.organCodi is not null and " +
//			"			(ntf.procedimentCodiNotib is null or (ntf.procedimentIsComu = true and ntf.procedimentRequirePermission = false)) and ntf.organCodi in (:organsGestorsCodisNotib)" +
//			"		) " + // Té permís sobre l'òrgan
//			"   or 	((ntf.procedimentCodiNotib is null or ntf.procedimentIsComu = true) and ntf.usuariCodi = :usuariCodi) " +								// És una notificaicó sense procediment o un procediment comú, iniciat pel propi usuari
//			"   or 	(:esProcedimentOrgansIdsNotibNull = false and ntf.procedimentCodiNotib is not null and " +
//			"			CONCAT(ntf.procedimentCodiNotib, '-', ntf.organCodi) in (:procedimentOrgansIdsNotib)" +
//			"		) " +	// Procediment comú amb permís de procediment-òrgan
			"	) " +
			"and (:#{#filtre.isSuperAdmin} = false or ntf.entitat in (:#{#filtre.entitatsActives})) " +
			"and (:#{#filtre.isAdminOrgan} = false or " +
			"   (" +
			"	(:#{#filtre.procedimentsCodisNotibNull} = false and ntf.procedimentCodiNotib is not null " +
			"		and (ntf.procedimentCodiNotib in (:#{#filtre.procedimentsCodisNotibSplit[0]}) " +
			"				or ntf.procedimentCodiNotib in (:#{#filtre.procedimentsCodisNotibSplit[1]}) " +
			"				or ntf.procedimentCodiNotib in (:#{#filtre.procedimentsCodisNotibSplit[2]}) " +
			"				or ntf.procedimentCodiNotib in (:#{#filtre.procedimentsCodisNotibSplit[3]})))" +
//			"   or (ntf.procedimentCodiNotib is null and ntf.organGestor is not null and ntf.organGestor.codi in (:organs))) " +
			"   or (ntf.organCodi is not null and ntf.organCodi in (:#{#filtre.organs}))" +
			"	)) " +
			"and (:#{#filtre.notMassivaIdNull} = true or ntf.notificacioMassiva.id = :#{#filtre.notMassivaId}) " +
			"and (:#{#filtre.enviamentTipusNull} = true or ntf.enviamentTipus = :#{#filtre.enviamentTipus}) " +
			"and (:#{#filtre.concepteNull} = true or lower(ntf.concepte) like concat('%', lower(:#{#filtre.concepte}), '%')) " +
			"and (:#{#filtre.estatNull} = true or bitand(ntf.estatMask, :#{#filtre.estatMask}) <> 0) " +
			"and (:#{#filtre.dataIniciNull} = true or ntf.createdDate >= :#{#filtre.dataInici}) " +
			"and (:#{#filtre.dataFiNull} = true or ntf.createdDate <= :#{#filtre.dataFi}) "+
			"and (:#{#filtre.organCodiNull} = true or ntf.organCodi = :#{#filtre.organCodi}) " +
			"and (:#{#filtre.procedimentNull} = true or ntf.procedimentCodi = :#{#filtre.procedimentCodi}) " +
			"and (:#{#filtre.titularNull} = true or lower(ntf.titular) like concat('%', lower(:#{#filtre.titular}), '%'))" +
			"and (:#{#filtre.tipusUsuariNull} = true or ntf.tipusUsuari = :#{#filtre.tipusUsuari}) " +
			"and (:#{#filtre.numExpedientNull} = true or lower(ntf.numExpedient) like concat('%', lower(:#{#filtre.numExpedient}), '%')) " +
			"and (:#{#filtre.creadaPerNull} = true or ntf.createdBy.codi = :#{#filtre.creadaPer}) " +
			"and (:#{#filtre.identificadorNull} = true or ntf.notificaIds like concat('%', :#{#filtre.identificador}, '%')) " +
			"and (:#{#filtre.registreNumNull} = true or ntf.registreNums like concat('%', :#{#filtre.registreNum}, '%')) " +
			"and (:#{#filtre.nomesSenseErrors} = false or ntf.notificaErrorData is null) " +
			"and (:#{#filtre.adminOrgan} = true or :#{#filtre.nomesAmbErrors} = false or ntf.notificaErrorData is not null) " +
			"and (:#{#filtre.referenciaNull} = true or lower(ntf.referencia) like '%' || lower(:#{#filtre.referencia}) || '%')"
	)
    List<Long> findIdsAmbFiltre(FiltreNotificacio filtre);

	@Query("select ntf from NotificacioTableEntity ntf " +
			"where " +
			"    (:#{#filtre.entitatIdNull} = true or ntf.entitat.id = :#{#filtre.entitatId}) " +
			" and  (" +
			// PERMISOS
			// Iniciada pel propi usuari
			" :#{#filtre.isUsuariEntitat} = true or " +
			" :#{#filtre.isSuperAdmin} = true or " +
			"   ntf.usuariCodi = :#{#filtre.usuariCodi} " +
			// Té permís consulta sobre el procediment
//			"	or (:#{#filtre.procedimentsCodisNotibNull} = false and ntf.procedimentCodiNotib is not null " +
//			"			and ntf.procedimentCodiNotib in (:#{#filtre.procedimentsCodisNotib}) and ntf.procedimentIsComu = false) " +
			"	or (:#{#filtre.procedimentsCodisNotibNull} = false and ntf.procedimentCodiNotib is not null " +
			"			and (ntf.procedimentCodiNotib in (:#{#filtre.procedimentsCodisNotibSplit[0]}) " +
			"					or ntf.procedimentCodiNotib in (:#{#filtre.procedimentsCodisNotibSplit[1]}) " +
			"					or ntf.procedimentCodiNotib in (:#{#filtre.procedimentsCodisNotibSplit[2]}) " +
			"					or ntf.procedimentCodiNotib in (:#{#filtre.procedimentsCodisNotibSplit[3]})) " +
			"			and ntf.procedimentIsComu = false) " +
			// Té permís consulta sobre l'òrgan
			"	or (:#{#filtre.organsGestorsCodisNotibNull} = false and ntf.organCodi is not null " +
			"			and (ntf.procedimentIsComu = false or ntf.procedimentRequirePermission = false) " +
			"			and ntf.organCodi in (:#{#filtre.organsGestorsCodisNotib})) " +
			// Procediment comú amb permís comú sobre l'òrgan
			"	or (:#{#filtre.esOrgansGestorsComunsCodisNotibNull} = false and ntf.organCodi is not null " +
			"			and ntf.procedimentIsComu = true and ntf.procedimentRequirePermission = false " +
			"			and ntf.organCodi in (:#{#filtre.organsGestorsComunsCodisNotib})) " +
			// Procediment comú amb permís de procediment-òrgan
			"   or (:#{#filtre.procedimentOrgansIdsNotibNull} = false and ntf.procedimentCodiNotib is not null " +
			"			and CONCAT(ntf.procedimentCodiNotib, '-', ntf.organCodi) in (:#{#filtre.procedimentOrgansIdsNotib})" +
			"		) " +
			//
//			"		(:esProcedimentsCodisNotibNull = false and ntf.procedimentCodiNotib is not null and ntf.procedimentCodiNotib in (:procedimentsCodisNotib) and ntf.procedimentIsComu = false) " +			 // Té permís sobre el procediment
//			"	or	(:esOrgansGestorsCodisNotibNull = false and ntf.organCodi is not null and " +
//			"			(ntf.procedimentCodiNotib is null or (ntf.procedimentIsComu = true and ntf.procedimentRequirePermission = false)) and ntf.organCodi in (:organsGestorsCodisNotib)" +
//			"		) " + // Té permís sobre l'òrgan
//			"   or 	((ntf.procedimentCodiNotib is null or ntf.procedimentIsComu = true) and ntf.usuariCodi = :usuariCodi) " +								// És una notificaicó sense procediment o un procediment comú, iniciat pel propi usuari
//			"   or 	(:esProcedimentOrgansIdsNotibNull = false and ntf.procedimentCodiNotib is not null and " +
//			"			CONCAT(ntf.procedimentCodiNotib, '-', ntf.organCodi) in (:procedimentOrgansIdsNotib)" +
//			"		) " +	// Procediment comú amb permís de procediment-òrgan
			"	) " +
			"and (:#{#filtre.isSuperAdmin} = false or ntf.entitat in (:#{#filtre.entitatsActives})) " +
			"and (:#{#filtre.isAdminOrgan} = false or " +
			"   (" +
			"	(:#{#filtre.procedimentsCodisNotibNull} = false and ntf.procedimentCodiNotib is not null and " +
			"	(ntf.procedimentCodiNotib in (:#{#filtre.procedimentsCodisNotibSplit[0]}) " +
			"		or ntf.procedimentCodiNotib in (:#{#filtre.procedimentsCodisNotibSplit[1]}) " +
			"		or ntf.procedimentCodiNotib in (:#{#filtre.procedimentsCodisNotibSplit[2]}) " +
			"		or ntf.procedimentCodiNotib in (:#{#filtre.procedimentsCodisNotibSplit[3]})))" +
//			"   or (ntf.procedimentCodiNotib is null and ntf.organGestor is not null and ntf.organGestor.codi in (:organs))) " +
			"   or (ntf.organCodi is not null and ntf.organCodi in (:#{#filtre.organs}))" +
			"	)) " +
			"and (:#{#filtre.notMassivaIdNull} = true or ntf.notificacioMassiva.id = :#{#filtre.notMassivaId}) " +
			"and (:#{#filtre.enviamentTipusNull} = true or ntf.enviamentTipus = :#{#filtre.enviamentTipus}) " +
			"and (:#{#filtre.concepteNull} = true or lower(ntf.concepte) like concat('%', lower(:#{#filtre.concepte}), '%')) " +
			"and (:#{#filtre.estatNull} = true or bitand(ntf.estatMask, :#{#filtre.estatMask}) <> 0) " +
			"and (:#{#filtre.dataIniciNull} = true or ntf.createdDate >= :#{#filtre.dataInici}) " +
			"and (:#{#filtre.dataFiNull} = true or ntf.createdDate <= :#{#filtre.dataFi}) "+
			"and (:#{#filtre.organCodiNull} = true or ntf.organCodi = :#{#filtre.organCodi}) " +
			"and (:#{#filtre.procedimentNull} = true or ntf.procedimentCodi = :#{#filtre.procedimentCodi}) " +
			"and (:#{#filtre.titularNull} = true or lower(ntf.titular) like concat('%', lower(:#{#filtre.titular}), '%'))" +
			"and (:#{#filtre.tipusUsuariNull} = true or ntf.tipusUsuari = :#{#filtre.tipusUsuari}) " +
			"and (:#{#filtre.numExpedientNull} = true or lower(ntf.numExpedient) like concat('%', lower(:#{#filtre.numExpedient}), '%')) " +
			"and (:#{#filtre.creadaPerNull} = true or ntf.createdBy.codi = :#{#filtre.creadaPer}) " +
			"and (:#{#filtre.identificadorNull} = true or ntf.notificaIds like concat('%', :#{#filtre.identificador}, '%')) " +
			"and (:#{#filtre.registreNumNull} = true or ntf.registreNums like concat('%', :#{#filtre.registreNum}, '%')) " +
			"and (:#{#filtre.nomesSenseErrors} = false or ntf.notificaErrorData is null) " +
			"and (:#{#filtre.adminOrgan} = true or :#{#filtre.nomesAmbErrors} = false or ntf.notificaErrorData is not null) " +
			"and (:#{#filtre.deleted} = ntf.deleted) " +
			"and (:#{#filtre.referenciaNull} = true or lower(ntf.referencia) like '%' || lower(:#{#filtre.referencia}) || '%')")
	Page<NotificacioTableEntity> findAmbFiltre(FiltreNotificacio filtre, Pageable paginacio);

}
