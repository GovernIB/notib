/**
 * 
 */
package es.caib.notib.ejb;

import es.caib.notib.logic.intf.ws.adviser.common.Opciones;
import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.Acuse;
import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.Receptor;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import java.math.BigInteger;

/**
 * Implementació dels mètodes per al servei de recepció de
 * callbacks de portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
public class AdviserServiceWs extends AbstractService<es.caib.notib.logic.intf.ws.adviser.AdviserServiceWsV2> implements es.caib.notib.logic.intf.ws.adviser.AdviserServiceWsV2 {

	@Override
	@PermitAll
	public void sincronizarEnvio(
			String organismoEmisor,
            Holder<String> identificador,
            BigInteger tipoEntrega,
			BigInteger modoNotificacion,
			String estado,
            XMLGregorianCalendar fechaEstado,
            Receptor receptor,
            Acuse acusePDF,
			Acuse acuseXML,
			Opciones opcionesSincronizarEnvio,
			Holder<String> codigoRespuesta,
			Holder<String> descripcionRespuesta,
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
