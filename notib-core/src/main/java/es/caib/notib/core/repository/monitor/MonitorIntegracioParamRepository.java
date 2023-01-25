package es.caib.notib.core.repository.monitor;

import es.caib.notib.core.entity.monitor.MonitorIntegracioEntity;
import es.caib.notib.core.entity.monitor.MonitorIntegracioParamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MonitorIntegracioParamRepository extends JpaRepository<MonitorIntegracioParamEntity, Long> {

    List<MonitorIntegracioParamEntity> findByMonitorIntegracio(@Param("integracio")MonitorIntegracioEntity integracioEntity);
}
