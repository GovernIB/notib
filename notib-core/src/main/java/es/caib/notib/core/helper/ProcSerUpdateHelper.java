package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.procediment.ProcSerDataDto;
import es.caib.notib.core.api.service.AuditService.TipusEntitat;
import es.caib.notib.core.api.service.AuditService.TipusOperacio;
import es.caib.notib.core.aspect.Audita;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.entity.ServeiEntity;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.core.repository.ServeiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Helper amb mètode de actualitzar procedimetns auditables
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ProcSerUpdateHelper {
	
	@Autowired
	private ProcedimentRepository procedimentRepository;
	@Autowired
	private ServeiRepository serveiRepository;
	@Autowired
	private OrganGestorRepository organGestorRepository;
	@Autowired
	private ConfigHelper configHelper;

	@Audita(entityType = TipusEntitat.PROCEDIMENT, operationType = TipusOperacio.UPDATE)
	public ProcedimentEntity updateProcediment(
			ProcSerDataDto procedimentGda,
			ProcedimentEntity procediment,
			OrganGestorEntity organGestor) {
		procediment.update(
				procedimentGda.getNom() != null ? procedimentGda.getNom().trim() : null,
				procedimentGda.isComu() ? organGestorRepository.findByCodi(procediment.getEntitat().getDir3Codi()) : organGestor,
				procedimentGda.isComu());
		procediment.updateDataActualitzacio(new Date());
		return procedimentRepository.save(procediment);
	}

	@Audita(entityType = TipusEntitat.PROCEDIMENT, operationType = TipusOperacio.CREATE)
	public ProcedimentEntity nouProcediment(ProcSerDataDto procedimentGda, EntitatEntity entitat, OrganGestorEntity organGestor) {
		ProcedimentEntity procediment;
		procediment = ProcedimentEntity.getBuilder(
				procedimentGda.getCodi(),
				procedimentGda.getNom() != null ? procedimentGda.getNom().trim() : null,
				configHelper.getAsIntByEntitat("es.caib.notib.procediment.alta.auto.retard", 10),
				configHelper.getAsIntByEntitat("es.caib.notib.procediment.alta.auto.caducitat", 15),
				entitat,
				false,
				procedimentGda.isComu() ? organGestorRepository.findByCodi(entitat.getDir3Codi()) : organGestor,
				null,
				null,
				null,
				null,
				procedimentGda.isComu(),
				false).build();
		
		procediment.updateDataActualitzacio(new Date());
		return procedimentRepository.save(procediment);
	}

	@Audita(entityType = TipusEntitat.SERVEI, operationType = TipusOperacio.UPDATE)
	public ServeiEntity updateServei(
			ProcSerDataDto serveiGda,
			ServeiEntity servei,
			OrganGestorEntity organGestor) {
		servei.update(
				serveiGda.getNom() != null ? serveiGda.getNom().trim() : null,
				serveiGda.isComu() ? organGestorRepository.findByCodi(serveiGda.getEntitat().getDir3Codi()) : organGestor,
				serveiGda.isComu());
		servei.updateDataActualitzacio(new Date());
		return serveiRepository.save(servei);
	}

	@Audita(entityType = TipusEntitat.SERVEI, operationType = TipusOperacio.CREATE)
	public ServeiEntity nouServei(ProcSerDataDto serveiGda, EntitatEntity entitat, OrganGestorEntity organGestor) {
		ServeiEntity servei = ServeiEntity.getBuilder(
				serveiGda.getCodi(),
				serveiGda.getNom() != null ? serveiGda.getNom().trim() : null,
				configHelper.getAsIntByEntitat("es.caib.notib.procediment.alta.auto.retard"),
				configHelper.getAsIntByEntitat("es.caib.notib.procediment.alta.auto.caducitat"),
				entitat,
				false,
				serveiGda.isComu() ? organGestorRepository.findByCodi(entitat.getDir3Codi()) : organGestor,
				null,
				null,
				null,
				null,
				serveiGda.isComu(),
				false).build();

		servei.updateDataActualitzacio(new Date());
		return serveiRepository.save(servei);
	}
}
