/**
 * 
 */
package es.caib.notib.core.helper;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.CreateException;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.naming.NamingException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.message.WSSecHeader;
import org.apache.ws.security.message.WSSecSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

import es.caib.notib.core.api.dto.NotificaCertificacioArxiuTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaCertificacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliConcretTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliNumeracioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaRespostaCertificacioDto;
import es.caib.notib.core.api.dto.NotificaRespostaDatatDto;
import es.caib.notib.core.api.dto.NotificaRespostaDatatDto.NotificaRespostaDatatEventDto;
import es.caib.notib.core.api.dto.NotificaRespostaEstatDto;
import es.caib.notib.core.api.dto.NotificaServeiTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioDestinatariDto;
import es.caib.notib.core.api.dto.NotificacioDestinatariEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.exception.SistemaExternException;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.entity.NotificacioDestinatariEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.repository.NotificacioDestinatariRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.wsdl.notifica.ArrayOfTipoDestinatario;
import es.caib.notib.core.wsdl.notifica.CertificacionEnvioRespuesta;
import es.caib.notib.core.wsdl.notifica.DatadoEnvio;
import es.caib.notib.core.wsdl.notifica.DireccionElectronicaHabilitada;
import es.caib.notib.core.wsdl.notifica.Documento;
import es.caib.notib.core.wsdl.notifica.EstadoRespuesta;
import es.caib.notib.core.wsdl.notifica.IdentificadorEnvio;
import es.caib.notib.core.wsdl.notifica.InfoEnvio;
import es.caib.notib.core.wsdl.notifica.NotificaWsPortType;
import es.caib.notib.core.wsdl.notifica.OpcionesEmision;
import es.caib.notib.core.wsdl.notifica.ResultadoAlta;
import es.caib.notib.core.wsdl.notifica.ResultadoCertificacion;
import es.caib.notib.core.wsdl.notifica.ResultadoDatado;
import es.caib.notib.core.wsdl.notifica.ResultadoEstado;
import es.caib.notib.core.wsdl.notifica.ResultadoInfoEnvio;
import es.caib.notib.core.wsdl.notifica.TipoDestinatario;
import es.caib.notib.core.wsdl.notifica.TipoDomicilio;
import es.caib.notib.core.wsdl.notifica.TipoEnvio;
import es.caib.notib.core.wsdl.notifica.TipoIntento;
import es.caib.notib.core.wsdl.notifica.TipoMunicipio;
import es.caib.notib.core.wsdl.notifica.TipoOrganismoEmisor;
import es.caib.notib.core.wsdl.notifica.TipoOrganismoPagadorCIE;
import es.caib.notib.core.wsdl.notifica.TipoOrganismoPagadorCorreos;
import es.caib.notib.core.wsdl.notifica.TipoPais;
import es.caib.notib.core.wsdl.notifica.TipoPersonaDestinatario;
import es.caib.notib.core.wsdl.notifica.TipoProcedimiento;
import es.caib.notib.core.wsdl.notifica.TipoProvincia;
import es.caib.notib.core.wsdl.sede.CertificacionSede;
import es.caib.notib.core.wsdl.sede.ComunicacionSede;
import es.caib.notib.core.wsdl.sede.ResultadoCertificacionSede;
import es.caib.notib.core.wsdl.sede.ResultadoComunicacionSede;
import es.caib.notib.core.wsdl.sede.SedeWsPortType;

