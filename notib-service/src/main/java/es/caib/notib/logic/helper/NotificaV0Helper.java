
package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.dto.*;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.logic.intf.exception.ValidationException;
import es.caib.notib.logic.intf.service.AuditService;
import es.caib.notib.logic.aspect.Audita;
import es.caib.notib.logic.aspect.UpdateEnviamentTable;
import es.caib.notib.logic.aspect.UpdateNotificacioTable;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.ResultadoAltaRemesaEnvios;
import es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.ResultadoEnvio;
import es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.ResultadoEnvios;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.InputStream;
import java.util.*;

/**
 * Helper MOCK de prova.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class NotificaV0Helper extends AbstractNotificaHelper {

	@Autowired
	private NotificacioRepository notificacioRepository;
	@Autowired
	private NotificacioEventHelper notificacioEventHelper;
	@Autowired
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private IntegracioHelper integracioHelper;

	@UpdateNotificacioTable
	public NotificacioEntity notificacioEnviar(Long notificacioId, boolean ambEnviamentPerEmail) {

		var info = new IntegracioInfo(IntegracioHelper.INTCODI_NOTIFICA,"Enviament d'una notificació", IntegracioAccioTipusEnumDto.ENVIAMENT,
												new AccioParam("Identificador de la notificacio", String.valueOf(notificacioId)));

		var notificacio = notificacioRepository.findById(notificacioId).orElseThrow();
		log.info(" [NOT] Inici enviament notificació [Id: " + notificacio.getId() + ", Estat: " + notificacio.getEstat() + "]");
		if (!NotificacioEstatEnumDto.REGISTRADA.equals(notificacio.getEstat()) && !NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(notificacio.getEstat())) {
			log.error(" [NOT] la notificació no té l'estat REGISTRADA.");
			throw new ValidationException(notificacioId, NotificacioEntity.class, "La notificació no te l'estat " + NotificacioEstatEnumDto.REGISTRADA);
		}
		notificacio.updateNotificaNouEnviament(pluginHelper.getNotificaReintentsPeriodeProperty());
		try {
			log.info(" >>> Enviant notificació...");
			var resultadoAlta = enviaNotificacio(notificacio);
			notificacio.updateNotificaEnviamentData();
			if ("000".equals(resultadoAlta.getCodigoRespuesta()) && "OK".equalsIgnoreCase(resultadoAlta.getDescripcionRespuesta())) {
				log.info(" >>> ... OK");
				if (ambEnviamentPerEmail) {
					auditNotificacioHelper.updateNotificacioMixtaEnviadaNotifica(notificacio);
				} else {
					auditNotificacioHelper.updateNotificacioEnviada(notificacio);
				}
				//Crea un nou event
				Map<NotificacioEnviamentEntity, String> identificadorsEnviaments = new HashMap<>();
				for (var resultadoEnvio: resultadoAlta.getResultadoEnvios().getItem()) {
					for (var enviament: notificacio.getEnviamentsPerNotifica()) {
						if (enviament.getTitular() != null && enviament.getTitular().getNif().equalsIgnoreCase(resultadoEnvio.getNifTitular())) {
							identificadorsEnviaments.put(enviament, resultadoEnvio.getIdentificador());
						}
					}
				}
				notificacioEventHelper.addEnviamentNotificaOKEvent(notificacio, identificadorsEnviaments);
				integracioHelper.addAccioOk(info);
			} else {
				log.info(" >>> ... ERROR:");
				//Crea un nou event
				var errorDescripcio = "[" + resultadoAlta.getCodigoRespuesta() + "] " + resultadoAlta.getDescripcionRespuesta();
				log.info(" >>> " + errorDescripcio);
				updateEventWithEnviament(notificacio, errorDescripcio, NotificacioErrorTipusEnumDto.ERROR_REMOT,true);
				integracioHelper.addAccioError(info, errorDescripcio);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			var errorDescripcio = ex instanceof SOAPFaultException ? ex.getMessage() : ExceptionUtils.getStackTrace(ex);
			updateEventWithEnviament(notificacio, errorDescripcio, NotificacioErrorTipusEnumDto.ERROR_XARXA,true);
			integracioHelper.addAccioError(info, "Error al enviar la notificació", ex);
		}
		var fiReintents = notificacio.getNotificaEnviamentIntent() >= pluginHelper.getNotificaReintentsMaxProperty();
		if (fiReintents && (NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(notificacio.getEstat())/* || NotificacioEstatEnumDto.REGISTRADA.equals(notificacio.getEstat())*/)) {
			auditNotificacioHelper.updateNotificacioFinalitzadaAmbErrors(notificacio);
		}
		log.info(" [NOT] Fi enviament notificació: [Id: " + notificacio.getId() + ", Estat: " + notificacio.getEstat() + "]");
		return notificacio;
	}

	private void updateEventWithEnviament(NotificacioEntity notificacio, String errorDescripcio,
										  NotificacioErrorTipusEnumDto notificacioErrorTipus, boolean notificaError) {

		notificacioEventHelper.addErrorEvent(notificacio, NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT, errorDescripcio, notificacioErrorTipus, notificaError);

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


	@UpdateEnviamentTable
	@Audita(entityType = AuditService.TipusEntitat.ENVIAMENT, operationType = AuditService.TipusOperacio.UPDATE)
	private NotificacioEnviamentEntity enviamentRefrescarEstat(NotificacioEnviamentEntity enviament, boolean raiseExceptions) throws Exception {

		var info = new IntegracioInfo(IntegracioHelper.INTCODI_NOTIFICA, "Consultar estat d'un enviament",
				IntegracioAccioTipusEnumDto.ENVIAMENT, new AccioParam("Identificador de l'enviament", String.valueOf(enviament.getId())));

		log.info(" [EST] Inici actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
		var notificacio = notificacioRepository.findById(enviament.getNotificacio().getId()).orElseThrow();

		try {
			var dataUltimDatat = enviament.getNotificaDataCreacio();
			var dataUltimaCertificacio = enviament.getNotificaCertificacioData();
			enviament.updateNotificaDataRefrescEstat();
			enviament.updateNotificaNovaConsulta(pluginHelper.getConsultaReintentsPeriodeProperty());
			if (enviament.getNotificaIdentificador() == null) {
				log.info(" [EST] Fi actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
				var errorDescripcio = "L'enviament no té identificador de Notifica";
//				integracioHelper.addAccioError(info, errorDescripcio);
				throw new ValidationException(enviament, NotificacioEnviamentEntity.class, errorDescripcio);
			}
			var infoEnvio = new InfoEnvioV2();
			infoEnvio.setIdentificador(enviament.getNotificaIdentificador());
			var apiKey = notificacio.getEntitat().getApiKey();
			var resultadoInfoEnvio = infoEnviament(enviament);
			if (resultadoInfoEnvio.getDatados() == null) {
				String errorDescripcio = "La resposta rebuda de Notifica no conté informació de datat";
//				integracioHelper.addAccioError(info, errorDescripcio);
				throw new ValidationException(enviament, NotificacioEnviamentEntity.class, errorDescripcio);
			}

			Datado datatDarrer = null;
			for (var datado: resultadoInfoEnvio.getDatados().getDatado()) {
				var datatData = toDate(datado.getFecha());
				if (datatDarrer == null) {
					datatDarrer = datado;
				} else if (datado.getFecha() != null) {
					var datatDarrerData = toDate(datatDarrer.getFecha());
					if (datatData.after(datatDarrerData)) {
						datatDarrer = datado;
					}
				}
				var event = new NotificaRespostaDatatDto.NotificaRespostaDatatEventDto();
				event.setData(datatData);
				event.setEstat(datado.getResultado());
			}
			if (datatDarrer == null) {
				String errorDescripcio = "No s'ha pogut trobar el darrer datat dins la resposta rebuda de Notifica";
//				integracioHelper.addAccioError(info, errorDescripcio);
				throw new ValidationException(enviament, NotificacioEnviamentEntity.class, errorDescripcio);
			}

			if (resultadoInfoEnvio.getCertificacion() != null) {
				log.info("Actualitzant informació enviament amb certificació...");
				var certificacio = resultadoInfoEnvio.getCertificacion();
				configHelper.setEntitatCodi(enviament.getNotificacio().getEntitat().getCodi());
				var dataCertificacio = toDate(certificacio.getFechaCertificacion());
				if (!dataCertificacio.equals(dataUltimaCertificacio)) {
					var decodificat = certificacio.getContenidoCertificacion();
					if (enviament.getNotificaCertificacioArxiuId() != null) {
//						pluginHelper.gestioDocumentalDelete(
//								enviament.getNotificaCertificacioArxiuId(),
//								PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS);
					}
					var gestioDocumentalId = pluginHelper.gestioDocumentalCreate(PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS, decodificat);
					log.info("Actualitzant certificació enviament...");
					enviament.updateNotificaCertificacio(dataCertificacio, gestioDocumentalId, certificacio.getHash(), certificacio.getOrigen(), certificacio.getMetadatos(),
															certificacio.getCsv(), certificacio.getMime(), Integer.parseInt(certificacio.getSize()),
															null, null, null);
					log.info("Fi actualització certificació. Creant nou event per certificació...");
					//Crea un nou event
					notificacioEventHelper.addNotificaCallbackEvent(notificacio, enviament, NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO, datatDarrer.getResultado());
				}
				log.info("Enviament actualitzat");
			}
			notificacioEventHelper.addNotificaConsultaInfoEvent(notificacio, enviament, "", false);
			var dataDatat = toDate(resultadoInfoEnvio.getFechaCreacion());
			var estat = getEstatNotifica(datatDarrer.getResultado());
			log.info("Actualitzant informació enviament amb Datat...");
			if (!dataDatat.equals(dataUltimDatat) || !estat.equals(enviament.getNotificaEstat())) {
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
				if (estat.name() != null) {
					log.info("Nou estat: " + estat.name());
				}
				//Crea un nou event
				log.info("Creant nou event per Datat...");
				notificacioEventHelper.addNotificaCallbackEvent(notificacio, enviament, NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT, datatDarrer.getResultado());
				log.info("Actualitzant Datat enviament...");
				enviamentUpdateDatat(estat, toDate(datatDarrer.getFecha()), null, datatDarrer.getOrigen(), datatDarrer.getNifReceptor(),
										datatDarrer.getNombreReceptor(), null, null, enviament);
				log.info("Fi actualització Datat");
			}
			log.info("Enviament actualitzat");

			enviament.refreshNotificaConsulta();
			//integracioHelper.addAccioOk(info);
			log.info(" [EST] Fi actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");

		} catch (Exception ex) {
			var errorPrefix = "Error al consultar l'estat d'un enviament fet amb NotificaV0 (notificacioId="
								+ notificacio.getId() + ", notificaIdentificador=" + enviament.getNotificaIdentificador() + ")";
			log.error(errorPrefix, ex);
			notificacioEventHelper.addNotificaConsultaInfoEvent(notificacio, enviament, ExceptionUtils.getStackTrace(ex), true);
			log.info(" [EST] Fi actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
			if (enviament.getNotificaIntentNum() >= pluginHelper.getConsultaReintentsMaxProperty()) {
				notificacioEventHelper.addNotificaConsultaErrorEvent(notificacio, enviament);
			}
			//integracioHelper.addAccioError(info, "Error consultat l'estat de l'enviament", ex);
			if (raiseExceptions){
				throw ex;
			}
		}
		return enviament;
	}

	
	public ResultadoInfoEnvioV2 infoEnviament(NotificacioEnviamentEntity enviament) throws SistemaExternException {

		var resultat = new ResultadoInfoEnvioV2();
		try {
			var datats = new Datados();
			var datat = new Datado();
			XMLGregorianCalendar date;
			date = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
			datat.setFecha(date);
			datat.setNifReceptor(enviament.getTitular().getNif());
			datat.setNombreReceptor(enviament.getTitular().getNom() + (enviament.getTitular().getLlinatge1() != null ? " " + enviament.getTitular().getLlinatge1() : "")
					+ (enviament.getTitular().getLlinatge2() != null ? " " + enviament.getTitular().getLlinatge2() : ""));
			datat.setOrigen("electronico");
			datat.setResultado("expirada");
			datats.getDatado().add(datat);
			resultat.setDatados(datats);
			Certificacion certificacio = new Certificacion();
			certificacio.setFechaCertificacion(date);
			certificacio.setHash("b081c7abf42d5a8e5a4050958f28046bdf86158c");
			certificacio.setOrigen("electronico");
			certificacio.setCsv("dasd-dsadad-asdasd-asda-sda-das");
			certificacio.setMime("application/pdf");
			byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());
			certificacio.setContenidoCertificacion(arxiuBytes);
			certificacio.setSize(String.valueOf(arxiuBytes.length));
			resultat.setCertificacion(certificacio);
			resultat.setFechaCreacion(date);
			resultat.setFechaPuestaDisposicion(date);
			var cal = date.toGregorianCalendar();
			cal.add(Calendar.DAY_OF_MONTH, 1);
			var dataCaducitat = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
			resultat.setFechaCaducidad(dataCaducitat);
		} catch (Exception e) {}		
		return resultat;
	}

	private InputStream getContingutNotificacioAdjunt() {
		return getClass().getResourceAsStream("/es/caib/notib/logic/certificacio.pdf");
	}

	public ResultadoAltaRemesaEnvios enviaNotificacio(NotificacioEntity notificacio) throws Exception {

		var resultat = new ResultadoAltaRemesaEnvios();
		resultat.setCodigoRespuesta("000");
		resultat.setDescripcionRespuesta("OK");
		if (notificacio.getConcepte().startsWith("throwEx")) {
			throw new Exception("PROVA EXCEPCIO");
		}
		if (notificacio.getConcepte().startsWith("NError")) {
			resultat.setCodigoRespuesta("003");
			resultat.setDescripcionRespuesta("ERROR");
		}
		var resultadoEnvios = new ResultadoEnvios();
		for (var enviament: notificacio.getEnviaments()) {
			ResultadoEnvio resultatEnviament = new ResultadoEnvio();
			resultatEnviament.setNifTitular(enviament.getTitular().getNif());
			resultatEnviament.setIdentificador(getRandomAlphaNumericString(20));
			resultadoEnvios.getItem().add(resultatEnviament);
		}
		resultat.setResultadoEnvios(resultadoEnvios);
		return resultat;
	}
	
	private String getRandomAlphaNumericString(int n) {

		var AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";
		var sb = new StringBuilder(n);
		int index;
		for (var i = 0; i < n; i++) {
			index = (int)(AlphaNumericString.length() * Math.random());
			sb.append(AlphaNumericString.charAt(index)); 
		} 
		return sb.toString(); 
	} 

}
