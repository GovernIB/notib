package es.caib.notib.logic.service;

import com.google.common.base.Strings;
import es.caib.notib.client.domini.CieEstat;
import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.helper.NotificaHelper;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.service.AdviserService;
import es.caib.notib.logic.intf.service.CieAdviserService;
import es.caib.notib.logic.intf.util.NifHelper;
import es.caib.notib.logic.intf.ws.adviser.nexea.NexeaAdviserWs;
import es.caib.notib.logic.intf.ws.adviser.nexea.common.Opciones;
import es.caib.notib.logic.intf.ws.adviser.nexea.sincronizarenvio.Acuse;
import es.caib.notib.logic.intf.ws.adviser.nexea.sincronizarenvio.Receptor;
import es.caib.notib.logic.intf.ws.adviser.nexea.sincronizarenvio.SincronizarEnvio;
import es.caib.notib.logic.intf.ws.adviser.nexea.sincronizarenvio.ResultadoSincronizarEnvio;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ScheduledMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import java.math.BigInteger;

@Slf4j
@Service
public class CieAdviserServiceImpl implements CieAdviserService {

    @Autowired
    private AdviserService adviserService;
    @Autowired
    private NotificacioEnviamentRepository enviamentRepository;
    @Autowired
    private IntegracioHelper integracioHelper;
    @Autowired
    private ConversioTipusHelper conversioTipusHelper;
    @Autowired
    private JmsTemplate jmsTemplate;

    @Override
    public  es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.ResultadoSincronizarEnvio sincronizarEnvio(es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.SincronizarEnvio sincronizarEnvio) {

        var info = new IntegracioInfo(IntegracioCodi.CIE, "Sincronitzar enviament", IntegracioAccioTipusEnumDto.RECEPCIO,
                new AccioParam("Identificador Nexea", sincronizarEnvio.getIdentificador()),
                new AccioParam("Estat", sincronizarEnvio.getEstado()));

        var sinc = conversioTipusHelper.convertir(sincronizarEnvio, SincronizarEnvio.class);
        var resposta = sincronitzarEntregaPostal(sinc, info);

        if (NexeaAdviserWs.CODI_OK_DEC.equalsIgnoreCase(resposta.getDescripcionRespuesta())) {
            integracioHelper.addAccioOk(info);
        } else {
            integracioHelper.addAccioError(info, resposta.getDescripcionRespuesta());
        }
        return conversioTipusHelper.convertir(sincronizarEnvio, es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.ResultadoSincronizarEnvio.class);
    }

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

        var info = new IntegracioInfo(IntegracioCodi.CIE, "Sincronitzar enviament", IntegracioAccioTipusEnumDto.RECEPCIO,
                new AccioParam("Identificador Nexea", identificador.value),
                new AccioParam("Estat", estado));

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
        var resposta = sincronitzarEntregaPostal(sincronizarEnvio, info);
        codigoRespuesta.value = resposta.getCodigoRespuesta();
        descripcionRespuesta.value = resposta.getDescripcionRespuesta();
        if (!NexeaAdviserWs.CODI_OK.equals(resposta.getCodigoRespuesta())) {
            return;
        }

        var sinc = conversioTipusHelper.convertir(sincronizarEnvio, es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.SincronizarEnvio.class);
        sinc.setIdentificador(resposta.getIdentificador());
        var resultadoSincronizarEnvio = adviserService.sincronizarEnvio(sinc);
        var codi = AdviserServiceImpl.ResultatEnviamentEnum.getByCodi(resultadoSincronizarEnvio.getCodigoRespuesta());
        if (NexeaAdviserWs.CODI_OK_DEC.equalsIgnoreCase(resposta.getDescripcionRespuesta())) {
            codigoRespuesta.value = NexeaAdviserWs.CODI_OK;
            descripcionRespuesta.value = NexeaAdviserWs.CODI_OK_DEC;
            integracioHelper.addAccioOk(info);
        } else {
            codigoRespuesta.value = codi.getCodiNexea();
            descripcionRespuesta.value = codi.getDesc();
            integracioHelper.addAccioError(info, resposta.getDescripcionRespuesta());
        }
    }

    private ResultadoSincronizarEnvio sincronitzarEntregaPostal(SincronizarEnvio sincronizarEnvio, IntegracioInfo info) {

        var identificador = sincronizarEnvio.getIdentificador();
        var resultado = new ResultadoSincronizarEnvio();
        try {
            if (Strings.isNullOrEmpty(identificador)) {
                var error = "[CIE Adviser] Error l'identificador no pot ser null";
                log.error(error);
                resultado.setCodigoRespuesta(NexeaAdviserWs.CODI_ERROR_IDENTIFICADOR_INCORRECTE);
                resultado.setDescripcionRespuesta("Identificador incorrecto");
                return resultado;
            }
            var enviament = enviamentRepository.findByCieId(identificador);
            if (enviament == null) {
                resultado.setCodigoRespuesta(NexeaAdviserWs.CODI_ERROR_IDENTIFICADOR_INEXISTENT);
                resultado.setDescripcionRespuesta("Identificador no se corresponde con el CIE");
                return resultado;
            }
            info.setAplicacio(enviament.getNotificacio().getTipusUsuari(), enviament.getNotificacio().getCreatedBy().get().getCodi());
//            if (!receptorValid(sincronizarEnvio.getReceptor())) {
//                resultado.setCodigoRespuesta(NexeaAdviserWs.CODI_ERROR_VALIDACIO);
//                resultado.setDescripcionRespuesta("Receptor no tiene el formato correcto");
//                return resultado;
//            }
            var estat = CieEstat.valueOf(sincronizarEnvio.getEstado().toUpperCase());
            enviament.getEntregaPostal().setCieEstat(estat);
            info.setCodiEntitat(enviament.getNotificacio().getEntitat().getCodi());
            resultado.setIdentificador(enviament.getNotificaIdentificador());
            resultado.setCodigoRespuesta(NexeaAdviserWs.CODI_OK);
            resultado.setDescripcionRespuesta(NexeaAdviserWs.CODI_OK_DEC);
            if (CieEstat.NOTIFICADA.equals(estat)) {
                jmsTemplate.convertAndSend(NotificaHelper.CUA_SINCRONIZAR_ENVIO_OE, sincronizarEnvio,
                        m -> {
                            m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY,  0L);
                            return m;
                        });
            }
            return resultado;
        } catch (Exception e) {
            var error = "[CIE Adviser] Error al sincronitzar l'enviament amb id " + identificador;
            log.error(error, e);
            resultado.setCodigoRespuesta(NexeaAdviserWs.CODI_ERROR);
            resultado.setDescripcionRespuesta(error);
            return resultado;
        }
    }

    private boolean receptorValid(Receptor receptor) {
        return receptor != null && !Strings.isNullOrEmpty(receptor.getNifReceptor())
                && NifHelper.isvalid(receptor.getNifReceptor());
    }
}
