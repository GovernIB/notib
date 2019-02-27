/**
 * 
 */
package es.caib.notib.core.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

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
import es.caib.notib.core.api.exception.SistemaExternException;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.entity.PersonaEntity;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.core.wsdl.notificaV2.NotificaWsV2PortType;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.AltaRemesaEnvios;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.Destinatarios;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.Documento;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.EntregaDEH;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.Envio;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.Envios;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.Opcion;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.Opciones;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.Persona;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.ResultadoAltaRemesaEnvios;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.ResultadoEnvio;
import es.caib.notib.core.wsdl.notificaV2.infoEnvioV2.Certificacion;
import es.caib.notib.core.wsdl.notificaV2.infoEnvioV2.CodigoDIR;
import es.caib.notib.core.wsdl.notificaV2.infoEnvioV2.Datado;
import es.caib.notib.core.wsdl.notificaV2.infoEnvioV2.InfoEnvioV2;
import es.caib.notib.core.wsdl.notificaV2.infoEnvioV2.ResultadoInfoEnvioV2;

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
	private NotificacioEventRepository notificacioEventRepository;
	@Autowired
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Autowired
	private ProcedimentRepository procedimentRepository;
	
	@Autowired
	private PluginHelper pluginHelper;



	public boolean notificacioEnviar(
			Long notificacioId) {
		NotificacioEntity notificacio = notificacioRepository.findById(notificacioId);
		if (!NotificacioEstatEnumDto.PENDENT.equals(notificacio.getEstat())) {
			throw new ValidationException(
					notificacioId,
					NotificacioEntity.class,
					"La notificació no te l'estat " + NotificacioEstatEnumDto.PENDENT);
		}
		notificacio.updateNotificaNouEnviament(pluginHelper.getNotificaReintentsPeriodeProperty());
		try {
			ResultadoAltaRemesaEnvios resultadoAlta = enviaNotificacio(notificacio);
			if ("000".equals(resultadoAlta.getCodigoRespuesta()) && "OK".equalsIgnoreCase(resultadoAlta.getDescripcionRespuesta())) {
				for (ResultadoEnvio resultadoEnvio: resultadoAlta.getResultadoEnvios().getItem()) {
					for (NotificacioEnviamentEntity enviament: notificacio.getEnviaments()) {
						if (enviament.getTitular().getNif().equalsIgnoreCase(resultadoEnvio.getNifTitular())) {
							enviament.updateNotificaEnviada(
									resultadoEnvio.getIdentificador());
						}
					}
				}
				NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT,
						notificacio).build();
				notificacio.updateEstat(NotificacioEstatEnumDto.ENVIADA);
				notificacio.updateEventAfegir(event);
				notificacio.updateNotificaError(
						null,
						null);
				notificacioEventRepository.save(event);
			} else {
				NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT,
						notificacio).
						error(true).
						errorDescripcio("[" + resultadoAlta.getCodigoRespuesta() + "] " + resultadoAlta.getDescripcionRespuesta()).
						build();
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
					"Error al donar d'alta la notificació a Notific@ (notificacioId=" + notificacioId + ")",
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
		return NotificacioEstatEnumDto.ENVIADA.equals(notificacio.getEstat());
	}

	public boolean enviamentRefrescarEstat(
			Long enviamentId) throws SistemaExternException {
		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findOne(enviamentId);
		NotificacioEntity notificacio = notificacioRepository.findById(enviament.getNotificacioId());
		enviament.setNotificacio(notificacio);
//		NotificacioEnviamentEstatEnumDto estatActual = enviament.getNotificaEstat();
		Date dataUltimDatat = enviament.getNotificaDataCreacio();
		Date dataUltimaCertificacio = enviament.getNotificaCertificacioData();

		NotificacioEventEntity eventDatat  = null;
		NotificacioEventEntity eventCert  = null;
		
		enviament.updateNotificaDataRefrescEstat();
		
		String errorPrefix = "Error al consultar l'estat d'un enviament fet amb NotificaV2 (" +
				"notificacioId=" + notificacio.getId() + ", " +
				"notificaIdentificador=" + enviament.getNotificaIdentificador() + ")";
		
		try {
			InfoEnvioV2 infoEnvio = new InfoEnvioV2();
			infoEnvio.setIdentificador(enviament.getNotificaIdentificador());
			ResultadoInfoEnvioV2 resultadoInfoEnvio = getNotificaWs().infoEnvioV2(infoEnvio);
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
						eventDatat = NotificacioEventEntity.getBuilder(
								NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT,
								enviament.getNotificacio()).
								enviament(enviament).
								descripcio(datatDarrer.getResultado()).
//								callbackInicialitza().
								build();
						notificacio.updateEventAfegir(eventDatat);
						enviament.updateNotificaError(false, null);
					}
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
							new ByteArrayInputStream(decodificat));
					
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
					eventCert = NotificacioEventEntity.getBuilder(
							NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO,
							enviament.getNotificacio()).
							enviament(enviament).
