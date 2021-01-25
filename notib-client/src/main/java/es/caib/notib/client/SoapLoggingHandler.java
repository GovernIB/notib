/**
 * 
 */
package es.caib.notib.client;

import java.io.ByteArrayOutputStream;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
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
@Deprecated
public class SoapLoggingHandler implements SOAPHandler<SOAPMessageContext> {

	private Logger logger;
	
	public SoapLoggingHandler(Class<?> clazz) {
		super();
		logger = LoggerFactory.getLogger(clazz);
	}

	public Set<QName> getHeaders() {
		return null;
	}

	public boolean handleMessage(SOAPMessageContext smc) {
		logToSystemOut(smc);
		return true;
	}

	public boolean handleFault(SOAPMessageContext smc) {
		logToSystemOut(smc);
		return true;
	}

	public void close(MessageContext messageContext) {
	}



	private void logToSystemOut(SOAPMessageContext smc) {
		if (logger.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder();
			Boolean outboundProperty = (Boolean)smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (outboundProperty.booleanValue())
				sb.append("Missatge SOAP enviat: ");
			else
				sb.append("Missatge SOAP rebut: ");
			SOAPMessage message = smc.getMessage();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				message.writeTo(baos);
				sb.append(baos.toString());
			} catch (Exception ex) {
				sb.append("Error al processar el missatge XML: " + ex.getMessage());
			}
			logger.debug(sb.toString());
		}
	}

}
