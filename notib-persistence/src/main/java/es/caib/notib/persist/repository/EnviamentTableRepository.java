package es.caib.notib.persist.repository;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.EnviamentTableEntity;
import es.caib.notib.persist.objectes.FiltreEnviament;
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
public interface EnviamentTableRepository extends JpaRepository<EnviamentTableEntity, Long> {

	@Query("select id from EnviamentTableEntity where notificaReferencia is null")
	List<Long> findIdsSenseReferencia();

	@Modifying
	@Query("update EnviamentTableEntity nte set nte.notificaReferencia =" +
			" (select net.notificaReferencia from NotificacioEnviamentEntity net " +
			"	where net.id = nte.id) where nte.notificaReferencia is null")
	void updateReferenciesNules();

	@Query( "select nenv " +
			"from EnviamentTableEntity nenv " +
			"where (:entitat = nenv.entitat) " +
			"and ((:esProcedimentsCodisNotibNull = false and nenv.procedimentCodiNotib is not null and nenv.procedimentCodiNotib in (:procedimentsCodisNotib))" +	// Té permís sobre el procediment
			"	or (:isOrgansGestorsCodisNotibNull = false and nenv.organCodi is not null and " +
			"			(nenv.procedimentCodiNotib is null or (nenv.procedimentIsComu = true and nenv.procedimentRequirePermission = false)) and nenv.organCodi in (:organsGestorsCodisNotib)" +
			"		) " + // Té permís sobre l'òrgan
			"   or ((nenv.procedimentCodiNotib is null or nenv.procedimentIsComu = true) and nenv.usuariCodi = :usuariCodi)" + // És una notificaicó sense procediment o un procediment comú, iniciat pel propi usuari
			"   or 	(:esProcedimentOrgansIdsNotibNull = false and nenv.procedimentCodiNotib is not null and " +
			"			CONCAT(nenv.procedimentCodiNotib, '-', nenv.organCodi) in (:procedimentOrgansIdsNotib)" +
			"		) " +	// Procediment comú amb permís de procediment-òrgan
			") " +
			"and (nenv.grupCodi = null or (nenv.grupCodi in (:grupsProcedimentCodisNotib)))")
	Page<EnviamentTableEntity> find4UserRole(
			@Param("entitat") EntitatEntity entitat,
			@Param("esProcedimentsCodisNotibNull") boolean esProcedimentsCodisNotibNull,
			@Param("procedimentsCodisNotib") List<String> procedimentsCodisNotib,
			@Param("isOrgansGestorsCodisNotibNull") boolean isOrgansGestorsCodisNotibNull,
			@Param("organsGestorsCodisNotib") List<String> organsGestorsCodisNotib,
			@Param("esProcedimentOrgansIdsNotibNull") boolean esProcedimentOrgansIdsNotibNull,
			@Param("procedimentOrgansIdsNotib") List<String> procedimentOrgansIdsNotib,
			@Param("grupsProcedimentCodisNotib") List<String> grupsProcedimentCodisNotib,
			@Param("usuariCodi") String usuariCodi,
			Pageable pageable);

