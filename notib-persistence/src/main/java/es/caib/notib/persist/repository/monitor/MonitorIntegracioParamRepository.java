package es.caib.notib.persist.repository.monitor;


import es.caib.notib.persist.entity.monitor.MonitorIntegracioEntity;
import es.caib.notib.persist.entity.monitor.MonitorIntegracioParamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MonitorIntegracioParamRepository extends JpaRepository<MonitorIntegracioParamEntity, Long> {

    List<MonitorIntegracioParamEntity> findByMonitorIntegracioOrderByIdAsc(@Param("integracio")MonitorIntegracioEntity integracioEntity);

    @Modifying
    @Query(value = "DELETE FROM NOT_MON_INT_PARAM p WHERE p.MON_INT_ID IN (:ids)", nativeQuery = true)
    void eliminarAntics(@Param("ids") List<Long> ids);

}
