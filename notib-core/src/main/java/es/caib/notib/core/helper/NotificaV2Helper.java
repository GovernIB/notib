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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.message.WSSecHeader;
import org.apache.ws.security.message.WSSecSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

import es.caib.notib.core.api.dto.NotificaCertificacioArxiuTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaCertificacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliViaTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaRespostaCertificacioDto;
import es.caib.notib.core.api.dto.NotificaRespostaDatatDto;
import es.caib.notib.core.api.dto.NotificaRespostaDatatDto.NotificaRespostaDatatEventDto;
import es.caib.notib.core.api.dto.NotificaRespostaEstatDto;
import es.caib.notib.core.api.dto.NotificacioDestinatariEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.exception.SistemaExternException;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.wsdl.notifica.CertificacionEnvioRespuesta;
import es.caib.notib.core.wsdl.notifica.DatadoEnvio;
import es.caib.notib.core.wsdl.notifica.ResultadoCertificacion;
import es.caib.notib.core.wsdl.notifica.TipoIntento;
import es.caib.notib.core.wsdl.notificav2.AltaRemesaEnvios;
import es.caib.notib.core.wsdl.notificav2.Certificacion;
import es.caib.notib.core.wsdl.notificav2.Datado;
import es.caib.notib.core.wsdl.notificav2.Destinatarios2;
import es.caib.notib.core.wsdl.notificav2.Documento2;
import es.caib.notib.core.wsdl.notificav2.EntregaDEH2;
import es.caib.notib.core.wsdl.notificav2.EntregaPostal2;
import es.caib.notib.core.wsdl.notificav2.Envio;
import es.caib.notib.core.wsdl.notificav2.Envios;
import es.caib.notib.core.wsdl.notificav2.InfoEnvioV2;
import es.caib.notib.core.wsdl.notificav2.NotificaWsV2PortType;
import es.caib.notib.core.wsdl.notificav2.Opcion2;
import es.caib.notib.core.wsdl.notificav2.Opciones2;
import es.caib.notib.core.wsdl.notificav2.OrganismoPagadorCIE2;
import es.caib.notib.core.wsdl.notificav2.OrganismoPagadorPostal2;
import es.caib.notib.core.wsdl.notificav2.Persona2;
import es.caib.notib.core.wsdl.notificav2.ResultadoAltaRemesaEnvios;
import es.caib.notib.core.wsdl.notificav2.ResultadoEnvio;
import es.caib.notib.core.wsdl.notificav2.ResultadoInfoEnvioV2;
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
public class NotificaV2Helper extends AbstractNotificaHelper {

	@Autowired
	private NotificacioRepository notificacioRepository;
	@Autowired
	private NotificacioEnviamentRepository notificacioDestinatariRepository;
	@Autowired
	private NotificacioEventRepository notificacioEventRepository;

	@Autowired
	private PluginHelper pluginHelper;

	private boolean modeTest;



	@Transactional
	public boolean enviament(
			Long notificacioId) {
		NotificacioEntity notificacio = notificacioRepository.findOne(notificacioId);
		if (!NotificacioEstatEnumDto.PENDENT.equals(notificacio.getEstat())) {
			throw new ValidationException(
					notificacioId,
					NotificacioEntity.class,
					"La notificació no te l'estat " + NotificacioEstatEnumDto.PENDENT);
		}
		try {
			AltaRemesaEnvios altaRemesaEnvios = generarAltaRemesaEnvios(notificacio);
			ResultadoAltaRemesaEnvios resultadoAlta = getNotificaWs().altaRemesaEnvios(
					altaRemesaEnvios);
			if ("000".equals(resultadoAlta.getCodigoRespuesta()) && "OK".equalsIgnoreCase(resultadoAlta.getDescripcionRespuesta())) {
				for (ResultadoEnvio resultadoEnvio: resultadoAlta.getResultadoEnvios().getItem()) {
					for (NotificacioEnviamentEntity enviament: notificacio.getEnviaments()) {
						if (enviament.getNotificaReferencia().equals(resultadoEnvio.getIdentificador())) {
							enviament.updateNotificaIdentificador(
									resultadoEnvio.getIdentificador());
							enviament.updateNotificaEstat(
									new Date(),
									NotificacioDestinatariEstatEnumDto.NOTIB_ENVIADA,
									true);
						}
					}
				}
				NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT,
						notificacio).build();
				notificacio.updateEstat(NotificacioEstatEnumDto.ENVIADA);
				notificacio.updateEventAfegir(event);
				notificacio.updateErrorNotifica(
						false,
						null);
				notificacioEventRepository.save(event);
			} else {
				NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT,
						notificacio).
						error(true).
						errorDescripcio("Error retornat per notifica: [" + resultadoAlta.getCodigoRespuesta() + "] " + resultadoAlta.getDescripcionRespuesta()).
						build();
				notificacio.updateErrorNotifica(
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
			notificacio.updateErrorNotifica(
					true,
					event);
			notificacioEventRepository.save(event);
		}
		return NotificacioEstatEnumDto.ENVIADA.equals(notificacio.getEstat());
	}

