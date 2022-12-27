/**
 * 
 */
package es.caib.notib.core.repository;

import es.caib.notib.core.entity.OficinaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base de dades del tipus oficina.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface OficinaRepository extends JpaRepository<OficinaEntity, String> {
	
	OficinaEntity findByCodi(String codi);

	List<OficinaEntity> findByEntitat_Dir3CodiAndSirIsTrue(String dir3Codi);

	List<OficinaEntity> findByOrganGestorCodiAndSirIsTrue(String organ);

}
