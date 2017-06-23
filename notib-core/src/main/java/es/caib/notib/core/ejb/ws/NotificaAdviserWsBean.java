/**
 * 
 */
package es.caib.notib.core.ejb.ws;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
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
		name = "AdviserWS",
		serviceName = "AdviserWSService",
		portName = "AdviserWSServicePort",
		targetNamespace = "https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/")
@WebContext(
		contextRoot = "/notib/ws",
		urlPattern = "/AdviserWS",
		authMethod = "WSBASIC",
		transportGuarantee = "NONE",
		secureWSDLAccess = false)
//@RolesAllowed({"NOT_NOTIFICA"})
//@SecurityDomain("seycon")
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class NotificaAdviserWsBean implements AdviserWS {

	@Resource
	private SessionContext sessionContext;
	@Autowired
	private AdviserWS delegate;



	@Override
	public void datadoOrganismo(
			DatadoRequest datadoOrganismo, 
			Holder<String> codigoRespuesta,
			Holder<String> textoRespuesta) {
		delegate.datadoOrganismo(
				datadoOrganismo, 
				codigoRespuesta, 
				textoRespuesta);
	}
	@Override
	public void certificacionOrganismo(
			CertificadoRequest certificacionOrganismo, 
			Holder<String> codigoRespuesta,
			Holder<String> textoRespuesta) {
		delegate.certificacionOrganismo(
				certificacionOrganismo, 
				codigoRespuesta, 
				textoRespuesta);
	}

}
