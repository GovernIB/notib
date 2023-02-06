/**
 * 
 */
package es.caib.notib.logic.helper;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.logic.intf.dto.NotificaDomiciliViaTipusEnumDto;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.repository.NotificacioRepository;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
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
@Slf4j
@Component
public abstract class AbstractNotificaHelper {
	
	@Autowired
	protected AuditNotificacioHelper auditNotificacioHelper;
	@Autowired
	protected ConfigHelper configHelper;
	@Autowired
	protected NotificacioRepository notificacioRepository;
	@Autowired 
	private EmailNotificacioHelper emailNotificacioHelper;
	
	private boolean modeTest;
	
	public abstract NotificacioEntity notificacioEnviar(Long notificacioId, boolean ambEnviamentPerEmail);

	public abstract NotificacioEnviamentEntity enviamentRefrescarEstat(Long enviamentId) throws SistemaExternException;

	public abstract NotificacioEnviamentEntity enviamentRefrescarEstat(Long enviamentId, boolean raiseExceptions) throws Exception;

	public boolean isConnexioNotificaDisponible() {
		return getNotificaUrlProperty() != null;
	}

	public void setModeTest(boolean modeTest) {
		this.modeTest = modeTest;
	}

	public NotificacioEnviamentEntity enviamentUpdateDatat(EnviamentEstat notificaEstat, Date notificaEstatData, String notificaEstatDescripcio, String notificaDatatOrigen,
															String notificaDatatReceptorNif, String notificaDatatReceptorNom, String notificaDatatNumSeguiment,
															String notificaDatatErrorDescripcio, NotificacioEnviamentEntity enviament) throws Exception {

		var estatFinal = EnviamentEstat.ABSENT.equals(notificaEstat) ||
						EnviamentEstat.ADRESA_INCORRECTA.equals(notificaEstat) ||
						EnviamentEstat.ERROR_ENTREGA.equals(notificaEstat) ||
						EnviamentEstat.EXPIRADA.equals(notificaEstat) ||
						EnviamentEstat.EXTRAVIADA.equals(notificaEstat) ||
						EnviamentEstat.MORT.equals(notificaEstat) ||
						EnviamentEstat.LLEGIDA.equals(notificaEstat) ||
						EnviamentEstat.NOTIFICADA.equals(notificaEstat) ||
						EnviamentEstat.REBUTJADA.equals(notificaEstat) ||
						EnviamentEstat.DESCONEGUT.equals(notificaEstat) ||
						EnviamentEstat.SENSE_INFORMACIO.equals(notificaEstat);
		enviament.updateNotificaDatat(notificaEstat, notificaEstatData, estatFinal, notificaEstatDescripcio, notificaDatatOrigen, notificaDatatReceptorNif,
										notificaDatatReceptorNom, notificaDatatNumSeguiment, notificaDatatErrorDescripcio);
		var estatsEnviamentsFinals = true;
		var estatsEnviamentsNotificaFinals = true;
		var enviaments = enviament.getNotificacio().getEnviaments();
		for (var env: enviaments) {
			if (env.getId().equals(enviament.getId())) {
				env = enviament;
			}
			if (!env.isNotificaEstatFinal()) {
				estatsEnviamentsFinals = false;
				if (!env.isPerEmail()) {
					estatsEnviamentsNotificaFinals = false;
				}
				break;
			}
		}
		log.info("Estat final: " + estatsEnviamentsFinals);
		if (!estatsEnviamentsNotificaFinals) {
			return enviament;
		}
		if (estatsEnviamentsFinals) {
			auditNotificacioHelper.updateEstatAFinalitzada(notificaEstat.name(), enviament.getNotificacio());
		} else {
			auditNotificacioHelper.updateEstatAFinalitzadaAmbError(notificaEstat.name(), enviament.getNotificacio());
		}
		log.info("Envio correu en cas d'usuaris no APLICACIÓ");
		var notificacio = enviament.getNotificacio();
		if (!TipusUsuariEnumDto.INTERFICIE_WEB.equals(notificacio.getTipusUsuari())) {
			return enviament;
		}
		var startTime = System.nanoTime();
		try {
			emailNotificacioHelper.prepararEnvioEmailNotificacio(notificacio);
		} catch (Exception ex) {
			throw new Exception("Hi ha hagut un error preparant mail notificació (prepararEnvioEmailNotificacio) [Id: " + enviament.getId() + "]", ex);
		}
		var elapsedTime = (System.nanoTime() - startTime) / 10e6;
		log.info(" [TIMER-EST] Preparar enviament mail notificació (prepararEnvioEmailNotificacio)  [Id: " + enviament.getId() + "]: " + elapsedTime + " ms");

//			//Marcar com a processada si la notificació s'ha fet des de una aplicació
//			if (enviament.getNotificacio() != null && enviament.getNotificacio().getTipusUsuari() == TipusUsuariEnumDto.APLICACIO) {
//				log.info("Marcant notificació com processada per ser usuari aplicació...");
//				enviament.getNotificacio().updateEstat(NotificacioEstatEnumDto.PROCESSADA);
//				enviament.getNotificacio().updateMotiu(notificaEstat.name());
//				enviament.getNotificacio().updateEstatDate(new Date());
//			}
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
	private static final EnviamentEstat[] estatsNotib = new EnviamentEstat[] {
			EnviamentEstat.ABSENT,
			EnviamentEstat.DESCONEGUT,
			EnviamentEstat.ADRESA_INCORRECTA,
			EnviamentEstat.ENVIADA_DEH,
			EnviamentEstat.ENVIADA_CI,
			EnviamentEstat.ENTREGADA_OP,
			EnviamentEstat.LLEGIDA,
			EnviamentEstat.ERROR_ENTREGA,
			EnviamentEstat.EXTRAVIADA,
			EnviamentEstat.MORT,
			EnviamentEstat.NOTIFICADA,
			EnviamentEstat.PENDENT_ENVIAMENT,
			EnviamentEstat.PENDENT_CIE,
			EnviamentEstat.PENDENT_DEH,
			EnviamentEstat.PENDENT_SEU,
			EnviamentEstat.REBUTJADA,
			EnviamentEstat.EXPIRADA,
			EnviamentEstat.ENVIAMENT_PROGRAMAT,
			EnviamentEstat.SENSE_INFORMACIO,
			EnviamentEstat.ANULADA};

	protected EnviamentEstat getEstatNotifica(String estatCodi) {

		for (var i = 0; i < estatsNotifica.length; i++) {
			if (estatCodi.equalsIgnoreCase(estatsNotifica[i])) {
				return estatsNotib[i];
			}
		}
		return null;
	}
	protected boolean isEstatFinal(String estatCodi) {

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
		for (var i = 0; i < estatsNotifica.length; i++) {
			if (estatCodi.equalsIgnoreCase(estatsNotifica[i])) {
				return esFinal[i];
			}
		}
		return false;
	}

	protected String viaTipusToString(NotificaDomiciliViaTipusEnumDto viaTipus) {

		if (viaTipus == null) {
			return null;
		}
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
	}

	protected XMLGregorianCalendar toXmlGregorianCalendar(Date date) throws DatatypeConfigurationException {

		if (date == null) {
			return null;
		}
		var sdfCaducitat = new SimpleDateFormat("yyyy-MM-dd");
		var gc = new GregorianCalendar();
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
		var idlong = (modeTest) ? id.longValue() + System.currentTimeMillis() : id.longValue();
		var bytes = longToBytes(idlong);
		var cipher = Cipher.getInstance("RC4");
		var rc4Key = new SecretKeySpec(getClauXifratIdsProperty().getBytes(),"RC4");
		cipher.init(Cipher.ENCRYPT_MODE, rc4Key);
		var xifrat = cipher.doFinal(bytes);
		return new String(Base64.encodeBase64(xifrat));
//		return new String(Hex.encodeHex(xifrat));
	}
	protected Long desxifrarId(String idXifrat) throws GeneralSecurityException {

		var cipher = Cipher.getInstance("RC4");
		var rc4Key = new SecretKeySpec(getClauXifratIdsProperty().getBytes(),"RC4");
		cipher.init(Cipher.DECRYPT_MODE, rc4Key);
		if (idXifrat.length() < 11) {
			throw new SistemaExternException(IntegracioHelper.INTCODI_CLIENT, "La longitud mínima del identificador xifrat ha de ser 11 caràcters.");
		}
		var desxifrat = cipher.doFinal(Base64.decodeBase64(idXifrat.getBytes()));
		return new Long(bytesToLong(desxifrat));
	}

	protected byte[] longToBytes(long l) {

		var result = new byte[Long.SIZE / Byte.SIZE];
	    for (var i = 7; i >= 0; i--) {
	        result[i] = (byte)(l & 0xFF);
	        l >>= 8;
	    }
	    return result;
	}
	protected long bytesToLong(byte[] b) {

		long result = 0;
	    for (var i = 0; i < 8; i++) {
	        result <<= 8;
	        result |= (b[i] & 0xFF);
	    }
	    return result;
	}

	protected String concatenarLlinatges(String llinatge1, String llinatge2) {

		if (llinatge1 == null && llinatge2 == null) {
			return null;
		}
		var llinatges = new StringBuilder();
		llinatges.append(llinatge1.trim());
		if (llinatge2 != null && !llinatge2.trim().isEmpty()) {
			llinatges.append(" ");
			llinatges.append(llinatge2);
		}
		return llinatges.toString();
	}
	public boolean isAdviserActiu() {
		return configHelper.getConfigAsBoolean("es.caib.notib.adviser.actiu");
	}
	protected String getNotificaUrlProperty() {
		return configHelper.getConfig("es.caib.notib.notifica.url");
	}
	protected String getUsernameProperty() {
		return configHelper.getConfig("es.caib.notib.notifica.username");
	}
	protected String getPasswordProperty() {
		return configHelper.getConfig("es.caib.notib.notifica.password");
	}
	protected String getClauXifratIdsProperty() {
		return configHelper.getConfig("es.caib.notib.notifica.clau.xifrat.ids");
	}

	public class ApiKeySOAPHandler implements SOAPHandler<SOAPMessageContext> {
		private final String apiKey;
		public ApiKeySOAPHandler(String apiKey) {
			this.apiKey = apiKey;
		}
		@Override
		public boolean handleMessage(SOAPMessageContext context) {

			var outboundProperty = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (!outboundProperty.booleanValue()) {
				return true;
			}
			try {
				var envelope = context.getMessage().getSOAPPart().getEnvelope();
				var factory = SOAPFactory.newInstance();
				var apiKeyElement = factory.createElement(new QName("https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/", "api_key"));
				apiKeyElement.addTextNode(apiKey);
				var header = envelope.getHeader();
				if (header == null) {
					header = envelope.addHeader();
				}
				header.addChildElement(apiKeyElement);
			} catch (SOAPException ex) {
				log.error("No s'ha pogut afegir l'API key a la petició SOAP per Notifica", ex);
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
}
