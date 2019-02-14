package es.caib.notib.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.notib.core.entity.DocumentEntity;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long>  {

}
