package es.caib.notib.logic.statemachine.listeners;

import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import es.caib.notib.logic.intf.statemachine.events.EnviamentNotificaRequest;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class EnviamentNotificaListener {

//    private final StateMachineService<EnviamentSmEstat, EnviamentSmEvent> stateMachineService;
    private final EnviamentSmService enviamentSmService;
    private final NotificacioEnviamentRepository notificacioEnviamentRepository;
    private final NotificacioService notificacioService;

    private static Map<Long, Integer> notificacionsExecutades = new HashMap<>();
    public static synchronized boolean haDeExecutar(Long notificacioId, Integer intent) {
        var ultimIntent = notificacionsExecutades.get(notificacioId);
        if (ultimIntent == null || ultimIntent < intent) {
            notificacionsExecutades.put(notificacioId, intent);
            return true;
        }
        return false;
    }
    public static void netejaExecucio(Long notificacioId) {
        notificacionsExecutades.remove(notificacioId);
    }

    @Transactional
    @JmsListener(destination = SmConstants.CUA_NOTIFICA, containerFactory = SmConstants.JMS_FACTORY_ACK)
    public void receiveEnviamentNotifica(@Payload EnviamentNotificaRequest enviamentNotificaRequest,
                                         @Headers MessageHeaders headers,
                                         Message message) throws JMSException {
        var enviament = notificacioEnviamentRepository.findByUuid(enviamentNotificaRequest.getEnviamentNotificaDto().getUuid()).orElseThrow();
        var notificacio = enviament.getNotificacio();
        var numIntent = enviamentNotificaRequest.getNumIntent();
        log.debug("[SM] Rebut enviament a notifica <" + enviament + ">");

        // Al notificar es processen tots els enviaments a l'hora. --> Notificacio
        // Per tant controlam que només s'executi el primer enviament de la notificació
        if (haDeExecutar(notificacio.getId(), numIntent)) {
            notificacioService.notificacioEnviar(notificacio.getId());

            for (var env : notificacio.getEnviaments()) {
                var enviamentSuccess = env.getNotificaEstatData() != null;
                var event = env.isPerEmail() ?
                        enviamentSuccess ? EnviamentSmEvent.EM_SUCCESS : EnviamentSmEvent.EM_ERROR :
                        enviamentSuccess ? EnviamentSmEvent.NT_SUCCESS : EnviamentSmEvent.NT_ERROR;
                switch (event) {
                    case EM_SUCCESS:
                        enviamentSmService.emailSuccess(env.getNotificaReferencia());
                        break;
                    case EM_ERROR:
                        enviamentSmService.emailFailed(env.getNotificaReferencia());
                        break;
                    case NT_SUCCESS:
                        enviamentSmService.notificaSuccess(env.getNotificaReferencia());
                        break;
                    case NT_ERROR:
                        enviamentSmService.notificaFailed(env.getNotificaReferencia());
                        break;
                }
            }

            if (NotificacioEstatEnumDto.ENVIADA.equals(notificacio.getEstat()) ||
                    NotificacioEstatEnumDto.FINALITZADA.equals(notificacio.getEstat()) ||
                    NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(notificacio.getEstat())) {
                netejaExecucio(notificacio.getId());
            }
        }
        message.acknowledge();

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class NotExecucio {
        Integer numIntent;
        Boolean success;
    }
}
