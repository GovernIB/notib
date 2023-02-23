/**
 * 
 */
package es.caib.notib.core.ejb.ws;

import es.caib.notib.client.domini.*;
import es.caib.notib.core.api.ws.notificacio.NotificacioServiceWsException;
import es.caib.notib.core.api.ws.notificacio.NotificacioServiceWsV2;
import es.caib.notib.core.helper.UsuariHelper;
import org.jboss.annotation.security.SecurityDomain;
import org.jboss.wsf.spi.annotation.WebContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

/**
 * EJB per a la publicació del servei web de gestió de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@WebService(
		name = "NotificacioServiceV2",
		serviceName = "NotificacioServiceV2",
		portName = "NotificacioServiceV2Port",
		targetNamespace = "http://www.caib.es/notib/ws/notificacio")
@WebContext(
		contextRoot = "/notib/ws",
		urlPattern = "/notificacioV2WS",
		authMethod = "WSBASIC",
		transportGuarantee = "NONE",
		secureWSDLAccess = false)
@RolesAllowed({"NOT_APL"})
@SecurityDomain("seycon")
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class NotificacioServiceWsBeanV2 implements NotificacioServiceWsV2 {

	@Resource
	private SessionContext sessionContext;

	@Autowired
	private UsuariHelper usuariHelper;

	@Autowired
	private NotificacioServiceWsV2 delegate;



	@Override
	public RespostaAlta alta(NotificacioV2 notificacio) {
		usuariHelper.generarUsuariAutenticatEjb(
				sessionContext,
				true);
		return delegate.alta(notificacio);
	}

    @Override
    public RespostaAltaV2 altaV2(NotificacioV2 notificacio) throws NotificacioServiceWsException {
        return delegate.altaV2(notificacio);
    }

    @Override
	public RespostaConsultaEstatNotificacio consultaEstatNotificacio(String identificador) {
		usuariHelper.generarUsuariAutenticatEjb(
				sessionContext,
				true);
		return delegate.consultaEstatNotificacio(identificador);
	}

	@Override
	public RespostaConsultaEstatNotificacioV2 consultaEstatNotificacioV2(String identificador) {
		return delegate.consultaEstatNotificacioV2(identificador);
	}

	@Override
	public RespostaConsultaEstatEnviament consultaEstatEnviament(String referencia) {
		usuariHelper.generarUsuariAutenticatEjb(
				sessionContext,
				true);
		return delegate.consultaEstatEnviament(referencia);
	}

	@Override
	public RespostaConsultaEstatEnviamentV2 consultaEstatEnviamentV2(String referencia) throws NotificacioServiceWsException {
		return delegate.consultaEstatEnviamentV2(referencia);
	}

	@Override
	public boolean donarPermisConsulta(PermisConsulta permisConsulta) {
		usuariHelper.generarUsuariAutenticatEjb(
				sessionContext,
				true);
		return delegate.donarPermisConsulta(permisConsulta);
	}
	
	@Override
	public RespostaConsultaDadesRegistre consultaDadesRegistre(
			DadesConsulta dadesConsulta) {
		return delegate.consultaDadesRegistre(dadesConsulta);
	}

    @Override
    public RespostaConsultaDadesRegistreV2 consultaDadesRegistreV2(DadesConsulta dadesConsulta) {
        return delegate.consultaDadesRegistreV2(dadesConsulta);
    }

    @Override
	public RespostaConsultaJustificantEnviament consultaJustificantEnviament(
			@WebParam(name="identificador") @XmlElement(required = true) String identificador){
		return delegate.consultaJustificantEnviament(identificador);

	}

}