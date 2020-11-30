package es.caib.notib.core.repository.historic;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.notib.core.entity.historic.HistoricOrganEntity;


public interface HistoricOrganGestorRepository extends JpaRepository<HistoricOrganEntity, Long> {


}
