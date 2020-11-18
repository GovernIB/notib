/**
 * 
 */
package es.caib.notib.core.service.ws;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.mail.internet.InternetAddress;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.Timer;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.notib.core.api.dto.AccioParam;
import es.caib.notib.core.api.dto.DocumentDto;
import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.core.api.dto.IntegracioInfo;
import es.caib.notib.core.api.dto.InteressatTipusEnumDto;
import es.caib.notib.core.api.dto.LlibreDto;
import es.caib.notib.core.api.dto.NotificaDomiciliConcretTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliNumeracioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliViaTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.dto.OrganismeDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.ServeiTipusEnumDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.api.exception.RegistreNotificaException;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.api.ws.notificacio.Certificacio;
import es.caib.notib.core.api.ws.notificacio.DadesConsulta;
import es.caib.notib.core.api.ws.notificacio.DocumentV2;
import es.caib.notib.core.api.ws.notificacio.EntregaPostalViaTipusEnum;
import es.caib.notib.core.api.ws.notificacio.Enviament;
import es.caib.notib.core.api.ws.notificacio.EnviamentEstatEnum;
import es.caib.notib.core.api.ws.notificacio.EnviamentReferencia;
import es.caib.notib.core.api.ws.notificacio.EnviamentTipusEnum;
import es.caib.notib.core.api.ws.notificacio.NotificacioEstatEnum;
import es.caib.notib.core.api.ws.notificacio.NotificacioServiceWsException;
import es.caib.notib.core.api.ws.notificacio.NotificacioServiceWsV2;
import es.caib.notib.core.api.ws.notificacio.NotificacioV2;
import es.caib.notib.core.api.ws.notificacio.PermisConsulta;
import es.caib.notib.core.api.ws.notificacio.Persona;
import es.caib.notib.core.api.ws.notificacio.RespostaAlta;
import es.caib.notib.core.api.ws.notificacio.RespostaConsultaDadesRegistre;
import es.caib.notib.core.api.ws.notificacio.RespostaConsultaEstatEnviament;
import es.caib.notib.core.api.ws.notificacio.RespostaConsultaEstatNotificacio;
import es.caib.notib.core.entity.AplicacioEntity;
import es.caib.notib.core.entity.DocumentEntity;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.PersonaEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.helper.CacheHelper;
import es.caib.notib.core.helper.CaducitatHelper;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.CreacioSemaforDto;
import es.caib.notib.core.helper.IntegracioHelper;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.helper.NifHelper;
import es.caib.notib.core.helper.NotificaHelper;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.helper.PropertiesHelper;
import es.caib.notib.core.helper.RegistreNotificaHelper;
import es.caib.notib.core.repository.AplicacioRepository;
import es.caib.notib.core.repository.DocumentRepository;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.core.repository.PersonaRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.plugin.registre.RespostaJustificantRecepcio;
import es.caib.plugins.arxiu.api.DocumentContingut;


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
	private PersonaRepository personaRepository;
	@Autowired
	private DocumentRepository documentRepository;
