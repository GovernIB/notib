/**
 * 
 */
package es.caib.notib.core.service.ws;

import com.codahale.metrics.Timer;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.organisme.OrganismeDto;
import es.caib.notib.core.api.exception.NoMetadadesException;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.api.service.JustificantService;
import es.caib.notib.core.api.ws.notificacio.*;
import es.caib.notib.core.cacheable.OrganGestorCachable;
import es.caib.notib.core.entity.*;
import es.caib.notib.core.helper.*;
import es.caib.notib.core.repository.*;
import es.caib.notib.plugin.registre.RespostaJustificantRecepcio;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Encoder;

import javax.jws.WebService;
import javax.mail.internet.InternetAddress;
import java.io.*;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.util.*;

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

	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private NotificacioRepository notificacioRepository;
	@Autowired
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Autowired
	private ProcedimentRepository procedimentRepository;
	@Autowired
	private ProcedimentOrganRepository procedimentOrganRepository;
	@Autowired
	private PersonaRepository personaRepository;
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private AplicacioRepository aplicacioRepository;
	@Autowired
	private OrganGestorRepository organGestorRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired 
	private PermisosHelper permisosHelper;
	@Autowired 
	private NotificacioEventRepository notificacioEventRepository;
	@Autowired
	private NotificaHelper notificaHelper;
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
	private AuditNotificacioHelper auditNotificacioHelper;
	@Autowired
	private AuditEnviamentHelper auditEnviamentHelper;
	@Autowired
	private OrganGestorCachable organGestorCachable;
	@Autowired
	private NotificacioHelper notificacioHelper;
	@Autowired
	private OrganGestorHelper organGestorHelper;
	@Autowired
	private JustificantService justificantService;

	private static final String COMUNICACIOAMBADMINISTRACIO = "comunicacioAmbAdministracio";
	@Transactional
	@Override
	public RespostaAlta alta(
			NotificacioV2 notificacio) throws NotificacioServiceWsException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("[ALTA] Alta de notificació: " + notificacio.toString());
			
			RespostaAlta resposta = new RespostaAlta();
			ProcedimentEntity procediment = null;
			OrganGestorEntity organGestor = null;
			ProcedimentOrganEntity procedimentOrgan = null;
			
			// Generar informació per al monitor d'integracions
			IntegracioInfo info = generateInfoAlta(notificacio);
			
			// Obtenir dades bàsiques per a la notificació
			String emisorDir3Codi = notificacio.getEmisorDir3Codi();
			logger.debug(">> [ALTA] emisorDir3Codi: " + emisorDir3Codi);
			
			EntitatEntity entitat = entitatRepository.findByDir3Codi(emisorDir3Codi);
			logger.debug(">> [ALTA] entitat: " + (entitat == null ? "null": (entitat.getCodi() + " - " + entitat.getNom())));
			
			String usuariCodi = SecurityContextHolder.getContext().getAuthentication().getName();
			logger.debug(">> [ALTA] usuariCodi: " + usuariCodi);
			
			AplicacioEntity aplicacio = null;
			if (entitat != null && usuariCodi != null)
				aplicacio = aplicacioRepository.findByEntitatIdAndUsuariCodi(entitat.getId(), usuariCodi);
			logger.debug(">> [ALTA] aplicacio: " + (aplicacio == null ? "null" : aplicacio.getUsuariCodi()));
			
			resposta = validarNotificacio(
					notificacio,
					emisorDir3Codi,
					entitat,
					aplicacio);
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
					procediment = procedimentRepository.findByCodiAndEntitat(
							notificacio.getProcedimentCodi(), 
							entitat);

					if (procediment != null) {
						logger.debug(">> [ALTA] procediment: " + procediment.getNom());
						
						// Grups de notificació
						String errorDescripcio = getGrupNotificacio(notificacio, entitat, procediment);
						if (errorDescripcio != null) {
							integracioHelper.addAccioError(info, errorDescripcio);
							return setRespostaError(errorDescripcio);
						}
						
						//Comprovar si no hi ha una caducitat posar una per defecte (dia acutal + dies caducitat procediment)
						// La caducitat únicament és necessària per a notificacions. Per tant tindrà procedimetns
						if (notificacio.getCaducitat() == null) {
							notificacio.setCaducitat(
									CaducitatHelper.sumarDiesLaborals(
											new Date(),
											procediment.getCaducitat()));
						}
						
						// Organ gestor
						if (!procediment.isComu()) { // || (procediment.isComu() && notificacio.getOrganGestor() == null)) { --> Tot procediment comú ha de informa un òrgan gestor
							organGestor = procediment.getOrganGestor();

							if (notificacio.getOrganGestor() != null && !notificacio.getOrganGestor().isEmpty() &&
									organGestor != null && !notificacio.getOrganGestor().equals(organGestor.getCodi())) {
								logger.debug(">> [ALTA] Organ gestor no es correspon amb el de l'procediment");
								errorDescripcio = "[1024] El camp 'organ gestor' no es correspon a l'òrgan gestor de l'procediment.";
								integracioHelper.addAccioError(info, errorDescripcio);
								return setRespostaError(errorDescripcio);
							}
						}
					} else {
						logger.debug(">> [ALTA] Sense procediment");
						String errorDescripcio = "[1330] No s'ha trobat cap procediment amb el codi indicat.";
						integracioHelper.addAccioError(info, errorDescripcio);
						return setRespostaError(errorDescripcio);
					}
				}
				
				// Òrgan gestor
				if (organGestor == null) {
					organGestor = organGestorRepository.findByCodi(notificacio.getOrganGestor());
					if (organGestor == null) {
						Map<String, OrganismeDto> organigramaEntitat = organGestorCachable.findOrganigramaByEntitat(entitat.getDir3Codi());
						if (!organigramaEntitat.containsKey(notificacio.getOrganGestor())) {
							logger.debug(">> [ALTA] Organ gestor desconegut");
							String errorDescripcio = "[1023] El camp 'organ gestor' no es correspon a cap Òrgan Gestor de l'entitat especificada.";
							integracioHelper.addAccioError(info, errorDescripcio);
							return setRespostaError(errorDescripcio);
						}
						organGestorHelper.crearOrganGestor(entitat, notificacio.getOrganGestor());
					}
				}
				if (procediment != null && procediment.isComu() && organGestor != null) {
					procedimentOrgan = procedimentOrganRepository.findByProcedimentIdAndOrganGestorId(procediment.getId(), organGestor.getId());
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
						documentEntity = getDocument(notificacio.getDocument(), document);
						midaTotal = document.getMida();
						numDoc++;
						if (notificacio.getDocument2() != null) {
							document2 = comprovaDocument(notificacio.getDocument2());
							document2Entity = getDocument(notificacio.getDocument2(), document2);
							midaTotal += document2.getMida();
							if (document2.getMida() > getMaxSizeFile()) {
								return setRespostaError("[1065] La longitud del document2 supera el màxim definit (" + getMaxSizeFile() / (1024*1024) + "Mb).");
							}
						}
						numDoc++;
						if (notificacio.getDocument3() != null) {
							document3 = comprovaDocument(notificacio.getDocument3());
							document3Entity = getDocument(notificacio.getDocument3(), document3);
							midaTotal += document3.getMida();
							if (document3.getMida() > getMaxSizeFile()) {
								return setRespostaError("[1065] La longitud del document3 supera el màxim definit (" + getMaxSizeFile() / (1024*1024) + "Mb).");
							}
						}
						numDoc++;
						if (notificacio.getDocument4() != null) {
							document4 = comprovaDocument(notificacio.getDocument4());
							document4Entity = getDocument(notificacio.getDocument4(), document4);
							midaTotal += document4.getMida();
							if (document4.getMida() > getMaxSizeFile()) {
								return setRespostaError("[1065] La longitud del document4 supera el màxim definit (" + getMaxSizeFile() / (1024*1024) + "Mb).");
							}
						}
						numDoc++;
						if (notificacio.getDocument5() != null) {
							document5 = comprovaDocument(notificacio.getDocument5());
							document5Entity = getDocument(notificacio.getDocument5(), document5);
							midaTotal += document5.getMida();
							if (document5.getMida() > getMaxSizeFile()) {
								return setRespostaError("[1065] La longitud del document5 supera el màxim definit (" + getMaxSizeFile() / (1024*1024) + "Mb).");
							}
						}
					} catch (NoMetadadesException me) {
						logger.error("Error al obtenir les metadades del document " + numDoc, me);
						String errorDescripcio = "[1066] No s'han pogut obtenir les metadades del document " + numDoc + ": " + me.getMessage();
						integracioHelper.addAccioError(info, errorDescripcio);
						return setRespostaError(errorDescripcio);
					} catch (Exception e) {
						logger.error("Error al obtenir el document " + numDoc, e);
						String errorDescripcio = "[1064] No s'ha pogut obtenir el document " + numDoc + ": " + e.getMessage();
						integracioHelper.addAccioError(info, errorDescripcio);
						return setRespostaError(errorDescripcio);
					}
					// Mida dels documents
					if (midaTotal > getMaxTotalSizeFile()) {
						return setRespostaError("[1065] La mida màxima del conjunt de documents supera el total màxim (" + getMaxTotalSizeFile() / (1024*1024) + "Mb).");
					}

				} else {
					try {
						document = comprovaDocument(notificacio.getDocument()); //, !comunicacioAmbAdministracio);
						documentEntity = getDocument(notificacio.getDocument(), document);
					} catch (Exception e) {
						logger.error("Error al obtenir el document", e);
						String errorDescripcio = "[1064] No s'ha pogut obtenir el document a notificar: " + e.getMessage();
						integracioHelper.addAccioError(info, errorDescripcio);
						return setRespostaError(errorDescripcio);
					}
//					byte[] base64Decoded = Base64.decodeBase64(notificacio.getDocument().getContingutBase64());
					if (document.getMida() > getMaxSizeFile()) {
						return setRespostaError("[1065] La longitud del document supera el màxim definit (" + getMaxSizeFile() / (1024*1024) + "Mb).");
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
								notificacio.getIdioma())
						.document(documentEntity)
						.document2(document2Entity)
						.document3(document3Entity)
						.document4(document4Entity)
						.document5(document5Entity)
						.build();
				
				NotificacioEntity notificacioGuardada = auditNotificacioHelper.desaNotificacio(notificacioEntity);
				logger.debug(">> [ALTA] notificacio guardada");
				
				// Enviaments
				List<EnviamentReferencia> referencies = new ArrayList<EnviamentReferencia>();
				for (Enviament enviament: notificacio.getEnviaments()) {
					
					// Comprovat titular
					if (enviament.getTitular() == null) {
						String errorDescripcio = "[1110] El camp 'titular' no pot ser null.";
						integracioHelper.addAccioError(info, errorDescripcio);
						logger.debug(">> [ALTA] Titular null");
						return setRespostaError(errorDescripcio);
					}
					
					EnviamentReferencia ref = saveEnviament(
							entitat,
							notificacioGuardada,
							enviament);
					referencies.add(ref);
				}
				logger.debug(">> [ALTA] enviaments creats");

				notificacioGuardada = notificacioRepository.saveAndFlush(notificacioGuardada);

				if (NotificacioComunicacioTipusEnumDto.SINCRON.equals(pluginHelper.getNotibTipusComunicacioDefecte())) {
					logger.info(" [ALTA] Enviament SINCRON notificació [Id: " + notificacioGuardada.getId() + ", Estat: " + notificacioGuardada.getEstat() + "]");
					synchronized(CreacioSemaforDto.getCreacioSemafor()) {
						boolean notificar = registreNotificaHelper.realitzarProcesRegistrar(notificacioGuardada);
						if (notificar)
							notificaHelper.notificacioEnviar(notificacioGuardada.getId());
					}
					
				} else {
					inicialitzaCallbacks(notificacioGuardada);
				}
				
				return generaResposta(info, notificacioGuardada, referencies);
			} catch (Exception ex) {
				logger.error("Error creant notificació", ex);
				integracioHelper.addAccioError(info, "Error creant la notificació", ex);
				throw new RuntimeException(
						"[NOTIFICACIO/COMUNICACIO] Hi ha hagut un error creant la " + notificacio.getEnviamentTipus().name() + ": " + ex.getMessage(),
						ex);
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
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
				ProcedimentEntity procediment = procedimentRepository.findByEntitatAndCodiProcediment(
						entitat,
						permisConsulta.getProcedimentCodi());
				
	
				List<PermisDto> permisos = permisosHelper.findPermisos(
						procediment.getId(),
						ProcedimentEntity.class);
				
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
						permisosHelper.updatePermis(
								procediment.getId(),
								ProcedimentEntity.class,
								permisDto);
					}
				}
				totbe = true;
				integracioHelper.addAccioOk(info);
			} catch (Exception ex) {
				integracioHelper.addAccioError(info, "Error donant permís de consulta", ex);
				throw new RuntimeException(
						"No s'ha pogut assignar el permís a l'usuari: " + permisConsulta.getUsuariCodi(),
						ex);
			}
			return totbe;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	

	@Override
	@Transactional(readOnly = true)
	public RespostaConsultaEstatNotificacio consultaEstatNotificacio(
			String identificador) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			IntegracioInfo info = new IntegracioInfo(
					IntegracioHelper.INTCODI_CLIENT, 
					"Consulta de l'estat d'una notificació", 
					IntegracioAccioTipusEnumDto.RECEPCIO, 
					new AccioParam("Identificador xifrat de la notificacio", identificador));
			
			Long notificacioId;
			RespostaConsultaEstatNotificacio resposta = new RespostaConsultaEstatNotificacio();
	
			try {
				try {
					notificacioId = notificaHelper.desxifrarId(identificador);
					info.getParams().add(new AccioParam("Identificador desxifrat de la notificació", String.valueOf(notificacioId)));
				} catch (GeneralSecurityException ex) {
					resposta.setError(true);
					resposta.setErrorData(new Date());
					resposta.setErrorDescripcio("No s'ha pogut desxifrar l'identificador de la notificació " + identificador);
					integracioHelper.addAccioError(info, "Error al desxifrar l'identificador de la notificació a consultar", ex);
					return resposta;
				}
				NotificacioEntity notificacio = notificacioRepository.findById(notificacioId);
				
				if (notificacio == null) {
					resposta.setError(true);
					resposta.setErrorData(new Date());
					resposta.setErrorDescripcio("Error: No s'ha trobat cap notificació amb l'identificador " + identificador);
					integracioHelper.addAccioError(info, "No existeix cap notificació amb l'identificador especificat");
					return resposta;
				} else {
					switch (notificacio.getEstat()) {
					case PENDENT:
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						break;
					case ENVIADA:
						resposta.setEstat(NotificacioEstatEnum.ENVIADA);
						break;
					case REGISTRADA:
						resposta.setEstat(NotificacioEstatEnum.REGISTRADA);
						break;
					case FINALITZADA:
						resposta.setEstat(NotificacioEstatEnum.FINALITZADA);
						break;
					case PROCESSADA:
						resposta.setEstat(NotificacioEstatEnum.PROCESSADA);
						break;
					}
				}

				NotificacioEventEntity errorEvent = notificacioHelper.getNotificaErrorEvent(notificacio);
				if (errorEvent != null) {
					resposta.setError(true);
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
	@Transactional
	public RespostaConsultaEstatEnviament consultaEstatEnviament(
			String referencia) throws NotificacioServiceWsException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			IntegracioInfo info = new IntegracioInfo(
					IntegracioHelper.INTCODI_CLIENT, 
					"Consulta de l'estat d'un enviament", 
					IntegracioAccioTipusEnumDto.RECEPCIO); 
			
			NotificacioEnviamentEntity enviament = null;
			try {
				Long enviamentId = notificaHelper.desxifrarId(referencia);
				enviament = notificacioEnviamentRepository.findById(enviamentId);
				info.getParams().add(new AccioParam("Identificador xifrat de l'enviament", referencia));
				info.getParams().add(new AccioParam("Identificador desxifrat de l'enviament", String.valueOf(enviamentId)));
			} catch (Exception e) {
				info.getParams().add(new AccioParam("Referència de l'enviament", referencia));
			}
			if (enviament == null)
				enviament = notificacioEnviamentRepository.findByNotificaReferencia(referencia);
			
			RespostaConsultaEstatEnviament resposta = new RespostaConsultaEstatEnviament();
			logger.debug("Consultant estat enviament amb referencia: " + referencia);
			try {
				if (enviament == null) {
					resposta.setError(true);
					resposta.setErrorData(new Date());
					resposta.setErrorDescripcio("Error: No s'ha trobat cap enviament amb la referencia " + referencia);
					integracioHelper.addAccioError(info, "No existeix cap enviament amb l'identificador especificat");
					return resposta;
				} else {
					//Es canosulta l'estat periòdicament, no es necessita realitzar una consulta actica a Notifica
					// Si Notib no utilitza el servei Adviser de @Notifica, i ja ha estat enviat a @Notifica
					// serà necessari consultar l'estat de la notificació a Notifica
					if (	!notificaHelper.isAdviserActiu() &&
							!enviament.isNotificaEstatFinal() &&
							!enviament.getNotificaEstat().equals(NotificacioEnviamentEstatEnumDto.NOTIB_PENDENT)) {
						logger.debug("Consultat estat de l'enviament amb referencia " + referencia + " a Notifica.");
						enviament = notificaHelper.enviamentRefrescarEstat(enviament.getId());
					}
					resposta.setEstat(toEnviamentEstat(enviament.getNotificaEstat()));
					resposta.setEstatData(enviament.getNotificaEstatData());
					resposta.setEstatDescripcio(enviament.getNotificaEstatDescripcio());
					resposta.setReceptorNif(enviament.getNotificaDatatReceptorNif());
					resposta.setReceptorNom(enviament.getNotificaDatatReceptorNom());
					if (enviament.getNotificaCertificacioData() != null) {
						logger.debug("Guardant certificació enviament amb referencia: " + referencia);
						Certificacio certificacio = new Certificacio();
						certificacio.setData(enviament.getNotificaCertificacioData());
						certificacio.setOrigen(enviament.getNotificaCertificacioOrigen());
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						pluginHelper.gestioDocumentalGet(
								enviament.getNotificaCertificacioArxiuId(),
								PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS,
								baos);
						certificacio.setContingutBase64(Base64.encodeBase64String(baos.toByteArray()));
						
						if (enviament.getNotificaCertificacioTamany() != null)
							certificacio.setTamany(enviament.getNotificaCertificacioTamany());
						
						certificacio.setHash(enviament.getNotificaCertificacioHash());
						certificacio.setMetadades(enviament.getNotificaCertificacioMetadades());
						certificacio.setCsv(enviament.getNotificaCertificacioCsv());
						certificacio.setTipusMime(enviament.getNotificaCertificacioMime());
						resposta.setCertificacio(certificacio);
						logger.debug("Certificació de l'enviament amb referencia: " + referencia + " s'ha obtingut correctament.");
					}
					
					if (enviament.getNotificacioErrorEvent() != null) {
						resposta.setError(true);
						NotificacioEventEntity errorEvent = enviament.getNotificacioErrorEvent();
						resposta.setErrorData(errorEvent.getData());
						resposta.setErrorDescripcio(errorEvent.getErrorDescripcio());
						logger.debug("Notifica error de l'enviament amb referencia: " + referencia + ": " + enviament.isNotificaError());
					}
				}
			} catch (Exception ex) {
				logger.debug("Error consultar estat enviament amb referencia: " + referencia, ex);
				integracioHelper.addAccioError(info, "Error al obtenir l'estat de l'enviament", ex);
			}
			integracioHelper.addAccioOk(info);
			return resposta;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public RespostaConsultaDadesRegistre consultaDadesRegistre(DadesConsulta dadesConsulta) {
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
			
			RespostaConsultaDadesRegistre resposta = new RespostaConsultaDadesRegistre();
			if (dadesConsulta.getIdentificador() != null) {
				logger.debug("Consultant les dades de registre de la notificació amb identificador: " + dadesConsulta.getIdentificador());
				int numeroRegistre = 0;
				Long notificacioId;
				try {
					notificacioId = notificaHelper.desxifrarId(dadesConsulta.getIdentificador());
					info.getParams().add(new AccioParam("Identificador desxifrat de la notificació", String.valueOf(notificacioId)));
				} catch (GeneralSecurityException ex) {
					resposta.setError(true);
					resposta.setErrorData(new Date());
					resposta.setErrorDescripcio("No s'ha pogut desxifrar l'identificador de la notificació " + dadesConsulta.getIdentificador());
					integracioHelper.addAccioError(info, "Error al desxifrar l'identificador de la notificació a consultar", ex);
					return resposta;
				}
				NotificacioEntity notificacio = notificacioRepository.findById(notificacioId);
				
				if (notificacio == null) {
					resposta.setError(true);
					resposta.setErrorData(new Date());
					resposta.setErrorDescripcio("Error: No s'ha trobat cap notificació amb l'identificador " + dadesConsulta.getIdentificador());
					integracioHelper.addAccioError(info, "No existeix cap notificació amb l'identificador especificat");
					return resposta;
				} else {
					//Dades registre i consutla justificant
					if (notificacio.getRegistreNumero() != null)
						numeroRegistre = notificacio.getRegistreNumero();
					
					String numeroRegistreFormatat = notificacio.getRegistreNumeroFormatat();
					String codiDir3Entitat = notificacio.getEmisorDir3Codi();
		
					if (numeroRegistreFormatat == null) {
						resposta.setError(true);
						resposta.setErrorData(new Date());
						resposta.setErrorDescripcio("Error: No s'ha trobat cap registre relacionat amb la notificació: " + notificacioId);
						integracioHelper.addAccioError(info, "No hi ha cap registre associat a la notificació");
						return resposta;
					}
		
					resposta.setDataRegistre(notificacio.getRegistreData());
					resposta.setNumRegistre(numeroRegistre);
					resposta.setNumRegistreFormatat(numeroRegistreFormatat);
					if (dadesConsulta.isAmbJustificant()) {
						RespostaJustificantRecepcio justificant = pluginHelper.obtenirJustificant(
								codiDir3Entitat, 
								numeroRegistreFormatat);
						if (justificant.getErrorCodi() == null) {
							resposta.setJustificant(justificant.getJustificant());
						} else {
							resposta.setError(true);
							resposta.setErrorData(new Date());
							String errorDescripcio = justificant.getErrorCodi() + ": " + justificant.getErrorDescripcio();
							resposta.setErrorDescripcio(errorDescripcio);
							integracioHelper.addAccioError(info, errorDescripcio);
							return  resposta;
						}
					}	
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
					integracioHelper.addAccioError(info, "No existeix cap enviament amb la referència especificada");
					return resposta;
				} else {
					//Dades registre i consutla justificant
					String numeroRegistreFormatat = enviament.getRegistreNumeroFormatat();
//					if (enviament.getNotificacio() == null) {
//						NotificacioEntity notificacio = notificacioRepository.findById(enviament.getNotificacioId());
//						enviament.setNotificacio(notificacio);
//					}
					String codiDir3Entitat = enviament.getNotificacio().getEmisorDir3Codi();
					if (numeroRegistreFormatat == null) {
						resposta.setError(true);
						resposta.setErrorData(new Date());
						resposta.setErrorDescripcio("Error: No s'ha trobat cap registre relacionat amb l'enviament: " + enviament.getId());
						integracioHelper.addAccioError(info, "No hi ha cap registre associat a l'enviament");
						return resposta;
					}
		
					resposta.setDataRegistre(enviament.getRegistreData());
					resposta.setNumRegistre(0);
					resposta.setNumRegistreFormatat(numeroRegistreFormatat);
					if (dadesConsulta.isAmbJustificant()) {
						RespostaJustificantRecepcio justificant = pluginHelper.obtenirJustificant(
								codiDir3Entitat, 
								numeroRegistreFormatat);
						if (justificant.getErrorCodi() == null) {
							resposta.setJustificant(justificant.getJustificant());
						} else {
							resposta.setError(true);
							resposta.setErrorData(new Date());
							String errorDescripcio = justificant.getErrorCodi() + ": " + justificant.getErrorDescripcio();
							resposta.setErrorDescripcio(errorDescripcio);
							integracioHelper.addAccioError(info, errorDescripcio);
							return  resposta;
						}
					}	
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
	public RespostaConsultaJustificant consultaJustificantEnviament(
			String identificador) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			IntegracioInfo info = new IntegracioInfo(
					IntegracioHelper.INTCODI_CLIENT,
					"Consulta de la justificació d'una notificació",
					IntegracioAccioTipusEnumDto.RECEPCIO,
					new AccioParam("Identificador xifrat de la notificacio", identificador));

			Long notificacioId;
			RespostaConsultaJustificant resposta = new RespostaConsultaJustificant();

			try {
				try {
					notificacioId = notificaHelper.desxifrarId(identificador);
					info.getParams().add(new AccioParam("Identificador desxifrat de la notificació", String.valueOf(notificacioId)));
				} catch (GeneralSecurityException ex) {
					resposta.setError(true);
					resposta.setErrorData(new Date());
					resposta.setErrorDescripcio("No s'ha pogut desxifrar l'identificador de la notificació " + identificador);
					integracioHelper.addAccioError(info, "Error al desxifrar l'identificador de la notificació a consultar", ex);
					return resposta;
				}
				NotificacioEntity notificacio = notificacioRepository.findById(notificacioId);
				if (notificacio == null) {
					resposta.setError(true);
					resposta.setErrorData(new Date());
					resposta.setErrorDescripcio("Error: No s'ha trobat cap notificació amb l'identificador " + identificador);
					integracioHelper.addAccioError(info, "No existeix cap notificació amb l'identificador especificat");
					return resposta;
				}
			} catch (Exception ex) {
				integracioHelper.addAccioError(info, "Error al obtenir la informació de la notificació", ex);
				throw new RuntimeException(
						"[NOTIFICACIO/COMUNICACIO] Hi ha hagut un error consultant la notificació: " + ex.getMessage(),
						ex);
			}
			ProgresDescarregaDto progres = justificantService.consultaProgresGeneracioJustificant(identificador);
			if (progres != null && progres.getProgres() != null &&  progres.getProgres() < 100) {
				// Ja hi ha un altre procés generant el justificant
				resposta.setError(true);
				resposta.setErrorData(new Date());
				resposta.setErrorDescripcio("Ja hi ha un altre procés generant el justificant de la notificacio");
				return resposta;
			}

			try {
				FitxerDto justificantDto = justificantService.generarJustificantEnviament(notificacioId, identificador);
				if (justificantDto == null || justificantDto.getContingut() == null) {
					resposta.setError(true);
					resposta.setErrorData(new Date());
					resposta.setErrorDescripcio("Error durant la generació del justificant de la notificació");
					return resposta;
				}
				resposta.setJustificant(FitxerBase64EncodedDto.builder()
						.nom(justificantDto.getNom())
						.contentType(justificantDto.getContentType())
						.tamany(justificantDto.getTamany())
						.contingut(new BASE64Encoder().encode(justificantDto.getContingut())).build());
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






















































































































































	private RespostaAlta generaResposta(
			IntegracioInfo info,
			NotificacioEntity notificacioGuardada,
			List<EnviamentReferencia> referencies) {
		RespostaAlta resposta = new RespostaAlta();
		try {
			resposta.setIdentificador(
					notificaHelper.xifrarId(notificacioGuardada.getId()));
			logger.debug(">> [ALTA] identificador creat");
		} catch (GeneralSecurityException ex) {
			logger.debug(">> [ALTA] Error creant identificador");
			throw new RuntimeException(
					"No s'ha pogut crear l'identificador de la notificació",
					ex);
		}
		switch (notificacioGuardada.getEstat()) {
			case PENDENT:
				resposta.setEstat(NotificacioEstatEnum.PENDENT);
				break;
			case ENVIADA:
				resposta.setEstat(NotificacioEstatEnum.ENVIADA);
				break;
			case REGISTRADA:
				resposta.setEstat(NotificacioEstatEnum.REGISTRADA);
				break;
			case FINALITZADA:
				resposta.setEstat(NotificacioEstatEnum.FINALITZADA);
				break;
			case PROCESSADA:
				resposta.setEstat(NotificacioEstatEnum.PROCESSADA);
				break;
			default:
				break;
		}
		NotificacioEventEntity errorEvent = notificacioHelper.getNotificaErrorEvent(notificacioGuardada);
		if (errorEvent != null) {
			logger.debug(">> [ALTA] Event d'error de Notifica!: " + errorEvent.getDescripcio() + " - " + errorEvent.getErrorDescripcio());
			resposta.setError(true);
			resposta.setErrorDescripcio(
					errorEvent.getErrorDescripcio());
		}
		resposta.setReferencies(referencies);
		logger.debug(">> [ALTA] afegides referències");
		integracioHelper.addAccioOk(info);
		return resposta;
	}

	private void inicialitzaCallbacks(NotificacioEntity notificacioGuardada) {
		logger.debug(">> [ALTA] notificació assíncrona");
		List<NotificacioEnviamentEntity> enviamentsEntity = notificacioEnviamentRepository.findByNotificacio(notificacioGuardada);
		for (NotificacioEnviamentEntity enviament : enviamentsEntity) {
			NotificacioEventEntity eventDatat = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.CALLBACK_CLIENT_PENDENT,
					notificacioGuardada).
					enviament(enviament).
					callbackInicialitza().
					build();
			notificacioGuardada.updateEventAfegir(eventDatat);
			notificacioEventRepository.saveAndFlush(eventDatat);
		}
		logger.debug(">> [ALTA] callbacks de client inicialitzats");
	}

	private EnviamentReferencia saveEnviament(
			EntitatEntity entitat,
			NotificacioEntity notificacioGuardada,
			Enviament enviament) {

		ServeiTipusEnumDto serveiTipus = getServeiTipus(enviament);
		NotificaDomiciliNumeracioTipusEnumDto numeracioTipus = null;
		NotificaDomiciliConcretTipusEnumDto tipusConcret = null;
		if (enviament.isEntregaPostalActiva() && enviament.getEntregaPostal() != null) {
			logger.debug(">> [ALTA] Entrega postal");
			tipusConcret = getDomiciliTipusConcret(enviament);
			numeracioTipus = getDomiciliNumeracioTipus(enviament);
		}

		PersonaEntity titular = saveTitular(enviament);
		List<PersonaEntity> destinataris = getDestinataris(enviament);
		EntregaPostalViaTipusEnum viaTipus = getViaTipus(enviament);

		NotificacioEnviamentEntity enviamentSaved = auditEnviamentHelper.desaEnviamentAmbReferencia(
				entitat,
				notificacioGuardada,
				enviament,
				serveiTipus,
				numeracioTipus,
				tipusConcret,
				titular,
				destinataris,
				viaTipus);
		EnviamentReferencia enviamentReferencia = new EnviamentReferencia();
		enviamentReferencia.setReferencia(enviamentSaved.getNotificaReferencia());
		if (titular.getInteressatTipus() != InteressatTipusEnumDto.ADMINISTRACIO)
			enviamentReferencia.setTitularNif(titular.getNif().toUpperCase());
		else
			enviamentReferencia.setTitularNif(titular.getDir3Codi().toUpperCase());
		notificacioGuardada.addEnviament(enviamentSaved);
		return enviamentReferencia;
	}

	private EntregaPostalViaTipusEnum getViaTipus(Enviament enviament) {
		EntregaPostalViaTipusEnum viaTipus = null;

		if (enviament.getEntregaPostal() != null) {
			viaTipus = enviament.getEntregaPostal().getViaTipus();
		}
		return viaTipus;
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

	private NotificaDomiciliNumeracioTipusEnumDto getDomiciliNumeracioTipus(Enviament enviament) {
		NotificaDomiciliNumeracioTipusEnumDto numeracioTipus;
		if (enviament.getEntregaPostal().getNumeroCasa() != null) {
			numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.NUMERO;
		} else if (enviament.getEntregaPostal().getApartatCorreus() != null) {
			numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.APARTAT_CORREUS;
		} else if (enviament.getEntregaPostal().getPuntKm() != null) {
			numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.PUNT_KILOMETRIC;
		} else {
			numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.SENSE_NUMERO;
		}
		return numeracioTipus;
	}

	private NotificaDomiciliConcretTipusEnumDto getDomiciliTipusConcret(
			Enviament enviament) {
		NotificaDomiciliConcretTipusEnumDto tipusConcret = null;
		if (enviament.getEntregaPostal().getTipus() != null) {
			switch (enviament.getEntregaPostal().getTipus()) {
				case APARTAT_CORREUS:
					tipusConcret = NotificaDomiciliConcretTipusEnumDto.APARTAT_CORREUS;
					break;
				case ESTRANGER:
					tipusConcret = NotificaDomiciliConcretTipusEnumDto.ESTRANGER;
					break;
				case NACIONAL:
					tipusConcret = NotificaDomiciliConcretTipusEnumDto.NACIONAL;
					break;
				case SENSE_NORMALITZAR:
					tipusConcret = NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR;
					break;
			}
		} else {
			throw new ValidationException(
					"ENTREGA_POSTAL",
					"L'entrega postal te el camp tipus buit");
		}
		return tipusConcret;
	}

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
			String documentGesdocId = pluginHelper.gestioDocumentalCreate(
					PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
					contingut);
			document.setArxiuGestdocId(documentGesdocId);
			document.setMida(Long.valueOf(contingut.length));
			document.setMediaType(getMimeTypeFromContingut(contingut));
			document.setOrigen(origen);
			document.setValidesa(validesa);
			document.setTipoDocumental(tipoDocumental);
			document.setModoFirma(modoFirma);
			logger.debug(">> [ALTA] documentId: " + documentGesdocId);
		} else if (documentV2.getUuid() != null) {
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
			} else {
				document.setOrigen(origen);
				document.setValidesa(validesa);
				document.setTipoDocumental(tipoDocumental);
				document.setModoFirma(modoFirma);
			}

			// Recuperar csv
			Map<String, Object> metadadesAddicionals = doc.getMetadades().getMetadadesAddicionals();
			if (metadadesAddicionals != null) {
				if (metadadesAddicionals.containsKey("csv"))
					document.setCsv((String) metadadesAddicionals.get("csv"));
				else if (metadadesAddicionals.containsKey("eni:csv"))
					document.setCsv((String) metadadesAddicionals.get("eni:csv"));
			}

		} else if (documentV2.getCsv() != null) {
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

		} else if (documentV2.getUrl() != null) {
			String arxiuUrl = documentV2.getUrl();
			logger.debug(">> [ALTA] documentUrl: " + arxiuUrl);
			byte[] contingut = pluginHelper.getUrlDocumentContent(arxiuUrl);
			document.setMida(Long.valueOf(contingut.length));
			document.setMediaType(getMimeTypeFromContingut(contingut));
			document.setOrigen(origen);
			document.setValidesa(validesa);
			document.setTipoDocumental(tipoDocumental);
			document.setModoFirma(modoFirma);
		}
		return document;
	}

	private String getMimeTypeFromContingut(byte[] contingut) {
		String mimeType = null;

		try {
			InputStream is = new BufferedInputStream(new ByteArrayInputStream(contingut));
			mimeType = URLConnection.guessContentTypeFromStream(is);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return mimeType;
	}

	private String getGrupNotificacio(NotificacioV2 notificacio, EntitatEntity entitat, ProcedimentEntity procediment) {
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
		String json = "S'ha produït un error al intentar llegir la informació de la notificació";
		ObjectMapper mapper  = new ObjectMapper();
		try {
			json = mapper.writeValueAsString(notificacio);
		} catch (Exception e) { }

		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_CLIENT,
				"Alta de notificació",
				IntegracioAccioTipusEnumDto.RECEPCIO,
				new AccioParam("Notificacio", json));
		return info;
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
	// 1070 | El camp 'usuariCodi' no pot ser null (Requisit per fer el registre de sortida)
	// 1071 | El camp 'usuariCodi' no pot pot tenir una longitud superior a 64 caràcters
	// 1072 | El camp 'arxiuNom' no pot pot tenir una longitud superior a 200 caràcters."
	// 1080 | El camp 'numExpedient' no pot pot tenir una longitud superior a 256 caràcters
	// 1090 | El camp 'grupCodi' no pot pot tenir una longitud superior a 64 caràcters
	// 1100 | El camp 'enviaments' no pot ser null
	// 1101 | El camp 'serveiTipus' d'un enviament no pot ser null
	// 1110 | El titular d'un enviament no pot ser null
	// 1111 | El camp 'interessat_tipus' del titular d'un enviament no pot ser null
	// 1112 | El camp 'nom' del titular no pot ser tenir una longitud superior a 255 caràcters
	// 1113 | El camp 'llinatge1' del titular no pot ser major que 40 caràcters
	// 1114 | El camp 'llinatge2' del titular no pot ser major que 40 caràcters
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
	// 1171 | El camp 'nom' del titular no pot tenir una longitud superior a 255 caràcters
	// 1172 | El camp 'llinatge1' del destinatari no pot ser major que 40 caràcters
	// 1173 | El camp 'llinatge2' del destinatari no pot ser major que 40 caràcters
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
	protected RespostaAlta validarNotificacio(
			NotificacioV2 notificacio,
			String emisorDir3Codi,
			EntitatEntity entitat,
			AplicacioEntity aplicacio) {
		RespostaAlta resposta = new RespostaAlta();
		boolean comunicacioSenseAdministracio = false;
		boolean comunicacioAmbAdministracio = false;
		Map<String, OrganismeDto> organigramaByEntitat = null;

		if (notificacio.getEnviamentTipus() == EnviamentTipusEnum.COMUNICACIO) {
			organigramaByEntitat = organGestorCachable.findOrganigramaByEntitat(emisorDir3Codi);
		}

		// Emisor
		if (emisorDir3Codi == null || emisorDir3Codi.isEmpty()) {
			return setRespostaError("[1000] El camp 'emisorDir3Codi' no pot ser null.");
		} 
		if (emisorDir3Codi.length() > 9) {
			return setRespostaError("[1001] El camp 'emisorDir3Codi' no pot tenir una longitud superior a 9 caràcters.");
		}
		// Entitat
		if (entitat == null) {
			return setRespostaError("[1010] No s'ha trobat cap entitat configurada a Notib amb el codi Dir3 " + emisorDir3Codi + ". (emisorDir3Codi)");
		}
		if (!entitat.isActiva()) {
			return setRespostaError("[1011] L'entitat especificada està desactivada per a l'enviament de notificacions");
		}
		// Aplicacio
		if (aplicacio == null) {
			return setRespostaError("[1012] L'usuari d'aplicació no està assignat a l'entitat amb codi Dir3 " + emisorDir3Codi);
		}
		// Procediment
		if (notificacio.getProcedimentCodi() != null && notificacio.getProcedimentCodi().length() > 9) {
			return setRespostaError("[1021] El camp 'procedimentCodi' no pot tenir una longitud superior a 9 caràcters.");
		}
		// Concepte
		if (notificacio.getConcepte() == null || notificacio.getConcepte().isEmpty()) {
			return setRespostaError("[1030] El concepte de la notificació no pot ser null.");
		}
		if (notificacio.getConcepte().length() > 240) {
			return setRespostaError("[1031] El concepte de la notificació no pot tenir una longitud superior a 240 caràcters.");
		}
		if (!validFormat(notificacio.getConcepte()).isEmpty()) {
			return setRespostaError("[1032] El format del camp concepte no és correcte. Inclou els caràcters ("+ listToString(validFormat(notificacio.getConcepte())) +") que no són correctes");
		}
		// Descripcio
		if (notificacio.getDescripcio() != null && notificacio.getDescripcio().length() > 1000){
			return setRespostaError("[1040] La descripció de la notificació no pot contenir més de 1000 caràcters.");
		}
		if (notificacio.getDescripcio() != null && !validFormat(notificacio.getDescripcio()).isEmpty()) {
			return setRespostaError("[1041] El format del camp descripció no és correcte. Inclou els caràcters ("+ listToString(validFormat(notificacio.getDescripcio())) +") que no són correctes");
		}
		if (notificacio.getDescripcio() != null && hasSaltLinia(notificacio.getDescripcio())) {
			return setRespostaError("[1042] Els salts de línia no estan permesos al camp descripció.");
		}
		// Tipus d'enviament
		if (notificacio.getEnviamentTipus() == null) {
			return setRespostaError("[1050] El tipus d'enviament de la notificació no pot ser null.");
		}
		// Document
		if (notificacio.getDocument() == null) {
			return setRespostaError("[1060] El camp 'document' no pot ser null.");
		}
		DocumentV2 document = notificacio.getDocument();
		//TODO: Revisar la validación del nom. Para CSV/UUid en el formulario web
		//NO se pide un nombre; se recupera posteriormente del plugin.
		if (document.getArxiuNom() == null || document.getArxiuNom().isEmpty()) {
			return setRespostaError("[1061] El camp 'arxiuNom' del document no pot ser null.");
		}
		if (document.getArxiuNom() != null && document.getArxiuNom().length() > 200) {
			return setRespostaError("[1072] El camp 'arxiuNom' no pot pot tenir una longitud superior a 200 caràcters.");
		}
		if (	(document.getContingutBase64() == null || document.getContingutBase64().isEmpty()) &&
				(document.getCsv() == null || document.getCsv().isEmpty()) &&
				(document.getUrl() == null || document.getUrl().isEmpty()) &&
				(document.getUuid() == null || document.getUuid().isEmpty())) {
			return setRespostaError("[1062] És necessari incloure un document (contingutBase64, CSV, UUID o URL) a la notificació.");
		}
		// Usuari
		if (notificacio.getUsuariCodi() == null || notificacio.getUsuariCodi().isEmpty()) {
			return setRespostaError("[1070] El camp 'usuariCodi' no pot ser null (Requisit per fer el registre de sortida).");
		} 
		if (notificacio.getUsuariCodi().length() > 64) {
			return setRespostaError("[1071] El camp 'usuariCodi' no pot pot tenir una longitud superior a 64 caràcters.");
		}
		// Número d'expedient
		if (notificacio.getNumExpedient() != null && notificacio.getNumExpedient().length() > 80) {
			return setRespostaError("[1080] El camp 'numExpedient' no pot pot tenir una longitud superior a 80 caràcters.");
		}
		// GrupCodi
		if (notificacio.getGrupCodi() != null && notificacio.getGrupCodi().length() > 64) {
			return setRespostaError("[1090] El camp 'grupCodi' no pot pot tenir una longitud superior a 64 caràcters.");
		}
		// Enviaments
		if (notificacio.getEnviaments() == null || notificacio.getEnviaments().isEmpty()) {
			return setRespostaError("[1100] El camp 'enviaments' no pot ser null.");
		}
		for(Enviament enviament : notificacio.getEnviaments()) {
			//Si és comunicació a administració i altres mitjans (persona física/jurídica) --> Excepció
			if (notificacio.getEnviamentTipus() == EnviamentTipusEnum.COMUNICACIO) {
				if (enviament.getTitular().getInteressatTipus() == InteressatTipusEnumDto.ADMINISTRACIO) {
					comunicacioAmbAdministracio = true;
				}
				if ((enviament.getTitular().getInteressatTipus() == InteressatTipusEnumDto.FISICA) || (enviament.getTitular().getInteressatTipus() == InteressatTipusEnumDto.JURIDICA))  {
					comunicacioSenseAdministracio = true;
				}
			}
			boolean senseNif = true;
			
			// Servei tipus
			if(enviament.getServeiTipus() == null) {
				return setRespostaError("[1101] El camp 'serveiTipus' d'un enviament no pot ser null.");
			}
			
			// Titular
			if(enviament.getTitular() == null) {
				return setRespostaError("[1110] El titular d'un enviament no pot ser null.");
			}
			// - Tipus
			if(enviament.getTitular().getInteressatTipus() == null) {
				return setRespostaError("[1111] El camp 'interessat_tipus' del titular d'un enviament no pot ser null.");
			}
			// - Nom
			if(enviament.getTitular().getNom() != null && enviament.getTitular().getNom().length() > 255) {
				return setRespostaError("[1112] El camp 'nom' del titular no pot ser tenir una longitud superior a 255 caràcters.");
			}
			// - Llinatge 1
			if (enviament.getTitular().getLlinatge1() != null && enviament.getTitular().getLlinatge1().length() > 40) {
				return setRespostaError("[1113] El camp 'llinatge1' del titular no pot ser major que 40 caràcters.");
			}
			// - Llinatge 2
			if (enviament.getTitular().getLlinatge2() != null && enviament.getTitular().getLlinatge2().length() > 40) {
				return setRespostaError("[1114] El camp 'llinatge2' del titular no pot ser major que 40 caràcters.");
			}
			// - Nif
			if(enviament.getTitular().getNif() != null && enviament.getTitular().getNif().length() > 9) {
				return setRespostaError("[1115] El camp 'nif' del titular d'un enviament no pot tenir una longitud superior a 9 caràcters.");
			}
			if (enviament.getTitular().getNif() != null && !enviament.getTitular().getNif().isEmpty()) {
				if (NifHelper.isvalid(enviament.getTitular().getNif())) {
					senseNif = false;
				} else {
					return setRespostaError("[1116] El 'nif' del titular no és vàlid.");
				}
				switch (enviament.getTitular().getInteressatTipus()) {
					case FISICA:
						if (!NifHelper.isValidNifNie(enviament.getTitular().getNif())) {
							return setRespostaError("[1123] El camp 'nif' del titular no és un tipus de document vàlid per a persona física. Només s'admet NIF/NIE.");
						}
						break;
					case JURIDICA:
						if (!NifHelper.isValidCif(enviament.getTitular().getNif())) {
							return setRespostaError("[1123] El camp 'nif' del titular no és un tipus de document vàlid per a persona jurídica. Només s'admet CIF.");
						}
						break;
					case ADMINISTRACIO:
						break;
				}
			}
			// - Email
			if (enviament.getTitular().getEmail() != null && enviament.getTitular().getEmail().length() > 160) {
				return setRespostaError("[1117] El camp 'email' del titular no pot ser major que 160 caràcters.");
			}
			if (enviament.getTitular().getEmail() != null && !isEmailValid(enviament.getTitular().getEmail())) {
				return setRespostaError("[1118] El format del camp 'email' del titular no és correcte");
			}
			// - Telèfon
			if (enviament.getTitular().getTelefon() != null && enviament.getTitular().getTelefon().length() > 16) {
				return setRespostaError("[1119] El camp 'telefon' del titular no pot ser major que 16 caràcters.");
			}
			// - Raó social
			if (enviament.getTitular().getRaoSocial() != null && enviament.getTitular().getRaoSocial().length() > 80) {
				return setRespostaError("[1120] El camp 'raoSocial' del titular no pot ser major que 80 caràcters.");
			}
			// - Codi Dir3
			if (enviament.getTitular().getDir3Codi() != null && enviament.getTitular().getDir3Codi().length() > 9) {
				return setRespostaError("[1121] El camp 'dir3Codi' del titular no pot ser major que 9 caràcters.");
			}
			// - Incapacitat
			if (enviament.getTitular().isIncapacitat() && (enviament.getDestinataris() == null || enviament.getDestinataris().isEmpty())) {
				return setRespostaError("[1122] En cas de titular amb incapacitat es obligatori indicar un destinatari.");
			}
			//   - Persona física
			if(enviament.getTitular().getInteressatTipus().equals(InteressatTipusEnumDto.FISICA)) {
				if(enviament.getTitular().getNom() == null || enviament.getTitular().getNom().isEmpty()) {
					return setRespostaError("[1130] El camp 'nom' de la persona física titular no pot ser null.");
				}
				if (enviament.getTitular().getLlinatge1() == null || enviament.getTitular().getLlinatge1().isEmpty()) {
					return setRespostaError("[1131] El camp 'llinatge1' de la persona física titular d'un enviament no pot ser null en el cas de persones físiques.");
				}
				if(enviament.getTitular().getNif() == null || enviament.getTitular().getNif().isEmpty()) {
					return setRespostaError("[1132] El camp 'nif' de la persona física titular d'un enviament no pot ser null.");
				}
			//   - Persona jurídica
			} else if(enviament.getTitular().getInteressatTipus().equals(InteressatTipusEnumDto.JURIDICA)) {
				if((enviament.getTitular().getRaoSocial() == null || enviament.getTitular().getRaoSocial().isEmpty()) && (enviament.getTitular().getNom() == null || enviament.getTitular().getNom().isEmpty())) {
					return setRespostaError("[1140] El camp 'raoSocial/nom' de la persona jurídica titular d'un enviament no pot ser null.");
				}
				if(enviament.getTitular().getNif() == null || enviament.getTitular().getNif().isEmpty()) {
					return setRespostaError("[1141] El camp 'nif' de la persona jurídica titular d'un enviament no pot ser null.");
				}
			//   - Administració
			} else if(enviament.getTitular().getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO)) {
				if(enviament.getTitular().getNom() == null || enviament.getTitular().getNom().isEmpty()) {
					return setRespostaError("[1150] El camp 'nom' de l'administració titular d'un enviament no pot ser null.");
				}
				if(enviament.getTitular().getDir3Codi() == null) {
					return setRespostaError("[1151] El camp 'dir3codi' de l'administració titular d'un enviament no pot ser null.");
				}
				OrganGestorDto organDir3 = cacheHelper.unitatPerCodi(enviament.getTitular().getDir3Codi());
				if (organDir3 == null) {
					return setRespostaError("[1152] El camp 'dir3codi' (" + enviament.getTitular().getDir3Codi() + ") no es correspon a un codi Dir3 vàlid.");
				}
				if (notificacio.getEnviamentTipus() == EnviamentTipusEnum.COMUNICACIO) {
					if (organDir3.getSir() == null || !organDir3.getSir()) {
						return setRespostaError("[1153] El camp 'dir3codi' (" + enviament.getTitular().getDir3Codi() + ") no disposa d'oficina SIR. És obligatori per a comunicacions.");
					}
					if (organigramaByEntitat.containsKey(enviament.getTitular().getDir3Codi())) {
						return setRespostaError("[1154] El camp 'dir3codi' (" + enviament.getTitular().getDir3Codi() + ") fa referència a una administració de la pròpia entitat. No es pot utilitzar Notib per enviar comunicacions dins la pròpia entitat.");
					}
				}
				if (enviament.getTitular().getNif() == null || enviament.getTitular().getNif().isEmpty()) {
					enviament.getTitular().setNif(organDir3.getCif());
				}
			}
			
			// Destinataris
			if (!isMultipleDestinataris() && enviament.getDestinataris() != null && enviament.getDestinataris().size() > 1) {
				return setRespostaError("[1160] El numero de destinatais està limitat a un destinatari.");
			}
			if (enviament.getDestinataris() != null) {
				// Destinatari
				for(Persona destinatari : enviament.getDestinataris()) {
					if(destinatari.getInteressatTipus() == null) {
						return setRespostaError("[1170] El camp 'interessat_tipus' del destinatari d'un enviament no pot ser null.");
					}
					// - Nom
					if(destinatari.getNom() != null && destinatari.getNom().length() > 255) {
						return setRespostaError("[1171] El camp 'nom' del titular no pot tenir una longitud superior a 255 caràcters.");
					}
					// - Llinatge 1
					if (destinatari.getLlinatge1() != null && destinatari.getLlinatge1().length() > 40) {
						return setRespostaError("[1172] El camp 'llinatge1' del destinatari no pot ser major que 40 caràcters.");
					}
					// - Llinatge 2
					if (destinatari.getLlinatge2() != null && destinatari.getLlinatge2().length() > 40) {
						return setRespostaError("[1173] El camp 'llinatge2' del destinatari no pot ser major que 40 caràcters.");
					}
					// - Nif
					if(destinatari.getNif() != null && destinatari.getNif().length() > 9) {
						return setRespostaError("[1174] El camp 'nif' del destinatari d'un enviament no pot tenir una longitud superior a 9 caràcters.");
					}
					if (destinatari.getNif() != null && !destinatari.getNif().isEmpty()) {
						if (NifHelper.isvalid(destinatari.getNif())) {
							senseNif = false;
						} else {
							return setRespostaError("[1175] El 'nif' del destinatari no és vàlid.");
						}
						switch (destinatari.getInteressatTipus()) {
						case FISICA:
							if (!NifHelper.isValidNifNie(destinatari.getNif())) {
								return setRespostaError("[1181] El camp 'nif' del destinatari no és un tipus de document vàlid per a persona física. Només s'admet NIF/NIE.");
							}
							break;
						case JURIDICA:
							if (!NifHelper.isValidCif(destinatari.getNif())) {
								return setRespostaError("[1181] El camp 'nif' del destinatari no és un tipus de document vàlid per a persona jurídica. Només s'admet CIF.");
							}
							break;
						case ADMINISTRACIO:
							break;
					}
					}
					// - Email
					if (destinatari.getEmail() != null && destinatari.getEmail().length() > 160) {
						return setRespostaError("[1176] El camp 'email' del destinatari no pot ser major que 160 caràcters.");
					}
					if (destinatari.getEmail() != null && !isEmailValid(destinatari.getEmail())) {
						return setRespostaError("[1177] El format del camp 'email' del destinatari no és correcte");
					}
					// - Telèfon
					if (destinatari.getTelefon() != null && destinatari.getTelefon().length() > 16) {
						return setRespostaError("[1178] El camp 'telefon' del destinatari no pot ser major que 16 caràcters.");
					}
					// - Raó social
					if (destinatari.getRaoSocial() != null && destinatari.getRaoSocial().length() > 80) {
						return setRespostaError("[1179] El camp 'raoSocial' del destinatari no pot ser major que 80 caràcters.");
					}
					// - Codi Dir3
					if (destinatari.getDir3Codi() != null && destinatari.getDir3Codi().length() > 9) {
						return setRespostaError("[1180] El camp 'dir3Codi' del destinatari no pot ser major que 9 caràcters.");
					}
					
					if(destinatari.getInteressatTipus().equals(InteressatTipusEnumDto.FISICA)) {
						if(destinatari.getNom() == null || destinatari.getNom().isEmpty()) {
							return setRespostaError("[1190] El camp 'nom' de la persona física destinatària d'un enviament no pot ser null.");
						}
						if (destinatari.getLlinatge1() == null) {
							return setRespostaError("[1191] El camp 'llinatge1' del destinatari d'un enviament no pot ser null en el cas de persones físiques.");
						}
						if(destinatari.getNif() == null) {
							return setRespostaError("[1192] El camp 'nif' de la persona física destinatària d'un enviament no pot ser null.");
						}
					} else if(destinatari.getInteressatTipus().equals(InteressatTipusEnumDto.JURIDICA)) {
						if((destinatari.getRaoSocial() == null || destinatari.getRaoSocial().isEmpty()) && (destinatari.getNom() == null || destinatari.getNom().isEmpty())) {
							return setRespostaError("[1200] El camp 'raoSocial/nom' de la persona jurídica destinatària d'un enviament no pot ser null.");
						}
						if(destinatari.getNif() == null) {
							return setRespostaError("[1201] El camp 'nif' de la persona jurídica destinatària d'un enviament no pot ser null.");
						}
					} else if(destinatari.getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO)) {
						if(destinatari.getNom() == null || destinatari.getNom().isEmpty()) {
							return setRespostaError("[1210] El camp 'nom' de l'administració destinatària d'un enviament no pot ser null.");
						}
						if(destinatari.getDir3Codi() == null) {
							return setRespostaError("[1211] El camp 'dir3codi' de l'administració destinatària d'un enviament no pot ser null.");
						}
						OrganGestorDto organDir3 = cacheHelper.unitatPerCodi(destinatari.getDir3Codi());
						if (organDir3 == null) {
							return setRespostaError("[1212] El camp 'dir3codi' (" + destinatari.getDir3Codi() + ") de l'administració destinatària d'un enviament no es correspon a un codi Dir3 vàlid.");
						}
						if (notificacio.getEnviamentTipus() == EnviamentTipusEnum.COMUNICACIO) {
							if (organDir3.getSir() == null || !organDir3.getSir()) {
								return setRespostaError("[1213] El camp 'dir3codi' (" + destinatari.getDir3Codi() + ") de l'administració destinatària d'un enviament no disposa d'oficina SIR. És obligatori per a comunicacions.");
							}
							if (organigramaByEntitat.containsKey(destinatari.getDir3Codi())) {
								return setRespostaError("[1214] El camp 'dir3codi' (" + destinatari.getDir3Codi() + ") de l'administració destinatària d'un enviament fa referència a una administració de la pròpia entitat. No es pot utilitzar Notib per enviar comunicacions dins la pròpia entitat.");
							}
						}
						if (destinatari.getNif() == null || destinatari.getNif().isEmpty()) {
							destinatari.setNif(organDir3.getCif());
						}
					}
					
				}
			}
			if (notificacio.getEnviamentTipus() == EnviamentTipusEnum.NOTIFICACIO && senseNif) {
				return setRespostaError("[1220] En una notificació, com a mínim un dels interessats ha de tenir el Nif informat.");
			}

			// Entrega postal
			if(enviament.isEntregaPostalActiva()){
				if (enviament.getEntregaPostal().getTipus() == null) {
					return setRespostaError("[1230] El camp 'entregaPostalTipus' no pot ser null.");
				}
				if(enviament.getEntregaPostal().getCodiPostal() == null || enviament.getEntregaPostal().getCodiPostal().isEmpty()) {
					return setRespostaError("[1231] El camp 'codiPostal' no pot ser null (indicar 00000 en cas de no disposar del codi postal).");
				}

				if (enviament.getEntregaPostal().getViaNom() != null && enviament.getEntregaPostal().getViaNom().length() > 50) {
					return setRespostaError("[1232] El camp 'viaNom' de l'entrega postal no pot contenir més de 50 caràcters.");
				}
				if (enviament.getEntregaPostal().getNumeroCasa() != null && enviament.getEntregaPostal().getNumeroCasa().length() > 5) {
					return setRespostaError("[1233] El camp 'numeroCasa' de l'entrega postal no pot contenir més de 5 caràcters.");
				}
				if (enviament.getEntregaPostal().getPuntKm() != null && enviament.getEntregaPostal().getPuntKm().length() > 5) {
					return setRespostaError("[1234] El camp 'puntKm' de l'entrega postal no pot contenir més de 5 caràcters.");
				}
				if (enviament.getEntregaPostal().getPortal() != null && enviament.getEntregaPostal().getPortal().length() > 3) {
					return setRespostaError("[1235] El camp 'portal' de l'entrega postal no pot contenir més de 3 caràcters.");
				}
				if (enviament.getEntregaPostal().getPorta() != null && enviament.getEntregaPostal().getPorta().length() > 3) {
					return setRespostaError("[1236] El camp 'porta' de l'entrega postal no pot contenir més de 3 caràcters.");
				}
				if (enviament.getEntregaPostal().getEscala() != null && enviament.getEntregaPostal().getEscala().length() > 3) {
					return setRespostaError("[1237] El camp 'escala' de l'entrega postal no pot contenir més de 3 caràcters.");
				}
				if (enviament.getEntregaPostal().getPlanta() != null && enviament.getEntregaPostal().getPlanta().length() > 3) {
					return setRespostaError("[1238] El camp 'planta' de l'entrega postal no pot contenir més de 3 caràcters.");
				}
				if (enviament.getEntregaPostal().getBloc() != null && enviament.getEntregaPostal().getBloc().length() > 3) {
					return setRespostaError("[1239] El camp 'bloc' de l'entrega postal no pot contenir més de 3 caràcters.");
				}
				if (enviament.getEntregaPostal().getComplement() != null && enviament.getEntregaPostal().getComplement().length() > 40) {
					return setRespostaError("[1240] El camp 'complement' de l'entrega postal no pot contenir més de 40 caràcters.");
				}
				if (enviament.getEntregaPostal().getNumeroQualificador() != null && enviament.getEntregaPostal().getNumeroQualificador().length() > 3) {
					return setRespostaError("[1241] El camp 'numeroQualificador' de l'entrega postal no pot contenir més de 3 caràcters.");
				}
				if(enviament.getEntregaPostal().getCodiPostal() != null && enviament.getEntregaPostal().getCodiPostal().length() > 10) {
					return setRespostaError("[1242] El camp 'codiPostal' no pot contenir més de 10 caràcters).");
				}
				if(enviament.getEntregaPostal().getApartatCorreus() != null && enviament.getEntregaPostal().getApartatCorreus().length() > 10) {
					return setRespostaError("[1243] El camp 'apartatCorreus' no pot contenir més de 10 caràcters).");
				}
				if (enviament.getEntregaPostal().getMunicipiCodi() != null && enviament.getEntregaPostal().getMunicipiCodi().length() > 6) {
					return setRespostaError("[1244] El camp 'municipiCodi' de l'entrega postal no pot contenir més de 6 caràcters.");
				}
				if (enviament.getEntregaPostal().getProvincia() != null && enviament.getEntregaPostal().getProvincia().length() > 2) {
					return setRespostaError("[1245] El camp 'provincia' de l'entrega postal no pot contenir més de 2 caràcters.");
				}
				if (enviament.getEntregaPostal().getPaisCodi() != null && enviament.getEntregaPostal().getPaisCodi().length() > 2) {
					return setRespostaError("[1246] El camp 'paisCodi' de l'entrega postal no pot contenir més de 2 caràcters.");
				}
				if (enviament.getEntregaPostal().getPoblacio() != null && enviament.getEntregaPostal().getPoblacio().length() > 255) {
					return setRespostaError("[1247] El camp 'poblacio' de l'entrega postal no pot contenir més de 255 caràcters.");
				}
				if (enviament.getEntregaPostal().getLinea1() != null && enviament.getEntregaPostal().getLinea1().length() > 50) {
					return setRespostaError("[1248] El camp 'linea1' de l'entrega postal no pot contenir més de 50 caràcters.");
				}
				if (enviament.getEntregaPostal().getLinea2() != null && enviament.getEntregaPostal().getLinea2().length() > 50) {
					return setRespostaError("[1249] El camp 'linea2' de l'entrega postal no pot contenir més de 50 caràcters.");
				}
				if(enviament.getEntregaPostal().getTipus().equals(NotificaDomiciliConcretTipusEnumDto.NACIONAL)) {
					if (enviament.getEntregaPostal().getViaTipus() == null) {
						return setRespostaError("[1260] El camp 'viaTipus' no pot ser null en cas d'entrega NACIONAL NORMALITZAT.");
					}
					if (enviament.getEntregaPostal().getViaNom() == null || enviament.getEntregaPostal().getViaNom().isEmpty()) {
						return setRespostaError("[1261] El camp 'viaNom' no pot ser null en cas d'entrega NACIONAL NORMALITZAT.");
					}
					if (enviament.getEntregaPostal().getPuntKm() == null && enviament.getEntregaPostal().getNumeroCasa() == null) {
						return setRespostaError("[1262] S'ha d'indicar almenys un 'numeroCasa' o 'puntKm'");
					}
					if (enviament.getEntregaPostal().getMunicipiCodi() == null || enviament.getEntregaPostal().getMunicipiCodi().isEmpty()) {
						return setRespostaError("[1263] El camp 'municipiCodi' no pot ser null en cas d'entrega NACIONAL NORMALITZAT.");
					}
					if (enviament.getEntregaPostal().getProvincia() == null || enviament.getEntregaPostal().getProvincia().isEmpty()) {
						return setRespostaError("[1264] El camp 'provincia' no pot ser null en cas d'entrega NACIONAL NORMALITZAT.");
					}
					if (enviament.getEntregaPostal().getPoblacio() == null || enviament.getEntregaPostal().getPoblacio().isEmpty()) {
						return setRespostaError("[1265] El camp 'poblacio' no pot ser null en cas d'entrega NACIONAL NORMALITZAT.");
					}
				}
				if(enviament.getEntregaPostal().getTipus().equals(NotificaDomiciliConcretTipusEnumDto.ESTRANGER)) {
					if (enviament.getEntregaPostal().getViaNom() == null || enviament.getEntregaPostal().getViaNom().isEmpty()) {
						return setRespostaError("[1270] El camp 'viaNom' no pot ser null en cas d'entrega ESTRANGER NORMALITZAT.");
					}
					if (enviament.getEntregaPostal().getPaisCodi() == null || enviament.getEntregaPostal().getPaisCodi().isEmpty()) {
						return setRespostaError("[1271] El camp 'paisCodi' no pot ser null en cas d'entrega ESTRANGER NORMALITZAT.");
					}
					if (enviament.getEntregaPostal().getPoblacio() == null || enviament.getEntregaPostal().getPoblacio().isEmpty()) {
						return setRespostaError("[1272] El camp 'poblacio' no pot ser null en cas d'entrega ESTRANGER NORMALITZAT.");
					}
				}
				if(enviament.getEntregaPostal().getTipus().equals(NotificaDomiciliConcretTipusEnumDto.APARTAT_CORREUS)) {
					if (enviament.getEntregaPostal().getApartatCorreus() == null || enviament.getEntregaPostal().getApartatCorreus().isEmpty()) {
						return setRespostaError("[1280] El camp 'apartatCorreus' no pot ser null en cas d'entrega APARTAT CORREUS.");
					}
					if (enviament.getEntregaPostal().getMunicipiCodi() == null || enviament.getEntregaPostal().getMunicipiCodi().isEmpty()) {
						return setRespostaError("[1281] El camp 'municipiCodi' no pot ser null en cas d'entrega APARTAT CORREUS.");
					}
					if (enviament.getEntregaPostal().getProvincia() == null || enviament.getEntregaPostal().getProvincia().isEmpty()) {
						return setRespostaError("[1282] El camp 'provincia' no pot ser null en cas d'entrega APARTAT CORREUS.");
					}
					if (enviament.getEntregaPostal().getPoblacio() == null || enviament.getEntregaPostal().getPoblacio().isEmpty()) {
						return setRespostaError("[1283] El camp 'poblacio' no pot ser null en cas d'entrega APARTAT CORREUS.");
					}
				}
				if(enviament.getEntregaPostal().getTipus().equals(NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR)) {
					if (enviament.getEntregaPostal().getLinea1() == null || enviament.getEntregaPostal().getLinea1().isEmpty()) {
						return setRespostaError("[1290] El camp 'linea1' no pot ser null.");
					}
					if (enviament.getEntregaPostal().getLinea2() == null || enviament.getEntregaPostal().getLinea2().isEmpty()) {
						return setRespostaError("[1291] El camp 'linea2' no pot ser null.");
					}
//					if (enviament.getEntregaPostal().getPaisCodi() == null || enviament.getEntregaPostal().getPaisCodi().isEmpty()) {
//						return setRespostaError("[PAIS] El camp 'paisCodi' no pot ser null en cas d'entrega SENSE NORMALITZAR.");
//					}
				}
			}

			// Entrega DEH
			if (!entitat.isAmbEntregaDeh() && enviament.isEntregaDehActiva()) {
				return setRespostaError("[1300] El camp 'entrega DEH' de l'entitat ha d'estar actiu en cas d'enviaments amb entrega DEH");
			}
			if (enviament.isEntregaDehActiva() && enviament.getEntregaDeh() == null) {
				return setRespostaError("[1301] El camp 'entregaDeh' d'un enviament no pot ser null");
			}
			if (enviament.isEntregaDehActiva() && (enviament.getTitular().getNif() == null || enviament.getTitular().getNif().isEmpty())) {
				return setRespostaError("[1302] El nif del titular és obligatori quan s'activa la entrega DEH");
			}
			
		}
		if (comunicacioAmbAdministracio && comunicacioSenseAdministracio) {
			return setRespostaError("[1310] Una comunicació no pot estar dirigida a una administració i a una persona física/jurídica a la vegada.");
		}
		
		// Procediment
		if (notificacio.getEnviamentTipus() == EnviamentTipusEnum.NOTIFICACIO ) {
			if (notificacio.getProcedimentCodi() == null) {
				return setRespostaError("[1020] El camp 'procedimentCodi' no pot ser null.");
			}
		} else if (notificacio.getProcedimentCodi() == null && notificacio.getOrganGestor() == null){
			return setRespostaError("[1022] El camp 'organ gestor' no pot ser null en una comunicació amb l'administració on no s'especifica un procediment.");
		}
//		if (notificacio.getEnviamentTipus() == EnviamentTipusEnum.COMUNICACIO &&  comunicacioSenseAdministracio) {
//			if (notificacio.getProcedimentCodi() == null) {
//				return setRespostaError("[1020] El camp 'procedimentCodi' no pot ser null.");
//			}
//		}

		// Documents
		if (comunicacioAmbAdministracio) {
			RespostaAlta respostaDoc = null;
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
				if (!isFormatValid(document.getContingutBase64())) {
					return setRespostaError("[1063] El format del document no és vàlid. Les notificacions i comunicacions a ciutadà només admeten els formats PDF i ZIP.");
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
				return setRespostaError("[1067] Les notificacions i comunicacions a ciutadà només admeten 1 únic document.");
			}

		}

		return resposta;
	}

	private RespostaAlta validaDocumentComunicacioAdmin(DocumentV2 document, int numDocument) {

		if (document.getArxiuNom() == null || document.getArxiuNom().isEmpty()) {
			return setRespostaError("[1061] El camp 'arxiuNom' del document " + numDocument + " no pot ser null.");
		}
		if (document.getArxiuNom() != null && document.getArxiuNom().length() > 200) {
			return setRespostaError("[1072] El camp 'arxiuNom' del document " + numDocument + " no pot pot tenir una longitud superior a 200 caràcters.");
		}
		if (	(document.getContingutBase64() == null || document.getContingutBase64().isEmpty()) &&
				(document.getCsv() == null || document.getCsv().isEmpty()) &&
				(document.getUrl() == null || document.getUrl().isEmpty()) &&
				(document.getUuid() == null || document.getUuid().isEmpty())) {
			return setRespostaError("[1062] El document " + numDocument + " no té contingut (contingutBase64, CSV, UUID o URL).");
		}

		// Format
		if (!isComunicacioAdminFormatValid(document.getArxiuNom())) {
			return setRespostaError("[1063] El format del document no és vàlid. Les comunicacions a administració només admeten els formats JPG, PEG, ODT, ODP, ODS, ODG, DOCX, XLSX, PPTX, PDF, PNG, RTF, SVG, GIFF, TXT, XML, XSIG, CSIG i HTML.");
		}
		// Metadades
		if ((document.getContingutBase64() != null && !document.getContingutBase64().isEmpty()) ||
				(document.getUrl() != null && !document.getUrl().isEmpty())) {
			if (document.getOrigen() == null) {
				return setRespostaError("[1066] Error en les metadades del document. No està informat l'ORIGEN del document " + numDocument);
			}
			if (document.getValidesa() == null) {
				return setRespostaError("[1066] Error en les metadades del document. No està informat la VALIDESA del document " + numDocument);
			}
			if (document.getTipoDocumental() == null) {
				return setRespostaError("[1066] Error en les metadades del document. No està informat el TIPUS DOCUMENTAL del document " + numDocument);
			}
			if (document.getArxiuNom().toUpperCase().endsWith("PDF") && document.getModoFirma() == null) {
				return setRespostaError("[1066] Error en les metadades del document. No està informat el MODE de FIRMA del document tipus PDF " + numDocument);
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

	private RespostaAlta setRespostaError(String descripcioError) {
		RespostaAlta resposta = new RespostaAlta();
		resposta.setError(true);
		resposta.setEstat(NotificacioEstatEnum.PENDENT);
		resposta.setErrorDescripcio(descripcioError);
		return resposta;
	}
	
	private EnviamentEstatEnum toEnviamentEstat(NotificacioEnviamentEstatEnumDto estat) {
		if (estat == null) return null;
		switch (estat) {
		case ABSENT:
			return EnviamentEstatEnum.ABSENT;
		case ADRESA_INCORRECTA:
			return EnviamentEstatEnum.ADRESA_INCORRECTA;
		case DESCONEGUT:
			return EnviamentEstatEnum.DESCONEGUT;
		case ENTREGADA_OP:
			return EnviamentEstatEnum.ENTREGADA_OP;
		case ENVIADA_CI:
			return EnviamentEstatEnum.ENVIADA_CI;
		case ENVIADA_DEH:
			return EnviamentEstatEnum.ENVIADA_DEH;
		case ENVIAMENT_PROGRAMAT:
			return EnviamentEstatEnum.ENVIAMENT_PROGRAMAT;
		case ERROR_ENTREGA:
			return EnviamentEstatEnum.ERROR_ENTREGA;
		case EXPIRADA:
			return EnviamentEstatEnum.EXPIRADA;
		case EXTRAVIADA:
			return EnviamentEstatEnum.EXTRAVIADA;
		case LLEGIDA:
			return EnviamentEstatEnum.LLEGIDA;
		case MORT:
			return EnviamentEstatEnum.MORT;
		case NOTIB_ENVIADA:
			return EnviamentEstatEnum.NOTIB_ENVIADA;
		case NOTIB_PENDENT:
			return EnviamentEstatEnum.NOTIB_PENDENT;
		case NOTIFICADA:
			return EnviamentEstatEnum.NOTIFICADA;
		case PENDENT_CIE:
			return EnviamentEstatEnum.PENDENT_CIE;
		case PENDENT_DEH:
			return EnviamentEstatEnum.PENDENT_DEH;
		case PENDENT_ENVIAMENT:
			return EnviamentEstatEnum.PENDENT_ENVIAMENT;
		case PENDENT_SEU:
			return EnviamentEstatEnum.PENDENT_SEU;
		case REBUTJADA:
			return EnviamentEstatEnum.REBUTJADA;
		case SENSE_INFORMACIO:
			return EnviamentEstatEnum.SENSE_INFORMACIO;
		case ENVIAT_SIR:
			return EnviamentEstatEnum.ENVIAT_SIR;
		case ANULADA:
			return EnviamentEstatEnum.ANULADA;
		default:
			return null;
		}
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
	
	private boolean isEmailValid(String email) {
		boolean valid = true;
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (Exception e) {
			valid = false; //no vàlid
		}
		return valid;
	}
	
	private boolean isFormatValid(String docBase64) {
		boolean valid = true;
		String[] formatsValids = {"JVBERi0","UEsDBBQAAAAIA"}; //PDF / ZIP
		
		if (!(docBase64.startsWith(formatsValids[0]) || docBase64.startsWith(formatsValids[1])))
			valid = false;
		
		return valid;
	}

	private boolean isPDF(String docBase64) {
		return docBase64.startsWith("JVBERi0");
	}
	
	private static Boolean isMultipleDestinataris() {
		String property = "es.caib.notib.destinatari.multiple";
		logger.debug("Consulta del valor de la property (" +
				"property=" + property + ")");
		return PropertiesHelper.getProperties().getAsBoolean(property, false);
	}
	
	private static Long getMaxSizeFile() {
		String property = "es.caib.notib.notificacio.document.size";
		logger.debug("Consulta del valor de la property (property=" + property + ")");
		return Long.valueOf(PropertiesHelper.getProperties().getProperty(property, "10485760"));
	}

	private static Long getMaxTotalSizeFile() {
		String property = "es.caib.notib.notificacio.document.total.size";
		logger.debug("Consulta del valor de la property (property=" + property + ")");
		return Long.valueOf(PropertiesHelper.getProperties().getProperty(property, "15728640"));
	}
	
	// Indica si usar valores por defecto cuando ni el documento ni documentV2 tienen metadades
	private boolean getUtilizarValoresPorDefecto() {
		return PropertiesHelper.getProperties().getAsBoolean(
				"es.caib.notib.document.metadades.por.defecto", true);
	}
	private static final Logger logger = LoggerFactory.getLogger(NotificacioServiceWsImplV2.class);

}
