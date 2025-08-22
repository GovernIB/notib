package es.caib.notib.logic.helper;

import com.google.common.base.Strings;
import es.caib.notib.client.domini.CieEstat;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.client.domini.NotificaDomiciliConcretTipus;
import es.caib.notib.client.domini.ampliarPlazo.AmpliarPlazoOE;
import es.caib.notib.client.domini.ampliarPlazo.RespuestaAmpliarPlazoOE;
import es.caib.notib.logic.comanda.ComandaListener;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.exception.ValidationException;
import es.caib.notib.logic.intf.service.AuditService.TipusOperacio;
import es.caib.notib.logic.intf.statemachine.events.ConsultaNotificaRequest;
import es.caib.notib.logic.intf.ws.adviser.nexea.NexeaAdviserWs;
import es.caib.notib.logic.intf.ws.adviser.nexea.sincronizarenvio.SincronizarEnvio;
import es.caib.notib.logic.objectes.LoggingTipus;
import es.caib.notib.logic.utils.NotibLogger;
import es.caib.notib.logic.wsdl.notificaV2.NotificaWsV2PortType;
import es.caib.notib.logic.wsdl.notificaV2.SincronizarEnvioWsPortType;
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
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioLigero.Datado;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioLigero.InfoEnvioLigero;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioLigero.RespuestaInfoEnvioLigero;
import es.caib.notib.logic.wsdl.notificaV2.sincronizarEnvioOE.Acuse;
import es.caib.notib.logic.wsdl.notificaV2.sincronizarEnvioOE.Receptor;
import es.caib.notib.logic.wsdl.notificaV2.sincronizarEnvioOE.RespuestaSincronizarEnvioOE;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.ProcSerRepository;
import es.caib.notib.plugin.utils.WsClientHelper;
import lombok.extern.slf4j.Slf4j;
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
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static es.caib.notib.logic.helper.SubsistemesHelper.SubsistemesEnum.CIE;
import static es.caib.notib.logic.helper.SubsistemesHelper.SubsistemesEnum.NOT;

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
	@Autowired
	private AccioMassivaHelper accioMassivaHelper;
    @Autowired
    private ComandaListener comandaListener;

	private static final String NOTIB = "Notib";
    @Autowired
    private ConversioTipusHelper conversioTipusHelper;

	public NotificacioEntity notificacioEnviar(Long notificacioId, boolean ambEnviamentPerEmail) {

		long start = System.currentTimeMillis();
		boolean errorSbs = false;
 		var info = new IntegracioInfo(IntegracioCodi.NOTIFICA,"Enviament d'una notificació", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Identificador de la notificacio", String.valueOf(notificacioId)));

		try {
			var notificacio = notificacioRepository.findById(notificacioId).orElseThrow();
			info.setAplicacio(notificacio.getTipusUsuari(), notificacio.getCreatedBy().get().getCodi());
			log.info(" [NOT] Inici enviament notificació [Id: " + notificacio.getId() + ", Estat: " + notificacio.getEstat() + "]");
			info.setCodiEntitat(notificacio.getEntitat() != null ? notificacio.getEntitat().getCodi() : null);
			notificacio.updateNotificaNouEnviament();
			// Validacions
			if (!NotificacioEstatEnumDto.REGISTRADA.equals(notificacio.getEstat()) && !NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(notificacio.getEstat())) {
				var msg = "la notificació no té l'estat REGISTRADA o ENVIADA AMB ERRORS.";
				log.error(" [NOT] " + msg);
				integracioHelper.addAccioError(info, msg);
				throw new ValidationException(notificacioId, NotificacioEntity.class, msg);
			}
			var error = false;
			String errorDescripcio = null;
			notificacio.updateNotificaEnviamentData();
			try {
				log.info(" >>> Enviant notificació...");
				var startTime = System.nanoTime();
				double elapsedTime;
				var resultadoAlta = enviaNotificacio(notificacio);
				elapsedTime = (System.nanoTime() - startTime) / 10e6;
				log.info(" [TIMER-NOT] Notificació enviar (enviaNotificacio SOAP-QUERY)  [Id: " + notificacioId + "]: " + elapsedTime + " ms");
				var resultadoEnvios = resultadoAlta.getResultadoEnvios();
//				if ("000".equals(resultadoAlta.getCodigoRespuesta()) && "OK".equalsIgnoreCase(resultadoAlta.getDescripcionRespuesta())) {
				if ("000".equals(resultadoAlta.getCodigoRespuesta()) && resultadoEnvios != null && !resultadoEnvios.getItem().isEmpty()) {
					if (!"OK".equalsIgnoreCase(resultadoAlta.getDescripcionRespuesta())) {
						// TODO FICAR MISSATGE DE WARNING (IMATGE APB)
						log.info("[Notificav2Helper] Enviament amb estat 000 pero desc no ok. Desc: " + resultadoAlta.getDescripcionRespuesta());
					}

					startTime = System.nanoTime();
					log.info(" >>> ... OK");
					if (!ambEnviamentPerEmail) {
						notificacio.updateEstat(NotificacioEstatEnumDto.ENVIADA);
					}
					List<Long> enviamentsActualitzats = new ArrayList<>();
					List<String> identificadorsNoUtilitzats = new ArrayList<>();
					for (var resultadoEnvio: resultadoEnvios.getItem()) {
						boolean assignat = false;
						for (var enviament: notificacio.getEnviamentsPerNotifica()) {
							var nif = enviament.getTitular().isIncapacitat() ? enviament.getDestinataris().get(0).getNif() : enviament.getTitular().getNif();
							if (nif != null && nif.equalsIgnoreCase(resultadoEnvio.getNifTitular()) && !enviamentsActualitzats.contains(enviament.getId())) {
								enviamentsActualitzats.add(enviament.getId());
								enviament.updateNotificaEnviada(resultadoEnvio.getIdentificador());
								enviamentTableHelper.actualitzarRegistre(enviament);
								auditHelper.auditaEnviament(enviament, TipusOperacio.UPDATE, "NotificaV2Helper.notificacioEnviar");
								assignat = true;
								break;
							}
						}
						if (!assignat) {
							identificadorsNoUtilitzats.add(resultadoEnvio.getIdentificador());
						}
					}
					if (!identificadorsNoUtilitzats.isEmpty()) {
						int i = 0;
						for (var enviament: notificacio.getEnviamentsPerNotifica()) {
							if (enviament.getNotificaIdentificador() == null && i < identificadorsNoUtilitzats.size()) {
								enviament.updateNotificaEnviada(identificadorsNoUtilitzats.get(i++));
							}
						}
					}

					// Amb el canvi de infoEnvioV2 cap a infoLigero, ara no es disposa dels camps de cada de caducitat i de disposició.
					// Per això les emplenem aquí, amb les dades amb que hem realitzat la petició.
					var dataCreacio = toDate(resultadoAlta.getFechaCreacion().getValue());
					Date dataDisposicio = notificacio.getEnviamentDataProgramada() != null ? notificacio.getEnviamentDataProgramada() : dataCreacio;
					incrementarDataCaducitat(notificacio, dataCreacio);
					Date dataCaducitat = notificacio.getCaducitat();
					for (var enviament: notificacio.getEnviamentsPerNotifica()) {
						enviament.setNotificaDataCaducitat(dataCaducitat);
						enviament.setNotificaDataDisposicio(dataDisposicio);
                        //Enviar estat pendent a Comanda
                        comandaListener.enviarTasca(enviament);
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
					var errorNotib = NOTIB.equals(resultadoAlta.getCodigoRespuesta());
					var origenError = !errorNotib ? "Error retornat per Notifica: " : "Error retornat per Notib: ";
					errorSbs = errorNotib;
					errorDescripcio = origenError + " [" + resultadoAlta.getCodigoRespuesta() + "] " + resultadoAlta.getDescripcionRespuesta();
					log.info(" >>> ... ERROR: " + errorDescripcio);
					if (!Strings.isNullOrEmpty(resultadoAlta.getDescripcionRespuesta()) && resultadoAlta.getDescripcionRespuesta().equals("SistemaExternException")) {
						integracioHelper.addAccioError(info, errorDescripcio);
						errorSbs = true;
					}
				}
			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
				error = true;
				errorDescripcio = ex instanceof SOAPFaultException ? ex.getMessage() : ExceptionUtils.getStackTrace(ex);
				integracioHelper.addAccioError(info, "Error al enviar la notificació", ex);
				errorSbs = true;
			}
			var fiReintents = notificacio.getNotificaEnviamentIntent() >= pluginHelper.getNotificaReintentsMaxProperty();
			if (fiReintents && (NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(notificacio.getEstat()) /*|| NotificacioEstatEnumDto.REGISTRADA.equals(notificacio.getEstat())*/)) {
				notificacio.updateEstat(NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS);
			}
			var eventInfo = NotificacioEventHelper.EventInfo.builder().notificacio(notificacio).error(error).errorDescripcio(errorDescripcio).fiReintents(fiReintents).build();
			notificacioEventHelper.addNotificaEnviamentEvent(eventInfo);
			callbackHelper.updateCallbacks(notificacio, error, errorDescripcio);
			log.info(" [NOT] Fi enviament notificació: [Id: " + notificacio.getId() + ", Estat: " + notificacio.getEstat() + "]");
			notificacioTableHelper.actualitzarRegistre(notificacio);
			auditHelper.auditaNotificacio(notificacio, TipusOperacio.UPDATE, "NotificaV2Helper.notificacioEnviar");
            if (errorSbs) {
                SubsistemesHelper.addErrorOperation(NOT);
            } else {
                SubsistemesHelper.addSuccessOperation(NOT, System.currentTimeMillis() - start);
            }
			return notificacio;
		} catch (Exception ex) {
			log.error("Error inesperat enviant la notificacio", ex);
			SubsistemesHelper.addErrorOperation(NOT);
			throw ex;
		}
	}



	@Transactional(timeout = 60, propagation = Propagation.REQUIRES_NEW)
	public NotificacioEnviamentEntity enviamentRefrescarEstat(ConsultaNotificaRequest consulta) throws Exception {

		log.info(String.format(" [NOT] Refrescant estat de notific@ de l'enviament (Id=%d)", consulta.getConsultaNotificaDto().getId()));
		try {
			return enviamentRefrescarEstat(consulta, false);
		} catch (Exception e) {
			throw e;
		}
	}

//	@Transactional(timeout = 60, propagation = Propagation.REQUIRES_NEW)
//	public NotificacioEnviamentEntity enviamentRefrescarEstat(Long enviamentId, boolean raiseExceptions) throws Exception {
//
//		var enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
//		return enviamentRefrescarEstat(enviament, raiseExceptions);
//	}

	@Transactional(timeout = 60, propagation = Propagation.REQUIRES_NEW)
	public NotificacioEnviamentEntity enviamentRefrescarEstat(ConsultaNotificaRequest consulta, boolean raiseExceptions) throws Exception {

		var enviament = notificacioEnviamentRepository.findById(consulta.getConsultaNotificaDto().getId()).orElseThrow();
		var info = new IntegracioInfo(IntegracioCodi.NOTIFICA,"Consultar estat d'un enviament", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Identificador de l'enviament", String.valueOf(enviament.getId())));
		info.setAplicacio(enviament.getNotificacio().getTipusUsuari(), enviament.getNotificacio().getCreatedBy().get().getCodi());
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
				if (consulta.getAccioMassivaId() != null) {
					accioMassivaHelper.actualitzar(consulta.getAccioMassivaId(), enviament.getId(), errorDescripcio, null);
				}
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
			var dataDatat = toDate(darrerDatat.getFecha());
			var estat = getEstatNotifica(darrerDatat.getResultado());
			if (!dataDatat.equals(dataUltimDatat) || !estat.equals(enviament.getNotificaEstat())) {
				actualitzaDatatEnviament(resultadoInfoEnvio, enviament, darrerDatat);
			}
			log.info(" [EST] Enviament actualitzat");
			enviament.refreshNotificaConsulta();
			integracioHelper.addAccioOk(info);
			if (consulta.getAccioMassivaId() != null) {
				accioMassivaHelper.actualitzar(consulta.getAccioMassivaId(), enviament.getId(), null, null);
			}
			log.info(" [EST] Fi actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");

		} catch (Exception ex) {
			error = true;
			errorMaxReintents = enviament.getNotificaIntentNum() >= pluginHelper.getConsultaReintentsMaxProperty();
			errorDescripcio = getErrorDescripcio(enviament.getNotificaIntentNum(), ex);
			log.info(" [EST] Fi actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
			integracioHelper.addAccioError(info, "Error consultat l'estat de l'enviament", ex);
			excepcio = ex;
			if (consulta.getAccioMassivaId() != null) {
				accioMassivaHelper.actualitzar(consulta.getAccioMassivaId(), enviament.getId(), ex.getMessage(), Arrays.toString(ex.getStackTrace()));
			}
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
		return ex instanceof ValidationException ? ex.getMessage() : ExceptionUtils.getStackTrace(ex);
	}

	private RespuestaInfoEnvioLigero getNotificaResultadoInfoEnvio(NotificacioEnviamentEntity enviament, IntegracioInfo info) throws Exception {

		var infoEnvio = new InfoEnvioLigero();
		infoEnvio.setIdentificador(enviament.getNotificaIdentificador());
		infoEnvio.setNivelDetalle(BigInteger.valueOf(3L));
		var apiKey = enviament.getNotificacio().getEntitat().getApiKey();
		var resultadoInfoEnvio = getSincronizarEnvioWs(apiKey).infoEnvioLigero(infoEnvio);
		if (resultadoInfoEnvio.getDatados() == null) {
			var errorDescripcio = "La resposta rebuda de Notifica no conté informació de datat";
			integracioHelper.addAccioError(info, errorDescripcio);
			throw new ValidationException(enviament, NotificacioEnviamentEntity.class, errorDescripcio);
		}
		return resultadoInfoEnvio;
	}

	@Transactional
	public RespuestaSincronizarEnvioOE enviamentEntregaPostalNotificada(SincronizarEnvio sincronizarEnvio) throws Exception {

		try {
			var enviament = notificacioEnviamentRepository.findByCieId(sincronizarEnvio.getIdentificador());
			var apiKey = enviament.getNotificacio().getEntitat().getApiKey();
			var organEmisor = enviament.getNotificacio().getEmisorDir3Codi();
			var id = enviament.getNotificaIdentificador();
			var tipoEntrega = BigInteger.valueOf(1);
			var modoNotificacion = BigInteger.valueOf(1);
			var estat = new Holder<>(CieEstat.NOTIFICADA.name().toLowerCase());
			var data = new Date();
			var gregorianCalendar = new GregorianCalendar();
			gregorianCalendar.setTime(data);
			var xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
			xmlGregorianCalendar.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
			var dataHolder = new Holder<>(xmlGregorianCalendar);
			var receptor = sincronizarEnvio.getReceptor() == null ? getReceptor(enviament) : conversioTipusHelper.convertir(sincronizarEnvio.getReceptor(), Receptor.class);
			var acusePdf = conversioTipusHelper.convertir(sincronizarEnvio.getAcusePDF(), Acuse.class);
			var acuseXml = conversioTipusHelper.convertir(sincronizarEnvio.getAcuseXML(), Acuse.class);
			var opciones = conversioTipusHelper.convertir(sincronizarEnvio.getOpcionesSincronizarEnvio(), es.caib.notib.logic.wsdl.notificaV2.common.Opciones.class);
			Holder<String> codigoRespuesta = new Holder<>();
			Holder<String> descripcionRespuesta = new Holder<>();
			Holder<es.caib.notib.logic.wsdl.notificaV2.common.Opciones> opcionesRespuestaSincronizarOE = new Holder<>();

			getSincronizarEnvioWs(apiKey).sincronizarEnvioOE(organEmisor, id, tipoEntrega, modoNotificacion, estat, dataHolder,
					null, receptor, acusePdf, acuseXml, opciones, codigoRespuesta, descripcionRespuesta, opcionesRespuestaSincronizarOE);

			var error = !NexeaAdviserWs.SYNC_ENVIO_OE_OK.equals(codigoRespuesta.value);
			var errorDesc = error ? codigoRespuesta.value + " - " + descripcionRespuesta.value : "";
			notificacioEventHelper.addNotificaEnvioOE(enviament, error, errorDesc, false);
			var resposta = new RespuestaSincronizarEnvioOE();
			resposta.setCodigoRespuesta(codigoRespuesta.value);
			resposta.setDescripcionRespuesta(descripcionRespuesta.value);
			return resposta;
		} catch (Exception e) {
			var resposta = new RespuestaSincronizarEnvioOE();
			resposta.setCodigoRespuesta("error");
			return resposta;
		}
	}

    private Receptor getReceptor(NotificacioEnviamentEntity enviament) {

        var receptor = new Receptor();
        var titular = enviament.getTitular();
        receptor.setNifReceptor(titular.getNif());
        receptor.setNombreReceptor(titular.getNomSencer());
        receptor.setVinculoReceptor(BigInteger.ONE);
        if (!titular.isIncapacitat()) {
            return receptor;
        }
        receptor.setVinculoReceptor(BigInteger.TWO);
        var destinatari = enviament.getDestinataris().get(0);
        receptor.setNifRepresentante(destinatari.getNif());
        receptor.setNombreRepresentante(destinatari.getNomSencer());
        return receptor;
    }


    @Override
	@Transactional
	public RespuestaAmpliarPlazoOE ampliarPlazoOE(AmpliarPlazoOE ampliarPlazo, List<NotificacioEnviamentEntity> enviaments) {

		var info = new IntegracioInfo(IntegracioCodi.NOTIFICA, "Ampliació de termini", IntegracioAccioTipusEnumDto.ENVIAMENT);
		RespuestaAmpliarPlazoOE resposta;
		try {
			var apiKey = enviaments.get(0).getNotificacio().getEntitat().getApiKey();
			var organEmisor = enviaments.get(0).getNotificacio().getEmisorDir3Codi();
			var ap = conversioTipusHelper.convertir(ampliarPlazo, es.caib.notib.logic.wsdl.notificaV2.ampliarPlazoOE.AmpliarPlazoOE.class);
			ap.setOrganismoEmisor(organEmisor);
			System.setProperty("jaxb.debug", "true");
			var respuesta = getSincronizarEnvioWs(apiKey).ampliarPlazoOE(ap);
			resposta = new RespuestaAmpliarPlazoOE();
			resposta.setCodigoRespuesta(respuesta.getCodigoRespuesta());
			resposta.setDescripcionRespuesta(respuesta.getDescripcionRespuesta());
			resposta.setAmpliacionesPlazo(conversioTipusHelper.convertir(respuesta.getAmpliacionesPlazo(), es.caib.notib.client.domini.ampliarPlazo.AmpliacionesPlazo.class));
		} catch (Exception ex) {
			var msg = "Error inesperat al ampliarPlazosOE ";
			log.error(msg, ex);
			resposta = new RespuestaAmpliarPlazoOE();
			resposta.setCodigoRespuesta("error");
			resposta.setError(true);
			resposta.setDescripcionRespuesta(msg + ex.getMessage());
		}
		var ok = false;
		var errorDesc = "";
		var codiEntitat = "";
		ok = "000".equals(resposta.getCodigoRespuesta());
		errorDesc = !ok ? resposta.getDescripcionRespuesta()  : "";
		for (var enviament : enviaments) {
			info.addParam("Notificacio/Enviament", enviament.getNotificacio().getId() + "/" + enviament.getId());
			codiEntitat +=  enviament.getNotificacio().getEntitat().getCodi();
//			if (!ok) {
//				notificacioEventHelper.addNotificaAmpliarPlazo(enviament, !ok, errorDesc, false);
//				continue;
//			}
			var ampliaciones = resposta.getAmpliacionesPlazo();
			if (ampliaciones == null || ampliaciones.getAmpliacionPlazo() == null || ampliaciones.getAmpliacionPlazo().isEmpty()) {
				errorDesc = "[ampliarPlazoOE] No han arribat dades suficients per guardar la informacio a Notib";
				log.error(errorDesc);
				notificacioEventHelper.addNotificaAmpliarPlazo(enviament, true, errorDesc, false);
				if (ampliarPlazo.getAccioMassiva() != null) {
					accioMassivaHelper.actualitzar(ampliarPlazo.getAccioMassiva(), enviament.getId(), errorDesc, "");
				}

				continue;
			}
			for (var ampliacion : ampliaciones.getAmpliacionPlazo()) {
				if (!enviament.getNotificaIdentificador().equals(ampliacion.getIdentificador())) {
					continue;
				}

				if ("KO".equals(ampliacion.getEstado())) {
					resposta.setError(true);
					resposta.setCodigoRespuesta(ampliacion.getCodigo());
					resposta.setDescripcionRespuesta(ampliacion.getMensajeError());
					errorDesc += ampliacion.getMensajeError();
					ok = false;
					notificacioEventHelper.addNotificaAmpliarPlazo(enviament, true, ampliacion.getMensajeError(), false);
					if (ampliarPlazo.getAccioMassiva() != null) {
						accioMassivaHelper.actualitzar(ampliarPlazo.getAccioMassiva(), enviament.getId(), ampliacion.getMensajeError(), "");
					}
					continue;
				}
				Date dataCaducitat;
				if (Strings.isNullOrEmpty(ampliacion.getFechaCaducidad())) {
					var error = "[ampliarPlazoOE] Data de caducitat buida";
					resposta.setError(true);
					resposta.setCodigoRespuesta("Error");
					resposta.setDescripcionRespuesta(error);
					ok = false;
					notificacioEventHelper.addNotificaAmpliarPlazo(enviament, true, error, false);
					if (ampliarPlazo.getAccioMassiva() != null) {
						accioMassivaHelper.actualitzar(ampliarPlazo.getAccioMassiva(), enviament.getId(), error, "");
					}
					continue;
				}
				try {
					dataCaducitat = new SimpleDateFormat("dd/MM/yyyy").parse(ampliacion.getFechaCaducidad());
					enviament.setNotificaDataCaducitat(dataCaducitat);
				} catch (Exception ex) {
					var error = "[ampliarPlazoOE] Error convertit la data de caducitat " + ampliacion.getFechaCaducidad();
					log.error(error, ex);
					resposta.setError(true);
					resposta.setCodigoRespuesta("Error");
					resposta.setDescripcionRespuesta(error);
					ok = false;
					notificacioEventHelper.addNotificaAmpliarPlazo(enviament, true, error, false);
					if (ampliarPlazo.getAccioMassiva() != null) {
						accioMassivaHelper.actualitzar(ampliarPlazo.getAccioMassiva(), enviament.getId(), error, "");
					}
					continue;
				}
				enviament.setPlazoAmpliado(true);
				var caducitatOriginal = enviament.getNotificacio().getCaducitatOriginal();
				if (caducitatOriginal == null) {
					var caducitat = enviament.getNotificacio().getCaducitat();
					enviament.getNotificacio().setCaducitatOriginal(caducitat);
				}
				enviament.getNotificacio().setCaducitat(dataCaducitat);
				notificacioEnviamentRepository.save(enviament);
				notificacioEventHelper.addNotificaAmpliarPlazo(enviament, false, "", false);
				if (ampliarPlazo.getAccioMassiva() != null) {
					accioMassivaHelper.actualitzar(ampliarPlazo.getAccioMassiva(), enviament.getId(), "", "");
				}
			}
		}
		info.setCodiEntitat(codiEntitat);
		if (ok) {
			integracioHelper.addAccioOk(info);
		} else {
			integracioHelper.addAccioError(info, errorDesc);
		}
		return resposta;
	}

	private Datado getDarrerDatat(RespuestaInfoEnvioLigero resultadoInfoEnvio, NotificacioEnviamentEntity enviament, IntegracioInfo info) throws DatatypeConfigurationException, ParseException {

		info.setCodiEntitat(enviament.getNotificacio().getEntitat().getCodi());
		if (resultadoInfoEnvio.getDatados() == null) {
			var errorDescripcio = "La resposta rebuda de Notifica no conté informació de datat";
			NotibLogger.getInstance().error(errorDescripcio, log, LoggingTipus.ENTREGA_CIE);
			integracioHelper.addAccioError(info, errorDescripcio);
			throw new ValidationException(enviament, NotificacioEnviamentEntity.class, errorDescripcio);
		}
		Datado datatDarrer = null;
		NotibLogger.getInstance().info("Recorrente els datats ", log, LoggingTipus.ENTREGA_CIE);
		for (var datado: resultadoInfoEnvio.getDatados().getDatado()) {
			var datatData = toDate(datado.getFecha());
			if (datatDarrer == null) {
				datatDarrer = datado;
				continue;
			}
			NotibLogger.getInstance().info("Datat " + datado, log, LoggingTipus.ENTREGA_CIE);
			if (datado.getFecha() != null) {
				var datatDarrerData = toDate(datatDarrer.getFecha());
				if (datatData.after(datatDarrerData)) {
					datatDarrer = datado;
				}
			}
		}
		if (datatDarrer == null) {
			var errorDescripcio = "No s'ha pogut trobar el darrer datat dins la resposta rebuda de Notifica";
			NotibLogger.getInstance().error(errorDescripcio, log, LoggingTipus.ENTREGA_CIE);
			integracioHelper.addAccioError(info, errorDescripcio);
			throw new ValidationException(enviament, NotificacioEnviamentEntity.class, errorDescripcio);
		}
		return datatDarrer;
	}

	private void actualitzaCertificacio(RespuestaInfoEnvioLigero resultadoInfoEnvio, NotificacioEnviamentEntity enviament, Datado darrerDatat) throws DatatypeConfigurationException, ParseException {

		var dataUltimaCertificacio = enviament.getNotificaCertificacioData();
		var certificacio = resultadoInfoEnvio.getCertificacion();
		var dataCertificacio = toDate(certificacio.getFechaCertificacion());
		ConfigHelper.setEntitatCodi(enviament.getNotificacio().getEntitat().getCodi());
		if (dataCertificacio.equals(dataUltimaCertificacio) && enviament.getNotificaCertificacioArxiuId() != null) {
			log.info(" [EST] El certificat de l'enviament ja estava actualitzat");
			return;
		}
		var acuse = certificacio.getAcusePDF();
		var decodificat = acuse.getContenido();
		if (enviament.getNotificaCertificacioArxiuId() != null) {
			pluginHelper.gestioDocumentalDelete(enviament.getNotificaCertificacioArxiuId(), PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS);
		}
		var gestioDocumentalId = pluginHelper.gestioDocumentalCreate(PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS, decodificat);
		log.info(" [EST] Actualitzant certificació enviament...");
		enviament.updateNotificaCertificacio(
				dataCertificacio,
				gestioDocumentalId,
				acuse.getHash(),
				certificacio.getOrigen(),
				certificacio.getMetadatos(),
				acuse.getCsv(),
				"application/pdf",
				acuse.getContenido().length,
				null,
				null,
				null);

		log.info(" [EST] Fi actualització certificació. Creant nou event per certificació...");
		//Crea un nou event
		notificacioEventHelper.addAdviserCertificacioEvent(enviament, false, null);
		callbackHelper.updateCallback(enviament, false, null);
	}

	private void actualitzaDatatEnviament(RespuestaInfoEnvioLigero resultadoInfoEnvio, NotificacioEnviamentEntity enviament, Datado darrerDatat) throws Exception {

		var dataDatat = toDate(darrerDatat.getFecha());
		var estat = getEstatNotifica(darrerDatat.getResultado());
		enviament.setNotificaDataCreacio(dataDatat);
//		var organismoEmisor = resultadoInfoEnvio.getCodigoOrganismoEmisor();
//		var organismoEmisorRaiz = resultadoInfoEnvio.getCodigoOrganismoEmisorRaiz();
//		enviament.updateNotificaInformacio(
//				dataDatat,
//				toDate(resultadoInfoEnvio.getFechaPuestaDisposicion()),
//				toDate(resultadoInfoEnvio.getFechaCaducidad()),
//				(organismoEmisor != null) ? organismoEmisor.getCodigo() : null,
//				(organismoEmisor != null) ? organismoEmisor.getDescripcionCodigoDIR() : null,
//				(organismoEmisor != null) ? organismoEmisor.getNifDIR() : null,
//				(organismoEmisorRaiz != null) ? organismoEmisorRaiz.getCodigo() : null,
//				(organismoEmisorRaiz != null) ? organismoEmisorRaiz.getDescripcionCodigoDIR() : null,
//				(organismoEmisorRaiz != null) ? organismoEmisorRaiz.getNifDIR() : null);
		if (estat != null) {
			log.info(" [EST] Nou estat: " + estat.name());
		}
		//Crea un nou event
		log.info(" [EST] Creant nou event per Datat...");
		notificacioEventHelper.addAdviserDatatEvent(enviament, false, null);
		callbackHelper.updateCallback(enviament, false, null);
		log.info(" [EST] L'event s'ha guardat correctament...");
		log.info(" [EST] Actualitzant Datat enviament...");
		enviamentUpdateDatat(estat,
				toDate(darrerDatat.getFecha()),
				null,
				darrerDatat.getOrigen(),
				darrerDatat.getNifReceptor(),
				darrerDatat.getNombreReceptor(),
				null,
				null,
				enviament);
        //Enviar la informacio del canvi d'estat a Comanda
        comandaListener.enviarTasca(enviament);
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
		} catch (Exception ex) {
			var resultat = new ResultadoAltaRemesaEnvios();
			resultat.setCodigoRespuesta(NOTIB);
			resultat.setDescripcionRespuesta("Error generant els enviaments " + ex.getMessage());
			return resultat;
		}
	}

	private AltaRemesaEnvios generarAltaRemesaEnvios(NotificacioEntity notificacio) throws Exception {

		var envios = new AltaRemesaEnvios();
		Integer retardPostal = null;
		ConfigHelper.setEntitatCodi(notificacio.getEntitat().getCodi());
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
				var dataProgramadaWithZeroTime = formatter.parse(formatter.format(notificacio.getEnviamentDataProgramada()));
				if (dataProgramadaWithZeroTime.after(todayWithZeroTime)) {
					envios.setFechaEnvioProgramado(toXmlGregorianCalendar(notificacio.getEnviamentDataProgramada()));
				}
			}
			envios.setConcepto(notificacio.getConcepte().replace('·', '.').replace("'","´"));
			if (!Strings.isNullOrEmpty(notificacio.getDescripcio())) {
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
				if(documento.getContenido() != null) {
					String hash256 = Base64.encodeBase64String(Hex.decodeHex(DigestUtils.sha256Hex(documento.getContenido()).toCharArray()));
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
			var error = "Error generant la petició (notificacioId=" + notificacio.getId() + ")";
			log.error(error, ex);
			throw new Exception(ex);
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
				var destinatari = enviament.getDestinataris().get(0);
				titular.setNif(destinatari.getNif());
				titular.setApellidos(concatenarLlinatges(destinatari.getLlinatge1(), destinatari.getLlinatge2()));
				titular.setTelefono(destinatari.getTelefon());
				titular.setEmail(destinatari.getEmail());
				if (destinatari.getInteressatTipus().equals(InteressatTipus.JURIDICA)
					|| destinatari.getInteressatTipus().equals(InteressatTipus.ADMINISTRACIO)) {
					var raoSocial = !Strings.isNullOrEmpty(destinatari.getRaoSocial()) ? destinatari.getRaoSocial() : destinatari.getNom();
					titular.setRazonSocial(raoSocial);
				} else {
					titular.setNombre(destinatari.getNom());
				}
				titular.setCodigoDestino(destinatari.getDir3Codi());
				titularIncapacitat = true;
			} else {
				var envTitular = enviament.getTitular();
				titular.setNif(InteressatTipus.FISICA_SENSE_NIF.equals(envTitular.getInteressatTipus()) ? null : envTitular.getNif());
				titular.setApellidos(concatenarLlinatges(envTitular.getLlinatge1(), envTitular.getLlinatge2()));
				titular.setTelefono(envTitular.getTelefon());
				titular.setEmail(InteressatTipus.FISICA_SENSE_NIF.equals(envTitular.getInteressatTipus()) ? null : envTitular.getEmail());
				var interessatTipus = enviament.getTitular().getInteressatTipus();
				if (InteressatTipus.JURIDICA.equals(interessatTipus) || InteressatTipus.ADMINISTRACIO.equals(interessatTipus) ) {
					var raoSocial = !Strings.isNullOrEmpty(envTitular.getRaoSocial()) ? envTitular.getRaoSocial() : envTitular.getNom();
					titular.setRazonSocial(raoSocial);
				} else {
					titular.setNombre(envTitular.getNom());
				}
				titular.setCodigoDestino(envTitular.getDir3Codi());
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

			if (enviament.getEntregaPostal() != null
					&&  (notificacio.getProcediment() != null && notificacio.getProcediment().getEntregaCieEfectiva() != null && !notificacio.getProcediment().getEntregaCieEfectiva().getCie().isCieExtern()
						|| notificacio.getOrganGestor().getEntregaCie() != null && !notificacio.getOrganGestor().getEntregaCie().getCie().isCieExtern())) {

				NotibLogger.getInstance().error("[NOTIFICA] Enviament " + enviament.getNotificaReferencia() + " amb entrega postal amb cie Notifica " , log, LoggingTipus.ENTREGA_CIE);
				var entregaPostal = new EntregaPostal();
				var procedimentNotificacio = notificacio.getProcediment();
				if (procedimentNotificacio != null) {
					var entregaCieEntity = procedimentNotificacio.getEntregaCieEfectiva();
					entregaCieEntity = entregaCieEntity == null ? notificacio.getOrganGestor().getEntregaCie() : entregaCieEntity;
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
					var linea1 = !Strings.isNullOrEmpty(entregaPostalEntity.getDomiciliLinea1()) ?
									entregaPostalEntity.getDomiciliLinea1().replace("'", "´") : null;
					var linea2 = !Strings.isNullOrEmpty(entregaPostalEntity.getDomiciliLinea2()) ?
							entregaPostalEntity.getDomiciliLinea2().replace("'", "´") : null;
					entregaPostal.setLinea1(linea1);
					entregaPostal.setLinea2(linea2);
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

		var logMissatge = configHelper.getConfigAsBoolean("es.caib.notib.log.tipus.NOTIFICA_SOAP");
		return new WsClientHelper<NotificaWsV2PortType>().generarClientWs(
				getClass().getResource("/es/caib/notib/logic/wsdl/NotificaWsV21.wsdl"),
				getNotificaUrlProperty(),
				new QName("https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/","NotificaWsV2Service"),
				getUsernameProperty(),
				getPasswordProperty(),
				logMissatge,
				true,
				NotificaWsV2PortType.class,
				new ApiKeySOAPHandlerV2(apiKey));
	}

	private SincronizarEnvioWsPortType getSincronizarEnvioWs(String apiKey) throws InstanceNotFoundException, MalformedObjectNameException, MalformedURLException, RemoteException, NamingException, CreateException {

		return new WsClientHelper<SincronizarEnvioWsPortType>().generarClientWs(
				getClass().getResource("/es/caib/notib/logic/wsdl/SincronizarEnvio.wsdl"),
				getNotificaSincronitzarUrlProperty(),
				new QName("http://administracionelectronica.gob.es/notifica/ws/sincronizarenvio/1.0/","SincronizarEnvioWsService"),
				apiKey,	// Username
				apiKey,	// Password
				null,
				false,
				true,
				SincronizarEnvioWsPortType.class,
				new ApiKeySOAPHandlerV2(apiKey));
//				new Handler[0]);
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
