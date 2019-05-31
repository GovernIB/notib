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

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
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
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.EntregaPostal;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.Envio;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.Envios;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.Opcion;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.Opciones;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.OrganismoPagadorCIE;
import es.caib.notib.core.wsdl.notificaV2.altaremesaenvios.OrganismoPagadorPostal;
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
	@Autowired 
	private EmailHelper emailHelper;
	@Autowired 
	ConversioTipusHelper conversioTipusHelper;
	
	public boolean notificacioEnviar(
			Long notificacioId) {
		NotificacioEntity notificacio = notificacioRepository.findById(notificacioId);
		if (!NotificacioEstatEnumDto.REGISTRADA.equals(notificacio.getEstat())) {
			throw new ValidationException(
					notificacioId,
					NotificacioEntity.class,
					"La notificació no te l'estat " + NotificacioEstatEnumDto.REGISTRADA);
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
			if (enviament.getNotificaIdentificador() != null) {
				InfoEnvioV2 infoEnvio = new InfoEnvioV2();
				infoEnvio.setIdentificador(enviament.getNotificaIdentificador());
				String apiKey = enviament.getNotificacio().getEntitat().getApiKey();
				ResultadoInfoEnvioV2 resultadoInfoEnvio = getNotificaWs(apiKey).infoEnvioV2(infoEnvio);
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
							if (notificacio.getEstat() == NotificacioEstatEnumDto.FINALITZADA) {
								emailHelper.prepararEnvioEmailNotificacio(notificacio);
							}
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
			} else {
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
			return false;
		}
	}

	
	public ResultadoInfoEnvioV2 infoEnviament(
			NotificacioEnviamentEntity enviament) throws SistemaExternException {
		try {
			InfoEnvioV2 infoEnvio = new InfoEnvioV2();
			infoEnvio.setIdentificador(enviament.getNotificaIdentificador());
			String apiKey = enviament.getNotificacio().getEntitat().getApiKey();
			return getNotificaWs(apiKey).infoEnvioV2(infoEnvio);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	


	public ResultadoAltaRemesaEnvios enviaNotificacio(
			NotificacioEntity notificacio) throws Exception {
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
		}
		return resultat;
	}

	private AltaRemesaEnvios generarAltaRemesaEnvios(
			NotificacioEntity notificacio) throws GeneralSecurityException, DatatypeConfigurationException, DecoderException {
		AltaRemesaEnvios envios = new AltaRemesaEnvios();
		Integer retardPostal;
		
		envios.setCodigoOrganismoEmisor(notificacio.getEntitat().getDir3Codi());
		switch (notificacio.getEnviamentTipus()) {
		case COMUNICACIO:
			envios.setTipoEnvio(new BigInteger("1"));
			break;
		case NOTIFICACIO:
			envios.setTipoEnvio(new BigInteger("2"));
			break;
		}
		
//		if (notificacio.getProcedimentCodiNotib() != null) {
//			dataProgramada = procedimentRepository.findOne(notificacio.getProcediment().getId()).getEnviamentDataProgramada();
//		} else {
//			dataProgramada = notificacio.getEnviamentDataProgramada();
//		}
		envios.setFechaEnvioProgramado(
				toXmlGregorianCalendar(notificacio.getEnviamentDataProgramada()));
		
		envios.setConcepto(notificacio.getConcepte());
		envios.setDescripcion(notificacio.getDescripcio());
		envios.setProcedimiento(
				notificacio.getProcedimentCodiNotib());
		Documento  documento = new Documento();
		if(notificacio.getDocument() != null && notificacio.getDocument().getArxiuGestdocId() != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			pluginHelper.gestioDocumentalGet(
					notificacio.getDocument().getArxiuGestdocId(),
					PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
					baos);
			documento.setContenido(baos.toByteArray());
//			documento.setMetadatos(notificacio.getDocument().getMetadades());
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
			retardPostal = procedimentRepository.findOne(notificacio.getProcediment().getId()).getRetard();
		} else {
			retardPostal = notificacio.getRetard();
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
					if (enviament.getDomiciliConcretTipus() != null) {
						EntregaPostal entregaPostal = new EntregaPostal();
						OrganismoPagadorPostal pagadorPostal = new OrganismoPagadorPostal();
						if (notificacio.getPagadorPostal() != null) {
							pagadorPostal.setCodigoDIR3Postal(notificacio.getPagadorPostal().getDir3codi());
							pagadorPostal.setCodClienteFacturacionPostal(notificacio.getPagadorPostal().getFacturacioClientCodi());
							pagadorPostal.setNumContratoPostal(notificacio.getPagadorPostal().getContracteNum());
							pagadorPostal.setFechaVigenciaPostal(
								toXmlGregorianCalendar(notificacio.getPagadorPostal().getContracteDataVig()));
						}
						entregaPostal.setOrganismoPagadorPostal(pagadorPostal);
						OrganismoPagadorCIE pagadorCie = new OrganismoPagadorCIE();
						if (notificacio.getPagadorCie() != null) {
							pagadorCie.setCodigoDIR3CIE(notificacio.getPagadorCie().getDir3codi());
							pagadorCie.setFechaVigenciaCIE(
								toXmlGregorianCalendar(notificacio.getPagadorCie().getContracteDataVig()));
						}
						entregaPostal.setOrganismoPagadorCIE(pagadorCie);
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
						entregaPostal.setTipoVia(
								viaTipusToString(enviament.getDomiciliViaTipus()));
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
						entregaPostal.setLinea1(enviament.getDomiciliLinea1());
						entregaPostal.setLinea2(enviament.getDomiciliLinea2());
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
				if (enviament.getDehObligat() != null) {
					EntregaDEH entregaDeh = new EntregaDEH();
					entregaDeh.setObligado(enviament.getDehObligat());
					if (enviament.getDehObligat() != true) {
						entregaDeh.setCodigoProcedimiento(notificacio.getProcedimentCodiNotib());
					}
					envio.setEntregaDEH(entregaDeh);
				}
				envios.getEnvio().add(envio);
			}
		}
		return envios;
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
