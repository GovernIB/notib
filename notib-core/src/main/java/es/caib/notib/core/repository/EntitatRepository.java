/**
 * 
 */
package es.caib.notib.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.notib.core.entity.EntitatEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntitatRepository extends JpaRepository<EntitatEntity, Long> {

	EntitatEntity findByCodi(String codi);

	EntitatEntity findByDir3Codi(String dir3Codi);

	List<EntitatEntity> findByActiva(boolean activa);

	@Query(	"from " +
			"     EntitatEntity eu " +
			"where " +
			"    lower(eu.codi) like concat('%', lower(:filtre), '%') " +
			" or lower(eu.nom) like concat('%', lower(:filtre), '%') " +
			" or lower(eu.dir3Codi) like concat('%', lower(:filtre), '%') ")
	public Page<EntitatEntity> findByFiltre(
			@Param("filtre") String filtre,
			Pageable paginacio);

}
