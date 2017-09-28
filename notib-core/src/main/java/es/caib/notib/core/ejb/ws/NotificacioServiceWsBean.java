/**
 * 
 */
package es.caib.notib.core.ejb.ws;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.jboss.annotation.security.SecurityDomain;
import org.jboss.wsf.spi.annotation.WebContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.notib.core.api.ws.notificacio2.InformacioEnviament;
import es.caib.notib.core.api.ws.notificacio2.Notificacio;
import es.caib.notib.core.api.ws.notificacio2.NotificacioServiceWs;
import es.caib.notib.core.api.ws.notificacio2.NotificacioServiceWsException;
import es.caib.notib.core.helper.UsuariHelper;

/**
 * EJB per a la publicació del servei web de gestió de notificacions.
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
@RolesAllowed({"NOT_APL"})
@SecurityDomain("seycon")
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class NotificacioServiceWsBean implements NotificacioServiceWs {

	@Autowired
	private SessionContext sessionContext;
	@Autowired
	private UsuariHelper usuariHelper;

	@Autowired
	private NotificacioServiceWs delegate;



	@Override
	public List<String> alta(
			Notificacio notificacio) throws NotificacioServiceWsException {
		usuariHelper.generarUsuariAutenticatEjb(
				sessionContext,
				true);
		return delegate.alta(notificacio);
	}

	@Override
	public InformacioEnviament consulta(
			String identificador) throws NotificacioServiceWsException {
		usuariHelper.generarUsuariAutenticatEjb(
				sessionContext,
				true);
		return delegate.consulta(identificador);
	}

}
