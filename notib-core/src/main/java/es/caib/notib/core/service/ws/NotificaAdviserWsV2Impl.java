/**
 * 
 */
package es.caib.notib.core.service.ws;

import com.codahale.metrics.Timer;
import com.google.common.base.Strings;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.NotificacioEstatEnum;
import es.caib.notib.core.api.dto.AccioParam;
import es.caib.notib.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.core.api.dto.IntegracioInfo;
import es.caib.notib.core.api.dto.NotificaCertificacioArxiuTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaCertificacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.core.api.service.AuditService;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.helper.CallbackHelper;
import es.caib.notib.core.helper.ConfigHelper;
import es.caib.notib.core.helper.EnviamentHelper;
import es.caib.notib.core.helper.IntegracioHelper;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.helper.NotificaHelper;
import es.caib.notib.core.helper.NotificacioEventHelper;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
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

	@Autowired
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
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
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private CallbackHelper callbackHelper;
	@Autowired
	private EnviamentHelper enviamentHelper;

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
			Date dataEstat = toDate(fechaEstado);

			logger.info("[ADV] Inici sincronització enviament Adviser [");
			logger.info("        Id: " + (identificador != null ? identificador : ""));
			logger.info("        OrganismoEmisor: " + organismoEmisor);
			logger.info("        TipoEntrega: " + tipoEntrega);
			logger.info("        ModoNotificacion: " + modoNotificacion);
			logger.info("        Estat: " + estado);
			if (dataEstat != null) {
				logger.info("        FechaEstado: " + sdf.format(dataEstat));
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
					new AccioParam("Data de l'estat", dataEstat != null ? sdf.format(dataEstat) : ""),
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
					dataEstat,
					receptor,
					acusePDF,
					codigoRespuesta,
					descripcionRespuesta,
					info);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private NotificacioEnviamentEntity updateEnviament(
			String organismoEmisor,
			String identificador,
			BigInteger tipoEntrega,
			BigInteger modoNotificacion,
			String estado,
			Date dataEstat,
			Receptor receptor,
			Acuse acusePDF,
			Holder<String> codigoRespuesta,
			Holder<String> descripcionRespuesta,
			IntegracioInfo info) {
		NotificacioEnviamentEntity enviament = null;

		String eventErrorDescripcio = null;
		try {
			enviament = notificacioEnviamentRepository.findByNotificaIdentificador(identificador);
			if (enviament == null) {
				logger.error(
						"Error al processar petició datadoOrganismo dins el callback de Notifica (identificadorDestinatario=" + identificador + "): " +
								"No s'ha trobat cap enviament amb l'identificador especificat (" + identificador + ").");
				codigoRespuesta.value = "002";
				descripcionRespuesta.value = "Identificador no encontrado";
				integracioHelper.addAccioError(info, "No s'ha trobat cap enviament amb l'identificador especificat");
				return enviament;
			}

			if (Strings.isNullOrEmpty(configHelper.getEntitatActualCodi())) {
				ConfigHelper.setEntitatCodi(enviament.getNotificacio().getEntitat().getCodi());
			}
			if (enviament.getNotificacio() != null && enviament.getNotificacio().getEntitat() != null) {
				info.setCodiEntitat(enviament.getNotificacio().getEntitat().getCodi());
			}

			if (enviament.isNotificaEstatFinal()) {

				String msg = "L'enviament amb identificador " + enviament.getNotificaIdentificador() + " ha rebut un callback de l'adviser de tipus " + tipoEntrega + " quan ja es troba en estat final." ;
				logger.debug(msg);

				// DATAT
				if (tipoEntrega.equals(BigInteger.valueOf(1L))) { //if datado (1L)
					logger.warn("Error al processar petició datadoOrganismo dins el callback de Notifica (L'enviament amb l'identificador especificat (" + identificador + ") ja es troba en un estat final.");
					if (receptor != null && !isBlank(receptor.getNifReceptor())) {
						enviament.updateReceptorDatat(receptor.getNifReceptor(), receptor.getNombreReceptor());
					}

					codigoRespuesta.value = "000";
					descripcionRespuesta.value = "OK";
					integracioHelper.addAccioError(info, "L'enviament ja es troba en un estat final");
					eventErrorDescripcio = msg;

				// CERTIFICACIO
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

				// DATAT + CERTIFICACIO
				} else {
					eventErrorDescripcio = msg;
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
						dataEstat,
						estado,
						getModoNotificacion(modoNotificacion),
						receptorNif,
						receptorNombre,
						null,
						null,
						enviament);
				logger.debug("Registrant event callbackdatat de l'Adviser...");

				codigoRespuesta.value = "000";
				descripcionRespuesta.value = "OK";


				// CERTIFICACIO o DATAT + CERTIFICACIO
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

//		} catch (DatatypeConfigurationException ex) {
//			codigoRespuesta.value = "004";
//			descripcionRespuesta.value = "Fecha incorrecta";
//			integracioHelper.addAccioError(info, "La data de l'estat no té un format vàlid");
		} catch (Exception ex) {
			codigoRespuesta.value = "666";
			descripcionRespuesta.value = "Error procesando peticion";

			eventErrorDescripcio = ExceptionUtils.getStackTrace(ex);
			logger.error("Error al processar petició datadoOrganismo dins el callback de Notifica (identificadorDestinatario=" + identificador + ")", ex);
			integracioHelper.addAccioError(info, "Error processant la petició", ex);
		}

		logger.debug("Peticició processada correctament.");

		NotificacioEstatEnumDto estat = enviament.getNotificacio().getEstat();
		boolean isError = !NotificacioEstatEnumDto.FINALITZADA.equals(estat) && !NotificacioEstatEnumDto.PROCESSADA.equals(estat) && !Strings.isNullOrEmpty(eventErrorDescripcio);
		if (tipoEntrega.equals(BigInteger.valueOf(1L)) || tipoEntrega.equals(BigInteger.valueOf(2L))) {
			notificacioEventHelper.addAdviserDatatEvent(enviament, isError, eventErrorDescripcio);
		}
		callbackHelper.updateCallback(enviament, isError, eventErrorDescripcio);
		enviamentHelper.auditaEnviament(enviament, AuditService.TipusOperacio.UPDATE, "NotificaAdviserWsV2Impl.sincronizarEnvio");
		logger.info("[ADV] Fi sincronització enviament Adviser [Id: " + (identificador != null ? identificador : "") + "]");
		return enviament;
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
		String gestioDocumentalId = null;
		boolean ambAcuse = acusePDF != null && acusePDF.getContenido() != null && acusePDF.getContenido().length > 0;
		boolean isError = false;
		String errorDesc = "";
		try {
			if (ambAcuse) {
				String certificacioAntiga = enviament.getNotificaCertificacioArxiuId();

				logger.debug("Nou estat enviament: " + enviament.getNotificaEstatDescripcio());
				if (enviament.getNotificacio() != null)
					logger.debug("Nou estat notificació: " + enviament.getNotificacio().getEstat().name());
				try {
					logger.info("Guardant certificació acusament de rebut...");
					gestioDocumentalId = pluginHelper.gestioDocumentalCreate(
							PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS,
							acusePDF.getContenido());
				} catch (Exception ex) {
					logger.error("No s'ha pogut guardar la certificació a la gestió documental", ex);
				}
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
				notificacioEventHelper.addAdviserCertificacioEvent(enviament, false, null);

				//si hi havia una certificació antiga
				if (certificacioAntiga != null) {
					logger.debug("Esborrant certificació antiga...");
					pluginHelper.gestioDocumentalDelete(certificacioAntiga, PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS);
				}

				codigoRespuesta.value = "000";
				descripcionRespuesta.value = "OK";
				logger.debug("Event callbackcertificacio registrat correctament: " + NotificacioEventTipusEnumDto.ADVISER_CERTIFICACIO.name());
			} else {
				isError = true;
				errorDesc = "Error al processar petició datadoOrganismo dins el callback de Notifica (identificadorDestinatario=" + identificador + "): " +
							"No s'ha trobat el camp amb l'acús PDF a dins la petició rebuda.";
				logger.error(errorDesc);
				notificacioEventHelper.addAdviserCertificacioEvent(enviament, true, errorDesc);
				codigoRespuesta.value = "001";
				descripcionRespuesta.value = "Organismo Desconocido";
			}
		} catch (Exception ex) {
			isError = true;
			errorDesc = "Error al processar petició datadoOrganismo dins el callback de Notifica (identificadorDestinatario=" + identificador + ")";
			logger.error(errorDesc, ex);
			notificacioEventHelper.addAdviserCertificacioEvent(enviament, true, ExceptionUtils.getStackTrace(ex));
			codigoRespuesta.value = "666";
			descripcionRespuesta.value = "Error procesando peticion";
		}

		callbackHelper.updateCallback(enviament, isError, errorDesc);
		logger.debug("Sortint de la certificació...");
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

	private Date toDate(XMLGregorianCalendar calendar) {
		if (calendar == null) {
			return null;
		}
		return calendar.toGregorianCalendar().getTime();
	}

	private static final Logger logger = LoggerFactory.getLogger(NotificaAdviserWsV2Impl.class);

}
