package es.caib.notib.persist.repository.explotacio;

import es.caib.notib.persist.entity.explotacio.ExplotTempsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExplotTempsRepository extends JpaRepository<ExplotTempsEntity, Long> {

    ExplotTempsEntity findFirstByData(LocalDate data);
    List<ExplotTempsEntity> findByDataBetween(LocalDate dataAfter, LocalDate dataBefore);

    @Query("SELECT e.data FROM ExplotTempsEntity e WHERE e.data BETWEEN :startDate AND :endDate")
    List<LocalDate> findDatesBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