	@Query( "select" +
			"	nenv " +
			"from" +
			"    EnviamentTableEntity nenv " +
			"where " +
			"    (:entitat = nenv.entitat) " +
			"and (:esDataEnviamentFiNull = true or nenv.createdDate <= :dataEnviamentFi) " +
			"and (:esDataEnviamentIniciNull = true or nenv.createdDate >= :dataEnviamentInici) " +
			"and (:isCodiProcedimentNull = true or lower(nenv.procedimentCodiNotib) like lower('%'||:codiProcediment||'%')) " +
			"and (:isGrupNull = true or lower(nenv.grupCodi) like lower('%'||:grup||'%')) " +
			"and (:isConcepteNull = true or lower(nenv.concepte) like lower('%'||:concepte||'%')) " +
			"and (:isDescripcioNull = true or lower(nenv.descripcio) like lower('%'||:descripcio||'%')) " +
			"and (:isDataProgramadaDisposicioIniciNull = true or nenv.enviamentDataProgramada >= :dataProgramadaDisposicioInici) " +
			"and (:isDataProgramadaDisposicioFiNull = true or nenv.enviamentDataProgramada <= :dataProgramadaDisposicioFi) " +
			"and (:isDataCaducitatIniciNull = true or nenv.notificaDataCaducitat >= :dataCaducitatInici) " +
			"and (:isDataCaducitatFiNull = true or nenv.notificaDataCaducitat <= :dataCaducitatFi) " +
			"and (:isTipusEnviamentNull = true or nenv.tipusEnviament = :tipusEnviament) " +
			"and (:isCsvNull = true or lower(nenv.csv_uuid) like lower('%'||:csv||'%')) " +
			"and (:isEstatNull = true or nenv.estat = :estat or nenv.notificaEstat = :notificaEstat)" +
			"and (:esCodiNotificaNull = true or lower(nenv.notificaIdentificador) like lower('%'||:codiNotifica||'%')) " +
			"and (:esCreatedbyNull = true or lower(CASE WHEN nenv.createdBy.codi is null THEN '' ELSE nenv.createdBy.codi END) like lower('%'||:createdByCodi||'%')) " +
			"and (:esNifTitularNull = true or lower(nenv.titularNif) like lower('%'||:nifTitular||'%')) " +
			"and (:esNomTitularNull = true or lower(concat('[', nenv.titularLlinatge1, ' ', nenv.titularLlinatge2, ', ', nenv.titularNom,']')) like lower('%'||:nomTitular||'%')) " +
			"and (:esEmailTitularNull = true or nenv.titularEmail = :emailTitular) " +
			"and (:esDir3CodiNull = true or lower(nenv.organCodi) like lower('%'||:dir3Codi||'%')) " +
			"and (:isNumeroCertCorreusNull = true or nenv.notificaCertificacioNumSeguiment like lower('%'||:numeroCertCorreus||'%')) " +
			"and (:isUsuariNull = true or nenv.usuariCodi like lower('%'||:usuari||'%')) "+
			"and (:isNumeroRegistreNull = true or cast(nenv.registreNumero as string) like lower('%'||:numeroRegistre||'%')) " +
			"and (:isNotificaReferenciaNull = true or nenv.notificaReferencia like '%'||:notificaReferencia||'%') "+
			"and (:esDataRegistreIniciNull = true or nenv.registreData >= :dataRegistreInici) " +
			"and (:esDataRegistreFiNull = true or nenv.registreData <= :dataRegistreFi) " +
			"and ((:esProcedimentsCodisNotibNull = false and nenv.procedimentCodiNotib is not null and nenv.procedimentCodiNotib in (:procedimentsCodisNotib))" +	// Té permís sobre el procediment
			"	or (:isOrgansGestorsCodisNotibNull = false and nenv.organCodi is not null and " +
			"			(nenv.procedimentCodiNotib is null or (nenv.procedimentIsComu = true and nenv.procedimentRequirePermission = false)) and nenv.organCodi in (:organsGestorsCodisNotib)" +
			"		) " + // Té permís sobre l'òrgan
			"   or ((nenv.procedimentCodiNotib is null or nenv.procedimentIsComu = true) and nenv.usuariCodi = :usuariCodi)" + // És una notificaicó sense procediment o un procediment comú, iniciat pel propi usuari
			"   or 	(:esProcedimentOrgansIdsNotibNull = false and nenv.procedimentCodiNotib is not null and " +
			"			CONCAT(nenv.procedimentCodiNotib, '-', nenv.organCodi) in (:procedimentOrgansIdsNotib)" +
			"		) " +	// Procediment comú amb permís de procediment-òrgan
			") " +
			"and (nenv.grupCodi = null or (nenv.grupCodi in (:grupsProcedimentCodisNotib))) " +
			"and (:isHasZeronotificaEnviamentIntentNull = true or " +
			"	(:hasZeronotificaEnviamentIntent = true and nenv.registreEnviamentIntent = 0) or " +
			"	(:hasZeronotificaEnviamentIntent = false and nenv.registreEnviamentIntent > 0) " +
			") " +
			"and (:isReferenciaNotificacioNull = true or lower(nenv.notificacio.referencia) like '%'||lower(:referenciaNotificacio)||'%') " +
			"and (:nomesSenseErrors = false or nenv.hasErrors = true) " +
			"and (:nomesAmbErrors = false or nenv.hasErrors = false)"
	)
	Page<EnviamentTableEntity> find4UserRole(
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
			@Param("tipusEnviament") EnviamentTipus tipusEnviament,
			@Param("isCsvNull") boolean isCsvNull,
			@Param("csv") String csv,
			@Param("isEstatNull") boolean isEstatNull,
			@Param("estat") NotificacioEstatEnumDto estat,
			@Param("notificaEstat") EnviamentEstat notificaEstat,
			@Param("entitat") EntitatEntity entitat,
			@Param("esDataEnviamentIniciNull") boolean esDataEnviamentIniciNull,
			@Param("dataEnviamentInici") Date dataEnviamentInici,
			@Param("esDataEnviamentFiNull") boolean esDataEnviamentFiNull,
			@Param("dataEnviamentFi") Date dataEnviamentFi,
			@Param("esCodiNotificaNull") boolean esCodiNotificaNull,
			@Param("codiNotifica") String codiNotifica,
			@Param("esCreatedbyNull") boolean esCreatedbyNull,
			@Param("createdByCodi") String createdByCodi,
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
			@Param("isNotificaReferenciaNull") boolean isNotificaReferenciaNull,
			@Param("notificaReferencia") String notificaReferencia,
			@Param("esDataRegistreIniciNull") boolean esDataRegistreIniciNull,
			@Param("dataRegistreInici") Date dataRegistreInici,
			@Param("esDataRegistreFiNull") boolean esDataRegistreFiNull,
			@Param("dataRegistreFi") Date dataRegistreFi,
			@Param("esProcedimentsCodisNotibNull") boolean esProcedimentsCodisNotibNull,
			@Param("procedimentsCodisNotib") List<String> procedimentsCodisNotib,
			@Param("isOrgansGestorsCodisNotibNull") boolean isOrgansGestorsCodisNotibNull,
			@Param("organsGestorsCodisNotib") List<String> organsGestorsCodisNotib,
			@Param("esProcedimentOrgansIdsNotibNull") boolean esProcedimentOrgansIdsNotibNull,
			@Param("procedimentOrgansIdsNotib") List<String> procedimentOrgansIdsNotib,
			@Param("grupsProcedimentCodisNotib") List<String> grupsProcedimentCodisNotib,
			@Param("usuariCodi") String usuariCodi,
			@Param("nomesAmbErrors") boolean nomesAmbErrors,
			@Param("nomesSenseErrors") boolean nomesSenseErrors,
			@Param("isHasZeronotificaEnviamentIntentNull") boolean isHasZeronotificaEnviamentIntentNull,
			@Param("hasZeronotificaEnviamentIntent") Boolean hasZeronotificaEnviamentIntent,
			@Param("isReferenciaNotificacioNull") boolean isReferenciaNotificacioNull,
			@Param("referenciaNotificacio") String referenciaNotificacio,
			Pageable pageable);

	@Query( "from EnviamentTableEntity nenv " +
			"where (:entitat = nenv.entitat) " +
			"  and (nenv.organCodi is not null and nenv.organCodi in (:organs))")
	Page<EnviamentTableEntity> find4OrganAdminRole(@Param("entitat") EntitatEntity entitat, @Param("organs") List<String> organs, Pageable pageable);

