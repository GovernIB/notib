/**
 * 
 */
package es.caib.notib.logic.ejb.ws;

import es.caib.notib.client.domini.*;
import es.caib.notib.logic.intf.ws.notificacio.NotificacioServiceWsException;
import es.caib.notib.logic.intf.ws.notificacio.NotificacioServiceWsV2;
import es.caib.notib.logic.helper.UsuariHelper;
import org.jboss.ws.api.annotation.WebContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;

/**
 * EJB per a la publicació del servei web de gestió de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@SOAPBinding(style = SOAPBinding.Style.RPC)
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
public class NotificacioServiceWsBeanV2 implements NotificacioServiceWsV2 {

	@Resource
	private SessionContext sessionContext;

	@Autowired
	private UsuariHelper usuariHelper;

	@Autowired
	private NotificacioServiceWsV2 delegate;



	@Override
	@WebMethod
//	@WebResult(name = "respuesta")
	public RespostaAlta alta(NotificacioV2 notificacio) {
		usuariHelper.generarUsuariAutenticatEjb(
				sessionContext,
				true);
		return delegate.alta(notificacio);
	}

    @Override
	@WebMethod
    public RespostaAltaV2 altaV2(NotificacioV2 notificacio) throws NotificacioServiceWsException {
        return delegate.altaV2(notificacio);
    }

    @Override
	@WebMethod
	public RespostaConsultaEstatNotificacio consultaEstatNotificacio(String identificador) {
		usuariHelper.generarUsuariAutenticatEjb(
				sessionContext,
				true);
		return delegate.consultaEstatNotificacio(identificador);
	}

	@Override
	@WebMethod
	public RespostaConsultaEstatNotificacioV2 consultaEstatNotificacioV2(String identificador) {
		return delegate.consultaEstatNotificacioV2(identificador);
	}

	@Override
	@WebMethod
	public RespostaConsultaEstatEnviament consultaEstatEnviament(String referencia) {
		usuariHelper.generarUsuariAutenticatEjb(
				sessionContext,
				true);
		return delegate.consultaEstatEnviament(referencia);
	}

	@Override
	@WebMethod
	public RespostaConsultaEstatEnviamentV2 consultaEstatEnviamentV2(String referencia) throws NotificacioServiceWsException {
		return delegate.consultaEstatEnviamentV2(referencia);
	}

	@Override
	@WebMethod
	public boolean donarPermisConsulta(PermisConsulta permisConsulta) {
		usuariHelper.generarUsuariAutenticatEjb(
				sessionContext,
				true);
		return delegate.donarPermisConsulta(permisConsulta);
	}
	
	@Override
	@WebMethod
	public RespostaConsultaDadesRegistre consultaDadesRegistre(
			DadesConsulta dadesConsulta) {
		return delegate.consultaDadesRegistre(dadesConsulta);
	}

    @Override
	@WebMethod
    public RespostaConsultaDadesRegistreV2 consultaDadesRegistreV2(DadesConsulta dadesConsulta) {
        return delegate.consultaDadesRegistreV2(dadesConsulta);
    }

    @Override
	@WebMethod
	public RespostaConsultaJustificantEnviament consultaJustificantEnviament(
			@WebParam(name="identificador") @XmlElement(required = true) String identificador){
		return delegate.consultaJustificantEnviament(identificador);

	}

}
