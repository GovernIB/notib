package es.caib.notib.persist.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.notib.persist.entity.cie.PagadorCieEntity;
import es.caib.notib.persist.entity.cie.PagadorCieFormatFullaEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus pagador cie.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PagadorCieFormatFullaRepository extends JpaRepository<PagadorCieFormatFullaEntity, Long> {

	Page<PagadorCieFormatFullaEntity> findByPagadorCie(PagadorCieEntity pagadorCie, Pageable pageable);
	
	List<PagadorCieFormatFullaEntity> findByPagadorCie(PagadorCieEntity pagadorCie);}
