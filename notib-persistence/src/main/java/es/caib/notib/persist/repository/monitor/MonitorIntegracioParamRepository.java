package es.caib.notib.persist.repository.monitor;


import es.caib.notib.persist.entity.monitor.MonitorIntegracioEntity;
import es.caib.notib.persist.entity.monitor.MonitorIntegracioParamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MonitorIntegracioParamRepository extends JpaRepository<MonitorIntegracioParamEntity, Long> {

    List<MonitorIntegracioParamEntity> findByMonitorIntegracio(@Param("integracio") MonitorIntegracioEntity integracioEntity);

}
