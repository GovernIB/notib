/**
 * 
 */
package es.caib.notib.core.helper;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.security.GeneralSecurityException;
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

import es.caib.notib.core.api.dto.NotificaDomiciliViaTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaRespostaEstatDto;
import es.caib.notib.core.api.dto.NotificacioDestinatariEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.exception.SistemaExternException;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
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
public abstract class AbstractNotificaHelper {

	@Autowired
	private NotificacioEnviamentRepository notificacioDestinatariRepository;
	@Autowired
	private NotificacioEventRepository notificacioEventRepository;

	private boolean modeTest;



	public abstract boolean enviament(
			Long notificacioId);

	public abstract NotificaRespostaEstatDto refrescarEstat(
			NotificacioEnviamentEntity destinatari) throws SistemaExternException;

	@Transactional
	public boolean comunicacioSeu(
			Long notificacioDestinatariId) {
		NotificacioEnviamentEntity enviament = notificacioDestinatariRepository.findOne(
				notificacioDestinatariId);
		NotificacioEntity notificacio = enviament.getNotificacio();
		NotificacioEventEntity event;
		boolean error = false;
		try {
			ComunicacionSede comunicacionSede = new ComunicacionSede();
			comunicacionSede.setIdentificadorDestinatario(enviament.getNotificaIdentificador());
			comunicacionSede.setFecha(
					toXmlGregorianCalendar(enviament.getSeuDataFi()));
			comunicacionSede.setOrganismoRemisor(notificacio.getEntitat().getDir3Codi());
			ResultadoComunicacionSede resultadoComunicacion = getSedeWs().comunicacionSede(comunicacionSede);
			if ("000".equals(resultadoComunicacion.getCodigoRespuesta())) {
				event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.SEU_NOTIFICA_COMUNICACIO,
						notificacio).
						enviament(enviament).
						build();
				notificacioEventRepository.save(event);
				enviament.updateNotificaError(
						false,
						null);
			} else {
				event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.SEU_NOTIFICA_COMUNICACIO,
						notificacio).
						enviament(enviament).
						error(true).
						errorDescripcio("Error retornat per notifica: [" + resultadoComunicacion.getCodigoRespuesta() + "] " + resultadoComunicacion.getDescripcionRespuesta()).
						build();
				enviament.updateNotificaError(
						true,
						event);
				notificacioEventRepository.save(event);
				error = true;
			}
		} catch (Exception ex) {
			logger.error(
					"Error al comunicar el canvi d'estat d'una notificació de la seu a Notifica (" +
					"notificacioId=" + notificacio.getId() + ", " +
					"notificaIdentificador=" + enviament.getNotificaIdentificador() + ")",
					ex);
			event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.SEU_NOTIFICA_COMUNICACIO,
					notificacio).
					enviament(enviament).
					error(true).
					errorDescripcio(ExceptionUtils.getStackTrace(ex)).
					build();
			enviament.updateNotificaError(
					true,
					event);
			notificacioEventRepository.save(event);
			error = true;
		}
		enviament.updateSeuNotificaInformat();
		notificacio.updateEventAfegir(event);
		return !error;
	}

	@Transactional
	public void certificacioSeu(
			NotificacioEnviamentEntity enviament,
			byte[] document) {
		NotificacioEntity notificacio = enviament.getNotificacio();
		NotificacioEventEntity event;
		try {
			CertificacionSede certificacionSede = new CertificacionSede();
			certificacionSede.setEnvioDestinatario(enviament.getNotificaIdentificador());
			if (NotificacioDestinatariEstatEnumDto.LLEGIDA.equals(enviament.getSeuEstat())) {
				certificacionSede.setEstado("notificada");
			} else if (NotificacioDestinatariEstatEnumDto.REBUTJADA.equals(enviament.getSeuEstat())) {
				certificacionSede.setEstado("rehusada");
			}
			certificacionSede.setFecha(
					toXmlGregorianCalendar(enviament.getSeuDataFi()));
			certificacionSede.setDocumento(
					Base64.encodeBase64String(document));
			certificacionSede.setHashDocumento(
					Base64.encodeBase64String(
							Hex.decodeHex(
									DigestUtils.sha1Hex(document).toCharArray())));
			//certificacionSede.setCsv(value);
			certificacionSede.setOrganismoRemisor(notificacio.getEntitat().getDir3Codi());
			ResultadoCertificacionSede resultadoCertificacion = getSedeWs().certificacionSede(certificacionSede);
			if ("000".equals(resultadoCertificacion.getCodigoRespuesta())) {
				event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.SEU_NOTIFICA_CERTIFICACIO,
						notificacio).
						enviament(enviament).
						build();
				notificacioEventRepository.save(event);
			} else {
				event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.SEU_NOTIFICA_CERTIFICACIO,
						notificacio).
						enviament(enviament).
						error(true).
						errorDescripcio("Error retornat per notifica: [" + resultadoCertificacion.getCodigoRespuesta() + "] " + resultadoCertificacion.getDescripcionRespuesta()).
						build();
				enviament.updateSeuError(
						true,
						event,
						true);
				notificacioEventRepository.save(event);
			}
		} catch (Exception ex) {
			logger.error(
					"Error al enviar la certificació d'una notificació de la seu a Notifica (" +
					"notificacioId=" + notificacio.getId() + ", " +
					"notificaIdentificador=" + enviament.getNotificaIdentificador() + ")",
					ex);
			event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.SEU_NOTIFICA_CERTIFICACIO,
					notificacio).
					enviament(enviament).
					error(true).
					errorDescripcio(ExceptionUtils.getStackTrace(ex)).
					build();
			enviament.updateSeuError(
					true,
					event,
					true);
			notificacioEventRepository.save(event);
		}
		enviament.updateSeuNotificaInformat();
		notificacio.updateEventAfegir(event);
	}

	public String generarReferencia(NotificacioEnviamentEntity notificacioDestinatari) throws GeneralSecurityException {
		return xifrarIdPerNotifica(notificacioDestinatari.getId());
	}

	public boolean isAdviserActiu() {
		String actiu = PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.adviser.actiu");
		if (actiu != null) {
			return new Boolean(actiu).booleanValue();
		} else {
			return true;
		}
	}

	public boolean isConnexioNotificaDisponible() {
		return getUrlProperty() != null && getApiKeyProperty() != null;
	}

	public void setModeTest(boolean modeTest) {
		this.modeTest = modeTest;
	}



	private static final String[] estatsNotifica = new String[] {
			"ausente",
			"desconocido",
			"direccion_incorrecta",
			"enviado_deh",
			"enviado_ci",
			"entregado_op",
			"leida",
			"error",
			"extraviada",
			"fallecido",
			"notificada",
			"pendiente_envio",
			"pendiente_cie",
			"pendiente_deh",
			"pendiente_sede",
			"rehusada",
			"expirada",
			"envio_programado",
			"sin_informacion"};
	private static final NotificacioDestinatariEstatEnumDto[] estatsNotib = new NotificacioDestinatariEstatEnumDto[] {
			NotificacioDestinatariEstatEnumDto.ABSENT,
			NotificacioDestinatariEstatEnumDto.DESCONEGUT,
			NotificacioDestinatariEstatEnumDto.ADRESA_INCORRECTA,
			NotificacioDestinatariEstatEnumDto.ENVIADA_DEH,
			NotificacioDestinatariEstatEnumDto.ENVIADA_CI,
			NotificacioDestinatariEstatEnumDto.ENTREGADA_OP,
			NotificacioDestinatariEstatEnumDto.LLEGIDA,
			NotificacioDestinatariEstatEnumDto.ERROR_ENTREGA,
			NotificacioDestinatariEstatEnumDto.EXTRAVIADA,
			NotificacioDestinatariEstatEnumDto.MORT,
			NotificacioDestinatariEstatEnumDto.NOTIFICADA,
			NotificacioDestinatariEstatEnumDto.PENDENT_ENVIAMENT,
			NotificacioDestinatariEstatEnumDto.PENDENT_CIE,
			NotificacioDestinatariEstatEnumDto.PENDENT_DEH,
			NotificacioDestinatariEstatEnumDto.PENDENT_SEU,
			NotificacioDestinatariEstatEnumDto.REBUTJADA,
			NotificacioDestinatariEstatEnumDto.EXPIRADA,
			NotificacioDestinatariEstatEnumDto.ENVIAMENT_PROGRAMAT,
			NotificacioDestinatariEstatEnumDto.SENSE_INFORMACIO};
	protected NotificacioDestinatariEstatEnumDto getEstatNotifica(
			String estatCodi) {
		for (int i = 0; i < estatsNotifica.length; i++) {
			if (estatCodi.equalsIgnoreCase(estatsNotifica[i])) {
				return estatsNotib[i];
			}
		}
		return null;
	}
	protected boolean isEstatFinal(
			String estatCodi) {
		boolean[] esFinal = new boolean[] {
			true,
			true,
			true,
			false,
			false,
			false,
			true,
			true,
			true,
			true,
			true,
			false,
			false,
			false,
			false,
			true,
			true,
			false,
			false};
		for (int i = 0; i < estatsNotifica.length; i++) {
			if (estatCodi.equalsIgnoreCase(estatsNotifica[i])) {
				return esFinal[i];
			}
		}
		return false;
	}

	protected String viaTipusToString(NotificaDomiciliViaTipusEnumDto viaTipus) {
		if (viaTipus != null) {
			switch (viaTipus) {
			case ALAMEDA:
				return "ALMDA";
			case AVENIDA:
				return "AVDA";
			case AVINGUDA:
				return "AVGDA";
			case BARRIO:
				return "BAR";
			case BULEVAR:
				return "BVR";
			case CALLE:
				return "CALLE";
			case CALLEJA:
				return "CJA";
			case CAMI:
				return "CAMÍ";
			case CAMINO:
				return "CAMNO";
			case CAMPO:
				return "CAMPO";
			case CARRER:
				return "CARR";
			case CARRERA:
				return "CRA";
			case CARRETERA:
				return "CTRA";
			case CUESTA:
				return "CSTA";
			case EDIFICIO:
				return "EDIF";
			case ENPARANTZA:
				return "EPTZA";
			case ESTRADA:
				return "ESTR";
			case GLORIETA:
				return "GTA";
			case JARDINES:
				return "JARD";
			case JARDINS:
				return "JARDI";
			case KALEA:
				return "KALEA";
			case OTROS:
				return "OTROS";
			case PARQUE:
				return "PRQUE";
			case PASAJE:
				return "PSJ";
			case PASEO:
				return "PASEO";
			case PASSATGE:
				return "PASTG";
			case PASSEIG:
				return "PSG";
			case PLACETA:
				return "PLCTA";
			case PLAZA:
				return "PLAZA";
			case PLAZUELA:
				return "PLZA";
			case PLAÇA:
				return "PLAÇA";
			case POBLADO:
				return "POBL";
			case POLIGONO:
				return "POLIG";
			case PRAZA:
				return "PRAZA";
			case RAMBLA:
				return "RAMBL";
			case RONDA:
				return "RONDA";
			case RUA:
				return "RÚA";
			case SECTOR:
				return "SECT";
			case TRAVESIA:
				return "TRAV";
			case TRAVESSERA:
				return "TRAVS";
			case URBANIZACION:
				return "URB";
			case VIA:
				return "VIA";
			default:
				return null;
			}
		} else {
			return null;
		}
	}

	protected XMLGregorianCalendar toXmlGregorianCalendar(Date date) throws DatatypeConfigurationException {
		if (date == null) {
			return null;
		}
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
	}
	protected Date toDate(XMLGregorianCalendar calendar) throws DatatypeConfigurationException {
		if (calendar == null) {
			return null;
		}
		return calendar.toGregorianCalendar().getTime();
	}

	protected String xifrarIdPerNotifica(Long id) throws GeneralSecurityException {
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
		return new String(Base64.encodeBase64(xifrat));
	}
	protected Long desxifrarIdPerNotifica(String idXifrat) throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("RC4");
		SecretKeySpec rc4Key = new SecretKeySpec(getClauXifratIdsProperty().getBytes(),"RC4");
		cipher.init(Cipher.DECRYPT_MODE, rc4Key);
		byte[] desxifrat = cipher.doFinal(Base64.decodeBase64(idXifrat.getBytes()));
		return new Long(bytesToLong(desxifrat));
	}

	protected byte[] longToBytes(long l) {
		byte[] result = new byte[Long.SIZE / Byte.SIZE];
	    for (int i = 7; i >= 0; i--) {
	        result[i] = (byte)(l & 0xFF);
	        l >>= 8;
	    }
	    return result;
	}
	protected long bytesToLong(byte[] b) {
		long result = 0;
	    for (int i = 0; i < 8; i++) {
	        result <<= 8;
	        result |= (b[i] & 0xFF);
	    }
	    return result;
	}

	protected String concatenarLlinatges(
			String llinatge1,
			String llinatge2) {
		if (llinatge1 == null && llinatge2 == null) {
			return null;
		}
		StringBuilder llinatges = new StringBuilder();
		llinatges.append(llinatge1.trim());
		if (llinatge2 != null && !llinatge2.trim().isEmpty()) {
			llinatges.append(" ");
			llinatges.append(llinatge2);
		}
		return llinatges.toString();
	}
	/*private String[] separarLlinatges(
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
	}*/

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

	protected String getUrlProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.url");
	}
	protected String getUsernameProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.username");
	}
	protected String getPasswordProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.password");
	}
	protected String getClauXifratIdsProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.clau.xifrat.ids",
				"P0rt4FI8");
	}
	protected String getApiKeyProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.apikey");
	}
	/*private String getKeystorePathProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.keystore.path");
	}
	private String getKeystoreTypeProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.keystore.type");
	}
	private String getKeystorePasswordProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.keystore.password");
	}
	private String getKeystoreCertAliasProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.keystore.cert.alias");
	}
	private String getKeystoreCertPasswordProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.keystore.cert.password");
	}
	private Boolean useNotificaAdviser() {
		String useAdviser = PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.use.adviser");
		return "true".equalsIgnoreCase(useAdviser);
	}
	private Boolean sendToNotificaOnAlta() {
		String sendToNotifica = PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.send.alta");
		return !"false".equalsIgnoreCase(sendToNotifica);
	}*/

	public class ApiKeySOAPHandler implements SOAPHandler<SOAPMessageContext> {
		private final String apiKey;
		public ApiKeySOAPHandler(String apiKey) {
			this.apiKey = apiKey;
		}
		@Override
		public boolean handleMessage(SOAPMessageContext context) {
			Boolean outboundProperty = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (outboundProperty.booleanValue()) {
				
				try {
					SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();
					SOAPFactory factory = SOAPFactory.newInstance();
					SOAPElement apiKeyElement = factory.createElement(
							new QName(
									"https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/", 
									"api_key"));
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
	
	public class ChunkedSOAPHandler implements SOAPHandler<SOAPMessageContext> {
		private final String chunked;
		public ChunkedSOAPHandler(String chunked) {
			this.chunked = chunked;
		}
		@Override
		public boolean handleMessage(SOAPMessageContext context) {
			context.put("__CHUNKED__", chunked);
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

	private static final Logger logger = LoggerFactory.getLogger(AbstractNotificaHelper.class);

}
