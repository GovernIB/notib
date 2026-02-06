package es.caib.notib.persist.repository;

import es.caib.notib.persist.entity.SincronizarEnvioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SincronizarEnvioRepository extends JpaRepository<SincronizarEnvioEntity, Long> {
}
