package es.caib.notib.core.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.notib.core.entity.GrupEntity;
import es.caib.notib.core.entity.GrupProcedimentEntity;
import es.caib.notib.core.entity.ProcedimentEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus grups procediment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface GrupProcedimentRepository extends JpaRepository<GrupProcedimentEntity, Long> {

	public GrupProcedimentEntity findByGrup(GrupEntity grup);
	public List<GrupProcedimentEntity> findByProcediment(ProcedimentEntity procediment);
	public List<GrupProcedimentEntity> findByProcediment(ProcedimentEntity procediment,Pageable paginacio);
	public GrupProcedimentEntity findByGrupAndProcediment(GrupEntity grup, ProcedimentEntity procediment);
}
