package es.caib.notib.persist.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.notib.persist.entity.DocumentEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long>  {

    DocumentEntity getByArxiuGestdocId(String arxiuGestdocId);

    Optional<DocumentEntity> findTopByUuidIsNotNullOrderByIdDesc();


    @Modifying
    @Query(value = "UPDATE NOT_DOCUMENT " +
            "SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
            "    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
            "WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
            nativeQuery = true)
    void updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);

}
