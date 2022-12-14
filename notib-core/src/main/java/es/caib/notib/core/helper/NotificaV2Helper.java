package es.caib.notib.core.helper;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.InteressatTipusEnumDto;
import es.caib.notib.client.domini.NotificaDomiciliConcretTipusEnumDto;
import es.caib.notib.core.api.dto.AccioParam;
import es.caib.notib.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.core.api.dto.IntegracioInfo;
import es.caib.notib.core.api.dto.NotificacioErrorTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.core.api.exception.SistemaExternException;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.api.service.AuditService.TipusEntitat;
import es.caib.notib.core.api.service.AuditService.TipusOperacio;
import es.caib.notib.core.aspect.Audita;
import es.caib.notib.core.aspect.UpdateEnviamentTable;
import es.caib.notib.core.aspect.UpdateNotificacioTable;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.PersonaEntity;
import es.caib.notib.core.entity.ProcSerEntity;
import es.caib.notib.core.entity.cie.EntregaCieEntity;
import es.caib.notib.core.entity.cie.EntregaPostalEntity;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.ProcSerRepository;
import es.caib.notib.core.wsdl.notificaV2.NotificaWsV2PortType;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.*;
import es.caib.notib.core.wsdl.notificaV2.infoEnvioV2.Certificacion;
import es.caib.notib.core.wsdl.notificaV2.infoEnvioV2.CodigoDIR;
import es.caib.notib.core.wsdl.notificaV2.infoEnvioV2.Datado;
import es.caib.notib.core.wsdl.notificaV2.infoEnvioV2.InfoEnvioV2;
import es.caib.notib.core.wsdl.notificaV2.infoEnvioV2.ResultadoInfoEnvioV2;
import lombok.NonNull;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Helper per a interactuar amb la versió 2 del servei web de Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class NotificaV2Helper extends AbstractNotificaHelper {

	@Autowired
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private ProcSerRepository procSerRepository;
	@Autowired
	private IntegracioHelper integracioHelper;
	@Autowired
	private NotificacioEventHelper notificacioEventHelper;


	@UpdateNotificacioTable
	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.UPDATE)
	public NotificacioEntity notificacioEnviar(Long notificacioId, boolean ambEnviamentPerEmail) {

		IntegracioInfo info = new IntegracioInfo(IntegracioHelper.INTCODI_NOTIFICA,"Enviament d'una notificació", IntegracioAccioTipusEnumDto.ENVIAMENT,
												new AccioParam("Identificador de la notificacio", String.valueOf(notificacioId)));

		NotificacioEntity notificacio = notificacioRepository.findById(notificacioId);
		logger.info(" [NOT] Inici enviament notificació [Id: " + notificacio.getId() + ", Estat: " + notificacio.getEstat() + "]");
		info.setCodiEntitat(notificacio.getEntitat() != null ? notificacio.getEntitat().getCodi() : null);
		notificacio.updateNotificaNouEnviament(pluginHelper.getNotificaReintentsPeriodeProperty());
		if (!NotificacioEstatEnumDto.REGISTRADA.equals(notificacio.getEstat()) && !NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(notificacio.getEstat())) {
			logger.error(" [NOT] la notificació no té l'estat REGISTRADA.");
			integracioHelper.addAccioError(info, "La notificació no està registrada");
			throw new ValidationException(notificacioId, NotificacioEntity.class, "La notificació no te l'estat " + NotificacioEstatEnumDto.REGISTRADA);
		}
		try {
			logger.info(" >>> Enviant notificació...");

			long startTime = System.nanoTime();
			double elapsedTime;
			ResultadoAltaRemesaEnvios resultadoAlta = enviaNotificacio(notificacio);
			elapsedTime = (System.nanoTime() - startTime) / 10e6;
			logger.info(" [TIMER-NOT] Notificació enviar (enviaNotificacio SOAP-QUERY)  [Id: " + notificacioId + "]: " + elapsedTime + " ms");
			notificacio.updateNotificaEnviamentData();
			if ("000".equals(resultadoAlta.getCodigoRespuesta()) && "OK".equalsIgnoreCase(resultadoAlta.getDescripcionRespuesta())) {
				startTime = System.nanoTime();
				logger.info(" >>> ... OK");
				if (ambEnviamentPerEmail) {
					auditNotificacioHelper.updateNotificacioMixtaEnviadaNotifica(notificacio);
				} else {
 					auditNotificacioHelper.updateNotificacioEnviada(notificacio);
				}
				//Crea un nou event
				Map<NotificacioEnviamentEntity, String> identificadorsResultatsEnviaments = new HashMap<>();
				for (ResultadoEnvio resultadoEnvio: resultadoAlta.getResultadoEnvios().getItem()) {
					for (NotificacioEnviamentEntity enviament: notificacio.getEnviamentsPerNotifica()) {
						String nif = enviament.getTitular().getNif();
						if (nif != null && nif.equalsIgnoreCase(resultadoEnvio.getNifTitular())) {
							identificadorsResultatsEnviaments.put(enviament, resultadoEnvio.getIdentificador());
						}
					}
				}
				notificacioEventHelper.addEnviamentNotificaOKEvent(notificacio, identificadorsResultatsEnviaments);
				elapsedTime = (System.nanoTime() - startTime) / 10e6;
				logger.info(" [TIMER-NOT] Notificació enviar (Preparar events)  [Id: " + notificacioId + "]: " + elapsedTime + " ms");
				integracioHelper.addAccioOk(info);
			} else {
				logger.info(" >>> ... ERROR:");
				//Crea un nou event
				String errorDescripcio = "[" + resultadoAlta.getCodigoRespuesta() + "] " + resultadoAlta.getDescripcionRespuesta();
				logger.info(" >>> " + errorDescripcio);
				updateEventWithEnviament(notificacio, errorDescripcio, NotificacioErrorTipusEnumDto.ERROR_REMOT,true);
				integracioHelper.addAccioError(info, errorDescripcio);
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			String errorDescripcio = ex instanceof SOAPFaultException ? ex.getMessage() : ExceptionUtils.getStackTrace(ex);
			updateEventWithEnviament(notificacio, errorDescripcio, NotificacioErrorTipusEnumDto.ERROR_XARXA,true);
			integracioHelper.addAccioError(info, "Error al enviar la notificació", ex);
		}
		boolean fiReintents = notificacio.getNotificaEnviamentIntent() >= pluginHelper.getNotificaReintentsMaxProperty();
		if (fiReintents && (NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(notificacio.getEstat()) /*|| NotificacioEstatEnumDto.REGISTRADA.equals(notificacio.getEstat())*/)) {
			auditNotificacioHelper.updateNotificacioFinalitzadaAmbErrors(notificacio);
		}
		logger.info(" [NOT] Fi enviament notificació: [Id: " + notificacio.getId() + ", Estat: " + notificacio.getEstat() + "]");
		return notificacio;
	}


	@Transactional(timeout = 60, propagation = Propagation.REQUIRES_NEW)
	public NotificacioEnviamentEntity enviamentRefrescarEstat(Long enviamentId) throws SistemaExternException {

		logger.info(String.format(" [NOT] Refrescant estat de notific@ de l'enviament (Id=%d)", enviamentId));
		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findOne(enviamentId);
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
		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findOne(enviamentId);
		return enviamentRefrescarEstat(enviament, raiseExceptions);
	}

	@UpdateEnviamentTable
	@Audita(entityType = TipusEntitat.ENVIAMENT, operationType = TipusOperacio.UPDATE)
	private NotificacioEnviamentEntity enviamentRefrescarEstat(@NonNull NotificacioEnviamentEntity enviament, boolean raiseExceptions) throws Exception {

		IntegracioInfo info = new IntegracioInfo(IntegracioHelper.INTCODI_NOTIFICA,"Consultar estat d'un enviament", IntegracioAccioTipusEnumDto.ENVIAMENT,
												new AccioParam("Identificador de l'enviament", String.valueOf(enviament.getId())));

		logger.info(" [EST] Inici actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
		info.setCodiEntitat(enviament.getNotificacio() != null && enviament.getNotificacio().getEntitat() != null ?  enviament.getNotificacio().getEntitat().getCodi() : null);
		long startTime;
		double elapsedTime;
		try {
			Date dataUltimDatat = enviament.getNotificaDataCreacio();

			enviament.updateNotificaDataRefrescEstat();
			enviament.updateNotificaNovaConsulta(pluginHelper.getConsultaReintentsPeriodeProperty());

			if (enviament.getNotificaIdentificador() == null) {
				logger.info(" [EST] Fi actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
				String errorDescripcio = "L'enviament no té identificador de Notifica";
				integracioHelper.addAccioError(info, errorDescripcio);
				throw new ValidationException(enviament, NotificacioEnviamentEntity.class, errorDescripcio);
			}

			startTime = System.nanoTime();
			ResultadoInfoEnvioV2 resultadoInfoEnvio = getNotificaResultadoInfoEnvio(enviament, info);
			elapsedTime = (System.nanoTime() - startTime) / 10e6;
			logger.info(" [TIMER-EST] Refrescar estat enviament (infoEnvioV2)  [Id: " + enviament.getId() + "]: " + elapsedTime + " ms");

			Datado darrerDatat = getDarrerDatat(resultadoInfoEnvio, enviament, info);
			if (resultadoInfoEnvio.getCertificacion() != null) {
				logger.info(" [EST] Actualitzant certificació de l'enviament [Id: " + enviament.getId() + "] ...");
				actualitzaCertificacio(resultadoInfoEnvio, enviament, darrerDatat);

			} else {
				logger.info(" [EST] Notifica no té cap certificació de l'enviament [Id: " + enviament.getId() + "] ...");

			}
			notificacioEventHelper.addNotificaConsultaInfoEvent(enviament.getNotificacio(), enviament, "", false);

			logger.info(" [EST] Actualitzant informació enviament amb Datat...");
			Date dataDatat = toDate(resultadoInfoEnvio.getFechaCreacion());
			EnviamentEstat estat = getEstatNotifica(darrerDatat.getResultado());
			if (!dataDatat.equals(dataUltimDatat) || !estat.equals(enviament.getNotificaEstat())) {
				actualitzaDatatEnviament(resultadoInfoEnvio, enviament, darrerDatat);
			}

			logger.info(" [EST] Enviament actualitzat");

			enviament.refreshNotificaConsulta();
			integracioHelper.addAccioOk(info);
			logger.info(" [EST] Fi actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");

		} catch (Exception ex) {
			notificacioEventHelper.addNotificaConsultaInfoEvent(enviament.getNotificacio(), enviament, ExceptionUtils.getStackTrace(ex),true);
			logger.info(" [EST] Fi actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
			if (enviament.getNotificaIntentNum() >= pluginHelper.getConsultaReintentsMaxProperty()) {
				notificacioEventHelper.addNotificaConsultaErrorEvent(enviament.getNotificacio(), enviament);
			}
			integracioHelper.addAccioError(info, "Error consultat l'estat de l'enviament", ex);
			if (raiseExceptions){
				throw ex;
			}
			String errorPrefix = "Error al consultar l'estat d'un enviament fet amb NotificaV2 (notificacioId=" + enviament.getNotificacio().getId() + ", "
								+ "notificaIdentificador=" + enviament.getNotificaIdentificador() + ")";
			logger.error(errorPrefix, ex);

		}
		return enviament;
	}

	private ResultadoInfoEnvioV2 getNotificaResultadoInfoEnvio(NotificacioEnviamentEntity enviament, IntegracioInfo info) throws Exception {

		InfoEnvioV2 infoEnvio = new InfoEnvioV2();
		infoEnvio.setIdentificador(enviament.getNotificaIdentificador());
		String apiKey = enviament.getNotificacio().getEntitat().getApiKey();
		ResultadoInfoEnvioV2 resultadoInfoEnvio = getNotificaWs(apiKey).infoEnvioV2(infoEnvio);
		if (resultadoInfoEnvio.getDatados() == null) {
			String errorDescripcio = "La resposta rebuda de Notifica no conté informació de datat";
			integracioHelper.addAccioError(info, errorDescripcio);
			throw new ValidationException(enviament, NotificacioEnviamentEntity.class, errorDescripcio);
		}
		return resultadoInfoEnvio;
	}

	private Datado getDarrerDatat(ResultadoInfoEnvioV2 resultadoInfoEnvio, NotificacioEnviamentEntity enviament, IntegracioInfo info) throws DatatypeConfigurationException {

		info.setCodiEntitat(enviament.getNotificacio().getEntitat().getCodi());
		if (resultadoInfoEnvio.getDatados() == null) {
			String errorDescripcio = "La resposta rebuda de Notifica no conté informació de datat";
			integracioHelper.addAccioError(info, errorDescripcio);
			throw new ValidationException(enviament, NotificacioEnviamentEntity.class, errorDescripcio);
		}

		Datado datatDarrer = null;
		for (Datado datado: resultadoInfoEnvio.getDatados().getDatado()) {
			Date datatData = toDate(datado.getFecha());
			if (datatDarrer == null) {
				datatDarrer = datado;
			} else if (datado.getFecha() != null) {
				Date datatDarrerData = toDate(datatDarrer.getFecha());
				if (datatData.after(datatDarrerData)) {
					datatDarrer = datado;
				}
			}
		}
		if (datatDarrer == null) {
			String errorDescripcio = "No s'ha pogut trobar el darrer datat dins la resposta rebuda de Notifica";
			integracioHelper.addAccioError(info, errorDescripcio);
			throw new ValidationException(enviament, NotificacioEnviamentEntity.class, errorDescripcio);
		}
		return datatDarrer;
	}

	private void actualitzaCertificacio(ResultadoInfoEnvioV2 resultadoInfoEnvio,
										NotificacioEnviamentEntity enviament,
										Datado darrerDatat) throws DatatypeConfigurationException {
		Date dataUltimaCertificacio = enviament.getNotificaCertificacioData();
		Certificacion certificacio = resultadoInfoEnvio.getCertificacion();
		Date dataCertificacio = toDate(certificacio.getFechaCertificacion());
		if (dataCertificacio.equals(dataUltimaCertificacio)) {
			logger.info(" [EST] El certificat de l'enviament ja esteia actualitzat");
			return;
		}
		byte[] decodificat = certificacio.getContenidoCertificacion();
		if (enviament.getNotificaCertificacioArxiuId() != null) {
			pluginHelper.gestioDocumentalDelete(
					enviament.getNotificaCertificacioArxiuId(),
					PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS);
		}
		String gestioDocumentalId = pluginHelper.gestioDocumentalCreate(
				PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS,
				decodificat);

		logger.info(" [EST] Actualitzant certificació enviament...");
		enviament.updateNotificaCertificacio(
				dataCertificacio,
				gestioDocumentalId,
				certificacio.getHash(),
				certificacio.getOrigen(),
				certificacio.getMetadatos(),
				certificacio.getCsv(),
				certificacio.getMime(),
				Integer.parseInt(certificacio.getSize()),
				null,
				null,
				null);

		logger.info(" [EST] Fi actualització certificació. Creant nou event per certificació...");
		//Crea un nou event
		notificacioEventHelper.addNotificaCallbackEvent(enviament.getNotificacio(), enviament,
				NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO,
				darrerDatat.getResultado());
	}

	private void actualitzaDatatEnviament(ResultadoInfoEnvioV2 resultadoInfoEnvio,
										  NotificacioEnviamentEntity enviament,
										  Datado darrerDatat) throws Exception {
		Date dataDatat = toDate(resultadoInfoEnvio.getFechaCreacion());
		EnviamentEstat estat = getEstatNotifica(darrerDatat.getResultado());
		CodigoDIR organismoEmisor = resultadoInfoEnvio.getCodigoOrganismoEmisor();
		CodigoDIR organismoEmisorRaiz = resultadoInfoEnvio.getCodigoOrganismoEmisorRaiz();
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
		if (estat != null)
			logger.info(" [EST] Nou estat: " + estat.name());

		//Crea un nou event
		logger.info(" [EST] Creant nou event per Datat...");
		notificacioEventHelper.addNotificaCallbackEvent(enviament.getNotificacio(), enviament,
				NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT,
				darrerDatat.getResultado());
		logger.info(" [EST] L'event s'ha guardat correctament...");

		logger.info(" [EST] Actualitzant Datat enviament...");
		enviamentUpdateDatat(
				estat,
				toDate(darrerDatat.getFecha()),
				null,
				darrerDatat.getOrigen(),
				darrerDatat.getNifReceptor(),
				darrerDatat.getNombreReceptor(),
				null,
				null,
				enviament);
		logger.info(" [EST] Fi actualització Datat");
	}

	private ResultadoAltaRemesaEnvios enviaNotificacio(NotificacioEntity notificacio) throws Exception {

		ResultadoAltaRemesaEnvios resultat = null;
		try {
			String apiKey = notificacio.getEntitat().getApiKey();
			AltaRemesaEnvios altaRemesaEnvios = generarAltaRemesaEnvios(notificacio);
			resultat = getNotificaWs(apiKey).altaRemesaEnvios(altaRemesaEnvios);
		} catch (SOAPFaultException sfe) {
			String codiResposta = sfe.getFault().getFaultCode();
			String descripcioResposta = sfe.getFault().getFaultString();
			resultat = new ResultadoAltaRemesaEnvios();
			resultat.setCodigoRespuesta(codiResposta);
			resultat.setDescripcionRespuesta(descripcioResposta);
			logger.error("Error al donar d'alta la notificació a Notific@ (notificacioId=" + notificacio.getId() + ")", descripcioResposta);
		}
		return resultat;
	}

	private AltaRemesaEnvios generarAltaRemesaEnvios(NotificacioEntity notificacio) throws GeneralSecurityException, DatatypeConfigurationException, DecoderException {

		AltaRemesaEnvios envios = new AltaRemesaEnvios();
		Integer retardPostal = null;
		try {
//			envios.setCodigoOrganismoEmisor(notificacio.getEntitat().getDir3Codi());
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
	 			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				Date today = new Date();
				Date todayWithZeroTime = formatter.parse(formatter.format(today));
				Date DataProgramadaWithZeroTime = formatter.parse(formatter.format(notificacio.getEnviamentDataProgramada()));
				
				if (DataProgramadaWithZeroTime.after(todayWithZeroTime)) {
					envios.setFechaEnvioProgramado(toXmlGregorianCalendar(notificacio.getEnviamentDataProgramada()));
				}
			}
			envios.setConcepto(notificacio.getConcepte().replace('·', '.'));
			if (notificacio.getDescripcio() != null) {
				envios.setDescripcion(notificacio.getDescripcio().replace('·', '.'));
			}
			envios.setProcedimiento(notificacio.getProcedimentCodiNotib());
			Documento  documento = new Documento();
			if(notificacio.getDocument() != null && notificacio.getDocument().getArxiuGestdocId() != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				pluginHelper.gestioDocumentalGet(notificacio.getDocument().getArxiuGestdocId(), PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS, baos);
				documento.setContenido(baos.toByteArray());
				Opciones opcionesDocumento = new Opciones();
				Opcion opcionNormalizado = new Opcion();
				opcionNormalizado.setTipo("normalizado");
				opcionNormalizado.setValue(notificacio.getDocument().getNormalitzat()  ? "si" : "no");
				opcionesDocumento.getOpcion().add(opcionNormalizado);
				Opcion opcionGenerarCsv = new Opcion();
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
				byte[] contingut = pluginHelper.documentToRegistreAnnexDto(notificacio.getDocument()).getArxiuContingut();
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
	        	String url = notificacio.getDocument().getUrl();
	            documento.setEnlaceDocumento(url);           
	            String hash256 = Base64.encodeBase64String(Hex.decodeHex(DigestUtils.sha256Hex(url).toCharArray()));
				//Hash a enviar
				documento.setHash(hash256);
	            Opciones opcionesDocumento = new Opciones();
	            Opcion opcionNormalizado = new Opcion();
	            opcionNormalizado.setTipo("normalizado");
	            opcionNormalizado.setValue(notificacio.getDocument().getNormalitzat()  ? "si" : "no");
	            opcionesDocumento.getOpcion().add(opcionNormalizado);
	            Opcion opcionGenerarCsv = new Opcion();
	            opcionGenerarCsv.setTipo("generarCsv");
	            opcionGenerarCsv.setValue("no");
	            opcionesDocumento.getOpcion().add(opcionGenerarCsv);
	            documento.setOpcionesDocumento(opcionesDocumento);
	            envios.setDocumento(documento);
	        } else if (notificacio.getDocument() != null && notificacio.getDocument().getUuid() != null) {
	            byte[] contingut = pluginHelper.documentToRegistreAnnexDto(notificacio.getDocument()).getArxiuContingut();
	        	documento.setContenido(contingut);
	            if(contingut != null) {
					String hash256 = Base64.encodeBase64String(Hex.decodeHex(DigestUtils.sha256Hex(contingut).toCharArray()));
					//Hash a enviar
					documento.setHash(hash256);
				}
	            Opciones opcionesDocumento = new Opciones();
	            Opcion opcionNormalizado = new Opcion();
	            opcionNormalizado.setTipo("normalizado");
	            opcionNormalizado.setValue(notificacio.getDocument().getNormalitzat()  ? "si" : "no");
	            opcionesDocumento.getOpcion().add(opcionNormalizado);
	            Opcion opcionGenerarCsv = new Opcion();
	            opcionGenerarCsv.setTipo("generarCsv");
	            opcionGenerarCsv.setValue("no");
	            opcionesDocumento.getOpcion().add(opcionGenerarCsv);
	            documento.setOpcionesDocumento(opcionesDocumento);
	            envios.setDocumento(documento);
	        } else if(notificacio.getDocument() != null) {
				documento.setHash(notificacio.getDocument().getHash());
				if(notificacio.getDocument().getContingutBase64() != null) {
		        	byte[] contingut = notificacio.getDocument().getContingutBase64().getBytes();
					documento.setContenido(contingut);	
					String hash256 = Base64.encodeBase64String(Hex.decodeHex(DigestUtils.sha256Hex(contingut).toCharArray()));
					//Hash a enviar
					documento.setHash(hash256);
				}
				Opciones opcionesDocumento = new Opciones();
				Opcion opcionNormalizado = new Opcion();
				opcionNormalizado.setTipo("normalizado");
				opcionNormalizado.setValue(notificacio.getDocument().getNormalitzat()  ? "si" : "no");
				opcionesDocumento.getOpcion().add(opcionNormalizado);
				Opcion opcionGenerarCsv = new Opcion();
				opcionGenerarCsv.setTipo("generarCsv");
				opcionGenerarCsv.setValue("no");
				opcionesDocumento.getOpcion().add(opcionGenerarCsv);
				documento.setOpcionesDocumento(opcionesDocumento);
				envios.setDocumento(documento);
			}

			envios.setEnvios(generarEnvios(notificacio));
			Opciones opcionesRemesa = new Opciones();

			if(notificacio.getRetard() != null) {
				retardPostal = notificacio.getRetard();
			} else if (notificacio.getProcediment() != null) {
				ProcSerEntity procSer = procSerRepository.findOne(notificacio.getProcediment().getId());
				retardPostal = procSer != null ? procSer.getRetard() : null;
			}
			
			if (retardPostal != null) {
				Opcion opcionRetardo = new Opcion();
				opcionRetardo.setTipo("retardo");
				opcionRetardo.setValue(retardPostal.toString()); // número de días
				opcionesRemesa.getOpcion().add(opcionRetardo);
			}

			if (notificacio.getCaducitat() != null) {
				Opcion opcionCaducidad = new Opcion();
				opcionCaducidad.setTipo("caducidad");
				SimpleDateFormat sdfCaducitat = new SimpleDateFormat("yyyy-MM-dd");
				opcionCaducidad.setValue(sdfCaducitat.format(notificacio.getCaducitat())); // formato YYYY-MM-DD
				opcionesRemesa.getOpcion().add(opcionCaducidad);
			}
			envios.setOpcionesRemesa(opcionesRemesa);
		} catch (Exception ex) {
			logger.error("Error generant la petició (notificacioId=" + notificacio.getId() + ")", ex);
		}
		return envios;
	}

	private Envios generarEnvios(NotificacioEntity notificacio) throws DatatypeConfigurationException {

		Envios envios = new Envios();
		for (NotificacioEnviamentEntity enviament: notificacio.getEnviamentsPerNotifica()) {
			if (enviament == null) {
				continue;
			}
			Envio envio = new Envio();
//			envio.setReferenciaEmisor(enviament.getNotificaReferencia());
			Persona titular = new Persona();
			if (enviament.getTitular().isIncapacitat() && enviament.getDestinataris() != null) {
				titular.setNif(enviament.getDestinataris().get(0).getNif());
				titular.setApellidos(concatenarLlinatges(enviament.getDestinataris().get(0).getLlinatge1(), enviament.getDestinataris().get(0).getLlinatge2()));
				titular.setTelefono(enviament.getDestinataris().get(0).getTelefon());
				titular.setEmail(enviament.getDestinataris().get(0).getEmail());
				if (enviament.getDestinataris().get(0).getInteressatTipus().equals(InteressatTipusEnumDto.JURIDICA)) {
					titular.setRazonSocial(enviament.getDestinataris().get(0).getRaoSocial());
				} else {
					titular.setNombre(enviament.getDestinataris().get(0).getNom());
				}
				titular.setCodigoDestino(enviament.getDestinataris().get(0).getDir3Codi());
				enviament.getDestinataris().remove(0);
			} else {
				titular.setNif(InteressatTipusEnumDto.FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus()) ? null : enviament.getTitular().getNif());
				titular.setApellidos(concatenarLlinatges(enviament.getTitular().getLlinatge1(), enviament.getTitular().getLlinatge2()));
				titular.setTelefono(enviament.getTitular().getTelefon());
				titular.setEmail(InteressatTipusEnumDto.FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus()) ? null : enviament.getTitular().getEmail());
				if (enviament.getTitular().getInteressatTipus().equals(InteressatTipusEnumDto.JURIDICA)) {
					titular.setRazonSocial(enviament.getTitular().getRaoSocial());
				} else {
					titular.setNombre(enviament.getTitular().getNom());
				}
				titular.setCodigoDestino(enviament.getTitular().getDir3Codi());
			}

			envio.setTitular(titular);
			Destinatarios destinatarios = new Destinatarios();
			for(PersonaEntity destinatari : enviament.getDestinataris()) {
				if (destinatari.getNif() != null) {
					Persona destinatario = new Persona();
					destinatario.setNif(destinatari.getNif());
					destinatario.setApellidos(concatenarLlinatges(destinatari.getLlinatge1(), destinatari.getLlinatge2()));
					destinatario.setTelefono(destinatari.getTelefon());
					destinatario.setEmail(destinatari.getEmail());
					if (destinatari.getInteressatTipus().equals(InteressatTipusEnumDto.JURIDICA)) {
						destinatario.setRazonSocial(destinatari.getRaoSocial());
					} else {
						destinatario.setNombre(destinatari.getNom());
					}
					destinatario.setCodigoDestino(destinatari.getDir3Codi());
					destinatarios.getDestinatario().add(destinatario);
				}
			}
			if (!destinatarios.getDestinatario().isEmpty()) {
				envio.setDestinatarios(destinatarios);
			}
			if (enviament.getEntregaPostal() != null) {
				EntregaPostal entregaPostal = new EntregaPostal();
				ProcSerEntity procedimentNotificacio = notificacio.getProcediment();
				if (procedimentNotificacio != null && procedimentNotificacio.getEntregaCie() != null) {
					EntregaCieEntity entregaCieEntity = procedimentNotificacio.getEntregaCie();
					if (entregaCieEntity.getOperadorPostal() != null) {
						OrganismoPagadorPostal pagadorPostal = new OrganismoPagadorPostal();
						pagadorPostal.setCodigoDIR3Postal(entregaCieEntity.getOperadorPostal().getOrganGestor().getCodi());
						pagadorPostal.setCodClienteFacturacionPostal(entregaCieEntity.getOperadorPostal().getFacturacioClientCodi());
						pagadorPostal.setNumContratoPostal(entregaCieEntity.getOperadorPostal().getContracteNum());
						pagadorPostal.setFechaVigenciaPostal(toXmlGregorianCalendar(entregaCieEntity.getOperadorPostal().getContracteDataVig()));
						entregaPostal.setOrganismoPagadorPostal(pagadorPostal);
					}
					if (entregaCieEntity.getCie() != null) {
						OrganismoPagadorCIE pagadorCie = new OrganismoPagadorCIE();
						pagadorCie.setCodigoDIR3CIE(entregaCieEntity.getCie().getOrganGestor().getCodi());
						pagadorCie.setFechaVigenciaCIE(toXmlGregorianCalendar(entregaCieEntity.getCie().getContracteDataVig()));
						entregaPostal.setOrganismoPagadorCIE(pagadorCie);
					}
				}
				EntregaPostalEntity entregaPostalEntity = enviament.getEntregaPostal();
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
				if (!NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR.equals(entregaPostalEntity.getDomiciliConcretTipus())) {
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
				Opciones opcionesCie = new Opciones();
				if (entregaPostalEntity.getDomiciliCie() != null) {
					Opcion opcionCie = new Opcion();
					opcionCie.setTipo("cie");
					opcionCie.setValue(entregaPostalEntity.getDomiciliCie().toString()); // identificador CIE
					opcionesCie.getOpcion().add(opcionCie);
				}
				if (entregaPostalEntity.getFormatSobre() != null) {
					Opcion opcionFormatoSobre = new Opcion();
					opcionFormatoSobre.setTipo("formatoSobre");
					opcionFormatoSobre.setValue(entregaPostalEntity.getFormatSobre()); // americano, C5...
					opcionesCie.getOpcion().add(opcionFormatoSobre);
				}
				if (entregaPostalEntity.getFormatFulla() != null) {
					Opcion opcionFormatoHoja = new Opcion();
					opcionFormatoHoja.setTipo("formatoHoja");
					opcionFormatoHoja.setValue(entregaPostalEntity.getFormatFulla()); // A4, A5...
					opcionesCie.getOpcion().add(opcionFormatoHoja);
				}
				entregaPostal.setOpcionesCIE(opcionesCie);
				envio.setEntregaPostal(entregaPostal);
			}
			if (enviament.getDehNif() != null && enviament.getDehProcedimentCodi() != null && enviament.getDehObligat() != null) {
				EntregaDEH entregaDeh = new EntregaDEH();
				entregaDeh.setObligado(enviament.getDehObligat());
				entregaDeh.setCodigoProcedimiento(enviament.getDehProcedimentCodi());
				envio.setEntregaDEH(entregaDeh);
			}
			envios.getEnvio().add(envio);
		}
		return envios;
	}
	
	private void updateEventWithEnviament(NotificacioEntity notificacio, String errorDescripcio, NotificacioErrorTipusEnumDto notificacioErrorTipus, boolean notificaError) {

		notificacioEventHelper.addErrorEvent(notificacio, NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT, errorDescripcio, notificacioErrorTipus, notificaError);
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

			Boolean outboundProperty = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (!outboundProperty.booleanValue()) {
				return true;
			}
			try {
				SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();
				SOAPFactory factory = SOAPFactory.newInstance();
				/*SOAPElement apiKeyElement = factory.createElement("apiKey");*/
				SOAPElement apiKeyElement = factory.createElement(new QName("https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/","apiKey"));
				apiKeyElement.addTextNode(apiKey);
				SOAPHeader header = envelope.getHeader();
				if (header == null) {
					header = envelope.addHeader();
				}
				header.addChildElement(apiKeyElement);
				context.getMessage().saveChanges();

				// Debug
//					StringBuilder sb = new StringBuilder();
//					ByteArrayOutputStream baos = new ByteArrayOutputStream();
//					try {
//						context.getMessage().writeTo(baos);
//						sb.append(baos.toString());
//					} catch (Exception ex) {
//						sb.append("Error al processar el missatge XML: " + ex.getMessage());
//					}
//					logger.info(sb.toString());

			} catch (SOAPException ex) {
				logger.error("No s'ha pogut afegir l'API key a la petició SOAP per Notifica", ex);
			}
	        return true;
	    }
		@Override
		public boolean handleFault(SOAPMessageContext context) {
			return false;
		}
		@Override
		public void close(MessageContext context) {
		}
		@Override
		public Set<QName> getHeaders() {
			return new TreeSet<QName>();
		}
	}

	private boolean isCodiDir3Entitat() {
		return configHelper.getAsBoolean("es.caib.notib.plugin.codi.dir3.entitat");
	}
	private static final Logger logger = LoggerFactory.getLogger(NotificaV2Helper.class);

}
