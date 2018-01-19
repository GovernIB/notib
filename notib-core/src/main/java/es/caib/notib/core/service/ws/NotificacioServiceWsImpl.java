/**
 * 
 */
package es.caib.notib.core.service.ws;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jws.WebService;
import javax.validation.ValidationException;

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
import es.caib.notib.core.api.dto.NotificacioDestinatariEstatEnumDto;
import es.caib.notib.core.api.ws.notificacio.Certificacio;
import es.caib.notib.core.api.ws.notificacio.Document;
import es.caib.notib.core.api.ws.notificacio.EntregaDeh;
import es.caib.notib.core.api.ws.notificacio.EntregaPostal;
import es.caib.notib.core.api.ws.notificacio.EntregaPostalTipusEnum;
import es.caib.notib.core.api.ws.notificacio.EntregaPostalViaTipusEnum;
import es.caib.notib.core.api.ws.notificacio.Enviament;
import es.caib.notib.core.api.ws.notificacio.EnviamentEstatEnum;
import es.caib.notib.core.api.ws.notificacio.EnviamentTipusEnum;
import es.caib.notib.core.api.ws.notificacio.InformacioEnviament;
import es.caib.notib.core.api.ws.notificacio.Notificacio;
import es.caib.notib.core.api.ws.notificacio.NotificacioServiceWs;
import es.caib.notib.core.api.ws.notificacio.NotificacioServiceWsException;
import es.caib.notib.core.api.ws.notificacio.PagadorCie;
import es.caib.notib.core.api.ws.notificacio.PagadorPostal;
import es.caib.notib.core.api.ws.notificacio.ParametresSeu;
import es.caib.notib.core.api.ws.notificacio.Persona;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.helper.NotificaHelper;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.helper.PropertiesHelper;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioRepository;


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
	private NotificaHelper notificaHelper;
	@Autowired
	private PluginHelper pluginHelper;



	@Transactional
	@Override
	public List<String> alta(
			Notificacio notificacio) throws NotificacioServiceWsException {
		String emisorDir3Codi = notificacio.getEmisorDir3Codi();
		if (emisorDir3Codi == null) {
			// TODO Error de validació
			throw new ValidationException("El camp 'emisorDir3Codi' no pot ser null.");
		}
		EntitatEntity entitat = entitatRepository.findByDir3Codi(emisorDir3Codi);
		if (entitat == null) {
			// TODO Error de validació
			throw new ValidationException("No s'ha trobat cap entitat configurada a Notib amb el codi Dir3 " + emisorDir3Codi + ". (emisorDir3Codi)");
		}
		Document document = notificacio.getDocument();
		if (document == null) {
			// TODO Error de validació
			throw new ValidationException("El camp 'document' no pot ser null.");
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
		NotificacioEntity.Builder notificacioBuilder = NotificacioEntity.getBuilder(
				entitat,
				emisorDir3Codi,
				enviamentTipus, 
				notificacio.getEnviamentDataProgramada(),
				notificacio.getConcepte(),
				document.getArxiuNom(),
				documentGesdocId,
				document.getHash(),
				document.isNormalitzat(),
				document.isGenerarCsv(),
				null).
				descripcio(notificacio.getDescripcio()).
				caducitat(notificacio.getCaducitat()).
				retardPostal(notificacio.getRetard()).
				descripcio(notificacio.getDescripcio()).
				procedimentCodiSia(notificacio.getProcedimentCodi());
		PagadorPostal pagadorPostal = notificacio.getPagadorPostal();
		if (pagadorPostal != null) {
			notificacioBuilder.
			pagadorCorreusCodiDir3(pagadorPostal.getDir3Codi()).
			pagadorCorreusContracteNum(pagadorPostal.getContracteNum()).
			pagadorCorreusCodiClientFacturacio(pagadorPostal.getFacturacioClientCodi()).
			pagadorCorreusDataVigencia(pagadorPostal.getContracteDataVigencia());
		}
		PagadorCie pagadorCie = notificacio.getPagadorCie();
		if (pagadorCie != null) {
			notificacioBuilder.
			pagadorCieCodiDir3(pagadorCie.getDir3Codi()).
			pagadorCieDataVigencia(pagadorCie.getContracteDataVigencia());
		}
		ParametresSeu parametresSeu = notificacio.getParametresSeu();
		if (parametresSeu != null) {
			notificacioBuilder.
			seuExpedientSerieDocumental(parametresSeu.getExpedientSerieDocumental()).
			seuExpedientUnitatOrganitzativa(parametresSeu.getExpedientUnitatOrganitzativa()).
			seuAvisTitol(parametresSeu.getAvisTitol()).
			seuAvisText(parametresSeu.getAvisText()).
			seuAvisTextMobil(parametresSeu.getAvisTextMobil()).
			seuOficiTitol(parametresSeu.getOficiTitol()).
			seuOficiText(parametresSeu.getOficiText()).
			seuRegistreLlibre(parametresSeu.getRegistreLlibre()).
			seuRegistreOficina(parametresSeu.getRegistreOficina()).
			seuIdioma(parametresSeu.getIdioma()).
			seuExpedientTitol(parametresSeu.getExpedientTitol()).
			seuExpedientIdentificadorEni(parametresSeu.getExpedientIdentificadorEni());
		}
		NotificacioEntity notificacioEntity = notificacioBuilder.build();
		notificacioRepository.saveAndFlush(notificacioEntity);
		List<String> referencies = new ArrayList<String>();
		List<NotificacioEnviamentEntity> enviaments = new ArrayList<NotificacioEnviamentEntity>();
		for (Enviament enviament: notificacio.getEnviaments()) {
			Persona titular = enviament.getTitular();
			if (titular == null) {
				// TODO Error de validació
				throw new ValidationException("El camp 'titular' no pot ser null.");
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
			NotificacioEnviamentEntity.Builder destinatariBuilder = NotificacioEnviamentEntity.getBuilder(
					titular.getNif(),
					serveiTipus,
					notificacioEntity).
					titularNom(titular.getNom()).
					titularLlinatge1(titular.getLlinatge1()).
					titularLlinatge2(titular.getLlinatge2()).
					titularTelefon(titular.getTelefon()).
					titularEmail(titular.getEmail());
			if (enviament.getDestinataris() != null) {
				if (enviament.getDestinataris().size() != 1) {
					// TODO Error de validació
					throw new ValidationException(
							"Únicament es pot indicar un destinatari");
				}
				Persona destinatari = enviament.getDestinataris().get(0);
				destinatariBuilder.
				destinatariNif(destinatari.getNif()).
				destinatariNom(destinatari.getNom()).
				destinatariLlinatge1(destinatari.getLlinatge1()).
				destinatariLlinatge2(destinatari.getLlinatge2()).
				destinatariTelefon(destinatari.getTelefon()).
				destinatariEmail(destinatari.getEmail());
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
				destinatariBuilder.
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
				destinatariBuilder.
				dehObligat(entregaDeh.isObligat()).
				dehNif(titular.getNif()).
				dehProcedimentCodi(entregaDeh.getProcedimentCodi());
			}
			NotificacioEnviamentEntity enviamentEntity = destinatariBuilder.
					//serveiTipus(NotificaServeiTipusEnumDto serveiTipus).
					//formatSobre(String formatSobre).
					//formatFulla(String formatFulla).
					build();
			NotificacioEnviamentEntity enviamentSaved = notificacioEnviamentRepository.saveAndFlush(
					enviamentEntity);
			String referencia;
			try {
				referencia = notificaHelper.generarReferencia(enviamentSaved);
			} catch (GeneralSecurityException ex) {
				throw new RuntimeException(
						"No s'ha pogut crear la referencia per al destinatari",
						ex);
			}
			enviamentSaved.updateNotificaReferencia(referencia);
			referencies.add(referencia);
			enviaments.add(enviamentSaved);
		}
		notificacioEntity.updateEnviaments(enviaments);
		notificacioRepository.saveAndFlush(notificacioEntity);
		if (getEnviamentSincronProperty()) {
			notificaHelper.enviament(notificacioEntity.getId());
		}
		return referencies;
	}

	@Override
	@Transactional
	public InformacioEnviament consulta(
			String referencia) throws NotificacioServiceWsException {
		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findByNotificaReferencia(referencia);
		InformacioEnviament informacioEnviament = new InformacioEnviament();
		if (enviament == null) {
			// Error de no trobat
			throw new ValidationException("Error: No s'ha trobat cap notificació amb la referencia " + referencia);
		} else {
			// Si Notib no utilitza el servei Adviser de @Notifica, i ja ha estat enviat a @Notifica
			// serà necessari consultar l'estat de la notificació a Notifica
			if (	!notificaHelper.isAdviserActiu() &&
					!enviament.getNotificaEstat().equals(NotificacioDestinatariEstatEnumDto.NOTIB_PENDENT)) {
				notificaHelper.refrescarEstat(enviament);
			}
			NotificacioEntity notificacio = enviament.getNotificacio();
			informacioEnviament.setConcepte(notificacio.getConcepte());
			informacioEnviament.setDescripcio(notificacio.getDescripcio());
			informacioEnviament.setEmisorDir3Codi(notificacio.getEmisorDir3Codi());
			// informacioEnviament.setEmisorDir3Descripcio(notificacio.getEmisorDir3Descripcio());
			// informacioEnviament.setEmisorArrelDir3Codi(notificacio.getEmisorArrelDir3Codi());
			// informacioEnviament.setEmisorArrelDir3Descripcio(notificacio.getEmisorArrelDir3Descripcio());
			// informacioEnviament.setDestiDir3Codi(notificacio.getDestiDir3Codi());
			// informacioEnviament.setDestiDir3Descripcio(notificacio.getDestiDir3Descripcio());
			EnviamentTipusEnum enviamentTipus = null;
			if (notificacio.getEnviamentTipus() != null) {
				switch (notificacio.getEnviamentTipus()) {
				case COMUNICACIO:
					enviamentTipus = EnviamentTipusEnum.COMUNICACIO;
					break;
				case NOTIFICACIO:
					enviamentTipus = EnviamentTipusEnum.NOTIFICACIO;
					break;
				}
			}
			informacioEnviament.setEnviamentTipus(enviamentTipus);
			informacioEnviament.setDataCreacio(enviament.getNotificaDataCreacio());
			informacioEnviament.setDataPostaDisposicio(enviament.getNotificaDataDisposicio());
			informacioEnviament.setDataCaducitat(notificacio.getCaducitat());
			informacioEnviament.setRetard(notificacio.getRetardPostal());
			informacioEnviament.setProcedimentCodi(notificacio.getProcedimentCodiSia());
			informacioEnviament.setProcedimentDescripcio(notificacio.getProcedimentDescripcioSia());
			informacioEnviament.setReferencia(enviament.getNotificaReferencia());
			Persona titular = new Persona();
			titular.setNif(enviament.getTitularNif());
			titular.setNom(enviament.getTitularNom());
			titular.setLlinatge1(enviament.getTitularLlinatge1());
			titular.setLlinatge2(enviament.getTitularLlinatge2());
			titular.setTelefon(enviament.getTitularTelefon());
			titular.setEmail(enviament.getTitularEmail());
			informacioEnviament.setTitular(titular);
			if (enviament.getDestinatariNif() != null) {
				Persona destinatari = new Persona();
				destinatari.setNif(enviament.getDestinatariNif());
				destinatari.setNom(enviament.getDestinatariNom());
				destinatari.setLlinatge1(enviament.getDestinatariLlinatge1());
				destinatari.setLlinatge2(enviament.getDestinatariLlinatge2());
				destinatari.setTelefon(enviament.getDestinatariTelefon());
				destinatari.setEmail(enviament.getDestinatariEmail());
				informacioEnviament.setDestinataris(Arrays.asList(destinatari));
			}
			if (enviament.getDomiciliTipus() != null || enviament.getDomiciliConcretTipus() != null) {
				EntregaPostal entregaPostal = new EntregaPostal();
				EntregaPostalTipusEnum tipus = null;
				if (enviament.getDomiciliConcretTipus() != null) {
					switch (enviament.getDomiciliConcretTipus()) {
					case APARTAT_CORREUS:
						tipus = EntregaPostalTipusEnum.APARTAT_CORREUS;
						break;
					case ESTRANGER:
						tipus = EntregaPostalTipusEnum.ESTRANGER;
						break;
					case NACIONAL:
						tipus = EntregaPostalTipusEnum.NACIONAL;
						break;
					case SENSE_NORMALITZAR:
						tipus = EntregaPostalTipusEnum.SENSE_NORMALITZAR;
						break;
					}
				}
				entregaPostal.setTipus(tipus);
				entregaPostal.setViaTipus(
						toEntregaPostalViaTipusEnum(enviament.getDomiciliViaTipus()));
				entregaPostal.setViaNom(enviament.getDomiciliViaNom());
				entregaPostal.setNumeroCasa(enviament.getDomiciliNumeracioNumero());
				//entregaPostal.setNumeroQualificador(numeroQualificador);
				entregaPostal.setPuntKm(enviament.getDomiciliNumeracioPuntKm());
				entregaPostal.setApartatCorreus(enviament.getDomiciliApartatCorreus());
				entregaPostal.setPortal(enviament.getDomiciliPortal());
				entregaPostal.setEscala(enviament.getDomiciliEscala());
				entregaPostal.setPlanta(enviament.getDomiciliPlanta());
				entregaPostal.setPorta(enviament.getDomiciliPorta());
				entregaPostal.setBloc(enviament.getDomiciliBloc());
				entregaPostal.setComplement(enviament.getDomiciliComplement());
				entregaPostal.setCodiPostal(enviament.getDomiciliCodiPostal());
				entregaPostal.setPoblacio(enviament.getDomiciliPoblacio());
				entregaPostal.setMunicipiCodi(enviament.getDomiciliMunicipiCodiIne());
				entregaPostal.setProvinciaCodi(enviament.getDomiciliProvinciaCodi());
				entregaPostal.setPaisCodi(enviament.getDomiciliPaisCodiIso());
				entregaPostal.setLinea1(enviament.getDomiciliLinea1());
				entregaPostal.setLinea2(enviament.getDomiciliLinea2());
				entregaPostal.setCie(enviament.getDomiciliCie());
				//entregaPostal.setFormatSobre(formatSobre);
				//entregaPostal.setFormatFulla(formatFulla);
				informacioEnviament.setEntregaPostal(entregaPostal);
			}
			if (enviament.getDehProcedimentCodi() != null) {
				EntregaDeh entregaDeh = new EntregaDeh();
				entregaDeh.setObligat(enviament.getDehObligat());
				entregaDeh.setProcedimentCodi(enviament.getDehProcedimentCodi());
				informacioEnviament.setEntregaDeh(entregaDeh);
			}
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
				certificacio.setTipusMime(enviament.getNotificaCertificacioTipusMime());
				informacioEnviament.setCertificacio(certificacio);
			}
			informacioEnviament.setEstat(
					toEnviamentEstat(
							NotificacioEnviamentEntity.calcularEstatCombinatNotificaSeu(enviament)));
			informacioEnviament.setEstatData(
					NotificacioEnviamentEntity.calcularDataCombinadaNotificaSeu(enviament));
			informacioEnviament.setNotificaEstat(
					toEnviamentEstat(enviament.getNotificaEstat()));
			informacioEnviament.setNotificaEstatData(
					enviament.getNotificaEstatData());
			informacioEnviament.setNotificaError(enviament.isNotificaError());
			if (enviament.getNotificaErrorEvent() != null) {
				NotificacioEventEntity event = enviament.getNotificaErrorEvent();
				informacioEnviament.setNotificaErrorData(
						event.getData());
				informacioEnviament.setNotificaErrorDescripcio(
						event.getErrorDescripcio());
			}
			informacioEnviament.setSeuEstat(
					toEnviamentEstat(enviament.getSeuEstat()));
			informacioEnviament.setSeuEstatData(
					enviament.getSeuDataEstat());
			informacioEnviament.setSeuError(enviament.isSeuError());
			if (enviament.getSeuErrorEvent() != null) {
				NotificacioEventEntity event = enviament.getSeuErrorEvent();
				informacioEnviament.setSeuErrorData(
						event.getData());
				informacioEnviament.setSeuErrorDescripcio(
						event.getErrorDescripcio());
			}
		}
		return informacioEnviament;
	}



	private NotificaDomiciliViaTipusEnumDto toEnviamentViaTipusEnum(
			EntregaPostalViaTipusEnum viaTipus) {
		if (viaTipus == null) {
			return null;
		}
		return NotificaDomiciliViaTipusEnumDto.valueOf(viaTipus.name());
	}
	private EntregaPostalViaTipusEnum toEntregaPostalViaTipusEnum(
			NotificaDomiciliViaTipusEnumDto viaTipus) {
		if (viaTipus == null) {
			return null;
		}
		return EntregaPostalViaTipusEnum.valueOf(viaTipus.name());
	}
	private EnviamentEstatEnum toEnviamentEstat(NotificacioDestinatariEstatEnumDto estat) {
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

	private boolean getEnviamentSincronProperty() {
		String enviamentSincron = PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.enviament.sincron");
		if (enviamentSincron != null) {
			return new Boolean(enviamentSincron);
		} else {
			return false;
		}
	}

}
