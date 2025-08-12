/**
 * 
 */
package es.caib.notib.persist.repository;

import es.caib.notib.logic.intf.dto.Taula;
import org.springframework.data.jpa.repository.JpaRepository;
import es.caib.notib.persist.entity.ColumnesEntity;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.UsuariEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus columnes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ColumnesRepository extends JpaRepository<ColumnesEntity, Long> {

	ColumnesEntity findByEntitatAndUser(EntitatEntity entitat, UsuariEntity usuari);

	@Query(value = "FROM ColumnesEntity c WHERE c.entitat.id = :entitatId AND c.user.codi = :usuariCodi")
	ColumnesEntity existeixUsuariPerEntitat(@Param("usuariCodi") String usuariCodi, @Param("entitatId") Long entitatId);

	@Transactional
	@Modifying
	@Query("DELETE FROM ColumnesEntity c WHERE c.entitat.id = :entitatId")
	int deleteByEntitatId(@Param("entitatId") Long entitatId);

	@Transactional
	@Modifying
	@Query("UPDATE ColumnesEntity c SET c.referenciaNotificacio = false WHERE c.referenciaNotificacio is NULL")
	void refNotUpdateNulls();


	@Modifying
	@Query(value = "UPDATE NOT_COLUMNES " +
			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
			nativeQuery = true)
	int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);

	@Modifying
	@Query(value = "UPDATE NOT_COLUMNES SET USUARI_CODI = :codiNou WHERE USUARI_CODI = :codiAntic", nativeQuery = true)
	int updateUsuariCodi(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);

}