	@Query( "from" +
			"    EnviamentTableEntity nenv " +
			"where " +
			"    (:entitat = nenv.entitat) " +
			"and (:esDataEnviamentFiNull = true or nenv.createdDate <= :dataEnviamentFi) " +
			"and (:esDataEnviamentIniciNull = true or nenv.createdDate >= :dataEnviamentInici) " +
			"and (:isCodiProcedimentNull = true or lower(nenv.procedimentCodiNotib) like lower('%'||:codiProcediment||'%')) " +
			"and (:isGrupNull = true or lower(nenv.grupCodi) like lower('%'||:grup||'%')) " +
			"and (:isConcepteNull = true or lower(nenv.concepte) like lower('%'||:concepte||'%')) " +
			"and (:isDescripcioNull = true or lower(nenv.descripcio) like lower('%'||:descripcio||'%')) " +
			"and (:isDataProgramadaDisposicioIniciNull = true or nenv.enviamentDataProgramada >= :dataProgramadaDisposicioInici) " +
			"and (:isDataProgramadaDisposicioFiNull = true or nenv.enviamentDataProgramada <= :dataProgramadaDisposicioFi) " +
			"and (:isDataCaducitatIniciNull = true or nenv.notificaDataCaducitat >= :dataCaducitatInici) " +
			"and (:isDataCaducitatFiNull = true or nenv.notificaDataCaducitat <= :dataCaducitatFi) " +
			"and (:isTipusEnviamentNull = true or nenv.tipusEnviament = :tipusEnviament) " +
			"and (:isCsvNull = true or lower(nenv.csv_uuid) like lower('%'||:csv||'%')) " +
			"and (:isEstatNull = true or nenv.estat = :estat or nenv.notificaEstat = :notificaEstat)" +
			"and (:esCodiNotificaNull = true or lower(nenv.notificaIdentificador) like lower('%'||:codiNotifica||'%')) " +
			"and (:esCreatedbyNull = true or lower(CASE WHEN nenv.createdBy.codi is null THEN '' ELSE nenv.createdBy.codi END) like lower('%'||:createdByCodi||'%')) " +
			"and (:esNifTitularNull = true or lower(nenv.titularNif) like lower('%'||:nifTitular||'%')) " +
			"and (:esNomTitularNull = true or lower(concat('[', nenv.titularLlinatge1, ' ', nenv.titularLlinatge2, ', ', nenv.titularNom,']')) like lower('%'||:nomTitular||'%')) " +
			"and (:esEmailTitularNull = true or nenv.titularEmail = :emailTitular) " +
			"and (:esDir3CodiNull = true or lower(nenv.organCodi) like lower('%'||:dir3Codi||'%')) " +
			"and (:isNumeroCertCorreusNull = true or nenv.notificaCertificacioNumSeguiment like lower('%'||:numeroCertCorreus||'%')) " +
			"and (:isUsuariNull = true or nenv.usuariCodi like lower('%'||:usuari||'%')) " +
			"and (:isNumeroRegistreNull = true or cast(nenv.registreNumero as string) like lower('%'||:numeroRegistre||'%')) " +
			"and (:isNotificaReferenciaNull = true or nenv.notificaReferencia like '%'||:notificaReferencia||'%') " +
			"and (:esDataRegistreIniciNull = true or nenv.registreData >= :dataRegistreInici) " +
			"and (:esDataRegistreFiNull = true or nenv.registreData <= :dataRegistreFi) " +
			"and (nenv.organCodi is not null and nenv.organCodi in (:organs)) " +
			"and (:isHasZeronotificaEnviamentIntentNull = true or " +
			"	(:hasZeronotificaEnviamentIntent = true and nenv.registreEnviamentIntent = 0) or " +
			"	(:hasZeronotificaEnviamentIntent = false and nenv.registreEnviamentIntent > 0) " +
			") " +
			"and (:isReferenciaNotificacioNull = true or lower(nenv.notificacio.referencia) like '%'||lower(:referenciaNotificacio)||'%') " +
			"and (:nomesSenseErrors = false or nenv.hasErrors = true) " +
			"and (:nomesAmbErrors = false or nenv.hasErrors = false)"
	)
	Page<EnviamentTableEntity> find4OrganAdminRole(
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
			@Param("tipusEnviament") EnviamentTipus tipusEnviament,
			@Param("isCsvNull") boolean isCsvNull,
			@Param("csv") String csv,
			@Param("isEstatNull") boolean isEstatNull,
			@Param("estat") NotificacioEstatEnumDto estat,
			@Param("notificaEstat") EnviamentEstat notificaEstat,
			@Param("entitat") EntitatEntity entitat,
			@Param("esDataEnviamentIniciNull") boolean esDataEnviamentIniciNull,
			@Param("dataEnviamentInici") Date dataEnviamentInici,
			@Param("esDataEnviamentFiNull") boolean esDataEnviamentFiNull,
			@Param("dataEnviamentFi") Date dataEnviamentFi,
			@Param("esCodiNotificaNull") boolean esCodiNotificaNull,
			@Param("codiNotifica") String codiNotifica,
			@Param("esCreatedbyNull") boolean esCreatedbyNull,
			@Param("createdByCodi") String createdByCodi,
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
			@Param("isNotificaReferenciaNull") boolean isNotificaReferenciaNull,
			@Param("notificaReferencia") String notificaReferencia,
			@Param("esDataRegistreIniciNull") boolean esDataRegistreIniciNull,
			@Param("dataRegistreInici") Date dataRegistreInici,
			@Param("esDataRegistreFiNull") boolean esDataRegistreFiNull,
			@Param("dataRegistreFi") Date dataRegistreFi,
			@Param("nomesAmbErrors") boolean nomesAmbErrors,
			@Param("nomesSenseErrors") boolean nomesSenseErrors,
			@Param("isHasZeronotificaEnviamentIntentNull") boolean isHasZeronotificaEnviamentIntentNull,
			@Param("hasZeronotificaEnviamentIntent") Boolean hasZeronotificaEnviamentIntent,
			@Param("isReferenciaNotificacioNull") boolean isReferenciaNotificacioNull,
			@Param("referenciaNotificacio") String referenciaNotificacio,
			@Param("organs") List<String> organs,
			Pageable pageable);

	@Query( "from EnviamentTableEntity nenv where (:entitat = nenv.entitat)")
	Page<EnviamentTableEntity> find4EntitatAdminRole(@Param("entitat") EntitatEntity entitat, Pageable pageable);

