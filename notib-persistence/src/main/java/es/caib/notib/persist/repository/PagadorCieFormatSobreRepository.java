package es.caib.notib.persist.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.notib.persist.entity.cie.PagadorCieEntity;
import es.caib.notib.persist.entity.cie.PagadorCieFormatSobreEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus pagador cie.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PagadorCieFormatSobreRepository extends JpaRepository<PagadorCieFormatSobreEntity, Long> {

	Page<PagadorCieFormatSobreEntity> findByPagadorCie(PagadorCieEntity pagadorCie, Pageable pageable);
	
	List<PagadorCieFormatSobreEntity> findByPagadorCie(PagadorCieEntity pagadorCie);

	@Modifying
	@Query(value = "UPDATE NOT_FORMATS_SOBRE " +
			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
			nativeQuery = true)
	int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);

}
