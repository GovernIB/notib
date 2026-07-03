package es.caib.notib.logic.cacheable;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.dto.RespostaAccio;
import es.caib.notib.logic.intf.model.CacheResource;
import es.caib.notib.logic.intf.model.SeleccioStringForm;
import es.caib.notib.logic.intf.service.CacheService;
import es.caib.notib.persist.resourceentity.CacheResourceEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class BuidarCacheActionExecutor implements BaseMutableResourceService.ActionExecutor<CacheResourceEntity, SeleccioStringForm, Serializable> {

	private final CacheService cacheService;


	@Override
	public Serializable exec(String code, CacheResourceEntity entity, SeleccioStringForm params) throws ActionExecutionException {

		try {
			if (params == null || params.getIds() == null) {
				throw new ActionExecutionException(CacheResource.class, null, code, "La selecció no pot ser buida");
			}
			for (var seleccio : params.getIds()) {
				cacheService.removeCache(seleccio);
			}
			return RespostaAccio.builder().build();
		} catch (Exception ex) {
			var msg = "Error inesperat buidant la cache ";
			log.error("[BuidarCacheActionExecutor] " + msg + " - " + ex.getMessage());
			throw new ActionExecutionException(CacheResource.class, null, code, msg + ex.getMessage());
		}
	}

	@Override
	public void onChange(Serializable id, SeleccioStringForm previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, SeleccioStringForm target) {

	}

}
