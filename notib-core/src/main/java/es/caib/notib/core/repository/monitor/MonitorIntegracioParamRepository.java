package es.caib.notib.core.repository.monitor;

import es.caib.notib.core.entity.monitor.MonitorIntegracioEntity;
import es.caib.notib.core.entity.monitor.MonitorIntegracioParamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface MonitorIntegracioParamRepository extends JpaRepository<MonitorIntegracioParamEntity, Long> {

    List<MonitorIntegracioParamEntity> findByMonitorIntegracio(@Param("integracio")MonitorIntegracioEntity integracioEntity);

    @Modifying
    @Query("delete MonitorIntegracioParamEntity monParam " +
            "where monParam.monitorIntegracio.id in " +
            "( 	select mon.id " +
            "	from MonitorIntegracioEntity mon " +
            "	where mon.data < :data )")
    void deleteDataBefore(@Param("data") Date data);
}
