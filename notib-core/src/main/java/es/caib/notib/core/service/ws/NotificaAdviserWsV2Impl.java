/**
 * 
 */
package es.caib.notib.core.service.ws;

import com.codahale.metrics.Timer;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.core.api.dto.AccioParam;
import es.caib.notib.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.core.api.dto.IntegracioInfo;
import es.caib.notib.core.api.dto.NotificaCertificacioArxiuTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaCertificacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.service.AuditService.TipusEntitat;
import es.caib.notib.core.api.service.AuditService.TipusOperacio;
import es.caib.notib.core.aspect.Audita;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.helper.IntegracioHelper;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.helper.NotificaHelper;
import es.caib.notib.core.helper.NotificacioEventHelper;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.wsdl.adviser.Acuse;
import es.caib.notib.core.wsdl.adviser.AdviserWsV2PortType;
import es.caib.notib.core.wsdl.adviser.Opciones;
import es.caib.notib.core.wsdl.adviser.Receptor;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebService;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Implementació del servei adviser de Notifica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
@WebService(
		name = "adviserWs",
		serviceName = "AdviserWsV2Service",
		portName = "AdviserWsV2PortType",
		endpointInterface = "es.caib.notib.core.wsdl.adviser.AdviserWsV2PortType",
		targetNamespace = "https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/")
public class NotificaAdviserWsV2Impl implements AdviserWsV2PortType {

//	@Autowired
//	private NotificacioRepository notificacioRepository;
	@Autowired
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Autowired
	private NotificacioEventRepository notificacioEventRepository;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private NotificaHelper notificaHelper;
	@Autowired
	private IntegracioHelper integracioHelper;
	@Autowired
	private MetricsHelper metricsHelper;
	@Autowired
	private NotificacioEventHelper notificacioEventHelper;

	private final Object lock = new Object();
	
	@Override
	@Transactional
	public void sincronizarEnvio(
			String organismoEmisor, 
			Holder<String> hIdentificador, 
			BigInteger tipoEntrega,
			BigInteger modoNotificacion, 
			String estado, 
			XMLGregorianCalendar fechaEstado, 
			Receptor receptor,
			Acuse acusePDF, 
			Acuse acuseXML, 
			Opciones opcionesSincronizarEnvio, 
			Holder<String> codigoRespuesta,
			Holder<String> descripcionRespuesta, 
			Holder<Opciones> opcionesResultadoSincronizarEnvio) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			String identificador;
			
