package es.caib.notib.core.repository.monitor;

import es.caib.notib.core.api.dto.IntegracioAccioEstatEnumDto;
import es.caib.notib.core.entity.monitor.MonitorIntegracioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface MonitorIntegracioRepository extends JpaRepository<MonitorIntegracioEntity, Long> {


    @Query("from MonitorIntegracioEntity n " +
            "where lower(n.codi) like concat('%', lower(:codi),'%') " +
            "and (:isCodiEntitatNull = true or lower(n.codiEntitat) like concat('%', lower(:codiEntitat), '%')) " +
            "and (:isAplicacioNull = true or lower(n.aplicacio) like concat('%', lower(:aplicacio), '%')) " +
            "order by n.data desc ")
    List<MonitorIntegracioEntity> getByFiltre(@Param("codi") String codi,
                                              @Param("isCodiEntitatNull") boolean isCodiEntitatNull,
                                              @Param("codiEntitat") String codiEntitat,
                                              @Param("isAplicacioNull") boolean isAplicacioNull,
                                              @Param("aplicacio") String aplicacio);

    List<MonitorIntegracioEntity> findAllByCodiOrderByDataDesc(@Param("codi") String codi);

    int countByCodiAndEstat(@Param("codi") String codi, @Param("estat")IntegracioAccioEstatEnumDto estat);

    void deleteByDataIsBefore(@Param("llindar") Date llindar);
}
