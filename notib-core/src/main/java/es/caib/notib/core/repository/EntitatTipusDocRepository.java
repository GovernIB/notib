/**
 * 
 */
package es.caib.notib.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.EntitatTipusDocEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntitatTipusDocRepository extends JpaRepository<EntitatTipusDocEntity, Long> {
	
	public List<EntitatTipusDocEntity> findByEntitat(EntitatEntity entitat);
	
	@Transactional
	@Modifying
	@Query("delete from " +
			"   EntitatTipusDocEntity tipus " +
			"where " +
			"   tipus.tipusDocEnum != :tipusDoc" +
			" AND " +
			"   tipus.entitat.id = :entitatId")
	public void deleteNotInList(
			@Param("entitatId") Long entitatId,
			@Param("tipusDoc") TipusDocumentEnumDto tipusDoc);
}
