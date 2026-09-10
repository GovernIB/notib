package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.CacheHelper;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.NotificacioEventHelper;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ResourceNotUpdatedException;
import es.caib.notib.logic.intf.model.ConfigResource;
import es.caib.notib.logic.intf.resourceservice.ConfigResourceService;
import es.caib.notib.logic.intf.service.ConfigService;
import es.caib.notib.logic.objectes.LoggingTipus;
import es.caib.notib.logic.service.ActiveMqServiceImpl;
import es.caib.notib.logic.utils.NotibLogger;
import es.caib.notib.persist.resourceentity.ConfigResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementació del servei de gestió de propietats de configuració.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigResourceServiceImpl extends BaseMutableResourceService<ConfigResource, Long, ConfigResourceEntity> implements ConfigResourceService {

	private final ConfigHelper configHelper;
	private final PluginHelper pluginHelper;
	private final CacheHelper cacheHelper;
	private final NotibLogger logger;
	private final ConfigService configService;

	@Override
	protected void beforeUpdateEntity(ConfigResourceEntity entity, ConfigResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotUpdatedException {

		if (entity.isJbossProperty()) {
			log.error("ATENCIÓ S'ESTÀ INTENTANT GUARDAR UNA PROPIETAT QUE NO ÉS CONFIGURABLE (key={})", entity.getKey());
			throw new ResourceNotUpdatedException(ConfigResource.class, entity.getKey(), "La propietat no és configurable");
		}
	}

	// Mateixos efectes que es fan a ConfigServiceImpl.updateProperty (el mètode que es crida en editar una
	// propietat des del JSP antic): en modificar-se una propietat de configuració des de la interfície de
	// React s'han de recarregar igualment les propietats en memòria i reiniciar els plugins/caches afectats,
	// perquè el canvi tengui efecte immediatament i no només després d'un reinici de l'aplicació.
	@Override
	protected void afterUpdateSave(ConfigResourceEntity entity, ConfigResource resource, Map<String, AnswerRequiredException.AnswerValue> answers, boolean anyOrderChanged) {

		var key = entity.getKey();
		log.info("Actualització valor propietat {} a {}", key, entity.getValue());
		configHelper.reloadDbProperties();
		pluginHelper.resetPlugins(entity.getConfigGroup().getKey());
		NotificacioEventHelper.clearNotificaConsultaActiva();
		cacheHelper.clearAllCaches();
		if ("es.caib.notib.state.machine.delay".equals(key)) {
			configService.carregarDelaysReintentsRemeses();
		}
		if ("es.caib.notib.log.tipus.STATE_MACHINE".equals(key)) {
			ActiveMqServiceImpl.resetJobSchedulerHistoric();
		}
		if (key.contains(NotibLogger.PREFIX)) {
			if (key.endsWith(LoggingTipus.KEYCLOAK.name())) {
				pluginHelper.resetPlugins("USUARIS");
			} else {
				logger.setLogTipus(key);
			}
		}
	}

}
