package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.enviaments.CancelarEntregaPostalActionExecutor;
import es.caib.notib.logic.enviaments.DiagramaStateMachineReportGenerator;
import es.caib.notib.logic.enviaments.EntregaPostalCertificacioReportGenerator;
import es.caib.notib.logic.enviaments.EntregaPostalPerspectiveApplicator;
import es.caib.notib.logic.enviaments.EnviamentCertificacioReportGenerator;
import es.caib.notib.logic.enviaments.RefrescarEstatEntregaPostalActionExecutor;
import es.caib.notib.logic.enviaments.RefrescarEstatNotificaActionExecutor;
import es.caib.notib.logic.enviaments.RefrescarEstatSirActionExecutor;
import es.caib.notib.logic.enviaments.TitularPerspectiveApplicator;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.ExtendedPermission;
import es.caib.notib.logic.intf.model.NotificacioEnviamentResource;
import es.caib.notib.logic.intf.resourceservice.NotificacioEnviamentResourceService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.persist.resourceentity.NotificacioEnviamentResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementació del servei de gestió d'enviaments d'una notificació.
 * Aquest servei només s'ha implementat perquè feia falta per a poder consultar els fields d'aquest recurs al formulari
 * del front.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacioEnviamentResourceServiceImpl extends BaseMutableResourceService<NotificacioEnviamentResource, Long, NotificacioEnviamentResourceEntity> implements NotificacioEnviamentResourceService {

	private final UserSessionHelper userSessionHelper;
	private final AuthenticationHelper authenticationHelper;
	private final NotibPermissionHelper notibPermissionHelper;
	private final PluginHelper pluginHelper;
	private final MetricsHelper metricsHelper;
	private final NotificacioService notificacioService;

	@PostConstruct
	public void init() {

		register(NotificacioEnviamentResource.PERSPECTIVE_TITULAR, new TitularPerspectiveApplicator());
		register(NotificacioEnviamentResource.PERSPECTIVE_ENTREGA_POSTAL, new EntregaPostalPerspectiveApplicator());
		register(NotificacioEnviamentResource.REPORT_DESCARREGAR_DIAGRAMA_STATE_MACHINE, new DiagramaStateMachineReportGenerator());
		register(NotificacioEnviamentResource.REPORT_DESCARREGAR_CIE_CERTIFICACIO, new EntregaPostalCertificacioReportGenerator(pluginHelper, metricsHelper));
		register(NotificacioEnviamentResource.REPORT_DESCARREGAR_CERTIFICACIO_ENVIAMENT, new EnviamentCertificacioReportGenerator(notificacioService));
		register(NotificacioEnviamentResource.ACTION_REFRESCAR_ESTAT_NOTIFICA, new RefrescarEstatNotificaActionExecutor(notificacioService));
		register(NotificacioEnviamentResource.ACTION_REFRESCAR_ESTAT_SIR, new RefrescarEstatSirActionExecutor(notificacioService));
		register(NotificacioEnviamentResource.ACTION_REFRESCAR_ESTAT_ENTREGA_POSTAL, new RefrescarEstatEntregaPostalActionExecutor(notificacioService));
		register(NotificacioEnviamentResource.ACTION_CANCELAR_ENTREGA_POSTAL, new CancelarEntregaPostalActionExecutor(notificacioService));
	}

	@Override
	protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {

		// Condició per a mostrar només les notificacions de l'entitat actual
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
		// Condició per a mostrar només les notificacions amb permís de lectura
		var ids = notibPermissionHelper.getIdsToCheckNotificacioPermission(BasePermission.READ, BasePermission.READ);
		var permissionFilter = NotificacioResourceServiceImpl.springFilterWithReadPermission(ids, "notificacio.");
		if (!permissionFilter.isEmpty()) {
			andConditions.add("(" + permissionFilter + ")");
		}
		return String.join(" and ", andConditions);
	}

	/*
	 * Com que aquest servei no s'ha d'utilitzar més que per a consultar els fields feim que si s'intenta crear un
	 * recurs llençam una excepció.
	 */
	@Override
	protected void beforeCreateSave(NotificacioEnviamentResourceEntity entity, NotificacioEnviamentResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
		throw new ResourceNotCreatedException(getResourceClass(), "Create is not allowed");
	}

	@Override
	protected void afterConversion(NotificacioEnviamentResourceEntity entity, NotificacioEnviamentResource resource) {

		var organGestor = entity.getNotificacio().getOrganGestor();
		var procediment = entity.getNotificacio().getProcediment();
		resource.setNotificacioOrganGestor(ResourceReference.toResourceReference(organGestor.getId(), organGestor.getCodiNom()));
		resource.setNotificacioProcediment(ResourceReference.toResourceReference(procediment.getId(), procediment.getNom()));
		resource.setReferenciaNotificacio(entity.getNotificacio().getReferencia());
		resource.setAnulable(entity.isAnulable());
		resource.setNotificacioEstat(entity.getNotificacio().getEstat());
		var titular = entity.getTitular();
		resource.setTitular(ResourceReference.toResourceReference(titular.getId(), titular.getNomSencerNif()));
	}
}
