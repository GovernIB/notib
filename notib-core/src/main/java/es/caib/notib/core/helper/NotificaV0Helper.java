
package es.caib.notib.core.helper;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.core.api.dto.AccioParam;
import es.caib.notib.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.core.api.dto.IntegracioInfo;
import es.caib.notib.core.api.dto.NotificaRespostaDatatDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.core.api.exception.SistemaExternException;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.api.service.AuditService;
import es.caib.notib.core.aspect.Audita;
import es.caib.notib.core.aspect.UpdateEnviamentTable;
import es.caib.notib.core.aspect.UpdateNotificacioTable;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.ResultadoAltaRemesaEnvios;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.ResultadoEnvio;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.ResultadoEnvios;
import es.caib.notib.core.wsdl.notificaV2.infoEnvioV2.Certificacion;
import es.caib.notib.core.wsdl.notificaV2.infoEnvioV2.CodigoDIR;
import es.caib.notib.core.wsdl.notificaV2.infoEnvioV2.Datado;
import es.caib.notib.core.wsdl.notificaV2.infoEnvioV2.Datados;
import es.caib.notib.core.wsdl.notificaV2.infoEnvioV2.InfoEnvioV2;
import es.caib.notib.core.wsdl.notificaV2.infoEnvioV2.ResultadoInfoEnvioV2;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
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
	private NotificacioRepository notificacioRepository;
	@Autowired
	private NotificacioEventHelper notificacioEventHelper;
	@Autowired
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private IntegracioHelper integracioHelper;

	@SneakyThrows
	@UpdateNotificacioTable
	public NotificacioEntity notificacioEnviar(Long notificacioId, boolean ambEnviamentPerEmail) {

		Thread.sleep(1000);
		IntegracioInfo info = new IntegracioInfo(IntegracioHelper.INTCODI_NOTIFICA,"Enviament d'una notificació", IntegracioAccioTipusEnumDto.ENVIAMENT,
												new AccioParam("Identificador de la notificacio", String.valueOf(notificacioId)));

		NotificacioEntity notificacio = notificacioRepository.findById(notificacioId);
		log.info(" [NOT] Inici enviament notificació [Id: " + notificacio.getId() + ", Estat: " + notificacio.getEstat() + "]");
		if (!NotificacioEstatEnumDto.REGISTRADA.equals(notificacio.getEstat()) && !NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(notificacio.getEstat())) {
			log.error(" [NOT] la notificació no té l'estat REGISTRADA.");
			throw new ValidationException(notificacioId, NotificacioEntity.class, "La notificació no te l'estat " + NotificacioEstatEnumDto.REGISTRADA);
		}
		notificacio.updateNotificaNouEnviament(pluginHelper.getNotificaReintentsPeriodeProperty());

		boolean error = false;
		String errorDescripcio = null;
//		NotificacioErrorTipusEnumDto errorTipus = null;
		try {
			log.info(" >>> Enviant notificació...");
			ResultadoAltaRemesaEnvios resultadoAlta = enviaNotificacio(notificacio);
			notificacio.updateNotificaEnviamentData();
			if ("000".equals(resultadoAlta.getCodigoRespuesta()) && "OK".equalsIgnoreCase(resultadoAlta.getDescripcionRespuesta())) {
				log.info(" >>> ... OK");

				if (ambEnviamentPerEmail) {
					auditNotificacioHelper.updateNotificacioMixtaEnviadaNotifica(notificacio);
				} else {
					auditNotificacioHelper.updateNotificacioEnviada(notificacio);
				}
				//Crea un nou event
				for (ResultadoEnvio resultadoEnvio: resultadoAlta.getResultadoEnvios().getItem()) {
					for (NotificacioEnviamentEntity enviament: notificacio.getEnviamentsPerNotifica()) {
						if (enviament.getTitular() != null && enviament.getTitular().getNif().equalsIgnoreCase(resultadoEnvio.getNifTitular())) {
							auditEnviamentHelper.updateEnviamentEnviat(enviament, resultadoEnvio.getIdentificador());
						}
					}
				}
				integracioHelper.addAccioOk(info);
			} else {
				log.info(" >>> ... ERROR:");
				error = true;
				errorDescripcio = "Intent " + notificacio.getNotificaEnviamentIntent() + "\n\nError retornat per Notifica: [" + resultadoAlta.getCodigoRespuesta() + "] " + resultadoAlta.getDescripcionRespuesta();
//				errorTipus = NotificacioErrorTipusEnumDto.ERROR_REMOT;
				log.info(" >>> " + errorDescripcio);
				integracioHelper.addAccioError(info, errorDescripcio);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			error = true;
			errorDescripcio = "Intent " + notificacio.getNotificaEnviamentIntent() + "\n\n" + (ex instanceof SOAPFaultException ? ex.getMessage() : ExceptionUtils.getStackTrace(ex));
//			errorTipus = NotificacioErrorTipusEnumDto.ERROR_XARXA;
			integracioHelper.addAccioError(info, "Error al enviar la notificació", ex);
		}
		boolean fiReintents = notificacio.getNotificaEnviamentIntent() >= pluginHelper.getNotificaReintentsMaxProperty();
		if (fiReintents && (NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(notificacio.getEstat())/* || NotificacioEstatEnumDto.REGISTRADA.equals(notificacio.getEstat())*/)) {
			auditNotificacioHelper.updateNotificacioFinalitzadaAmbErrors(notificacio);
		}

		notificacioEventHelper.addNotificaEnviamentEvent(notificacio, error, errorDescripcio, fiReintents);
		log.info(" [NOT] Fi enviament notificació: [Id: " + notificacio.getId() + ", Estat: " + notificacio.getEstat() + "]");
		return notificacio;
	}

	@SneakyThrows
	@Transactional(timeout = 60, propagation = Propagation.REQUIRES_NEW)
	public NotificacioEnviamentEntity enviamentRefrescarEstat(Long enviamentId) throws SistemaExternException {
		Thread.sleep(1000);
		log.info(String.format(" [NOT] Refrescant estat de notific@ de l'enviament (Id=%d)", enviamentId));
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

	@SneakyThrows
	@Transactional(timeout = 60, propagation = Propagation.REQUIRES_NEW)
	public NotificacioEnviamentEntity enviamentRefrescarEstat(Long enviamentId, boolean raiseExceptions) throws Exception {
		Thread.sleep(1000);
		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findOne(enviamentId);
		return enviamentRefrescarEstat(enviament, raiseExceptions);
	}


	@UpdateEnviamentTable
	@Audita(entityType = AuditService.TipusEntitat.ENVIAMENT, operationType = AuditService.TipusOperacio.UPDATE)
	private NotificacioEnviamentEntity enviamentRefrescarEstat(NotificacioEnviamentEntity enviament, boolean raiseExceptions) throws Exception {

		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_NOTIFICA,
				"Consultar estat d'un enviament",
				IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Identificador de l'enviament", String.valueOf(enviament.getId())));

		log.info(" [EST] Inici actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
		NotificacioEntity notificacio = notificacioRepository.findById(enviament.getNotificacio().getId());

		boolean error = false;
		String errorDescripcio = null;
		boolean errorMaxReintents = false;
		Exception excepcio = null;
		try {
			Date dataUltimDatat = enviament.getNotificaDataCreacio();
			Date dataUltimaCertificacio = enviament.getNotificaCertificacioData();

			enviament.updateNotificaDataRefrescEstat();
			enviament.updateNotificaNovaConsulta(pluginHelper.getConsultaReintentsPeriodeProperty());

			if (enviament.getNotificaIdentificador() == null) {
				log.info(" [EST] Fi actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
				errorDescripcio = "L'enviament no té identificador de Notifica";
//				integracioHelper.addAccioError(info, errorDescripcio);
				throw new ValidationException(enviament, NotificacioEnviamentEntity.class, errorDescripcio);
			}
			InfoEnvioV2 infoEnvio = new InfoEnvioV2();
			infoEnvio.setIdentificador(enviament.getNotificaIdentificador());

			String apiKey = notificacio.getEntitat().getApiKey();
			ResultadoInfoEnvioV2 resultadoInfoEnvio = infoEnviament(enviament);

			if (resultadoInfoEnvio.getDatados() == null) {
				errorDescripcio = "La resposta rebuda de Notifica no conté informació de datat";
//				integracioHelper.addAccioError(info, errorDescripcio);
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
				NotificaRespostaDatatDto.NotificaRespostaDatatEventDto event = new NotificaRespostaDatatDto.NotificaRespostaDatatEventDto();
				event.setData(datatData);
				event.setEstat(datado.getResultado());
			}
			if (datatDarrer == null) {
				errorDescripcio = "No s'ha pogut trobar el darrer datat dins la resposta rebuda de Notifica";
//				integracioHelper.addAccioError(info, errorDescripcio);
				throw new ValidationException(
						enviament,
						NotificacioEnviamentEntity.class,
						errorDescripcio);
			}

			if (resultadoInfoEnvio.getCertificacion() != null) {
				log.info("Actualitzant informació enviament amb certificació...");
				Certificacion certificacio = resultadoInfoEnvio.getCertificacion();
				configHelper.setEntitatCodi(enviament.getNotificacio().getEntitat().getCodi());
				Date dataCertificacio = toDate(certificacio.getFechaCertificacion());
				if (!dataCertificacio.equals(dataUltimaCertificacio)) {
					byte[] decodificat = certificacio.getContenidoCertificacion();
					if (enviament.getNotificaCertificacioArxiuId() != null) {
//						pluginHelper.gestioDocumentalDelete(
//								enviament.getNotificaCertificacioArxiuId(),
//								PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS);
					}
					String gestioDocumentalId = pluginHelper.gestioDocumentalCreate(
							PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS,
							decodificat);
					log.info("Actualitzant certificació enviament...");
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

					log.info("Fi actualització certificació. Creant nou event per certificació...");
					//Crea un nou event
					notificacioEventHelper.addAdviserCertificacioEvent(enviament, false, null);
				}
				log.info("Enviament actualitzat");
			}

			Date dataDatat = toDate(resultadoInfoEnvio.getFechaCreacion());
			EnviamentEstat estat = getEstatNotifica(datatDarrer.getResultado());
			log.info("Actualitzant informació enviament amb Datat...");
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
					log.info("Nou estat: " + estat.name());

				//Crea un nou event
				log.info("Creant nou event per Datat...");
				notificacioEventHelper.addAdviserDatatEvent(enviament, false, null);

				log.info("Actualitzant Datat enviament...");
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
				log.info("Fi actualització Datat");

			}
			log.info("Enviament actualitzat");

			enviament.refreshNotificaConsulta();
			//integracioHelper.addAccioOk(info);
			log.info(" [EST] Fi actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");

		} catch (Exception ex) {
			String errorPrefix = "Error al consultar l'estat d'un enviament fet amb NotificaV0 (" +
					"notificacioId=" + notificacio.getId() + ", " +
					"notificaIdentificador=" + enviament.getNotificaIdentificador() + ")";
			log.error(errorPrefix, ex);
			error = true;
			errorMaxReintents = enviament.getNotificaIntentNum() >= pluginHelper.getConsultaReintentsMaxProperty();
			errorDescripcio = getErrorDescripcio(enviament.getNotificaIntentNum(), ex);
			excepcio = ex;
			log.info(" [EST] Fi actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
		}
		notificacioEventHelper.addNotificaConsultaEvent(enviament, error, errorDescripcio, errorMaxReintents);
		if (error && raiseExceptions){
			throw excepcio;
		}
		return enviament;
	}

	private String getErrorDescripcio(int intent, Exception ex) {
		String errorDescripcio;
		// Generam el missatge d'error
		errorDescripcio = "Intent " + intent + "\n\n";
		if (ex instanceof ValidationException) {
			errorDescripcio += ex.getMessage();
		} else {
			errorDescripcio += ExceptionUtils.getStackTrace(ex);
		}
		return errorDescripcio;
	}
	
	public ResultadoInfoEnvioV2 infoEnviament(
			NotificacioEnviamentEntity enviament) throws SistemaExternException {
		ResultadoInfoEnvioV2 resultat = new ResultadoInfoEnvioV2();
		
		try {
			Datados datats = new Datados();
			Datado datat = new Datado();
			XMLGregorianCalendar date;
				date = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
				datat.setFecha(date);
			datat.setNifReceptor(enviament.getTitular().getNif());
			datat.setNombreReceptor(enviament.getTitular().getNom()
					+ (enviament.getTitular().getLlinatge1() != null ? " " + enviament.getTitular().getLlinatge1() : "")
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
			GregorianCalendar cal = date.toGregorianCalendar();
			cal.add(Calendar.DAY_OF_MONTH, 1);
			XMLGregorianCalendar dataCaducitat = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
			resultat.setFechaCaducidad(dataCaducitat);
		} catch (Exception e) {}		
		return resultat;
	}

	private InputStream getContingutNotificacioAdjunt() {
		return getClass().getResourceAsStream(
				"/es/caib/notib/core/certificacio.pdf");
	}
	


	public ResultadoAltaRemesaEnvios enviaNotificacio(NotificacioEntity notificacio) throws Exception {

		ResultadoAltaRemesaEnvios resultat = new ResultadoAltaRemesaEnvios();
		resultat.setCodigoRespuesta("000");
		resultat.setDescripcionRespuesta("OK");

		if (notificacio.getConcepte().startsWith("throwEx")) {
			throw new Exception("PROVA EXCEPCIO");
		}
		if (notificacio.getConcepte().startsWith("NError")) {
			resultat.setCodigoRespuesta("003");
			resultat.setDescripcionRespuesta("ERROR");
		}
		ResultadoEnvios resultadoEnvios = new ResultadoEnvios();
		for (NotificacioEnviamentEntity enviament: notificacio.getEnviaments()) {
			ResultadoEnvio resultatEnviament = new ResultadoEnvio();
			resultatEnviament.setNifTitular(enviament.getTitular().getNif());
			resultatEnviament.setIdentificador(getRandomAlphaNumericString(20));
			resultadoEnvios.getItem().add(resultatEnviament);
		}
		resultat.setResultadoEnvios(resultadoEnvios);
		return resultat;
	}
	
	private String getRandomAlphaNumericString(int n) { 
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz"; 
		StringBuilder sb = new StringBuilder(n); 
		for (int i = 0; i < n; i++) { 
			int index = (int)(AlphaNumericString.length() * Math.random()); 
			sb.append(AlphaNumericString.charAt(index)); 
		} 
		return sb.toString(); 
	} 

}
