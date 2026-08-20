package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.enviaments.DiagramaStateMachineReportGenerator;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.model.NotificacioEnviamentResource;
import es.caib.notib.logic.intf.model.NotificacioMassivaResource;
import es.caib.notib.logic.intf.model.NotificacioResource;
import es.caib.notib.logic.intf.resourceservice.NotificacioMassivaResourceService;
import es.caib.notib.logic.intf.service.NotificacioMassivaService;
import es.caib.notib.logic.notificacioMassiva.NotificacioMassivaCodiPostalReportGenerator;
import es.caib.notib.logic.notificacioMassiva.NotificacioMassivaCsvNotificacioReportGenerator;
import es.caib.notib.logic.notificacioMassiva.NotificacioMassivaErrorsExecucioReportGenerator;
import es.caib.notib.logic.notificacioMassiva.NotificacioMassivaErrorsValidacioReportGenerator;
import es.caib.notib.logic.notificacioMassiva.NotificacioMassivaModelCsvReportGenerator;
import es.caib.notib.logic.notificacioMassiva.NotificacioMassivaPosposarActionExecutor;
import es.caib.notib.logic.notificacioMassiva.NotificacioMassivaReactivarActionExecutor;
import es.caib.notib.logic.notificacioMassiva.NotificacioMassivaResumPerspectiveApplicator;
import es.caib.notib.logic.notificacioMassiva.NotificacioMassivaResumReportGenerator;
import es.caib.notib.logic.notificacioMassiva.NotificacioMassivaZipNotificacioReportGenerator;
import es.caib.notib.persist.resourceentity.NotificacioMassivaResourceEntity;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.UUID;

/**
 * Implementació del servei de notificacions massives.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacioMassivaResourceServiceImpl
	extends BaseMutableResourceService<NotificacioMassivaResource, Long, NotificacioMassivaResourceEntity>
	implements NotificacioMassivaResourceService {

	private final UserSessionHelper userSessionHelper;
	private final AuthenticationHelper authenticationHelper;
	private final NotibPermissionHelper notibPermissionHelper;
	private final NotificacioMassivaService notificacioMassivaService;

	@PostConstruct
	public void init() {
		register(NotificacioMassivaResource.REPORT_DESCARREGAR_CSV_NOTIFICACIO_MASSIVA, new NotificacioMassivaCsvNotificacioReportGenerator(notificacioMassivaService));
		register(NotificacioMassivaResource.REPORT_DESCARREGAR_ZIP_NOTIFICACIO_MASSIVA, new NotificacioMassivaZipNotificacioReportGenerator(notificacioMassivaService));
		register(NotificacioMassivaResource.REPORT_DESCARREGAR_RESUM_NOTIFICACIO_MASSIVA, new NotificacioMassivaResumReportGenerator(notificacioMassivaService));
		register(NotificacioMassivaResource.REPORT_DESCARREGAR_ERRORS_VALIDACIO_NOTIFICACIO_MASSIVA, new NotificacioMassivaErrorsValidacioReportGenerator(notificacioMassivaService));
		register(NotificacioMassivaResource.REPORT_DESCARREGAR_ERRORS_EXECUCIO_NOTIFICACIO_MASSIVA, new NotificacioMassivaErrorsExecucioReportGenerator(notificacioMassivaService));
		register(NotificacioMassivaResource.ACTION_POSPOSAR_NOTIFICACIO_MASSIVA, new NotificacioMassivaPosposarActionExecutor(notificacioMassivaService));
		register(NotificacioMassivaResource.ACTION_REACTIVAR_NOTIFICACIO_MASSIVA, new NotificacioMassivaReactivarActionExecutor(notificacioMassivaService));
		register(NotificacioMassivaResource.PERSPECTIVE_RESUM_NOTIFACIO_MASSIVA, new NotificacioMassivaResumPerspectiveApplicator(notificacioMassivaService));
		register(NotificacioMassivaResource.REPORT_DESCARREGAR_CODIS_ENTREGA_POSTAL, new NotificacioMassivaCodiPostalReportGenerator(notificacioMassivaService));
		register(NotificacioMassivaResource.REPORT_DESCARREGAR_MODEL_DADES_NOTIFICACIO_MASSIVA, new NotificacioMassivaModelCsvReportGenerator(notificacioMassivaService));

	}

	@Override
	protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {

		// TODO FALTA LES CONDICIONS PER QUINES MASSIVES POT VEURE L'USUARI
		// Condició per a mostrar només les notificacions massives de l'entitat actual
		String entitatFilter = "entitat.id:" + userSessionHelper.getCurrentEntitatId();
//		boolean isRoleUser = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_USER);
//		boolean isRoleAdmin = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN);
//		boolean isRoleAdminLectura = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN_LECTURA);
		boolean isRoleAdminOrgan = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ORGAN);
//		if ((isRoleAdmin && notibPermissionHelper.currentEntitatPermissionAllowed(ExtendedPermission.PERM2)) ||
//			(isRoleAdminLectura && notibPermissionHelper.currentEntitatPermissionAllowed(ExtendedPermission.PERMX))) {
//			return entitatFilter;
//		}
		if (isRoleAdminOrgan && notibPermissionHelper.currentOrganGestorPermissionAllowed(BasePermission.ADMINISTRATION)) {
			return entitatFilter + " and organGestor.id:" + userSessionHelper.getCurrentOrganGestorId();
		}
		return entitatFilter;
	}

	@Override
	public void beforeCreateSave(NotificacioMassivaResourceEntity entity, NotificacioMassivaResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {

		log.info("beforeCreateSave");
		entity.setEntitat(userSessionHelper.getCurrentEntitat());
	}
}
