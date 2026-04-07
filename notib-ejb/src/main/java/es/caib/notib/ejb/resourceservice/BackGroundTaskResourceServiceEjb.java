package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ArtifactNotFoundException;
import es.caib.notib.logic.intf.base.exception.ReportGenerationException;
import es.caib.notib.logic.intf.base.exception.ResourceFieldNotFoundException;
import es.caib.notib.logic.intf.base.model.ResourceArtifactType;
import es.caib.notib.logic.intf.resourceservice.BackGroundTaskResourceService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Stateless
@RolesAllowed("**")
public class BackGroundTaskResourceServiceEjb extends AbstractServiceEjb<BackGroundTaskResourceService> implements BackGroundTaskResourceService {

	@Delegate private BackGroundTaskResourceService delegateService;

	protected void setDelegateService(BackGroundTaskResourceService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	public <P extends Serializable> Map<String, Object> artifactOnChange(ResourceArtifactType type, String code,
																		 String id, P previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers)
			throws ArtifactNotFoundException, ResourceFieldNotFoundException, AnswerRequiredException {
		return delegateService.artifactOnChange(type, code, id, previous, fieldName, fieldValue, answers);
	}

	@Override
	public <P extends Serializable> List<?> artifactReportGenerateData(String id, String code, P params)
			throws ArtifactNotFoundException, ReportGenerationException {
		return delegateService.artifactReportGenerateData(id, code, params);
	}

	@Override
	public <P extends Serializable> Serializable artifactActionExec(String id, String code, P params)
			throws ArtifactNotFoundException, ActionExecutionException {
		return delegateService.artifactActionExec(id, code, params);
	}
}
