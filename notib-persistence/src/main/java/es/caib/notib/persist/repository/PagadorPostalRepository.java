package es.caib.notib.persist.repository;

import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.cie.PagadorPostalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus pagador postal.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PagadorPostalRepository extends JpaRepository<PagadorPostalEntity, Long> {

//	PagadorPostalEntity findByOrganismePagadorCodi(String organismePagador);
	List<PagadorPostalEntity> findByEntitat(EntitatEntity entitat);

	@Query("SELECT n.entregaCie.operadorPostal FROM EntitatEntity n where n = :entitat")
	PagadorPostalEntity obtenirPagadorsEntitat(@Param("entitat") EntitatEntity entitat);

//	@Query("FROM PagadorPostalEntity nap " +
//			"JOIN EntregaCieEntity nec on nec.operadorPostalId = nap.id " +
//			"JOIN EntitatEntity n ON n.entregaCie = nec and n = :entitat")
//	IdentificadorTextDto obtenirPagadorsEntitat(@Param("entitat") EntitatEntity entitat);


	List<PagadorPostalEntity> findByContracteDataVigGreaterThanEqual(Date llindar);

	List<PagadorPostalEntity> findByEntitatAndContracteDataVigGreaterThanEqual(EntitatEntity entitat, Date llindar);
//	List<PagadorPostalEntity> findByEntitatAndOrganismePagadorAndContracteDataVigGreaterThanEqual(EntitatEntity entitat, OrganGestorEntity organ, Date llindar);
	List<PagadorPostalEntity> findByEntitatAndOrganGestorAndContracteDataVigGreaterThanEqual(EntitatEntity entitat, OrganGestorEntity organ, Date llindar);

	List<PagadorPostalEntity> findByEntitatIdAndOrganGestorCodiIn(Long entitatId, List<String> organsFills);
	List<PagadorPostalEntity> findByOrganGestorId(Long organGestorId);

	@Query(	"from PagadorPostalEntity b " +
			"where " +
			"	 (:esNullFiltreOrganismePagador = true or lower(b.organGestor.codi) like lower('%'||:organismePagadorCodi||'%')) " +
			"and (:esNullFiltreNumContracte = true or lower(b.contracteNum) like lower('%'||:numContracte||'%')) " +
			"and b.entitat = :entitat")
	Page<PagadorPostalEntity> findByCodiDir3AndNumContacteNotNullFiltrePaginatAndEntitat(
			@Param("esNullFiltreOrganismePagador") boolean esNullFiltreOrganismePagador,
			@Param("organismePagadorCodi") String organismePagadorCodi,
			@Param("esNullFiltreNumContracte") boolean esNullFiltreNumContracte,
			@Param("numContracte") String filtreNumContracte,
			@Param("entitat") EntitatEntity entitat,
			Pageable paginacio);

	@Query(	"from PagadorPostalEntity b " +
			"where " +
			"	 (:esNullFiltreOrganismePagador = true or lower(b.organGestor.codi) like lower('%'||:organismePagadorCodi||'%')) " +
			"and (:esNullFiltreNumContracte = true or lower(b.contracteNum) like lower('%'||:numContracte||'%')) " +
			"and (b.organGestor.codi in (:organsGestors)) " +
			"and b.entitat = :entitat")
	Page<PagadorPostalEntity> findByCodiDir3AndNumContacteNotNullFiltrePaginatAndEntitatWithOrgan(
			@Param("esNullFiltreOrganismePagador") boolean esNullFiltreOrganismePagador,
			@Param("organismePagadorCodi") String organismePagadorCodi,
			@Param("esNullFiltreNumContracte") boolean esNullFiltreNumContracte,
			@Param("numContracte") String filtreNumContracte,
			@Param("organsGestors") List<String> organsGestors,
			@Param("entitat") EntitatEntity entitat,
			Pageable paginacio);
}
