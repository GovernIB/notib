/**
 * 
 */
package es.caib.notib.logic.service.ws;

import es.caib.notib.logic.intf.service.AdviserService;
import es.caib.notib.logic.intf.service.AdviserServiceWs;
import es.caib.notib.logic.intf.ws.adviser.AdviserServiceWsV2;
import es.caib.notib.logic.intf.ws.adviser.common.Opciones;
import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.Acuse;
import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.Receptor;
import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.ResultadoSincronizarEnvio;
import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.SincronizarEnvio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import java.math.BigInteger;

/**
 * Implementació del servei adviser de Notifica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class AdviserServiceWsV2Impl implements AdviserServiceWsV2, AdviserServiceWs {

    @Autowired
    AdviserService adviserService;

	@Override
	@Transactional
	public void sincronizarEnvio(
			String organismoEmisor,
			Holder<String> hIdentificador,
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

        SincronizarEnvio sincronizarEnvio = new SincronizarEnvio();
        sincronizarEnvio.setOrganismoEmisor(organismoEmisor);
        sincronizarEnvio.setIdentificador(hIdentificador.value);
        sincronizarEnvio.setTipoEntrega(tipoEntrega);
        sincronizarEnvio.setModoNotificacion(modoNotificacion);
        sincronizarEnvio.setEstado(estado);
        sincronizarEnvio.setFechaEstado(fechaEstado);
        sincronizarEnvio.setReceptor(receptor);
        sincronizarEnvio.setAcusePDF(acusePDF);
        sincronizarEnvio.setAcuseXML(acuseXML);
        sincronizarEnvio.setOpcionesSincronizarEnvio(opcionesSincronizarEnvio);

        ResultadoSincronizarEnvio resultadoSincronizarEnvio = adviserService.sincronizarEnvio(sincronizarEnvio);
        codigoRespuesta.value = resultadoSincronizarEnvio.getCodigoRespuesta();
        descripcionRespuesta.value = resultadoSincronizarEnvio.getDescripcionRespuesta();
    }

}