	@Query( "from " +
			"    EnviamentTableEntity nenv " +
			"where " +
			"    (:entitat = nenv.entitat) " +
			"and (:esDataEnviamentIniciNull = true or nenv.createdDate >= :dataEnviamentInici) " +
			"and (:isCodiProcedimentNull = true or lower(CASE WHEN nenv.procedimentCodiNotib is null THEN '' ELSE nenv.procedimentCodiNotib END) like lower('%'||:codiProcediment||'%')) " +
			"and (:isGrupNull = true or lower(CASE WHEN nenv.grupCodi is null THEN '' ELSE nenv.grupCodi END) like lower('%'||:grup||'%')) " +
			"and (:isConcepteNull = true or lower(nenv.concepte) like lower('%'||:concepte||'%')) " +
			"and (:isDescripcioNull = true or lower(nenv.descripcio) like lower('%'||:descripcio||'%')) " +
			"and (:isDataProgramadaDisposicioIniciNull = true or nenv.enviamentDataProgramada >= :dataProgramadaDisposicioInici) " +
			"and (:isDataProgramadaDisposicioFiNull = true or nenv.enviamentDataProgramada <= :dataProgramadaDisposicioFi) " +
			"and (:isDataCaducitatIniciNull = true or nenv.notificaDataCaducitat >= :dataCaducitatInici) " +
			"and (:isDataCaducitatFiNull = true or nenv.notificaDataCaducitat <= :dataCaducitatFi) " +
			"and (:isTipusEnviamentNull = true or nenv.tipusEnviament = :tipusEnviament) " +
			"and (:isCsvNull = true or lower(CASE WHEN nenv.csv_uuid is null THEN '' ELSE nenv.csv_uuid END) like lower('%'||:csv||'%')) " +
			"and (:isEstatNull = true or nenv.estat = :estat or nenv.notificaEstat = :notificaEstat)" +
			"and (:esDataEnviamentFiNull = true or nenv.createdDate <= :dataEnviamentFi) " +
			"and (:esCodiNotificaNull = true or lower(CASE WHEN nenv.notificaIdentificador is null THEN '' ELSE nenv.notificaIdentificador END) like lower('%'||:codiNotifica||'%')) " +
			"and (:esCreatedbyNull = true or lower(CASE WHEN nenv.createdBy.codi is null THEN '' ELSE nenv.createdBy.codi END) like lower('%'||:createdByCodi||'%')) " +
			"and (:esNifTitularNull = true or lower(CASE WHEN nenv.titularNif is null THEN '' ELSE nenv.titularNif END) like lower('%'||:nifTitular||'%')) " +
			"and (:esNomTitularNull = true or lower(concat('[', nenv.titularLlinatge1, ' ', nenv.titularLlinatge2, ', ', nenv.titularNom,']')) like lower('%'||:nomTitular||'%')) " +
			"and (:esEmailTitularNull = true or nenv.titularEmail = :emailTitular) " +
			"and (:esDir3CodiNull = true or lower(CASE WHEN nenv.organCodi is null THEN '' ELSE nenv.organCodi END) like lower('%'||:dir3Codi||'%')) " +
			"and (:isNumeroCertCorreusNull = true or nenv.notificaCertificacioNumSeguiment like lower('%'||:numeroCertCorreus||'%')) " +
			"and (:isUsuariNull = true or nenv.usuariCodi like lower('%'||:usuari||'%')) " +
			"and (:isNumeroRegistreNull = true or cast(nenv.registreNumero as string) like lower('%'||:numeroRegistre||'%')) " +
			"and (:isNotificaReferenciaNull = true or nenv.notificaReferencia like '%'||:notificaReferencia||'%') " +
			"and (:esDataRegistreIniciNull = true or nenv.registreData >= :dataRegistreInici) " +
			"and (:esDataRegistreFiNull = true or nenv.registreData <= :dataRegistreFi)" +
			"and (:isHasZeronotificaEnviamentIntentNull = true or " +
			"	(:hasZeronotificaEnviamentIntent = true and nenv.registreEnviamentIntent = 0) or " +
			"	(:hasZeronotificaEnviamentIntent = false and nenv.registreEnviamentIntent > 0) " +
			") " +
			"and (:nomesSenseErrors = false or nenv.hasErrors = true) " +
			"and (:isReferenciaNotificacioNull = true or lower(nenv.notificacio.referencia) like '%'||lower(:referenciaNotificacio)||'%') " +
			"and (:nomesAmbErrors = false or nenv.hasErrors = false)"
	)
	Page<EnviamentTableEntity> find4EntitatAdminRole(
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
			@Param("tipusEnviament") EnviamentTipus tipusEnviament,
			@Param("isCsvNull") boolean isCsvNull,
			@Param("csv") String csv,
			@Param("isEstatNull") boolean isEstatNull,
			@Param("estat") NotificacioEstatEnumDto estat,
			@Param("notificaEstat") EnviamentEstat notificaEstat,
			@Param("entitat") EntitatEntity entitat,
			@Param("esDataEnviamentIniciNull") boolean esDataEnviamentIniciNull,
			@Param("dataEnviamentInici") Date dataEnviamentInici,
			@Param("esDataEnviamentFiNull") boolean esDataEnviamentFiNull,
			@Param("dataEnviamentFi") Date dataEnviamentFi,
			@Param("esCodiNotificaNull") boolean esCodiNotificaNull,
			@Param("codiNotifica") String codiNotifica,
			@Param("esCreatedbyNull") boolean esCreatedbyNull,
			@Param("createdByCodi") String createdByCodi,
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
			@Param("isNotificaReferenciaNull") boolean isNotificaReferenciaNull,
			@Param("notificaReferencia") String notificaReferencia,
			@Param("esDataRegistreIniciNull") boolean esDataRegistreIniciNull,
			@Param("dataRegistreInici") Date dataRegistreInici,
			@Param("esDataRegistreFiNull") boolean esDataRegistreFiNull,
			@Param("dataRegistreFi") Date dataRegistreFi,
			@Param("nomesAmbErrors") boolean nomesAmbErrors,
			@Param("nomesSenseErrors") boolean nomesSenseErrors,
			@Param("isHasZeronotificaEnviamentIntentNull") boolean isHasZeronotificaEnviamentIntentNull,
			@Param("hasZeronotificaEnviamentIntent") Boolean hasZeronotificaEnviamentIntent,
			@Param("isReferenciaNotificacioNull") boolean isReferenciaNotificacioNull,
			@Param("referenciaNotificacio") String referenciaNotificacio,
			Pageable pageable);
	
//	@Modifying
//	@Query("update EnviamentTableEntity et " +
//			"set et.organEstat = (SELECT og.estat from OrganGestorEntity og where og.codi = et.organCodi) " +
//			"where et.organCodi is not null")
//	void updateOrganGestorEstat();
//
//	@Modifying
//	@Query("update EnviamentTableEntity et " +
//			"set et.organEstat = :estat " +
//			"where et.organCodi in :organs")
//	void updateOrganGestorEstat(@Param("estat") OrganGestorEstatEnum estat, @Param("organs") List<String> organs);

