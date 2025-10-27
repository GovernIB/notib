package es.caib.notib.ejb;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.base.exception.*;
import es.caib.notib.logic.intf.base.model.ResourceArtifactType;
import es.caib.notib.logic.intf.resourceservice.EnviamentResourceService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Stateless
@RolesAllowed("**")
public class EnviamentResourceServiceEjb extends AbstractServiceEjb<EnviamentResourceService> implements EnviamentResourceService {
    @Delegate
    private EnviamentResourceService delegateService;

    protected void setDelegateService(EnviamentResourceService delegateService) {
        this.delegateService = delegateService;
    }

    @Override
    public <P extends Serializable> Serializable artifactActionExec(Long id, String code, P params)
            throws ArtifactNotFoundException, ActionExecutionException {
        return delegateService.artifactActionExec(id, code, params);
    }

    @Override
    public <P extends Serializable> List<?> artifactReportGenerateData(Long id, String code, P params)
            throws ArtifactNotFoundException, ReportGenerationException {
        return delegateService.artifactReportGenerateData(id, code, params);
    }

    @Override
    public <P extends Serializable> Map<String, Object> artifactOnChange(ResourceArtifactType type, String code,
                                                                         Long id, P previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers)
            throws ArtifactNotFoundException, ResourceFieldNotFoundException, AnswerRequiredException {
        return delegateService.artifactOnChange(type, code, id, previous, fieldName, fieldValue, answers);
    }

}