/**
 * Helper per a interactuar amb el servei web de Notifica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class NotificaHelper {

	@Autowired
	private NotificacioRepository notificacioRepository;
	@Autowired
	private NotificacioDestinatariRepository notificacioDestinatariRepository;
	@Autowired
	private NotificacioEventRepository notificacioEventRepository;

	@Autowired
	private PluginHelper pluginHelper;

	private boolean modeTest;



	@Transactional
	public void enviament(
			Long notificacioId) {
		NotificacioEntity notificacio = notificacioRepository.findOne(notificacioId);
		if (!NotificacioEstatEnumDto.PENDENT.equals(notificacio.getEstat())) {
			throw new ValidationException(
					notificacioId,
					NotificacioEntity.class,
					"La notificació no te l'estat " + NotificacioEstatEnumDto.PENDENT);
		}
		try {
			TipoEnvio tipoEnvio = generarTipoEnvio(notificacio);
			ResultadoAlta resultadoAlta = getNotificaWs().altaEnvio(tipoEnvio);
			if ("000".equals(resultadoAlta.getCodigoRespuesta())) {
				if (resultadoAlta.getIdentificadores() != null && resultadoAlta.getIdentificadores().getItem() != null) {
					for (IdentificadorEnvio identificadorEnvio: resultadoAlta.getIdentificadores().getItem()) {
						for (NotificacioDestinatariEntity destinatari: notificacio.getDestinataris()) {
							if (destinatari.getReferencia().equals(identificadorEnvio.getReferenciaEmisor())) {
								destinatari.updateNotificaIdentificador(
										identificadorEnvio.getIdentificador());
								destinatari.updateNotificaEstat(
										NotificacioDestinatariEstatEnumDto.NOTIB_ENVIADA,
										null,
										null,
										null,
										null,
										null);
							}
						}
					}
				}
				NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT,
						notificacio).build();
				notificacio.updateEstat(NotificacioEstatEnumDto.ENVIADA);
				notificacio.updateEventAfegir(event);
				notificacioEventRepository.save(event);
			} else {
				NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT,
						notificacio).
						error(true).
						errorDescripcio("Error retornat per notifica: [" + resultadoAlta.getCodigoRespuesta() + "] " + resultadoAlta.getDescripcionRespuesta()).
						build();
				notificacio.updateError(
						true,
						event);
				notificacio.updateEventAfegir(event);
				notificacioEventRepository.save(event);
			}
		} catch (Exception ex) {
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
			notificacio.updateError(
					true,
					event);
			notificacioEventRepository.save(event);
		}
	}

	public NotificacioDto enviamentInfo(
			NotificacioDestinatariEntity destinatari) throws SistemaExternException {
		NotificacioEntity notificacio = destinatari.getNotificacio();
		String errorPrefix = "Error al consultar els detalls d'un enviament fet amb Notifica (" +
				"notificacioId=" + notificacio.getId() + ", " +
				"notificaIdentificador=" + destinatari.getNotificaIdentificador() + ")";
		try {
			InfoEnvio infoEnvio = new InfoEnvio();
			infoEnvio.setEnvioDestinatario(destinatari.getNotificaIdentificador());
			ResultadoInfoEnvio resultadoEnvio = getNotificaWs().infoEnvio(infoEnvio);
			if ("000".equals(resultadoEnvio.getCodigoRespuesta())) {
				NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_INFO,
						notificacio).
						notificacioDestinatari(destinatari).
						build();
				notificacio.updateEventAfegir(event);
				return generarNotificacioDto(resultadoEnvio.getInfoEnvio());
			} else {
				NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_INFO,
						notificacio).
						notificacioDestinatari(destinatari).
						error(true).
						errorDescripcio("Error retornat per notifica: [" + resultadoEnvio.getCodigoRespuesta() + "] " + resultadoEnvio.getDescripcionRespuesta()).
						build();
				notificacio.updateEventAfegir(event);
				throw new SistemaExternException(
						"NOTIFICA",
						errorPrefix + ": [" + resultadoEnvio.getCodigoRespuesta() + "] " + resultadoEnvio.getDescripcionRespuesta());
			}
		} catch (Exception ex) {
			logger.error(
					errorPrefix,
					ex);
			NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_INFO,
					notificacio).
					notificacioDestinatari(destinatari).
					error(true).
					errorDescripcio(ExceptionUtils.getStackTrace(ex)).
					build();
			notificacio.updateEventAfegir(event);
			throw new SistemaExternException(
					"NOTIFICA",
					errorPrefix,
					ex);
		}
	}

	public NotificaRespostaEstatDto enviamentEstat(
			NotificacioDestinatariEntity destinatari) throws SistemaExternException {
		NotificacioEntity notificacio = destinatari.getNotificacio();
		String errorPrefix = "Error al consultar l'estat d'un enviament fet amb Notifica (" +
				"notificacioId=" + notificacio.getId() + ", " +
				"notificaIdentificador=" + destinatari.getNotificaIdentificador() + ")";
		try {
			ResultadoEstado resultadoEstado = getNotificaWs().consultaEstado(
					destinatari.getNotificaIdentificador());
			if ("000".equals(resultadoEstado.getCodigoRespuesta())) {
				EstadoRespuesta estado = resultadoEstado.getEstado();
				NotificaRespostaEstatDto resposta = new NotificaRespostaEstatDto();
				resposta.setRespostaCodi(resultadoEstado.getCodigoRespuesta());
				resposta.setRespostaDescripcio(resultadoEstado.getDescripcionRespuesta());
				resposta.setIdentificador(estado.getIdentificadorEnvio());
				resposta.setEstat(estado.getEstado());
				resposta.setNumSeguiment(estado.getNccIdExterno());
				NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_ESTAT,
						notificacio).
						notificacioDestinatari(destinatari).
						build();
				notificacio.updateEventAfegir(event);
				return resposta;
			} else {
				NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_ESTAT,
						notificacio).
						notificacioDestinatari(destinatari).
						error(true).
						errorDescripcio("Error retornat per notifica: [" + resultadoEstado.getCodigoRespuesta() + "] " + resultadoEstado.getDescripcionRespuesta()).
						build();
				notificacio.updateEventAfegir(event);
				throw new SistemaExternException(
						"NOTIFICA",
						errorPrefix + ": [" + resultadoEstado.getCodigoRespuesta() + "] " + resultadoEstado.getDescripcionRespuesta());
			}
		} catch (Exception ex) {
			logger.error(
					errorPrefix,
					ex);
			NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_ESTAT,
					notificacio).
					notificacioDestinatari(destinatari).
					error(true).
					errorDescripcio(ExceptionUtils.getStackTrace(ex)).
					build();
			notificacio.updateEventAfegir(event);
			throw new SistemaExternException(
					"NOTIFICA",
					errorPrefix,
					ex);
		}
	}

	public NotificaRespostaDatatDto enviamentDatat(
			NotificacioDestinatariEntity destinatari) {
		NotificacioEntity notificacio = destinatari.getNotificacio();
		String errorPrefix = "Error al consultar el datat d'un enviament fet amb Notifica (" +
				"notificacioId=" + notificacio.getId() + ", " +
				"notificaIdentificador=" + destinatari.getNotificaIdentificador() + ")";
		try {
			ResultadoDatado resultadoDatado = getNotificaWs().consultaDatadoEnvio(
					destinatari.getNotificaIdentificador());
			if ("000".equals(resultadoDatado.getCodigoRespuesta())) {
				DatadoEnvio datadoEnvio = resultadoDatado.getDatado();
				comprovarIdentificadorEnviament(
						notificacio,
						destinatari,
						datadoEnvio.getIdentificadorEnvio().getIdentificador(),
						datadoEnvio.getIdentificadorEnvio().getNifTitular(),
						datadoEnvio.getIdentificadorEnvio().getReferenciaEmisor());
				/*destinatari.updateNotificaDatat(
						NotificaEstatEnumDto.toEnum(datadoEnvio.getEstadoActual()),
						datadoEnvio.getNccIdExterno(),
						toDate(datadoEnvio.getFechaActualizacion()));*/
				NotificaRespostaDatatDto resposta = new NotificaRespostaDatatDto();
				resposta.setRespostaCodi(resultadoDatado.getCodigoRespuesta());
				resposta.setRespostaDescripcio(resultadoDatado.getDescripcionRespuesta());
				if (datadoEnvio.getIdentificadorEnvio() != null) {
					resposta.setIdentificador(datadoEnvio.getIdentificadorEnvio().getIdentificador());
					resposta.setReferenciaEmisor(datadoEnvio.getIdentificadorEnvio().getReferenciaEmisor());
					resposta.setTitularNif(datadoEnvio.getIdentificadorEnvio().getNifTitular());
				}
				resposta.setEstatActual(datadoEnvio.getEstadoActual());
				resposta.setEstatActualDescripcio(datadoEnvio.getDescripcionEstadoActual());
				resposta.setDataActualitzacio(
						toDate(datadoEnvio.getFechaActualizacion()));
				resposta.setNumSeguiment(datadoEnvio.getNccIdExterno());
				if (datadoEnvio.getDatado() != null && datadoEnvio.getDatado().getItem() != null) {
					List<NotificaRespostaDatatEventDto> events = new ArrayList<NotificaRespostaDatatEventDto>();
					for (TipoIntento intento: datadoEnvio.getDatado().getItem()) {
						NotificaRespostaDatatEventDto event = new NotificaRespostaDatatEventDto();
						event.setEstat(intento.getEstado());
						event.setDescripcio(intento.getDescripcion());
						event.setData(toDate(intento.getFecha()));
						events.add(event);
					}
					resposta.setEvents(events);
				}
				NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_DATAT,
						notificacio).
						notificacioDestinatari(destinatari).
						build();
				notificacio.updateEventAfegir(event);
				return resposta;
			} else {
				NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_DATAT,
						notificacio).
						notificacioDestinatari(destinatari).
						error(true).
						errorDescripcio("Error retornat per notifica: [" + resultadoDatado.getCodigoRespuesta() + "] " + resultadoDatado.getDescripcionRespuesta()).
						build();
				notificacio.updateEventAfegir(event);
				throw new SistemaExternException(
						"NOTIFICA",
						errorPrefix + ": [" + resultadoDatado.getCodigoRespuesta() + "] " + resultadoDatado.getDescripcionRespuesta());
			}
		} catch (Exception ex) {
			logger.error(
					errorPrefix,
					ex);
			NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_DATAT,
					notificacio).
					notificacioDestinatari(destinatari).
					error(true).
					errorDescripcio(ExceptionUtils.getStackTrace(ex)).
					build();
			notificacio.updateEventAfegir(event);
			throw new SistemaExternException(
					"NOTIFICA",
					errorPrefix,
					ex);
		}
	}

	public NotificaRespostaCertificacioDto enviamentCertificacio(
			NotificacioDestinatariEntity destinatari) {
		NotificacioEntity notificacio = destinatari.getNotificacio();
		String errorPrefix = "Error al consultar la certificació d'un enviament fet amb Notifica (" +
				"notificacioId=" + notificacio.getId() + ", " +
				"notificaIdentificador=" + destinatari.getNotificaIdentificador() + ")";
		try {
			ResultadoCertificacion resultadoCertificacion = getNotificaWs().consultaCertificacionEnvio(
					destinatari.getNotificaIdentificador());
			if ("000".equals(resultadoCertificacion.getCodigoRespuesta())) {
				CertificacionEnvioRespuesta certificacion = resultadoCertificacion.getCertificacion();
				comprovarIdentificadorEnviament(
						notificacio,
						destinatari,
						certificacion.getIdentificadorEnvio().getIdentificador(),
						certificacion.getIdentificadorEnvio().getNifTitular(),
						certificacion.getIdentificadorEnvio().getReferenciaEmisor());
				//String gestioDocumentalId = null;
				NotificaCertificacioArxiuTipusEnumDto arxiuTipus = null;
				byte[] decodificat = null;
				if (certificacion.getPdfCertificado() != null) {
					arxiuTipus = NotificaCertificacioArxiuTipusEnumDto.PDF;
					decodificat = Base64.decode(certificacion.getPdfCertificado().getBytes());
				} else if (certificacion.getXmlCertificado() != null) {
					arxiuTipus = NotificaCertificacioArxiuTipusEnumDto.XML;
					decodificat = Base64.decode(certificacion.getXmlCertificado().getBytes());
				}
				/*gestioDocumentalId = pluginHelper.gestioDocumentalCreate(
						PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS,
						new ByteArrayInputStream(decodificat));
				destinatari.updateNotificaCertificacio(
						NotificaCertificacioTipusEnumDto.toEnum(certificacion.getCertificacion()),
						arxiuTipus,
						gestioDocumentalId,
						certificacion.getNccIdExterno(),
						toDate(certificacion.getFechaActualizacion()));*/
				NotificaRespostaCertificacioDto resposta = new NotificaRespostaCertificacioDto();
				resposta.setRespostaCodi(resultadoCertificacion.getCodigoRespuesta());
				resposta.setRespostaDescripcio(resultadoCertificacion.getDescripcionRespuesta());
				if (certificacion.getIdentificadorEnvio() != null) {
					resposta.setIdentificador(certificacion.getIdentificadorEnvio().getIdentificador());
					resposta.setReferenciaEmisor(certificacion.getIdentificadorEnvio().getReferenciaEmisor());
					resposta.setTitularNif(certificacion.getIdentificadorEnvio().getNifTitular());
				}
				resposta.setCertificacioTipus(
						NotificaCertificacioTipusEnumDto.toEnum(certificacion.getCertificacion()));
				resposta.setCertificatTipus(arxiuTipus);
				resposta.setCertificatContingut(decodificat);
				resposta.setDataActualitzacio(
						toDate(certificacion.getFechaActualizacion()));
				resposta.setNumSeguiment(certificacion.getNccIdExterno());
				NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_CERT,
						notificacio).
						notificacioDestinatari(destinatari).
						build();
				notificacio.updateEventAfegir(event);
				return resposta;
			} else {
				NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_CERT,
						notificacio).
						notificacioDestinatari(destinatari).
						error(true).
						errorDescripcio("Error retornat per notifica: [" + resultadoCertificacion.getCodigoRespuesta() + "] " + resultadoCertificacion.getDescripcionRespuesta()).
						build();
				notificacio.updateEventAfegir(event);
				throw new SistemaExternException(
						"NOTIFICA",
						errorPrefix + ": [" + resultadoCertificacion.getCodigoRespuesta() + "] " + resultadoCertificacion.getDescripcionRespuesta());
			}
		} catch (Exception ex) {
			logger.error(
					errorPrefix,
					ex);
			NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_CERT,
					notificacio).
					notificacioDestinatari(destinatari).
					error(true).
					errorDescripcio(ExceptionUtils.getStackTrace(ex)).
					build();
			notificacio.updateEventAfegir(event);
			throw new SistemaExternException(
					"NOTIFICA",
					errorPrefix,
					ex);
		}
	}

	@Transactional
	public void comunicacioCanviEstatSeu(
			Long notificacioDestinatariId) {
		NotificacioDestinatariEntity destinatari = notificacioDestinatariRepository.findOne(
				notificacioDestinatariId);
		NotificacioEntity notificacio = destinatari.getNotificacio();
		NotificacioEventEntity event;
		try {
			ComunicacionSede comunicacionSede = new ComunicacionSede();
			comunicacionSede.setIdentificadorDestinatario(destinatari.getNotificaIdentificador());
			comunicacionSede.setFecha(
					toXmlGregorianCalendar(destinatari.getSeuDataFi()));
			comunicacionSede.setOrganismoRemisor(notificacio.getEntitat().getDir3Codi());
			ResultadoComunicacionSede resultadoComunicacion = getSedeWs().comunicacionSede(comunicacionSede);
			if ("000".equals(resultadoComunicacion.getCodigoRespuesta())) {
				event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.SEU_NOTIFICA_COMUNICACIO,
						notificacio).
						notificacioDestinatari(destinatari).
						build();
			} else {
				event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.SEU_NOTIFICA_COMUNICACIO,
						notificacio).
						notificacioDestinatari(destinatari).
						error(true).
						errorDescripcio("Error retornat per notifica: [" + resultadoComunicacion.getCodigoRespuesta() + "] " + resultadoComunicacion.getDescripcionRespuesta()).
						build();
				destinatari.updateSeuError(
						true,
						event,
						true);
			}
		} catch (Exception ex) {
			logger.error(
					"Error al comunicar el canvi d'estat d'una notificació de la seu a Notifica (" +
					"notificacioId=" + notificacio.getId() + ", " +
					"notificaIdentificador=" + destinatari.getNotificaIdentificador() + ")",
					ex);
			event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.SEU_NOTIFICA_COMUNICACIO,
					notificacio).
					notificacioDestinatari(destinatari).
					error(true).
					errorDescripcio(ExceptionUtils.getStackTrace(ex)).
					build();
			destinatari.updateSeuError(
					true,
					event,
					true);
		}
		destinatari.updateSeuNotificaInformat();
		notificacio.updateEventAfegir(event);
	}

	@Transactional
	public void certificacioSeu(
			NotificacioDestinatariEntity destinatari) {
		NotificacioEntity notificacio = destinatari.getNotificacio();
		NotificacioEventEntity event;
		try {
			CertificacionSede certificacionSede = new CertificacionSede();
			certificacionSede.setEnvioDestinatario(destinatari.getNotificaIdentificador());
			if (NotificacioDestinatariEstatEnumDto.LLEGIDA.equals(destinatari.getSeuEstat())) {
				certificacionSede.setEstado("notificada");
			} else if (NotificacioDestinatariEstatEnumDto.REBUTJADA.equals(destinatari.getSeuEstat())) {
				certificacionSede.setEstado("rehusada");
			}
			certificacionSede.setFecha(
					toXmlGregorianCalendar(destinatari.getSeuDataFi()));
			// TODO generar certificacio
			//certificacionSede.setDocumento(value);
			//certificacionSede.setHashDocumento(value);
			//certificacionSede.setCsv(value);
			certificacionSede.setOrganismoRemisor(notificacio.getEntitat().getDir3Codi());
			ResultadoCertificacionSede resultadoCertificacion = getSedeWs().certificacionSede(certificacionSede);
			if ("000".equals(resultadoCertificacion.getCodigoRespuesta())) {
				event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.SEU_NOTIFICA_CERTIFICACIO,
						notificacio).
						notificacioDestinatari(destinatari).
						build();
			} else {
				event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.SEU_NOTIFICA_CERTIFICACIO,
						notificacio).
						notificacioDestinatari(destinatari).
						error(true).
						errorDescripcio("Error retornat per notifica: [" + resultadoCertificacion.getCodigoRespuesta() + "] " + resultadoCertificacion.getDescripcionRespuesta()).
						build();
				destinatari.updateSeuError(
						true,
						event,
						true);
			}
		} catch (Exception ex) {
			logger.error(
					"Error al enviar la certificació d'una notificació de la seu a Notifica (" +
					"notificacioId=" + notificacio.getId() + ", " +
					"notificaIdentificador=" + destinatari.getNotificaIdentificador() + ")",
					ex);
			event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.SEU_NOTIFICA_CERTIFICACIO,
					notificacio).
					notificacioDestinatari(destinatari).
					error(true).
					errorDescripcio(ExceptionUtils.getStackTrace(ex)).
					build();
			destinatari.updateSeuError(
					true,
					event,
					true);
		}
		destinatari.updateSeuNotificaInformat();
		notificacio.updateEventAfegir(event);
	}

	public String generarReferencia(NotificacioDestinatariEntity notificacioDestinatari) throws GeneralSecurityException {
		return xifrarIdPerNotifica(notificacioDestinatari.getId());
	}

	public boolean isConnexioNotificaDisponible() {
		return getUrlProperty() != null && getApiKeyProperty() != null;
	}

	public void setModeTest(boolean modeTest) {
		this.modeTest = modeTest;
	}



	private TipoEnvio generarTipoEnvio(
			NotificacioEntity notificacio) throws GeneralSecurityException, DatatypeConfigurationException {
		TipoEnvio envio = new TipoEnvio();
		TipoOrganismoEmisor organismoEmisor = new TipoOrganismoEmisor();
		organismoEmisor.setCodigoDir3(notificacio.getEntitat().getDir3Codi());
		organismoEmisor.setNombre(notificacio.getEntitat().getNom());
		envio.setOrganismoEmisor(organismoEmisor);
		envio.setConcepto(notificacio.getConcepte());
		if (notificacio.getEnviamentDataProgramada() != null) {
			envio.setFechaEnvioProgramado(
					toXmlGregorianCalendar(notificacio.getEnviamentDataProgramada()));
		}
		if (notificacio.getEnviamentTipus() != null) {
			envio.setTipoEnvio(notificacio.getEnviamentTipus().getText());
		}
		if (notificacio.getPagadorCorreusCodiDir3() != null) {
			TipoOrganismoPagadorCorreos pagadorCorreos = new TipoOrganismoPagadorCorreos();
			pagadorCorreos.setCodigoDir3(notificacio.getPagadorCorreusCodiDir3());
			pagadorCorreos.setCodigoClienteFacturacionCorreos(notificacio.getPagadorCorreusCodiClientFacturacio());
			pagadorCorreos.setNumeroContratoCorreos(notificacio.getPagadorCorreusContracteNum());
			pagadorCorreos.setFechaVigencia(
					toXmlGregorianCalendar(notificacio.getPagadorCieDataVigencia()));
			envio.setOrganismoPagadorCorreos(pagadorCorreos);
		}
		if (notificacio.getPagadorCieCodiDir3() != null) {
			TipoOrganismoPagadorCIE pagadorCie = new TipoOrganismoPagadorCIE();
			pagadorCie.setCodigoDir3(notificacio.getPagadorCieCodiDir3());
			pagadorCie.setFechaVigencia(
					toXmlGregorianCalendar(notificacio.getPagadorCieDataVigencia()));
			envio.setOrganismoPagadorCie(pagadorCie);
		}
		Documento documento = new Documento();
		documento.setHashSha1(notificacio.getDocumentSha1());
		documento.setNormalizado(notificacio.isDocumentNormalitzat() ? "si" : "no");
		documento.setGenerarCsv(notificacio.isDocumentGenerarCsv() ? "si" : "no");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		pluginHelper.gestioDocumentalGet(
				notificacio.getDocumentArxiuId(),
				PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
				baos);
		documento.setContenido(new String(Base64.encode(baos.toByteArray())));
		envio.setDocumento(documento);
		TipoProcedimiento procedimiento = null;
		if (notificacio.getProcedimentCodiSia() != null) {
			if (procedimiento == null)
				procedimiento = new TipoProcedimiento();
			procedimiento.setCodigoSia(notificacio.getProcedimentCodiSia());
		}
		if (notificacio.getProcedimentDescripcioSia() != null) {
			if (procedimiento == null)
				procedimiento = new TipoProcedimiento();
			procedimiento.setDescripcionSia(notificacio.getProcedimentDescripcioSia());
		}
		envio.setProcedimiento(procedimiento);
		envio.setDestinatarios(generarDestinatarios(notificacio));
		return envio;
	}

	private ArrayOfTipoDestinatario generarDestinatarios(
			NotificacioEntity notificacio) throws GeneralSecurityException {
		SimpleDateFormat sdfCaducitat = new SimpleDateFormat("yyyy-MM-dd");
		ArrayOfTipoDestinatario destinatarios = new ArrayOfTipoDestinatario();
		for (NotificacioDestinatariEntity destinatari: notificacio.getDestinataris()) {
			TipoDestinatario destinatario = new TipoDestinatario();
			if (destinatari.getReferencia() == null) {
				destinatari.updateReferencia(
						xifrarIdPerNotifica(destinatari.getId()));
			}
			destinatario.setReferenciaEmisor(destinatari.getReferencia());
			TipoPersonaDestinatario personaTitular = new TipoPersonaDestinatario();
			personaTitular.setNif(destinatari.getTitularNif());
			personaTitular.setNombre(destinatari.getTitularNom());
			personaTitular.setApellidos(
					concatenarLlinatges(
							destinatari.getTitularLlinatge1(),
							destinatari.getTitularLlinatge2()));
			personaTitular.setTelefono(destinatari.getTitularTelefon());
			personaTitular.setEmail(destinatari.getTitularEmail());
			destinatario.setTitular(personaTitular);
			TipoPersonaDestinatario personaDestinatario = new TipoPersonaDestinatario();
			personaDestinatario.setNif(destinatari.getDestinatariNif());
			personaDestinatario.setNombre(destinatari.getDestinatariNom());
			personaDestinatario.setApellidos(
					concatenarLlinatges(
							destinatari.getDestinatariLlinatge1(),
							destinatari.getDestinatariLlinatge2()));
			personaDestinatario.setTelefono(destinatari.getDestinatariTelefon());
			personaDestinatario.setEmail(destinatari.getDestinatariEmail());
			destinatario.setDestinatario(personaDestinatario);
			destinatario.setServicio(destinatari.getServeiTipus().getText());
			if (destinatari.getDomiciliTipus() != null) {
				destinatario.setTipoDomicilio(destinatari.getDomiciliTipus().getText());
			}
			if (destinatari.getDomiciliTipus() != null) {
				TipoDomicilio domicilio = new TipoDomicilio();
				if (destinatari.getDomiciliConcretTipus() != null) {
					domicilio.setTipoDomicilioConcreto(destinatari.getDomiciliConcretTipus().getText());
				}
				domicilio.setTipoVia(destinatari.getDomiciliViaTipus());
				domicilio.setNombreVia(destinatari.getDomiciliViaNom());
				if (destinatari.getDomiciliNumeracioTipus() != null) {
					domicilio.setCalificadorNumero(destinatari.getDomiciliNumeracioTipus().getText());
				}
				domicilio.setNumeroCasa(destinatari.getDomiciliNumeracioNumero());
				domicilio.setPuntoKilometrico(destinatari.getDomiciliNumeracioPuntKm());
				domicilio.setApartadoCorreos(destinatari.getDomiciliApartatCorreus());
				domicilio.setBloque(destinatari.getDomiciliBloc());
				domicilio.setPortal(destinatari.getDomiciliPortal());
				domicilio.setEscalera(destinatari.getDomiciliEscala());
				domicilio.setPlanta(destinatari.getDomiciliPlanta());
				domicilio.setPuerta(destinatari.getDomiciliPorta());
				domicilio.setComplemento(destinatari.getDomiciliComplement());
				domicilio.setPoblacion(destinatari.getDomiciliPoblacio());
				if (destinatari.getDomiciliMunicipiCodiIne() != null || destinatari.getDomiciliMunicipiNom() != null) {
					TipoMunicipio municipio = new TipoMunicipio();
					municipio.setCodigoIne(destinatari.getDomiciliMunicipiCodiIne());
					municipio.setNombre(destinatari.getDomiciliMunicipiNom());
					domicilio.setMunicipio(municipio);
				}
				domicilio.setCodigoPostal(destinatari.getDomiciliCodiPostal());
				if (destinatari.getDomiciliProvinciaCodi() != null || destinatari.getDomiciliProvinciaNom() != null) {
					TipoProvincia provincia = new TipoProvincia();
					provincia.setCodigoProvincia(destinatari.getDomiciliProvinciaCodi());
					provincia.setNombre(destinatari.getDomiciliProvinciaNom());
					domicilio.setProvincia(provincia);
				}
				if (destinatari.getDomiciliPaisCodiIso() != null || destinatari.getDomiciliPaisNom() != null) {
					TipoPais pais = new TipoPais();
					pais.setCodigoIso3166(destinatari.getDomiciliPaisCodiIso());
					pais.setNombre(destinatari.getDomiciliPaisNom());
					domicilio.setPais(pais);
				}
				domicilio.setLinea1(destinatari.getDomiciliLinea1());
				domicilio.setLinea2(destinatari.getDomiciliLinea2());
				domicilio.setCie(destinatari.getDomiciliCie());
				destinatario.setDomicilio(domicilio);
			}
			OpcionesEmision opcionesEmision = new OpcionesEmision();
			if (destinatari.getCaducitat() != null) {
				opcionesEmision.setCaducidad(
						sdfCaducitat.format(destinatari.getCaducitat()));
			}
			opcionesEmision.setRetardoPostalDeh(
					new Integer(destinatari.getRetardPostal()));
			destinatario.setOpcionesEmision(opcionesEmision);
			DireccionElectronicaHabilitada deh = new DireccionElectronicaHabilitada();
			deh.setCodigoProcedimiento(destinatari.getDehProcedimentCodi());
			deh.setNif(destinatari.getDehNif());
			deh.setObligado(destinatari.isDehObligat());
			destinatario.setDireccionElectronica(deh);
			destinatarios.getItem().add(destinatario);
		}
		return destinatarios;
	}

	private NotificacioDto generarNotificacioDto(
			TipoEnvio envio) throws ParseException, DatatypeConfigurationException {
		NotificacioDto notificacio = new NotificacioDto();
		notificacio.setEnviamentTipus(
				NotificaEnviamentTipusEnumDto.toEnum(envio.getTipoEnvio()));
		notificacio.setEnviamentDataProgramada(
				toDate(envio.getFechaEnvioProgramado()));
		notificacio.setConcepte(envio.getConcepto());
		if (envio.getOrganismoPagadorCorreos() != null) {
			notificacio.setPagadorCorreusCodiDir3(envio.getOrganismoPagadorCorreos().getCodigoDir3());
			notificacio.setPagadorCorreusContracteNum(envio.getOrganismoPagadorCorreos().getNumeroContratoCorreos());
			notificacio.setPagadorCorreusCodiClientFacturacio(envio.getOrganismoPagadorCorreos().getCodigoClienteFacturacionCorreos());
		}
		if (envio.getOrganismoPagadorCie() != null) {
			notificacio.setPagadorCieCodiDir3(envio.getOrganismoPagadorCie().getCodigoDir3());
			notificacio.setPagadorCieDataVigencia(
					toDate(envio.getOrganismoPagadorCie().getFechaVigencia()));
		}
		if (envio.getProcedimiento() != null) {
			notificacio.setProcedimentCodiSia(envio.getProcedimiento().getCodigoSia());
			notificacio.setProcedimentDescripcioSia(envio.getProcedimiento().getDescripcionSia());
		}
		if (envio.getDocumento() != null) {
			notificacio.setDocumentContingutBase64(envio.getDocumento().getContenido());
			notificacio.setDocumentSha1(envio.getDocumento().getHashSha1());
			notificacio.setDocumentNormalitzat(
					"si".equalsIgnoreCase(envio.getDocumento().getNormalizado()));
			notificacio.setDocumentGenerarCsv(
					"si".equalsIgnoreCase(envio.getDocumento().getGenerarCsv()));
		}
		if (envio.getDestinatarios() != null && envio.getDestinatarios().getItem() != null) {
			List<NotificacioDestinatariDto> destinataris = new ArrayList<NotificacioDestinatariDto>();
			for (TipoDestinatario destinatario: envio.getDestinatarios().getItem()) {
				destinataris.add(generarDestinatariDto(destinatario));
			}
			notificacio.setDestinataris(destinataris);
		}
		return notificacio;
	}
	private NotificacioDestinatariDto generarDestinatariDto(
			TipoDestinatario destinatario) throws ParseException {
		SimpleDateFormat sdfCaducitat = new SimpleDateFormat("yyyy-MM-dd");
		NotificacioDestinatariDto destinatari = new NotificacioDestinatariDto();
		destinatari.setTitularNom(destinatario.getTitular().getNombre());
		destinatari.setTitularLlinatge1(destinatario.getTitular().getApellidos());
		destinatari.setTitularNif(destinatario.getTitular().getNif());
		destinatari.setTitularTelefon(destinatario.getTitular().getTelefono());
		destinatari.setTitularEmail(destinatario.getTitular().getEmail());
		destinatari.setDestinatariNom(destinatario.getDestinatario().getNombre());
		String[] llinatgesSeparats = separarLlinatges(
				destinatario.getDestinatario().getApellidos());
		destinatari.setDestinatariLlinatge1(llinatgesSeparats[0]);
		destinatari.setDestinatariLlinatge2(llinatgesSeparats[1]);
		destinatari.setDestinatariNif(destinatario.getDestinatario().getNif());
		destinatari.setDestinatariTelefon(destinatario.getDestinatario().getTelefono());
		destinatari.setDestinatariEmail(destinatario.getDestinatario().getEmail());
		destinatari.setDomiciliTipus(
				NotificaDomiciliTipusEnumDto.toEnum(destinatario.getTipoDomicilio()));
		if (destinatario.getDomicilio() != null) {
			destinatari.setDomiciliConcretTipus(
					NotificaDomiciliConcretTipusEnumDto.toEnum(destinatario.getDomicilio().getTipoDomicilioConcreto()));
			destinatari.setDomiciliViaTipus(destinatario.getDomicilio().getTipoVia());
			destinatari.setDomiciliViaNom(destinatario.getDomicilio().getNombreVia());
			destinatari.setDomiciliNumeracioTipus(
					NotificaDomiciliNumeracioTipusEnumDto.toEnum(destinatario.getDomicilio().getCalificadorNumero()));
			destinatari.setDomiciliNumeracioNumero(destinatario.getDomicilio().getNumeroCasa());
			destinatari.setDomiciliNumeracioPuntKm(destinatario.getDomicilio().getPuntoKilometrico());
			destinatari.setDomiciliApartatCorreus(destinatario.getDomicilio().getApartadoCorreos());
			destinatari.setDomiciliBloc(destinatario.getDomicilio().getBloque());
			destinatari.setDomiciliPortal(destinatario.getDomicilio().getPortal());
			destinatari.setDomiciliEscala(destinatario.getDomicilio().getEscalera());
			destinatari.setDomiciliPlanta(destinatario.getDomicilio().getPlanta());
			destinatari.setDomiciliPorta(destinatario.getDomicilio().getPuerta());
			destinatari.setDomiciliComplement(destinatario.getDomicilio().getComplemento());
			destinatari.setDomiciliPoblacio(destinatario.getDomicilio().getPoblacion());
			if (destinatario.getDomicilio().getMunicipio() != null) {
				destinatari.setDomiciliMunicipiCodiIne(destinatario.getDomicilio().getMunicipio().getCodigoIne());
				destinatari.setDomiciliMunicipiNom(destinatario.getDomicilio().getMunicipio().getNombre());
			}
			destinatari.setDomiciliCodiPostal(destinatario.getDomicilio().getCodigoPostal());
			if (destinatario.getDomicilio().getProvincia() != null) {
				destinatari.setDomiciliProvinciaCodi(destinatario.getDomicilio().getProvincia().getCodigoProvincia());
				destinatari.setDomiciliProvinciaNom(destinatario.getDomicilio().getProvincia().getNombre());
			}
			if (destinatario.getDomicilio().getPais() != null) {
				destinatari.setDomiciliPaisCodiIso(destinatario.getDomicilio().getPais().getCodigoIso3166());
				destinatari.setDomiciliPaisNom(destinatario.getDomicilio().getPais().getNombre());
			}
			destinatari.setDomiciliLinea1(destinatario.getDomicilio().getLinea1());
			destinatari.setDomiciliLinea2(destinatario.getDomicilio().getLinea2());
			destinatari.setDomiciliCie(destinatario.getDomicilio().getCie());
		}
		if (destinatario.getDireccionElectronica() != null) {
			destinatari.setDehObligat(destinatario.getDireccionElectronica().isObligado());
			destinatari.setDehNif(destinatario.getDireccionElectronica().getNif());
			destinatari.setDehProcedimentCodi(destinatario.getDireccionElectronica().getCodigoProcedimiento());
		}
		destinatari.setServeiTipus(NotificaServeiTipusEnumDto.toEnum(destinatario.getServicio()));
		if (destinatario.getOpcionesEmision() != null) {
			destinatari.setRetardPostal(destinatario.getOpcionesEmision().getRetardoPostalDeh());
			if (destinatario.getOpcionesEmision().getCaducidad() != null) {
				destinatari.setCaducitat(
						sdfCaducitat.parse(destinatario.getOpcionesEmision().getCaducidad()));
			}
		}
		destinatari.setReferencia(destinatario.getReferenciaEmisor());
		return destinatari;
	}

	private void comprovarIdentificadorEnviament(
			NotificacioEntity notificacio,
			NotificacioDestinatariEntity destinatari,
			String identificador,
			String titularNif,
			String referenciaEmisor) {
		if (!identificador.equals(destinatari.getNotificaIdentificador())) {
			throw new SistemaExternException(
					"NOTIFICA",
					"La identificació retornada no coincideix amb la identificació de la petició (" + 
					"identificadorPeticio=" + destinatari.getNotificaIdentificador() + ", " +
					"identificadorRetornat=" + identificador + ")");
		}
		if (!titularNif.equalsIgnoreCase(destinatari.getTitularNif())) {
			throw new SistemaExternException(
					"NOTIFICA",
					"La identificació retornada no coincideix amb la identificació de la petició (" + 
					"titularNifPeticio=" + destinatari.getTitularNif() + ", " +
					"titularNifRetornat=" + titularNif + ")");
		}
		if (!referenciaEmisor.equals(destinatari.getReferencia())) {
			throw new SistemaExternException(
					"NOTIFICA",
					"La identificació retornada no coincideix amb la identificació de la petició (" + 
					"referenciaEmisorPeticio=" + destinatari.getReferencia() + ", " +
					"referenciaEmisorRetornat=" + referenciaEmisor + ")");
		}
	}

	private XMLGregorianCalendar toXmlGregorianCalendar(Date date) throws DatatypeConfigurationException {
		if (date == null) {
			return null;
		}
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
	}
	private Date toDate(XMLGregorianCalendar calendar) throws DatatypeConfigurationException {
		if (calendar == null) {
			return null;
		}
		return calendar.toGregorianCalendar().getTime();
	}

	private String xifrarIdPerNotifica(Long id) throws GeneralSecurityException {
		// Si el mode test està actiu concatena la data actual a l'identificador de
		// base de dades per a generar l'id de Notifica. Si no ho fessim així es
		// duplicarien els ids de Notifica en cada execució del test i les cridades
		// a Notifica donarien error.
		long idlong = (modeTest) ? id.longValue() + System.currentTimeMillis() : id.longValue();
		byte[] bytes = longToBytes(idlong);
		Cipher cipher = Cipher.getInstance("RC4");
		SecretKeySpec rc4Key = new SecretKeySpec(getClauXifratIdsProperty().getBytes(),"RC4");
		cipher.init(Cipher.ENCRYPT_MODE, rc4Key);
		byte[] xifrat = cipher.doFinal(bytes);
		return new String(Base64.encode(xifrat));
	}
	@SuppressWarnings("unused")
	private Long desxifrarIdPerNotifica(String idXifrat) throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("RC4");
		SecretKeySpec rc4Key = new SecretKeySpec(getClauXifratIdsProperty().getBytes(),"RC4");
		cipher.init(Cipher.DECRYPT_MODE, rc4Key);
		byte[] desxifrat = cipher.doFinal(Base64.decode(idXifrat.getBytes()));
		return new Long(bytesToLong(desxifrat));
	}

	private byte[] longToBytes(long l) {
		byte[] result = new byte[Long.SIZE / Byte.SIZE];
	    for (int i = 7; i >= 0; i--) {
	        result[i] = (byte)(l & 0xFF);
	        l >>= 8;
	    }
	    return result;
	}
	private long bytesToLong(byte[] b) {
		long result = 0;
	    for (int i = 0; i < 8; i++) {
	        result <<= 8;
	        result |= (b[i] & 0xFF);
	    }
	    return result;
	}

	private String concatenarLlinatges(
			String llinatge1,
			String llinatge2) {
		StringBuilder llinatges = new StringBuilder();
		llinatges.append(llinatge1.trim());
		if (llinatge2 != null && !llinatge2.trim().isEmpty()) {
			llinatges.append(" ");
			llinatges.append(llinatge2);
		}
		return llinatges.toString();
	}

	private String[] separarLlinatges(
			String llinatges) {
		int indexEspai = llinatges.indexOf(" ");
		if (indexEspai != -1) {
			return new String[] {
					llinatges.substring(0, indexEspai),
					llinatges.substring(indexEspai + 1)};
		} else {
			return new String[] {
					llinatges,
					null};
		}
	}

	private NotificaWsPortType getNotificaWs() throws InstanceNotFoundException, MalformedObjectNameException, MalformedURLException, RemoteException, NamingException, CreateException {
		NotificaWsPortType port = new WsClientHelper<NotificaWsPortType>().generarClientWs(
				getClass().getResource("/es/caib/notib/core/wsdl/NotificaWS.wsdl"),
				getUrlProperty(),
				new QName(
						"https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/", 
						"NotificaWsService"),
				getUsernameProperty(),
				getPasswordProperty(),
				NotificaWsPortType.class,
				new ApiKeySOAPHandler(getApiKeyProperty()),
				/*new FirmaSOAPHandler(
						getKeystorePathProperty(),
						getKeystoreTypeProperty(),
						getKeystorePasswordProperty(),
						getKeystoreCertAliasProperty(),
						getKeystoreCertPasswordProperty()),*/
				new WsClientHelper.SOAPLoggingHandler());
		return port;
	}

	private SedeWsPortType getSedeWs() throws InstanceNotFoundException, MalformedObjectNameException, MalformedURLException, RemoteException, NamingException, CreateException {
		SedeWsPortType port = new WsClientHelper<SedeWsPortType>().generarClientWs(
				getClass().getResource("/es/caib/notib/core/wsdl/SedeWs.wsdl"),
				getUrlProperty(),
				new QName(
						"https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/",
						"SedeWsService"),
				getUsernameProperty(),
				getPasswordProperty(),
				SedeWsPortType.class);
		return port;
	}

	private String getUrlProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.url");
	}
	private String getUsernameProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.username");
	}
	private String getPasswordProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.password");
	}
	private String getClauXifratIdsProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.clau.xifrat.ids",
				"P0rt4FI8");
	}
	private String getApiKeyProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.apikey");
	}
	@SuppressWarnings("unused")
	private String getKeystorePathProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.keystore.path");
	}
	@SuppressWarnings("unused")
	private String getKeystoreTypeProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.keystore.type");
	}
	@SuppressWarnings("unused")
	private String getKeystorePasswordProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.keystore.password");
	}
	@SuppressWarnings("unused")
	private String getKeystoreCertAliasProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.keystore.cert.alias");
	}
	@SuppressWarnings("unused")
	private String getKeystoreCertPasswordProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.keystore.cert.password");
	}

	public class ApiKeySOAPHandler implements SOAPHandler<SOAPMessageContext> {
		private final String apiKey;
		public ApiKeySOAPHandler(String apiKey) {
			this.apiKey = apiKey;
		}
		@Override
		public boolean handleMessage(SOAPMessageContext context) {
			Boolean outboundProperty = (Boolean)context.get(
					MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (outboundProperty.booleanValue()) {
				try {
					SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();
					SOAPFactory factory = SOAPFactory.newInstance();
					SOAPElement apiKeyElement = factory.createElement("api_key");
					apiKeyElement.addTextNode(apiKey);
					SOAPHeader header = envelope.addHeader();
					header.addChildElement(apiKeyElement);
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

	public class FirmaSOAPHandler implements SOAPHandler<SOAPMessageContext> {
		private String keystoreLocation;
		private String keystoreType;
		private String keystorePassword;
		private String keystoreCertAlias;
		private String keystoreCertPassword;
		public FirmaSOAPHandler(
				String keystoreLocation,
				String keystoreType,
				String keystorePassword,
				String keystoreCertAlias,
				String keystoreCertPassword) {
			this.keystoreLocation = keystoreLocation;
			this.keystoreType = keystoreType;
			this.keystorePassword = keystorePassword;
			this.keystoreCertAlias = keystoreCertAlias;
			this.keystoreCertPassword = keystoreCertPassword;
		}
		@Override
		public boolean handleMessage(SOAPMessageContext context) {
			Boolean outboundProperty = (Boolean)context.get(
					MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (outboundProperty.booleanValue()) {
				try {
					Document document = toDocument(context.getMessage());
					Properties cryptoProperties = getCryptoProperties();
			        WSSecHeader header = new WSSecHeader();
			        header.setMustUnderstand(false);
			        header.insertSecurityHeader(document);
					WSSecSignature signer = new WSSecSignature();
					signer.setUserInfo(keystoreCertAlias, keystoreCertPassword);
					Crypto crypto = CryptoFactory.getInstance(cryptoProperties);
					Document signedDoc = signer.build(
							document,
							crypto,
							header);
					context.getMessage().getSOAPPart().setContent(
							new DOMSource(signedDoc));
				} catch (Exception ex) {
					throw new RuntimeException(
							"No s'ha pogut firmar el missatge SOAP",
							ex);
				}
				@SuppressWarnings("unchecked")
				Map<String, List<String>> headers = (Map<String, List<String>>)context.get(
						MessageContext.HTTP_REQUEST_HEADERS);
				if (headers != null) {
					for (String header: headers.keySet()) {
						List<String> values = headers.get(header);
						System.out.println(">>> " + header);
						for (String value: values) {
							System.out.println(">>>      " + value);
						}
					}
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
		private Properties getCryptoProperties() {
			Properties cryptoProperties = new Properties();
			cryptoProperties.put(
					"org.apache.ws.security.crypto.provider",
					"org.apache.ws.security.components.crypto.Merlin");
			cryptoProperties.put(
					"org.apache.ws.security.crypto.merlin.file",
					keystoreLocation);
			cryptoProperties.put(
					"org.apache.ws.security.crypto.merlin.keystore.type",
					keystoreType);
			if (keystorePassword != null && !keystorePassword.isEmpty()) {
				cryptoProperties.put(
						"org.apache.ws.security.crypto.merlin.keystore.password",
						keystorePassword);
			}
			cryptoProperties.put(
					"org.apache.ws.security.crypto.merlin.keystore.alias",
					keystoreCertAlias);
			return cryptoProperties;
		}
		private Document toDocument(SOAPMessage soapMsg) throws SOAPException, TransformerException {
			Source src = soapMsg.getSOAPPart().getContent();
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			DOMResult result = new DOMResult();
			transformer.transform(src, result);
			return (Document) result.getNode();
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(NotificaHelper.class);

}
