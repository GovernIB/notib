package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.procediment.ProcedimentDto;
import es.caib.notib.core.api.service.AuditService.TipusEntitat;
import es.caib.notib.core.api.service.AuditService.TipusOperacio;
import es.caib.notib.core.aspect.Audita;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.repository.ProcedimentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Helper amb mètode de actualitzar procedimetns auditables
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ProcedimentUpdateHelper {
	
	@Autowired
	private ProcedimentRepository procedimentRepository;
	@Autowired
	private ConfigHelper configHelper;

	@Audita(entityType = TipusEntitat.PROCEDIMENT, operationType = TipusOperacio.UPDATE)
	public ProcedimentEntity updateProcediment(
			ProcedimentDto procedimentGda,
			ProcedimentEntity procediment,
			OrganGestorEntity organGestor) {
		procediment.update(
				procedimentGda.getNom() != null ? procedimentGda.getNom().trim() : null,
				organGestor,
				procedimentGda.isComu());
		procediment.updateDataActualitzacio(new Date());
		return procedimentRepository.save(procediment);
	}

	@Audita(entityType = TipusEntitat.PROCEDIMENT, operationType = TipusOperacio.CREATE)
	public ProcedimentEntity nouProcediment(ProcedimentDto procedimentGda, EntitatEntity entitat, OrganGestorEntity organGestor) {
		ProcedimentEntity procediment;
		procediment = ProcedimentEntity.getBuilder(
				procedimentGda.getCodi(),
				procedimentGda.getNom() != null ? procedimentGda.getNom().trim() : null,
				configHelper.getAsInt("es.caib.notib.procediment.alta.auto.retard"),
				configHelper.getAsInt("es.caib.notib.procediment.alta.auto.caducitat"),
				entitat,
				null,
				false,
				organGestor,
				null,
				null,
				null,
				null,
				procedimentGda.isComu(),
				false).build();
		
		procediment.updateDataActualitzacio(new Date());
		return procedimentRepository.save(procediment);
	}
	
}
