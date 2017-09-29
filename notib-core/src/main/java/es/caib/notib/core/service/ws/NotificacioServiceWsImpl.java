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
import es.caib.notib.core.api.ws.notificacio2.Certificacio;
import es.caib.notib.core.api.ws.notificacio2.Document;
import es.caib.notib.core.api.ws.notificacio2.EntregaDeh;
import es.caib.notib.core.api.ws.notificacio2.EntregaPostal;
import es.caib.notib.core.api.ws.notificacio2.EntregaPostalTipusEnum;
import es.caib.notib.core.api.ws.notificacio2.EntregaPostalViaTipusEnum;
import es.caib.notib.core.api.ws.notificacio2.Enviament;
import es.caib.notib.core.api.ws.notificacio2.EnviamentEstatEnum;
import es.caib.notib.core.api.ws.notificacio2.EnviamentTipusEnum;
import es.caib.notib.core.api.ws.notificacio2.InformacioEnviament;
import es.caib.notib.core.api.ws.notificacio2.Notificacio;
import es.caib.notib.core.api.ws.notificacio2.NotificacioServiceWs;
import es.caib.notib.core.api.ws.notificacio2.NotificacioServiceWsException;
import es.caib.notib.core.api.ws.notificacio2.PagadorCie;
import es.caib.notib.core.api.ws.notificacio2.PagadorPostal;
import es.caib.notib.core.api.ws.notificacio2.ParametresSeu;
import es.caib.notib.core.api.ws.notificacio2.Persona;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.helper.NotificaHelper;
import es.caib.notib.core.helper.PluginHelper;
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
		}
		EntitatEntity entitat = entitatRepository.findByDir3Codi(emisorDir3Codi);
		if (emisorDir3Codi == null) {
			// TODO Error de validació
		}
		Document document = notificacio.getDocument();
		if (document == null) {
			// TODO Error de validació
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
					retardPostal(notificacio.getRetard()).
					caducitat(notificacio.getCaducitat()).
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
		// TODO decidir si es fa l'enviament immediatament o si s'espera
		// a que l'envii la tasca programada.
		// notificaHelper.intentarEnviament(notificacioEntity);
		return referencies;
	}

	@Override
	public InformacioEnviament consulta(
			String referencia) throws NotificacioServiceWsException {
		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findByNotificaReferencia(referencia);
		if (enviament == null) {
			// TODO Error de no trobat
		}
		NotificacioEntity notificacio = enviament.getNotificacio();
		InformacioEnviament informacioEnviament = new InformacioEnviament();
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
		informacioEnviament.setDataCaducitat(enviament.getCaducitat());
		informacioEnviament.setRetard(enviament.getRetardPostal());
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
		informacioEnviament.setNotificaEstat(
				toEnviamentEstat(enviament.getNotificaEstat()));
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
		informacioEnviament.setSeuError(enviament.isSeuError());
		if (enviament.getSeuErrorEvent() != null) {
			NotificacioEventEntity event = enviament.getSeuErrorEvent();
			informacioEnviament.setSeuErrorData(
					event.getData());
			informacioEnviament.setSeuErrorDescripcio(
					event.getErrorDescripcio());
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
	/*private NotificacioDto toNotificacioDto(Notificacio notificacio) {
		NotificacioDto dto = new NotificacioDto();
		NotificaEnviamentTipusEnumDto enviamentTipus = null;
		switch (notificacio.getEnviamentTipus()) {
		case COMUNICACIO:
			enviamentTipus = NotificaEnviamentTipusEnumDto.COMUNICACIO;
			break;
		case NOTIFICACIO:
			enviamentTipus = NotificaEnviamentTipusEnumDto.NOTIFICACIO;
			break;
		}
		dto.setEnviamentTipus(enviamentTipus);
		dto.setEnviamentDataProgramada(notificacio.getEnviamentDataProgramada());
		dto.setConcepte(notificacio.getConcepte());
		//notificacio.getDescripcio()
		if (notificacio.getPagadorPostal() != null) {
			PagadorPostal pagadorPostal = notificacio.getPagadorPostal();
			dto.setPagadorCorreusCodiDir3(pagadorPostal.getDir3Codi());
			dto.setPagadorCorreusContracteNum(pagadorPostal.getContracteNum());
			dto.setPagadorCorreusCodiClientFacturacio(pagadorPostal.getFacturacioClientCodi());
			dto.setPagadorCorreusDataVigencia(pagadorPostal.getContracteDataVigencia());
		}
		if (notificacio.getPagadorCie() != null) {
			PagadorCie pagadorCie = notificacio.getPagadorCie();
			dto.setPagadorCieCodiDir3(pagadorCie.getDir3Codi());
			dto.setPagadorCieDataVigencia(pagadorCie.getContracteDataVigencia());
		}
		dto.setProcedimentCodiSia(notificacio.getProcedimentCodi());
		if (notificacio.getDocument() != null) {
			Document document = notificacio.getDocument();
			dto.setDocumentArxiuNom(document.getArxiuNom());
			dto.setDocumentContingutBase64(document.getContingutBase64());
			dto.setDocumentSha1(document.getHash());
			dto.setDocumentNormalitzat(document.isNormalitzat());
			dto.setDocumentGenerarCsv(document.isGenerarCsv());
		}
		if (notificacio.getParametresSeu() != null) {
			ParametresSeu parametresSeu = notificacio.getParametresSeu();
			dto.setSeuExpedientSerieDocumental(
					parametresSeu.getExpedientSerieDocumental());
			dto.setSeuExpedientUnitatOrganitzativa(
					parametresSeu.getExpedientUnitatOrganitzativa());
			dto.setSeuExpedientIdentificadorEni(
					parametresSeu.getExpedientIdentificadorEni());
			dto.setSeuExpedientTitol(
					parametresSeu.getExpedientTitol());
			dto.setSeuRegistreOficina(
					parametresSeu.getRegistreOficina());
			dto.setSeuRegistreLlibre(
					parametresSeu.getRegistreLlibre());
			dto.setSeuIdioma(
					parametresSeu.getIdioma());
			dto.setSeuAvisTitol(
					parametresSeu.getAvisTitol());
			dto.setSeuAvisText(
					parametresSeu.getAvisText());
			dto.setSeuAvisTextMobil(
					parametresSeu.getAvisTextMobil());
			dto.setSeuOficiTitol(
					parametresSeu.getOficiTitol());
			dto.setSeuOficiText(
					parametresSeu.getOficiText());
		}
		if (notificacio.getEnviaments() != null) {
			List<NotificacioEnviamentDto> destinataris = new ArrayList<NotificacioEnviamentDto>();
			for (Enviament enviament: notificacio.getEnviaments()) {
				destinataris.add(
						toDestinatariDto(notificacio, enviament));
			}
			dto.setEnviaments(enviaments);
		} else {
			// TODO error si no hi ha cap enviament
		}
		return dto;
	}

	private NotificacioEnviamentDto toDestinatariDto(
			Notificacio notificacio,
			Enviament enviament) {
		NotificacioEnviamentDto dto = new NotificacioEnviamentDto();
		if (enviament.getTitular() != null) {
			Persona titular = enviament.getTitular();
			dto.setTitularNom(titular.getNom());
			dto.setTitularLlinatge1(titular.getLlinatge1());
			dto.setTitularLlinatge2(titular.getLlinatge2());
			dto.setTitularNif(titular.getNif());
			dto.setTitularTelefon(titular.getTelefon());
			dto.setTitularEmail(titular.getEmail());
		}
		if (enviament.getDestinataris() != null) {
			if (enviament.getDestinataris().size() > 1) {
				Persona destinatari = enviament.getDestinataris().get(0);
				dto.setDestinatariNom(destinatari.getNom());
				dto.setDestinatariLlinatge1(destinatari.getLlinatge1());
				dto.setDestinatariLlinatge2(destinatari.getLlinatge2());
				dto.setDestinatariNif(destinatari.getNif());
				dto.setDestinatariTelefon(destinatari.getTelefon());
				dto.setDestinatariEmail(destinatari.getEmail());
			} else {
				// TODO error si més d'un destinatari
			}
		}
		if (enviament.getEntregaPostal() != null) {
			EntregaPostal entregaPostal = enviament.getEntregaPostal();
			//dto.setDomiciliTipus(entregaPostal);
			NotificaDomiciliConcretTipusEnumDto domiciliConcretTipus = null;
			if (entregaPostal.getTipus() != null) {
				switch (entregaPostal.getTipus()) {
				case APARTAT_CORREUS:
					domiciliConcretTipus = NotificaDomiciliConcretTipusEnumDto.APARTADO_CORREOS;
					break;
				case ESTRANGER:
					domiciliConcretTipus = NotificaDomiciliConcretTipusEnumDto.EXTRANJERO;
					break;
				case NACIONAL:
					domiciliConcretTipus = NotificaDomiciliConcretTipusEnumDto.NACIONAL;
					break;
				case SENSE_NORMALITZAR:
					domiciliConcretTipus = NotificaDomiciliConcretTipusEnumDto.SIN_NORMALIZAR;
					break;
				}
			}
			dto.setDomiciliConcretTipus(domiciliConcretTipus);
			dto.setDomiciliViaTipus(viaTipusToString(entregaPostal.getViaTipus()));
			dto.setDomiciliViaNom(entregaPostal.getViaNom());
			NotificaDomiciliNumeracioTipusEnumDto numeracioTipus = null;
			if (entregaPostal.getApartatCorreus() != null) {
				numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.APARTADO_CORREOS;
			} else if (entregaPostal.getPuntKm() != null) {
				numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.PUNTO_KILOMETRICO;
			} else if (entregaPostal.getNumeroCasa() != null) {
				numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.NUMERO;
			} else {
				numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.SIN_NUMERO;
			}
			dto.setDomiciliNumeracioTipus(numeracioTipus);
			dto.setDomiciliNumeracioNumero(entregaPostal.getNumeroCasa());
			dto.setDomiciliNumeracioPuntKm(entregaPostal.getPuntKm());
			dto.setDomiciliApartatCorreus(entregaPostal.getApartatCorreus());
			dto.setDomiciliBloc(entregaPostal.getBloc());
			dto.setDomiciliPortal(entregaPostal.getPortal());
			dto.setDomiciliEscala(entregaPostal.getEscala());
			dto.setDomiciliPlanta(entregaPostal.getPlanta());
			dto.setDomiciliPorta(entregaPostal.getPorta());
			dto.setDomiciliComplement(entregaPostal.getComplement());
			dto.setDomiciliPoblacio(entregaPostal.getPoblacio());
			dto.setDomiciliMunicipiCodiIne(entregaPostal.getMunicipiCodi());
			//dto.setDomiciliMunicipiNom(entregaPostal);
			dto.setDomiciliCodiPostal(entregaPostal.getCodiPostal());
			dto.setDomiciliProvinciaCodi(entregaPostal.getProvinciaCodi());
			//dto.setDomiciliProvinciaNom(entregaPostal);
			dto.setDomiciliPaisCodiIso(entregaPostal.getPaisCodi());
			//dto.setDomiciliPaisNom(entregaPostal);
			dto.setDomiciliLinea1(entregaPostal.getLinea1());
			dto.setDomiciliLinea2(entregaPostal.getLinea2());
			dto.setDomiciliCie(entregaPostal.getCie());
		}
		if (enviament.getEntregaDeh() != null) {
			EntregaDeh entregaDeh = enviament.getEntregaDeh();
			dto.setDehObligat(entregaDeh.isObligat());
			if (dto.getDestinatariNif() != null) {
				dto.setDehNif(dto.getDestinatariNif());
			} else {
				dto.setDehNif(dto.getTitularNif());
			}
			dto.setDehProcedimentCodi(entregaDeh.getProcedimentCodi());
		}
		NotificaServeiTipusEnumDto serveiTipus = null;
		if (enviament.getServeiTipus() != null) {
			switch (enviament.getServeiTipus()) {
			case NORMAL:
				serveiTipus = NotificaServeiTipusEnumDto.NORMAL;
				break;
			case URGENT:
				serveiTipus = NotificaServeiTipusEnumDto.URGENTE;
				break;
			}
		}
		dto.setServeiTipus(serveiTipus);
		dto.setRetardPostal(notificacio.getRetard());
		dto.setCaducitat(notificacio.getCaducitat());
		return dto;
	}

	private InformacioEnviament toInformacioEnviament(
			NotificacioDto dto) {
		InformacioEnviament informacioEnviament = new InformacioEnviament();
		informacioEnviament.setConcepte(dto.getConcepte());
		//informacioEnviament.setDescripcio(dto.getde);
		informacioEnviament.setEmisorDir3Codi(dto.getEntitat().getDir3Codi());
		//informacioEnviament.setEmisorDir3Descripcio;
		//informacioEnviament.setEmisorArrelDir3Codi;
		//informacioEnviament.setEmisorArrelDir3Descripcio;
		//informacioEnviament.setDestiDir3Codi;
		//informacioEnviament.setDestiDir3Descripcio;
		EnviamentTipusEnum enviamentTipus = null;
		if (dto.getEnviamentTipus() != null) {
			switch (dto.getEnviamentTipus()) {
			case COMUNICACIO:
				enviamentTipus = EnviamentTipusEnum.COMUNICACIO;
				break;
			case NOTIFICACIO:
				enviamentTipus = EnviamentTipusEnum.NOTIFICACIO;
				break;
			}
		}
		informacioEnviament.setEnviamentTipus(enviamentTipus);
		//informacioEnviament.setDataCreacio(dataCreacio);
		//informacioEnviament.setDataPostaDisposicio(dataPostaDisposicio);
		informacioEnviament.setProcedimentCodi(dto.getProcedimentCodiSia());
		informacioEnviament.setProcedimentDescripcio(dto.getProcedimentDescripcioSia());
		if (dto.getDestinataris() != null && dto.getDestinataris().size() > 0) {
			NotificacioEnviamentDto destinatari = dto.getDestinataris().get(0);
			informacioEnviament.setIdentificador(destinatari.getNotificaIdentificador());
			informacioEnviament.setReferencia(destinatari.getReferencia());
			informacioEnviament.setDataCaducitat(destinatari.getCaducitat());
			informacioEnviament.setRetard(destinatari.getRetardPostal());
			Persona titular = new Persona();
			titular.setNom(destinatari.getTitularNom());
			titular.setLlinatge1(destinatari.getTitularLlinatge1());
			titular.setLlinatge2(destinatari.getTitularLlinatge2());
			titular.setNif(destinatari.getTitularNif());
			titular.setTelefon(destinatari.getTitularTelefon());
			titular.setEmail(destinatari.getTitularEmail());
			informacioEnviament.setTitular(titular);
			if (destinatari.getDestinatariNom() != null || destinatari.getDestinatariNif() != null) {
				Persona destinatariPersona = new Persona();
				destinatariPersona.setNom(destinatari.getDestinatariNom());
				destinatariPersona.setLlinatge1(destinatari.getDestinatariLlinatge1());
				destinatariPersona.setLlinatge2(destinatari.getDestinatariLlinatge2());
				destinatariPersona.setNif(destinatari.getDestinatariNif());
				destinatariPersona.setTelefon(destinatari.getDestinatariTelefon());
				destinatariPersona.setEmail(destinatari.getDestinatariEmail());
				informacioEnviament.setDestinataris(Arrays.asList(destinatariPersona));
			}
			if (destinatari.getDomiciliTipus() != null || destinatari.getDomiciliConcretTipus() != null) {
				EntregaPostal entregaPostal = new EntregaPostal();
				EntregaPostalTipusEnum tipus = null;
				switch (destinatari.getDomiciliConcretTipus()) {
				case APARTADO_CORREOS:
					tipus = EntregaPostalTipusEnum.APARTAT_CORREUS;
					break;
				case EXTRANJERO:
					tipus = EntregaPostalTipusEnum.ESTRANGER;
					break;
				case NACIONAL:
					tipus = EntregaPostalTipusEnum.NACIONAL;
					break;
				case SIN_NORMALIZAR:
					tipus = EntregaPostalTipusEnum.SENSE_NORMALITZAR;
					break;
				}
				entregaPostal.setTipus(tipus);
				entregaPostal.setViaTipus(toViaTipus(destinatari.getDomiciliViaTipus()));
				entregaPostal.setViaNom(destinatari.getDomiciliViaNom());
				entregaPostal.setNumeroCasa(destinatari.getDomiciliNumeracioNumero());
				//entregaPostal.setNumeroQualificador();
				entregaPostal.setPuntKm(destinatari.getDomiciliNumeracioPuntKm());
				entregaPostal.setApartatCorreus(destinatari.getDomiciliApartatCorreus());
				entregaPostal.setPortal(destinatari.getDomiciliPortal());
				entregaPostal.setEscala(destinatari.getDomiciliEscala());
				entregaPostal.setPlanta(destinatari.getDomiciliPlanta());
				entregaPostal.setPorta(destinatari.getDomiciliPorta());
				entregaPostal.setBloc(destinatari.getDomiciliBloc());
				entregaPostal.setComplement(destinatari.getDomiciliComplement());
				entregaPostal.setCodiPostal(destinatari.getDomiciliCodiPostal());
				entregaPostal.setPoblacio(destinatari.getDomiciliPoblacio());
				entregaPostal.setMunicipiCodi(destinatari.getDomiciliMunicipiCodiIne());
				entregaPostal.setProvinciaCodi(destinatari.getDomiciliProvinciaCodi());
				entregaPostal.setPaisCodi(destinatari.getDomiciliPaisCodiIso());
				entregaPostal.setLinea1(destinatari.getDomiciliLinea1());
				entregaPostal.setLinea2(destinatari.getDomiciliLinea2());
				entregaPostal.setCie(destinatari.getDomiciliCie());
				//entregaPostal.setFormatSobre();
				//entregaPostal.setFormatFulla();
				informacioEnviament.setEntregaPostal(entregaPostal);
			}
			if (destinatari.getDehProcedimentCodi() != null) {
				EntregaDeh entregaDeh = new EntregaDeh();
				entregaDeh.setObligat(destinatari.isDehObligat());
				entregaDeh.setProcedimentCodi(destinatari.getDehProcedimentCodi());
				informacioEnviament.setEntregaDeh(entregaDeh);
			}
			informacioEnviament.setEstat(
					toEnviamentEstat(destinatari.getEstat()));
			//informacioEnviament.setCertificacio();
		}
		return informacioEnviament;
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

	private EntregaPostalViaTipusEnum toViaTipus(String viaTipus) {
		if ("ALMDA".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.ALAMEDA;
		} else if ("AVDA".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.AVENIDA;
		} else if ("AVGDA".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.AVINGUDA;
		} else if ("BAR".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.BARRIO;
		} else if ("BVR".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.BULEVAR;
		} else if ("CALLE".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.CALLE;
		} else if ("CJA".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.CALLEJA;
		} else if ("CAMÍ".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.CAMI;
		} else if ("CAMNO".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.CAMINO;
		} else if ("CAMPO".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.CAMPO;
		} else if ("CARR".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.CARRER;
		} else if ("CRA".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.CARRERA;
		} else if ("CTRA".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.CARRETERA;
		} else if ("CSTA".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.CUESTA;
		} else if ("EDIF".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.EDIFICIO;
		} else if ("EPTZA".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.ENPARANTZA;
		} else if ("ESTR".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.ESTRADA;
		} else if ("GTA".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.GLORIETA;
		} else if ("JARD".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.JARDINES;
		} else if ("JARDI".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.JARDINS;
		} else if ("KALEA".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.KALEA;
		} else if ("OTROS".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.OTROS;
		} else if ("PRQUE".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.PARQUE;
		} else if ("PSJ".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.PASAJE;
		} else if ("PASEO".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.PASEO;
		} else if ("PASTG".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.PASSATGE;
		} else if ("PSG".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.PASSEIG;
		} else if ("PLCTA".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.PLACETA;
		} else if ("PLAZA".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.PLAZA;
		} else if ("PLZA".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.PLAZUELA;
		} else if ("PLAÇA".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.PLAÇA;
		} else if ("POBL".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.POBLADO;
		} else if ("POLIG".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.POLIGONO;
		} else if ("PRAZA".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.PRAZA;
		} else if ("RAMBL".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.RAMBLA;
		} else if ("RONDA".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.RONDA;
		} else if ("RÚA".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.RUA;
		} else if ("SECT".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.SECTOR;
		} else if ("TRAV".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.TRAVESIA;
		} else if ("TRAVS".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.TRAVESSERA;
		} else if ("URB".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.URBANIZACION;
		} else if ("VIA".equals(viaTipus)) {
			return EntregaPostalViaTipusEnum.VIA;
		} else {
			return null;
		}
	}

	private String viaTipusToString(EntregaPostalViaTipusEnum viaTipus) {
		if (viaTipus != null) {
			switch (viaTipus) {
			case ALAMEDA:
				return "ALMDA";
			case AVENIDA:
				return "AVDA";
			case AVINGUDA:
				return "AVGDA";
			case BARRIO:
				return "BAR";
			case BULEVAR:
				return "BVR";
			case CALLE:
				return "CALLE";
			case CALLEJA:
				return "CJA";
			case CAMI:
				return "CAMÍ";
			case CAMINO:
				return "CAMNO";
			case CAMPO:
				return "CAMPO";
			case CARRER:
				return "CARR";
			case CARRERA:
				return "CRA";
			case CARRETERA:
				return "CTRA";
			case CUESTA:
				return "CSTA";
			case EDIFICIO:
				return "EDIF";
			case ENPARANTZA:
				return "EPTZA";
			case ESTRADA:
				return "ESTR";
			case GLORIETA:
				return "GTA";
			case JARDINES:
				return "JARD";
			case JARDINS:
				return "JARDI";
			case KALEA:
				return "KALEA";
			case OTROS:
				return "OTROS";
			case PARQUE:
				return "PRQUE";
			case PASAJE:
				return "PSJ";
			case PASEO:
				return "PASEO";
			case PASSATGE:
				return "PASTG";
			case PASSEIG:
				return "PSG";
			case PLACETA:
				return "PLCTA";
			case PLAZA:
				return "PLAZA";
			case PLAZUELA:
				return "PLZA";
			case PLAÇA:
				return "PLAÇA";
			case POBLADO:
				return "POBL";
			case POLIGONO:
				return "POLIG";
			case PRAZA:
				return "PRAZA";
			case RAMBLA:
				return "RAMBL";
			case RONDA:
				return "RONDA";
			case RUA:
				return "RÚA";
			case SECTOR:
				return "SECT";
			case TRAVESIA:
				return "TRAV";
			case TRAVESSERA:
				return "TRAVS";
			case URBANIZACION:
				return "URB";
			case VIA:
				return "VIA";
			default:
				return null;
			}
		} else {
			return null;
		}
	}*/

}
