/**
 * 
 */
package es.caib.notib.logic.service.ws;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.google.common.base.Strings;
import es.caib.notib.client.domini.*;
import es.caib.notib.logic.cacheable.OrganGestorCachable;
import es.caib.notib.logic.helper.AuditHelper;
import es.caib.notib.logic.helper.CacheHelper;
import es.caib.notib.logic.helper.CaducitatHelper;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.DocumentHelper;
import es.caib.notib.logic.helper.EnviamentTableHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.helper.NotificaHelper;
import es.caib.notib.logic.helper.NotificacioHelper;
import es.caib.notib.logic.helper.NotificacioTableHelper;
import es.caib.notib.logic.helper.PermisosHelper;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.DocumentValidDto;
import es.caib.notib.logic.intf.dto.FitxerDto;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.logic.intf.dto.PermisDto;
import es.caib.notib.logic.intf.dto.ProgresDescarregaDto;
import es.caib.notib.logic.intf.dto.TipusEnumDto;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.Enviament;
import es.caib.notib.logic.intf.dto.notificacio.Notificacio;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.exception.ValidationException;
import es.caib.notib.logic.intf.service.AuditService;
import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.service.JustificantService;
import es.caib.notib.logic.intf.service.NotificacioServiceWs;
import es.caib.notib.logic.intf.ws.notificacio.NotificacioServiceWsException;
import es.caib.notib.logic.intf.ws.notificacio.NotificacioServiceWsV2;
import es.caib.notib.persist.entity.DocumentEntity;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.NotificacioEventEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.PersonaEntity;
import es.caib.notib.persist.entity.ProcSerEntity;
import es.caib.notib.persist.entity.ProcSerOrganEntity;
import es.caib.notib.persist.entity.ProcedimentEntity;
import es.caib.notib.persist.repository.AplicacioRepository;
import es.caib.notib.persist.repository.DocumentRepository;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.GrupProcSerRepository;
import es.caib.notib.persist.repository.GrupRepository;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.PersonaRepository;
import es.caib.notib.persist.repository.ProcSerOrganRepository;
import es.caib.notib.persist.repository.ProcSerRepository;
import es.caib.notib.plugin.registre.RespostaJustificantRecepcio;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;

import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.ByteArrayOutputStream;
import java.security.GeneralSecurityException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static es.caib.notib.client.domini.InteressatTipus.ADMINISTRACIO;
import static es.caib.notib.client.domini.InteressatTipus.FISICA_SENSE_NIF;
import static org.apache.commons.lang3.StringUtils.isBlank;


