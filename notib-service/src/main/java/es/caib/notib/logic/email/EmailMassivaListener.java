package es.caib.notib.logic.email;

import com.google.common.base.Strings;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.EmailNotificacioMassivaHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.persist.repository.NotificacioMassivaRepository;
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
import java.io.ByteArrayOutputStream;

@Slf4j
@RequiredArgsConstructor
@Component
public class EmailMassivaListener {

    private final NotificacioMassivaRepository notificacioMassivaRepository;
    private final PluginHelper pluginHelper;
    private final EmailNotificacioMassivaHelper emailNotificacioMassivaHelper;
    private final IntegracioHelper integracioHelper;

    @Transactional
    @JmsListener(destination = EmailConstants.CUA_EMAIL_MASSIVA, containerFactory = SmConstants.JMS_FACTORY_ACK)
    public void receiveMessage(@Payload Long notificacioMassivaId,
                               @Headers MessageHeaders headers,
                               Message message) throws JMSException {

        var info = new IntegracioInfo(
                IntegracioCodi.EMAIL,
                "Enviament de email per notificació massiva",
                IntegracioAccioTipusEnumDto.ENVIAMENT,
                new AccioParam("Identificador de la notificacio massiva", String.valueOf(notificacioMassivaId)));
        try {
            var notificacioMassiva = notificacioMassivaRepository.findById(notificacioMassivaId).orElseThrow();
            info.setCodiEntitat(notificacioMassiva.getEntitat().getCodi());
            if (!Strings.isNullOrEmpty(notificacioMassiva.getEmail())) {
                ConfigHelper.setEntitatCodi(notificacioMassiva.getEntitat().getCodi());
                info.addParam("Correu electrònic", notificacioMassiva.getEmail());
                var fileResumContent = getFileContent(notificacioMassiva.getResumGesdocId(), PluginHelper.GESDOC_AGRUPACIO_MASSIUS_INFORMES, false);
                var fileErrorsContent = getFileContent(notificacioMassiva.getErrorsGesdocId(), PluginHelper.GESDOC_AGRUPACIO_MASSIUS_ERRORS, false);
                emailNotificacioMassivaHelper.sendMail(notificacioMassiva, notificacioMassiva.getEmail(), fileResumContent, fileErrorsContent);
            }
            integracioHelper.addAccioOk(info);
        } catch (Exception ex) {
            log.error("Error enviant email per la notificacio massiva" + notificacioMassivaId, ex);
            integracioHelper.addAccioError(info, "Error enviant email", ex);
        }
        message.acknowledge();
    }

    private byte[] getFileContent(String gesdocId, String agrupacio, boolean isZip) {
        var baos = new ByteArrayOutputStream();
        pluginHelper.gestioDocumentalGet(gesdocId, agrupacio, baos, isZip);
        return baos.toByteArray();
    }

}