//							callbackInicialitza().
							build();
					notificacio.updateEventAfegir(eventCert);
				}
			}
			if (eventDatat != null) {
				eventDatat.callbackInicialitza();
			} else if (eventCert != null) {
				eventCert.callbackInicialitza();
			}
			NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_INFO,
					notificacio).
					enviament(enviament).build();
			notificacio.updateEventAfegir(event);
			return true;
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
			return false;
		}
	}



	private ResultadoAltaRemesaEnvios enviaNotificacio(
			NotificacioEntity notificacio) throws Exception {
		ResultadoAltaRemesaEnvios resultat = null;
		if (!NotificacioEstatEnumDto.PENDENT.equals(notificacio.getEstat())) {
			throw new ValidationException(
					notificacio.getId(),
					NotificacioEntity.class,
					"La notificació no te l'estat " + NotificacioEstatEnumDto.PENDENT);
		}
		try {
			AltaRemesaEnvios altaRemesaEnvios = generarAltaRemesaEnvios(notificacio);
			resultat = getNotificaWs().altaRemesaEnvios(altaRemesaEnvios);
		} catch (SOAPFaultException sfe) {
			String codiResposta = sfe.getFault().getFaultCode();
			String descripcioResposta = sfe.getFault().getFaultString();
			resultat = new ResultadoAltaRemesaEnvios();
			resultat.setCodigoRespuesta(codiResposta);
			resultat.setDescripcionRespuesta(descripcioResposta);
		}
		return resultat;
	}

	private AltaRemesaEnvios generarAltaRemesaEnvios(
			NotificacioEntity notificacio) throws GeneralSecurityException, DatatypeConfigurationException {
		AltaRemesaEnvios envios = new AltaRemesaEnvios();
		Date dataProgramada;
		Integer retardPostal;
		
		envios.setCodigoOrganismoEmisor(notificacio.getEmisorDir3Codi());
		switch (notificacio.getEnviamentTipus()) {
		case COMUNICACIO:
			envios.setTipoEnvio(new BigInteger("1"));
			break;
		case NOTIFICACIO:
			envios.setTipoEnvio(new BigInteger("2"));
			break;
		}
		//envios.setFechaEnvioProgramado(
		//		toXmlGregorianCalendar(notificacio.getEnviamentDataProgramada()));

		if (notificacio.getProcedimentCodiNotib() != null) {
			dataProgramada = procedimentRepository.findByCodi(notificacio.getProcedimentCodiNotib()).getEnviamentDataProgramada();
		} else {
			dataProgramada = notificacio.getEnviamentDataProgramada();
		}
		envios.setFechaEnvioProgramado(
				toXmlGregorianCalendar(dataProgramada));
		
//		envios.setFechaEnvioProgramado(
//				toXmlGregorianCalendar(notificacio.getEnviamentDataProgramada()));
		envios.setConcepto(notificacio.getConcepte());
		envios.setDescripcion(notificacio.getDescripcio());
		envios.setProcedimiento(
				notificacio.getProcedimentCodiNotib());

		if(notificacio.getDocument().getArxiuGestdocId() != null) {
			Documento documento = new Documento();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			pluginHelper.gestioDocumentalGet(
					notificacio.getDocument().getArxiuGestdocId(),
					PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
					baos);

			documento.setContenido(baos.toByteArray());
			documento.setHash(notificacio.getDocument().getHash());
			Opciones opcionesDocumento = new Opciones();
			Opcion opcionNormalizado = new Opcion();
			opcionNormalizado.setTipo("normalizado");
			opcionNormalizado.setValue(
					notificacio.getDocument().getNormalitzat()  ? "si" : "no"); // si o no
			opcionesDocumento.getOpcion().add(opcionNormalizado);
			Opcion opcionGenerarCsv = new Opcion();
			opcionGenerarCsv.setTipo("generarCsv");
			opcionGenerarCsv.setValue(
					notificacio.getDocument().getGenerarCsv()  ? "si" : "no"); // si o no
			opcionesDocumento.getOpcion().add(opcionGenerarCsv);
			documento.setOpcionesDocumento(opcionesDocumento);
			envios.setDocumento(documento);
		} else {
			Documento documento = new Documento();
			documento.setHash(notificacio.getDocument().getHash());
			if(notificacio.getDocument().getContingutBase64() != null) {
				documento.setContenido(notificacio.getDocument().getContingutBase64().getBytes());	
			}
			Opciones opcionesDocumento = new Opciones();
			Opcion opcionNormalizado = new Opcion();
			opcionNormalizado.setTipo("normalizado");
			opcionNormalizado.setValue(
					notificacio.getDocument().getNormalitzat()  ? "si" : "no"); // si o no
			opcionesDocumento.getOpcion().add(opcionNormalizado);
			Opcion opcionGenerarCsv = new Opcion();
			opcionGenerarCsv.setTipo("generarCsv");
			opcionGenerarCsv.setValue(
					notificacio.getDocument().getGenerarCsv()  ? "si" : "no"); // si o no
			opcionesDocumento.getOpcion().add(opcionGenerarCsv);
			documento.setOpcionesDocumento(opcionesDocumento);
			envios.setDocumento(documento);
		}
		//V1 rest
		//if (notificacio.getRetardPostal() != null) {
		//	Opcion opcionRetardo = new Opcion();
		//	opcionRetardo.setTipo("retardo");
		//	opcionRetardo.setValue(
		//			notificacio.getRetardPostal().toString()); // número de días
		//	opcionesRemesa.getOpcion().add(opcionRetardo);
		//}
		envios.setEnvios(generarEnvios(notificacio));
		Opciones opcionesRemesa = new Opciones();
		if(notificacio.getProcedimentCodiNotib() != null) {
			retardPostal = procedimentRepository.findByCodi(notificacio.getProcedimentCodiNotib()).getRetard();
		} else {
			retardPostal = notificacio.getRetardPostal();
		}
		
		if (retardPostal != null) {
			Opcion opcionRetardo = new Opcion();
			opcionRetardo.setTipo("retardo");
			opcionRetardo.setValue(retardPostal.toString()); // número de días
			opcionesRemesa.getOpcion().add(opcionRetardo);
		}
//		if (notificacio.getRetardPostal() != null) {
//			Opcion opcionRetardo = new Opcion();
//			opcionRetardo.setTipo("retardo");
//			opcionRetardo.setValue(
//					notificacio.getRetardPostal().toString()); // número de días
//			opcionesRemesa.getOpcion().add(opcionRetardo);
//		}
		if (notificacio.getCaducitat() != null) {
			Opcion opcionCaducidad = new Opcion();
			opcionCaducidad.setTipo("caducidad");
			SimpleDateFormat sdfCaducitat = new SimpleDateFormat("yyyy-MM-dd");
			opcionCaducidad.setValue(
					sdfCaducitat.format(notificacio.getCaducitat())); // formato YYYY-MM-DD
			opcionesRemesa.getOpcion().add(opcionCaducidad);
		}
		envios.setOpcionesRemesa(opcionesRemesa);
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
				titular.setNif(enviament.getTitular().getNif());
				titular.setNombre(enviament.getTitular().getNom());
				titular.setApellidos(
						concatenarLlinatges(
								enviament.getTitular().getLlinatge1(),
								enviament.getTitular().getLlinatge2()));
				titular.setTelefono(enviament.getTitular().getTelefon());
				titular.setEmail(enviament.getTitular().getEmail());
				titular.setRazonSocial(enviament.getTitular().getRaoSocial());
				titular.setCodigoDestino(enviament.getTitular().getCodiEntitatDesti());
				envio.setTitular(titular);
					Destinatarios destinatarios = new Destinatarios();
					for(PersonaEntity destinatari : enviament.getDestinataris()) {
						if (destinatari.getNif() != null) {
							Persona destinatario = new Persona();
							destinatario.setNif(destinatari.getNif());
							destinatario.setNombre(destinatari.getNom());
							destinatario.setApellidos(
									concatenarLlinatges(
											destinatari.getLlinatge1(),
											destinatari.getLlinatge2()));
							destinatario.setTelefono(destinatari.getTelefon());
							destinatario.setEmail(destinatari.getEmail());
							destinatario.setRazonSocial(destinatari.getRaoSocial());
							destinatario.setCodigoDestino(destinatari.getCodiEntitatDesti());
							destinatarios.getDestinatario().add(destinatario);
						}
					}
					envio.setDestinatarios(destinatarios);
				if (enviament.getDehObligat() != null) {
					EntregaDEH entregaDeh = new EntregaDEH();
					entregaDeh.setObligado(enviament.getDehObligat());
					envio.setEntregaDEH(entregaDeh);
				}
				envios.getEnvio().add(envio);
			}
		}
		return envios;
	}

	private NotificaWsV2PortType getNotificaWs() throws InstanceNotFoundException, MalformedObjectNameException, MalformedURLException, RemoteException, NamingException, CreateException {
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
				new ApiKeySOAPHandlerV2(getApiKeyProperty()));
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
					SOAPElement apiKeyElement = factory.createElement("apiKey");
					/*SOAPElement apiKeyElement = factory.createElement(
							new QName(
									"https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/", 
									"apiKey"));*/
					apiKeyElement.addTextNode(apiKey);
					SOAPHeader header = envelope.getHeader();
					if (header == null) {
						header = envelope.addHeader();
					}
					header.addChildElement(apiKeyElement);
					context.getMessage().saveChanges();
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

	private static final Logger logger = LoggerFactory.getLogger(NotificaV2Helper.class);

}
