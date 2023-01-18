package es.caib.notib.persist.repository.monitor;

import es.caib.notib.logic.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.notib.persist.entity.monitor.MonitorIntegracioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface MonitorIntegracioRepository extends JpaRepository<MonitorIntegracioEntity, Long> {

    @Transactional
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

    int countByCodiAndEstat(@Param("codi") String codi, @Param("estat") IntegracioAccioEstatEnumDto estat);

    void deleteByDataIsBefore(@Param("llindar") Date llindar);

    @Modifying
    void deleteByCodiAndCodiEntitat(@Param("codi") String codi, @Param("codiEntitat") String codiEntitat);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    MonitorIntegracioEntity save(@Param("integracio") MonitorIntegracioEntity integracio);
}
