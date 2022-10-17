package es.caib.notib.core.repository.monitor;

import es.caib.notib.core.api.dto.IntegracioAccioEstatEnumDto;
import es.caib.notib.core.entity.monitor.MonitorIntegracioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MonitorIntegracioRepository extends JpaRepository<MonitorIntegracioEntity, Long> {


    List<MonitorIntegracioEntity> findAllByCodi(@Param("codi") String codi);

    int countByCodiAndEstat(@Param("codi") String codi, @Param("estat")IntegracioAccioEstatEnumDto estat);
}
