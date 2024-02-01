package es.caib.notib.persist.repository.monitor;

import es.caib.notib.logic.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodiEnum;
import es.caib.notib.persist.entity.monitor.MonitorIntegracioEntity;
import es.caib.notib.persist.filtres.FiltreMonitorIntegracio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            "where n.codi = :#{#filtre.codi} " +
            "and (:#{#filtre.codiEntitatNull} = true or lower(n.codiEntitat) like concat('%', lower(:#{#filtre.codiEntitat}), '%')) " +
            "and (:#{#filtre.aplicacioNull} = true or lower(n.aplicacio) like concat('%', lower(:#{#filtre.aplicacio}), '%')) " +
            "and (:#{#filtre.descripcioNull} = true or lower(n.descripcio) like concat('%', lower(:#{#filtre.descripcio}), '%')) " +
            "and (:#{#filtre.dataIniciNull} = true or n.data >= :#{#filtre.dataInici}) " +
            "and (:#{#filtre.dataFiNull} = true or n.data <= :#{#filtre.dataFi}) "+
            "and (:#{#filtre.tipusNull} = true or n.tipus = :#{#filtre.tipus}) " +
            "and (:#{#filtre.estatNull} = true or n.estat = :#{#filtre.estat}) " +
            "order by n.data desc ")
    Page<MonitorIntegracioEntity> getByFiltre(FiltreMonitorIntegracio filtre, Pageable pageable);

    int countByCodiAndEstat(@Param("codi") IntegracioCodiEnum codi, @Param("estat")IntegracioAccioEstatEnumDto estat);

    List<MonitorIntegracioEntity> findByDataLessThan(@Param("llindar") Date llindar);

    @Query(value = "SELECT id FROM (SELECT n.id FROM not_mon_int n WHERE n.DATA < :llindar) WHERE rownum < 1000", nativeQuery = true)
    List<Long> getNotificacionsAntigues(@Param("llindar") Date llindar);

    @Query(value = "SELECT count(id) FROM (SELECT * FROM not_mon_int n WHERE n.DATA < :llindar) WHERE rownum = 1", nativeQuery = true)
    int existeixenAntics(@Param("llindar") Date llindar);

    @Modifying
    @Query(value = "DELETE from not_mon_int m WHERE m.id in (:ids)", nativeQuery = true)
    void eliminarAntics(@Param("ids") List<Long> ids);

    @Modifying
    void deleteByCodiAndCodiEntitat(@Param("codi") IntegracioCodiEnum codi, @Param("codiEntitat") String codiEntitat);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    MonitorIntegracioEntity save(@Param("integracio") MonitorIntegracioEntity integracio);

}
