package es.caib.notib.persist.repository;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.logic.intf.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.NotificacioMassivaEntity;
import es.caib.notib.persist.entity.NotificacioTableEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.cdi.Eager;
import org.springframework.data.repository.query.Param;

import java.util.Date;
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
	 * Consulta de la taula de remeses sense filtres per al rol usuari
	 */
	@Query( "select ntf " +
			"from " +
			"    NotificacioTableEntity ntf " +
			"where " +
			"   (" +
			"		(:esProcedimentsCodisNotibNull = false and ntf.procedimentCodiNotib is not null and ntf.procedimentCodiNotib in (:procedimentsCodisNotib)) " +	// Té permís sobre el procediment
			"	or	(:esOrgansGestorsCodisNotibNull = false and ntf.organCodi is not null " +
			"												and (ntf.procedimentCodiNotib is null or (ntf.procedimentIsComu = true and ntf.procedimentRequirePermission = false)) " + // comunicacions o procediments comuns
			"												and ntf.organCodi in (:organsGestorsCodisNotib)) " +						// Té permís sobre l'òrgan
			"   or 	((ntf.procedimentCodiNotib is null or ntf.procedimentIsComu = true) and ntf.usuariCodi = :usuariCodi) " +										// És una notificaicó sense procediment o un procediment comú, iniciat pel propi usuari
			"   or 	(:esProcedimentOrgansIdsNotibNull = false and ntf.procedimentCodiNotib is not null and " +
			"			CONCAT(ntf.procedimentCodiNotib, '-', ntf.organCodi) in (:procedimentOrgansIdsNotib)" +
			"		) " +	// Procediment comú amb permís de procediment-òrgan
			"	) " +
			"and (ntf.grupCodi = null or (ntf.grupCodi in (:grupsProcedimentCodisNotib))) " +
			"and (ntf.entitat = :entitat) " )
	Page<NotificacioTableEntity> findByProcedimentCodiNotibAndGrupsCodiNotibAndEntitat(
			@Param("esProcedimentsCodisNotibNull") boolean esProcedimentsCodisNotibNull,
			@Param("procedimentsCodisNotib") List<? extends String> procedimentsCodisNotib,
			@Param("grupsProcedimentCodisNotib") List<? extends String> grupsProcedimentCodisNotib,
			@Param("esOrgansGestorsCodisNotibNull") boolean esOrgansGestorsCodisNotibNull,
			@Param("organsGestorsCodisNotib") List<? extends String> organsGestorsCodisNotib,
			@Param("esProcedimentOrgansIdsNotibNull") boolean esProcedimentOrgansIdsNotibNull,
			@Param("procedimentOrgansIdsNotib") List<String> procedimentOrgansIdsNotib,
			@Param("entitat") EntitatEntity entitat,
			@Param("usuariCodi") String usuariCodi,
			Pageable paginacio);

	/**
	 * Consulta de la taula de remeses sense filtres per al rol d'administrador d'òrgan gestor
	 *
	 */
	@Query( "select ntf " +
			"from " +
			"    NotificacioTableEntity ntf " +
			"where " +
			"   (" +
			"	(:esProcedimentsCodisNotibNull = false and ntf.procedimentCodiNotib is not null and ntf.procedimentCodiNotib in (:procedimentsCodisNotib))" +
//			"   or (ntf.procedimentCodiNotib is null and ntf.organGestor is not null and ntf.organGestor.codi in (:organs))) " +
			"   or (ntf.organCodi is not null and ntf.organCodi in (:organs))" +
			"	) " +
			"and (ntf.entitat = :entitat) ")
    Page<NotificacioTableEntity> findByProcedimentCodiNotibAndEntitat(
			@Param("esProcedimentsCodisNotibNull") boolean esProcedimentsCodisNotibNull,
			@Param("procedimentsCodisNotib") List<? extends String> procedimentsCodisNotib,
			@Param("entitat") EntitatEntity entitat,
			@Param("organs") List<String> organs,
			Pageable paginacio);

	/**
	 * Consulta de la taula de remeses sense filtres per al rol d'administrador d'entitat
	 *
	 */
	@Query( "select ntf from NotificacioTableEntity ntf where ntf.entitat = (:entitatActual)")
	Page<NotificacioTableEntity> findByEntitatActual(@Param("entitatActual") EntitatEntity entitatActiva, Pageable paginacio);

	/**
	 * Consulta de la taula de remeses sense filtres per al rol superadministrador
	 *
	 */
	@Query("from NotificacioTableEntity ntf where ntf.entitat in (:entitatActiva)")
	Page<NotificacioTableEntity> findByEntitatActiva(@Param("entitatActiva") List<EntitatEntity> entitatActiva, Pageable paginacio);

	/**
	 * Consulta de la taula de remeses per al rol d'usuari
	 */
	@Query(	"select ntf " +
			"from " +
			"     NotificacioTableEntity ntf " +
			"where " +
			"	 (:entitat = ntf.entitat) " +
			"and (:isEntitatIdNull = true or ntf.entitat.id = :entitatId) " +
			"and (" +
			"		(:isProcNull = false and ntf.procedimentCodiNotib is not null and ntf.procedimentCodiNotib in (:procedimentsCodisNotib)) " +			 // Té permís sobre el procediment
			"	or	(:isOrgansGestorsCodisNotibNull = false and ntf.organCodi is not null and " +
			"			(ntf.procedimentCodiNotib is null or (ntf.procedimentIsComu = true and ntf.procedimentRequirePermission = false)) and ntf.organCodi in (:organsGestorsCodisNotib)" +
			"		) " + // Té permís sobre l'òrgan
			"   or 	((ntf.procedimentCodiNotib is null or ntf.procedimentIsComu = true) and ntf.usuariCodi = :usuariCodi) " +								// És una notificaicó sense procediment o un procediment comú, iniciat pel propi usuari
			"   or 	(:esProcedimentOrgansIdsNotibNull = false and ntf.procedimentCodiNotib is not null and " +
			"			CONCAT(ntf.procedimentCodiNotib, '-', ntf.organCodi) in (:procedimentOrgansIdsNotib)" +
			"		) " +	// Procediment comú amb permís de procediment-òrgan
			"	) " +
			"and (ntf.grupCodi = null or (ntf.grupCodi in (:grupsProcedimentCodisNotib))) " +
			"and (:isEnviamentTipusNull = true or ntf.enviamentTipus = :enviamentTipus) " +
			"and (:isConcepteNull = true or lower(ntf.concepte) like concat('%', lower(:concepte), '%')) " +
			"and (:isEstatNull = true or bitand(ntf.estat, :estatMask) <> 0) " +
			"and (:isDataIniciNull = true or ntf.createdDate >= :dataInici) " +
			"and (:isDataFiNull = true or ntf.createdDate <= :dataFi) " +
			"and (:isOrganCodiNull = true or ntf.organCodi = :organCodi) " +
			"and (:isProcedimentNull = true or ntf.procedimentCodi = :procedimentCodi) " +
			"and (:isTitularNull = true or lower(ntf.titular) like concat('%', lower(:titular), '%')) " +
			"and (:isTipusUsuariNull = true or ntf.tipusUsuari = :tipusUsuari) " +
			"and (:isNumExpedientNull = true or ntf.numExpedient = :numExpedient)" +
			"and (:isCreadaPerNull = true or ntf.createdBy.codi = :creadaPer) " +
			"and (:isIdentificadorNull = true or lower(ntf.notificaIds) like concat('%', lower(:identificador), '%'))" +
			"and (:nomesSenseErrors = false or ntf.notificaErrorData is null) " +
			"and (:nomesAmbErrors = false or ntf.notificaErrorData is not null) " +
			"and (:isReferenciaNull = true or lower(ntf.referencia) like concat('%', lower(:referencia), '%'))")
	Page<NotificacioTableEntity> findAmbFiltreAndProcedimentCodiNotibAndGrupsCodiNotib(
			@Param("entitat") EntitatEntity entitat,
			@Param("isEntitatIdNull") boolean isEntitatIdNull,
			@Param("entitatId") Long entitatId,
			@Param("isProcNull") boolean isProcNull,
			@Param("procedimentsCodisNotib") List<String> procedimentsCodisNotib,
			@Param("grupsProcedimentCodisNotib") List<String> grupsProcedimentCodisNotib,
			@Param("isOrgansGestorsCodisNotibNull") boolean isOrgansGestorsCodisNotibNull,
			@Param("organsGestorsCodisNotib") List<? extends String> organsGestorsCodisNotib,
			@Param("esProcedimentOrgansIdsNotibNull") boolean esProcedimentOrgansIdsNotibNull,
			@Param("procedimentOrgansIdsNotib") List<String> procedimentOrgansIdsNotib,
			@Param("isEnviamentTipusNull") boolean isEnviamentTipusNull,
			@Param("enviamentTipus") NotificaEnviamentTipusEnumDto enviamentTipus,
			@Param("isConcepteNull") boolean isConcepteNull,
			@Param("concepte") String concepte,
			@Param("isEstatNull") boolean isEstatNull,
			@Param("estatMask") Integer estatMask,
//			@Param("notificaEstat") EnviamentEstat notificaEstat,
			@Param("isDataIniciNull") boolean isDataIniciNull,
			@Param("dataInici") Date dataInici,
			@Param("isDataFiNull") boolean isDataFiNull,
			@Param("dataFi") Date dataFi,
			@Param("isTitularNull") boolean isTitularNull,
			@Param("titular") String titular,
			@Param("isOrganCodiNull") boolean isOrganGestorNull,
			@Param("organCodi") String organCodi,
			@Param("isProcedimentNull") boolean isProcedimentNull,
			@Param("procedimentCodi") String procedimentCodi,
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
			@Param("nomesSenseErrors") boolean nomesSenseErrors,
			@Param("isReferenciaNull") boolean isReferenciaNull,
			@Param("referencia") String referencia,
			Pageable paginacio);

	/**
	 * Consulta de la taula de remeses pels rols d'administrador d'entitat i superusuari
	 */
	@Query(	"select ntf " +
			"from " +
			"     NotificacioTableEntity ntf " +
			"where " +
			"    (:isEntitatIdNull = true or ntf.entitat.id = :entitatId) " +
			"and (:isEnviamentTipusNull = true or ntf.enviamentTipus = :enviamentTipus) " +
			"and (:isConcepteNull = true or lower(ntf.concepte) like concat('%', lower(:concepte), '%')) " +
			"and (:isEstatNull = true or bitand(ntf.estatMask, :estatMask) <> 0) " +
//			"and (:isEstatNull = true or ntf.estat = :estat  or (" +
//			"    select count(env.id) " +
//			"    from ntf.enviaments env " +
//			"    where env.notificaEstat = :notificaEstat" +
//			"    ) > 0 ) " +
			"and (:isDataIniciNull = true or ntf.createdDate >= :dataInici) " +
			"and (:isDataFiNull = true or ntf.createdDate <= :dataFi) "+
			"and (:isOrganCodiNull = true or ntf.organCodi = :organCodi) " +
			"and (:isProcedimentNull = true or ntf.procedimentCodi = :procedimentCodi) " +
			"and (:isTitularNull = true or lower(ntf.titular) like concat('%', lower(:titular), '%'))" +
//			"and (:isTitularNull = true or (" +
//			"    select count(env.id) " +
//			"    from ntf.enviaments env " +
//			"    where " +
//			"       lower(concat(env.titular.nom, ' ', env.titular.llinatge1)) like concat('%', lower(:titular), '%') " +
//			"    or lower(env.titular.nif) like concat('%', lower(:titular), '%') " +
//			"    ) > 0) " +
			"and (:isTipusUsuariNull = true or ntf.tipusUsuari = :tipusUsuari) " +
			"and (:isNumExpedientNull = true or lower(ntf.numExpedient) like concat('%', lower(:numExpedient), '%')) " +
			"and (:isCreadaPerNull = true or ntf.createdBy.codi = :creadaPer) " +
			"and (:isIdentificadorNull = true or " +
			"		(ntf.id in (select notificacio.id" +
			"				from NotificacioEnviamentEntity env" +
			"				where lower(env.notificaIdentificador) like concat('%', lower(:identificador), '%'))))" +
			"and (:nomesSenseErrors = false or ntf.notificaErrorData is null) " +
			"and (:nomesAmbErrors = false or ntf.notificaErrorData is not null) " +
			"and (:isReferenciaNull = true or lower(ntf.referencia) like '%' || lower(:referencia) || '%')")
	Page<NotificacioTableEntity> findAmbFiltre(
			@Param("isEntitatIdNull") boolean isEntitatIdNull,
			@Param("entitatId") Long entitatId,
			@Param("isEnviamentTipusNull") boolean isEnviamentTipusNull,
			@Param("enviamentTipus") NotificaEnviamentTipusEnumDto enviamentTipus,
			@Param("isConcepteNull") boolean isConcepteNull,
			@Param("concepte") String concepte,
			@Param("isEstatNull") boolean isEstatNull,
			@Param("estatMask") Integer estatMask,
//			@Param("notificaEstat") EnviamentEstat notificaEstat,
			@Param("isDataIniciNull") boolean isDataIniciNull,
			@Param("dataInici") Date dataInici,
			@Param("isDataFiNull") boolean isDataFiNull,
			@Param("dataFi") Date dataFi,
			@Param("isTitularNull") boolean isTitularNull,
			@Param("titular") String titular,
			@Param("isOrganCodiNull") boolean isOrganCodiNull,
			@Param("organCodi") String organCodi,
			@Param("isProcedimentNull") boolean isProcedimentNull,
			@Param("procedimentCodi") String procedimentCodi,
			@Param("isTipusUsuariNull") boolean isTipusUsuariNull,
			@Param("tipusUsuari") TipusUsuariEnumDto tipusUsuari,
			@Param("isNumExpedientNull") boolean isNumExpedientNull,
			@Param("numExpedient") String numExpedient,
			@Param("isCreadaPerNull") boolean isCreadaPerNull,
			@Param("creadaPer") String creadaPer,
			@Param("isIdentificadorNull") boolean isIdentificadorNull,
			@Param("identificador") String identificador,
			@Param("nomesAmbErrors") boolean nomesAmbErrors,
			@Param("nomesSenseErrors") boolean nomesSenseErrors,
			@Param("isReferenciaNull") boolean isReferenciaNull,
			@Param("referencia") String referencia,
			Pageable paginacio);

	/**
	 * Consulta de la taula de remeses per al rol d'administrador d'òrgan gestor
	 *
	 */
	@Query(	"select ntf " +
			"from " +
			"     NotificacioTableEntity ntf " +
			"where " +
			"     (:isEntitatIdNull = true or ntf.entitat.id = :entitatId) " +
			"and (:entitat = ntf.entitat) " +
			"and ((:isProcNull = false and ntf.procedimentCodiNotib is not null and ntf.procedimentCodiNotib in (:procedimentsCodisNotib)) " +
//			"   or (ntf.procedimentCodiNotib is null and ntf.organGestor is not null and ntf.organGestor.codi in (:organs))) " +
			"   or (ntf.organCodi is not null and ntf.organCodi in (:organs))) " +
			"and (:isEnviamentTipusNull = true or ntf.enviamentTipus = :enviamentTipus) " +
			"and (:isConcepteNull = true or lower(ntf.concepte) like concat('%', lower(:concepte), '%')) " +
			"and (:isEstatNull = true or bitand(ntf.estatMask, :estatMask) <> 0) " +
//			"and (:isEstatNull = true or ntf.estat = :estat or (" +
//			"    select count(env.id) " +
//			"    from ntf.enviaments env" +
//			"    where env.notificaEstat = :notificaEstat" +
//			"    ) > 0 ) " +
			"and (:isDataIniciNull = true or ntf.createdDate >= :dataInici) " +
			"and (:isDataFiNull = true or ntf.createdDate <= :dataFi) "+
			"and (:isOrganCodiNull = true or ntf.organCodi = :organCodi) " +
			"and (:isProcedimentCodiNull = true or ntf.procedimentCodi = :procedimentCodi) " +
			"and (:isTitularNull = true or lower(ntf.titular) like concat('%', lower(:titular), '%'))" +
//			"and (:isTitularNull = true or (" +
//			"    select count(env.id) " +
//			"    from ntf.enviaments env " +
//			"    where " +
//			"       lower(concat(env.titular.nom, ' ', env.titular.llinatge1)) like concat('%', lower(:titular), '%') " +
//			"    or lower(env.titular.nif) like concat('%', lower(:titular), '%') " +
//			"    ) > 0) " +
			"and (:isTipusUsuariNull = true or ntf.tipusUsuari = :tipusUsuari) " +
			"and (:isNumExpedientNull = true or ntf.numExpedient = :numExpedient)" +
			"and (:isCreadaPerNull = true or ntf.createdBy.codi = :creadaPer) " +
			"and (:nomesSenseErrors = false or ntf.notificaErrorData is null) " +
			"and (:isIdentificadorNull = true or " +
			"		(ntf.id in (select env.notificacio.id"
			+ "				from NotificacioEnviamentEntity env"
			+ "				where env.notificaIdentificador = :identificador))) " +
			" and (:isReferenciaNull = true or lower(ntf.referencia) like '%' || lower(:referencia) || '%')")
	Page<NotificacioTableEntity> findAmbFiltreAndProcedimentCodiNotib(
			@Param("entitat") EntitatEntity entitat,
			@Param("isEntitatIdNull") boolean isEntitatIdNull,
			@Param("entitatId") Long entitatId,
			@Param("isProcNull") boolean isProcNull,
			@Param("procedimentsCodisNotib") List<String> procedimentsCodisNotib,
			@Param("isEnviamentTipusNull") boolean isEnviamentTipusNull,
			@Param("enviamentTipus") NotificaEnviamentTipusEnumDto enviamentTipus,
			@Param("isConcepteNull") boolean isConcepteNull,
			@Param("concepte") String concepte,
			@Param("isEstatNull") boolean isEstatNull,
			@Param("estatMask") Integer estatMask,
//			@Param("notificaEstat") EnviamentEstat notificaEstat,
			@Param("isDataIniciNull") boolean isDataIniciNull,
			@Param("dataInici") Date dataInici,
			@Param("isDataFiNull") boolean isDataFiNull,
			@Param("dataFi") Date dataFi,
			@Param("isTitularNull") boolean isTitularNull,
			@Param("titular") String titular,
			@Param("isOrganCodiNull") boolean isOrganCodiNull,
			@Param("organCodi") String organCodi,
			@Param("isProcedimentCodiNull") boolean isProcedimentCodiNull,
			@Param("procedimentCodi") String procedimentCodi,
			@Param("isTipusUsuariNull") boolean isTipusUsuariNull,
			@Param("tipusUsuari") TipusUsuariEnumDto tipusUsuar,
			@Param("isNumExpedientNull") boolean isNumExpedientNull,
			@Param("numExpedient") String numExpedient,
			@Param("isCreadaPerNull") boolean isCreadaPerNull,
			@Param("creadaPer") String creadaPer,
			@Param("isIdentificadorNull") boolean isIdentificadorNull,
			@Param("identificador") String identificador,
			@Param("organs") List<String> organs,
			@Param("nomesSenseErrors") boolean nomesSenseErrors,
			@Param("isReferenciaNull") boolean isReferenciaNull,
			@Param("referencia") String referencia,
			Pageable paginacio);



	/**
	 * Consulta de la taula de remeses d
	 */
	@Query(	"select ntf " +
			"from " +
			"     NotificacioTableEntity ntf " +
			"where " +
			"    (ntf.notificacioMassiva = :notificacioMassiva) " +
			"and (:isEntitatIdNull = true or ntf.entitat.id = :entitatId) " +
			"and (:isEnviamentTipusNull = true or ntf.enviamentTipus = :enviamentTipus) " +
			"and (:isConcepteNull = true or lower(ntf.concepte) like concat('%', lower(:concepte), '%')) " +
			"and (:isEstatNull = true or bitand(ntf.estatMask, :estatMask) <> 0) " +
//			"and (:isEstatNull = true or ntf.estat = :estat  or (" +
//			"    select count(env.id) " +
//			"    from ntf.enviaments env " +
//			"    where env.notificaEstat = :notificaEstat" +
//			"    ) > 0 ) " +
			"and (:isDataIniciNull = true or ntf.createdDate >= :dataInici) " +
			"and (:isDataFiNull = true or ntf.createdDate <= :dataFi) "+
			"and (:isOrganCodiNull = true or ntf.organCodi = :organCodi) " +
			"and (:isProcedimentNull = true or ntf.procedimentCodi = :procedimentCodi) " +
			"and (:isTitularNull = true or lower(ntf.titular) like concat('%', lower(:titular), '%'))" +
//			"and (:isTitularNull = true or (" +
//			"    select count(env.id) " +
//			"    from ntf.enviaments env " +
//			"    where " +
//			"       lower(concat(env.titular.nom, ' ', env.titular.llinatge1)) like concat('%', lower(:titular), '%') " +
//			"    or lower(env.titular.nif) like concat('%', lower(:titular), '%') " +
//			"    ) > 0) " +
			"and (:isTipusUsuariNull = true or ntf.tipusUsuari = :tipusUsuari) " +
			"and (:isHasZeronotificaEnviamentIntentNull = true or " +
			"	(:hasZeronotificaEnviamentIntent = true and ntf.registreEnviamentIntent = 0) or " +
			"	(:hasZeronotificaEnviamentIntent = false and ntf.registreEnviamentIntent > 0) " +
			") " +
			"and (:isNumExpedientNull = true or lower(ntf.numExpedient) like concat('%', lower(:numExpedient), '%')) " +
			"and (:isCreadaPerNull = true or ntf.createdBy.codi = :creadaPer) " +
			"and (:isIdentificadorNull = true or " +
			"		(ntf.id in (select notificacio.id" +
			"				from NotificacioEnviamentEntity env" +
			"				where lower(env.notificaIdentificador) like concat('%', lower(:identificador), '%'))))" +
			"and (:nomesSenseErrors = false or ntf.notificaErrorData is null) " +
			"and (:nomesAmbErrors = false or ntf.notificaErrorData is not null)")
	Page<NotificacioTableEntity> findAmbFiltreByNotificacioMassiva(
			@Param("isEntitatIdNull") boolean isEntitatIdNull,
			@Param("entitatId") Long entitatId,
			@Param("notificacioMassiva") NotificacioMassivaEntity notificacioMassiva,
			@Param("isEnviamentTipusNull") boolean isEnviamentTipusNull,
			@Param("enviamentTipus") NotificaEnviamentTipusEnumDto enviamentTipus,
			@Param("isConcepteNull") boolean isConcepteNull,
			@Param("concepte") String concepte,
			@Param("isEstatNull") boolean isEstatNull,
			@Param("estatMask") Integer estatMask,
//			@Param("notificaEstat") EnviamentEstat notificaEstat,
			@Param("isDataIniciNull") boolean isDataIniciNull,
			@Param("dataInici") Date dataInici,
			@Param("isDataFiNull") boolean isDataFiNull,
			@Param("dataFi") Date dataFi,
			@Param("isTitularNull") boolean isTitularNull,
			@Param("titular") String titular,
			@Param("isOrganCodiNull") boolean isOrganCodiNull,
			@Param("organCodi") String organCodi,
			@Param("isProcedimentNull") boolean isProcedimentNull,
			@Param("procedimentCodi") String procedimentCodi,
			@Param("isTipusUsuariNull") boolean isTipusUsuariNull,
			@Param("tipusUsuari") TipusUsuariEnumDto tipusUsuari,
			@Param("isNumExpedientNull") boolean isNumExpedientNull,
			@Param("numExpedient") String numExpedient,
			@Param("isCreadaPerNull") boolean isCreadaPerNull,
			@Param("creadaPer") String creadaPer,
			@Param("isIdentificadorNull") boolean isIdentificadorNull,
			@Param("identificador") String identificador,
			@Param("nomesAmbErrors") boolean nomesAmbErrors,
			@Param("nomesSenseErrors") boolean nomesSenseErrors,
			@Param("isHasZeronotificaEnviamentIntentNull") boolean isHasZeronotificaEnviamentIntentNull,
			@Param("hasZeronotificaEnviamentIntent") Boolean hasZeronotificaEnviamentIntent,
			Pageable paginacio);

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
}
