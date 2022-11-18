/**
 * 
 */
package es.caib.notib.core.repository;

import es.caib.notib.core.entity.EntitatEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntitatRepository extends JpaRepository<EntitatEntity, Long> {

	EntitatEntity findById(Long id);

	EntitatEntity findByCodi(String codi);

	EntitatEntity findByDir3Codi(String dir3Codi);

	List<EntitatEntity> findByActiva(boolean activa);

	@Query(	" select " +
			"	distinct e " +
			" from " +
			"     EntitatEntity e " +
			" where " +
			"     e.id in (:ids) ")
	List<EntitatEntity> findByIds(@Param("ids") List<Long> ids);

	@Query(	"from " +
			"     EntitatEntity eu " +
			"where " +
			"    lower(eu.codi) like concat('%', lower(:filtre), '%') " +
			" or lower(eu.nom) like concat('%', lower(:filtre), '%') " +
			" or lower(eu.dir3Codi) like concat('%', lower(:filtre), '%') ")
	Page<EntitatEntity> findByFiltre(@Param("filtre") String filtre, Pageable paginacio);

	@Query(	" select " +
			"	distinct og.entitat.id " +
			" from " +
			"     OrganGestorEntity og " +
			" where " +
			"     og.id in (:organGestorsIds)")
	List<Long> findByOrganGestorsIds( @Param("organGestorsIds") List<Long> organGestorsIds);


	@Query(	" select " +
			"	distinct e " +
			" from " +
			"     EntitatEntity e " +
			" where " +
			"     e.id in (:ids) " +
			" and e.activa = :activa ")
	List<EntitatEntity> findByIdsAndActiva(@Param("ids") List<Long> ids,
										   @Param("activa") boolean activa);

}
