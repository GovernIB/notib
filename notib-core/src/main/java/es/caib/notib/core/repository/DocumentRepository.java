package es.caib.notib.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.notib.core.entity.DocumentEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long>  {

}
