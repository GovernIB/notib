
package es.caib.notib.logic.helper;

import com.google.common.base.Strings;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.NotificaRespostaDatatDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.logic.intf.exception.ValidationException;
import es.caib.notib.logic.intf.service.AuditService;
import es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.ResultadoAltaRemesaEnvios;
import es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.ResultadoEnvio;
import es.caib.notib.logic.wsdl.notificaV2.altaremesaenvios.ResultadoEnvios;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Certificacion;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Datado;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.Datados;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.InfoEnvioV2;
import es.caib.notib.logic.wsdl.notificaV2.infoEnvioV2.ResultadoInfoEnvioV2;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import liquibase.pro.packaged.Z;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
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
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Helper MOCK de prova.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class NotificaV0Helper extends AbstractNotificaHelper {

	@Autowired
	private CallbackHelper callbackHelper;
	@Autowired
	private NotificacioEventHelper notificacioEventHelper;
	@Autowired
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private IntegracioHelper integracioHelper;
	@Autowired
	private EnviamentTableHelper enviamentTableHelper;
	private MockPlay mockPlay;


	@SneakyThrows
	public NotificacioEntity notificacioEnviar(Long notificacioId, boolean ambEnviamentPerEmail) {

		Thread.sleep(1000);
		var info = new IntegracioInfo(IntegracioHelper.INTCODI_NOTIFICA,"Enviament d'una notificació", IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Identificador de la notificacio", String.valueOf(notificacioId)));

		var notificacio = notificacioRepository.findById(notificacioId).orElseThrow();
		log.info(" [NOT] Inici enviament notificació [Id: " + notificacio.getId() + ", Estat: " + notificacio.getEstat() + "]");
		if (!NotificacioEstatEnumDto.REGISTRADA.equals(notificacio.getEstat()) && !NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(notificacio.getEstat())) {
			log.error(" [NOT] la notificació no té l'estat REGISTRADA.");
			throw new ValidationException(notificacioId, NotificacioEntity.class, "La notificació no te l'estat " + NotificacioEstatEnumDto.REGISTRADA);
		}
		notificacio.updateNotificaNouEnviament(pluginHelper.getNotificaReintentsPeriodeProperty());
		var error = false;
		String errorDescripcio = null;
		try {
			log.info(" >>> Enviant notificació...");
			var resultadoAlta = enviaNotificacio(notificacio);
			notificacio.updateNotificaEnviamentData();
			if ("000".equals(resultadoAlta.getCodigoRespuesta()) && "OK".equalsIgnoreCase(resultadoAlta.getDescripcionRespuesta())) {
				log.info(" >>> ... OK");
				if (!ambEnviamentPerEmail) {
					notificacio.updateEstat(NotificacioEstatEnumDto.ENVIADA);
				}
				//Crea un nou event
				for (var resultadoEnvio : resultadoAlta.getResultadoEnvios().getItem()) {
					for (var enviament : notificacio.getEnviamentsPerNotifica()) {
						var nif = enviament.getTitular().isIncapacitat() ? enviament.getDestinataris().get(0).getNif() : enviament.getTitular().getNif();
						if (enviament.getTitular() != null && nif.equalsIgnoreCase(resultadoEnvio.getNifTitular())) {
							enviament.updateNotificaEnviada(resultadoEnvio.getIdentificador());
							enviamentTableHelper.actualitzarRegistre(enviament);
							auditHelper.auditaEnviament(enviament, AuditService.TipusOperacio.UPDATE, "NotificaV0Helper.notificacioEnviar");
						}
					}
				}
				if (pluginHelper.enviarCarpeta()) {
					for (var e : notificacio.getEnviaments()) {
						pluginHelper.enviarNotificacioMobil(e);
					}
				}
				integracioHelper.addAccioOk(info);
			} else {
				error = true;
				errorDescripcio = "Error retornat per Notifica: [" + resultadoAlta.getCodigoRespuesta() + "] " + resultadoAlta.getDescripcionRespuesta();
				log.info(" >>> ... ERROR: " + errorDescripcio);
				integracioHelper.addAccioError(info, errorDescripcio);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			error = true;
			errorDescripcio = ex instanceof SOAPFaultException ? ex.getMessage() : ExceptionUtils.getStackTrace(ex);
			integracioHelper.addAccioError(info, "Error al enviar la notificació", ex);
		}
		var fiReintents = notificacio.getNotificaEnviamentIntent() >= pluginHelper.getNotificaReintentsMaxProperty();
		if (fiReintents && (NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(notificacio.getEstat())/* || NotificacioEstatEnumDto.REGISTRADA.equals(notificacio.getEstat())*/)) {
			notificacio.updateEstat(NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS);
		}
		notificacioEventHelper.addNotificaEnviamentEvent(notificacio, error, errorDescripcio, fiReintents);
		callbackHelper.updateCallbacks(notificacio, error, errorDescripcio);
		log.info(" [NOT] Fi enviament notificació: [Id: " + notificacio.getId() + ", Estat: " + notificacio.getEstat() + "]");
		notificacioTableHelper.actualitzarRegistre(notificacio);
		auditHelper.auditaNotificacio(notificacio, AuditService.TipusOperacio.UPDATE, "NotificaV0Helper.notificacioEnviar");
		return notificacio;
	}

	@SneakyThrows
	@Transactional(timeout = 60, propagation = Propagation.REQUIRES_NEW)
	public NotificacioEnviamentEntity enviamentRefrescarEstat(Long enviamentId) throws SistemaExternException {

		Thread.sleep(1000);
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

	@SneakyThrows
	@Transactional(timeout = 60, propagation = Propagation.REQUIRES_NEW)
	public NotificacioEnviamentEntity enviamentRefrescarEstat(Long enviamentId, boolean raiseExceptions) throws Exception {
		Thread.sleep(1000);
		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
		return enviamentRefrescarEstat(enviament, raiseExceptions);
	}


	private NotificacioEnviamentEntity enviamentRefrescarEstat(NotificacioEnviamentEntity enviament, boolean raiseExceptions) throws Exception {

		log.info(" [EST] Inici actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
		var notificacio = notificacioRepository.findById(enviament.getNotificacio().getId()).orElseThrow();
		mockPlay = new MockPlay(notificacio.getConcepte());
		var error = false;
		String errorDescripcio = null;
		var errorMaxReintents = false;
		Exception excepcio = null;
		try {
			var dataUltimDatat = enviament.getNotificaDataCreacio();
			var dataUltimaCertificacio = enviament.getNotificaCertificacioData();
			enviament.updateNotificaDataRefrescEstat();
			enviament.updateNotificaNovaConsulta(pluginHelper.getConsultaReintentsPeriodeProperty());
			if (enviament.getNotificaIdentificador() == null || enviament.getNotificaIntentNum() < mockPlay.getIntentsConsulta()) {
				log.info(" [EST] Fi actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
				errorDescripcio = "L'enviament no té identificador de Notifica";
				throw new ValidationException(enviament, NotificacioEnviamentEntity.class, errorDescripcio);
			}
			var infoEnvio = new InfoEnvioV2();
			infoEnvio.setIdentificador(enviament.getNotificaIdentificador());
			var resultadoInfoEnvio = infoEnviament(enviament);
			if (resultadoInfoEnvio.getDatados() == null) {
				errorDescripcio = "La resposta rebuda de Notifica no conté informació de datat";
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
				errorDescripcio = "No s'ha pogut trobar el darrer datat dins la resposta rebuda de Notifica";
				throw new ValidationException(enviament, NotificacioEnviamentEntity.class, errorDescripcio);
			}

			if (resultadoInfoEnvio.getCertificacion() != null) {
				log.info("Actualitzant informació enviament amb certificació...");
				var certificacio = resultadoInfoEnvio.getCertificacion();
				ConfigHelper.setEntitatCodi(enviament.getNotificacio().getEntitat().getCodi());
				var dataCertificacio = toDate(certificacio.getFechaCertificacion());
				if (!dataCertificacio.equals(dataUltimaCertificacio)) {
					var decodificat = certificacio.getContenidoCertificacion();
					String gestioDocumentalId = pluginHelper.gestioDocumentalCreate(PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS, decodificat);
					log.info("Actualitzant certificació enviament...");
					enviament.updateNotificaCertificacio(dataCertificacio, gestioDocumentalId, certificacio.getHash(), certificacio.getOrigen(), certificacio.getMetadatos(),
							certificacio.getCsv(), certificacio.getMime(), Integer.parseInt(certificacio.getSize()), null, null, null);

					log.info("Fi actualització certificació. Creant nou event per certificació...");
					//Crea un nou event
					notificacioEventHelper.addAdviserCertificacioEvent(enviament, false, null);
					callbackHelper.updateCallback(enviament, false, null);
				}
				log.info("Enviament actualitzat");
			}

			var dataDatat = toDate(resultadoInfoEnvio.getFechaCreacion());
			var estat = mockPlay.getEstatConsulta() == null ? getEstatNotifica(datatDarrer.getResultado()) : mockPlay.getEstatConsulta();
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
				notificacioEventHelper.addAdviserDatatEvent(enviament, false, null);
				callbackHelper.updateCallback(enviament, false, null);
				log.info("Actualitzant Datat enviament...");
				enviamentUpdateDatat(estat, toDate(datatDarrer.getFecha()), null, datatDarrer.getOrigen(), datatDarrer.getNifReceptor(),
						datatDarrer.getNombreReceptor(), null, null, enviament);
				log.info("Fi actualització Datat");

			}
			log.info("Enviament actualitzat");
			enviament.refreshNotificaConsulta();
			log.info(" [EST] Fi actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");

		} catch (Exception ex) {
			var errorPrefix = "Error al consultar l'estat d'un enviament fet amb NotificaV0 (notificacioId=" + notificacio.getId() + ", notificaIdentificador=" + enviament.getNotificaIdentificador() + ")";
			log.error(errorPrefix, ex);
			error = true;
			errorMaxReintents = enviament.getNotificaIntentNum() >= pluginHelper.getConsultaReintentsMaxProperty();
			errorDescripcio = getErrorDescripcio(enviament.getNotificaIntentNum(), ex);
			excepcio = ex;
			log.info(" [EST] Fi actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
		}
		notificacioEventHelper.addNotificaConsultaEvent(enviament, error, errorDescripcio, errorMaxReintents);
		callbackHelper.updateCallback(enviament, false, null);
		enviamentTableHelper.actualitzarRegistre(enviament);
		auditHelper.auditaEnviament(enviament, AuditService.TipusOperacio.UPDATE, "NotificaV0Helper.enviamentRefrescarEstat");
		if (error && raiseExceptions){
			throw excepcio;
		}
		return enviament;
	}

	private String getErrorDescripcio(int intent, Exception ex) {

		// Generam el missatge d'error
		return ex instanceof ValidationException ? ex.getMessage() : ExceptionUtils.getStackTrace(ex);
	}

	public ResultadoInfoEnvioV2 infoEnviament(NotificacioEnviamentEntity enviament) throws SistemaExternException {

		var resultat = new ResultadoInfoEnvioV2();
		try {
			var datats = new Datados();
			var datat = new Datado();
			var date = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
			datat.setFecha(date);
			datat.setNifReceptor(enviament.getTitular().getNif());
			datat.setNombreReceptor(enviament.getTitular().getNom()
					+ (enviament.getTitular().getLlinatge1() != null ? " " + enviament.getTitular().getLlinatge1() : "")
					+ (enviament.getTitular().getLlinatge2() != null ? " " + enviament.getTitular().getLlinatge2() : ""));
			datat.setOrigen("electronico");
			datat.setResultado("expirada");
			datats.getDatado().add(datat);
			resultat.setDatados(datats);
			var certificacio = new Certificacion();
			certificacio.setFechaCertificacion(date);
			certificacio.setHash("b081c7abf42d5a8e5a4050958f28046bdf86158c");
			certificacio.setOrigen("electronico");
			certificacio.setCsv("dasd-dsadad-asdasd-asda-sda-das");
			certificacio.setMime("application/pdf");
			var arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());
			certificacio.setContenidoCertificacion(arxiuBytes);
			certificacio.setSize(String.valueOf(arxiuBytes.length));
			resultat.setCertificacion(certificacio);
			resultat.setFechaCreacion(date);
			resultat.setFechaPuestaDisposicion(date);
			var cal = date.toGregorianCalendar();
			cal.add(Calendar.DAY_OF_MONTH, 1);
			XMLGregorianCalendar dataCaducitat = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
			resultat.setFechaCaducidad(dataCaducitat);
		} catch (Exception ex) {
			log.error("Error obtinguent la info de l'enviament", ex);
		}
		return resultat;
	}

	private InputStream getContingutNotificacioAdjunt() {
		return getClass().getResourceAsStream("/es/caib/notib/logic/certificacio.pdf");
	}

	public ResultadoAltaRemesaEnvios enviaNotificacio(NotificacioEntity notificacio) throws Exception {

		mockPlay = new MockPlay(notificacio.getConcepte());
		var resultat = new ResultadoAltaRemesaEnvios();
		resultat.setCodigoRespuesta("000");
		resultat.setDescripcionRespuesta("OK");
		if (notificacio.getConcepte().startsWith("throwEx") || notificacio.getNotificaEnviamentIntent() < mockPlay.getIntentsEnviament()) {
			throw new Exception("PROVA EXCEPCIO");
		}
		if (notificacio.getConcepte().startsWith("NError")) {
			resultat.setCodigoRespuesta("003");
			resultat.setDescripcionRespuesta("ERROR");
		}
		var resultadoEnvios = new ResultadoEnvios();
		ResultadoEnvio resultatEnviament;
		for (var enviament: notificacio.getEnviaments()) {
			resultatEnviament = new ResultadoEnvio();
//			resultatEnviament.setNifTitular(enviament.getTitular().getNif());
			if (enviament.getTitular().isIncapacitat() && enviament.getDestinataris() != null) {
				resultatEnviament.setNifTitular(enviament.getDestinataris().get(0).getNif());
			} else {
				resultatEnviament.setNifTitular(enviament.getTitular().getNif());
			}
			resultatEnviament.setIdentificador(getRandomAlphaNumericString(20));
			resultadoEnvios.getItem().add(resultatEnviament);
		}
		resultat.setResultadoEnvios(resultadoEnvios);
		return resultat;
	}
	
	private String getRandomAlphaNumericString(int n) {

		var alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";
		var sb = new StringBuilder(n);
		int index;
		for (var i = 0; i < n; i++) {
			index = (int)(alphaNumericString.length() * Math.random());
			sb.append(alphaNumericString.charAt(index));
		} 
		return sb.toString(); 
	}

	@Getter
	@Setter
	private class MockPlay {

		private int intentsEnviament;
		private int intentsConsulta;
		private EnviamentEstat estatConsulta;

		// Format per jugar amb el mock ((E0;C0;estatConsulta))
		public MockPlay(String concepte) {

			if (!concepte.startsWith("((") || !concepte.contains("))")) {
				return;
			}
			var str = concepte.substring(2, concepte.indexOf(")"));
			var split = str.split(";");
			if (split.length != 3) {
				return;

			}
			var env = split[0];
			if (!Strings.isNullOrEmpty(env)) {
				try {
					intentsEnviament = Integer.valueOf(env.substring(1));
				} catch (Exception ex) {

				}
			}
			var con = split[1];
			if (!Strings.isNullOrEmpty(con)) {
				try {
					intentsConsulta = Integer.valueOf(con.substring(1));
				} catch (Exception ex) {

				}
			}
			var estat = split[2];
			if (!Strings.isNullOrEmpty(estat)) {
				try {
					estatConsulta = EnviamentEstat.valueOf(estat.toUpperCase());
				} catch (Exception ex) {
					estatConsulta = null;
				}
			}
		}
	}

}
