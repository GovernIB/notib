/**
 * 
 */
package es.caib.notib.plugin.utils;

import java.io.ByteArrayOutputStream;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SOAP Handler per a imprimir l'XML de peticions i repostes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SOAPLoggingHandler implements SOAPHandler<SOAPMessageContext> {

	public Set<QName> getHeaders() {
		return null;
	}

	public boolean handleMessage(SOAPMessageContext messageContext) {

		log(messageContext);
		return true;
	}

	public boolean handleFault(SOAPMessageContext messageContext) {

		log(messageContext);
		return true;
	}

	public void close(MessageContext messageContext) {
	}

	private void log(SOAPMessageContext messageContext) {

		if (!LOGGER.isDebugEnabled()) {
			return;
		}
		var sb = new StringBuilder();
		var outboundProperty = (Boolean)messageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		sb.append(outboundProperty.booleanValue() ? "Missarge sortint: " : "Missarge entrant: ");
		var message = messageContext.getMessage();
		var baos = new ByteArrayOutputStream();
		try {
			message.writeTo(baos);
			sb.append(baos);
		} catch (Exception ex) {
			sb.append("Error al imprimir el missatge XML: " + ex.getMessage());
		}
		LOGGER.debug(sb.toString());
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(SOAPLoggingHandler.class);

}
