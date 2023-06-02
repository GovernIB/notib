package es.caib.notib.logic.service.ws;

import es.caib.notib.logic.intf.ws.adviser.AdviserServiceWsV2;
import es.caib.notib.logic.intf.ws.adviser.common.Opciones;
import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.Acuse;
import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.Receptor;
import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.SincronizarEnvio;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;

/** Prova del WS Adviser des de Notific@ que rep notificacions sobre
 * canvis d'estat de notificacions o de certificat.
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NotificaAdviserWsTest {
	
	private static final String ENDPOINT_ADDRESS = "http://localhost:8180/notib/ws/adviserV2";
	private static final String EMISOR_DIR3 = "EA0004518";
	private static final String CERIFICACIO_B64 = 
			  "JVBERi0xLjQKJcOkw7zDtsOfCjIgMCBvYmoKPDwvTGVuZ3RoIDMgMCBSL0ZpbHRlci9GbGF0ZURl"
			+ "Y29kZT4+CnN0cmVhbQp4nDPQM1Qo5ypUMFAwALJMLU31jBQsTAz1LBSKUrnCtRTyIHJAWJTO5RTC"
			+ "ZWoGlDI3NwEqDklR0HczVDA0UghJi7YxMLTTNbQxMDIwNjAxMLWLDfHicg3hCuQKVAAADMAVAgpl"
			+ "bmRzdHJlYW0KZW5kb2JqCgozIDAgb2JqCjk4CmVuZG9iagoKNSAwIG9iago8PC9MZW5ndGggNiAw"
			+ "IFIvRmlsdGVyL0ZsYXRlRGVjb2RlL0xlbmd0aDEgNzIwOD4+CnN0cmVhbQp4nOVYfXBTV3Y/9z59"
			+ "25ae/IVBYD3xMNiRLRkLgwFjC1uSZQxY/kplE9sSlmwLsCUk2WzYzeBslyQjwoZmExISpqTbdCez"
			+ "m5k8A906O9ngzCTT7rRpsp00k91A4rY70z8WLyRbtp1ssHvu07PBbD5mup3pH32W3v2dz3vuOec+"
			+ "vet0ciIKuTAFHLiHxsKJ1WoVBYB/ACD5Q5NpYbHJLSCeA6DFw4mRsXLXh78B4P4LQKseOfLg8HsP"
			+ "//smgBw0Ub84Gg1HZr75LzUA/IvI2DqKjL0LD2qRvor0htGx9Dca6NkCADOSUHIkPhSeVPMc0iVI"
			+ "G8fC30j8peZhFdLlSAvj4bHo/uj3n0LaD6Dbm4in0hHYsAiw+ikmTySjiVt9z+iQvozxpZFH8I9d"
			+ "uQg1jKacSg3/jy/1aSgCv3oXmCAh31dc3Muwmo2L11feF/Yufva/GYUuOzwLP4DLcBp+Af2KwAcB"
			+ "iMEEcu6+3oCfI5ddAeiDH0LmS9y+DDMoz+qF4Ak49yV6AXgGLsHfrpglAGPwTYzlr+EXZDP8DFsl"
			+ "Dp8SHTwMb6HXT5G374tcUSPehmU4fBf3Q3ienoI99FdInGMS6qQ8vAnnyQB6TuM6Ty+vuP4PnD4K"
			+ "D+G9C0ZhErF8qXd9/kvQL/4WV/UQ7IFvw244cpfFa+QCZ8D6dcMFzOkbMs+5JNT6uUP0x5Te/h4S"
			+ "fwYj+A0TXDs9ze0Gj9pMcLe4vb3Bnu6uzo5A+/59e9v2tPpbfF5Pc9Nud2PDrvqdO7bXbdtau7na"
			+ "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"
			+ "rkfDLVqZpedgWbkvNOi2DqLSgb5qa19LhbXAld+jxmBVqGjirFwj187FuSe4K5xW1xkotXbgdy5w"
			+ "M0BN7dZ2Z7v8fh1us6GjPYk9U3u4Vl+F1d9SZzW1WFucLe+0fNxyo0Uz2EIu4Mf3iu+Kj3P7Kpw+"
			+ "t6/U5lvrt/QUu4p6eJephxLoIS7ocZoWTdRkGjSdMHEmaAQ6VUzUZIacme7ustvbZrSLnW2SLnBA"
			+ "Io9JZV3s7u7okzSPSdDTdyA4Tch3e0+ePg1N69qkmq6gFFrX2yZFELgZmELAr5suhqbeVCrNziJ2"
			+ "PJEgnMA72CeQNZDKMsG+JAZ7iqRSkEoRO5PJEDmQsjM24zAbgpYDKWA3JrXLWgylUiUD/w02VnuI"
			+ "CmVuZHN0cmVhbQplbmRvYmoKCjYgMCBvYmoKNDI4OAplbmRvYmoKCjcgMCBvYmoKPDwvVHlwZS9G"
			+ "b250RGVzY3JpcHRvci9Gb250TmFtZS9CQUFBQUErTGliZXJhdGlvblNlcmlmCi9GbGFncyA0Ci9G"
			+ "b250QkJveFstMTc2IC0zMDMgMTAwNSA5ODFdL0l0YWxpY0FuZ2xlIDAKL0FzY2VudCA4OTEKL0Rl"
			+ "c2NlbnQgLTIxNgovQ2FwSGVpZ2h0IDk4MQovU3RlbVYgODAKL0ZvbnRGaWxlMiA1IDAgUgo+Pgpl"
			+ "bmRvYmoKCjggMCBvYmoKPDwvTGVuZ3RoIDI0Ni9GaWx0ZXIvRmxhdGVEZWNvZGU+PgpzdHJlYW0K"
			+ "eJxdUMtuhDAMvOcrfNw9rBLYsr0gpGorJA59qLQfEBJDI5UkCuHA39cJ21bqIdGM7bFHw6/dY2dN"
			+ "5K/BqR4jjMbqgItbg0IYcDKWFSVoo+KN5V/N0jNO2n5bIs6dHV1dM/5GvSWGDQ4P2g14ZPwlaAzG"
			+ "TnD4uPbE+9X7L5zRRhCsaUDjSHuepH+WM/KsOnWa2iZuJ5L8DbxvHqHMvNitKKdx8VJhkHZCVgvR"
			+ "QN22DUOr//WqXTGM6lMGmixoUohKNITLjO/LhM8ZX9qE7/b6JeFqrxd5921LupJi+HEPag2BnOes"
			+ "suVk1lj8jdM7n1T5fQOwu3a7CmVuZHN0cmVhbQplbmRvYmoKCjkgMCBvYmoKPDwvVHlwZS9Gb250"
			+ "L1N1YnR5cGUvVHJ1ZVR5cGUvQmFzZUZvbnQvQkFBQUFBK0xpYmVyYXRpb25TZXJpZgovRmlyc3RD"
			+ "aGFyIDAKL0xhc3RDaGFyIDUKL1dpZHRoc1szNjUgNTU2IDMzMyA1MDAgNTAwIDQ0MyBdCi9Gb250"
			+ "RGVzY3JpcHRvciA3IDAgUgovVG9Vbmljb2RlIDggMCBSCj4+CmVuZG9iagoKMTAgMCBvYmoKPDwv"
			+ "RjEgOSAwIFIKPj4KZW5kb2JqCgoxMSAwIG9iago8PC9Gb250IDEwIDAgUgovUHJvY1NldFsvUERG"
			+ "L1RleHRdCj4+CmVuZG9iagoKMSAwIG9iago8PC9UeXBlL1BhZ2UvUGFyZW50IDQgMCBSL1Jlc291"
			+ "cmNlcyAxMSAwIFIvTWVkaWFCb3hbMCAwIDU5NSA4NDJdL0dyb3VwPDwvUy9UcmFuc3BhcmVuY3kv"
			+ "Q1MvRGV2aWNlUkdCL0kgdHJ1ZT4+L0NvbnRlbnRzIDIgMCBSPj4KZW5kb2JqCgo0IDAgb2JqCjw8"
			+ "L1R5cGUvUGFnZXMKL1Jlc291cmNlcyAxMSAwIFIKL01lZGlhQm94WyAwIDAgNTk1IDg0MiBdCi9L"
			+ "aWRzWyAxIDAgUiBdCi9Db3VudCAxPj4KZW5kb2JqCgoxMiAwIG9iago8PC9UeXBlL0NhdGFsb2cv"
			+ "UGFnZXMgNCAwIFIKL09wZW5BY3Rpb25bMSAwIFIgL1hZWiBudWxsIG51bGwgMF0KL0xhbmcoY2Et"
			+ "RVMpCj4+CmVuZG9iagoKMTMgMCBvYmoKPDwvQXV0aG9yPEZFRkYwMDczMDA2OTAwNkYwMDZFMDAy"
			+ "MD4KL0NyZWF0b3I8RkVGRjAwNTcwMDcyMDA2OTAwNzQwMDY1MDA3Mj4KL1Byb2R1Y2VyPEZFRkYw"
			+ "MDRDMDA2OTAwNjIwMDcyMDA2NTAwNEYwMDY2MDA2NjAwNjkwMDYzMDA2NTAwMjAwMDM1MDAyRTAw"
			+ "MzE+Ci9DcmVhdGlvbkRhdGUoRDoyMDE3MTAyMDA5NDgxOCswMicwMCcpPj4KZW5kb2JqCgp4cmVm"
			+ "CjAgMTQKMDAwMDAwMDAwMCA2NTUzNSBmIAowMDAwMDA1Mzc2IDAwMDAwIG4gCjAwMDAwMDAwMTkg"
			+ "MDAwMDAgbiAKMDAwMDAwMDE4OCAwMDAwMCBuIAowMDAwMDA1NTE5IDAwMDAwIG4gCjAwMDAwMDAy"
			+ "MDcgMDAwMDAgbiAKMDAwMDAwNDU3OSAwMDAwMCBuIAowMDAwMDA0NjAwIDAwMDAwIG4gCjAwMDAw"
			+ "MDQ3OTUgMDAwMDAgbiAKMDAwMDAwNTExMCAwMDAwMCBuIAowMDAwMDA1Mjg5IDAwMDAwIG4gCjAw"
			+ "MDAwMDUzMjEgMDAwMDAgbiAKMDAwMDAwNTYxOCAwMDAwMCBuIAowMDAwMDA1NzE1IDAwMDAwIG4g"
			+ "CnRyYWlsZXIKPDwvU2l6ZSAxNC9Sb290IDEyIDAgUgovSW5mbyAxMyAwIFIKL0lEIFsgPDJBQUU5"
			+ "MThDM0MxNzI5QkRBRDVGNDdDNTQyQ0JBMkVFPgo8MkFBRTkxOEMzQzE3MjlCREFENUY0N0M1NDJD"
			+ "QkEyRUU+IF0KL0RvY0NoZWNrc3VtIC9BNUVBQUYyOTA3RkY3OTZBQTVFNkM0MDQ5Mzk4Nzc2MAo+"
			+ "PgpzdGFydHhyZWYKNTkyNAolJUVPRgo=";
	private static final String CERIFICACIO_SHA1 = "b081c7abf42d5a8e5a4050958f28046bdf86158c";
	
	
//	@Test
	public void a_datadoOrganismoTest() throws Exception {
		AdviserServiceWsV2 ws = this.getWS();

		// Data
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(new Date());
		XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

		// Receptor enviament
		Receptor receptor = new Receptor();
		receptor.setNifReceptor("12345678Z");
		receptor.setNombreReceptor("destinatariNom0 destLlinatge1_0 destLlinatge2_0");

		Acuse acusePDF = new Acuse();

		acusePDF.setContenido(CERIFICACIO_B64.getBytes());
		acusePDF.setCsvResguardo("dasd-dsadad-asdasd-asda-sda-das");
		acusePDF.setHash(CERIFICACIO_SHA1);

		SincronizarEnvio sincronizarEnvio = new SincronizarEnvio();
		sincronizarEnvio.setOrganismoEmisor(EMISOR_DIR3);
		sincronizarEnvio.setIdentificador("39128285cf121cb00453");
		sincronizarEnvio.setTipoEntrega(unmarshal("2"));
		sincronizarEnvio.setModoNotificacion(unmarshal("5"));
		sincronizarEnvio.setEstado("notificada");
		sincronizarEnvio.setFechaEstado(date);
		sincronizarEnvio.setReceptor(receptor);
		sincronizarEnvio.setAcusePDF(acusePDF);
		sincronizarEnvio.setAcuseXML(null);
		sincronizarEnvio.setOpcionesSincronizarEnvio(null);

		Holder<String> codigoRespuesta = new Holder<String>();
		Holder<String> descripcionRespuesta = new Holder<String>();


		Holder<Opciones> opcionesResultadoSincronizarEnvio = new Holder<Opciones>();
		
		Holder<String> identificador = new Holder<String>();
		identificador.value = "39128285cf121cb00453";

//		ResultadoSincronizarEnvio resultadoSincronizarEnvio =
		ws.sincronizarEnvio(//sincronizarEnvio);
				EMISOR_DIR3,
				identificador,
				unmarshal("2"),
				unmarshal("5"),
				"notificada",
				date,
				receptor,
				acusePDF,
				null,
				null,
				codigoRespuesta,
				descripcionRespuesta,
				opcionesResultadoSincronizarEnvio);
		
//		assertNotNull(resultadoSincronizarEnvio.getCodigoRespuesta());
		assertNotNull(codigoRespuesta);
//		assertNotNull(resultadoSincronizarEnvio.getDescripcionRespuesta());
		assertNotNull(descripcionRespuesta);
	}
	
//	@Test
//	public void b_certificacionOrganismoTest() throws Exception {
//		AdviserWS ws = this.getWS();
//		
//		CertificadoRequest certificado = new CertificadoRequest();
//		
//		certificado.setAcuseOSobre("acuse");
//		certificado.setCertificacion(CERIFICACIO_B64);
//		certificado.setHashSha1(CERIFICACIO_SHA1);
//		certificado.setIdentificadorDestinatario("39128285cf121cb00453");
//		certificado.setOrganismoEmisor(EMISOR_DIR3);
//		
//		ws.certificacionOrganismo(
//				certificado, 
//				new Holder<String>(), 
//				new Holder<String>());
//	}
	
	private AdviserServiceWsV2 getWS() throws Exception {
		URL url = new URL(ENDPOINT_ADDRESS + "?wsdl");
		QName qname = new QName(
				"https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/",
				"AdviserWsV2Service");
		Service service = Service.create(url, qname);
		AdviserServiceWsV2 backofficeWs = service.getPort(AdviserServiceWsV2.class);
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
				"admin");
		 
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
	
	public BigInteger unmarshal(String s) throws Exception {
		try {
			return new BigInteger(s);
		} catch (NumberFormatException e) {
			return null;
		}
	}

}
