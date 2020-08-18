package es.caib.notib.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.PagadorPostalEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus pagador postal.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PagadorPostalRepository extends JpaRepository<PagadorPostalEntity, Long> {

	PagadorPostalEntity findByDir3codi(String dir3codi);
	
	@Query(	"from " +
			"    PagadorPostalEntity b " +
			"where " +
			"(:esNullFiltreCodi = true or b.dir3codi = :dir3codi) " +
			"and (:esNullFiltreNumContracte = true or b.contracteNum = :numContracte) " + 
			"and (:esNullFiltreOrganGestor = true or b.organGestor.codi in (:organsGestors)) " +
			"and b.entitat = :entitat")
	Page<PagadorPostalEntity> findByCodiDir3AndNumContacteNotNullFiltrePaginatAndEntitat(
			@Param("esNullFiltreCodi") boolean esNullFiltreDir3codi,
			@Param("dir3codi") String dir3codi, 
			@Param("esNullFiltreNumContracte") boolean esNullFiltreNumContracte,
			@Param("numContracte") String filtreNumContracte,
			@Param("esNullFiltreOrganGestor") boolean esNullOrganGestor,
//			@Param("organGestorId") Long organGestorId,
			@Param("organsGestors") List<String> organsGestors,
			@Param("entitat") EntitatEntity entitat,
			Pageable paginacio);
	
	List<PagadorPostalEntity> findByEntitat(EntitatEntity entitat);

	List<PagadorPostalEntity> findByEntitatIdAndOrganGestorCodiIn(Long entitatId, List<String> organsFills);
	
}
