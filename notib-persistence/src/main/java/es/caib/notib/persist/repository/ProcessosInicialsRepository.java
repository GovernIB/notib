package es.caib.notib.persist.repository;

import es.caib.notib.logic.intf.dto.ProcessosInicialsEnum;
import es.caib.notib.persist.entity.ProcesosInicialsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProcessosInicialsRepository extends JpaRepository<ProcesosInicialsEntity, String> {

    List<ProcesosInicialsEntity> findProcesosInicialsEntityByInitTrue();

    @Modifying
    @Query("update ProcesosInicialsEntity p set p.init = :init where p.codi = :codi")
    void updateInit(@Param("codi") ProcessosInicialsEnum codi, @Param("init") boolean init);

}
