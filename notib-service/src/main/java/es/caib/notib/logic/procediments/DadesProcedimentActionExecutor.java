package es.caib.notib.logic.procediments;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.CaducitatHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.model.ProcedimentResource;
import es.caib.notib.logic.intf.service.GrupService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.persist.resourceentity.ProcedimentResourceEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

/**
 * Acció per a activar un procediment.
 */
@Slf4j
@AllArgsConstructor
public class DadesProcedimentActionExecutor implements BaseMutableResourceService.ActionExecutor<ProcedimentResourceEntity, ProcedimentResource.ProcedimentResourceFilter, DadesProcediment> {

	private final OrganGestorService organGestorService;
	private final GrupService grupService;
	private final UserSessionHelper userSessionHelper;

	@Override
	public DadesProcediment exec(String code, ProcedimentResourceEntity entity, ProcedimentResource.ProcedimentResourceFilter params) throws ActionExecutionException {

		try {
			var dadesProcediment = new DadesProcediment();
			dadesProcediment.setEntregaCieActiva(true);
			dadesProcediment.setOrganCodi(entity.isComu() ? params.getOrganGestor() != null ? params.getOrganGestor().getId() : null : entity.getOrganGestor().getId());
			// entity.getCaducitat() és un camp opcional (Integer, nullable): un procediment sense caducitat
			// configurada hi té null. CaducitatHelper.sumarDiesNaturals(int) desempaqueta l'argument, i
			// DadesProcediment.setCaducitat(Date) crida SimpleDateFormat.format(null) -totes dues coses
			// llencen NullPointerException si es criden amb null en lloc de deixar-ho sense establir.
			if (entity.getCaducitat() != null) {
				dadesProcediment.setCaducitat(CaducitatHelper.sumarDiesNaturals(entity.getCaducitat()));
			}
			dadesProcediment.setCaducitatDiesNaturals(entity.getCaducitat());
			dadesProcediment.setRetard(entity.getRetard());
			dadesProcediment.setAgrupable(entity.isAgrupar());
			if (entity.isAgrupar()) {
				dadesProcediment.setGrups(grupService.findByProcedimentAndUsuariGrups(entity.getId()));
			}
			dadesProcediment.setEntregaCieActiva(entity.isEntregaCieActivaAlgunNivell());
			dadesProcediment.setEntregaCieVigent(entity.isEntregaCieVigent());
			var codi = dadesProcediment.getOrganCodi();
			if (codi != null) {
				var entitatActual = userSessionHelper.getCurrentEntitat();
				var entitatDto = new EntitatDto();
				entitatDto.setId(entitatActual.getId());
				var organ = organGestorService.findById(entitatActual.getId(), Long.valueOf(codi));
				var cieActiuPerPare = organGestorService.entregaCieActivaPerPare(entitatDto, organ.getCodi());
				// NO sobreescriure: cal combinar amb el valor ja establert (entity.isEntregaCieActivaAlgunNivell()),
				// no descartar-lo. Amb una assignació directa, un procediment amb l'entrega CIE activa a nivell
				// propi deixava de mostrar l'opció d'entrega postal si l'òrgan seleccionat no la tenia activa
				// (ni cap dels seus pares), perquè aquest valor "true" quedava sobreescrit per un "false".
				dadesProcediment.setEntregaCieActiva(dadesProcediment.isEntregaCieActiva() || organ.isEntregaCieActiva() || cieActiuPerPare);
			}
			return dadesProcediment;
		} catch (Exception ex) {
			var msg = "Error al activar el procediment " + entity.getId() + ": ";
			log.error(msg, ex);
			throw new ActionExecutionException(ProcedimentResource.class, entity.getId(), code, msg + ex.getMessage(), ex);
		}
	}

	@Override
	public void onChange(Serializable id, ProcedimentResource.ProcedimentResourceFilter previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, ProcedimentResource.ProcedimentResourceFilter target) {
	}
}
