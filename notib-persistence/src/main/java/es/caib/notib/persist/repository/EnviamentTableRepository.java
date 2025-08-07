package es.caib.notib.persist.repository;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.EnviamentTableEntity;
import es.caib.notib.persist.filtres.FiltreEnviament;
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

	@Query( "from EnviamentTableEntity nenv where (:entitat = nenv.entitat)")
	Page<EnviamentTableEntity> find4EntitatAdminRole(@Param("entitat") EntitatEntity entitat, Pageable pageable);

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
	@Query("update EnviamentTableEntity nt set  nt.notificaReferencia = :notificaReferencia where nt.id = :enviamentId ")
	void updateNotificaReferencia(@Param("notificaReferencia") String notificaReferencia, @Param("enviamentId") Long procedimentCodi);

	@Modifying
	@Query("update EnviamentTableEntity nt set nt.organEstat = :estat where nt.organCodi = :organCodi")
	void updateOrganEstat(@Param("organCodi") String organCodi, @Param("estat") OrganGestorEstatEnum estat);

	@Query( "select nenv from EnviamentTableEntity nenv " +
			"where " +
			"    (:#{#filtre.entitatIdNull} = true or :#{#filtre.entitatId} = nenv.entitat.id) " +
			"and (:#{#filtre.isSuperAdmin} = true or " +
			" :#{#filtre.isUsuari} = false or " +
			"(" +
			// PERMISOS
			// Iniciada pel propi usuari
			"	nenv.usuariCodi = :#{#filtre.usuariCodi} " +
			// Té permís consulta sobre el procediment
			"	or (:#{#filtre.procedimentsCodisNotibNull} = false and nenv.procedimentCodiNotib is not null " +
			"			and (nenv.procedimentCodiNotib in (:#{#filtre.procedimentsCodisNotibSplit[0]}) " +
			"				or nenv.procedimentCodiNotib in (:#{#filtre.procedimentsCodisNotibSplit[1]}) " +
			"				or nenv.procedimentCodiNotib in (:#{#filtre.procedimentsCodisNotibSplit[2]}) " +
			"				or nenv.procedimentCodiNotib in (:#{#filtre.procedimentsCodisNotibSplit[3]})) " +
			"		and nenv.procedimentIsComu = false) " +
			// Té permís consulta sobre l'òrgan
			"	or (:#{#filtre.organsGestorsCodisNotibNull} = false and nenv.organCodi is not null " +
			"			and nenv.procedimentIsComu = false and nenv.procedimentRequirePermission = false " +
			"			and nenv.organCodi in (:#{#filtre.organsGestorsCodisNotib})) " +
			// Procediment comú amb permís comú sobre l'òrgan
			"	or (:#{#filtre.organsGestorsComunsCodisNotibNull} = false and nenv.organCodi is not null " +
			"			and nenv.procedimentIsComu = true and nenv.procedimentRequirePermission = false " +
			"			and nenv.organCodi in (:#{#filtre.organsGestorsComunsCodisNotib})) " +
			// Procediment comú amb permís de procediment-òrgan
			"   or (:#{#filtre.procedimentOrgansAmbPermisNull} = false and nenv.procedimentCodiNotib is not null " +
			"			and CONCAT(nenv.procedimentCodiNotib, '-', nenv.organCodi) in (:#{#filtre.procedimentOrgansAmbPermis})" +
			"		) " +
			") " +
			"and (nenv.grupCodi = null or (nenv.grupCodi in (:#{#filtre.rols}))) " +
			") " +
			"and (:#{#filtre.isSuperAdmin} = false or nenv.entitat in (:#{#filtre.entitatsActives})) " +
			"and (:#{#filtre.isAdminOrgan} = false or (nenv.organCodi is not null and nenv.organCodi in (:#{#filtre.organs})))" +
			"and (:#{#filtre.dataCreacioIniciNull} = true or nenv.createdDate >= :#{#filtre.dataCreacioInici}) " +
			"and (:#{#filtre.dataCreacioFiNull} = true or nenv.createdDate <= :#{#filtre.dataCreacioFi}) " +
			"and (:#{#filtre.dataEnviamentIniciNull} = true or ((nenv.notificacio.notificaEnviamentData is not null or nenv.registreData is not null)  " +
			"  and CASE WHEN nenv.notificacio.notificaEnviamentData is not null THEN nenv.notificacio.notificaEnviamentData  ELSE nenv.registreData END >= :#{#filtre.dataEnviamentInici})) " +
			"and (:#{#filtre.dataEnviamentFiNull} = true or ((nenv.notificacio.notificaEnviamentData is not null and nenv.registreData is not null) " +
			" 	and CASE WHEN nenv.notificacio.notificaEnviamentData is not null THEN nenv.notificacio.notificaEnviamentData  ELSE nenv.registreData END <= :#{#filtre.dataEnviamentFi})) " +
			"and (:#{#filtre.codiProcedimentNull} = true " +
				"or lower(CASE WHEN nenv.procedimentCodiNotib is null THEN '' ELSE nenv.procedimentCodiNotib END) like lower('%'||:#{#filtre.codiProcediment}||'%') " +
				"or lower(nenv.notificacio.procediment.nom) like '%' || lower(:#{#filtre.codiProcediment}) || '%') " +
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
			"and (:#{#filtre.nomTitularNull} = true or (lower(concat(nenv.titularNom, ' ', nenv.titularLlinatge1, ' ', nenv.titularLlinatge2)) like '%'|| lower(:#{#filtre.nomTitular}) ||'%') " +
			"or lower(CASE WHEN nenv.titularNif is null THEN '' ELSE nenv.titularNif END) like '%'|| lower(:#{#filtre.nomTitular}) ||'%') " +
			"and (:#{#filtre.nomTitularNull} = true " +
				"or lower(concat('[', nenv.titularLlinatge1, ' ', nenv.titularLlinatge2, ', ', nenv.titularNom,']')) like lower('%'||:#{#filtre.nomTitular}||'%') " +
				"or lower(CASE WHEN nenv.titularNif is null THEN '' ELSE nenv.titularNif END) like '%'|| lower(:#{#filtre.nomTitular}) ||'%') " +
			"and (:#{#filtre.dir3CodiNull} = true " +
				"or lower(CASE WHEN nenv.organCodi is null THEN '' ELSE nenv.organCodi END) like lower('%'||:#{#filtre.dir3Codi}||'%') " +
				"or lower(nenv.notificacio.organGestor.nom) like '%' || lower(:#{#filtre.dir3Codi}) || '%') " +
			"and (:#{#filtre.usuariNull} = true or nenv.usuariCodi like lower('%'||:#{#filtre.usuari}||'%')) " +
			"and (:#{#filtre.registreNumeroNull} = true or cast(nenv.registreNumero as string) like lower('%'||:#{#filtre.registreNumero}||'%')) " +
			"and (:#{#filtre.codiNotibEnviamentNull} = true or nenv.notificaReferencia like '%'||:#{#filtre.codiNotibEnviament}||'%') " +
			"and (:#{#filtre.hasZeronotificaEnviamentIntentNull} = true or " +
			"	(:#{#filtre.hasZeronotificaEnviamentIntent} = true and nenv.registreEnviamentIntent = 0) or " +
			"	(:#{#filtre.hasZeronotificaEnviamentIntent} = false and nenv.registreEnviamentIntent > 0) " +
			") " +
			"and (:#{#filtre.nomesSenseErrors} = false or nenv.hasErrors = true) " +
			"and (:#{#filtre.entregaPostal} is null or nenv.entregaPostal = :#{#filtre.entregaPostal}) " +
			"and (:#{#filtre.referenciaNotificacioNull} = true or lower(nenv.notificacio.referencia) like '%'||lower(:#{#filtre.referenciaNotificacio})||'%') " +
			"and (:#{#filtre.nomesAmbErrors} = false or nenv.hasErrors = false)"
	)
	Page<EnviamentTableEntity> findAmbFiltre(FiltreEnviament filtre, Pageable pageable);

	@Query( "select nenv.id from EnviamentTableEntity nenv " +
			"where " +
			"    (:#{#filtre.entitatIdNull} = true or :#{#filtre.entitatId} = nenv.entitat.id) " +
			"and (:#{#filtre.isSuperAdmin} = true or " +
			" :#{#filtre.isUsuari} = false or " +
			"(" +
			// PERMISOS
			// Iniciada pel propi usuari
			"	nenv.usuariCodi = :#{#filtre.usuariCodi} " +
			// Té permís consulta sobre el procediment
			"	or (:#{#filtre.procedimentsCodisNotibNull} = false and nenv.procedimentCodiNotib is not null " +
			"			and (nenv.procedimentCodiNotib in (:#{#filtre.procedimentsCodisNotibSplit[0]}) " +
			"				or nenv.procedimentCodiNotib in (:#{#filtre.procedimentsCodisNotibSplit[1]}) " +
			"				or nenv.procedimentCodiNotib in (:#{#filtre.procedimentsCodisNotibSplit[2]}) " +
			"				or nenv.procedimentCodiNotib in (:#{#filtre.procedimentsCodisNotibSplit[3]})) " +
			"		and nenv.procedimentIsComu = false) " +
			// Té permís consulta sobre l'òrgan
			"	or (:#{#filtre.organsGestorsCodisNotibNull} = false and nenv.organCodi is not null " +
			"			and nenv.procedimentIsComu = false and nenv.procedimentRequirePermission = false " +
			"			and nenv.organCodi in (:#{#filtre.organsGestorsCodisNotib})) " +
			// Procediment comú amb permís comú sobre l'òrgan
			"	or (:#{#filtre.organsGestorsComunsCodisNotibNull} = false and nenv.organCodi is not null " +
			"			and nenv.procedimentIsComu = true and nenv.procedimentRequirePermission = false " +
			"			and nenv.organCodi in (:#{#filtre.organsGestorsComunsCodisNotib})) " +
			// Procediment comú amb permís de procediment-òrgan
			"   or (:#{#filtre.procedimentOrgansAmbPermisNull} = false and nenv.procedimentCodiNotib is not null " +
			"			and CONCAT(nenv.procedimentCodiNotib, '-', nenv.organCodi) in (:#{#filtre.procedimentOrgansAmbPermis})" +
			"		) " +
			") " +
			"and (nenv.grupCodi = null or (nenv.grupCodi in (:#{#filtre.rols}))) " +
			") " +
			"and (:#{#filtre.isSuperAdmin} = false or nenv.entitat in (:#{#filtre.entitatsActives})) " +
			"and (:#{#filtre.isAdminOrgan} = false or (nenv.organCodi is not null and nenv.organCodi in (:#{#filtre.organs})))" +
			"and (:#{#filtre.dataCreacioIniciNull} = true or nenv.createdDate >= :#{#filtre.dataCreacioInici}) " +
			"and (:#{#filtre.dataCreacioFiNull} = true or nenv.createdDate <= :#{#filtre.dataCreacioFi}) " +
			"and (:#{#filtre.dataEnviamentIniciNull} = true or ((nenv.notificacio.notificaEnviamentData is not null or nenv.registreData is not null)  " +
			"  and CASE WHEN nenv.notificacio.notificaEnviamentData is not null THEN nenv.notificacio.notificaEnviamentData  ELSE nenv.registreData END >= :#{#filtre.dataEnviamentInici})) " +
			"and (:#{#filtre.dataEnviamentFiNull} = true or ((nenv.notificacio.notificaEnviamentData is not null and nenv.registreData is not null) " +
			" 	and CASE WHEN nenv.notificacio.notificaEnviamentData is not null THEN nenv.notificacio.notificaEnviamentData  ELSE nenv.registreData END <= :#{#filtre.dataEnviamentFi})) " +
			"and (:#{#filtre.codiProcedimentNull} = true " +
			"or lower(CASE WHEN nenv.procedimentCodiNotib is null THEN '' ELSE nenv.procedimentCodiNotib END) like lower('%'||:#{#filtre.codiProcediment}||'%') " +
			"or lower(nenv.notificacio.procediment.nom) like '%' || lower(:#{#filtre.codiProcediment}) || '%') " +
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
			"and (:#{#filtre.nomTitularNull} = true or (lower(concat(nenv.titularNom, ' ', nenv.titularLlinatge1, ' ', nenv.titularLlinatge2)) like '%'|| lower(:#{#filtre.nomTitular}) ||'%') " +
			"or lower(CASE WHEN nenv.titularNif is null THEN '' ELSE nenv.titularNif END) like '%'|| lower(:#{#filtre.nomTitular}) ||'%') " +
			"and (:#{#filtre.nomTitularNull} = true " +
			"or lower(concat('[', nenv.titularLlinatge1, ' ', nenv.titularLlinatge2, ', ', nenv.titularNom,']')) like lower('%'||:#{#filtre.nomTitular}||'%') " +
			"or lower(CASE WHEN nenv.titularNif is null THEN '' ELSE nenv.titularNif END) like '%'|| lower(:#{#filtre.nomTitular}) ||'%') " +
			"and (:#{#filtre.dir3CodiNull} = true " +
			"or lower(CASE WHEN nenv.organCodi is null THEN '' ELSE nenv.organCodi END) like lower('%'||:#{#filtre.dir3Codi}||'%') " +
			"or lower(nenv.notificacio.organGestor.nom) like '%' || lower(:#{#filtre.dir3Codi}) || '%') " +
			"and (:#{#filtre.usuariNull} = true or nenv.usuariCodi like lower('%'||:#{#filtre.usuari}||'%')) " +
			"and (:#{#filtre.registreNumeroNull} = true or cast(nenv.registreNumero as string) like lower('%'||:#{#filtre.registreNumero}||'%')) " +
			"and (:#{#filtre.codiNotibEnviamentNull} = true or nenv.notificaReferencia like '%'||:#{#filtre.codiNotibEnviament}||'%') " +
			"and (:#{#filtre.hasZeronotificaEnviamentIntentNull} = true or " +
			"	(:#{#filtre.hasZeronotificaEnviamentIntent} = true and nenv.registreEnviamentIntent = 0) or " +
			"	(:#{#filtre.hasZeronotificaEnviamentIntent} = false and nenv.registreEnviamentIntent > 0) " +
			") " +
			"and (:#{#filtre.nomesSenseErrors} = false or nenv.hasErrors = true) " +
			"and (:#{#filtre.entregaPostal} is null or nenv.entregaPostal = :#{#filtre.entregaPostal}) " +
			"and (:#{#filtre.referenciaNotificacioNull} = true or lower(nenv.notificacio.referencia) like '%'||lower(:#{#filtre.referenciaNotificacio})||'%') " +
			"and (:#{#filtre.nomesAmbErrors} = false or nenv.hasErrors = false)"
	)
	List<Long> findIdsAmbFiltre(FiltreEnviament filtre);

	@Modifying
	@Query(value = "UPDATE NOT_NOTIFICACIO_ENV_TABLE SET CREATEDBY_CODI = :codiNou WHERE CREATEDBY_CODI = :codiAntic", nativeQuery = true)
	int updateCreatedByCodi(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);

	@Modifying
	@Query(value = "UPDATE NOT_NOTIFICACIO_ENV_TABLE SET LASTMODIFIEDBY_CODI = :codiNou WHERE LASTMODIFIEDBY_CODI = :codiAntic", nativeQuery = true)
	int updateLastModifiedByCodi(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);


}
