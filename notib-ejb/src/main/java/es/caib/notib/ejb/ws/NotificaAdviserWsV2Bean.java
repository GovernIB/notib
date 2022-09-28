/**
 * 
 */
package es.caib.notib.ejb.ws;

import java.math.BigInteger;

import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;

import es.caib.notib.ejb.AbstractService;
import org.jboss.ws.api.annotation.WebContext;
import org.springframework.beans.factory.annotation.Autowired;

import es.caib.notib.logic.wsdl.adviser.Acuse;
import es.caib.notib.logic.wsdl.adviser.AdviserWsV2PortType;
import es.caib.notib.logic.wsdl.adviser.Opciones;
import es.caib.notib.logic.wsdl.adviser.Receptor;

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
public class NotificaAdviserWsV2Bean extends AbstractService<AdviserWsV2PortType> implements AdviserWsV2PortType {

	@Autowired
	private AdviserWsV2PortType delegate;

	@Override
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
		delegate.sincronizarEnvio(
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
