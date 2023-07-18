/**
 * 
 */
package es.caib.notib.core.service.ws;

import com.codahale.metrics.Timer;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import es.caib.notib.client.domini.*;
import es.caib.notib.core.api.dto.AccioParam;
import es.caib.notib.core.api.dto.CallbackEstatEnumDto;
import es.caib.notib.core.api.dto.DocumentDto;
import es.caib.notib.core.api.dto.FitxerDto;
import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.core.api.dto.IntegracioInfo;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.ProcSerTipusEnum;
import es.caib.notib.core.api.dto.ProgresDescarregaDto;
import es.caib.notib.core.api.dto.ServeiTipusEnumDto;
import es.caib.notib.core.api.dto.SignatureInfoDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.organisme.OrganismeDto;
import es.caib.notib.core.api.exception.NoMetadadesException;
import es.caib.notib.core.api.exception.SignatureValidationException;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.api.service.AuditService;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.api.service.JustificantService;
import es.caib.notib.core.api.ws.notificacio.NotificacioServiceWsException;
import es.caib.notib.core.api.ws.notificacio.NotificacioServiceWsV2;
import es.caib.notib.core.cacheable.OrganGestorCachable;
import es.caib.notib.core.entity.AplicacioEntity;
import es.caib.notib.core.entity.CallbackEntity;
import es.caib.notib.core.entity.DocumentEntity;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.PersonaEntity;
import es.caib.notib.core.entity.ProcSerEntity;
import es.caib.notib.core.entity.ProcSerOrganEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.helper.CacheHelper;
import es.caib.notib.core.helper.CaducitatHelper;
import es.caib.notib.core.helper.ConfigHelper;
import es.caib.notib.core.helper.EnviamentHelper;
import es.caib.notib.core.helper.EnviamentTableHelper;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.NotificacioValidatorHelper;
import es.caib.notib.core.helper.SemaforNotificacio;
import es.caib.notib.core.helper.IntegracioHelper;
import es.caib.notib.core.helper.MessageHelper;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.helper.NifHelper;
import es.caib.notib.core.helper.NotificaHelper;
import es.caib.notib.core.helper.NotificacioHelper;
import es.caib.notib.core.helper.NotificacioTableHelper;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.helper.RegistreNotificaHelper;
import es.caib.notib.core.helper.SemaforNotificacio;
import es.caib.notib.core.repository.AplicacioRepository;
import es.caib.notib.core.repository.CallbackRepository;
import es.caib.notib.core.repository.DocumentRepository;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.core.repository.PersonaRepository;
import es.caib.notib.core.repository.ProcSerOrganRepository;
import es.caib.notib.core.repository.ProcSerRepository;
import es.caib.notib.core.utils.MimeUtils;
import es.caib.notib.plugin.registre.RespostaJustificantRecepcio;
import es.caib.notib.plugin.usuari.DadesUsuari;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.time.DateUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebService;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static es.caib.notib.client.domini.InteressatTipusEnumDto.FISICA_SENSE_NIF;
import static org.apache.commons.lang3.StringUtils.isBlank;


