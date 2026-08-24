package es.caib.notib.logic.entitat;

import com.google.common.base.Strings;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.dto.LlibreDto;
import es.caib.notib.logic.intf.dto.OficinaDto;
import es.caib.notib.logic.intf.dto.entitat.EntitatActionParams;
import es.caib.notib.logic.intf.model.EntitatResource;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class OficinaEntitatActionExecutor implements BaseMutableResourceService.ActionExecutor<EntitatResourceEntity, EntitatActionParams, OficinesEntitat> {

	private final EntitatService entitatService;

	@Override
	public OficinesEntitat exec(String code, EntitatResourceEntity entity, EntitatActionParams params) throws ActionExecutionException {

		try {
			if (params == null || Strings.isNullOrEmpty(params.getCodiDir3())) {
				return OficinesEntitat.builder().oficines(new ArrayList<>()).build();
			}
			var oficines  = entitatService.findOficinesEntitat(params.getCodiDir3());
			return OficinesEntitat.builder().oficines(oficines).build();
		} catch (Exception ex) {
			var msg = "Error inesperat obtinguent el llibre per entitat";
			log.error("[OficinaEntitatActionExecutor] " + msg);
			throw new ActionExecutionException(EntitatResource.class, null, "-1", msg + ": " + ex.getMessage());
		}
	}

	@Override
	public void onChange(Serializable id, EntitatActionParams previous, String fieldName, Object fieldValue, Map answers, String[] previousFieldNames, EntitatActionParams target) {

	}

}
