package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.NotificaRespostaDatatDto.NotificaRespostaDatatEventDto;
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
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.core.wsdl.notificaV2.NotificaWsV2PortType;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.Destinatarios;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.Documento;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.EntregaDEH;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.EntregaPostal;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.Opcion;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.Opciones;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.OrganismoPagadorCIE;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.OrganismoPagadorPostal;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.Persona;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.*;
import es.caib.notib.core.wsdl.notificaV2.infoEnvioV2.*;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ejb.CreateException;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.naming.NamingException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
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
import java.util.*;

/**
 * Helper per a interactuar amb la versió 2 del servei web de Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class NotificaV2Helper extends AbstractNotificaHelper {

	@Autowired
	private NotificacioRepository notificacioRepository;
	@Autowired
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired 
	private EmailNotificacioHelper emailNotificacioHelper;
	@Autowired 
	ConversioTipusHelper conversioTipusHelper;
	@Autowired 
	ProcedimentRepository procedimentRepository;
	@Autowired
	IntegracioHelper integracioHelper;
	@Autowired
	private NotificacioEventHelper notificacioEventHelper;
	@Autowired
	private ConfigHelper configHelper;

	@UpdateNotificacioTable
	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.UPDATE)
	public NotificacioEntity notificacioEnviar(Long notificacioId) {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_NOTIFICA, 
				"Enviament d'una notificació", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Identificador de la notificacio", String.valueOf(notificacioId)));
		
		NotificacioEntity notificacio = notificacioRepository.findById(notificacioId);
		logger.info(" [NOT] Inici enviament notificació [Id: " + notificacio.getId() + ", Estat: " + notificacio.getEstat() + "]");
		if (!NotificacioEstatEnumDto.REGISTRADA.equals(notificacio.getEstat())) {
			logger.error(" [NOT] la notificació no té l'estat REGISTRADA.");
			integracioHelper.addAccioError(info, "La notificació no està registrada");
			throw new ValidationException(
					notificacioId,
					NotificacioEntity.class,
					"La notificació no te l'estat " + NotificacioEstatEnumDto.REGISTRADA);
		}

		notificacio.updateNotificaNouEnviament(pluginHelper.getNotificaReintentsPeriodeProperty());

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

				auditNotificacioHelper.updateNotificacioEnviada(notificacio);

				//Crea un nou event
				Map<NotificacioEnviamentEntity, String> identificadorsResultatsEnviaments = new HashMap<>();
				for (ResultadoEnvio resultadoEnvio: resultadoAlta.getResultadoEnvios().getItem()) {
					for (NotificacioEnviamentEntity enviament: notificacio.getEnviaments()) {
						if (enviament.getTitular().getNif().equalsIgnoreCase(resultadoEnvio.getNifTitular())) {
							identificadorsResultatsEnviaments.put(enviament, resultadoEnvio.getIdentificador());
						}
					}
				}
				notificacioEventHelper.addEnviamentNotificaOKEvent(notificacio, identificadorsResultatsEnviaments);
				elapsedTime = (System.nanoTime() - startTime) / 10e6;
				logger.info(" [TIMER-NOT] Notificació enviar (Preparar events)  [Id: " + notificacioId + "]: " + elapsedTime + " ms");

				integracioHelper.addAccioOk(info);
			} else {
				logger.info(" >>> ... ERROR");
				//Crea un nou event
				String errorDescripcio = "[" + resultadoAlta.getCodigoRespuesta() + "] " + resultadoAlta.getDescripcionRespuesta();
				updateEventWithEnviament(
						notificacio, 
						errorDescripcio, 
						NotificacioErrorTipusEnumDto.ERROR_REMOT,
						true);
				integracioHelper.addAccioError(info, errorDescripcio);
			}
		} catch (Exception ex) {
			logger.error(
					ex.getMessage(),
					ex);
			String errorDescripcio;
			if (ex instanceof SOAPFaultException) {
				errorDescripcio = ex.getMessage();
			} else {
				errorDescripcio = ExceptionUtils.getStackTrace(ex);
			}
			updateEventWithEnviament(
					notificacio, 
					errorDescripcio, 
					NotificacioErrorTipusEnumDto.ERROR_XARXA,
					false);
			integracioHelper.addAccioError(info, "Error al enviar la notificació", ex);
		}
		logger.info(" [NOT] Fi enviament notificació: [Id: " + notificacio.getId() + ", Estat: " + notificacio.getEstat() + "]");
		return notificacio;
	}


	public NotificacioEnviamentEntity enviamentRefrescarEstat(Long enviamentId) throws SistemaExternException {
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

	public NotificacioEnviamentEntity enviamentRefrescarEstat(Long enviamentId, boolean raiseExceptions) throws Exception {
		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findOne(enviamentId);
		return enviamentRefrescarEstat(enviament, raiseExceptions);
	}

	@UpdateEnviamentTable
	@Audita(entityType = TipusEntitat.ENVIAMENT, operationType = TipusOperacio.UPDATE)
	private NotificacioEnviamentEntity enviamentRefrescarEstat(NotificacioEnviamentEntity enviament, boolean raiseExceptions) throws Exception {
		
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_NOTIFICA, 
				"Consultar estat d'un enviament", 
				IntegracioAccioTipusEnumDto.ENVIAMENT, 
				new AccioParam("Identificador de l'enviament", String.valueOf(enviament.getId())));


		logger.info(" [EST] Inici actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
		NotificacioEntity notificacio = notificacioRepository.findById(enviament.getNotificacio().getId());
//		enviament.setNotificacio(notificacio);
		Date dataUltimDatat = enviament.getNotificaDataCreacio();
		Date dataUltimaCertificacio = enviament.getNotificaCertificacioData();

		enviament.updateNotificaDataRefrescEstat();
		enviament.updateNotificaNovaConsulta(pluginHelper.getConsultaReintentsPeriodeProperty());
		
		String errorPrefix = "Error al consultar l'estat d'un enviament fet amb NotificaV2 (" +
				"notificacioId=" + notificacio.getId() + ", " +
				"notificaIdentificador=" + enviament.getNotificaIdentificador() + ")";
		long startTime;
		double elapsedTime;
		try {
			if (enviament.getNotificaIdentificador() == null) {
				logger.info(" [EST] Fi actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
				String errorDescripcio = "L'enviament no té identificador de Notifica";
				integracioHelper.addAccioError(info, errorDescripcio);
				throw new ValidationException(
						enviament,
						NotificacioEnviamentEntity.class,
						errorDescripcio);
			}
			InfoEnvioV2 infoEnvio = new InfoEnvioV2();
			infoEnvio.setIdentificador(enviament.getNotificaIdentificador());

			startTime = System.nanoTime();
			String apiKey = enviament.getNotificacio().getEntitat().getApiKey();
			ResultadoInfoEnvioV2 resultadoInfoEnvio = getNotificaWs(apiKey).infoEnvioV2(infoEnvio);
			elapsedTime = (System.nanoTime() - startTime) / 10e6;
			logger.info(" [TIMER-EST] Refrescar estat enviament (infoEnvioV2)  [Id: " + enviament.getId() + "]: " + elapsedTime + " ms");

			if (resultadoInfoEnvio.getDatados() == null) {
				String errorDescripcio = "La resposta rebuda de Notifica no conté informació de datat";
				integracioHelper.addAccioError(info, errorDescripcio);
				throw new ValidationException(
						enviament,
						NotificacioEnviamentEntity.class,
						errorDescripcio);
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
				NotificaRespostaDatatEventDto event = new NotificaRespostaDatatEventDto();
				event.setData(datatData);
				event.setEstat(datado.getResultado());
			}
			if (datatDarrer == null) {
				String errorDescripcio = "No s'ha pogut trobar el darrer datat dins la resposta rebuda de Notifica";
				integracioHelper.addAccioError(info, errorDescripcio);
				throw new ValidationException(
						enviament,
						NotificacioEnviamentEntity.class,
						errorDescripcio);
			}

			if (resultadoInfoEnvio.getCertificacion() != null) {
				logger.info("Actualitzant informació enviament amb certificació...");
				startTime = System.nanoTime();
				Certificacion certificacio = resultadoInfoEnvio.getCertificacion();

				Date dataCertificacio = toDate(certificacio.getFechaCertificacion());
				if (!dataCertificacio.equals(dataUltimaCertificacio)) {
					byte[] decodificat = certificacio.getContenidoCertificacion();
					if (enviament.getNotificaCertificacioArxiuId() != null) {
						pluginHelper.gestioDocumentalDelete(
								enviament.getNotificaCertificacioArxiuId(),
								PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS);
					}
					String gestioDocumentalId = pluginHelper.gestioDocumentalCreate(
							PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS,
							decodificat);
					logger.info("Actualitzant certificació enviament...");
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
					logger.info("Fi actualització certificació");

					logger.info("Creant nou event per certificació...");
					//Crea un nou event
					notificacioEventHelper.addNotificaCallbackEvent(notificacio, enviament,
							NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO,
							datatDarrer.getResultado());
				}
				elapsedTime = (System.nanoTime() - startTime) / 10e6;
				logger.info(" [TIMER-EST] Actualitzar informació enviament amb certificació  [Id: " + enviament.getId() + "]: " + elapsedTime + " ms");
				logger.info("Enviament actualitzat");
			}
			notificacioEventHelper.addNotificaConsultaInfoEvent(notificacio, enviament, "", false);

			Date dataDatat = toDate(resultadoInfoEnvio.getFechaCreacion());
			NotificacioEnviamentEstatEnumDto estat = getEstatNotifica(datatDarrer.getResultado());
			logger.info("Actualitzant informació enviament amb Datat...");
			if (!dataDatat.equals(dataUltimDatat) || !estat.equals(enviament.getNotificaEstat())) {
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
				if (estat.name() != null)
					logger.info("Nou estat: " + estat.name());

				//Crea un nou event
				logger.info("Creant nou event per Datat...");
				notificacioEventHelper.addNotificaCallbackEvent(notificacio, enviament,
						NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT,
						datatDarrer.getResultado());
				logger.info("L'event s'ha guardat correctament...");

				logger.info("Actualitzant Datat enviament...");
				enviamentUpdateDatat(
						estat,
						toDate(datatDarrer.getFecha()),
						null,
						datatDarrer.getOrigen(),
						datatDarrer.getNifReceptor(),
						datatDarrer.getNombreReceptor(),
						null,
						null,
						enviament);
				logger.info("Fi actualització Datat");

				logger.info("Envio correu en cas d'usuaris no APLICACIÓ");
				if (notificacio.getTipusUsuari() == TipusUsuariEnumDto.INTERFICIE_WEB && notificacio.getEstat() == NotificacioEstatEnumDto.FINALITZADA) {
					startTime = System.nanoTime();
					emailNotificacioHelper.prepararEnvioEmailNotificacio(notificacio);
					elapsedTime = (System.nanoTime() - startTime) / 10e6;
					logger.info(" [TIMER-EST] Preparar enviament mail notificació (prepararEnvioEmailNotificacio)  [Id: " + enviament.getId() + "]: " + elapsedTime + " ms");
				}
			}
			logger.info("Enviament actualitzat");

			enviament.refreshNotificaConsulta();
			integracioHelper.addAccioOk(info);
			logger.info(" [EST] Fi actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");

		} catch (Exception ex) {
			logger.error(
					errorPrefix,
					ex);
			notificacioEventHelper.addNotificaConsultaInfoEvent(notificacio, enviament,
					ExceptionUtils.getStackTrace(ex),
					true);

			logger.info(" [EST] Fi actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
			if (enviament.getNotificaIntentNum() >= pluginHelper.getConsultaReintentsMaxProperty()) {
				notificacioEventHelper.addNotificaConsultaErrorEvent(notificacio, enviament);
			}
			integracioHelper.addAccioError(info, "Error consultat l'estat de l'enviament", ex);
			if (raiseExceptions){
				throw ex;
			}
		}
		return enviament;
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
			logger.error(
					"Error al donar d'alta la notificació a Notific@ (notificacioId=" + notificacio.getId() + ")",
					descripcioResposta);
		}
		return resultat;
	}

	private AltaRemesaEnvios generarAltaRemesaEnvios(
			NotificacioEntity notificacio) throws GeneralSecurityException, DatatypeConfigurationException, DecoderException {
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
				
				if (DataProgramadaWithZeroTime.after(todayWithZeroTime))
					envios.setFechaEnvioProgramado(toXmlGregorianCalendar(notificacio.getEnviamentDataProgramada()));
			}
			envios.setConcepto(notificacio.getConcepte().replace('·', '.'));
			if (notificacio.getDescripcio() != null)
				envios.setDescripcion(notificacio.getDescripcio().replace('·', '.'));
			
			envios.setProcedimiento(notificacio.getProcedimentCodiNotib());
			Documento  documento = new Documento();
			if(notificacio.getDocument() != null && notificacio.getDocument().getArxiuGestdocId() != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				pluginHelper.gestioDocumentalGet(
						notificacio.getDocument().getArxiuGestdocId(),
						PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
						baos);
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
	            opcionNormalizado.setValue(
	                    notificacio.getDocument().getNormalitzat()  ? "si" : "no");
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
	            opcionNormalizado.setValue(
	                    notificacio.getDocument().getNormalitzat()  ? "si" : "no");
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
				opcionNormalizado.setValue(
						notificacio.getDocument().getNormalitzat()  ? "si" : "no");
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
				retardPostal = procedimentRepository.findOne(notificacio.getProcediment().getId()).getRetard();
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
			logger.error(
					"Error generant la petició (notificacioId=" + notificacio.getId() + ")",
					ex);
		}
		return envios;
	}

	private Envios generarEnvios(
			NotificacioEntity notificacio) throws DatatypeConfigurationException {
		Envios envios = new Envios();
		for (NotificacioEnviamentEntity enviament: notificacio.getEnviaments()) {
			if (enviament != null) {
				Envio envio = new Envio();
				envio.setReferenciaEmisor(enviament.getNotificaReferencia());
				Persona titular = new Persona();
				if (enviament.getTitular().isIncapacitat() && enviament.getDestinataris() != null) {
					titular.setNif(enviament.getDestinataris().get(0).getNif());
					titular.setApellidos(
							concatenarLlinatges(
									enviament.getDestinataris().get(0).getLlinatge1(),
									enviament.getDestinataris().get(0).getLlinatge2()));
					titular.setTelefono(enviament.getDestinataris().get(0).getTelefon());
					titular.setEmail(enviament.getDestinataris().get(0).getEmail());
					if (enviament.getDestinataris().get(0).getInteressatTipus().equals(InteressatTipusEnumDto.JURIDICA))
						titular.setRazonSocial(enviament.getDestinataris().get(0).getRaoSocial());
					else
						titular.setNombre(enviament.getDestinataris().get(0).getNom());
					
					titular.setCodigoDestino(enviament.getDestinataris().get(0).getDir3Codi());
					enviament.getDestinataris().remove(0);
				} else {
					titular.setNif(enviament.getTitular().getNif());
					titular.setApellidos(
							concatenarLlinatges(
									enviament.getTitular().getLlinatge1(),
									enviament.getTitular().getLlinatge2()));
					titular.setTelefono(enviament.getTitular().getTelefon());
					titular.setEmail(enviament.getTitular().getEmail());
					if (enviament.getTitular().getInteressatTipus().equals(InteressatTipusEnumDto.JURIDICA))
						titular.setRazonSocial(enviament.getTitular().getRaoSocial());
					else
						titular.setNombre(enviament.getTitular().getNom());
					
					titular.setCodigoDestino(enviament.getTitular().getDir3Codi());
				}
				
				envio.setTitular(titular);
					Destinatarios destinatarios = new Destinatarios();
					for(PersonaEntity destinatari : enviament.getDestinataris()) {
						if (destinatari.getNif() != null) {
							Persona destinatario = new Persona();
							destinatario.setNif(destinatari.getNif());
							destinatario.setApellidos(
									concatenarLlinatges(
											destinatari.getLlinatge1(),
											destinatari.getLlinatge2()));
							destinatario.setTelefono(destinatari.getTelefon());
							destinatario.setEmail(destinatari.getEmail());
							if (destinatari.getInteressatTipus().equals(InteressatTipusEnumDto.JURIDICA))
								destinatario.setRazonSocial(destinatari.getRaoSocial());
							else
								destinatario.setNombre(destinatari.getNom());
							destinatario.setCodigoDestino(destinatari.getDir3Codi());
							destinatarios.getDestinatario().add(destinatario);
						}
					}
					if (!destinatarios.getDestinatario().isEmpty())
						envio.setDestinatarios(destinatarios);
					
					if (enviament.getDomiciliConcretTipus() != null) {
						EntregaPostal entregaPostal = new EntregaPostal();
						if (notificacio.getProcediment() != null && notificacio.getProcediment().getPagadorpostal() != null) {
							OrganismoPagadorPostal pagadorPostal = new OrganismoPagadorPostal();
							pagadorPostal.setCodigoDIR3Postal(notificacio.getProcediment().getPagadorpostal().getDir3codi());
							pagadorPostal.setCodClienteFacturacionPostal(notificacio.getProcediment().getPagadorpostal().getFacturacioClientCodi());
							pagadorPostal.setNumContratoPostal(notificacio.getProcediment().getPagadorpostal().getContracteNum());
							pagadorPostal.setFechaVigenciaPostal(
								toXmlGregorianCalendar(notificacio.getProcediment().getPagadorpostal().getContracteDataVig()));
							entregaPostal.setOrganismoPagadorPostal(pagadorPostal);
						}
						if (notificacio.getProcediment() != null && notificacio.getProcediment().getPagadorcie() != null) {
							OrganismoPagadorCIE pagadorCie = new OrganismoPagadorCIE();
							pagadorCie.setCodigoDIR3CIE(notificacio.getProcediment().getPagadorcie().getDir3codi());
							pagadorCie.setFechaVigenciaCIE(
								toXmlGregorianCalendar(notificacio.getProcediment().getPagadorcie().getContracteDataVig()));
							entregaPostal.setOrganismoPagadorCIE(pagadorCie);
						}
						if (enviament.getDomiciliConcretTipus() != null) {
							switch (enviament.getDomiciliConcretTipus())  {
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
						if (!enviament.getDomiciliConcretTipus().equals(NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR)) {
							entregaPostal.setTipoVia(enviament.getDomiciliViaTipus() != null ? enviament.getDomiciliViaTipus().getVal() : null); //viaTipusToString(enviament.getDomiciliViaTipus()));
							entregaPostal.setNombreVia(enviament.getDomiciliViaNom());
							entregaPostal.setNumeroCasa(enviament.getDomiciliNumeracioNumero());
							entregaPostal.setPuntoKilometrico(enviament.getDomiciliNumeracioPuntKm());
							entregaPostal.setPortal(enviament.getDomiciliPortal());
							entregaPostal.setPuerta(enviament.getDomiciliPorta());
							entregaPostal.setEscalera(enviament.getDomiciliEscala());
							entregaPostal.setPlanta(enviament.getDomiciliPlanta());
							entregaPostal.setBloque(enviament.getDomiciliBloc());
							entregaPostal.setComplemento(enviament.getDomiciliComplement());
							entregaPostal.setCalificadorNumero(enviament.getDomiciliNumeracioQualificador());
							entregaPostal.setCodigoPostal(enviament.getDomiciliCodiPostal());
							entregaPostal.setApartadoCorreos(enviament.getDomiciliApartatCorreus());
							entregaPostal.setMunicipio(enviament.getDomiciliMunicipiCodiIne());
							entregaPostal.setProvincia(enviament.getDomiciliProvinciaCodi());
							entregaPostal.setPais(enviament.getDomiciliPaisCodiIso());
							entregaPostal.setPoblacion(enviament.getDomiciliPoblacio());
						} else if (enviament.getDomiciliConcretTipus().equals(NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR)) {
							entregaPostal.setLinea1(enviament.getDomiciliLinea1());
							entregaPostal.setLinea2(enviament.getDomiciliLinea2());
							entregaPostal.setCodigoPostal(enviament.getDomiciliCodiPostal());
							entregaPostal.setPais(enviament.getDomiciliPaisCodiIso());
						}
						if (entregaPostal.getPais() == null) {
							entregaPostal.setPais("ES");
						}
						Opciones opcionesCie = new Opciones();
						if (enviament.getDomiciliCie() != null) {
							Opcion opcionCie = new Opcion();
							opcionCie.setTipo("cie");
							opcionCie.setValue(enviament.getDomiciliCie().toString()); // identificador CIE
							opcionesCie.getOpcion().add(opcionCie);
						}
						if (enviament.getFormatSobre() != null) {
							Opcion opcionFormatoSobre = new Opcion();
							opcionFormatoSobre.setTipo("formatoSobre");
							opcionFormatoSobre.setValue(enviament.getFormatSobre()); // americano, C5...
							opcionesCie.getOpcion().add(opcionFormatoSobre);
						}
						if (enviament.getFormatFulla() != null) {
							Opcion opcionFormatoHoja = new Opcion();
							opcionFormatoHoja.setTipo("formatoHoja");
							opcionFormatoHoja.setValue(enviament.getFormatFulla()); // A4, A5...
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
		}
		return envios;
	}
	
	private void updateEventWithEnviament(
			NotificacioEntity notificacio,
			String errorDescripcio,
			NotificacioErrorTipusEnumDto notificacioErrorTipus,
			boolean notificaError) {

		notificacioEventHelper.addErrorEvent(notificacio,
				NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT, errorDescripcio, notificacioErrorTipus, notificaError);
	}
	
	private NotificaWsV2PortType getNotificaWs(String apiKey) throws InstanceNotFoundException, MalformedObjectNameException, MalformedURLException, RemoteException, NamingException, CreateException {
		NotificaWsV2PortType port = new WsClientHelper<NotificaWsV2PortType>().generarClientWs(
				getClass().getResource("/es/caib/notib/core/wsdl/NotificaWsV21.wsdl"),
				getNotificaUrlProperty(),
				new QName(
						"https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/", 
						"NotificaWsV2Service"),
				getUsernameProperty(),
				getPasswordProperty(),
				true,
				NotificaWsV2PortType.class,
				new ApiKeySOAPHandlerV2(apiKey));
		return port;
	}

	public class ApiKeySOAPHandlerV2 implements SOAPHandler<SOAPMessageContext> {
		private final String apiKey;
		public ApiKeySOAPHandlerV2(String apiKey) {
			this.apiKey = apiKey;
		}
		@Override
		public boolean handleMessage(SOAPMessageContext context) {
			Boolean outboundProperty = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (outboundProperty.booleanValue()) {
				try {
					SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();
					SOAPFactory factory = SOAPFactory.newInstance();
					/*SOAPElement apiKeyElement = factory.createElement("apiKey");*/
					SOAPElement apiKeyElement = factory.createElement(
							new QName(
									"https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/", 
									"apiKey"));
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
					logger.error(
							"No s'ha pogut afegir l'API key a la petició SOAP per Notifica",
							ex);
	        	}
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
