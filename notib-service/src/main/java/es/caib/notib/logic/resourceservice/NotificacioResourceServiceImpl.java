package es.caib.notib.logic.resourceservice;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.accionsMassives.ActualitzarEstatMassiuActionExecutor;
import es.caib.notib.logic.accionsMassives.AmpliarTerminiMassiuActionExecutor;
import es.caib.notib.logic.accionsMassives.AnularMassiuActionExecutor;
import es.caib.notib.logic.accionsMassives.CertificacioMassiuReportGenerator;
import es.caib.notib.logic.accionsMassives.EnviarNotificacionsMovilMassiuActionExecutor;
import es.caib.notib.logic.accionsMassives.EsborrarMassiuActionExecutor;
import es.caib.notib.logic.accionsMassives.JusitficantEnviamentMassiuReportGenerator;
import es.caib.notib.logic.accionsMassives.MarcarProcessatMassiuActionExecutor;
import es.caib.notib.logic.accionsMassives.ReactivarConsultesCanviEstatMassiuActionExecutor;
import es.caib.notib.logic.accionsMassives.ReactivarRegistreMassiuActionExecutor;
import es.caib.notib.logic.accionsMassives.ReenviarAmbErrorMassiuActionExecutor;
import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.enviaments.NotificacioEventStartSm;
import es.caib.notib.logic.enviaments.EnviarCallbackActionExecutor;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.LegacyHelper;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.ExtendedPermission;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.model.DocumentResource;
import es.caib.notib.logic.intf.model.NotificacioEnviamentResource;
import es.caib.notib.logic.intf.model.NotificacioResource;
import es.caib.notib.logic.intf.model.OrganGestorResource;
import es.caib.notib.logic.intf.model.PersonaResource;
import es.caib.notib.logic.intf.resourceservice.NotificacioResourceService;
import es.caib.notib.logic.intf.service.AccioMassivaService;
import es.caib.notib.logic.intf.service.CallbackService;
import es.caib.notib.logic.intf.service.EnviamentService;
import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.service.JustificantService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.logic.notificacions.AmpliarTerminiRemesaActionExecutor;
import es.caib.notib.logic.notificacions.AnularRemesaActionExecutor;
import es.caib.notib.logic.notificacions.CertificacioReportGenerator;
import es.caib.notib.logic.notificacions.DocumentEnviatReportGenerator;
import es.caib.notib.logic.notificacions.DocumentPerspectiveApplicator;
import es.caib.notib.logic.notificacions.EnviamentPerspectiveApplicator;
import es.caib.notib.logic.notificacions.EnviarEntregaPostalActionExecutor;
import es.caib.notib.logic.notificacions.EnviarNotificaActionExecutor;
import es.caib.notib.logic.notificacions.EsborrarRemesaActionExecutor;
import es.caib.notib.logic.notificacions.ExportarExcelReportGenerator;
import es.caib.notib.logic.notificacions.GrupPerspectiveApplicator;
import es.caib.notib.logic.notificacions.JusitficantEnviamentReportGenerator;
import es.caib.notib.logic.notificacions.MarcarProcessatActionExecutor;
import es.caib.notib.logic.notificacions.NotificacioDetallPerspectiveApplicator;
import es.caib.notib.logic.notificacions.OperadorPostalCiePerspectiveApplicator;
import es.caib.notib.logic.notificacions.ReactivarAmbErrorActionExecutor;
import es.caib.notib.logic.notificacions.ReactivarCallbacksMassiuActionExecutor;
import es.caib.notib.logic.notificacions.ReactivarConsultaSirActionExecutor;
import es.caib.notib.logic.notificacions.ReactivarEstatNotificaActionExecutor;
import es.caib.notib.logic.notificacions.RecuperarRemesaActionExecutor;
import es.caib.notib.logic.notificacions.ReenviarAmbErrorActionExecutor;
import es.caib.notib.logic.notificacions.ReenviarCallbacksMassiuActionExecutor;
import es.caib.notib.logic.notificacions.RefrescarEstatActionExecutor;
import es.caib.notib.logic.notificacions.RegistrarRemesaActionExecutor;
import es.caib.notib.persist.resourceentity.DocumentResourceEntity;
import es.caib.notib.persist.resourceentity.NotificacioEnviamentResourceEntity;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import es.caib.notib.persist.resourceentity.PersonaResourceEntity;
import es.caib.notib.persist.resourcerepository.CallbackResourceRepository;
import es.caib.notib.persist.resourcerepository.DocumentResourceRepository;
import es.caib.notib.persist.resourcerepository.EventResourceRepository;
import es.caib.notib.persist.resourcerepository.NotificacioEnviamentResourceRepository;
import es.caib.notib.persist.resourcerepository.NotificacioResourceRepository;
import es.caib.notib.persist.resourcerepository.PersonaResourceRepository;
import es.caib.notib.persist.resourcerepository.ProcedimentOrganGestorResourceRepository;
import es.caib.notib.persist.resourcerepository.UsuariResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementació del servei de gestió de notificacions.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacioResourceServiceImpl extends BaseMutableResourceService<NotificacioResource, Long, NotificacioResourceEntity> implements NotificacioResourceService {

	private final UserSessionHelper userSessionHelper;
	private final AuthenticationHelper authenticationHelper;
	private final LegacyHelper legacyHelper;
	private final ConfigHelper configHelper;
	private final MessageHelper messageHelper;
	private final NotibPermissionHelper notibPermissionHelper;
	private final NotificacioEnviamentResourceRepository notificacioEnviamentResourceRepository;
	private final UsuariResourceRepository usuariResourceRepository;
	private final EventResourceRepository eventResourceRepository;
	private final DocumentResourceRepository documentResourceRepository;
	private final CallbackResourceRepository callbackResourceRepository;
	private final PersonaResourceRepository personaResourceRepository;
	private final ProcedimentOrganGestorResourceRepository procedimentOrganGestorResourceRepository;
	private final JustificantService justificantService;
	private final NotificacioService notificacioService;
	private final EnviamentService enviamentService;
	private final AccioMassivaService accioMassivaService;
	private final CallbackService callbackService;
	private final ApplicationEventPublisher eventPublisher;

	@PostConstruct
	public void init() {

		register(null, new NotificacioResourceServiceImpl.InitOnChangeLogicProcessor());
		register(NotificacioResource.Fields.organGestor, new NotificacioResourceServiceImpl.OrganGestorOnChangeLogicProcessor());
		register(NotificacioResource.Fields.caducitat, new NotificacioResourceServiceImpl.CaducitatOnChangeLogicProcessor());
		register(NotificacioResource.Fields.caducitatDiesNaturals, new NotificacioResourceServiceImpl.CaducitatOnChangeLogicProcessor());
		register(NotificacioResource.PERSPECTIVE_DOCUMENTS_NOTIFICACIO, new DocumentPerspectiveApplicator());
		register(NotificacioResource.PERSPECTIVE_ENVIAMENTS_NOTIFICACIO, new EnviamentPerspectiveApplicator());
		register(NotificacioResource.PERSPECTIVE_NOTIFICACIO_DETALL, new NotificacioDetallPerspectiveApplicator(notificacioEnviamentResourceRepository, configHelper, callbackResourceRepository, eventResourceRepository, messageHelper, notibPermissionHelper, usuariResourceRepository));
		register(NotificacioResource.PERSPECTIVE_OPERADORS_CIE_POSTAL, new OperadorPostalCiePerspectiveApplicator());
		register(NotificacioResource.PERSPECTIVE_GRUP, new GrupPerspectiveApplicator());
		register(NotificacioResource.REPORT_DESCARREGAR_JUSTIFICANT_NOTIFICACIO, new JusitficantEnviamentReportGenerator(justificantService));
		register(NotificacioResource.REPORT_DESCARREGAR_JUSTIFICANT_MASSIU, new JusitficantEnviamentMassiuReportGenerator(accioMassivaService, notificacioService, userSessionHelper, authenticationHelper));
		register(NotificacioResource.REPORT_DESCARREGAR_DOCUMENT_ENVIAT, new DocumentEnviatReportGenerator(notificacioService));
		register(NotificacioResource.REPORT_EXPORTAR_EXCEL, new ExportarExcelReportGenerator(accioMassivaService, userSessionHelper, authenticationHelper, enviamentService, notificacioService));
		register(NotificacioResource.REPORT_DESCARREGAR_CERTIFICACIO, new CertificacioReportGenerator(notificacioService, messageHelper));
		register(NotificacioResource.REPORT_DESCARREGAR_CERTIFICACIO_MASSIU, new CertificacioMassiuReportGenerator(accioMassivaService, notificacioService, userSessionHelper, authenticationHelper));
		register(NotificacioResource.ACTION_ANULAR_REMESA, new AnularRemesaActionExecutor(notificacioService));
		register(NotificacioResource.ACTION_AMPLIAR_TERMINI, new AmpliarTerminiRemesaActionExecutor(notificacioService));
		register(NotificacioResource.ACTION_MARCAR_PROCESSAT, new MarcarProcessatActionExecutor(notificacioService));
		register(NotificacioResource.ACTION_ESBORRAR_REMESA, new EsborrarRemesaActionExecutor(notificacioService, messageHelper));
		register(NotificacioResource.ACTION_RECUPERAR_REMESA, new RecuperarRemesaActionExecutor(notificacioService, messageHelper));
		register(NotificacioResource.ACTION_ENVIAR_CALLBACK, new EnviarCallbackActionExecutor(callbackService));
		register(NotificacioResource.ACTION_ENVIAR_ENTREGA_POSTAL, new EnviarEntregaPostalActionExecutor(notificacioService));
		register(NotificacioResource.ACTION_REGISTRAR_REMESA, new RegistrarRemesaActionExecutor(notificacioService));
		register(NotificacioResource.ACTION_ENVIAR_NOTIFICA, new EnviarNotificaActionExecutor(notificacioService));
		register(NotificacioResource.ACTION_REACTIVAR_ESTAT_NOTIFICA, new ReactivarEstatNotificaActionExecutor(notificacioService));
		register(NotificacioResource.ACTION_REACTIVAR_CONSULTA_SIR, new ReactivarConsultaSirActionExecutor(notificacioService));
		register(NotificacioResource.ACTION_REACTIVAR_AMB_ERRORS, new ReactivarAmbErrorActionExecutor(notificacioService));
		register(NotificacioResource.ACTION_REENVIAR_AMB_ERRORS, new ReenviarAmbErrorActionExecutor(notificacioService));
		register(NotificacioResource.ACTION_ACTUALITZAR_ESTAT_MASSIU, new ActualitzarEstatMassiuActionExecutor(accioMassivaService, userSessionHelper, authenticationHelper, enviamentService));
		register(NotificacioResource.ACTION_REINTENTAR_REGISTRE_MASSIU, new ReactivarRegistreMassiuActionExecutor(accioMassivaService, userSessionHelper, authenticationHelper));
		register(NotificacioResource.ACTION_REENVIAR_AMB_ERROR_MASSIU, new ReenviarAmbErrorMassiuActionExecutor(accioMassivaService, userSessionHelper, authenticationHelper, enviamentService));
		register(NotificacioResource.ACTION_ESBORRAR_MASSIU, new EsborrarMassiuActionExecutor(accioMassivaService, userSessionHelper, authenticationHelper, enviamentService));
		register(NotificacioResource.ACTION_REACTIVAR_CONSULTES_CANVI_ESTAT_MASSIU, new ReactivarConsultesCanviEstatMassiuActionExecutor(accioMassivaService, userSessionHelper, authenticationHelper, enviamentService));
		register(NotificacioResource.ACTION_REACTIVAR_CALLBACKS_MASSIU, new ReactivarCallbacksMassiuActionExecutor(accioMassivaService, userSessionHelper, authenticationHelper, enviamentService));
		register(NotificacioResource.ACTION_REENVIAR_CALLBACKS_MASSIU, new ReenviarCallbacksMassiuActionExecutor(accioMassivaService, userSessionHelper, authenticationHelper, enviamentService));
		register(NotificacioResource.ACTION_ENVIAR_NOTIFICACIONS_MOVIL_MASSIU, new EnviarNotificacionsMovilMassiuActionExecutor(accioMassivaService, userSessionHelper, authenticationHelper, enviamentService));
		register(NotificacioResource.ACTION_MARCAR_PROCESSAT_MASSIU, new MarcarProcessatMassiuActionExecutor(accioMassivaService, userSessionHelper, authenticationHelper));
		register(NotificacioResource.ACTION_ANULAR_MASSIU, new AnularMassiuActionExecutor(accioMassivaService, userSessionHelper, authenticationHelper));
		register(NotificacioResource.ACTION_AMPLIAR_TERMINI_MASSIU, new AmpliarTerminiMassiuActionExecutor(accioMassivaService, userSessionHelper, authenticationHelper));
		register(NotificacioResource.REFRESCAR_ESTAT, new RefrescarEstatActionExecutor(legacyHelper));
	}

	@Override
	protected NotificacioResource entityToResource(NotificacioResourceEntity entity) {

		if (Boolean.TRUE.equals(entity.getPerActualitzar())) {
			legacyHelper.actualitzarColumnaEstat(entity);
		}
		var resource = super.entityToResource(entity);
		resource.setEstatString(entity.getEstatString());
		return resource;
	}

	@Override
	protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {

		// Condició per a mostrar només les notificacions de l'entitat actual
		var isRolSuper = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_SUPER);
		if (isRolSuper) {
			return "";
		}
		var entitatFilter = "entitat.id:" + userSessionHelper.getCurrentEntitatId();
		var isRoleAdmin = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN);
		var isRoleAdminLectura = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN_LECTURA);
		var isRoleAdminOrgan = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ORGAN);
		if ((isRoleAdmin && notibPermissionHelper.currentEntitatPermissionAllowed(ExtendedPermission.PERM2)) ||
			(isRoleAdminLectura && notibPermissionHelper.currentEntitatPermissionAllowed(ExtendedPermission.PERMX))) {
			return entitatFilter;
		}
		if (isRoleAdminOrgan && notibPermissionHelper.currentOrganGestorPermissionAllowed(BasePermission.ADMINISTRATION)) {
			return entitatFilter + " and organGestor.id:" + userSessionHelper.getCurrentOrganGestorId();
		}
		// Condició per a mostrar només les notificacions amb permís de lectura
		var ids = notibPermissionHelper.getIdsToCheckNotificacioPermission(BasePermission.READ, BasePermission.READ);
		if (ids.isEmpty()) {
			return !currentSpringFilter.contains("createdBy:") ? entitatFilter + " and createdBy:'" + authenticationHelper.getCurrentUserName() + "'" : entitatFilter;
		}
		List<String> andConditions = new ArrayList<>();
		andConditions.add(entitatFilter);
		var permissionFilter = springFilterWithReadPermission(ids, "");
		if (!permissionFilter.isEmpty()) {
			andConditions.add("(" + permissionFilter + ")");
		}
		return String.join(" and ", andConditions);
	}

	@Override
	public void beforeCreateSave(NotificacioResourceEntity entity, NotificacioResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {

		entity.setUsuariCodi(authenticationHelper.getCurrentUserName());
		entity.setEntitat(userSessionHelper.getCurrentEntitat());
		entity.setEmisorDir3Codi(entity.getEntitat().getDir3Codi());
		entity.setComunicacioTipus(NotificacioComunicacioTipusEnumDto.ASINCRON);
		entity.setEstat(NotificacioEstatEnumDto.PENDENT);
		entity.setReferencia(UUID.randomUUID().toString());
		entity.setProcedimentCodiNotib(entity.getProcediment().getCodi());
		emplenarProcedimentOrganGestor(entity);
		checkCreatePermission(entity);
		if (resource.getDocumentsInfo() != null) {
			saveDocuments(entity, resource.getDocumentsInfo());
		}
	}


	@Override
	public void afterCreateSave(NotificacioResourceEntity entity, NotificacioResource resource, Map<String, AnswerRequiredException.AnswerValue> answers, boolean anyOrderChanged) {

		List<Long> enviamentsIds = new ArrayList<>();
		if (resource.getEnviamentsInfo() != null) {
			resource.getEnviamentsInfo().forEach(e -> {
				Long enviamentId = saveEnviament(entity, e);
				enviamentsIds.add(enviamentId);
			});
		}
		legacyHelper.altaNotificacio(entity.getId(), enviamentsIds);
		eventPublisher.publishEvent(new NotificacioEventStartSm(entity.getId()));
	}

	/*
	 * Condició en format Spring Filter per a mostrar només les notificacions sobre les que es tenen permisos. Les
	 * notificacions es poden veure si es compleix alguna de les següents condicions:
	 *   a) L'usuari te permís sobre l'òrgan gestor de la notificació.
	 *   b) La notificació te un procediment no comú i l'usuari te permís sobre aquest procediment.
	 *   c) La notificació te un procediment comú amb "requereix permisos directes" i l'usuari te permís
	 *      sobre la combinació organ gestor - procediment de la notificació.
	 *   d) La notificació te un procediment comú sense "requereix permisos directes",
	 *      l'usuari te permís sobre la combinació organ gestor - procediment de la notificació i les combinacions
	 *      òrgan gestor - procediment son únicament dels òrgans gestors amb permís de procediments comuns.
	 */
	public static String springFilterWithReadPermission(NotibPermissionHelper.IdsToCheckNotificacioPermission ids, String fieldPrefix) {

		List<String> permissionOrConditions = new ArrayList<>();
		// a)
		String joinedOrganGestorIds = ids.getOrganGestorIds().stream().map(String::valueOf).collect(Collectors.joining(","));
		if (!joinedOrganGestorIds.isEmpty()) {
			permissionOrConditions.add(fieldPrefix + "organGestor.id in (" + joinedOrganGestorIds + ")");
		}
		// b)
		String joinedProcedimentNoComuIds = ids.getProcedimentNoComuIds().stream().map(String::valueOf).collect(Collectors.joining(","));
		if (!joinedProcedimentNoComuIds.isEmpty()) {
			permissionOrConditions.add(fieldPrefix + "procediment.id in (" + joinedProcedimentNoComuIds + ")");
		}
		// c) o d)
		String joinedProcedimentComuOrganGestorIds = ids.getProcedimentComuOrganGestorIds().stream().map(String::valueOf).collect(Collectors.joining(","));
		if (!joinedProcedimentComuOrganGestorIds.isEmpty()) {
			permissionOrConditions.add(fieldPrefix + "procedimentOrganGestor.id in (" + joinedProcedimentComuOrganGestorIds + ")");
		}
		return !permissionOrConditions.isEmpty() ? String.join(" or ", permissionOrConditions) : "id is null";
	}

	/*
	 * Es verifica si es tenen permisos per a crear la notificació. Les condicions que es verifiquen son les mateixes
	 * del mètode springFilterWithReadPermission().
	 */
	public void checkCreatePermission(NotificacioResourceEntity entity) {

		var organPermission = notibPermissionHelper.getOrganGestorNotificacioCreatePermission(entity.getEnviamentTipus());
		var procedimentPermission = notibPermissionHelper.getProcedimentNotificacioCreatePermission(entity.getEnviamentTipus());
		var ids = notibPermissionHelper.getIdsToCheckNotificacioPermission(organPermission, procedimentPermission);
		var organGestorId = entity.getOrganGestor().getId();
		var procedimentId = entity.getProcediment().getId();
		var procedimentOrganGestorId = entity.getProcedimentOrganGestor() != null ? entity.getProcedimentOrganGestor().getId() : null;
		var permissionGranted = (organGestorId != null && ids.getOrganGestorIds().contains(organGestorId)) || // a)
			(procedimentId != null && ids.getProcedimentNoComuIds().contains(procedimentId)) || // b)
			(procedimentOrganGestorId != null && ids.getProcedimentComuOrganGestorIds().contains(procedimentOrganGestorId)); // c) o d)
		if (!permissionGranted) {
			throw new ResourceNotCreatedException(NotificacioResource.class, "Not allowed to create notification. Permission check failed.");
		}
	}

	private Long saveEnviament(NotificacioResourceEntity notificacio, NotificacioEnviamentResource enviament) {

		var uuid = UUID.randomUUID().toString();
		var enviamentNou = NotificacioEnviamentResourceEntity.builder().resource(enviament).notificacio(notificacio).build();
		enviamentNou.setReferenciaEnviament(uuid);
		enviamentNou.setNotificaEstat(EnviamentEstat.PENDENT);
		var enviamentCreat = notificacioEnviamentResourceRepository.saveAndFlush(enviamentNou);
		var titular = saveDestinatari(enviamentCreat, enviament.getTitularInfo());
		enviamentCreat.setTitular(titular);
		enviamentCreat.setReferenciaEnviament(uuid);
		if (enviament.getRepresentantsInfo() != null) {
			enviament.getRepresentantsInfo().forEach(r -> saveDestinatari(enviamentCreat, r));
		}
		return enviamentCreat.getId();
	}

	private void saveDocuments(NotificacioResourceEntity notificacio, List<DocumentResource> documents) {

		// Crea els documents associats amb la notificació a la base de dades.
		for (int i = 0; i < documents.size(); i++) {
			DocumentResource document = documents.get(i);
			DocumentResourceEntity documentNou = DocumentResourceEntity.builder().resource(document).build();
			String arxiuGestdocId = legacyHelper.notificacioAdjuntCreate(document.getAttachment());
			documentNou.setArxiuGestdocId(arxiuGestdocId);
			DocumentResourceEntity documentCreat = documentResourceRepository.save(documentNou);
			if (i == 0) {
				notificacio.setDocument(documentCreat);
			} else if (i == 1) {
				notificacio.setDocument2(documentCreat);
			} else if (i == 2) {
				notificacio.setDocument3(documentCreat);
			} else if (i == 3) {
				notificacio.setDocument4(documentCreat);
			} else if (i == 4) {
				notificacio.setDocument5(documentCreat);
			}
		}
	}

	private PersonaResourceEntity saveDestinatari(NotificacioEnviamentResourceEntity enviament, PersonaResource destinatari) {
		// Crea el destinatari a la base de dades.
		return personaResourceRepository.save(PersonaResourceEntity.builder().resource(destinatari).enviament(enviament).build());
	}

	private void emplenarProcedimentOrganGestor(NotificacioResourceEntity entity) {

		if (entity.getProcediment() != null && entity.getProcediment().isComu() && entity.getOrganGestor() != null) {
			var procedimentOrganGestor = procedimentOrganGestorResourceRepository.findByProcedimentAndOrganGestor(entity.getProcediment(), entity.getOrganGestor());
			procedimentOrganGestor.ifPresent(entity::setProcedimentOrganGestor);
		}
	}



	/*
	 * Lògica onChange que s'executa al carregar el formulari.
	 */
	static class InitOnChangeLogicProcessor implements OnChangeLogicProcessor<NotificacioResource> {

		@Override
		public void onChange(Serializable id, NotificacioResource previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, NotificacioResource target) {
			caducitatOnChange(previous.getCaducitatDiesNaturals(), previous, target);
		}
	}

	/*
	 * Lògica onChange pel camp organGestor. Si l'usuari te permís "comunicacions sense procediment" sobre l'òrgan
	 * gestor i la notificació és una comunicació s'ha de posar el camp procedimentRequired a false.
	 */
	class OrganGestorOnChangeLogicProcessor implements OnChangeLogicProcessor<NotificacioResource> {

		@Override
		public void onChange(Serializable id, NotificacioResource previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, NotificacioResource target) {

			ResourceReference<OrganGestorResource, Long> organGestor = (ResourceReference)fieldValue;
			var isComunicacio = previous.getEnviamentTipus() != null && (EnviamentTipus.COMUNICACIO.equals(previous.getEnviamentTipus()) || EnviamentTipus.SIR.equals(previous.getEnviamentTipus()));
			if (organGestor == null || !isComunicacio) {
				target.setProcedimentRequired(true);
				return;
			}
			List<Long> organGestorIdsWithPermission = notibPermissionHelper.organGestorIdsWithPermissionRecursive(ExtendedPermission.PERM7);
			var hasComunicacionsSenseProcedimentPermission = organGestorIdsWithPermission.contains(organGestor.getId());
			target.setProcedimentRequired(!hasComunicacionsSenseProcedimentPermission);
		}
	}

	/*
	 * Lògica onChange pel camp interessatTipus. Segons el valor d'aquest camp canvien els camps visibles / obligatoris.
	 */
	static class CaducitatOnChangeLogicProcessor implements OnChangeLogicProcessor<NotificacioResource> {

		@Override
		public void onChange(Serializable id, NotificacioResource previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, NotificacioResource target) {

			if (NotificacioResource.Fields.caducitat.equals(fieldName)) {
				var isCaducitatDiesNaturalsInPreviousFieldNames = previousFieldNames != null && previousFieldNames.length > 0 && NotificacioResource.Fields.caducitatDiesNaturals.equals(previousFieldNames[0]);
				if (!isCaducitatDiesNaturalsInPreviousFieldNames) {
					Date date = (Date) fieldValue;
					caducitatOnChange(date, previous, target);
				}
				return;
			}
			if (!NotificacioResource.Fields.caducitatDiesNaturals.equals(fieldName)) {
				return;
			}
			var isCaducitatInPreviousFieldNames = previousFieldNames != null && previousFieldNames.length > 0 && NotificacioResource.Fields.caducitat.equals(previousFieldNames[0]);
			if (!isCaducitatInPreviousFieldNames) {
				var caducitatDiesNaturals = (Integer) fieldValue;
				caducitatOnChange(caducitatDiesNaturals, previous, target);
			}
		}
	}

	private static void caducitatOnChange(Date caducitat, NotificacioResource previous, NotificacioResource target) {

		Integer numDiesNaturals = null;
		if (caducitat != null) {
			LocalDate dataConvertida = caducitat.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			numDiesNaturals = (int)ChronoUnit.DAYS.between(LocalDate.now(), dataConvertida);
		}
		target.setCaducitatDiesNaturals(numDiesNaturals);
		/*// Només feim el canvi si el nombre de dies naturals és diferent a la que ja hi havia per a evitar bucle
		// infinit d'onChange.
		if (!Objects.equals(numDiesNaturals, previous.getCaducitatDiesNaturals())) {
			target.setCaducitatDiesNaturals(numDiesNaturals);
		}*/
	}

	private static void caducitatOnChange(Integer caducitatDiesNaturals, NotificacioResource previous, NotificacioResource target) {

		Date caducitat = null;
			if (caducitatDiesNaturals != null) {
			caducitat = Date.from(LocalDate.now().plusDays(caducitatDiesNaturals).atStartOfDay(ZoneId.systemDefault()).toInstant());
		}
		target.setCaducitat(caducitat);
		/*// Només feim el canvi si la caducitat és diferent a la que ja hi havia per a evitar bucle infinit d'onChange.
		if (!Objects.equals(caducitat, previous.getCaducitat())) {
			target.setCaducitat(caducitat);
		}*/
	}

}
