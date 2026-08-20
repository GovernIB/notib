package es.caib.notib.logic.procSer;

import es.caib.notib.logic.base.service.BaseReadonlyResourceService;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.model.ProcedimentResource;
import es.caib.notib.persist.resourcerepository.OrganGestorResourceRepository;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/*
 * Lògica onChange pel camp comu. Segons el valor d'aquest camp canvien els camps visibles / habilitats.
 */
@RequiredArgsConstructor
public class ComuOnChangeLogicProcessor implements BaseReadonlyResourceService.OnChangeLogicProcessor<ProcedimentResource> {

	private final OrganGestorResourceRepository organGestorResourceRepository;
	private final UserSessionHelper userSessionHelper;

	@Override
	public void onChange(Serializable id, ProcedimentResource previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, ProcedimentResource target) {

		if (!(boolean)fieldValue) {
			target.setFieldEntregaCieHidden(false);
			target.setFieldOrganGestorDisabled(false);
			target.setOrganGestor(null);
			return;
		}
		target.setFieldEntregaCieHidden(true);
		target.setFieldOrganGestorDisabled(true);
		var entitat = userSessionHelper.getCurrentEntitat();
		var organEntitat = organGestorResourceRepository.findByEntitatAndCodi(entitat, entitat.getDir3Codi()).orElseThrow();
		target.setOrganGestor(ResourceReference.toResourceReference(organEntitat.getId(), organEntitat.getCodi() + ", " + organEntitat.getNom()));
	}
}
