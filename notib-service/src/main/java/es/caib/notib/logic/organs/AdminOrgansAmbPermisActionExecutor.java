package es.caib.notib.logic.organs;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.dto.organisme.OrgansAmbPermis;
import es.caib.notib.logic.intf.model.OrganGestorResource;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class AdminOrgansAmbPermisActionExecutor implements BaseMutableResourceService.ActionExecutor<OrganGestorResourceEntity, Long, OrgansAmbPermis> {

	private final OrganGestorService organGestorService;
	private final UserSessionHelper userSessionHelper;

	@Override
	public OrgansAmbPermis exec(String code, OrganGestorResourceEntity entity, Long params) throws ActionExecutionException {

		var entitatActual = userSessionHelper.getCurrentEntitatId();
		var organs = organGestorService.findAccessiblesByUsuariAndEntitatActual(entitatActual);
		List<OrganGestorResource> organsAmbPermis = new ArrayList<>();
		OrganGestorResource organAmbPermis;
		for (var organ : organs) {
			organAmbPermis = new OrganGestorResource();
			organAmbPermis.setId(organ.getId());
			organAmbPermis.setCodi(organ.getCodi());
			organAmbPermis.setNom(organ.getNom());
			organsAmbPermis.add(organAmbPermis);
		}
		return OrgansAmbPermis.builder().organs(organsAmbPermis).build();
	}

	@Override
	public void onChange(Serializable id, Long previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Long target) {

	}

}