/**
 * Implementació del servei per a l'enviament i consulta de notificacions V2 (Sense paràmetres SEU).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
@WebService(
		name = "NotificacioServiceV2",
		serviceName = "NotificacioServiceV2",
		portName = "NotificacioServiceV2Port",
		targetNamespace = "http://www.caib.es/notib/ws/notificacio",
		endpointInterface = "es.caib.notib.core.api.service.ws.NotificacioServiceV2")
public class NotificacioServiceWsImplV2 implements NotificacioServiceWsV2 {

	private final static Pattern UUID_REGEX_PATTERN = Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");

	public static boolean isValidUUID(String str) {
		if (str == null) {
			return false;
		}
		return UUID_REGEX_PATTERN.matcher(str).matches();
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
	private AplicacioRepository aplicacioRepository;
	@Autowired
	private OrganGestorRepository organGestorRepository;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private NotificacioEventRepository notificacioEventRepository;
	@Autowired
	private CallbackRepository callbackRepository;
	@Autowired
	private NotificaHelper notificaHelper;
	@Autowired
	private EnviamentHelper enviamentHelper;
	@Autowired
	private EnviamentTableHelper enviamentTableHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private GrupService grupService;
	@Autowired
	private RegistreNotificaHelper registreNotificaHelper;
	@Autowired
	private IntegracioHelper integracioHelper;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private MetricsHelper metricsHelper;
	@Autowired
	private NotificacioTableHelper notificacioTableHelper;
	@Autowired
	private OrganGestorCachable organGestorCachable;
	@Autowired
	private NotificacioHelper notificacioHelper;
	@Autowired
	private JustificantService justificantService;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private MessageHelper messageHelper;
	@Autowired
	private NotificacioValidatorHelper notificacioValidatorHelper;


	private static final String COMUNICACIOAMBADMINISTRACIO = "comunicacioAmbAdministracio";

	@Transactional
	@Override
	public RespostaAlta alta(NotificacioV2 notificacio) throws NotificacioServiceWsException {

		RespostaAltaV2 resposta = altaV2(notificacio);
		return RespostaAlta.builder()
				.identificador(resposta.getIdentificador())
				.estat(resposta.getEstat())
				.referencies(resposta.getReferenciesAsV1())
				.error(resposta.isError())
				.errorDescripcio(resposta.getErrorDescripcio())
				.build();
	}

	@Transactional
	@Override
	public RespostaAltaV2 altaV2(NotificacioV2 notificacio) throws NotificacioServiceWsException {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("[ALTA] Alta de notificació: " + notificacio.toString());

			RespostaAltaV2 resposta;
			ProcSerEntity procediment = null;
			OrganGestorEntity organGestor = null;
			ProcSerOrganEntity procedimentOrgan = null;

			// Generar informació per al monitor d'integracions
			IntegracioInfo info = generateInfoAlta(notificacio);

			// Obtenir dades bàsiques per a la notificació
			String emisorDir3Codi = notificacio.getEmisorDir3Codi();
			logger.debug(">> [ALTA] emisorDir3Codi: " + emisorDir3Codi);
			EntitatEntity entitat = entitatRepository.findByDir3Codi(emisorDir3Codi);
			if (entitat != null) {
				ConfigHelper.setEntitatCodi(entitat.getCodi());
				info.setCodiEntitat(entitat.getCodi());
			}
			logger.debug(">> [ALTA] entitat: " + (entitat == null ? "null": (entitat.getCodi() + " - " + entitat.getNom())));

			String usuariCodi = SecurityContextHolder.getContext().getAuthentication().getName();

			logger.debug(">> [ALTA] usuariCodi: " + usuariCodi);

			AplicacioEntity aplicacio = null;
			if (entitat != null && usuariCodi != null) {
				aplicacio = aplicacioRepository.findByEntitatIdAndUsuariCodi(entitat.getId(), usuariCodi);
			}
			logger.debug(">> [ALTA] aplicacio: " + (aplicacio == null ? "null" : aplicacio.getUsuariCodi()));

			resposta = validarNotificacio(notificacio, emisorDir3Codi, entitat, aplicacio);
			logger.debug(">> [ALTA] validacio: [error=" + resposta.isError() + ", estat=" + resposta.getEstat() + ", descripcio=" + resposta.getErrorDescripcio() + "]");

			if (resposta.isError()) {
				integracioHelper.addAccioError(info, resposta.getErrorDescripcio());
				return resposta;
			}

			boolean comunicacioAmbAdministracio = COMUNICACIOAMBADMINISTRACIO.equals(resposta.getErrorDescripcio());
			try {
				// Obtenir tipus d'enviament
				NotificaEnviamentTipusEnumDto enviamentTipus = getEnviamentTipus(notificacio);

				// Obtenir dades depenents de procediment (Procediment NO obligatori per a comunicacions a administracions)
				if (notificacio.getProcedimentCodi() != null) {
					procediment = procSerRepository.findByCodiAndEntitat(notificacio.getProcedimentCodi(), entitat);

					if (procediment != null) {

						if (!procediment.isActiu()) {
							String errorDescripcio = messageHelper.getMessage("error.validacio.alta.notificacio.procediment.inactiu");
							integracioHelper.addAccioError(info, errorDescripcio);
							return setRespostaError(errorDescripcio);
						}

						if (ProcSerTipusEnum.SERVEI.equals(procediment.getTipus()) && NotificaEnviamentTipusEnumDto.NOTIFICACIO.equals(enviamentTipus)) {
							String errorDescripcio = messageHelper.getMessage("error.validacio.alta.notificacio.amb.servei.nomes.comunicacions");
							integracioHelper.addAccioError(info, errorDescripcio);
							return setRespostaError(errorDescripcio);
						}
						logger.debug(">> [ALTA] procediment: " + procediment.getNom());

						if (!procediment.isEntregaCieActivaAlgunNivell()) {
							for (Enviament enviament : notificacio.getEnviaments()) {
								if (enviament.isEntregaPostalActiva()) {
									String errorDescripcio = messageHelper.getMessage("error.validacio.alta.enviament.entrega.postal.invalida");
									integracioHelper.addAccioError(info, errorDescripcio);
									return setRespostaError(errorDescripcio);
								}
							}
						}
						// Grups de notificació
						String errorDescripcio = getGrupNotificacio(notificacio, entitat, procediment);
						if (errorDescripcio != null) {
							integracioHelper.addAccioError(info, errorDescripcio);
							return setRespostaError(errorDescripcio);
						}

						//Comprovar si no hi ha una caducitat posar una per defecte (dia acutal + dies caducitat procediment)
						// La caducitat únicament és necessària per a notificacions. Per tant tindrà procedimetns
						if (notificacio.getCaducitat() == null) {
							if (notificacio.getCaducitatDiesNaturals() != null) {
								notificacio.setCaducitat(CaducitatHelper.sumarDiesNaturals(new Date(), notificacio.getCaducitatDiesNaturals()));
							} else {
								notificacio.setCaducitat(CaducitatHelper.sumarDiesLaborals(new Date(), procediment.getCaducitat()));
							}
						}

						// Organ gestor
						if (!procediment.isComu()) { // || (procediment.isComu() && notificacio.getOrganGestor() == null)) { --> Tot procediment comú ha de informa un òrgan gestor
							organGestor = procediment.getOrganGestor();

							if (notificacio.getOrganGestor() != null && !notificacio.getOrganGestor().isEmpty() &&
									organGestor != null && !notificacio.getOrganGestor().equals(organGestor.getCodi())) {
								logger.debug(">> [ALTA] Organ gestor no es correspon amb el del procediment");
								errorDescripcio = messageHelper.getMessage("error.validacio.organ.gestor.no.correspon.organ.procediment");
								integracioHelper.addAccioError(info, errorDescripcio);
								return setRespostaError(errorDescripcio);
							}
						}
					} else {
						logger.debug(">> [ALTA] Sense procediment");
						String errorDescripcio = messageHelper.getMessage("error.validacio.procser.amb.codi.no.trobat");
						integracioHelper.addAccioError(info, errorDescripcio);
						return setRespostaError(errorDescripcio);
					}
				}

				// Òrgan gestor
				if (organGestor == null) {
					organGestor = organGestorRepository.findByCodi(notificacio.getOrganGestor());
					if (organGestor == null) {
//						Map<String, OrganismeDto> organigramaEntitat = organGestorCachable.findOrganigramaByEntitat(entitat.getDir3Codi());
//						if (!organigramaEntitat.containsKey(notificacio.getOrganGestor())) {
						logger.debug(">> [ALTA] Organ gestor desconegut");
						String errorDescripcio = messageHelper.getMessage("error.validacio.organ.gestor.no.organ.entitat");
						integracioHelper.addAccioError(info, errorDescripcio);
						return setRespostaError(errorDescripcio);
//						}
//						organGestor = organGestorHelper.crearOrganGestor(entitat, notificacio.getOrganGestor());
					}
				}
				if (procediment != null && procediment.isComu() && organGestor != null) {
					procedimentOrgan = procedimentOrganRepository.findByProcSerIdAndOrganGestorId(procediment.getId(), organGestor.getId());
				}

				// Dades no depenents de procediment

				// DOCUMENT
				// Comprovam si el document és vàlid
				DocumentDto document = null;
				DocumentEntity documentEntity = null;
				DocumentDto document2 = null;
				DocumentEntity document2Entity = null;
				DocumentDto document3 = null;
				DocumentEntity document3Entity = null;
				DocumentDto document4 = null;
				DocumentEntity document4Entity = null;
				DocumentDto document5 = null;
				DocumentEntity document5Entity = null;

				if (comunicacioAmbAdministracio) {
					int numDoc = 1;
					Long midaTotal = 0L;
					try {
						document = comprovaDocument(notificacio.getDocument()); //, !comunicacioAmbAdministracio);
						if (!MimeUtils.isMimeValidSIR(document.getMediaType())) {
							return setRespostaError(messageHelper.getMessage("error.validacio.document.format.invalid.comunicacions.administracio"));
						}
						documentEntity = getDocument(notificacio.getDocument(), document);
						midaTotal = document.getMida();
						numDoc++;
						if (notificacio.getDocument2() != null) {
							document2 = comprovaDocument(notificacio.getDocument2());
							document2Entity = getDocument(notificacio.getDocument2(), document2);
							midaTotal += document2.getMida();
							if (document2.getMida() > getMaxSizeFile()) {
								return setRespostaError(
										messageHelper.getMessage("error.validacio.document.longitud.max.a")
												+ getMaxSizeFile() / (1024*1024)
												+ messageHelper.getMessage("error.validacio.document.longitud.max.b")
												+ " " + messageHelper.getMessage("errors.validacio.document") + "2");
							}
						}
						numDoc++;
						if (notificacio.getDocument3() != null) {
							document3 = comprovaDocument(notificacio.getDocument3());
							document3Entity = getDocument(notificacio.getDocument3(), document3);
							midaTotal += document3.getMida();
							if (document3.getMida() > getMaxSizeFile()) {
								return setRespostaError(
										messageHelper.getMessage("error.validacio.document.longitud.max.a")
												+ getMaxSizeFile() / (1024*1024)
												+ messageHelper.getMessage("error.validacio.document.longitud.max.b")
												+ " " + messageHelper.getMessage("errors.validacio.document") + "3");
							}
						}
						numDoc++;
						if (notificacio.getDocument4() != null) {
							document4 = comprovaDocument(notificacio.getDocument4());
							document4Entity = getDocument(notificacio.getDocument4(), document4);
							midaTotal += document4.getMida();
							if (document4.getMida() > getMaxSizeFile()) {
								return setRespostaError(
										messageHelper.getMessage("error.validacio.document.longitud.max.a")
												+ getMaxSizeFile() / (1024*1024)
												+ messageHelper.getMessage("error.validacio.document.longitud.max.b")
												+ " " + messageHelper.getMessage("errors.validacio.document") + "4");
							}
						}
						numDoc++;
						if (notificacio.getDocument5() != null) {
							document5 = comprovaDocument(notificacio.getDocument5());
							document5Entity = getDocument(notificacio.getDocument5(), document5);
							midaTotal += document5.getMida();
							if (document5.getMida() > getMaxSizeFile()) {
								return setRespostaError(
										messageHelper.getMessage("error.validacio.document.longitud.max.a")
												+ getMaxSizeFile() / (1024*1024)
												+ messageHelper.getMessage("error.validacio.document.longitud.max.b")
												+ " " + messageHelper.getMessage("errors.validacio.document") + "5");
							}
						}
					} catch (NoMetadadesException me) {
						logger.error("Error al obtenir les metadades del document " + numDoc, me);
						String errorDescripcio = messageHelper.getMessage("error.obtenint.metadades.document") + numDoc + ": " + me.getMessage();
						integracioHelper.addAccioError(info, errorDescripcio, me);
						return setRespostaError(errorDescripcio);
					} catch (SignatureValidationException sve) {
						logger.error("Error al validar la firma del document: " + sve.getNom(), sve);
						String errorDescripcio = messageHelper.getMessage("error.validant.firma.document") + sve.getNom() + ": " + sve.getMessage();
						integracioHelper.addAccioError(info, errorDescripcio, sve);
						return setRespostaError(errorDescripcio);
					} catch (Exception e) {
						logger.error("Error al obtenir el document " + numDoc, e);
						String errorDescripcio = messageHelper.getMessage("error.obtenint.document") + numDoc + ": " + e.getMessage();
						integracioHelper.addAccioError(info, errorDescripcio, e);
						return setRespostaError(errorDescripcio);
					}
					// Mida dels documents
					if (midaTotal > getMaxTotalSizeFile()) {
						return setRespostaError(
								messageHelper.getMessage("error.mida.conjunt.documents.supera.max")
										+ getMaxTotalSizeFile() / (1024*1024)
										+ messageHelper.getMessage("error.validacio.document.longitud.max.b"));
					}

				} else {
					try {

						document = comprovaDocument(notificacio.getDocument()); //, !comunicacioAmbAdministracio);
						documentEntity = getDocument(notificacio.getDocument(), document);
					} catch (SignatureValidationException sve) {
						logger.error("Error al validar la firma del document: " + sve.getNom(), sve);
						String errorDescripcio = messageHelper.getMessage("error.validant.firma.document") + sve.getNom() + ": " + sve.getMessage();
						integracioHelper.addAccioError(info, errorDescripcio, sve);
						return setRespostaError(errorDescripcio);
					} catch (Exception e) {
						logger.error("Error al obtenir el document", e);
						String errorDescripcio = messageHelper.getMessage("error.obtenint.document") + " a notificar: " + e.getMessage();
						integracioHelper.addAccioError(info, errorDescripcio);
						return setRespostaError(errorDescripcio);
					}
//					byte[] base64Decoded = Base64.decodeBase64(notificacio.getDocument().getContingutBase64());
					if (document.getMida() > getMaxSizeFile()) {
						return setRespostaError(
								messageHelper.getMessage("error.validacio.document.longitud.max.a")
										+ getMaxSizeFile() / (1024*1024)
										+ messageHelper.getMessage("error.validacio.document.longitud.max.b"));
					}
				}

				NotificacioEntity notificacioEntity = NotificacioEntity.
						getBuilderV2(
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

				NotificacioEntity notificacioGuardada = notificacioRepository.saveAndFlush(notificacioEntity);
				notificacioTableHelper.crearRegistre(notificacioEntity);
				notificacioHelper.auditaNotificacio(notificacioEntity, AuditService.TipusOperacio.CREATE, "NotificacioServiceWsImplV2.altaV2");

				logger.debug(">> [ALTA] notificacio guardada");

				// Enviaments
				List<EnviamentReferenciaV2> referencies = new ArrayList<>();
				for (Enviament enviament: notificacio.getEnviaments()) {

					// Comprovat titular
					if (enviament.getTitular() == null) {
						String errorDescripcio = messageHelper.getMessage("error.validacio.titular.enviament.no.null");
						integracioHelper.addAccioError(info, errorDescripcio);
						logger.debug(">> [ALTA] Titular null");
						return setRespostaError(errorDescripcio);
					}

					EnviamentReferenciaV2 ref = saveEnviament(entitat, notificacioGuardada, enviament);
					referencies.add(ref);
				}
				logger.debug(">> [ALTA] enviaments creats");

				notificacioGuardada = notificacioRepository.saveAndFlush(notificacioGuardada);

				if (NotificacioComunicacioTipusEnumDto.SINCRON.equals(pluginHelper.getNotibTipusComunicacioDefecte())) {
					logger.info(" [ALTA] Enviament SINCRON notificació [Id: " + notificacioGuardada.getId() + ", Estat: " + notificacioGuardada.getEstat() + "]");
					synchronized(SemaforNotificacio.agafar(notificacioGuardada.getId())) {
						boolean notificar = registreNotificaHelper.realitzarProcesRegistrar(notificacioGuardada);
						if (notificar) {
							notificaHelper.notificacioEnviar(notificacioGuardada.getId());
						}
					}
					SemaforNotificacio.alliberar(notificacioGuardada.getId());
				}
//				else {
//					inicialitzaCallbacks(notificacioGuardada);
//				}

				return generaResposta(info, notificacioGuardada, referencies);
			} catch (Exception ex) {
				logger.error("Error creant notificació", ex);
				integracioHelper.addAccioError(info, "Error creant la notificació", ex);
				throw new RuntimeException("[NOTIFICACIO/COMUNICACIO] Hi ha hagut un error creant la " + notificacio.getEnviamentTipus().name() + ": " + ex.getMessage(), ex);
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public boolean donarPermisConsulta(PermisConsulta permisConsulta) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			String json = "S'ha produït un error al intentar llegir la informació dels permisos";
			ObjectMapper mapper  = new ObjectMapper();
			try {
				json = mapper.writeValueAsString(permisConsulta);
			} catch (Exception e) { }

			IntegracioInfo info = new IntegracioInfo(
					IntegracioHelper.INTCODI_CLIENT,
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
				ProcSerEntity procediment = procSerRepository.findByEntitatAndCodiProcediment(entitat, permisConsulta.getProcedimentCodi());

				List<PermisDto> permisos = permisosHelper.findPermisos(procediment.getId(), ProcedimentEntity.class);
				if (permisos == null || permisos.isEmpty()) {
					PermisDto permisNou = new PermisDto();
					permisos = new ArrayList<PermisDto>();

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
				for (PermisDto permisDto : permisos) {
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


	@Override
	@Transactional(readOnly = true)
	public RespostaConsultaEstatNotificacio consultaEstatNotificacio(String identificador) {
		RespostaConsultaEstatNotificacioV2 resposta = consultaEstatNotificacioV2(identificador);
		return RespostaConsultaEstatNotificacio.builder()
				.estat(resposta.getEstat())
				.error(resposta.isError())
				.errorData(resposta.getErrorData())
				.errorDescripcio(resposta.getErrorDescripcio())
				.build();
	}

	@Override
	@Transactional(readOnly = true)
	public RespostaConsultaEstatNotificacioV2 consultaEstatNotificacioV2(String identificador) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			IntegracioInfo info = new IntegracioInfo(
					IntegracioHelper.INTCODI_CLIENT,
					"Consulta de l'estat d'una notificació",
					IntegracioAccioTipusEnumDto.RECEPCIO,
					new AccioParam("Identificador xifrat de la notificacio", identificador));

//			Long notificacioId;
			RespostaConsultaEstatNotificacioV2 resposta = new RespostaConsultaEstatNotificacioV2();
			resposta.setIdentificador(identificador);

			try {
				NotificacioEntity notificacio = null;

				if (isValidUUID(identificador)) {
					notificacio = notificacioRepository.findByReferencia(identificador);
				} else {
					try {
						Long notificacioId = notificaHelper.desxifrarId(identificador);
						info.getParams().add(new AccioParam("Identificador desxifrat de la notificació", String.valueOf(notificacioId)));
						notificacio = notificacioRepository.findById(notificacioId);
					} catch (GeneralSecurityException ex) {
						resposta.setError(true);
						resposta.setErrorData(new Date());
						resposta.setErrorDescripcio("No s'ha pogut desxifrar l'identificador de la notificació " + identificador);
						integracioHelper.addAplicacioAccioParam(info, null);
						integracioHelper.addAccioError(info, "Error al desxifrar l'identificador de la notificació a consultar", ex);
						return resposta;
					}
				}

				if (notificacio == null) {
					resposta.setError(true);
					resposta.setErrorData(new Date());
					resposta.setErrorDescripcio("Error: No s'ha trobat cap notificació amb l'identificador " + identificador);
					integracioHelper.addAplicacioAccioParam(info, null);
					integracioHelper.addAccioError(info, "No existeix cap notificació amb l'identificador especificat");
					return resposta;
				}

				ConfigHelper.setEntitatCodi(notificacio.getEntitat().getCodi());
				info.setCodiEntitat(notificacio.getEntitat().getCodi());
				integracioHelper.addAplicacioAccioParam(info, notificacio.getEntitat().getId());
				switch (notificacio.getEstat()) {
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
				}

				resposta.setTipus(notificacio.getEnviamentTipus().name());
				resposta.setEmisorDir3(notificacio.getEmisorDir3Codi());
				if (notificacio.getProcediment() != null)
					resposta.setProcediment(Procediment.builder()
							.codiSia(notificacio.getProcediment().getCodi())
							.nom(notificacio.getProcediment().getNom())
							.build());
				resposta.setConcepte(notificacio.getConcepte());
				if (notificacio.getOrganGestor() != null)
					resposta.setOrganGestorDir3(notificacio.getOrganGestor().getCodi());
				resposta.setNumExpedient(notificacio.getNumExpedient());

				resposta.setDataCreada(notificacio.getCreatedDate().toDate());
				resposta.setDataEnviada(notificacio.getNotificaEnviamentNotificaData());
				resposta.setDataFinalitzada(notificacio.getEstatDate());
				resposta.setDataProcessada(notificacio.getEstatProcessatDate());

				NotificacioEventEntity errorEvent = notificacioHelper.getNotificaErrorEvent(notificacio);
				if (errorEvent != null) {
					NotificacioEstatEnumDto estat = notificacio.getEstat();
					boolean isError = !NotificacioEstatEnumDto.FINALITZADA.equals(estat) && !NotificacioEstatEnumDto.PROCESSADA.equals(estat) && !Strings.isNullOrEmpty(errorEvent.getErrorDescripcio());
					resposta.setError(isError);
					resposta.setErrorData(errorEvent.getData());
					resposta.setErrorDescripcio(errorEvent.getErrorDescripcio());
					//				// Si l'error és de reintents de consulta o SIR, hem d'obtenir el missatge d'error de l'event que ha provocat la fallada
					//				if (errorEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_ERROR) ||
					//						errorEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_SIR_ERROR)) {
					//					List<NotificacioEventEntity> events = new ArrayList<NotificacioEventEntity>(notificacio.getEvents());
					//					Collections.sort(events, new Comparator<NotificacioEventEntity>() {
					//						@Override
					//						public int compare(NotificacioEventEntity o1, NotificacioEventEntity o2) {
					//							return o1.getId().compareTo(o2.getId());
					//						}
					//					});
					//					int index = events.indexOf(errorEvent);
					//					if (index > 0) {
					//						NotificacioEventEntity eventErrada = events.get(index - 1);
					//						resposta.setErrorDescripcio(StringUtils.abbreviate(resposta.getErrorDescripcio() + " - " + eventErrada.getErrorDescripcio(), 2048));
					//					}
					//				}
				}
			} catch (Exception ex) {
				integracioHelper.addAccioError(info, "Error al obtenir la informació de l'estat de la notificació", ex);
				throw new RuntimeException(
						"[NOTIFICACIO/COMUNICACIO] Hi ha hagut un error consultant la notificació: " + ex.getMessage(),
						ex);
			}
			integracioHelper.addAccioOk(info);
			return resposta;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public RespostaConsultaEstatEnviament consultaEstatEnviament(String referencia) {
		RespostaConsultaEstatEnviamentV2 resposta = consultaEstatEnviamentV2(referencia);
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
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			IntegracioInfo info = new IntegracioInfo(IntegracioHelper.INTCODI_CLIENT,"Consulta de l'estat d'un enviament", IntegracioAccioTipusEnumDto.RECEPCIO);
			NotificacioEnviamentEntity enviament = null;
			Long enviamentId = null;
			try { //TODO FER IGUAL QUE LA NOTIFICACIO
				enviamentId = notificaHelper.desxifrarId(referencia);
				enviament = notificacioEnviamentRepository.findById(enviamentId);
			} catch (Exception e) {
				logger.error("Error consultatEStatEnviamentV2 -> Referencia: " + referencia + " enviament id: " + enviamentId);
			}
			if (enviament != null) {
				info.getParams().add(new AccioParam("Identificador xifrat de l'enviament", referencia));
				info.getParams().add(new AccioParam("Identificador desxifrat de l'enviament", String.valueOf(enviament.getId())));
			} else  {
				enviament = notificacioEnviamentRepository.findByNotificaReferencia(referencia);
				info.getParams().add(new AccioParam("Referència de l'enviament", referencia));
			}

			logger.debug("Consultant estat enviament amb referencia: " + referencia);
			RespostaConsultaEstatEnviamentV2 resposta = RespostaConsultaEstatEnviamentV2.builder().referencia(referencia).build();
			try {
				if (enviament == null) {
					resposta.setError(true);
					resposta.setErrorData(new Date());
					resposta.setErrorDescripcio("Error: No s'ha trobat cap enviament amb la referencia " + referencia);
					integracioHelper.addAplicacioAccioParam(info, null);
					integracioHelper.addAccioError(info, "No existeix cap enviament amb l'identificador especificat");
					return resposta;
				}
				if (enviament.getNotificacio() != null && enviament.getNotificacio().getEntitat() != null) {
					info.setCodiEntitat(enviament.getNotificacio().getEntitat().getCodi());
				}
				ConfigHelper.setEntitatCodi(enviament.getNotificacio().getEntitat().getCodi());
				integracioHelper.addAplicacioAccioParam(info, enviament.getNotificacio().getEntitat().getId());
				//Es canosulta l'estat periòdicament, no es necessita realitzar una consulta actica a Notifica
				// Si Notib no utilitza el servei Adviser de @Notifica, i ja ha estat enviat a @Notifica
				// serà necessari consultar l'estat de la notificació a Notifica
				if (!notificaHelper.isAdviserActiu() && !enviament.isNotificaEstatFinal()
						&& !enviament.getNotificaEstat().equals(EnviamentEstat.NOTIB_PENDENT)) {
					logger.debug("Consultat estat de l'enviament amb referencia " + referencia + " a Notifica.");
					enviament = notificaHelper.enviamentRefrescarEstat(enviament.getId());
				}
				resposta.setIdentificador(enviament.getNotificacio().getReferencia());
				resposta.setNotificaIndentificador(enviament.getNotificaIdentificador());
				resposta.setEstat(toEnviamentEstat(enviament.getNotificaEstat()));
				resposta.setEstatData(getEstatDate(enviament));
				resposta.setEstatDescripcio(enviament.getNotificaEstatDescripcio());
				resposta.setDehNif(enviament.getDehNif());
				resposta.setDehObligat(enviament.getDehObligat() != null ? enviament.getDehObligat() : false);
				resposta.setEntragaPostalActiva(enviament.getEntregaPostal() != null);
				if (enviament.getEntregaPostal() != null) {
					resposta.setAdressaPostal(enviament.getEntregaPostal().toString());
				}
				boolean esSir = NotificaEnviamentTipusEnumDto.COMUNICACIO.equals(enviament.getNotificacio().getEnviamentTipus()) &&
						InteressatTipusEnumDto.ADMINISTRACIO.equals(enviament.getTitular().getInteressatTipus());
				resposta.setEnviamentSir(esSir);

				// INTERESSAT
				Persona interessat = Persona.builder()
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
					List<Persona> representants = new ArrayList<>();
					for (PersonaEntity destinatari: enviament.getDestinataris()) {
						Persona representant = Persona.builder()
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
							.estat(toEnviamentEstat(enviament.getNotificaEstat()))
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
					logger.debug("Guardant certificació enviament amb referencia: " + referencia);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					pluginHelper.gestioDocumentalGet(
							enviament.getNotificaCertificacioArxiuId(),
							PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS,
							baos);
					String certificacioBase64 = Base64.encodeBase64String(baos.toByteArray());

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
					logger.debug("Certificació de l'enviament amb referencia: " + referencia + " s'ha obtingut correctament.");
				}

				if (enviament.getNotificacioErrorEvent() != null) {
					NotificacioEstatEnumDto estat = enviament.getNotificacio().getEstat();
					NotificacioEventEntity errorEvent = enviament.getNotificacioErrorEvent();
					boolean isError = !NotificacioEstatEnumDto.FINALITZADA.equals(estat) && !NotificacioEstatEnumDto.PROCESSADA.equals(estat) && !Strings.isNullOrEmpty(errorEvent.getErrorDescripcio());
					resposta.setError(isError);
					resposta.setErrorData(errorEvent.getData());
					resposta.setErrorDescripcio(errorEvent.getErrorDescripcio());
					logger.debug("Notifica error de l'enviament amb referencia: " + referencia + ": " + enviament.isNotificaError());
				}
			} catch (Exception ex) {
				logger.debug("Error consultar estat enviament amb referencia: " + referencia, ex);
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
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			String json = "S'ha produït un error al intentar llegir la informació de les dades de la consulta";
			ObjectMapper mapper  = new ObjectMapper();
			try {
				json = mapper.writeValueAsString(dadesConsulta);
			} catch (Exception e) { }

			IntegracioInfo info = new IntegracioInfo(
					IntegracioHelper.INTCODI_CLIENT,
					"Consulta de les dades de registre",
					IntegracioAccioTipusEnumDto.RECEPCIO,
					new AccioParam("Dades de la consulta", json));

			RespostaConsultaDadesRegistreV2 resposta = new RespostaConsultaDadesRegistreV2();
			String numeroRegistreFormatat = null;
			String codiDir3Entitat = null;

			if (dadesConsulta.getIdentificador() != null) {
				logger.debug("Consultant les dades de registre de la notificació amb identificador: " + dadesConsulta.getIdentificador());
				int numeroRegistre = 0;
				NotificacioEntity notificacio;

				if (isValidUUID(dadesConsulta.getIdentificador())) {
					notificacio = notificacioRepository.findByReferencia(dadesConsulta.getIdentificador());
				} else {
					try {
						Long notificacioId = notificaHelper.desxifrarId(dadesConsulta.getIdentificador());
						info.getParams().add(new AccioParam("Identificador desxifrat de la notificació", String.valueOf(notificacioId)));
						notificacio = notificacioRepository.findById(notificacioId);
					} catch (GeneralSecurityException ex) {
						resposta.setError(true);
						resposta.setErrorData(new Date());
						resposta.setErrorDescripcio("No s'ha pogut desxifrar l'identificador de la notificació " + dadesConsulta.getIdentificador());
						integracioHelper.addAplicacioAccioParam(info, null);
						integracioHelper.addAccioError(info, "Error al desxifrar l'identificador de la notificació a consultar", ex);
						return resposta;
					}
				}

				if (notificacio == null) {
					resposta.setError(true);
					resposta.setErrorData(new Date());
					resposta.setErrorDescripcio("Error: No s'ha trobat cap notificació amb l'identificador " + dadesConsulta.getIdentificador());
					integracioHelper.addAplicacioAccioParam(info, null);
					integracioHelper.addAccioError(info, "No existeix cap notificació amb l'identificador especificat");
					return resposta;
				}
				ConfigHelper.setEntitatCodi(notificacio.getEntitat().getCodi());
				integracioHelper.addAplicacioAccioParam(info, notificacio.getEntitat().getId());
				info.setCodiEntitat(notificacio.getEntitat().getCodi());
				//Dades registre i consutla justificant
				numeroRegistreFormatat = notificacio.getRegistreNumeroFormatat();
				codiDir3Entitat = notificacio.getEmisorDir3Codi();
				if (numeroRegistreFormatat == null) {
					resposta.setError(true);
					resposta.setErrorData(new Date());
					resposta.setErrorDescripcio("Error: No s'ha trobat cap registre relacionat amb la notificació: " + dadesConsulta.getIdentificador());
					integracioHelper.addAccioError(info, "No hi ha cap registre associat a la notificació");
					return resposta;
				}

				resposta.setDataRegistre(notificacio.getRegistreData());
				resposta.setNumRegistre(notificacio.getRegistreNumero());
				resposta.setNumRegistreFormatat(numeroRegistreFormatat);
				resposta.setOficina(notificacio.getRegistreOficinaNom());
				resposta.setLlibre(notificacio.getRegistreLlibreNom());
				// SIR
				NotificacioEnviamentEntity enviament = null;
				if (!notificacio.getEnviaments().isEmpty()) {
					enviament = notificacio.getEnviaments().iterator().next();
				}
				boolean esSir = NotificaEnviamentTipusEnumDto.COMUNICACIO.equals(notificacio.getEnviamentTipus()) && enviament != null &&
						InteressatTipusEnumDto.ADMINISTRACIO.equals(enviament.getTitular().getInteressatTipus());
				resposta.setEnviamentSir(esSir);
				if (esSir && notificacio.getEnviaments().size() == 1) {
					resposta.setDataRecepcioSir(enviament.getSirRecepcioData());
					resposta.setDataRegistreDestiSir(enviament.getSirRegDestiData());
				}
			} else if (dadesConsulta.getReferencia() != null) {
				logger.debug("Consultant les dades de registre de l'enviament amb referència: " + dadesConsulta.getReferencia());

				NotificacioEnviamentEntity enviament = null;
				try {
					Long enviamentId = notificaHelper.desxifrarId(dadesConsulta.getReferencia());
					enviament = notificacioEnviamentRepository.findById(enviamentId);
					info.getParams().add(new AccioParam("Identificador desxifrat de l'enviament", String.valueOf(enviamentId)));
				} catch (Exception e) { }
				if (enviament == null)
					enviament = notificacioEnviamentRepository.findByNotificaReferencia(dadesConsulta.getReferencia());

				if (enviament == null) {
					resposta.setError(true);
					resposta.setErrorData(new Date());
					resposta.setErrorDescripcio("Error: No s'ha trobat cap enviament amb la referència" + dadesConsulta.getReferencia());
					integracioHelper.addAplicacioAccioParam(info, null);
					integracioHelper.addAccioError(info, "No existeix cap enviament amb la referència especificada");
					return resposta;
				}

				//Dades registre i consutla justificant
				numeroRegistreFormatat = enviament.getRegistreNumeroFormatat();
				codiDir3Entitat = enviament.getNotificacio().getEmisorDir3Codi();
				info.setCodiEntitat(enviament.getNotificacio().getEntitat().getCodi());
				integracioHelper.addAplicacioAccioParam(info, enviament.getNotificacio().getEntitat().getId());
				if (numeroRegistreFormatat == null) {
					resposta.setError(true);
					resposta.setErrorData(new Date());
					resposta.setErrorDescripcio("Error: No s'ha trobat cap registre relacionat amb l'enviament: " + enviament.getId());
					integracioHelper.addAccioError(info, "No hi ha cap registre associat a l'enviament");
					return resposta;
				}

				resposta.setDataRegistre(enviament.getRegistreData());
				resposta.setNumRegistreFormatat(numeroRegistreFormatat);
				resposta.setOficina(enviament.getNotificacio().getRegistreOficinaNom());
				resposta.setLlibre(enviament.getNotificacio().getRegistreLlibreNom());
				// SIR
				boolean esSir = NotificaEnviamentTipusEnumDto.COMUNICACIO.equals(enviament.getNotificacio().getEnviamentTipus()) &&
						InteressatTipusEnumDto.ADMINISTRACIO.equals(enviament.getTitular().getInteressatTipus());
				resposta.setEnviamentSir(esSir);
				if (esSir) {
					resposta.setDataRecepcioSir(enviament.getSirRecepcioData());
					resposta.setDataRegistreDestiSir(enviament.getSirRegDestiData());
				}
			}
			if (dadesConsulta.isAmbJustificant()) {
				RespostaJustificantRecepcio justificant = pluginHelper.obtenirJustificant(codiDir3Entitat, numeroRegistreFormatat);
				integracioHelper.addAplicacioAccioParam(info, null);
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

	@Override
	@Transactional(readOnly = true)
	public RespostaConsultaJustificantEnviament consultaJustificantEnviament(
			String identificador) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			IntegracioInfo info = new IntegracioInfo(
					IntegracioHelper.INTCODI_CLIENT,
					"Consulta de la justificació d'una notificació",
					IntegracioAccioTipusEnumDto.RECEPCIO,
					new AccioParam("Identificador xifrat de la notificacio", identificador));

			NotificacioEntity notificacio = null;
			RespostaConsultaJustificantEnviament resposta = new RespostaConsultaJustificantEnviament();

			try {
				if (isValidUUID(identificador)) {
					notificacio = notificacioRepository.findByReferencia(identificador);
				} else {
					try {
						Long notificacioId = notificaHelper.desxifrarId(identificador);
						info.getParams().add(new AccioParam("Identificador desxifrat de la notificació", String.valueOf(notificacioId)));
						notificacio = notificacioRepository.findById(notificacioId);
					} catch (GeneralSecurityException ex) {
						resposta.setError(true);
						resposta.setErrorData(new Date());
						resposta.setErrorDescripcio("No s'ha pogut desxifrar l'identificador de la notificació " + identificador);
						integracioHelper.addAplicacioAccioParam(info, null);
						integracioHelper.addAccioError(info, "Error al desxifrar l'identificador de la notificació a consultar", ex);
						return resposta;
					}
				}
				if (notificacio == null) {
					resposta.setError(true);
					resposta.setErrorData(new Date());
					resposta.setErrorDescripcio("Error: No s'ha trobat cap notificació amb l'identificador " + identificador);
					integracioHelper.addAplicacioAccioParam(info, null);
					integracioHelper.addAccioError(info, "No existeix cap notificació amb l'identificador especificat");
					return resposta;
				}
			} catch (Exception ex) {
				integracioHelper.addAplicacioAccioParam(info, null);
				integracioHelper.addAccioError(info, "Error al obtenir la informació de la notificació", ex);
				throw new RuntimeException(
						"[NOTIFICACIO/COMUNICACIO] Hi ha hagut un error consultant la notificació: " + ex.getMessage(),
						ex);
			}
			ConfigHelper.setEntitatCodi(notificacio.getEntitat().getCodi());
			info.setCodiEntitat(notificacio.getEntitat().getCodi());
			integracioHelper.addAplicacioAccioParam(info, notificacio.getEntitat().getId());
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
						.contingut(Base64.encodeBase64(justificantDto.getContingut())).build());
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


	private RespostaAltaV2 generaResposta(IntegracioInfo info, NotificacioEntity notificacioGuardada, List<EnviamentReferenciaV2> referencies) {

		RespostaAltaV2 resposta = new RespostaAltaV2();
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
		NotificacioEventEntity errorEvent = notificacioHelper.getNotificaErrorEvent(notificacioGuardada);
		if (errorEvent != null) {
//			logger.debug(">> [ALTA] Event d'error de Notifica!: " + errorEvent.getErrorDescripcio());
			info.setCodiEntitat(errorEvent.getNotificacio().getEntitat().getCodi());
			resposta.setError(true);
			resposta.setErrorDescripcio(errorEvent.getErrorDescripcio());
			resposta.setErrorData(new Date());
		}
		resposta.setReferencies(referencies);
		resposta.setDataCreacio(notificacioGuardada.getCreatedDate() != null ? notificacioGuardada.getCreatedDate().toDate() : null);
		logger.debug(">> [ALTA] afegides referències");
		integracioHelper.addAccioOk(info);
		return resposta;
	}

	private void inicialitzaCallbacks(NotificacioEntity notificacioGuardada) {
		logger.debug(">> [ALTA] notificació assíncrona");
		List<NotificacioEnviamentEntity> enviamentsEntity = notificacioEnviamentRepository.findByNotificacio(notificacioGuardada);
		for (NotificacioEnviamentEntity enviament : enviamentsEntity) {
			// TODO CALLBACK: Només si no n'hi ha algun
			CallbackEntity c = CallbackEntity.builder()
					.usuariCodi(enviament.getCreatedBy().getCodi())
					.notificacioId(notificacioGuardada.getId())
					.enviamentId(enviament.getId())
					.estat(CallbackEstatEnumDto.PENDENT)
					.data(new Date())
					.error(false)
 					.errorDesc(null).build();
			callbackRepository.saveAndFlush(c);
		}
		logger.debug(">> [ALTA] callbacks de client inicialitzats");
	}

	private EnviamentReferenciaV2 saveEnviament(EntitatEntity entitat, NotificacioEntity notificacioGuardada, Enviament enviament) {

		ServeiTipusEnumDto serveiTipus = getServeiTipus(enviament);
		if (enviament.isEntregaPostalActiva() && enviament.getEntregaPostal() != null && enviament.getEntregaPostal().getTipus() == null) {
			throw new ValidationException("ENTREGA_POSTAL", "L'entrega postal te el camp tipus buit");
		}

		PersonaEntity titular = saveTitular(enviament);
		List<PersonaEntity> destinataris = getDestinataris(enviament);

		NotificacioEnviamentEntity enviamentSaved = notificacioEnviamentRepository.saveAndFlush(
				NotificacioEnviamentEntity.getBuilderV2(
						enviament,
						entitat.isAmbEntregaDeh(),
						serveiTipus,
						notificacioGuardada,
						titular,
						destinataris,
						UUID.randomUUID().toString()).build());
		enviamentTableHelper.crearRegistre(enviamentSaved);
		enviamentHelper.auditaEnviament(enviamentSaved, AuditService.TipusOperacio.CREATE, "NotificacioServiceWsImplV2.altaV2");
		logger.debug(">> [ALTA] enviament creat");


		EnviamentReferenciaV2 enviamentReferencia = new EnviamentReferenciaV2();
		enviamentReferencia.setReferencia(enviamentSaved.getNotificaReferencia());
		String titularNif = !FISICA_SENSE_NIF.equals(titular.getInteressatTipus()) ?
				(!InteressatTipusEnumDto.ADMINISTRACIO.equals(titular.getInteressatTipus()) ? titular.getNif().toUpperCase() : titular.getDir3Codi().toUpperCase())
				: null;
		enviamentReferencia.setTitularNif(titularNif);
		enviamentReferencia.setTitularNom(titular.getNom());
		enviamentReferencia.setTitularEmail(titular.getEmail());
		notificacioGuardada.addEnviament(enviamentSaved);
		return enviamentReferencia;
	}

	private List<PersonaEntity> getDestinataris(Enviament enviament) {
		List<PersonaEntity> destinataris = new ArrayList<PersonaEntity>();
		if (enviament.getDestinataris() != null) {
			for(Persona persona: enviament.getDestinataris()) {
				PersonaEntity destinatari = personaRepository.save(PersonaEntity.getBuilderV2(
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
		PersonaEntity titular = personaRepository.save(PersonaEntity.getBuilderV2(
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
		return titular;
	}

//	private NotificaDomiciliNumeracioTipusEnumDto getDomiciliNumeracioTipus(Enviament enviament) {
//		NotificaDomiciliNumeracioTipusEnumDto numeracioTipus;
//		if (enviament.getEntregaPostal().getNumeroCasa() != null) {
//			numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.NUMERO;
//		} else if (enviament.getEntregaPostal().getApartatCorreus() != null) {
//			numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.APARTAT_CORREUS;
//		} else if (enviament.getEntregaPostal().getPuntKm() != null) {
//			numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.PUNT_KILOMETRIC;
//		} else {
//			numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.SENSE_NUMERO;
//		}
//		return numeracioTipus;
//	}

//	private NotificaDomiciliConcretTipusEnumDto getDomiciliTipusConcret(
//			Enviament enviament) {
//		NotificaDomiciliConcretTipusEnumDto tipusConcret = null;
//		if (enviament.getEntregaPostal().getTipus() != null) {
//			switch (enviament.getEntregaPostal().getTipus()) {
//				case APARTAT_CORREUS:
//					tipusConcret = NotificaDomiciliConcretTipusEnumDto.APARTAT_CORREUS;
//					break;
//				case ESTRANGER:
//					tipusConcret = NotificaDomiciliConcretTipusEnumDto.ESTRANGER;
//					break;
//				case NACIONAL:
//					tipusConcret = NotificaDomiciliConcretTipusEnumDto.NACIONAL;
//					break;
//				case SENSE_NORMALITZAR:
//					tipusConcret = NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR;
//					break;
//			}
//		} else {
//			throw new ValidationException(
//					"ENTREGA_POSTAL",
//					"L'entrega postal te el camp tipus buit");
//		}
//		return tipusConcret;
//	}

	private ServeiTipusEnumDto getServeiTipus(Enviament enviament) {
		ServeiTipusEnumDto serveiTipus = null;
		if (enviament.getServeiTipus() != null) {
			switch (enviament.getServeiTipus()) {
				case NORMAL:
					serveiTipus = ServeiTipusEnumDto.NORMAL;
					break;
				case URGENT:
					serveiTipus = ServeiTipusEnumDto.URGENT;
					break;
			}
		}
		return serveiTipus;
	}

	private DocumentEntity getDocument(DocumentV2 documentV2, DocumentDto document) {
		DocumentEntity documentEntity = null;
		if(documentV2.getCsv() != null ||
				documentV2.getUuid() != null ||
				documentV2.getContingutBase64() != null ||
				documentV2.getUrl() != null ||
				documentV2.getArxiuId() != null) {

			documentEntity = documentRepository.saveAndFlush(DocumentEntity.getBuilderV2(
					documentV2.getArxiuId(),
					document.getArxiuGestdocId(),
					documentV2.getArxiuNom(),
					documentV2.getUrl(),
					documentV2.isNormalitzat(),
					documentV2.getUuid(),
					documentV2.getCsv(),
					document.getMediaType(),
					document.getMida(),
					document.getOrigen(),
					document.getValidesa(),
					document.getTipoDocumental(),
					document.getModoFirma()).build());
			logger.debug(">> [ALTA] document creat");
		}
		return documentEntity;
	}

	private NotificaEnviamentTipusEnumDto getEnviamentTipus(
			NotificacioV2 notificacio) {
		NotificaEnviamentTipusEnumDto enviamentTipus = null;
		if (notificacio.getEnviamentTipus() != null) {
			switch (notificacio.getEnviamentTipus()) {
				case COMUNICACIO:
					enviamentTipus = NotificaEnviamentTipusEnumDto.COMUNICACIO;
					break;
				case NOTIFICACIO:
					enviamentTipus = NotificaEnviamentTipusEnumDto.NOTIFICACIO;
					break;
			}
			logger.debug(">> [ALTA] enviament tipus: " + enviamentTipus);
		}
		return enviamentTipus;
	}

	private DocumentDto comprovaDocument(DocumentV2 documentV2) {

		DocumentDto document = new DocumentDto();
		// -- Per compatibilitat amb versions anteriors, posam valors per defecte
		boolean utilizarValoresPorDefecto = getUtilizarValoresPorDefecto();
		OrigenEnum origen = documentV2.getOrigen();
		ValidesaEnum validesa = documentV2.getValidesa();
		TipusDocumentalEnum tipoDocumental = documentV2.getTipoDocumental();
		Boolean modoFirma = documentV2.getModoFirma();
		if (utilizarValoresPorDefecto) {
			origen = documentV2.getOrigen() != null ? documentV2.getOrigen() : OrigenEnum.ADMINISTRACIO;
			validesa = documentV2.getValidesa() != null ? documentV2.getValidesa() : ValidesaEnum.ORIGINAL;
			tipoDocumental = documentV2.getTipoDocumental() != null ? documentV2.getTipoDocumental() : TipusDocumentalEnum.NOTIFICACIO;
			modoFirma = documentV2.getModoFirma() != null ? documentV2.getModoFirma() : false;
		}
		// --
		if(documentV2.getContingutBase64() != null) {
			logger.debug(">> [ALTA] document contingut Base64");
			byte[] contingut = Base64.decodeBase64(documentV2.getContingutBase64());
			boolean isPdf = MimeUtils.isPDF(Base64.encodeBase64String(contingut));
			String mediaType = MimeUtils.getMimeTypeFromContingut(documentV2.getArxiuNom(), documentV2.getContingutBase64());
			if (isPdf && isValidaFirmaRestEnabled()) {
				SignatureInfoDto signatureInfo = pluginHelper.detectSignedAttachedUsingValidateSignaturePlugin(contingut, documentV2.getArxiuNom(), mediaType);
				if (signatureInfo.isError()) {
					throw new SignatureValidationException(documentV2.getArxiuNom(), signatureInfo.getErrorMsg());
				}
			}
			String documentGesdocId = pluginHelper.gestioDocumentalCreate(PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS, contingut);
			document.setArxiuGestdocId(documentGesdocId);
			document.setMida(Long.valueOf(contingut.length));
			document.setMediaType(mediaType);
			document.setOrigen(origen);
			document.setValidesa(validesa);
			document.setTipoDocumental(tipoDocumental);
			document.setModoFirma(modoFirma);
			logger.debug(">> [ALTA] documentId: " + documentGesdocId);
			return document;
		}
		if (documentV2.getUuid() != null) {
			String arxiuUuid = documentV2.getUuid();
			logger.debug(">> [ALTA] documentUuid: " + arxiuUuid);
			DocumentContingut contingut = null;
			contingut = pluginHelper.arxiuGetImprimible(arxiuUuid, true);

			document.setMida(contingut.getTamany());
			document.setMediaType(contingut.getTipusMime());

			Document doc = pluginHelper.arxiuDocumentConsultar(arxiuUuid, null, true, true);
			if (doc.getMetadades() == null &&
					(origen == null || validesa == null ||
							tipoDocumental == null || modoFirma == null)) {
				throw new NoMetadadesException("No s'han obtingut metadades de la consulta a l'arxiu ni de documentV2 ni per defecte");
			}
			if (doc.getMetadades() != null) {
				document.setOrigen(OrigenEnum.valorAsEnum(doc.getMetadades().getOrigen().ordinal()));
				document.setValidesa(ValidesaEnum.valorAsEnum(pluginHelper.estatElaboracioToValidesa(doc.getMetadades().getEstatElaboracio())));
				document.setTipoDocumental(TipusDocumentalEnum.valorAsEnum(doc.getMetadades().getTipusDocumental().toString()));
				document.setModoFirma(pluginHelper.getModeFirma(doc, doc.getContingut().getArxiuNom()) == 1 ? Boolean.TRUE : Boolean.FALSE);
				// Recuperar csv
				Map<String, Object> metadadesAddicionals = doc.getMetadades().getMetadadesAddicionals();
				if (metadadesAddicionals != null) {
					if (metadadesAddicionals.containsKey("csv"))
						document.setCsv((String) metadadesAddicionals.get("csv"));
					else if (metadadesAddicionals.containsKey("eni:csv"))
						document.setCsv((String) metadadesAddicionals.get("eni:csv"));
				}
			} else {
				document.setOrigen(origen);
				document.setValidesa(validesa);
				document.setTipoDocumental(tipoDocumental);
				document.setModoFirma(modoFirma);
			}
			return document;
		}
		if (documentV2.getCsv() != null) {
			String arxiuCsv = documentV2.getCsv();
			logger.debug(">> [ALTA] documentCsv: " + arxiuCsv);
			DocumentContingut contingut = null;
			contingut = pluginHelper.arxiuGetImprimible(arxiuCsv, false);

			document.setMida(contingut.getTamany());
			document.setMediaType(contingut.getTipusMime());

			Document doc = pluginHelper.arxiuDocumentConsultar(arxiuCsv, null, true, false);
			if (doc.getMetadades() == null &&
					(origen == null || validesa == null ||
							tipoDocumental == null || modoFirma == null)) {
				throw new NoMetadadesException("No s'han obtingut metadades de la consulta a l'arxiu ni de documentV2 ni per defecte");
			}
			if (doc.getMetadades() != null) {
				document.setOrigen(OrigenEnum.valorAsEnum(doc.getMetadades().getOrigen().ordinal()));
				document.setValidesa(ValidesaEnum.valorAsEnum(pluginHelper.estatElaboracioToValidesa(doc.getMetadades().getEstatElaboracio())));
				document.setTipoDocumental(TipusDocumentalEnum.valorAsEnum(doc.getMetadades().getTipusDocumental().toString()));
				document.setModoFirma(pluginHelper.getModeFirma(doc, doc.getContingut().getArxiuNom()) == 1 ? Boolean.TRUE : Boolean.FALSE);
			} else {
				document.setOrigen(origen);
				document.setValidesa(validesa);
				document.setTipoDocumental(tipoDocumental);
				document.setModoFirma(modoFirma);
			}
			return document;
		}
		if (documentV2.getUrl() != null) {
			String arxiuUrl = documentV2.getUrl();
			logger.debug(">> [ALTA] documentUrl: " + arxiuUrl);
			byte[] contingut = pluginHelper.getUrlDocumentContent(arxiuUrl);
			document.setMida(Long.valueOf(contingut.length));
			document.setMediaType(MimeUtils.getMimeTypeFromContingut(documentV2.getArxiuNom(), contingut));
			document.setOrigen(origen);
			document.setValidesa(validesa);
			document.setTipoDocumental(tipoDocumental);
			document.setModoFirma(modoFirma);
		}
		return document;
	}



	private String getGrupNotificacio(NotificacioV2 notificacio, EntitatEntity entitat, ProcSerEntity procediment) {
		String errorDescripcio = null;
		if (procediment.isAgrupar() && notificacio.getGrupCodi() != null && !notificacio.getGrupCodi().isEmpty()) {
			logger.debug(">> [ALTA] procediment amb grups");
			// Llistat de procediments amb grups
			List<GrupDto> grupsProcediment = grupService.findByProcedimentAndUsuariGrups(procediment.getId());
			GrupDto grupNotificacio = grupService.findByCodi(
					notificacio.getGrupCodi(),
					entitat.getId());
			if (grupNotificacio == null) {
				logger.debug(">> [ALTA] procediment grup: Sense grup");
				errorDescripcio = "[1320] El grup indicat " + notificacio.getGrupCodi() + " no està definit dins NOTIB.";
			} else {
				if (grupsProcediment == null || grupsProcediment.isEmpty()) {
					errorDescripcio = "[1321] S'ha indicat un grup per les notificacions però el procediment " + notificacio.getProcedimentCodi() + " no té cap grup assignat.";
				} else {
					if(!grupsProcediment.contains(grupNotificacio)) {
						errorDescripcio = "[1322] El grup indicat " + notificacio.getGrupCodi() + " no està assignat al procediment " + notificacio.getProcedimentCodi();
					}
				}
			}
		}
		return errorDescripcio;
	}

	private IntegracioInfo generateInfoAlta(NotificacioV2 notificacio) {

		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_CLIENT,
				"Alta de notificació",
				IntegracioAccioTipusEnumDto.RECEPCIO);

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

		addMapToInfo(info, notificaAtributMap);
		integracioHelper.addAplicacioAccioParam(info, null);
		return info;
	}

	private void addMapToInfo(IntegracioInfo info, Map<String, Object> notificaAtributMap) {
		for (Map.Entry<String, Object> atribut: notificaAtributMap.entrySet()) {
			if (atribut.getValue() == null)
				continue;

//			if (atribut.getValue() instanceof Map) {
//				info.addParam("Inici " + atribut.getKey(), "------------------------------");
//				addMapToInfo(info, (Map<String, Object>) atribut.getValue());
//				info.addParam("Fi " + atribut.getKey(), "------------------------------");
//			} else if (atribut.getValue() instanceof List) {
//				info.addParam("Inici " + atribut.getKey(), "------------------------------");
//				int i = 0;
//				for (Object element: (List) atribut.getValue()) {
//					if (element == null)
//						continue;
//
//					i++;
//					if (element instanceof Map) {
//						info.addParam("Inici " + atribut.getKey() + "[" + i + "]", "------------------------------");
//						addMapToInfo(info, (Map<String, Object>) element);
//						info.addParam("Fi " + atribut.getKey() + "[" + i + "]", "------------------------------");
//					} else {
//						info.addParam(atribut.getKey() + "[" + i + "]", element.toString());
//					}
//				}
//				info.addParam("Fi " + atribut.getKey(), "------------------------------");
//			} else {
			info.addParam(atribut.getKey(), atribut.getValue().toString());
//			}
		}
	}

	// Taula de codis d'error de la validació de la API
	// ------------------------------------------------------------------------------------------
	// Codi | Descripció
	// ------------------------------------------------------------------------------------------
	// 1000 | El camp 'emisorDir3Codi' no pot ser null
	// 1001 | El camp 'emisorDir3Codi' no pot tenir una longitud superior a 9 caràcters
	// 1010 | No s'ha trobat cap entitat configurada a Notib amb el codi Dir3 especificat
	// 1011 | L'entitat especificada està desactivada per a l'enviament de notificacions
	// 1012 | L'usuari d'aplicació no està assignat a l'entitat
	// 1020 | El camp 'procedimentCodi' no pot ser null
	// 1021 | El camp 'procedimentCodi' no pot tenir una longitud superior a 9 caràcters
	// 1022 | El camp 'organ gestor' no pot ser null en una comunicació amb l'administració on no s'especifica un procediment
	// 1023 | El camp 'organ gestor' no es correspon a cap Òrgan Gestor de l'entitat especificada
	// 1024 | El camp 'organ gestor' no es correspon a l'òrgan gestor de l'procediment
	// 1025 | El procediment es troba actualment deshabilitat
	// 1029 | No es pot donar d'alta un enviament amb entrega postal activa pel procediment indicat
	// 1030 | El concepte de la notificació no pot ser null
	// 1031 | El concepte de la notificació no pot tenir una longitud superior a 255 caràcters
	// 1032 | El format del camp concepte no és correcte. (Inclou caràcters no permesos)
	// 1040 | La descripció de la notificació no pot contenir més de 1000 caràcters
	// 1041 | El format del camp descripció no és correcte
	// 1042 | Els salts de línia no estan permesos al camp descripció
	// 1050 | El tipus d'enviament de la notificació no pot ser null
	// 1060 | El camp 'document' no pot ser null
	// 1061 | El camp 'arxiuNom' del document no pot ser null
	// 1062 | És necessari incloure un document a la notificació
	// 1063 | El format del document no és vàlid. Els formats vàlids són PDF i ZIP.
	// 1064 | No s'ha pogut obtenir el document a notificar
	// 1065 | La longitud del document supera el màxim definit
	// 1066 | Error en les metadades del document
	// 1067 | Les notificacions i comunicacions a ciutadà només admeten 1 únic document.
	// 1068 | Error validant la firma del document.
	// 1070 | El camp 'usuariCodi' no pot ser null (Requisit per fer el registre de sortida)
	// 1071 | El camp 'usuariCodi' no pot pot tenir una longitud superior a 64 caràcters
	// 1072 | El camp 'arxiuNom' no pot pot tenir una longitud superior a 200 caràcters."
	// 1080 | El camp 'numExpedient' no pot pot tenir una longitud superior a 256 caràcters
	// 1090 | El camp 'grupCodi' no pot pot tenir una longitud superior a 64 caràcters
	// 1100 | El camp 'enviaments' no pot ser null
	// 1101 | El camp 'serveiTipus' d'un enviament no pot ser null
	// 1110 | El titular d'un enviament no pot ser null
	// 1111 | El camp 'interessat_tipus' del titular d'un enviament no pot ser null
	// 1112 | El camp 'nom' del titular no pot ser tenir una longitud superior a 30 caràcters
	// 1113 | El camp 'llinatge1' del titular no pot ser major que 30 caràcters
	// 1114 | El camp 'llinatge2' del titular no pot ser major que 30 caràcters
	// 1115 | El camp 'nif' del titular d'un enviament no pot tenir una longitud superior a 9 caràcters
	// 1116 | El 'nif' del titular no és vàlid
	// 1117 | El camp 'email' del titular no pot ser major que 160 caràcters
	// 1118 | El format del camp 'email' del titular no és correcte
	// 1119 | El camp 'telefon' del titular no pot ser major que 16 caràcters
	// 1120 | El camp 'raoSocial' del titular no pot ser major que 255 caràcters
	// 1121 | El camp 'dir3Codi' del titular no pot ser major que 9 caràcters
	// 1122 | En cas de titular amb incapacitat es obligatori indicar un destinatari
	// 1123 | El camp 'nif' del titular no és un tipus de document vàlid per a aquest tipus de persona
	// 1130 | El camp 'nom' de la persona física titular no pot ser null
	// 1131 | El camp 'llinatge1' de la persona física titular d'un enviament no pot ser null en el cas de persones físiques
	// 1132 | El camp 'nif' de la persona física titular d'un enviament no pot ser null
	// 1140 | El camp 'raoSocial' de la persona jurídica titular d'un enviament no pot ser null
	// 1141 | El camp 'nif' de la persona jurídica titular d'un enviament no pot ser null
	// 1150 | El camp 'nom' de l'administració titular d'un enviament no pot ser null
	// 1151 | El camp 'dir3codi' de l'administració titular d'un enviament no pot ser null
	// 1152 | El camp 'dir3codi' no es correspon a un codi Dir3 vàlid
	// 1153 | El camp 'dir3codi' no disposa d'oficina SIR. És obligatori per a comunicacions
	// 1154 | El camp 'dir3codi' fa referència a una administració de la pròpia entitat. No es pot utilitzar Notib per enviar comunicacions dins la pròpia entitat
	// 1160 | El numero de destinatais està limitat a un destinatari
	// 1170 | El camp 'interessat_tipus' del destinatari d'un enviament no pot ser null
	// 1171 | El camp 'nom' del titular no pot tenir una longitud superior a 30 caràcters
	// 1172 | El camp 'llinatge1' del destinatari no pot ser major que 30 caràcters
	// 1173 | El camp 'llinatge2' del destinatari no pot ser major que 30 caràcters
	// 1174 | El camp 'nif' del destinatari d'un enviament no pot tenir una longitud superior a 9 caràcters
	// 1175 | El 'nif' del destinatari no és vàlid
	// 1176 | El camp 'email' del destinatari no pot ser major que 255 caràcters
	// 1177 | El format del camp 'email' del destinatari no és correcte
	// 1178 | El camp 'telefon' del destinatari no pot ser major que 16 caràcters
	// 1179 | El camp 'raoSocial' del destinatari no pot ser major que 255 caràcters
	// 1180 | El camp 'dir3Codi' del destinatari no pot ser major que 9 caràcters
	// 1181 | El camp 'nif' del destinatari no és un tipus de document vàlid per a aquest tipus de persona
	// 1190 | El camp 'nom' de la persona física destinatària d'un enviament no pot ser null
	// 1191 | El camp 'llinatge1' del destinatari d'un enviament no pot ser null en el cas de persones físiques
	// 1192 | El camp 'nif' de la persona física destinatària d'un enviament no pot ser null
	// 1200 | El camp 'raoSocial' de la persona jurídica destinatària d'un enviament no pot ser null
	// 1201 | El camp 'nif' de la persona jurídica destinatària d'un enviament no pot ser null
	// 1210 | El camp 'nom' de l'administració destinatària d'un enviament no pot ser null
	// 1211 | El camp 'dir3codi' de l'administració destinatària d'un enviament no pot ser null
	// 1212 | El camp 'dir3codi' de l'administració destinatària d'un enviament no es correspon a un codi Dir3 vàlid
	// 1213 | El camp 'dir3codi' de l'administració destinatària d'un enviament no disposa d'oficina SIR. És obligatori per a comunicacions
	// 1214 | El camp 'dir3codi' de l'administració destinatària d'un enviament fa referència a una administració de la pròpia entitat. No es pot utilitzar Notib per enviar comunicacions dins la pròpia entitat
	// 1220 | En una notificació, com a mínim un dels interessats ha de tenir el Nif informat
	// 1230 | El camp 'entregaPostalTipus' no pot ser null
	// 1231 | El camp 'codiPostal' no pot ser null (indicar 00000 en cas de no disposar del codi postal)
	// 1232 | El camp 'viaNom' de l'entrega postal no pot contenir més de 50 caràcters
	// 1233 | El camp 'numeroCasa' de l'entrega postal no pot contenir més de 5 caràcters
	// 1234 | El camp 'puntKm' de l'entrega postal no pot contenir més de 5 caràcters
	// 1235 | El camp 'portal' de l'entrega postal no pot contenir més de 3 caràcters
	// 1236 | El camp 'porta' de l'entrega postal no pot contenir més de 3 caràcters
	// 1237 | El camp 'escala' de l'entrega postal no pot contenir més de 3 caràcters
	// 1238 | El camp 'planta' de l'entrega postal no pot contenir més de 3 caràcters
	// 1239 | El camp 'bloc' de l'entrega postal no pot contenir més de 3 caràcters
	// 1240 | El camp 'complement' de l'entrega postal no pot contenir més de 40 caràcters
	// 1241 | El camp 'numeroQualificador' de l'entrega postal no pot contenir més de 3 caràcters
	// 1242 | El camp 'codiPostal' no pot contenir més de 10 caràcters)
	// 1243 | El camp 'apartatCorreus' no pot contenir més de 10 caràcters)
	// 1244 | El camp 'municipiCodi' de l'entrega postal no pot contenir més de 6 caràcters
	// 1245 | El camp 'provincia' de l'entrega postal no pot contenir més de 2 caràcters
	// 1246 | El camp 'paisCodi' de l'entrega postal no pot contenir més de 2 caràcters
	// 1247 | El camp 'poblacio' de l'entrega postal no pot contenir més de 255 caràcters
	// 1248 | El camp 'linea1' de l'entrega postal no pot contenir més de 50 caràcters
	// 1249 | El camp 'linea2' de l'entrega postal no pot contenir més de 50 caràcters
	// 1260 | El camp 'viaTipus' no pot ser null en cas d'entrega NACIONAL NORMALITZAT
	// 1261 | El camp 'viaNom' no pot ser null en cas d'entrega NACIONAL NORMALITZAT
	// 1262 | S'ha d'indicar almenys un 'numeroCasa' o 'puntKm'
	// 1263 | El camp 'municipiCodi' no pot ser null en cas d'entrega NACIONAL NORMALITZAT
	// 1264 | El camp 'provincia' no pot ser null en cas d'entrega NACIONAL NORMALITZAT
	// 1265 | El camp 'poblacio' no pot ser null en cas d'entrega NACIONAL NORMALITZAT
	// 1270 | El camp 'viaNom' no pot ser null en cas d'entrega ESTRANGER NORMALITZAT
	// 1271 | El camp 'paisCodi' no pot ser null en cas d'entrega ESTRANGER NORMALITZAT
	// 1272 | El camp 'poblacio' no pot ser null en cas d'entrega ESTRANGER NORMALITZAT
	// 1280 | El camp 'apartatCorreus' no pot ser null en cas d'entrega APARTAT CORREUS
	// 1281 | El camp 'municipiCodi' no pot ser null en cas d'entrega APARTAT CORREUS
	// 1282 | El camp 'provincia' no pot ser null en cas d'entrega APARTAT CORREUS
	// 1283 | El camp 'poblacio' no pot ser null en cas d'entrega APARTAT CORREUS
	// 1290 | El camp 'linea1' no pot ser null
	// 1291 | El camp 'linea2' no pot ser null
	// 1300 | El camp 'entrega DEH' de l'entitat ha d'estar actiu en cas d'enviaments amb entrega DEH
	// 1301 | El camp 'entregaDeh' d'un enviament no pot ser null
	// 1302 | El nif del titular és obligatori quan s'activa la entrega DEH
	// 1310 | Una comunicació no pot estar dirigida a una administració i a una persona física/jurídica a la vegada
	// 1320 | El grup indicat no està definit dins NOTIB
	// 1321 | S'ha indicat un grup per les notificacions però el procediment no té cap grup assignat
	// 1322 | El grup indicat no està assignat al procediment
	// 1330 | No s'ha trobat cap procediment amb el codi indicat
	// 1331 | No es pot donar d'alta una notificació amb servei. Els serveis només s'admeten en comunicacions
	protected RespostaAltaV2 validarNotificacio(
			NotificacioV2 notificacio,
			String emisorDir3Codi,
			EntitatEntity entitat,
			AplicacioEntity aplicacio) {
		boolean comunicacioSenseAdministracio = false;
		boolean comunicacioAmbAdministracio = false;
		Map<String, OrganismeDto> organigramaByEntitat = null;

		if (notificacio.getEnviamentTipus() == EnviamentTipusEnum.COMUNICACIO) {
			organigramaByEntitat = organGestorCachable.findOrganigramaByEntitat(emisorDir3Codi);
		}

		// Emisor
		if (emisorDir3Codi == null || emisorDir3Codi.isEmpty()) {
			return setRespostaError(messageHelper.getMessage("error.validacio.emisordir3codi.no.null"));
		}
		if (emisorDir3Codi.length() > 9) {
			return setRespostaError(messageHelper.getMessage("error.validacio.emisordir3codi.longitud.max"));
		}
		// Entitat
		if (entitat == null) {
			return setRespostaError(messageHelper.getMessage("error.validacio.entitat.no.configurada.amb.codidir3.a")
					+ emisorDir3Codi + messageHelper.getMessage("error.validacio.entitat.no.configurada.amb.codidir3.b"));
		}
		if (!entitat.isActiva()) {
			return setRespostaError(messageHelper.getMessage("error.validacio.entitat.desactivada.per.enviament.notificacions"));
		}
		// Aplicacio
		if (aplicacio == null) {
			return setRespostaError(messageHelper.getMessage("error.validacio.usuari.no.assignat.entitat") + emisorDir3Codi);
		}
		// Procediment
		if (notificacio.getProcedimentCodi() != null && notificacio.getProcedimentCodi().length() > 9) {
			return setRespostaError(messageHelper.getMessage("error.validacio.procediment.codi.longitud.max"));
		}
		// Concepte
		if (notificacio.getConcepte() == null || notificacio.getConcepte().isEmpty()) {
			return setRespostaError(messageHelper.getMessage("error.validacio.concepte.no.null"));
		}
		if (notificacio.getConcepte().length() > 240) {
			return setRespostaError(messageHelper.getMessage("error.validacio.concepte.longitud.max"));
		}
		if (!validFormat(notificacio.getConcepte()).isEmpty()) {
			return setRespostaError(messageHelper.getMessage("error.validacio.concepte.format.invalid.a")
					+ listToString(validFormat(notificacio.getConcepte()))
					+ messageHelper.getMessage("error.validacio.concepte.format.invalid.b"));
		}
		// Descripcio
		if (notificacio.getDescripcio() != null && notificacio.getDescripcio().length() > 1000) {
			return setRespostaError(messageHelper.getMessage("error.validacio.descripcio.notificacio.longitud.max"));
		}
		if (notificacio.getDescripcio() != null && !validFormat(notificacio.getDescripcio()).isEmpty()) {
			return setRespostaError(
					messageHelper.getMessage("error.validacio.descripcio.invalid.a")
							+ listToString(validFormat(notificacio.getDescripcio())) +
							messageHelper.getMessage("error.validacio.descripcio.invalid.b"));
		}
		if (notificacio.getDescripcio() != null && hasSaltLinia(notificacio.getDescripcio())) {
			return setRespostaError(messageHelper.getMessage("error.validacio.descripcio.no.salts.liniea"));
		}
		// Tipus d'enviament
		if (notificacio.getEnviamentTipus() == null) {
			return setRespostaError(messageHelper.getMessage("error.validacio.tipus.enviament.no.null"));
		}
		// Dates
		Date now = new Date();
		Date dataProg = notificacio.getEnviamentDataProgramada();
		Date dataCaducitat = notificacio.getCaducitat();
		if (dataProg != null && dataProg.before(now) && !DateUtils.isSameDay(dataProg, now)) {
			return setRespostaError(messageHelper.getMessage("error.validacio.data.enviament.programada.anterior"));
		}
		if (dataCaducitat != null && dataCaducitat.before(now) && !DateUtils.isSameDay(dataCaducitat, now)) {
			return setRespostaError(messageHelper.getMessage("error.validacio.data.caducitat.anterior"));
		}
		if (dataProg != null && dataCaducitat != null && dataCaducitat.before(dataProg)) {
			return setRespostaError(messageHelper.getMessage("error.validacio.data.caducitat.anterior.data.programada"));
		}

		// Document
		if (notificacio.getDocument() == null) {
			return setRespostaError(messageHelper.getMessage("error.validacio.document.no.null"));
		}
		DocumentV2 document = notificacio.getDocument();
		//TODO: Revisar la validación del nom. Para CSV/UUid en el formulario web
		//NO se pide un nombre; se recupera posteriormente del plugin.
		if (document.getArxiuNom() == null || document.getArxiuNom().isEmpty()) {
			return setRespostaError(messageHelper.getMessage("error.validacio.nom.arxiu.document.no.null"));
		}
		if (document.getArxiuNom() != null && document.getArxiuNom().length() > 200) {
			return setRespostaError(messageHelper.getMessage("error.validacio.arxiu.nom.longitud.max"));
		}
		if ((document.getContingutBase64() == null || document.getContingutBase64().isEmpty()) &&
				(document.getCsv() == null || document.getCsv().isEmpty()) &&
				(document.getUrl() == null || document.getUrl().isEmpty()) &&
				(document.getUuid() == null || document.getUuid().isEmpty())) {
			return setRespostaError(messageHelper.getMessage("error.validacio.document.necessari"));
		}
		// Usuari
		if (notificacio.getUsuariCodi() == null || notificacio.getUsuariCodi().isEmpty()) {
			return setRespostaError(messageHelper.getMessage("error.validacio.usuari.codi.no.null"));
		}
		if (notificacio.getUsuariCodi().length() > 64) {
			return setRespostaError(messageHelper.getMessage("error.validacio.usuari.codi.longitud.max"));
		}
		DadesUsuari dades = cacheHelper.findUsuariAmbCodi(notificacio.getUsuariCodi());
		if (dades == null || Strings.isNullOrEmpty(dades.getCodi())) {
			return setRespostaError(messageHelper.getMessage("error.validacio.usuari.no.trobat"));
		}

		// Número d'expedient
		if (notificacio.getNumExpedient() != null && notificacio.getNumExpedient().length() > 80) {
			return setRespostaError(messageHelper.getMessage("error.validacio.num.expedient.longitud.max"));
		}
		// GrupCodi
		if (notificacio.getGrupCodi() != null && notificacio.getGrupCodi().length() > 64) {
			return setRespostaError(messageHelper.getMessage("error.validacio.grup.codi.longitud.max"));
		}
		// Enviaments
		if (notificacio.getEnviaments() == null || notificacio.getEnviaments().isEmpty()) {
			return setRespostaError(messageHelper.getMessage("error.validacio.enviaments.no.null"));
		}
		List<String> nifs = new ArrayList<>();
		for(Enviament enviament : notificacio.getEnviaments()) {
			//Si és comunicació a administració i altres mitjans (persona física/jurídica) --> Excepció
			if (notificacio.getEnviamentTipus() == EnviamentTipusEnum.COMUNICACIO) {
				comunicacioAmbAdministracio = comunicacioAmbAdministracio || InteressatTipusEnumDto.isAdministracio(enviament.getTitular().getInteressatTipus());
				comunicacioSenseAdministracio = comunicacioSenseAdministracio || !InteressatTipusEnumDto.isAdministracio(enviament.getTitular().getInteressatTipus());
			}
			boolean senseNif = true;

			// Servei tipus
			if(enviament.getServeiTipus() == null) {
				return setRespostaError(messageHelper.getMessage("error.validacio.servei.tipus.no.null"));
			}

			// Titular
			Persona titular = enviament.getTitular();
			if(enviament.getTitular() == null) {
				return setRespostaError(messageHelper.getMessage("error.validacio.titular.enviament.no.null"));
			}
			// - Tipus
			if(titular.getInteressatTipus() == null) {
				return setRespostaError(messageHelper.getMessage("error.validacio.interessat.tipus.titular.enviament.no.null"));
			}
			// - Nom
			if(titular.getNom() != null && !InteressatTipusEnumDto.ADMINISTRACIO.equals(titular.getInteressatTipus()) && titular.getNom().length() > 30) {
				return setRespostaError(messageHelper.getMessage("error.validacio.nom.titular.longitud.max"));
			}
			if(titular.getNom() != null && InteressatTipusEnumDto.ADMINISTRACIO.equals(titular.getInteressatTipus()) && titular.getNom().length() > 255) {
				return setRespostaError(messageHelper.getMessage("error.validacio.nom.titular.longitud.max.administracio"));
			}
			// - Llinatge 1
			if (titular.getLlinatge1() != null && titular.getLlinatge1().length() > 30) {
				return setRespostaError(messageHelper.getMessage("error.validacio.llinatge1.titular.longitud.max"));
			}
			// - Llinatge 2
			if (titular.getLlinatge2() != null && titular.getLlinatge2().length() > 30) {
				return setRespostaError(messageHelper.getMessage("error.validacio.llinatge2.titular.longitud.max"));
			}
			// - Nif
			if(titular.getNif() != null && titular.getNif().length() > 9) {
				return setRespostaError(messageHelper.getMessage("error.validacio.nif.titular.longitud.max"));
			}
			if (!Strings.isNullOrEmpty(titular.getNif()) && !InteressatTipusEnumDto.FISICA_SENSE_NIF.equals(titular.getInteressatTipus())) {
				String nif = titular.getNif().toLowerCase();
				if (nifs.contains(nif)) {
					return setRespostaError(messageHelper.getMessage("notificacio.form.valid.nif.repetit"));
				} else {
					nifs.add(nif);
				}
			}
			if (!FISICA_SENSE_NIF.equals(titular.getInteressatTipus()) && titular.getNif() != null && !titular.getNif().isEmpty()) {
				if (NifHelper.isvalid(enviament.getTitular().getNif())) {
					senseNif = false;
				} else {
					return setRespostaError(messageHelper.getMessage("error.validacio.nif.titular.invalid"));
				}
				switch (titular.getInteressatTipus()) {
					case FISICA:
						if (!NifHelper.isValidNifNie(titular.getNif())) {
							return setRespostaError(messageHelper.getMessage("error.validacio.nif.titular.tipus.document.no.valid.persona.fisica"));
						}
						break;
					case JURIDICA:
						if (!NifHelper.isValidCif(titular.getNif())) {
							return setRespostaError(messageHelper.getMessage("error.validacio.nif.titular.tipus.document.invalid.persona.juridica"));
						}
						break;
					case ADMINISTRACIO:
						break;
				}
			}
			// - Email
			if (titular.getEmail() != null && titular.getEmail().length() > 160) {
				return setRespostaError(messageHelper.getMessage("error.validacio.email.titular.longitud.max"));
			}
			if (titular.getEmail() != null && !isEmailValid(titular.getEmail())) {
				return setRespostaError(messageHelper.getMessage("error.validacio.email.titular.format.invalid"));
			}
			// - Telèfon
			if (titular.getTelefon() != null && titular.getTelefon().length() > 16) {
				return setRespostaError(messageHelper.getMessage("error.validacio.telefon.longitud.max"));
			}
			// - Raó social
			if (titular.getRaoSocial() != null && titular.getRaoSocial().length() > 80) {
				return setRespostaError(messageHelper.getMessage("error.validacio.rao.social.longitud.max"));
			}
			// - Codi Dir3
			if (titular.getDir3Codi() != null && titular.getDir3Codi().length() > 9) {
				return setRespostaError(messageHelper.getMessage("error.validacio.dir3codi.titular.longitud.max"));
			}
			// - Incapacitat
			if (titular.isIncapacitat() && (enviament.getDestinataris() == null || enviament.getDestinataris().isEmpty())) {
				return setRespostaError(messageHelper.getMessage("error.validacio.indicar.destinatari.titular.incapacitat"));
			}
			//   - Persona física
			if(titular.getInteressatTipus().equals(InteressatTipusEnumDto.FISICA)) {
				if(titular.getNom() == null || titular.getNom().isEmpty()) {
					return setRespostaError(messageHelper.getMessage("error.validacio.nom.persona.fisica.no.null"));
				}
				if (titular.getLlinatge1() == null || titular.getLlinatge1().isEmpty()) {
					return setRespostaError(messageHelper.getMessage("error.validacio.llinatge1.persona.fisica.titular.enviament.no.null"));
				}
				if(titular.getNif() == null || titular.getNif().isEmpty()) {
					return setRespostaError(messageHelper.getMessage("error.validacio.nif.persona.fisica.titular.enviament.no.null"));
				}
				//   - Persona física sense nif
			} else if(titular.getInteressatTipus().equals(FISICA_SENSE_NIF)) {
				if(titular.getNom() == null || titular.getNom().isEmpty()) {
					return setRespostaError(messageHelper.getMessage("error.validacio.nom.persona.fisica.no.null"));
				}
				if (titular.getLlinatge1() == null || titular.getLlinatge1().isEmpty()) {
					return setRespostaError(messageHelper.getMessage("error.validacio.llinatge1.persona.fisica.titular.enviament.no.null"));
				}
				// Email obligatori si no té destinataris amb nif o enviament postal
				if ((titular.getEmail() == null || titular.getEmail().isEmpty())
						&& (enviament.getDestinataris() == null || enviament.getDestinataris().isEmpty())
						&& !enviament.isEntregaPostalActiva() ) {
					return setRespostaError(messageHelper.getMessage("error.validacio.email.persona.fisica.sense.nif.email.titular.enviament.no.null"));
				}
				//   - Persona jurídica
			} else if(titular.getInteressatTipus().equals(InteressatTipusEnumDto.JURIDICA)) {
				if((titular.getRaoSocial() == null || titular.getRaoSocial().isEmpty()) && (titular.getNom() == null || titular.getNom().isEmpty())) {
					return setRespostaError(messageHelper.getMessage("error.validacio.rao.social.persona.juridica.titular.enviament.no.null"));
				}
				if(titular.getNif() == null || titular.getNif().isEmpty()) {
					return setRespostaError(messageHelper.getMessage("error.validacio.nif.persona.juridica.titular.enviament.no.null"));
				}
				//   - Administració
			} else if(titular.getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO)) {
				if(titular.getNom() == null || titular.getNom().isEmpty()) {
					return setRespostaError(messageHelper.getMessage("error.validacio.nom.administracio.titular.enviament.no.null"));
				}
				if(titular.getDir3Codi() == null) {
					return setRespostaError(messageHelper.getMessage("error.validacio.dir3codi.administracio.titular.enviament.no.null"));
				}
				senseNif = false;
				OrganGestorDto organDir3 = cacheHelper.unitatPerCodi(enviament.getTitular().getDir3Codi());
				if (organDir3 == null) {
					return setRespostaError(
							messageHelper.getMessage("error.validacio.dir3codi.invalid.a")
									+ enviament.getTitular().getDir3Codi() +
									messageHelper.getMessage("error.validacio.dir3codi.invalid.b"));
				}
				if (notificacio.getEnviamentTipus() == EnviamentTipusEnum.COMUNICACIO) {
					if (organDir3.getSir() == null || !organDir3.getSir()) {
						return setRespostaError(
								messageHelper.getMessage("error.validacio.dir3codi.no.oficina.sir.a")
										+ enviament.getTitular().getDir3Codi() +
										messageHelper.getMessage("error.validacio.dir3codi.no.oficina.sir.b"));
					}
					if (!isPermesComunicacionsSirPropiaEntitat() && organigramaByEntitat.containsKey(enviament.getTitular().getDir3Codi())) {
						return setRespostaError(
								messageHelper.getMessage("error.validacio.dir3.codi.referencia.administracio.propia.entitat.a")
										+ enviament.getTitular().getDir3Codi() +
										messageHelper.getMessage("error.validacio.dir3.codi.referencia.administracio.propia.entitat.b"));
					}
				}
				if (titular.getNif() == null || titular.getNif().isEmpty()) {
					titular.setNif(organDir3.getCif());
				}
			}

			// Destinataris
			if (!isMultipleDestinataris() && enviament.getDestinataris() != null && enviament.getDestinataris().size() > 1) {
				return setRespostaError(messageHelper.getMessage("error.validacio.num.destinataris.limit"));
			}
			if (enviament.getDestinataris() != null) {
				// Destinatari
				for(Persona destinatari : enviament.getDestinataris()) {
					if(destinatari.getInteressatTipus() == null) {
						return setRespostaError(messageHelper.getMessage("error.validacio.interessat.tipus.enviament.no.null"));
					}
					// - Nom
					if(destinatari.getNom() != null && destinatari.getNom().length() > 30) {
						return setRespostaError(messageHelper.getMessage("error.validacio.nom.destinatari.longitud.max"));
					}
					// - Llinatge 1
					if (destinatari.getLlinatge1() != null && destinatari.getLlinatge1().length() > 30) {
						return setRespostaError(messageHelper.getMessage("error.validacio.llinatge1.destinatari.longitud.max"));
					}
					// - Llinatge 2
					if (destinatari.getLlinatge2() != null && destinatari.getLlinatge2().length() > 30) {
						return setRespostaError(messageHelper.getMessage("error.validacio.llinatge2.destinatari.longitud.max"));
					}
					// - Nif
					if(destinatari.getNif() != null && destinatari.getNif().length() > 9) {
						return setRespostaError(messageHelper.getMessage("error.validacio.nif.destinatari.longitud.max"));
					}
					if (!Strings.isNullOrEmpty(destinatari.getNif())) {
						String nif = destinatari.getNif().toLowerCase();
						if (nifs.contains(nif)) {
							return setRespostaError(messageHelper.getMessage("notificacio.form.valid.nif.repetit"));
						} else {
							nifs.add(nif);
						}
					}
					if (!FISICA_SENSE_NIF.equals(destinatari.getInteressatTipus()) && destinatari.getNif() != null && !destinatari.getNif().isEmpty()) {
						if (NifHelper.isvalid(destinatari.getNif())) {
							senseNif = false;
						} else {
							return setRespostaError(messageHelper.getMessage("error.validacio.nif.destinatari.invalid"));
						}
						switch (destinatari.getInteressatTipus()) {
							case FISICA:
								if (!NifHelper.isValidNifNie(destinatari.getNif())) {
									return setRespostaError(messageHelper.getMessage("error.validacio.nif.destinatari.invalid.persona.fisica"));
								}
								break;
							case JURIDICA:
								if (!NifHelper.isValidCif(destinatari.getNif())) {
									return setRespostaError(messageHelper.getMessage("error.validacio.nif.destinatari.invalid.persona.juridica"));
								}
								break;
							case ADMINISTRACIO:
								break;
						}
					}
					// - Email
					if (destinatari.getEmail() != null && destinatari.getEmail().length() > 160) {
						return setRespostaError(messageHelper.getMessage("error.validacio.email.destinatari.longitud.max"));
					}
					if (destinatari.getEmail() != null && !isEmailValid(destinatari.getEmail())) {
						return setRespostaError(messageHelper.getMessage("error.validacio.email.destinatari.format.incorrecte"));
					}
					// - Telèfon
					if (destinatari.getTelefon() != null && destinatari.getTelefon().length() > 16) {
						return setRespostaError(messageHelper.getMessage("error.validacio.telefon.destinatari.longitud.max"));
					}
					// - Raó social
					if (destinatari.getRaoSocial() != null && destinatari.getRaoSocial().length() > 80) {
						return setRespostaError(messageHelper.getMessage("error.rao.social.destinatari.longitud.max"));
					}
					// - Codi Dir3
					if (destinatari.getDir3Codi() != null && destinatari.getDir3Codi().length() > 9) {
						return setRespostaError(messageHelper.getMessage("error.dir3codi.destinatari.longitud.max"));
					}

					if(destinatari.getInteressatTipus().equals(InteressatTipusEnumDto.FISICA)) {
						if(destinatari.getNom() == null || destinatari.getNom().isEmpty()) {
							return setRespostaError(messageHelper.getMessage("error.nom.persona.fisica.destinataria.enviament.no.null"));
						}
						if (destinatari.getLlinatge1() == null) {
							return setRespostaError(messageHelper.getMessage("error.llinatge1.persona.fisica.destinatari.enviament.no.null"));
						}
						if(destinatari.getNif() == null) {
							return setRespostaError(messageHelper.getMessage("error.nif.persona.fisica.destinataria.enviament.no.null"));
						}
					} else if(destinatari.getInteressatTipus().equals(FISICA_SENSE_NIF)) {
						return setRespostaError(messageHelper.getMessage("error.validacio.nif.destinatari.invalid.persona.fisica.sense.nif"));
					} else if(destinatari.getInteressatTipus().equals(InteressatTipusEnumDto.JURIDICA)) {
						if((destinatari.getRaoSocial() == null || destinatari.getRaoSocial().isEmpty()) && (destinatari.getNom() == null || destinatari.getNom().isEmpty())) {
							return setRespostaError(messageHelper.getMessage("error.rao.social.persona.juridica.destinataria.enviament.no.null"));
						}
						if(destinatari.getNif() == null) {
							return setRespostaError(messageHelper.getMessage("error.nif.persona.juridica.destinataria.enviament.no.null"));
						}
					} else if(destinatari.getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO)) {
						if(destinatari.getNom() == null || destinatari.getNom().isEmpty()) {
							return setRespostaError(messageHelper.getMessage("error.nom.administracio.desti.enviament.no.null"));
						}
						if(destinatari.getDir3Codi() == null) {
							return setRespostaError(messageHelper.getMessage("error.dir3codi.administracio.desti.enviament.no.null"));
						}
						OrganGestorDto organDir3 = cacheHelper.unitatPerCodi(destinatari.getDir3Codi());
						if (organDir3 == null) {
							return setRespostaError(
									messageHelper.getMessage("error.dir3codi.administracio.desti.enviament.invalid.a")
											+ destinatari.getDir3Codi() +
											messageHelper.getMessage("error.dir3codi.administracio.desti.enviament.invalid.b"));
						}
						if (notificacio.getEnviamentTipus() == EnviamentTipusEnum.COMUNICACIO) {
							if (organDir3.getSir() == null || !organDir3.getSir()) {
								return setRespostaError(
										messageHelper.getMessage("error.dir3codi.administracio.desti.enviament.no.oficina.sir.a")
												+ destinatari.getDir3Codi() +
												messageHelper.getMessage("error.dir3codi.administracio.desti.enviament.no.oficina.sir.b"));
							}
							if (organigramaByEntitat.containsKey(destinatari.getDir3Codi())) {
								return setRespostaError(
										messageHelper.getMessage("error.dir3codi.administracio.desti.enviament.referencia.admin.propia.entitat.a")
												+ destinatari.getDir3Codi() +
												messageHelper.getMessage("error.dir3codi.administracio.desti.enviament.referencia.admin.propia.entitat.b"));
							}
						}
						if (destinatari.getNif() == null || destinatari.getNif().isEmpty()) {
							destinatari.setNif(organDir3.getCif());
						}
					}

				}
			}
			if (notificacio.getEnviamentTipus() == EnviamentTipusEnum.NOTIFICACIO && senseNif && !FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus())) {
				return setRespostaError(messageHelper.getMessage("error.validacio.nif.informat.interessats"));
			}

			// Entrega postal
			if(enviament.isEntregaPostalActiva()){
				if (enviament.getEntregaPostal().getTipus() == null) {
					return setRespostaError(messageHelper.getMessage("error.validacio.entrega.posta.tipus.no.null"));
				}
				if(enviament.getEntregaPostal().getCodiPostal() == null || enviament.getEntregaPostal().getCodiPostal().isEmpty()) {
					return setRespostaError(messageHelper.getMessage("error.validacio.codi.postal.no.null"));
				}

				if (enviament.getEntregaPostal().getViaNom() != null && enviament.getEntregaPostal().getViaNom().length() > 50) {
					return setRespostaError(messageHelper.getMessage("error.validacio.via.nom.entrega.postal.longitud.max"));
				}
				if (enviament.getEntregaPostal().getNumeroCasa() != null && enviament.getEntregaPostal().getNumeroCasa().length() > 5) {
					return setRespostaError(messageHelper.getMessage("error.validacio.numero.casa.entrega.posta.longitud.max"));
				}
				if (enviament.getEntregaPostal().getPuntKm() != null && enviament.getEntregaPostal().getPuntKm().length() > 5) {
					return setRespostaError(messageHelper.getMessage("error.validacio.punt.km.longitud.max"));
				}
				if (enviament.getEntregaPostal().getPortal() != null && enviament.getEntregaPostal().getPortal().length() > 3) {
					return setRespostaError(messageHelper.getMessage("error.validacio.portal.longitud.max"));
				}
				if (enviament.getEntregaPostal().getPorta() != null && enviament.getEntregaPostal().getPorta().length() > 3) {
					return setRespostaError(messageHelper.getMessage("error.validacio.porta.longitud.max"));
				}
				if (enviament.getEntregaPostal().getEscala() != null && enviament.getEntregaPostal().getEscala().length() > 3) {
					return setRespostaError(messageHelper.getMessage("error.validacio.escala.longitud.max"));
				}
				if (enviament.getEntregaPostal().getPlanta() != null && enviament.getEntregaPostal().getPlanta().length() > 3) {
					return setRespostaError(messageHelper.getMessage("error.validacio.planta.longitud.max"));
				}
				if (enviament.getEntregaPostal().getBloc() != null && enviament.getEntregaPostal().getBloc().length() > 3) {
					return setRespostaError(messageHelper.getMessage("error.validacio.bloc.longitud.max"));
				}
				if (enviament.getEntregaPostal().getComplement() != null && enviament.getEntregaPostal().getComplement().length() > 40) {
					return setRespostaError(messageHelper.getMessage("error.validacio.complement.longitud.max"));
				}
				if (enviament.getEntregaPostal().getNumeroQualificador() != null && enviament.getEntregaPostal().getNumeroQualificador().length() > 3) {
					return setRespostaError(messageHelper.getMessage("error.validacio.numero.qualificador.longitud.max"));
				}
				if(enviament.getEntregaPostal().getCodiPostal() != null && enviament.getEntregaPostal().getCodiPostal().length() > 10) {
					return setRespostaError(messageHelper.getMessage("error.validacio.codi.postal.longitud.max"));
				}
				if(enviament.getEntregaPostal().getApartatCorreus() != null && enviament.getEntregaPostal().getApartatCorreus().length() > 10) {
					return setRespostaError(messageHelper.getMessage("error.validacio.apartat.correus.longitud.max"));
				}
				if (enviament.getEntregaPostal().getMunicipiCodi() != null && enviament.getEntregaPostal().getMunicipiCodi().length() > 6) {
					return setRespostaError(messageHelper.getMessage("error.validacio.municipi.codi.entrega.postal.longitud.max"));
				}
				if (enviament.getEntregaPostal().getProvincia() != null && enviament.getEntregaPostal().getProvincia().length() > 2) {
					return setRespostaError(messageHelper.getMessage("error.validacio.provincia.entrega.postal.longitud.max"));
				}
				if (enviament.getEntregaPostal().getPaisCodi() != null && enviament.getEntregaPostal().getPaisCodi().length() > 2) {
					return setRespostaError(messageHelper.getMessage("error.validacio.pais.codi.entrega.postal.longitud.max"));
				}
				if (enviament.getEntregaPostal().getPoblacio() != null && enviament.getEntregaPostal().getPoblacio().length() > 255) {
					return setRespostaError(messageHelper.getMessage("error.validacio.poblacio.entrega.postal.longitud.max"));
				}
				if (enviament.getEntregaPostal().getLinea1() != null && enviament.getEntregaPostal().getLinea1().length() > 50) {
					return setRespostaError(messageHelper.getMessage("error.validacio.linea1.entrega.postal.longitud.max"));
				}
				if (enviament.getEntregaPostal().getLinea2() != null && enviament.getEntregaPostal().getLinea2().length() > 50) {
					return setRespostaError(messageHelper.getMessage("error.validacio.linea2.entrega.postal.longitud.max"));
				}
				if(enviament.getEntregaPostal().getTipus().equals(NotificaDomiciliConcretTipusEnumDto.NACIONAL)) {
					if (enviament.getEntregaPostal().getViaTipus() == null) {
						return setRespostaError(messageHelper.getMessage("error.validacio.via.tipus.entrega.nacional.normalitzat"));
					}
					if (enviament.getEntregaPostal().getViaNom() == null || enviament.getEntregaPostal().getViaNom().isEmpty()) {
						return setRespostaError(messageHelper.getMessage("error.validacio.via.nom.no.null.entrega.nacional.normalitzat"));
					}
					if (enviament.getEntregaPostal().getPuntKm() == null && enviament.getEntregaPostal().getNumeroCasa() == null) {
						return setRespostaError(messageHelper.getMessage("error.validacio.indicar.num.casa.punt.km"));
					}
					if (enviament.getEntregaPostal().getMunicipiCodi() == null || enviament.getEntregaPostal().getMunicipiCodi().isEmpty()) {
						return setRespostaError(messageHelper.getMessage("error.validacio.municipi.codi.no.null.entrega.nacional"));
					}
					if (enviament.getEntregaPostal().getProvincia() == null || enviament.getEntregaPostal().getProvincia().isEmpty()) {
						return setRespostaError(messageHelper.getMessage("error.validacio.provincia.no.null.entrega.nacional"));
					}
					if (enviament.getEntregaPostal().getPoblacio() == null || enviament.getEntregaPostal().getPoblacio().isEmpty()) {
						return setRespostaError(messageHelper.getMessage("error.validacio.poblacio.codi.no.null.entrega.nacional.normalitzat"));
					}
				}
				if(enviament.getEntregaPostal().getTipus().equals(NotificaDomiciliConcretTipusEnumDto.ESTRANGER)) {
					if (enviament.getEntregaPostal().getViaNom() == null || enviament.getEntregaPostal().getViaNom().isEmpty()) {
						return setRespostaError(messageHelper.getMessage("error.validacio.via.nom.no.null.entrega.estranger.normalitzat"));
					}
					if (enviament.getEntregaPostal().getPaisCodi() == null || enviament.getEntregaPostal().getPaisCodi().isEmpty()) {
						return setRespostaError(messageHelper.getMessage("error.validacio.pais.codi.no.null.entrega.estranger.normalitzat"));
					}
					if (enviament.getEntregaPostal().getPoblacio() == null || enviament.getEntregaPostal().getPoblacio().isEmpty()) {
						return setRespostaError(messageHelper.getMessage("error.validacio.poblacio.no.null.entrega.estranger.normalitzat"));
					}
				}
				if(enviament.getEntregaPostal().getTipus().equals(NotificaDomiciliConcretTipusEnumDto.APARTAT_CORREUS)) {
					if (enviament.getEntregaPostal().getApartatCorreus() == null || enviament.getEntregaPostal().getApartatCorreus().isEmpty()) {
						return setRespostaError(messageHelper.getMessage("error.validacio.apartat.correus.no.null.entrega.apartat.correus"));
					}
					if (enviament.getEntregaPostal().getMunicipiCodi() == null || enviament.getEntregaPostal().getMunicipiCodi().isEmpty()) {
						return setRespostaError(messageHelper.getMessage("error.validacio.municipi.codi.no.null.entrega.apartat.correus"));
					}
					if (enviament.getEntregaPostal().getProvincia() == null || enviament.getEntregaPostal().getProvincia().isEmpty()) {
						return setRespostaError(messageHelper.getMessage("error.validacio.provincia.no.null.entrega.apartat.correus"));
					}
					if (enviament.getEntregaPostal().getPoblacio() == null || enviament.getEntregaPostal().getPoblacio().isEmpty()) {
						return setRespostaError(messageHelper.getMessage("error.validacio.poblacio.no.null.entrega.apartat.correus"));
					}
				}
				if(enviament.getEntregaPostal().getTipus().equals(NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR)) {
					if (enviament.getEntregaPostal().getLinea1() == null || enviament.getEntregaPostal().getLinea1().isEmpty()) {
						return setRespostaError(messageHelper.getMessage("error.validacio.linea1.entrega.postal.no.null"));
					}
					if (enviament.getEntregaPostal().getLinea2() == null || enviament.getEntregaPostal().getLinea2().isEmpty()) {
						return setRespostaError(messageHelper.getMessage("error.validacio.linea2.entrega.postal.no.null"));
					}
//					if (enviament.getEntregaPostal().getPaisCodi() == null || enviament.getEntregaPostal().getPaisCodi().isEmpty()) {
//						return setRespostaError("[PAIS] El camp 'paisCodi' no pot ser null en cas d'entrega SENSE NORMALITZAR.");
//					}
				}
			}

			// Entrega DEH
			if (!entitat.isAmbEntregaDeh() && enviament.isEntregaDehActiva()) {
				return setRespostaError(messageHelper.getMessage("error.validacio.entrega.deh.inactiu"));
			}
			if (enviament.isEntregaDehActiva() && enviament.getEntregaDeh() == null) {
				return setRespostaError(messageHelper.getMessage("error.validacio.entrega.deh.no.null"));
			}
			if (enviament.isEntregaDehActiva() && (enviament.getTitular().getNif() == null || enviament.getTitular().getNif().isEmpty())) {
				return setRespostaError(messageHelper.getMessage("error.validacio.nif.obligatori.entrega.deh"));
			}

		}
		if (comunicacioAmbAdministracio && comunicacioSenseAdministracio) {
			return setRespostaError(messageHelper.getMessage("error.validacio.comunicacio.destinatari.incorrecte"));
		}

		// Procediment
		if (notificacio.getEnviamentTipus() == EnviamentTipusEnum.NOTIFICACIO ) {
			if (notificacio.getProcedimentCodi() == null) {
				return setRespostaError(messageHelper.getMessage("error.validacio.procediment.codi.no.null"));
			}
		} else if (notificacio.getProcedimentCodi() == null && notificacio.getOrganGestor() == null){
			return setRespostaError(messageHelper.getMessage("error.validacio.organ.gestor.no.null.comunicacio.administracio.sense.procediment"));
		}
//		if (notificacio.getEnviamentTipus() == EnviamentTipusEnum.COMUNICACIO &&  comunicacioSenseAdministracio) {
//			if (notificacio.getProcedimentCodi() == null) {
//				return setRespostaError("[1020] El camp 'procedimentCodi' no pot ser null.");
//			}
//		}

		RespostaAltaV2 resposta = RespostaAltaV2.builder().build();
		// Documents
		if (comunicacioAmbAdministracio) {
			RespostaAltaV2 respostaDoc;
			if (notificacio.getDocument2() != null && !notificacio.getDocument2().isEmpty()) {
				respostaDoc = validaDocumentComunicacioAdmin(notificacio.getDocument2(), 2);
				if (respostaDoc != null)
					return respostaDoc;
			}
			if (notificacio.getDocument3() != null && !notificacio.getDocument3().isEmpty()) {
				respostaDoc = validaDocumentComunicacioAdmin(notificacio.getDocument3(), 3);
				if (respostaDoc != null)
					return respostaDoc;
			}
			if (notificacio.getDocument4() != null && !notificacio.getDocument4().isEmpty()) {
				respostaDoc = validaDocumentComunicacioAdmin(notificacio.getDocument4(), 4);
				if (respostaDoc != null)
					return respostaDoc;
			}
			if (notificacio.getDocument5() != null && !notificacio.getDocument5().isEmpty()) {
				respostaDoc = validaDocumentComunicacioAdmin(notificacio.getDocument5(), 5);
				if (respostaDoc != null)
					return respostaDoc;
			}
			// Apanyo: Posam estat Registrada per a indicar que aquesta és una comunicació amb administració, i per tant va a registre i no a Notifica
			resposta.setErrorDescripcio(COMUNICACIOAMBADMINISTRACIO);
		} else {

			if (document.getContingutBase64() != null && !document.getContingutBase64().isEmpty()) {
				if (!MimeUtils.isFormatValid(document)) {
					return setRespostaError(messageHelper.getMessage("error.validacio.document.format.invalid"));
				}
			}

			// Metadades
			if ((document.getContingutBase64() != null && !document.getContingutBase64().isEmpty()) ||
					(document.getUrl() != null && !document.getUrl().isEmpty())) {
				if (document.getOrigen() == null) {
					document.setOrigen(OrigenEnum.CIUTADA);
//					return setRespostaError("[1066] Error en les metadades del document. No està informat l'ORIGEN del document");
				}
				if (document.getValidesa() == null) {
					document.setValidesa(ValidesaEnum.ORIGINAL);
//					return setRespostaError("[1066] Error en les metadades del document. No està informat la VALIDESA del document");
				}
				if (document.getTipoDocumental() == null) {
					document.setTipoDocumental(TipusDocumentalEnum.NOTIFICACIO);
//					return setRespostaError("[1066] Error en les metadades del document. No està informat el TIPUS DOCUMENTAL del document");
				}
				if (document.getArxiuNom().toUpperCase().endsWith("PDF") && document.getModoFirma() == null) {
					document.setModoFirma(false);
//					return setRespostaError("[1066] Error en les metadades del document. No està informat el MODE de FIRMA del document tipus PDF");
				}
			}
			if (notificacio.getDocument2() != null ||
					notificacio.getDocument3() != null ||
					notificacio.getDocument4() != null ||
					notificacio.getDocument5() != null) {
				return setRespostaError(messageHelper.getMessage("error.validacio.not.coms.ciutada.nomes.un.document"));
			}

		}

		return resposta;
	}

	private RespostaAltaV2 validaDocumentComunicacioAdmin(DocumentV2 document, int numDocument) {

		if (document.getArxiuNom() == null || document.getArxiuNom().isEmpty()) {
			return setRespostaError(messageHelper.getMessage("error.validacio.nom.arxiu.document.no.null")
					+ " " + messageHelper.getMessage("error.validacio.num.document") + numDocument);
		}
		if (document.getArxiuNom() != null && document.getArxiuNom().length() > 200) {
			return setRespostaError(messageHelper.getMessage("error.validacio.arxiu.nom.longitud.max")
					+ " " + messageHelper.getMessage("error.validacio.num.document") + numDocument );
		}
		if ((document.getContingutBase64() == null || document.getContingutBase64().isEmpty()) &&
				(document.getCsv() == null || document.getCsv().isEmpty()) &&
				(document.getUrl() == null || document.getUrl().isEmpty()) &&
				(document.getUuid() == null || document.getUuid().isEmpty())) {
			return setRespostaError(messageHelper.getMessage("error.validacio.document.necessari")
					+ messageHelper.getMessage("error.validacio.num.document") + numDocument);
		}

		// Format
		if (!isComunicacioAdminFormatValid(document.getArxiuNom())) {
			return setRespostaError(messageHelper.getMessage("error.validacio.document.format.invalid.comunicacions.administracio"));
		}
		// Metadades
		if ((document.getContingutBase64() != null && !document.getContingutBase64().isEmpty()) ||
				(document.getUrl() != null && !document.getUrl().isEmpty())) {
			if (document.getOrigen() == null) {
				return setRespostaError(messageHelper.getMessage("error.obtenint.metadades.document.origen.no.informat") + numDocument);
			}
			if (document.getValidesa() == null) {
				return setRespostaError(messageHelper.getMessage("error.obtenint.metadades.document.validesa.no.informat") + numDocument);
			}
			if (document.getTipoDocumental() == null) {
				return setRespostaError(messageHelper.getMessage("error.obtenint.metadades.document.tipus.doc.no.informat") + numDocument);
			}
			if (document.getArxiuNom().toUpperCase().endsWith("PDF") && document.getModoFirma() == null) {
				return setRespostaError(messageHelper.getMessage("error.obtenint.metadades.document.mode.firma.no.informat") + numDocument);
			}
		}
		return null;
	}

	private boolean isComunicacioAdminFormatValid(String arxiuNom) {
		String[] formats = {"jpg", "peg", "odt", "odp", "ods", "odg", "docx", "xlsx", "pptx", "pdf", "png", "rtf", "svg", "giff", "txt", "xml", "xsig", "csig", "html"};
		List<String> formatsValids = new ArrayList<>(Arrays.asList(formats));

		String[] nom_spitted = arxiuNom.split("\\.");
		String extensio = nom_spitted[nom_spitted.length - 1].toLowerCase();
		return formatsValids.contains(extensio);

	}

	private RespostaAltaV2 setRespostaError(String descripcioError) {
		return RespostaAltaV2.builder()
				.error(true)
				.estat(NotificacioEstatEnum.PENDENT)
				.errorDescripcio(descripcioError)
				.errorData(new Date())
				.build();
	}

	private EnviamentEstat toEnviamentEstat(EnviamentEstat estat) {
		if (estat == null) return null;
		switch (estat) {
			case ABSENT:
				return EnviamentEstat.ABSENT;
			case ADRESA_INCORRECTA:
				return EnviamentEstat.ADRESA_INCORRECTA;
			case DESCONEGUT:
				return EnviamentEstat.DESCONEGUT;
			case ENTREGADA_OP:
				return EnviamentEstat.ENTREGADA_OP;
			case ENVIADA_CI:
				return EnviamentEstat.ENVIADA_CI;
			case ENVIADA_DEH:
				return EnviamentEstat.ENVIADA_DEH;
			case ENVIAMENT_PROGRAMAT:
				return EnviamentEstat.ENVIAMENT_PROGRAMAT;
			case ERROR_ENTREGA:
				return EnviamentEstat.ERROR_ENTREGA;
			case EXPIRADA:
				return EnviamentEstat.EXPIRADA;
			case EXTRAVIADA:
				return EnviamentEstat.EXTRAVIADA;
			case LLEGIDA:
				return EnviamentEstat.LLEGIDA;
			case MORT:
				return EnviamentEstat.MORT;
			case NOTIB_ENVIADA:
				return EnviamentEstat.NOTIB_ENVIADA;
			case NOTIB_PENDENT:
				return EnviamentEstat.NOTIB_PENDENT;
			case NOTIFICADA:
				return EnviamentEstat.NOTIFICADA;
			case PENDENT_CIE:
				return EnviamentEstat.PENDENT_CIE;
			case PENDENT_DEH:
				return EnviamentEstat.PENDENT_DEH;
			case PENDENT_ENVIAMENT:
				return EnviamentEstat.PENDENT_ENVIAMENT;
			case PENDENT_SEU:
				return EnviamentEstat.PENDENT_SEU;
			case REBUTJADA:
				return EnviamentEstat.REBUTJADA;
			case SENSE_INFORMACIO:
				return EnviamentEstat.SENSE_INFORMACIO;
			case ENVIAT_SIR:
				return EnviamentEstat.ENVIAT_SIR;
			case ANULADA:
				return EnviamentEstat.ANULADA;
			default:
				return null;
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

	private Date getEstatDate(NotificacioEnviamentEntity enviament) {
		switch (enviament.getNotificacio().getEstat()) {
			case PENDENT:
				return enviament.getCreatedDate().toDate();
			case REGISTRADA:
				return enviament.getRegistreData();
			case PROCESSADA:
				return enviament.getNotificacio().getEstatProcessatDate();
			default:
				return enviament.getNotificaEstatData() != null ? enviament.getNotificaEstatData() : enviament.getNotificacio().getNotificaEnviamentNotificaData();
		}
	}

	private ReceptorInfo getReceptor(NotificacioEnviamentEntity enviament) {
		ReceptorInfo receptor = ReceptorInfo.builder()
				.nif(enviament.getNotificaDatatReceptorNif())
				.nom(enviament.getNotificaDatatReceptorNom())
				.build();

		if (isBlank(receptor.getNif())) {
			if (enviament.getDestinataris() == null || enviament.getDestinataris().isEmpty()) {
				receptor.setNif(enviament.getTitular().getNif());
				receptor.setNom(enviament.getTitular().getNomSencer());
			} else if (!InteressatTipusEnumDto.FISICA.equals(enviament.getTitular().getInteressatTipus()) && enviament.getDestinataris().size() == 1) {
				receptor.setNif(enviament.getDestinataris().get(0).getNif());
				receptor.setNom(enviament.getDestinataris().get(0).getNomSencer());
			}
		}

		return receptor;
	}

	private ArrayList<Character> validFormat(String value) {
		String CONTROL_CARACTERS = " aàáäbcçdeèéëfghiìíïjklmnñoòóöpqrstuùúüvwxyzAÀÁÄBCÇDEÈÉËFGHIÌÍÏJKLMNÑOÒÓÖPQRSTUÙÚÜVWXYZ0123456789-_'\"/:().,¿?!¡;·";
		ArrayList<Character> charsNoValids = new ArrayList<Character>();
		char[] chars = value.replace("\n", "").replace("\r", "").toCharArray();

		boolean esCaracterValid = true;
		for (int i = 0; i < chars.length; i++) {
			esCaracterValid = !(CONTROL_CARACTERS.indexOf(chars[i]) < 0);
			if (!esCaracterValid) {
				charsNoValids.add(chars[i]);
			}
		}
		return charsNoValids;
	}

	private boolean hasSaltLinia(String value) {
		return value.contains("\r") || value.contains("\n") || value.contains("\r\n");
	}

	private StringBuilder listToString(ArrayList<?> list) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			str.append(list.get(i));
		}
		return str;
	}

	public static final Pattern EMAIL_REGEX = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$", Pattern.CASE_INSENSITIVE);

	private boolean isEmailValid(String email) {
		try {
			Matcher matcher = EMAIL_REGEX.matcher(email);
			return matcher.find();
		} catch (Exception e) {
			return false;
		}
	}


	private Boolean isMultipleDestinataris() {

		String property = "es.caib.notib.destinatari.multiple";
		logger.debug("Consulta del valor de la property (property=" + property + ")");
		return configHelper.getAsBoolean(property);
	}

	private Long getMaxSizeFile() {
		return configHelper.getAsLong("es.caib.notib.notificacio.document.size");
	}

	private Long getMaxTotalSizeFile() {
		return configHelper.getAsLong("es.caib.notib.notificacio.document.total.size");
	}

	// Indica si usar valores por defecto cuando ni el documento ni documentV2 tienen metadades
	private boolean getUtilizarValoresPorDefecto() {
		return configHelper.getAsBoolean("es.caib.notib.document.metadades.por.defecto");
	}

	private boolean isValidaFirmaRestEnabled() {
		return configHelper.getAsBoolean("es.caib.notib.plugins.validatesignature.enable.rest");
	}

	private boolean isPermesComunicacionsSirPropiaEntitat() {
		return configHelper.getAsBoolean("es.caib.notib.comunicacions.sir.internes");
	}

	private static final Logger logger = LoggerFactory.getLogger(NotificacioServiceWsImplV2.class);

	@Data
	@Builder
	private static class ReceptorInfo {
		String nif;
		String nom;
	}
}
