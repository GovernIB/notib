package es.caib.notib.core.repository;

import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.cie.PagadorCieEntity;
import es.caib.notib.core.entity.cie.PagadorPostalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus pagador cie.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PagadorCieRepository extends JpaRepository<PagadorCieEntity, Long> {

	List<PagadorCieEntity> findByContracteDataVigGreaterThanEqual(Date llindar);

	List<PagadorCieEntity> findByEntitatAndContracteDataVigGreaterThanEqual(EntitatEntity entitat, Date llindar);

	List<PagadorCieEntity> findByEntitatAndOrganGestorAndContracteDataVigGreaterThanEqual(EntitatEntity entitat, OrganGestorEntity organ, Date llindar);

	@Query("SELECT n.entregaCie.cie FROM EntitatEntity n where n = :entitat")
	PagadorCieEntity obtenirPagadorsEntitat(@Param("entitat") EntitatEntity entitat);

	List<PagadorCieEntity> findByEntitat(EntitatEntity entitat);
	public List<PagadorCieEntity> findByEntitatIdAndOrganGestorCodiIn(Long entitatId, List<String> organsFills);
	public List<PagadorCieEntity> findByOrganGestor(OrganGestorEntity organGestorId);
	
	@Query(	"from " +
			"    PagadorCieEntity b " +
			"where " +
			"(:esNullFiltreOrganismePagador = true or b.organGestor.codi = :organismePagador) " +
			"and b.entitat = :entitat")
	public Page<PagadorCieEntity> findByCodiDir3NotNullFiltrePaginatAndEntitat(
			@Param("esNullFiltreOrganismePagador") boolean esNullFiltreOrganismePagador,
			@Param("organismePagador") String organismePagador,
			@Param("entitat") EntitatEntity entitat,
			Pageable paginacio);
	
	@Query(	"from " +
			"    PagadorCieEntity b " +
			"where " +
			"(:esNullFiltreOrganismePagador = true or b.organGestor.codi = :organismePagador) " +
			"and (b.organGestor.codi in (:organsGestors)) " +
			"and b.entitat = :entitat")
	public Page<PagadorCieEntity> findByCodiDir3NotNullFiltrePaginatAndEntitatWithOrgan(
			@Param("esNullFiltreOrganismePagador") boolean esNullFiltreOrganismePagador,
			@Param("organismePagador") String organismePagador,
			@Param("organsGestors") List<String> organsGestors,
			@Param("entitat") EntitatEntity entitat,
			Pageable paginacio);
	
	
}
