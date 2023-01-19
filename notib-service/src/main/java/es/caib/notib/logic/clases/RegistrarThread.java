package es.caib.notib.logic.clases;

import es.caib.notib.logic.helper.NotificacioHelper;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegistrarThread implements Runnable {

    protected Long notificacioId;
    protected NotificacioHelper notificacioHelper;

    public RegistrarThread(Long notificacioId, NotificacioHelper notificacioHelper) {
        this.notificacioId = notificacioId;
        this.notificacioHelper= notificacioHelper;
    }

    @Override
    public void run() {

        log.info("[REG] >>> Realitzant registre de la notificació amb id " + notificacioId);
        try {
            notificacioHelper.registrarNotificar(notificacioId);
        } catch (RegistreNotificaException e) {
            log.error("Error registrant la notificació amb id " + notificacioId, e);
            throw new RuntimeException(e);
        }
    }

}