/**
 * 
 */
package es.caib.notib.ejb.ws;

import es.caib.notib.ejb.AbstractService;
import es.caib.notib.logic.wsdl.adviser.Acuse;
import es.caib.notib.logic.wsdl.adviser.AdviserWsV2PortType;
import es.caib.notib.logic.wsdl.adviser.Opciones;
import es.caib.notib.logic.wsdl.adviser.Receptor;
import org.jboss.ws.api.annotation.WebContext;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import java.math.BigInteger;

/**
 * Implementació dels mètodes per al servei de recepció de
 * callbacks de portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@SOAPBinding(style = SOAPBinding.Style.RPC)
@WebService(
		name = "adviserWs",
		serviceName = "AdviserWsV2Service",
		portName = "AdviserWsV2PortType",
		targetNamespace = "https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/")
@WebContext(
		contextRoot = "/notib/ws",
		urlPattern = "/adviser",
		transportGuarantee = "NONE",
		secureWSDLAccess = false)
//@RolesAllowed({"NOT_NOTIFICA"})
public class NotificaAdviserWsV2 extends AbstractService<AdviserWsV2PortType> implements AdviserWsV2PortType {

	@Override
	@PermitAll
	@WebMethod
	public void sincronizarEnvio(
			@WebParam(name = "organismoEmisor", targetNamespace = "")
			String organismoEmisor, 
			@WebParam(name = "identificador", targetNamespace = "")
			Holder<String> identificador, 
			@WebParam(name = "tipoEntrega", targetNamespace = "")
			BigInteger tipoEntrega,
			@WebParam(name = "modoNotificacion", targetNamespace = "")
			BigInteger modoNotificacion, 
			@WebParam(name = "estado", targetNamespace = "")
			String estado, 
			@WebParam(name = "fechaEstado", targetNamespace = "")
			XMLGregorianCalendar fechaEstado, 
			@WebParam(name = "receptor", targetNamespace = "")
			Receptor receptor,
			@WebParam(name = "acusePDF", targetNamespace = "")
			Acuse acusePDF, 
			@WebParam(name = "acuseXML", targetNamespace = "")
			Acuse acuseXML,
			@WebParam(name = "opcionesSincronizarEnvio", targetNamespace = "")
			Opciones opcionesSincronizarEnvio, 
			@WebParam(mode = WebParam.Mode.OUT, name = "codigoRespuesta", targetNamespace = "")
			Holder<String> codigoRespuesta,
			@WebParam(mode = WebParam.Mode.OUT, name = "descripcionRespuesta", targetNamespace = "")
			Holder<String> descripcionRespuesta, 
			@WebParam(mode = WebParam.Mode.OUT, name = "opcionesResultadoSincronizarEnvio", targetNamespace = "")
			Holder<Opciones> opcionesResultadoSincronizarEnvio) {
		getDelegateService().sincronizarEnvio(
				organismoEmisor, 
				identificador, 
				tipoEntrega, 
				modoNotificacion, 
				estado, 
				fechaEstado,
				receptor, 
				acusePDF,
				acuseXML, 
				opcionesSincronizarEnvio, 
				codigoRespuesta, 
				descripcionRespuesta,
				opcionesResultadoSincronizarEnvio);
	}

}
