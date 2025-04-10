/**
 * 
 */
package es.caib.notib.persist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.logic.intf.dto.TipusDocumentEnumDto;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.EntitatTipusDocEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntitatTipusDocRepository extends JpaRepository<EntitatTipusDocEntity, Long> {
	
	public List<EntitatTipusDocEntity> findByEntitat(EntitatEntity entitat);
	
	@Query("from EntitatTipusDocEntity tipus where tipus.tipusDocEnum = :tipusDoc AND tipus.entitat.id = :entitatId")
	public EntitatTipusDocEntity findByEntitatAndTipus(@Param("entitatId") Long entitat, @Param("tipusDoc") TipusDocumentEnumDto tipusDoc);
	
	@Transactional
	@Modifying
	@Query("delete from EntitatTipusDocEntity tipus where tipus.tipusDocEnum != :tipusDoc AND tipus.entitat.id = :entitatId")
	public int deleteNotInList(@Param("entitatId") Long entitatId, @Param("tipusDoc") TipusDocumentEnumDto tipusDoc);


	@Modifying
	@Query(value = "UPDATE NOT_ENTITAT_TIPUS_DOC " +
			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
			nativeQuery = true)
	void updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);

}
