/**
 * 
 */
package es.caib.notib.core.repository.config;

import es.caib.notib.core.entity.config.ConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus notificacio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConfigRepository extends JpaRepository<ConfigEntity, String> {

    ConfigEntity findByKeyAndEntitatCodi(String key, String entitatCodi);

    List<ConfigEntity> findByEntitatCodiIsNull();

    List<ConfigEntity> findByKeyContainsKeyAndEntitatCodiIsNotNull(String key);

    @Transactional
    @Modifying
    @Query("DELETE FROM ConfigEntity c WHERE c.entitatCodi = :entitatCodi")
    int deleteByEntitatCodi(@Param("entitatCodi") String entitatCodi);

}