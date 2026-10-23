package es.caib.notib.logic.config;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.dto.RespostaAccio;
import es.caib.notib.logic.intf.model.AccioMassivaParams;
import es.caib.notib.logic.intf.model.AvisResource;
import es.caib.notib.logic.intf.model.CacheResource;
import es.caib.notib.logic.intf.service.ConfigService;
import es.caib.notib.persist.resourceentity.AvisResourceEntity;
import es.caib.notib.persist.resourceentity.ConfigResourceEntity;
import es.caib.notib.persist.resourcerepository.AvisResourceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

/**
 * Accions massives sobre avisos: activar, desactivar i esborrar.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@AllArgsConstructor
public class SyncJBossActionExecutor implements BaseMutableResourceService.ActionExecutor<ConfigResourceEntity, AccioMassivaParams, Serializable> {

	private final ConfigService configService;

	@Override
	public Serializable exec(String code, ConfigResourceEntity entity, AccioMassivaParams params) throws ActionExecutionException {

		try {

			var editedProperties = configService.syncFromJBossProperties();
//			return SyncResponse.builder().status(true).editedProperties(editedProperties).build();
			return RespostaAccio.builder().build();
		} catch (Exception ex) {
			var msg = "Error inesperat sincronitzant les propietats amb JBOSS ";
			log.error("[SyncJBossActionExecutor] " + msg + " - " + ex.getMessage());
			throw new ActionExecutionException(CacheResource.class, null, code, msg + ex.getMessage());
		}
	}

	@Override
	public void onChange(Serializable id, AccioMassivaParams previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, AccioMassivaParams target) {
	}
}