	public NotificaRespostaEstatDto refrescarEstat(
			NotificacioEnviamentEntity enviament) throws SistemaExternException {
		NotificaRespostaDatatDto respostaDatat = enviamentInfo(enviament);
		NotificaRespostaEstatDto resposta = new NotificaRespostaEstatDto();
		resposta.setData(respostaDatat.getDataActualitzacio());
		resposta.setEstatCodi(respostaDatat.getEstatActual());
		resposta.setEstatDescripcio(respostaDatat.getEstatActualDescripcio());
		resposta.setNumSeguiment(respostaDatat.getNumSeguiment());
		if (isEstatFinal(respostaDatat.getEstatActual())) {
			//NotificaRespostaCertificacioDto respostaCertificacio = enviamentCertificacio(destinatari);
			enviamentCertificacio(destinatari);
			// TODO
		}
		return resposta;
	}



	private AltaRemesaEnvios generarAltaRemesaEnvios(
			NotificacioEntity notificacio) throws GeneralSecurityException, DatatypeConfigurationException {
		AltaRemesaEnvios envios = new AltaRemesaEnvios();
		envios.setCodigoOrganismoEmisor(notificacio.getEmisorDir3Codi());
		switch (notificacio.getEnviamentTipus()) {
		case COMUNICACIO:
			envios.setTipoEnvio(new BigInteger("1"));
			break;
		case NOTIFICACIO:
			envios.setTipoEnvio(new BigInteger("2"));
			break;
		}
		envios.setFechaEnvioProgramado(
				toXmlGregorianCalendar(notificacio.getEnviamentDataProgramada()));
		envios.setConcepto(notificacio.getConcepte());
		envios.setDescripcion(notificacio.getDescripcio());
		envios.setProcedimiento(
				notificacio.getProcedimentCodiSia());
		Documento2 documento = new Documento2();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		pluginHelper.gestioDocumentalGet(
				notificacio.getDocumentArxiuId(),
				PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
				baos);
		documento.setContenido(Base64.encodeBase64(baos.toByteArray()));
		documento.setHash(notificacio.getDocumentHash());
		//documento.setEnlaceDocumento(value);
		//documento.setMetadatos(value);
		Opciones2 opcionesDocumento = new Opciones2();
		Opcion2 opcionNormalizado = new Opcion2();
		opcionNormalizado.setTipo("normalizado");
		opcionNormalizado.setValue(
				notificacio.isDocumentNormalitzat() ? "si" : "no"); // si o no
		opcionesDocumento.getOpcion().add(opcionNormalizado);
		Opcion2 opcionGenerarCsv = new Opcion2();
		opcionGenerarCsv.setTipo("generarCsv");
		opcionGenerarCsv.setValue(
				notificacio.isDocumentGenerarCsv() ? "si" : "no"); // si o no
		opcionesDocumento.getOpcion().add(opcionGenerarCsv);
		documento.setOpcionesDocumento(opcionesDocumento);
		envios.setDocumento(documento);
		envios.setEnvios(generarEnvios(notificacio));
		Opciones2 opcionesRemesa = new Opciones2();
		if (notificacio.getRetardPostal() != null) {
			Opcion2 opcionRetardo = new Opcion2();
			opcionRetardo.setTipo("retardo");
			opcionRetardo.setValue(
					notificacio.getRetardPostal().toString()); // número de días
			opcionesRemesa.getOpcion().add(opcionRetardo);
		}
		if (notificacio.getCaducitat() != null) {
			Opcion2 opcionCaducidad = new Opcion2();
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
			Envio envio = new Envio();
			envio.setReferenciaEmisor(enviament.getNotificaReferencia());
			EntregaDEH2 entregaDeh = new EntregaDEH2();
			entregaDeh.setCodigoProcedimiento(
					notificacio.getProcedimentCodiSia());
			entregaDeh.setObligado(enviament.getDehObligat());
			envio.setEntregaDEH(entregaDeh);
			Persona2 titular = new Persona2();
			titular.setNif(enviament.getTitularNif());
			titular.setNombre(enviament.getTitularNom());
			titular.setApellidos(
					concatenarLlinatges(
							enviament.getTitularLlinatge1(),
							enviament.getTitularLlinatge2()));
			titular.setTelefono(enviament.getTitularTelefon());
			titular.setEmail(enviament.getTitularEmail());
			titular.setRazonSocial(enviament.getTitularRaoSocial());
			titular.setCodigoDestino(enviament.getTitularCodiDesti());
			envio.setTitular(titular);
			Destinatarios2 destinatarios = new Destinatarios2();
			Persona2 destinatario = new Persona2();
			destinatario.setNif(enviament.getDestinatariNif());
			destinatario.setNombre(enviament.getDestinatariNom());
			destinatario.setApellidos(
					concatenarLlinatges(
							enviament.getDestinatariLlinatge1(),
							enviament.getDestinatariLlinatge2()));
			destinatario.setTelefono(enviament.getDestinatariTelefon());
			destinatario.setEmail(enviament.getDestinatariEmail());
			destinatario.setRazonSocial(enviament.getDestinatariRaoSocial());
			destinatario.setCodigoDestino(enviament.getDestinatariCodiDesti());
			destinatarios.getDestinatario().add(destinatario);
			envio.setDestinatarios(destinatarios);
			EntregaPostal2 entregaPostal = new EntregaPostal2();
			OrganismoPagadorPostal2 pagadorPostal = new OrganismoPagadorPostal2();
			pagadorPostal.setCodigoDIR3Postal(notificacio.getPagadorCorreusCodiDir3());
			pagadorPostal.setCodClienteFacturacionPostal(notificacio.getPagadorCorreusCodiClientFacturacio());
			pagadorPostal.setNumContratoPostal(notificacio.getPagadorCorreusContracteNum());
			pagadorPostal.setFechaVigenciaPostal(
					toXmlGregorianCalendar(notificacio.getPagadorCieDataVigencia()));
			entregaPostal.setOrganismoPagadorPostal(pagadorPostal);
			OrganismoPagadorCIE2 pagadorCie = new OrganismoPagadorCIE2();
			pagadorCie.setCodigoDIR3CIE(notificacio.getPagadorCieCodiDir3());
			pagadorCie.setFechaVigenciaCIE(
					toXmlGregorianCalendar(notificacio.getPagadorCieDataVigencia()));
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
			Opciones2 opcionesCie = new Opciones2();
			if (enviament.getDomiciliCie() != null) {
				Opcion2 opcionCie = new Opcion2();
				opcionCie.setTipo("cie");
				opcionCie.setValue(enviament.getDomiciliCie().toString()); // identificador CIE
				opcionesCie.getOpcion().add(opcionCie);
			}
			if (enviament.getFormatSobre() != null) {
				Opcion2 opcionFormatoSobre = new Opcion2();
				opcionFormatoSobre.setTipo("formatoSobre");
				opcionFormatoSobre.setValue(enviament.getFormatSobre()); // americano, C5...
				opcionesCie.getOpcion().add(opcionFormatoSobre);
			}
			if (enviament.getFormatFulla() != null) {
				Opcion2 opcionFormatoHoja = new Opcion2();
				opcionFormatoHoja.setTipo("formatoHoja");
				opcionFormatoHoja.setValue(enviament.getFormatFulla()); // A4, A5...
				opcionesCie.getOpcion().add(opcionFormatoHoja);
			}
			entregaPostal.setOpcionesCIE(opcionesCie);
			envio.setEntregaPostal(entregaPostal);
			envios.getEnvio().add(envio);
		}
		return envios;
	}

