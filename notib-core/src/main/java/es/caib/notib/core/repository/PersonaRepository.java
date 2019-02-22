package es.caib.notib.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.notib.core.entity.PersonaEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus persona.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PersonaRepository extends JpaRepository<PersonaEntity, Long> {

}
