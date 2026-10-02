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
import es.caib.notib.logic.helper.NotificacioEstatAsyncHelper;
import es.caib.notib.logic.helper.NotificacioListHelper;
import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.notib.logic.intf.base.model.FieldOption;
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
import es.caib.notib.logic.intf.service.GrupService;
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
import es.caib.notib.persist.resourcerepository.GrupResourceRepository;
import es.caib.notib.persist.resourcerepository.ProcedimentOrganGestorResourceRepository;
import es.caib.notib.persist.resourcerepository.UsuariResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.JoinType;

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
	private final GrupResourceRepository grupResourceRepository;
	private final GrupService grupService;
	private final JustificantService justificantService;
	private final NotificacioService notificacioService;
	private final EnviamentService enviamentService;
	private final AccioMassivaService accioMassivaService;
	private final CallbackService callbackService;
	private final ApplicationEventPublisher eventPublisher;
	private final NotificacioEstatAsyncHelper notificacioEstatAsyncHelper;
	private final NotificacioListHelper notificacioListHelper;

	private static final ThreadLocal<Boolean> consultaLlistat = new ThreadLocal<>();

	@PostConstruct
	public void init() {

		register(null, new NotificacioResourceServiceImpl.InitOnChangeLogicProcessor());
		register(NotificacioResource.Fields.organGestor, new NotificacioResourceServiceImpl.OrganGestorOnChangeLogicProcessor());
		register(NotificacioResource.Fields.grupCodi, new NotificacioResourceServiceImpl.GrupCodiFieldOptionsProvider());
		register(NotificacioResource.Fields.caducitat, new NotificacioResourceServiceImpl.CaducitatOnChangeLogicProcessor());
		register(NotificacioResource.Fields.caducitatDiesNaturals, new NotificacioResourceServiceImpl.CaducitatOnChangeLogicProcessor());
		register(NotificacioResource.PERSPECTIVE_DOCUMENTS_NOTIFICACIO, new DocumentPerspectiveApplicator());
		register(NotificacioResource.PERSPECTIVE_ENVIAMENTS_NOTIFICACIO, new EnviamentPerspectiveApplicator());
		register(NotificacioResource.PERSPECTIVE_NOTIFICACIO_DETALL, new NotificacioDetallPerspectiveApplicator(notificacioEnviamentResourceRepository, configHelper, callbackResourceRepository, eventResourceRepository, messageHelper, usuariResourceRepository));
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
		register(NotificacioResource.ACTION_MARCAR_PROCESSAT, new MarcarProcessatActionExecutor(notificacioService, notibPermissionHelper));
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
		register(NotificacioResource.ACTION_MARCAR_PROCESSAT_MASSIU, new MarcarProcessatMassiuActionExecutor(accioMassivaService, userSessionHelper, authenticationHelper, notibPermissionHelper));
		register(NotificacioResource.ACTION_ANULAR_MASSIU, new AnularMassiuActionExecutor(accioMassivaService, userSessionHelper, authenticationHelper));
		register(NotificacioResource.ACTION_AMPLIAR_TERMINI_MASSIU, new AmpliarTerminiMassiuActionExecutor(accioMassivaService, userSessionHelper, authenticationHelper));
		register(NotificacioResource.REFRESCAR_ESTAT, new RefrescarEstatActionExecutor(legacyHelper));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<NotificacioResource> findPage(String quickFilter, String filter, String[] namedQueries, String[] perspectives, Pageable pageable) {

		// Només la càrrega del llistat pot diferir el càlcul de l'estat: altres conversions de
		// múltiples remeses (p.ex. l'exportació) necessiten el valor definitiu
		consultaLlistat.set(true);
		try {
			return super.findPage(quickFilter, filter, namedQueries, perspectives, pageable);
		} finally {
			consultaLlistat.remove();
		}
	}

	@Override
	protected NotificacioResource entityToResource(NotificacioResourceEntity entity) {

		var resource = super.entityToResource(entity);
		resource.setEstatString(entity.getEstatString());
		return resource;
	}

	/*
	 * Les remeses amb l'estat pendent d'actualitzar (per_actualitzar) es recalculen totes juntes en
	 * una sola transacció, o bé de manera asíncrona (enviant el resultat via SSE) si així ho indica
	 * la propietat es.caib.notib.app.llistat.remeses.estat.asincron.
	 */
	@Override
	protected void afterConversion(List<NotificacioResourceEntity> entities, List<NotificacioResource> resources) {

		emplenarPermisProcessar(entities, resources);
		Map<Long, NotificacioResource> pendents = new LinkedHashMap<>();
		for (int i = 0; i < entities.size(); i++) {
			if (Boolean.TRUE.equals(entities.get(i).getPerActualitzar())) {
				pendents.put(entities.get(i).getId(), resources.get(i));
			}
		}
		if (pendents.isEmpty()) {
			return;
		}
		var asincron = Boolean.TRUE.equals(consultaLlistat.get()) && configHelper.getConfigAsBoolean(NotificacioEstatAsyncHelper.PROPERTY_ESTAT_ASINCRON, true);
		if (asincron) {
			pendents.values().forEach(r -> r.setEstatPendent(true));
			notificacioEstatAsyncHelper.calcularEstatsAsync(pendents.keySet(), authenticationHelper.getCurrentUserName());
			return;
		}
		actualitzarEstats(pendents);
	}

	@Override
	protected void afterConversion(NotificacioResourceEntity entity, NotificacioResource resource) {

		emplenarPermisProcessar(List.of(entity), List.of(resource));
		if (Boolean.TRUE.equals(entity.getPerActualitzar())) {
			actualitzarEstats(Map.of(entity.getId(), resource));
		}
	}

	/*
	 * Permís per a marcar com a processada (acció MARCAR_PROCESSAT del llistat i del detall): remeses
	 * finalitzades sobre el procediment o l'òrgan gestor de les quals l'usuari actual té permís de
	 * processar. És la mateixa regla que aplica NotificacioServiceImpl.marcarComProcessada
	 * (PermisosService.hasNotificacioPermis), però calculant els codis amb permís un sol cop.
	 */
	private void emplenarPermisProcessar(List<NotificacioResourceEntity> entities, List<NotificacioResource> resources) {

		List<String> codisAmbPermis = null;
		for (int i = 0; i < entities.size(); i++) {
			var entity = entities.get(i);
			if (!NotificacioEstatEnumDto.FINALITZADA.equals(entity.getEstat())) {
				continue;
			}
			if (codisAmbPermis == null) {
				codisAmbPermis = getCodisAmbPermisProcessar();
			}
			var procedimentCodi = entity.getProcediment() != null ? entity.getProcediment().getCodi() : null;
			var organCodi = entity.getOrganGestor() != null ? entity.getOrganGestor().getCodi() : null;
			resources.get(i).setPermisProcessar(
					(procedimentCodi != null && codisAmbPermis.contains(procedimentCodi)) ||
					(organCodi != null && codisAmbPermis.contains(organCodi)));
		}
	}

	private List<String> getCodisAmbPermisProcessar() {

		var entitatId = userSessionHelper.getCurrentEntitatId();
		if (entitatId == null) {
			return List.of();
		}
		try {
			return notificacioListHelper.getCodisProcedimentsAndOrgansAmpPermisProcessar(entitatId, authenticationHelper.getCurrentUserName());
		} catch (Exception ex) {
			log.error("Error obtenint els permisos de processar de l'usuari actual", ex);
			return List.of();
		}
	}

	private void actualitzarEstats(Map<Long, NotificacioResource> pendents) {

		// actualitzarColumnesEstat persisteix els nous valors en una transacció (REQUIRES_NEW) separada
		// d'aquesta, per això s'empren els valors que retorna en lloc de rellegir les entitats
		try {
			var estats = NotificacioEstatAsyncHelper.actualitzarColumnesEstat(legacyHelper, pendents.keySet());
			pendents.forEach((id, resource) -> {
				var estat = estats.get(id);
				if (estat != null) {
					resource.setEstatString(estat);
				}
			});
		} catch (Exception ex) {
			// Es mostra el darrer valor persistit: es tornarà a intentar a la propera consulta
			log.error("Error actualitzant la columna estat de les remeses " + pendents.keySet(), ex);
		}
	}

	// Columnes del llistat que no són camps de l'entitat i s'ordenen per un altre camp
	private static final Map<String, String> CAMPS_ORDENACIO = Map.of(
			// Valor calculat: igual que al llistat JSP, s'ordena per l'estat de la remesa
			"estatString", "estat",
			// Valors de not_notificacio_table (vegeu additionalSpecification)
			"enviadaDate", "taula.enviadaDate",
			"registreNums", "taula.registreNums",
			"titular", "taula.titular");

	@Override
	protected Sort processSort(Sort sort) {

		if (sort == null || sort.isUnsorted()) {
			return sort;
		}
		return Sort.by(sort.stream()
				.map(o -> CAMPS_ORDENACIO.containsKey(o.getProperty()) ? o.withProperty(CAMPS_ORDENACIO.get(o.getProperty())) : o)
				.collect(Collectors.toList()));
	}

	// Màxim d'elements d'una clàusula IN a Oracle
	private static final int MIDA_MAXIMA_IN = 1000;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	protected Page<NotificacioResourceEntity> entityRepositoryFindEntities(String quickFilter, String filter, String[] namedQueries, Pageable pageable) {

		// A partir de la segona pàgina es consulten primer només els ids de la pàgina i després les remeses.
		// Amb OFFSET, la base de dades ha de llegir i ordenar totes les files anteriors a la pàgina: amb les
		// files senceres (i el JOIN a not_notificacio_table) l'ordenació no cap a memòria, i la darrera pàgina
		// d'un milió de remeses tardava més de 30 s. Ordenant només els ids, entre una dècima i uns segons.
		if (pageable.isUnpaged() || pageable.getOffset() == 0 || pageable.getPageSize() > MIDA_MAXIMA_IN) {
			return super.entityRepositoryFindEntities(quickFilter, filter, namedQueries, pageable);
		}
		Specification<NotificacioResourceEntity> specification = toFindProcessedSpecification(quickFilter, filter, namedQueries);
		var cb = entityManager.getCriteriaBuilder();
		var idsQuery = cb.createQuery(Long.class);
		var root = idsQuery.from(NotificacioResourceEntity.class);
		var predicate = specification.toPredicate(root, idsQuery, cb);
		if (predicate != null) {
			idsQuery.where(predicate);
		}
		idsQuery.select(root.get("id")).orderBy(QueryUtils.toOrders(toProcessedSort(pageable.getSort()), root, cb));
		List<Long> ids = entityManager.createQuery(idsQuery)
				.setFirstResult((int) pageable.getOffset())
				.setMaxResults(pageable.getPageSize())
				.getResultList();
		List<NotificacioResourceEntity> content = new ArrayList<>();
		if (!ids.isEmpty()) {
			// Els ids ja compleixen els filtres (i els permisos): només cal carregar-los, amb la taula
			Specification<NotificacioResourceEntity> perIds = (r, q, b) -> r.get("id").in(ids);
			var perId = entityRepository.findAll(perIds.and(additionalSpecification(namedQueries, false))).stream()
					.collect(Collectors.toMap(NotificacioResourceEntity::getId, Function.identity()));
			ids.stream().map(perId::get).filter(Objects::nonNull).forEach(content::add);
		}
		return PageableExecutionUtils.getPage(content, pageable, () -> entityRepository.count(specification));
	}

	@Override
	protected Specification<NotificacioResourceEntity> additionalSpecification(String[] namedQueries, boolean isSingleResult) {

		if (isSingleResult) {
			return null;
		}
		// Els llistats carreguen not_notificacio_table (taula) amb un INNER JOIN, en la mateixa consulta
		// (sense, el @OneToOne EAGER es carregaria amb un SELECT per fila). Ha de ser INNER i no LEFT:
		// amb un LEFT JOIN la base de dades no pot recórrer els índexs de not_notificacio_table per
		// ordenar-hi (data d'enviament, números de registre, titular) i ha d'ordenar totes les remeses.
		// La consulta de COUNT (paginació) no fa el JOIN: amb un milió de remeses passa de més d'un segon
		// a menys d'una dècima. És exacta perquè totes les remeses tenen fila a la taula: si no
		// es pot crear, l'alta falla (NotificacioTableHelper.crearRegistre), i les remeses antigues que no
		// en tenien es reparen en arrencar (procés inicial CREAR_REGISTRES_NOT_NOTIFICACIO_TABLE).
		return (root, query, cb) -> {
			if (NotificacioResourceEntity.class.equals(query.getResultType())) {
				root.fetch("taula", JoinType.INNER);
			}
			return null;
		};
	}

	@Override
	protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries, boolean isSingleResult) {

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
		entity.setProcedimentCodiNotib(entity.getProcediment() != null ? entity.getProcediment().getCodi() : null);
		emplenarProcedimentOrganGestor(entity);
		emplenarGrup(entity, resource);
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
		var procedimentId = entity.getProcediment() != null ? entity.getProcediment().getId() : null;
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
		// Cal fer flush del titular abans de tornar: LegacyHelper.altaNotificacio (cridat just
		// després des d'afterCreateSave) carrega aquest mateix registre amb un altre EntityManager
		// find() per id, que no dispara auto-flush i quedaria cachejat amb titular_id a NULL si
		// encara no s'ha escrit a BD, provocant un NPE més endavant a isComunicacioSir().
		notificacioEnviamentResourceRepository.saveAndFlush(enviamentCreat);
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

		if (EnviamentTipus.SIR.equals(enviament.getNotificacio().getEnviamentTipus()) && destinatari == null) {

		}
		// Crea el destinatari a la base de dades.
		return personaResourceRepository.save(PersonaResourceEntity.builder().resource(destinatari).enviament(enviament).build());
	}

	private void emplenarProcedimentOrganGestor(NotificacioResourceEntity entity) {

		if (entity.getProcediment() != null && entity.getProcediment().isComu() && entity.getOrganGestor() != null) {
			var procedimentOrganGestor = procedimentOrganGestorResourceRepository.findByProcedimentAndOrganGestor(entity.getProcediment(), entity.getOrganGestor());
			procedimentOrganGestor.ifPresent(entity::setProcedimentOrganGestor);
		}
	}

	private void emplenarGrup(NotificacioResourceEntity entity, NotificacioResource resource) {

		if (resource.getGrupCodi() == null) {
			return;
		}
		var grup = grupResourceRepository.findByEntitatAndCodi(entity.getEntitat(), resource.getGrupCodi());
		grup.ifPresent(entity::setGrup);
	}

	/*
	 * Retorna els grups del procediment indicat pel paràmetre "procediment" als quals l'usuari actual te accés,
	 * reaprofitant la mateixa lògica que ja s'utilitza al formulari JSP (GrupServiceImpl.findByProcedimentAndUsuariGrups).
	 */
	class GrupCodiFieldOptionsProvider implements FieldOptionsProvider {
		@Override
		public List<FieldOption> getOptions(String fieldName, Map<String, String[]> requestParameterMap) {
			if (requestParameterMap == null || requestParameterMap.get("procediment") == null) {
				return new ArrayList<>();
			}
			var procedimentId = Long.valueOf(requestParameterMap.get("procediment")[0]);
			return grupService.findByProcedimentAndUsuariGrups(procedimentId).stream().
					map(g -> new FieldOption(g.getCodi(), g.getNom())).
					collect(Collectors.toList());
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
