package es.caib.notib.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.notib.core.entity.PersonaEntity;

public interface PersonaRepository extends JpaRepository<PersonaEntity, Long> {

}