	private NotificaRespostaDatatDto enviamentInfo(
			NotificacioEnviamentEntity enviament) {
		NotificacioEntity notificacio = enviament.getNotificacio();
		String errorPrefix = "Error al consultar l'estat d'un enviament fet amb NotificaV2 (" +
				"notificacioId=" + notificacio.getId() + ", " +
				"notificaIdentificador=" + enviament.getNotificaIdentificador() + ")";
		ResultadoInfoEnvioV2 resultadoInfoEnvio = null;
		try {
			InfoEnvioV2 infoEnvio = new InfoEnvioV2();
			infoEnvio.setIdentificador(enviament.getNotificaIdentificador());
			resultadoInfoEnvio = getNotificaWs().infoEnvioV2(infoEnvio);
			// TODO Validar info
			/*resultadoInfoEnvio.getIdentificador();
			resultadoInfoEnvio.getEstado();
			resultadoInfoEnvio.getConcepto();
			resultadoInfoEnvio.getDescripcion();
			resultadoInfoEnvio.getCodigoOrganismoEmisor();
			resultadoInfoEnvio.getCodigoOrganismoEmisorRaiz();
			resultadoInfoEnvio.getTipoEnvio();
			resultadoInfoEnvio.getFechaCreacion();
			resultadoInfoEnvio.getFechaPuestaDisposicion();
			resultadoInfoEnvio.getFechaCaducidad();
			resultadoInfoEnvio.getRetardo();
			resultadoInfoEnvio.getProcedimiento();
			resultadoInfoEnvio.getDocumento();
			resultadoInfoEnvio.getReferenciaEmisor();
			resultadoInfoEnvio.getTitular();
			resultadoInfoEnvio.getDestinatarios();
			resultadoInfoEnvio.getEntregaPostal();
			resultadoInfoEnvio.getEntregaDEH();
			resultadoInfoEnvio.getDatados();
			resultadoInfoEnvio.getCertificacion();
			resultadoInfoEnvio.getOpcionesEnvio();*/
			NotificaRespostaDatatDto resposta = new NotificaRespostaDatatDto();
			resposta.setIdentificador(resultadoInfoEnvio.getIdentificador());
			resposta.setReferenciaEmisor(resultadoInfoEnvio.getReferenciaEmisor());
			resposta.setTitularNif(resultadoInfoEnvio.getTitular().getNif());
			if (resultadoInfoEnvio.getDatados() != null) {
				Datado datatDarrer = null;
				List<NotificaRespostaDatatEventDto> events = new ArrayList<NotificaRespostaDatatEventDto>();
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
					events.add(event);
				}
				if (datatDarrer != null) {
					NotificacioDestinatariEstatEnumDto estat = getEstatNotifica(
							datatDarrer.getResultado());
					resposta.setEstatActual(datatDarrer.getResultado());
					resposta.setDataActualitzacio(toDate(datatDarrer.getFecha()));
					resposta.setEvents(events);
					enviament.updateNotificaInfo(
							toDate(datatDarrer.getFecha()),
							estat,
							datatDarrer.getOrigen(),
							datatDarrer.getNifReceptor(),
							datatDarrer.getNombreReceptor(),
							datatDarrer.getDescripcionError());
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
				Certificacion certificacion = resultadoInfoEnvio.getCertificacion();
				byte[] decodificat = Base64.decodeBase64(certificacion.getContenido());
				String gestioDocumentalId = pluginHelper.gestioDocumentalCreate(
						PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS,
						new ByteArrayInputStream(decodificat));
				enviament.updateNotificaCertificacio(
						toDate(certificacion.getFechaCertificacion()),
						gestioDocumentalId,
						certificacion.getHash(),
						certificacion.getOrigen(),
						certificacion.getMetadatos(),
						certificacion.getCsv(),
						certificacion.getMime(),
						Integer.parseInt(certificacion.getSize()),
						null,
						null,
						null);
			}
			NotificacioEventEntity event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_INFO,
					notificacio).
					enviament(enviament).
					build();
			notificacio.updateEventAfegir(event);
			return resposta;
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
			throw new SistemaExternException(
					"NOTIFICA",
					errorPrefix,
					ex);
		}
	}

	private NotificaWsV2PortType getNotificaWs() throws InstanceNotFoundException, MalformedObjectNameException, MalformedURLException, RemoteException, NamingException, CreateException {
		NotificaWsV2PortType port = new WsClientHelper<NotificaWsV2PortType>().generarClientWs(
				getClass().getResource("/es/caib/notib/core/wsdl/NotificaWSV2.wsdl"),
				getUrlProperty(),
				new QName(
						"https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/", 
						"NotificaWsService"),
				getUsernameProperty(),
				getPasswordProperty(),
				NotificaWsV2PortType.class,
				new ApiKeySOAPHandler(getApiKeyProperty()),
				/*new FirmaSOAPHandler(
						getKeystorePathProperty(),
						getKeystoreTypeProperty(),
						getKeystorePasswordProperty(),
						getKeystoreCertAliasProperty(),
						getKeystoreCertPasswordProperty()),*/
//				new ChunkedSOAPHandler("false"),
				new WsClientHelper.SOAPLoggingHandler());
		return port;
	}

	private static final Logger logger = LoggerFactory.getLogger(NotificaV2Helper.class);

}
