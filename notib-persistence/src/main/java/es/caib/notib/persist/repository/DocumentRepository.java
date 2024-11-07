package es.caib.notib.persist.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.notib.persist.entity.DocumentEntity;

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
}
