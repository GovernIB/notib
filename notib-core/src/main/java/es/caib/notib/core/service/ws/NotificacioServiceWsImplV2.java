/**
 * 
 */
package es.caib.notib.core.service.ws;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jersey.core.util.Base64;

import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.InteressatTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliConcretTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliNumeracioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliViaTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioDtoV2;
import es.caib.notib.core.api.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.ServeiTipusEnumDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.api.ws.notificacio.Certificacio;
import es.caib.notib.core.api.ws.notificacio.DadesConsulta;
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
import es.caib.notib.core.api.ws.notificacio.RespostaConsultaEstatEnviament;
import es.caib.notib.core.api.ws.notificacio.RespostaConsultaEstatNotificacio;
import es.caib.notib.core.api.ws.notificacio.RespostaConsultaDadesRegistre;
import es.caib.notib.core.entity.DocumentEntity;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.entity.PersonaEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.helper.CaducitatHelper;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.CreacioSemaforDto;
import es.caib.notib.core.helper.NotificaHelper;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.helper.PropertiesHelper;
import es.caib.notib.core.helper.RegistreNotificaHelper;
import es.caib.notib.core.repository.DocumentRepository;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.repository.PersonaRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.plugin.registre.RespostaJustificantRecepcio;


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
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired 
	private PermisosHelper permisosHelper;
	@Autowired 
	NotificacioEventRepository notificacioEventRepository;
	@Autowired
	private NotificaHelper notificaHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private GrupService grupService;
	@Autowired
	private RegistreNotificaHelper registreNotificaHelper;
	
	@Transactional
	@Override
	public RespostaAlta alta(
			NotificacioV2 notificacio) throws NotificacioServiceWsException {
		RespostaAlta resposta = new RespostaAlta();
		String emisorDir3Codi = notificacio.getEmisorDir3Codi();
		EntitatEntity entitat = entitatRepository.findByDir3Codi(emisorDir3Codi);
		resposta = validarNotificacio(
				notificacio,
				emisorDir3Codi,
				entitat);
		
		if (resposta.isError()) {
			return resposta;
		}
		
		try {
			ProcedimentEntity procediment = procedimentRepository.findByCodiAndEntitat(
					notificacio.getProcedimentCodi(), 
					entitat);

			if(procediment != null) {
				if (procediment.isAgrupar() && notificacio.getGrupCodi() != null && !notificacio.getGrupCodi().isEmpty()) {
					// Llistat de procediments amb grups
					List<GrupDto> grupsProcediment = grupService.findByProcedimentGrups(procediment.getId());
					GrupDto grupNotificacio = grupService.findByCodi(
							notificacio.getGrupCodi(),
							entitat.getId());
					if (grupNotificacio == null) {
						if (!grupsProcediment.contains(grupNotificacio)) {
							resposta.setError(true);
							resposta.setEstat(NotificacioEstatEnum.PENDENT);
							resposta.setErrorDescripcio("[GRUP] El grup indicat " + notificacio.getGrupCodi() + " no està definit dins NOTIB.");
							return resposta;
						}
					}
					if (grupsProcediment == null || grupsProcediment.isEmpty()) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[GRUP_PROCEDIMENT] S'ha indicat un grup per les notificacions però el procediment " + notificacio.getProcedimentCodi() + " no té cap grup assignat.");
						return resposta;
					} else {
						if(! Arrays.asList(grupsProcediment).contains(grupsProcediment)) {
							resposta.setError(true);
							resposta.setEstat(NotificacioEstatEnum.PENDENT);
							resposta.setErrorDescripcio("[GRUP_PROCEDIMENT] El grup indicat " + notificacio.getGrupCodi() + " no està assignat al procediment " + notificacio.getProcedimentCodi());
							return resposta;
						}
					}
				}
				String documentGesdocId = null;
				if(notificacio.getDocument().getContingutBase64() != null) {
					documentGesdocId = pluginHelper.gestioDocumentalCreate(
							PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
							new ByteArrayInputStream(
									Base64.decode(notificacio.getDocument().getContingutBase64())));
				}
				
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
				}
				
				DocumentEntity documentEntity = null;
				if(notificacio.getDocument().getCsv() != null || 
				   notificacio.getDocument().getUuid() != null || 
				   notificacio.getDocument().getContingutBase64() != null || 
				   notificacio.getDocument().getArxiuId() != null) {
		
					documentEntity = documentRepository.saveAndFlush(DocumentEntity.getBuilderV2(
							notificacio.getDocument().getArxiuId(), 
							documentGesdocId, 
							notificacio.getDocument().getArxiuNom(),  
							notificacio.getDocument().getUrl(),  
							notificacio.getDocument().isNormalitzat(),  
							notificacio.getDocument().getUuid(),
							notificacio.getDocument().getCsv()).build());
				}		
				//Comprovar si no hi ha una caducitat posar una per defecte (dia acutal + dies caducitat procediment)
				if (notificacio.getCaducitat() != null) {
					notificacio.setCaducitat(CaducitatHelper.sumarDiesLaborals(
							notificacio.getCaducitat(),
							procediment.getCaducitat()));
				} else {
					notificacio.setCaducitat(CaducitatHelper.sumarDiesLaborals(
							new Date(),
							procediment.getCaducitat()));
				}
				NotificacioEntity.BuilderV2 notificacioBuilder = NotificacioEntity.
					getBuilderV2(
						entitat,
						emisorDir3Codi,
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
						TipusUsuariEnumDto.APLICACIO 
					).document(documentEntity);
					
					NotificacioEntity notificacioGuardada = notificacioRepository.saveAndFlush(notificacioBuilder.build());
					List<EnviamentReferencia> referencies = new ArrayList<EnviamentReferencia>();
					for (Enviament enviament: notificacio.getEnviaments()) {
						if (enviament.getTitular() == null) {
							resposta.setError(true);
							resposta.setEstat(NotificacioEstatEnum.PENDENT);
							resposta.setErrorDescripcio("[TITULAR] El camp 'titular' no pot ser null.");
							return resposta;
						}
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
						
						NotificaDomiciliNumeracioTipusEnumDto numeracioTipus = null;
						NotificaDomiciliConcretTipusEnumDto tipusConcret = null;
						if (enviament.isEntregaPostalActiva() && enviament.getEntregaPostal() != null) {
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
		//						tipus = NotificaDomiciliTipusEnumDto.CONCRETO;
							} else {
								throw new ValidationException(
										"ENTREGA_POSTAL",
										"L'entrega postal te el camp tipus buit");
							}
							if (enviament.getEntregaPostal().getNumeroCasa() != null) {
								numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.NUMERO;
							} else if (enviament.getEntregaPostal().getApartatCorreus() != null) {
								numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.APARTAT_CORREUS;
							} else if (enviament.getEntregaPostal().getPuntKm() != null) {
								numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.PUNT_KILOMETRIC;
							} else {
								numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.SENSE_NUMERO;
							}
						}
						
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
						EntregaPostalViaTipusEnum viaTipus = null;
						
						if (enviament.getEntregaPostal() != null) {
							viaTipus = enviament.getEntregaPostal().getViaTipus();
						}
						NotificacioEnviamentEntity enviamentSaved = notificacioEnviamentRepository.saveAndFlush(
								NotificacioEnviamentEntity.getBuilderV2(
										enviament, 
										entitat.isAmbEntregaDeh(),
										conversioTipusHelper.convertir(
												notificacio, 
												NotificacioDtoV2.class), 
										numeracioTipus, 
										tipusConcret, 
										serveiTipus, 
										notificacioGuardada, 
										titular, 
										destinataris)
								.domiciliViaTipus(toEnviamentViaTipusEnum(viaTipus)).build());
						
						String referencia;
						try {
							referencia = notificaHelper.xifrarId(enviamentSaved.getId());
						} catch (GeneralSecurityException ex) {
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
						referencies.add(enviamentReferencia);
						notificacioGuardada.addEnviament(enviamentSaved);
					}
					
					notificacioRepository.saveAndFlush(notificacioGuardada);
					if (NotificacioComunicacioTipusEnumDto.SINCRON.equals(pluginHelper.getNotibTipusComunicacioDefecte())) {
						List<NotificacioEnviamentEntity> enviamentsEntity = notificacioEnviamentRepository.findByNotificacio(notificacioGuardada);
						
						List<NotificacioEnviamentDtoV2> enviaments = conversioTipusHelper.convertirList(
								enviamentsEntity, 
								NotificacioEnviamentDtoV2.class);
						
						synchronized(CreacioSemaforDto.getCreacioSemafor()) {
							registreNotificaHelper.realitzarProcesRegistrarNotificar(
									notificacioGuardada,
									enviaments);
						}
						
					} else {
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
						
					}
		
					try {
						resposta.setIdentificador(
								notificaHelper.xifrarId(notificacioGuardada.getId()));
					} catch (GeneralSecurityException ex) {
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
						resposta.setError(true);
						resposta.setErrorDescripcio(
								notificacioGuardada.getNotificaErrorEvent().getErrorDescripcio());
					}
					resposta.setReferencies(referencies);
					return resposta;
			} else {
				resposta.setError(true);
				resposta.setEstat(NotificacioEstatEnum.PENDENT);
				resposta.setErrorDescripcio("[PROCEDIMENT] No s'ha trobat cap procediment amb el codi indicat.");
				return resposta;
			}
		} catch (Exception ex) {
			throw new RuntimeException(
					"[NOTIFICACIO/COMUNICACIO] Hi ha hagut un error creant la " + notificacio.getEnviamentTipus().name() + ": " + ex.getMessage(),
					ex);
		}
	}
	
	@Override
	public boolean donarPermisConsulta(PermisConsulta permisConsulta) {
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
		} catch (Exception ex) {
			throw new RuntimeException(
					"No s'ha pogut assignar el permís a l'usuari: " + permisConsulta.getUsuariCodi(),
					ex);
		}
		return totbe;
	}
	

	@Override
	@Transactional
	public RespostaConsultaEstatNotificacio consultaEstatNotificacio(
			String identificador) {
		Long notificacioId;
		RespostaConsultaEstatNotificacio resposta = new RespostaConsultaEstatNotificacio();

		try {
			try {
				notificacioId = notificaHelper.desxifrarId(identificador);
			} catch (GeneralSecurityException ex) {
				resposta.setError(true);
				resposta.setErrorData(new Date());
				resposta.setErrorDescripcio("No s'ha pogut desxifrar l'identificador de la notificació " + identificador);
				return resposta;
			}
			NotificacioEntity notificacio = notificacioRepository.findById(notificacioId);
			
			if (notificacio == null) {
				resposta.setError(true);
				resposta.setErrorData(new Date());
				resposta.setErrorDescripcio("Error: No s'ha trobat cap notificació amb l'identificador " + identificador);
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
				resposta.setErrorData(
						notificacio.getNotificaErrorEvent().getData());
				resposta.setErrorDescripcio(
						notificacio.getNotificaErrorEvent().getErrorDescripcio());
			}
		} catch (Exception ex) {
			throw new RuntimeException(
					"[NOTIFICACIO/COMUNICACIO] Hi ha hagut un error consultant la notificació: " + ex.getMessage(),
					ex);
		}
		return resposta;
	}

	@Override
	@Transactional
	public RespostaConsultaEstatEnviament consultaEstatEnviament(
			String referencia) throws NotificacioServiceWsException {
		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findByNotificaReferencia(referencia);
		RespostaConsultaEstatEnviament resposta = new RespostaConsultaEstatEnviament();
		logger.debug("Consultant estat enviament amb referencia: " + referencia);
		try {
			if (enviament == null) {
				resposta.setError(true);
				resposta.setErrorData(new Date());
				resposta.setErrorDescripcio("Error: No s'ha trobat cap enviament amb la referencia " + referencia);
				return resposta;
			} else {
				//Es canosulta l'estat periòdicament, no es necessita realitzar una consulta actica a Notifica
				// Si Notib no utilitza el servei Adviser de @Notifica, i ja ha estat enviat a @Notifica
				// serà necessari consultar l'estat de la notificació a Notifica
				if (	!notificaHelper.isAdviserActiu() &&
						!enviament.isNotificaEstatFinal() &&
						!enviament.getNotificaEstat().equals(NotificacioEnviamentEstatEnumDto.NOTIB_PENDENT)) {
					notificaHelper.enviamentRefrescarEstat(enviament.getId());
				}
				logger.debug("Estat enviament amb referencia " + referencia + ":" + enviament.getNotificaEstatDescripcio());
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
					certificacio.setContingutBase64(
							new String(Base64.encode(baos.toByteArray())));
					
					if (enviament.getNotificaCertificacioTamany() != null)
						certificacio.setTamany(enviament.getNotificaCertificacioTamany());
					
					certificacio.setHash(enviament.getNotificaCertificacioHash());
					certificacio.setMetadades(enviament.getNotificaCertificacioMetadades());
					certificacio.setCsv(enviament.getNotificaCertificacioCsv());
					certificacio.setTipusMime(enviament.getNotificaCertificacioMime());
					resposta.setCertificacio(certificacio);
					logger.debug("Certificació de l'enviament amb referencia: " + referencia + " s'ha guardat correctament.");
				}
				logger.debug("Notifica error de l'enviament amb referencia: " + referencia + ": " + enviament.isNotificaError());
				if (enviament.isNotificaError()) {
					resposta.setError(true);
					NotificacioEventEntity errorEvent = enviament.getNotificaErrorEvent();
					resposta.setErrorData(errorEvent.getData());
					resposta.setErrorDescripcio(errorEvent.getErrorDescripcio());
					logger.debug("Error consultar estat enviament amb referencia: " + referencia);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resposta;
	}
	
	@Override
	public RespostaConsultaDadesRegistre consultaDadesRegistre(DadesConsulta dadesConsulta) {
		RespostaConsultaDadesRegistre resposta = new RespostaConsultaDadesRegistre();
		if (dadesConsulta.getIdentificador() != null) {
			logger.debug("Consultant les dades de registre de la notificació amb identificador: " + dadesConsulta.getIdentificador());
			int numeroRegistre = 0;
			Long notificacioId;
			try {
				notificacioId = notificaHelper.desxifrarId(dadesConsulta.getIdentificador());
			} catch (GeneralSecurityException ex) {
				resposta.setError(true);
				resposta.setErrorData(new Date());
				resposta.setErrorDescripcio("No s'ha pogut desxifrar l'identificador de la notificació " + dadesConsulta.getIdentificador());
				return resposta;
			}
			NotificacioEntity notificacio = notificacioRepository.findById(notificacioId);
			
			if (notificacio == null) {
				resposta.setError(true);
				resposta.setErrorData(new Date());
				resposta.setErrorDescripcio("Error: No s'ha trobat cap notificació amb l'identificador " + dadesConsulta.getIdentificador());
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
						resposta.setErrorDescripcio(justificant.getErrorCodi() + ": " + justificant.getErrorDescripcio());
						return  resposta;
					}
				}	
			}
		} else if (dadesConsulta.getReferencia() != null) {
			logger.debug("Consultant les dades de registre de l'enviament amb referència: " + dadesConsulta.getReferencia());
			NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findByNotificaReferencia(dadesConsulta.getReferencia());
			if (enviament == null) {
				resposta.setError(true);
				resposta.setErrorData(new Date());
				resposta.setErrorDescripcio("Error: No s'ha trobat cap enviament amb la referència" + dadesConsulta.getReferencia());
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
						resposta.setErrorDescripcio(justificant.getErrorCodi() + ": " + justificant.getErrorDescripcio());
						return  resposta;
					}
				}	
			}
		}
		return resposta;
	}
	
	protected RespostaAlta validarNotificacio(
			NotificacioV2 notificacio,
			String emisorDir3Codi,
			EntitatEntity entitat) {
		RespostaAlta resposta = new RespostaAlta();
		boolean comunicacioSenseAdministracio = false;
		boolean comunicacioAmbAdministracio = false;
		
		if (emisorDir3Codi == null) {
			resposta.setError(true);
			resposta.setEstat(NotificacioEstatEnum.PENDENT);
			resposta.setErrorDescripcio("[EMISOR] El camp 'emisorDir3Codi' no pot ser null.");
			return resposta;
		}
		if (entitat == null) {
			resposta.setError(true);
			resposta.setEstat(NotificacioEstatEnum.PENDENT);
			resposta.setErrorDescripcio("[ENTITAT] No s'ha trobat cap entitat configurada a Notib amb el codi Dir3 " + emisorDir3Codi + ". (emisorDir3Codi)");
			return resposta;
		}
		if (!entitat.isActiva()) {
			resposta.setError(true);
			resposta.setEstat(NotificacioEstatEnum.PENDENT);
			resposta.setErrorDescripcio("[ENTITAT] L'entitat especificada està desactivada per a l'enviament de notificacions");
			return resposta;
		}
		if (notificacio.getProcedimentCodi() == null) {
			resposta.setError(true);
			resposta.setEstat(NotificacioEstatEnum.PENDENT);
			resposta.setErrorDescripcio("[PROCEDIMENT_CODI] El camp 'procedimentCodi' no pot ser null.");
			return resposta;
		}
		if (notificacio.getConcepte() == null) {
			resposta.setError(true);
			resposta.setEstat(NotificacioEstatEnum.PENDENT);
			resposta.setErrorDescripcio("[CONCEPTE] El concepte de la notificació no pot ser null.");
			return resposta;
		}
		if (!validConcepteDescripcio(notificacio.getConcepte())) {
			resposta.setError(true);
			resposta.setEstat(NotificacioEstatEnum.PENDENT);
			resposta.setErrorDescripcio("[CONCEPTE] El format del camp concepte no és correcte.");
			return resposta;
		}
		if (notificacio.getConcepte().length() > 255) {
			resposta.setError(true);
			resposta.setEstat(NotificacioEstatEnum.PENDENT);
			resposta.setErrorDescripcio("[CONCEPTE] El concepte de la notificació no pot contenir més de 255 caràcters.");
			return resposta;
		}
		if (notificacio.getDescripcio() != null && !validConcepteDescripcio(notificacio.getDescripcio())) {
			resposta.setError(true);
			resposta.setEstat(NotificacioEstatEnum.PENDENT);
			resposta.setErrorDescripcio("[DESCRIPCIO] El format del camp descripció no és correcte.");
			return resposta;
		}
		if (notificacio.getDescripcio() != null && notificacio.getDescripcio().length() > 1000){
			resposta.setError(true);
			resposta.setEstat(NotificacioEstatEnum.PENDENT);
			resposta.setErrorDescripcio("[DESCRIPCIO] La descripció de la notificació no pot contenir més de 1000 caràcters.");
			return resposta;
		}
		if (notificacio.getEnviamentTipus() == null) {
			resposta.setError(true);
			resposta.setEstat(NotificacioEstatEnum.PENDENT);
			resposta.setErrorDescripcio("[ENVIAMENT_TIPUS] El tipus d'enviament de la notificació no pot ser null.");
			return resposta;
		}
		if (notificacio.getDocument() == null) {
			resposta.setError(true);
			resposta.setEstat(NotificacioEstatEnum.PENDENT);
			resposta.setErrorDescripcio("[DOCUMENT] El camp 'document' no pot ser null.");
			return resposta;
		}
		if (notificacio.getEnviaments() == null) {
			resposta.setError(true);
			resposta.setEstat(NotificacioEstatEnum.PENDENT);
			resposta.setErrorDescripcio("[ENVIAMENTS] El camp 'enviaments' no pot ser null.");
			return resposta;
		}
		if (notificacio.getUsuariCodi() == null || notificacio.getUsuariCodi().isEmpty()) {
			resposta.setError(true);
			resposta.setEstat(NotificacioEstatEnum.PENDENT);
			resposta.setErrorDescripcio("[USUARI_CODI] El camp 'usuariCodi' no pot ser null (Requisit per fer el registre de sortida).");
			return resposta;
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
			if(enviament.getTitular() == null) {
				resposta.setError(true);
				resposta.setEstat(NotificacioEstatEnum.PENDENT);
				resposta.setErrorDescripcio("[TITULAR] El titular d'un enviament no pot ser null.");
				return resposta;
			}
			if(enviament.getTitular().getNom() == null) {
				resposta.setError(true);
				resposta.setEstat(NotificacioEstatEnum.PENDENT);
				resposta.setErrorDescripcio("[NOM] El camp 'nom' del titular no pot ser null.");
				return resposta;
			}
			if (enviament.getTitular().getEmail() != null && enviament.getTitular().getEmail().length() > 255) {
				resposta.setError(true);
				resposta.setEstat(NotificacioEstatEnum.PENDENT);
				resposta.setErrorDescripcio("[EMAIL] El camp 'email' del titular no pot ser major que 40 caràcters.");
				return resposta;
			}
			
			if (enviament.getTitular().getNom().length() > 255) {
				resposta.setError(true);
				resposta.setEstat(NotificacioEstatEnum.PENDENT);
				resposta.setErrorDescripcio("[NOM] El camp 'nom' del titular no pot ser major que 255 caràcters.");
				return resposta;
			}
			if (enviament.getTitular().isIncapacitat() && (enviament.getDestinataris() == null || enviament.getDestinataris().isEmpty())) {
				resposta.setError(true);
				resposta.setEstat(NotificacioEstatEnum.PENDENT);
				resposta.setErrorDescripcio("[DESTINATARI] En cas de titular amb incapacitat es obligatori indicar un destinatari.");
				return resposta;
			}
			if (!Boolean.getBoolean(isMultipleDestinataris()) && enviament.getDestinataris() != null && enviament.getDestinataris().size() > 1) {
				resposta.setError(true);
				resposta.setEstat(NotificacioEstatEnum.PENDENT);
				resposta.setErrorDescripcio("[DESTINATARI_MULTIPLE] El numero de destinatais està limitat a un destinatari.");
				return resposta;
			}
			if(enviament.isEntregaPostalActiva()){
				if (enviament.getEntregaPostal().getTipus() == null) {
					resposta.setError(true);
					resposta.setEstat(NotificacioEstatEnum.PENDENT);
					resposta.setErrorDescripcio("[ENTREGA_POSTAL_TIPUS] El camp 'entregaPostalTipus' no pot ser null.");
					return resposta;
				}
				if(enviament.getEntregaPostal().getTipus().equals(NotificaDomiciliConcretTipusEnumDto.NACIONAL)) {
					if((enviament.getEntregaPostal().getViaNom() == null || enviament.getEntregaPostal().getViaNom().isEmpty())) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[ENTREGA_POSTAL_NOM_VIA] El camp 'viaNom' de l'entrega postal d'un enviament no pot ser null.");
						return resposta;
					}
					if (enviament.getEntregaPostal().getViaTipus() == null) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[VIA_TIPUS] El camp 'viaTipus' no pot ser null en cas d'entrega NACIONAL NORMALITZAT.");
						return resposta;
					}
					if (enviament.getEntregaPostal().getViaNom() == null || enviament.getEntregaPostal().getViaNom().isEmpty()) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[VIA_NOM] El camp 'viaNom' no pot ser null en cas d'entrega NACIONAL NORMALITZAT.");
						return resposta;
					}
					if (enviament.getEntregaPostal().getPuntKm() == null && enviament.getEntregaPostal().getNumeroCasa() == null) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[PUNT_KM_NUM_CASA] S'ha d'indicar almenys un 'numeroCasa' o 'puntKm'");
						return resposta;
					}
					if (enviament.getEntregaPostal().getProvincia() == null || enviament.getEntregaPostal().getProvincia().isEmpty()) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[PROVINCIA] El camp 'provincia' no pot ser null en cas d'entrega NACIONAL NORMALITZAT.");
						return resposta;
					}
					if (enviament.getEntregaPostal().getProvincia().length() > 2) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[PROVINCIA]  El camp 'provincia' de l'entrega postal no pot contenir més de 2 caràcters.");
						return resposta;
					}
					if (enviament.getEntregaPostal().getMunicipiCodi() == null || enviament.getEntregaPostal().getMunicipiCodi().isEmpty()) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[MUNICIPI_CODI] El camp 'municipiCodi' no pot ser null en cas d'entrega NACIONAL NORMALITZAT.");
						return resposta;
					}
					if (enviament.getEntregaPostal().getMunicipiCodi().length() > 6) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[MUNICIPI_CODI]  El camp 'municipiCodi' de l'entrega postal no pot contenir més de 6 caràcters.");
						return resposta;
					}
					if (enviament.getEntregaPostal().getPoblacio() == null || enviament.getEntregaPostal().getPoblacio().isEmpty()) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[POBLACIO] El camp 'poblacio' no pot ser null en cas d'entrega NACIONAL NORMALITZAT.");
						return resposta;
					}
				}
				if(enviament.getEntregaPostal().getTipus().equals(NotificaDomiciliConcretTipusEnumDto.ESTRANGER)) {
					if((enviament.getEntregaPostal().getViaNom() == null || enviament.getEntregaPostal().getViaNom().isEmpty())) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[ENTREGA_POSTAL_NOM_VIA] El camp 'viaNom' de l'entrega postal d'un enviament no pot ser null.");
						return resposta;
					}
					if (enviament.getEntregaPostal().getViaNom() == null || enviament.getEntregaPostal().getViaNom().isEmpty()) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[VIA_NOM] El camp 'viaNom' no pot ser null en cas d'entrega ESTRANGER NORMALITZAT.");
						return resposta;
					}
					if (enviament.getEntregaPostal().getPaisCodi() == null || enviament.getEntregaPostal().getPaisCodi().isEmpty()) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[PAIS] El camp 'paisCodi' no pot ser null en cas d'entrega ESTRANGER NORMALITZAT.");
						return resposta;
					}
					if (enviament.getEntregaPostal().getPaisCodi().length() > 3) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[PAIS]  El camp 'paisCodi' de l'entrega postal no pot contenir més de 3 caràcters.");
						return resposta;
					}
					if (enviament.getEntregaPostal().getPoblacio() == null || enviament.getEntregaPostal().getPoblacio().isEmpty()) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[POBLACIO] El camp 'poblacio' no pot ser null en cas d'entrega ESTRANGER NORMALITZAT.");
						return resposta;
					}
				}
				if(enviament.getEntregaPostal().getTipus().equals(NotificaDomiciliConcretTipusEnumDto.APARTAT_CORREUS)) {
					if (enviament.getEntregaPostal().getApartatCorreus() == null || enviament.getEntregaPostal().getApartatCorreus().isEmpty()) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[APARTAT_CORREUS] El camp 'apartatCorreus' no pot ser null en cas d'entrega APARTAT CORREUS.");
						return resposta;
					}
					if (enviament.getEntregaPostal().getProvincia() == null || enviament.getEntregaPostal().getProvincia().isEmpty()) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[PROVINCIA] El camp 'provincia' no pot ser null en cas d'entrega APARTAT CORREUS.");
						return resposta;
					}
					if (enviament.getEntregaPostal().getProvincia().length() > 3) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[PROVINCIA]  El camp 'provincia' de l'entrega postal no pot contenir més de 2 caràcters.");
						return resposta;
					}
					if (enviament.getEntregaPostal().getPoblacio() == null || enviament.getEntregaPostal().getPoblacio().isEmpty()) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[POBLACIO] El camp 'poblacio' no pot ser null en cas d'entrega APARTAT CORREUS.");
						return resposta;
					}
					if (enviament.getEntregaPostal().getMunicipiCodi() == null || enviament.getEntregaPostal().getMunicipiCodi().isEmpty()) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[MUNICIPI_CODI] El camp 'municipiCodi' no pot ser null en cas d'entrega APARTAT CORREUS.");
						return resposta;
					}
				}
				if(!enviament.getEntregaPostal().getTipus().equals(NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR)) {
					if (enviament.getEntregaPostal().getViaNom().length() > 50) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[VIA_NOM] El camp 'viaNom' de l'entrega postal no pot contenir més de 50 caràcters.");
						return resposta;
					}
					if (enviament.getEntregaPostal().getNumeroCasa() != null && enviament.getEntregaPostal().getNumeroCasa().length() > 5) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[NUM_CASA] El camp 'numeroCasa' de l'entrega postal no pot contenir més de 5 caràcters.");
						return resposta;
					}
					if (enviament.getEntregaPostal().getNumeroQualificador() != null && !enviament.getEntregaPostal().getNumeroQualificador().isEmpty() && enviament.getEntregaPostal().getNumeroQualificador().length() > 3) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[NUM_QUAL] El camp 'numeroQualificador' de l'entrega postal no pot contenir més de 3 caràcters.");
						return resposta;
					}
				}
				if(enviament.getEntregaPostal().getTipus().equals(NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR)) {
					if (enviament.getEntregaPostal().getLinea1() == null || enviament.getEntregaPostal().getLinea1().isEmpty()) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[LINEA1] El camp 'linea1' no pot ser null.");
						return resposta;
					}
					if (enviament.getEntregaPostal().getLinea2() == null || enviament.getEntregaPostal().getLinea2().isEmpty()) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[LINEA2] El camp 'linea2' no pot ser null.");
						return resposta;
					}
					if (enviament.getEntregaPostal().getLinea1().length() > 50) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[LINEA1] El camp 'linea1' de l'entrega postal no pot contenir més de 50 caràcters.");
						return resposta;
					}
					if (enviament.getEntregaPostal().getLinea2().length() > 50) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[LINEA2] El camp 'linea2' de l'entrega postal no pot contenir més de 50 caràcters.");
						return resposta;
					}
					if (enviament.getEntregaPostal().getPaisCodi() == null || enviament.getEntregaPostal().getPaisCodi().isEmpty()) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[PAIS] El camp 'paisCodi' no pot ser null en cas d'entrega SENSE NORMALITZAR.");
						return resposta;
					}
					if (enviament.getEntregaPostal().getPaisCodi().length() > 3) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[PAIS]  El camp 'paisCodi' de l'entrega postal no pot contenir més de 3 caràcters.");
						return resposta;
					}
				}
				if(enviament.getEntregaPostal().getCodiPostal() == null || enviament.getEntregaPostal().getCodiPostal().isEmpty()) {
					resposta.setError(true);
					resposta.setEstat(NotificacioEstatEnum.PENDENT);
					resposta.setErrorDescripcio("[CODI_POSTAL] El camp 'codiPostal' no pot ser null (indicar 00000 en cas de no disposar del codi postal).");
					return resposta;
				}
			}

			if (!entitat.isAmbEntregaDeh() && enviament.isEntregaDehActiva()) {
				resposta.setError(true);
				resposta.setEstat(NotificacioEstatEnum.PENDENT);
				resposta.setErrorDescripcio("[ENTREGA_DEH] El camp 'entrega DEH' de l'entitat ha d'estar actiu en cas d'enviaments amb entrega DEH");
				return resposta;
			}
			if (enviament.isEntregaDehActiva() && enviament.getEntregaDeh() == null) {
				resposta.setError(true);
				resposta.setEstat(NotificacioEstatEnum.PENDENT);
				resposta.setErrorDescripcio("[ENTREGA_DEH] El camp 'entregaDeh' d'un enviament no pot ser null");
				return resposta;
			}
			if(enviament.getServeiTipus() == null) {
				resposta.setError(true);
				resposta.setEstat(NotificacioEstatEnum.PENDENT);
				resposta.setErrorDescripcio("[SERVEI_TIPUS] El camp 'serveiTipus' d'un enviament no pot ser null.");
				return resposta;
			}
			if(enviament.getTitular().getInteressatTipus() == null) {
				resposta.setError(true);
				resposta.setEstat(NotificacioEstatEnum.PENDENT);
				resposta.setErrorDescripcio("[TITULAR_INTERESSATTIPUS] El camp 'interessat_tipus' del titular d'un enviament no pot ser null.");
				return resposta;
			}
			
			if(enviament.getTitular().getInteressatTipus().equals(InteressatTipusEnumDto.FISICA)) {
				if (enviament.getTitular().getLlinatge1() == null) {
					resposta.setError(true);
					resposta.setEstat(NotificacioEstatEnum.PENDENT);
					resposta.setErrorDescripcio("[TITULAR_LLINATGE1] El camp 'llinatge1' del titular d'un enviament no pot ser null en el cas de persones físiques.");
					return resposta;
				}
				if (enviament.getTitular().getLlinatge1().length() > 40) {
					resposta.setError(true);
					resposta.setEstat(NotificacioEstatEnum.PENDENT);
					resposta.setErrorDescripcio("[LLINATGE1] El camp 'llinatge1' del titular no pot ser major que 40 caràcters.");
					return resposta;
				}
				if (enviament.getTitular().getLlinatge2() != null && enviament.getTitular().getLlinatge2().length() > 40) {
					resposta.setError(true);
					resposta.setEstat(NotificacioEstatEnum.PENDENT);
					resposta.setErrorDescripcio("[LLINATGE2] El camp 'llinatge2' del titular no pot ser major que 40 caràcters.");
					return resposta;
				}
				if(enviament.getTitular().getNif() == null) {
					resposta.setError(true);
					resposta.setEstat(NotificacioEstatEnum.PENDENT);
					resposta.setErrorDescripcio("[TITULAR_NIF] El camp 'nif' del titular d'un enviament no pot ser null.");
					return resposta;
				}
			} else if(enviament.getTitular().getInteressatTipus().equals(InteressatTipusEnumDto.JURIDICA)) {
				if(enviament.getTitular().getNif() == null) {
					resposta.setError(true);
					resposta.setEstat(NotificacioEstatEnum.PENDENT);
					resposta.setErrorDescripcio("[TITULAR_NIF] El camp 'nif' del titular d'un enviament no pot ser null.");
					return resposta;
				}
			} else if(enviament.getTitular().getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO)) {
				if(enviament.getTitular().getDir3Codi() == null) {
					resposta.setError(true);
					resposta.setEstat(NotificacioEstatEnum.PENDENT);
					resposta.setErrorDescripcio("[TITULAR_DIR3CAIB] El camp 'dir3codi' del titular d'un enviament no pot ser null.");
					return resposta;
				}
			}
			
			if (enviament.getDestinataris() != null) {
				for(Persona destinatari : enviament.getDestinataris()) {
					if(destinatari.getInteressatTipus() == null) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[DESTINATARI_INTERESSATTIPUS] El camp 'interessat_tipus' del destinatari d'un enviament no pot ser null.");
						return resposta;
					}
					if(destinatari.getNom() == null) {
						resposta.setError(true);
						resposta.setEstat(NotificacioEstatEnum.PENDENT);
						resposta.setErrorDescripcio("[DESTINATARI_NOM] El camp 'nom' del destinatari d'un enviament no pot ser null.");
						return resposta;
					}
					
					if(destinatari.getInteressatTipus().equals(InteressatTipusEnumDto.FISICA)) {
						if (destinatari.getLlinatge1() == null) {
							resposta.setError(true);
							resposta.setEstat(NotificacioEstatEnum.PENDENT);
							resposta.setErrorDescripcio("[DESTINATARI_LLINATGE1] El camp 'llinatge1' del destinatari d'un enviament no pot ser null en el cas de persones físiques.");
							return resposta;
						}
						if (destinatari.getLlinatge1().length() > 40) {
							resposta.setError(true);
							resposta.setEstat(NotificacioEstatEnum.PENDENT);
							resposta.setErrorDescripcio("[LLINATGE1] El camp 'llinatge1' del destinatari no pot ser major que 40 caràcters.");
							return resposta;
						}
						if (destinatari.getLlinatge2() != null && enviament.getTitular().getLlinatge2().length() > 40) {
							resposta.setError(true);
							resposta.setEstat(NotificacioEstatEnum.PENDENT);
							resposta.setErrorDescripcio("[LLINATGE2] El camp 'llinatge2' del destinatari no pot ser major que 40 caràcters.");
							return resposta;
						}
						if(destinatari.getNif() == null) {
							resposta.setError(true);
							resposta.setEstat(NotificacioEstatEnum.PENDENT);
							resposta.setErrorDescripcio("[DESTINATARI_NIF] El camp 'nif' del destinatari d'un enviament no pot ser null.");
							return resposta;
						}
					} else if(destinatari.getInteressatTipus().equals(InteressatTipusEnumDto.JURIDICA)) {
						if(destinatari.getNif() == null) {
							resposta.setError(true);
							resposta.setEstat(NotificacioEstatEnum.PENDENT);
							resposta.setErrorDescripcio("[DESTINATARI_NIF] El camp 'nif' del destinatari d'un enviament no pot ser null.");
							return resposta;
						}
					} else if(destinatari.getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO)) {
						if(destinatari.getDir3Codi() == null) {
							resposta.setError(true);
							resposta.setEstat(NotificacioEstatEnum.PENDENT);
							resposta.setErrorDescripcio("[DESTINATARI_NIF] El camp 'dir3codi' del destinatari d'un enviament no pot ser null.");
							return resposta;
						}
					}
				}
			}
		}
		if (comunicacioAmbAdministracio && comunicacioSenseAdministracio) {
			resposta.setError(true);
			resposta.setEstat(NotificacioEstatEnum.PENDENT);
			resposta.setErrorDescripcio("[COMUNICACIO] Una comunicació no pot estar dirigida a una administració i a una persona física/jurídica a la vegada.");
			return resposta;
		}
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
	
	private boolean validConcepteDescripcio(String value) {
		String CONTROL_CARACTERS = " aàáäbcçdeèéëfghiìíïjklmnñoòóöpqrstuùúüvwxyzAÀÁÄBCÇDEÈÉËFGHIÌÍÏJKLMNÑOÒÓÖPQRSTUÙÚÜVWXYZ0123456789-_'\"/:().,¿?!¡;";
		char[] concepte_chars = value.toCharArray();
		
		boolean esCaracterValid = true;
		for (int i = 0; esCaracterValid && i < concepte_chars.length; i++) {
			esCaracterValid = !(CONTROL_CARACTERS.indexOf(concepte_chars[i]) < 0);
			if (!esCaracterValid) {
				break;
			}
	    }
		return esCaracterValid;
	}
	
	private static String isMultipleDestinataris() {
		String property = "es.caib.notib.destinatari.multiple";
		logger.debug("Consulta del valor de la property (" +
				"property=" + property + ")");
		return PropertiesHelper.getProperties().getProperty(property);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(NotificacioServiceWsImplV2.class);

}
