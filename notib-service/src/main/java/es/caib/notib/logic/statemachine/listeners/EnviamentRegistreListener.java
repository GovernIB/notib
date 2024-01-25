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
    public void receiveEnviamentRegistre(@Payload EnviamentRegistreRequest enviamentRegistreRequest, @Headers MessageHeaders headers, Message message) throws JMSException, RegistreNotificaException, InterruptedException {

        var enviamentUuid = enviamentRegistreRequest.getEnviamentUuid();
        if (enviamentUuid != null) {
            log.debug("[SM] Rebut enviament de registre <" + enviamentUuid + ">");
        } else {
            log.error("[SM] Rebut enviament de registre sense Enviament");
        }

        semaphore.acquire();
        try {
            var enviament = notificacioEnviamentRepository.findByUuid(enviamentUuid).orElseThrow();
            var notificacio = enviament.getNotificacio();
            var numIntent = enviamentRegistreRequest.getNumIntent();
            notificacio.setRegistreEnviamentIntent(numIntent);
            log.debug("[SM] Enviament de registre <" + enviamentUuid + "> registrant ");
            // Registrar enviament
            boolean registreSuccess = registreSmHelper.registrarEnviament(enviament, numIntent);
            log.debug("[SM] Enviament de registre <" + enviamentUuid + "> registrat ");

            // Actualitzar notificació
            if (notificacioEnviamentRepository.areEnviamentsRegistrats(notificacio.getId()) == 1) {
                log.debug("[SM] Enviament de registre <" + enviamentUuid + "> actualitzant notificacio");
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
            log.debug("[SM] Enviament de registre <" + enviamentUuid + "> actualitzant registre");
            notificacioTableHelper.actualitzarRegistre(notificacio);
            log.debug("[SM] Enviament de registre <" + enviamentUuid + "> audita notificacio");
            auditHelper.auditaNotificacio(notificacio, AuditService.TipusOperacio.UPDATE, "RegistreSmHelper.registrarEnviament");

//            TEST
//            var registreSuccess = new Random().nextBoolean();
//            if (registreSuccess) {
//                enviament.setRegistreData(new Date());
//                notificacioEnviamentRepository.save(enviament);
//            }
            log.debug("[SM] Enviament de registre <" + enviamentUuid + "> is success " + registreSuccess);
            if (registreSuccess) {
                log.debug("[SM] Enviament de registre <" + enviamentUuid + "> success ");
                enviamentSmService.registreSuccess(enviamentUuid);
            } else {
                log.debug("[SM] Enviament de registre <" + enviamentUuid + "> failed ");
                enviamentSmService.registreFailed(enviamentUuid);
            }
        } catch (Exception ex) {
            if (enviamentUuid != null) {
                log.debug("[SM] Enviament de registre <" + enviamentUuid + "> error ", ex);
                enviamentSmService.registreFailed(enviamentUuid);
            }
        } finally {
            semaphore.release();
        }
        log.debug("[SM] Enviament de registre <" + enviamentUuid + "> completat");
        message.acknowledge();

    }

}
