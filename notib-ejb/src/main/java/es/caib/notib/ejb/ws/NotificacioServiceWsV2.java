/**
 * 
 */
package es.caib.notib.ejb.ws;

import es.caib.notib.client.domini.DadesConsulta;
import es.caib.notib.client.domini.PermisConsulta;
import es.caib.notib.client.domini.RespostaAlta;
import es.caib.notib.client.domini.RespostaAltaV2;
import es.caib.notib.client.domini.RespostaConsultaDadesRegistre;
import es.caib.notib.client.domini.RespostaConsultaDadesRegistreV2;
import es.caib.notib.client.domini.RespostaConsultaEstatEnviament;
import es.caib.notib.client.domini.RespostaConsultaEstatEnviamentV2;
import es.caib.notib.client.domini.RespostaConsultaEstatNotificacio;
import es.caib.notib.client.domini.RespostaConsultaEstatNotificacioV2;
import es.caib.notib.client.domini.RespostaConsultaJustificantEnviament;
import es.caib.notib.ejb.AbstractService;
import es.caib.notib.ejb.helper.UsuariAuthHelper;
import es.caib.notib.logic.intf.dto.notificacio.Notificacio;
import es.caib.notib.logic.intf.ws.notificacio.NotificacioServiceWsException;
import org.jboss.ws.api.annotation.WebContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;

/**
 * EJB per a la publicació del servei web de gestió de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
@SOAPBinding(style = SOAPBinding.Style.RPC)
@WebService(
		name = "NotificacioServiceV2",
		serviceName = "NotificacioServiceV2",
		portName = "NotificacioServiceV2Port",
		targetNamespace = "http://www.caib.es/notib/ws/notificacio")
@WebContext(contextRoot = "/notib/ws", urlPattern = "/notificacioV2WS")
// TODO
//		authMethod = "KEYCLOAK",
//		transportGuarantee = "NONE",
//		secureWSDLAccess = false)
//@RolesAllowed({"NOT_APL"})
public class NotificacioServiceWsV2 extends AbstractService<es.caib.notib.logic.intf.ws.notificacio.NotificacioServiceWsV2> implements es.caib.notib.logic.intf.ws.notificacio.NotificacioServiceWsV2 {

	@Resource
	private SessionContext sessionContext;

	@Autowired
	private UsuariAuthHelper usuariHelper;



	@Override
	@WebMethod
//	@WebResult(name = "respuesta")
	public RespostaAlta alta(Notificacio notificacio) {
		usuariHelper.generarUsuariAutenticatEjb(sessionContext, true);
		return getDelegateService().alta(notificacio);
	}

    @Override
	@WebMethod
    public RespostaAltaV2 altaV2(Notificacio notificacio) throws NotificacioServiceWsException {
        return getDelegateService().altaV2(notificacio);
    }

    @Override
	@WebMethod
	public RespostaConsultaEstatNotificacio consultaEstatNotificacio(String identificador) {
		usuariHelper.generarUsuariAutenticatEjb(sessionContext, true);
		return getDelegateService().consultaEstatNotificacio(identificador);
	}

	@Override
	@WebMethod
	public RespostaConsultaEstatNotificacioV2 consultaEstatNotificacioV2(String identificador) {
		return getDelegateService().consultaEstatNotificacioV2(identificador);
	}

	@Override
	@WebMethod
	public RespostaConsultaEstatEnviament consultaEstatEnviament(String referencia) {
		usuariHelper.generarUsuariAutenticatEjb(sessionContext, true);
		return getDelegateService().consultaEstatEnviament(referencia);
	}

	@Override
	@WebMethod
	public RespostaConsultaEstatEnviamentV2 consultaEstatEnviamentV2(String referencia) throws NotificacioServiceWsException {
		return getDelegateService().consultaEstatEnviamentV2(referencia);
	}

	@Override
	@WebMethod
	public boolean donarPermisConsulta(PermisConsulta permisConsulta) {
		usuariHelper.generarUsuariAutenticatEjb(sessionContext, true);
		return getDelegateService().donarPermisConsulta(permisConsulta);
	}
	
	@Override
	@WebMethod
	public RespostaConsultaDadesRegistre consultaDadesRegistre(DadesConsulta dadesConsulta) {
		return getDelegateService().consultaDadesRegistre(dadesConsulta);
	}

    @Override
	@WebMethod
    public RespostaConsultaDadesRegistreV2 consultaDadesRegistreV2(DadesConsulta dadesConsulta) {
        return getDelegateService().consultaDadesRegistreV2(dadesConsulta);
    }

    @Override
	@WebMethod
	public RespostaConsultaJustificantEnviament consultaJustificantEnviament(@WebParam(name="identificador") @XmlElement(required = true) String identificador){
		return getDelegateService().consultaJustificantEnviament(identificador);

	}

}
