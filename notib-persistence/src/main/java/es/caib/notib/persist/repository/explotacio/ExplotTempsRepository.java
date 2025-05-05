package es.caib.notib.persist.repository.explotacio;

import es.caib.notib.persist.entity.explotacio.ExplotTempsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ExplotTempsRepository extends JpaRepository<ExplotTempsEntity, Long> {

    ExplotTempsEntity findFirstByData(LocalDate data);
}
