/**
 * 
 */
package es.caib.notib.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.caib.notib.core.entity.ColumnesEntity;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.UsuariEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus columnes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ColumnesRepository extends JpaRepository<ColumnesEntity, Long> {

	ColumnesEntity findByEntitatAndUser(
			EntitatEntity entitat,
			UsuariEntity usuari);
}
