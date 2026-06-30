package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.callbacks.ActivarCallbackPendentActionExecutor;
import es.caib.notib.logic.callbacks.ActivarCallbackPendentMassiuActionExecutor;
import es.caib.notib.logic.callbacks.EnviarCallbackPendentActionExecutor;
import es.caib.notib.logic.callbacks.EnviarCallbackPendentMassiuActionExecutor;
import es.caib.notib.logic.callbacks.PausarCallbackPendentActionExecutor;
import es.caib.notib.logic.callbacks.PausarCallbackPendentMassiuActionExecutor;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.permission.ExtendedPermission;
import es.caib.notib.logic.intf.model.CallbackResource;
import es.caib.notib.logic.intf.resourceservice.CallbackResourceService;
import es.caib.notib.logic.intf.service.CallbackService;
import es.caib.notib.persist.resourcerepository.AplicacioResourceRepository;
import es.caib.notib.persist.resourcerepository.CallbackResourceEntity;
import es.caib.notib.persist.resourcerepository.NotificacioResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementació del servei de gestió d'avisos.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackResourceServiceImpl extends BaseMutableResourceService<CallbackResource, Long, CallbackResourceEntity> implements CallbackResourceService {

	private final NotificacioResourceRepository notificacioRepository;
	private final AplicacioResourceRepository aplicacioRepository;
	private final ConfigHelper configHelper;
	private final CallbackService callbackService;
	private final UserSessionHelper userSessionHelper;
	private final AuthenticationHelper authenticationHelper;
	private final NotibPermissionHelper notibPermissionHelper;


	@PostConstruct
	public void init() {

		register(CallbackResource.ACTION_ENVIAR_CALLBACK_PENDENT, new EnviarCallbackPendentActionExecutor(callbackService));
		register(CallbackResource.ACTION_PAUSAR_CALLBACK_PENDENT, new PausarCallbackPendentActionExecutor(callbackService));
		register(CallbackResource.ACTION_ACTIVAR_CALLBACK_PENDENT, new ActivarCallbackPendentActionExecutor(callbackService));
		register(CallbackResource.ACTION_ENVIAR_CALLBACK_PENDENT_MASSIU, new EnviarCallbackPendentMassiuActionExecutor(callbackService));
		register(CallbackResource.ACTION_ACTIVAR_CALLBACK_PENDENT_MASSIU, new ActivarCallbackPendentMassiuActionExecutor(callbackService));
		register(CallbackResource.ACTION_PAUSAR_CALLBACK_PENDENT_MASSIU, new PausarCallbackPendentMassiuActionExecutor(callbackService));

	}

	@Override
	protected void afterConversion(CallbackResourceEntity entity, CallbackResource resource) {


		var notificacio = notificacioRepository.findById(entity.getNotificacioId()).orElseThrow();
		resource.setNotificacioReferencia(notificacio.getReferencia());
		var aplicacio = aplicacioRepository.findByUsuariCodiAndEntitatId(entity.getUsuariCodi(), notificacio.getEntitat().getId());
		if (aplicacio != null) {
			resource.setEndpoint(aplicacio.getCallbackUrl());
		}
		var maxIntents = configHelper.getConfigAsInteger("es.caib.notib.tasca.callback.pendents.notifica.events.intents.max");
		resource.setMaxIntents(maxIntents);
	}


	@Override
	protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {

		// Condició per a mostrar només els callbackss de l'entitat actual
		var entitatFilter = "notificacio.entitat.id:" + userSessionHelper.getCurrentEntitatId();
		var isRoleAdmin = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN);
		var isRoleAdminLectura = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN_LECTURA);
		var isRoleAdminOrgan = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ORGAN);
		if ((isRoleAdmin && notibPermissionHelper.currentEntitatPermissionAllowed(ExtendedPermission.PERM2)) ||
			(isRoleAdminLectura && notibPermissionHelper.currentEntitatPermissionAllowed(ExtendedPermission.PERMX))) {
			return entitatFilter;
		}
		if (isRoleAdminOrgan && notibPermissionHelper.currentOrganGestorPermissionAllowed(BasePermission.ADMINISTRATION)) {
			return entitatFilter + " and notificacio.organGestor.id:" + userSessionHelper.getCurrentOrganGestorId();
		}
		List<String> andConditions = new ArrayList<>();
		andConditions.add(entitatFilter);
		return String.join(" and ", andConditions);
	}

}
