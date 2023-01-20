package es.caib.notib.persist.repository;

import es.caib.notib.persist.entity.OficinaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OficinaRepository extends JpaRepository<OficinaEntity, String> {

    OficinaEntity findByCodi(String codi);

    List<OficinaEntity> findByEntitat_Dir3CodiAndSirIsTrue(String dir3Codi);

    List<OficinaEntity> findByOrganGestorCodiAndSirIsTrue(String organ);

}