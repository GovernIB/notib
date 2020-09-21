/**
 * 
 */
package es.caib.notib.core.service.ws;

import java.io.ByteArrayOutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jws.WebService;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.Timer;

import es.caib.notib.core.api.dto.NotificaDomiciliConcretTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliNumeracioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliViaTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto;
import es.caib.notib.core.api.dto.ServeiTipusEnumDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.ws.notificacio.Certificacio;
import es.caib.notib.core.api.ws.notificacio.ComunicacioTipusEnum;
import es.caib.notib.core.api.ws.notificacio.Document;
import es.caib.notib.core.api.ws.notificacio.EntregaPostalViaTipusEnum;
import es.caib.notib.core.api.ws.notificacio.Enviament;
import es.caib.notib.core.api.ws.notificacio.EnviamentEstatEnum;
import es.caib.notib.core.api.ws.notificacio.EnviamentReferencia;
import es.caib.notib.core.api.ws.notificacio.Notificacio;
import es.caib.notib.core.api.ws.notificacio.NotificacioEstatEnum;
import es.caib.notib.core.api.ws.notificacio.NotificacioServiceWs;
import es.caib.notib.core.api.ws.notificacio.NotificacioServiceWsException;
import es.caib.notib.core.api.ws.notificacio.Persona;
import es.caib.notib.core.api.ws.notificacio.RespostaAlta;
import es.caib.notib.core.api.ws.notificacio.RespostaConsultaEstatEnviament;
import es.caib.notib.core.api.ws.notificacio.RespostaConsultaEstatNotificacio;
import es.caib.notib.core.entity.DocumentEntity;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.entity.PagadorCieEntity;
import es.caib.notib.core.entity.PagadorPostalEntity;
import es.caib.notib.core.entity.PersonaEntity;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.helper.NotificaHelper;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.repository.DocumentRepository;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.repository.PagadorCieRepository;
import es.caib.notib.core.repository.PagadorPostalRepository;
import es.caib.notib.core.repository.PersonaRepository;


