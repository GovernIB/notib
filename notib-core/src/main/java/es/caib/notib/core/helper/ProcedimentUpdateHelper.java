package es.caib.notib.core.helper;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.service.AuditService.TipusEntitat;
import es.caib.notib.core.api.service.AuditService.TipusOperacio;
import es.caib.notib.core.aspect.Audita;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.repository.ProcedimentRepository;

/**
 * Helper amb mètode de actualitzar procedimetns auditables
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ProcedimentUpdateHelper {
	
	@Autowired
	private ProcedimentRepository procedimentRepository;
	
	@Audita(entityType = TipusEntitat.PROCEDIMENT, operationType = TipusOperacio.UPDATE)
	public ProcedimentEntity updateProcediment(
			ProcedimentDto procedimentGda,
			ProcedimentEntity procediment,
			OrganGestorEntity organGestor) {
		procediment.update(
				procedimentGda.getNom(),
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
				procedimentGda.getNom(),
				Integer.parseInt(PropertiesHelper.getProperties().getProperty("es.caib.notib.procediment.alta.auto.retard", "10")),
				Integer.parseInt(PropertiesHelper.getProperties().getProperty("es.caib.notib.procediment.alta.auto.caducitat", "15")),
				entitat,
				null,
				null,
				false,
				organGestor,
				null,
				null,
				null,
				null,
				procedimentGda.isComu()).build();
		
		procediment.updateDataActualitzacio(new Date());
		return procedimentRepository.save(procediment);
	}
	
}
