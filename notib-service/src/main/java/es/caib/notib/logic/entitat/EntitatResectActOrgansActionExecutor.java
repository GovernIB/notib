package es.caib.notib.logic.entitat;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.CacheHelper;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.dto.RespostaActionExecutor;
import es.caib.notib.logic.intf.dto.entitat.EntitatActionParams;
import es.caib.notib.logic.intf.model.EntitatResource;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class EntitatResectActOrgansActionExecutor implements BaseMutableResourceService.ActionExecutor<EntitatResourceEntity, EntitatActionParams, RespostaActionExecutor> {

	private final EntitatService entitatService;

	@Override
	public RespostaActionExecutor exec(String code, EntitatResourceEntity entity, EntitatActionParams params) throws ActionExecutionException {

		try {
			entitatService.resetActualitzacioOrgans(entity.getId());
			return RespostaActionExecutor.builder().ok(true).build();
		} catch (Exception ex) {
			var msg = "Error inesperat resetejant l'actualització d'organs";
			log.error("[EntitatResectActOrgansActionExecutor] " + msg);
			throw new ActionExecutionException(EntitatResource.class, null, "-1", msg + ": " + ex.getMessage());
		}
	}

	@Override
	public void onChange(Serializable id, EntitatActionParams previous, String fieldName, Object fieldValue, Map answers, String[] previousFieldNames, EntitatActionParams target) {

	}

}
