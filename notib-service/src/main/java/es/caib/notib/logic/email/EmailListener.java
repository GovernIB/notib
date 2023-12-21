package es.caib.notib.logic.email;

import es.caib.notib.logic.helper.EmailNotificacioHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodiEnum;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.persist.repository.NotificacioRepository;
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

@Slf4j
@RequiredArgsConstructor
@Component
public class EmailListener {

    private final EmailNotificacioHelper emailNotificacioHelper;
    private final NotificacioRepository notificacioRepository;
    private final IntegracioHelper integracioHelper;

    @Transactional
    @JmsListener(destination = EmailConstants.CUA_EMAIL_NOTIFICACIO, containerFactory = SmConstants.JMS_FACTORY_ACK)
    public void receiveMessage(@Payload Long notificacioId,
                               @Headers MessageHeaders headers,
                               Message message) throws JMSException {

        var info = new IntegracioInfo(
                IntegracioCodiEnum.EMAIL,
                "Enviament de emails per notificació",
                IntegracioAccioTipusEnumDto.ENVIAMENT,
                new AccioParam("Identificador de la notificacio ", String.valueOf(notificacioId)));

        try {
            var notificacio = notificacioRepository.findById(notificacioId).orElseThrow();
            info.setCodiEntitat(notificacio.getEntitat().getCodi());
            var resultat = emailNotificacioHelper.prepararEnvioEmailNotificacio(notificacio);
            info.addParam("Resultat", resultat);
            integracioHelper.addAccioOk(info);
        } catch (Exception ex) {
            log.error("Error enviant els emails per la notificacio " + notificacioId, ex);
            integracioHelper.addAccioError(info, "Error enviant email", ex);
        }
        message.acknowledge();
    }
}
