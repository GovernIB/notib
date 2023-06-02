package es.caib.notib.logic.intf.service;


import es.caib.notib.logic.intf.ws.adviser.common.Opciones;
import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.Acuse;
import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.Receptor;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import java.math.BigInteger;

public interface AdviserServiceWs {

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
            Holder<Opciones> opcionesResultadoSincronizarEnvio);
}
