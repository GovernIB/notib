package es.caib.notib.persist.repository.explotacio;

import es.caib.notib.persist.entity.explotacio.ExplotEnvBasicStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExplotEnvBasicStatsRepository extends JpaRepository<ExplotEnvBasicStatsEntity, Long> {

    List<ExplotEnvBasicStatsEntity> findByDiaBetween(LocalDate from, LocalDate to);

}
