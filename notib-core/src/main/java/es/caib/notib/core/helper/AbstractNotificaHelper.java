/**
 * 
 */
package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.NotificaDomiciliViaTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto;
import es.caib.notib.core.api.exception.SistemaExternException;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Mètodes comuns per a accedir a Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public abstract class AbstractNotificaHelper {
	
	@Autowired
	AuditNotificacioHelper auditNotificacioHelper;

	private boolean modeTest;
	
	public abstract NotificacioEntity notificacioEnviar(
			Long notificacioId);

	public abstract NotificacioEnviamentEntity enviamentRefrescarEstat(
			Long enviamentId) throws SistemaExternException;

	public String generarReferencia(NotificacioEnviamentEntity notificacioDestinatari) throws GeneralSecurityException {
		return xifrarId(notificacioDestinatari.getId());
	}

	public boolean isAdviserActiu() {
		String actiu = PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.adviser.actiu");
		if (actiu != null) {
			return new Boolean(actiu).booleanValue();
		} else {
			return true;
		}
	}

	public boolean isConnexioNotificaDisponible() {
		return getNotificaUrlProperty() != null;
	}

	public void setModeTest(boolean modeTest) {
		this.modeTest = modeTest;
	}

	public NotificacioEnviamentEntity enviamentUpdateDatat(
			NotificacioEnviamentEstatEnumDto notificaEstat,
			Date notificaEstatData,
			String notificaEstatDescripcio,
			String notificaDatatOrigen,
			String notificaDatatReceptorNif,
			String notificaDatatReceptorNom,
			String notificaDatatNumSeguiment,
			String notificaDatatErrorDescripcio,
			NotificacioEnviamentEntity enviament) {
		boolean estatFinal = 
				NotificacioEnviamentEstatEnumDto.ABSENT.equals(notificaEstat) ||
				NotificacioEnviamentEstatEnumDto.ADRESA_INCORRECTA.equals(notificaEstat) ||
				NotificacioEnviamentEstatEnumDto.ERROR_ENTREGA.equals(notificaEstat) ||
				NotificacioEnviamentEstatEnumDto.EXPIRADA.equals(notificaEstat) ||
				NotificacioEnviamentEstatEnumDto.EXTRAVIADA.equals(notificaEstat) ||
				NotificacioEnviamentEstatEnumDto.MORT.equals(notificaEstat) ||
				NotificacioEnviamentEstatEnumDto.LLEGIDA.equals(notificaEstat) ||
				NotificacioEnviamentEstatEnumDto.NOTIFICADA.equals(notificaEstat) ||
				NotificacioEnviamentEstatEnumDto.REBUTJADA.equals(notificaEstat) ||
				NotificacioEnviamentEstatEnumDto.DESCONEGUT.equals(notificaEstat) ||
				NotificacioEnviamentEstatEnumDto.SENSE_INFORMACIO.equals(notificaEstat);
		enviament.updateNotificaDatat(
				notificaEstat,
				notificaEstatData,
				estatFinal,
				notificaEstatDescripcio,
				notificaDatatOrigen,
				notificaDatatReceptorNif,
				notificaDatatReceptorNom,
				notificaDatatNumSeguiment,
				notificaDatatErrorDescripcio);
		boolean estatsEnviamentsFinals = true;
		Set<NotificacioEnviamentEntity> enviaments = enviament.getNotificacio().getEnviaments();
		for (NotificacioEnviamentEntity env: enviaments) {
			if (env.getId().equals(enviament.getId())) {
				env = enviament;
			}
			if (!env.isNotificaEstatFinal()) {
				estatsEnviamentsFinals = false;
				break;
			}
		}
		logger.info("Estat final: " + estatsEnviamentsFinals);
		if (estatsEnviamentsFinals) {
			auditNotificacioHelper.updateEstatNotificacio(notificaEstat.name(), enviament.getNotificacio());

//			//Marcar com a processada si la notificació s'ha fet des de una aplicació
//			if (enviament.getNotificacio() != null && enviament.getNotificacio().getTipusUsuari() == TipusUsuariEnumDto.APLICACIO) {
//				logger.info("Marcant notificació com processada per ser usuari aplicació...");
//				enviament.getNotificacio().updateEstat(NotificacioEstatEnumDto.PROCESSADA);
//				enviament.getNotificacio().updateMotiu(notificaEstat.name());
//				enviament.getNotificacio().updateEstatDate(new Date());
//			}
		}
		return enviament;
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
			"sin_informacion",
			"anulada"};
	private static final NotificacioEnviamentEstatEnumDto[] estatsNotib = new NotificacioEnviamentEstatEnumDto[] {
			NotificacioEnviamentEstatEnumDto.ABSENT,
			NotificacioEnviamentEstatEnumDto.DESCONEGUT,
			NotificacioEnviamentEstatEnumDto.ADRESA_INCORRECTA,
			NotificacioEnviamentEstatEnumDto.ENVIADA_DEH,
			NotificacioEnviamentEstatEnumDto.ENVIADA_CI,
			NotificacioEnviamentEstatEnumDto.ENTREGADA_OP,
			NotificacioEnviamentEstatEnumDto.LLEGIDA,
			NotificacioEnviamentEstatEnumDto.ERROR_ENTREGA,
			NotificacioEnviamentEstatEnumDto.EXTRAVIADA,
			NotificacioEnviamentEstatEnumDto.MORT,
			NotificacioEnviamentEstatEnumDto.NOTIFICADA,
			NotificacioEnviamentEstatEnumDto.PENDENT_ENVIAMENT,
			NotificacioEnviamentEstatEnumDto.PENDENT_CIE,
			NotificacioEnviamentEstatEnumDto.PENDENT_DEH,
			NotificacioEnviamentEstatEnumDto.PENDENT_SEU,
			NotificacioEnviamentEstatEnumDto.REBUTJADA,
			NotificacioEnviamentEstatEnumDto.EXPIRADA,
			NotificacioEnviamentEstatEnumDto.ENVIAMENT_PROGRAMAT,
			NotificacioEnviamentEstatEnumDto.SENSE_INFORMACIO,
			NotificacioEnviamentEstatEnumDto.ANULADA};
	protected NotificacioEnviamentEstatEnumDto getEstatNotifica(
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
		SimpleDateFormat sdfCaducitat = new SimpleDateFormat("yyyy-MM-dd");
		GregorianCalendar gc = new GregorianCalendar();
		sdfCaducitat.setCalendar(gc);
		gc.setTime(date);
		return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(gc.get(Calendar.YEAR),gc.get(Calendar.MONTH) + 1,gc.get(Calendar.DAY_OF_MONTH),DatatypeConstants.FIELD_UNDEFINED);
	}
	protected Date toDate(XMLGregorianCalendar calendar) throws DatatypeConfigurationException {
		if (calendar == null) {
			return null;
		}
		return calendar.toGregorianCalendar().getTime();
	}

	protected String xifrarId(Long id) throws GeneralSecurityException {
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
//		return new String(Hex.encodeHex(xifrat));
	}
	protected Long desxifrarId(String idXifrat) throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("RC4");
		SecretKeySpec rc4Key = new SecretKeySpec(getClauXifratIdsProperty().getBytes(),"RC4");
		cipher.init(Cipher.DECRYPT_MODE, rc4Key);

		if (idXifrat.length() < 11) {
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_CLIENT,
					"La longitud mínima del identificador xifrat ha de ser 11 caràcters.");
		}
		
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

