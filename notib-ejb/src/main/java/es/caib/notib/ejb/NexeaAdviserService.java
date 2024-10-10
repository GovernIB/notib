package es.caib.notib.ejb;

import es.caib.notib.logic.intf.ws.adviser.nexea.common.Opciones;
import es.caib.notib.logic.intf.ws.adviser.nexea.sincronizarenvio.Acuse;
import es.caib.notib.logic.intf.ws.adviser.nexea.sincronizarenvio.Receptor;
import org.springframework.context.annotation.Primary;

import javax.ejb.Stateless;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import java.math.BigInteger;

@Primary
@Stateless
public class NexeaAdviserService extends AbstractService<es.caib.notib.logic.intf.service.NexeaAdviserService> implements es.caib.notib.logic.intf.service.NexeaAdviserService  {

    @Override
    public void sincronizarEnvio(String organismoEmisor,
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

        getDelegateService().sincronizarEnvio(organismoEmisor, identificador, tipoEntrega, modoNotificacion, estado, fechaEstado, receptor, acusePDF, acuseXML,
                                                opcionesSincronizarEnvio, codigoRespuesta, descripcionRespuesta, opcionesResultadoSincronizarEnvio);
    }
}
