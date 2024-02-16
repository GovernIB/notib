package es.caib.notib.persist.repository;

import es.caib.notib.persist.entity.ColumnesRemesesEntity;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.UsuariEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColumnesRemesesRepository  extends JpaRepository<ColumnesRemesesEntity, Long> {

    ColumnesRemesesEntity findByEntitatAndUser(EntitatEntity entitat, UsuariEntity usuari);
}