//	private SedeWsPortType getSedeWs() throws InstanceNotFoundException, MalformedObjectNameException, MalformedURLException, RemoteException, NamingException, CreateException {
//		SedeWsPortType port = new WsClientHelper<SedeWsPortType>().generarClientWs(
//				getClass().getResource("/es/caib/notib/core/wsdl/SedeWs.wsdl"),
//				getSedeUrlProperty(),
//				new QName(
//						"https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/",
//						"SedeWsService"),
//				getUsernameProperty(),
//				getPasswordProperty(),
//				SedeWsPortType.class,
//				new ApiKeySOAPHandler(getApiKeyProperty()),
//				new WsClientHelper.SOAPLoggingHandler(AbstractNotificaHelper.class));
//		return port;
//	}

	protected String getNotificaUrlProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.url");
	}
//	protected String getSedeUrlProperty() {
//		return PropertiesHelper.getProperties().getProperty(
//				"es.caib.notib.notifica.sede.url");
//	}
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

//	public class FirmaSOAPHandler implements SOAPHandler<SOAPMessageContext> {
//		private String keystoreLocation;
//		private String keystoreType;
//		private String keystorePassword;
//		private String keystoreCertAlias;
//		private String keystoreCertPassword;
//		public FirmaSOAPHandler(
//				String keystoreLocation,
//				String keystoreType,
//				String keystorePassword,
//				String keystoreCertAlias,
//				String keystoreCertPassword) {
//			this.keystoreLocation = keystoreLocation;
//			this.keystoreType = keystoreType;
//			this.keystorePassword = keystorePassword;
//			this.keystoreCertAlias = keystoreCertAlias;
//			this.keystoreCertPassword = keystoreCertPassword;
//		}
//		@Override
//		public boolean handleMessage(SOAPMessageContext context) {
//			Boolean outboundProperty = (Boolean)context.get(
//					MessageContext.MESSAGE_OUTBOUND_PROPERTY);
//			if (outboundProperty.booleanValue()) {
//				try {
//					Document document = toDocument(context.getMessage());
//					Properties cryptoProperties = getCryptoProperties();
//			        WSSecHeader header = new WSSecHeader();
//			        header.setMustUnderstand(false);
//			        header.insertSecurityHeader(document);
//					WSSecSignature signer = new WSSecSignature();
//					signer.setUserInfo(keystoreCertAlias, keystoreCertPassword);
//					Crypto crypto = CryptoFactory.getInstance(cryptoProperties);
//					Document signedDoc = signer.build(
//							document,
//							crypto,
//							header);
//					context.getMessage().getSOAPPart().setContent(
//							new DOMSource(signedDoc));
//				} catch (Exception ex) {
//					throw new RuntimeException(
//							"No s'ha pogut firmar el missatge SOAP",
//							ex);
//				}
//				@SuppressWarnings("unchecked")
//				Map<String, List<String>> headers = (Map<String, List<String>>)context.get(
//						MessageContext.HTTP_REQUEST_HEADERS);
//				if (headers != null) {
//					for (String header: headers.keySet()) {
//						List<String> values = headers.get(header);
//						System.out.println(">>> " + header);
//						for (String value: values) {
//							System.out.println(">>>      " + value);
//						}
//					}
//				}
//			}
//			return true;
//		}
//		@Override
//		public boolean handleFault(SOAPMessageContext context) {
//			return false;
//		}
//		@Override
//		public void close(MessageContext context) {
//		}
//		@Override
//		public Set<QName> getHeaders() {
//			return new TreeSet<QName>();
//		}
//		private Properties getCryptoProperties() {
//			Properties cryptoProperties = new Properties();
//			cryptoProperties.put(
//					"org.apache.ws.security.crypto.provider",
//					"org.apache.ws.security.components.crypto.Merlin");
//			cryptoProperties.put(
//					"org.apache.ws.security.crypto.merlin.file",
//					keystoreLocation);
//			cryptoProperties.put(
//					"org.apache.ws.security.crypto.merlin.keystore.type",
//					keystoreType);
//			if (keystorePassword != null && !keystorePassword.isEmpty()) {
//				cryptoProperties.put(
//						"org.apache.ws.security.crypto.merlin.keystore.password",
//						keystorePassword);
//			}
//			cryptoProperties.put(
//					"org.apache.ws.security.crypto.merlin.keystore.alias",
//					keystoreCertAlias);
//			return cryptoProperties;
//		}
//		private Document toDocument(SOAPMessage soapMsg) throws SOAPException, TransformerException {
//			Source src = soapMsg.getSOAPPart().getContent();
//			TransformerFactory tf = TransformerFactory.newInstance();
//			Transformer transformer = tf.newTransformer();
//			DOMResult result = new DOMResult();
//			transformer.transform(src, result);
//			return (Document) result.getNode();
//		}
//	}

	private static final Logger logger = LoggerFactory.getLogger(AbstractNotificaHelper.class);

}
