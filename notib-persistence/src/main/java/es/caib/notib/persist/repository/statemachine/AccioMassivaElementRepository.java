package es.caib.notib.persist.repository.statemachine;

import es.caib.notib.persist.entity.AccioMassivaElementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccioMassivaElementRepository extends JpaRepository<AccioMassivaElementEntity, Long>  {

    @Query("from AccioMassivaElementEntity e where e.accioMassiva.id = :accioMassivaId and e.elementId = :accioMassivaElementId")
    AccioMassivaElementEntity getByAccioMassivaIdElementId(@Param("accioMassivaId") Long accioMassivaId, @Param("accioMassivaElementId") Long accioMassivaElementId);
}