			synchronized (lock) {
				identificador = hIdentificador == null ? null : hIdentificador.value;
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
			
			logger.info("[ADV] Inici sincronització enviament Adviser [");
			logger.info("        Id: " + (identificador != null ? identificador : ""));
			logger.info("        OrganismoEmisor: " + organismoEmisor);
			logger.info("        TipoEntrega: " + tipoEntrega);
			logger.info("        ModoNotificacion: " + modoNotificacion);
			logger.info("        Estat: " + estado);
			if (fechaEstado != null) {
				logger.info("        FechaEstado: " + sdf.format(fechaEstado.toGregorianCalendar().getTime()));
			}
			logger.info("        Receptor: " + (receptor != null ? receptor.getNifReceptor() : "") + "]");
			
			logger.debug("--------------------------------------------------------------");
			logger.debug("Processar petició dins l'Adviser...");
			
			IntegracioInfo info = new IntegracioInfo(
					IntegracioHelper.INTCODI_NOTIFICA, 
					"Recepció de canvi de notificació via Adviser", 
					IntegracioAccioTipusEnumDto.RECEPCIO, 
					new AccioParam("Organisme emisor", organismoEmisor),
					new AccioParam("Identificador", (identificador != null ? identificador : "")),
					new AccioParam("Tipus d'entrega", String.valueOf(tipoEntrega)),
					new AccioParam("Mode de notificació", String.valueOf(modoNotificacion)),
					new AccioParam("Estat", estado),
					new AccioParam("Data de l'estat", fechaEstado != null ? sdf.format(fechaEstado.toGregorianCalendar().getTime()) : ""),
					new AccioParam("Receptor", receptor != null ? 
							receptor.getNombreReceptor() + " (" + receptor.getNifReceptor() + ")" + 
							(receptor.getNifRepresentante() != null ? " - Representant: " + 
									receptor.getNombreRepresentante() + " (" + receptor.getNifRepresentante() + ")" : "") : ""),
					new AccioParam("Acús en PDF (Hash)", acusePDF != null ? acusePDF.getHash() : ""),
					new AccioParam("Acús en XML (Hash)", acuseXML != null ? acuseXML.getHash() : ""));
			
			updateEnviament(
					organismoEmisor,
					identificador,
					tipoEntrega,
					modoNotificacion,
					estado,
					fechaEstado,
					receptor,
					acusePDF,
					codigoRespuesta,
					descripcionRespuesta,
					info);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	// TODO: Arreglar això --> No es crida AOP en crides dins la mateixa classe
	@Audita(entityType = TipusEntitat.ENVIAMENT, operationType = TipusOperacio.UPDATE)
	private NotificacioEnviamentEntity updateEnviament(
			String organismoEmisor,
			String identificador,
			BigInteger tipoEntrega,
			BigInteger modoNotificacion,
			String estado,
			XMLGregorianCalendar fechaEstado,
			Receptor receptor,
			Acuse acusePDF,
			Holder<String> codigoRespuesta,
			Holder<String> descripcionRespuesta,
			IntegracioInfo info) {
		NotificacioEnviamentEntity enviament = null;

		boolean createEvent =false;
		boolean eventInitialitzaCallback=false;
		String eventErrorDescripcio = null;
		try {
			enviament = notificacioEnviamentRepository.findByNotificaIdentificador(identificador);
			if (enviament == null) {
				logger.error(
						"Error al processar petició datadoOrganismo dins el callback de Notifica (" +
								"identificadorDestinatario=" + identificador + "): " +
								"No s'ha trobat cap enviament amb l'identificador especificat (" + identificador + ").");
				codigoRespuesta.value = "002";
				descripcionRespuesta.value = "Identificador no encontrado";
				integracioHelper.addAccioError(info, "No s'ha trobat cap enviament amb l'identificador especificat");
				return enviament;
			}

			if (enviament.getNotificacio() != null && enviament.getNotificacio().getEntitat() != null) {
				info.setCodiEntitat(enviament.getNotificacio().getEntitat().getCodi());
			}
			if (enviament.isNotificaEstatFinal()) {
				if (tipoEntrega.equals(BigInteger.valueOf(1L))) { //if datado (1L)
					logger.warn(
							"Error al processar petició datadoOrganismo dins el callback de Notifica (" +
							"L'enviament amb l'identificador especificat (" + identificador + ") ja es troba en un estat final.");
					//Crea un nou event builder
					createEvent = true;
					eventInitialitzaCallback = false;

					if (receptor != null && !isBlank(receptor.getNifReceptor())) {
						enviament.updateReceptorDatat(receptor.getNifReceptor(), receptor.getNombreReceptor());
					}

					codigoRespuesta.value = "000";
					descripcionRespuesta.value = "OK";
					integracioHelper.addAccioError(info, "L'enviament ja es troba en un estat final");
				} else if (tipoEntrega.equals(BigInteger.valueOf(3L))) { //if certificació (3L)
					logger.debug("Guardant certificació de l'enviament [tipoEntrega=" + tipoEntrega + ", id=" + enviament.getId() + "]");
					certificacionOrganismo(
							acusePDF,
							organismoEmisor,
							modoNotificacion,
							identificador,
							codigoRespuesta,
							descripcionRespuesta,
							enviament);
					logger.debug("Certificació guardada correctament.");
				}
			} else {
				String receptorNombre = null;
				String receptorNif = null;
				if (receptor != null) {
					receptorNombre = receptor.getNombreReceptor();
					receptorNif = receptor.getNifReceptor();
				}
				EnviamentEstat notificaEstat = getNotificaEstat(estado);

				//Update enviament
				notificaHelper.enviamentUpdateDatat(
						notificaEstat,
						toDate(fechaEstado),
						estado,
						getModoNotificacion(modoNotificacion),
						receptorNif,
						receptorNombre,
						null,
						null,
						enviament);
				logger.debug("Registrant event callbackdatat de l'Adviser...");
				//Crea un nou event builder
				createEvent = true;
				eventInitialitzaCallback = true;

				codigoRespuesta.value = "000";
				descripcionRespuesta.value = "OK";


				//if (datado + certificació) or (certificació)
				if (tipoEntrega.equals(BigInteger.valueOf(2L)) || tipoEntrega.equals(BigInteger.valueOf(3L))) {
					logger.debug("Guardant certificació de l'enviament [tipoEntrega=" + tipoEntrega + ", id=" + enviament.getId() + "]");
					certificacionOrganismo(
							acusePDF,
							organismoEmisor,
							modoNotificacion,
							identificador,
							codigoRespuesta,
							descripcionRespuesta,
							enviament);
					logger.debug("Certificació guardada correctament.");
				}
				integracioHelper.addAccioOk(info);

//					if ("expirada".equals(estado) && acusePDF == null && enviament.getNotificaCertificacioData() == null) {
//						logger.debug("Consultant la certificació de l'enviament expirat...");
//						notificaHelper.enviamentRefrescarEstat(enviament.getId());
//					}
			}

		} catch (DatatypeConfigurationException ex) {
			codigoRespuesta.value = "004";
			descripcionRespuesta.value = "Fecha incorrecta";
			integracioHelper.addAccioError(info, "La data de l'estat no té un format vàlid");
		} catch (Exception ex) {
			logger.error(
					"Error al processar petició datadoOrganismo dins el callback de Notifica (" +
					"identificadorDestinatario=" + identificador + ")",
					ex);
			if (enviament != null) {
				//Crea un nou event
				logger.debug(
						"Error al processar petició datadoOrganismo dins el callback de Notifica (" +
								"identificadorDestinatario=" + identificador + ")");
				createEvent = true;
				eventInitialitzaCallback = true;
				eventErrorDescripcio = ExceptionUtils.getStackTrace(ex);

			}
			codigoRespuesta.value = "666";
			descripcionRespuesta.value = "Error procesando peticion";
			integracioHelper.addAccioError(info, "Error processant la petició", ex);
		}

		if (enviament != null && !createEvent && !tipoEntrega.equals(BigInteger.valueOf(3L))) {
			logger.debug("L'event de l'enviament identificador " + enviament.getNotificaIdentificador() + " és null");
			createEvent = true;
			eventInitialitzaCallback = true;
			eventErrorDescripcio = "Error retornat cap a Notifica: [" + codigoRespuesta.value + "] " + descripcionRespuesta.value;
		}

		logger.debug("Peticició processada correctament.");

		if (enviament != null && createEvent) { // si no hi ha cap errada enviament mai serà null quan createEvent es true
			notificacioEventHelper.addNotificaCallbackEvent(enviament.getNotificacio(), enviament,
					NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT,
					estado,
					eventErrorDescripcio,
					eventInitialitzaCallback
			);

			logger.debug("Event callbackdatat registrat correctament: " + NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT.name());
		}
		logger.info("[ADV] Fi sincronització enviament Adviser [Id: " + (identificador != null ? identificador : "") + "]");
		return enviament;
	}

	private EnviamentEstat getNotificaEstat(String estado) {
		EnviamentEstat notificaEstat = null;
		if ("pendiente_envio".equals(estado)) {
			notificaEstat = EnviamentEstat.PENDENT_ENVIAMENT;
		} else if ("enviado_ci".equals(estado)) {
			notificaEstat = EnviamentEstat.ENVIADA_CI;
		} else if ("notificada".equals(estado)) {
			notificaEstat = EnviamentEstat.NOTIFICADA;
		} else if ("extraviada".equals(estado)) {
			notificaEstat = EnviamentEstat.EXTRAVIADA;
		} else if ("rehusada".equals(estado)) {
			notificaEstat = EnviamentEstat.REBUTJADA;
		} else if ("desconocido".equals(estado)) {
			notificaEstat = EnviamentEstat.DESCONEGUT;
		} else if ("fallecido".equals(estado)) {
			notificaEstat = EnviamentEstat.MORT;
		} else if ("ausente".equals(estado)) {
			notificaEstat = EnviamentEstat.ABSENT;
		} else if ("direccion_incorrecta".equals(estado)) {
			notificaEstat = EnviamentEstat.ADRESA_INCORRECTA;
		} else if ("sin_informacion".equals(estado)) {
			notificaEstat = EnviamentEstat.SENSE_INFORMACIO;
		} else if ("error".equals(estado)) {
			notificaEstat = EnviamentEstat.ERROR_ENTREGA;
		} else if ("pendiente_sede".equals(estado)) {
			notificaEstat = EnviamentEstat.PENDENT_SEU;
		} else if ("enviado_deh".equals(estado)) {
			notificaEstat = EnviamentEstat.ENVIADA_DEH;
		} else if ("leida".equals(estado)) {
			notificaEstat = EnviamentEstat.LLEGIDA;
		} else if ("envio_programado".equals(estado)) {
			notificaEstat = EnviamentEstat.ENVIAMENT_PROGRAMAT;
		} else if ("pendiente_cie".equals(estado)) {
			notificaEstat = EnviamentEstat.PENDENT_CIE;
		} else if ("pendiente_deh".equals(estado)) {
			notificaEstat = EnviamentEstat.PENDENT_DEH;
		} else if ("entregado_op".equals(estado)) {
			notificaEstat = EnviamentEstat.ENTREGADA_OP;
		} else if ("expirada".equals(estado)) {
			notificaEstat = EnviamentEstat.EXPIRADA;
		} else if ("anulada".equals(estado)) {
			notificaEstat = EnviamentEstat.ANULADA;
		}
		return notificaEstat;
	}

	private void certificacionOrganismo(
			Acuse acusePDF,
			String organismoEmisor,
			BigInteger modoNotificacion,
			String identificador,
			Holder<String> codigoRespuesta,
			Holder<String> descripcionRespuesta,
			NotificacioEnviamentEntity enviament) throws Exception {
		if (enviament == null) {
			throw new Exception("Enviament should not be null");
		}
		NotificacioEventEntity eventCert = null;
		String gestioDocumentalId = null;
		try {
			if (acusePDF != null && acusePDF.getContenido() != null && acusePDF.getContenido().length > 0) {
				//si hi ha una certificació
				if (enviament.getNotificaCertificacioArxiuId() != null) {
					logger.debug("Esborrant certificació antiga...");
					pluginHelper.gestioDocumentalDelete(
							enviament.getNotificaCertificacioArxiuId(),
							PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS);
				}
				logger.debug("Nou estat enviament: " + enviament.getNotificaEstatDescripcio());
				
				if (enviament.getNotificacio() != null)
					logger.debug("Nou estat notificació: " + enviament.getNotificacio().getEstat().name());
				// Hash document certificacio
//				if (acusePDF.getContenido() != null && acusePDF.getContenido().length > 0) {
				try {
					logger.info("Guardant certificació acusament de rebut...");
					gestioDocumentalId = pluginHelper.gestioDocumentalCreate(
							PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS,
							acusePDF.getContenido());
				} catch (Exception ex) {
					logger.error("No s'ha pogut guardar la certificació a la gestió documental", ex);
				}
//				}
				logger.debug("Actualitzant enviament amb la certificació. ID gestió documental: " + gestioDocumentalId);
				enviament.updateNotificaCertificacio(
						new Date(),
						gestioDocumentalId,
						acusePDF.getHash(), // hash
						getModoNotificacion(modoNotificacion), // origen
						null, // metadades
						acusePDF.getCsvResguardo(), // csv
						null, // tipus mime
						null, // tamany
						NotificaCertificacioTipusEnumDto.ACUSE,
						NotificaCertificacioArxiuTipusEnumDto.PDF,
						null); // núm. seguiment
				logger.debug("Registrant event callbackcertificacio de l'Adviser...");
				eventCert = notificacioEventHelper.addNotificaCallbackEvent(enviament.getNotificacio(), enviament,
						NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO,
						null,
						null,
						true
				);
				
				codigoRespuesta.value = "000";
				descripcionRespuesta.value = "OK";
				logger.debug("Event callbackcertificacio registrat correctament: " + eventCert.getTipus().name());
			} else {
				logger.error(
						"Error al processar petició datadoOrganismo dins el callback de Notifica (" +
						"identificadorDestinatario=" + identificador + "): " +
						"No s'ha trobat el camp amb l'acús PDF a dins la petició rebuda.");
				codigoRespuesta.value = "001";
				descripcionRespuesta.value = "Organismo Desconocido";
			}
		} catch (Exception ex) {
			logger.error(
					"Error al processar petició datadoOrganismo dins el callback de Notifica (" +
					"identificadorDestinatario=" + identificador + ")",
					ex);
			logger.debug(
					"Error al processar petició datadoOrganismo dins el callback de Notifica (" +
					"identificadorDestinatario=" + identificador + ")");
			eventCert = notificacioEventHelper.addNotificaCallbackEvent(enviament.getNotificacio(), enviament,
					NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO,
					null,
					ExceptionUtils.getStackTrace(ex),
					true
			);
			codigoRespuesta.value = "666";
			descripcionRespuesta.value = "Error procesando peticion";
		}
		if (eventCert == null) {
			logger.debug("L'event de l'enviament identificador " + enviament.getNotificaIdentificador() + " és null (certificació).");
			notificacioEventHelper.addNotificaCallbackEvent(enviament.getNotificacio(), enviament,
					NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO,
					null,
					"Error retornat cap a Notifica: [" + codigoRespuesta.value + "] " + descripcionRespuesta.value,
					true
			);
		}

		logger.debug("Sortint de la certificació...");
	}

	private String getModoNotificacion(BigInteger modo) {
		String modoNotificacion = null;
		switch (modo.intValue()) {
		case 1:
			modoNotificacion = "sede";
			break;
		case 2:
			modoNotificacion = "funcionario_habilitado";
			break;
		case 3:
			modoNotificacion = "postal";
			break;
		case 4:
			modoNotificacion = "electronico";
			break;
		case 5:
			modoNotificacion = "carpeta";
			break;
		}
		return modoNotificacion;
	}

	private Date toDate(XMLGregorianCalendar calendar) throws DatatypeConfigurationException {
		if (calendar == null) {
			return null;
		}
		return calendar.toGregorianCalendar().getTime();
	}

	private static final Logger logger = LoggerFactory.getLogger(NotificaAdviserWsV2Impl.class);

}
