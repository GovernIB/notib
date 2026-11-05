package es.caib.notib.logic.avisos;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.dto.RespostaActionExecutor;
import es.caib.notib.logic.intf.model.AvisResource;
import es.caib.notib.logic.intf.resourceservice.SseEventService;
import es.caib.notib.persist.resourceentity.AvisResourceEntity;
import es.caib.notib.persist.resourcerepository.AvisResourceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class AvisActivarDesactivarActionExecutor implements BaseMutableResourceService.ActionExecutor<AvisResourceEntity, Serializable, RespostaActionExecutor> {

    private final AvisResourceRepository avisResourceRepository;
    private final SseEventService sseEventService;

    @Override
    public RespostaActionExecutor exec(String code, AvisResourceEntity entity, Serializable params) throws ActionExecutionException {
        try {
            entity.setActiu(!Boolean.TRUE.equals(entity.getActiu()));
            avisResourceRepository.save(entity);
            sseEventService.notifyAvisosActius();
            return RespostaActionExecutor.builder().ok(true).build();
        } catch (Exception ex) {
            var msg = "Error inesperat activant/desactivant l'avís";
            log.error("[AvisActivarDesactivarActionExecutor] " + msg);
            throw new ActionExecutionException(AvisResource.class, null, "-1", msg + ": " + ex.getMessage());
        }
    }

    @Override
    public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map answers, String[] previousFieldNames, Serializable target) {
    }
}
