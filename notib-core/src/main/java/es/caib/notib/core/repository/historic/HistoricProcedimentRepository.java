package es.caib.notib.core.repository.historic;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.notib.core.entity.historic.HistoricProcedimentEntity;

public interface HistoricProcedimentRepository extends JpaRepository<HistoricProcedimentEntity, Long> {

}
