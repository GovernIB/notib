package es.caib.notib.logic.accionsMassives;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.model.AccioMassivaResource;
import es.caib.notib.persist.resourceentity.AccioMassivaResourceEntity;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Slf4j
public class AccioMassivaActionExecutor implements BaseMutableResourceService.ActionExecutor<AccioMassivaResourceEntity, Serializable, AccioMassivaResource> {

	@Override
	public AccioMassivaResource exec(String code, AccioMassivaResourceEntity entity, Serializable params) throws ActionExecutionException {

		log.info("action exec");
		return null;
	}

	@Override
	public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map answers, String[] previousFieldNames, Serializable target) {

	}

}
