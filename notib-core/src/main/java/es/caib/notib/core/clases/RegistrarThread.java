package es.caib.notib.core.clases;

import es.caib.notib.core.api.exception.RegistreNotificaException;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.helper.NotificacioHelper;
import es.caib.notib.core.helper.SemaforNotificacio;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

//@Component
@Slf4j
public class RegistrarThread implements Runnable {

    protected Long notificacio;
    protected NotificacioHelper notificacioHelper;

    public RegistrarThread(Long notificacio, NotificacioHelper notificacioHelper) {
        this.notificacio = notificacio;
        this.notificacioHelper= notificacioHelper;
    }
    @Override
    public void run() {

        log.info("[REG] >>> Realitzant registre de la notificació amb id " + notificacio);
        try {
            notificacioHelper.registrarNotificar(notificacio);
        } catch (RegistreNotificaException e) {
            log.error("Error registrant la notificació amb id " + notificacio, e);
            throw new RuntimeException(e);
        }
    }

}
