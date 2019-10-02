/**
 * 
 */
package es.caib.notib.core.ejb.ws;

import java.math.BigInteger;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;

import org.jboss.wsf.spi.annotation.WebContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.notib.core.wsdl.adviser.Acuse;
import es.caib.notib.core.wsdl.adviser.AdviserWsV2PortType;
import es.caib.notib.core.wsdl.adviser.Opciones;
import es.caib.notib.core.wsdl.adviser.Receptor;

/**
 * Implementació dels mètodes per al servei de recepció de
 * callbacks de portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
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
//@SecurityDomain("seycon")
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class NotificaAdviserWsV2Bean implements AdviserWsV2PortType {

	@Autowired
	private AdviserWsV2PortType delegate;

	@Override
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
