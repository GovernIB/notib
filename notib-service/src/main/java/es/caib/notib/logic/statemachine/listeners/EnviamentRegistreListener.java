package es.caib.notib.logic.statemachine.listeners;

import es.caib.notib.logic.helper.AuditHelper;
import es.caib.notib.logic.helper.NotificacioTableHelper;
import es.caib.notib.logic.helper.RegistreSmHelper;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import es.caib.notib.logic.intf.service.AuditService;
import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.statemachine.events.EnviamentRegistreRequest;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
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
import java.util.Date;
import java.util.concurrent.Semaphore;

@Slf4j
@RequiredArgsConstructor
@Component
public class EnviamentRegistreListener {

//    private final StateMachineService<EnviamentSmEstat, EnviamentSmEvent> stateMachineService;
    private final EnviamentSmService enviamentSmService;
    private final NotificacioEnviamentRepository notificacioEnviamentRepository;
    private final RegistreSmHelper registreSmHelper;
    private final NotificacioTableHelper notificacioTableHelper;
    private final AuditHelper auditHelper;

    private Semaphore semaphore = new Semaphore(5);

    @Transactional
    @JmsListener(destination = SmConstants.CUA_REGISTRE, containerFactory = SmConstants.JMS_FACTORY_ACK)
    public void receiveEnviamentRegistre(@Payload EnviamentRegistreRequest enviamentRegistreRequest,
                                         @Headers MessageHeaders headers,
                                         Message message) throws JMSException, RegistreNotificaException, InterruptedException {

        var enviamentUuid = enviamentRegistreRequest.getEnviamentUuid();
        log.debug("[SM] Rebut enviament de registre <" + enviamentUuid + ">");
        var enviament = notificacioEnviamentRepository.findByUuid(enviamentRegistreRequest.getEnviamentUuid()).orElseThrow();
        var notificacio = enviament.getNotificacio();
        var numIntent = enviamentRegistreRequest.getNumIntent();
        notificacio.setRegistreEnviamentIntent(numIntent);
        semaphore.acquire();
        try {
            // Registrar enviament
            boolean registreSuccess = registreSmHelper.registrarEnviament(enviament, numIntent);

            // Actualitzar notificació
            if (notificacioEnviamentRepository.areEnviamentsRegistrats(notificacio.getId()) == 1) {
                var isSir = notificacio.isComunicacioSir();
                notificacio.updateEstat(isSir ? NotificacioEstatEnumDto.ENVIAT_SIR : NotificacioEstatEnumDto.REGISTRADA);

                // És possible que el registre ja retorni estats finals al registrar SIR?
                if (isSir && notificacio.getEnviaments().stream().allMatch(e -> e.isRegistreEstatFinal())) {
                    var nouEstat = NotificacioEstatEnumDto.FINALITZADA;
                    //Marcar com a processada si la notificació s'ha fet des de una aplicació
                    if (enviament.getNotificacio() != null && enviament.getNotificacio().getTipusUsuari() == TipusUsuariEnumDto.APLICACIO) {
                        nouEstat = NotificacioEstatEnumDto.PROCESSADA;
                    }
                    notificacio.updateEstat(nouEstat);
                    notificacio.updateMotiu(enviament.getRegistreEstat().name());
                    notificacio.updateEstatDate(new Date());
                }
            }
            notificacioTableHelper.actualitzarRegistre(notificacio);
            auditHelper.auditaNotificacio(notificacio, AuditService.TipusOperacio.UPDATE, "RegistreSmHelper.registrarEnviament");

//            TEST
//            var registreSuccess = new Random().nextBoolean();
//            if (registreSuccess) {
//                enviament.setRegistreData(new Date());
//                notificacioEnviamentRepository.save(enviament);
//            }

            if (registreSuccess) {
                enviamentSmService.registreSuccess(enviamentUuid);
            } else {
                enviamentSmService.registreFailed(enviamentUuid);
            }
        } catch (Exception ex) {
            enviamentSmService.registreFailed(enviamentUuid);
        } finally {
            semaphore.release();
        }
        message.acknowledge();

    }

}
