package es.caib.notib.logic.helper;

import com.google.common.base.Strings;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.client.domini.NotificaDomiciliConcretTipus;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.logic.intf.exception.ValidationException;
import es.caib.notib.logic.intf.service.AuditService.TipusOperacio;
import es.caib.notib.logic.wsdl.notificaV2.NotificaWsV2PortType;
import es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.AltaRemesaEnvios;
import es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.Destinatarios;
import es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.Documento;
import es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.EntregaDEH;
import es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.EntregaPostal;
import es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.Envio;
import es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.Envios;
import es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.Opcion;
import es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.Opciones;
import es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.OrganismoPagadorCIE;
import es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.OrganismoPagadorPostal;
import es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.Persona;
import es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.ResultadoAltaRemesaEnvios;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Datado;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.InfoEnvioV2;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.ResultadoInfoEnvioV2;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.ProcSerRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.ejb.CreateException;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.naming.NamingException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Helper per a interactuar amb la versió 2 del servei web de Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class NotificaV2Helper extends AbstractNotificaHelper {

	@Autowired
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private CallbackHelper callbackHelper;
	@Autowired
	private ProcSerRepository procSerRepository;
	@Autowired
	private IntegracioHelper integracioHelper;
	@Autowired
	private NotificacioEventHelper notificacioEventHelper;
	@Autowired
	private EnviamentTableHelper enviamentTableHelper;


	public NotificacioEntity notificacioEnviar(Long notificacioId, boolean ambEnviamentPerEmail) {

		var info = new IntegracioInfo(IntegracioHelper.INTCODI_NOTIFICA,"Enviament d'una notificació", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Identificador de la notificacio", String.valueOf(notificacioId)));

		var notificacio = notificacioRepository.findById(notificacioId).orElseThrow();
		log.info(" [NOT] Inici enviament notificació [Id: " + notificacio.getId() + ", Estat: " + notificacio.getEstat() + "]");
		info.setCodiEntitat(notificacio.getEntitat() != null ? notificacio.getEntitat().getCodi() : null);
		notificacio.updateNotificaNouEnviament(pluginHelper.getNotificaReintentsPeriodeProperty());

		// Validacions
		if (!NotificacioEstatEnumDto.REGISTRADA.equals(notificacio.getEstat()) && !NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(notificacio.getEstat())) {
			log.error(" [NOT] la notificació no té l'estat REGISTRADA o ENVIADA AMB ERRORS.");
			integracioHelper.addAccioError(info, "La notificació no està registrada, o enviada amb errors");
			throw new ValidationException(notificacioId, NotificacioEntity.class, "La notificació no te l'estat " + NotificacioEstatEnumDto.REGISTRADA.name() + " o " + NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.name());
		}
		var error = false;
		String errorDescripcio = null;
		try {
			log.info(" >>> Enviant notificació...");
			var startTime = System.nanoTime();
			double elapsedTime;
			var resultadoAlta = enviaNotificacio(notificacio);
			elapsedTime = (System.nanoTime() - startTime) / 10e6;
			log.info(" [TIMER-NOT] Notificació enviar (enviaNotificacio SOAP-QUERY)  [Id: " + notificacioId + "]: " + elapsedTime + " ms");
			notificacio.updateNotificaEnviamentData();

			if ("000".equals(resultadoAlta.getCodigoRespuesta()) && "OK".equalsIgnoreCase(resultadoAlta.getDescripcionRespuesta())) {
				startTime = System.nanoTime();
				log.info(" >>> ... OK");
				if (!ambEnviamentPerEmail) {
					notificacio.updateEstat(NotificacioEstatEnumDto.ENVIADA);
				}
				List<Long> enviamentsActualitzats = new ArrayList<>();
				for (var resultadoEnvio: resultadoAlta.getResultadoEnvios().getItem()) {
					for (var enviament: notificacio.getEnviamentsPerNotifica()) {
						var nif = enviament.getTitular().getNif();
						if (nif != null && nif.equalsIgnoreCase(resultadoEnvio.getNifTitular()) && !enviamentsActualitzats.contains(enviament.getId())) {
							enviamentsActualitzats.add(enviament.getId());
							enviament.updateNotificaEnviada(resultadoEnvio.getIdentificador());
							enviamentTableHelper.actualitzarRegistre(enviament);
							auditHelper.auditaEnviament(enviament, TipusOperacio.UPDATE, "NotificaV2Helper.notificacioEnviar");
							break;
						}
					}
				}
				if (pluginHelper.enviarCarpeta()) {
					for (NotificacioEnviamentEntity e : notificacio.getEnviaments()) {
						pluginHelper.enviarNotificacioMobil(e);
					}
				}
				elapsedTime = (System.nanoTime() - startTime) / 10e6;
				log.info(" [TIMER-NOT] Notificació enviar (Preparar events)  [Id: " + notificacioId + "]: " + elapsedTime + " ms");
				integracioHelper.addAccioOk(info);
			} else {
				error = true;
				errorDescripcio = "Intent " + notificacio.getNotificaEnviamentIntent() + " \n\nError retornat per Notifica: [" + resultadoAlta.getCodigoRespuesta() + "] " + resultadoAlta.getDescripcionRespuesta();
				log.info(" >>> ... ERROR: " + errorDescripcio);
				integracioHelper.addAccioError(info, errorDescripcio);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			error = true;
			errorDescripcio = "Intent " + notificacio.getNotificaEnviamentIntent() + "\n\n" + (ex instanceof SOAPFaultException ? ex.getMessage() : ExceptionUtils.getStackTrace(ex));
			integracioHelper.addAccioError(info, "Error al enviar la notificació", ex);
		}
		var fiReintents = notificacio.getNotificaEnviamentIntent() >= pluginHelper.getNotificaReintentsMaxProperty();
		if (fiReintents && (NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(notificacio.getEstat()) /*|| NotificacioEstatEnumDto.REGISTRADA.equals(notificacio.getEstat())*/)) {
			notificacio.updateEstat(NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS);
		}
		notificacioEventHelper.addNotificaEnviamentEvent(notificacio, error, errorDescripcio, fiReintents);
		callbackHelper.updateCallbacks(notificacio, error, errorDescripcio);
		log.info(" [NOT] Fi enviament notificació: [Id: " + notificacio.getId() + ", Estat: " + notificacio.getEstat() + "]");
		notificacioTableHelper.actualitzarRegistre(notificacio);
		auditHelper.auditaNotificacio(notificacio, TipusOperacio.UPDATE, "NotificaV2Helper.notificacioEnviar");
		return notificacio;
	}



	@Transactional(timeout = 60, propagation = Propagation.REQUIRES_NEW)
	public NotificacioEnviamentEntity enviamentRefrescarEstat(Long enviamentId) throws SistemaExternException {

		log.info(String.format(" [NOT] Refrescant estat de notific@ de l'enviament (Id=%d)", enviamentId));
		var enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
		try {
			return enviamentRefrescarEstat(enviament, false);
		} catch (Exception e) {
			if (e instanceof SistemaExternException) {
				throw (SistemaExternException) e;
			}
		}
		return enviament;
	}

	@Transactional(timeout = 60, propagation = Propagation.REQUIRES_NEW)
	public NotificacioEnviamentEntity enviamentRefrescarEstat(Long enviamentId, boolean raiseExceptions) throws Exception {

		var enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
		return enviamentRefrescarEstat(enviament, raiseExceptions);
	}

	private NotificacioEnviamentEntity enviamentRefrescarEstat(@NonNull NotificacioEnviamentEntity enviament, boolean raiseExceptions) throws Exception {

		var info = new IntegracioInfo(IntegracioHelper.INTCODI_NOTIFICA,"Consultar estat d'un enviament", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Identificador de l'enviament", String.valueOf(enviament.getId())));

		log.info(" [EST] Inici actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
		info.setCodiEntitat(enviament.getNotificacio() != null && enviament.getNotificacio().getEntitat() != null ?  enviament.getNotificacio().getEntitat().getCodi() : null);
		long startTime;
		double elapsedTime;
		var error = false;
		String errorDescripcio = null;
		var errorMaxReintents = false;
		Exception excepcio = null;
		try {
			var dataUltimDatat = enviament.getNotificaDataCreacio();
			enviament.updateNotificaDataRefrescEstat();
			enviament.updateNotificaNovaConsulta(pluginHelper.getConsultaReintentsPeriodeProperty());
			// Validacions
			if (enviament.getNotificaIdentificador() == null) {
				log.info(" [EST] Fi actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
				errorDescripcio = "L'enviament no té identificador de Notifica";
				integracioHelper.addAccioError(info, errorDescripcio);
				throw new ValidationException(enviament, NotificacioEnviamentEntity.class, errorDescripcio);
			}
			startTime = System.nanoTime();
			var resultadoInfoEnvio = getNotificaResultadoInfoEnvio(enviament, info);
			elapsedTime = (System.nanoTime() - startTime) / 10e6;
			log.info(" [TIMER-EST] Refrescar estat enviament (infoEnvioV2)  [Id: " + enviament.getId() + "]: " + elapsedTime + " ms");
			var darrerDatat = getDarrerDatat(resultadoInfoEnvio, enviament, info);
			if (resultadoInfoEnvio.getCertificacion() != null) {
				log.info(" [EST] Actualitzant certificació de l'enviament [Id: " + enviament.getId() + "] ...");
				actualitzaCertificacio(resultadoInfoEnvio, enviament, darrerDatat);
			} else {
				log.info(" [EST] Notifica no té cap certificació de l'enviament [Id: " + enviament.getId() + "] ...");
			}
			log.info(" [EST] Actualitzant informació enviament amb Datat...");
			var dataDatat = toDate(resultadoInfoEnvio.getFechaCreacion());
			var estat = getEstatNotifica(darrerDatat.getResultado());
			if (!dataDatat.equals(dataUltimDatat) || !estat.equals(enviament.getNotificaEstat())) {
				actualitzaDatatEnviament(resultadoInfoEnvio, enviament, darrerDatat);
			}
			log.info(" [EST] Enviament actualitzat");
			enviament.refreshNotificaConsulta();
			integracioHelper.addAccioOk(info);
			log.info(" [EST] Fi actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");

		} catch (Exception ex) {
			error = true;
			errorMaxReintents = enviament.getNotificaIntentNum() >= pluginHelper.getConsultaReintentsMaxProperty();
			errorDescripcio = getErrorDescripcio(enviament.getNotificaIntentNum(), ex);
			log.info(" [EST] Fi actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
			integracioHelper.addAccioError(info, "Error consultat l'estat de l'enviament", ex);
			excepcio = ex;
			var msg = "Error al consultar l'estat d'un enviament fet amb NotificaV2 (notificacioId=";
			log.error(msg + enviament.getNotificacio().getId() + ", notificaIdentificador=" + enviament.getNotificaIdentificador() + ")", ex);

		}
		notificacioEventHelper.addNotificaConsultaEvent(enviament, error, errorDescripcio, errorMaxReintents);
		enviamentTableHelper.actualitzarRegistre(enviament);
		auditHelper.auditaEnviament(enviament, TipusOperacio.UPDATE, "NotificaV2Helper.enviamentRefrescarEstat");
		if (error && raiseExceptions){
			throw excepcio;
		}
		return enviament;
	}

	private String getErrorDescripcio(int intent, Exception ex) {
		return "Intent " + intent + "\n\n" + (ex instanceof ValidationException ? ex.getMessage() : ExceptionUtils.getStackTrace(ex));
	}

	private ResultadoInfoEnvioV2 getNotificaResultadoInfoEnvio(NotificacioEnviamentEntity enviament, IntegracioInfo info) throws Exception {

		var infoEnvio = new InfoEnvioV2();
		infoEnvio.setIdentificador(enviament.getNotificaIdentificador());
		var apiKey = enviament.getNotificacio().getEntitat().getApiKey();
		var resultadoInfoEnvio = getNotificaWs(apiKey).infoEnvioV2(infoEnvio);
		if (resultadoInfoEnvio.getDatados() == null) {
			var errorDescripcio = "La resposta rebuda de Notifica no conté informació de datat";
			integracioHelper.addAccioError(info, errorDescripcio);
			throw new ValidationException(enviament, NotificacioEnviamentEntity.class, errorDescripcio);
		}
		return resultadoInfoEnvio;
	}

	private Datado getDarrerDatat(ResultadoInfoEnvioV2 resultadoInfoEnvio, NotificacioEnviamentEntity enviament, IntegracioInfo info) throws DatatypeConfigurationException {

		info.setCodiEntitat(enviament.getNotificacio().getEntitat().getCodi());
		if (resultadoInfoEnvio.getDatados() == null) {
			var errorDescripcio = "La resposta rebuda de Notifica no conté informació de datat";
			integracioHelper.addAccioError(info, errorDescripcio);
			throw new ValidationException(enviament, NotificacioEnviamentEntity.class, errorDescripcio);
		}
		Datado datatDarrer = null;
		for (var datado: resultadoInfoEnvio.getDatados().getDatado()) {
			var datatData = toDate(datado.getFecha());
			if (datatDarrer == null) {
				datatDarrer = datado;
				continue;
			}
			if (datado.getFecha() != null) {
				var datatDarrerData = toDate(datatDarrer.getFecha());
				if (datatData.after(datatDarrerData)) {
					datatDarrer = datado;
				}
			}
		}
		if (datatDarrer == null) {
			var errorDescripcio = "No s'ha pogut trobar el darrer datat dins la resposta rebuda de Notifica";
			integracioHelper.addAccioError(info, errorDescripcio);
			throw new ValidationException(enviament, NotificacioEnviamentEntity.class, errorDescripcio);
		}
		return datatDarrer;
	}

	private void actualitzaCertificacio(ResultadoInfoEnvioV2 resultadoInfoEnvio, NotificacioEnviamentEntity enviament, Datado darrerDatat) throws DatatypeConfigurationException {

		var dataUltimaCertificacio = enviament.getNotificaCertificacioData();
		var certificacio = resultadoInfoEnvio.getCertificacion();
		var dataCertificacio = toDate(certificacio.getFechaCertificacion());
		ConfigHelper.setEntitatCodi(enviament.getNotificacio().getEntitat().getCodi());
		if (dataCertificacio.equals(dataUltimaCertificacio) && enviament.getNotificaCertificacioArxiuId() != null) {
			log.info(" [EST] El certificat de l'enviament ja estava actualitzat");
			return;
		}
		var decodificat = certificacio.getContenidoCertificacion();
		if (enviament.getNotificaCertificacioArxiuId() != null) {
			pluginHelper.gestioDocumentalDelete(enviament.getNotificaCertificacioArxiuId(), PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS);
		}
		var gestioDocumentalId = pluginHelper.gestioDocumentalCreate(PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS, decodificat);
		log.info(" [EST] Actualitzant certificació enviament...");
		enviament.updateNotificaCertificacio(dataCertificacio, gestioDocumentalId, certificacio.getHash(), certificacio.getOrigen(), certificacio.getMetadatos(),
				certificacio.getCsv(), certificacio.getMime(), Integer.parseInt(certificacio.getSize()), null, null, null);

		log.info(" [EST] Fi actualització certificació. Creant nou event per certificació...");
		//Crea un nou event
		notificacioEventHelper.addAdviserCertificacioEvent(enviament, false, null);
		callbackHelper.updateCallback(enviament, false, null);
	}

	private void actualitzaDatatEnviament(ResultadoInfoEnvioV2 resultadoInfoEnvio, NotificacioEnviamentEntity enviament, Datado darrerDatat) throws Exception {

		var dataDatat = toDate(resultadoInfoEnvio.getFechaCreacion());
		var estat = getEstatNotifica(darrerDatat.getResultado());
		var organismoEmisor = resultadoInfoEnvio.getCodigoOrganismoEmisor();
		var organismoEmisorRaiz = resultadoInfoEnvio.getCodigoOrganismoEmisorRaiz();
		enviament.updateNotificaInformacio(
				dataDatat,
				toDate(resultadoInfoEnvio.getFechaPuestaDisposicion()),
				toDate(resultadoInfoEnvio.getFechaCaducidad()),
				(organismoEmisor != null) ? organismoEmisor.getCodigo() : null,
				(organismoEmisor != null) ? organismoEmisor.getDescripcionCodigoDIR() : null,
				(organismoEmisor != null) ? organismoEmisor.getNifDIR() : null,
				(organismoEmisorRaiz != null) ? organismoEmisorRaiz.getCodigo() : null,
				(organismoEmisorRaiz != null) ? organismoEmisorRaiz.getDescripcionCodigoDIR() : null,
				(organismoEmisorRaiz != null) ? organismoEmisorRaiz.getNifDIR() : null);
		if (estat != null) {
			log.info(" [EST] Nou estat: " + estat.name());
		}
		//Crea un nou event
		log.info(" [EST] Creant nou event per Datat...");
		notificacioEventHelper.addAdviserDatatEvent(enviament, false, null);
		callbackHelper.updateCallback(enviament, false, null);
		log.info(" [EST] L'event s'ha guardat correctament...");
		log.info(" [EST] Actualitzant Datat enviament...");
		enviamentUpdateDatat(estat, toDate(darrerDatat.getFecha()), null, darrerDatat.getOrigen(), darrerDatat.getNifReceptor(),
				darrerDatat.getNombreReceptor(), null, null, enviament);
		log.info(" [EST] Fi actualització Datat");
	}

	private ResultadoAltaRemesaEnvios enviaNotificacio(NotificacioEntity notificacio) throws Exception {

		try {
			var apiKey = notificacio.getEntitat().getApiKey();
			var altaRemesaEnvios = generarAltaRemesaEnvios(notificacio);
			return getNotificaWs(apiKey).altaRemesaEnvios(altaRemesaEnvios);
		} catch (SOAPFaultException sfe) {
			var codiResposta = sfe.getFault().getFaultCode();
			var descripcioResposta = sfe.getFault().getFaultString();
			var resultat = new ResultadoAltaRemesaEnvios();
			resultat.setCodigoRespuesta(codiResposta);
			resultat.setDescripcionRespuesta(descripcioResposta);
			log.error("Error al donar d'alta la notificació a Notific@ (notificacioId=" + notificacio.getId() + ")", descripcioResposta);
			return resultat;
		}
	}

	private AltaRemesaEnvios generarAltaRemesaEnvios(NotificacioEntity notificacio) throws GeneralSecurityException, DatatypeConfigurationException, DecoderException {

		var envios = new AltaRemesaEnvios();
		Integer retardPostal = null;
		ConfigHelper.setEntitatCodi(notificacio.getEntitat().getDir3Codi());
		try {
			if (!isCodiDir3Entitat() && notificacio.getOrganGestor() != null) {
				envios.setCodigoOrganismoEmisor(notificacio.getOrganGestor().getCodi());
			} else if(!isCodiDir3Entitat() && notificacio.getProcediment() != null && notificacio.getProcediment().getOrganGestor() != null) {
				envios.setCodigoOrganismoEmisor(notificacio.getProcediment().getOrganGestor().getCodi());
			} else {
				envios.setCodigoOrganismoEmisor(notificacio.getEntitat().getDir3Codi());
			}
			switch (notificacio.getEnviamentTipus()) {
				case COMUNICACIO:
					envios.setTipoEnvio(new BigInteger("1"));
					break;
				case NOTIFICACIO:
					envios.setTipoEnvio(new BigInteger("2"));
					break;
			}
			if (notificacio.getEnviamentDataProgramada() != null) {
				var formatter = new SimpleDateFormat("dd/MM/yyyy");
				var today = new Date();
				var todayWithZeroTime = formatter.parse(formatter.format(today));
				var DataProgramadaWithZeroTime = formatter.parse(formatter.format(notificacio.getEnviamentDataProgramada()));
				if (DataProgramadaWithZeroTime.after(todayWithZeroTime)) {
					envios.setFechaEnvioProgramado(toXmlGregorianCalendar(notificacio.getEnviamentDataProgramada()));
				}
			}
			envios.setConcepto(notificacio.getConcepte().replace('·', '.'));
			if (notificacio.getDescripcio() != null) {
				envios.setDescripcion(notificacio.getDescripcio().replace('·', '.'));
			}
			envios.setProcedimiento(notificacio.getProcedimentCodiNotib());
			var documento = new Documento();
			if(notificacio.getDocument() != null && notificacio.getDocument().getArxiuGestdocId() != null) {
				var baos = new ByteArrayOutputStream();
				pluginHelper.gestioDocumentalGet(notificacio.getDocument().getArxiuGestdocId(), PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS, baos);
				documento.setContenido(baos.toByteArray());
				var opcionesDocumento = new Opciones();
				var opcionNormalizado = new Opcion();
				opcionNormalizado.setTipo("normalizado");
				opcionNormalizado.setValue(notificacio.getDocument().getNormalitzat()  ? "si" : "no");
				opcionesDocumento.getOpcion().add(opcionNormalizado);
				var opcionGenerarCsv = new Opcion();
				opcionGenerarCsv.setTipo("generarCsv");
				opcionGenerarCsv.setValue("no");
				opcionesDocumento.getOpcion().add(opcionGenerarCsv);
				documento.setOpcionesDocumento(opcionesDocumento);
				if(baos.toByteArray() != null) {
					String hash256 = Base64.encodeBase64String(Hex.decodeHex(DigestUtils.sha256Hex(baos.toByteArray()).toCharArray()));
					//Hash a enviar
					documento.setHash(hash256);
				}
				envios.setDocumento(documento);
			} else if (notificacio.getDocument() != null && notificacio.getDocument().getCsv() != null) {
				var contingut = pluginHelper.documentToRegistreAnnexDto(notificacio.getDocument()).getArxiuContingut();
				documento.setContenido(contingut);
				if(contingut != null) {
					String hash256 = Base64.encodeBase64String(Hex.decodeHex(DigestUtils.sha256Hex(contingut).toCharArray()));
					//Hash a enviar
					documento.setHash(hash256);
				}
				Opciones opcionesDocumento = new Opciones();
				Opcion opcionNormalizado = new Opcion();
				opcionNormalizado.setTipo("normalizado");
				opcionNormalizado.setValue(
						notificacio.getDocument().getNormalitzat()  ? "si" : "no");
				opcionesDocumento.getOpcion().add(opcionNormalizado);
				Opcion opcionGenerarCsv = new Opcion();
				opcionGenerarCsv.setTipo("generarCsv");
				opcionGenerarCsv.setValue("no");
				opcionesDocumento.getOpcion().add(opcionGenerarCsv);
				documento.setOpcionesDocumento(opcionesDocumento);
				envios.setDocumento(documento);
			} else if (notificacio.getDocument() != null && notificacio.getDocument().getUrl() != null) {
				var url = notificacio.getDocument().getUrl();
				documento.setEnlaceDocumento(url);
				var hash256 = Base64.encodeBase64String(Hex.decodeHex(DigestUtils.sha256Hex(url).toCharArray()));
				//Hash a enviar
				documento.setHash(hash256);
				var opcionesDocumento = new Opciones();
				var opcionNormalizado = new Opcion();
				opcionNormalizado.setTipo("normalizado");
				opcionNormalizado.setValue(notificacio.getDocument().getNormalitzat()  ? "si" : "no");
				opcionesDocumento.getOpcion().add(opcionNormalizado);
				var opcionGenerarCsv = new Opcion();
				opcionGenerarCsv.setTipo("generarCsv");
				opcionGenerarCsv.setValue("no");
				opcionesDocumento.getOpcion().add(opcionGenerarCsv);
				documento.setOpcionesDocumento(opcionesDocumento);
				envios.setDocumento(documento);
			} else if (notificacio.getDocument() != null && notificacio.getDocument().getUuid() != null) {
				var contingut = pluginHelper.documentToRegistreAnnexDto(notificacio.getDocument()).getArxiuContingut();
				documento.setContenido(contingut);
				if(contingut != null) {
					var hash256 = Base64.encodeBase64String(Hex.decodeHex(DigestUtils.sha256Hex(contingut).toCharArray()));
					//Hash a enviar
					documento.setHash(hash256);
				}
				var opcionesDocumento = new Opciones();
				var opcionNormalizado = new Opcion();
				opcionNormalizado.setTipo("normalizado");
				opcionNormalizado.setValue(notificacio.getDocument().getNormalitzat()  ? "si" : "no");
				opcionesDocumento.getOpcion().add(opcionNormalizado);
				var opcionGenerarCsv = new Opcion();
				opcionGenerarCsv.setTipo("generarCsv");
				opcionGenerarCsv.setValue("no");
				opcionesDocumento.getOpcion().add(opcionGenerarCsv);
				documento.setOpcionesDocumento(opcionesDocumento);
				envios.setDocumento(documento);
			} else if(notificacio.getDocument() != null) {
				documento.setHash(notificacio.getDocument().getHash());
				if(notificacio.getDocument().getContingutBase64() != null) {
					var contingut = notificacio.getDocument().getContingutBase64().getBytes();
					documento.setContenido(contingut);
					var hash256 = Base64.encodeBase64String(Hex.decodeHex(DigestUtils.sha256Hex(contingut).toCharArray()));
					//Hash a enviar
					documento.setHash(hash256);
				}
				var opcionesDocumento = new Opciones();
				var opcionNormalizado = new Opcion();
				opcionNormalizado.setTipo("normalizado");
				opcionNormalizado.setValue(notificacio.getDocument().getNormalitzat()  ? "si" : "no");
				opcionesDocumento.getOpcion().add(opcionNormalizado);
				var opcionGenerarCsv = new Opcion();
				opcionGenerarCsv.setTipo("generarCsv");
				opcionGenerarCsv.setValue("no");
				opcionesDocumento.getOpcion().add(opcionGenerarCsv);
				documento.setOpcionesDocumento(opcionesDocumento);
				envios.setDocumento(documento);
			}
			envios.setEnvios(generarEnvios(notificacio));
			var opcionesRemesa = new Opciones();
			if(notificacio.getRetard() != null) {
				retardPostal = notificacio.getRetard();
			} else if (notificacio.getProcediment() != null) {
				var procSer = procSerRepository.findById(notificacio.getProcediment().getId()).orElse(null);
				retardPostal = procSer != null ? procSer.getRetard() : null;
			}
			if (retardPostal != null) {
				var opcionRetardo = new Opcion();
				opcionRetardo.setTipo("retardo");
				opcionRetardo.setValue(retardPostal.toString()); // número de días
				opcionesRemesa.getOpcion().add(opcionRetardo);
			}
			if (notificacio.getCaducitat() != null) {
				var opcionCaducidad = new Opcion();
				opcionCaducidad.setTipo("caducidad");
				var sdfCaducitat = new SimpleDateFormat("yyyy-MM-dd");
				opcionCaducidad.setValue(sdfCaducitat.format(notificacio.getCaducitat())); // formato YYYY-MM-DD
				opcionesRemesa.getOpcion().add(opcionCaducidad);
			}
			envios.setOpcionesRemesa(opcionesRemesa);
		} catch (Exception ex) {
			log.error("Error generant la petició (notificacioId=" + notificacio.getId() + ")", ex);
		}
		return envios;
	}

	private Envios generarEnvios(NotificacioEntity notificacio) throws DatatypeConfigurationException {

		var envios = new Envios();
		for (var enviament: notificacio.getEnviamentsPerNotifica()) {
			if (enviament == null) {
				continue;
			}
			var envio = new Envio();
			var titular = new Persona();
			var titularIncapacitat = false;
			if (enviament.getTitular().isIncapacitat() && enviament.getDestinataris() != null) {
				titular.setNif(enviament.getDestinataris().get(0).getNif());
				titular.setApellidos(concatenarLlinatges(enviament.getDestinataris().get(0).getLlinatge1(), enviament.getDestinataris().get(0).getLlinatge2()));
				titular.setTelefono(enviament.getDestinataris().get(0).getTelefon());
				titular.setEmail(enviament.getDestinataris().get(0).getEmail());
				if (enviament.getDestinataris().get(0).getInteressatTipus().equals(InteressatTipus.JURIDICA)) {
					titular.setRazonSocial(enviament.getDestinataris().get(0).getRaoSocial());
				} else {
					titular.setNombre(enviament.getDestinataris().get(0).getNom());
				}
				titular.setCodigoDestino(enviament.getDestinataris().get(0).getDir3Codi());
				titularIncapacitat = true;
			} else {
				titular.setNif(InteressatTipus.FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus()) ? null : enviament.getTitular().getNif());
				titular.setApellidos(concatenarLlinatges(enviament.getTitular().getLlinatge1(), enviament.getTitular().getLlinatge2()));
				titular.setTelefono(enviament.getTitular().getTelefon());
				titular.setEmail(InteressatTipus.FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus()) ? null : enviament.getTitular().getEmail());
				if (enviament.getTitular().getInteressatTipus().equals(InteressatTipus.JURIDICA)) {
					titular.setRazonSocial(enviament.getTitular().getRaoSocial());
				} else {
					titular.setNombre(enviament.getTitular().getNom());
				}
				titular.setCodigoDestino(enviament.getTitular().getDir3Codi());
			}
			envio.setTitular(titular);
			var destinatarios = new Destinatarios();
			var counter = 0;
			for (var destinatari : enviament.getDestinataris()) {
				if (Strings.isNullOrEmpty(destinatari.getNif()) || titularIncapacitat && counter == 0) {
					continue;
				}
				counter++;
				var destinatario = new Persona();
				destinatario.setNif(destinatari.getNif());
				destinatario.setApellidos(concatenarLlinatges(destinatari.getLlinatge1(), destinatari.getLlinatge2()));
				destinatario.setTelefono(destinatari.getTelefon());
				destinatario.setEmail(destinatari.getEmail());
				if (destinatari.getInteressatTipus().equals(InteressatTipus.JURIDICA)) {
					destinatario.setRazonSocial(destinatari.getRaoSocial());
				} else {
					destinatario.setNombre(destinatari.getNom());
				}
				destinatario.setCodigoDestino(destinatari.getDir3Codi());
				destinatarios.getDestinatario().add(destinatario);
			}
			if (!destinatarios.getDestinatario().isEmpty()) {
				envio.setDestinatarios(destinatarios);
			}
			if (enviament.getEntregaPostal() != null) {
				var entregaPostal = new EntregaPostal();
				var procedimentNotificacio = notificacio.getProcediment();
				if (procedimentNotificacio != null && procedimentNotificacio.getEntregaCie() != null) {
					var entregaCieEntity = procedimentNotificacio.getEntregaCie();
					if (entregaCieEntity.getOperadorPostal() != null) {
						var pagadorPostal = new OrganismoPagadorPostal();
						pagadorPostal.setCodigoDIR3Postal(entregaCieEntity.getOperadorPostal().getOrganGestor().getCodi());
						pagadorPostal.setCodClienteFacturacionPostal(entregaCieEntity.getOperadorPostal().getFacturacioClientCodi());
						pagadorPostal.setNumContratoPostal(entregaCieEntity.getOperadorPostal().getContracteNum());
						pagadorPostal.setFechaVigenciaPostal(toXmlGregorianCalendar(entregaCieEntity.getOperadorPostal().getContracteDataVig()));
						entregaPostal.setOrganismoPagadorPostal(pagadorPostal);
					}
					if (entregaCieEntity.getCie() != null) {
						var pagadorCie = new OrganismoPagadorCIE();
						pagadorCie.setCodigoDIR3CIE(entregaCieEntity.getCie().getOrganGestor().getCodi());
						pagadorCie.setFechaVigenciaCIE(toXmlGregorianCalendar(entregaCieEntity.getCie().getContracteDataVig()));
						entregaPostal.setOrganismoPagadorCIE(pagadorCie);
					}
				}
				var entregaPostalEntity = enviament.getEntregaPostal();
				if (entregaPostalEntity.getDomiciliConcretTipus() != null) {
					switch (entregaPostalEntity.getDomiciliConcretTipus())  {
						case NACIONAL:
							entregaPostal.setTipoDomicilio(new BigInteger("1"));
							break;
						case ESTRANGER:
							entregaPostal.setTipoDomicilio(new BigInteger("2"));
							break;
						case APARTAT_CORREUS:
							entregaPostal.setTipoDomicilio(new BigInteger("3"));
							break;
						case SENSE_NORMALITZAR:
							entregaPostal.setTipoDomicilio(new BigInteger("4"));
							break;
					}
				}
				if (!NotificaDomiciliConcretTipus.SENSE_NORMALITZAR.equals(entregaPostalEntity.getDomiciliConcretTipus())) {
					entregaPostal.setTipoVia(entregaPostalEntity.getDomiciliViaTipus() != null ? entregaPostalEntity.getDomiciliViaTipus().getVal() : null); //viaTipusToString(enviament.getDomiciliViaTipus()));
					entregaPostal.setNombreVia(entregaPostalEntity.getDomiciliViaNom());
					entregaPostal.setNumeroCasa(entregaPostalEntity.getDomiciliNumeracioNumero());
					entregaPostal.setPuntoKilometrico(entregaPostalEntity.getDomiciliNumeracioPuntKm());
					entregaPostal.setPortal(entregaPostalEntity.getDomiciliPortal());
					entregaPostal.setPuerta(entregaPostalEntity.getDomiciliPorta());
					entregaPostal.setEscalera(entregaPostalEntity.getDomiciliEscala());
					entregaPostal.setPlanta(entregaPostalEntity.getDomiciliPlanta());
					entregaPostal.setBloque(entregaPostalEntity.getDomiciliBloc());
					entregaPostal.setComplemento(entregaPostalEntity.getDomiciliComplement());
					entregaPostal.setCalificadorNumero(entregaPostalEntity.getDomiciliNumeracioQualificador());
					entregaPostal.setCodigoPostal(entregaPostalEntity.getDomiciliCodiPostal());
					entregaPostal.setApartadoCorreos(entregaPostalEntity.getDomiciliApartatCorreus());
					entregaPostal.setMunicipio(entregaPostalEntity.getDomiciliMunicipiCodiIne());
					entregaPostal.setProvincia(entregaPostalEntity.getDomiciliProvinciaCodi());
					entregaPostal.setPais(entregaPostalEntity.getDomiciliPaisCodiIso());
					entregaPostal.setPoblacion(entregaPostalEntity.getDomiciliPoblacio());
				} else {
					entregaPostal.setLinea1(entregaPostalEntity.getDomiciliLinea1());
					entregaPostal.setLinea2(entregaPostalEntity.getDomiciliLinea2());
					entregaPostal.setCodigoPostal(entregaPostalEntity.getDomiciliCodiPostal());
					entregaPostal.setPais(entregaPostalEntity.getDomiciliPaisCodiIso());
				}
				if (entregaPostal.getPais() == null) {
					entregaPostal.setPais("ES");
				}
				var opcionesCie = new Opciones();
				if (entregaPostalEntity.getDomiciliCie() != null) {
					var opcionCie = new Opcion();
					opcionCie.setTipo("cie");
					opcionCie.setValue(entregaPostalEntity.getDomiciliCie().toString()); // identificador CIE
					opcionesCie.getOpcion().add(opcionCie);
				}
				if (entregaPostalEntity.getFormatSobre() != null) {
					var opcionFormatoSobre = new Opcion();
					opcionFormatoSobre.setTipo("formatoSobre");
					opcionFormatoSobre.setValue(entregaPostalEntity.getFormatSobre()); // americano, C5...
					opcionesCie.getOpcion().add(opcionFormatoSobre);
				}
				if (entregaPostalEntity.getFormatFulla() != null) {
					var opcionFormatoHoja = new Opcion();
					opcionFormatoHoja.setTipo("formatoHoja");
					opcionFormatoHoja.setValue(entregaPostalEntity.getFormatFulla()); // A4, A5...
					opcionesCie.getOpcion().add(opcionFormatoHoja);
				}
				entregaPostal.setOpcionesCIE(opcionesCie);
				envio.setEntregaPostal(entregaPostal);
			}
			if (enviament.getDehNif() != null && enviament.getDehProcedimentCodi() != null && enviament.getDehObligat() != null) {
				var entregaDeh = new EntregaDEH();
				entregaDeh.setObligado(enviament.getDehObligat());
				entregaDeh.setCodigoProcedimiento(enviament.getDehProcedimentCodi());
				envio.setEntregaDEH(entregaDeh);
			}
			envios.getEnvio().add(envio);
		}
		return envios;
	}

	private NotificaWsV2PortType getNotificaWs(String apiKey) throws InstanceNotFoundException, MalformedObjectNameException, MalformedURLException, RemoteException, NamingException, CreateException {

		return new WsClientHelper<NotificaWsV2PortType>().generarClientWs(
				getClass().getResource("/es/caib/notib/core/wsdl/NotificaWsV21.wsdl"), getNotificaUrlProperty(),
				new QName("https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/","NotificaWsV2Service"),
				getUsernameProperty(), getPasswordProperty(),true, NotificaWsV2PortType.class, new ApiKeySOAPHandlerV2(apiKey));
	}

	private static class ApiKeySOAPHandlerV2 implements SOAPHandler<SOAPMessageContext> {
		private final String apiKey;

		public ApiKeySOAPHandlerV2(String apiKey) {
			this.apiKey = apiKey;
		}

		@Override
		public boolean handleMessage(SOAPMessageContext context) {

			var outboundProperty = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (!outboundProperty) {
				return true;
			}
			try {
				var envelope = context.getMessage().getSOAPPart().getEnvelope();
				var factory = SOAPFactory.newInstance();
				/*SOAPElement apiKeyElement = factory.createElement("apiKey");*/
				var apiKeyElement = factory.createElement(new QName("https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/","apiKey"));
				apiKeyElement.addTextNode(apiKey);
				var header = envelope.getHeader();
				if (header == null) {
					header = envelope.addHeader();
				}
				header.addChildElement(apiKeyElement);
				context.getMessage().saveChanges();
			} catch (SOAPException ex) {
				log.error("No s'ha pogut afegir l'API key a la petició SOAP per Notifica", ex);
			}
			return true;
		}

		@Override
		public boolean handleFault(SOAPMessageContext context) {
			return false;
		}

		@Override
		public void close(MessageContext context) {
			// close
		}

		@Override
		public Set<QName> getHeaders() {
			return new TreeSet<>();
		}
	}

	private boolean isCodiDir3Entitat() {
		return configHelper.getConfigAsBoolean("es.caib.notib.plugin.codi.dir3.entitat");
	}


}
