/**
 * 
 */
package es.caib.notib.core.ejb.ws;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.jboss.annotation.security.SecurityDomain;
import org.jboss.wsf.spi.annotation.WebContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.notib.core.api.ws.notificacio.Notificacio;
import es.caib.notib.core.api.ws.notificacio.NotificacioCertificacio;
import es.caib.notib.core.api.ws.notificacio.NotificacioEstat;
import es.caib.notib.core.api.ws.notificacio.NotificacioWsService;

/**
 * Implementació dels mètodes per al servei de bústies de RIPEA.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@WebService(
		name = "Notificacio",
		serviceName = "NotificacioService",
		portName = "NotificacioServicePort",
		targetNamespace = "http://www.caib.es/notib/ws/notificacio")
@WebContext(
		contextRoot = "/notib/ws",
		urlPattern = "/notificacio",
		authMethod = "WSBASIC",
		transportGuarantee = "NONE",
		secureWSDLAccess = false)
@RolesAllowed({"IPA_BSTWS"})
@SecurityDomain("seycon")
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class NotificacioWsServiceBean implements NotificacioWsService {

	@Autowired
	private NotificacioWsService delegate;

	@Override
	public List<String> alta(
			Notificacio notificacio) {
		
		return delegate.alta(notificacio);
	}

	@Override
	public Notificacio consulta(
			String referencia) {
		
		return delegate.consulta(referencia);
	}

	@Override
	public NotificacioEstat consultaEstat(
			String referencia) {
		
		return delegate.consultaEstat(referencia);
	}

	@Override
	public NotificacioCertificacio consultaCertificacio(
			String referencia) {
		
		return delegate.consultaCertificacio(referencia);
	}

}