/**
 * Implementació del servei per a l'enviament i consulta de notificacions V2 (Sense paràmetres SEU).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
@WebService(name = "NotificacioServiceV2", serviceName = "NotificacioServiceV2", portName = "NotificacioServiceV2Port",
		targetNamespace = "http://www.caib.es/notib/ws/notificacio",
		endpointInterface = "es.caib.notib.logic.intf.ws.notificacio.NotificacioServiceWsV2")
public class NotificacioServiceWsImplV2 implements NotificacioServiceWsV2, NotificacioServiceWs {

	private static final Pattern UUID_REGEX_PATTERN = Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");
//	private static final Pattern UUID_REGEX_PATTERN = Pattern.compile("/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}/g"); com.google.re2j.Pattern
	private static final OrigenEnum ORIGEN = OrigenEnum.ADMINISTRACIO;
	private static final ValidesaEnum VALIDESA = ValidesaEnum.ORIGINAL;
	private static final TipusDocumentalEnum TIPUS_DOCUMENTAL = TipusDocumentalEnum.NOTIFICACIO;
	private static final boolean MODE_FIRMA = false;

	public static boolean isValidUUID(String str) {
		return str != null && UUID_REGEX_PATTERN.matcher(str).matches();
	}

	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private NotificacioRepository notificacioRepository;
	@Autowired
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Autowired
	private ProcSerRepository procSerRepository;
	@Autowired
	private ProcSerOrganRepository procedimentOrganRepository;
	@Autowired
	private PersonaRepository personaRepository;
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private OrganGestorRepository organGestorRepository;
	@Autowired
	private GrupProcSerRepository grupProcSerRepository;
	@Autowired
	private GrupRepository grupRepository;
	@Autowired
	private AplicacioRepository aplicacioRepository;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private NotificaHelper notificaHelper;
	@Autowired
	private EnviamentTableHelper enviamentTableHelper;
	@Autowired
	private PluginHelper pluginHelper;
//	@Autowired
//	private RegistreNotificaHelper registreNotificaHelper;
	@Autowired
	private IntegracioHelper integracioHelper;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private MetricsHelper metricsHelper;
	@Autowired
	private NotificacioTableHelper notificacioTableHelper;
	@Autowired
	private NotificacioHelper notificacioHelper;
	@Autowired
	private JustificantService justificantService;
	@Autowired
	private DocumentHelper documentHelper;
	@Autowired
	private AuditHelper auditHelper;
	@Autowired
	private MessageHelper messageHelper;
	@Autowired
	private OrganGestorCachable organGestorCachable;
	@Autowired
	private ConfigHelper configHelper;

	@Autowired
	private EnviamentSmService enviamentSmService;

	@PersistenceContext
	private EntityManager entityManager;

//	@Autowired
//	private NotificacioValidator notificacioValidator;

	// Per test
//	public void setNotificacioValidator(NotificacioValidator notificacioValidator) {
//		this.notificacioValidator = notificacioValidator;
//	}
	public void setDocumentHelperTest(DocumentHelper documentHelper) {
		this.documentHelper = documentHelper;
	}


	// Alta notificació
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Transactional
	@Override
	public RespostaAlta alta(Notificacio notificacio) throws NotificacioServiceWsException {

		var resposta = altaV2(notificacio);
		resposta.getReferenciesAsV1().forEach(r -> enviamentSmService.altaEnviament(r.getReferencia()));
		return RespostaAlta.builder().identificador(resposta.getIdentificador()).estat(resposta.getEstat()).referencies(resposta.getReferenciesAsV1())
				.error(resposta.isError()).errorDescripcio(resposta.getErrorDescripcio()).build();
	}

	@Transactional
	@Override
	public RespostaAltaV2 altaV2(Notificacio notificacio) throws NotificacioServiceWsException {

		var timer = metricsHelper.iniciMetrica();
		// Generar informació per al monitor d'integracions
		EntitatEntity entitat = null;
		try {
			entitat = entitatRepository.findByDir3Codi(notificacio.getEmisorDir3Codi());
		} catch (Exception ex) {
			log.error("Error entitat no trobada a la bdd " + notificacio.getEmisorDir3Codi(), ex);
		}
		var info = generateInfoAlta(notificacio, entitat != null ? entitat.getId() : null);
		try {
			log.debug("[ALTA] Alta de notificació: " + notificacio.toString());
			var resposta = RespostaAltaV2.builder().build();

			// Obtenir dades bàsiques per a la notificació
			ProcSerEntity procediment = null;
			OrganGestorEntity organGestor = null;
			ProcSerOrganEntity procedimentOrgan = null;
			DocumentValidDto document = null;
			DocumentValidDto document2 = null;
			DocumentValidDto document3 = null;
			DocumentValidDto document4 = null;
			DocumentValidDto document5 = null;

			// Entitat
			var emisorDir3Codi = notificacio.getEmisorDir3Codi();
			if (entitat != null) {
				ConfigHelper.setEntitatCodi(entitat.getCodi());
				info.setCodiEntitat(entitat.getCodi());
			}
			// Procediment
			if (entitat != null && !Strings.isNullOrEmpty(notificacio.getProcedimentCodi())) {
				procediment = procSerRepository.findByCodiAndEntitat(notificacio.getProcedimentCodi(), entitat);
			}
			// Òrgan
			if (procediment != null && !procediment.isComu()) {
				organGestor = procediment.getOrganGestor();
			}
			if (organGestor == null && !Strings.isNullOrEmpty(notificacio.getOrganGestor())) {
				organGestor = organGestorRepository.findByCodi(notificacio.getOrganGestor());
			}
			// Procediment-Òrgan
			if (procediment != null && procediment.isComu() && organGestor != null) {
				procedimentOrgan = procedimentOrganRepository.findByProcSerIdAndOrganGestorId(procediment.getId(), organGestor.getId());
			}
			// Enviament tipus
			var enviamentTipus = getEnviamentTipus(notificacio);
			var comunicacioSir = EnviamentTipus.SIR.equals(enviamentTipus);
			// Documents
			document = documentHelper.getDocument(notificacio.getDocument());
			if (comunicacioSir) {
				document2 = documentHelper.getDocument(notificacio.getDocument2());
				document3 = documentHelper.getDocument(notificacio.getDocument3());
				document4 = documentHelper.getDocument(notificacio.getDocument4());
				document5 = documentHelper.getDocument(notificacio.getDocument5());
			}

			// Calcular la data de caducitat. Depenent de procediment (Procediment NO obligatori per a comunicacions a administracions)
			// Comprovar si no hi ha una caducitat posar una per defecte (dia acutal + dies caducitat procediment)
			// La caducitat únicament és necessària per a notificacions. Per tant tindrà procediment
			if (procediment != null && (notificacio.getCaducitat() == null)) {
				var caducitat = notificacio.getCaducitatDiesNaturals() != null ? CaducitatHelper.sumarDiesNaturals(new Date(), notificacio.getCaducitatDiesNaturals())
						: CaducitatHelper.sumarDiesLaborals(new Date(), procediment.getCaducitat());
				notificacio.setCaducitat(caducitat);
			}

			// Assignar el cif de l'administració com a NIF del titular tipus administració
			notificacio.getEnviaments().stream()
				.filter(e -> e.getTitular() != null && ADMINISTRACIO.equals(e.getTitular().getInteressatTipus()) && !Strings.isNullOrEmpty(e.getTitular().getDir3Codi()))
				.forEach(e -> {
					if (!Strings.isNullOrEmpty(e.getTitular().getNif())) {
						return;
					}
					var organDir3 = cacheHelper.unitatPerCodi(e.getTitular().getDir3Codi());
					if (organDir3 != null) {
						e.getTitular().setNif(organDir3.getCif());
					}
				});

			// Validació
			var errors = new BindException(notificacio, "notificacio");
			var docs = new DocumentValidDto[] { document, document2, document3, document4, document5 };
			var notificacioValidator = new NotificacioValidator(
					aplicacioRepository,
					grupRepository,
					procSerRepository,
					grupProcSerRepository,
					messageHelper,
					cacheHelper,
					organGestorCachable,
					configHelper);
			notificacioValidator.setWarns(new BindException(notificacio, "notificacio"));
			notificacioValidator.setNotificacio(notificacio);
			notificacioValidator.setEntitat(entitat);
			notificacioValidator.setProcediment(procediment);
			notificacioValidator.setOrganGestor(organGestor);
			notificacioValidator.setDocuments(docs);
			notificacioValidator.setErrors(errors);
			notificacioValidator.setLocale(new Locale("rest"));
 			notificacioValidator.validate();
			if (errors.hasErrors()) {
				String errorDescripcio = errors.getAllErrors().stream().map(e -> e.getCode()).collect(Collectors.joining(", "));
				integracioHelper.addAccioError(info, errorDescripcio);
				log.debug(">> [ALTA] validacio: [errors=" + errorDescripcio + "]");
				return setRespostaError(errorDescripcio);
			}
			String avisos = "";
			if (notificacioValidator.getWarns().hasErrors()) {
				avisos = notificacioValidator.getWarns().getAllErrors().stream().map(e -> e.getCode()).collect(Collectors.joining(", "));
			}


			// Desat
			// DOCUMENTS
			DocumentEntity documentEntity = getDocumentEntity(document);
			DocumentEntity document2Entity = getDocumentEntity(document2);
			DocumentEntity document3Entity = getDocumentEntity(document3);
			DocumentEntity document4Entity = getDocumentEntity(document4);
			DocumentEntity document5Entity = getDocumentEntity(document5);

			// NOTIFICACIO
			var notificacioEntity = NotificacioEntity.getBuilderV2(
							entitat,
							emisorDir3Codi,
							organGestor,
							pluginHelper.getNotibTipusComunicacioDefecte(),
							enviamentTipus,
							notificacio.getConcepte(),
							notificacio.getDescripcio(),
							notificacio.getEnviamentDataProgramada(),
							notificacio.getRetard(),
							notificacio.getCaducitat(),
							notificacio.getUsuariCodi(),
							notificacio.getProcedimentCodi(),
							procediment,
							notificacio.getGrupCodi(),
							notificacio.getNumExpedient(),
							TipusUsuariEnumDto.APLICACIO,
							procedimentOrgan,
							notificacio.getIdioma(),
							UUID.randomUUID().toString())
					.document(documentEntity)
					.document2(document2Entity)
					.document3(document3Entity)
					.document4(document4Entity)
					.document5(document5Entity)
					.build();

			var notificacioGuardada = notificacioRepository.saveAndFlush(notificacioEntity);
			notificacioTableHelper.crearRegistre(notificacioEntity);
			auditHelper.auditaNotificacio(notificacioEntity, AuditService.TipusOperacio.CREATE, "NotificacioServiceWsImplV2.altaV2");
			log.debug(">> [ALTA] notificacio guardada");
			// ENVIAMNETS
			List<EnviamentReferenciaV2> referencies = new ArrayList<>();
			EnviamentReferenciaV2 ref;
			for (var enviament: notificacio.getEnviaments()) {
				ref = saveEnviament(entitat, notificacioGuardada, enviament);
				referencies.add(ref);
			}
			log.debug(">> [ALTA] enviaments creats");
			notificacioGuardada = notificacioRepository.saveAndFlush(notificacioGuardada);
			return generaResposta(info, notificacioGuardada, referencies, avisos);
		} catch (Exception ex) {
			log.error("Error creant notificació", ex);
			integracioHelper.addAccioError(info, "Error creant la notificació", ex);
			throw new RuntimeException("[NOTIFICACIO/COMUNICACIO] Hi ha hagut un error creant la " + notificacio.getEnviamentTipus().name() + ": " + ex.getMessage(), ex);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private IntegracioInfo generateInfoAlta(Notificacio notificacio, Long entitatId) {

		IntegracioInfo info = new IntegracioInfo(IntegracioCodi.CALLBACK, "Alta de notificació", IntegracioAccioTipusEnumDto.RECEPCIO);

		ObjectMapper mapper  = new ObjectMapper();
		Map<String, Object> notificaAtributMap = new HashMap<>();
		mapper.registerModule(new SimpleModule() {
			@Override
			public void setupModule(SetupContext context) {
				super.setupModule(context);
				context.addBeanSerializerModifier(new BeanSerializerModifier() {
					@Override
					public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
						List<BeanPropertyWriter> serializableProperties = new ArrayList<>();
						for (BeanPropertyWriter beanPropertyWriter: beanProperties) {
							if (!"contingutBase64".equals(beanPropertyWriter.getName())) {
								serializableProperties.add(beanPropertyWriter);
							}
						}
						return serializableProperties;
					}
				});
			}
		});

		try {
			notificaAtributMap = mapper.readValue(mapper.writeValueAsString(notificacio), HashMap.class);
		} catch (Exception e) {
			notificaAtributMap.put("Error", "S'ha produït un error al intentar llegir la informació de la notificació");
		}
		notificaAtributMap.entrySet().stream().filter(e -> e.getValue() != null).forEach((e) -> info.addParam(e.getKey(), e.getValue().toString()));
		integracioHelper.addAplicacioAccioParam(info, entitatId);
		return info;
	}

	private RespostaAltaV2 setRespostaError(String descripcioError) {
		return RespostaAltaV2.builder().error(true).estat(NotificacioEstatEnum.PENDENT).errorDescripcio(descripcioError).errorData(new Date()).build();
	}


	// Consulta estat Notificació
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	@Transactional(readOnly = true)
	public RespostaConsultaEstatNotificacio consultaEstatNotificacio(String identificador) {

		var resposta = consultaEstatNotificacioV2(identificador);
		return RespostaConsultaEstatNotificacio.builder().estat(resposta.getEstat()).error(resposta.isError()).errorData(resposta.getErrorData())
				.errorDescripcio(resposta.getErrorDescripcio()).build();
	}

	@Override
	@Transactional(readOnly = true)
	public RespostaConsultaEstatNotificacioV2 consultaEstatNotificacioV2(String identificador) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consultant estat notificacio amb identificador: " + identificador);
			var info = new IntegracioInfo(IntegracioCodi.CALLBACK, "Consulta de l'estat d'una notificació", IntegracioAccioTipusEnumDto.RECEPCIO, new AccioParam("Identificador xifrat de la notificacio", identificador));
			var resposta = RespostaConsultaEstatNotificacioV2.builder().identificador(identificador).build();
			try {
				var notificacio = getNotificacioByIdentificador(identificador, resposta, info);
				if (notificacio == null) {
					return resposta;
				}
				info.setAplicacio(notificacio.getTipusUsuari(), notificacio.getUsuariCodi());
				resposta.setEstat(toNotificacioEstat(notificacio.getEstat()));
				resposta.setTipus(notificacio.getEnviamentTipus().name());
				resposta.setEmisorDir3(notificacio.getEmisorDir3Codi());
				if (notificacio.getProcediment() != null) {
					resposta.setProcediment(Procediment.builder().codiSia(notificacio.getProcediment().getCodi()).nom(notificacio.getProcediment().getNom()).build());
				}
				resposta.setConcepte(notificacio.getConcepte());
				if (notificacio.getOrganGestor() != null) {
					resposta.setOrganGestorDir3(notificacio.getOrganGestor().getCodi());
				}
				resposta.setNumExpedient(notificacio.getNumExpedient());
				resposta.setDataCreada(notificacio.getCreatedDate().isPresent() ? Date.from(notificacio.getCreatedDate().get().atZone(ZoneId.systemDefault()).toInstant()) : null);
				resposta.setDataEnviada(notificacio.getNotificaEnviamentNotificaData());
				resposta.setDataFinalitzada(notificacio.getEstatDate());
				resposta.setDataProcessada(notificacio.getEstatProcessatDate());
				var errorEvent = notificacioHelper.getNotificaErrorEvent(notificacio);
				if (errorEvent != null) {
					var estat = notificacio.getEstat();
					var isError = !NotificacioEstatEnumDto.FINALITZADA.equals(estat) && !NotificacioEstatEnumDto.PROCESSADA.equals(estat) && !Strings.isNullOrEmpty(errorEvent.getErrorDescripcio());
					resposta.setError(isError);
					resposta.setErrorData(errorEvent.getData());
					resposta.setErrorDescripcio(errorEvent.getErrorDescripcio());
				}
				integracioHelper.addAccioOk(info);
				return resposta;
			} catch (Exception ex) {
				integracioHelper.addAccioError(info, "Error al obtenir la informació de l'estat de la notificació", ex);
				throw new RuntimeException("[NOTIFICACIO/COMUNICACIO] Hi ha hagut un error consultant la notificació: " + ex.getMessage(), ex);
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private NotificacioEstatEnum toNotificacioEstat(NotificacioEstatEnumDto estat) {

		switch (estat) {
			case PENDENT:
				return NotificacioEstatEnum.PENDENT;
			case ENVIADA:
				return NotificacioEstatEnum.ENVIADA;
			case ENVIADA_AMB_ERRORS:
				return NotificacioEstatEnum.ENVIADA_AMB_ERRORS;
			case REGISTRADA:
				return NotificacioEstatEnum.REGISTRADA;
			case FINALITZADA:
				return NotificacioEstatEnum.FINALITZADA;
			case FINALITZADA_AMB_ERRORS:
				return NotificacioEstatEnum.FINALITZADA_AMB_ERRORS;
			case PROCESSADA:
				return NotificacioEstatEnum.PROCESSADA;
			default:
				return null;
		}
	}


	// Consulta estat Enviament
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	@Transactional(readOnly = true)
	public RespostaConsultaEstatEnviament consultaEstatEnviament(String referencia) {

		var resposta = consultaEstatEnviamentV2(referencia);
		return RespostaConsultaEstatEnviament.builder()
				.estat(resposta.getEstat())
				.estatData(resposta.getEstatData())
				.estatOrigen(resposta.getDatat() != null ? resposta.getDatat().getOrigen() : null)
				.estatDescripcio(resposta.getEstatDescripcio())
				.receptorNif(resposta.getDatat() != null ? resposta.getDatat().getReceptorNif() : null)
				.receptorNom(resposta.getDatat() != null ? resposta.getDatat().getReceptorNom() : null)
				.certificacio(resposta.getCertificacio())
				.error(resposta.isError())
				.errorData(resposta.getErrorData())
				.errorDescripcio(resposta.getErrorDescripcio())
				.build();
	}

	@Override
	@Transactional(readOnly = true)
	public RespostaConsultaEstatEnviamentV2 consultaEstatEnviamentV2(String referencia) throws NotificacioServiceWsException {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consultant estat enviament amb referencia: " + referencia);
			IntegracioInfo info = new IntegracioInfo(IntegracioCodi.CALLBACK,"Consulta de l'estat d'un enviament", IntegracioAccioTipusEnumDto.RECEPCIO);
			RespostaConsultaEstatEnviamentV2 resposta = RespostaConsultaEstatEnviamentV2.builder().referencia(referencia).build();

			try {
				NotificacioEnviamentEntity enviament = getEnviamentByReferencia(referencia, resposta, info);
				if (enviament == null) {
					return resposta;
				}
				// Si Notib no utilitza el servei Adviser de @Notifica, i ja ha estat enviat a @Notifica
				// serà necessari consultar l'estat de la notificació a Notifica
				if (!notificaHelper.isAdviserActiu() && !enviament.isNotificaEstatFinal()
						&& !enviament.getNotificaEstat().equals(EnviamentEstat.NOTIB_PENDENT)) {
					log.debug("Consultat estat de l'enviament amb referencia " + referencia + " a Notifica.");
					enviament = notificaHelper.enviamentRefrescarEstat(enviament.getId());
				}
				resposta.setIdentificador(enviament.getNotificacio().getReferencia());
				resposta.setNotificaIndentificador(enviament.getNotificaIdentificador());
				resposta.setEstat(enviament.getNotificaEstat());
				resposta.setEstatData(getEstatDate(enviament));
				resposta.setEstatDescripcio(enviament.getNotificaEstatDescripcio());
				resposta.setDehNif(enviament.getDehNif());
				resposta.setDehObligat(enviament.getDehObligat() != null ? enviament.getDehObligat() : false);
				resposta.setEntragaPostalActiva(enviament.getEntregaPostal() != null);
				if (enviament.getEntregaPostal() != null) {
					resposta.setAdressaPostal(enviament.getEntregaPostal().toString());
				}
				boolean esSir = EnviamentTipus.COMUNICACIO.equals(enviament.getNotificacio().getEnviamentTipus()) &&
						InteressatTipus.ADMINISTRACIO.equals(enviament.getTitular().getInteressatTipus());
				resposta.setEnviamentSir(esSir);

				// INTERESSAT
				PersonaV2 interessat = PersonaV2.builder()
						.interessatTipus(enviament.getTitular().getInteressatTipus())
						.nom(enviament.getTitular().getNom())
						.llinatge1(enviament.getTitular().getLlinatge1())
						.llinatge2(enviament.getTitular().getLlinatge2())
						.nif(enviament.getTitular().getNif())
						.telefon(enviament.getTitular().getTelefon())
						.email(enviament.getTitular().getEmail())
						.raoSocial(enviament.getTitular().getRaoSocial())
						.dir3Codi(enviament.getTitular().getDir3Codi())
						.incapacitat(enviament.getTitular().isIncapacitat())
						.build();
				resposta.setInteressat(interessat);
				if (enviament.getDestinataris() != null && !enviament.getDestinataris().isEmpty()) {
					List<PersonaV2> representants = new ArrayList<>();
					for (PersonaEntity destinatari: enviament.getDestinataris()) {
						PersonaV2 representant = PersonaV2.builder()
								.interessatTipus(destinatari.getInteressatTipus())
								.nom(destinatari.getNom())
								.llinatge1(destinatari.getLlinatge1())
								.llinatge2(destinatari.getLlinatge2())
								.nif(destinatari.getNif())
								.telefon(destinatari.getTelefon())
								.email(destinatari.getEmail())
								.raoSocial(destinatari.getRaoSocial())
								.dir3Codi(destinatari.getDir3Codi())
								.build();
						representants.add(representant);
					}
					resposta.setRepresentants(representants);
				}
				// REGISTRE
				if (NotificacioEstatEnumDto.isRegistrat(enviament.getNotificacio().getEstat())) {
					Registre registre = Registre.builder()
							.numeroFormatat(enviament.getRegistreNumeroFormatat())
							.data(enviament.getRegistreData())
							.estat(toRegistreEstat(enviament.getRegistreEstat()))
							.oficina(enviament.getNotificacio().getRegistreOficinaNom())
							.llibre(enviament.getNotificacio().getRegistreLlibreNom())
							.build();
					resposta.setRegistre(registre);
				}
				// SIR
				if (esSir && NotificacioEstatEnumDto.isSirEnviat(enviament.getNotificacio().getEstat())) {
					Sir sir = Sir.builder()
							.dataRecepcio(enviament.getSirRecepcioData())
							.dataRegistreDesti(enviament.getSirRegDestiData())
							.build();
					resposta.setSir(sir);
				}
				// DATAT
				if (!esSir && !isBlank(enviament.getNotificaIdentificador())) {
					ReceptorInfo receptor = getReceptor(enviament);

					Datat datat = Datat.builder()
							.estat(enviament.getNotificaEstat())
							.data(enviament.getNotificaEstatData())
							.origen(enviament.getNotificaDatatOrigen())
							.receptorNif(receptor.getNif())
							.receptorNom(receptor.getNom())
							.numSeguiment(enviament.getNotificaDatatNumSeguiment())
							.errorDescripcio(enviament.getNotificaDatatErrorDescripcio())
							.build();
					resposta.setDatat(datat);
				}

				// Certificacio
				if (enviament.getNotificaCertificacioData() != null) {
					log.debug("Guardant certificació enviament amb referencia: " + referencia);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					pluginHelper.gestioDocumentalGet(enviament.getNotificaCertificacioArxiuId(), PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS, baos, false);
					String certificacioBase64 = org.apache.commons.codec.binary.Base64.encodeBase64String(baos.toByteArray());

					Certificacio certificacio = Certificacio.builder()
							.data(enviament.getNotificaCertificacioData())
							.origen(enviament.getNotificaCertificacioOrigen())
							.contingutBase64(certificacioBase64)
							.hash(enviament.getNotificaCertificacioHash())
							.metadades(enviament.getNotificaCertificacioMetadades())
							.csv(enviament.getNotificaCertificacioCsv())
							.tipusMime(enviament.getNotificaCertificacioMime())
							.build();
					if (enviament.getNotificaCertificacioTamany() != null)
						certificacio.setTamany(enviament.getNotificaCertificacioTamany());

					resposta.setCertificacio(certificacio);
					log.debug("Certificació de l'enviament amb referencia: " + referencia + " s'ha obtingut correctament.");
				}

				if (enviament.getUltimEvent() != null) {
					NotificacioEstatEnumDto estat = enviament.getNotificacio().getEstat();
					NotificacioEventEntity errorEvent = enviament.getUltimEvent();
					var isError = !NotificacioEstatEnumDto.FINALITZADA.equals(estat) && !NotificacioEstatEnumDto.PROCESSADA.equals(estat) && !Strings.isNullOrEmpty(errorEvent.getErrorDescripcio());
					resposta.setError(isError);
					resposta.setErrorData(errorEvent.getData());
					resposta.setErrorDescripcio(errorEvent.getErrorDescripcio());
					log.debug("Notifica error de l'enviament amb referencia: " + referencia + ": " + enviament.isNotificaError());
				}
			} catch (Exception ex) {
				log.debug("Error consultar estat enviament amb referencia: " + referencia, ex);
				integracioHelper.addAccioError(info, "Error al obtenir l'estat de l'enviament", ex);
				resposta.setError(true);
				resposta.setErrorData(new Date());
				resposta.setErrorDescripcio("Error inesperat al obtenir la informació de l'enviament amb referencia: " + referencia);
				return resposta;
			}
			integracioHelper.addAccioOk(info);
			return resposta;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private Date getEstatDate(NotificacioEnviamentEntity enviament) {
		switch (enviament.getNotificacio().getEstat()) {
			case PENDENT:
				return Date.from(enviament.getCreatedDate().get().atZone(ZoneId.systemDefault()).toInstant());
			case REGISTRADA:
				return enviament.getRegistreData();
			case PROCESSADA:
				return enviament.getNotificacio().getEstatProcessatDate();
			default:
				return enviament.getNotificaEstatData() != null ? enviament.getNotificaEstatData() : enviament.getNotificacio().getNotificaEnviamentNotificaData();
		}
	}

	private RegistreEstatEnum toRegistreEstat(NotificacioRegistreEstatEnumDto estat) {
		if (estat == null) return null;
		switch (estat) {
			case VALID:
				return RegistreEstatEnum.VALID;
			case RESERVA:
				return RegistreEstatEnum.RESERVA;
			case PENDENT:
				return RegistreEstatEnum.PENDENT;
			case OFICI_EXTERN:
				return RegistreEstatEnum.OFICI_EXTERN;
			case OFICI_INTERN:
				return RegistreEstatEnum.OFICI_INTERN;
			case OFICI_ACCEPTAT:
				return RegistreEstatEnum.OFICI_ACCEPTAT;
			case DISTRIBUIT:
				return RegistreEstatEnum.DISTRIBUIT;
			case ANULAT:
				return RegistreEstatEnum.ANULAT;
			case RECTIFICAT:
				return RegistreEstatEnum.RECTIFICAT;
			case REBUTJAT:
				return RegistreEstatEnum.REBUTJAT;
			case REENVIAT:
				return RegistreEstatEnum.REENVIAT;
			case DISTRIBUINT:
				return RegistreEstatEnum.DISTRIBUINT;
			case OFICI_SIR:
				return RegistreEstatEnum.OFICI_SIR;
			default:
				return null;
		}
	}

	private ReceptorInfo getReceptor(NotificacioEnviamentEntity enviament) {

		var receptor = ReceptorInfo.builder().nif(enviament.getNotificaDatatReceptorNif()).nom(enviament.getNotificaDatatReceptorNom()).build();
		if (isBlank(receptor.getNif())) {
			if (enviament.getDestinataris() == null || enviament.getDestinataris().isEmpty()) {
				receptor.setNif(enviament.getTitular().getNif());
				receptor.setNom(enviament.getTitular().getNomSencer());
			} else if (!InteressatTipus.FISICA.equals(enviament.getTitular().getInteressatTipus()) && enviament.getDestinataris().size() == 1) {
				receptor.setNif(enviament.getDestinataris().get(0).getNif());
				receptor.setNom(enviament.getDestinataris().get(0).getNomSencer());
			}
		}

		return receptor;
	}


	// Consulta dades de registre
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	@Transactional(readOnly = true)
	public RespostaConsultaDadesRegistre consultaDadesRegistre(DadesConsulta dadesConsulta) {
		RespostaConsultaDadesRegistreV2 resposta = consultaDadesRegistreV2(dadesConsulta);
		return RespostaConsultaDadesRegistre.builder()
				.numRegistre(resposta.getNumRegistre())
				.numRegistreFormatat(resposta.getNumRegistreFormatat())
				.dataRegistre(resposta.getDataRegistre())
				.justificant(resposta.getJustificant())
				.error(resposta.isError())
				.errorData(resposta.getErrorData())
				.errorDescripcio(resposta.getErrorDescripcio())
				.build();
	}

	@Override
	@Transactional(readOnly = true)
	public RespostaConsultaDadesRegistreV2 consultaDadesRegistreV2(DadesConsulta dadesConsulta) {
		var timer = metricsHelper.iniciMetrica();
		try {
			String json = "S'ha produït un error al intentar llegir la informació de les dades de la consulta";
			ObjectMapper mapper  = new ObjectMapper();
			try {
				json = mapper.writeValueAsString(dadesConsulta);
			} catch (Exception e) {
				log.error("Error convertint les dades de consulta a JSON", e);
			}

			IntegracioInfo info = new IntegracioInfo(
					IntegracioCodi.CALLBACK,
					"Consulta de les dades de registre",
					IntegracioAccioTipusEnumDto.RECEPCIO,
					new AccioParam("Dades de la consulta", json));

			var resposta = new RespostaConsultaDadesRegistreV2();
			NotificacioEntity notificacio = null;
			String numeroRegistreFormatat = null;

			// Donam prioritat a l'enviament
			if (dadesConsulta.getReferencia() != null) {
				log.debug("Consultant les dades de registre de l'enviament amb referència: " + dadesConsulta.getReferencia());
				NotificacioEnviamentEntity enviament = getEnviamentByReferencia(dadesConsulta.getReferencia(), resposta, info);
				if (enviament == null)
					return resposta;

				notificacio = enviament.getNotificacio();

				//Dades registre
				numeroRegistreFormatat = obtenirDadesRegistre(resposta, enviament, notificacio, info, dadesConsulta.getReferencia(), true);
			} else if (dadesConsulta.getIdentificador() != null) {
				log.debug("Consultant les dades de registre de la notificació amb identificador: " + dadesConsulta.getIdentificador());
				notificacio = getNotificacioByIdentificador(dadesConsulta.getIdentificador(), resposta, info);
				if (notificacio == null)
					return resposta;

				NotificacioEnviamentEntity enviament = notificacio.getEnviaments().stream().findAny().get();

				//Dades registre
				numeroRegistreFormatat = obtenirDadesRegistre(resposta, enviament, notificacio, info, dadesConsulta.getIdentificador(), false);
			}
			if (resposta.isError())
				return resposta;

			// Consutla justificant
			if (dadesConsulta.isAmbJustificant()) {
				RespostaJustificantRecepcio justificant = pluginHelper.obtenirJustificant(notificacio.getEmisorDir3Codi(), numeroRegistreFormatat);
				var entitatId = notificacio.getEntitat() != null ? notificacio.getEntitat().getId() : null;
				integracioHelper.addAplicacioAccioParam(info, entitatId);
				if (justificant.getErrorCodi() == null || "OK".equals(justificant.getErrorCodi())) {
					resposta.setJustificant(justificant.getJustificant());
				} else {
					resposta.setError(true);
					resposta.setErrorData(new Date());
					String errorDescripcio = "No s'ha pogut obtenir el justificant de registre. " + justificant.getErrorCodi() + ": " + justificant.getErrorDescripcio();
					resposta.setErrorDescripcio(errorDescripcio);
					integracioHelper.addAccioError(info, errorDescripcio);
					return  resposta;
				}
			}
			integracioHelper.addAccioOk(info);
			return resposta;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Nullable
	private String obtenirDadesRegistre(RespostaConsultaDadesRegistreV2 resposta, NotificacioEnviamentEntity enviament, NotificacioEntity notificacio, IntegracioInfo info, String identificador, boolean isEnviament) {
		String numeroRegistreFormatat = isEnviament ? enviament.getRegistreNumeroFormatat() : notificacio.getRegistreNumeroFormatat();
		Date dataRegistre = isEnviament ? enviament.getRegistreData() : notificacio.getRegistreData();

		if (numeroRegistreFormatat == null) {
			resposta.setError(true);
			resposta.setErrorData(new Date());
			var idf = (isEnviament ? "l'enviament:" : "la notificacio:") + identificador;
			resposta.setErrorDescripcio("Error: No s'ha trobat cap registre relacionat amb " + idf);
			integracioHelper.addAccioError(info, "No hi ha cap registre associat a " + idf);
			return null;
		}

		resposta.setDataRegistre(dataRegistre);
		resposta.setNumRegistreFormatat(numeroRegistreFormatat);
		if (!isEnviament)
			resposta.setNumRegistre(notificacio.getRegistreNumero());
		resposta.setOficina(notificacio.getRegistreOficinaNom());
		resposta.setLlibre(notificacio.getRegistreLlibreNom());
		// SIR
		boolean esSir = EnviamentTipus.COMUNICACIO.equals(notificacio.getEnviamentTipus()) &&
				InteressatTipus.ADMINISTRACIO.equals(enviament.getTitular().getInteressatTipus());
		resposta.setEnviamentSir(esSir);
		if (esSir && (isEnviament || notificacio.getEnviaments().size() == 1)) {
			resposta.setDataRecepcioSir(enviament.getSirRecepcioData());
			resposta.setDataRegistreDestiSir(enviament.getSirRegDestiData());
		}
		return numeroRegistreFormatat;
	}


	// Consulta justificant d'enviament
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	@Transactional(readOnly = true)
	public RespostaConsultaJustificantEnviament consultaJustificantEnviament(
			String identificador) {
		var timer = metricsHelper.iniciMetrica();
		try {
			IntegracioInfo info = new IntegracioInfo(
					IntegracioCodi.CALLBACK,
					"Consulta de la justificació d'una notificació",
					IntegracioAccioTipusEnumDto.RECEPCIO,
					new AccioParam("Identificador xifrat de la notificacio", identificador));
			RespostaConsultaJustificantEnviament resposta = new RespostaConsultaJustificantEnviament();

			NotificacioEntity notificacio = getNotificacioByIdentificador(identificador, resposta, info);
			if (notificacio == null)
				return resposta;

			ProgresDescarregaDto progres = justificantService.consultaProgresGeneracioJustificant(identificador);
			if (progres != null && progres.getProgres() != null &&  progres.getProgres() < 100) {
				// Ja hi ha un altre procés generant el justificant
				resposta.setError(true);
				resposta.setErrorData(new Date());
				resposta.setErrorDescripcio("Ja hi ha un altre procés generant el justificant de la notificacio");
				return resposta;
			}

			try {
				FitxerDto justificantDto = justificantService.generarJustificantEnviament(notificacio.getId(), identificador);
				if (justificantDto == null || justificantDto.getContingut() == null) {
					resposta.setError(true);
					resposta.setErrorData(new Date());
					resposta.setErrorDescripcio("Error durant la generació del justificant de la notificació");
					return resposta;
				}
				resposta.setJustificant(Fitxer.builder()
						.nom(justificantDto.getNom())
						.contentType(justificantDto.getContentType())
						.tamany(justificantDto.getTamany())
						.contingut(org.apache.commons.codec.binary.Base64.encodeBase64(justificantDto.getContingut())).build());
				integracioHelper.addAccioOk(info);
				return resposta;

			} catch (Exception ex) {
				resposta.setError(true);
				resposta.setErrorData(new Date());
				resposta.setErrorDescripcio("Error durant la generació del justificant de la notificació");
				return resposta;
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}


	// Donar permís de consulta sobre procediment
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	@Transactional
	public boolean donarPermisConsulta(PermisConsulta permisConsulta) {
		var timer = metricsHelper.iniciMetrica();
		try {
			String json = "S'ha produït un error al intentar llegir la informació dels permisos";
			ObjectMapper mapper  = new ObjectMapper();
			try {
				json = mapper.writeValueAsString(permisConsulta);
			} catch (Exception e) {
				log.error("Error convertint el permis a JSON", e);
			}

			IntegracioInfo info = new IntegracioInfo(
					IntegracioCodi.CALLBACK,
					"Donar permis de consulta",
					IntegracioAccioTipusEnumDto.RECEPCIO,
					new AccioParam("Permís", json));

			boolean totbe = false;
			try {

				EntitatEntity entitat = entitatRepository.findByDir3Codi(permisConsulta.getCodiDir3Entitat());
				if (entitat == null) {
					integracioHelper.addAccioError(info, "Error donant permís de consulta: no s'ha especificat un codi Dir3 d'entitat vàlid");
					throw new RuntimeException("No s'ha pogut assignar el permís a l'usuari: " + permisConsulta.getUsuariCodi() + ". No s'ha especificat un codi Dir3 d'entitat vàlid");
				}
				ConfigHelper.setEntitatCodi(entitat.getCodi());
				info.setCodiEntitat(entitat.getCodi());
				integracioHelper.addAplicacioAccioParam(info, entitat.getId());
				var procediment = procSerRepository.findByEntitatAndCodiProcediment(entitat, permisConsulta.getProcedimentCodi());
				if (procediment == null) {
					integracioHelper.addAccioError(info, "Procediment inexistent");
					return false;
				}
				var permisos = permisosHelper.findPermisos(procediment.getId(), ProcedimentEntity.class);
				if (permisos == null || permisos.isEmpty()) {
					PermisDto permisNou = new PermisDto();
					permisos = new ArrayList<>();
					permisNou.setPrincipal(permisConsulta.getUsuariCodi());
					permisNou.setTipus(TipusEnumDto.USUARI);
					//Consulta
					permisNou.setRead(permisConsulta.isPermisConsulta());
					permisNou.setProcessar(false);
					permisNou.setNotificacio(false);
					//gestió
					permisNou.setAdministration(false);
					permisos.add(permisNou);
				}
				for (var permisDto : permisos) {
					if (permisDto.getPrincipal().equals(permisConsulta.getUsuariCodi())) {
						permisDto.setRead(permisConsulta.isPermisConsulta());
						permisosHelper.updatePermis(procediment.getId(), ProcedimentEntity.class, permisDto);
					}
				}
				totbe = true;
				integracioHelper.addAccioOk(info);
			} catch (Exception ex) {
				integracioHelper.addAccioError(info, "Error donant permís de consulta", ex);
				throw new RuntimeException("No s'ha pogut assignar el permís a l'usuari: " + permisConsulta.getUsuariCodi(), ex);
			}
			return totbe;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}


	// Obtenir notificació a partir de l'identificador enviat al client

	@Nullable
	private NotificacioEntity getNotificacioByIdentificador(String identificador, RespostaBase resposta, IntegracioInfo info) {

		Throwable t = null;
		NotificacioEntity notificacio = null;
		if (isValidUUID(identificador)) {
			notificacio = notificacioRepository.findByReferencia(identificador);
		} else {
			try {
				Long notificacioId = notificaHelper.desxifrarId(identificador);
				info.getParams().add(new AccioParam("Identificador desxifrat de la notificació", String.valueOf(notificacioId)));
				notificacio = notificacioRepository.findById(notificacioId).orElse(null);
			} catch (GeneralSecurityException ex) {
				t = ex;
				integracioHelper.addAccioError(info, "Error al desxifrar l'identificador de la notificació a consultar", ex);
			}
		}
		if (notificacio == null) {
			var errorDesc = (t == null ? "Error: No s'ha trobat cap notificació amb l'identificador " : "No s'ha pogut desxifrar l'identificador de la notificació ") + identificador;
			resposta.setError(true);
			resposta.setErrorData(new Date());
			resposta.setErrorDescripcio(errorDesc);
			integracioHelper.addAplicacioAccioParam(info, null);
			integracioHelper.addAccioError(info, errorDesc, t);
		} else {
			ConfigHelper.setEntitatCodi(notificacio.getEntitat().getCodi());
			integracioHelper.addAplicacioAccioParam(info, notificacio.getEntitat().getId());
			info.setCodiEntitat(notificacio.getEntitat().getCodi());
		}
		return notificacio;
	}

	// Obtenir enviament a partir de la referència enviada al client

	@Nullable
	private NotificacioEnviamentEntity getEnviamentByReferencia(String referencia, RespostaBase resposta, IntegracioInfo info) {
		Throwable t = null;
		NotificacioEnviamentEntity enviament = null;
		if (isValidUUID(referencia)) {
			enviament = notificacioEnviamentRepository.findByNotificaReferencia(referencia);
		} else {
			try {
				Long enviamentId = notificaHelper.desxifrarId(referencia);
				info.getParams().add(new AccioParam("Referència desxifrada de l'enviament'", String.valueOf(enviamentId)));
				enviament = notificacioEnviamentRepository.findById(enviamentId).orElse(null);
			} catch (GeneralSecurityException ex) {
				t = ex;
				integracioHelper.addAccioError(info, "Error al desxifrar l'identificador de la notificació a consultar", ex);
			}
		}
		if (enviament == null) {
			var errorDesc = (t == null ? "Error: No s'ha trobat cap enviament amb la referència " : "No s'ha pogut desxifrar la referència de l'enviament ") + referencia;
			resposta.setError(true);
			resposta.setErrorData(new Date());
			resposta.setErrorDescripcio(errorDesc);
			integracioHelper.addAplicacioAccioParam(info, null);
			integracioHelper.addAccioWarn(info, errorDesc, t);
			return enviament;
		}
		var isEstatFinal = EnviamentEstat.EXPIRADA.equals(enviament.getNotificaEstat()) || EnviamentEstat.REBUTJADA.equals(enviament.getNotificaEstat()) || EnviamentEstat.NOTIFICADA.equals(enviament.getNotificaEstat());
		if (enviament.getNotificaCertificacioArxiuId() == null && isEstatFinal) {
			try {
				notificaHelper.enviamentRefrescarEstat(enviament.getId());
				entityManager.refresh(enviament);
			} catch (Exception ex) {
				log.error("No s'ha pogut actualitzar la certificació de l'enviament amb id: " + enviament.getId(), ex);
			}
		}
		ConfigHelper.setEntitatCodi(enviament.getNotificacio().getEntitat().getCodi());
		integracioHelper.addAplicacioAccioParam(info, enviament.getNotificacio().getEntitat().getId());
		info.setCodiEntitat(enviament.getNotificacio().getEntitat().getCodi());

		return enviament;
	}


	private RespostaAltaV2 generaResposta(IntegracioInfo info, NotificacioEntity notificacioGuardada, List<EnviamentReferenciaV2> referencies, String warns) {

		RespostaAltaV2 resposta = new RespostaAltaV2();
		resposta.setErrorDescripcio(warns);
		resposta.setIdentificador(notificacioGuardada.getReferencia());
		switch (notificacioGuardada.getEstat()) {
			case PENDENT:
				resposta.setEstat(NotificacioEstatEnum.PENDENT);
				break;
			case ENVIADA:
				resposta.setEstat(NotificacioEstatEnum.ENVIADA);
				break;
			case ENVIADA_AMB_ERRORS:
				resposta.setEstat(NotificacioEstatEnum.ENVIADA_AMB_ERRORS);
				break;
			case REGISTRADA:
				resposta.setEstat(NotificacioEstatEnum.REGISTRADA);
				break;
			case FINALITZADA:
				resposta.setEstat(NotificacioEstatEnum.FINALITZADA);
				break;
			case FINALITZADA_AMB_ERRORS:
				resposta.setEstat(NotificacioEstatEnum.FINALITZADA_AMB_ERRORS);
				break;
			case PROCESSADA:
				resposta.setEstat(NotificacioEstatEnum.PROCESSADA);
				break;
			default:
				break;
		}
		var errorEvent = notificacioHelper.getNotificaErrorEvent(notificacioGuardada);
		if (errorEvent != null) {
			info.setCodiEntitat(errorEvent.getNotificacio().getEntitat().getCodi());
			resposta.setError(true);
			resposta.setErrorDescripcio(errorEvent.getErrorDescripcio());
			resposta.setErrorData(new Date());
		}
		resposta.setReferencies(referencies);
		resposta.setDataCreacio(notificacioGuardada.getCreatedDate().isPresent() ? Date.from(notificacioGuardada.getCreatedDate().orElseThrow().atZone(ZoneId.systemDefault()).toInstant()) : null);
		log.debug(">> [ALTA] afegides referències");
		integracioHelper.addAccioOk(info);
		return resposta;
	}

	private EnviamentReferenciaV2 saveEnviament(EntitatEntity entitat, NotificacioEntity notificacioGuardada, Enviament enviament) {

		var serveiTipus = getServeiTipus(enviament);
		if (enviament.isEntregaPostalActiva() && enviament.getEntregaPostal() != null && enviament.getEntregaPostal().getTipus() == null) {
			throw new ValidationException("ENTREGA_POSTAL", "L'entrega postal te el camp tipus buit");
		}

		var titular = saveTitular(enviament);
		var destinataris = getDestinataris(enviament);
		var enviamentSaved = notificacioEnviamentRepository.saveAndFlush(
				NotificacioEnviamentEntity.getBuilderV2(enviament,
						entitat.isAmbEntregaDeh(),
						serveiTipus,
						notificacioGuardada,
						titular,
						destinataris,
						UUID.randomUUID().toString()).build());
		enviamentSmService.acquireStateMachine(enviamentSaved.getUuid());
		enviamentTableHelper.crearRegistre(enviamentSaved);
		auditHelper.auditaEnviament(enviamentSaved, AuditService.TipusOperacio.CREATE, "NotificacioServiceWsImplV2.altaV2");
		log.debug(">> [ALTA] enviament creat");


		EnviamentReferenciaV2 enviamentReferencia = new EnviamentReferenciaV2();
		enviamentReferencia.setReferencia(enviamentSaved.getNotificaReferencia());
		String titularNif = !FISICA_SENSE_NIF.equals(titular.getInteressatTipus()) ?
				(!InteressatTipus.ADMINISTRACIO.equals(titular.getInteressatTipus()) ? titular.getNif().toUpperCase() : titular.getDir3Codi().toUpperCase())
				: null;
		enviamentReferencia.setTitularNif(titularNif);
		enviamentReferencia.setTitularNom(titular.getNom());
		enviamentReferencia.setTitularEmail(titular.getEmail());
		notificacioGuardada.addEnviament(enviamentSaved);
		return enviamentReferencia;
	}

	private List<PersonaEntity> getDestinataris(Enviament enviament) {
		List<PersonaEntity> destinataris = new ArrayList<>();
		if (enviament.getDestinataris() != null) {
			for(var persona: enviament.getDestinataris()) {
				var destinatari = personaRepository.save(PersonaEntity.getBuilderV2(
						persona.getInteressatTipus(),
						persona.getEmail(),
						persona.getLlinatge1(),
						persona.getLlinatge2(),
						persona.getNif(),
						persona.getNom(),
						persona.getTelefon(),
						persona.getRaoSocial(),
						persona.getDir3Codi()
				).incapacitat(false).build());
				destinataris.add(destinatari);
			}
		}
		return destinataris;
	}

	private PersonaEntity saveTitular(Enviament enviament) {
		return personaRepository.save(PersonaEntity.getBuilderV2(
				enviament.getTitular().getInteressatTipus(),
				enviament.getTitular().getEmail(),
				enviament.getTitular().getLlinatge1(),
				enviament.getTitular().getLlinatge2(),
				enviament.getTitular().getNif(),
				enviament.getTitular().getNom(),
				enviament.getTitular().getTelefon(),
				enviament.getTitular().getRaoSocial(),
				enviament.getTitular().getDir3Codi()
		).incapacitat(enviament.getTitular().isIncapacitat()).build());
	}

	private ServeiTipus getServeiTipus(Enviament enviament) {

		if (enviament.getServeiTipus() == null) {
			return null;
		}
		ServeiTipus serveiTipus = ServeiTipus.NORMAL;
		if (ServeiTipus.URGENT.equals(enviament.getServeiTipus())) {
			serveiTipus = ServeiTipus.URGENT;
		}
		return serveiTipus;
	}

	private EnviamentTipus getEnviamentTipus(Notificacio notificacio) {

		EnviamentTipus enviamentTipus = null;
		if (notificacio.getEnviamentTipus() != null) {
			switch (notificacio.getEnviamentTipus()) {
				case COMUNICACIO:
					enviamentTipus = EnviamentTipus.COMUNICACIO;
					break;
				case NOTIFICACIO:
					enviamentTipus = EnviamentTipus.NOTIFICACIO;
					break;
				case SIR:
					enviamentTipus = EnviamentTipus.SIR;
					break;
			}
			log.debug(">> [ALTA] enviament tipus: " + enviamentTipus);
		}
		return enviamentTipus;
	}

	private DocumentEntity getDocumentEntity(DocumentValidDto document) {
		DocumentEntity documentEntity = null;
		if(document != null && (!Strings.isNullOrEmpty(document.getCsv()) ||
				!Strings.isNullOrEmpty(document.getUuid()) ||
				!Strings.isNullOrEmpty(document.getArxiuGestdocId()))) {

			documentEntity = documentRepository.saveAndFlush(DocumentEntity.getBuilderV2(
					document.getArxiuGestdocId(),
					document.getArxiuNom(),
					document.isNormalitzat(),
					document.getUuid(),
					document.getCsv(),
					document.getMediaType(),
					document.getMida(),
					document.getOrigen(),
					document.getValidesa(),
					document.getTipoDocumental(),
					document.getModoFirma()).build());
			log.debug(">> [ALTA] document creat");
		}
		return documentEntity;
	}

//	public static final Pattern EMAIL_REGEX = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$", Pattern.CASE_INSENSITIVE);

	@Data
	@Builder
	private static class ReceptorInfo {
		String nif;
		String nom;
	}
}
