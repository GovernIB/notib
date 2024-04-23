package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.dto.procediment.ProcSerDataDto;
import es.caib.notib.logic.intf.service.AuditService.TipusEntitat;
import es.caib.notib.logic.intf.service.AuditService.TipusOperacio;
import es.caib.notib.logic.aspect.Audita;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.ProcedimentEntity;
import es.caib.notib.persist.entity.ServeiEntity;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.ProcedimentRepository;
import es.caib.notib.persist.repository.ServeiRepository;
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
	public ProcedimentEntity updateProcediment(ProcSerDataDto procedimentGda, ProcedimentEntity procediment, OrganGestorEntity organGestor) {

		var o = procedimentGda.isComu() ? organGestorRepository.findByCodi(procediment.getEntitat().getDir3Codi()) : organGestor;
		procediment.update(procedimentGda.getNom() != null ? procedimentGda.getNom().trim() : null, o, procedimentGda.isComu());
		procediment.updateDataActualitzacio(new Date());
		return procedimentRepository.save(procediment);
	}

	@Audita(entityType = TipusEntitat.PROCEDIMENT, operationType = TipusOperacio.CREATE)
	public ProcedimentEntity nouProcediment(ProcSerDataDto procedimentGda, EntitatEntity entitat, OrganGestorEntity organGestor) {

		var retard = configHelper.getConfigAsInteger("es.caib.notib.procediment.alta.auto.retard");
		var caducitat = configHelper.getConfigAsInteger("es.caib.notib.procediment.alta.auto.caducitat");
		var nom  =  procedimentGda.getNom() != null ? procedimentGda.getNom().trim() : null;
		var organ = procedimentGda.isComu() ? organGestorRepository.findByCodi(entitat.getDir3Codi()) : organGestor;
		var procediment = ProcedimentEntity.getBuilder(procedimentGda.getCodi(), nom, retard, caducitat, entitat, false, organ,
				null, null, null, null, procedimentGda.isComu(),
				false, procedimentGda.isManual()).build();
		
		procediment.updateDataActualitzacio(new Date());
		return procedimentRepository.save(procediment);
	}

	@Audita(entityType = TipusEntitat.SERVEI, operationType = TipusOperacio.UPDATE)
	public ServeiEntity updateServei(ProcSerDataDto serveiGda, ServeiEntity servei, OrganGestorEntity organGestor) {

		var organ = serveiGda.isComu() ? organGestorRepository.findByCodi(serveiGda.getEntitat().getDir3Codi()) : organGestor;
		servei.update(serveiGda.getNom() != null ? serveiGda.getNom().trim() : null, organ, serveiGda.isComu());
		servei.updateDataActualitzacio(new Date());
		return serveiRepository.save(servei);
	}

	@Audita(entityType = TipusEntitat.SERVEI, operationType = TipusOperacio.CREATE)
	public ServeiEntity nouServei(ProcSerDataDto serveiGda, EntitatEntity entitat, OrganGestorEntity organGestor) {

		var retard = configHelper.getConfigAsInteger("es.caib.notib.procediment.alta.auto.retard");
		var caducitat = configHelper.getConfigAsInteger("es.caib.notib.procediment.alta.auto.caducitat");
		var nom = serveiGda.getNom() != null ? serveiGda.getNom().trim() : null;
		var organ = serveiGda.isComu() ? organGestorRepository.findByCodi(entitat.getDir3Codi()) : organGestor;
		var servei = ServeiEntity.getBuilder(serveiGda.getCodi(), nom, retard, caducitat, entitat, false, organ,
				null, null, null, null, serveiGda.isComu(),
				false, serveiGda.isManual()).build();

		servei.updateDataActualitzacio(new Date());
		return serveiRepository.save(servei);
	}
}