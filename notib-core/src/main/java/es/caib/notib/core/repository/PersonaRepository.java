package es.caib.notib.core.repository;

import es.caib.notib.core.entity.PersonaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus persona.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PersonaRepository extends JpaRepository<PersonaEntity, Long> {

    List<PersonaEntity> findByEnviamentId(Long enviamentId);
}
