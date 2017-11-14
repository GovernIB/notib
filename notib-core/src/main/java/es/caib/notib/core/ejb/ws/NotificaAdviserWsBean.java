/**
 * 
 */
package es.caib.notib.core.ejb.ws;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.Holder;

import org.jboss.wsf.spi.annotation.WebContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.notib.core.wsdl.adviser.AdviserWS;
import es.caib.notib.core.wsdl.adviser.CertificadoRequest;
import es.caib.notib.core.wsdl.adviser.DatadoRequest;

/**
 * Implementació dels mètodes per al servei de recepció de
 * callbacks de portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@WebService(
		name = "adviserWS",
		serviceName = "AdviserWSService",
		portName = "AdviserWSServicePort",
		targetNamespace = "https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/")
@WebContext(
		contextRoot = "/notib/ws",
		urlPattern = "/adviser",
		authMethod = "WSBASIC",
		transportGuarantee = "NONE",
		secureWSDLAccess = false)
//@RolesAllowed({"NOT_NOTIFICA"})
//@SecurityDomain("seycon")
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class NotificaAdviserWsBean implements AdviserWS {

	@Autowired
	private AdviserWS delegate;



	@Override
	public void datadoOrganismo(
			@WebParam(name = "datadoOrganismo", targetNamespace = "") 
			DatadoRequest datadoOrganismo, 
			@WebParam(mode = WebParam.Mode.OUT, name = "codigo_respuesta", targetNamespace = "") 
			Holder<String> codigoRespuesta,
			@WebParam(mode = WebParam.Mode.OUT, name = "texto_respuesta", targetNamespace = "")
			Holder<String> textoRespuesta) {
		delegate.datadoOrganismo(
				datadoOrganismo, 
				codigoRespuesta, 
				textoRespuesta);
	}
	@Override
	public void certificacionOrganismo(
			@WebParam(name = "certificacionOrganismo", targetNamespace = "")
			CertificadoRequest certificacionOrganismo,
			@WebParam(mode = WebParam.Mode.OUT, name = "codigo_respuesta", targetNamespace = "")
			Holder<String> codigoRespuesta,
			@WebParam(mode = WebParam.Mode.OUT, name = "texto_respuesta", targetNamespace = "")
			Holder<String> textoRespuesta) {
		delegate.certificacionOrganismo(
				certificacionOrganismo, 
				codigoRespuesta, 
				textoRespuesta);
	}

}
