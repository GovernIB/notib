package es.caib.notib.logic.plugin.cie;

import es.caib.notib.logic.helper.NotificacioEventHelper;
import es.caib.notib.logic.intf.dto.cie.CiePluginConstants;
import es.caib.notib.plugin.cie.RespostaCie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;

@Slf4j
@RequiredArgsConstructor
@Component
public class CiePluginListener {

    private final CiePluginHelper ciePluginHelper;
    private final NotificacioEventHelper notificacioEventHelper;

    @JmsListener(destination = CiePluginConstants.CUA_CIE_PLUGIN_ENVIAR, containerFactory = CiePluginConstants.JMS_FACTORY_ACK)
    public void receiveEnviamentCie(@Payload String notificacioReferencia, @Headers MessageHeaders headers, Message message) throws JMSException, InterruptedException {

        message.acknowledge();
        RespostaCie resposta = null;
        try {
            // TODO AFEGIR EVENT A LA CUA i programar reintents segons max reintents
            resposta = ciePluginHelper.enviar(notificacioReferencia);
            if (resposta.getIdentificador() != null) {
                ciePluginHelper.guardarRespostaCie(notificacioReferencia, resposta);
            }
        } catch (Exception ex) {
            log.error("Error al enviar la entrega cie per la notificacio amb referencia" + notificacioReferencia, ex);
        }
        ciePluginHelper.afegirEventsEnviarCie(notificacioReferencia, resposta);
    }

    @JmsListener(destination = CiePluginConstants.CUA_CIE_PLUGIN_CANCELAR, containerFactory = CiePluginConstants.JMS_FACTORY_ACK)
    public void receiveCancelarEnviamentCie(@Payload String uuidEnviament, @Headers MessageHeaders headers, Message message) throws JMSException, InterruptedException {

        message.acknowledge();
        boolean resposta;
        try {
            // TODO AFEGIR EVENT A LA CUA i programar reintents segons max reintents
            resposta = ciePluginHelper.cancelar(uuidEnviament);
            ciePluginHelper.cancelarEntregaPostal(uuidEnviament, resposta);
        } catch (Exception ex) {
            log.error("Error al enviar la entrega cie per la notificacio amb referencia" + uuidEnviament, ex);
            ciePluginHelper.cancelarEntregaPostal(uuidEnviament, false);
        }
    }
}