	@Modifying
	@Query("update EnviamentTableEntity nt " +
			"set " +
			" nt.procedimentIsComu = :procedimentComu, " +
			" nt.procedimentRequirePermission = :procedimentRequirePermission " +
			"where nt.procedimentCodiNotib = :procedimentCodi ")
	void updateProcediment(@Param("procedimentComu") boolean procedimentComu,
						   @Param("procedimentRequirePermission") boolean procedimentRequireDirectPermission,
						   @Param("procedimentCodi") String procedimentCodi);

	@Modifying
	@Query("update EnviamentTableEntity nt " +
			"set " +
			" nt.notificaReferencia = :notificaReferencia " +
			"where nt.id = :enviamentId ")
	void updateNotificaReferencia(@Param("notificaReferencia") String notificaReferencia,
								  @Param("enviamentId") Long procedimentCodi);


	@Modifying
	@Query("update EnviamentTableEntity nt set nt.organEstat = :estat where nt.organCodi = :organCodi")
	void updateOrganEstat(@Param("organCodi") String organCodi, @Param("estat") OrganGestorEstatEnum estat);

	@Query( "select nenv from EnviamentTableEntity nenv " +
			"where " +
			"    (:#{#filtre.entitat} = nenv.entitat) " +
			"and (:#{#filtre.isUsuari} = false or (" +
			// PERMISOS
			// Iniciada pel propi usuari
			"	nenv.usuariCodi = :#{#filtre.usuariCodi} " +
			// Té permís consulta sobre el procediment
			"	or (:#{#filtre.procedimentsCodisNotibNull} = false and nenv.procedimentCodiNotib is not null " +
			"			and nenv.procedimentCodiNotib in (:#{#filtre.procedimentsCodisNotib}) and nenv.procedimentIsComu = false) " +
			// Té permís consulta sobre l'òrgan
			"	or (:#{#filtre.organsGestorsCodisNotibNull} = false and nenv.organCodi is not null " +
			"			and (nenv.procedimentIsComu = false or nenv.procedimentRequirePermission = false) " +
			"			and nenv.organCodi in (:#{#filtre.organsGestorsCodisNotib})) " +
			// Procediment comú amb permís comú sobre l'òrgan
			"	or (:#{#filtre.organsGestorsComunsCodisNotibNull} = false and nenv.organCodi is not null " +
			"			and nenv.procedimentIsComu = true and nenv.procedimentRequirePermission = false " +
			"			and nenv.organCodi in (:#{#filtre.organsGestorsComunsCodisNotib})) " +
			// Procediment comú amb permís de procediment-òrgan
			"   or (:#{#filtre.procedimentOrgansAmbPermisNull} = false and nenv.procedimentCodiNotib is not null " +
			"			and CONCAT(nenv.procedimentCodiNotib, '-', nenv.organCodi) in (:#{#filtre.procedimentOrgansAmbPermis})" +
			"		) " +
//			"(:esProcedimentsCodisNotibNull = false and nenv.procedimentCodiNotib is not null and nenv.procedimentCodiNotib in (:procedimentsCodisNotib))" +	// Té permís sobre el procediment
//			"	or (:esOrgansGestorsCodisNotibNull = false and nenv.organCodi is not null and " +
//			"			(nenv.procedimentCodiNotib is null or (nenv.procedimentIsComu = true and nenv.procedimentRequirePermission = false)) and nenv.organCodi in (:organsGestorsCodisNotib)" +
//			"		) " + // Té permís sobre l'òrgan
//			"   or ((nenv.procedimentCodiNotib is null or nenv.procedimentIsComu = true) and nenv.usuariCodi = :usuariCodi)" + // És una notificaicó sense procediment o un procediment comú, iniciat pel propi usuari
//			"   or 	(:esProcedimentOrgansIdsNotibNull = false and nenv.procedimentCodiNotib is not null and " +
//			"			CONCAT(nenv.procedimentCodiNotib, '-', nenv.organCodi) in (:procedimentOrgansIdsNotib)" +
//			"		) " +	// Procediment comú amb permís de procediment-òrgan
			") " +
			"and (nenv.grupCodi = null or (nenv.grupCodi in (:#{#filtre.rols}))) " +
			") " +
			"and (:#{#filtre.isAdminOrgan} = false or (nenv.organCodi is not null and nenv.organCodi in (:#{#filtre.organs})))" +
			"and (:#{#filtre.dataEnviamentIniciNull} = true or nenv.createdDate >= :#{#filtre.dataEnviamentInici}) " +
			"and (:#{#filtre.codiProcedimentNull} = true or lower(CASE WHEN nenv.procedimentCodiNotib is null THEN '' ELSE nenv.procedimentCodiNotib END) like lower('%'||:#{#filtre.codiProcediment}||'%')) " +
			"and (:#{#filtre.grupNull} = true or lower(CASE WHEN nenv.grupCodi is null THEN '' ELSE nenv.grupCodi END) like lower('%'||:#{#filtre.grup}||'%')) " +
			"and (:#{#filtre.concepteNull} = true or lower(nenv.concepte) like lower('%'||:#{#filtre.concepte}||'%')) " +
			"and (:#{#filtre.descripcioNull} = true or lower(nenv.descripcio) like lower('%'||:#{#filtre.descripcio}||'%')) " +
			"and (:#{#filtre.dataProgramadaDisposicioIniciNull} = true or nenv.enviamentDataProgramada >= :#{#filtre.dataProgramadaDisposicioInici}) " +
			"and (:#{#filtre.dataProgramadaDisposicioFiNull} = true or nenv.enviamentDataProgramada <= :#{#filtre.dataProgramadaDisposicioFi}) " +
			"and (:#{#filtre.dataCaducitatIniciNull} = true or nenv.notificaDataCaducitat >= :#{#filtre.dataCaducitatInici}) " +
			"and (:#{#filtre.dataCaducitatFiNull} = true or nenv.notificaDataCaducitat <= :#{#filtre.dataCaducitatFi}) " +
			"and (:#{#filtre.enviamentTipusNull} = true or nenv.tipusEnviament = :#{#filtre.enviamentTipus}) " +
			"and (:#{#filtre.csvUuidNull} = true or lower(CASE WHEN nenv.csv_uuid is null THEN '' ELSE nenv.csv_uuid END) like lower('%'||:#{#filtre.csvUuid}||'%')) " +
			"and (:#{#filtre.estatNull} = true or nenv.estat = :#{#filtre.estat} or nenv.notificaEstat = :#{#filtre.notificaEstat})" +
			"and (:#{#filtre.dataEnviamentFiNull} = true or nenv.createdDate <= :#{#filtre.dataEnviamentFi}) " +
			"and (:#{#filtre.codiNotificaNull} = true or lower(CASE WHEN nenv.notificaIdentificador is null THEN '' ELSE nenv.notificaIdentificador END) like lower('%'||:#{#filtre.codiNotifica}||'%')) " +
			"and (:#{#filtre.creadaPerNull} = true or lower(CASE WHEN nenv.createdBy.codi is null THEN '' ELSE nenv.createdBy.codi END) like lower('%'||:#{#filtre.creadaPerCodi}||'%')) " +
			"and (:#{#filtre.nifTitularNull} = true or lower(CASE WHEN nenv.titularNif is null THEN '' ELSE nenv.titularNif END) like lower('%'||:#{#filtre.nifTitular}||'%')) " +
			"and (:#{#filtre.nomTitularNull} = true or lower(concat('[', nenv.titularLlinatge1, ' ', nenv.titularLlinatge2, ', ', nenv.titularNom,']')) like lower('%'||:#{#filtre.nomTitular}||'%')) " +
			"and (:#{#filtre.emailTitularNull} = true or nenv.titularEmail = :#{#filtre.emailTitular}) " +
			"and (:#{#filtre.dir3CodiNull} = true or lower(CASE WHEN nenv.organCodi is null THEN '' ELSE nenv.organCodi END) like lower('%'||:#{#filtre.dir3Codi}||'%')) " +
			"and (:#{#filtre.numeroCertCorreusNull} = true or nenv.notificaCertificacioNumSeguiment like lower('%'||:#{#filtre.numeroCertCorreus}||'%')) " +
			"and (:#{#filtre.usuariNull} = true or nenv.usuariCodi like lower('%'||:#{#filtre.usuari}||'%')) " +
			"and (:#{#filtre.registreNumeroNull} = true or cast(nenv.registreNumero as string) like lower('%'||:#{#filtre.registreNumero}||'%')) " +
			"and (:#{#filtre.codiNotibEnviamentNull} = true or nenv.notificaReferencia like '%'||:#{#filtre.codiNotibEnviament}||'%') " +
			"and (:#{#filtre.dataRegistreIniciNull} = true or nenv.registreData >= :#{#filtre.dataRegistreInici}) " +
			"and (:#{#filtre.dataRegistreFiNull} = true or nenv.registreData <= :#{#filtre.dataRegistreFi})" +
			"and (:#{#filtre.hasZeronotificaEnviamentIntentNull} = true or " +
			"	(:#{#filtre.hasZeronotificaEnviamentIntent} = true and nenv.registreEnviamentIntent = 0) or " +
			"	(:#{#filtre.hasZeronotificaEnviamentIntent} = false and nenv.registreEnviamentIntent > 0) " +
			") " +
			"and (:#{#filtre.nomesSenseErrors} = false or nenv.hasErrors = true) " +
			"and (:#{#filtre.referenciaNotificacioNull} = true or lower(nenv.notificacio.referencia) like '%'||lower(:#{#filtre.referenciaNotificacio})||'%') " +
			"and (:#{#filtre.nomesAmbErrors} = false or nenv.hasErrors = false)"
	)
	Page<EnviamentTableEntity> findAmbFiltre(FiltreEnviament filtre, Pageable pageable);


