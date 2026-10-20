package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.model.AvisResource;
import es.caib.notib.logic.intf.resourceservice.AvisResourceService;
import es.caib.notib.persist.resourceentity.AvisResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementació del servei de gestió d'avisos.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AvisResourceServiceImpl extends BaseMutableResourceService<AvisResource, Long, AvisResourceEntity> implements AvisResourceService {

	private final AuthenticationHelper authenticationHelper;
	private final UserSessionHelper userSessionHelper;
//	private final EventHelper eventHelper;

	@Override
	protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries, boolean isSingleResult) {

		var isRolSuper = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_SUPER);
		if (isRolSuper) {
			return "";
		}
		return "entitat.id:" + userSessionHelper.getCurrentEntitatId();
	}

	/**
	 * Qualsevol canvi a la taula d'avisos s'ha de veure a l'instant a les pantalles que ja estan
	 * obertes, i no només a la següent recàrrega: després de desar-lo o esborrar-lo es notifica
	 * per SSE (veure {@link EventHelper#notifyAvisosActius()}, que difereix l'enviament fins que
	 * la transacció s'ha confirmat).
	 */
//	@Override
//	protected void afterCreate(AvisResourceEntity entity, AvisResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
//		eventHelper.notifyAvisosActius();
//	}
//
//	@Override
//	protected void afterUpdate(AvisResourceEntity entity, AvisResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
//		eventHelper.notifyAvisosActius();
//	}
//
//	@Override
//	protected void afterDelete(AvisResourceEntity entity, Map<String, AnswerRequiredException.AnswerValue> answers) {
//		eventHelper.notifyAvisosActius();
//	}


}
