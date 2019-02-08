/**
 * 
 */
package es.caib.notib.core.service.ws;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jersey.core.util.Base64;

import es.caib.notib.core.api.dto.NotificaDomiciliConcretTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliNumeracioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliViaTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaServeiTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto;
import es.caib.notib.core.api.dto.RegistreAnotacioDto;
import es.caib.notib.core.api.exception.RegistrePluginException;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.ws.notificacio.Certificacio;
import es.caib.notib.core.api.ws.notificacio.ComunicacioTipusEnum;
import es.caib.notib.core.api.ws.notificacio.DocumentV2;
import es.caib.notib.core.api.ws.notificacio.EntregaDeh;
import es.caib.notib.core.api.ws.notificacio.EntregaPostal;
import es.caib.notib.core.api.ws.notificacio.EntregaPostalViaTipusEnum;
import es.caib.notib.core.api.ws.notificacio.EnviamentV2;
import es.caib.notib.core.api.ws.notificacio.EnviamentEstatEnum;
import es.caib.notib.core.api.ws.notificacio.EnviamentReferencia;
import es.caib.notib.core.api.ws.notificacio.NotificacioEstatEnum;
import es.caib.notib.core.api.ws.notificacio.NotificacioServiceWsException;
import es.caib.notib.core.api.ws.notificacio.NotificacioServiceWsV2;
import es.caib.notib.core.api.ws.notificacio.NotificacioV2;
import es.caib.notib.core.api.ws.notificacio.ParametresRegistre;
import es.caib.notib.core.api.ws.notificacio.PersonaV2;
import es.caib.notib.core.api.ws.notificacio.RespostaAlta;
import es.caib.notib.core.api.ws.notificacio.RespostaConsultaEstatEnviament;
import es.caib.notib.core.api.ws.notificacio.RespostaConsultaEstatNotificacio;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.NotificaHelper;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.repository.ProcedimentRepository;


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
	private NotificaHelper notificaHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private AplicacioService aplicacioService;
	
	



	@Transactional
	@Override
	public RespostaAlta alta(
			NotificacioV2 notificacio) throws NotificacioServiceWsException {
		String emisorDir3Codi = notificacio.getEmisorDir3Codi();
		if (emisorDir3Codi == null) {
			throw new ValidationException(
					"EMISOR", 
					"El camp 'emisorDir3Codi' no pot ser null.");
		}
		EntitatEntity entitat = entitatRepository.findByDir3Codi(emisorDir3Codi);
		if (entitat == null) {
			throw new ValidationException(
					"ENTITAT", 
					"No s'ha trobat cap entitat configurada a Notib amb el codi Dir3 " + emisorDir3Codi + ". (emisorDir3Codi)");
		}
		if (!entitat.isActiva()) {
			throw new ValidationException(
					"ENTITAT", 
					"L'entitat especificada està desactivada per a l'enviament de notificacions");
		}
		if (notificacio.getConcepte() == null) {
			throw new ValidationException(
					"CONCEPTE", 
					"El concepte de la notificació no pot ser null.");
		}
		if (notificacio.getEnviamentTipus() == null) {
			throw new ValidationException(
					"ENVIAMENT_TIPUS", 
					"El tipus d'enviament de la notificació no pot ser null.");
		}
		DocumentV2 document = notificacio.getDocument();
		if (document == null) {
			throw new ValidationException(
					"DOCUMENT",
					"El camp 'document' no pot ser null.");
		}
		String documentGesdocId = pluginHelper.gestioDocumentalCreate(
				PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
				new ByteArrayInputStream(
						Base64.decode(notificacio.getDocument().getContingutBase64())));
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
		NotificacioEntity.BuilderV2 notificacioBuilder = NotificacioEntity.getBuilderV2(
				entitat,
				emisorDir3Codi,
				comunicacioTipus,
				enviamentTipus, 
				notificacio.getConcepte(),
				notificacio.getDescripcio(),
				notificacio.getEnviamentDataProgramada(),
				notificacio.getRetard(),
				notificacio.getCaducitat(),
				notificacio.getDocument(),
				notificacio.getCodiProcediment(),
				notificacio.getCodiGrup(),
				notificacio.getEnviaments(),
				notificacio.getParametresRegistre()).
				descripcio(notificacio.getDescripcio()).
				caducitat(notificacio.getCaducitat()).
				retardPostal(notificacio.getRetard()).
				descripcio(notificacio.getDescripcio()).
				procedimentCodiNotib(notificacio.getCodiProcediment()).
				grupCodi(notificacio.getCodiGrup());
		
		ParametresRegistre parametresRegistre = notificacio.getParametresRegistre();
		if (parametresRegistre != null) {
			notificacioBuilder.
			registreOficina(parametresRegistre.getOficina()).
			registreLlibre(parametresRegistre.getLlibre());
		}
		
		
		NotificacioEntity notificacioEntity = notificacioBuilder.build();
		notificacioRepository.saveAndFlush(notificacioEntity);
		List<EnviamentReferencia> referencies = new ArrayList<EnviamentReferencia>();
		for (EnviamentV2 enviament: notificacio.getEnviaments()) {
			PersonaV2 titular = enviament.getTitular();
			if (titular == null) {
				throw new ValidationException(
						"TITULAR",
						"El camp 'titular' no pot ser null.");
			}
			NotificaServeiTipusEnumDto serveiTipus = null;
			if (enviament.getServeiTipus() != null) {
				switch (enviament.getServeiTipus()) {
				case NORMAL:
					serveiTipus = NotificaServeiTipusEnumDto.NORMAL;
					break;
				case URGENT:
					serveiTipus = NotificaServeiTipusEnumDto.URGENT;
					break;
				}
			}
			NotificacioEnviamentEntity.Builder enviamentBuilder = NotificacioEnviamentEntity.getBuilder(
					titular.getNif().toUpperCase(),
					serveiTipus,
					notificacioEntity).
					titularNom(titular.getNom()).
					titularLlinatge1(titular.getLlinatge1()).
					titularLlinatge2(titular.getLlinatge2()).
					titularTelefon(titular.getTelefon()).
					titularEmail(titular.getEmail()).
					titularRaoSocial(titular.getRaoSocial()).
					titularCodiDesti(titular.getCodiEntitatDesti());
			if (enviament.getDestinataris() != null) {
				if (enviament.getDestinataris().size() > 1) {
					throw new ValidationException(
							"DESTINATARI",
							"Únicament es pot indicar un destinatari");
				} else if (enviament.getDestinataris().size() == 1) {
					PersonaV2 destinatari = enviament.getDestinataris().get(0);
					enviamentBuilder.
					destinatariNif(destinatari.getNif().toUpperCase()).
					destinatariNom(destinatari.getNom()).
					destinatariLlinatge1(destinatari.getLlinatge1()).
					destinatariLlinatge2(destinatari.getLlinatge2()).
					destinatariTelefon(destinatari.getTelefon()).
					destinatariEmail(destinatari.getEmail()).
					destinatariRaoSocial(destinatari.getRaoSocial()).
					destinatariCodiDesti(destinatari.getCodiEntitatDesti());
				}
			}
			EntregaPostal entregaPostal = enviament.getEntregaPostal();
			if (entregaPostal != null) {
				NotificaDomiciliTipusEnumDto tipus = null;
				NotificaDomiciliConcretTipusEnumDto tipusConcret = null;
				if (entregaPostal.getTipus() != null) {
					switch (entregaPostal.getTipus()) {
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
					tipus = NotificaDomiciliTipusEnumDto.CONCRETO;
				} else {
					throw new ValidationException(
							"ENTREGA_POSTAL",
							"L'entrega postal te el camp tipus buit");
				}
				NotificaDomiciliNumeracioTipusEnumDto numeracioTipus = null;
				if (entregaPostal.getNumeroCasa() != null) {
					numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.NUMERO;
				} else if (entregaPostal.getApartatCorreus() != null) {
					numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.APARTAT_CORREUS;
				} else if (entregaPostal.getPuntKm() != null) {
					numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.PUNT_KILOMETRIC;
				} else {
					numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.SENSE_NUMERO;
				}
				enviamentBuilder.
				domiciliTipus(tipus).
				domiciliConcretTipus(tipusConcret).
				domiciliViaTipus(
						toEnviamentViaTipusEnum(entregaPostal.getViaTipus())).
				domiciliViaNom(entregaPostal.getViaNom()).
				domiciliNumeracioTipus(numeracioTipus).
				domiciliNumeracioNumero(entregaPostal.getNumeroCasa()).
				domiciliNumeracioPuntKm(entregaPostal.getPuntKm()).
				domiciliApartatCorreus(entregaPostal.getApartatCorreus()).
				domiciliBloc(entregaPostal.getBloc()).
				domiciliPortal(entregaPostal.getPortal()).
				domiciliEscala(entregaPostal.getEscala()).
				domiciliPlanta(entregaPostal.getPlanta()).
				domiciliPorta(entregaPostal.getPorta()).
				domiciliComplement(entregaPostal.getComplement()).
				domiciliCodiPostal(entregaPostal.getCodiPostal()).
				domiciliPoblacio(entregaPostal.getPoblacio()).
				domiciliMunicipiCodiIne(entregaPostal.getMunicipiCodi()).
				domiciliProvinciaCodi(entregaPostal.getProvinciaCodi()).
				domiciliPaisCodiIso(entregaPostal.getPaisCodi()).
				domiciliLinea1(entregaPostal.getLinea1()).
				domiciliLinea2(entregaPostal.getLinea2()).
				domiciliCie(entregaPostal.getCie());
			}
			EntregaDeh entregaDeh = enviament.getEntregaDeh();
			if (entregaDeh != null) {
				enviamentBuilder.
				dehObligat(entregaDeh.isObligat()).
				dehNif(titular.getNif().toUpperCase()).
				dehProcedimentCodi(entregaDeh.getProcedimentCodi());
			}
			NotificacioEnviamentEntity enviamentSaved = notificacioEnviamentRepository.saveAndFlush(
					enviamentBuilder.build());
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
			enviamentReferencia.setTitularNif(titular.getNif().toUpperCase());
			enviamentReferencia.setReferencia(referencia);
			referencies.add(enviamentReferencia);
			notificacioEntity.addEnviament(enviamentSaved);
		}
		notificacioRepository.saveAndFlush(notificacioEntity);
		if (NotificacioComunicacioTipusEnumDto.SINCRON.equals(notificacioEntity.getComunicacioTipus())) {
			if(NotificaEnviamentTipusEnumDto.COMUNICACIO.equals(notificacioEntity.getEnviamentTipus()) && notificacioEntity.getEnviaments().get(0).getTitularCodiDesti().equals("Administració")) {
				//TODO: Registrar SIR
			} else {
				//TODO: Registrar Normal
				try {
					pluginHelper.registrarSortida(pluginHelper.notificacioToRegistreAnotacio(notificacioEntity), "NOTIB", aplicacioService.getVersioActual());
				} catch (RegistrePluginException e) {
					e.getMessage();
				}
				notificaHelper.notificacioEnviar(notificacioEntity.getId());
				notificacioEntity = notificacioRepository.findOne(notificacioEntity.getId());
			}
		}
		RespostaAlta resposta = new RespostaAlta();
		try {
			resposta.setIdentificador(
					notificaHelper.xifrarId(notificacioEntity.getId()));
		} catch (GeneralSecurityException ex) {
			throw new RuntimeException(
					"No s'ha pogut crear l'identificador de la notificació",
					ex);
		}
		switch (notificacioEntity.getEstat()) {
		case PENDENT:
			resposta.setEstat(NotificacioEstatEnum.PENDENT);
			break;
		case ENVIADA:
			resposta.setEstat(NotificacioEstatEnum.ENVIADA);
			break;
		case FINALITZADA:
			resposta.setEstat(NotificacioEstatEnum.FINALITZADA);
			break;
		}
		if (notificacioEntity.getNotificaErrorEvent() != null) {
			resposta.setError(true);
			resposta.setErrorDescripcio(
					notificacioEntity.getNotificaErrorEvent().getErrorDescripcio());
		}
		resposta.setReferencies(referencies);
		return resposta;
	}
	
//	
//	public void registrarSortidaAnotacio(Long notificacioId){
//		NotificacioEntity notificacio = notificacioRepository.findOne(notificacioId);
//		RegistreAnotacioDto registreAnotacio = new RegistreAnotacioDto();
//
//		
//		registreAnotacio.getOficina();
//		registreAnotacio.getLlibre();
//		registreAnotacio.getAssumpteExtracte();
//		registreAnotacio.getDocumentacioFisica();
//		registreAnotacio.getAssumpteIdiomaCodi();
//		registreAnotacio.getAssumpteTipus();		
//		registreAnotacio.getExpedientNumero();
//		registreAnotacio.getAssumpteCodi();
//		registreAnotacio.getObservacions();
//		
//		
////		registreAnotacio.set
//		
//		/*Notificació*/
//		notificacio.getEmisorDir3Codi();
//		notificacio.getComunicacioTipus();
//		notificacio.getEnviamentTipus();
//		notificacio.getConcepte();
//		notificacio.getDescripcio();
//		notificacio.getNotificaEnviamentData();
//		notificacio.getCaducitat();
//		notificacio.getProcedimentCodiNotib();
//		/*-----------*/
//		
//		
//
//		/*Document*/
//		notificacio.getDocumentArxiuId();
//		DocumentDto document = new DocumentDto();
//		document.getArxiuNom();
//		document.getContingutBase64();
//		document.getHash();
//		document.getUrl();
//		document.getMetadades();
//		document.isNormalitzat();
//		document.isGenerarCsv();
//		/*--------*/
//		
//		for(NotificacioEnviamentEntity enviament : notificacio.getEnviaments()) {
//			enviament.getNotificaReferencia();
//			enviament.getServeiTipus();
//			
//			/*Titular*/
//			enviament.getTitularNom();
//			enviament.getTitularLlinatge1();
//			enviament.getTitularLlinatge2();
//			enviament.getTitularNif();
//			enviament.getTitularTelefon();
//			enviament.getTitularEmail();
//			/*------*/
//			
//			/*Destinataris*/
//			enviament.getDestinatariNom();
//			enviament.getDestinatariLlinatge1();
//			enviament.getDestinatariLlinatge2();
//			enviament.getDestinatariNif();
//			enviament.getDestinatariTelefon();
//			enviament.getDestinatariEmail();
//			/*------------*/
//			
//			
//			/*Entrega Postal*/
//			enviament.getDomiciliTipus();
//			enviament.getDomiciliViaTipus();
//			enviament.getDomiciliViaNom();
//			enviament.getDomiciliNumeracioNumero();
//			enviament.getDomiciliNumeracioQualificador();
//			enviament.getDomiciliNumeracioPuntKm();
//			enviament.getDomiciliApartatCorreus();
//			enviament.getDomiciliPortal();
//			enviament.getDomiciliEscala();
//			enviament.getDomiciliPlanta();
//			enviament.getDomiciliPorta();
//			enviament.getDomiciliBloc();
//			enviament.getDomiciliComplement();
//			enviament.getDomiciliCodiPostal();
//			enviament.getDomiciliPoblacio();
//			enviament.getDomiciliMunicipiCodiIne();
//			enviament.getDomiciliProvinciaCodi();
//			enviament.getDomiciliPaisCodiIso();
//			enviament.getDomiciliLinea1();
//			enviament.getDomiciliLinea2();
//			enviament.getDomiciliCie();
//			enviament.getFormatSobre();
//			enviament.getFormatFulla();
//			/*-------------*/
//			
//			/*Entrega DEH*/
//			enviament.getDehObligat();
//			enviament.getDehProcedimentCodi();
//			/*-----------*/
//		}
//		
////		ProcedimentDto procediment = procedimentRepository.findByCodi(notificacio.getProcedimentCodiNotib());
//		
////		registreAnotacio.setUnitatAdministrativa(notificacio.getSeuExpedientUnitatOrganitzativa());
////		registreAnotacio.setOficina(notificacio.getSeuRegistreOficina());
////		registreAnotacio.setLlibre(notificacio.getSeuRegistreLlibre());
////		registreAnotacio.setEntitatCodi(notificacio.getCifEntitat());
////		registreAnotacio.setAssumpteIdiomaCodi("ca");
////		registreAnotacio.setOrgan(notificacio.getPagadorCieCodiDir3());
////		registreAnotacio.setExpedientNumero(notificacio.getSeuExpedientIdentificadorEni());
////		notificacio.get
//		//		registreAnotacio.
////		registreAnotacio.
////		registreAnotacio.
////		registreAnotacio.
////		registreAnotacio.
//		try {
//			pluginHelper.registrarSortida(registreAnotacio, "notib", "0.1");
//		} catch (RegistrePluginException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}


	@Override
	@Transactional
	public RespostaConsultaEstatNotificacio consultaEstatNotificacio(
			String identificador) {
		Long notificacioId;
		try {
			notificacioId = notificaHelper.desxifrarId(identificador);
		} catch (GeneralSecurityException ex) {
			throw new RuntimeException(
					"No s'ha pogut desxifrar l'identificador de la notificació",
					ex);
		}
		NotificacioEntity notificacio = notificacioRepository.findOne(notificacioId);
		RespostaConsultaEstatNotificacio resposta = new RespostaConsultaEstatNotificacio();
		switch (notificacio.getEstat()) {
		case PENDENT:
			resposta.setEstat(NotificacioEstatEnum.PENDENT);
			break;
		case ENVIADA:
			resposta.setEstat(NotificacioEstatEnum.ENVIADA);
			break;
		case FINALITZADA:
			resposta.setEstat(NotificacioEstatEnum.FINALITZADA);
			break;
		}
		if (notificacio.getNotificaErrorEvent() != null) {
			resposta.setError(true);
			resposta.setErrorData(
					notificacio.getNotificaErrorEvent().getData());
			resposta.setErrorDescripcio(
					notificacio.getNotificaErrorEvent().getErrorDescripcio());
		}
		return resposta;
	}

	@Override
	@Transactional
	public RespostaConsultaEstatEnviament consultaEstatEnviament(
			String referencia) throws NotificacioServiceWsException {
		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findByNotificaReferencia(referencia);
		RespostaConsultaEstatEnviament resposta = new RespostaConsultaEstatEnviament();
		if (enviament == null) {
			// Error de no trobat
			throw new ValidationException(
					"REFERENCIA",
					"Error: No s'ha trobat cap notificació amb la referencia " + referencia);
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
				certificacio.setContingutBase64(
						new String(Base64.encode(baos.toByteArray())));
				certificacio.setTamany(enviament.getNotificaCertificacioTamany());
				certificacio.setHash(enviament.getNotificaCertificacioHash());
				certificacio.setMetadades(enviament.getNotificaCertificacioMetadades());
				certificacio.setCsv(enviament.getNotificaCertificacioCsv());
				certificacio.setTipusMime(enviament.getNotificaCertificacioMime());
				resposta.setCertificacio(certificacio);
			}
			if (enviament.isNotificaError()) {
				resposta.setError(true);
				NotificacioEventEntity errorEvent = enviament.getNotificaErrorEvent();
				resposta.setErrorData(errorEvent.getData());
				resposta.setErrorDescripcio(errorEvent.getErrorDescripcio());
			}
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
		default:
			return null;
		}
	}

}
