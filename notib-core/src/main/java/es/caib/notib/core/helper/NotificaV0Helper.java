/**
 * 
 */
package es.caib.notib.core.helper;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.NotificaRespostaDatatDto.NotificaRespostaDatatEventDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioErrorTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.api.exception.SistemaExternException;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.ResultadoAltaRemesaEnvios;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.ResultadoEnvio;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.ResultadoEnvios;
import es.caib.notib.core.wsdl.notificaV2.infoEnvioV2.Certificacion;
import es.caib.notib.core.wsdl.notificaV2.infoEnvioV2.CodigoDIR;
import es.caib.notib.core.wsdl.notificaV2.infoEnvioV2.Datado;
import es.caib.notib.core.wsdl.notificaV2.infoEnvioV2.Datados;
import es.caib.notib.core.wsdl.notificaV2.infoEnvioV2.ResultadoInfoEnvioV2;

/**
 * Helper MOCK de prova.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class NotificaV0Helper extends AbstractNotificaHelper {

	@Autowired
	private NotificacioRepository notificacioRepository;
	@Autowired
	private NotificacioEventRepository notificacioEventRepository;
	@Autowired
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Autowired
	private PluginHelper pluginHelper;
	
	public boolean notificacioEnviar(
			Long notificacioId) {
		NotificacioEntity notificacio = notificacioRepository.findById(notificacioId);
		logger.info(" [NOT] Inici enviament notificació [Id: " + notificacio.getId() + ", Estat: " + notificacio.getEstat() + "]");
		if (!NotificacioEstatEnumDto.REGISTRADA.equals(notificacio.getEstat())) {
			logger.error(" [NOT] la notificació no té l'estat REGISTRADA.");
			throw new ValidationException(
					notificacioId,
					NotificacioEntity.class,
					"La notificació no te l'estat " + NotificacioEstatEnumDto.REGISTRADA);
		}
		notificacio.updateNotificaNouEnviament(pluginHelper.getNotificaReintentsPeriodeProperty());
		try {
			logger.info(" >>> Enviant notificació...");
			ResultadoAltaRemesaEnvios resultadoAlta = enviaNotificacio(notificacio);
			if ("000".equals(resultadoAlta.getCodigoRespuesta()) && "OK".equalsIgnoreCase(resultadoAlta.getDescripcionRespuesta())) {
				logger.info(" >>> ... OK");
				//Crea un nou event
				NotificacioEventEntity.Builder eventBulider = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT,
						notificacio);
				if (notificacio.getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB)
					eventBulider.callbackInicialitza();
				
				NotificacioEventEntity event = eventBulider.build();
				
				for (ResultadoEnvio resultadoEnvio: resultadoAlta.getResultadoEnvios().getItem()) {
					for (NotificacioEnviamentEntity enviament: notificacio.getEnviaments()) {
						if (enviament.getTitular().getNif().equalsIgnoreCase(resultadoEnvio.getNifTitular())) {
							enviament.updateNotificaEnviada(
									resultadoEnvio.getIdentificador());
							
							//Registrar event per enviament
							notificacio.updateEstat(NotificacioEstatEnumDto.ENVIADA);
							logger.info(" >>> Canvi estat a ENVIADA ");
							eventBulider.enviament(enviament);
							notificacio.updateEventAfegir(event);
							notificacio.updateNotificaError(
									null,
									null);
							notificacioEventRepository.save(event);
						}
					}
				}
			} else {
				logger.info(" >>> ... ERROR");
				//Crea un nou event
				NotificacioEventEntity.Builder eventBulider = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT,
						notificacio).
						error(true).
						errorDescripcio("[" + resultadoAlta.getCodigoRespuesta() + "] " + resultadoAlta.getDescripcionRespuesta());
				
				if (notificacio.getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB)
					eventBulider.callbackInicialitza();
				NotificacioEventEntity event = eventBulider.build();
				
				notificacio.updateNotificaError(
						NotificacioErrorTipusEnumDto.ERROR_REMOT,
						event);
				notificacio.updateEventAfegir(event);
				notificacioEventRepository.save(event);
				for (NotificacioEnviamentEntity enviament: notificacio.getEnviaments()) {
					enviament.updateNotificaError(true, event);
				}
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
			NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT,
					notificacio).
					error(true).
					errorDescripcio(errorDescripcio).
					build();
			notificacio.updateEventAfegir(event);
			notificacioEventRepository.save(event);
			notificacio.updateNotificaError(
					NotificacioErrorTipusEnumDto.ERROR_XARXA,
					event);
		}
		logger.info(" [NOT] Fi enviament notificació: [Id: " + notificacio.getId() + ", Estat: " + notificacio.getEstat() + "]");
		return NotificacioEstatEnumDto.ENVIADA.equals(notificacio.getEstat());
	}

	public boolean enviamentRefrescarEstat(
			Long enviamentId) throws SistemaExternException {
		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findOne(enviamentId);
		logger.info(" [EST] Inici actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
		NotificacioEntity notificacio = notificacioRepository.findById(enviament.getNotificacioId());
		enviament.setNotificacio(notificacio);
		Date dataUltimDatat = enviament.getNotificaDataCreacio();
		Date dataUltimaCertificacio = enviament.getNotificaCertificacioData();

		NotificacioEventEntity.Builder eventDatatBuilder  = null;
		NotificacioEventEntity.Builder eventCertBuilder  = null;
		
		enviament.updateNotificaDataRefrescEstat();
		
		String errorPrefix = "Error al consultar l'estat d'un enviament fet amb NotificaV2 (" +
				"notificacioId=" + notificacio.getId() + ", " +
				"notificaIdentificador=" + enviament.getNotificaIdentificador() + ")";
		
		try {
			if (enviament.getNotificaIdentificador() != null) {
				ResultadoInfoEnvioV2 resultadoInfoEnvio = infoEnviament(enviament);
				Datado datatDarrer = null;
				if (resultadoInfoEnvio.getDatados() != null) {
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
					if (datatDarrer != null) {
						
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
							
							logger.info("Creant nou event per Datat...");
							//Crea un nou event
							eventDatatBuilder = NotificacioEventEntity.getBuilder(
									NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT,
									enviament.getNotificacio()).
									enviament(enviament).
									descripcio(datatDarrer.getResultado());
							logger.info("Event Datat creat");
							
							if (enviament.getNotificacio().getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB) {
								logger.info("Inicialitzar nou event callback per usuari aplicació...");
								eventDatatBuilder.callbackInicialitza();
								logger.info("Event callback inicialitzat");
							}
							NotificacioEventEntity eventDatat = eventDatatBuilder.build();
							
							logger.info("Estat callback: " + eventDatat.getCallbackEstat());
							logger.info("Afegint event Datat a la notificació...");
							notificacio.updateEventAfegir(eventDatat);
							
							enviament.updateNotificaError(false, null);
							
							logger.info("Guardant event...");
							notificacioEventRepository.save(eventDatat);
							logger.info("L'event s'ha guardat correctament...");
							logger.info("Envio correu en cas d'usuaris no APLICACIÓ");
//							}
						}
						logger.info("Enviament actualitzat");
					} else {
						throw new ValidationException(
								enviament,
								NotificacioEnviamentEntity.class,
								"No s'ha pogut trobar el darrer datat dins la resposta rebuda de Notific@");
					}
				} else {
					throw new ValidationException(
							enviament,
							NotificacioEnviamentEntity.class,
							"La resposta rebuda de Notific@ no conté informació de datat");
				}
				if (resultadoInfoEnvio.getCertificacion() != null) {
					logger.info("Actualitzant informació enviament amb certificació...");
					Certificacion certificacio = resultadoInfoEnvio.getCertificacion();
					
					Date dataCertificacio = toDate(certificacio.getFechaCertificacion());
					if (!dataCertificacio.equals(dataUltimaCertificacio)) {
//						String gestioDocumentalId = "1574780444718";
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
						eventCertBuilder = NotificacioEventEntity.getBuilder(
								NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO,
								enviament.getNotificacio()).
								enviament(enviament).
								descripcio(datatDarrer.getResultado());
						logger.info("Event Datat creat");
						
						if (enviament.getNotificacio().getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB) {
							logger.info("Inicialitzar nou event callback per usuari aplicació...");
							eventCertBuilder.callbackInicialitza();
							logger.info("Event callback inicialitzat");
						}
						
						NotificacioEventEntity eventCert = eventCertBuilder.build();

						logger.info("Estat callback: " + eventCert.getCallbackEstat());
						logger.info("Afegint event certificació a la notificació...");
						
						notificacio.updateEventAfegir(eventCert);
						
						logger.info("Guardant event...");
						notificacioEventRepository.save(eventCert);
						logger.info("L'event s'ha guardat correctament...");
					}
					logger.info("Enviament actualitzat");
				}
				NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_INFO,
						notificacio).
						enviament(enviament).build();
				notificacio.updateEventAfegir(event);
				logger.info(" [EST] Fi actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
				return true;
			} else {
				logger.info(" [EST] Fi actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
				return false;
			}
		} catch (Exception ex) {
			logger.error(
					errorPrefix,
					ex);
			NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_INFO,
					notificacio).
					enviament(enviament).
					error(true).
					errorDescripcio(ExceptionUtils.getStackTrace(ex)).
					build();
			notificacio.updateEventAfegir(event);
			notificacioEventRepository.save(event);
			enviament.updateNotificaError(
					true,
					event);
			logger.info(" [EST] Fi actualitzar estat enviament [Id: " + enviament.getId() + ", Estat: " + enviament.getNotificaEstat() + "]");
			return false;
		}
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
	


	public ResultadoAltaRemesaEnvios enviaNotificacio(
			NotificacioEntity notificacio) throws Exception {
		ResultadoAltaRemesaEnvios resultat = new ResultadoAltaRemesaEnvios();
		resultat.setCodigoRespuesta("000");
		resultat.setDescripcionRespuesta("OK");

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

	private static final Logger logger = LoggerFactory.getLogger(NotificaV0Helper.class);

}