	@Query( "select nenv.id from EnviamentTableEntity nenv " +
			"where " +
			"    (:#{#filtre.entitat} = nenv.entitat) " +
			"and (:#{#filtre.isUsuari} = false or (" +
			// PERMISOS
			// Iniciada pel propi usuari
			"	nenv.usuariCodi = :#{#filtre.usuariCodi} " +
			// Té permís consulta sobre el procediment
			"	or (:#{#filtre.procedimentsCodisNotibNull} = false and nenv.procedimentCodiNotib is not null " +
			"			and nenv.procedimentCodiNotib in (:#{#filtre.procedimentsCodisNotib}) and nenv.procedimentIsComu = false) " +
			// Té permís consulta sobre l'òrgan
			"	or (:#{#filtre.organsGestorsCodisNotibNull} = false and nenv.organCodi is not null " +
			"			and (nenv.procedimentIsComu = false or nenv.procedimentRequirePermission = false) " +
			"			and nenv.organCodi in (:#{#filtre.organsGestorsCodisNotib})) " +
			// Procediment comú amb permís comú sobre l'òrgan
			"	or (:#{#filtre.organsGestorsComunsCodisNotibNull} = false and nenv.organCodi is not null " +
			"			and nenv.procedimentIsComu = true and nenv.procedimentRequirePermission = false " +
			"			and nenv.organCodi in (:#{#filtre.organsGestorsComunsCodisNotib})) " +
			// Procediment comú amb permís de procediment-òrgan
			"   or (:#{#filtre.procedimentOrgansAmbPermisNull} = false and nenv.procedimentCodiNotib is not null " +
			"			and CONCAT(nenv.procedimentCodiNotib, '-', nenv.organCodi) in (:#{#filtre.procedimentOrgansAmbPermis})" +
			"		) " +
//			"(:esProcedimentsCodisNotibNull = false and nenv.procedimentCodiNotib is not null and nenv.procedimentCodiNotib in (:procedimentsCodisNotib))" +	// Té permís sobre el procediment
//			"	or (:esOrgansGestorsCodisNotibNull = false and nenv.organCodi is not null and " +
//			"			(nenv.procedimentCodiNotib is null or (nenv.procedimentIsComu = true and nenv.procedimentRequirePermission = false)) and nenv.organCodi in (:organsGestorsCodisNotib)" +
//			"		) " + // Té permís sobre l'òrgan
//			"   or ((nenv.procedimentCodiNotib is null or nenv.procedimentIsComu = true) and nenv.usuariCodi = :usuariCodi)" + // És una notificaicó sense procediment o un procediment comú, iniciat pel propi usuari
//			"   or 	(:esProcedimentOrgansIdsNotibNull = false and nenv.procedimentCodiNotib is not null and " +
//			"			CONCAT(nenv.procedimentCodiNotib, '-', nenv.organCodi) in (:procedimentOrgansIdsNotib)" +
//			"		) " +	// Procediment comú amb permís de procediment-òrgan
			") " +
			"and (nenv.grupCodi = null or (nenv.grupCodi in (:#{#filtre.rols}))) " +
			") " +
			"and (:#{#filtre.isAdminOrgan} = false or (nenv.organCodi is not null and nenv.organCodi in (:#{#filtre.organs})))" +
			"and (:#{#filtre.dataEnviamentIniciNull} = true or nenv.createdDate >= :#{#filtre.dataEnviamentInici}) " +
			"and (:#{#filtre.codiProcedimentNull} = true or lower(CASE WHEN nenv.procedimentCodiNotib is null THEN '' ELSE nenv.procedimentCodiNotib END) like lower('%'||:#{#filtre.codiProcediment}||'%')) " +
			"and (:#{#filtre.grupNull} = true or lower(CASE WHEN nenv.grupCodi is null THEN '' ELSE nenv.grupCodi END) like lower('%'||:#{#filtre.grup}||'%')) " +
			"and (:#{#filtre.concepteNull} = true or lower(nenv.concepte) like lower('%'||:#{#filtre.concepte}||'%')) " +
			"and (:#{#filtre.descripcioNull} = true or lower(nenv.descripcio) like lower('%'||:#{#filtre.descripcio}||'%')) " +
			"and (:#{#filtre.dataProgramadaDisposicioIniciNull} = true or nenv.enviamentDataProgramada >= :#{#filtre.dataProgramadaDisposicioInici}) " +
			"and (:#{#filtre.dataProgramadaDisposicioFiNull} = true or nenv.enviamentDataProgramada <= :#{#filtre.dataProgramadaDisposicioFi}) " +
			"and (:#{#filtre.dataCaducitatIniciNull} = true or nenv.notificaDataCaducitat >= :#{#filtre.dataCaducitatInici}) " +
			"and (:#{#filtre.dataCaducitatFiNull} = true or nenv.notificaDataCaducitat <= :#{#filtre.dataCaducitatFi}) " +
			"and (:#{#filtre.enviamentTipusNull} = true or nenv.tipusEnviament = :#{#filtre.enviamentTipus}) " +
			"and (:#{#filtre.csvUuidNull} = true or lower(CASE WHEN nenv.csv_uuid is null THEN '' ELSE nenv.csv_uuid END) like lower('%'||:#{#filtre.csvUuid}||'%')) " +
			"and (:#{#filtre.estatNull} = true or nenv.estat = :#{#filtre.estat} or nenv.notificaEstat = :#{#filtre.notificaEstat})" +
			"and (:#{#filtre.dataEnviamentFiNull} = true or nenv.createdDate <= :#{#filtre.dataEnviamentFi}) " +
			"and (:#{#filtre.codiNotificaNull} = true or lower(CASE WHEN nenv.notificaIdentificador is null THEN '' ELSE nenv.notificaIdentificador END) like lower('%'||:#{#filtre.codiNotifica}||'%')) " +
			"and (:#{#filtre.creadaPerNull} = true or lower(CASE WHEN nenv.createdBy.codi is null THEN '' ELSE nenv.createdBy.codi END) like lower('%'||:#{#filtre.creadaPerCodi}||'%')) " +
			"and (:#{#filtre.nifTitularNull} = true or lower(CASE WHEN nenv.titularNif is null THEN '' ELSE nenv.titularNif END) like lower('%'||:#{#filtre.nifTitular}||'%')) " +
			"and (:#{#filtre.nomTitularNull} = true or lower(concat('[', nenv.titularLlinatge1, ' ', nenv.titularLlinatge2, ', ', nenv.titularNom,']')) like lower('%'||:#{#filtre.nomTitular}||'%')) " +
			"and (:#{#filtre.emailTitularNull} = true or nenv.titularEmail = :#{#filtre.emailTitular}) " +
			"and (:#{#filtre.dir3CodiNull} = true or lower(CASE WHEN nenv.organCodi is null THEN '' ELSE nenv.organCodi END) like lower('%'||:#{#filtre.dir3Codi}||'%')) " +
			"and (:#{#filtre.numeroCertCorreusNull} = true or nenv.notificaCertificacioNumSeguiment like lower('%'||:#{#filtre.numeroCertCorreus}||'%')) " +
			"and (:#{#filtre.usuariNull} = true or nenv.usuariCodi like lower('%'||:#{#filtre.usuari}||'%')) " +
			"and (:#{#filtre.registreNumeroNull} = true or cast(nenv.registreNumero as string) like lower('%'||:#{#filtre.registreNumero}||'%')) " +
			"and (:#{#filtre.codiNotibEnviamentNull} = true or nenv.notificaReferencia like '%'||:#{#filtre.codiNotibEnviament}||'%') " +
			"and (:#{#filtre.dataRegistreIniciNull} = true or nenv.registreData >= :#{#filtre.dataRegistreInici}) " +
			"and (:#{#filtre.dataRegistreFiNull} = true or nenv.registreData <= :#{#filtre.dataRegistreFi})" +
			"and (:#{#filtre.hasZeronotificaEnviamentIntentNull} = true or " +
			"	(:#{#filtre.hasZeronotificaEnviamentIntent} = true and nenv.registreEnviamentIntent = 0) or " +
			"	(:#{#filtre.hasZeronotificaEnviamentIntent} = false and nenv.registreEnviamentIntent > 0) " +
			") " +
			"and (:#{#filtre.nomesSenseErrors} = false or nenv.hasErrors = true) " +
			"and (:#{#filtre.referenciaNotificacioNull} = true or lower(nenv.notificacio.referencia) like '%'||lower(:#{#filtre.referenciaNotificacio})||'%') " +
			"and (:#{#filtre.nomesAmbErrors} = false or nenv.hasErrors = false)"
	)
	List<Long> findIdsAmbFiltre(FiltreEnviament filtre);
//			@Param("isCodiProcedimentNull") boolean isCodiProcedimentNull,
//			@Param("codiProcediment") String codiProcediment,
//			@Param("isGrupNull") boolean isGrupNull,
//			@Param("grup") String grup,
//			@Param("isConcepteNull") boolean isConcepteNull,
//			@Param("concepte") String concepte,
//			@Param("isDescripcioNull") boolean isDescripcioNull,
//			@Param("descripcio") String descripcio,
//			@Param("isDataProgramadaDisposicioIniciNull") boolean isDataProgramadaDisposicioIniciNull,
//			@Param("dataProgramadaDisposicioInici") Date dataProgramadaDisposicioInici,
//			@Param("isDataProgramadaDisposicioFiNull") boolean isDataProgramadaDisposicioFiNull,
//			@Param("dataProgramadaDisposicioFi") Date dataProgramadaDisposicioFi,
//			@Param("isDataCaducitatIniciNull") boolean isDataCaducitatIniciNull,
//			@Param("dataCaducitatInici") Date dataCaducitatInici,
//			@Param("isDataCaducitatFiNull") boolean dataCaducitatFiNull,
//			@Param("dataCaducitatFi") Date dataCaducitatFi,
//			@Param("isTipusEnviamentNull") boolean isTipusEnviamentNull,
//			@Param("tipusEnviament") EnviamentTipus tipusEnviament,
//			@Param("isCsvNull") boolean isCsvNull,
//			@Param("csv") String csv,
//			@Param("isEstatNull") boolean isEstatNull,
//			@Param("estat") NotificacioEstatEnumDto estat,
//			@Param("notificaEstat") EnviamentEstat notificaEstat,
//			@Param("entitat") EntitatEntity entitat,
//			@Param("esDataEnviamentIniciNull") boolean esDataEnviamentIniciNull,
//			@Param("dataEnviamentInici") Date dataEnviamentInici,
//			@Param("esDataEnviamentFiNull") boolean esDataEnviamentFiNull,
//			@Param("dataEnviamentFi") Date dataEnviamentFi,
//			@Param("esCodiNotificaNull") boolean esCodiNotificaNull,
//			@Param("codiNotifica") String codiNotifica,
//			@Param("esCreatedbyNull") boolean esCreatedbyNull,
//			@Param("createdByCodi") String createdByCodi,
//			@Param("esNifTitularNull") boolean esNifTitularNull,
//			@Param("nifTitular") String nifTitular,
//			@Param("esNomTitularNull") boolean esNomTitularNull,
//			@Param("nomTitular") String nomTitular,
//			@Param("esEmailTitularNull") boolean esEmailTitularNull,
//			@Param("emailTitular") String emailTitular,
//			@Param("esDir3CodiNull") boolean esdir3CodiNull,
//			@Param("dir3Codi") String dir3Codi,
//			@Param("isNumeroCertCorreusNull") boolean isNumeroCertCorreusNull,
//			@Param("numeroCertCorreus") String numeroCertCorreus,
//			@Param("isUsuariNull") boolean isUsuariNull,
//			@Param("usuari") String usuari,
//			@Param("isNumeroRegistreNull") boolean isNumeroRegistreNull,
//			@Param("numeroRegistre") String numeroRegistre,
//			@Param("isNotificaReferenciaNull") boolean isNotificaReferenciaNull,
//			@Param("notificaReferencia") String notificaReferencia,
//			@Param("esDataRegistreIniciNull") boolean esDataRegistreIniciNull,
//			@Param("dataRegistreInici") Date dataRegistreInici,
//			@Param("esDataRegistreFiNull") boolean esDataRegistreFiNull,
//			@Param("dataRegistreFi") Date dataRegistreFi,
//			@Param("nomesAmbErrors") boolean nomesAmbErrors,
//			@Param("nomesSenseErrors") boolean nomesSenseErrors,
//			@Param("isHasZeronotificaEnviamentIntentNull") boolean isHasZeronotificaEnviamentIntentNull,
//			@Param("hasZeronotificaEnviamentIntent") Boolean hasZeronotificaEnviamentIntent,
//			@Param("isReferenciaNotificacioNull") boolean isReferenciaNotificacioNull,
//			@Param("referenciaNotificacio") String referenciaNotificacio,
//			@Param("esProcedimentsCodisNotibNull") boolean esProcedimentsCodisNotibNull,
//			@Param("procedimentsCodisNotib") List<String> procedimentsCodisNotib,
//			@Param("isOrgansGestorsCodisNotibNull") boolean isOrgansGestorsCodisNotibNull,
//			@Param("organsGestorsCodisNotib") List<String> organsGestorsCodisNotib,
//			@Param("esProcedimentOrgansIdsNotibNull") boolean esProcedimentOrgansIdsNotibNull,
//			@Param("procedimentOrgansIdsNotib") List<String> procedimentOrgansIdsNotib,
//			@Param("grupsProcedimentCodisNotib") List<String> grupsProcedimentCodisNotib,
//			@Param("usuariCodi") String usuariCodi,
//			@Param("isUsuari") boolean isUsuari,
//			@Param("isAdminOrgan") boolean isAdminOrgan,
//			@Param("organs") List<String> organs);

}
