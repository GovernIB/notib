package es.caib.notib.core.service.ws;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.junit.Test;

import es.caib.notib.core.wsdl.adviser.AdviserWS;

/** Prova del WS Adviser des de Notific@ que rep notificacions sobre
 * canvis d'estat de notificacions o de certificat.
 *
 */
public class NotificaAdviserWsTest {
	
	private static final String ENDPOINT_ADDRESS = "http://localhost:8080/notib/ws/adviserWS";

	@Test
	public void datadoOrganismoTest() throws Exception {
		AdviserWS ws = this.getWS();
		ws.datadoOrganismo(null, null, null);
	}
	
	private AdviserWS getWS() throws Exception {
		URL url = new URL(ENDPOINT_ADDRESS + "?wsdl");
		QName qname = new QName(
				"http://notificaws.local/ws/soap/AdviserWS",
				"AdviserWSService");
		Service service = Service.create(url, qname);
		AdviserWS backofficeWs = service.getPort(AdviserWS.class);
		BindingProvider bp = (BindingProvider)backofficeWs;
		@SuppressWarnings("rawtypes")
		List<Handler> handlerChain = new ArrayList<Handler>();
		handlerChain.add(new LogMessageHandler());
		bp.getBinding().setHandlerChain(handlerChain);
		
		// Autenticació
		bp.getRequestContext().put(
				BindingProvider.USERNAME_PROPERTY,
				"admin");
		bp.getRequestContext().put(
				BindingProvider.PASSWORD_PROPERTY,
				"admin15");
		 
		return backofficeWs;
	}
	
	private class LogMessageHandler implements SOAPHandler<SOAPMessageContext> {
		public boolean handleMessage(SOAPMessageContext messageContext) {
			log(messageContext);
			return true;
		}
		public Set<QName> getHeaders() {
			return Collections.emptySet();
		}
		public boolean handleFault(SOAPMessageContext messageContext) {
			log(messageContext);
			return true;
		}
		public void close(MessageContext context) {
		}
		private void log(SOAPMessageContext messageContext) {
			SOAPMessage msg = messageContext.getMessage();
			try {
				Boolean outboundProperty = (Boolean)messageContext.get(
						MessageContext.MESSAGE_OUTBOUND_PROPERTY);
				if (outboundProperty)
					System.out.print("Missatge SOAP petició: ");
				else
					System.out.print("Missatge SOAP resposta: ");
				msg.writeTo(System.out);
				System.out.println();
			} catch (SOAPException ex) {
				Logger.getLogger(LogMessageHandler.class.getName()).log(
						Level.SEVERE,
						null,
						ex);
			} catch (IOException ex) {
				Logger.getLogger(LogMessageHandler.class.getName()).log(
						Level.SEVERE,
						null,
						ex);
			}
		}
	}
	

}
