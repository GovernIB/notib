package es.caib.notib.persist.repository;

import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.cie.EntregaPostalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EntregaPostalRepository extends JpaRepository<EntregaPostalEntity, Long>  {

    Optional<EntregaPostalEntity> findTopByCieIdNotNullOrderByIdDesc();
}
