package es.caib.notib.core.threads;

import es.caib.notib.core.api.exception.RegistreNotificaException;
import es.caib.notib.core.helper.NotificacioHelper;
import lombok.extern.slf4j.Slf4j;

//@Component
@Slf4j
public class RegistrarThread implements Runnable {
//public class RegistrarThread implements Callable<Boolean> {

    protected Long notificacioId;
    protected NotificacioHelper notificacioHelper;

    public RegistrarThread(Long notificacioId, NotificacioHelper notificacioHelper) {
        this.notificacioId = notificacioId;
        this.notificacioHelper= notificacioHelper;
    }

    @Override
    public void run() {
//    public Boolean call() {

        log.info("[REG] >>> Realitzant registre de la notificació amb id " + notificacioId);
        try {
            notificacioHelper.registrarNotificar(notificacioId);
//            return true;
        } catch (RegistreNotificaException e) {
            log.error("Error registrant la notificació amb id " + notificacioId, e);
            throw new RuntimeException(e);
        }
    }

}
