package es.caib.notib.core.repository;

import es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.EnviamentTableEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.entity.UsuariEntity;
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
public interface EnviamentTableRepository extends JpaRepository<EnviamentTableEntity, Long> {
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
			"and (:isTipusEnviamentNull = true or lower(nenv.tipusEnviament) like lower('%'||:tipusEnviament||'%')) " +
			"and (:isCsvNull = true or lower(nenv.csv_uuid) like lower('%'||:csv||'%')) " +
			"and (:isEstatNull = true or lower(nenv.estat) like lower('%'||:estat||'%') " +
			"						  or nenv.notificaEstat like lower('%'||:notificaEstat||'%'))" +
			"and (:esCodiNotificaNull = true or lower(nenv.notificaIdentificador) like lower('%'||:codiNotifica||'%')) " +
			"and (:esCreatedbyNull = true or lower(nenv.createdBy) like lower('%'||:createdBy||'%')) " +
			"and (:esNifTitularNull = true or lower(nenv.titularNif) like lower('%'||:nifTitular||'%')) " +
			"and (:esNomTitularNull = true or lower(concat('[', nenv.titularLlinatge1, ' ', nenv.titularLlinatge2, ', ', nenv.titularNom,']')) like lower('%'||:nomTitular||'%')) " +
			"and (:esEmailTitularNull = true or nenv.titularEmail = :emailTitular) " +
			"and (:esDir3CodiNull = true or lower(nenv.organCodi) like lower('%'||:dir3Codi||'%')) " +
			"and (:isNumeroCertCorreusNull = true or nenv.notificaCertificacioNumSeguiment like lower('%'||:numeroCertCorreus||'%')) " +
			"and (:isUsuariNull = true or nenv.usuariCodi like lower('%'||:usuari||'%')) "+
			"and (:isNumeroRegistreNull = true or nenv.registreNumero like lower('%'||:numeroRegistre||'%')) "+
			"and (:isNotificaReferenciaNull = true or nenv.notificaReferencia like '%'||:notificaReferencia||'%') "+
			"and (:esDataRegistreIniciNull = true or nenv.registreData >= :dataRegistreInici) " +
			"and (:esDataRegistreFiNull = true or nenv.registreData <= :dataRegistreFi) " +
			"and ((:esProcedimentsCodisNotibNull = false and nenv.procedimentCodiNotib is not null and nenv.procedimentCodiNotib in (:procedimentsCodisNotib))" +	// Té permís sobre el procediment
			"	or (:isOrgansGestorsCodisNotibNull = false and nenv.organCodi is not null and " +
			"			(nenv.procedimentCodiNotib is null or (nenv.procedimentIsComu = true and nenv.procedimentRequirePermission = false)) and nenv.organCodi in (:organsGestorsCodisNotib)" +
			"		) " + // Té permís sobre l'òrgan
			"   or ((nenv.procedimentCodiNotib is null or nenv.procedimentIsComu = true) and nenv.usuariCodi = :usuariCodi)" + // És una notificaicó sense procediment o un procediment comú, iniciat pel propi usuari
			") " +
			"and (nenv.grupCodi = null or (nenv.grupCodi in (:grupsProcedimentCodisNotib))) " +
			"and (:isHasZeronotificaEnviamentIntentNull = true or " +
			"	(:hasZeronotificaEnviamentIntent = true and nenv.registreEnviamentIntent = 0) or " +
			"	(:hasZeronotificaEnviamentIntent = false and nenv.registreEnviamentIntent > 0) " +
			") " +
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
			@Param("grupsProcedimentCodisNotib") List<String> grupsProcedimentCodisNotib,
			@Param("usuariCodi") String usuariCodi,
			@Param("nomesAmbErrors") boolean nomesAmbErrors,
			@Param("nomesSenseErrors") boolean nomesSenseErrors,
			@Param("isHasZeronotificaEnviamentIntentNull") boolean isHasZeronotificaEnviamentIntentNull,
			@Param("hasZeronotificaEnviamentIntent") Boolean hasZeronotificaEnviamentIntent,
			Pageable pageable);

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
			"and (:isTipusEnviamentNull = true or lower(nenv.tipusEnviament) like lower('%'||:tipusEnviament||'%')) " +
			"and (:isCsvNull = true or lower(nenv.csv_uuid) like lower('%'||:csv||'%')) " +
			"and (:isEstatNull = true or lower(nenv.estat) like lower('%'||:estat||'%') " +
			"						  or nenv.notificaEstat like lower('%'||:notificaEstat||'%'))" +
			"and (:esCodiNotificaNull = true or lower(nenv.notificaIdentificador) like lower('%'||:codiNotifica||'%')) " +
			"and (:esCreatedbyNull = true or lower(nenv.createdBy) like lower('%'||:createdBy||'%')) " +
			"and (:esNifTitularNull = true or lower(nenv.titularNif) like lower('%'||:nifTitular||'%')) " +
			"and (:esNomTitularNull = true or lower(concat('[', nenv.titularLlinatge1, ' ', nenv.titularLlinatge2, ', ', nenv.titularNom,']')) like lower('%'||:nomTitular||'%')) " +
			"and (:esEmailTitularNull = true or nenv.titularEmail = :emailTitular) " +
			"and (:esDir3CodiNull = true or lower(nenv.organCodi) like lower('%'||:dir3Codi||'%')) " +
			"and (:isNumeroCertCorreusNull = true or nenv.notificaCertificacioNumSeguiment like lower('%'||:numeroCertCorreus||'%')) " +
			"and (:isUsuariNull = true or nenv.usuariCodi like lower('%'||:usuari||'%')) " +
			"and (:isNumeroRegistreNull = true or nenv.registreNumero like lower('%'||:numeroRegistre||'%')) " +
			"and (:isNotificaReferenciaNull = true or nenv.notificaReferencia like '%'||:notificaReferencia||'%') " +
			"and (:esDataRegistreIniciNull = true or nenv.registreData >= :dataRegistreInici) " +
			"and (:esDataRegistreFiNull = true or nenv.registreData <= :dataRegistreFi) " +
			"and (nenv.organCodi is not null and nenv.organCodi in (:organs)) " +
			"and (:isHasZeronotificaEnviamentIntentNull = true or " +
			"	(:hasZeronotificaEnviamentIntent = true and nenv.registreEnviamentIntent = 0) or " +
			"	(:hasZeronotificaEnviamentIntent = false and nenv.registreEnviamentIntent > 0) " +
			") " +
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
			@Param("isNotificaReferenciaNull") boolean isNotificaReferenciaNull,
			@Param("notificaReferencia") String notificaReferencia,
			@Param("esDataRegistreIniciNull") boolean esDataRegistreIniciNull,
			@Param("dataRegistreInici") Date dataRegistreInici,
			@Param("esDataRegistreFiNull") boolean esDataRegistreFiNull,
			@Param("dataRegistreFi") Date dataRegistreFi,
			@Param("organs") List<String> organs,
			@Param("nomesAmbErrors") boolean nomesAmbErrors,
			@Param("nomesSenseErrors") boolean nomesSenseErrors,
			@Param("isHasZeronotificaEnviamentIntentNull") boolean isHasZeronotificaEnviamentIntentNull,
			@Param("hasZeronotificaEnviamentIntent") Boolean hasZeronotificaEnviamentIntent,
			Pageable pageable);

	@Query( "from " +
			"    EnviamentTableEntity nenv " +
			"where " +
			"    (:entitat = nenv.entitat) " +
			"and (:esDataEnviamentIniciNull = true or nenv.createdDate >= :dataEnviamentInici) " +
			"and (:isCodiProcedimentNull = true or lower(nenv.procedimentCodiNotib) like lower('%'||:codiProcediment||'%')) " +
			"and (:isGrupNull = true or lower(nenv.grupCodi) like lower('%'||:grup||'%')) " +
			"and (:isConcepteNull = true or lower(nenv.concepte) like lower('%'||:concepte||'%')) " +
			"and (:isDescripcioNull = true or lower(nenv.descripcio) like lower('%'||:descripcio||'%')) " +
			"and (:isDataProgramadaDisposicioIniciNull = true or nenv.enviamentDataProgramada >= :dataProgramadaDisposicioInici) " +
			"and (:isDataProgramadaDisposicioFiNull = true or nenv.enviamentDataProgramada <= :dataProgramadaDisposicioFi) " +
			"and (:isDataCaducitatIniciNull = true or nenv.notificaDataCaducitat >= :dataCaducitatInici) " +
			"and (:isDataCaducitatFiNull = true or nenv.notificaDataCaducitat <= :dataCaducitatFi) " +
			"and (:isTipusEnviamentNull = true or lower(nenv.tipusEnviament) like lower('%'||:tipusEnviament||'%')) " +
			"and (:isCsvNull = true or lower(nenv.csv_uuid) like lower('%'||:csv||'%')) " +
			"and (:isEstatNull = true or lower(nenv.estat) like lower('%'||:estat||'%') or nenv.notificaEstat like lower('%'||:notificaEstat||'%'))" +
			"and (:esDataEnviamentFiNull = true or nenv.createdDate <= :dataEnviamentFi) " +
			"and (:esCodiNotificaNull = true or lower(nenv.notificaIdentificador) like lower('%'||:codiNotifica||'%')) " +
			"and (:esCreatedbyNull = true or lower(nenv.createdBy.codi) like lower('%'||:createdByCodi||'%')) " +
			"and (:esNifTitularNull = true or lower(nenv.titularNif) like lower('%'||:nifTitular||'%')) " +
			"and (:esNomTitularNull = true or lower(concat('[', nenv.titularLlinatge1, ' ', nenv.titularLlinatge2, ', ', nenv.titularNom,']')) like lower('%'||:nomTitular||'%')) " +
			"and (:esEmailTitularNull = true or nenv.titularEmail = :emailTitular) " +
			"and (:esDir3CodiNull = true or lower(nenv.organCodi) like lower('%'||:dir3Codi||'%')) " +
			"and (:isNumeroCertCorreusNull = true or nenv.notificaCertificacioNumSeguiment like lower('%'||:numeroCertCorreus||'%')) " +
			"and (:isUsuariNull = true or nenv.usuariCodi like lower('%'||:usuari||'%')) " +
			"and (:isNumeroRegistreNull = true or nenv.registreNumero like lower('%'||:numeroRegistre||'%')) " +
			"and (:isNotificaReferenciaNull = true or nenv.notificaReferencia like '%'||:notificaReferencia||'%') " +
			"and (:esDataRegistreIniciNull = true or nenv.registreData >= :dataRegistreInici) " +
			"and (:esDataRegistreFiNull = true or nenv.registreData <= :dataRegistreFi)" +
			"and (:isHasZeronotificaEnviamentIntentNull = true or " +
			"	(:hasZeronotificaEnviamentIntent = true and nenv.registreEnviamentIntent = 0) or " +
			"	(:hasZeronotificaEnviamentIntent = false and nenv.registreEnviamentIntent > 0) " +
			") " +
			"and (:nomesSenseErrors = false or nenv.hasErrors = true) " +
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
			@Param("createdByCodi") String createdBy,
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
			Pageable pageable);
	
	@Modifying
	@Query("update EnviamentTableEntity et " +
			"set et.organEstat = (SELECT og.estat from OrganGestorEntity og where og.codi = et.organCodi) " +
			"where et.organCodi is not null")
	void updateOrganGestorEstat();

	@Modifying
	@Query("update EnviamentTableEntity nt " +
			"set " +
			" nt.procedimentIsComu = :procedimentComu, " +
			" nt.procedimentRequirePermission = :procedimentRequirePermission " +
			"where nt.procedimentCodiNotib = :procedimentCodi ")
	void updateProcediment(@Param("procedimentComu") boolean procedimentComu,
						   @Param("procedimentRequirePermission") boolean procedimentRequireDirectPermission,
						   @Param("procedimentCodi") String procedimentCodi);
}
