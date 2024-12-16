/**
 * 
 */
package es.caib.notib.ejb;

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
import es.caib.notib.client.domini.ampliarPlazo.AmpliarPlazoOE;
import es.caib.notib.client.domini.ampliarPlazo.RespuestaAmpliarPlazoOE;
import es.caib.notib.logic.intf.dto.notificacio.Notificacio;
import es.caib.notib.logic.intf.ws.notificacio.NotificacioServiceWsException;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.jws.WebParam;
import javax.xml.bind.annotation.XmlElement;

/**
 * EJB per a la publicació del servei web de gestió de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
public class NotificacioServiceWs extends AbstractService<es.caib.notib.logic.intf.ws.notificacio.NotificacioServiceWsV2> implements es.caib.notib.logic.intf.ws.notificacio.NotificacioServiceWsV2 {

//	@Autowired
//	private UsuariAuthHelper usuariHelper;


	@Override
	@RolesAllowed({"NOT_APL"})
	public RespostaAlta alta(Notificacio notificacio) {

//		usuariHelper.generarUsuariAutenticatEjb(sessionContext, true);
		return getDelegateService().alta(notificacio);
	}

    @Override
	@RolesAllowed({"NOT_APL"})
    public RespostaAltaV2 altaV2(Notificacio notificacio) throws NotificacioServiceWsException {
        return getDelegateService().altaV2(notificacio);
    }

    @Override
	@RolesAllowed({"NOT_APL"})
	public RespostaConsultaEstatNotificacio consultaEstatNotificacio(String identificador) {

//		usuariHelper.generarUsuariAutenticatEjb(sessionContext, true);
		return getDelegateService().consultaEstatNotificacio(identificador);
	}

	@Override
	@RolesAllowed({"NOT_APL"})
	public RespostaConsultaEstatNotificacioV2 consultaEstatNotificacioV2(String identificador) {
		return getDelegateService().consultaEstatNotificacioV2(identificador);
	}

	@Override
	@RolesAllowed({"NOT_APL"})
	public RespostaConsultaEstatEnviament consultaEstatEnviament(String referencia) {

//		usuariHelper.generarUsuariAutenticatEjb(sessionContext, true);
		return getDelegateService().consultaEstatEnviament(referencia);
	}

	@Override
	@RolesAllowed({"NOT_APL"})
	public RespostaConsultaEstatEnviamentV2 consultaEstatEnviamentV2(String referencia) throws NotificacioServiceWsException {
		return getDelegateService().consultaEstatEnviamentV2(referencia);
	}

	@Override
	@RolesAllowed({"NOT_APL"})
	public boolean donarPermisConsulta(PermisConsulta permisConsulta) {

//		usuariHelper.generarUsuariAutenticatEjb(sessionContext, true);
		return getDelegateService().donarPermisConsulta(permisConsulta);
	}
	
	@Override
	@RolesAllowed({"NOT_APL"})
	public RespostaConsultaDadesRegistre consultaDadesRegistre(DadesConsulta dadesConsulta) {
		return getDelegateService().consultaDadesRegistre(dadesConsulta);
	}

    @Override
	@RolesAllowed({"NOT_APL"})
    public RespostaConsultaDadesRegistreV2 consultaDadesRegistreV2(DadesConsulta dadesConsulta) {
        return getDelegateService().consultaDadesRegistreV2(dadesConsulta);
    }

    @Override
	@RolesAllowed({"NOT_APL"})
	public RespostaConsultaJustificantEnviament consultaJustificantEnviament(@WebParam(name="identificador") @XmlElement(required = true) String identificador){
		return getDelegateService().consultaJustificantEnviament(identificador);

	}

	@Override
	@RolesAllowed({"NOT_APL"})
	public RespuestaAmpliarPlazoOE ampliarPlazo(AmpliarPlazoOE ampliarPlazo) {
		return getDelegateService().ampliarPlazo(ampliarPlazo);
	}

}
