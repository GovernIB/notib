/**
 * 
 */
package es.caib.notib.core.helper;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.activation.DataHandler;
import javax.ejb.CreateException;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.naming.NamingException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jersey.core.impl.provider.entity.DataSourceProvider.ByteArrayDataSource;

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
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.repository.NotificacioRepository;
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
	private PluginHelper pluginHelper;


	public ResultadoAltaRemesaEnvios enviaNotificacio(
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
	
	@Transactional
	public boolean enviament(
			Long notificacioId) {
		NotificacioEntity notificacio = notificacioRepository.findOne(notificacioId);
		try {
			ResultadoAltaRemesaEnvios resultadoAlta = enviaNotificacio(notificacio);
			
			if ("000".equals(resultadoAlta.getCodigoRespuesta()) && "OK".equalsIgnoreCase(resultadoAlta.getDescripcionRespuesta())) {
				int i = 0;
				for (ResultadoEnvio resultadoEnvio: resultadoAlta.getResultadoEnvios().getItem()) {
					NotificacioEnviamentEntity enviament = notificacio.getEnviaments().get(i++);
//					for (NotificacioEnviamentEntity enviament: notificacio.getEnviaments()) {
//						if (enviament.getNotificaReferencia().equals(resultadoEnvio.getIdentificador())) {
					enviament.updateNotificaIdentificador(
							resultadoEnvio.getIdentificador());
					enviament.updateNotificaEstat(
							new Date(),
							NotificacioDestinatariEstatEnumDto.NOTIB_ENVIADA,
							true);
//						}
//					}
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
			notificacio.updateErrorNotifica(
					true,
					event);
			notificacioEventRepository.save(event);
			for (NotificacioEnviamentEntity enviament: notificacio.getEnviaments()) {
				enviament.updateNotificaError(true, event);
			}
		}
		return NotificacioEstatEnumDto.ENVIADA.equals(notificacio.getEstat());
	}

	public ResultadoInfoEnvioV2 infoEnviament(
			NotificacioEnviamentEntity enviament) throws Exception {
		
		ResultadoInfoEnvioV2 resultat = null;
		
//		NotificacioEntity notificacio = enviament.getNotificacio();
		
		try {
			InfoEnvioV2 infoEnvio = new InfoEnvioV2();
			infoEnvio.setIdentificador(enviament.getNotificaIdentificador());
			resultat = getNotificaWs().infoEnvioV2(infoEnvio);
		} catch (SOAPFaultException sfe) {
			String codiResposta = sfe.getFault().getFaultCode();
			String descripcioResposta = sfe.getFault().getFaultString();
			
			resultat = new ResultadoInfoEnvioV2();
			resultat.setConcepto(codiResposta);
			resultat.setDescripcion(descripcioResposta);
		}
		return resultat;
	}
	
	public NotificaRespostaEstatDto refrescarEstat(
			NotificacioEnviamentEntity enviament) throws SistemaExternException {
		NotificacioEntity notificacio = enviament.getNotificacio();
		String errorPrefix = "Error al consultar l'estat d'un enviament fet amb NotificaV2 (" +
				"notificacioId=" + notificacio.getId() + ", " +
				"notificaIdentificador=" + enviament.getNotificaIdentificador() + ")";
		ResultadoInfoEnvioV2 resultadoInfoEnvio = null;
		try {
			InfoEnvioV2 infoEnvio = new InfoEnvioV2();
			infoEnvio.setIdentificador(enviament.getNotificaIdentificador());
			
//	        Holder<String> identificador = new Holder<String>();
//	        Holder<String> estado = new Holder<String>();
//	        Holder<String> concepto = new Holder<String>();
//	        Holder<String> descripcion = new Holder<String>();
//	        Holder<CodigoDIR> codigoOrganismoEmisor = new Holder<CodigoDIR>();
//	        Holder<CodigoDIR> codigoOrganismoEmisorRaiz = new Holder<CodigoDIR>();
//	        Holder<String> tipoEnvio = new Holder<String>();
//	        Holder<XMLGregorianCalendar> fechaCreacion = new Holder<XMLGregorianCalendar>();
//	        Holder<XMLGregorianCalendar> fechaPuestaDisposicion = new Holder<XMLGregorianCalendar>();
//	        Holder<XMLGregorianCalendar> fechaCaducidad = new Holder<XMLGregorianCalendar>();
//	        Holder<BigInteger> retardo = new Holder<BigInteger>();
//	        Holder<Procedimiento> procedimiento = new Holder<Procedimiento>();
//	        Holder<Documento> documento = new Holder<Documento>();
//	        Holder<String> referenciaEmisor = new Holder<String>();
//	        Holder<Persona> titular = new Holder<Persona>();
//	        Holder<Destinatarios> destinatarios = new Holder<Destinatarios>();
//	        Holder<EntregaPostal> entregaPostal = new Holder<EntregaPostal>();
//	        Holder<EntregaDEH> entregaDEH = new Holder<EntregaDEH>();
//	        Holder<Datados> datados = new Holder<Datados>();
//	        Holder<Certificacion> certificacion = new Holder<Certificacion>();
//	        Holder<Opciones> opcionesEnvio = new Holder<Opciones>();
	        
			resultadoInfoEnvio = getNotificaWs().infoEnvioV2(
//					identificador, 
//					estado, 
//					concepto, 
//					descripcion, 
//					codigoOrganismoEmisor, 
//					codigoOrganismoEmisorRaiz, 
//					tipoEnvio, 
//					fechaCreacion, 
//					fechaPuestaDisposicion, 
//					fechaCaducidad, 
//					retardo, 
//					procedimiento, 
//					documento, 
//					referenciaEmisor, 
//					titular, 
//					destinatarios, 
//					entregaPostal, 
//					entregaDEH, 
//					datados, 
//					certificacion, 
//					opcionesEnvio);
					infoEnvio);
			NotificaRespostaEstatDto resposta = new NotificaRespostaEstatDto();
			if (resultadoInfoEnvio.getDatados() != null) {
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
					NotificaRespostaDatatEventDto event = new NotificaRespostaDatatEventDto();
					event.setData(datatData);
					event.setEstat(datado.getResultado());
				}
				if (datatDarrer != null) {
					NotificacioDestinatariEstatEnumDto estat = getEstatNotifica(
							datatDarrer.getResultado());
					resposta.setData(toDate(datatDarrer.getFecha()));
					resposta.setEstatCodi(datatDarrer.getResultado());
					resposta.setEstatDescripcio(null);
					resposta.setNumSeguiment(null);
					resposta.setOrigen(datatDarrer.getOrigen());
					resposta.setReceptorNom(datatDarrer.getNombreReceptor());
					resposta.setReceptorNif(datatDarrer.getNifReceptor());
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
				Certificacion certificacio = resultadoInfoEnvio.getCertificacion();
				byte[] decodificat = Base64.decodeBase64(certificacio.getContenidoCertificacion());
//				byte[] decodificat = Base64.decodeBase64(certificacio.getContenidoCertificacion().getValue());
				
				String gestioDocumentalId = pluginHelper.gestioDocumentalCreate(
						PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS,
//						certificacio.getContenidoCertificacion().getHref().getInputStream());
						new ByteArrayInputStream(decodificat));
				enviament.updateNotificaCertificacio(
						toDate(certificacio.getFechaCertificacion()),
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
				resposta.setCertificacioDisponible(true);
				resposta.setCertificacioContingut(null);
				resposta.setCertificacioHash(certificacio.getHash());
				resposta.setCertificacioCsv(certificacio.getCsv());
				resposta.setCertificacioTamany(
						new Integer(certificacio.getSize()));
				resposta.setCertificacioData(
						toDate(certificacio.getFechaCertificacion()));
				resposta.setCertificacioOrigen(certificacio.getOrigen());
				resposta.setCertificacioMetadades(certificacio.getMetadatos());
				resposta.setCertificacioTipusMime(certificacio.getMime());
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
		Documento documento = new Documento();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		pluginHelper.gestioDocumentalGet(
				notificacio.getDocumentArxiuId(),
				PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
				baos);
		
		documento.setContenido(baos.toByteArray());
//		documento.setContenido(Base64.encodeBase64(baos.toByteArray()));
//		byte[] bDoc = baos.toByteArray();
////		InputStream bais = new ByteArrayInputStream(bDoc);
//		InputStream bais = new BufferedInputStream(new ByteArrayInputStream(bDoc));
//		Contenido2 contenido = new Contenido2();
//		DataHandler dataHandler = null;
//		String fileType = MimeTypeHelper.getContentTypeByFileName(notificacio.getDocumentArxiuNom());
//		try {
//			ByteArrayDataSource bads = new ByteArrayDataSource(bais, fileType);
//			bads.setName(notificacio.getDocumentArxiuNom());
//			dataHandler = new DataHandler(bads);
////			dataHandler = new DataHandler(new FileDataSource(new File("/home/siona/git/notib/notib-client/src/test/resources/es/caib/notib/client/notificacio_adjunt.pdf")));
//		} catch (IOException ex) {
////			ex.printStackTrace();
//			throw new SistemaExternException(
//					"NOTIFICA",
//					"Error al generar el document a enviar a Notific@",
//					ex);
//		}
//		contenido.setHref(dataHandler);
//		contenido.setValue(notificacio.getDocumentArxiuNom());
//		documento.setContenido(contenido);
		documento.setHash(notificacio.getDocumentHash());
		//documento.setEnlaceDocumento(value);
		//documento.setMetadatos(value);
		Opciones opcionesDocumento = new Opciones();
		Opcion opcionNormalizado = new Opcion();
		opcionNormalizado.setTipo("normalizado");
		opcionNormalizado.setValue(
				notificacio.isDocumentNormalitzat() ? "si" : "no"); // si o no
		opcionesDocumento.getOpcion().add(opcionNormalizado);
		Opcion opcionGenerarCsv = new Opcion();
		opcionGenerarCsv.setTipo("generarCsv");
		opcionGenerarCsv.setValue(
				notificacio.isDocumentGenerarCsv() ? "si" : "no"); // si o no
		opcionesDocumento.getOpcion().add(opcionGenerarCsv);
		documento.setOpcionesDocumento(opcionesDocumento);
		envios.setDocumento(documento);
		envios.setEnvios(generarEnvios(notificacio));
		Opciones opcionesRemesa = new Opciones();
		if (notificacio.getRetardPostal() != null) {
			Opcion opcionRetardo = new Opcion();
			opcionRetardo.setTipo("retardo");
			opcionRetardo.setValue(
					notificacio.getRetardPostal().toString()); // número de días
			opcionesRemesa.getOpcion().add(opcionRetardo);
		}
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
				Destinatarios destinatarios = new Destinatarios();
				Persona destinatario = new Persona();
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
				if (enviament.getDomiciliConcretTipus() != null) {
					EntregaPostal entregaPostal = new EntregaPostal();
					OrganismoPagadorPostal pagadorPostal = new OrganismoPagadorPostal();
					pagadorPostal.setCodigoDIR3Postal(notificacio.getPagadorCorreusCodiDir3());
					pagadorPostal.setCodClienteFacturacionPostal(notificacio.getPagadorCorreusCodiClientFacturacio());
					pagadorPostal.setNumContratoPostal(notificacio.getPagadorCorreusContracteNum());
					pagadorPostal.setFechaVigenciaPostal(
							toXmlGregorianCalendar(notificacio.getPagadorCieDataVigencia()));
					entregaPostal.setOrganismoPagadorPostal(pagadorPostal);
					OrganismoPagadorCIE pagadorCie = new OrganismoPagadorCIE();
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
					entregaDeh.setCodigoProcedimiento(
							enviament.getDehProcedimentCodi());
					envio.setEntregaDEH(entregaDeh);
				}
				envios.getEnvio().add(envio);
			}
		}
		return envios;
	}

//	private NotificaWsV2PortType getNotificaWs() throws InstanceNotFoundException, MalformedObjectNameException, MalformedURLException, RemoteException, NamingException, CreateException {
//		return getNotificaWs(null, null);
//	}

	private NotificaWsV2PortType getNotificaWs() throws InstanceNotFoundException, MalformedObjectNameException, MalformedURLException, RemoteException, NamingException, CreateException {
//			String documentId, String fileType
		NotificaWsV2PortType port = new WsClientHelper<NotificaWsV2PortType>().generarClientWs(
				getClass().getResource("/es/caib/notib/core/wsdl/NotificaWsV21.wsdl"),
				getUrlProperty(),
				new QName(
						"https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/", 
						"NotificaWsV2Service"),
				getUsernameProperty(),
				getPasswordProperty(),
				NotificaWsV2PortType.class,
				new ApiKeySOAPHandlerV2(getApiKeyProperty()),
//				new ChunkedSOAPHandler("false"),
//				new DocumentHandler(documentId, fileType),
				new WsClientHelper.SOAPLoggingHandler(NotificaWsV2PortType.class));
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
				
				Map<String, List<String>> requestHeaders = (Map<String, List<String>>) context.get(MessageContext.HTTP_REQUEST_HEADERS);
				if (requestHeaders == null) {
	                requestHeaders = new HashMap<String, List<String>>();
	                context.put(MessageContext.HTTP_REQUEST_HEADERS, requestHeaders);
	            }
//				requestHeaders.put(RequestMyService.MY_CONSTANT, Collections.singletonList(something));
				
				try {
					SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();
					SOAPFactory factory = SOAPFactory.newInstance();
					SOAPElement apiKeyElement = factory.createElement(
							new QName(
									"https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/", 
									"apiKey"));
					apiKeyElement.addTextNode(apiKey);
					SOAPHeader header = envelope.getHeader();
					if (header == null)
						header = envelope.addHeader();
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
	
	public class DocumentHandler implements SOAPHandler<SOAPMessageContext> {
		private final String documentId;
		private final String fileType;
		public DocumentHandler(String documentId, String fileType) {
			this.documentId = documentId;
			this.fileType = fileType;
		}
		@Override
		public boolean handleMessage(SOAPMessageContext context) {
			if (documentId != null) {
				Boolean outboundProperty = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
				if (outboundProperty.booleanValue()) {
					
					try {
						SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();
//						SOAPFactory factory = SOAPFactory.newInstance();
//						SOAPElement apiKeyElement = factory.createElement(
	//							new QName(
	//									"https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/", 
	//									"apiKey"));
	//					apiKeyElement.addTextNode(apiKey);
	//					SOAPHeader header = envelope.getHeader();
	//					if (header == null)
	//						header = envelope.addHeader();
	//					header.addChildElement(apiKeyElement);
						SOAPBody body = envelope.getBody();
						SOAPBodyElement soapBodyElement = (SOAPBodyElement)body.getChildElements().next();
						SOAPElement documentElement = (SOAPElement)soapBodyElement.getChildElements(
								new QName(
										"https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios",
										"documento")).next();
						SOAPElement contenido = (SOAPElement)documentElement.getChildElements(
								new QName(
										"https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios",
										"contenido")).next();
						
						contenido.addAttribute(new QName("href"), "cid:" + documentId);
						contenido.setValue("");
						
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						pluginHelper.gestioDocumentalGet(
								documentId,
								PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
								baos);
	//					documento.setContenido(Base64.encodeBase64(baos.toByteArray()));
						byte[] bDoc = baos.toByteArray();
	//					InputStream bais = new ByteArrayInputStream(bDoc);
						InputStream bais = new BufferedInputStream(new ByteArrayInputStream(bDoc));
//						String fileType = MimeTypeHelper.getContentTypeByFileName(notificacio.getDocumentArxiuNom());
						ByteArrayDataSource bads = new ByteArrayDataSource(bais, fileType);
						bads.setName(documentId);
						DataHandler dataHandler = new DataHandler(bads);
	
		                SOAPMessage objSOAPMessage = context.getMessage();
		                AttachmentPart objAttachment = objSOAPMessage.createAttachmentPart(dataHandler);
		                objAttachment.setContentId(documentId);
		                objSOAPMessage.addAttachmentPart(objAttachment);
		                context.setMessage(objSOAPMessage);
		                objSOAPMessage.writeTo(System.out);
						
					} catch (Exception ex) {
						logger.error(
								"No s'ha pogut afegir el document a la petició SOAP per Notifica",
								ex);
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
	}

	private static final Logger logger = LoggerFactory.getLogger(NotificaV2Helper.class);

}
