package es.caib.notib.persist.repository.statemachine;

import es.caib.notib.persist.entity.AccioMassivaElementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccioMassivaElementRepository extends JpaRepository<AccioMassivaElementEntity, Long>  {

    AccioMassivaElementEntity findByElementId(Long accioMassivaElemId);
}
