package es.caib.notib.persist.repository;

import es.caib.notib.persist.entity.cie.EntregaCieEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus pagador postal.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntregaCieRepository extends JpaRepository<EntregaCieEntity, Long> {

}
