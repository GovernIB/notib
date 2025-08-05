package es.caib.notib.persist.repository.explotacio;

import es.caib.notib.persist.entity.explotacio.ExplotEnvInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.Optional;

public interface ExplotEnvInfoRepository extends JpaRepository<ExplotEnvInfoEntity, Long> {

    Optional<ExplotEnvInfoEntity> findByEnviamentId(Long enviamentId);

    @Query("select e from ExplotEnvInfoEntity e where e.enviament.notificaReferencia = :uuid")
    Optional<ExplotEnvInfoEntity> findByUuid(@Param("uuid") String uuid);

    @Query("SELECT MIN(TRUNC(dataCreacio)) FROM ExplotEnvInfoEntity")
    public Date getFirstDate();

}
