package es.caib.notib.persist.repository;

import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.GrupEntity;
import es.caib.notib.persist.entity.GrupProcSerEntity;
import es.caib.notib.persist.entity.ProcSerEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus grups procediment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface GrupProcSerRepository extends JpaRepository<GrupProcSerEntity, Long> {

	public List<GrupProcSerEntity> findByGrup(GrupEntity grup);
	public List<GrupProcSerEntity> findByProcSer(ProcSerEntity procSer);
	public List<GrupProcSerEntity> findByProcSer(ProcSerEntity procSer, Pageable paginacio);
	public GrupProcSerEntity findByGrupAndProcSer(GrupEntity grup, ProcSerEntity procSer);
	public List<GrupProcSerEntity> findByProcSerEntitat(EntitatEntity entitat);

	@Query(
			"select distinct g.codi " +
			"  from GrupProcSerEntity gp " +
			"  left outer join gp.procSer p " +
			"  left outer join gp.grup g " +
			" where p.codi in (:procedimentCodis) " +
			"	and p.entitat = :entitat ")
	public List<String> getGrupCodisByProcSerIds(
			@Param("procedimentCodis") List<String> procedimentCodis,
			@Param("entitat") EntitatEntity entitat);
}