/**
 * Implementació del servei per a l'enviament i consulta de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
@WebService(
		name = "NotificacioService",
		serviceName = "NotificacioService",
		portName = "NotificacioServicePort",
		targetNamespace = "http://www.caib.es/notib/ws/notificacio",
		endpointInterface = "es.caib.notib.core.api.service.ws.NotificacioService")
public class NotificacioServiceWsImpl implements NotificacioServiceWs {

	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private NotificacioRepository notificacioRepository;
	@Autowired
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Autowired
	private PersonaRepository personaRepository;
	@Autowired
	private PagadorPostalRepository pagadorPostalRepository;
	@Autowired
	private PagadorCieRepository pagadorCieRepository;
	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	private NotificaHelper notificaHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private MetricsHelper metricsHelper;
	
	@Autowired
	private AplicacioService aplicacioService;

	@Transactional
	@Override
	public RespostaAlta alta(
			Notificacio notificacio) throws NotificacioServiceWsException {
		
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			String emisorDir3Codi = notificacio.getEmisorDir3Codi();
			RespostaAlta resposta = new RespostaAlta();
			UsuariDto usuariActual = aplicacioService.getUsuariActual();
	
			if (emisorDir3Codi == null) {
				resposta.setError(true);
				resposta.setEstat(NotificacioEstatEnum.PENDENT);
				resposta.setErrorDescripcio("[EMISOR] El camp 'emisorDir3Codi' no pot ser null.");
				return resposta;
			}
			EntitatEntity entitat = entitatRepository.findByDir3Codi(emisorDir3Codi);
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
			if (notificacio.getConcepte() == null) {
				resposta.setError(true);
				resposta.setEstat(NotificacioEstatEnum.PENDENT);
				resposta.setErrorDescripcio("[CONCEPTE] El concepte de la notificació no pot ser null.");
				return resposta;
			}
			if (notificacio.getEnviamentTipus() == null) {
				resposta.setError(true);
				resposta.setEstat(NotificacioEstatEnum.PENDENT);
				resposta.setErrorDescripcio("[ENVIAMENT_TIPUS] El tipus d'enviament de la notificació no pot ser null.");
				return resposta;
			}
			Document document = notificacio.getDocument();
			if (document == null) {
				resposta.setError(true);
				resposta.setEstat(NotificacioEstatEnum.PENDENT);
				resposta.setErrorDescripcio("[DOCUMENT] El camp 'document' no pot ser null.");
				return resposta;
			}
			String documentGesdocId = null;
			if(notificacio.getDocument().getContingutBase64() != null) {
				documentGesdocId = pluginHelper.gestioDocumentalCreate(
						PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
						Base64.decodeBase64(notificacio.getDocument().getContingutBase64()));
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
			NotificacioComunicacioTipusEnumDto comunicacioTipus = pluginHelper.getNotibTipusComunicacioDefecte();
			if (notificacio.getComunicacioTipus() != null && ComunicacioTipusEnum.SINCRON.equals(notificacio.getComunicacioTipus())) {
				comunicacioTipus = NotificacioComunicacioTipusEnumDto.SINCRON;
			}
			PagadorPostalEntity pagadorPostal = pagadorPostalRepository.saveAndFlush(PagadorPostalEntity.getBuilder(
					 notificacio.getPagadorPostal().getDir3Codi(),
					 notificacio.getPagadorPostal().getContracteNum(),
					 notificacio.getPagadorPostal().getContracteDataVigencia(),
					 notificacio.getPagadorPostal().getFacturacioClientCodi(),
					 entitat).build());
			
			PagadorCieEntity pagadorCie = pagadorCieRepository.saveAndFlush(PagadorCieEntity.getBuilder(
					notificacio.getPagadorCie().getDir3Codi(),
					notificacio.getPagadorCie().getContracteDataVigencia(),
					entitat).build());
			
			DocumentEntity documentEntity = documentRepository.saveAndFlush(DocumentEntity.getBuilder(
					notificacio.getDocument().getArxiuId(), 
					documentGesdocId, 
					notificacio.getDocument().getArxiuNom(),  
					notificacio.getDocument().getHash(),  
					notificacio.getDocument().getUrl(),  
					notificacio.getDocument().getMetadades(),  
					notificacio.getDocument().isNormalitzat(),  
					notificacio.getDocument().isGenerarCsv()).build());
			
			NotificacioEntity.BuilderV1 notificacioBuilder = NotificacioEntity.getBuilderV1(
					entitat,
					emisorDir3Codi,
					comunicacioTipus,
					enviamentTipus, 
					notificacio.getConcepte(),
					notificacio.getDescripcio(),
					notificacio.getEnviamentDataProgramada(),
					notificacio.getRetard(),
					notificacio.getCaducitat(),
					documentEntity,
					pagadorPostal,
					pagadorCie,
					notificacio.getEnviaments()).usuariCodi(usuariActual.getCodi());
			
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
				if (enviament.getEntregaPostal() != null) {
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
	//					tipus = NotificaDomiciliTipusEnumDto.CONCRETO;
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
				
				PersonaEntity titular = personaRepository.save(PersonaEntity.getBuilder(
						enviament.getTitular().getEmail(), 
						enviament.getTitular().getLlinatge1(), 
						enviament.getTitular().getLlinatge2(), 
						enviament.getTitular().getNif(), 
						enviament.getTitular().getNom(), 
						enviament.getTitular().getTelefon()).build());
				
				List<PersonaEntity> destinataris = new ArrayList<PersonaEntity>();
				for(Persona persona: enviament.getDestinataris()) {
					PersonaEntity destinatari = personaRepository.save(PersonaEntity.getBuilder(
							persona.getEmail(), 
							persona.getLlinatge1(), 
							persona.getLlinatge2(), 
							persona.getNif(), 
							persona.getNom(), 
							persona.getTelefon()).build());
					destinataris.add(destinatari);
				}			
				NotificacioEnviamentEntity enviamentSaved = notificacioEnviamentRepository.saveAndFlush(
						NotificacioEnviamentEntity.getBuilderV1(
								enviament, 
								numeracioTipus, 
								tipusConcret, 
								serveiTipus, 
								notificacioGuardada, 
								titular, 
								destinataris)
						.domiciliViaTipus(toEnviamentViaTipusEnum(enviament.getEntregaPostal().getViaTipus())).build());
				
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
				referencies.add(enviamentReferencia);
				notificacioGuardada.addEnviament(enviamentSaved);
			}
			notificacioRepository.saveAndFlush(notificacioGuardada);
			/*Mirar que tots els enviaments siguin amb titular del mateix tipus.*/
	//		Boolean esAdministracio = false;
			for(NotificacioEnviamentEntity enviament: notificacioGuardada.getEnviaments()) {
				enviament.getTitular();
			}
			if (NotificacioComunicacioTipusEnumDto.SINCRON.equals(pluginHelper.getNotibTipusComunicacioDefecte())) {
	//			for(NotificacioEnviamentEntity enviament : notificacioGuardada.getEnviaments()) {
	////				AsientoRegistralBean arb = pluginHelper.notificacioToAsientoRegistralBean(notificacioGuardada);
	////				RespostaConsultaRegistre arbResposta = null;
	//				if(NotificaEnviamentTipusEnumDto.COMUNICACIO.equals(notificacioGuardada.getEnviamentTipus())/*Administracio*/) {
	//					//Regweb3 + SIR
	////					arbResposta = pluginHelper.registreSortidaAsientoRegistral(entitat.getDir3Codi(), notificacioGuardada, enviament, 1L);
	////					if(arbResposta.getEstado().equals(EstatRegistre.DISTRIBUIT.geValorLong())) {
	////						
	////					}else if(arbResposta.getEstado().equals(EstatRegistre.OFICI_EXTERN.geValorLong())) {
	////						JustificanteWs justificant = pluginHelper.obtenerJustificante(entitat.getDir3Codi(), arbResposta.getNumeroRegistroFormateado(), arbResposta.getLibroCodigo(), 2L);
	////					}else if(arbResposta.getEstado().equals(EstatRegistre.OFICI_SIR.geValorLong())) {
	////						OficioBean oficiExtern = pluginHelper.obtenerOficioExterno(entitat.getDir3Codi(), arbResposta.getNumeroRegistroFormateado(), arbResposta.getLibroCodigo());
	////					}
	//				} else {
	//					//Regweb3 + Notifica
	//					try {
	////						arbResposta = pluginHelper.comunicarAsientoRegistral(entitat.getDir3Codi(), arb, 1L);
	////						notificacio.setRegistreNumero(arbResposta.getNumeroRegistroFormateado());
	////						notificacio.setRegistreData(arbResposta.getFechaRegistro().toGregorianCalendar().getTime());
	//						notificaHelper.notificacioEnviar(notificacioGuardada.getId());
	//						notificacioGuardada = notificacioRepository.findById(notificacioGuardada.getId());
	//					} catch (Exception e) {
	//						e.printStackTrace();
	//					}
	//				}
	//			}
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
			case FINALITZADA:
				resposta.setEstat(NotificacioEstatEnum.FINALITZADA);
				break;
			case PROCESSADA:
				resposta.setEstat(NotificacioEstatEnum.PROCESSADA);
				break;
			case REGISTRADA:
				resposta.setEstat(NotificacioEstatEnum.REGISTRADA);
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
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public RespostaConsultaEstatNotificacio consultaEstatNotificacio(
			String identificador) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			Long notificacioId;
			RespostaConsultaEstatNotificacio resposta = new RespostaConsultaEstatNotificacio();
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
				if (notificacio.getNotificaErrorEvent() != null) {
					resposta.setError(true);
					resposta.setErrorData(
							notificacio.getNotificaErrorEvent().getData());
					resposta.setErrorDescripcio(
							notificacio.getNotificaErrorEvent().getErrorDescripcio());
				}
			}
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
			NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findByNotificaReferencia(referencia);
			RespostaConsultaEstatEnviament resposta = new RespostaConsultaEstatEnviament();
			if (enviament == null) {
				resposta.setError(true);
				resposta.setErrorData(new Date());
				resposta.setErrorDescripcio("Error: No s'ha trobat cap enviament amb la referencia " + referencia);
				return resposta;
			} else {
	//			Es canosulta l'estat periòdicament, no es necessita realitzar una consulta actica a Notifica
				// Si Notib no utilitza el servei Adviser de @Notifica, i ja ha estat enviat a @Notifica
				// serà necessari consultar l'estat de la notificació a Notifica
				if (	!notificaHelper.isAdviserActiu() &&
						!enviament.isNotificaEstatFinal() &&
						!enviament.getNotificaEstat().equals(NotificacioEnviamentEstatEnumDto.NOTIB_PENDENT)) {
					notificaHelper.enviamentRefrescarEstat(enviament.getId());
				}
				resposta.setEstat(toEnviamentEstat(enviament.getNotificaEstat()));
				resposta.setEstatData(enviament.getNotificaEstatData());
				resposta.setEstatDescripcio(enviament.getNotificaEstatDescripcio());
				resposta.setReceptorNif(enviament.getNotificaDatatReceptorNif());
				resposta.setReceptorNom(enviament.getNotificaDatatReceptorNom());
				if (enviament.getNotificaCertificacioData() != null) {
					Certificacio certificacio = new Certificacio();
					certificacio.setData(enviament.getNotificaCertificacioData());
					certificacio.setOrigen(enviament.getNotificaCertificacioOrigen());
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					pluginHelper.gestioDocumentalGet(
							enviament.getNotificaCertificacioArxiuId(),
							PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS,
							baos);
					certificacio.setContingutBase64(Base64.encodeBase64String(baos.toByteArray()));
					certificacio.setTamany(enviament.getNotificaCertificacioTamany());
					certificacio.setHash(enviament.getNotificaCertificacioHash());
					certificacio.setMetadades(enviament.getNotificaCertificacioMetadades());
					certificacio.setCsv(enviament.getNotificaCertificacioCsv());
					certificacio.setTipusMime(enviament.getNotificaCertificacioMime());
					resposta.setCertificacio(certificacio);
				}
				if (enviament.isNotificaError()) {
					resposta.setError(true);
					NotificacioEventEntity errorEvent = enviament.getNotificacioErrorEvent();
					resposta.setErrorData(errorEvent.getData());
					resposta.setErrorDescripcio(errorEvent.getErrorDescripcio());
				}
			}
			return resposta;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
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
		default:
			return null;
		}
	}

}
