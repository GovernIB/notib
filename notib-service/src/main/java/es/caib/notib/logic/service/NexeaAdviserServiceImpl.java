package es.caib.notib.logic.service;

import com.google.common.base.Strings;
import es.caib.notib.client.domini.CieEstat;
import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.NotificaHelper;
import es.caib.notib.logic.intf.service.AdviserService;
import es.caib.notib.logic.intf.service.NexeaAdviserService;
import es.caib.notib.logic.intf.ws.adviser.nexea.NexeaAdviserWs;
import es.caib.notib.logic.intf.ws.adviser.nexea.common.Opciones;
import es.caib.notib.logic.intf.ws.adviser.nexea.sincronizarenvio.Acuse;
import es.caib.notib.logic.intf.ws.adviser.nexea.sincronizarenvio.Receptor;
import es.caib.notib.logic.intf.ws.adviser.nexea.sincronizarenvio.SincronizarEnvio;
import es.caib.notib.logic.intf.ws.adviser.nexea.sincronizarenvio.ResultadoSincronizarEnvio;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import java.math.BigInteger;

@Slf4j
@Service
public class NexeaAdviserServiceImpl implements NexeaAdviserService  {

    @Autowired
    private AdviserService adviserService;
    @Autowired
    private NotificacioEnviamentRepository enviamentRepository;
    @Autowired
    private NotificaHelper notificaHelper;
    @Autowired
    private ConversioTipusHelper conversioTipusHelper;

    @Transactional
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

        var sincronizarEnvio = new SincronizarEnvio();
        sincronizarEnvio.setOrganismoEmisor(organismoEmisor);
        sincronizarEnvio.setIdentificador(identificador.value);
        sincronizarEnvio.setTipoEntrega(tipoEntrega);
        sincronizarEnvio.setModoNotificacion(modoNotificacion);
        sincronizarEnvio.setEstado(estado);
        sincronizarEnvio.setFechaEstado(fechaEstado);
        sincronizarEnvio.setReceptor(receptor);
        sincronizarEnvio.setAcusePDF(acusePDF);
        sincronizarEnvio.setAcuseXML(acuseXML);
        sincronizarEnvio.setOpcionesSincronizarEnvio(opcionesSincronizarEnvio);
        var resposta = sincronitzarEntregaPostal(sincronizarEnvio);
        if (NexeaAdviserWs.CODI_ERROR.equals(resposta.getCodigoRespuesta())) {
            codigoRespuesta.value = resposta.getCodigoRespuesta();
            descripcionRespuesta.value = resposta.getDescripcionRespuesta();
            return;
        }
        codigoRespuesta.value = resposta.getCodigoRespuesta();
        descripcionRespuesta.value = resposta.getDescripcionRespuesta();

        var sinc = conversioTipusHelper.convertir(sincronizarEnvio, es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.SincronizarEnvio.class);
        sinc.setIdentificador(resposta.getIdentificador());
        var resultadoSincronizarEnvio = adviserService.sincronizarEnvio(sinc);
        if (NexeaAdviserWs.CODI_OK_DEC.equalsIgnoreCase(resposta.getDescripcionRespuesta())) {
            codigoRespuesta.value = NexeaAdviserWs.CODI_OK_DEC;
            descripcionRespuesta.value = NexeaAdviserWs.CODI_OK_DEC;
        } else {
            codigoRespuesta.value = resultadoSincronizarEnvio.getCodigoRespuesta();
            descripcionRespuesta.value = resultadoSincronizarEnvio.getDescripcionRespuesta();
        }
    }

    private ResultadoSincronizarEnvio sincronitzarEntregaPostal(SincronizarEnvio sincronizarEnvio) {

        var identificador = sincronizarEnvio.getIdentificador();
        var resultado = new ResultadoSincronizarEnvio();
        try {
            if (Strings.isNullOrEmpty(identificador)) {
                var error = "[Nexea Adviser] Error l'identificador no pot ser null";
                log.error(error);
                resultado.setCodigoRespuesta(NexeaAdviserWs.CODI_ERROR_IDENTIFICADOR_INCORRECTE);
                resultado.setDescripcionRespuesta("Identificador incorrecto");
                return resultado;
            }
            var estat = CieEstat.valueOf(sincronizarEnvio.getEstado().toUpperCase());
            var enviament = enviamentRepository.findByCieId(identificador);
            if (enviament == null) {
                resultado.setCodigoRespuesta(NexeaAdviserWs.CODI_ERROR_IDENTIFICADOR_INEXISTENT);
                resultado.setDescripcionRespuesta("Identificador no se corresponde con el CIE");
                return resultado;
            }
            resultado.setIdentificador(enviament.getNotificaIdentificador());
            enviament.getEntregaPostal().setCieEstat(estat);
            if (!CieEstat.NOTIFICADA.equals(estat)) {
                resultado.setCodigoRespuesta(NexeaAdviserWs.CODI_OK);
                return resultado;
            }
            // Si l'estat retornat per nexea es notificada avisar a Notifica
            var resposta = notificaHelper.enviamentEntregaPostalNotificada(enviament);
            resultado.setCodigoRespuesta(NexeaAdviserWs.CODI_OK);
            if (!NexeaAdviserWs.CODI_OK_DEC.equalsIgnoreCase(resposta.getDescripcionRespuesta())) {
                resultado.setCodigoRespuesta(NexeaAdviserWs.CODI_ERROR);
            }
            return resultado;
        } catch (Exception e) {
            var error = "[Nexea Adviser] Error al sincronitzar l'enviament amb id " + identificador;
            log.error(error, e);
            resultado.setCodigoRespuesta(NexeaAdviserWs.CODI_ERROR);
            resultado.setDescripcionRespuesta(error);
            return resultado;
        }
    }
}
