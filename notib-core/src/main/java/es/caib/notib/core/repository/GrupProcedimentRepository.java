package es.caib.notib.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.caib.notib.core.entity.GrupEntity;
import es.caib.notib.core.entity.GrupProcedimentEntity;
import es.caib.notib.core.entity.ProcedimentEntity;

public interface GrupProcedimentRepository extends JpaRepository<GrupProcedimentEntity, Long> {

	
	public GrupProcedimentEntity findByGrup(GrupEntity grup);
	public List<GrupProcedimentEntity> findByProcediment(ProcedimentEntity procediment);
	public GrupProcedimentEntity findByGrupAndProcediment(GrupEntity grup, ProcedimentEntity procediment);
}