//	@Autowired
//	private EntitatTipusDocRepository entitatTipusDocRepository;
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
						if (!procediment.isComu() || (procediment.isComu() && notificacio.getOrganGestor() == null)) {
							organGestor = procediment.getOrganGestor();
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
						Map<String, OrganismeDto> organigramaEntitat = cacheHelper.findOrganigramaByEntitat(entitat.getDir3Codi());
						if (!organigramaEntitat.containsKey(notificacio.getOrganGestor())) {
							logger.debug(">> [ALTA] Organ gestor desconegut");
							String errorDescripcio = "[1023] El camp 'organ gestor' no es correspon a cap Òrgan Gestor de l'entitat especificada.";
							integracioHelper.addAccioError(info, errorDescripcio);
							return setRespostaError(errorDescripcio);
						}
						LlibreDto llibreOrgan = pluginHelper.llistarLlibreOrganisme(
								entitat.getCodi(),
								notificacio.getOrganGestor());
						
						organGestor = OrganGestorEntity.getBuilder(
								notificacio.getOrganGestor(),
								organigramaEntitat.get(notificacio.getOrganGestor()).getNom(),
								entitat,
								llibreOrgan.getCodi(),
								llibreOrgan.getNomLlarg()).build();
						organGestorRepository.save(organGestor);
					}
				}
				
				// Dades no depenents de procediment
				
				// DOCUMENT
				// Comprovam si el document és vàlid
				DocumentDto document = null;
				try {
					document = comprovaDocument(notificacio);
				} catch (Exception e) {
					logger.error("Error al obtenir el document", e);
					String errorDescripcio = "[1064] No s'ha pogut obtenir el document a notificar: " + e.getMessage();
					integracioHelper.addAccioError(info, errorDescripcio);
					return setRespostaError(errorDescripcio);
				}
				// Obtenim el document
				DocumentEntity documentEntity = getDocument(notificacio, document);
	
				NotificacioEntity.BuilderV2 notificacioBuilder = NotificacioEntity.
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
								TipusUsuariEnumDto.APLICACIO)
						.document(documentEntity);
				
				NotificacioEntity notificacioGuardada = notificacioRepository.saveAndFlush(notificacioBuilder.build());
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

				notificacioRepository.saveAndFlush(notificacioGuardada);

				
				if (NotificacioComunicacioTipusEnumDto.SINCRON.equals(pluginHelper.getNotibTipusComunicacioDefecte())) {
					processaNotificacio(notificacioGuardada);
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
		if (notificacioGuardada.getNotificaErrorEvent() != null) {
			logger.debug(">> [ALTA] Event d'error de Notifica!: " + notificacioGuardada.getNotificaErrorEvent().getDescripcio() + " - " + notificacioGuardada.getNotificaErrorEvent().getErrorDescripcio());
			resposta.setError(true);
			resposta.setErrorDescripcio(
					notificacioGuardada.getNotificaErrorEvent().getErrorDescripcio());
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

	private void processaNotificacio(NotificacioEntity notificacioGuardada) throws RegistreNotificaException {
		logger.debug(">> [ALTA] notificació síncrona");
		List<NotificacioEnviamentEntity> enviamentsEntity = notificacioEnviamentRepository.findByNotificacio(notificacioGuardada);
		
		List<NotificacioEnviamentDtoV2> enviaments = conversioTipusHelper.convertirList(
				enviamentsEntity, 
				NotificacioEnviamentDtoV2.class);
		
		logger.info(" [ALTA] Enviament SINCRON notificació [Id: " + notificacioGuardada.getId() + ", Estat: " + notificacioGuardada.getEstat() + "]");
		synchronized(CreacioSemaforDto.getCreacioSemafor()) {
			registreNotificaHelper.realitzarProcesRegistrarNotificar(
					notificacioGuardada,
					enviaments);
		}
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
		
		NotificacioEnviamentEntity enviamentSaved = notificacioEnviamentRepository.saveAndFlush(
				NotificacioEnviamentEntity.getBuilderV2(
						enviament, 
						entitat.isAmbEntregaDeh(),
						numeracioTipus, 
						tipusConcret, 
						serveiTipus, 
						notificacioGuardada, 
						titular, 
						destinataris)
				.domiciliViaTipus(toEnviamentViaTipusEnum(viaTipus)).build());
		logger.debug(">> [ALTA] enviament creat");
		
		String referencia;
		try {
			referencia = notificaHelper.xifrarId(enviamentSaved.getId());
			logger.debug(">> [ALTA] referencia creada");
		} catch (GeneralSecurityException ex) {
			logger.debug(">> [ALTA] Error creant referència");
			throw new RuntimeException(
					"No s'ha pogut crear la referencia per al destinatari",
					ex);
		}
		enviamentSaved.updateNotificaReferencia(referencia);
		EnviamentReferencia enviamentReferencia = new EnviamentReferencia();
		enviamentReferencia.setReferencia(referencia);
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

	private DocumentEntity getDocument(NotificacioV2 notificacio, DocumentDto document) {
		DocumentEntity documentEntity = null;
		if(notificacio.getDocument().getCsv() != null || 
		   notificacio.getDocument().getUuid() != null || 
		   notificacio.getDocument().getContingutBase64() != null || 
		   notificacio.getDocument().getUrl() != null ||
		   notificacio.getDocument().getArxiuId() != null) {

			documentEntity = documentRepository.saveAndFlush(DocumentEntity.getBuilderV2(
					notificacio.getDocument().getArxiuId(), 
					document.getArxiuGestdocId(), 
					notificacio.getDocument().getArxiuNom(),  
					notificacio.getDocument().getUrl(),  
					notificacio.getDocument().isNormalitzat(),  
					notificacio.getDocument().getUuid(),
					notificacio.getDocument().getCsv(),
					document.getMediaType(),
					document.getMida()).build());
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

	private DocumentDto comprovaDocument(NotificacioV2 notificacio) {
		DocumentDto document = new DocumentDto();
		if(notificacio.getDocument().getContingutBase64() != null) {
			logger.debug(">> [ALTA] document contingut Base64");
			byte[] contingut = Base64.decodeBase64(notificacio.getDocument().getContingutBase64()); 
			String documentGesdocId = pluginHelper.gestioDocumentalCreate(
					PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
					contingut);
			document.setArxiuGestdocId(documentGesdocId);
			document.setMida(Long.valueOf(contingut.length));
			document.setMediaType(getMimeTypeFromContingut(contingut));
			logger.debug(">> [ALTA] documentId: " + documentGesdocId);
		} else if (notificacio.getDocument().getUuid() != null) {
			String arxiuUuid = notificacio.getDocument().getUuid();
			logger.debug(">> [ALTA] documentUuid: " + arxiuUuid);
			DocumentContingut contingut = pluginHelper.arxiuGetImprimible(arxiuUuid, true);
			document.setMida(contingut.getTamany());
			document.setMediaType(contingut.getTipusMime());
		} else if (notificacio.getDocument().getCsv() != null) {
			String arxiuCsv = notificacio.getDocument().getCsv();
			logger.debug(">> [ALTA] documentCsv: " + arxiuCsv);
			DocumentContingut contingut = pluginHelper.arxiuGetImprimible(arxiuCsv, false);
			document.setMida(contingut.getTamany());
			document.setMediaType(contingut.getTipusMime());
		} else if (notificacio.getDocument().getUrl() != null) {
			String arxiuUrl = notificacio.getDocument().getUrl();
			logger.debug(">> [ALTA] documentUrl: " + arxiuUrl);
			byte[] contingut = pluginHelper.getUrlDocumentContent(arxiuUrl);
			document.setMida(Long.valueOf(contingut.length));
			document.setMediaType(getMimeTypeFromContingut(contingut));
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
				if (notificacio.getNotificaErrorEvent() != null) {
					resposta.setError(true);
					NotificacioEventEntity errorEvent = notificacio.getNotificaErrorEvent();
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
						notificaHelper.enviamentRefrescarEstat(enviament.getId());
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
					if (enviament.getNotificacio() == null) {
						NotificacioEntity notificacio = notificacioRepository.findById(enviament.getNotificacioId());
						enviament.setNotificacio(notificacio);
					}
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
	// 1030 | El concepte de la notificació no pot ser null
	// 1031 | El concepte de la notificació no pot tenir una longitud superior a 255 caràcters
	// 1032 | El format del camp concepte no és correcte. (Inclou caràcters no permesos)
	// 1040 | La descripció de la notificació no pot contenir més de 1000 caràcters
	// 1041 | El format del camp descripció no és correcte
	// 1050 | El tipus d'enviament de la notificació no pot ser null
	// 1060 | El camp 'document' no pot ser null
	// 1061 | El camp 'arxiuNom' del document no pot ser null
	// 1062 | És necessari incloure un document a la notificació
	// 1063 | El format del document no és vàlid. Els formats vàlids són PDF i ZIP.
	// 1064 | No s'ha pogut obtenir el document a notificar
	// 1065 | La longitud del document supera el màxim definit
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
	// 1130 | El camp 'nom' de la persona física titular no pot ser null
	// 1131 | El camp 'llinatge1' de la persona física titular d'un enviament no pot ser null en el cas de persones físiques
	// 1132 | El camp 'nif' de la persona física titular d'un enviament no pot ser null
	// 1140 | El camp 'raoSocial' de la persona jurídica titular d'un enviament no pot ser null
	// 1141 | El camp 'nif' de la persona jurídica titular d'un enviament no pot ser null
	// 1150 | El camp 'nom' de l'administració titular d'un enviament no pot ser null
	// 1151 | El camp 'dir3codi' de l'administració titular d'un enviament no pot ser null
	// 1160 | El numero de destinatais està limitat a un destinatari
	// 1170 | El camp 'interessat_tipus' del destinatari d'un enviament no pot ser null
	// 1171 | El camp 'nom' del titular no pot tenir una longitud superior a 255 caràcters
	// 1172 | El camp 'llinatge1' del destinatari no pot ser major que 40 caràcters
	// 1173 | El camp 'llinatge2' del destinatari no pot ser major que 40 caràcters
	// 1174 | El camp 'nif' del destinatari d'un enviament no pot tenir una longitud superior a 9 caràcters
	// 1175 | El 'nif' del titular no és vàlid
	// 1176 | El camp 'email' del destinatari no pot ser major que 255 caràcters
	// 1177 | El format del camp 'email' del destinatari no és correcte
	// 1178 | El camp 'telefon' del destinatari no pot ser major que 16 caràcters
	// 1179 | El camp 'raoSocial' del destinatari no pot ser major que 255 caràcters
	// 1180 | El camp 'dir3Codi' del destinatari no pot ser major que 9 caràcters
	// 1190 | El camp 'nom' de la persona física destinatària d'un enviament no pot ser null
	// 1191 | El camp 'llinatge1' del destinatari d'un enviament no pot ser null en el cas de persones físiques
	// 1192 | El camp 'nif' de la persona física destinatària d'un enviament no pot ser null
	// 1200 | El camp 'raoSocial' de la persona jurídica destinatària d'un enviament no pot ser null
	// 1201 | El camp 'nif' de la persona jurídica destinatària d'un enviament no pot ser null
	// 1210 | El camp 'nom' de l'administració destinatària d'un enviament no pot ser null
	// 1211 | El camp 'dir3codi' de l'administració destinatària d'un enviament no pot ser null
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
//		// Procediment
//		if (notificacio.getProcedimentCodi() == null) {
//			return setRespostaError("[1020] El camp 'procedimentCodi' no pot ser null.");
//		}
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
		// Tipus d'enviament
		if (notificacio.getEnviamentTipus() == null) {
			return setRespostaError("[1050] El tipus d'enviament de la notificació no pot ser null.");
		}
		// Document
		if (notificacio.getDocument() == null) {
			return setRespostaError("[1060] El camp 'document' no pot ser null.");
		}
		DocumentV2 document = notificacio.getDocument();
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
			return setRespostaError("[1062] És necessari incloure un document a la notificació.");
		}
		if (document.getContingutBase64() != null && !document.getContingutBase64().isEmpty()) {
			byte[] base64Decoded = Base64.decodeBase64(notificacio.getDocument().getContingutBase64());
			if (!isFormatValid(document.getContingutBase64()))
				return setRespostaError("[1063] El format del document no és vàlid. Els formats vàlids són PDF i ZIP.");
			if (base64Decoded.length > getMaxSizeFile()) {
				return setRespostaError("[1065] La longitud del document supera el màxim definit (" + getMaxSizeFile() / (1024*1024) + "Mb).");
			}
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
				if ((enviament.getTitular().getInteressatTipus() == InteressatTipusEnumDto.FISICA) || (enviament.getTitular().getInteressatTipus() == InteressatTipusEnumDto.FISICA))  {
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
			if (enviament.getTitular().getRaoSocial() != null && enviament.getTitular().getRaoSocial().length() > 255) {
				return setRespostaError("[1120] El camp 'raoSocial' del titular no pot ser major que 255 caràcters.");
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
			}
			
			// Destinataris
			if (!Boolean.getBoolean(isMultipleDestinataris()) && enviament.getDestinataris() != null && enviament.getDestinataris().size() > 1) {
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
							return setRespostaError("[1175] El 'nif' del titular no és vàlid.");
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
					if (destinatari.getRaoSocial() != null && destinatari.getRaoSocial().length() > 255) {
						return setRespostaError("[1179] El camp 'raoSocial' del destinatari no pot ser major que 255 caràcters.");
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
		// if (!comunicacioAmbAdministracio) {
		if (notificacio.getEnviamentTipus() == EnviamentTipusEnum.NOTIFICACIO) {
			if (notificacio.getProcedimentCodi() == null) {
				return setRespostaError("[1020] El camp 'procedimentCodi' no pot ser null.");
			}
		} else if (notificacio.getProcedimentCodi() == null && notificacio.getOrganGestor() == null){
			return setRespostaError("[1022] El camp 'organ gestor' no pot ser null en una comunicació amb l'administració on no s'especifica un procediment.");
		}
		return resposta;
	}
	
	private RespostaAlta setRespostaError(String descripcioError) {
		RespostaAlta resposta = new RespostaAlta();
		resposta.setError(true);
		resposta.setEstat(NotificacioEstatEnum.PENDENT);
		resposta.setErrorDescripcio(descripcioError);
		return resposta;
	}
	
	private NotificaDomiciliViaTipusEnumDto toEnviamentViaTipusEnum(
			EntregaPostalViaTipusEnum viaTipus) {
		if (viaTipus == null) {
			return null;
		}
		return NotificaDomiciliViaTipusEnumDto.valueOf(viaTipus.name());
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
	
	private static String isMultipleDestinataris() {
		String property = "es.caib.notib.destinatari.multiple";
		logger.debug("Consulta del valor de la property (" +
				"property=" + property + ")");
		return PropertiesHelper.getProperties().getProperty(property);
	}
	
	private static Long getMaxSizeFile() {
		String property = "es.caib.notib.notificacio.document.size";
		logger.debug("Consulta del valor de la property (property=" + property + ")");
		return Long.valueOf(PropertiesHelper.getProperties().getProperty(property, "10485760"));
	}
	
	private static final Logger logger = LoggerFactory.getLogger(NotificacioServiceWsImplV2.class);

}
